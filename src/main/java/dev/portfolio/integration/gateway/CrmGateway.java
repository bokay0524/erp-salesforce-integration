package dev.portfolio.integration.gateway;

import dev.portfolio.integration.dto.BulkSyncResult;
import dev.portfolio.integration.dto.SyncResult;
import dev.portfolio.integration.model.ProductRecord;

import java.util.List;
import java.util.Map;

public interface CrmGateway {
    SyncResult upsert(String entityType, String externalKey, Map<String, Object> payload);
    SyncResult delete(String entityType, String externalKey);
    BulkSyncResult bulkUpsertProducts(List<ProductRecord> products);
    Map<String, Object> snapshot();
}
