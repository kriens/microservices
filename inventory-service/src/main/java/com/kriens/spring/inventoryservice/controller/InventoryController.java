package com.kriens.spring.inventoryservice.controller;

import com.kriens.spring.inventoryservice.DTO.InventoryResponse;
import com.kriens.spring.inventoryservice.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    private List<InventoryResponse> isInStock(@RequestParam List<String> skuCode){
        return inventoryService.inStock(skuCode);
    }


}
