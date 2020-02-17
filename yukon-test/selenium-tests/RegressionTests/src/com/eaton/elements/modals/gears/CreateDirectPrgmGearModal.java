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
        return new DropDownElement(this.driver, "fields.refreshShedTime", getModal());
    }

    // ShedTime
    public DropDownElement getShedTime() {
        return new DropDownElement(this.driver, "fields.shedTime", getModal());
    }

    // CommandResendRate
    public DropDownElement getCommandResendRate() {
        return new DropDownElement(this.driver, "fields.sendRate", getModal());
    }

    // GroupsPerSend
    public DropDownElement getGroupsPerSend() {
        return new DropDownElement(this.driver, "fields.numberOfGroups", getModal());
    }

    // GroupSelectionMethod
    public DropDownElement getGroupSelectionMethod() {
        return new DropDownElement(this.driver, "fields.groupSelectionMethod", getModal());
    }

    // TODO Control Percent element does not have a unique way to select it
    // TODO Cycle Period element does not have a unique way to select it

    // CycleCountSendType
    public DropDownElement getCycleCountSendType() {
        return new DropDownElement(this.driver, "fields.cycleCountSendType", getModal());
    }

    // MaxCycleCount
    public DropDownElement getMaxCycleCount() {
        return new DropDownElement(this.driver, "fields.maxCycleCount", getModal());
    }

    // StrtingPeriodCount
    public DropDownElement getStartingPeriodCount() {
        return new DropDownElement(this.driver, "fields.startingPeriodCount", getModal());
    }

    // ControlStartState
    public DropDownElement getControlStartState() {
        return new DropDownElement(this.driver, "fields.startControlState", getModal());
    }

    // Mode
    public RadioButtonElement getMode() {
        return new RadioButtonElement(this.driver, "fields.isHeatMode", getModal());
    }

    // Unit
    public RadioButtonElement getUnit() {
        return new RadioButtonElement(this.driver, "fields.measureUnit", getModal());
    }

    // Setpoint
    public RadioButtonElement getSetpoint() {
        return new RadioButtonElement(this.driver, "fields.absoluteOrDelta", getModal());
    }

    // MinimumTemperature
    public TextEditElement getMinimumTemperature() {
        return new TextEditElement(this.driver, "fields.minValue", getModal());
    }

    // MaximumTemperature
    public TextEditElement getMaximumTemperature() {
        return new TextEditElement(this.driver, "fields.maxValue", getModal());
    }

    // RandomOffsetTime
    public TextEditElement getRandomOffsetTime() {
        return new TextEditElement(this.driver, "fields.random", getModal());
    }

    // Ta
    public TextEditElement getTa() {
        return new TextEditElement(this.driver, "fields.valueTa", getModal());
    }

    // Tb
    public TextEditElement getTb() {
        return new TextEditElement(this.driver, "fields.valueTb", getModal());
    }

    // DeltaB
    public TextEditElement getDeltaB() {
        return new TextEditElement(this.driver, "fields.valueB", getModal());
    }

    // Tc
    public TextEditElement getTc() {
        return new TextEditElement(this.driver, "fields.valueTc", getModal());
    }

    // Td
    public TextEditElement getTd() {
        return new TextEditElement(this.driver, "fields.valueTd", getModal());
    }

    // DeltaD
    public TextEditElement getTDeltaD() {
        return new TextEditElement(this.driver, "fields.valueD", getModal());
    }

    // Te
    public TextEditElement getTe() {
        return new TextEditElement(this.driver, "fields.valueTe", getModal());
    }

    // Tf
    public TextEditElement getTf() {
        return new TextEditElement(this.driver, "fields.valueTf", getModal());
    }

    // DeltaF
    public TextEditElement getDeltaF() {
        return new TextEditElement(this.driver, "fields.valueF", getModal());
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
        return new DropDownElement(this.driver, "fields.indicator", getModal());
    }

    // Max Indicator Timeout
    // TODO Max Indicator Timeout element does not have a unique way to select it

    // Resend Rate
    // TODO Resend Rate element does not have a unique way to select it

    // OPTIONAL ATTRIBUTES
    // TODO Group Capacity Reduction element does not have a unique way to select it

    // WhenToChange
    public DropDownElement getWhenToChange() {
        return new DropDownElement(this.driver, "fields.whenToChangeFields.whenToChange", getModal());
    }

    // TODO KW Reduction element does not have a unique way to select it

    // RAMP IN
    // RampIn
    public TrueFalseCheckboxElement getRampIn() {
        return new TrueFalseCheckboxElement(this.driver, "rampIn", getModal());
    }

    // NoRamp
    public TrueFalseCheckboxElement getNoRamp() {
        return new TrueFalseCheckboxElement(this.driver, "fields.noRamp", getModal());
    }

    // Ramp F/Hour
    // TODO Ramp F/Hour element does not have a unique way to select it

    // Max
    // TODO Max element does not have a unique way to select it

    // STOP CONTROL
    // HowToStopControl
    public DropDownElement getHowToStopControl() {
        return new DropDownElement(this.driver, "fields.howToStopControl", getModal());
    }

    // StopCommandRepeat
    // TODO Stop Command Repeat element does not have a unique way to select it

    // RampOutTime
    // TODO Ramp Out Time element does not have a unique way to select it
}
