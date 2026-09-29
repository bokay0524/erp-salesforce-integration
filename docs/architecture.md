# Architecture Notes

## Why classic Spring MVC is intentionally retained

This portfolio already includes a separate Spring Boot/React project. This repository is designed to demonstrate experience with enterprise systems that use a traditional Spring MVC WAR architecture, XML configuration, MyBatis and an external servlet container.

The public reconstruction modernizes secrets management and adapter boundaries without converting the application to Spring Boot.

## Layers

1. **Controller** — integration endpoints only; no UI responsibility.
2. **Service** — orchestration, validation, transaction boundaries and sync logging.
3. **Repository/MyBatis** — ERP reads and master/detail writes.
4. **Gateway** — external CRM boundary. Business services depend on the `CrmGateway` interface, not Salesforce HTTP code.
5. **Sync History** — integration observability and audit trail.

## Bulk integration

Large product masters are processed through a job-style adapter because a bulk API has a different lifecycle than record-level REST calls: create job → upload data → close upload → poll state → inspect result counts.

## Reliability choices

- External keys are used for idempotent upsert semantics.
- CRM → ERP quote/order persistence is transactional.
- Integration history records direction, entity, operation, status and message.
- The real CRM adapter is opt-in; the default demo cannot accidentally call an external organization.
