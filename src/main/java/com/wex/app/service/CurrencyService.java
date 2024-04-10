package com.wex.app.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wex.app.ApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * @author Anthony Merlo
 * @since 10/04/2024
 */
@Component
public class CurrencyService {

    private static final String EX_RATE_URL = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    public Double getExchangeRate(LocalDate date, String currency) {
        LocalDate dte = date.minusMonths(6);

        String params = UriUtils.encodePath(String.format("fields=country_currency_desc,exchange_rate,record_date&filter=country_currency_desc:in:(%s),record_date:gte:%s", currency, dte), "UTF-8");

        ExchangeRates r = getRates(String.format("%s?%s", EX_RATE_URL, params));

        if (r == null || r.data == null || r.data.length == 0) {
            throw new ApiException("No exchange rates available");
        }

        return Double.valueOf(r.data[0].exchange_rate);
    }

    private static ExchangeRates getRates(String url) {
        try {
            URL u = new URL(url);
            try (InputStream in = u.openStream()) {
                String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                return OBJECT_MAPPER.readValue(json, ExchangeRates.class);
            }
        } catch (IOException e) {
            throw new ApiException("Failed to fetch exchange rates", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ExchangeRates(Data[] data) {

    }

    record Data(String country_currency_desc, String exchange_rate, LocalDate record_date) {

    }
}
