package com.leyou.item.controller;


import com.leyou.item.entity.Item;
import com.leyou.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {


    @Autowired
    private ItemService itemService;

    @PostMapping("/save")
    public Item save(Item item){
        Item item1 = itemService.save(item);
        return item1;
    }

    @PostMapping("/save2")
    public ResponseEntity<Item> save2(Item item){
        Item item1 = itemService.save(item);
       return ResponseEntity.status(HttpStatus.CREATED).body(item1);

    }
}
