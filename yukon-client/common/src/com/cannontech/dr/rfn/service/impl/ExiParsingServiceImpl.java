package com.cannontech.dr.rfn.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.openexi.proc.common.EXIOptionsException;
import org.openexi.proc.common.GrammarOptions;
import org.openexi.proc.grammars.GrammarCache;
import org.openexi.sax.EXIReader;
import org.openexi.schema.EXISchema;
import org.openexi.scomp.EXISchemaFactory;
import org.openexi.scomp.EXISchemaFactoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.ParseExiException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.dr.rfn.service.ExiParsingService;

public class ExiParsingServiceImpl implements ExiParsingService {
    
    enum SchemaLocation {
        SCHEMA_0_0_2("0.0.2", "rfnLcrExiMessageSchema_v0_0_2.xsd"),
        SCHEMA_0_0_3("0.0.3", "rfnLcrExiMessageSchema_v0_0_3.xsd");
       
        private String version;
        private String schema;
        private static final String classpath = "classpath:com/cannontech/dr/rfn/endpoint/";

        private SchemaLocation(String version, String schema) {
            this.version = version;
            this.schema = schema;     
        }

        public String getVersion() {
            return version;
        }

        public String getLocation() {
            return classpath + schema;
        }  
    }
    
    @Autowired ResourceLoader loader;
    private static final Logger log = YukonLogManager.getLogger(ExiParsingService.class);
    
    private Map<String, EXISchema> schemas = new HashMap<String, EXISchema>();
    
    public SimpleXPathTemplate parseRfLcrReading(byte[] payload) {
        // This removes the 4-byte header from the payload.
        // This will need to be handled differently to allow handling of multiple schema versions.
        ByteBuffer trimmedPayloadBuffer = ByteBuffer.allocate(payload.length-4);
        trimmedPayloadBuffer.put(payload, 4, payload.length-4);
        byte[] trimmedPayload = new byte[payload.length-4];
        trimmedPayloadBuffer.rewind();
        trimmedPayloadBuffer.get(trimmedPayload);
        
        // Set up input and output streams.
        ByteArrayInputStream bis = new ByteArrayInputStream(trimmedPayload);
        StringWriter xmlWriter = new StringWriter();
        StreamResult output = new StreamResult(xmlWriter);

        // Set up standard SAX objects for XML parsing.
        SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        
        //Get the schema version from the header
        ByteBuffer header = ByteBuffer.allocate(4);
        header.put(payload, 0, 4);
        String schemaVersion = getSchemaVersion(header);
        EXISchema exiSchema = getLcrReadingSchema(schemaVersion);
        
        // Create grammar using RF LCR reading schema & default options.
        short options = GrammarOptions.DEFAULT_OPTIONS;
        GrammarCache grammar = new GrammarCache(exiSchema, options);
        
        // Create reader object, which is informed by the LCR reading schema.
        EXIReader reader = new EXIReader();
        try {
            reader.setEXISchema(grammar);
        } catch (EXIOptionsException e) {
            throw new RuntimeException("Cannot create EXI schema from .xsd file.", e);
        }
        
        try {
            // Set up output StreamResult.
            TransformerHandler transformerHandler = saxTransformerFactory.newTransformerHandler();
            // Prepare to send the results from the transformer to a StringWriter object.
            transformerHandler.setResult(output);
            // Assign the transformer handler to interpret XML content.
            reader.setContentHandler(transformerHandler);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Error setting up the transformer for XML conversion.", e);
        }

        // Parse the incoming data
        try {
            reader.parse(new InputSource(bis));
        } catch (IOException | SAXException e) {
            throw new ParseExiException("Error while parsing the RF LCR payload.", e);
        }
       
        if (log.isDebugEnabled()) {
            final String reconstitutedString = xmlWriter.getBuffer().toString();
            log.debug("Incoming RF LCR payload xml: " + reconstitutedString);
        }
        
        // Convert transformed output to SimpleXPathTemplate.
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(xmlWriter.getBuffer().toString());
        return template;
    }
    
    /**
     * This method is used to parse the schema version from the header and return a schema version in X.X.X format.
     */
    private String getSchemaVersion(ByteBuffer header){
        StringBuilder version = new StringBuilder();
        ByteBuffer majorMinorBuffer = ByteBuffer.allocate(1);
        majorMinorBuffer.put(header.array(), 2, 1);
        //majorMinorBuffer.put((byte) 0xEA); to test
        int major = (majorMinorBuffer.get(0) & 0xF0) >>> 4;
        int minor = majorMinorBuffer.get(0) & 0x0F;
        version.append(major);
        version.append(".");
        version.append(minor);
        version.append(".");
        ByteBuffer revisionBuffer = ByteBuffer.allocate(1);
        revisionBuffer.put(header.array(), 3, 1);
        version.append(revisionBuffer.get(0));        
        log.debug("Parsed schema version: " + version);
        return version.toString();
    }
    
    /**
     * This method is used to convert the .xsd file that defines the format of the
     * incoming RF LCR 'Read Now' data.  The xsd needs to be transformed into an
     * EXISchema object so that during transformation it can inform the EXI reader
     * about the structure of the encoded XML data.
     * 
     * The first time this is called the EXISchema is cached so the file is only
     * processed once.
     * 
     * @return The EXISchema object representation of the file rfnLcrExiMessageSchema.xsd.
     */
    private EXISchema getLcrReadingSchema(String version) {
        // Serialize the EXI schemas once and then re-use it
        if (schemas.isEmpty()) {
            EXISchemaFactory factory = new EXISchemaFactory();
            for (SchemaLocation location : SchemaLocation.values()) {
                try {
                    Resource informingSchemaResource = loader.getResource(location.getLocation());
                    InputSource is = new InputSource(informingSchemaResource.getInputStream());
                    EXISchema schema = factory.compile(is);
                    schemas.put(location.getVersion(), schema);
                } catch (EXISchemaFactoryException | IOException e) {
                    throw new RuntimeException("Error creating EXI schema version:"
                                               + location.getVersion() + " location:"
                                               + location.getLocation(), e);
                }
            }
        }
        EXISchema schema = schemas.get(version);
        if(schema == null){
            throw new RuntimeException("There is no EXI schema for version "+version);
        }
        log.debug("Retrieved schema version: " + version);
        return schema;
    }

}
