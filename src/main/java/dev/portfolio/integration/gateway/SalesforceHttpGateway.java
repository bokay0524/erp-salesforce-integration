package dev.portfolio.integration.gateway;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.portfolio.integration.dto.BulkSyncResult;
import dev.portfolio.integration.dto.SyncResult;
import dev.portfolio.integration.model.ProductRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

/**
 * Optional production-style adapter. It is never active in the default demo profile.
 * All organization-specific object/field names and credentials are supplied through environment variables.
 */
@Component
@Profile("salesforce")
public class SalesforceHttpGateway implements CrmGateway {
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    @Value("${crm.api-version}") private String apiVersion;
    @Value("${crm.token-url}") private String tokenUrl;
    @Value("${crm.client-id}") private String clientId;
    @Value("${crm.client-secret}") private String clientSecret;
    @Value("${crm.product-object}") private String productObject;
    @Value("${crm.product-external-id}") private String productExternalId;
    @Value("${crm.customer-object}") private String customerObject;
    @Value("${crm.customer-external-id}") private String customerExternalId;
    @Value("${crm.order-object}") private String orderObject;
    @Value("${crm.order-external-id}") private String orderExternalId;
    @Value("${crm.delivery-object}") private String deliveryObject;
    @Value("${crm.delivery-external-id}") private String deliveryExternalId;

    private volatile Token token;

    @Override
    public SyncResult upsert(String entityType, String externalKey, Map<String, Object> payload) {
        try {
            ObjectConfig cfg = config(entityType);
            Token t = token();
            String url = t.instanceUrl + "/services/data/" + apiVersion + "/sobjects/" + cfg.objectName + "/" + cfg.externalIdField + "/" + enc(externalKey);
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Bearer " + t.accessToken)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json.writeValueAsString(payload)))
                .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            boolean ok = response.statusCode() == 201 || response.statusCode() == 204;
            return new SyncResult(ok, entityType, externalKey, "UPSERT", "Salesforce HTTP " + response.statusCode());
        } catch (Exception e) {
            throw new IllegalStateException("Salesforce upsert failed", e);
        }
    }

    @Override
    public SyncResult delete(String entityType, String externalKey) {
        try {
            ObjectConfig cfg = config(entityType);
            Token t = token();
            String url = t.instanceUrl + "/services/data/" + apiVersion + "/sobjects/" + cfg.objectName + "/" + cfg.externalIdField + "/" + enc(externalKey);
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Bearer " + t.accessToken)
                .DELETE().build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            boolean ok = response.statusCode() == 204 || response.statusCode() == 404;
            return new SyncResult(ok, entityType, externalKey, "DELETE", "Salesforce HTTP " + response.statusCode());
        } catch (Exception e) {
            throw new IllegalStateException("Salesforce delete failed", e);
        }
    }

    @Override
    public BulkSyncResult bulkUpsertProducts(List<ProductRecord> products) {
        try {
            Token t = token();
            String jobsUrl = t.instanceUrl + "/services/data/" + apiVersion + "/jobs/ingest";
            Map<String, Object> job = new LinkedHashMap<>();
            job.put("operation", "upsert");
            job.put("object", productObject);
            job.put("externalIdFieldName", productExternalId);
            job.put("contentType", "CSV");
            job.put("lineEnding", "LF");
            HttpResponse<String> create = send("POST", jobsUrl, "application/json", json.writeValueAsString(job), t);
            ensure2xx(create, "create bulk job");
            Map<String, Object> created = json.readValue(create.body(), new TypeReference<Map<String, Object>>(){});
            String jobId = String.valueOf(created.get("id"));

            String csv = toCsv(products);
            HttpResponse<String> upload = send("PUT", jobsUrl + "/" + jobId + "/batches", "text/csv", csv, t);
            ensure2xx(upload, "upload bulk data");
            HttpResponse<String> close = send("PATCH", jobsUrl + "/" + jobId, "application/json", "{\"state\":\"UploadComplete\"}", t);
            ensure2xx(close, "close bulk job");

            String state = "UploadComplete";
            int succeeded = 0;
            int failed = 0;
            for (int i = 0; i < 20; i++) {
                Thread.sleep(500L);
                HttpResponse<String> status = send("GET", jobsUrl + "/" + jobId, null, null, t);
                ensure2xx(status, "check bulk job");
                Map<String, Object> body = json.readValue(status.body(), new TypeReference<Map<String, Object>>(){});
                state = String.valueOf(body.get("state"));
                succeeded = intValue(body.get("numberRecordsProcessed"));
                failed = intValue(body.get("numberRecordsFailed"));
                if ("JobComplete".equals(state) || "Failed".equals(state) || "Aborted".equals(state)) break;
            }
            return new BulkSyncResult(jobId, products.size(), succeeded, failed, state);
        } catch (Exception e) {
            throw new IllegalStateException("Salesforce bulk upsert failed", e);
        }
    }

    @Override
    public Map<String, Object> snapshot() {
        return Collections.singletonMap("message", "Snapshot is available only in mock mode");
    }

    private Token token() throws Exception {
        Token cached = token;
        if (cached != null && cached.expiresAt > System.currentTimeMillis()) return cached;
        if (clientId.isBlank() || clientSecret.isBlank()) throw new IllegalStateException("CRM_CLIENT_ID and CRM_CLIENT_SECRET are required");
        String body = "grant_type=client_credentials&client_id=" + enc(clientId) + "&client_secret=" + enc(clientSecret);
        HttpRequest request = HttpRequest.newBuilder(URI.create(tokenUrl))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        ensure2xx(response, "authenticate");
        Map<String, Object> payload = json.readValue(response.body(), new TypeReference<Map<String, Object>>(){});
        token = new Token(String.valueOf(payload.get("access_token")), String.valueOf(payload.get("instance_url")), System.currentTimeMillis() + 50 * 60 * 1000L);
        return token;
    }

    private HttpResponse<String> send(String method, String url, String contentType, String body, Token t) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url)).header("Authorization", "Bearer " + t.accessToken);
        if (contentType != null) b.header("Content-Type", contentType);
        if ("GET".equals(method)) b.GET();
        else b.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        return http.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    private void ensure2xx(HttpResponse<?> response, String action) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(action + " returned HTTP " + response.statusCode());
        }
    }

    private String toCsv(List<ProductRecord> products) {
        StringBuilder out = new StringBuilder(productExternalId + ",Name,Category__c,Unit_Price__c,Active__c\n");
        for (ProductRecord p : products) {
            out.append(csv(p.getProductCode())).append(',').append(csv(p.getProductName())).append(',')
               .append(csv(p.getCategory())).append(',').append(p.getUnitPrice()).append(',').append(p.isActive()).append('\n');
        }
        return out.toString();
    }

    private String csv(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String enc(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }
    private int intValue(Object value) { return value instanceof Number ? ((Number)value).intValue() : 0; }

    private ObjectConfig config(String entityType) {
        switch (entityType) {
            case "CUSTOMER": return new ObjectConfig(customerObject, customerExternalId);
            case "ORDER": return new ObjectConfig(orderObject, orderExternalId);
            case "DELIVERY": return new ObjectConfig(deliveryObject, deliveryExternalId);
            case "PRODUCT": return new ObjectConfig(productObject, productExternalId);
            default: throw new IllegalArgumentException("Unsupported entity type: " + entityType);
        }
    }

    private static class ObjectConfig {
        final String objectName; final String externalIdField;
        ObjectConfig(String objectName, String externalIdField) { this.objectName = objectName; this.externalIdField = externalIdField; }
    }
    private static class Token {
        final String accessToken; final String instanceUrl; final long expiresAt;
        Token(String accessToken, String instanceUrl, long expiresAt) { this.accessToken = accessToken; this.instanceUrl = instanceUrl; this.expiresAt = expiresAt; }
    }
}
