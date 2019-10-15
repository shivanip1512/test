package com.cannontech.rest.api.dr.helper;

import com.cannontech.rest.api.gear.fields.MockAbsoluteOrDelta;
import com.cannontech.rest.api.gear.fields.MockBeatThePeakGearFields;
import com.cannontech.rest.api.gear.fields.MockBtpLedIndicator;
import com.cannontech.rest.api.gear.fields.MockControlStartState;
import com.cannontech.rest.api.gear.fields.MockCycleCountSendType;
import com.cannontech.rest.api.gear.fields.MockEcobeeCycleGearFields;
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.gear.fields.MockGroupSelectionMethod;
import com.cannontech.rest.api.gear.fields.MockHoneywellCycleGearFields;
import com.cannontech.rest.api.gear.fields.MockHowToStopControl;
import com.cannontech.rest.api.gear.fields.MockItronCycleGearFields;
import com.cannontech.rest.api.gear.fields.MockItronCycleType;
import com.cannontech.rest.api.gear.fields.MockLatchingGearFields;
import com.cannontech.rest.api.gear.fields.MockMasterCycleGearFields;
import com.cannontech.rest.api.gear.fields.MockMode;
import com.cannontech.rest.api.gear.fields.MockNoControlGearFields;
import com.cannontech.rest.api.gear.fields.MockProgramGearFields;
import com.cannontech.rest.api.gear.fields.MockRotationGearFields;
import com.cannontech.rest.api.gear.fields.MockSepCycleGearFields;
import com.cannontech.rest.api.gear.fields.MockSepTemperatureOffsetGearFields;
import com.cannontech.rest.api.gear.fields.MockSimpleThermostatRampingGearFields;
import com.cannontech.rest.api.gear.fields.MockSmartCycleGearFields;
import com.cannontech.rest.api.gear.fields.MockTargetCycleGearFields;
import com.cannontech.rest.api.gear.fields.MockTemperatureMeasureUnit;
import com.cannontech.rest.api.gear.fields.MockThermostatSetbackGearFields;
import com.cannontech.rest.api.gear.fields.MockTimeRefreshGearFields;
import com.cannontech.rest.api.gear.fields.MockWhenToChange;
import com.cannontech.rest.api.gear.fields.MockWhenToChangeFields;

public class GearFieldHelper {
    
    
    public final static MockProgramGearFields createProgramGearFields(MockGearControlMethod controlMethod) {

        MockProgramGearFields gearFields = null;

        switch (controlMethod) {
        case TrueCycle:
        case MagnitudeCycle:
        case SmartCycle: 
                gearFields = MockSmartCycleGearFields.builder()
                                                 .noRamp(false)
                                                 .controlPercent(50)
                                                 .cyclePeriodInMinutes(30)
                                                 .cycleCountSendType(MockCycleCountSendType.FixedCount)
                                                 .maxCycleCount(0)
                                                 .startingPeriodCount(8)
                                                 .sendRate(3600)
                                                 .stopCommandRepeat(0)
                                                 .howToStopControl(MockHowToStopControl.StopCycle)
                                                 .capacityReduction(100)
                                                 .whenToChangeFields(MockWhenToChangeFields.builder().whenToChange(MockWhenToChange.None).build())
                                                 .build();
            break;
        case TargetCycle:
                gearFields = MockTargetCycleGearFields.builder()
                                                  .noRamp(false)
                                                  .controlPercent(50)
                                                  .cyclePeriodInMinutes(30)
                                                  .cycleCountSendType(MockCycleCountSendType.FixedCount)
                                                  .maxCycleCount(0)
                                                  .startingPeriodCount(8)
                                                  .sendRate(3600)
                                                  .stopCommandRepeat(0)
                                                  .howToStopControl(MockHowToStopControl.StopCycle)
                                                  .capacityReduction(100)
                                                  .kWReduction(12.0)
                                                  .whenToChangeFields(MockWhenToChangeFields.builder().whenToChange(MockWhenToChange.None).build())
                                                  .build();
            break;
        case EcobeeCycle:
                gearFields = MockEcobeeCycleGearFields.builder()
                                                  .mandatory(true)
                                                  .rampIn(true)
                                                  .rampOut(true)
                                                  .controlPercent(50)
                                                  .howToStopControl(MockHowToStopControl.Restore)
                                                  .capacityReduction(100)
                                                  .whenToChangeFields(MockWhenToChangeFields.builder().whenToChange(MockWhenToChange.None).build())
                                                  .build();
            break;
        case HoneywellCycle:
                gearFields = MockHoneywellCycleGearFields.builder()
                                                     .rampInOut(true)
                                                     .controlPercent(3)
                                                     .cyclePeriodInMinutes(30)
                                                     .howToStopControl(MockHowToStopControl.Restore)
                                                     .capacityReduction(93)
                                                     .whenToChangeFields(MockWhenToChangeFields.builder().whenToChange(MockWhenToChange.None).build())
                                                     .build();
            break;
        case ItronCycle:
                gearFields = MockItronCycleGearFields.builder()
                                                 .cycleType(MockItronCycleType.STANDARD)
                                                 .rampIn(true)
                                                 .rampOut(true)
                                                 .dutyCyclePercent(95)
                                                 .dutyCyclePeriodInMinutes(30)
                                                 .criticality(97)
                                                 .capacityReduction(93)
                                                 .howToStopControl(MockHowToStopControl.Restore)
                                                 .whenToChangeFields(MockWhenToChangeFields.builder().whenToChange(MockWhenToChange.None).build())
                                                 .build();
            break;
        case SepCycle:
                gearFields = MockSepCycleGearFields.builder()
                                               .rampIn(true)
                                               .rampOut(true)
                                               .trueCycle(true)
                                               .controlPercent(51)
                                               .criticality(5)
                                               .howToStopControl(MockHowToStopControl.TimeIn)
                                               .capacityReduction(98)
                                               .whenToChangeFields(MockWhenToChangeFields.builder().whenToChange(MockWhenToChange.None).build())
                                               .build();
            break;
        case MasterCycle:
                gearFields = MockMasterCycleGearFields.builder()
                                                  .controlPercent(50)
                                                  .cyclePeriodInMinutes(30)
                                                  .groupSelectionMethod(MockGroupSelectionMethod.LastControlled)
                                                  .howToStopControl(MockHowToStopControl.TimeIn)
                                                  .capacityReduction(100)
                                                  .whenToChangeFields(MockWhenToChangeFields.builder().whenToChange(MockWhenToChange.None).build())
                                                  .build();
            break;
        case TimeRefresh:
                gearFields = MockTimeRefreshGearFields.builder()
                                                  .refreshShedTime(MockCycleCountSendType.FixedShedTime)
                                                  .shedTime(3600)
                                                  .numberOfGroups(0)
                                                  .sendRate(1800)
                                                  .groupSelectionMethod(MockGroupSelectionMethod.LastControlled)
                                                  .howToStopControl(MockHowToStopControl.TimeIn)
                                                  .stopCommandRepeat(0)
                                                  .capacityReduction(100)
                                                  .whenToChangeFields(MockWhenToChangeFields.builder().whenToChange(MockWhenToChange.None).build())
                                                  .build();
            break;
        case Rotation:
                gearFields = MockRotationGearFields.builder()
                                               .capacityReduction(100)
                                               .groupSelectionMethod(MockGroupSelectionMethod.LastControlled)
                                               .howToStopControl(MockHowToStopControl.TimeIn)
                                               .numberOfGroups(0)
                                               .sendRate(30)
                                               .shedTime(10)
                                               .build();
            break;
        case Latching:
            gearFields = MockLatchingGearFields.builder()
                                           .startControlState(MockControlStartState.Open)
                                           .capacityReduction(100)
                                           .build();
            break;
        case ThermostatRamping:
                gearFields = MockThermostatSetbackGearFields.builder()
                                                        .absoluteOrDelta(MockAbsoluteOrDelta.DELTA)
                                                        .measureUnit(MockTemperatureMeasureUnit.FAHRENHEIT)
                                                        .isHeatMode(false)
                                                        .isCoolMode(true)
                                                        .minValue(1)
                                                        .maxValue(5)
                                                        .valueB(1)
                                                        .valueD(1)
                                                        .valueF(1)
                                                        .random(1)
                                                        .valueTa(1)
                                                        .valueTb(1)
                                                        .valueTc(1)
                                                        .valueTd(1)
                                                        .valueTe(1)
                                                        .valueTf(1)
                                                        .howToStopControl(MockHowToStopControl.TimeIn)
                                                        .capacityReduction(95)
                                                        .whenToChangeFields(MockWhenToChangeFields.builder().whenToChange(MockWhenToChange.None).build())
                                                        .build();
            break;
        case SimpleThermostatRamping:
                gearFields = MockSimpleThermostatRampingGearFields.builder()
                                                              .mode(MockMode.COOL)
                                                              .randomStartTimeInMinutes(60)
                                                              .preOpTemp(1)
                                                              .preOpTimeInMinutes(120)
                                                              .preOpHoldInMinutes(120)
                                                              .rampPerHour(0.2f)
                                                              .max(3)
                                                              .rampOutTimeInMinutes(180)
                                                              .maxRuntimeInMinutes(480)
                                                              .howToStopControl(MockHowToStopControl.TimeIn)
                                                              .whenToChangeFields(MockWhenToChangeFields.builder().whenToChange(MockWhenToChange.None).build())
                                                              .build();
            break;
        case SepTemperatureOffset:
                gearFields = MockSepTemperatureOffsetGearFields.builder()
                                                           .rampIn(true)
                                                           .rampOut(false)
                                                           .celsiusOrFahrenheit(MockTemperatureMeasureUnit.CELSIUS)
                                                           .mode(MockMode.HEAT)
                                                           .offset(1.0)
                                                           .criticality(5)
                                                           .howToStopControl(MockHowToStopControl.Restore)
                                                           .capacityReduction(96)
                                                           .whenToChangeFields(MockWhenToChangeFields.builder().whenToChange(MockWhenToChange.None).build())
                                                           .build();
            break;
        case BeatThePeak:
                gearFields = MockBeatThePeakGearFields.builder()
                                                  .indicator(MockBtpLedIndicator.Yellow)
                                                  .timeoutInMinutes(20)
                                                  .resendInMinutes(15)
                                                  .whenToChangeFields(MockWhenToChangeFields.builder().whenToChange(MockWhenToChange.None).build())
                                                  .build();
            break;
        case NoControl:
                gearFields = MockNoControlGearFields.builder()
                                                .whenToChangeFields(MockWhenToChangeFields.builder().whenToChange(MockWhenToChange.None).build())
                                                .build();
            break;
        }
        return gearFields;
    }


}
