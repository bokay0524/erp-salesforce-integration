package dev.portfolio.integration.gateway;

import dev.portfolio.integration.dto.BulkSyncResult;
import dev.portfolio.integration.dto.SyncResult;
import dev.portfolio.integration.model.ProductRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("!salesforce")
public class MockSalesforceGateway implements CrmGateway {
    private final Map<String, Map<String, Map<String, Object>>> store = new ConcurrentHashMap<>();

    @Override
    public SyncResult upsert(String entityType, String externalKey, Map<String, Object> payload) {
        store.computeIfAbsent(entityType, key -> new ConcurrentHashMap<>())
             .put(externalKey, new LinkedHashMap<>(payload));
        return new SyncResult(true, entityType, externalKey, "UPSERT", "Mock Salesforce upsert completed");
    }

    @Override
    public SyncResult delete(String entityType, String externalKey) {
        Map<String, Map<String, Object>> bucket = store.get(entityType);
        if (bucket != null) bucket.remove(externalKey);
        return new SyncResult(true, entityType, externalKey, "DELETE", "Mock Salesforce delete completed");
    }

    @Override
    public BulkSyncResult bulkUpsertProducts(List<ProductRecord> products) {
        String jobId = "JOB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        for (ProductRecord product : products) {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("name", product.getProductName());
            payload.put("category", product.getCategory());
            payload.put("unitPrice", product.getUnitPrice());
            payload.put("active", product.isActive());
            upsert("PRODUCT", product.getProductCode(), payload);
        }
        return new BulkSyncResult(jobId, products.size(), products.size(), 0, "JobComplete");
    }

    @Override
    public Map<String, Object> snapshot() {
        Map<String, Object> copy = new LinkedHashMap<>();
        store.forEach((type, records) -> copy.put(type, new LinkedHashMap<>(records)));
        return copy;
    }
}
