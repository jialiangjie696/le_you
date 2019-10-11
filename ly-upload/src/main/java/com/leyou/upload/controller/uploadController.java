package com.leyou.upload.controller;


import com.leyou.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class uploadController {


    @Autowired
    private UploadService uploadService;

    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file){


           String  imageUrl =  uploadService.upload(file);

        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/signature")
    public ResponseEntity<Map<String,Object>> signature(){

        Map<String,Object>  map=  uploadService.signature();


        return ResponseEntity.ok(map);
    }

}
