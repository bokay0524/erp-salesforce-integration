package dev.portfolio.integration.controller;

import dev.portfolio.integration.dto.QuoteOrderRequest;
import dev.portfolio.integration.service.InboundIntegrationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/crm")
public class InboundCrmController {
    private final InboundIntegrationService service;
    public InboundCrmController(InboundIntegrationService service) { this.service = service; }

    @PostMapping("/quote-orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> upsert(@RequestBody QuoteOrderRequest request) {
        service.upsertQuoteOrder(request);
        return Collections.singletonMap("status", "saved");
    }

    @DeleteMapping("/quote-orders")
    public Map<String, String> delete(@RequestParam String quoteNo, @RequestParam String orderNo) {
        service.deleteQuoteOrder(quoteNo, orderNo);
        return Collections.singletonMap("status", "deleted");
    }
}
