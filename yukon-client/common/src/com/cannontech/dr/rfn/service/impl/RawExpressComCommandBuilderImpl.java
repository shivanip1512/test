package com.cannontech.dr.rfn.service.impl;

import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.exception.InvalidExpressComSerialNumberException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.dao.LMGroupDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.rfn.model.PqrResponseType;
import com.cannontech.dr.rfn.service.RawExpressComCommandBuilder;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteLMConfiguration;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsAppliance;
import com.cannontech.stars.dr.hardware.model.ExpressComAddressView;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public class RawExpressComCommandBuilderImpl implements RawExpressComCommandBuilder {

    private static final Logger log = YukonLogManager.getLogger(RawExpressComCommandBuilderImpl.class);
    
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private StarsCustAccountInformationDao caiDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private LMGroupDao lmGroupDao;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;

    @Override
    public byte[] getCommandAsHexStringByteArray(LmHardwareCommand lmHardwareCommand)
            throws InvalidExpressComSerialNumberException {
        // Write inner ExpressCom payload.
        ByteBuffer innerPayload = getInnerPayload(lmHardwareCommand);

        return wrapAndConvertToAscii(innerPayload); 
    }

    /**
     * Builds ExpressCom command as byte array, see document 'Expresscom Protocol (2.0 Final).docx' for reference.
     * @throws CommandCompletionException 
     */
    private ByteBuffer getInnerPayload(LmHardwareCommand command) throws InvalidExpressComSerialNumberException {
        RfnDevice device = rfnDeviceDao.getDeviceForId(command.getDevice().getDeviceID());
        
        Integer serialNumber;
        try {
            serialNumber = Integer.parseInt(device.getRfnIdentifier().getSensorSerialNumber());
        } catch (NumberFormatException e) {
            log.error("Device serial number either contained alphanumeric characters or was too large. "
                    + "Non-numeric characters are not allowed in ExpressCom message serial numbers. "
                    + "Serial numbers must not be larger than 2147483647."
                    + "Device: " + device + " did not meet these requirements.");
            throw new InvalidExpressComSerialNumberException(
                    "Device serial number either contained alphanumeric characters or was too large. "
                            + "Non-numeric characters are not allowed in ExpressCom message serial numbers. "
                            + "Serial numbers must not be larger than 2147483647."
                            + "Device: " + device.getName() + " did not meet these requirements.");
        }
        ByteBuffer outputBuffer = ByteBuffer.allocate(1024);

        // 0x00 indicates command selects by device serial number.
        outputBuffer.put((byte) 0x00);
        
        // Write serial number as a 4-byte Integer. 
        outputBuffer.putInt(serialNumber);
        
        // Buffer for config data.
        ByteBuffer dataBuffer;
        
        // Add command-type specific payload type & message data.
        LmHardwareCommandType type = command.getType();
        switch(type) {
            case IN_SERVICE:
                outputBuffer.put((byte) 0x15); // Permanent Service Change
                outputBuffer.put((byte) 0x80);
                break;
            case CONFIG:
                ByteBuffer configCommand = getExpressComForConfigCommand(command);
                outputBuffer.put(configCommand.array(), 0, configCommand.position());
                break;
            case READ_NOW:
                outputBuffer.put((byte) 0xA2); // Transmit DRReport Packet
                break;
            case OUT_OF_SERVICE:
                outputBuffer.put((byte) 0x15); // Permanent Service Change
                outputBuffer.put((byte) 0x00);
                break;
            case TEMP_OUT_OF_SERVICE:
                // There are 3 commands here and this is desiged to match Porter. Priority Change, Restore, and Temp Service
                outputBuffer.put((byte) 0x03); // Priority Change Message
                outputBuffer.put((byte) 0x00); // Max Priority
                
                outputBuffer.put((byte) 0x09); // Restore Message
                outputBuffer.put((byte) 0x80); // Flags = Randomization included, all loads restored
                outputBuffer.put((byte) 0x02); // 2 minute randomization
                
                outputBuffer.put((byte) 0x16); // Temporary Service Change Message
                outputBuffer.put((byte) 0x80); // Flags = Disable device, include time, do not disable cold load pickup or lights
                
                Duration duration = command.findParam(LmHardwareCommandParam.DURATION, Duration.class);
                outputBuffer.putShort((short) duration.toStandardHours().getHours());
                
                break;
            case CANCEL_TEMP_OUT_OF_SERVICE:
                getTempOutOfServiceInnerBytes(outputBuffer);
                break;
            case SHED:
                outputBuffer.put((byte) 0x08);
                
                /* Control Flags [BEHDLLLL]:
                Bit 7: B = Bit set then beginning random ramp time for the started of payload is included.  Bit not set then field is omitted from the payload.
                Bit 6: E = Bit set then ending random ramp time for the end of payload is included.  Bit not set then field is omitted from the payload.
                Bit 5: H = Bit set then Control Time is in hours, otherwise it is in minutes.
                Bit 4: D = Bit set then a Delayed Start Time is included in payload (in minutes)
                Bits 0-3: LLLL = Load Number to control (supports 1 to 15 loads with 0 targeting all loads).
                */
                byte flags = command.findParam(LmHardwareCommandParam.RELAY, Integer.class).byteValue();
                final byte ControlInHoursFlag = 0x20;
                
                Duration shedDuration = command.findParam(LmHardwareCommandParam.DURATION, Duration.class);
                
                if (shedDuration.getStandardMinutes() <= 255) {
                    // We can send up to 255 minutes
                    outputBuffer.put(flags);
                    outputBuffer.put((byte) shedDuration.getStandardMinutes());
                } else {
                    // If > 255, we have to send the value in hours. This is rounding down to avoid over control.
                    outputBuffer.put((byte)(flags | ControlInHoursFlag));
                    outputBuffer.put((byte) shedDuration.getStandardHours());
                }
                break;
            case RESTORE:
                
                /* Control Flags [RxxDLLLL]
                Bit 7: R = bit set then Random Start Time is included in the payload.  If not set then random minutes field is omitted.
                Bits 6: Restore Immediatly
                Bits 5: Unused.
                Bit 4: D = Delay Time is included in payload
                Bits 0-3: LLLL = Load Number to control (supports 1 to 15 loads with 0 targeting all loads).
                */
                outputBuffer.put((byte) 0x09);
                byte ctrlFlags = command.findParam(LmHardwareCommandParam.RELAY, Integer.class).byteValue();
                outputBuffer.put(ctrlFlags);
                break;
            case PQR_ENABLE:
                boolean enable = command.findParam(LmHardwareCommandParam.PQR_ENABLE, Boolean.class);
                byte enableVal = enable ? (byte) 1 : (byte) 0;
                outputConfigMessage(outputBuffer, type, enableVal);
                break;
            case PQR_LOV_PARAMETERS:
                dataBuffer = ByteBuffer.allocate(type.getDataLength());
                // Build the message data
                double triggerVolts = command.findParam(LmHardwareCommandParam.PQR_LOV_TRIGGER, Double.class);
                short triggerTenthsOfVolt = (short) (triggerVolts * 10);
                dataBuffer.putShort(triggerTenthsOfVolt);
                double restoreVolts = command.findParam(LmHardwareCommandParam.PQR_LOV_RESTORE, Double.class);
                short restoreTenthsOfVolt = (short) (restoreVolts * 10);
                dataBuffer.putShort(restoreTenthsOfVolt);
                short lovTriggerTime = command.findParam(LmHardwareCommandParam.PQR_LOV_TRIGGER_TIME, Short.class);
                dataBuffer.putShort(lovTriggerTime);
                short lovRestoreTime = command.findParam(LmHardwareCommandParam.PQR_LOV_RESTORE_TIME, Short.class);
                dataBuffer.putShort(lovRestoreTime);
                // Output the config message to the output buffer in appropriate format
                outputConfigMessage(outputBuffer, type, dataBuffer.array());
                break;
            case PQR_LOV_EVENT_DURATION:
                dataBuffer = ByteBuffer.allocate(type.getDataLength());
                // Build the message data
                dataBuffer.put(PqrResponseType.OVER_VOLTAGE.getValue());
                short minLovDuration = command.findParam(LmHardwareCommandParam.PQR_LOV_MIN_EVENT_DURATION, Short.class);
                dataBuffer.putShort(minLovDuration);
                short maxLovDuration = command.findParam(LmHardwareCommandParam.PQR_LOV_MAX_EVENT_DURATION, Short.class);
                dataBuffer.putShort(maxLovDuration);
                // Output the config message to the output buffer in appropriate format
                outputConfigMessage(outputBuffer, type, dataBuffer.array());
                break;
            case PQR_LOV_DELAY_DURATION:
                dataBuffer = ByteBuffer.allocate(type.getDataLength());
                // Build the message data
                dataBuffer.put(PqrResponseType.OVER_VOLTAGE.getValue());
                short lovStartRandomTime = command.findParam(LmHardwareCommandParam.PQR_LOV_START_RANDOM_TIME, Short.class);
                dataBuffer.putShort(lovStartRandomTime);
                short lovEndRandomTime = command.findParam(LmHardwareCommandParam.PQR_LOV_END_RANDOM_TIME, Short.class);
                dataBuffer.putShort(lovEndRandomTime);
                // Output the config message to the output buffer in appropriate format
                outputConfigMessage(outputBuffer, type, dataBuffer.array());
                break;
            case PQR_LOF_PARAMETERS:
                dataBuffer = ByteBuffer.allocate(type.getDataLength());
                // Build the message data
                short triggerMicroseconds = command.findParam(LmHardwareCommandParam.PQR_LOF_TRIGGER, Short.class);
                dataBuffer.putShort(triggerMicroseconds);
                short restoreMicroseconds = command.findParam(LmHardwareCommandParam.PQR_LOF_RESTORE, Short.class);
                dataBuffer.putShort(restoreMicroseconds);
                short lofTriggerTime = command.findParam(LmHardwareCommandParam.PQR_LOF_TRIGGER_TIME, Short.class);
                dataBuffer.putShort(lofTriggerTime);
                short lofRestoreTime = command.findParam(LmHardwareCommandParam.PQR_LOF_RESTORE_TIME, Short.class);
                dataBuffer.putShort(lofRestoreTime);
                // Output the config message to the output buffer in appropriate format
                outputConfigMessage(outputBuffer, type, dataBuffer.array());
                break;
            case PQR_LOF_EVENT_DURATION:
                dataBuffer = ByteBuffer.allocate(type.getDataLength());
                // Build the message data
                dataBuffer.put(PqrResponseType.OVER_FREQUENCY.getValue());
                short minLofDuration = command.findParam(LmHardwareCommandParam.PQR_LOF_MIN_EVENT_DURATION, Short.class);
                dataBuffer.putShort(minLofDuration);
                short maxLofDuration = command.findParam(LmHardwareCommandParam.PQR_LOF_MAX_EVENT_DURATION, Short.class);
                dataBuffer.putShort(maxLofDuration);
                // Output the config message to the output buffer in appropriate format
                outputConfigMessage(outputBuffer, type, dataBuffer.array());
                break;
            case PQR_LOF_DELAY_DURATION:
                dataBuffer = ByteBuffer.allocate(type.getDataLength());
                // Build the message data
                dataBuffer.put(PqrResponseType.OVER_FREQUENCY.getValue());
                short lofStartRandomTime = command.findParam(LmHardwareCommandParam.PQR_LOF_START_RANDOM_TIME, Short.class);
                dataBuffer.putShort(lofStartRandomTime);
                short lofEndRandomTime = command.findParam(LmHardwareCommandParam.PQR_LOF_END_RANDOM_TIME, Short.class);
                dataBuffer.putShort(lofEndRandomTime);
                // Output the config message to the output buffer in appropriate format
                outputConfigMessage(outputBuffer, type, dataBuffer.array());
                break;
            case PQR_EVENT_SEPARATION:
                dataBuffer = ByteBuffer.allocate(type.getDataLength());
                // Build the message data
                short minimumEventSeparation = command.findParam(LmHardwareCommandParam.PQR_EVENT_SEPARATION, Short.class);
                dataBuffer.putShort(minimumEventSeparation);
                // Output the config message to the output buffer in appropriate format
                outputConfigMessage(outputBuffer, type, dataBuffer.array());
                break; 
            default:
                throw new IllegalArgumentException("Command Type: " + type + " not implemented");
        }
        ByteBuffer trimmedOutput = ByteBuffer.allocate(outputBuffer.position());
        trimmedOutput.put(outputBuffer.array(), 0, outputBuffer.position());
        
        log.debug("Inner payload: " + Arrays.toString(trimmedOutput.array()));
        
        return trimmedOutput;
    }

    /**
     * Builds an expresscom configuration message as specified in the ExpressCom Protocol 2.0 spec, section 5.15.
     */
    private void outputConfigMessage(ByteBuffer outputBuffer, LmHardwareCommandType type, byte... dataBytes) {
        //Denotes a Configuration Message
        outputBuffer.put((byte) 0x10);
        
        //Denotes the type of configuration
        outputBuffer.put(type.getConfigNumber());
        
        //Specifies data length
        outputBuffer.put(type.getDataLength());
        
        //Data bytes
        outputBuffer.put(dataBytes);
    }
    
    @Override
    public byte[] getBroadcastCancelAllTempOutOfServiceCommand(int spid) {
        ByteBuffer outputBuffer = ByteBuffer.allocate(32);
        outputBuffer.put((byte) 0x80); // Use only SPID level addressing.
        outputBuffer.putShort((short) spid); 
        getTempOutOfServiceInnerBytes(outputBuffer);
        
        ByteBuffer trimmedOutput = ByteBuffer.allocate(outputBuffer.position());
        trimmedOutput.put(outputBuffer.array(), 0, outputBuffer.position());
        
        return wrapAndConvertToAscii(trimmedOutput);
    }
    
    @Override
    public byte[] getPerformanceVerificationCommand(long messageId) {
        ByteBuffer outputBuffer = ByteBuffer.allocate(32);
        outputBuffer.put((byte) 0xc0); // Use only SPID level addressing.
        outputBuffer.putShort((short) 0x0000); // Use SPID 0 (per Karl Slingsby and Ryan Brager)
        outputBuffer.putShort((short) 0xffff); // Geo 0xFFFF is the all-call universal address.
        
        outputBuffer.put((byte) 0x3c); // Expresscom command code.
        
        outputBuffer.putInt((int) messageId);
        
        ByteBuffer trimmedOutput = ByteBuffer.allocate(outputBuffer.position());
        trimmedOutput.put(outputBuffer.array(), 0, outputBuffer.position());
        
        return wrapAndConvertToAscii(trimmedOutput);
    }
    
    private void getTempOutOfServiceInnerBytes(ByteBuffer outputBuffer) {
        outputBuffer.put((byte) 0x03); // Priority Change Message
        outputBuffer.put((byte) 0x00); // Max Priority
        
        outputBuffer.put((byte) 0x16); // Temporary Service Change
        outputBuffer.put((byte) 0x00); // Cancel a previous temporary out of service command
    }
    
    private byte[] wrapAndConvertToAscii(ByteBuffer xcomPayload) {
        StringWriter sw = new StringWriter();
        // Commands begin with the 's' character.
        sw.write('s');
        sw.write(toHexString(xcomPayload.array()));
        // Commands terminate with 't' character.
        sw.write('t');
        return(sw.toString().getBytes());
    }

    private ByteBuffer getExpressComForConfigCommand(LmHardwareCommand parameters) {
        ByteBuffer configCommand = ByteBuffer.allocate(1024);
        YukonEnergyCompany yec = ecDao.getEnergyCompanyByInventoryId(parameters.getDevice().getInventoryID());
        boolean trackAddressing = energyCompanySettingDao.getBoolean(EnergyCompanySettingType.TRACK_HARDWARE_ADDRESSING, yec.getEnergyCompanyId());

        // Either we have track hardware addressing on or we need a group/appliance category.
        if (trackAddressing) {
            //Use per-device addressing info.
            LiteLMConfiguration config = parameters.getDevice().getLMConfiguration();
            if (config == null) {
                throw new BadConfigurationException("There is no configuration saved for serial #" + parameters.getDevice().getDeviceID() + ".");
            }
        } else if (!trackAddressing) {
            Integer optionalGroupId = null;
            Object param = parameters.findParam(LmHardwareCommandParam.OPTIONAL_GROUP_ID,
                    LmHardwareCommandParam.OPTIONAL_GROUP_ID.getClazz());
            if (param != null) {
                optionalGroupId = (Integer) param;
                if (optionalGroupId > 0) {
                    // Use group supplied in parameters.
                    configCommand.put(buildAddressingConfig(optionalGroupId));
                }
            } else {  
                // Get group from appliance category.
                LiteLmHardwareBase device = parameters.getDevice();
                if (device.getAccountID() > 0) {
                    LiteAccountInfo liteAcctInfo = caiDao.getByAccountId(device.getAccountID());
    
                    boolean configFound = false;
                    for (LiteStarsAppliance liteApp : liteAcctInfo.getAppliances()) {
                        int appInventoryId = liteApp.getInventoryID();
                        int inventoryId = device.getInventoryID();
                        int addressingGroupId = liteApp.getAddressingGroupID();
                        
                        if (appInventoryId == inventoryId && addressingGroupId > 0) {
                            try {
                                ByteBuffer addressing = buildAddressingConfig(addressingGroupId);
                                configCommand.put(addressing.array(),0,addressing.position());
                                configFound=true;
                            } catch (NotFoundException e) {
                                log.error(e.getMessage(), e);
                            }
                        } else if (appInventoryId == inventoryId && addressingGroupId == 0) {
                            throw new BadConfigurationException("Addressing Group is not assigned.  If no groups are available in the Assigned Group column, please verify that your programs are valid Yukon LM Programs with assigned load groups.");
                        }
                    }
                    if (configFound == false) {
                        throw new BadConfigurationException("Addressing Group is not assigned.  If no groups are available in the Assigned Group column, please verify that your programs are valid Yukon LM Programs with assigned load groups.");
                    }
                } else {
                    throw new BadConfigurationException("Unable to find STARS account info for device: " + device.getDeviceLabel());
                }
            }
        }
        return configCommand;
    }

    private ByteBuffer buildAddressingConfig(int groupId) {
        ByteBuffer addressing = ByteBuffer.allocate(1024);
        ExpressComAddressView config = lmGroupDao.getExpressComAddressing(groupId);
        
        // Build GEO addressing byte buffer based on the address usage of the group.
        String addressUsage = config.getUsage().toLowerCase();
        
        // Config 0x01 - GEO addressing
        // Format: 0x10 0x01 <length> <data>
        byte xcomAddressField = 0;
        final int xcomAddressFieldSize = 1; //Size of the xcomAddressField in bytes.
        ByteBuffer geoBuffer = ByteBuffer.allocate(1024);

        // Parse addressUsage to find what data is available. 
        if (addressUsage.contains("s")) { 
            xcomAddressField |= 0x80; 
            geoBuffer.putShort((short) config.getSpid()); 
        }
        if (addressUsage.contains("g")) { 
            xcomAddressField |= 0x40;
            geoBuffer.putShort((short) config.getGeo()); 
        }
        if (addressUsage.contains("b")) { 
            xcomAddressField |= 0x20; 
            geoBuffer.putShort((short) config.getSubstation()); 
        } 
        if (addressUsage.contains("f")) { 
            xcomAddressField |= 0x10; 
            geoBuffer.putShort((short) config.getFeeder()); 
        }
        if (addressUsage.contains("z")) { 
            xcomAddressField |= 0x08; 
            geoBuffer.put((byte) ((config.getZip() >>> 16) & 0xFF)); 
            geoBuffer.put((byte) ((config.getZip() >>> 8) & 0xFF));
            geoBuffer.put((byte)  (config.getZip() & 0xFF));
        }
        if (addressUsage.contains("u")) { 
            xcomAddressField |= 0x04; 
            geoBuffer.putShort((short) config.getUser()); 
        }

        if (xcomAddressField != 0) {
            //One of these address levels was provided, lets send it:
            addressing.put((byte) 0x10); // Denotes a Config message.
            addressing.put((byte) 0x01); // GEO level addressing config message.
            addressing.put((byte) (geoBuffer.position() + xcomAddressFieldSize)); // Length
            addressing.put(xcomAddressField);
            addressing.put(geoBuffer.array(),0,geoBuffer.position()); 
        }
      
        // Config 0x07 - Program, splinter, load addressing
        // Format: 0x10 0x07 <length> <data>
        ByteBuffer loadBuffer = ByteBuffer.allocate(1024);
        
        xcomAddressField = 0;  // Clear all address field flags.
        
        if (addressUsage.contains("p")) {
            xcomAddressField |= 0x20; 
            loadBuffer.put((byte) config.getProgram()); 
        }
        if (addressUsage.contains("r")) {
            xcomAddressField |= 0x10; 
            loadBuffer.put((byte) config.getSplinter()); 
        }
        
        if (xcomAddressField != 0) {
            for(int i = 1; i < 10; i++) {
                if (config.getRelay().contains(Integer.toString(i))) {
                    addressing.put((byte) 0x10); // Denotes a Config message.
                    addressing.put((byte) 0x07); // Load level addressing config message.
                    addressing.put((byte) (loadBuffer.position() + xcomAddressFieldSize));
                    addressing.put((byte) (xcomAddressField | i));
                    addressing.put(loadBuffer.array(),0,loadBuffer.position());
                }
            }
        }
        ByteBuffer addressingResized = ByteBuffer.allocate(addressing.position());
        addressingResized.put(addressing.array(), 0, addressing.position());
        return addressingResized;
    }
    
    public static String toHexString(byte[] bytes) {
        char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j*2] = hexArray[v/16];
            hexChars[j*2 + 1] = hexArray[v%16];
        }
        return new String(hexChars);
    }
    
    @Override
    public boolean isValidBroadcastSpid(int spid) {
        if (spid >= 1 && spid <= 65534) {
            log.debug("Valid numeric SPID found in role property 'Broadcast Opt Out Cancel SPID'. Broadcast messaging will be used with SPID:" + spid);
            return true;
        } else if (spid == 0) {
            log.debug("Role property value 'Broadcast Opt Out Cancel SPID' was blank or 0.  Per-device messaging will be used.");
        } else {
            log.error("Out-of-range SPID address specified for broadcast cancel all opt out command.\n" +
                    "Valid values are 1 - 65534.  The invalid SPID was: " + spid);
        }
        return false;
    }
}