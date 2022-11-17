package com.project.gallery.backend.controller;


import com.project.gallery.backend.dto.OrderDto;
import com.project.gallery.backend.entity.Order;
import com.project.gallery.backend.repository.CartRepository;
import com.project.gallery.backend.repository.OrderRepository;
import com.project.gallery.backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;


@RestController
public class OrderController {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    JwtService jwtService;

    @GetMapping("/api/orders")
    public ResponseEntity getOrder(@CookieValue(value = "token", required = false) String token) {
        if (!jwtService.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        int memberId = jwtService.getId(token);
        List<Order> orders = orderRepository.findByMemberIdOrderByIdDesc(memberId);

        return new ResponseEntity<>(orders, HttpStatus.OK);


    }

    @Transactional
    @PostMapping("/api/orders")
    public ResponseEntity pushOrder(@RequestBody OrderDto dto,
                                    @CookieValue(value = "token", required = false) String token) {

        if (!jwtService.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        int memberId = jwtService.getId(token);
        Order newOrder = new Order();


        newOrder.setMemberId(memberId);
        newOrder.setName(dto.getName());
        newOrder.setAddress(dto.getAddress());
        newOrder.setPayment(dto.getPayment());
        newOrder.setCardNumber(dto.getCardNumber());
        newOrder.setItems(dto.getItems());


        orderRepository.save(newOrder);
        cartRepository.deleteByMemberId(memberId);


        return new ResponseEntity<>(HttpStatus.OK);

    }

}