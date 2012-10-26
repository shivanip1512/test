
package com.cannontech.cbc.cyme.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.model.BankState;
import com.cannontech.capcontrol.model.PointPaoIdentifier;
import com.cannontech.cbc.cyme.CymeConfigurationException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigStringKeysEnum;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.enums.Phase;
import com.cannontech.enums.RegulatorPointMapping;
import com.google.common.collect.Lists;

public class CymeXMLBuilder {

    @Autowired private ConfigurationSource configurationSource;
    @Autowired private CcMonitorBankListDao monitorBankListDao;
    @Autowired private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    @Autowired private PaoDao paoDao;
    
    private static final Logger log = YukonLogManager.getLogger(CymeXMLBuilder.class);
    
    public String generateStudy(Collection<PointPaoIdentifier> paosInSystem, Map<Integer,PointValueQualityHolder> currentPointValues, List<String> paoNames){
        
        List<String> modifDeviceStrings = Lists.newArrayList();
        int maxIndex = 1;
        String loadFactor = null;
        String networkIds = "";
        String reportName;
        
        try {
            reportName = configurationSource.getRequiredString(MasterConfigStringKeysEnum.CYME_REPORT_NAME);
        } catch (UnknownKeyException e) {
          throw new CymeConfigurationException("Missing the type of the report for CYME to generate. Add CPARM CYME_REPORT_NAME to the master.cfg file.");  
        }
        
        for (String paoName: paoNames) {
            networkIds += "<NetworkID>"+paoName+"</NetworkID>";
        }
        
        for (PointPaoIdentifier entry : paosInSystem) {
            PaoType paoType = entry.getPaoIdentifier().getPaoType();
            if (paoType == PaoType.CAPBANK) {
                String bankName = entry.getPaoName();
                PointValueQualityHolder pointValueQualityHolder = currentPointValues.get(entry.getPointId());
                BankState state = PointStateHelper.decodeRawState(BankState.class, (int)pointValueQualityHolder.getValue());
                
                String xml =
                "<Modif>" +
                "<ModifID>"+(maxIndex) + "</ModifID>" +
                "<Index>"+(maxIndex++) + "</Index>" +
                "<Transactions>" +           
                    "<Transaction>" +
                        "<Data Type=\"ShuntCapacitor\" DeviceNumber=\"" +bankName+ "\" ModifType=\"Delta\">" +
                            "<ConnectionStatus>" + convertBankStatus(state) + "</ConnectionStatus>" +
                        "</Data>" +
                    "</Transaction>" +
                "</Transactions>" +
                "</Modif>";

                modifDeviceStrings.add(xml);
            } else if (paoType == PaoType.GANG_OPERATED) {
                String regName = entry.getPaoName();
                PointValueQualityHolder pointValueQualityHolder = currentPointValues.get(entry.getPointId());
                int tapPosition = (int)pointValueQualityHolder.getValue();
                String xml =   
                        "<Modif>" +
                            "<ModifID>"+(maxIndex) + "</ModifID>" +
                            "<Index>"+(maxIndex++) + "</Index>" +
                            "<Transactions>" +           
                                "<Transaction>" +
                                    "<Data Type=\"Regulator\" DeviceNumber=\"" +regName+ "\" ModifType=\"Delta\">" +
                                          "<TapPositionA>"+ tapPosition +"</TapPositionA>" +
                                          "<TapPositionB>"+ tapPosition +"</TapPositionB>" +
                                          "<TapPositionC>"+ tapPosition +"</TapPositionC>" +
                                    "</Data>" +
                                "</Transaction>" +
                            "</Transactions>" +
                         "</Modif>";
                modifDeviceStrings.add(xml);
            } else if (paoType == PaoType.PHASE_OPERATED) {

                //This is a hack, we are going to look up all related regulators on the assumption
                //  there is a regulator per phase using a PaoName of 'regName'_'Phase Letter'
                List<String> tapStrings = Lists.newArrayList();
                
                //Strip of the phase from this regulator.
                String regName = entry.getPaoName().substring(0, entry.getPaoName().lastIndexOf("_"));                
                
                String tapString = generateStringForTap(regName,Phase.A,currentPointValues);
                if( tapString != null) {
                    tapStrings.add(tapString);
                }
                
                tapString = generateStringForTap(regName,Phase.B,currentPointValues);
                if( tapString != null) {
                    tapStrings.add(tapString);
                }
                
                tapString = generateStringForTap(regName,Phase.C,currentPointValues);
                if( tapString != null) {
                    tapStrings.add(tapString);
                }
                
                String xml =    "<Modif>" +
                                "<ModifID>"+(maxIndex) + "</ModifID>" +
                                "<Index>"+(maxIndex++) + "</Index>" +
                                "<Transactions>" +           
                                    "<Transaction>" +
                                        "<Data Type=\"Regulator\" DeviceNumber=\"" +regName+ "\" ModifType=\"Delta\">";
                                        for(String tapPositionStr : tapStrings) {
                                            xml += tapPositionStr;
                                        }
                                 xml += "</Data>" +
                                    "</Transaction>" +
                                "</Transactions>" +
                                "</Modif>";
                                 
                modifDeviceStrings.add(xml);
            } else if (paoType == PaoType.LOAD_TAP_CHANGER) {
                String regName = entry.getPaoName();
                PointValueQualityHolder pointValueQualityHolder = currentPointValues.get(entry.getPointId());
                int tapPosition = (int)pointValueQualityHolder.getValue();
                String xml =    "<Modif>" +
                                "<ModifID>" +(maxIndex)+ "</ModifID>" +
                                "<Index>" +(maxIndex++)+ "</Index>" +
                                "<Transactions>" +           
                                    "<Transaction>" +
                                        "<Data Type=\"Transformer\" DeviceNumber=\"" +regName+ "\" ModifType=\"Delta\">" +
                                            "<LTCSettings ModifType=\"Delta\">" +
                                                "<TapSetting>" +tapPosition+ "</TapSetting>" +
                                            "</LTCSettings>" +
                                        "</Data>" +
                                    "</Transaction>" +
                                "</Transactions>" +
                                "</Modif>";
                modifDeviceStrings.add(xml);
            } else if (paoType == PaoType.CAP_CONTROL_SUBBUS) {
                PointValueQualityHolder pointValueQualityHolder = currentPointValues.get(entry.getPointId());
                loadFactor = Double.toString(pointValueQualityHolder.getValue());
            }
        }
        
        if (loadFactor == null) {
            log.error("Missing Load Factor value in the point data. Cannot generate CYME study data.");
            throw new NotFoundException("LoadFactor not found.");
        }
         
        String xml = 
            "<Cyme Version=\"5.04\" Revision=\"07\">" +
                "<Version>5.04</Version>" +
                "<Revision>07</Revision>" +
                    "<Studies>" +
                        "<Study>" +
                            "<ProjectID>MyProjectID</ProjectID>" +
                            "<LoadNetworkOption>2</LoadNetworkOption>" +
                            "<StudyParameters>" +
                                "<LoadAllFeedersDisplayed>1</LoadAllFeedersDisplayed>" +
                            "</StudyParameters>" +
                            "<CalculationParameters>" +
                                "<LoadFlowParameters>" +
                                    "<LoadFlowLoadScalingFactors>" +
                                        "<Mode>LoadFlowFactorGlobal</Mode>" +
                                        "<P>" + loadFactor + "</P>" +
                                        "<Q>" + loadFactor + "</Q>" +
                                    "</LoadFlowLoadScalingFactors>" +
                                "</LoadFlowParameters>" +
                            "</CalculationParameters>" +
                        "<Modifs>";
                for (String xmlFrag : modifDeviceStrings ) {
                    xml += xmlFrag;
                }
                xml += "</Modifs>" +	
                      "<Commands>" +
                          "<CommandLoadFlow>" +
                              "<CommandID>CommandLoadFlow</CommandID>" +
                              "<ReportCommands>" +
                                  "<ReportCommand>" +
                                      "<ReportId>"+ reportName + "</ReportId>" +
                                  "</ReportCommand>" +
                              "</ReportCommands>" +
                              "<ProcessCommand>1</ProcessCommand>" +
                              "<OwnerID>MyProjectID</OwnerID>" +
                              "<CommandTypeID>CommandLoadFlowID</CommandTypeID>" +
                              "<CommandIndex>"+(maxIndex-1)+"</CommandIndex> " +
                              "<ExecuteAfter>1</ExecuteAfter>" +
                              "<Networks>" + networkIds + "</Networks>" +
                          "</CommandLoadFlow>" +
                      "</Commands>" +
                      "<CommandTypes>" +
                          "<CommandType>" +
                              "<CommandTypeID>CommandLoadAllocationID</CommandTypeID>" +
                              "<CommandType>LoadAllocation</CommandType>" +
                          "</CommandType>" +
                          "<CommandType>" +
                              "<CommandTypeID>CommandLoadFlowID</CommandTypeID>" +
                              "<CommandType>LoadFlow</CommandType>" +
                          "</CommandType>" +
                      "</CommandTypes>" +
                      "<NetworkToDisplayList>" + networkIds + "</NetworkToDisplayList>" +
                      "<NetworkToLoadList>" + networkIds + "</NetworkToLoadList>" +
                  "</Study>" +
              "</Studies>" +
            "</Cyme>";
            
        return xml;
    }

    private String generateStringForTap(String regName, Phase phase, Map<Integer, PointValueQualityHolder> currentPointValues) {
        //Lookup the regulator for phase
        YukonPao regulator = paoDao.findYukonPao(regName+"_"+phase.name(), PaoType.PHASE_OPERATED);
        //If null, there is no regulator for the phase. Omission results in a 0 in CYME
        if (regulator != null) {
            //Find the tap position value for each regulator
            try{
                LitePoint tapPositionPoint = extraPaoPointAssignmentDao.getLitePoint(regulator, RegulatorPointMapping.TAP_POSITION);
                PointValueQualityHolder pointValueQualityHolder = currentPointValues.get(tapPositionPoint.getLiteID());
                if (pointValueQualityHolder != null) {
                    return new String("<TapPosition"+phase.name()+">"+ (int)pointValueQualityHolder.getValue() +"</TapPosition"+phase.name()+">");
                }
            } catch (NotFoundException e) {
                //Point not attached to regulator
                return null;
            }
        }
        //No regulator.
        //Not an error. this could be a single phase zone.
        return null;
    }
    
    private String convertBankStatus(BankState state) {
        
        switch (state) {
            case OPEN:
            case OPEN_FAIL://Should we consider this a closed because it "Failed to open?"
            case OPEN_PENDING:
            case OPEN_QUESTIONABLE:
                return "Disconnected";
            case CLOSE:
            case CLOSE_FAIL://Should we consider this an open because it "Failed to close?"
            case CLOSE_PENDING:
            case CLOSE_QUESTIONABLE:
                return "Connected";
        }
        throw new UnsupportedOperationException("Attempted to convert an unknown BankState: " + state.name());
    }

}
