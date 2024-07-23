package com.mmendes.orderms.controller;

import com.mmendes.orderms.controller.dto.ApiResponse;
import com.mmendes.orderms.controller.dto.OrderResponse;
import com.mmendes.orderms.controller.dto.PaginationResponse;
import com.mmendes.orderms.service.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("customers/{customerId}/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> listOrders(@PathVariable("customerId") Long customerId,
                                                                 @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
        var body = orderService.findAll(customerId, PageRequest.of(page, pageSize));
        var totalOnOrders = orderService.findTotalOnOrderCustomerId(customerId);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        Map.of("TotalOnOrders", totalOnOrders),
                        body.getContent(),
                        PaginationResponse.fromPage(body)
                )
        );
    }
}
