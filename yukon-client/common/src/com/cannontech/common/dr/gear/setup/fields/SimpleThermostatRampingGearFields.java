package com.cannontech.common.dr.gear.setup.fields;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
    private Integer randomStartTimeInMinutes;
    private Integer preOpTemp;
    private Integer preOpTimeInMinutes;
    private Integer preOpHoldInMinutes;
    private Float rampPerHour;
    // Better variable name can be used
    private Integer max;

    private Integer rampOutTimeInMinutes;
    private Integer maxRuntimeInMinutes;

    private HowToStopControl howToStopControl;

    private WhenToChangeFields whenToChangeFields;

    public Integer getPreOpTemp() {
        return preOpTemp;
    }

    public void setPreOpTemp(Integer preOpTemp) {
        this.preOpTemp = preOpTemp;
    }

    public Float getRampPerHour() {
        return rampPerHour;
    }

    public void setRampPerHour(Float rampPerHour) {
        if (rampPerHour != null) {
            this.rampPerHour =
                new BigDecimal(rampPerHour.doubleValue()).setScale(1, RoundingMode.HALF_DOWN).floatValue();
        } else {
            this.rampPerHour = rampPerHour;
        }
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Integer getRandomStartTimeInMinutes() {
        return randomStartTimeInMinutes;
    }

    public void setRandomStartTimeInMinutes(Integer randomStartTimeInMinutes) {
        this.randomStartTimeInMinutes = randomStartTimeInMinutes;
    }

    public Integer getPreOpTimeInMinutes() {
        return preOpTimeInMinutes;
    }

    public void setPreOpTimeInMinutes(Integer preOpTimeInMinutes) {
        this.preOpTimeInMinutes = preOpTimeInMinutes;
    }

    public Integer getPreOpHoldInMinutes() {
        return preOpHoldInMinutes;
    }

    public void setPreOpHoldInMinutes(Integer preOpHoldInMinutes) {
        this.preOpHoldInMinutes = preOpHoldInMinutes;
    }

    public Integer getRampOutTimeInMinutes() {
        return rampOutTimeInMinutes;
    }

    public void setRampOutTimeInMinutes(Integer rampOutTimeInMinutes) {
        this.rampOutTimeInMinutes = rampOutTimeInMinutes;
    }

    public Integer getMaxRuntimeInMinutes() {
        return maxRuntimeInMinutes;
    }

    public void setMaxRuntimeInMinutes(Integer maxRuntimeInMinutes) {
        this.maxRuntimeInMinutes = maxRuntimeInMinutes;
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
            setMode(Mode.HEAT);
        }

        boolean isCoolMode = rampingGear.getSettings().charAt(3) == 'I';
        if (isCoolMode) {
            setMode(Mode.COOL);
        }

        Integer random = rampingGear.getRandom();
        if (random != null && random.intValue() != 0) {
            setRandomStartTimeInMinutes(random / 60);
        }

        Integer preCoolTemp = rampingGear.getValueB();
        if (preCoolTemp != null && preCoolTemp.intValue() != 0) {
            setPreOpTemp(preCoolTemp);
        }

        Integer preCoolTime = rampingGear.getValueTb();
        if (preCoolTime != null && preCoolTime.intValue() != 0) {
            setPreOpTimeInMinutes(preCoolTime / 60);
        }

        Integer preCoolHold = rampingGear.getValueTc();
        if (preCoolHold != null && preCoolHold.intValue() != 0) {
            setPreOpHoldInMinutes(preCoolHold / 60);
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
            setRampOutTimeInMinutes(restoreTime / 60);
        }

        Integer maxRuntime = rampingGear.getValueTa();
        if (maxRuntime != null && maxRuntime.intValue() != 0) {
            setMaxRuntimeInMinutes(maxRuntime / 60);
        }

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(gear);
        setWhenToChangeFields(whenToChangeFields);

    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {

        LMThermostatGear lmThermostatGear = (LMThermostatGear) programDirectGear;

        lmThermostatGear.setMethodStopType(getHowToStopControl().name());

        char heatChar = (getMode() == Mode.HEAT) ? 'H' : '-';
        lmThermostatGear.getSettings().setCharAt(2, heatChar);

        char coolChar = (getMode() == Mode.COOL) ? coolChar = 'I' : '-';
        lmThermostatGear.getSettings().setCharAt(3, coolChar);

        lmThermostatGear.setRandom(getRandomStartTimeInMinutes() * 60);
        lmThermostatGear.setValueB(getPreOpTemp());
        lmThermostatGear.setValueTb(getPreOpTimeInMinutes() * 60);
        lmThermostatGear.setValueTc(getPreOpHoldInMinutes() * 60);
        lmThermostatGear.setRampRate(getRampPerHour());
        lmThermostatGear.setValueD(getMax());
        lmThermostatGear.setValueTf(getRampOutTimeInMinutes() * 60);
        lmThermostatGear.setValueTa(getMaxRuntimeInMinutes() * 60);
        lmThermostatGear.setMethodRate(0);

        whenToChangeFields.buildDBPersistent(programDirectGear);

    }

}
