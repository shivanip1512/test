package com.cannontech.dr.rfn.service.impl;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.database.db.device.lm.LMGroupExpressCom;
import com.cannontech.dr.rfn.service.RawExpressComCommandBuilder;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteLMConfiguration;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsAppliance;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public class RawExpressComCommandBuilderImpl implements RawExpressComCommandBuilder {

    private static final Logger log = YukonLogManager.getLogger(RawExpressComCommandBuilderImpl.class);
    
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private EnergyCompanyRolePropertyDao ecRolePropertyDao;
    @Autowired private StarsCustAccountInformationDao caiDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    
    @Override
    public byte[] getCommand(LmHardwareCommand parameters) {
        ByteBuffer cmd = ByteBuffer.allocate(1024);
        
        RfnDevice device = rfnDeviceDao.getDeviceForId(parameters.getDevice().getDeviceID());
        
        Integer serialNumber = Integer.parseInt(device.getRfnIdentifier().getSensorSerialNumber());

        Charset charset = Charset.forName("US-ASCII");
        // Commands begin with the 's' character.
        cmd.put(charset.encode("s"));
        
        // 0x00 indicates command selects by device serial number.
        cmd.put((byte) 0x00);
        
        // Write serial number as a 4-byte Integer. 
        cmd.putInt(serialNumber);
        
        // Add command-type specific payload type & message data.
        switch(parameters.getType()) {
            case IN_SERVICE:
                cmd.put((byte) 0x15);
                cmd.put((byte) 0x80);
                break;
            case OUT_OF_SERVICE:
                cmd.put((byte) 0x15);
                cmd.put((byte) 0x00);
                break;
            case CONFIG:
                ByteBuffer configCommand = getExpressComForConfigCommand(parameters);
                cmd.put(configCommand.array(), 0, configCommand.position());
                break;
            case READ_NOW:
                cmd.put((byte) 0xA2);
                break;
        }
        // Commands terminate with 't' character.
        cmd.put(charset.encode("t"));
        
        byte[] output = new byte[cmd.position()];
        cmd.rewind();
        cmd.get(output);
        
        return output;
    }

    private ByteBuffer getExpressComForConfigCommand(LmHardwareCommand parameters) {
        ByteBuffer configCommand = ByteBuffer.allocate(1024);
        YukonEnergyCompany yec = yukonEnergyCompanyService.getEnergyCompanyByInventoryId(parameters.getDevice().getInventoryID());
        boolean trackAddressing = ecRolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.TRACK_HARDWARE_ADDRESSING, yec);
        
        // Either we have track hardware addressing on or we need a group/appliance category.
        if(trackAddressing) {
            //Use per-device addressing info.
            LiteLMConfiguration config = parameters.getDevice().getLMConfiguration();
            if (config == null) {
                throw new BadConfigurationException("There is no configuration saved for serial #" + parameters.getDevice().getDeviceID() + ".");
            }
            String[] programs = config.getExpressCom().getProgram().split(",");
            String[] splinters = config.getExpressCom().getSplinter().split(",");
            ByteBuffer loadBuffer = ByteBuffer.allocate(1024);
            byte xcomAddressField = 0;
            // TODO: Handle varying program/splinter config between loads.
            /*
            for (int loadNo = 1; loadNo <= 8; loadNo++) {
                int prog = 0;
                if (programs.length >= loadNo && programs[loadNo - 1].length() > 0) {
                    prog = Integer.parseInt(programs[loadNo - 1]);
                }
                int splt = 0;
                if (splinters.length >= loadNo && splinters[loadNo - 1].length() > 0) {
                    splt = Integer.parseInt(splinters[loadNo - 1]);
                }

                if (prog > 0) {
                    xcomAddressField |= 0x20; 
                    //loadBuffer.put((byte) config.getProgramID().intValue());
                }
                if (splt > 0) {
                    xcomAddressField |= 0x10; 
                    //loadBuffer.put((byte) group.getSplinterID().intValue()); 
                }
             
            }
            */
        } else if(!trackAddressing) {
            Integer optionalGroupId = null;
            Object param = parameters.findParam(LmHardwareCommandParam.OPTIONAL_GROUP_ID,
                    LmHardwareCommandParam.OPTIONAL_GROUP_ID.getClass());
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
    
                    for (LiteStarsAppliance liteApp : liteAcctInfo.getAppliances()) {
                        int appInventoryId = liteApp.getInventoryID();
                        int inventoryId = device.getInventoryID();
                        int addressingGroupId = liteApp.getAddressingGroupID();
                        
                        if (appInventoryId == inventoryId && addressingGroupId > 0) {
                            try {
                                ByteBuffer addressing = buildAddressingConfig(addressingGroupId);
                                configCommand.put(addressing.array(),0,addressing.position());
                            } catch (NotFoundException e) {
                                log.error(e.getMessage(), e);
                            }
                        } else if (appInventoryId == inventoryId && addressingGroupId == 0) {
                            throw new BadConfigurationException("Unable to config since no Addressing Group is assigned.  If no groups are available in the Assigned Group column, please verify that your programs are valid Yukon LM Programs with assigned load groups.");
                        }
                    }
                }
            }
        }
        return configCommand;
    }

    private ByteBuffer buildAddressingConfig(Integer groupId) {
        ByteBuffer addressing = ByteBuffer.allocate(1024);
        LMGroupExpressCom group = new LMGroupExpressCom();
        group.setLmGroupID(groupId);
        dbPersistentDao.retrieveDBPersistent(group);
        
        // Build GEO addressing byte buffer based on the address usage of the group.
        String addressUsage = group.getAddressUsage().toLowerCase();
        
        // Config 0x01 - GEO addressing
        // Format: 0x10 0x01 <length> <data>
        byte xcomAddressField = 0;
        ByteBuffer geoBuffer = ByteBuffer.allocate(1024);

        // Parse addressUsage to find what data is available. 
        if (addressUsage.contains("s")) { 
            xcomAddressField |= 0x80; 
            geoBuffer.putShort((short) group.getServiceProviderID().intValue()); 
        }
        if (addressUsage.contains("g")) { 
            xcomAddressField |= 0x40;
            geoBuffer.putShort((short) group.getGeoID().intValue()); 
        }
        if (addressUsage.contains("b")) { 
            xcomAddressField |= 0x20; 
            geoBuffer.putShort((short) group.getSubstationID().intValue()); 
        } 
        if (addressUsage.contains("f")) { 
            xcomAddressField |= 0x10; 
            geoBuffer.putShort((short) group.getFeederID().intValue()); 
        }
        if (addressUsage.contains("z")) { 
            xcomAddressField |= 0x08; 
            geoBuffer.put((byte) ((group.getZipID().intValue() >>> 16) & 0xFF)); 
            geoBuffer.put((byte) ((group.getZipID().intValue() >>> 8) & 0xFF));
            geoBuffer.put((byte)  (group.getZipID().intValue() & 0xFF));
        }
        if (addressUsage.contains("u")) { 
            xcomAddressField |= 0x04; 
            geoBuffer.putShort((short) group.getUserID().intValue()); 
        }

        if(xcomAddressField != 0) {
            //One of these address levels was provided, lets send it:
            addressing.put((byte) 0x10); // Denotes a Config message.
            addressing.put((byte) 0x01); // GEO level addressing config message.
            addressing.put((byte) geoBuffer.position()); // Length
            addressing.put(geoBuffer.array(),0,geoBuffer.position()); 
        }
      
        // Config 0x07 - Program, splinter, load addressing
        // Format: 0x10 0x07 <length> <data>
        ByteBuffer loadBuffer = ByteBuffer.allocate(1024);
        final int xcomAddressFieldSize = 1; //Size of the xcomAddressField in bytes.
        
        xcomAddressField = 0;  // Clear all address field flags.
        
        if(addressUsage.contains("p")) {
            xcomAddressField |= 0x20; 
            loadBuffer.put((byte) group.getProgramID().intValue()); 
        }
        if(addressUsage.contains("r")) {
            xcomAddressField |= 0x10; 
            loadBuffer.put((byte) group.getSplinterID().intValue()); 
        }
        
        if(xcomAddressField != 0) {
            Boolean foundRelay = false;
            for(int i = 1; i < 10; i++)
            {
                if(group.getRelayUsage().contains(Integer.toString(i)))
                {
                    foundRelay = true;
                    
                    addressing.put((byte) 0x10); // Denotes a Config message.
                    addressing.put((byte) 0x07); // Load level addressing config message.
                    addressing.put((byte) (loadBuffer.position() + xcomAddressFieldSize));
                    addressing.put((byte) (xcomAddressField | i));
                    addressing.put(loadBuffer.array(),0,loadBuffer.position());
                }
            }
            
            if(!foundRelay) {
                addressing.put((byte) 0x10); // Denotes a Config message.
                addressing.put((byte) 0x07); // Load level addressing config message.
                addressing.put((byte) (loadBuffer.position() + xcomAddressFieldSize));
                addressing.put((byte) xcomAddressField);
                addressing.put(loadBuffer.array(),0,loadBuffer.position());   
            }
        }
        ByteBuffer addressingResized = ByteBuffer.allocate(addressing.position());
        addressingResized.put(addressing.array(), 0, addressing.position());
        return addressingResized;
    }

}