package com.eaton.elements.modals.gears;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;

public class CreateDirectPrgmGearModal extends CreateGearsModal {

    public CreateDirectPrgmGearModal(WebDriver driver, String modalName) {
        super(driver, modalName);
    }

    // CONTROL PARAMETERS
    // RefreshShedType
    public DropDownElement getRefreshShedType() {
        return new DropDownElement(this.driver, "fields.refreshShedTime", null);
    }

    // ShedTime
    public DropDownElement getShedTime() {
        return new DropDownElement(this.driver, "fields.shedTime", null);
    }

    // CommandResendRate
    public DropDownElement getCommandResendRate() {
        return new DropDownElement(this.driver, "fields.sendRate", null);
    }

    // GroupsPerSend
    public DropDownElement getGroupsPerSend() {
        return new DropDownElement(this.driver, "fields.numberOfGroups", null);
    }

    // GroupSelectionMethod
    public DropDownElement getGroupSelectionMethod() {
        return new DropDownElement(this.driver, "fields.groupSelectionMethod", null);
    }

    // TODO Control Percent element does not have a unique way to select it
    // TODO Cycle Period element does not have a unique way to select it

    // CycleCountSendType
    public DropDownElement getCycleCountSendType() {
        return new DropDownElement(this.driver, "fields.cycleCountSendType", null);
    }

    // MaxCycleCount
    public DropDownElement getMaxCycleCount() {
        return new DropDownElement(this.driver, "fields.maxCycleCount", null);
    }

    // StrtingPeriodCount
    public DropDownElement getStartingPeriodCount() {
        return new DropDownElement(this.driver, "fields.startingPeriodCount", null);
    }

    // ControlStartState
    public DropDownElement getControlStartState() {
        return new DropDownElement(this.driver, "fields.startControlState", null);
    }

    // Mode
    public RadioButtonElement getMode() {
        return new RadioButtonElement(this.driver, "fields.isHeatMode", null);
    }

    // Unit
    public RadioButtonElement getUnit() {
        return new RadioButtonElement(this.driver, "fields.measureUnit", null);
    }

    // Setpoint
    public RadioButtonElement getSetpoint() {
        return new RadioButtonElement(this.driver, "fields.absoluteOrDelta", null);
    }

    // MinimumTemperature
    public TextEditElement getMinimumTemperature() {
        return new TextEditElement(this.driver, "fields.minValue", null);
    }

    // MaximumTemperature
    public TextEditElement getMaximumTemperature() {
        return new TextEditElement(this.driver, "fields.maxValue", null);
    }

    // RandomOffsetTime
    public TextEditElement getRandomOffsetTime() {
        return new TextEditElement(this.driver, "fields.random", null);
    }

    // Ta
    public TextEditElement getTa() {
        return new TextEditElement(this.driver, "fields.valueTa", null);
    }

    // Tb
    public TextEditElement getTb() {
        return new TextEditElement(this.driver, "fields.valueTb", null);
    }

    // DeltaB
    public TextEditElement getDeltaB() {
        return new TextEditElement(this.driver, "fields.valueB", null);
    }

    // Tc
    public TextEditElement getTc() {
        return new TextEditElement(this.driver, "fields.valueTc", null);
    }

    // Td
    public TextEditElement getTd() {
        return new TextEditElement(this.driver, "fields.valueTd", null);
    }

    // DeltaD
    public TextEditElement getTDeltaD() {
        return new TextEditElement(this.driver, "fields.valueD", null);
    }

    // Te
    public TextEditElement getTe() {
        return new TextEditElement(this.driver, "fields.valueTe", null);
    }

    // Tf
    public TextEditElement getTf() {
        return new TextEditElement(this.driver, "fields.valueTf", null);
    }

    // DeltaF
    public TextEditElement getDeltaF() {
        return new TextEditElement(this.driver, "fields.valueF", null);
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
        return new DropDownElement(this.driver, "fields.indicator", null);
    }

    // Max Indicator Timeout
    // TODO Max Indicator Timeout element does not have a unique way to select it

    // Resend Rate
    // TODO Resend Rate element does not have a unique way to select it

    // OPTIONAL ATTRIBUTES
    // TODO Group Capacity Reduction element does not have a unique way to select it

    // WhenToChange
    public DropDownElement getWhenToChange() {
        return new DropDownElement(this.driver, "fields.whenToChangeFields.whenToChange", null);
    }

    // TODO KW Reduction element does not have a unique way to select it

    // RAMP IN
    // RampIn
    public TrueFalseCheckboxElement getRampIn() {
        return new TrueFalseCheckboxElement(this.driver, "rampIn", null);
    }

    // NoRamp
    public TrueFalseCheckboxElement getNoRamp() {
        return new TrueFalseCheckboxElement(this.driver, "fields.noRamp", null);
    }

    // Ramp F/Hour
    // TODO Ramp F/Hour element does not have a unique way to select it

    // Max
    // TODO Max element does not have a unique way to select it

    // STOP CONTROL
    // HowToStopControl
    public DropDownElement getHowToStopControl() {
        return new DropDownElement(this.driver, "fields.howToStopControl", null);
    }

    // StopCommandRepeat
    // TODO Stop Command Repeat element does not have a unique way to select it

    // RampOutTime
    // TODO Ramp Out Time element does not have a unique way to select it
}
