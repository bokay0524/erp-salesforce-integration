package dev.portfolio.integration.dto;

public class BulkSyncResult {
    private final String jobId;
    private final int submitted;
    private final int succeeded;
    private final int failed;
    private final String state;

    public BulkSyncResult(String jobId, int submitted, int succeeded, int failed, String state) {
        this.jobId = jobId;
        this.submitted = submitted;
        this.succeeded = succeeded;
        this.failed = failed;
        this.state = state;
    }
    public String getJobId() { return jobId; }
    public int getSubmitted() { return submitted; }
    public int getSucceeded() { return succeeded; }
    public int getFailed() { return failed; }
    public String getState() { return state; }
}
