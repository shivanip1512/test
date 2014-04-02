package com.cannontech.web.stars.dr.operator.hardware.model;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.LazyList;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;

public class HardwareConfigurationDto {
    private int accountId;
    private int inventoryId;
    private String action;
    private List<ProgramEnrollmentDto> programEnrollments =
        LazyList.ofInstance(ProgramEnrollmentDto.class);

    // hardware addressing fields (used only when TRACK_HARDWARE_ADDRESSING
    // role property is true)
    private AddressingInfo addressingInfo = null;

    // Relay section
    private String[] coldLoadPickup = new String[8];
    private String[] tamperDetect = new String[2];


    public HardwareConfigurationDto(HardwareConfigType hardwareConfigType) {
        if (hardwareConfigType != null) {
            switch (hardwareConfigType) {
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
            }
        }

        Arrays.fill(coldLoadPickup, "");
        Arrays.fill(tamperDetect, "");
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<ProgramEnrollmentDto> getProgramEnrollments() {
        return programEnrollments;
    }

    public void setProgramEnrollments(List<ProgramEnrollmentDto> programEnrollments) {
        this.programEnrollments = programEnrollments;
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

        if (hardwareType.isHasTamperDetect()) {
            config.setTamperDetect(StringUtils.join(tamperDetect, ','));
        }

        if (addressingInfo != null) {
            addressingInfo.updateStarsConfiguration(config);
        }
        return config;
    }
}
