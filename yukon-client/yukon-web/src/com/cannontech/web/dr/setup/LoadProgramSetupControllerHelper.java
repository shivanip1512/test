package com.cannontech.web.dr.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.cannontech.common.dr.gear.setup.AbsoluteOrDelta;
import com.cannontech.common.dr.gear.setup.BtpLedIndicator;
import com.cannontech.common.dr.gear.setup.ControlStartState;
import com.cannontech.common.dr.gear.setup.CycleCountSendType;
import com.cannontech.common.dr.gear.setup.GroupSelectionMethod;
import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.Mode;
import com.cannontech.common.dr.gear.setup.OperationalState;
import com.cannontech.common.dr.gear.setup.TemperatureMeasureUnit;
import com.cannontech.common.dr.gear.setup.WhenToChange;
import com.cannontech.common.dr.gear.setup.fields.BeatThePeakGearFields;
import com.cannontech.common.dr.gear.setup.fields.EcobeeCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.HoneywellCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.ItronCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.RotationGearFields;
import com.cannontech.common.dr.gear.setup.fields.SepTemperatureOffsetGearFields;
import com.cannontech.common.dr.gear.setup.fields.SimpleThermostatRampingGearFields;
import com.cannontech.common.dr.gear.setup.fields.SmartCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.TargetCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.ThermostatSetbackGearFields;
import com.cannontech.common.dr.gear.setup.fields.WhenToChangeFields;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.Notification;
import com.cannontech.common.dr.program.setup.model.NotificationGroup;
import com.cannontech.common.dr.program.setup.model.ProgramControlWindow;
import com.cannontech.common.dr.program.setup.model.ProgramControlWindowFields;
import com.cannontech.common.dr.program.setup.model.ProgramDirectMemberControl;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.dr.itron.model.ItronCycleType;
import com.cannontech.dr.nest.model.v3.PeakLoadShape;
import com.cannontech.dr.nest.model.v3.PostLoadShape;
import com.cannontech.dr.nest.model.v3.PrepLoadShape;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class LoadProgramSetupControllerHelper {

    @Autowired ServerDatabaseCache cache;
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    private static final List<PaoType> switchTypes = PaoType.getDirectLMProgramTypes().stream()
                                                                                      .sorted((p1, p2) -> p1.getDbString().compareTo(p2.getDbString()))
                                                                                      .collect(Collectors.toList());

    public void buildProgramModelMap(ModelMap model, YukonUserContext userContext, HttpServletRequest request,
            LoadProgram loadProgram) {

        retrieveProgramConstraints(model, request, userContext);
        if (loadProgram.getType() == PaoType.LM_NEST_PROGRAM) {
            model.addAttribute("operationalStates", List.of(OperationalState.ManualOnly));
        } else {
            model.addAttribute("operationalStates", OperationalState.values());
        }
        model.addAttribute("selectedSwitchType", loadProgram.getType());
        model.addAttribute("loadProgram", loadProgram);
        model.addAttribute("switchTypes", switchTypes);

        List<Integer> selectedMemberIds = new ArrayList<>();
        if (loadProgram.getMemberControl() != null) {
            loadProgram.getMemberControl().forEach(p -> selectedMemberIds.add(p.getSubordinateProgId()));
        }
        model.addAttribute("selectedMemberIds", selectedMemberIds);

        List<Integer> selectedNotificationGroupIds = new ArrayList<>();
        if (loadProgram.getNotification() != null
                && loadProgram.getNotification().getAssignedNotificationGroups() != null) {
            loadProgram.getNotification().getAssignedNotificationGroups().forEach(
                p -> selectedNotificationGroupIds.add(p.getNotificationGrpID()));
        }
        model.addAttribute("selectedNotificationGroupIds", selectedNotificationGroupIds);

        List<Integer> selectedGroupIds = new ArrayList<>();
        if (loadProgram.getAssignedGroups() != null) {
            loadProgram.getAssignedGroups().forEach(p -> selectedGroupIds.add(p.getGroupId()));
        }
        model.addAttribute("selectedGroupIds", selectedGroupIds);
        
        if (model.containsAttribute("gearInfos")) {
            List<GearInfo> gearInfos = (List<GearInfo>) model.get("gearInfos");
            model.addAttribute("gearInfos", gearInfos);
        } else {
            buildGearInfo(model, loadProgram);
        }

    }

    public void buildGearInfo(ModelMap model, LoadProgram loadProgram) {

        if (CollectionUtils.isNotEmpty(loadProgram.getGears())) {
            List<GearInfo> gearInfos = new ArrayList<>();
            loadProgram.getGears().forEach(gear -> {
                GearInfo info = new GearInfo();
                info.setId(gear.getGearId().toString());
                info.setName(gear.getGearName());
                info.setControlMethod(gear.getControlMethod());
                gearInfos.add(info);
            });
            model.addAttribute("gearInfos", gearInfos);
        }

    }

    public void setDefaultProgramControlWindow(LoadProgram loadProgram) {
        ProgramControlWindow window = new ProgramControlWindow();
        ProgramControlWindowFields fields = new ProgramControlWindowFields(0, 0);
        window.setControlWindowOne(fields);
        window.setControlWindowTwo(fields);
        loadProgram.setControlWindow(window);
    }

    /**
     * Retrieve ProgramConstraints
     */
    @SuppressWarnings("unchecked")
    private void retrieveProgramConstraints(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {

        List<LMDto> constraints = new ArrayList<>();
        String url = helper.findWebServerUrl(request, userContext, ApiURL.drAllProgramConstraintUrl);
        ResponseEntity<List<? extends Object>> response =
            apiRequestHelper.callAPIForList(userContext, request, url, LMDto.class, HttpMethod.GET, LMDto.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            constraints = (List<LMDto>) response.getBody();
        }
        model.addAttribute("constraints", constraints);
    }

    public void buildProgramGroup(LoadProgram loadProgram, List<Integer> selectedGroupIds) {
        List<ProgramGroup> assignedGroups = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(selectedGroupIds)) {
            Integer groupOrder = 1;
            for (Integer selectedGroupId : selectedGroupIds) {
                ProgramGroup group = new ProgramGroup();
                group.setGroupId(selectedGroupId);
                group.setGroupOrder(groupOrder);
                assignedGroups.add(group);
                groupOrder++;
            }
            loadProgram.setAssignedGroups(assignedGroups);
        }
    }

    public void buildProgramDirectMemberControl(LoadProgram loadProgram, List<Integer> selectedMemberIds) {

        List<ProgramDirectMemberControl> directMemberControls = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(selectedMemberIds)) {
            directMemberControls =
                Lists.transform(selectedMemberIds, new Function<Integer, ProgramDirectMemberControl>() {
                    public ProgramDirectMemberControl apply(Integer subordinateProgId) {
                        ProgramDirectMemberControl directMemberControl = new ProgramDirectMemberControl();
                        directMemberControl.setSubordinateProgId(subordinateProgId);

                        LiteYukonPAObject excludedPao = cache.getAllLMPrograms().stream().filter(
                            program -> program.getLiteID() == subordinateProgId).findFirst().get();
                        directMemberControl.setSubordinateProgName(excludedPao.getPaoName());
                        return directMemberControl;
                    }
                });
            loadProgram.setMemberControl(directMemberControls);
        }
    }

    public void buildNotificationGroup(LoadProgram loadProgram, List<Integer> selectedNotificationGroupIds) {

        List<NotificationGroup> notificationGroups = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(selectedNotificationGroupIds)) {
            notificationGroups =
                Lists.transform(selectedNotificationGroupIds, new Function<Integer, NotificationGroup>() {
                    public NotificationGroup apply(Integer notificationGrpID) {
                        NotificationGroup notificationGroup = new NotificationGroup();
                        notificationGroup.setNotificationGrpID(notificationGrpID);

                        List<LiteNotificationGroup> notificationGroups = cache.getAllContactNotificationGroups();
                        LiteNotificationGroup liteNotificationGroup = notificationGroups.stream().filter(
                            notifGroup -> notifGroup.getNotificationGroupID() == notificationGrpID).findFirst().get();
                        notificationGroup.setNotificationGrpName(liteNotificationGroup.getNotificationGroupName());
                        return notificationGroup;
                    }
                });
            Notification notification = loadProgram.getNotification();
            notification.setAssignedNotificationGroups(notificationGroups);
            loadProgram.setNotification(notification);
        }
    }

    public void buildNotificationModel(ModelMap model, LoadProgram loadProgram) {
        if (loadProgram.getNotification() == null) {
            Notification notif = new Notification();
            notif.setEnableOnSchedule(false);
            notif.setNotifyOnAdjust(false);
            loadProgram.setNotification(notif);
        }

    }
    /**
     * Default values for object should be set here.
     */
    public void setDefaultGearFieldValues(ProgramGear programGear) {
        WhenToChangeFields whenToChange = new WhenToChangeFields();
        whenToChange.setWhenToChange(WhenToChange.None);
        switch (programGear.getControlMethod()) {
        case MagnitudeCycle:
        case TrueCycle:
        case SmartCycle:
        case TargetCycle:
            SmartCycleGearFields smartCycleGearFields = (SmartCycleGearFields) programGear.getFields();
            setSmartCycleGearFieldsDefaultValue(smartCycleGearFields);
            programGear.setFields(smartCycleGearFields);
            break;
        case EcobeeCycle:
            EcobeeCycleGearFields ecobeeCycleGearFields = (EcobeeCycleGearFields) programGear.getFields();
            ecobeeCycleGearFields.setMandatory(false);
            ecobeeCycleGearFields.setRampIn(true);
            ecobeeCycleGearFields.setRampOut(true);
            ecobeeCycleGearFields.setControlPercent(50);
            ecobeeCycleGearFields.setCapacityReduction(100);
            ecobeeCycleGearFields.setWhenToChangeFields(getWhenToChangeDefaultValues());
            break;
        case HoneywellCycle:
            HoneywellCycleGearFields honeywellCycleGearFields = (HoneywellCycleGearFields) programGear.getFields();
            honeywellCycleGearFields.setRampInOut(true);
            honeywellCycleGearFields.setControlPercent(0);
            honeywellCycleGearFields.setCapacityReduction(100);
            honeywellCycleGearFields.setWhenToChangeFields(getWhenToChangeDefaultValues());
            break;
        case ItronCycle:
            ItronCycleGearFields itronCycleGearFields = (ItronCycleGearFields) programGear.getFields();
            itronCycleGearFields.setRampIn(true);
            itronCycleGearFields.setRampOut(true);
            itronCycleGearFields.setCapacityReduction(100);
            itronCycleGearFields.setDutyCyclePercent(50);
            itronCycleGearFields.setCriticality(1);
            itronCycleGearFields.setWhenToChangeFields(getWhenToChangeDefaultValues());
            break;
        case ThermostatRamping:
            ThermostatSetbackGearFields gearFields = (ThermostatSetbackGearFields) programGear.getFields();
            gearFields.setCapacityReduction(100);
            gearFields.setWhenToChangeFields(whenToChange);
            gearFields.setHowToStopControl(HowToStopControl.TimeIn);
            gearFields.setValueTa(0);
            gearFields.setValueTb(0);
            gearFields.setValueTc(0);
            gearFields.setValueTd(0);
            gearFields.setValueTe(0);
            gearFields.setValueTf(0);
            gearFields.setValueD(0);
            gearFields.setValueB(0);
            gearFields.setValueF(0);
            gearFields.setMinValue(0);
            gearFields.setMaxValue(0);
            gearFields.setIsCoolMode(false);
            gearFields.setIsHeatMode(false);
            gearFields.setAbsoluteOrDelta(AbsoluteOrDelta.DELTA);
            gearFields.setMeasureUnit(TemperatureMeasureUnit.CELSIUS);
            break;
        case SimpleThermostatRamping:
            SimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (SimpleThermostatRampingGearFields)programGear.getFields();
            simpleThermostatRampingGearFields.setMode(Mode.COOL);
            simpleThermostatRampingGearFields.setRandomStartTimeInMinutes(0);
            simpleThermostatRampingGearFields.setPreOpTemp(0);
            simpleThermostatRampingGearFields.setPreOpTimeInMinutes(0);
            simpleThermostatRampingGearFields.setPreOpHoldInMinutes(0);
            simpleThermostatRampingGearFields.setMaxRuntimeInMinutes(480);
            simpleThermostatRampingGearFields.setWhenToChangeFields(whenToChange);
            simpleThermostatRampingGearFields.setMax(0);
            simpleThermostatRampingGearFields.setRampPerHour(0f);
            simpleThermostatRampingGearFields.setRampOutTimeInMinutes(0);
            simpleThermostatRampingGearFields.setHowToStopControl(HowToStopControl.TimeIn);
            break;
        case SepTemperatureOffset:
            SepTemperatureOffsetGearFields sepTemperatureOffsetGearFields = (SepTemperatureOffsetGearFields)programGear.getFields();
            sepTemperatureOffsetGearFields.setRampIn(true);
            sepTemperatureOffsetGearFields.setRampOut(true);
            sepTemperatureOffsetGearFields.setMode(Mode.HEAT);
            sepTemperatureOffsetGearFields.setCelsiusOrFahrenheit(TemperatureMeasureUnit.FAHRENHEIT);
            sepTemperatureOffsetGearFields.setOffset(1.0);
            sepTemperatureOffsetGearFields.setCriticality(6);
            sepTemperatureOffsetGearFields.setHowToStopControl(HowToStopControl.TimeIn);
            sepTemperatureOffsetGearFields.setCapacityReduction(100);
            sepTemperatureOffsetGearFields.setWhenToChangeFields(whenToChange);
            break;
        case Rotation:
            RotationGearFields rotationGearFields = (RotationGearFields) programGear.getFields();
            setDefaultRotationGearFields(rotationGearFields);
            break;
        case BeatThePeak:
            BeatThePeakGearFields beatThePeakGearFields = (BeatThePeakGearFields) programGear.getFields();
            setDefaultBeatThePeakGearFields(beatThePeakGearFields);
            break;
        }
    }
    
    
    private void setDefaultRotationGearFields(RotationGearFields rotationGearFields) {
        rotationGearFields.setShedTime(5);
        rotationGearFields.setSendRate(30);
        rotationGearFields.setCapacityReduction(100);
    }

    private void setDefaultBeatThePeakGearFields(BeatThePeakGearFields rotationGearFields) {
        rotationGearFields.setResendInMinutes(0);
        rotationGearFields.setTimeoutInMinutes(0);
    }

    private void setSmartCycleGearFieldsDefaultValue(SmartCycleGearFields smartCycleGearFields) {
        smartCycleGearFields.setNoRamp(false);
        smartCycleGearFields.setControlPercent(50);
        smartCycleGearFields.setCyclePeriodInMinutes(30);
        smartCycleGearFields.setCycleCountSendType(CycleCountSendType.FixedCount);
        smartCycleGearFields.setStartingPeriodCount(8);
        smartCycleGearFields.setSendRate(TimeIntervals.HOURS_1.getSeconds());
        smartCycleGearFields.setHowToStopControl(HowToStopControl.StopCycle);
        smartCycleGearFields.setStopCommandRepeat(0);
        smartCycleGearFields.setCapacityReduction(100);
        if (smartCycleGearFields instanceof TargetCycleGearFields) {
            TargetCycleGearFields targetCycleGearFields = (TargetCycleGearFields) smartCycleGearFields;
            targetCycleGearFields.setkWReduction(0.0);
        }
        smartCycleGearFields.setWhenToChangeFields(getWhenToChangeDefaultValues());
    }

    private WhenToChangeFields getWhenToChangeDefaultValues() {
        WhenToChangeFields whenToChange = new WhenToChangeFields();
        whenToChange.setWhenToChange(WhenToChange.None);
        whenToChange.setChangeDurationInMinutes(0);
        whenToChange.setChangePriority(0);
        whenToChange.setTriggerNumber(1);
        whenToChange.setTriggerOffset(0.0);
        return whenToChange;
    }

    public void buildGearModelMap(GearControlMethod controlMethod, ModelMap model, HttpServletRequest request,
            YukonUserContext userContext) {
        switch (controlMethod) {
        case MagnitudeCycle:
        case TrueCycle:
        case SmartCycle:
        case TargetCycle:
            model.addAttribute("cycleCountSendType", List.of(CycleCountSendType.FixedCount, CycleCountSendType.CountDown, CycleCountSendType.LimitedCountDown));
            List<Integer> startingPeriodCount = new ArrayList<>();
            for (int i = 1; i <= 63; i++) {
                startingPeriodCount.add(i);
            }
            model.addAttribute("maxCycleCount", startingPeriodCount);
            model.addAttribute("startingPeriodCount", startingPeriodCount);
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore, HowToStopControl.StopCycle));
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("commandResendRate", TimeIntervals.getCommandResendRate());
            break;
        case EcobeeCycle:
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore));
            break;
        case HoneywellCycle:
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore));
            model.addAttribute("cyclePeriod", List.of(30));
            break;
        case ItronCycle:
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("cycleType",List.of(ItronCycleType.STANDARD,ItronCycleType.TRUE_CYCLE,ItronCycleType.SMART_CYCLE));
            model.addAttribute("dutyCyclePeriod", ImmutableList.of(30, 60));
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore));
            break;
        case NestStandardCycle:
            model.addAttribute("preparationLoadShaping",List.of(PrepLoadShape.PREP_STANDARD,PrepLoadShape.PREP_RAMPING,PrepLoadShape.PREP_UNSPECIFIED,PrepLoadShape.PREP_NONE));
            model.addAttribute("peakLoadShaping",List.of(PeakLoadShape.PEAK_STANDARD,PeakLoadShape.PEAK_SYMMETRIC,PeakLoadShape.PEAK_UNIFORM,PeakLoadShape.PEAK_UNSPECIFIED));
            model.addAttribute("postPeakLoadShaping",List.of(PostLoadShape.POST_STANDARD,PostLoadShape.POST_RAMPING,PostLoadShape.POST_UNSPECIFIED));
            break;
        case ThermostatRamping:
            model.addAttribute("tempreatureMode", true);
            model.addAttribute("units", TemperatureMeasureUnit.values());
            model.addAttribute("setpoints", AbsoluteOrDelta.values());
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("howtoStopControlFields", Lists.newArrayList(HowToStopControl.TimeIn, HowToStopControl.Restore));
            break;
        case SimpleThermostatRamping:
            model.addAttribute("tempreatureModes", Mode.values());
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("howtoStopControlFields", Lists.newArrayList(HowToStopControl.TimeIn, HowToStopControl.Restore));
            break;
        case SepTemperatureOffset:
            model.addAttribute("tempreatureModes", Mode.values());
            model.addAttribute("units", TemperatureMeasureUnit.values());
            model.addAttribute("howtoStopControlFields", Lists.newArrayList(HowToStopControl.TimeIn, HowToStopControl.Restore));
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            break;
        case Rotation:
            model.addAttribute("shedTime", TimeIntervals.getRotationshedtime());
            List<Integer> groupOptions = new ArrayList<>();
            for (int i = 1; i <= 25; i++) {
                groupOptions.add(i);
            }
            model.addAttribute("groupOptions", groupOptions);
            model.addAttribute("commandResendRate", TimeIntervals.getCommandResendRate());
            model.addAttribute("groupSelectionMethodOptions", GroupSelectionMethod.values());
            model.addAttribute("stopControlOptions", List.of(HowToStopControl.Restore,HowToStopControl.StopCycle));
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            break;
        case BeatThePeak:
            model.addAttribute("indicators", BtpLedIndicator.values());
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            break;
        case NoControl:
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            break;
        case Latching:
            model.addAttribute("controlStartState", ControlStartState.values());
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            break;
        }
    }
    
    public void setValidationMessageInFlash(BindingResult result, FlashScope flash, LoadProgram loadProgram , String baseKey ){
        List<FieldError> errorList = result.getFieldErrors();
        Set<Integer> gearPositionIndexes = errorList.stream()
                                                    .filter(fieldError -> fieldError.getField().contains("gears") 
                                                        && !(fieldError.getField().contains("gearName")) 
                                                        && !(fieldError.getField().contains("controlMethod")))
                                                    .map(fieldError -> Integer.parseInt(fieldError.getField().replaceAll("[\\D]", "")))
                                                    .collect(Collectors.toSet());
        
        List<String> filteredList = 
                IntStream.range(0, loadProgram.getGears().size())
                         .filter(i -> gearPositionIndexes.contains(i))
                         .mapToObj(loadProgram.getGears()::get)
                         .map(gear -> gear.getGearName())
                         .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(filteredList)) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "gear.error", String.join(", ", filteredList)));
        }
        
    }

}
