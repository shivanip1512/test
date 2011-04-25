package com.cannontech.thirdparty.service.impl;

import java.util.List;
import java.util.Set;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.ZigbeeTextMessage;
import com.cannontech.core.dao.LMGroupDao;
import com.cannontech.core.dao.SepDeviceClassDao;
import com.cannontech.database.data.device.lm.SepDeviceClass;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.model.ZigbeeThermostat;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DigiXMLBuilder {

    private SepDeviceClassDao sepDeviceClassDao;
    private LMGroupDao lmGroupDao;
    private GatewayDeviceDao gatewayDeviceDao;
    
    public String buildInstallStatMessage(ZigbeeDevice gateway, ZigbeeThermostat thermostat) {
        String gatewayMacAddress = convertMacAddresstoDigi(gateway.getZigbeeMacAddress());
        String thermostatMac = thermostat.getMacAddress();
        String installCode = thermostat.getInstallCode();
        installCode = installCode.replaceAll(":","");
        
        int crc = zigbeeCRC16(installCode);
        String hexCrc = Integer.toHexString(crc);
        
        String xml = "<sci_request version=\"1.0\">" +
                        "<send_message>" +
                            "<targets>" +
                                "<device id=\"" + gatewayMacAddress + "\"/>" +
                            "</targets>" +
                            "<rci_request version=\"1.1\">" +
                                "<do_command target=\"RPC_request\">" +
                                    "<add_device synchronous=\"true\">" +
                                        "<device_address type=\"MAC\">" +
                                        thermostatMac +
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
    
    public String buildUninstallStatMessage(ZigbeeDevice gateway, ZigbeeDevice device) {
        String macAddress = convertMacAddresstoDigi(gateway.getZigbeeMacAddress());
        
        String thermostatMac = device.getZigbeeMacAddress();
        thermostatMac = thermostatMac.replaceAll(":","");
        
        String xml = 
               "<sci_request version=\"1.0\">"
            + "  <send_message>"
            + "    <targets>"
            + "      <device id=\"" + macAddress + "\"/>"
            + "    </targets>"
            + "    <rci_request version=\"1.1\">"
            + "      <do_command target=\"RPC_request\">"
            + "        <remove_device synchronous=\"true\">"
            + "          <device_address type=\"MAC\">" + thermostatMac + "</device_address>"
            + "        </remove_device>"
            + "      </do_command>"
            + "    </rci_request>"
            + "  </send_message>"
            + "</sci_request>";
        
        return xml;
    }
    
    public String buildTextMessage(ZigbeeDevice gateway, ZigbeeTextMessage message) {
        String macAddress = convertMacAddresstoDigi(gateway.getZigbeeMacAddress());
        int confirmationValue = message.isConfirmationRequired() ? 128:0;
        
        int startTime = 0; // Zero means now
        Instant startInstant = message.getStartTime();
        if (startInstant.isAfterNow()) {
            startTime = (int)(startInstant.getMillis() / 1000);
        }
        
        String xml = 
               "<sci_request version=\"1.0\">"
            + "  <send_message>"
            + "    <targets>"
            + "      <device id=\"" + macAddress + "\"/>"
            + "    </targets>"
            + "    <rci_request version=\"1.1\">"
            + "      <do_command target=\"RPC_request\">"
            + "        <create_message_event synchronous=\"true\">"
            + "          <record type=\"DisplayMessageRecord\">"
            + "            <message_id>" + message.getMessageId() + "</message_id>"
            + "            <message_control>" + confirmationValue + "</message_control>"
            + "            <start_time>" + startTime + "</start_time>"
            + "            <duration_in_minutes>" + message.getDisplayDuration().toPeriod().toStandardMinutes().getMinutes() + "</duration_in_minutes>"
            + "            <message type=\"string\">" + message.getMessage() + "</message>"
            + "          </record>"
            + "        </create_message_event>"
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
        Set<String> macAddresses = Sets.newHashSet();        
        for (ZigbeeDevice gateway : gateways) {
            String macAddress = convertMacAddresstoDigi(gateway.getZigbeeMacAddress());
            macAddresses.add(macAddress);
        }
        
        String xml = 
           "<sci_request version=\"1.0\">"
        + "  <send_message>"
        + "    <targets>";
            //Setup all gateway MacAddresses
            for (String macAddress : macAddresses) {
                xml += "      <device id=\"" + macAddress + "\"/>";
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
    
    public String buildSepRestore(int eventId, SepRestoreMessage restoreMessage) {
        int groupId = restoreMessage.getGroupId();
                
        //Get DeviceClass
        Set<SepDeviceClass> deviceClasses = sepDeviceClassDao.getSepDeviceClassesByDeviceId(groupId);
        short deviceClass = buildDeviceClass(deviceClasses);
        
        //Get Util Enrollment Group value
        byte utilEnrollmentGroup = lmGroupDao.getUtilityEnrollmentGroupForSepGroup(groupId);
        
        //Get gateways on the group
        List<ZigbeeDevice> gateways = gatewayDeviceDao.getZigbeeGatewaysForGroupId(groupId);
        List<String> macAddresses = Lists.newArrayList();

        for (ZigbeeDevice gateway : gateways) {
            String macAddress = convertMacAddresstoDigi(gateway.getZigbeeMacAddress());
            macAddresses.add(macAddress);
        }
        
        String xml = 
           "<sci_request version=\"1.0\">"
        + "  <send_message>"
        + "    <targets>";
            //Setup all gateway MacAddresses
            for (String macAddress : macAddresses) {
                xml += "      <device id=\"" + macAddress + "\"/>";
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
    
    public static String convertMacAddresstoDigi(String mac) {
        String digiMac = mac.replaceAll(":","");
        
        digiMac = "00000000-00000000-" + digiMac.substring(0,6) + "FF-FF" + digiMac.substring(6);
        
        return digiMac;
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
    
    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
    
    @Autowired
    public void setSepDeviceClassDao(SepDeviceClassDao sepDeviceClassDao) {
        this.sepDeviceClassDao = sepDeviceClassDao;
    }
    
    @Autowired
    public void setLmGroupDao(LMGroupDao lmGroupDao) {
        this.lmGroupDao = lmGroupDao;
    }
}
