package com.example.vebproject.controllers;

import com.example.vebproject.models.Publisher;
import com.example.vebproject.services.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PublisherController {
    private final PublisherService publisherService;
    @Autowired
    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }
    @GetMapping("/getallpublishers")
    public ResponseEntity<?> getAllPublishers(){
        List<Publisher> publishers = publisherService.getAllPublishers();
        if(publishers.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(publishers);
    }
}
