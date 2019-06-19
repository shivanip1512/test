package com.cannontech.common.dr.setup;

import java.util.Set;

import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupDigiSep;
import com.cannontech.database.data.device.lm.SepDeviceClass;

public class LoadGroupDigiSep extends LoadGroupBase {

    private Integer utilityEnrollmentGroup;
    private Set<SepDeviceClass> deviceClassSet;
    private Integer rampInMinutes = 30;
    private Integer rampOutMinutes = 30;

    public Integer getUtilityEnrollmentGroup() {
        return utilityEnrollmentGroup;
    }

    public void setUtilityEnrollmentGroup(Integer utilityEnrollmentGroup) {
        this.utilityEnrollmentGroup = utilityEnrollmentGroup;
    }

    public Set<SepDeviceClass> getDeviceClassSet() {
        return deviceClassSet;
    }

    public void setDeviceClassSet(Set<SepDeviceClass> deviceClassSet) {
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
    public void buildModel(LMGroup loadGroup) {
        // Set parent fields
        super.buildModel(loadGroup);

        setUtilityEnrollmentGroup(((LMGroupDigiSep) loadGroup).getLmGroupSep().getUtilityEnrollmentGroup());
        setDeviceClassSet(((LMGroupDigiSep) loadGroup).getLmGroupSep().getDeviceClassSet());
        setRampInMinutes(((LMGroupDigiSep) loadGroup).getLmGroupSep().getRampInMinutes());
        setRampOutMinutes(((LMGroupDigiSep) loadGroup).getLmGroupSep().getRampOutMinutes());

    }

    @Override
    public void buildDBPersistent(LMGroup group) {
        // Set parent fields
        super.buildDBPersistent(group);

        com.cannontech.database.db.device.lm.LMGroupSep digiSep = ((LMGroupDigiSep) group).getLmGroupSep();
        digiSep.setUtilityEnrollmentGroup(getUtilityEnrollmentGroup());
        digiSep.setDeviceClassSet(getDeviceClassSet());
        digiSep.setRampInMinutes(getRampInMinutes());
        digiSep.setRampOutMinutes(getRampOutMinutes());

        ((LMGroupDigiSep) group).setLmGroupSep(digiSep);

    }

}
