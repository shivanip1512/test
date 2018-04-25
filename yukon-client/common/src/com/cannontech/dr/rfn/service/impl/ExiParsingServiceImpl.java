package com.cannontech.dr.rfn.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.Logger;
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
import com.cannontech.common.exception.ParseException;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.dr.rfn.service.ExiParsingService;
import com.cannontech.dr.rfn.service.ParsingService;

public class ExiParsingServiceImpl implements ExiParsingService <SimpleXPathTemplate> {
    
    @Autowired ResourceLoader loader;
    private static final Logger log = YukonLogManager.getLogger(ExiParsingServiceImpl.class);
    private static final Logger rfnLogger = YukonLogManager.getRfnLogger();

    private Map<Schema, EXISchema> schemas = new HashMap<Schema, EXISchema>();
    
    @Override
    public SimpleXPathTemplate parseRfLcrReading(RfnIdentifier rfnId, byte[] payload) {

        byte[] data = Arrays.copyOf(payload, payload.length);
        if (data[0] == expresscomPayloadHeader) {
            // Trim 3-byte Expresscom header from the payload
            data = Arrays.copyOfRange(data, 3, data.length);
        }

        //Get the schema version from the header
        byte[] header = Arrays.copyOfRange(data, 0, 4);
        Schema schema = ParsingService.getSchema(header);
        EXISchema exiSchema = getExiSchema(schema);
        
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
        byte[] report = Arrays.copyOfRange(data, 4, data.length);
        
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
        } catch (IOException | SAXException | ArrayIndexOutOfBoundsException e) {
            throw new ParseException("Error while parsing the RF LCR payload.", e);
        } catch (OutOfMemoryError e) {
            // While not normally done, we catch OOM here since the error can't be handled at a lower level, it isn't
            // affecting application stability, and the memory footprint does not grow when this is reached.
            throw new ParseException("Out of heap memory when parsing EXI payload, likely due to malformed EXI encoded data.", e);
        }
       
        final String reconstitutedString = xmlWriter.getBuffer().toString();
        if (rfnLogger.isDebugEnabled()) {
            rfnLogger.debug("    device: " + rfnId + " payload: " + reconstitutedString);
        }
        
        // Convert transformed output to SimpleXPathTemplate.
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(reconstitutedString);
        return template;
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
    private EXISchema getExiSchema(Schema schema) {
        // Serialize the EXI schemas once and then re-use it
        if (schemas.isEmpty()) {
            EXISchemaFactory factory = new EXISchemaFactory();
            for (Schema location : Schema.values()) {
                //allowing only EXI Schema
                if (location == Schema.SCHEMA_0_0_3 || location == Schema.SCHEMA_0_0_2) {
                    try {
                        Resource informingSchemaResource = loader.getResource(location.getLocation());
                        InputSource is = new InputSource(informingSchemaResource.getInputStream());
                        EXISchema exiSchema = factory.compile(is);
                        schemas.put(location, exiSchema);
                    } catch (EXISchemaFactoryException | IOException e) {
                        throw new RuntimeException("Error creating EXI schema version:"
                                                   + location.getVersion() + " location:"
                                                   + location.getLocation(), e);
                    }
                }
            }
        }
        EXISchema exiSchema = schemas.get(schema);
        if (exiSchema == null) {
            throw new RuntimeException("There is no EXI schema for version " + schema.getVersion());
        }
        log.debug("Retrieved schema version: " + schema.getVersion());
        return exiSchema;
    }

    @Override
    public byte[] encodePayload(String xmlPayload) throws TransmogrifierException, EXIOptionsException, IOException {
        EXISchema schema = getExiSchema(Schema.SCHEMA_0_0_3);
        short defaultOptions = GrammarOptions.DEFAULT_OPTIONS;
        GrammarCache grammarCache = new GrammarCache(schema, defaultOptions);
        
        Transmogrifier transmogrifier = new Transmogrifier();
        transmogrifier.setEXISchema(grammarCache);
        byte[] output;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
                StringReader input = new StringReader(xmlPayload)) {
            transmogrifier.setOutputStream(outputStream);
            InputSource inputSource = new InputSource(input);
            transmogrifier.encode(inputSource);
            byte[] byteArray = outputStream.toByteArray();
            output = Arrays.copyOf(byteArray, byteArray.length);
        }
        return output;
    }

}
