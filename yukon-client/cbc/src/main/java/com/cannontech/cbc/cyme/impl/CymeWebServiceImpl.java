package com.cannontech.cbc.cyme.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestOperations;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.cbc.cyme.CymeWebService;
import com.cannontech.cbc.cyme.model.CymeSimulationStatus;
import com.cannontech.cbc.cyme.model.SimulationResultSummaryData;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.util.ServletUtil;

public class CymeWebServiceImpl implements CymeWebService {
    @Autowired private @Qualifier("cyme") RestOperations cymeRestTemplate;

    private final String baseCymeUrl;
    private static final String SIMULATION_URL_PART = "/CYMDIST/SimulationsService.svc/rest/Simulation";
    private static final String RUN_STUDY_URL_END = "/Run/Study/";
    private static final String CHECK_REPORT_STATUS_URL_END = "/Status/";
    private static final String GENERATE_RESULT_SUMMARY_URL_END = "/Result/Summary/";
    private static final String GENERATE_REPORT_URL_PART = "/Report/";
    private static final Logger log = YukonLogManager.getLogger(CymeWebService.class);

    private static final Namespace cymeDcNamespace = Namespace.getNamespace("ns1", "http://schemas.datacontract.org/2004/07/Cyme.Services.CymDistService.DataContracts");
    private static final Namespace cymeDtoNamespace = Namespace.getNamespace("a", "http://schemas.datacontract.org/2004/07/Cyme.CymDist.Server.Domain.DataTransferObjects");

    public static Properties cymeDcProperties = new Properties();

    static {
        cymeDcProperties.put(cymeDcNamespace.getPrefix(), cymeDcNamespace.getURI());
        cymeDcProperties.put(cymeDtoNamespace.getPrefix(), cymeDtoNamespace.getURI());
    }

    @Autowired
    public CymeWebServiceImpl(ConfigurationSource configurationSource) {
        if (!configurationSource.getBoolean(MasterConfigBoolean.CYME_ENABLED, false)) {
            baseCymeUrl = null;
            return;
        }
        
        baseCymeUrl =
            configurationSource.getString(MasterConfigString.CYME_DIST_BASE_URL, "http://localhost:8866");
        log.debug("Using " + baseCymeUrl + " as cyme url.");
        
        try {
            URI uri = new URI(baseCymeUrl);
            
            String hostAddress = uri.getHost();
            if (hostAddress == null) {
                log.error("Cyme url is missing a valid host component: " + baseCymeUrl);
                return;
            }
            
            Integer portNumber = uri.getPort();
            if (portNumber == -1) {
                log.error("Cyme url is missing a port number: " + baseCymeUrl);
                return;
            }
            
            YukonHttpProxy.addNonProxyHosts(ServletUtil.getUriHostWithoutPrefix(hostAddress));
            
        } catch (URISyntaxException e) {
            log.error("Cyme url is not a valid URL.", e);
        }
    }

    @Override
    public String runSimulation(String xmlData) {
        log.debug("Study data being sent: " + xmlData);
        
        String response, studyUrl = null;
        try {
            studyUrl = baseCymeUrl + SIMULATION_URL_PART + RUN_STUDY_URL_END;
            response = cymeRestTemplate.postForObject(studyUrl, xmlData, String.class);
        } catch (DigiWebServiceException e) {
            log.error("Error sending study data to cyme at url: " + studyUrl, e);
            return null;
        }
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
        log.info("Checking Simulation Status with CYME");

        String response = cymeRestTemplate.getForObject(baseCymeUrl + SIMULATION_URL_PART + "/" + simulationId + CHECK_REPORT_STATUS_URL_END, String.class);

        log.debug(response);

        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(response);
        template.setNamespaces(cymeDcProperties);

        String statusStr = template.evaluateAsString("/ns1:GetSimulationStatusResponse/ns1:Status");
        CymeSimulationStatus status = CymeSimulationStatus.getFromCymeValue(statusStr);
        log.info("Simulation Status is " + status.getCymeValue());

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
        String summary = cymeRestTemplate.getForObject(baseCymeUrl + SIMULATION_URL_PART + "/" + simulationId + GENERATE_RESULT_SUMMARY_URL_END, String.class);

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
        String response = cymeRestTemplate.getForObject(baseCymeUrl + SIMULATION_URL_PART + "/" + simulationSummary.getSimulationId()
                                                    + GENERATE_REPORT_URL_PART + simulationSummary.getReportId() + "/" + simulationSummary.getNetworkId()
                                                    + "/", String.class);

        log.info("Retrieved report from CYME");
        log.debug(response);

        return response;
    }
}
