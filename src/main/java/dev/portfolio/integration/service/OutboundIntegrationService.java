package dev.portfolio.integration.service;

import dev.portfolio.integration.dto.BulkSyncResult;
import dev.portfolio.integration.dto.SyncResult;
import dev.portfolio.integration.gateway.CrmGateway;
import dev.portfolio.integration.model.*;
import dev.portfolio.integration.repository.ErpMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OutboundIntegrationService {
    private final ErpMapper erpMapper;
    private final CrmGateway crmGateway;

    public OutboundIntegrationService(ErpMapper erpMapper, CrmGateway crmGateway) {
        this.erpMapper = erpMapper;
        this.crmGateway = crmGateway;
    }

    public SyncResult syncCustomer(String customerCode) {
        CustomerRecord customer = require(erpMapper.selectCustomer(customerCode), "Customer not found: " + customerCode);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("name", customer.getCustomerName());
        payload.put("businessNo", customer.getBusinessNo());
        payload.put("email", customer.getEmail());
        payload.put("phone", customer.getPhone());
        SyncResult result = crmGateway.upsert("CUSTOMER", customer.getCustomerCode(), payload);
        history("ERP_TO_CRM", "CUSTOMER", customerCode, "UPSERT", result);
        return result;
    }

    public BulkSyncResult bulkSyncProducts() {
        List<ProductRecord> products = erpMapper.selectAllProducts();
        BulkSyncResult result = crmGateway.bulkUpsertProducts(products);
        SyncResult auditResult = new SyncResult(result.getFailed() == 0, "PRODUCT", "BULK", "BULK_UPSERT", result.getState());
        history("ERP_TO_CRM", "PRODUCT", "BULK", "BULK_UPSERT", auditResult);
        return result;
    }

    public SyncResult syncOrder(String orderNo) {
        OrderRecord order = require(erpMapper.selectOrder(orderNo), "Order not found: " + orderNo);
        order.setItems(erpMapper.selectOrderItems(orderNo));
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("quoteNo", order.getQuoteNo());
        payload.put("customerCode", order.getCustomerCode());
        payload.put("orderDate", order.getOrderDate());
        payload.put("status", order.getStatus());
        payload.put("totalAmount", order.getTotalAmount());
        payload.put("items", order.getItems());
        SyncResult result = crmGateway.upsert("ORDER", orderNo, payload);
        history("ERP_TO_CRM", "ORDER", orderNo, "UPSERT", result);
        return result;
    }

    public SyncResult cancelOrder(String orderNo) {
        require(erpMapper.selectOrder(orderNo), "Order not found: " + orderNo);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("status", "CANCELLED");
        SyncResult result = crmGateway.upsert("ORDER", orderNo, payload);
        history("ERP_TO_CRM", "ORDER", orderNo, "CANCEL", result);
        return result;
    }

    public SyncResult syncDelivery(String deliveryNo) {
        DeliveryRecord delivery = require(erpMapper.selectDelivery(deliveryNo), "Delivery not found: " + deliveryNo);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("orderNo", delivery.getOrderNo());
        payload.put("shippedAt", delivery.getShippedAt());
        payload.put("carrier", delivery.getCarrier());
        payload.put("trackingNo", delivery.getTrackingNo());
        payload.put("status", delivery.getStatus());
        SyncResult result = crmGateway.upsert("DELIVERY", deliveryNo, payload);
        history("ERP_TO_CRM", "DELIVERY", deliveryNo, "UPSERT", result);
        return result;
    }

    public List<SyncHistoryRecord> history() { return erpMapper.selectSyncHistory(); }

    private void history(String direction, String entityType, String key, String operation, SyncResult result) {
        SyncHistoryRecord h = new SyncHistoryRecord();
        h.setDirection(direction); h.setEntityType(entityType); h.setExternalKey(key); h.setOperation(operation);
        h.setStatus(result.isSuccess() ? "SUCCESS" : "FAILED"); h.setMessage(result.getMessage()); h.setCreatedAt(LocalDateTime.now());
        erpMapper.insertSyncHistory(h);
    }

    private <T> T require(T value, String message) {
        if (value == null) throw new IllegalArgumentException(message);
        return value;
    }
}
