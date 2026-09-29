package dev.portfolio.integration.gateway;

import dev.portfolio.integration.dto.BulkSyncResult;
import dev.portfolio.integration.model.ProductRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MockSalesforceGatewayTest {
    @Test
    void upsertAndDeleteAreIdempotentByExternalKey() {
        MockSalesforceGateway gateway = new MockSalesforceGateway();
        assertTrue(gateway.upsert("CUSTOMER", "CUST-1", Map.of("name", "Demo")).isSuccess());
        assertTrue(gateway.upsert("CUSTOMER", "CUST-1", Map.of("name", "Demo Updated")).isSuccess());
        Map<String, Object> customerBucket = cast(gateway.snapshot().get("CUSTOMER"));
        assertEquals(1, customerBucket.size());
        assertTrue(gateway.delete("CUSTOMER", "CUST-1").isSuccess());
        assertTrue(cast(gateway.snapshot().get("CUSTOMER")).isEmpty());
    }

    @Test
    void bulkSyncSimulatesSalesforceJobLifecycle() {
        MockSalesforceGateway gateway = new MockSalesforceGateway();
        ProductRecord p = new ProductRecord();
        p.setProductCode("P-1"); p.setProductName("Demo Product"); p.setCategory("TEST");
        p.setUnitPrice(BigDecimal.TEN); p.setActive(true);
        BulkSyncResult result = gateway.bulkUpsertProducts(Collections.singletonList(p));
        assertEquals("JobComplete", result.getState());
        assertEquals(1, result.getSucceeded());
        assertEquals(0, result.getFailed());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> cast(Object value) { return (Map<String, Object>) value; }
}
