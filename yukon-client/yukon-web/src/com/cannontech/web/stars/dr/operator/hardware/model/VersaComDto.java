package com.cannontech.web.stars.dr.operator.hardware.model;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.VersaCom;

public class VersaComDto implements AddressingInfo {
    // HardwareConfigType.VERSACOM
    private Integer utility = 0; // also used by SA305
    private Integer section = null;
    private boolean[] classAddressBits = new boolean[16];
    private boolean[] divisionBits = new boolean[16];

    public Integer getUtility() {
        return utility;
    }

    public void setUtility(Integer utility) {
        this.utility = utility;
    }

    public Integer getSection() {
        return section;
    }

    public void setSection(Integer section) {
        this.section = section;
    }

    public boolean[] getClassAddressBits() {
        return classAddressBits;
    }

    public void setClassAddressBits(boolean[] classAddressBits) {
        this.classAddressBits = classAddressBits;
    }

    public boolean[] getDivisionBits() {
        return divisionBits;
    }

    public void setDivisionBits(boolean[] divisionBits) {
        this.divisionBits = divisionBits;
    }

    @Override
    public void updateFromStarsConfiguration(StarsLMConfiguration config) {
        VersaCom versaCom = config.getVersaCom();
        utility = versaCom.getUtility();
        section = HardwareConfigurationDto.nullIfZero(versaCom.getSection());
        HardwareConfigurationDto.splitBits(versaCom.getClassAddress(), classAddressBits);
        HardwareConfigurationDto.splitBits(versaCom.getDivision(), divisionBits);
    }

    @Override
    public void updateStarsConfiguration(StarsLMConfiguration config) {
        VersaCom versaCom = new VersaCom();
        versaCom.setUtility(utility);
        versaCom.setSection(section == null ? 0 : section);
        versaCom.setClassAddress(HardwareConfigurationDto.joinBits(classAddressBits));
        versaCom.setDivision(HardwareConfigurationDto.joinBits(divisionBits));
        config.setVersaCom(versaCom);
    }

    @Override
    public void validate(Errors errors) {
        YukonValidationUtils.checkRange(errors, "addressingInfo.utility", utility,
                                        0, 255, true);
        YukonValidationUtils.checkRange(errors, "addressingInfo.section", section,
                                        1, 254, false);
    }
}
