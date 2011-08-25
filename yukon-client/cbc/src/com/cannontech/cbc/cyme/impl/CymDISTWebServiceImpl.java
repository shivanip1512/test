package com.cannontech.cbc.cyme.impl;

import java.io.StringReader;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;

import com.cannontech.cbc.cyme.CymDISTWebService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.xml.SimpleXPathTemplate;

public class CymDISTWebServiceImpl implements CymDISTWebService {

    private RestOperations restTemplate;
    private ConfigurationSource configurationSource;

    private static String baseCymeURL;
    private static final String simulationURLpart = "/CYMDIST/SimulationsService.svc/rest/Simulation";
    private static final String runStudyURLEnd = "/Run/Study/";
    private static final String checkReportStatusURLEnd = "/Status/";
    private static final String generateResultSummaryURLend = "/Result/Summary/";
    private static final String generateReportURLpart = "/Report/";
    private static final Logger log = YukonLogManager.getLogger(CymDISTWebService.class);

    private static final Namespace cymeDcNamespace = Namespace.getNamespace("ns1", "http://schemas.datacontract.org/2004/07/Cyme.Services.CymDistService.DataContracts");
    private static final Namespace cymeDtoNamespace = Namespace.getNamespace("a", "http://schemas.datacontract.org/2004/07/Cyme.CymDist.Server.Domain.DataTransferObjects");

    public static Properties cymeDcProperties = new Properties();

    static {
        cymeDcProperties.put(cymeDcNamespace.getPrefix(), cymeDcNamespace.getURI());
        cymeDcProperties.put(cymeDtoNamespace.getPrefix(), cymeDtoNamespace.getURI());
    }

    @PostConstruct
    public void initialize() {
        boolean cymeEnabled = configurationSource.getBoolean("CYME_ENABLED", false);

        if (cymeEnabled) {
            baseCymeURL = configurationSource.getRequiredString("CYME_DIST_BASE_URL");
            log.info(baseCymeURL);
        }
    }

    @Override
    public String runSimulation(String xmlData) {
        // TODO Auto-generated method stub
        String response = restTemplate.postForObject(baseCymeURL + simulationURLpart + runStudyURLEnd, xmlData, String.class);
        log.info("Simulation ran on CYME"); 
        log.debug(response); 

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(response);
        template.setNamespaces(cymeDcProperties);

        String simulationId = template.evaluateAsString("//ns1:SimulationId");
        return simulationId;
    }

    @Override
    public boolean getSimulationReportStatus(String simulationId) {
        String response = restTemplate.getForObject(baseCymeURL + simulationURLpart + "/" + simulationId + checkReportStatusURLEnd, String.class);
        log.info("Checked Simulation Status with CYME"); 
        log.debug(response);
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(response);
        template.setNamespaces(cymeDcProperties);

        String status = template.evaluateAsString("/ns1:GetSimulationStatusResponse/ns1:Status");   

        if ("Completed".equals(status)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getSimulationReport(String simulationId) {
        // Generate Reports
        String summary = restTemplate.getForObject(baseCymeURL + simulationURLpart + "/" + simulationId + generateResultSummaryURLend, String.class);

        log.info("Generate reports command sent to CYME"); 
        log.debug(summary);

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(summary);
        template.setNamespaces(cymeDcProperties);

        String NetworkId = template.evaluateAsString("//ns1:NetworkId");
        String ReportId = template.evaluateAsString("//ns1:ReportId");

        // Retrieve reports
        String response = restTemplate.getForObject(baseCymeURL + simulationURLpart + "/" + simulationId
                                                    + generateReportURLpart + ReportId + "/" + NetworkId
                                                    + "/", String.class);
        log.info("Retrieved report from CYME"); 
        log.debug(response);
        
        return response;
    }

    @Autowired
    public void setRestTemplate(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
}
