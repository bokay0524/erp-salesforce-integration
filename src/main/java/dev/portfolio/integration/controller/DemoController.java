package dev.portfolio.integration.controller;

import dev.portfolio.integration.gateway.CrmGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoController {
    private final CrmGateway crmGateway;
    public DemoController(CrmGateway crmGateway) { this.crmGateway = crmGateway; }

    @GetMapping
    public Map<String, Object> index() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("project", "ERP Salesforce Integration Portfolio");
        body.put("architecture", "Spring MVC + MyBatis + H2 + CRM Adapter");
        body.put("timestamp", OffsetDateTime.now().toString());
        return body;
    }

    @GetMapping("/health")
    public Map<String, String> health() { return Map.of("status", "UP"); }

    @GetMapping("/demo/crm-state")
    public Map<String, Object> crmState() { return crmGateway.snapshot(); }
}
