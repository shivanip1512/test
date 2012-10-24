package com.cannontech.cbc.cyme.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.PointIdContainer;
import com.cannontech.cbc.cyme.model.PhaseInformation;
import com.cannontech.cbc.cyme.model.SerializableDictionaryData;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.enums.Phase;
import com.cannontech.enums.RegulatorPointMapping;

public class CymeSimulationHelper {

    @Autowired private PaoDao paoDao;
    @Autowired private ZoneDao zoneDao;
    @Autowired private SimplePointAccessDao simplePointAccessDao;
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
                LiteYukonPAObject pao = null;
                String eqCode = cymeObject.getEqCode();
                
                PaoType paoType = translateEqCodeToPaoType(eqCode);
                
                if (paoType == null) {
                    log.warn("CYME CONFIG: EqCode not supported: " + eqCode);
                    continue;
                }
                
                try {
                    pao = paoDao.getLiteYukonPAObject(cymeObject.getEqNo(),
                                                      paoType.getPaoCategory().getCategoryId(),
                                                      paoType.getPaoClass().getPaoClassId(),
                                                      paoType.getDeviceTypeId());
                } catch (NotFoundException e) {
                    log.error("Pao not found with Name: " + cymeObject.getEqNo() + " and PaoType: " + paoType.getPaoTypeName() + ". Skipping.");
                    continue;
                }
                
                if (paoType == PaoType.CAPBANK) {
                    Map<Integer, Phase> phaseSetMap = zoneDao.getMonitorPointsForBankAndPhase(pao.getLiteID());
                    
                    if (phaseSetMap.size() == 0) {
                        log.warn("CYME CONFIG: Missing Voltage monitoring points on CapBank " + cymeObject.getEqNo() );
                        continue;
                    }
                    
                    for (Entry<Integer, Phase> entry : phaseSetMap.entrySet()) {
                        Phase phase = entry.getValue();
                        int pointId = entry.getKey();
    
                        if (phase == Phase.A) {
                            simplePointAccessDao.setPointValue(pointId, simulationTime, cymeObject.getPhaseA().getVoltage());
                        } else if (phase == Phase.B) {
                            simplePointAccessDao.setPointValue(pointId, simulationTime, cymeObject.getPhaseB().getVoltage());
                        } else if (phase == Phase.C) {
                            simplePointAccessDao.setPointValue(pointId, simulationTime, cymeObject.getPhaseC().getVoltage());
                        }
                    }
                } else if (paoType == PaoType.CAP_CONTROL_SUBBUS || paoType == PaoType.CAP_CONTROL_FEEDER) {
                        
                    PointIdContainer pointIds;
                    if (paoType == PaoType.CAP_CONTROL_SUBBUS) {
                        pointIds = substationBusDao.getSubstationBusPointIds(pao.getLiteID());
                    } else {
                        //Feeder
                        pointIds = feederDao.getFeederPointIds(pao.getLiteID());
                    }
                    
                    int varTotalId = pointIds.getVarTotalId();
                    int varAId = pointIds.getVarAId();
                    int varBId = pointIds.getVarBId();
                    int varCId = pointIds.getVarCId();
                    int voltId = pointIds.getVoltId();
                    int wattId = pointIds.getWattId();
                    
                    PhaseInformation phaseA = cymeObject.getPhaseA();
                    PhaseInformation phaseB = cymeObject.getPhaseB();
                    PhaseInformation phaseC = cymeObject.getPhaseC();
                    
                    float voltValue = (phaseA.getVoltage() + phaseB.getVoltage() + phaseC.getVoltage())/3.0f;
                    float wattValue = phaseA.getkW() + phaseB.getkW()+phaseC.getkW();

                    if (pointIds.isTotalizekVar()) {
                        if (varTotalId == 0) {
                            log.warn("CYME CONFIG: Missing Total kVar point on Subbus " + cymeObject.getEqNo() );
                        } else {
                            float varTotalValue = phaseA.getkVar() + phaseB.getkVar() + phaseC.getkVar();
                            simplePointAccessDao.setPointValue(varTotalId, simulationTime, varTotalValue);
                        }
                    } else {
                        if (varAId == 0 || varBId == 0 || varCId == 0) {
                            log.warn("CYME CONFIG: Missing phase kVar point(s) on Subbus " + cymeObject.getEqNo() );
                        } else {
                            float varAValue = phaseA.getkVar();
                            float varBValue = phaseB.getkVar();
                            float varCValue = phaseC.getkVar();
                            
                            simplePointAccessDao.setPointValue(varAId, simulationTime, varAValue);
                            simplePointAccessDao.setPointValue(varBId, simulationTime, varBValue);
                            simplePointAccessDao.setPointValue(varCId, simulationTime, varCValue);
                        }
                    }
                    
                    if (voltId == 0) {
                        log.warn("CYME CONFIG: Missing Volt point on Subbus " + cymeObject.getEqNo() );
                    } else {
                        simplePointAccessDao.setPointValue(voltId, simulationTime, voltValue);
                    }
                    if (wattId == 0) {
                        log.warn("CYME CONFIG: Missing Watt point on Subbus " + cymeObject.getEqNo() );
                    } else {
                        simplePointAccessDao.setPointValue(wattId, simulationTime, wattValue);
                    }
                } else if (paoType == PaoType.GANG_OPERATED) {
                    try {
                        LitePoint litePoint = extraPaoPointAssignmentDao.getLitePoint(pao, RegulatorPointMapping.VOLTAGE_Y);
                        simplePointAccessDao.setPointValue(litePoint.getLiteID(), simulationTime, cymeObject.getPhaseA().getVoltage());
                    } catch (NotFoundException e) {
                        log.warn("CYME CONFIG: Missing Voltage_Y attribute on Regulator: " + cymeObject.getEqNo() );
                    }                    
                } else if (paoType == PaoType.LOAD_TAP_CHANGER) {
                    try {
                        LitePoint litePoint = extraPaoPointAssignmentDao.getLitePoint(pao, RegulatorPointMapping.VOLTAGE_Y);
                        simplePointAccessDao.setPointValue(litePoint.getLiteID(), simulationTime, cymeObject.getPhaseA().getVoltage());
                    } catch (NotFoundException e) {
                        log.warn("CYME CONFIG: Missing Voltage_Y attribute on Regulator: " + cymeObject.getEqNo() );
                    }                    
                }
            }
        }
    }
    
    private PaoType translateEqCodeToPaoType(String eqCode) {
        
        if ("Shunt Capacitor".equals(eqCode)) {
            return PaoType.CAPBANK;
        } else if ("Substation".equals(eqCode)) {
            return PaoType.CAP_CONTROL_SUBBUS;
        } else if ("Regulator".equals(eqCode)) {
            return PaoType.GANG_OPERATED;
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
            
            // We shouldn't have to do this!!!  FIx if we can get CYME to promise only legal numbers
            String tempIStr = template.evaluateAsString("IA").split("#")[0];
            String tempVBaseStr = template.evaluateAsString("VBaseA").split("#")[0];
            String tempKVarStr = template.evaluateAsString("KVARA").split("#")[0];
            String tempKWStr = template.evaluateAsString("KWA").split("#")[0];
            String tempTapPosStr =  template.evaluateAsString("RegTapA").split("#")[0];
            
            float tempI = Float.parseFloat(tempIStr); 
            float tempVBase = Float.parseFloat(tempVBaseStr);
            float tempKVar = Float.parseFloat(tempKVarStr);
            float tempKW = Float.parseFloat(tempKWStr);
            
            float tempTapPos = 0;
            if (! tempTapPosStr.isEmpty()) {
                tempTapPos = Float.parseFloat(tempTapPosStr);
            }
            
            PhaseInformation phaseA = new PhaseInformation(tempI,tempVBase,tempKVar,tempKW,tempTapPos);
            
            // We shouldn't have to do this!!!  FIx if we can get CYME to promise only legal numbers
            tempIStr = template.evaluateAsString("IB").split("#")[0];
            tempVBaseStr = template.evaluateAsString("VBaseB").split("#")[0];
            tempKVarStr = template.evaluateAsString("KVARB").split("#")[0];
            tempKWStr = template.evaluateAsString("KWB").split("#")[0];
            tempTapPosStr =  template.evaluateAsString("RegTapB").split("#")[0];
            
            tempI = Float.parseFloat(tempIStr); 
            tempVBase = Float.parseFloat(tempVBaseStr);
            tempKVar = Float.parseFloat(tempKVarStr);
            tempKW = Float.parseFloat(tempKWStr);
            tempTapPos = 0;
            if (! tempTapPosStr.isEmpty()) {
                tempTapPos = Float.parseFloat(tempTapPosStr);
            }

            PhaseInformation phaseB = new PhaseInformation(tempI,tempVBase,tempKVar,tempKW,tempTapPos);
            
            // We shouldn't have to do this!!!  FIx if we can get CYME to promise only legal numbers
            tempIStr = template.evaluateAsString("IC").split("#")[0];
            tempVBaseStr = template.evaluateAsString("VBaseC").split("#")[0];
            tempKVarStr = template.evaluateAsString("KVARC").split("#")[0];
            tempKWStr = template.evaluateAsString("KWC").split("#")[0];
            tempTapPosStr =  template.evaluateAsString("RegTapB").split("#")[0];
            
            tempI = Float.parseFloat(tempIStr); 
            tempVBase = Float.parseFloat(tempVBaseStr);
            tempKVar = Float.parseFloat(tempKVarStr);
            tempKW = Float.parseFloat(tempKWStr);
            tempTapPos = 0;
            if (! tempTapPosStr.isEmpty()) {
                tempTapPos = Float.parseFloat(tempTapPosStr);
            }

            PhaseInformation phaseC = new PhaseInformation(tempI,tempVBase,tempKVar,tempKW,tempTapPos);
            
            String eqCode = template.evaluateAsString("EqCode");
            
            String ltcTapValueStr = template.evaluateAsString("XfoTap");
            int ltcTapValue = 0;
            if (! ltcTapValueStr.isEmpty()) {
                ltcTapValue = (int)Float.parseFloat(ltcTapValueStr);
            }
            
            return new SerializableDictionaryData(eqNo, fdrNwId, phaseA, phaseB, phaseC, ltcTapValue, eqCode);
        }
    };

}
