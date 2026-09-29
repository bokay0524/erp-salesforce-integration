# 3-Minute Reviewer Guide

This project has no frontend by design. The fastest review path is:

1. **`README.md`** — architecture and the two integration directions.
2. **`OutboundIntegrationService`** — ERP → CRM orchestration for customer, product, order and delivery data.
3. **`CrmGateway`** — integration boundary that isolates external-system concerns.
4. **`MockSalesforceGateway` / `SalesforceHttpGateway`** — safe local simulation vs. opt-in HTTP adapter.
5. **`InboundIntegrationService`** — CRM → ERP quote/order master-detail transaction.
6. **`ErpMapper.xml`** — MyBatis-based ERP data access.
7. **`IntegrationContextTest`** — executable end-to-end examples against H2 + Mock Salesforce.

A reviewer can run `mvn jetty:run`, execute the requests in `docs/requests.http`, and inspect `/api/demo/crm-state` and `/api/erp/sync-history` to verify the flow without any Salesforce account.
