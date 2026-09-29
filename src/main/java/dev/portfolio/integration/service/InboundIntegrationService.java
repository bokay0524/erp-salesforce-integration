package dev.portfolio.integration.service;

import dev.portfolio.integration.dto.QuoteOrderRequest;
import dev.portfolio.integration.model.SyncHistoryRecord;
import dev.portfolio.integration.repository.ErpMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InboundIntegrationService {
    private final ErpMapper erpMapper;
    public InboundIntegrationService(ErpMapper erpMapper) { this.erpMapper = erpMapper; }

    @Transactional
    public void upsertQuoteOrder(QuoteOrderRequest request) {
        validate(request);
        erpMapper.mergeQuote(request.getQuote());
        erpMapper.deleteQuoteItems(request.getQuote().getQuoteNo());
        for (QuoteOrderRequest.Line line : request.getQuote().getItems()) {
            erpMapper.insertQuoteItem(request.getQuote().getQuoteNo(), line);
        }

        erpMapper.mergeOrder(request.getOrder());
        erpMapper.deleteOrderItems(request.getOrder().getOrderNo());
        for (QuoteOrderRequest.Line line : request.getOrder().getItems()) {
            erpMapper.insertOrderItem(request.getOrder().getOrderNo(), line);
        }
        audit("QUOTE_ORDER", request.getOrder().getOrderNo(), "UPSERT", "SUCCESS", "CRM payload persisted transactionally");
    }

    @Transactional
    public void deleteQuoteOrder(String quoteNo, String orderNo) {
        erpMapper.deleteOrderItems(orderNo);
        erpMapper.deleteOrder(orderNo);
        erpMapper.deleteQuoteItems(quoteNo);
        erpMapper.deleteQuote(quoteNo);
        audit("QUOTE_ORDER", orderNo, "DELETE", "SUCCESS", "Quote and order removed transactionally");
    }

    private void validate(QuoteOrderRequest request) {
        if (request == null || request.getQuote() == null || request.getOrder() == null) {
            throw new IllegalArgumentException("quote and order are required");
        }
        if (blank(request.getQuote().getQuoteNo()) || blank(request.getOrder().getOrderNo())) {
            throw new IllegalArgumentException("quoteNo and orderNo are required");
        }
        if (blank(request.getQuote().getCustomerCode()) || blank(request.getOrder().getCustomerCode())) {
            throw new IllegalArgumentException("customerCode is required");
        }
    }

    private boolean blank(String v) { return v == null || v.trim().isEmpty(); }
    private void audit(String entity, String key, String operation, String status, String message) {
        SyncHistoryRecord h = new SyncHistoryRecord();
        h.setDirection("CRM_TO_ERP"); h.setEntityType(entity); h.setExternalKey(key); h.setOperation(operation);
        h.setStatus(status); h.setMessage(message); h.setCreatedAt(LocalDateTime.now());
        erpMapper.insertSyncHistory(h);
    }
}
