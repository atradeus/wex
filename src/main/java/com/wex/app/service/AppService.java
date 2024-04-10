package com.wex.app.service;

import com.wex.app.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
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
            ZonedDateTime date,
            Double amount) {

        // copies a transaction but with new ID
        public Transaction(Transaction t) {
            this(UUID.randomUUID(), t.description, t.date, t.amount);
        }

        public String toCsvString() {
            return format("%s,%s,%s,%s", id, description, date, amount);
        }
    }

    public record TransactionResponse(
            UUID id,
            String description,
            ZonedDateTime date,
            Double usdAmount,
            Double amount) {
        public TransactionResponse(Transaction t, Double exchangeRate) {
            this(t.id, t.description, t.date, t.amount, exchangeRate * t.amount);
        }
    }

    private final Path txFilePath;
    private final CurrencyService currencyService;

    public AppService(@Value("${tx.file.path}") Path txFilePath, CurrencyService currencyService) {
        this.txFilePath = txFilePath;
        this.currencyService = currencyService;
    }

    public void persist(Transaction t) {
        Transaction tx = new Transaction(t);

        System.out.printf("persisting %s\n", tx);

        if (Files.notExists(txFilePath)) {
            try {
                Files.createFile(txFilePath);
            } catch (IOException e) {
                throw new ApiException("Failed to create file", e);
            }
        }

        try {
            Files.write(txFilePath, format("%s\n ", tx.toCsvString()).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new ApiException("Failed to persist data to file", e);
        }
    }

    public TransactionResponse get(UUID id, String currency) {
        try (Stream<String> lines = Files.lines(txFilePath)) {
            return lines
                    .map(row -> {
                        var v = row.split(",");
                        return new Transaction(UUID.fromString(v[0]), v[1], ZonedDateTime.parse(v[2]), Double.valueOf(v[3]));
                    })
                    .filter(t -> t.id().equals(id))
                    .findFirst()
                    .map(t -> new TransactionResponse(t, currencyService.getExchangeRate(t.date().toLocalDate(), currency)))
                    .orElseThrow(() -> new ApiException("No transaction found for id=" + id));
        } catch (IOException e) {
            throw new ApiException("Failed to open transaction file");
        }
    }
}
