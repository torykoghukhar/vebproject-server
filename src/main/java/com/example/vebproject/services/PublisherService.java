package com.example.vebproject.services;

import com.example.vebproject.models.Publisher;
import com.example.vebproject.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherService {
    private final PublisherRepository publisherRepository;
    @Autowired
    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;}
        public List<Publisher> getAllPublishers(){ return publisherRepository.findAll();}
}
