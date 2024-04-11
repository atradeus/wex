package com.wex.app;

import com.wex.app.service.AppService;
import com.wex.app.service.AppService.Transaction;
import com.wex.app.service.CurrencyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Anthony Merlo
 * @since 10/04/2024
 */
class AppServiceTest {

    AppService appService;
    Path tmpPath;
    CurrencyService currencyService;

    @BeforeEach
    void setUp() throws IOException {
        currencyService = mock(CurrencyService.class);
        tmpPath = Files.createTempFile("tx", "csv");
        appService = new AppService(tmpPath, currencyService);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tmpPath);
    }

    @Test
    void persist() throws IOException {
        Transaction t = createTx();
        Transaction newTx = appService.persist(t);

        String file = Files.readString(tmpPath);
        String expected = String.format("%s,description,2024-01-01T00:00,1.01\n", newTx.id());

        assertEquals(expected, file);
    }

    @Test
    void get() {
        Transaction t = createTx();
        var newTx = appService.persist(t);

        given(currencyService.getExchangeRate(any(), anyString())).willReturn(2.0);

        var result = appService.get(newTx.id(), "United Kingdom-Pound");

        assertNotNull(result);
        assertEquals(newTx.id(), result.id());
        assertEquals(2.02, result.amount());
        assertEquals("description", result.description());
    }

    Transaction createTx() {
        var date = LocalDateTime.of(2024, 1, 1, 0, 0);
        return new Transaction(null, "description", date, 1.01001);
    }
}