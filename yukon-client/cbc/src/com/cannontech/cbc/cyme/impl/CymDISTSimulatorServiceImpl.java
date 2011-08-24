package com.cannontech.cbc.cyme.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cyme.CymDISTSimulatorService;
import com.cannontech.cbc.cyme.CymDISTWebService;
import com.cannontech.cbc.cyme.CymeResultCap;
import com.cannontech.cbc.dao.SubstationBusDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.enums.Phase;
import com.cannontech.thirdparty.model.DeviceCore;
import com.google.common.collect.Sets;

public class CymDISTSimulatorServiceImpl implements CymDISTSimulatorService, PointDataListener {

    private static final Logger logger = YukonLogManager.getLogger(CymDISTSimulatorServiceImpl.class);
    private ConfigurationSource configurationSource;
    private AsyncDynamicDataSource asyncDynamicDataSource;
    private CymDISTWebService cymDISTWebService;
    private PaoDao paoDao;
    private PointDao pointDao;
    private ZoneDao zoneDao;
    private SimplePointAccessDao simplePointAccessDao;
    private SubstationBusDao substationBusDao;

    private static ObjectMapper<Node, CymeResultCap> cymeCapBankNodeMapper = new ObjectMapper<Node, CymeResultCap>() {
        @Override
        public CymeResultCap map(Node node) throws DOMException {
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(node);
            template.setNamespaces(CymDISTWebServiceImpl.cymeDcProperties);

            String eqNo = template.evaluateAsString("EqNo");
            String fdrNwId = template.evaluateAsString("NetworkId");
            float iA = template.evaluateAsFloat("IA");
            float iB = template.evaluateAsFloat("IB");
            float iC = template.evaluateAsFloat("IC");
            float vBaseA = template.evaluateAsFloat("VBaseA");
            float vBaseB = template.evaluateAsFloat("VBaseB");
            float vBaseC = template.evaluateAsFloat("VBaseC");
            float kVarA = template.evaluateAsFloat("KVARA");
            float kVarB = template.evaluateAsFloat("KVARB");
            float kVarC = template.evaluateAsFloat("KVARC");
            float kWA = template.evaluateAsFloat("KWA");
            float kWB = template.evaluateAsFloat("KWB");
            float kWC = template.evaluateAsFloat("KWC");

            return new CymeResultCap(eqNo,
                                     fdrNwId,
                                     iA,
                                     iB,
                                     iC,
                                     vBaseA,
                                     vBaseB,
                                     vBaseC,
                                     kVarA,
                                     kVarB,
                                     kVarC,
                                     kWA,
                                     kWB,
                                     kWC);
        }
    };

    @PostConstruct
    public void initialize() {

        boolean cymeEnabled = configurationSource.getBoolean("CYME_ENABLED", false);

        if (cymeEnabled) {
            String subBusName = configurationSource.getRequiredString("CYME_INTEGRATION_SUBBUS");
            LiteYukonPAObject pao = paoDao.getLiteYukonPAObject(subBusName,PaoType.CAP_CONTROL_SUBBUS.getPaoCategory().getCategoryId(),
                                                                PaoType.CAP_CONTROL_SUBBUS.getPaoClass().getPaoClassId(),
                                                                PaoType.CAP_CONTROL_SUBBUS.getDeviceTypeId());
            logger.info("CYME integration is configured for sub bus with name: " + subBusName);

            // Determine PointId that holds 'Bank State' for each bank
            Collection<Integer> statusPointIds = substationBusDao.getBankStatusPointIdsBySubbusId(pao.getLiteID());

            // Register for points
            Set<Integer> pointList = Sets.newHashSet(statusPointIds);
            asyncDynamicDataSource.registerForPointData(this, pointList);
        }
    }

    @Override
    public void pointDataReceived(PointValueQualityHolder pointData) {
        String simulationId;
        LitePoint point = pointDao.getLitePoint(pointData.getId());

        if (pointData.getValue() == 6.0) {
            // Open Pending
            String bankName = paoDao.getYukonPAOName(point.getPaobjectID());
            String xmlData = CymeXMLBuilder.openCapbank(bankName);

            simulationId = cymDISTWebService.runSimulation(xmlData);
        } else if (pointData.getValue() == 7.0) {
            // Close Pending
            String bankName = paoDao.getYukonPAOName(point.getPaobjectID());
            String xmlData = CymeXMLBuilder.closeCapbank(bankName);
            cymDISTWebService.runSimulation(xmlData);

            simulationId = cymDISTWebService.runSimulation(xmlData);
        } else {
            // No Operation needed
            return;
        }

        while (!cymDISTWebService.getSimulationReportStatus(simulationId)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.warn("caught exception in pointDataReceived", e);
            }
        }
        String report = cymDISTWebService.getSimulationReport(simulationId);

        // Process report into list of Point changes and send to dispatch
        processReport(report);

        return;
    }

    private void processReport(String report) {
        if (report != null) {
            SimpleXPathTemplate template = new SimpleXPathTemplate();
            template.setContext(report);
            template.setNamespaces(CymDISTWebServiceImpl.cymeDcProperties);

            List<CymeResultCap> capBanks = template.evaluate("/ns1:GetSimulationReportResponse/ns1:Values/a:SerializableDictionaryData", cymeCapBankNodeMapper);

            for (CymeResultCap cap : capBanks) {
                LiteYukonPAObject pao = paoDao.getLiteYukonPAObject(cap.getEqNo(),
                                                                    PaoType.CAPBANK.getPaoCategory().getCategoryId(),
                                                                    PaoType.CAPBANK.getPaoClass().getPaoClassId(),
                                                                    PaoType.CAPBANK.getDeviceTypeId());
                Set<Entry<Integer, Phase>> phaseSet = (zoneDao.getMonitorPointsForBankAndPhase(pao.getLiteID()).entrySet());
                Iterator<Map.Entry<Integer, Phase>> phaseIter = phaseSet.iterator();

                while (phaseIter.hasNext()) {
                    Entry<Integer, Phase> entry = phaseIter.next();
                    Phase phase = entry.getValue();
                    int pointId = entry.getKey();

                    if (phase == Phase.A) {
                        simplePointAccessDao.setPointValue(pointId, cap.getVBaseA());
                    } else if (phase == Phase.B) {
                        simplePointAccessDao.setPointValue(pointId, cap.getVBaseB());
                    } else if (phase == Phase.C) {
                        simplePointAccessDao.setPointValue(pointId, cap.getVBaseC());
                    }
                }
            }
        }
    }

    // TODO
    // private String buildLowerTapSimulationXML
    // private String buildRaiseTapSimulationXML

    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    @Autowired
    public void setAsyncDynamicDataSource(AsyncDynamicDataSource asyncDynamicDataSource) {
        this.asyncDynamicDataSource = asyncDynamicDataSource;
    }

    @Autowired
    public void setCymDISTWebService(CymDISTWebService cymDISTWebService) {
        this.cymDISTWebService = cymDISTWebService;
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    @Autowired
    public void setSubstationBusDao(SubstationBusDao substationBusDao) {
        this.substationBusDao = substationBusDao;
    }

    @Autowired
    public void setZoneDao(ZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }

    @Autowired
    public void setSimplePointAccessDao(SimplePointAccessDao simplePointAccessDao) {
        this.simplePointAccessDao = simplePointAccessDao;
    }
}
