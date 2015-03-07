package com.cannontech.web.stars.dr.operator.hardware.model;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;

public class HardwareConfig {
    
    private HardwareConfigType type;
    
    // hardware addressing fields (used only when TRACK_HARDWARE_ADDRESSING role property is true)
    private AddressingInfo addressingInfo = null;
    
    private String[] coldLoadPickup = new String[8];
    private String[] tamperDetect = new String[2];
    
    public HardwareConfig(HardwareConfigType type) {
        
        this.type = type;
        
        if (type != null) {
            switch (type) {
            case EXPRESSCOM:
                addressingInfo = new ExpressComDto();
                break;
            case VERSACOM:
                addressingInfo = new VersaComDto();
                break;
            case SA205:
                addressingInfo = new Sa205Dto();
                break;
            case SA305:
                addressingInfo = new Sa305Dto();
                break;
            case SA_SIMPLE:
                addressingInfo = new SaSimpleDto();
                break;
            case ECOBEE:
                break;
            case NOT_CONFIGURABLE:
                break;
            case SEP:
                break;
            default:
                break;
            }
        }
        
        Arrays.fill(coldLoadPickup, "");
        Arrays.fill(tamperDetect, "");
    }
    
    public HardwareConfigType getType() {
        return type;
    }
    
    public void setType(HardwareConfigType type) {
        this.type = type;
    }
    
    public AddressingInfo getAddressingInfo() {
        return addressingInfo;
    }
    
    public void setAddressingInfo(AddressingInfo addressingInfo) {
        this.addressingInfo = addressingInfo;
    }
    
    public String[] getColdLoadPickup() {
        return coldLoadPickup;
    }
    
    public void setColdLoadPickup(String[] coldLoadPickup) {
        this.coldLoadPickup = coldLoadPickup;
    }
    
    public String[] getTamperDetect() {
        return tamperDetect;
    }
    
    public void setTamperDetect(String[] tamperDetect) {
        this.tamperDetect = tamperDetect;
    }
    
    static void splitBits(int bitField, boolean[] destination) {
        for (int bitPosition = 0; bitPosition < 16; bitPosition++) {
            destination[bitPosition] = (bitField & (1 << bitPosition)) > 0;
        }
    }
    
    static Integer nullIfZero(int intIn) {
        return intIn == 0 ? null : intIn;
    }
    
    static Integer[] stringToIntegerArray(String string) {
        
        String[] stringArray = string.split(",", -1);
        Integer[] retVal = new Integer[stringArray.length];
        for (int index = 0; index < retVal.length; index++) {
            if (StringUtils.isNotBlank(stringArray[index])) {
                retVal[index] = Integer.parseInt(stringArray[index]);
            }
        }
        
        return retVal;
    }
    
    static int joinBits(boolean[] bits) {
        
        int retVal = 0;
        for (int bitPosition = 0; bitPosition < bits.length; bitPosition++) {
            if (bits[bitPosition]) {
                retVal |= (1 << bitPosition);
            }
        }
        
        return retVal;
    }
    
    public void setStarsLMConfiguration(StarsLMConfiguration config,
            HardwareType hardwareType) {
        if (config == null) {
            return;
        }
        
        if (addressingInfo != null) {
            addressingInfo.updateFromStarsConfiguration(config);
        }
        
        if (config.getColdLoadPickup() != null) {
            String[] clp = config.getColdLoadPickup().split(",");
            System.arraycopy(clp, 0, coldLoadPickup, 0, clp.length);
        }
        
        if (config.getTamperDetect() != null) {
            String[] td = config.getTamperDetect().split(",");
            System.arraycopy(td, 0, tamperDetect, 0, td.length);
        }
    }
    
    public StarsLMConfiguration getStarsLMConfiguration(HardwareType hardwareType) {
        
        StarsLMConfiguration config = new StarsLMConfiguration();
        config.setColdLoadPickup(StringUtils.join(coldLoadPickup, ','));
        
        if (hardwareType.getHardwareConfigType().isHasTamperDetect()) {
            config.setTamperDetect(StringUtils.join(tamperDetect, ','));
        }
        
        if (addressingInfo != null) {
            addressingInfo.updateStarsConfiguration(config);
        }
        
        return config;
    }
    
}