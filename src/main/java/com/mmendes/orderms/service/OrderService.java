package com.mmendes.orderms.service;

import com.mmendes.orderms.controller.dto.OrderResponse;
import com.mmendes.orderms.entity.OrderEntity;
import com.mmendes.orderms.entity.OrderItem;
import com.mmendes.orderms.listener.dto.OrderCreateEventDto;
import com.mmendes.orderms.repository.OrderRepository;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;

    public OrderService(OrderRepository orderRepository, MongoTemplate mongoTemplate) {
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void save(OrderCreateEventDto orderItemEvent){
        OrderEntity entity = new OrderEntity();
        entity.setOrderId(orderItemEvent.codigoPedido());
        entity.setCustomerId(orderItemEvent.codigoCliente());
        entity.setTotal(getTotal(orderItemEvent));
        entity.setItems(getOrderList(orderItemEvent));

        orderRepository.save(entity);
    }

    public BigDecimal findTotalOnOrderCustomerId(Long customerId) {
        var aggregations = newAggregation(
                match(Criteria.where("customerId").is(customerId)),
                group().sum("total").as("total")
        );

        var response = mongoTemplate.aggregate(aggregations, "tb_orders", Document.class);

        return new BigDecimal(response.getUniqueMappedResult().get("total").toString());
    }

    private BigDecimal getTotal(OrderCreateEventDto orderItemEvent) {
        return orderItemEvent.itens().stream()
                .map(i -> i.preco().multiply(BigDecimal.valueOf(i.quantidade())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private static List<OrderItem> getOrderList(OrderCreateEventDto orderItemEvent) {
        return orderItemEvent.itens().stream().map(i -> new OrderItem(i.produto(), i.quantidade(), i.preco())).toList();
    }

    public Page<OrderResponse> findAll(Long customerId, PageRequest page){
        var orders = orderRepository.findAllByCustomerId(customerId, page);

        return orders.map(OrderResponse::fromEntity);
    }
}
