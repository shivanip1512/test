package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.AbsoluteOrDelta;
import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.TemperatureMeasureUnit;
import com.cannontech.database.data.device.lm.ThermostatSetbackGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ThermostatSetbackGearFields implements ProgramGearFields {

    private AbsoluteOrDelta absoluteOrDelta;
    private TemperatureMeasureUnit measureUnit;

    public Boolean isHeatMode;
    public Boolean isCoolMode;
    
    private Integer minValue;
    private Integer maxValue;
    private Integer valueB;
    private Integer valueD;
    private Integer valueF;
    private Integer random;

    private Integer valueTa;
    private Integer valueTb;
    private Integer valueTc;
    private Integer valueTd;     
    private Integer valueTe;
    private Integer valueTf;
    
    private HowToStopControl howToStopControl;
    private Integer capacityReduction;

    private WhenToChangeFields whenToChangeFields;

    public AbsoluteOrDelta getAbsoluteOrDelta() {
        return absoluteOrDelta;
    }

    public void setAbsoluteOrDelta(AbsoluteOrDelta absoluteOrDelta) {
        this.absoluteOrDelta = absoluteOrDelta;
    }

    public Boolean isHeatMode() {
        return isHeatMode;
    }

    public void setHeatMode(Boolean isHeatMode) {
        this.isHeatMode = isHeatMode;
    }

    public Boolean isCoolMode() {
        return isCoolMode;
    }

    public void setCoolMode(Boolean isCoolMode) {
        this.isCoolMode = isCoolMode;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getValueB() {
        return valueB;
    }

    public void setValueB(Integer valueB) {
        this.valueB = valueB;
    }

    public Integer getValueD() {
        return valueD;
    }

    public void setValueD(Integer valueD) {
        this.valueD = valueD;
    }

    public Integer getValueF() {
        return valueF;
    }

    public void setValueF(Integer valueF) {
        this.valueF = valueF;
    }

    public Integer getRandom() {
        return random;
    }

    public void setRandom(Integer random) {
        this.random = random;
    }

    public Integer getValueTa() {
        return valueTa;
    }

    public void setValueTa(Integer valueTa) {
        this.valueTa = valueTa;
    }

    public Integer getValueTb() {
        return valueTb;
    }

    public void setValueTb(Integer valueTb) {
        this.valueTb = valueTb;
    }

    public Integer getValueTc() {
        return valueTc;
    }

    public void setValueTc(Integer valueTc) {
        this.valueTc = valueTc;
    }

    public Integer getValueTd() {
        return valueTd;
    }

    public void setValueTd(Integer valueTd) {
        this.valueTd = valueTd;
    }

    public Integer getValueTe() {
        return valueTe;
    }

    public void setValueTe(Integer valueTe) {
        this.valueTe = valueTe;
    }

    public Integer getValueTf() {
        return valueTf;
    }

    public void setValueTf(Integer valueTf) {
        this.valueTf = valueTf;
    }

    public HowToStopControl getHowToStopControl() {
        return howToStopControl;
    }

    public void setHowToStopControl(HowToStopControl howToStopControl) {
        this.howToStopControl = howToStopControl;
    }

    public Integer getCapacityReduction() {
        return capacityReduction;
    }

    public void setCapacityReduction(Integer capacityReduction) {
        this.capacityReduction = capacityReduction;
    }

    public WhenToChangeFields getWhenToChangeFields() {
        return whenToChangeFields;
    }

    public void setWhenToChangeFields(WhenToChangeFields whenToChangeFields) {
        this.whenToChangeFields = whenToChangeFields;
    }

    public TemperatureMeasureUnit getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(TemperatureMeasureUnit measureUnit) {
        this.measureUnit = measureUnit;
    }

    @Override
    public void buildModel(LMProgramDirectGear programDirectGear) {
        ThermostatSetbackGear thermostatSetbackGear = (ThermostatSetbackGear) programDirectGear;

        setHowToStopControl(HowToStopControl.valueOf(thermostatSetbackGear.getMethodStopType()));
        setCapacityReduction(thermostatSetbackGear.getPercentReduction());

        if (thermostatSetbackGear.getValueB().intValue() != 0) {
            setValueTb(thermostatSetbackGear.getValueB());
        }

        if (thermostatSetbackGear.getValueD().intValue() != 0) {
            setValueD(thermostatSetbackGear.getValueD());
        }

        if (thermostatSetbackGear.getValueF().intValue() != 0) {
            setValueF(thermostatSetbackGear.getValueF());
        }

        if (thermostatSetbackGear.getRandom().intValue() != 0) {
            setRandom(thermostatSetbackGear.getRandom());
        }
        if (thermostatSetbackGear.getMaxValue().intValue() != 0) {
            setMaxValue(thermostatSetbackGear.getMaxValue());
        }

        if (thermostatSetbackGear.getMinValue().intValue() != 0) {
            setMinValue(thermostatSetbackGear.getMinValue());
        }

        if (thermostatSetbackGear.getValueTa().intValue() != 0) {
            setValueTa(thermostatSetbackGear.getValueTa());
        }

        if (thermostatSetbackGear.getValueTb().intValue() != 0) {
            setValueTb(thermostatSetbackGear.getValueTb());
        }

        if (thermostatSetbackGear.getValueTc().intValue() != 0) {
            setValueTc(thermostatSetbackGear.getValueTc());
        }

        if (thermostatSetbackGear.getValueTd().intValue() != 0) {
            setValueTd(thermostatSetbackGear.getValueTd());
        }

        if (thermostatSetbackGear.getValueTe().intValue() != 0) {
            setValueTe(thermostatSetbackGear.getValueTe());
        }

        if (thermostatSetbackGear.getValueTf().intValue() != 0) {
            setValueTf(thermostatSetbackGear.getValueTf());
        }

        if (thermostatSetbackGear.getSettings().charAt(0) == 'A') {
            setAbsoluteOrDelta(AbsoluteOrDelta.ABSOLUTE);
        } else {
            setAbsoluteOrDelta(AbsoluteOrDelta.DELTA);
        }

        if (thermostatSetbackGear.getSettings().charAt(1) == 'C') {
            setMeasureUnit(TemperatureMeasureUnit.CELSIUS);
        } else {
            setMeasureUnit(TemperatureMeasureUnit.FAHRENHEIT);
        }

        if (thermostatSetbackGear.getSettings().charAt(3) == 'I') {
            setCoolMode(true);
        }

        if (thermostatSetbackGear.getSettings().charAt(3) == 'H') {
            setHeatMode(true);
        }

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(programDirectGear);
        setWhenToChangeFields(whenToChangeFields);

    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        ThermostatSetbackGear thermostatSetbackGear = (ThermostatSetbackGear) programDirectGear;

        thermostatSetbackGear.setMethodStopType(getHowToStopControl().name());
        thermostatSetbackGear.setPercentReduction(getCapacityReduction());

        if (getValueB() != null) {
            thermostatSetbackGear.setValueB(getValueB());
        }
        if (getValueD() != null) {
            thermostatSetbackGear.setValueD(getValueD());
        }
        if (getValueF() != null) {
            thermostatSetbackGear.setValueF(getValueF());
        }
        if (getRandom() != null) {
            thermostatSetbackGear.setRandom(getRandom());
        }
        if (getMaxValue() != null) {
            thermostatSetbackGear.setMaxValue(getMaxValue());
        }
        if (getMinValue() != null) {
            thermostatSetbackGear.setMinValue(getMinValue());
        }
        if (getValueTa() != null) {
            thermostatSetbackGear.setValueTa(getValueTa());
        }
        if (getValueTb() != null) {
            thermostatSetbackGear.setValueTb(getValueTb());
        }
        if (getValueTc() != null) {
            thermostatSetbackGear.setValueTc(getValueTc());
        }
        if (getValueTd() != null) {
            thermostatSetbackGear.setValueTd(getValueTd());
        }
        if (getValueTe() != null) {
            thermostatSetbackGear.setValueTe(getValueTe());
        }
        if (getValueTf() != null) {
            thermostatSetbackGear.setValueTf(getValueTf());
        }

        if (getAbsoluteOrDelta() == AbsoluteOrDelta.ABSOLUTE) {
            thermostatSetbackGear.getSettings().setCharAt(0, 'A');
        } else {
            thermostatSetbackGear.getSettings().setCharAt(0, 'D');
        }

        if (getMeasureUnit() == TemperatureMeasureUnit.CELSIUS) {
            thermostatSetbackGear.getSettings().setCharAt(1, 'C');
        } else {
            thermostatSetbackGear.getSettings().setCharAt(1, 'F');
        }

        if (isHeatMode()) {
            thermostatSetbackGear.getSettings().setCharAt(2, 'H');
        } else {
            thermostatSetbackGear.getSettings().setCharAt(2, '-');
        }

        if (isCoolMode()) {
            // I for "icy goodness"
            thermostatSetbackGear.getSettings().setCharAt(3, 'I');
        } else {
            thermostatSetbackGear.getSettings().setCharAt(3, '-');
        }

        whenToChangeFields.buildDBPersistent(programDirectGear);

    }
}
