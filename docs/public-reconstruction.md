# Public Reconstruction Policy

This repository was created from scratch for portfolio use. It preserves **technical patterns**, not proprietary implementation details.

| Enterprise experience | Public repository representation |
| --- | --- |
| Internal ERP database | Generic H2 ERP schema |
| Company-specific tables / stored procedures | Generic customer/product/quote/order/delivery tables |
| Organization-specific Salesforce objects | Configurable fictitious object names |
| Real OAuth credentials | Environment variable placeholders only |
| Internal network paths and IPs | Removed |
| Production customer/product data | Synthetic demo data |
| Direct external API logic | `CrmGateway` adapter boundary |
| Salesforce Bulk API job flow | Generic job lifecycle implementation + mock simulation |

The goal is to demonstrate architecture, integration reasoning and implementation skill without exposing employer intellectual property or secrets.
