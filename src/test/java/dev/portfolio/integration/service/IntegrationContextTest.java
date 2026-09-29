package dev.portfolio.integration.service;

import dev.portfolio.integration.dto.BulkSyncResult;
import dev.portfolio.integration.dto.QuoteOrderRequest;
import dev.portfolio.integration.dto.SyncResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath:spring/root-context.xml")
class IntegrationContextTest {
    @Autowired OutboundIntegrationService outbound;
    @Autowired InboundIntegrationService inbound;

    @Test
    void demoDataCanBeSynchronizedThroughMockGateway() {
        SyncResult customer = outbound.syncCustomer("CUST-1001");
        SyncResult order = outbound.syncOrder("SO-2026-0001");
        BulkSyncResult products = outbound.bulkSyncProducts();
        assertTrue(customer.isSuccess());
        assertTrue(order.isSuccess());
        assertEquals("JobComplete", products.getState());
        assertTrue(outbound.history().size() >= 3);
    }

    @Test
    void crmQuoteOrderCanBePersistedThenSynchronizedBackOut() {
        QuoteOrderRequest request = new QuoteOrderRequest();

        QuoteOrderRequest.Line line = new QuoteOrderRequest.Line();
        line.setLineNo(1);
        line.setProductCode("PRD-001");
        line.setQuantity(2);
        line.setUnitPrice(new BigDecimal("120000"));

        QuoteOrderRequest.Quote quote = new QuoteOrderRequest.Quote();
        quote.setQuoteNo("Q-TEST-1000");
        quote.setCustomerCode("CUST-1002");
        quote.setQuoteDate(LocalDate.of(2026, 9, 29));
        quote.setStatus("APPROVED");
        quote.setItems(Collections.singletonList(line));

        QuoteOrderRequest.Order order = new QuoteOrderRequest.Order();
        order.setOrderNo("SO-TEST-1000");
        order.setQuoteNo("Q-TEST-1000");
        order.setCustomerCode("CUST-1002");
        order.setOrderDate(LocalDate.of(2026, 9, 29));
        order.setStatus("CONFIRMED");
        order.setTotalAmount(new BigDecimal("240000"));
        order.setItems(Collections.singletonList(line));

        request.setQuote(quote);
        request.setOrder(order);
        inbound.upsertQuoteOrder(request);

        SyncResult result = outbound.syncOrder("SO-TEST-1000");
        assertTrue(result.isSuccess());
    }
}
