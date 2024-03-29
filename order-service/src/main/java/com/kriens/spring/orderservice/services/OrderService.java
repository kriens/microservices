package com.kriens.spring.orderservice.services;

import com.kriens.spring.orderservice.dto.InventoryResponse;
import com.kriens.spring.orderservice.dto.OrderLineItemsDTO;
import com.kriens.spring.orderservice.dto.OrderRequest;
import com.kriens.spring.orderservice.event.OrderPlacedEvent;
import com.kriens.spring.orderservice.model.Order;
import com.kriens.spring.orderservice.model.OrderLineItems;
import com.kriens.spring.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsList()
                .stream().map(this::mapFromDto).toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode).toList();


        InventoryResponse[] inventoryResponseArray = webClient
                .build()
                .get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class).block();


        assert inventoryResponseArray != null;
        if(Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isPresent) && inventoryResponseArray.length>0){
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));
            return "Order placed successfully";
        }

        else
            throw new IllegalArgumentException("Product not in stock.");
    }

    private OrderLineItems mapFromDto(OrderLineItemsDTO orderLineItemsDTO) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setId(orderLineItemsDTO.getId());
        orderLineItems.setPrice(orderLineItemsDTO.getPrice());
        orderLineItems.setSkuCode(orderLineItemsDTO.getSkuCode());
        orderLineItems.setQuantity(orderLineItemsDTO.getQuantity());
        return orderLineItems;
    }
}
