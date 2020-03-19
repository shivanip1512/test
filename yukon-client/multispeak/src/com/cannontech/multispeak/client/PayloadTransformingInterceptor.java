package com.cannontech.multispeak.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.xml.transform.ResourceSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.cannontech.clientutils.YukonLogManager;

/**
 * This class takes the JAXB output, parses the document and writes a new document without the unused
 * namespace definitions (This class removed all the unused namespace generated in JAXB output )
 */
public class PayloadTransformingInterceptor extends LoggingInterceptor implements ClientInterceptor {

    private final static Logger log = YukonLogManager.getLogger(PayloadTransformingInterceptor.class);

    private Resource requestXslt;
    private Templates requestTemplates;

    public void setRequestXslt(Resource requestXslt) {
        this.requestXslt = requestXslt;
    }

    private void transformMessage(WebServiceMessage message, Transformer transformer) throws TransformerException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        transformer.transform(message.getPayloadSource(), new StreamResult(os));
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        transform(new StreamSource(is), message.getPayloadResult());
    }

    @Override
    public boolean handleFault(MessageContext arg0) throws WebServiceClientException {
        return false;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        if (requestTemplates != null) {
            WebServiceMessage request = messageContext.getRequest();
            Transformer transformer;
            try {
                transformer = requestTemplates.newTransformer();
                transformMessage(request, transformer);
            } catch (TransformerException e) {
                log.info("Request message is not transformed");
            }
        }

        logMessageSource("Request: ", getSource(messageContext.getRequest()));

        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {

        logMessageSource("Response: ", getSource(messageContext.getResponse()));

        MessageContextHolder.setMessageContext(messageContext);
        return false;
    }

    @PostConstruct
    public void init() throws Exception {
        TransformerFactory transformerFactory = getTransformerFactory();
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        if (requestXslt != null) {
            Source requestSource = new ResourceSource(xmlReader, requestXslt);
            requestTemplates = transformerFactory.newTemplates(requestSource);
        }
    }

    @Override
    public void afterCompletion(MessageContext arg0, Exception arg1) throws WebServiceClientException {

    }

}
