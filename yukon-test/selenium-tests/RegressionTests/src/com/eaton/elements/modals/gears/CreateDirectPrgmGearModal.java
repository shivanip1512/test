package com.eaton.elements.modals.gears;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class CreateDirectPrgmGearModal extends CreateGearsModal {

    public CreateDirectPrgmGearModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
    }

    // CONTROL PARAMETERS
    // RefreshShedType
    public DropDownElement getRefreshShedType() {
        return new DropDownElement(this.driverExt, "fields.refreshShedTime", getModal());
    }

    // ShedTime
    public DropDownElement getShedTime() {
        return new DropDownElement(this.driverExt, "fields.shedTime", getModal());
    }

    // CommandResendRate
    public DropDownElement getCommandResendRate() {
        return new DropDownElement(this.driverExt, "fields.sendRate", getModal());
    }

    // GroupsPerSend
    public DropDownElement getGroupsPerSend() {
        return new DropDownElement(this.driverExt, "fields.numberOfGroups", getModal());
    }

    // GroupSelectionMethod
    public DropDownElement getGroupSelectionMethod() {
        return new DropDownElement(this.driverExt, "fields.groupSelectionMethod", getModal());
    }

    // TODO Control Percent element does not have a unique way to select it
    // TODO Cycle Period element does not have a unique way to select it

    // CycleCountSendType
    public DropDownElement getCycleCountSendType() {
        return new DropDownElement(this.driverExt, "fields.cycleCountSendType", getModal());
    }

    // MaxCycleCount
    public DropDownElement getMaxCycleCount() {
        return new DropDownElement(this.driverExt, "fields.maxCycleCount", getModal());
    }

    // StrtingPeriodCount
    public DropDownElement getStartingPeriodCount() {
        return new DropDownElement(this.driverExt, "fields.startingPeriodCount", getModal());
    }

    // ControlStartState
    public DropDownElement getControlStartState() {
        return new DropDownElement(this.driverExt, "fields.startControlState", getModal());
    }

    // Mode
    public RadioButtonElement getMode() {
        return new RadioButtonElement(this.driverExt, "fields.isHeatMode", getModal());
    }

    // Unit
    public RadioButtonElement getUnit() {
        return new RadioButtonElement(this.driverExt, "fields.measureUnit", getModal());
    }

    // Setpoint
    public RadioButtonElement getSetpoint() {
        return new RadioButtonElement(this.driverExt, "fields.absoluteOrDelta", getModal());
    }

    // MinimumTemperature
    public TextEditElement getMinimumTemperature() {
        return new TextEditElement(this.driverExt, "fields.minValue", getModal());
    }

    // MaximumTemperature
    public TextEditElement getMaximumTemperature() {
        return new TextEditElement(this.driverExt, "fields.maxValue", getModal());
    }

    // RandomOffsetTime
    public TextEditElement getRandomOffsetTime() {
        return new TextEditElement(this.driverExt, "fields.random", getModal());
    }

    // Ta
    public TextEditElement getTa() {
        return new TextEditElement(this.driverExt, "fields.valueTa", getModal());
    }

    // Tb
    public TextEditElement getTb() {
        return new TextEditElement(this.driverExt, "fields.valueTb", getModal());
    }

    // DeltaB
    public TextEditElement getDeltaB() {
        return new TextEditElement(this.driverExt, "fields.valueB", getModal());
    }

    // Tc
    public TextEditElement getTc() {
        return new TextEditElement(this.driverExt, "fields.valueTc", getModal());
    }

    // Td
    public TextEditElement getTd() {
        return new TextEditElement(this.driverExt, "fields.valueTd", getModal());
    }

    // DeltaD
    public TextEditElement getTDeltaD() {
        return new TextEditElement(this.driverExt, "fields.valueD", getModal());
    }

    // Te
    public TextEditElement getTe() {
        return new TextEditElement(this.driverExt, "fields.valueTe", getModal());
    }

    // Tf
    public TextEditElement getTf() {
        return new TextEditElement(this.driverExt, "fields.valueTf", getModal());
    }

    // DeltaF
    public TextEditElement getDeltaF() {
        return new TextEditElement(this.driverExt, "fields.valueF", getModal());
    }

    // RandomStartTime
    // TODO Random Start Time element does not have a unique way to select it

    // PRE-OP (COOL OR HEAT)
    // Temp
    // TODO Temp element does not have a unique way to select it

    // Time
    // TODO Time element does not have a unique way to select it

    // Hold
    // TODO Hold element does not have a unique way to select it

    // MaxRuntime
    // TODO MaxRuntime element does not have a unique way to select it

    // Btp Led Indicator
    public DropDownElement getBtpLedIndicator() {
        return new DropDownElement(this.driverExt, "fields.indicator", getModal());
    }

    // Max Indicator Timeout
    // TODO Max Indicator Timeout element does not have a unique way to select it

    // Resend Rate
    // TODO Resend Rate element does not have a unique way to select it

    // OPTIONAL ATTRIBUTES
    // TODO Group Capacity Reduction element does not have a unique way to select it

    // WhenToChange
    public DropDownElement getWhenToChange() {
        return new DropDownElement(this.driverExt, "fields.whenToChangeFields.whenToChange", getModal());
    }

    // TODO KW Reduction element does not have a unique way to select it

    // RAMP IN
    // RampIn
    public TrueFalseCheckboxElement getRampIn() {
        return new TrueFalseCheckboxElement(this.driverExt, "rampIn", getModal());
    }

    // NoRamp
    public TrueFalseCheckboxElement getNoRamp() {
        return new TrueFalseCheckboxElement(this.driverExt, "fields.noRamp", getModal());
    }

    // Ramp F/Hour
    // TODO Ramp F/Hour element does not have a unique way to select it

    // Max
    // TODO Max element does not have a unique way to select it

    // STOP CONTROL
    // HowToStopControl
    public DropDownElement getHowToStopControl() {
        return new DropDownElement(this.driverExt, "fields.howToStopControl", getModal());
    }

    // StopCommandRepeat
    // TODO Stop Command Repeat element does not have a unique way to select it

    // RampOutTime
    // TODO Ramp Out Time element does not have a unique way to select it
}
