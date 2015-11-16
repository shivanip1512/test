package com.cannontech.web.stars.dr.operator.hardware.model;

import java.util.Arrays;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.dr.hardware.service.impl.PorterExpressComCommandBuilder;
import com.cannontech.stars.xml.serialize.SA205;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;

public class Sa205Dto implements AddressingInfo {
    
    private Integer slots[] = new Integer[6];
    
    public Sa205Dto() {
        Arrays.fill(slots, PorterExpressComCommandBuilder.SA205_UNUSED_ADDR);
    }
    
    public Integer[] getSlots() {
        return slots;
    }
    
    public void setSlots(Integer[] slots) {
        this.slots = slots;
    }
    
    @Override
    public void updateFromStarsConfiguration(StarsLMConfiguration config) {
        SA205 sa205 = config.getSA205();
        slots[0] = HardwareConfig.nullIfZero(sa205.getSlot1());
        slots[1] = HardwareConfig.nullIfZero(sa205.getSlot2());
        slots[2] = HardwareConfig.nullIfZero(sa205.getSlot3());
        slots[3] = HardwareConfig.nullIfZero(sa205.getSlot4());
        slots[4] = HardwareConfig.nullIfZero(sa205.getSlot5());
        slots[5] = HardwareConfig.nullIfZero(sa205.getSlot6());
    }
    
    @Override
    public void updateStarsConfiguration(StarsLMConfiguration config) {
        SA205 sa205 = new SA205();
        sa205.setSlot1(slots[0] == null ? 0 : slots[0]);
        sa205.setSlot2(slots[1] == null ? 0 : slots[1]);
        sa205.setSlot3(slots[2] == null ? 0 : slots[2]);
        sa205.setSlot4(slots[3] == null ? 0 : slots[3]);
        sa205.setSlot5(slots[4] == null ? 0 : slots[4]);
        sa205.setSlot6(slots[5] == null ? 0 : slots[5]);
        config.setSA205(sa205);
    }
    
    @Override
    public void validate(Errors errors) {
        validate(errors, "");
    }
    
    @Override
    public void validate(Errors errors, String path) {
        for (int index = 0; index < slots.length; index++) {
            YukonValidationUtils.checkRange(errors, path + "addressingInfo.slots[" + index + "]",
                                            slots[index], 0, 4095, false);
        }
    }
    
}