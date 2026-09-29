# Security and Confidentiality

This portfolio repository must never contain:

- employer or customer credentials
- Salesforce client IDs/secrets, access tokens or user passwords
- internal database addresses, usernames or passwords
- internal IP addresses, UNC paths or VPN-only URLs
- proprietary table/stored-procedure names copied from enterprise systems
- production/customer data
- organization-specific Salesforce metadata copied from a real tenant

The default profile uses an in-memory H2 database and a Mock Salesforce adapter. Real external connectivity is explicitly opt-in.

If a credential from an enterprise project has ever been committed to another public repository, rotate/revoke it rather than relying only on deleting the file from the latest commit.
