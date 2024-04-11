package com.wex.app.service;

import com.wex.app.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * @author Anthony Merlo
 * @since 10/04/2024
 */
@Component
public class AppService {

    public record Transaction(
            UUID id,
            String description,
            LocalDateTime date,
            Double amount) {

        // copies a transaction but with new ID
        public Transaction(Transaction t) {
            this(UUID.randomUUID(), t.description, t.date, round(t.amount));
        }
    }

    public record TransactionResponse(
            UUID id,
            String description,
            LocalDateTime date,
            Double usdAmount,
            Double amount) {
        public TransactionResponse(Transaction t, Double exchangeRate) {
            this(t.id, t.description, t.date, round(t.amount), round(exchangeRate * t.amount));
        }
    }

    private final Path txFilePath;
    private final CurrencyService currencyService;

    public AppService(@Value("${tx.file.path}") Path txFilePath, CurrencyService currencyService) {
        this.txFilePath = txFilePath;
        this.currencyService = currencyService;
    }

    public Transaction persist(Transaction t) {
        if (t.description.length() > 50) {
            throw new ApiException("Error: Description must not exceed 50 characters");
        }

        if (t.amount <= 0) {
            throw new ApiException("Error: Purchase amount must be a valid positive number");
        }

        Transaction tx = new Transaction(t);
        if (Files.notExists(txFilePath)) {
            try {
                Files.createFile(txFilePath);
            } catch (IOException e) {
                throw new ApiException("Failed to create file", e);
            }
        }

        try {
            String csvRow = format("%s,%s,%s,%s\n", tx.id, tx.description, tx.date, tx.amount);
            Files.write(txFilePath, csvRow.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new ApiException("Failed to persist data to file", e);
        }

        return tx;
    }

    public TransactionResponse get(UUID id, String currency) {
        try (Stream<String> lines = Files.lines(txFilePath)) {
            return lines
                    .map(row -> {
                        var v = row.split(",");
                        return new Transaction(UUID.fromString(v[0]), v[1], LocalDateTime.parse(v[2]), Double.valueOf(v[3]));
                    })
                    .filter(t -> t.id().equals(id))
                    .findFirst()
                    .map(t -> new TransactionResponse(t, currencyService.getExchangeRate(t.date().toLocalDate(), currency)))
                    .orElseThrow(() -> new ApiException("No transaction found for id=" + id));
        } catch (IOException e) {
            throw new ApiException("Failed to open transaction file");
        }
    }

    private static double round(double val) {
        val = Math.round(val * 100);
        return val / 100;
    }
}
