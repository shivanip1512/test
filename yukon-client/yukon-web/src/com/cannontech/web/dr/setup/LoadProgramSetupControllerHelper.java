package com.cannontech.web.dr.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import com.cannontech.common.dr.gear.setup.Setpoint;
import com.cannontech.common.dr.gear.setup.BtpLedIndicator;
import com.cannontech.common.dr.gear.setup.ControlStartState;
import com.cannontech.common.dr.gear.setup.CycleCountSendType;
import com.cannontech.common.dr.gear.setup.GroupSelectionMethod;
import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.Mode;
import com.cannontech.common.dr.gear.setup.OperationalState;
import com.cannontech.common.dr.gear.setup.StopOrder;
import com.cannontech.common.dr.gear.setup.TemperatureMeasureUnit;
import com.cannontech.common.dr.gear.setup.WhenToChange;
import com.cannontech.common.dr.gear.setup.fields.BeatThePeakGearFields;
import com.cannontech.common.dr.gear.setup.fields.EatonCloudCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.EcobeeCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.EcobeeSetpointGearFields;
import com.cannontech.common.dr.gear.setup.fields.EcobeePlusGearFields;
import com.cannontech.common.dr.gear.setup.fields.HoneywellCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.HoneywellSetpointGearFields;
import com.cannontech.common.dr.gear.setup.fields.ItronCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.LatchingGearFields;
import com.cannontech.common.dr.gear.setup.fields.MasterCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.NoControlGearFields;
import com.cannontech.common.dr.gear.setup.fields.RotationGearFields;
import com.cannontech.common.dr.gear.setup.fields.SepCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.SepTemperatureOffsetGearFields;
import com.cannontech.common.dr.gear.setup.fields.SimpleThermostatRampingGearFields;
import com.cannontech.common.dr.gear.setup.fields.SmartCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.TargetCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.ThermostatSetbackGearFields;
import com.cannontech.common.dr.gear.setup.fields.TimeRefreshGearFields;
import com.cannontech.common.dr.gear.setup.fields.WhenToChangeFields;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.dr.program.setup.model.Notification;
import com.cannontech.common.dr.program.setup.model.NotificationGroup;
import com.cannontech.common.dr.program.setup.model.ProgramDirectMemberControl;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.dr.eatonCloud.model.EatonCloudCycleType;
import com.cannontech.dr.itron.model.ItronCycleType;
import com.cannontech.dr.nest.model.v3.PeakLoadShape;
import com.cannontech.dr.nest.model.v3.PostLoadShape;
import com.cannontech.dr.nest.model.v3.PrepLoadShape;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiControllerHelper;
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

    }

    /**
     * Retrieve ProgramConstraints
     */
    @SuppressWarnings("unchecked")
    private void retrieveProgramConstraints(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {

        List<LMDto> constraints = new ArrayList<>();
        String url = helper.findWebServerUrl(request, userContext, ApiURL.drProgramConstraintUrl);
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
                    @Override
                    public ProgramDirectMemberControl apply(Integer subordinateProgId) {
                        ProgramDirectMemberControl directMemberControl = new ProgramDirectMemberControl();
                        directMemberControl.setSubordinateProgId(subordinateProgId);

                        LiteYukonPAObject excludedPao = cache.getAllLMPrograms()
                                .stream()
                                .filter(program -> program.getLiteID() == subordinateProgId)
                                .findFirst()
                                .get();
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
                    @Override
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

    public void buildProgramCopyModelMap(ModelMap model, YukonUserContext userContext, HttpServletRequest request,
            LoadProgramCopy programCopy, LiteYukonPAObject lmProgram) {

        retrieveProgramConstraints(model, request, userContext);
        if (lmProgram.getPaoType() == PaoType.LM_NEST_PROGRAM) {
            model.addAttribute("operationalStates", List.of(OperationalState.ManualOnly));
        } else {
            model.addAttribute("operationalStates", OperationalState.values());
        }
        model.addAttribute("loadProgramId", lmProgram.getLiteID());
        model.addAttribute("selectedSwitchType", lmProgram.getPaoType());
    }
    /**
     * Default values for object should be set here.
     */
    public void setDefaultGearFieldValues(ProgramGear programGear) {
        switch (programGear.getControlMethod()) {
        case MagnitudeCycle:
        case TrueCycle:
        case SmartCycle:
        case TargetCycle:
            SmartCycleGearFields smartCycleGearFields = (SmartCycleGearFields) programGear.getFields();
            setSmartCycleGearFieldsDefaultValue(smartCycleGearFields);
            break;
        case SepCycle:
            SepCycleGearFields sepCycleGearFields = (SepCycleGearFields) programGear.getFields();
            setSepCycleGearFieldsDefaultValues(sepCycleGearFields);
            break;
        case MasterCycle:
            MasterCycleGearFields masterCycleGearFields = (MasterCycleGearFields) programGear.getFields();
            setMasterCycleGearFieldsDefaultValues(masterCycleGearFields);
            break;
        case TimeRefresh:
            TimeRefreshGearFields timeRefreshGearFields = (TimeRefreshGearFields) programGear.getFields();
            setTimeRefreshGearFieldsDefaultValues(timeRefreshGearFields);
            break;
        case EcobeeCycle:
            EcobeeCycleGearFields ecobeeCycleGearFields = (EcobeeCycleGearFields) programGear.getFields();
            setEcobeeCycleGearFieldsDefaultValues(ecobeeCycleGearFields);
            break;
        case EcobeeSetpoint:
            EcobeeSetpointGearFields ecobeeSetpointGearFields = (EcobeeSetpointGearFields) programGear.getFields();
            setEcobeeSetpointGearFieldsDefaultValues(ecobeeSetpointGearFields);
            break;
        case EcobeePlus:
            EcobeePlusGearFields ecobeePlusGearFields = (EcobeePlusGearFields) programGear.getFields();
            setEcobeePlusFieldsDefaultValues(ecobeePlusGearFields);
            break;
        case HoneywellCycle:
            HoneywellCycleGearFields honeywellCycleGearFields = (HoneywellCycleGearFields) programGear.getFields();
            setHoneywellCycleGearFieldsDefaultValues(honeywellCycleGearFields);
            break;
        case HoneywellSetpoint:
            HoneywellSetpointGearFields honeywellSetpointGearFields = (HoneywellSetpointGearFields) programGear.getFields();
            setHoneywellSetpointGearFieldsDefaultValues(honeywellSetpointGearFields);
            break;
        case ItronCycle:
            ItronCycleGearFields itronCycleGearFields = (ItronCycleGearFields) programGear.getFields();
            setItronCycleGearFieldsDefaultValues(itronCycleGearFields);
            break;
        case ThermostatRamping:
            ThermostatSetbackGearFields thermostatCycleGearFields = (ThermostatSetbackGearFields) programGear.getFields();
            setThermostatCycleGearFieldsDefaultValues(thermostatCycleGearFields);
            break;
        case SimpleThermostatRamping:
            SimpleThermostatRampingGearFields simpleThermostatRampingGearFields = (SimpleThermostatRampingGearFields)programGear.getFields();
            setSimpleThermostatCycleGearFieldsDefaultValues(simpleThermostatRampingGearFields);
            break;
        case SepTemperatureOffset:
            SepTemperatureOffsetGearFields sepTemperatureOffsetGearFields = (SepTemperatureOffsetGearFields)programGear.getFields();
            setSepTemperatureOffsetGearFieldsDefaultValues(sepTemperatureOffsetGearFields);
            break;
        case Rotation:
            RotationGearFields rotationGearFields = (RotationGearFields) programGear.getFields();
            setDefaultRotationGearFields(rotationGearFields);
            break;
        case BeatThePeak:
            BeatThePeakGearFields beatThePeakGearFields = (BeatThePeakGearFields) programGear.getFields();
            setDefaultBeatThePeakGearFields(beatThePeakGearFields);
            break;
        case Latching:
            LatchingGearFields latchingGearFields = (LatchingGearFields) programGear.getFields();
            latchingGearFields.setCapacityReduction(100);
            break;
        case EatonCloudCycle:
            EatonCloudCycleGearFields eatonCloudCycleGearFields = (EatonCloudCycleGearFields) programGear.getFields();
            setEatonCloudCycleGearFieldsDefaultValues(eatonCloudCycleGearFields);
            break;
        }
    }

    private void setDefaultRotationGearFields(RotationGearFields rotationGearFields) {
        rotationGearFields.setShedTime(5);
        rotationGearFields.setSendRate(TimeIntervals.NONE.getSeconds());
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
        smartCycleGearFields.setSendRate(TimeIntervals.NONE.getSeconds());
        smartCycleGearFields.setHowToStopControl(HowToStopControl.StopCycle);
        smartCycleGearFields.setStopCommandRepeat(0);
        smartCycleGearFields.setCapacityReduction(100);
        if (smartCycleGearFields instanceof TargetCycleGearFields) {
            TargetCycleGearFields targetCycleGearFields = (TargetCycleGearFields) smartCycleGearFields;
            targetCycleGearFields.setkWReduction(0.0);
        }
    }

    private void setSepCycleGearFieldsDefaultValues(SepCycleGearFields sepCycleGearFields) {
        sepCycleGearFields.setRampIn(true);
        sepCycleGearFields.setRampOut(true);
        sepCycleGearFields.setTrueCycle(true);
        sepCycleGearFields.setControlPercent(50);
        sepCycleGearFields.setCriticality(6);
        sepCycleGearFields.setHowToStopControl(HowToStopControl.TimeIn);
        sepCycleGearFields.setCapacityReduction(100);
    }

    private void setMasterCycleGearFieldsDefaultValues(MasterCycleGearFields masterCycleGearFields) {
        masterCycleGearFields.setControlPercent(50);
        masterCycleGearFields.setCyclePeriodInMinutes(30);
        masterCycleGearFields.setGroupSelectionMethod(GroupSelectionMethod.LastControlled);
        masterCycleGearFields.setHowToStopControl(HowToStopControl.TimeIn);
        masterCycleGearFields.setStopOrder(StopOrder.RANDOM);
        masterCycleGearFields.setCapacityReduction(100);
    }

    private void setTimeRefreshGearFieldsDefaultValues(TimeRefreshGearFields timeRefreshGearFields) {
        timeRefreshGearFields.setShedTime(TimeIntervals.HOURS_1.getSeconds());
        timeRefreshGearFields.setSendRate(TimeIntervals.NONE.getSeconds());
        timeRefreshGearFields.setGroupSelectionMethod(GroupSelectionMethod.LastControlled);
        timeRefreshGearFields.setHowToStopControl(HowToStopControl.TimeIn);
        timeRefreshGearFields.setStopOrder(StopOrder.RANDOM);
        timeRefreshGearFields.setStopCommandRepeat(0);
        timeRefreshGearFields.setCapacityReduction(100);
        timeRefreshGearFields.setRampOutPercent(0);
    }

    private void setEcobeeCycleGearFieldsDefaultValues(EcobeeCycleGearFields ecobeeCycleGearFields) {
        ecobeeCycleGearFields.setMandatory(false);
        ecobeeCycleGearFields.setRampInOut(true);
        ecobeeCycleGearFields.setControlPercent(50);
        ecobeeCycleGearFields.setCapacityReduction(100);
    }

    private void setEcobeeSetpointGearFieldsDefaultValues(EcobeeSetpointGearFields ecobeeSetpointGearFields) {
        ecobeeSetpointGearFields.setMandatory(false);
        ecobeeSetpointGearFields.setSetpointOffset(0);
        ecobeeSetpointGearFields.setMode(Mode.COOL);
        ecobeeSetpointGearFields.setCapacityReduction(100);
    }
    
    private void setEcobeePlusFieldsDefaultValues(EcobeePlusGearFields ecobeePlusGearFields) {
        ecobeePlusGearFields.setRampInOut(true);
        ecobeePlusGearFields.setHeatingEvent(false);
        ecobeePlusGearFields.setCapacityReduction(100);
    }

    private void setHoneywellCycleGearFieldsDefaultValues(HoneywellCycleGearFields honeywellCycleGearFields) {
        honeywellCycleGearFields.setMandatory(false);
        honeywellCycleGearFields.setRampInOut(true);
        honeywellCycleGearFields.setControlPercent(50);
        honeywellCycleGearFields.setCapacityReduction(100);
    }

    private void setHoneywellSetpointGearFieldsDefaultValues(HoneywellSetpointGearFields honeywellSetpointGearFields) {
        honeywellSetpointGearFields.setMandatory(false);
        honeywellSetpointGearFields.setSetpointOffset(0);
        honeywellSetpointGearFields.setMode(Mode.COOL);
        honeywellSetpointGearFields.setCapacityReduction(100);
    }

    private void setItronCycleGearFieldsDefaultValues(ItronCycleGearFields itronCycleGearFields) {
        itronCycleGearFields.setRampIn(true);
        itronCycleGearFields.setRampOut(true);
        itronCycleGearFields.setCapacityReduction(100);
        itronCycleGearFields.setDutyCyclePercent(50);
        itronCycleGearFields.setCriticality(100);
    }

    private void setThermostatCycleGearFieldsDefaultValues(ThermostatSetbackGearFields thermostatCycleGearFields) {
        thermostatCycleGearFields.setRandom(0);
        thermostatCycleGearFields.setCapacityReduction(100);
        thermostatCycleGearFields.setHowToStopControl(HowToStopControl.TimeIn);
        thermostatCycleGearFields.setValueTa(0);
        thermostatCycleGearFields.setValueTb(0);
        thermostatCycleGearFields.setValueTc(0);
        thermostatCycleGearFields.setValueTd(0);
        thermostatCycleGearFields.setValueTe(0);
        thermostatCycleGearFields.setValueTf(0);
        thermostatCycleGearFields.setValueD(0);
        thermostatCycleGearFields.setValueB(0);
        thermostatCycleGearFields.setValueF(0);
        thermostatCycleGearFields.setMinValue(0);
        thermostatCycleGearFields.setMaxValue(0);

        if (thermostatCycleGearFields.getIsCoolMode() == null) {
            thermostatCycleGearFields.setIsCoolMode(false);
        }
        if (thermostatCycleGearFields.getIsHeatMode() == null) {
            thermostatCycleGearFields.setIsHeatMode(false);
        }
        if (thermostatCycleGearFields.getSetpoint() == null) {
            thermostatCycleGearFields.setSetpoint(Setpoint.DELTA);
        }
        if (thermostatCycleGearFields.getTempMeasureUnit() == null) {
            thermostatCycleGearFields.setTempMeasureUnit(TemperatureMeasureUnit.FAHRENHEIT);
        }
    }

    private void setSimpleThermostatCycleGearFieldsDefaultValues(
            SimpleThermostatRampingGearFields simpleThermostatRampingGearFields) {
        if (simpleThermostatRampingGearFields.getMode() == null) {
            simpleThermostatRampingGearFields.setMode(Mode.COOL);
        }
        if (simpleThermostatRampingGearFields.getRandomStartTimeInMinutes() == null) {
            simpleThermostatRampingGearFields.setRandomStartTimeInMinutes(0);
        }
        if (simpleThermostatRampingGearFields.getPreOpTemp() == null) {
            simpleThermostatRampingGearFields.setPreOpTemp(0);
        }
        if (simpleThermostatRampingGearFields.getPreOpTimeInMinutes() == null) {
            simpleThermostatRampingGearFields.setPreOpTimeInMinutes(0);
        }
        if (simpleThermostatRampingGearFields.getPreOpHoldInMinutes() == null) {
            simpleThermostatRampingGearFields.setPreOpHoldInMinutes(0);
        }
        if (simpleThermostatRampingGearFields.getMaxRuntimeInMinutes() == null) {
            simpleThermostatRampingGearFields.setMaxRuntimeInMinutes(480);
        }
        if (simpleThermostatRampingGearFields.getMax() == null) {
            simpleThermostatRampingGearFields.setMax(0);
        }
        if (simpleThermostatRampingGearFields.getRampPerHour() == null) {
            simpleThermostatRampingGearFields.setRampPerHour(0f);
        }
        if (simpleThermostatRampingGearFields.getRampOutTimeInMinutes() == null) {
            simpleThermostatRampingGearFields.setRampOutTimeInMinutes(0);
        }
        if (simpleThermostatRampingGearFields.getHowToStopControl() == null) {
            simpleThermostatRampingGearFields.setHowToStopControl(HowToStopControl.TimeIn);
        }
    }

    private void setSepTemperatureOffsetGearFieldsDefaultValues(SepTemperatureOffsetGearFields sepTemperatureOffsetGearFields) {
        sepTemperatureOffsetGearFields.setRampIn(true);
        sepTemperatureOffsetGearFields.setRampOut(true);
        sepTemperatureOffsetGearFields.setMode(Mode.HEAT);
        sepTemperatureOffsetGearFields.setTempMeasureUnit(TemperatureMeasureUnit.FAHRENHEIT);
        sepTemperatureOffsetGearFields.setOffset(1.0);
        sepTemperatureOffsetGearFields.setCriticality(6);
        sepTemperatureOffsetGearFields.setHowToStopControl(HowToStopControl.TimeIn);
        sepTemperatureOffsetGearFields.setCapacityReduction(100);
    }
    
    private void setEatonCloudCycleGearFieldsDefaultValues(EatonCloudCycleGearFields eatonCloudCycleGearFields) {
        eatonCloudCycleGearFields.setRampIn(true);
        eatonCloudCycleGearFields.setRampOut(true);
        eatonCloudCycleGearFields.setCapacityReduction(100);
        eatonCloudCycleGearFields.setDutyCyclePercent(50);
        eatonCloudCycleGearFields.setCriticality(100);
    }
    
    public void populateDefaultValuesForDependentFields(ProgramGear programGear) {
        switch (programGear.getControlMethod()) {
        case MagnitudeCycle:
        case TrueCycle:
        case SmartCycle:
        case TargetCycle:
            SmartCycleGearFields smartCycleGearFields = (SmartCycleGearFields) programGear.getFields();
            smartCycleGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(smartCycleGearFields.getWhenToChangeFields()));
            break;
        case SepCycle:
            SepCycleGearFields sepCycleGearFields = (SepCycleGearFields) programGear.getFields();
            sepCycleGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(sepCycleGearFields.getWhenToChangeFields()));
            break;
        case MasterCycle:
            MasterCycleGearFields masterCycleGearFields = (MasterCycleGearFields) programGear.getFields();
            masterCycleGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(masterCycleGearFields.getWhenToChangeFields()));
            
            if (masterCycleGearFields.getRampOutPercent() == null) {
                masterCycleGearFields.setRampOutPercent(0);
            }
            break;
        case TimeRefresh:
            TimeRefreshGearFields timeRefreshGearFields = (TimeRefreshGearFields) programGear.getFields();
            timeRefreshGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(timeRefreshGearFields.getWhenToChangeFields()));
            
            if (timeRefreshGearFields.getRampOutPercent() == null) {
                timeRefreshGearFields.setRampOutPercent(0);
            }
            break;
        case EcobeeCycle:
            EcobeeCycleGearFields ecobeeCycleGearFields = (EcobeeCycleGearFields) programGear.getFields();
            ecobeeCycleGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(ecobeeCycleGearFields.getWhenToChangeFields()));
            break;
        case EcobeeSetpoint:
            EcobeeSetpointGearFields ecobeeSetpointGearFields = (EcobeeSetpointGearFields) programGear.getFields();
            ecobeeSetpointGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(ecobeeSetpointGearFields.getWhenToChangeFields()));
            break;
            
        case EcobeePlus:
            EcobeePlusGearFields ecobeePlusFields = (EcobeePlusGearFields) programGear.getFields();
            ecobeePlusFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(ecobeePlusFields.getWhenToChangeFields()));
            break;
        case HoneywellCycle:
            HoneywellCycleGearFields honeywellCycleGearFields = (HoneywellCycleGearFields) programGear.getFields();
            honeywellCycleGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(honeywellCycleGearFields.getWhenToChangeFields()));
            break;
        case HoneywellSetpoint:
            HoneywellSetpointGearFields honeywellSetpointGearFields = (HoneywellSetpointGearFields) programGear.getFields();
            honeywellSetpointGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(honeywellSetpointGearFields.getWhenToChangeFields()));
            break;
        case ItronCycle:
            ItronCycleGearFields itronCycleGearFields = (ItronCycleGearFields) programGear.getFields();
            itronCycleGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(itronCycleGearFields.getWhenToChangeFields()));
            break;
        case ThermostatRamping:
            ThermostatSetbackGearFields thermostatCycleGearFields =
                (ThermostatSetbackGearFields) programGear.getFields();
            thermostatCycleGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(thermostatCycleGearFields.getWhenToChangeFields()));
            break;
        case SimpleThermostatRamping:
            SimpleThermostatRampingGearFields simpleThermostatRampingGearFields =
                (SimpleThermostatRampingGearFields) programGear.getFields();
            simpleThermostatRampingGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(simpleThermostatRampingGearFields.getWhenToChangeFields()));
            break;
        case SepTemperatureOffset:
            SepTemperatureOffsetGearFields sepTemperatureOffsetGearFields =
                (SepTemperatureOffsetGearFields) programGear.getFields();
            sepTemperatureOffsetGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(sepTemperatureOffsetGearFields.getWhenToChangeFields()));
            break;
        case Rotation:
            RotationGearFields rotationGearFields = (RotationGearFields) programGear.getFields();
            rotationGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(rotationGearFields.getWhenToChangeFields()));
            break;
        case BeatThePeak:
            BeatThePeakGearFields beatThePeakGearFields = (BeatThePeakGearFields) programGear.getFields();
            beatThePeakGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(beatThePeakGearFields.getWhenToChangeFields()));
            break;
        case EatonCloudCycle:
            EatonCloudCycleGearFields eatonCloudCycleGearFields = (EatonCloudCycleGearFields) programGear.getFields();
            eatonCloudCycleGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(eatonCloudCycleGearFields.getWhenToChangeFields()));
            break;
        case NoControl:
            NoControlGearFields noControlGearFields = (NoControlGearFields) programGear.getFields();
            noControlGearFields.setWhenToChangeFields(
                setWhenToChangeDefaultValues(noControlGearFields.getWhenToChangeFields()));
        }
    }

    /**
     * This method set the default value for WhenToChange fields if the field value is null. 
     * @param result */
    private WhenToChangeFields setWhenToChangeDefaultValues(WhenToChangeFields fields) {
        WhenToChangeFields whenToChange = null;
        if (fields == null) {
            whenToChange = new WhenToChangeFields();
        } else {
            whenToChange = fields;
        }
        if (whenToChange.getWhenToChange() == null) {
            whenToChange.setWhenToChange(WhenToChange.None);
        }
        if (whenToChange.getChangeDurationInMinutes() == null) {
            whenToChange.setChangeDurationInMinutes(0);
        }
        if (whenToChange.getChangePriority() == null) {
            whenToChange.setChangePriority(0);
        }
        if (whenToChange.getTriggerNumber() == null) {
            whenToChange.setTriggerNumber(1);
        }
        if (whenToChange.getTriggerOffset() == null) {
            whenToChange.setTriggerOffset(0.0);
        }
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
        case SepCycle:
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore, HowToStopControl.TimeIn));
            break;
        case MasterCycle:
            model.addAttribute("groupSelectionMethod", GroupSelectionMethod.values());
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore, HowToStopControl.TimeIn,
                HowToStopControl.RampOutTimeIn, HowToStopControl.RampOutRestore));
            model.addAttribute("stopOrder", List.of(StopOrder.RANDOM, StopOrder.FIRSTINFIRSTOUT));
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            break;
        case TimeRefresh:
            model.addAttribute("refreshShedType", List.of(CycleCountSendType.FixedShedTime, CycleCountSendType.DynamicShedTime));
            model.addAttribute("shedTime", TimeIntervals.getCommandResendRate());
            List<Integer> noOfGroups = new ArrayList<>();
            for (int i = 1; i <= 25; i++) {
                noOfGroups.add(i);
            }
            model.addAttribute("numberOfGroups", noOfGroups);
            model.addAttribute("commandResendRate", TimeIntervals.getCommandResendRate());
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore, HowToStopControl.TimeIn, HowToStopControl.RampOutTimeIn, HowToStopControl.RampOutRestore));
            model.addAttribute("stopOrder", List.of(StopOrder.RANDOM, StopOrder.FIRSTINFIRSTOUT));
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("groupSelectionMethod", List.of(GroupSelectionMethod.LastControlled, GroupSelectionMethod.AlwaysFirstGroup, GroupSelectionMethod.LeastControlTime));
            break;
        case EcobeeCycle:
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore));
            break;
        case EcobeeSetpoint:
            model.addAttribute("temperatureModes", Lists.newArrayList(Mode.values()));
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore));
            break;
        case EcobeePlus:
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore));
            break;
        case HoneywellCycle:
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore));
            model.addAttribute("cyclePeriod", List.of(30));
            break;
        case HoneywellSetpoint:
            model.addAttribute("temperatureModes", Lists.newArrayList(Mode.values()));
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore));
            break;
        case ItronCycle:
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("cycleType", ItronCycleType.values());
            model.addAttribute("dutyCyclePeriod", ImmutableList.of(15, 30, 60));
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore));
            break;
        case NestStandardCycle:
            model.addAttribute("preparationLoadShaping", PrepLoadShape.values());
            model.addAttribute("peakLoadShaping", PeakLoadShape.values());
            model.addAttribute("postPeakLoadShaping", PostLoadShape.values());
            break;
        case ThermostatRamping:
            model.addAttribute("units", Lists.newArrayList(TemperatureMeasureUnit.values()));
            model.addAttribute("setpoints", Lists.newArrayList(Setpoint.values()));
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("howtoStopControlFields", Lists.newArrayList(HowToStopControl.TimeIn, HowToStopControl.Restore));
            break;
        case SimpleThermostatRamping:
            model.addAttribute("temperatureModes", Lists.newArrayList(Mode.values()));
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("howtoStopControlFields", Lists.newArrayList(HowToStopControl.TimeIn, HowToStopControl.Restore));
            break;
        case SepTemperatureOffset:
            model.addAttribute("temperatureModes", Lists.newArrayList(Mode.values()));
            model.addAttribute("units", Lists.newArrayList(TemperatureMeasureUnit.values()));
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
            model.addAttribute("stopControlOptions", List.of(HowToStopControl.TimeIn,HowToStopControl.Restore));
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
        case EatonCloudCycle:
            model.addAttribute("whenToChangeFields", WhenToChange.values());
            model.addAttribute("cycleType", EatonCloudCycleType.values());
            model.addAttribute("dutyCyclePeriod", ImmutableList.of(15, 30, 60));
            model.addAttribute("howToStopControl", List.of(HowToStopControl.Restore));
            break;
        }
    }

}
