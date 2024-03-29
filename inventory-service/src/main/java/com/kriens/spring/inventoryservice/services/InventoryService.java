package com.kriens.spring.inventoryservice.services;

import com.kriens.spring.inventoryservice.DTO.InventoryDTO;
import com.kriens.spring.inventoryservice.DTO.InventoryResponse;
import com.kriens.spring.inventoryservice.model.Inventory;
import com.kriens.spring.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {


    private final InventoryRepository inventoryRepository;



    public List<InventoryResponse> inStock(List<String> skucode) {

        return inventoryRepository.findBySkuCodeIn(skucode).stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skucode(inventory.getSkuCode())
                                .isPresent(inventory.getQuantity()>0)
                                .build())
                .toList();
    }
}
