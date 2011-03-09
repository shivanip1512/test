package com.cannontech.yukon.api.ivr;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.xml.transform.StringSource;

import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.yukon.INotifConnection;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/notification/parameter/*")
public class NotificationParameterController {
    private INotifConnection notifClientConnection;
    
    @RequestMapping
    public void notifDocument(String callToken, Writer out) throws IOException {
        // get all parameters as an XML document
        String requestMessage = notifClientConnection.requestMessage(callToken);
        out.write(requestMessage);
    }
    
    @RequestMapping
    public void parameter(String callToken, String parameterName, Writer out) throws JDOMException, IOException {
        // get named parameter as string
        String requestMessage = notifClientConnection.requestMessage(callToken);
        
        SimpleXPathTemplate simpleXPathTemplate = new SimpleXPathTemplate();
        simpleXPathTemplate.setContext(new StringSource(requestMessage));
        String parameterValue = simpleXPathTemplate.evaluateAsString("//" + parameterName);
        out.write(parameterValue);
    }

    @RequestMapping
    public void template(String callToken, String template, Writer out) throws JDOMException, IOException {
        // get parsed template value
        String requestMessage = notifClientConnection.requestMessage(callToken);
        Document document = new SAXBuilder().build(new StringReader(requestMessage));
        List<?> types = document.getRootElement().getChildren();
        Element type = (Element) types.get(0);
        List<?> children = type.getChildren();
        
        Map<String, String> parameterMap = Maps.newHashMap();
        
        for (Object elementObj : children) {
            Element element = (Element) elementObj;
            String name = element.getName();
            String value = element.getTextTrim();
            parameterMap.put(name, value);
        }
        
        SimpleTemplateProcessor simpleTemplateProcessor = new SimpleTemplateProcessor();
        String result = simpleTemplateProcessor.process(template, parameterMap);
        
        out.write(result);
    }
    
    @Autowired
    public void setNotifClientConnection(INotifConnection notifClientConnection) {
        this.notifClientConnection = notifClientConnection;
    }
}
