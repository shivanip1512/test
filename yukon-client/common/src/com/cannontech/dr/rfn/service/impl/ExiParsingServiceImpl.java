package com.cannontech.dr.rfn.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;

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
    @Autowired ResourceLoader loader;
    private static final Logger log = YukonLogManager.getLogger(ExiParsingService.class);
    
    private static final String informingSchemaLocation = "classpath:com/cannontech/dr/rfn/endpoint/rfnLcrExiMessageSchema.xsd";
    private EXISchema lcrReadingSchema = null;
    
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
        
        // Create grammar using RF LCR reading schema & default options.
        short options = GrammarOptions.DEFAULT_OPTIONS;
        GrammarCache grammar = new GrammarCache(getLcrReadingSchema(), options);
        
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
    private EXISchema getLcrReadingSchema() {
        // Serialize the EXI schema once and then re-use it
        if (lcrReadingSchema == null) {
            EXISchemaFactory factory = new EXISchemaFactory();
            EXISchema schema = null;
            try {
                Resource informingSchemaResource = loader.getResource(informingSchemaLocation);
                InputSource is = new InputSource(informingSchemaResource.getInputStream());
                schema = factory.compile(is);
                lcrReadingSchema = schema;
            } catch (EXISchemaFactoryException | IOException e) {
                throw new RuntimeException("Error creating EXI schema.", e);
            }
        } 
        return lcrReadingSchema;
    }

}
