package com.projexion.api.pdf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdfEntity {

    private String clientName;
    private String clientAddress;
    private String clientCity;
    private String fiscalCode;
    private String vatNumber;

    private String city;
    private String date;
    private String invoiceNumber;
    private String invoiceTitle;

    private String bankName;
    private String iban;
    private String swift;

    private BigDecimal exchangeRate; // Optional: used to calculate CHF equivalent
    private List<PriceItemDto> priceList;

    // Optional helper methods for calculations
    public BigDecimal getTotalEur() {
        if (priceList == null || priceList.isEmpty()) return BigDecimal.ZERO;
        return priceList.stream()
                .map(PriceItemDto::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalChf() {
        if (exchangeRate == null) return getTotalEur();
        return getTotalEur().multiply(exchangeRate);
    }

    // Inner static class for price items
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceItemDto {
        private String description;
        private BigDecimal price;
    }
}