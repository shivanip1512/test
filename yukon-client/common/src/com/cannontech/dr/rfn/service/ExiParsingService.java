package com.cannontech.dr.rfn.service;

import java.io.IOException;

import org.openexi.proc.common.EXIOptionsException;
import org.openexi.sax.TransmogrifierException;


public interface ExiParsingService<T> extends ParsingService <T>{
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
}
