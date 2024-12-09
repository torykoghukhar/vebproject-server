package com.example.vebproject.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HistoryOrdersDTO {
    private Date date;
    private BigDecimal amount;
    private String status;

}
