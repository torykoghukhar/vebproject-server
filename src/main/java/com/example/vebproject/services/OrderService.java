package com.example.vebproject.services;

import com.example.vebproject.DTO.*;
import com.example.vebproject.models.*;
import com.example.vebproject.repository.BookRepository;
import com.example.vebproject.repository.OrderItemRepository;
import com.example.vebproject.repository.OrderRepository;
import com.example.vebproject.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private PaymentRepository paymentRepository;
    private BookRepository bookRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, PaymentRepository paymentRepository, BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.bookRepository = bookRepository;
    }

    public boolean createOrder(OrderDTO orderDTO, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setCreatedAt(new Date());

        Payment payment = new Payment();
        payment.setPaymentMethod(orderDTO.getPaymentMethod());
        payment.setPaymentStatus("PENDING");
        payment.setAmount(orderDTO.getAmount());
        payment.setCreatedAt(new Date());

        paymentRepository.save(payment);

        order.setPayment(payment);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDTO itemDTO : orderDTO.getBooks()) {
            Book book = bookRepository.findById(itemDTO.getId()).orElseThrow(() -> new RuntimeException("Book not found"));
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItems.add(orderItem);
        }

        order.setOrderitems(orderItems);
        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        return true;
    }

    public List<HistoryOrdersDTO> getAllOrders(User user) {
        return orderRepository.findByUser_Id(user.getId()).stream()
                .map(order -> new HistoryOrdersDTO(
                        order.getCreatedAt(),
                        order.getPayment().getAmount(),
                        order.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public List<StatisticDTO> getstatistic(DateDTO datedto) {
        List<Order> orders = orderRepository.findAllByCreatedAtBetween(datedto.getStartDate(), datedto.getEndDate());
        return orders.stream().map(order -> new StatisticDTO(order.getCreatedAt(), order.getPayment().getAmount(), order.getUser().getName(), order.getUser().getEmail()))
                .collect(Collectors.toList());
    }
}

