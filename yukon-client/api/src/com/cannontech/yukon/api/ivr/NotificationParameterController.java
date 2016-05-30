package com.cannontech.yukon.api.ivr;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.xml.transform.StringSource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.yukon.INotifConnection;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/notification/parameter/*")
public class NotificationParameterController {
    private INotifConnection notifClientConnection;
    private Logger log = YukonLogManager.getLogger(NotificationParameterController.class);
    
    @RequestMapping("notifDocument")
    public void notifDocument(String callToken, Writer out) throws IOException {
        log.debug("received notifDocument request for " + callToken);
        // get all parameters as an XML document
        String requestMessage = notifClientConnection.requestMessage(callToken);
        out.write(requestMessage);
    }
    
    @RequestMapping("parameter")
    public void parameter(String callToken, String parameterName, Writer out) throws JDOMException, IOException {
        log.debug("received parameter request for " + callToken + " with parameterName=" + parameterName);
        // get named parameter as string
        String requestMessage = notifClientConnection.requestMessage(callToken);
        
        SimpleXPathTemplate simpleXPathTemplate = new SimpleXPathTemplate();
        simpleXPathTemplate.setContext(new StringSource(requestMessage));
        String parameterValue = simpleXPathTemplate.evaluateAsString("//" + parameterName);
        
        log.debug("parameter writing for " + callToken + " with parameterValue=" + parameterValue);
        out.write(parameterValue);
    }

    @RequestMapping("template")
    public void template(String callToken, String template, Writer out) throws JDOMException, IOException {
        log.debug("received template request for " + callToken + " with template=" + template);
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
        
        log.debug("template writing for " + callToken + " with result=" + result);
        out.write(result);
    }
    
    @Autowired
    public void setNotifClientConnection(INotifConnection notifClientConnection) {
        this.notifClientConnection = notifClientConnection;
    }
}
