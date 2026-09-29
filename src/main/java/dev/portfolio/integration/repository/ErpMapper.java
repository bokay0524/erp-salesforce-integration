package dev.portfolio.integration.repository;

import dev.portfolio.integration.dto.QuoteOrderRequest;
import dev.portfolio.integration.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ErpMapper {
    CustomerRecord selectCustomer(@Param("customerCode") String customerCode);
    List<ProductRecord> selectAllProducts();
    OrderRecord selectOrder(@Param("orderNo") String orderNo);
    List<OrderItemRecord> selectOrderItems(@Param("orderNo") String orderNo);
    DeliveryRecord selectDelivery(@Param("deliveryNo") String deliveryNo);

    int mergeQuote(@Param("quote") QuoteOrderRequest.Quote quote);
    int deleteQuoteItems(@Param("quoteNo") String quoteNo);
    int insertQuoteItem(@Param("quoteNo") String quoteNo, @Param("line") QuoteOrderRequest.Line line);
    int mergeOrder(@Param("order") QuoteOrderRequest.Order order);
    int deleteOrderItems(@Param("orderNo") String orderNo);
    int insertOrderItem(@Param("orderNo") String orderNo, @Param("line") QuoteOrderRequest.Line line);
    int deleteQuote(@Param("quoteNo") String quoteNo);
    int deleteOrder(@Param("orderNo") String orderNo);

    int insertSyncHistory(SyncHistoryRecord history);
    List<SyncHistoryRecord> selectSyncHistory();
}
