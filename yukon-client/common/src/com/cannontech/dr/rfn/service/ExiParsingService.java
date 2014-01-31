package com.cannontech.dr.rfn.service;

import java.io.IOException;

import org.openexi.proc.common.EXIOptionsException;
import org.openexi.sax.TransmogrifierException;

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

    public byte[] encodePayload(String xmlPayload) throws TransmogrifierException, EXIOptionsException, IOException;
}
