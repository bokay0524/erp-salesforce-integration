package dev.portfolio.integration.controller;

import dev.portfolio.integration.dto.BulkSyncResult;
import dev.portfolio.integration.dto.SyncResult;
import dev.portfolio.integration.model.SyncHistoryRecord;
import dev.portfolio.integration.service.OutboundIntegrationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/erp")
public class IntegrationController {
    private final OutboundIntegrationService service;
    public IntegrationController(OutboundIntegrationService service) { this.service = service; }

    @PostMapping("/customers/{customerCode}/sync")
    public SyncResult syncCustomer(@PathVariable String customerCode) { return service.syncCustomer(customerCode); }

    @PostMapping("/products/bulk-sync")
    public BulkSyncResult bulkSyncProducts() { return service.bulkSyncProducts(); }

    @PostMapping("/orders/{orderNo}/sync")
    public SyncResult syncOrder(@PathVariable String orderNo) { return service.syncOrder(orderNo); }

    @PostMapping("/orders/{orderNo}/cancel")
    public SyncResult cancelOrder(@PathVariable String orderNo) { return service.cancelOrder(orderNo); }

    @PostMapping("/deliveries/{deliveryNo}/sync")
    public SyncResult syncDelivery(@PathVariable String deliveryNo) { return service.syncDelivery(deliveryNo); }

    @GetMapping("/sync-history")
    public List<SyncHistoryRecord> history() { return service.history(); }
}
