package dev.portfolio.integration.dto;

public class SyncResult {
    private final boolean success;
    private final String entityType;
    private final String externalKey;
    private final String operation;
    private final String message;

    public SyncResult(boolean success, String entityType, String externalKey, String operation, String message) {
        this.success = success;
        this.entityType = entityType;
        this.externalKey = externalKey;
        this.operation = operation;
        this.message = message;
    }
    public boolean isSuccess() { return success; }
    public String getEntityType() { return entityType; }
    public String getExternalKey() { return externalKey; }
    public String getOperation() { return operation; }
    public String getMessage() { return message; }
}
