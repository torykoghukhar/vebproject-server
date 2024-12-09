package com.example.vebproject.controllers;

import com.example.vebproject.DTO.DateDTO;
import com.example.vebproject.DTO.HistoryOrdersDTO;
import com.example.vebproject.DTO.StatisticDTO;
import com.example.vebproject.models.Order;
import com.example.vebproject.models.User;
import com.example.vebproject.services.OrderService;
import com.example.vebproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/allorders")
    public ResponseEntity<?> getAllOrders(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.getuser(principal.getName());
        List<HistoryOrdersDTO> orders = orderService.getAllOrders(user);
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/orderstatistic")
    public ResponseEntity<?> getOrderStatistic(@RequestBody DateDTO dateDTO) {
        if (dateDTO.getEndDate() == null || dateDTO.getStartDate() == null) {
            return ResponseEntity.badRequest().build();
        }
        List<StatisticDTO> statisticDTOS = orderService.getstatistic(dateDTO);
        if (statisticDTOS.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(statisticDTOS);
    }
}
