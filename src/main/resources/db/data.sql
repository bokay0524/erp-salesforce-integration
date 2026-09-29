INSERT INTO erp_customer(customer_code, customer_name, business_no, email, phone, updated_at) VALUES
('CUST-1001', 'Demo Manufacturing', '100-00-00001', 'purchasing@example.com', '02-0000-0000', CURRENT_TIMESTAMP),
('CUST-1002', 'Sample Automation', '100-00-00002', 'sales@example.com', '031-000-0000', CURRENT_TIMESTAMP);

INSERT INTO erp_product(product_code, product_name, category, unit_price, active, updated_at) VALUES
('PRD-001', 'Digital Caliper Demo', 'MEASUREMENT', 120000, TRUE, CURRENT_TIMESTAMP),
('PRD-002', 'Height Gauge Demo', 'MEASUREMENT', 450000, TRUE, CURRENT_TIMESTAMP),
('PRD-003', 'Inspection Fixture Demo', 'FIXTURE', 780000, TRUE, CURRENT_TIMESTAMP);

INSERT INTO erp_quote(quote_no, customer_code, quote_date, status) VALUES
('Q-2026-0001', 'CUST-1001', CURRENT_DATE, 'APPROVED');
INSERT INTO erp_quote_item(quote_no, line_no, product_code, quantity, unit_price) VALUES
('Q-2026-0001', 1, 'PRD-001', 2, 120000),
('Q-2026-0001', 2, 'PRD-002', 1, 450000);

INSERT INTO erp_sales_order(order_no, quote_no, customer_code, order_date, status, total_amount) VALUES
('SO-2026-0001', 'Q-2026-0001', 'CUST-1001', CURRENT_DATE, 'CONFIRMED', 690000);
INSERT INTO erp_sales_order_item(order_no, line_no, product_code, quantity, unit_price) VALUES
('SO-2026-0001', 1, 'PRD-001', 2, 120000),
('SO-2026-0001', 2, 'PRD-002', 1, 450000);

INSERT INTO erp_delivery(delivery_no, order_no, shipped_at, carrier, tracking_no, status) VALUES
('DLV-2026-0001', 'SO-2026-0001', CURRENT_TIMESTAMP, 'Demo Logistics', 'TRACK-000001', 'SHIPPED');
