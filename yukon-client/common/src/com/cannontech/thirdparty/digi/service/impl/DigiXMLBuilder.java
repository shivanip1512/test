package com.cannontech.thirdparty.digi.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.LMGroupDao;
import com.cannontech.core.dao.SepDeviceClassDao;
import com.cannontech.database.data.device.lm.SepDeviceClass;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.model.DevConnectwareId;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.model.DRLCClusterAttribute;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeEndpoint;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DigiXMLBuilder {

    @Autowired private SepDeviceClassDao sepDeviceClassDao;
    @Autowired private LMGroupDao lmGroupDao;
    @Autowired private ZigbeeDeviceDao zigbeeDeviceDao;
    
    private static int sourceEndPointId = 0x5E;
    private static int clusterId = 0x0701;
    private static int serverClient = 1;
    
    public String buildInstallEndPointMessage(ZigbeeDevice gateway, ZigbeeEndpoint endpoint) {
        DevConnectwareId connectwareId = new DevConnectwareId(gateway.getZigbeeMacAddress());
        String endpointMac = endpoint.getMacAddress();
        String installCode = endpoint.getInstallCode();
        installCode = installCode.replaceAll(":","");
        
        int crc = zigbeeCRC16(installCode);
        String hexCrc = String.format("%04x",crc);
                
        String xml = "<sci_request version=\"1.0\">" +
                        "<send_message>" +
                            "<targets>" +
                                "<device id=\"" + connectwareId.getDevConnectwareId() + "\"/>" +
                            "</targets>" +
                            "<rci_request version=\"1.1\">" +
                                "<do_command target=\"RPC_request\">" +
                                    "<add_device synchronous=\"true\">" +
                                        "<device_address type=\"MAC\">" +
                                        endpointMac +
                                        "</device_address>" +
                                        "<join_time>900</join_time>" +
                                        "<installation_code type=\"string\">" +
                                        installCode  + hexCrc.substring(2, 4) + hexCrc.substring(0, 2) +
                                        "</installation_code>" +
                                    "</add_device>" +
                                "</do_command>" +
                            "</rci_request>" +
                        "</send_message>" +
                    "</sci_request>";
        return xml;
    }
    
    public String buildUninstallEndPointMessage(ZigbeeDevice gateway, ZigbeeDevice device) {
        DevConnectwareId connectwareId = new DevConnectwareId(gateway.getZigbeeMacAddress());
        
        String endpointMac = device.getZigbeeMacAddress();
        endpointMac = endpointMac.replaceAll(":","");
        
        String xml = 
               "<sci_request version=\"1.0\">"
            + "  <send_message>"
            + "    <targets>"
            + "      <device id=\"" + connectwareId.getDevConnectwareId() + "\"/>"
            + "    </targets>"
            + "    <rci_request version=\"1.1\">"
            + "      <do_command target=\"RPC_request\">"
            + "        <remove_device synchronous=\"true\">"
            + "          <device_address type=\"MAC\">" + endpointMac + "</device_address>"
            + "        </remove_device>"
            + "      </do_command>"
            + "    </rci_request>"
            + "  </send_message>"
            + "</sci_request>";
        
        return xml;
    }
    
    public String buildTextMessage(List<ZigbeeDevice> gateways, YukonTextMessage message) {
        int confirmationValue = message.isConfirmationRequired() ? 128:0;
        
        long startTimeSeconds = 0; // Zero means now
        Instant startInstant = message.getStartTime();
        if (startInstant.isAfterNow()) {
            startTimeSeconds = TimeUtil.convertInstantToUtc2000Seconds(startInstant)/1000;
        }
        
        int displayDuration = message.getDisplayDuration().toPeriod().toStandardMinutes().getMinutes();
        
        String xml = 
               "<sci_request version=\"1.0\">"
            + "  <send_message>"

            + "    <targets>";
            for (ZigbeeDevice gateway : gateways) {
                DevConnectwareId connectwareId = new DevConnectwareId(gateway.getZigbeeMacAddress());

                xml += "      <device id=\"" + connectwareId.getDevConnectwareId() + "\"/>";
            }
            xml += "    </targets>"
                    
            + "    <rci_request version=\"1.1\">"
            + "      <do_command target=\"RPC_request\">"
            + "        <create_message_event synchronous=\"true\">"
            + "          <record type=\"DisplayMessageRecord\">"
            + "            <message_id>" + message.getMessageId() + "</message_id>"
            + "            <message_control>" + confirmationValue + "</message_control>"
            + "            <start_time>" + startTimeSeconds + "</start_time>"
            + "            <duration_in_minutes>" + displayDuration + "</duration_in_minutes>"
            + "            <message type=\"string\">" + message.getMessage() + "</message>"
            + "          </record>"
            + "        </create_message_event>"
            + "      </do_command>"
            + "    </rci_request>"
            + "  </send_message>"
            + "</sci_request>";
        
        return xml;
    }
    
    public String buildCancelMessageEvent(List<ZigbeeDevice> gateways, YukonCancelTextMessage cancelZigbeeText) {
        String xml = 
               "<sci_request version=\"1.0\">"
            + "  <send_message>"

            + "    <targets>";
            for (ZigbeeDevice gateway : gateways) {
                DevConnectwareId connectwareId = new DevConnectwareId(gateway.getZigbeeMacAddress());

                xml += "      <device id=\"" + connectwareId.getDevConnectwareId() + "\"/>";
            }
            xml += "    </targets>"
                    
            + "    <rci_request version=\"1.1\">"
            + "      <do_command target=\"RPC_request\">"
            + "        <cancel_message_event synchronous=\"true\">"
            + "          <record type=\"CancelMessageRecord\">"
            + "            <message_id>" + cancelZigbeeText.getMessageId() + "</message_id>"
            + "          </record>"
            + "        </cancel_message_event>"
            + "      </do_command>"
            + "    </rci_request>"
            + "  </send_message>"
            + "</sci_request>";
        
        return xml;
    }
    
    public String buildSEPControlMessage(int eventId,  List<ZigbeeDevice> gateways, SepControlMessage controlMessage) {
        int groupId = controlMessage.getGroupId();
        
        //Get DeviceClass
        Set<SepDeviceClass> deviceClasses = sepDeviceClassDao.getSepDeviceClassesByDeviceId(groupId);
        short deviceClass = buildDeviceClass(deviceClasses);
        
        //Get Util Enrollment Group value
        byte utilEnrollmentGroup = lmGroupDao.getUtilityEnrollmentGroupForSepGroup(groupId);
        
        //Get gateways on the group
        Set<String> connectwareIds = Sets.newHashSet();        
        for (ZigbeeDevice gateway : gateways) {
            DevConnectwareId connectwareId = new DevConnectwareId(gateway.getZigbeeMacAddress());
            connectwareIds.add(connectwareId.getDevConnectwareId());
        }
        
        String xml = 
           "<sci_request version=\"1.0\">"
        + "  <send_message>"
        + "    <targets>";
            //Setup all gateway MacAddresses
            for (String connectwareId : connectwareIds) {
                xml += "      <device id=\"" + connectwareId + "\"/>";
            }
        xml += "</targets>"
        + "    <rci_request version=\"1.1\">"
        + "      <do_command target=\"RPC_request\">"
        + "         <create_DRLC_event synchronous=\"true\">"
        + "             <record type=\"LoadControlEventRecord\">"
        + "                 <issuer_event_id>"
        +                       eventId 
        + "                 </issuer_event_id>"
        + "                 <device_class>"
        +                        deviceClass
        + "                 </device_class>"
        + "                 <utility_enrollment_group>"
        +                        utilEnrollmentGroup
        + "                 </utility_enrollment_group>"
        + "                 <start_time>"
        +                         controlMessage.getUtcStartTime()
        + "                 </start_time>"
        + "                 <duration_in_minutes>"
        +                        controlMessage.getControlMinutes()
        + "                 </duration_in_minutes>"
        + "                 <criticality_level>"
        +                         controlMessage.getCriticality()
        + "                 </criticality_level>"
        + "                 <cooling_temperature_set_point>" 
        +                        controlMessage.getCoolTempSetpoint()
        + "                 </cooling_temperature_set_point>"
        + "                 <heating_temperature_set_point>"
        +                       controlMessage.getHeatTempSetpoint()
        + "                 </heating_temperature_set_point>"
        + "                 <cooling_temperature_offset>" 
        +                        controlMessage.getCoolTempOffset()
        + "                 </cooling_temperature_offset>"
        + "                 <heating_temperature_offset>"
        +                       controlMessage.getHeatTempOffset()
        + "                 </heating_temperature_offset>"
        + "                 <average_load_adjustment_percentage >"
        +                        controlMessage.getAverageCyclePercent()
        + "                 </average_load_adjustment_percentage >"
        + "                 <duty_cycle>"
        +                        controlMessage.getStandardCyclePercent()
        + "                 </duty_cycle>"
        + "                 <event_control>"
        +                        controlMessage.getEventFlags()
        + "                 </event_control>"
        + "             </record>"
        + "         </create_DRLC_event>"
        + "      </do_command>"
        + "    </rci_request>"
        + "  </send_message>"
        + "</sci_request>";
        
        return xml;
    }
    
    public String buildSepRestore(int eventId, List<ZigbeeDevice> gateways, SepRestoreMessage restoreMessage) {
        int groupId = restoreMessage.getGroupId();
                
        //Get DeviceClass
        Set<SepDeviceClass> deviceClasses = sepDeviceClassDao.getSepDeviceClassesByDeviceId(groupId);
        short deviceClass = buildDeviceClass(deviceClasses);
        
        //Get Util Enrollment Group value
        byte utilEnrollmentGroup = lmGroupDao.getUtilityEnrollmentGroupForSepGroup(groupId);
        
        //Get gateways on the group
        List<String> macAddresses = Lists.newArrayList();

        for (ZigbeeDevice gateway : gateways) {
            DevConnectwareId macAddress = new DevConnectwareId(gateway.getZigbeeMacAddress());
            macAddresses.add(macAddress.getDevConnectwareId());
        }
        
        String xml = 
           "<sci_request version=\"1.0\">"
        + "  <send_message>"
        + "    <targets>";
            //Setup all gateway MacAddresses
            for (String connectwareId : macAddresses) {
                xml += "      <device id=\"" + connectwareId + "\"/>";
            }
        xml += "</targets>"
        + "    <rci_request version=\"1.1\">"
        + "      <do_command target=\"RPC_request\">"
        + "         <cancel_DRLC_event synchronous=\"true\">"
        + "             <record type=\"CancelLoadControlEventRecord\">"
        + "                 <issuer_event_id>"
        +                       eventId 
        + "                 </issuer_event_id>"
        + "                 <device_class>"
        +                        deviceClass
        + "                 </device_class>"
        + "                 <utility_enrollment_group>"
        +                        utilEnrollmentGroup
        + "                 </utility_enrollment_group>"
        + "                 <effect_time>"
        +                         restoreMessage.getRestoreTime()
        + "                 </effect_time>"
        + "                 <cancel_control>"
        +                         restoreMessage.getEventFlags()
        + "                 </cancel_control>"
        + "             </record>"
        + "         </cancel_DRLC_event>"
        + "      </do_command>"
        + "    </rci_request>"
        + "  </send_message>"
        + "</sci_request>";
        
        return xml;
    }
    
    public String buildWriteLMAddressing(ZigbeeDevice gateway, ZigbeeDevice endPoint, Map<DRLCClusterAttribute,Integer> attributes) {
        
        DevConnectwareId gatewayMac = new DevConnectwareId(gateway.getZigbeeMacAddress());
                
        //Find based on the endPoint Device
        ZigbeeEndpoint tstat = zigbeeDeviceDao.getZigbeeEndPoint(endPoint.getZigbeeDeviceId());
        
        String xml = 
           "<sci_request version=\"1.0\">"
        + "  <send_message>"
        + "  <targets>"
        + "    <device id=\"" + gatewayMac.getDevConnectwareId() + "\"/>"
        + "  </targets>"
        + "  <rci_request version=\"1.1\">"
        + "  <do_command target=\"RPC_request\">"
        
        /* Start Command */
        + "  <write_attributes>"
        + "    <destination_address type=\"MAC\">" + tstat.getMacAddress() + "</destination_address>"
        + "    <destination_endpoint_id>" + tstat.getDestinationEndPointId() + "</destination_endpoint_id>"
        + "    <source_endpoint_id>" +sourceEndPointId + "</source_endpoint_id>"
        + "    <cluster_id>" + clusterId + "</cluster_id>"
        + "    <server_or_client>" + serverClient + "</server_or_client>"
        + "    <record_list type=\"list\">";
        
        for (DRLCClusterAttribute attribute : attributes.keySet()) {
            int value = attributes.get(attribute);
            xml += "      <item type=\"WriteAttributeRecord\">"
                 +   "        <attribute_id>" + attribute.getId() + "</attribute_id>"
                 +   "        <attribute_type>" + attribute.getType() + "</attribute_type>"
                 +   "        <value type=\"int\">" + value + "</value>"
                 +   "      </item>";
        }
        
        xml += 
           "    </record_list>"
        + "  </write_attributes>"
        /* End Command Message */

        + "</do_command>"
        + "</rci_request>"
        + "</send_message>"
        + "</sci_request>";
        
        return xml;
    }
    
    public String buildReadLMAddressingForEndPoint(ZigbeeDevice gateway, ZigbeeDevice endPoint) {       
        //Find based on the endPoint Device
        ZigbeeEndpoint tstat = zigbeeDeviceDao.getZigbeeEndPoint(endPoint.getZigbeeDeviceId());

        DevConnectwareId gatewayMac = new DevConnectwareId(gateway.getZigbeeMacAddress());
        
        String xml = 
           "<sci_request version=\"1.0\">"
        + "  <send_message>"
        + "  <targets>"
        + "    <device id=\"" + gatewayMac.getDevConnectwareId() + "\"/>"
        + "  </targets>"
        + "  <rci_request version=\"1.1\">"
        + "  <do_command target=\"RPC_request\">"
        
        /* Start Command */
        +   "<read_attributes synchronous=\"true\">" 
        + "  <destination_address type=\"mac\">" + tstat.getMacAddress() + "</destination_address>"
        + "  <destination_endpoint_id>" + tstat.getDestinationEndPointId() + "</destination_endpoint_id>"
        + "  <source_endpoint_id>" + sourceEndPointId + "</source_endpoint_id>"
        + "  <cluster_id>" + clusterId + "</cluster_id>"
        + "  <server_or_client>" + serverClient + "</server_or_client>"
        + "  <record_list type=\"list\">"
        + "    <item type=\"ReadAttributeRecord\">"
        + "      <attribute_id>" + DRLCClusterAttribute.UTILITY_ENROLLMENT_GROUP.getId() + "</attribute_id>"
        + "    </item>"
        + "    <item type=\"ReadAttributeRecord\">"
        + "      <attribute_id>" + DRLCClusterAttribute.START_RANDOMIZE_MINUTES.getId() + "</attribute_id>"
        + "    </item>"
        + "    <item type=\"ReadAttributeRecord\">"
        + "      <attribute_id>" + DRLCClusterAttribute.STOP_RANDOMIZE_MINTES.getId() + "</attribute_id>"
        + "    </item>"
        + "    <item type=\"ReadAttributeRecord\">"
        + "      <attribute_id>" + DRLCClusterAttribute.DEVICE_CLASS.getId() + "</attribute_id>"
        + "    </item>"
        + "  </record_list>"
        + "</read_attributes>"
        /* End Command Message */
        
        + "</do_command>"
        + "</rci_request>"
        + "</send_message>"
        + "</sci_request>";
        
        return xml;
    }
    
    /**
     * Take the assigned classes and build up the 16 byte Device Class.
     * 
     * 0x0FFF is all devices. 
     * 
     * @param deviceClasses
     * @return
     */
    private short buildDeviceClass(Set<SepDeviceClass> deviceClasses) {
        short deviceClass = 0x0000;
        
        //Sum up the bits for each device class we are a part of
        for (SepDeviceClass sepDeviceClass : deviceClasses) {
            short bitValue = sepDeviceClass.getBitValue();
            deviceClass |= bitValue;
        }
        
        return deviceClass;
    }
    
    /**
     * CRC16 CCITT method with specifics for the ZigBee implementation.
     * 
     * @param installCode
     * @return
     */
    public int zigbeeCRC16 (String installCode) {

        int crc = 0xffff;          // initial value 
        int polynomial = 0x8408;  // Reversed Polynomial for ZigBee
        int[] bytes = new int[8];
        
        for (int i = 0; i < 16; i += 2) {
            String parseThis = installCode.substring(i,i+2);
            bytes[i/2] = Integer.parseInt(parseThis,16);
        }
        
        for (int b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b   >> (i) & 1) == 1);
                boolean c15 = ((crc & 1) == 1);
                crc >>= 1;
                if (c15 ^ bit) {
                    crc ^= polynomial;
                }
             }
        }

        crc &= 0xffff;
        crc ^= 0xffff;//Remove for regular CRC

        return crc;
    }
}
