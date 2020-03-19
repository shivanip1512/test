package com.cannontech.multispeak.client;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.Logger;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.TransformerObjectSupport;

import com.cannontech.clientutils.YukonLogManager;

public class LoggingInterceptor extends TransformerObjectSupport {

    private final static Logger log = YukonLogManager.getLogger(LoggingInterceptor.class);

    /**
     * Log intercepted Request/Response message in debug mode.
     */
    protected void logMessageSource(String logMessage, Source source) {
        if (log.isDebugEnabled()) {
            try {
                if (source != null) {
                    Transformer transformer = createNonIndentingTransformer();
                    StringWriter writer = new StringWriter();
                    transformer.transform(source, new StreamResult(writer));
                    String message = logMessage + writer.toString();
                    log.debug(message);
                }
            } catch (TransformerException e) {
                log.error("Message is not transformed " + e);
            }
        }
    }

    /**
     * Create Transformer object that is used while parsing XML.
     */
    private Transformer createNonIndentingTransformer() throws TransformerConfigurationException {
        Transformer transformer = createTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }

    /**
     * Get Source object from received webServiceMessage.
     */
    protected Source getSource(WebServiceMessage message) {
        if (message instanceof SoapMessage) {
            SoapMessage soapMessage = (SoapMessage) message;
            return soapMessage.getEnvelope().getSource();
        } else {
            return null;
        }
    }

}
