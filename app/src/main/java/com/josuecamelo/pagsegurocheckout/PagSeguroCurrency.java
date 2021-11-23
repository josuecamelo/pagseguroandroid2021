package com.josuecamelo.pagsegurocheckout;

/**
 * Use this enum to define the supported pagseguro country currency<br/>
 * <br/>Author: Josué Camelo, 23/11/2021
 */
public enum PagSeguroCurrency {

    /**
     * Country: Brasil, Currency: Real, Code: BRL
     */
    BRASIL("Real", "BRL");
    private String currencyName;
    private String currencyCode;

    private PagSeguroCurrency(String currencyName, String currencyCode) {
        this.currencyName = currencyName;
        this.currencyCode = currencyCode;
    }

    /**
     * @return the currency name
     */
    public String getCurrencyName() {
        return currencyName;
    }

    /**
     * @return the currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
}
