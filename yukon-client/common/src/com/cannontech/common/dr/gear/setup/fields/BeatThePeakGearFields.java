package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.BTPLEDIndicator;
import com.cannontech.database.data.device.lm.BeatThePeakGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.loadcontrol.gear.model.BeatThePeakGearContainer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BeatThePeakGearFields implements ProgramGearFields {

    private BTPLEDIndicator indicator;
    private Integer timeout;
    private Integer resend;

    WhenToChangeFields whenToChangeFields;

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getResend() {
        return resend;
    }

    public void setResend(Integer resend) {
        this.resend = resend;
    }

    public BTPLEDIndicator getIndicator() {
        return indicator;
    }

    public void setIndicator(BTPLEDIndicator indicator) {
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

        setTimeout(beatThePeakGear.getTimeout());
        setResend(beatThePeakGear.getResend() / 60);

        BeatThePeakGearContainer tgc = beatThePeakGear.getTierGearContainer();
        String alertLevel = tgc.getAlertLevel();
        setIndicator(BTPLEDIndicator.valueOf(alertLevel));

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(directGear);
        setWhenToChangeFields(whenToChangeFields);
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear directGear) {
        BeatThePeakGear beatThePeakGear = (BeatThePeakGear) directGear;

        beatThePeakGear.setTimeout(getTimeout());
        beatThePeakGear.setResend(getResend() * 60);

        BeatThePeakGearContainer btpContainer = new BeatThePeakGearContainer();
        btpContainer.setAlertLevel(getIndicator().name());
        beatThePeakGear.setTierGearContainer(btpContainer);
        whenToChangeFields.buildDBPersistent(beatThePeakGear);
    }

}
