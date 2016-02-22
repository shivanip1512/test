package com.cannontech.dr.rfn.service;

import java.io.IOException;
import java.util.Map;

import org.openexi.proc.common.EXIOptionsException;
import org.openexi.sax.TransmogrifierException;

import com.cannontech.common.exception.ParseExiException;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.google.common.collect.ImmutableMap;

public interface ExiParsingService {

    enum Schema {
        SCHEMA_0_0_2("0.0.2", "rfnLcrExiMessageSchema_v0_0_2.xsd"),
        SCHEMA_0_0_3("0.0.3", "rfnLcrExiMessageSchema_v0_0_3.xsd");
       
        private final String version;
        private final String schema;
        private static final String classpath = "classpath:com/cannontech/dr/rfn/endpoint/";
        private static final Map<String, Schema> lookupByVersion;
        
        static {
            ImmutableMap.Builder<String, Schema> builder = ImmutableMap.builder();
            for (Schema schema : values()) {
                builder.put(schema.getVersion(), schema);
            }
            lookupByVersion = builder.build();
        }
        
        private Schema(String version, String schema) {
            this.version = version;
            this.schema = schema;
        }

        public String getVersion() {
            return version;
        }

        public String getLocation() {
            return classpath + schema;
        } 
        
        public boolean supportsBroadcastVerificationMessages() {
           return this == SCHEMA_0_0_3;
        }

        public static Schema getSchema(String schemaVersion){
            return lookupByVersion.get(schemaVersion);
        }
    }
    
    /**
     * This method takes an encoded EXI message as a byte array and converts it
     *  into a SimpleXPathTemplate which can be queried using XPath statements.
     *  
     * @param payload The encoded EXI message as a byte array.
     * @return SimpleXPathTemplate The fully decoded XML document converted to a SimpleXPathTemplate.
     * @throws ParseExiException If EXI payload cannot be properly expanded. 
     */
    public SimpleXPathTemplate parseRfLcrReading(RfnIdentifier rfnId, byte[] payload) throws ParseExiException;

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
    
    Schema getSchema(byte[] payload);
}
