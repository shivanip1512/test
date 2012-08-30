package com.cannontech.cbc.cyme.impl;

import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;
import org.springframework.xml.xpath.NodeMapper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.cbc.cyme.CymeWebService;
import com.cannontech.cbc.cyme.model.CymeSimulationStatus;
import com.cannontech.cbc.cyme.model.SimulationResultSummaryData;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.thirdparty.digi.model.FileData;

public class CymeWebServiceImpl implements CymeWebService {
    
    private RestOperations restTemplate;    /*Autowired by Setter*/
    @Autowired private ConfigurationSource configurationSource;
    
    private static String baseCymeURL;
    private static final String simulationURLpart = "/CYMDIST/SimulationsService.svc/rest/Simulation";
    private static final String runStudyURLEnd = "/Run/Study/";
    private static final String checkReportStatusURLEnd = "/Status/";
    private static final String generateResultSummaryURLend = "/Result/Summary/";
    private static final String generateReportURLpart = "/Report/";
    private static final Logger log = YukonLogManager.getLogger(CymeWebService.class);

    private static final Namespace cymeDcNamespace = Namespace.getNamespace("ns1", "http://schemas.datacontract.org/2004/07/Cyme.Services.CymDistService.DataContracts");
    private static final Namespace cymeDtoNamespace = Namespace.getNamespace("a", "http://schemas.datacontract.org/2004/07/Cyme.CymDist.Server.Domain.DataTransferObjects");

    public static Properties cymeDcProperties = new Properties();

    static {
        cymeDcProperties.put(cymeDcNamespace.getPrefix(), cymeDcNamespace.getURI());
        cymeDcProperties.put(cymeDtoNamespace.getPrefix(), cymeDtoNamespace.getURI());
    }

    @PostConstruct
    public void initialize() {
        baseCymeURL = configurationSource.getRequiredString("CYME_DIST_BASE_URL");
        log.info(baseCymeURL);
    }

    @Override
    public String runSimulation(String xmlData) {
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
    public CymeSimulationStatus getSimulationReportStatus(String simulationId) {
        String response = restTemplate.getForObject(baseCymeURL + simulationURLpart + "/" + simulationId + checkReportStatusURLEnd, String.class);
        log.info("Checked Simulation Status with CYME"); 
        log.debug(response);
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(response);
        template.setNamespaces(cymeDcProperties);

        String statusStr = template.evaluateAsString("/ns1:GetSimulationStatusResponse/ns1:Status");   
        CymeSimulationStatus status = CymeSimulationStatus.getFromCymeValue(statusStr);

        return status;
    }

    private static ObjectMapper<Node, SimulationResultSummaryData> simulationResultSummaryDataRowMapper = new ObjectMapper<Node,SimulationResultSummaryData>() {

        @Override
        public SimulationResultSummaryData map(Node node) throws DOMException {                            
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(node);
            template.setNamespaces(cymeDcProperties);
            
            String networkId = template.evaluateAsString("//ns1:NetworkId");
            String reportId = template.evaluateAsString("//ns1:ReportId");
            String simulationId = template.evaluateAsString("//ns1:SimulationId");

            SimulationResultSummaryData data = new SimulationResultSummaryData(networkId,reportId,simulationId);
            
            return data;
        }
    };
    
    @Override
    public List<SimulationResultSummaryData> generateResultSummary(String simulationId) {
        // Generate Reports
        String summary = restTemplate.getForObject(baseCymeURL + simulationURLpart + "/" + simulationId + generateResultSummaryURLend, String.class);

        log.info("Generate reports command sent to CYME"); 
        log.debug(summary);

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(summary);
        template.setNamespaces(cymeDcProperties);

        List<SimulationResultSummaryData> results = template.evaluate("//ns1:SimulationResultSummaries", simulationResultSummaryDataRowMapper);
        
        return results;        
    }
    
    @Override
    public String getSimulationReport(SimulationResultSummaryData simulationSummary) {
        // Retrieve report
        String response = restTemplate.getForObject(baseCymeURL + simulationURLpart + "/" + simulationSummary.getSimulationId()
                                                    + generateReportURLpart + simulationSummary.getReportId() + "/" + simulationSummary.getNetworkId()
                                                    + "/", String.class);
        
        log.info("Retrieved report from CYME"); 
        log.debug(response);
        
        return response;
    }

    @Autowired
    public void setRestTemplate(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }
}
