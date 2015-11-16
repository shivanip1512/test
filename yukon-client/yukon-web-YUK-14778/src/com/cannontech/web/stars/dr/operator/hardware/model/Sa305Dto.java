package com.cannontech.web.stars.dr.operator.hardware.model;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.xml.serialize.SA305;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;

public class Sa305Dto implements AddressingInfo {
    
    private Integer utility = 0; // also used by SA305
    private Integer group = null;
    private Integer division = null;
    private Integer substation = null;
    private Integer rate = null;
    private Integer rateFamily = null;
    private Integer rateMember = null;
    
    public Integer getUtility() {
        return utility;
    }
    
    public void setUtility(Integer utility) {
        this.utility = utility;
    }
    
    public Integer getGroup() {
        return group;
    }
    
    public void setGroup(Integer group) {
        this.group = group;
    }
    
    public Integer getDivision() {
        return division;
    }
    
    public void setDivision(Integer division) {
        this.division = division;
    }
    
    public Integer getSubstation() {
        return substation;
    }
    
    public void setSubstation(Integer substation) {
        this.substation = substation;
    }
    
    public Integer getRate() {
        return rate;
    }
    
    public void setRate(Integer rate) {
        this.rate = rate;
    }
    
    public Integer getRateFamily() {
        return rateFamily;
    }
    
    public void setRateFamily(Integer rateFamily) {
        this.rateFamily = rateFamily;
    }
    
    public Integer getRateMember() {
        return rateMember;
    }
    
    public void setRateMember(Integer rateMember) {
        this.rateMember = rateMember;
    }
    
    @Override
    public void updateFromStarsConfiguration(StarsLMConfiguration config) {
        
        SA305 sa305 = config.getSA305();
        utility = sa305.getUtility();
        group = HardwareConfig.nullIfZero(sa305.getGroup());
        division = HardwareConfig.nullIfZero(sa305.getDivision());
        substation = HardwareConfig.nullIfZero(sa305.getSubstation());
        rate = HardwareConfig.nullIfZero(sa305.getRateRate());
        rateFamily = HardwareConfig.nullIfZero(sa305.getRateFamily());
        rateMember = HardwareConfig.nullIfZero(sa305.getRateMember());
    }
    
    @Override
    public void updateStarsConfiguration(StarsLMConfiguration config) {
        
        SA305 sa305 = new SA305();
        sa305.setUtility(utility == null ? 0 : utility);
        sa305.setGroup(group == null ? 0 : group);
        sa305.setDivision(division == null ? 0 : division);
        sa305.setSubstation(substation == null ? 0 : substation);
        sa305.setRateRate(rate == null ? 0 : rate);
        
        // Rate Hierarchy should not be on the configuration page; it is
        // part of the control command only
        sa305.setRateHierarchy(0);
        
        config.setSA305(sa305);
    }
    
    @Override
    public void validate(Errors errors) {
        validate(errors, "");
    }
    
    @Override
    public void validate(Errors errors, String path) {
        YukonValidationUtils.checkRange(errors, path + "addressingInfo.utility", utility,
                                        0, 15, true);
        YukonValidationUtils.checkRange(errors, path + "addressingInfo.group", group,
                                        0, 63, false);
        YukonValidationUtils.checkRange(errors, path + "addressingInfo.division", division,
                                        0, 63, false);
        YukonValidationUtils.checkRange(errors, path + "addressingInfo.substation", substation,
                                        0, 1023, false);
        YukonValidationUtils.checkRange(errors, path + "addressingInfo.rate", rate,
                                        0, 127, false);
    }
    
}