# API Examples

Base URL: `http://localhost:8080`

## ERP → CRM

| Method | Endpoint | Purpose |
| --- | --- | --- |
| POST | `/api/erp/customers/{customerCode}/sync` | Customer upsert |
| POST | `/api/erp/products/bulk-sync` | Product bulk upsert job |
| POST | `/api/erp/orders/{orderNo}/sync` | Order + line item upsert |
| POST | `/api/erp/orders/{orderNo}/cancel` | Order cancellation status update |
| POST | `/api/erp/deliveries/{deliveryNo}/sync` | Delivery confirmation upsert |
| GET | `/api/erp/sync-history` | Integration audit history |

## CRM → ERP

### Create or update quote/order

`POST /api/crm/quote-orders`

```json
{
  "quote": {
    "quoteNo": "Q-2026-1000",
    "customerCode": "CUST-1002",
    "quoteDate": "2026-09-29",
    "status": "APPROVED",
    "items": [
      {"lineNo": 1, "productCode": "PRD-001", "quantity": 1, "unitPrice": 120000}
    ]
  },
  "order": {
    "orderNo": "SO-2026-1000",
    "quoteNo": "Q-2026-1000",
    "customerCode": "CUST-1002",
    "orderDate": "2026-09-29",
    "status": "CONFIRMED",
    "totalAmount": 120000,
    "items": [
      {"lineNo": 1, "productCode": "PRD-001", "quantity": 1, "unitPrice": 120000}
    ]
  }
}
```

### Delete quote/order

`DELETE /api/crm/quote-orders?quoteNo=Q-2026-1000&orderNo=SO-2026-1000`

## Demo inspection

`GET /api/demo/crm-state` exposes the in-memory Mock Salesforce state so reviewers can verify integration behavior without an external account.
