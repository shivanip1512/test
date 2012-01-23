package com.cannontech.cbc.cyme.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.cbc.cyme.CymDISTSimulatorService;
import com.cannontech.cbc.cyme.CymDISTWebService;
import com.cannontech.cbc.cyme.CymeResultCap;
import com.cannontech.cbc.cyme.impl.PointStateHelper.BankState;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.PaoType;
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
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.enums.Phase;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
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
    private ResourceLoader resourceLoader;

    private static ObjectMapper<Node, CymeResultCap> cymeCapBankNodeMapper = new ObjectMapper<Node, CymeResultCap>() {
        
        @Override
        public CymeResultCap map(Node node) throws DOMException {
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(node);
            template.setNamespaces(CymDISTWebServiceImpl.cymeDcProperties);
            

            
            String eqNo = template.evaluateAsString("EqNo");
            String fdrNwId = template.evaluateAsString("NetworkId");
            
            PhaseInformation phaseA = new PhaseInformation(template.evaluateAsFloat("IA"), template.evaluateAsFloat("VBaseA"),
                                                           template.evaluateAsFloat("KVARA"), template.evaluateAsFloat("KWA"));
            PhaseInformation phaseB = new PhaseInformation(template.evaluateAsFloat("IB"), template.evaluateAsFloat("VBaseB"),
                                                           template.evaluateAsFloat("KVARB"), template.evaluateAsFloat("KWB"));
            PhaseInformation phaseC = new PhaseInformation(template.evaluateAsFloat("IC"), template.evaluateAsFloat("VBaseC"),
                                                           template.evaluateAsFloat("KVARC"), template.evaluateAsFloat("KWC"));         
            return new CymeResultCap(eqNo,
                                     fdrNwId,
                                     phaseA,
                                     phaseB,
                                     phaseC);
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
        } else {
            logger.info("Simulator is disabled and will not run.");
        }
    }

    @Override
    public void pointDataReceived(PointValueQualityHolder pointData) {
        String simulationId;
        LitePoint point = pointDao.getLitePoint(pointData.getId());
        BankState state = PointStateHelper.decodeRawState(BankState.class, pointData.getValue());

        if (state == BankState.OpenPending) {
            // Open Pending
            String bankName = paoDao.getYukonPAOName(point.getPaobjectID());
            String xmlData = CymeXMLBuilder.openCapbank(bankName);

            simulationId = cymDISTWebService.runSimulation(xmlData);
        } else if (state == BankState.ClosePending) {
            // Close Pending
            String bankName = paoDao.getYukonPAOName(point.getPaobjectID());
            String xmlData = CymeXMLBuilder.closeCapbank(bankName);
            cymDISTWebService.runSimulation(xmlData);

            simulationId = cymDISTWebService.runSimulation(xmlData);
        } else {
            logger.info("CapBank status change other than Openpending or Closepending.");
            return;
        }

        for(int i = 0; i < 15; i++){
            if(!cymDISTWebService.getSimulationReportStatus(simulationId)){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.warn("caught exception in pointDataReceived", e);
                }
            }
            else{
                String report = cymDISTWebService.getSimulationReport(simulationId);

                // Process report into list of Point changes and send to dispatch
                processReport(report);
                break;
            }
                
        }
        logger.debug("Simulation status not complete, timed out. ");
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
                Map<Integer, Phase>phaseSetMap = zoneDao.getMonitorPointsForBankAndPhase(pao.getLiteID());
                for (Entry<Integer, Phase> entry : phaseSetMap.entrySet()) {
                    Phase phase = entry.getValue();
                    int pointId = entry.getKey();

                    if (phase == Phase.A) {
                        simplePointAccessDao.setPointValue(pointId, cap.getPhaseA().getVoltage());
                    } else if (phase == Phase.B) {
                        simplePointAccessDao.setPointValue(pointId, cap.getPhaseB().getVoltage());
                    } else if (phase == Phase.C) {
                        simplePointAccessDao.setPointValue(pointId, cap.getPhaseC().getVoltage());
                    }
                }
            }
        }
    }
    
    private enum ElementType {
        LINE("OverheadLines"),
        CABLE("Cables"),
        REGULATOR("Regulators"),
        TRANSFORMER("Transformers"),
        CAPACITOR("ShuntCapacitors");
        
        private String classValue;
        
        private ElementType(String classValue) {
            this.classValue = classValue;
        }
        
        public static  ElementType getForClass(String classValue) {
            for (ElementType type : values()) {
                if (type.classValue.equals(classValue)) return type;
            }
            throw new IllegalArgumentException("Invalid class type: " + classValue);
        }
    }
    
    private enum ValueType {
        TO_NODE_ID("ToNodeId"),
        FROM_NODE_ID("FromNodeId"),
        EQ_CODE("EqCode"),
        EQ_ID("EqId"),
        EQ_NO("EqNo"),
        FEEDER_ID("FeederId"),
        BUS_ID("BusId"),
        SUB_NETWORK_ID("SubNetworkId"),
        IS_LTC("XfoIsLTC");
        
        private String value;
        
        private ValueType(String value) {
            this.value = value;
        }
        
        public static  ValueType getForValue(String value) {
            for (ValueType type : values()) {
                if (type.value.equals(value)) return type;
            }
            throw new IllegalArgumentException("Invalid value type: " + value);
        }
    }
    
    private class CymeElement {
        private Map<ValueType, String> values = Maps.newEnumMap(ValueType.class);
        public Map<ValueType, String> getValues() {
            return values;
        }
        public void setValues(Map<ValueType, String> values) {
            this.values = values;
        }
    }
    
    @PostConstruct
    public void testModel() throws JDOMException, IOException {
        String modelFile = "classpath:com/cannontech/cbc/cyme/testing/yukonTopology.xml";
        Resource model = resourceLoader.getResource(modelFile);
        
        SAXBuilder saxBuilder = new SAXBuilder();
        Document modelDoc = saxBuilder.build(model.getInputStream());
        
        Element rootElement = modelDoc.getRootElement();
        Element tabularResult = rootElement.getChild("TabularReportBody").getChild("TabularResults").getChild("TabularResult");
        Element networkIds = tabularResult.getChild("NetworkIDs");
        
        Iterable<Element> feedersAndStation = getElementChildren(networkIds, "NetworkID");
        Set<String> feederNames = Sets.newHashSetWithExpectedSize(Iterables.size(feedersAndStation));
        String substationName = null;
        Iterator<Element> fdrSubIter = feedersAndStation.iterator();
        while (fdrSubIter.hasNext()) {
            Element elem = fdrSubIter.next();
            
            // Check for Substation
            String name = elem.getText();
            if (!fdrSubIter.hasNext()) {
                substationName = name;
            } else {
                // This is a feeder name
                feederNames.add(name);
            }
            
        }
        
        logger.debug("Substation: " + substationName);
        logger.debug("Feeders: " + feederNames);
        
        Element tabularResultTypes = tabularResult.getChild("TabularResultTypes");
        ListMultimap<ElementType, CymeElement> typeMap = getTypeMap(tabularResultTypes);
//        for (ElementType elementType : typeMap.keySet()) {
//            for (CymeElement elem : typeMap.get(elementType)) {
//                for (ValueType valueType : elem.getValues().keySet()) {
//                    logger.debug(valueType + ": " + elem.getValues().get(valueType));
//                }
//            }
//        }
        
        // transformers
        List<CymeElement> cymeBuses = typeMap.get(ElementType.TRANSFORMER);
        Set<Bus> buses = Sets.newHashSet();
        for (CymeElement cymeElement : cymeBuses) {
            Bus bus = new Bus();
            bus.setName(cymeElement.getValues().get(ValueType.EQ_ID));
            buses.add(bus);
        }
        
        
        
    }
    
    private abstract class Thing {
        private String name;
        public void setName(String name) {this.name = name;}
        public String getName() {return name;}
    }
    
    private class Bus extends Thing {
        Set<Feeder> feeders = Sets.newHashSet();
    }
    
    private class Feeder extends Thing {
        Set<Bank> banks = Sets.newHashSet();
    }
    
    private class Bank extends Thing {
    }
    
    private ListMultimap<ElementType, CymeElement> getTypeMap(Element tabularResultTypes) {
        ListMultimap<ElementType, CymeElement> typeMap = ArrayListMultimap.create();
        Iterable<Element> types = getElementChildren(tabularResultTypes, "TabularResultType");
        
        for (Element type : types) {
            
            ElementType elementType = ElementType.getForClass(type.getAttributeValue("class"));
            Iterable<Element> rows = getElementChildren(type.getChild("TabularResultRows"), "TabularResultRow");
            
            for (Element row : rows) {
                
                CymeElement element = new CymeElement();
                Iterable<Element> values = getElementChildren(row.getChild("TabularValues"), "TabularValue");
                
                for (Element value : values) {
                    ValueType valueType = ValueType.getForValue(value.getAttributeValue("header"));
                    element.getValues().put(valueType, value.getTextTrim());
                }
                
                typeMap.put(elementType, element);
            }
        }
        return typeMap;
    }

    public static Iterable<Element> getElementChildren(Element element, String name) {
        return Iterables.filter(element.getChildren(name), Element.class);
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
    
    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
}