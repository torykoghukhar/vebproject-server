package com.example.vebproject.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDTO {
    private List<OrderItemDTO> books;
    private String cardNumber;
    private String cardCVV;
    private String paymentMethod;
    private BigDecimal amount;

}
