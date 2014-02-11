package com.cannontech.dr.rfn.service;

import java.io.IOException;

import org.openexi.proc.common.EXIOptionsException;
import org.openexi.sax.TransmogrifierException;

import com.cannontech.common.exception.ParseExiException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.dr.rfn.service.impl.ExiParsingServiceImpl.Schema;

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

    /**
     * This method encodes an XML payload into the EXI format.  It is only used for testing purposes within the
     * RFN LCR data simulator and should not be used in normal production situations.
     * The encoding uses the RFN LCR DR Report schema version 0.0.3, the most recent at the time of writing (2/10/2014).
     * 
     * @param xmlPayload The XML string to be encoded. See 
     * @return
     * @throws TransmogrifierException Thrown if there is an error during the conversion from XML to EXI.
     * @throws EXIOptionsException Thrown if there is a problem configuring the transmogrifier options.
     * @throws IOException Thrown if there is a problem reading from input or writing the output stream.
     */
    public byte[] encodePayload(String xmlPayload) throws TransmogrifierException, EXIOptionsException, IOException;
    
    public Schema getSchema(byte[] payload);
}
