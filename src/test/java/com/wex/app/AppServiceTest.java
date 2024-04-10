package com.wex.app;

//import org.assertj.core.util.Files;

import com.wex.app.service.AppService;
import com.wex.app.service.CurrencyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.mock;

/**
 * @author Anthony Merlo
 * @since 10/04/2024
 */
class AppServiceTest {

    private AppService appService;
    private Path tmpPath;

    @BeforeEach
    void setUp() throws IOException {
        CurrencyService currencyService = mock(CurrencyService.class);
        tmpPath = Files.createTempFile("tx", "csv");
        appService = new AppService(tmpPath, currencyService);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tmpPath);
    }

    @Test
    void persist() {


    }

    @Test
    void get() {
    }
}