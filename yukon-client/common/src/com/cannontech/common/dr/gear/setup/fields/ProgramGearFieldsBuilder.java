package com.cannontech.common.dr.gear.setup.fields;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.gear.setup.AbsoluteOrDelta;
import com.cannontech.common.dr.gear.setup.BtpLedIndicator;
import com.cannontech.common.dr.gear.setup.ControlStartState;
import com.cannontech.common.dr.gear.setup.CycleCountSendType;
import com.cannontech.common.dr.gear.setup.GroupSelectionMethod;
import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.Mode;
import com.cannontech.common.dr.gear.setup.StopOrder;
import com.cannontech.common.dr.gear.setup.TemperatureMeasureUnit;
import com.cannontech.common.dr.gear.setup.WhenToChange;
import com.cannontech.common.dr.setup.LMModelFactory;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.LMGearDao;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.dr.itron.model.ItronCycleType;
import com.cannontech.dr.nest.model.v3.LoadShapingOptions;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.gear.model.BeatThePeakGearContainer;
import com.cannontech.loadcontrol.gear.model.EcobeeSetpointValues;
import com.cannontech.loadcontrol.gear.model.LMThermostatGear;

public class ProgramGearFieldsBuilder {

    @Autowired private LMGearDao gearDao;

    public ProgramGearFields getProgramGearFields(LMProgramDirectGear directGear) {

        ProgramGearFields gearFields = null;

        switch (directGear.getControlMethod()) {
            case TrueCycle:
            case MagnitudeCycle:
            case SmartCycle:
                gearFields = getSmartCycleGearFields(directGear);
                break;
            case TargetCycle:
                gearFields = getSmartCycleGearFields(directGear);
                break;
            case EcobeeCycle:
                gearFields = getEcobeeCycleGearFields(directGear);
                break;
            case EcobeeSetpoint:
                gearFields = getEcobeeSetpointGearFields(directGear);
                break;
            case HoneywellCycle:
                gearFields = getHoneywellCycleGearFields(directGear);
                break;
            case NestCriticalCycle:
                break;
            case ItronCycle:
                gearFields = getItronCycleGearFields(directGear);
                break;
            case NestStandardCycle:
                gearFields = getNestStandardCycleGearFields(directGear);
                break;
            case SepCycle:
                gearFields = getSepCycleGearFields(directGear);
                break;
            case MasterCycle:
                gearFields = getMasterCycleGearFields(directGear);
                break;
            case TimeRefresh:
                gearFields = getTimeRefreshGearFields(directGear);
                break;
            case Rotation:
                gearFields = getRotationGearFields(directGear);
                break;
            case Latching:
                gearFields = getLatchingGearFields(directGear);
                break;
            case ThermostatRamping:
                gearFields = getThermostatSetbackGearFields(directGear);
                break;
            case SimpleThermostatRamping:
                gearFields = getSimpleThermostatRampingGearFields(directGear);
                break;
            case SepTemperatureOffset:
                gearFields = getSepTemperatureOffsetGearFields(directGear);
                break;
            case BeatThePeak:
                gearFields = getBeatThePeakGearFields(directGear);
                break;
            case NoControl:
                gearFields = getNoControlGearFields(directGear);
                break;
            case MeterDisconnect:
                break;
            case HoneywellSetpoint:
                break;
        }

        return gearFields;

    }

    /**
     * Build Smart/True/Target/Magnitude Cycle gear fields.
     */
    private ProgramGearFields getSmartCycleGearFields(LMProgramDirectGear directGear) {
        GearControlMethod controlMethod = directGear.getControlMethod();
        ProgramGearFields fields = LMModelFactory.createProgramGearFields(controlMethod);

        SmartCycleGearFields gearFields = (SmartCycleGearFields) fields;
        gearFields.setCapacityReduction(directGear.getPercentReduction());
        gearFields.setControlPercent(directGear.getMethodRate());
        gearFields.setCycleCountSendType(CycleCountSendType.valueOf(directGear.getMethodOptionType()));
        gearFields.setCyclePeriodInMinutes(directGear.getMethodPeriod() / 60);
        gearFields.setHowToStopControl(HowToStopControl.valueOf(directGear.getMethodStopType()));

        Integer maxCycleCount = directGear.getMethodOptionMax();

        if (maxCycleCount.intValue() > 0) {
            gearFields.setMaxCycleCount(maxCycleCount);
        } else {
            gearFields.setMaxCycleCount(0);
        }

        String frontRampOption = directGear.getFrontRampOption();
        if (frontRampOption.compareTo(CtiUtilities.STRING_NONE) != 0) {
            gearFields.setNoRamp(true);
        } else {
            gearFields.setNoRamp(false);
        }

        gearFields.setSendRate(directGear.getCycleRefreshRate());
        gearFields.setStartingPeriodCount(directGear.getMethodRateCount());
        gearFields.setStopCommandRepeat(directGear.getStopCommandRepeat());

        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);

        if (fields instanceof TargetCycleGearFields && controlMethod == GearControlMethod.TargetCycle) {
            TargetCycleGearFields targetGearFields = (TargetCycleGearFields) fields;
            targetGearFields.setkWReduction(directGear.getKwReduction());
        }

        return fields;
    }

    /**
     * Build Time Refresh Cycle gear fields.
     */
    private ProgramGearFields getTimeRefreshGearFields(LMProgramDirectGear directGear) {
        TimeRefreshGearFields gearFields = new TimeRefreshGearFields();

        String methodStopType = directGear.getMethodStopType();

        if (methodStopType.compareTo(IlmDefines.STOP_RAMP_OUT_FIFO) == 0) {
            gearFields.setHowToStopControl(HowToStopControl.RampOutTimeIn);
            gearFields.setStopOrder(StopOrder.FIRSTINFIRSTOUT);
            gearFields.setRampOutPercent(directGear.getRampOutPercent());
            gearFields.setRampOutIntervalInSeconds(directGear.getRampOutInterval());
        } else if (methodStopType.compareTo(IlmDefines.STOP_RAMP_OUT_RANDOM) == 0) {
            gearFields.setHowToStopControl(HowToStopControl.RampOutTimeIn);
            gearFields.setStopOrder(StopOrder.RANDOM);
            gearFields.setRampOutPercent(directGear.getRampOutPercent());
            gearFields.setRampOutIntervalInSeconds(directGear.getRampOutInterval());
        } else if (methodStopType.compareTo(IlmDefines.STOP_RAMP_OUT_FIFO_RESTORE) == 0) {
            gearFields.setHowToStopControl(HowToStopControl.RampOutRestore);
            gearFields.setStopOrder(StopOrder.FIRSTINFIRSTOUT);
            gearFields.setRampOutPercent(directGear.getRampOutPercent());
            gearFields.setRampOutIntervalInSeconds(directGear.getRampOutInterval());
        } else if (methodStopType.compareTo(IlmDefines.STOP_RAMP_OUT_RANDOM_RESTORE) == 0) {
            gearFields.setHowToStopControl(HowToStopControl.RampOutRestore);
            gearFields.setStopOrder(StopOrder.RANDOM);
            gearFields.setRampOutPercent(directGear.getRampOutPercent());
            gearFields.setRampOutIntervalInSeconds(directGear.getRampOutInterval());
        } else {
            gearFields.setHowToStopControl(HowToStopControl.valueOf(methodStopType));
        }

        Integer rampInPercent = directGear.getRampInPercent();
        Integer rampInIntervalInSeconds = directGear.getRampInInterval();

        if (rampInPercent.intValue() != 0 && rampInIntervalInSeconds.intValue() != 0) {
            gearFields.setRampInIntervalInSeconds(rampInIntervalInSeconds);
            gearFields.setRampInPercent(rampInPercent);
        }

        gearFields.setCapacityReduction(directGear.getPercentReduction());
        gearFields.setStopCommandRepeat(directGear.getStopCommandRepeat());
        gearFields.setShedTime(directGear.getMethodPeriod());
        gearFields.setNumberOfGroups(directGear.getMethodRateCount());

        String methodOptionType = directGear.getMethodOptionType();

        if (methodOptionType.compareTo(IlmDefines.OPTION_COUNT_DOWN) == 0) {
            gearFields.setRefreshShedTime(CycleCountSendType.valueOf(IlmDefines.OPTION_DYNAMIC_SHED));
        } else {
            gearFields.setRefreshShedTime(CycleCountSendType.valueOf(IlmDefines.OPTION_FIXED_SHED));
        }

        gearFields.setSendRate(directGear.getMethodRate());
        gearFields.setGroupSelectionMethod(GroupSelectionMethod.valueOf(directGear.getGroupSelectionMethod()));

        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);

        return gearFields;

    }

    /**
     * Build Master Cycle gear fields.
     */
    private ProgramGearFields getMasterCycleGearFields(LMProgramDirectGear directGear) {
        MasterCycleGearFields gearFields = new MasterCycleGearFields();

        String methodStopType = directGear.getMethodStopType();
        if (methodStopType.compareTo(IlmDefines.STOP_RAMP_OUT_FIFO) == 0) {
            gearFields.setHowToStopControl(HowToStopControl.RampOutTimeIn);
            gearFields.setStopOrder(StopOrder.FIRSTINFIRSTOUT);
            gearFields.setRampOutPercent(directGear.getRampOutPercent());
            gearFields.setRampOutIntervalInSeconds(directGear.getRampOutInterval());
        } else if (methodStopType.compareTo(IlmDefines.STOP_RAMP_OUT_RANDOM) == 0) {
            gearFields.setHowToStopControl(HowToStopControl.RampOutTimeIn);
            gearFields.setStopOrder(StopOrder.RANDOM);
            gearFields.setRampOutPercent(directGear.getRampOutPercent());
            gearFields.setRampOutIntervalInSeconds(directGear.getRampOutInterval());
        } else if (methodStopType.compareTo(IlmDefines.STOP_RAMP_OUT_FIFO_RESTORE) == 0) {
            gearFields.setHowToStopControl(HowToStopControl.RampOutRestore);
            gearFields.setStopOrder(StopOrder.FIRSTINFIRSTOUT);
            gearFields.setRampOutPercent(directGear.getRampOutPercent());
            gearFields.setRampOutIntervalInSeconds(directGear.getRampOutInterval());
        } else if (methodStopType.compareTo(IlmDefines.STOP_RAMP_OUT_RANDOM_RESTORE) == 0) {
            gearFields.setHowToStopControl(HowToStopControl.RampOutRestore);
            gearFields.setStopOrder(StopOrder.RANDOM);
            gearFields.setRampOutPercent(directGear.getRampOutPercent());
            gearFields.setRampOutIntervalInSeconds(directGear.getRampOutInterval());
        } else {
            gearFields.setHowToStopControl(HowToStopControl.valueOf(methodStopType));
        }

        Integer rampInPercent = directGear.getRampInPercent();
        Integer rampInIntervalInSeconds = directGear.getRampInInterval();

        if (rampInPercent.intValue() != 0 && rampInIntervalInSeconds.intValue() != 0) {
            gearFields.setRampInIntervalInSeconds(rampInIntervalInSeconds);
            gearFields.setRampInPercent(rampInPercent);
        }

        gearFields.setCapacityReduction(directGear.getPercentReduction());
        gearFields.setControlPercent(directGear.getMethodRate());
        gearFields.setCyclePeriodInMinutes(directGear.getMethodPeriod() / 60);
        gearFields.setGroupSelectionMethod(GroupSelectionMethod.valueOf(directGear.getGroupSelectionMethod()));

        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);
        return gearFields;
    }

    /**
     * Build Rotation Cycle gear fields.
     */
    private ProgramGearFields getRotationGearFields(LMProgramDirectGear directGear) {
        RotationGearFields gearFields = new RotationGearFields();
        gearFields.setHowToStopControl(HowToStopControl.valueOf(directGear.getMethodStopType()));
        gearFields.setCapacityReduction(directGear.getPercentReduction());
        gearFields.setShedTime(directGear.getMethodPeriod());
        gearFields.setNumberOfGroups(directGear.getMethodRateCount());
        gearFields.setSendRate(directGear.getMethodRate());
        gearFields.setGroupSelectionMethod(GroupSelectionMethod.valueOf(directGear.getGroupSelectionMethod()));

        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);
        return gearFields;

    }

    /**
     * Build Latching Cycle gear fields.
     */
    private ProgramGearFields getLatchingGearFields(LMProgramDirectGear directGear) {
        LatchingGearFields gearFields = new LatchingGearFields();
        gearFields.setCapacityReduction(directGear.getPercentReduction());
        ControlStartState startControlState = directGear.getMethodRateCount() == 0 ? ControlStartState.Open : ControlStartState.Close;
        gearFields.setStartControlState(startControlState);
        return gearFields;
    }

    /**
     * Build Latching Cycle gear fields.
     */

    private ProgramGearFields getThermostatSetbackGearFields(LMProgramDirectGear directGear) {
        ThermostatSetbackGearFields gearFields = new ThermostatSetbackGearFields();
        gearFields.setHowToStopControl(HowToStopControl.valueOf(directGear.getMethodStopType()));
        gearFields.setCapacityReduction(directGear.getPercentReduction());
        LMThermostatGear thermostatGear = gearDao.getLMThermostatGear(directGear.getGearId());

        gearFields.setValueB(thermostatGear.getValueB());
        gearFields.setValueD(thermostatGear.getValueD());
        gearFields.setValueF(thermostatGear.getValueF());
        gearFields.setRandom(thermostatGear.getRandom());
        gearFields.setMaxValue(thermostatGear.getMaxValue());
        gearFields.setMinValue(thermostatGear.getMinValue());
        gearFields.setValueTa(thermostatGear.getValueTa());
        gearFields.setValueTb(thermostatGear.getValueTb());
        gearFields.setValueTc(thermostatGear.getValueTc());
        gearFields.setValueTd(thermostatGear.getValueTd());
        gearFields.setValueTe(thermostatGear.getValueTe());
        gearFields.setValueTf(thermostatGear.getValueTf());

        if (thermostatGear.getSettings().charAt(0) == 'A') {
            gearFields.setAbsoluteOrDelta(AbsoluteOrDelta.ABSOLUTE);
        } else {
            gearFields.setAbsoluteOrDelta(AbsoluteOrDelta.DELTA);
        }

        if (thermostatGear.getSettings().charAt(1) == 'C') {
            gearFields.setMeasureUnit(TemperatureMeasureUnit.CELSIUS);
        } else {
            gearFields.setMeasureUnit(TemperatureMeasureUnit.FAHRENHEIT);
        }

        if (thermostatGear.getSettings().charAt(3) == 'I') {
            gearFields.setIsCoolMode(true);
        } else {
            gearFields.setIsCoolMode(false);
        }

        if (thermostatGear.getSettings().charAt(2) == 'H') {
            gearFields.setIsHeatMode(true);
        } else {
            gearFields.setIsHeatMode(false);
        }

        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);
        return gearFields;

    }

    /**
     * Build Simple ThermostatRamping Cycle gear fields.
     */

    private ProgramGearFields getSimpleThermostatRampingGearFields(LMProgramDirectGear directGear) {
        SimpleThermostatRampingGearFields gearFields = new SimpleThermostatRampingGearFields();
        gearFields.setHowToStopControl(HowToStopControl.valueOf(directGear.getMethodStopType()));

        LMThermostatGear thermostatGear = gearDao.getLMThermostatGear(directGear.getGearId());

        boolean isHeatMode = thermostatGear.getSettings().charAt(2) == 'H';
        if (isHeatMode) {
            gearFields.setMode(Mode.HEAT);
        }

        boolean isCoolMode = thermostatGear.getSettings().charAt(3) == 'I';
        if (isCoolMode) {
            gearFields.setMode(Mode.COOL);
        }

        Integer random = thermostatGear.getRandom();
        gearFields.setRandomStartTimeInMinutes(random);

        Integer preCoolTemp = thermostatGear.getValueB();
        gearFields.setPreOpTemp(preCoolTemp);

        Integer preCoolTime = thermostatGear.getValueTb();
        gearFields.setPreOpTimeInMinutes(preCoolTime);

        Integer preCoolHold = thermostatGear.getValueTc();
        gearFields.setPreOpHoldInMinutes(preCoolHold);

        Float rampRate = thermostatGear.getRampRate();
        if (rampRate != null) {
            gearFields.setRampPerHour(rampRate);
        }

        Integer maxRampTemp = thermostatGear.getValueD();
        gearFields.setMax(maxRampTemp);

        Integer restoreTime = thermostatGear.getValueTf();
        gearFields.setRampOutTimeInMinutes(restoreTime);

        Integer maxRuntime = thermostatGear.getValueTa();
        gearFields.setMaxRuntimeInMinutes(maxRuntime);

        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);
        return gearFields;

    }

    /**
     * Build BeatThePeak Cycle gear fields.
     */

    private ProgramGearFields getBeatThePeakGearFields(LMProgramDirectGear directGear) {
        BeatThePeakGearFields gearFields = new BeatThePeakGearFields();
        gearFields.setTimeoutInMinutes(directGear.getMethodPeriod());
        gearFields.setResendInMinutes(directGear.getMethodRate() / 60);

        BeatThePeakGearContainer tgc = gearDao.getContainer(directGear.getGearId());

        String alertLevel = tgc.getAlertLevel();
        gearFields.setIndicator(BtpLedIndicator.valueOf(alertLevel));

        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);
        return gearFields;

    }

    /**
     * Build NoControl Cycle gear fields.
     */

    private ProgramGearFields getNoControlGearFields(LMProgramDirectGear directGear) {
        NoControlGearFields gearFields = new NoControlGearFields();
        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);
        return gearFields;

    }

    /**
     * Build Ecobee Cycle gear fields.
     */

    private ProgramGearFields getEcobeeCycleGearFields(LMProgramDirectGear directGear) {
        EcobeeCycleGearFields gearFields = new EcobeeCycleGearFields();

        gearFields.setHowToStopControl(HowToStopControl.valueOf(directGear.getMethodStopType()));
        gearFields.setCapacityReduction(directGear.getPercentReduction());
        gearFields.setControlPercent(directGear.getMethodRate());
        gearFields.setRampIn(IlmDefines.RAMP_RANDOM.equals(directGear.getFrontRampOption()));
        gearFields.setMandatory(IlmDefines.OPTION_MANDATORY.equalsIgnoreCase(directGear.getMethodOptionType()));
        gearFields.setRampOut(IlmDefines.RAMP_RANDOM.equals(directGear.getBackRampOption()));

        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);
        return gearFields;

    }

    /**
     * Build Ecobee Setpoint Cycle gear fields.
     */

    private ProgramGearFields getEcobeeSetpointGearFields(LMProgramDirectGear directGear) {
        EcobeeSetpointGearFields gearFields = new EcobeeSetpointGearFields();

        gearFields.setHowToStopControl(HowToStopControl.valueOf(directGear.getMethodStopType()));
        gearFields.setCapacityReduction(directGear.getPercentReduction());
        gearFields.setMandatory(IlmDefines.OPTION_MANDATORY.equalsIgnoreCase(directGear.getMethodOptionType()));
        EcobeeSetpointValues setPointValues = gearDao.getEcobeeSetpointValues(directGear.getGearId());

        gearFields.setSetpointOffset(setPointValues.getSetpointOffset());
        gearFields.setMode(setPointValues.getHeatCool().getMode());

        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);

        return gearFields;

    }

    /**
     * Build Honeywell Cycle gear fields.
     */
    private ProgramGearFields getHoneywellCycleGearFields(LMProgramDirectGear directGear) {
        HoneywellCycleGearFields gearFields = new HoneywellCycleGearFields();
        gearFields.setHowToStopControl(HowToStopControl.valueOf(directGear.getMethodStopType()));
        gearFields.setControlPercent(directGear.getMethodRate());
        gearFields.setCapacityReduction(directGear.getPercentReduction());
        gearFields.setCyclePeriodInMinutes(directGear.getMethodRateCount());
        gearFields.setRampInOut(IlmDefines.RAMP_RANDOM.equals(directGear.getFrontRampOption()));

        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);
        return gearFields;

    }

    /**
     * Build Itron Cycle gear fields.
     */

    private ProgramGearFields getItronCycleGearFields(LMProgramDirectGear directGear) {
        ItronCycleGearFields gearFields = new ItronCycleGearFields();

        gearFields.setHowToStopControl(HowToStopControl.valueOf(directGear.getMethodStopType()));
        gearFields.setCapacityReduction(directGear.getPercentReduction());
        gearFields.setDutyCyclePercent(directGear.getMethodRate());
        gearFields.setRampIn(IlmDefines.RAMP_RANDOM.equals(directGear.getFrontRampOption()));
        gearFields.setRampOut(IlmDefines.RAMP_RANDOM.equals(directGear.getBackRampOption()));
        String methodOptionType = directGear.getMethodOptionType();
        gearFields.setCriticality(Integer.parseInt(methodOptionType));
        gearFields.setDutyCyclePeriodInMinutes(directGear.getMethodPeriod() / 60);
        ItronCycleType cycleType = gearDao.getItronCycleType(directGear.getGearId());
        gearFields.setCycleType(cycleType);

        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);
        return gearFields;

    }

    /**
     * Build NestStandard Cycle gear fields.
     */

    private ProgramGearFields getNestStandardCycleGearFields(LMProgramDirectGear directGear) {
        NestStandardCycleGearFields gearFields = new NestStandardCycleGearFields();

        LoadShapingOptions loadShapingOptions = gearDao.getLoadShapingOptions(directGear.getGearId());
        gearFields.setPeak(loadShapingOptions.getPeakLoadShape());
        gearFields.setPost(loadShapingOptions.getPostLoadShape());
        gearFields.setPrep(loadShapingOptions.getPrepLoadShape());
        return gearFields;

    }

    /**
     * Build Sep Cycle gear fields.
     */

    private ProgramGearFields getSepCycleGearFields(LMProgramDirectGear directGear) {
        SepCycleGearFields gearFields = new SepCycleGearFields();

        gearFields.setHowToStopControl(HowToStopControl.valueOf(directGear.getMethodStopType()));
        gearFields.setCapacityReduction(directGear.getPercentReduction());
        gearFields.setControlPercent(directGear.getMethodRate());
        gearFields.setCriticality(directGear.getMethodPeriod());
        String frontRampOption = directGear.getFrontRampOption();
        boolean isFrontRampEnabled = frontRampOption.compareTo(IlmDefines.RAMP_RANDOM) == 0;
        gearFields.setRampIn(isFrontRampEnabled);
        String backRampOption = directGear.getBackRampOption();
        boolean isBackRampEnabled = backRampOption.compareTo(IlmDefines.RAMP_RANDOM) == 0;
        gearFields.setRampOut(isBackRampEnabled);

        String methodOptionType = directGear.getMethodOptionType();
        boolean isTrueCycleEnabled = methodOptionType.compareTo(IlmDefines.OPTION_TRUE_CYCLE) == 0;
        gearFields.setTrueCycle(isTrueCycleEnabled);

        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);
        return gearFields;

    }

    /**
     * Build Sep TemperatureOff Cycle gear fields.
     */
    
    private ProgramGearFields getSepTemperatureOffsetGearFields(LMProgramDirectGear directGear) {
        SepTemperatureOffsetGearFields gearFields = new SepTemperatureOffsetGearFields();
        String frontRampOption = directGear.getFrontRampOption();
        boolean isFrontRampEnabled = frontRampOption.compareTo(IlmDefines.RAMP_RANDOM) == 0;

        gearFields.setRampIn(isFrontRampEnabled);
        String backRampOption = directGear.getBackRampOption();
        boolean isBackRampEnabled = backRampOption.compareTo(IlmDefines.RAMP_RANDOM) == 0;
        gearFields.setRampOut(isBackRampEnabled);
        LMThermostatGear thermostatGear = gearDao.getLMThermostatGear(directGear.getGearId());
        Integer valueTa = thermostatGear.getValueTa();
        Integer valueTb = thermostatGear.getValueTb();

        double heatingOffset = valueTa / 10;
        double coolingOffset = valueTb / 10;

        if (heatingOffset != 0.0 && coolingOffset == 0.0) {
            gearFields.setOffset(heatingOffset);
            gearFields.setMode(Mode.HEAT);
        } else if (heatingOffset == 0.0 && coolingOffset != 0.0) {
            gearFields.setOffset(coolingOffset);
            gearFields.setMode(Mode.COOL);
        } else if (heatingOffset == 0.0 && coolingOffset == 0.0) {
            gearFields.setOffset(0.0);
            gearFields.setMode(Mode.HEAT);
        } else {
            throw new RuntimeException("Illegal database values: Heating and cooling offsets cannot both be nonzero");
        }

        if (thermostatGear.getSettings().charAt(1) == 'C') {
            gearFields.setCelsiusOrFahrenheit(TemperatureMeasureUnit.CELSIUS);
        } else {
            gearFields.setCelsiusOrFahrenheit(TemperatureMeasureUnit.FAHRENHEIT);
        }

        gearFields.setCriticality(directGear.getMethodPeriod());
        gearFields.setHowToStopControl(HowToStopControl.valueOf(directGear.getMethodStopType()));
        gearFields.setCapacityReduction(directGear.getPercentReduction());

        WhenToChangeFields changeFields = getWhenToChangeFields(directGear);
        gearFields.setWhenToChangeFields(changeFields);
        return gearFields;

    }
    
    /**
     * Build WhenToChangeFields gear fields.
     */

    private WhenToChangeFields getWhenToChangeFields(LMProgramDirectGear directGear) {

        WhenToChangeFields changeFields = new WhenToChangeFields();
        WhenToChange whenToChange = WhenToChange.valueOf(directGear.getChangeCondition());
        changeFields.setWhenToChange(whenToChange);

        if (whenToChange == WhenToChange.Priority) {
            changeFields.setChangePriority(directGear.getChangePriority());
        }
        if (whenToChange == WhenToChange.Duration) {
            changeFields.setChangeDurationInMinutes(directGear.getChangeDuration() / 60);
        }
        if (whenToChange == WhenToChange.TriggerOffset) {
            changeFields.setTriggerNumber(directGear.getChangeTriggerNumber());
            changeFields.setTriggerOffset(directGear.getChangeTriggerOffset());
        }

        return changeFields;
    }

}
