package com.example.vebproject.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatisticDTO {
    private Date date;
    private BigDecimal amount;
    private String username;
    private String email;
}
