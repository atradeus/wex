package com.wex.app.controller;

import com.wex.app.service.AppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.wex.app.service.AppService.Transaction;

/**
 * @author Anthony Merlo
 * @since 10/04/2024
 */
@RestController
public class AppController {

    private final AppService wexService;

    public AppController(AppService appService) {
        this.wexService = appService;
    }

    @PostMapping
    void add(@RequestBody Transaction tx) {
        wexService.persist(tx);
    }

    @GetMapping
    void get(UUID id) {

    }
}




