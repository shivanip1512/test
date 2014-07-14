
package com.cannontech.cbc.cyme.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.BankState;
import com.cannontech.capcontrol.model.CymePaoPoint;
import com.cannontech.cbc.cyme.CymeDataListener;
import com.cannontech.cbc.cyme.exception.CymeConfigurationException;
import com.cannontech.cbc.cyme.exception.CymeMissingDataException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigStringKeysEnum;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.common.model.Phase;
import com.cannontech.capcontrol.RegulatorPointMapping;
import com.google.common.collect.Lists;

public class CymeXMLBuilder {

    @Autowired private ConfigurationSource configurationSource;
    @Autowired private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    @Autowired private PaoDao paoDao;
    @Autowired private SubstationBusDao substationBusDao;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private ZoneDao zoneDao;
    @Autowired private PointDao pointDao;
    
    private static final Logger log = YukonLogManager.getLogger(CymeXMLBuilder.class);
    
    public String generateStudy(final YukonPao substationBusPao) {
 
        String reportName = null;
        //Get the name of the report to request from Cyme
        try {
            reportName = configurationSource.getRequiredString(MasterConfigStringKeysEnum.CYME_REPORT_NAME);
        } catch (UnknownKeyException e) {
            throw new CymeConfigurationException("Missing the type of the report for CYME to generate. Add CPARM CYME_REPORT_NAME to the master.cfg file.");  
        }
        
        int maxIndex = 1;
        String loadFactorStr = null;
        PaoIdentifier busPaoIdentifier = substationBusPao.getPaoIdentifier();

        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(busPaoIdentifier.getPaoId());
        
        //Determine Network names
        //Subbus Name and Feeder Name are the network names. This may need to change
        List<String> paoNames = substationBusDao.getAssignedFeederPaoNames(busPaoIdentifier.getPaoId());
        String networkIds = "<NetworkID>"+liteYukonPAO.getPaoName()+"</NetworkID>";
        for (String paoName: paoNames) {
            networkIds += "<NetworkID>"+paoName+"</NetworkID>";
        }
        
        PaoPointIdentifier loadFactor = new PaoPointIdentifier(busPaoIdentifier, CymeDataListener.CYME_LOAD_FACTOR_IDENTIFIER);
        LitePoint litePoint = pointDao.getLitePoint(loadFactor);
        try{
            PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(litePoint.getPointID());

            loadFactorStr = Double.toString(pointValue.getValue());
            if (loadFactorStr == null) {
                log.error("Missing Load Factor value in the point data. Cannot generate CYME study data.");
                throw new NotFoundException("LoadFactor not found.");
            }

        } catch (IllegalArgumentException e) {
            throw new CymeMissingDataException("No value returned from Dispatch for Load Factor Point on Subbus with name: " + liteYukonPAO.getPaoName());
        }

        List<String> modifDeviceStrings = Lists.newArrayList();
        // Determine the Bank Status points to watch for Capcontrol Server changes.
        Collection<CymePaoPoint> bankPoints = substationBusDao.getBankStatusPointPaoIdsBySubbusId(busPaoIdentifier.getPaoId());
        maxIndex = buildBanksModifs(bankPoints,modifDeviceStrings,maxIndex);
        
        Collection<CymePaoPoint> regulatorPoints = zoneDao.getTapPointsBySubBusId(busPaoIdentifier.getPaoId());
        maxIndex = buildRegulatorModifs(regulatorPoints,modifDeviceStrings,maxIndex);
                 
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
                                        "<P>" + loadFactorStr + "</P>" +
                                        "<Q>" + loadFactorStr + "</Q>" +
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
            
        return handleReservedXmlCharacters(xml);
    }
    
    /**
     * Replaces XML reserved characters in the String with their XML-acceptable values 
     * to be sent to CYME so the study can be correctly processed. 
     * @param studyXml the xml String being processed.
     * @return the same String passed in with reserved XML characters escaped.
     */
    private String handleReservedXmlCharacters(String studyXml) {
    	String correctedXml = studyXml;
    	
    	{
    		// Ampersand
	    	String ampersandRegex = "&";
			Pattern p = Pattern.compile(ampersandRegex);
			correctedXml = p.matcher(correctedXml).replaceAll("&amp;");
    	}
    	
    	return correctedXml;
    }

    /**
     * Private method to refactor out the code repetition for each generating the TapPosition tag for each phase.
     * Expected result is <TapPosition'X'>int_value</TapPosition'X'>
     * 
     * Returns null if there is no regulator. This is valid since there can be any combination of phases, not for sure all 3.
     * 
     * @param regName
     * @param phase
     * @return
     */
    private String generateStringForTap(String regName, Phase phase) {
        //Lookup the regulator for phase
        YukonPao regulator = paoDao.findYukonPao(regName+"_"+phase.name(), PaoType.PHASE_OPERATED);
        //If null, there is no regulator for the phase. Omission results in a 0 in CYME
        if (regulator != null) {
            //Find the tap position value for each regulator
            PointValueQualityHolder pointValueQualityHolder = null;
            try {
                LitePoint tapPositionPoint = extraPaoPointAssignmentDao.getLitePoint(regulator, RegulatorPointMapping.TAP_POSITION);
                pointValueQualityHolder = dynamicDataSource.getPointValue(tapPositionPoint.getLiteID());
                
                if (pointValueQualityHolder == null) {
                    throw new CymeMissingDataException("No value returned from Dispatch for Tap Position for Regulator with name: " + regName);
                } else {
                    return "<TapPosition"+phase.name()+">"+ (int)pointValueQualityHolder.getValue() +"</TapPosition"+phase.name()+">";
                }
            } catch (DynamicDataAccessException e) {
                log.error("Exception requesting pointData from Dispatch. Dispatch is not connected.");
                throw e;
            } catch (NotFoundException nfe) {
                // Point not attached to regulator
                return null;
            }
        }
        //No regulator.
        //Not an error. this could be a single phase zone.
        return null;
    }
    
    private int buildBanksModifs(Collection<CymePaoPoint> paoPoints, List<String> modifs, int indexOffset) {
        for (CymePaoPoint entry : paoPoints) {
            String bankName = entry.getPaoName();
            PointValueQualityHolder pointValueQualityHolder = null;
            try {
                pointValueQualityHolder = dynamicDataSource.getPointValue(entry.getPointId());
                
                if (pointValueQualityHolder == null) {
                    throw new CymeMissingDataException("No value returned from Dispatch for Status Point on Bank with name: " + bankName);
                }
            } catch (DynamicDataAccessException e) {
                log.error("Exception requesting pointData from Dispatch. Dispatch is not connected.");
                throw e;
            }
            
            BankState state = PointStateHelper.decodeRawState(BankState.class, (int)pointValueQualityHolder.getValue());
            
            String xml =
            "<Modif>" +
            "<ModifID>"+(indexOffset) + "</ModifID>" +
            "<Index>"+(indexOffset++) + "</Index>" +
            "<Transactions>" +           
                "<Transaction>" +
                    "<Data Type=\"ShuntCapacitor\" DeviceNumber=\"" +bankName+ "\" ModifType=\"Delta\">" +
                        "<ConnectionStatus>" + convertBankStatus(state) + "</ConnectionStatus>" +
                    "</Data>" +
                "</Transaction>" +
            "</Transactions>" +
            "</Modif>";
            
            modifs.add(xml);
        }
        
        return indexOffset;
    }
    
    private int buildRegulatorModifs(Collection<CymePaoPoint> paoPoints, List<String> modifs, int indexOffset) {
        for (CymePaoPoint entry : paoPoints) {
            PaoType paoType = entry.getPaoIdentifier().getPaoType();
            
            if (paoType == PaoType.GANG_OPERATED) {
                String regName = entry.getPaoName();
                PointValueQualityHolder pointValueQualityHolder = null;
                try {
                    pointValueQualityHolder = dynamicDataSource.getPointValue(entry.getPointId());
                    
                    if (pointValueQualityHolder == null) {
                        throw new CymeMissingDataException("No value returned from Dispatch for Tap Position for Regulator with name: " + regName);
                    }
                } catch (DynamicDataAccessException e) {
                    log.error("Exception requesting pointData from Dispatch. Dispatch is not connected.");
                    throw e;
                }
                
                int tapPosition = (int)pointValueQualityHolder.getValue();
                String xml =   
                        "<Modif>" +
                            "<ModifID>"+(indexOffset) + "</ModifID>" +
                            "<Index>"+(indexOffset++) + "</Index>" +
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
                modifs.add(xml);
            } else if (paoType == PaoType.PHASE_OPERATED) {

                //This is a hack, we are going to look up all related regulators on the assumption
                //  there is a regulator per phase using a PaoName of 'regName'_'Phase Letter'
                List<String> tapStrings = Lists.newArrayList();
                
                //Strip of the phase from this regulator.
                String regName = entry.getPaoName().substring(0, entry.getPaoName().lastIndexOf("_"));                
                
                String tapString = generateStringForTap(regName, Phase.A);
                if (tapString != null) {
                    tapStrings.add(tapString);
                }
                
                tapString = generateStringForTap(regName, Phase.B);
                if (tapString != null) {
                    tapStrings.add(tapString);
                }
                
                tapString = generateStringForTap(regName, Phase.C);
                if (tapString != null) {
                    tapStrings.add(tapString);
                }
                
                String xml =    "<Modif>" +
                                "<ModifID>"+(indexOffset) + "</ModifID>" +
                                "<Index>"+(indexOffset++) + "</Index>" +
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
                                 
                modifs.add(xml);
            } else if (paoType == PaoType.LOAD_TAP_CHANGER) {
                String regName = entry.getPaoName();
                PointValueQualityHolder pointValueQualityHolder = null;
                try {
                    pointValueQualityHolder = dynamicDataSource.getPointValue(entry.getPointId());
                    
                    if (pointValueQualityHolder == null) {
                        throw new CymeMissingDataException("No value returned from Dispatch for Tap Position for Regulator with name: " + regName);
                    }
                } catch (DynamicDataAccessException e) {
                    log.error("Exception requesting pointData from Dispatch. Dispatch is not connected.");
                    throw e;
                }
                
                int tapPosition = (int)pointValueQualityHolder.getValue();
                String xml =    "<Modif>" +
                                "<ModifID>" +(indexOffset)+ "</ModifID>" +
                                "<Index>" +(indexOffset++)+ "</Index>" +
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
                modifs.add(xml);
            }
        }
        
        return indexOffset;
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
