package com.cannontech.dr.rfn.service;

import java.io.InputStream;
import java.text.ParseException;

import com.cannontech.common.util.xml.SimpleXPathTemplate;

public interface ExiParsingService {

    /**
     * This method takes an encoded EXI message as a byte array and converts it
     *  into a SimpleXPathTemplate which can be queried using XPath statements.
     *  
     * @param payload The encoded EXI message as a byte array.
     * @param informingSchema The schema to use when inflating the full-text XML document.
     * @return SimpleXPathTemplate
     * @throws ParseException 
     */
    public SimpleXPathTemplate decodeExiMessage(byte[] payload, InputStream informingSchema) throws ParseException;

}
