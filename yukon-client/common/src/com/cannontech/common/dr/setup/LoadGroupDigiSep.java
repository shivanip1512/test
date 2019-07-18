package com.cannontech.common.dr.setup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.cannontech.database.data.device.lm.LMGroupDigiSep;
import com.cannontech.database.data.device.lm.SepDeviceClass;
import com.cannontech.database.db.device.lm.LMGroupSep;

public class LoadGroupDigiSep extends LoadGroupBase<LMGroupDigiSep> {

    private Integer utilityEnrollmentGroup;
    private List<SepDeviceClass> deviceClassSet;
    private Integer rampInMinutes;
    private Integer rampOutMinutes;

    public Integer getUtilityEnrollmentGroup() {
        return utilityEnrollmentGroup;
    }

    public void setUtilityEnrollmentGroup(Integer utilityEnrollmentGroup) {
        this.utilityEnrollmentGroup = utilityEnrollmentGroup;
    }

    public List<SepDeviceClass> getDeviceClassSet() {
        return deviceClassSet;
    }

    public void setDeviceClassSet(List<SepDeviceClass> deviceClassSet) {
        this.deviceClassSet = deviceClassSet;
    }

    public Integer getRampInMinutes() {
        return rampInMinutes;
    }

    public void setRampInMinutes(Integer rampInMinutes) {
        this.rampInMinutes = rampInMinutes;
    }

    public Integer getRampOutMinutes() {
        return rampOutMinutes;
    }

    public void setRampOutMinutes(Integer rampOutMinutes) {
        this.rampOutMinutes = rampOutMinutes;
    }

    @Override
    public void buildModel(LMGroupDigiSep lmGroupDigiSep) {
        // Set parent fields
        super.buildModel(lmGroupDigiSep);

        setUtilityEnrollmentGroup(lmGroupDigiSep.getLmGroupSep().getUtilityEnrollmentGroup());
        setDeviceClassSet(new ArrayList<>(lmGroupDigiSep.getLmGroupSep().getDeviceClassSet()));
        setRampInMinutes(lmGroupDigiSep.getLmGroupSep().getRampInMinutes());
        setRampOutMinutes(lmGroupDigiSep.getLmGroupSep().getRampOutMinutes());

    }

    @Override
    public void buildDBPersistent(LMGroupDigiSep lmGroupDigiSep) {
        // Set parent fields
        super.buildDBPersistent(lmGroupDigiSep);

        LMGroupSep digiSep = lmGroupDigiSep.getLmGroupSep();
        digiSep.setUtilityEnrollmentGroup(getUtilityEnrollmentGroup());
        digiSep.setDeviceClassSet(new HashSet<>(getDeviceClassSet()));
        digiSep.setRampInMinutes(getRampInMinutes());
        digiSep.setRampOutMinutes(getRampOutMinutes());

        lmGroupDigiSep.setLmGroupSep(digiSep);

    }

}
