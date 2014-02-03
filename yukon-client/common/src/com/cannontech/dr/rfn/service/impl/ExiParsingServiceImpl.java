package com.cannontech.dr.rfn.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Arrays;
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
import org.openexi.sax.Transmogrifier;
import org.openexi.sax.TransmogrifierException;
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
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class ExiParsingServiceImpl implements ExiParsingService {
    
    enum SchemaLocation {
        SCHEMA_0_0_2("0.0.2", "rfnLcrExiMessageSchema_v0_0_2.xsd"),
        SCHEMA_0_0_3("0.0.3", "rfnLcrExiMessageSchema_v0_0_3.xsd");
       
        private final String version;
        private final String schema;
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
    
    private final static byte payloadType = (byte) 0xE2;
    
    @Autowired ResourceLoader loader;
    private static final Logger log = YukonLogManager.getLogger(ExiParsingService.class);
    
    private Map<String, EXISchema> schemas = new HashMap<String, EXISchema>();
    
    public SimpleXPathTemplate parseRfLcrReading(byte[] payload) {
    	
    	if(payload[0] == payloadType){
    		// Trim 3-byte Expresscom header from the payload
    		payload = Arrays.copyOfRange(payload, 3, payload.length);
    	}
    	
        //Get the schema version from the header
        byte[] header = Arrays.copyOfRange(payload, 0, 4);
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
        
        // Get report without 4-byte header
        byte[] report = Arrays.copyOfRange(payload, 4, payload.length);
        
        // Set up input and output streams.
        ByteArrayInputStream bis = new ByteArrayInputStream(report);
        StringWriter xmlWriter = new StringWriter();
        StreamResult output = new StreamResult(xmlWriter);

        // Set up standard SAX objects for XML parsing.
        SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        
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
    private String getSchemaVersion(byte[] exiHeader){
    	ByteBuffer header = ByteBuffer.wrap(exiHeader);
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

    @Override
    public byte[] encodePayload(String xmlPayload) throws TransmogrifierException, EXIOptionsException, IOException {
        EXISchema schema = getLcrReadingSchema("0.0.2");
        short defaultOptions = GrammarOptions.DEFAULT_OPTIONS;
        GrammarCache grammarCache = new GrammarCache(schema, defaultOptions);
        
        Transmogrifier transmogrifier = new Transmogrifier();
        transmogrifier.setEXISchema(grammarCache);
        
        ByteOutputStream outputStream = new ByteOutputStream();
        transmogrifier.setOutputStream(outputStream);

        StringReader input = new StringReader(xmlPayload);
        InputSource inputSource = new InputSource(input);
        transmogrifier.encode(inputSource);
        
        ByteBuffer buffer = ByteBuffer.allocate(outputStream.getCount());
        buffer.put(outputStream.getBytes(), 0, outputStream.getCount());
        buffer.rewind();
        byte[] output = new byte[outputStream.getCount()];
        buffer.get(output, 0, outputStream.getCount());
        return output;
    }

}
