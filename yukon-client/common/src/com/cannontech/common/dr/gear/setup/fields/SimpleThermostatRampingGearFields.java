package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.Mode;
import com.cannontech.database.data.device.lm.SimpleThermostatRampingGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.device.lm.LMThermostatGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SimpleThermostatRampingGearFields implements ProgramGearFields {

    public Mode mode;
    private Integer randomStartTime;
    private Integer preOpTemp;
    private Integer preOpTime;
    private Integer preOpHold;
    private Float rampPerHour;
    // Better variable name can be used
    private Integer max;

    private Integer rampOutTime;
    private Integer maxRuntime;

    private HowToStopControl howToStopControl;

    private WhenToChangeFields whenToChangeFields;

    public Integer getRandomStartTime() {
        return randomStartTime;
    }

    public void setRandomStartTime(Integer randomStartTime) {
        this.randomStartTime = randomStartTime;
    }

    public Integer getPreOpTemp() {
        return preOpTemp;
    }

    public void setPreOpTemp(Integer preOpTemp) {
        this.preOpTemp = preOpTemp;
    }

    public Integer getPreOpTime() {
        return preOpTime;
    }

    public void setPreOpTime(Integer preOpTime) {
        this.preOpTime = preOpTime;
    }

    public Integer getPreOpHold() {
        return preOpHold;
    }

    public void setPreOpHold(Integer preOpHold) {
        this.preOpHold = preOpHold;
    }

    public Float getRampPerHour() {
        return rampPerHour;
    }

    public void setRampPerHour(Float rampPerHour) {
        this.rampPerHour = rampPerHour;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMaxRuntime() {
        return maxRuntime;
    }

    public void setMaxRuntime(Integer maxRuntime) {
        this.maxRuntime = maxRuntime;
    }

    public Integer getRampOutTime() {
        return rampOutTime;
    }

    public void setRampOutTime(Integer rampOutTime) {
        this.rampOutTime = rampOutTime;
    }


    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public HowToStopControl getHowToStopControl() {
        return howToStopControl;
    }

    public void setHowToStopControl(HowToStopControl howToStopControl) {
        this.howToStopControl = howToStopControl;
    }

    public WhenToChangeFields getWhenToChangeFields() {
        return whenToChangeFields;
    }

    public void setWhenToChangeFields(WhenToChangeFields whenToChangeFields) {
        this.whenToChangeFields = whenToChangeFields;
    }

    @Override
    public void buildModel(LMProgramDirectGear gear) {

        SimpleThermostatRampingGear rampingGear = (SimpleThermostatRampingGear) gear;

        setHowToStopControl(HowToStopControl.valueOf(rampingGear.getMethodStopType()));

        boolean isHeatMode = rampingGear.getSettings().charAt(2) == 'H';
        if (isHeatMode) {
            setMode(Mode.Heat);
        }

        boolean isCoolMode = rampingGear.getSettings().charAt(3) == 'I';
        if (isCoolMode) {
            setMode(Mode.Cool);
        }

        Integer random = rampingGear.getRandom();
        if (random != null && random.intValue() != 0) {
            setRandomStartTime(random / 60);
        }

        Integer preCoolTemp = rampingGear.getValueB();
        if (preCoolTemp != null && preCoolTemp.intValue() != 0) {
            setPreOpTemp(preCoolTemp);
        }

        Integer preCoolTime = rampingGear.getValueTb();
        if (preCoolTime != null && preCoolTime.intValue() != 0) {
            setPreOpTime(preCoolTime / 60);
        }

        Integer preCoolHold = rampingGear.getValueTc();
        if (preCoolHold != null && preCoolHold.intValue() != 0) {
            setPreOpHold(preCoolHold / 60);
        }

        Float rampRate = rampingGear.getRampRate();
        if (rampRate != null && rampRate.intValue() != 0) {
            setRampPerHour(rampRate);
        }

        Integer maxRampTemp = rampingGear.getValueD();
        if (maxRampTemp != null && maxRampTemp.intValue() != 0) {
            setMax(maxRampTemp);
        }

        Integer restoreTime = rampingGear.getValueTf();
        if (restoreTime != null && restoreTime.intValue() != 0) {
            setRampOutTime(restoreTime / 60);
        }

        Integer maxRuntime = rampingGear.getValueTa();
        if (maxRuntime != null && maxRuntime.intValue() != 0) {
            setMaxRuntime(maxRuntime / 60);
        }

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(gear);
        setWhenToChangeFields(whenToChangeFields);
        
        
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {

        LMThermostatGear lmThermostatGear = (LMThermostatGear) programDirectGear;

        lmThermostatGear.setMethodStopType(getHowToStopControl().name());

        char heatChar = (getMode() == Mode.Heat) ? 'H' : '-';
        lmThermostatGear.getSettings().setCharAt(2, heatChar);

        char coolChar = (getMode() == Mode.Cool) ? coolChar = 'I' : '-';
        lmThermostatGear.getSettings().setCharAt(3, coolChar);

        lmThermostatGear.setRandom(getRandomStartTime() * 60);
        lmThermostatGear.setValueB(getPreOpTemp());
        lmThermostatGear.setValueTb(getPreOpTime() * 60);
        lmThermostatGear.setValueTc(getPreOpHold() * 60);
        lmThermostatGear.setRampRate(getRampPerHour());
        lmThermostatGear.setValueD(getMax());
        lmThermostatGear.setValueTf(getRampOutTime() * 60);
        lmThermostatGear.setValueTa(getMaxRuntime() * 60);
        lmThermostatGear.setMethodRate(0); 

        whenToChangeFields.buildDBPersistent(programDirectGear);
        
    }

}
