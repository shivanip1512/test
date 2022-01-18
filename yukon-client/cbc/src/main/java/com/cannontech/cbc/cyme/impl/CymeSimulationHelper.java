package com.cannontech.cbc.cyme.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.PointIdContainer;
import com.cannontech.cbc.cyme.model.PhaseInformation;
import com.cannontech.cbc.cyme.model.SerializableDictionaryData;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.Phase;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;

public class CymeSimulationHelper {

    @Autowired private PaoDao paoDao;
    @Autowired private ZoneDao zoneDao;
    @Autowired private SimplePointAccessDao simplePointAccessDao;
    @Autowired private PointDao pointDao;
    @Autowired private SubstationBusDao substationBusDao;
    @Autowired private FeederDao feederDao;
    @Autowired private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    
    private static final Logger log = YukonLogManager.getLogger(CymeSimulationHelper.class);
    
    public void processReport(String report, Instant simulationTime) {
        if (report != null) {
            SimpleXPathTemplate template = new SimpleXPathTemplate();
            template.setContext(report);
            template.setNamespaces(CymeWebServiceImpl.cymeDcProperties);

            List<SerializableDictionaryData> cymeObjects = template.evaluate("/ns1:GetSimulationReportResponse/ns1:Values/a:SerializableDictionaryData", cymeSerializableDictionaryDataNodeMapper);

            for (SerializableDictionaryData cymeObject : cymeObjects) {
                String eqCode = cymeObject.getEqCode();
                
                PaoType paoType = translateEqCodeToPaoType(eqCode);
                switch(paoType) {
                    case CAPBANK:
                        processCapBank(cymeObject,paoType,simulationTime);
                        break;
                    case CAP_CONTROL_SUBBUS:
                    case CAP_CONTROL_FEEDER:
                        processBusAndFeeder(cymeObject,paoType,simulationTime);
                        break;
                    case PHASE_OPERATED:
                    case GANG_OPERATED:
                        processRegulator(cymeObject,paoType,simulationTime);
                        break;
                    case LOAD_TAP_CHANGER:
                        processLoadTapChanger(cymeObject,paoType,simulationTime);
                        break;
                    default:
                        log.warn("CYME CONFIG: EqCode not supported: " + eqCode);
                        break;
                }
            }
        }
    }
    
    private void processRegulator(SerializableDictionaryData cymeObject, PaoType paoType, Instant simulationTime) {
        // This is a hack. CYME has all data in 1 object. We need to do some Yukon sensing smarts to properly update.

        // We have a name, Determine what type of regulator this is.
        // Check Gang_Operated first, thats the simplest.
        String regulatorName = cymeObject.getEqNo();

        updateRegulatorVoltagePoint(regulatorName, Phase.ALL, cymeObject.getPhaseA().getVoltage(), 0, 0, simulationTime);

        // Else it is some combinations of a phases, run em all.
        updateRegulatorVoltagePoint(regulatorName + "_A", Phase.A, cymeObject.getPhaseA().getVoltage(),
            cymeObject.getPhaseA().getVoltageSetPoint(), cymeObject.getRegulatorBandwidth(), simulationTime);
        updateRegulatorVoltagePoint(regulatorName + "_B", Phase.B, cymeObject.getPhaseB().getVoltage(),
            cymeObject.getPhaseB().getVoltageSetPoint(), cymeObject.getRegulatorBandwidth(), simulationTime);
        updateRegulatorVoltagePoint(regulatorName + "_C", Phase.C, cymeObject.getPhaseC().getVoltage(),
            cymeObject.getPhaseC().getVoltageSetPoint(), cymeObject.getRegulatorBandwidth(), simulationTime);
    }

    private boolean updateRegulatorVoltagePoint(String regulatorName, Phase phase, float value, float voltageSetPoint,
            float regulatorBandwidth, Instant timestamp) {

        PaoType paoType = (phase == Phase.ALL) ? PaoType.GANG_OPERATED : PaoType.PHASE_OPERATED;
        YukonPao regulator = paoDao.findYukonPao(regulatorName, paoType);
        if (regulator != null) {
            try {
                log.debug("regulatorName="+regulatorName+" paoType="+paoType);
                LitePoint litePoint = extraPaoPointAssignmentDao.getLitePoint(regulator, RegulatorPointMapping.VOLTAGE);
                simplePointAccessDao.setPointValue(litePoint, timestamp, value);
                // for the purpose of CYMDIST integration it is necessary to support set point and bandwidth on a single
                // phase regulator and NOT on the gang operated regulator.
                if (phase != Phase.ALL) {
                    proccessSetPointValueAndBandwith(regulator, voltageSetPoint, regulatorBandwidth, timestamp);
                }
                return true;
            } catch (NotFoundException e) {
                log.warn("CYME CONFIG: Missing Voltage attribute on Regulator: " + regulatorName);
            }
        }
        return false;
    }

    private void processLoadTapChanger(SerializableDictionaryData cymeObject, PaoType paoType, Instant simulationTime) {
        YukonPao pao = null;
        try {
            pao = paoDao.getYukonPao(cymeObject.getEqNo(), paoType);
        } catch (NotFoundException e) {
            log.error("Pao not found with Name: " + cymeObject.getEqNo() + " and PaoType: " + paoType.getPaoTypeName() + ". Skipping.");
            return;
        }
        try {
            LitePoint voltagePoint = extraPaoPointAssignmentDao.getLitePoint(pao, RegulatorPointMapping.VOLTAGE);
            simplePointAccessDao.setPointValue(voltagePoint, simulationTime, cymeObject.getPhaseA().getVoltage());
        } catch (NotFoundException e) {
            log.warn("CYME CONFIG: Missing Voltage attribute on LTC: " + cymeObject.getPhaseA().getVoltage() );
        }  
        
        proccessSetPointValueAndBandwith(pao, cymeObject.getLtcSetPoint(), cymeObject.getLtcBandwidth(),simulationTime) ;
    }
    
    private void proccessSetPointValueAndBandwith(YukonPao pao, float setPointValue, float bandwidth,
            Instant simulationTime) {
        try {
            LitePoint setPoint = extraPaoPointAssignmentDao.getLitePoint(pao, RegulatorPointMapping.FORWARD_SET_POINT);
            simplePointAccessDao.setPointValue(setPoint, simulationTime, setPointValue);
            log.debug("pao=" + pao + " FORWARD_SET_POINT----Point id=" + setPoint.getLiteID() + "----setPointValue="
                + setPointValue);
        } catch (NotFoundException e) {
            log.warn("pao=" + pao + " CYME CONFIG: Missing " + RegulatorPointMapping.FORWARD_SET_POINT);
        }
        try {
            LitePoint bandwidthPoint =
                extraPaoPointAssignmentDao.getLitePoint(pao, RegulatorPointMapping.FORWARD_BANDWIDTH);
            float value = (float) (1.2 * bandwidth * 2);
            simplePointAccessDao.setPointValue(bandwidthPoint, simulationTime, value);
            log.debug("pao=" + pao + " FORWARD_BANDWIDTH----Point id=" + bandwidthPoint.getLiteID() + "----bandwidth="
                + value);
        } catch (NotFoundException e) {
            log.warn("pao=" + pao + " CYME CONFIG: Missing " + RegulatorPointMapping.FORWARD_BANDWIDTH);
        }
    }
    
    private void processCapBank(SerializableDictionaryData cymeObject, PaoType paoType, Instant simulationTime) {
        YukonPao pao = null;
        try {
            pao = paoDao.getYukonPao(cymeObject.getEqNo(), paoType);
        } catch (NotFoundException e) {
            log.error("Pao not found with Name: " + cymeObject.getEqNo() + " and PaoType: " + paoType.getPaoTypeName() + ". Skipping.");
            return;
        }
        
        Map<Integer, Phase> phaseSetMap = zoneDao.getMonitorPointsForBankAndPhase(pao.getPaoIdentifier().getPaoId());
        
        if (phaseSetMap.size() == 0) {
            log.warn("CYME CONFIG: Missing Voltage monitoring points on CapBank " + cymeObject.getEqNo() );
            return;
        }
        
        for (Entry<Integer, Phase> entry : phaseSetMap.entrySet()) {
            Phase phase = entry.getValue();
            int pointId = entry.getKey();

            LitePoint point = pointDao.getLitePoint(pointId);
            
            if (phase == Phase.A) {
                simplePointAccessDao.setPointValue(point, simulationTime, cymeObject.getPhaseA().getVoltage());
            } else if (phase == Phase.B) {
                simplePointAccessDao.setPointValue(point, simulationTime, cymeObject.getPhaseB().getVoltage());
            } else if (phase == Phase.C) {
                simplePointAccessDao.setPointValue(point, simulationTime, cymeObject.getPhaseC().getVoltage());
            }
        }
    }
    
    private LitePoint getPoint(int pointId) {
        if (pointId == 0) {
            return null;
        }
        try {
            return pointDao.getLitePoint(pointId);
        }
        catch (NotFoundException ex) {
            log.warn(ex);
            return null;
        }
    }

    private void processBusAndFeeder(SerializableDictionaryData cymeObject, PaoType paoType, Instant simulationTime) {
        YukonPao pao = null;
        try {
            pao = paoDao.getYukonPao(cymeObject.getEqNo(), paoType);

        } catch (NotFoundException e) {
            log.error("Pao not found with Name: " + cymeObject.getEqNo() + " and PaoType: " + paoType.getPaoTypeName() + ". Skipping.");
            return;
        }
        
        PointIdContainer pointIds;
        String objectType;
        if (paoType == PaoType.CAP_CONTROL_SUBBUS) {
            pointIds = substationBusDao.getSubstationBusPointIds(pao.getPaoIdentifier().getPaoId());
            objectType = "Subbus";
        } else {
            //Feeder
            pointIds = feederDao.getFeederPointIds(pao.getPaoIdentifier().getPaoId());
            objectType = "Feeder";
        }
        
        LitePoint varTotal = getPoint(pointIds.getVarTotalId());
        LitePoint varA = getPoint(pointIds.getVarAId());
        LitePoint varB = getPoint(pointIds.getVarBId());
        LitePoint varC = getPoint(pointIds.getVarCId());
        LitePoint volt = getPoint(pointIds.getVoltId());
        LitePoint watt = getPoint(pointIds.getWattId());
        
        PhaseInformation phaseA = cymeObject.getPhaseA();
        PhaseInformation phaseB = cymeObject.getPhaseB();
        PhaseInformation phaseC = cymeObject.getPhaseC();
        
        float voltValue = (phaseA.getVoltage() + phaseB.getVoltage() + phaseC.getVoltage())/3.0f;
        float wattValue = phaseA.getkW() + phaseB.getkW()+phaseC.getkW();
    
        if (pointIds.isTotalizekVar()) {
            if (varTotal == null) {
                log.warn("CYME CONFIG: Missing Total kVar point on " + objectType + " " + cymeObject.getEqNo() );
            } else {
                float varTotalValue = phaseA.getkVar() + phaseB.getkVar() + phaseC.getkVar();
                simplePointAccessDao.setPointValue(varTotal, simulationTime, varTotalValue);
            }
        } else {
            if (varA == null || varB == null || varC == null) {
                log.warn("CYME CONFIG: Missing phase kVar point(s) on " + objectType + " "+ cymeObject.getEqNo() );
            } else {
                float varAValue = phaseA.getkVar();
                float varBValue = phaseB.getkVar();
                float varCValue = phaseC.getkVar();
                
                simplePointAccessDao.setPointValue(varA, simulationTime, varAValue);
                simplePointAccessDao.setPointValue(varB, simulationTime, varBValue);
                simplePointAccessDao.setPointValue(varC, simulationTime, varCValue);
            }
        }
        
        if (volt == null) {
            log.warn("CYME CONFIG: Missing Volt point on " + objectType + " " + cymeObject.getEqNo() );
        } else {
            simplePointAccessDao.setPointValue(volt, simulationTime, voltValue);
        }
        if (watt == null) {
            log.warn("CYME CONFIG: Missing Watt point on " + objectType + " "+ cymeObject.getEqNo() );
        } else {
            simplePointAccessDao.setPointValue(watt, simulationTime, wattValue);
        }
    }
    
    private PaoType translateEqCodeToPaoType(String eqCode) {
        
        if ("Shunt Capacitor".equals(eqCode)) {
            return PaoType.CAPBANK;
        } else if ("Substation".equals(eqCode)) {
            return PaoType.CAP_CONTROL_SUBBUS;
        } else if ("Regulator".equals(eqCode)) {
            //From cyme 1 regulator has all three phases in one object.
            //We will have to do some configuration sensing inside yukon to handle gang operated.
            return PaoType.PHASE_OPERATED;
        } else if ("Transformer".equals(eqCode)) {
            return PaoType.LOAD_TAP_CHANGER;
        } else if ("Breaker".equals(eqCode)) {
            return PaoType.CAP_CONTROL_FEEDER;
        }
        
        return null;
    }
    
    private static ObjectMapper<Node, SerializableDictionaryData> cymeSerializableDictionaryDataNodeMapper = new ObjectMapper<Node, SerializableDictionaryData>() {
        
        @Override
        public SerializableDictionaryData map(Node node) throws DOMException {
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(node);
            template.setNamespaces(CymeWebServiceImpl.cymeDcProperties);
        
            String eqNo = template.evaluateAsString("EqNo");
            String fdrNwId = template.evaluateAsString("NetworkId");
            
            PhaseInformation phaseA = getPhaseInformation(Phase.A, template);
            PhaseInformation phaseB = getPhaseInformation(Phase.B, template);
            PhaseInformation phaseC = getPhaseInformation(Phase.C, template);
            
            String eqCode = template.evaluateAsString("EqCode");
            
            String ltcTapValueStr = template.evaluateAsString("XfoTap");
            int ltcTapValue = 0;
            if (! ltcTapValueStr.isEmpty()) {
                ltcTapValue = (int)Float.parseFloat(ltcTapValueStr);
            }
            
            float ltcBandwidth = 0;
            try {
                ltcBandwidth = Float.parseFloat(template.evaluateAsString("XfoBand"));
            } catch (NumberFormatException nfe) {}

            float ltcSetPoint = 0;
            try {
                ltcSetPoint = Float.parseFloat(template.evaluateAsString("XfoSetPoint"));
            } catch (NumberFormatException nfe) {}

            float regulatorBandwidth = 0;
            try {
                regulatorBandwidth = Float.parseFloat(template.evaluateAsString("RegBandw"));
            } catch (NumberFormatException nfe) {}
            
            return new SerializableDictionaryData(eqNo, fdrNwId, phaseA, phaseB, phaseC, ltcTapValue, eqCode,
                ltcBandwidth, ltcSetPoint, regulatorBandwidth);
        }
    };
    
    private static PhaseInformation getPhaseInformation(Phase phase, SimpleXPathTemplate template) {

        // We shouldn't have to do this!!! FIx if we can get CYME to promise only legal numbers
        String tempIStr = template.evaluateAsString("I" + phase).split("#")[0];
        String tempVBaseStr = template.evaluateAsString("VBase" + phase).split("#")[0];
        String tempKVarStr = template.evaluateAsString("KVAR" + phase).split("#")[0];
        String tempKWStr = template.evaluateAsString("KW" + phase).split("#")[0];
        String tempTapPosStr = template.evaluateAsString("RegTap" + phase).split("#")[0];
        String tempVoltageSetPoint = template.evaluateAsString("RegVset" + phase).split("#")[0];
        
        float tempI = NumberUtils.toFloat(tempIStr);
        float tempVBase = NumberUtils.toFloat(tempVBaseStr);
        float tempKVar = NumberUtils.toFloat(tempKVarStr);
        float tempKW = NumberUtils.toFloat(tempKWStr);

        float tempTapPos = 0;
        if (StringUtils.isNotBlank(tempTapPosStr)) {
            tempTapPos = Float.parseFloat(tempTapPosStr);
        }
        float tempVSetPoint = 0;
        try {
            tempVSetPoint = Float.parseFloat(tempVoltageSetPoint);
        } catch (NumberFormatException e) {}
        PhaseInformation phaseInfo =
            new PhaseInformation(tempI, tempVBase, tempKVar, tempKW, tempTapPos, tempVSetPoint);
        return phaseInfo;
    }

}
