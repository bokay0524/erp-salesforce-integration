package dev.portfolio.integration.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class QuoteOrderRequest {
    private Quote quote;
    private Order order;
    public Quote getQuote() { return quote; }
    public void setQuote(Quote quote) { this.quote = quote; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public static class Quote {
        private String quoteNo;
        private String customerCode;
        private LocalDate quoteDate;
        private String status;
        private List<Line> items = new ArrayList<>();
        public String getQuoteNo() { return quoteNo; }
        public void setQuoteNo(String quoteNo) { this.quoteNo = quoteNo; }
        public String getCustomerCode() { return customerCode; }
        public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
        public LocalDate getQuoteDate() { return quoteDate; }
        public void setQuoteDate(LocalDate quoteDate) { this.quoteDate = quoteDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public List<Line> getItems() { return items; }
        public void setItems(List<Line> items) { this.items = items; }
    }

    public static class Order {
        private String orderNo;
        private String quoteNo;
        private String customerCode;
        private LocalDate orderDate;
        private String status;
        private BigDecimal totalAmount;
        private List<Line> items = new ArrayList<>();
        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
        public String getQuoteNo() { return quoteNo; }
        public void setQuoteNo(String quoteNo) { this.quoteNo = quoteNo; }
        public String getCustomerCode() { return customerCode; }
        public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
        public LocalDate getOrderDate() { return orderDate; }
        public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public List<Line> getItems() { return items; }
        public void setItems(List<Line> items) { this.items = items; }
    }

    public static class Line {
        private int lineNo;
        private String productCode;
        private int quantity;
        private BigDecimal unitPrice;
        public int getLineNo() { return lineNo; }
        public void setLineNo(int lineNo) { this.lineNo = lineNo; }
        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    }
}
