package com.mmendes.orderms.listener.dto;

import java.util.List;

public record OrderCreateEventDto(Long codigoPedido,
                                  Long codigoCliente,
                                  List<OrderItemEvent> itens) {
}
