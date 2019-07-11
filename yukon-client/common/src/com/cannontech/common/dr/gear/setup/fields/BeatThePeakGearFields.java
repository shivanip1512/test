package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.BtpLedIndicator;
import com.cannontech.database.data.device.lm.BeatThePeakGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.loadcontrol.gear.model.BeatThePeakGearContainer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BeatThePeakGearFields implements ProgramGearFields {

    private BtpLedIndicator indicator;
    private Integer timeoutInMinutes;
    private Integer resendInMinutes;

    WhenToChangeFields whenToChangeFields;

    public Integer getTimeoutInMinutes() {
        return timeoutInMinutes;
    }

    public void setTimeoutInMinutes(Integer timeoutInMinutes) {
        this.timeoutInMinutes = timeoutInMinutes;
    }

    public Integer getResendInMinutes() {
        return resendInMinutes;
    }

    public void setResendInMinutes(Integer resendInMinutes) {
        this.resendInMinutes = resendInMinutes;
    }

    public BtpLedIndicator getIndicator() {
        return indicator;
    }

    public void setIndicator(BtpLedIndicator indicator) {
        this.indicator = indicator;
    }

    public WhenToChangeFields getWhenToChangeFields() {
        return whenToChangeFields;
    }

    public void setWhenToChangeFields(WhenToChangeFields whenToChangeFields) {
        this.whenToChangeFields = whenToChangeFields;
    }

    @Override
    public void buildModel(LMProgramDirectGear directGear) {
        BeatThePeakGear beatThePeakGear = (BeatThePeakGear) directGear;

        setTimeoutInMinutes(beatThePeakGear.getTimeout());
        setResendInMinutes(beatThePeakGear.getResend() / 60);

        BeatThePeakGearContainer tgc = beatThePeakGear.getTierGearContainer();
        String alertLevel = tgc.getAlertLevel();
        setIndicator(BtpLedIndicator.valueOf(alertLevel));

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(directGear);
        setWhenToChangeFields(whenToChangeFields);
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear directGear) {
        BeatThePeakGear beatThePeakGear = (BeatThePeakGear) directGear;

        beatThePeakGear.setTimeout(getTimeoutInMinutes());
        beatThePeakGear.setResend(getResendInMinutes() * 60);

        BeatThePeakGearContainer btpContainer = new BeatThePeakGearContainer();
        btpContainer.setAlertLevel(getIndicator().name());
        beatThePeakGear.setTierGearContainer(btpContainer);
        whenToChangeFields.buildDBPersistent(beatThePeakGear);
    }

}
