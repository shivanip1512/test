package com.cannontech.web.stars.dr.operator.hardware.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.xml.serialize.ExpressCom;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;

public class ExpressComDto implements AddressingInfo {
    private Integer serviceProvider = 0;
    private Integer geo = null;
    private Integer substation = null;  // also used by SA305
    private boolean[] feederBits = new boolean[16];
    private Integer zip = null;
    private Integer userAddress = null;

    private Integer[] program = new Integer[8];
    private Integer[] splinter = new Integer[8];

    public Integer getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(Integer serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Integer getGeo() {
        return geo;
    }

    public void setGeo(Integer geo) {
        this.geo = geo;
    }

    public Integer getSubstation() {
        return substation;
    }

    public void setSubstation(Integer substation) {
        this.substation = substation;
    }

    public boolean[] getFeederBits() {
        return feederBits;
    }

    public void setFeederBits(boolean[] feederBits) {
        this.feederBits = feederBits;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    public Integer getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(Integer userAddress) {
        this.userAddress = userAddress;
    }

    public Integer[] getProgram() {
        return program;
    }

    public void setProgram(Integer[] Program) {
        this.program = Program;
    }

    public Integer[] getSplinter() {
        return splinter;
    }

    public void setExpressComSplinter(Integer[] expressComSplinter) {
        this.splinter = expressComSplinter;
    }

    @Override
    public void updateFromStarsConfiguration(StarsLMConfiguration config) {
        ExpressCom expressCom = config.getExpressCom();

        serviceProvider = expressCom.getServiceProvider();
        geo = HardwareConfigurationDto.nullIfZero(expressCom.getGEO());
        substation = HardwareConfigurationDto.nullIfZero(expressCom.getSubstation());

        HardwareConfigurationDto.splitBits(expressCom.getFeeder(), feederBits);

        zip = HardwareConfigurationDto.nullIfZero(expressCom.getZip());
        userAddress = HardwareConfigurationDto.nullIfZero(expressCom.getUserAddress());

        program = HardwareConfigurationDto.stringToIntegerArray(config.getExpressCom().getProgram());
        splinter = HardwareConfigurationDto.stringToIntegerArray(config.getExpressCom().getSplinter());
    }

    @Override
    public void updateStarsConfiguration(StarsLMConfiguration config) {
        ExpressCom expressCom = new ExpressCom();
        expressCom.setServiceProvider(serviceProvider);
        expressCom.setGEO(geo == null ? 0 : geo);
        expressCom.setSubstation(substation == null ? 0 : substation);
        expressCom.setFeeder(HardwareConfigurationDto.joinBits(feederBits));
        expressCom.setZip(zip == null ? 0 : zip);
        expressCom.setUserAddress(userAddress == null ? 0 : userAddress);

        expressCom.setProgram(StringUtils.join(program, ','));
        expressCom.setSplinter(StringUtils.join(splinter, ','));
        config.setExpressCom(expressCom);
    }

    @Override
    public void validate(Errors errors) {
        YukonValidationUtils.checkRange(errors, "addressingInfo.serviceProvider",
                                        serviceProvider,
                                        0, 65534, true);
        YukonValidationUtils.checkRange(errors, "addressingInfo.geo", geo,
                                        0, 65534, false);
        YukonValidationUtils.checkRange(errors, "addressingInfo.substation",
                                        substation, 0, 65534,
                                        false);
        YukonValidationUtils.checkRange(errors, "addressingInfo.zip", zip,
                                        0, 16777214, false);
        YukonValidationUtils.checkRange(errors, "addressingInfo.userAddress",
                                        userAddress,
                                        0, 65534, false);

        for (int index = 0; index < program.length; index++) {
            YukonValidationUtils.checkRange(errors, "addressingInfo.program[" + index + "]",
                                            program[index], 0, 254, false);
        }
        for (int index = 0; index < splinter.length; index++) {
            YukonValidationUtils.checkRange(errors,
                                            "addressingInfo.splinter[" + index + "]",
                                            splinter[index], 0, 254, false);
        }
    }
}
