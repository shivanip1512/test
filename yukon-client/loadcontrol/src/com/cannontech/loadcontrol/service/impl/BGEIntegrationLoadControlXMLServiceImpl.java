package com.cannontech.loadcontrol.service.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.loadcontrol.service.BGEIntegrationLoadControlXMLService;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.TimeoutException;

public class BGEIntegrationLoadControlXMLServiceImpl implements BGEIntegrationLoadControlXMLService {

    private LoadControlService loadControlService;
    
    //TODO userContext required for format?
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    // GET PROGRAM SATUS BY PROGRAM NAME
    public ProgramStatus getProgramStatusByProgramName(String xml) throws IOException, JDOMException, IllegalArgumentException {
        
        Element rootElement = getRootElementFromXmlString(xml);
        validateServiceName(rootElement, "programStatusRequest");
        Namespace ns = rootElement.getNamespace();
        
        String programName = rootElement.getChild("programName", ns).getTextTrim();
        ProgramStatus programStatus = loadControlService.getProgramStatusByProgramName(programName);
        
        return programStatus;
    }

    public List<ProgramStatus> getAllCurrentlyActivePrograms(String xml) throws IOException, IllegalArgumentException, JDOMException {
        
        Element rootElement = getRootElementFromXmlString(xml);
        validateServiceName(rootElement, "currentlyActiveProgramsRequest");
     
        return loadControlService.getAllCurrentlyActivePrograms();
    }
    
    public ProgramStatus startControlByProgramName(String xml) throws IOException, JDOMException, ParseException, IllegalArgumentException, TimeoutException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ProgramControlHistory> getControlHistoryByProgramName(String xml)
            throws IOException, JDOMException, IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScenarioProgramStartingGears getScenarioProgramStartingGearsByScenarioName(
            String xml) throws IOException, JDOMException,
            IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScenarioStatus startControlByScenarioName(String xml) throws IOException, JDOMException, ParseException, IllegalArgumentException, TimeoutException {

        Element rootElement = getRootElementFromXmlString(xml);
        validateServiceName(rootElement, "scenarioStartRequest");
        Namespace ns = rootElement.getNamespace();
        
        String scenarioName = rootElement.getChild("scenarioName", ns).getTextTrim();
        
        Date startDateTime = new Date();
        Element startDateTimeElement = rootElement.getChild("startDateTime", ns);
        if (startDateTimeElement != null) {
            String startDateTimeStr = startDateTimeElement.getTextTrim();
            startDateTime = dateFormatter.parse(startDateTimeStr);
        }
        
        //TODO how is no end date really handled?
        Date stopDateTime = null;
        Element stopDateTimeElement = rootElement.getChild("stopDateTime", ns);
        if (stopDateTimeElement != null) {
            String stopDateTimeStr = stopDateTimeElement.getTextTrim();
            stopDateTime = dateFormatter.parse(stopDateTimeStr);
        }
        
        ScenarioStatus scenarioStatus = loadControlService.startControlByScenarioName(scenarioName, startDateTime, stopDateTime, false);
        
        return scenarioStatus;
    }

    @Override
    public ProgramStatus stopControlByProgramName(String xml)
            throws IOException, JDOMException, ParseException, IllegalArgumentException,
            TimeoutException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScenarioStatus stopControlByScenarioName(String xml)
            throws IOException, JDOMException, ParseException, IllegalArgumentException,
            TimeoutException {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    //==============================================================================================
    // PRIVATE HELPER METHODS
    ///=============================================================================================
    private void validateServiceName(Element rootElement, String expectedServiceName) {
        
        String givenServiceName = rootElement.getName();
        
        if (!givenServiceName.equalsIgnoreCase(expectedServiceName)) {
            throw new IllegalArgumentException("Invalid Service Name For Method: " + givenServiceName);
        }
    }
    
    private Element getRootElementFromXmlString(String xml) throws IOException, JDOMException {
        
        Reader stream = new StringReader(xml);
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(stream);
        Element rootElement = document.getRootElement();
        
        return rootElement;
    }
    
    @Autowired
    public void setLoadControlService(LoadControlService loadControlService) {
        this.loadControlService = loadControlService;
    }

}
