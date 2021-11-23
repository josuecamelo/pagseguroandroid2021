package com.josuecamelo.pagsegurocheckout;

/**
 * Use this enum to define the supported pagseguro countries<br/>
 * <br/>Author: Josué Camelo, 23/11/2021
 */
public enum PagSeguroCountry {

    /**
     * Name: Brasil, Code: BRA
     */
    BRASIL("Brasil", "BRA");
    private String countryName;
    private String countryCode;

    private PagSeguroCountry(String countryName, String countryCode) {
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    /**
     * @return the country name
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * @return the country code
     */
    public String getCountryCode() {
        return countryCode;
    }
}
