package com.wex.app.controller;

import com.wex.app.service.AppService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.wex.app.service.AppService.Transaction;
import static com.wex.app.service.AppService.TransactionResponse;

/**
 * @author Anthony Merlo
 * @since 10/04/2024
 */
@RestController
public class AppController {

    private final AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }

    @PostMapping
    UUID add(@RequestBody Transaction tx) {
        return appService.persist(tx).id();
    }

    @GetMapping("/{id}/{currency}")
    TransactionResponse get(@PathVariable String id, @PathVariable String currency) {
        return appService.get(UUID.fromString(id), currency);
    }
}




