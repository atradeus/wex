package com.wex.app;

import com.wex.app.service.CurrencyService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Anthony Merlo
 * @since 10/04/2024
 */
class CurrencyServiceTest {

    @Test
    void testExchangeRate() {
        CurrencyService c = new CurrencyService();
        Double exchangeRate = c.getExchangeRate(LocalDate.now().minusDays(10), "United Kingdom-Pound");
        assertNotNull(exchangeRate);
    }
}