package com.josuecamelo.pagsegurocheckout;

/**
 * Use this enumeration to define the buyer's shipping option<br/>
 * <br/>Author: Josué Camelo, 23/11/2021
 */
public enum PagSeguroShippingType {
    /** Normal post delivery service.*/
    PAC(1),
    /** Post express currier. */
    SEDEX(2),
    /** Undefined shipping option. */
    NOT_DEFINED(3);

    private int shippingTypeRepresentation;
    private PagSeguroShippingType(int typeRepresentation){
        this.shippingTypeRepresentation = typeRepresentation;
    }

    /** returns the integer that represents the enumeration*/
    public String getShippingTypeRepresentation(){
       return String.valueOf(this.shippingTypeRepresentation);
    }
}
