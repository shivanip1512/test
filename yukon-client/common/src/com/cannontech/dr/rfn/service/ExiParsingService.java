package com.cannontech.dr.rfn.service;

import com.cannontech.common.exception.ParseExiException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;

public interface ExiParsingService {

    /**
     * This method takes an encoded EXI message as a byte array and converts it
     *  into a SimpleXPathTemplate which can be queried using XPath statements.
     *  
     * @param payload The encoded EXI message as a byte array.
     * @return SimpleXPathTemplate The fully decoded XML document converted to a SimpleXPathTemplate.
     * @throws ParseExiException If EXI payload cannot be properly expanded. 
     */
    public SimpleXPathTemplate parseRfLcrReading(byte[] payload) throws ParseExiException;

}
