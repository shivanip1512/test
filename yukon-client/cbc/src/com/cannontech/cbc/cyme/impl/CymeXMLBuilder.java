
package com.cannontech.cbc.cyme.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.model.BankState;
import com.cannontech.capcontrol.model.PointPaoIdentifier;
import com.cannontech.cbc.cyme.CymeConfigurationException;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigStringKeysEnum;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.google.common.collect.Lists;

public class CymeXMLBuilder {

    @Autowired private ConfigurationSource configurationSource;

    public String generateStudy(Collection<PointPaoIdentifier> paosInSystem, Map<Integer,PointValueQualityHolder> currentPointValues, List<String> paoNames){
        
        List<String> modifDeviceStrings = Lists.newArrayList();
        int maxIndex = 0;
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
                
                String xml =  "<ModifDeviceStatus>" +
                         "<Index>" + maxIndex + "</Index>" +    // Incremental number
                         "<ModifID>" + maxIndex++ +"</ModifID>" +   // Same as Index
                         "<Description>Disconnect ShuntCapacitor "+ bankName  +".</Description>" +
                         "<SectionID>" + bankName.split("-")[0] + "</SectionID>" +
                         "<DeviceType>ShuntCapacitor</DeviceType>" + // See list at the end of document
                         "<DeviceNumber>" + bankName + "</DeviceNumber>" +
                         "<ConnectionStatus>" + convertBankStatus(state) + "</ConnectionStatus>" + // Connected, Disconnected 
                         "</ModifDeviceStatus>";
                modifDeviceStrings.add(xml);
            } else if (paoType == PaoType.GANG_OPERATED) {
                String regName = entry.getPaoName();
                PointValueQualityHolder pointValueQualityHolder = currentPointValues.get(entry.getPointId());
                int tapPosition = (int)pointValueQualityHolder.getValue();
                String xml =   "<ModifRegulatorTapPosition>" +
                          "<ModifID>"+(maxIndex) + "</ModifID>" +
                          "<Index>"+(maxIndex++) + "</Index>" +
                          "<DeviceNumber>"+regName+"</DeviceNumber>" +
                          "<TapPositionA>"+ tapPosition +"</TapPositionA>" +
                          "<TapPositionB>"+ tapPosition +"</TapPositionB>" +
                          "<TapPositionC>"+ tapPosition +"</TapPositionC>" +
                          "</ModifRegulatorTapPosition>";
                modifDeviceStrings.add(xml);
            } else if (paoType == PaoType.LOAD_TAP_CHANGER) {
                String regName = entry.getPaoName();
                PointValueQualityHolder pointValueQualityHolder = currentPointValues.get(entry.getPointId());
                int tapPosition = (int)pointValueQualityHolder.getValue();
                String xml   = "<ModifTransformerTapPosition>" +
                          "<ModifID>"+(maxIndex) + "</ModifID>" +
                          "<Index>"+(maxIndex++) + "</Index>" +
                          "<DeviceNumber>"+regName+"</DeviceNumber>" +
                          "<TapPosition>"+ tapPosition +"</TapPosition>" +
                          "</ModifTransformerTapPosition>";
                modifDeviceStrings.add(xml);
            } else if (paoType == PaoType.CAP_CONTROL_SUBBUS) {
                PointValueQualityHolder pointValueQualityHolder = currentPointValues.get(entry.getPointId());
                loadFactor = Double.toString(pointValueQualityHolder.getValue());
            }
        }
        
        if (loadFactor == null) {
            throw new NotFoundException("Missing Load Factor value in the point data. Cannot generate CYME study data.");
        }
         
        String xml = 
            "<Cyme>" +
                    "<Cymdist>" +
                        "<SchemaVersion>1</SchemaVersion>" +
                        "<CymdistProperties>" +
                            "<Version>5.02</Version>" +
                            "<Revision>05</Revision>" +
                            "<Extensions>" +
                                "<FailureEvents>0</FailureEvents>" +
                            "</Extensions>" +
                        "</CymdistProperties>" +
                        "<Studies>" +
                            "<Study>" +
                            "<ProjectID>MyProjectID</ProjectID>" +
                            "<LoadNetworkOption>2</LoadNetworkOption>" +
                            "<StudyParameters>" +
                                "<LoadAllFeedersDisplayed>1</LoadAllFeedersDisplayed>" +
                            "</StudyParameters>" +
                            "<CalculationParameters>" +
                                "<LoadFlowParameters>" +
                                    "<LoadScalingFactors>" +
                                        "<Mode>LoadFlowFactorGlobal</Mode>" +
                                        "<P_Percent>" + loadFactor + "</P_Percent>" +
                                        "<Q_Percent>" + loadFactor + "</Q_Percent>" +
                                    "</LoadScalingFactors>" +
                                "</LoadFlowParameters>" +
                            "</CalculationParameters>" +
                        "<Modifs>";
                for (String xmlFrag : modifDeviceStrings ) {
                    xml += xmlFrag;
                }
                xml += "</Modifs>" +	
                          "<Commands>" +
                              "<CommandLoadFlow>" +
                                  "<ModifIndex>"+(maxIndex-1)+"</ModifIndex> " +
                                  "<OwnerID>MyProjectID</OwnerID>" +
                                  "<CommandTypeID>CommandLoadFlowID</CommandTypeID>" +
                                  "<CommandID>CommandLoadFlow</CommandID>" +
                                  "<ExecuteAfter>1</ExecuteAfter>" +
                                  "<Reports>" +
                                      "<Report>" +
                                          "<ReportID>"+ reportName + "</ReportID>" +
                                      "</Report>" +
                                  "</Reports>" +
                                  "<Networks>" + networkIds + "</Networks>" +
                              "</CommandLoadFlow>" +
                          "</Commands>" +
                          "<CommandTypes>" +
                              "<CommandType>" +
                                  "<CommandTypeID>CommandLoadAllocationID</CommandTypeID>" +
                                  "<CommandTypeEnum>LoadAllocation</CommandTypeEnum>" +
                              "</CommandType>" +
                              "<CommandType>" +
                                  "<CommandTypeID>CommandLoadFlowID</CommandTypeID>" +
                                  "<CommandTypeEnum>LoadFlow</CommandTypeEnum>" +
                              "</CommandType>" +
                          "</CommandTypes>" +
                          "<NetworksToDisplay>" + networkIds + "</NetworksToDisplay>" +
                          "<NetworksToLoad>" + networkIds + "</NetworksToLoad>" +
                      "</Study>" +
                  "</Studies>" +
              "</Cymdist>" +
            "</Cyme>";
            
        return xml;
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
