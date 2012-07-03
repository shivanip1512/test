package com.cannontech.dr.rfn.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.dr.rfn.service.ExiParsingService;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXISource;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class ExiParsingServiceImpl implements ExiParsingService {
    private static final Logger log = YukonLogManager.getLogger(ExiParsingService.class);
    
    public SimpleXPathTemplate decodeExiMessage(byte[] payload, InputStream informingSchema) {
        // Set up input and output streams.
        ByteArrayInputStream bis = new ByteArrayInputStream(payload);
        StringWriter xmlWriter = new StringWriter();
        StreamResult output = new StreamResult(xmlWriter);
        
        // Create exiFactory based on the incoming message schema.
        EXIFactory exiFactory = DefaultEXIFactory.newInstance();
        GrammarFactory grammarFactory = GrammarFactory.newInstance();
        EXISource exiSource = null;
        try {
            Grammar g = grammarFactory.createGrammar(informingSchema);
            exiFactory.setGrammar(g);
            exiSource = new EXISource(exiFactory);
        } catch (EXIException e) {
            log.error("Unable to create EXI source from informing schema: " + informingSchema.toString());
            e.printStackTrace();
        }
        
        // Create and configure saxSource with XML reader.
        XMLReader xmlReader = exiSource.getXMLReader();
        SAXSource saxSource = new SAXSource(new InputSource(bis));
        saxSource.setXMLReader(xmlReader);

        // Perform decoding.
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            transformer.transform(saxSource, output);
        } catch (TransformerException e) {
            log.error("Unable to decode EXI message.");
            e.printStackTrace();
        }
        
        // Convert transformed output to SimpleXPathTemplate.
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(xmlWriter.toString());
        return template;
    }
}
