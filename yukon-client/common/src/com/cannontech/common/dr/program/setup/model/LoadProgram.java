package com.cannontech.common.dr.program.setup.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.common.dr.gear.setup.OperationalState;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.database.data.device.lm.LMProgramDirectBase;
import com.cannontech.database.db.device.lm.LMDirectNotificationGroupList;
import com.cannontech.database.db.device.lm.LMProgramControlWindow;
import com.cannontech.database.db.device.lm.LMProgramDirect;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value = { "programId" }, allowGetters = true, ignoreUnknown = true)
public class LoadProgram implements DBPersistentConverter<LMProgramBase> {

    private Integer programId;
    private String name;

    private PaoType type;
    private OperationalState operationalState;
    private ProgramConstraint constraint;

    private Double triggerOffset;
    private Double restoreOffset;

    private List<ProgramGear> gears;
    private ProgramControlWindow controlWindow;
    private List<ProgramGroup> assignedGroups;
    private Notification notification;

    private List<ProgramDirectMemberControl> memberControl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    public Double getTriggerOffset() {
        return triggerOffset;
    }

    public void setTriggerOffset(Double triggerOffset) {
        if (triggerOffset != null) {
            this.triggerOffset = new BigDecimal(triggerOffset).setScale(4, RoundingMode.HALF_DOWN).doubleValue();
        } else {
            this.triggerOffset = triggerOffset;
        }
    }

    public Double getRestoreOffset() {
        return restoreOffset;
    }

    public void setRestoreOffset(Double restoreOffset) {
        if (restoreOffset != null) {
            this.restoreOffset = new BigDecimal(restoreOffset).setScale(4, RoundingMode.HALF_DOWN).doubleValue();
        } else {
            this.restoreOffset = restoreOffset;
        }
    }

    public OperationalState getOperationalState() {
        return operationalState;
    }

    public void setOperationalState(OperationalState operationalState) {
        this.operationalState = operationalState;
    }

    public List<ProgramGear> getGears() {
        return gears;
    }

    public void setGears(List<ProgramGear> gears) {
        this.gears = gears;
    }

    public ProgramControlWindow getControlWindow() {
        return controlWindow;
    }

    public void setControlWindow(ProgramControlWindow controlWindow) {
        this.controlWindow = controlWindow;
    }

    public ProgramConstraint getConstraint() {
        return constraint;
    }

    public void setConstraint(ProgramConstraint constraint) {
        this.constraint = constraint;
    }

    public List<ProgramDirectMemberControl> getMemberControl() {
        return memberControl;
    }

    public void setMemberControl(List<ProgramDirectMemberControl> memberControl) {
        this.memberControl = memberControl;
    }

    public List<ProgramGroup> getAssignedGroups() {
        return assignedGroups;
    }

    public void setAssignedGroups(List<ProgramGroup> assignedGroups) {
        this.assignedGroups = assignedGroups;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public void buildModel(LMProgramBase program) {

        setProgramId(program.getPAObjectID());
        setType(program.getPaoType());
        setName(program.getPAOName());
        setOperationalState(OperationalState.valueOf(program.getProgram().getControlType()));

        // Set Constraint Id
        ProgramConstraint programConstraint = new ProgramConstraint();
        programConstraint.setConstraintId(program.getProgram().getConstraintID());
        setConstraint(programConstraint);

        // Set ProgramGroup Ids
        List<ProgramGroup> loadGroups = new ArrayList<>();
        program.getLmProgramStorageVector().forEach(group -> {
            LMProgramDirectGroup directGroup = (LMProgramDirectGroup) group;
            ProgramGroup loadGroup = new ProgramGroup();
            loadGroup.buildModel(directGroup);
            loadGroups.add(loadGroup);
        });
        setAssignedGroups(loadGroups);

        // Set Control windows
        ProgramControlWindow programControlWindow = new ProgramControlWindow();
        program.getLmProgramControlWindowVector().forEach(window -> {
            if (window.getWindowNumber() == 1) {
                ProgramControlWindowFields controlWindowFields = new ProgramControlWindowFields();
                controlWindowFields.buildModel(window);
                programControlWindow.setControlWindowOne(controlWindowFields);
            } else if (window.getWindowNumber() == 2) {
                ProgramControlWindowFields controlWindowFields = new ProgramControlWindowFields();
                controlWindowFields.buildModel(window);
                programControlWindow.setControlWindowTwo(controlWindowFields);
            }
        });
        setControlWindow(programControlWindow);

        if (program instanceof LMProgramDirectBase) {
            LMProgramDirectBase lmProgramDirectBase = (LMProgramDirectBase) program;

            setTriggerOffset(lmProgramDirectBase.getDirectProgram().getTriggerOffset());
            setRestoreOffset(lmProgramDirectBase.getDirectProgram().getRestoreOffset());

            // Set notification
            buildNotificationModel(lmProgramDirectBase);

            // Set gears
            List<ProgramGear> gears = new ArrayList<>();
            lmProgramDirectBase.getLmProgramDirectGearVector().forEach(gear -> {

                ProgramGear programGear = new ProgramGear();
                programGear.buildModel(gear);
                gears.add(programGear);
            });
            setGears(gears);
        }
    }

    private void buildNotificationModel(LMProgramDirectBase lmProgramDirectBase) {
        Notification notification = new Notification();

        List<NotificationGroup> notificationGroupList = new ArrayList<>();
        lmProgramDirectBase.getLmProgramDirectNotifyGroupVector().forEach(notificationGroup -> {
            NotificationGroup group = new NotificationGroup();
            group.buildModel(notificationGroup);
            notificationGroupList.add(group);
        });

        if (CollectionUtils.isNotEmpty(notificationGroupList)) {
            notification.setAssignedNotificationGroups(notificationGroupList);
        }
        Integer numStart = lmProgramDirectBase.getDirectProgram().getNotifyActiveOffset();
        Integer numStop = lmProgramDirectBase.getDirectProgram().getNotifyInactiveOffset();

        if (numStart.intValue() != -1) {
            notification.setProgramStartInMinutes(lmProgramDirectBase.getDirectProgram().getNotifyActiveOffset() / 60);
        }
        if (numStop.intValue() != -1) {
            notification.setProgramStopInMinutes(lmProgramDirectBase.getDirectProgram().getNotifyInactiveOffset() / 60);
        }
        boolean isNotifyAdjust = lmProgramDirectBase.getDirectProgram().getNotifyAdjust() == LMProgramDirect.NOTIFY_ADJUST_ENABLED
                .intValue();
        if (isNotifyAdjust) {
            notification.setNotifyOnAdjust(true);
        } else {
            notification.setNotifyOnAdjust(false);
        }
        boolean isNotifyWhenScheduled = lmProgramDirectBase.getDirectProgram()
                .shouldNotifyWhenScheduled() == LMProgramDirect.NOTIFY_SCHEDULE_ENABLED.intValue();
        if (isNotifyWhenScheduled) {
            notification.setEnableOnSchedule(true);
        } else {
            notification.setEnableOnSchedule(false);
        }

        if (CollectionUtils.isNotEmpty(notificationGroupList) || numStart.intValue() != -1 || numStop.intValue() != -1
                || isNotifyAdjust || isNotifyWhenScheduled) {
            setNotification(notification);
        }
    }

    @Override
    public void buildDBPersistent(LMProgramBase lmProgramBase) {

        lmProgramBase.setName(getName());
        lmProgramBase.getProgram().setControlType(getOperationalState().name());

        ProgramConstraint constraint = getConstraint();
        if (constraint.getConstraintId() != null) {
            lmProgramBase.getProgram().setConstraintID(constraint.getConstraintId());
        }

        if (CollectionUtils.isNotEmpty(lmProgramBase.getLmProgramControlWindowVector())) {
            lmProgramBase.getLmProgramControlWindowVector().clear();
        }

        // Control Window
        if (getControlWindow() != null) {
            if (getControlWindow().getControlWindowOne() != null
                    && getControlWindow().getControlWindowOne().getAvailableStartTimeInMinutes() != null
                    && getControlWindow().getControlWindowOne().getAvailableStopTimeInMinutes() != null) {
                addControlWindowFields(lmProgramBase, 1, getControlWindow().getControlWindowOne());
            }
            if (getControlWindow().getControlWindowTwo() != null
                    && getControlWindow().getControlWindowTwo().getAvailableStartTimeInMinutes() != null
                    && getControlWindow().getControlWindowTwo().getAvailableStopTimeInMinutes() != null) {
                addControlWindowFields(lmProgramBase, 2, getControlWindow().getControlWindowTwo());
            }
        }
        // Build Groups DB Persistent object
        if (CollectionUtils.isNotEmpty(lmProgramBase.getLmProgramStorageVector())) {
            lmProgramBase.getLmProgramStorageVector().clear();
        }
        if (getAssignedGroups() != null) {
            int groupsListSize = getAssignedGroups().size();
            getAssignedGroups().forEach(grp -> {
                LMProgramDirectGroup group = new LMProgramDirectGroup();
                group.setDeviceID(getProgramId());
                if (groupsListSize == 1) {
                    group.setGroupOrder(1);
                } else {
                    group.setGroupOrder(grp.getGroupOrder());
                }
                grp.buildDBPersistent(group);
                lmProgramBase.getLmProgramStorageVector().addElement(group);
            });
        }

        // trigger Offset
        if (lmProgramBase instanceof LMProgramDirectBase) {
            LMProgramDirectBase prog = (LMProgramDirectBase) lmProgramBase;
            if (getTriggerOffset() != null) {
                prog.getDirectProgram().setTriggerOffset(getTriggerOffset());
            }
            if (getRestoreOffset() != null) {
                prog.getDirectProgram().setRestoreOffset(getRestoreOffset());
            }
            // notification object
            buildNotificationDBPersistent(prog);
        }
    }

    private void addControlWindowFields(LMProgramBase lmProgramBase, int controlWindowNumber,
            ProgramControlWindowFields controlWindowField) {
        LMProgramControlWindow controlWindow = new LMProgramControlWindow();
        controlWindowField.buildDBPersistent(controlWindow);
        controlWindow.setWindowNumber(controlWindowNumber);
        controlWindow.setDeviceID(getProgramId());
        lmProgramBase.getLmProgramControlWindowVector().add(controlWindow);
    }

    /**
     * Build DB Persistent for Notification
     */
    private void buildNotificationDBPersistent(LMProgramDirectBase prog) {
        if (CollectionUtils.isNotEmpty(prog.getLmProgramDirectNotifyGroupVector())) {
            prog.getLmProgramDirectNotifyGroupVector().clear();
        }
        if (getNotification() != null) {

            if (getNotification().getAssignedNotificationGroups() != null) {
                getNotification().getAssignedNotificationGroups().forEach(notificationGroup -> {

                    LMDirectNotificationGroupList group = new LMDirectNotificationGroupList();
                    notificationGroup.buildDBPersistent(group);
                    group.setDeviceID(getProgramId());
                    prog.getLmProgramDirectNotifyGroupVector().addElement(group);
                });
            }
            if (getNotification().getProgramStartInMinutes() != null) {
                Integer programStart = getNotification().getProgramStartInMinutes();
                prog.getDirectProgram().setNotifyActiveOffset(programStart * 60);
            } else {
                prog.getDirectProgram().setNotifyActiveOffset(-1);
            }

            if (getNotification().getProgramStopInMinutes() != null) {
                Integer programStop = getNotification().getProgramStopInMinutes();
                prog.getDirectProgram().setNotifyInactiveOffset(programStop * 60);
            } else {
                prog.getDirectProgram().setNotifyInactiveOffset(-1);
            }

            Boolean notifyOnAdjust = getNotification().getNotifyOnAdjust();
            if (notifyOnAdjust != null && notifyOnAdjust) {
                prog.getDirectProgram().setNotifyAdjust(LMProgramDirect.NOTIFY_ADJUST_ENABLED);
            } else {
                prog.getDirectProgram().setNotifyAdjust(LMProgramDirect.NOTIFY_ADJUST_DISABLED);
            }

            Boolean enableOnSchedule = getNotification().getEnableOnSchedule();
            if (enableOnSchedule != null && enableOnSchedule) {
                prog.getDirectProgram().setEnableSchedule(LMProgramDirect.NOTIFY_SCHEDULE_ENABLED);
            } else {
                prog.getDirectProgram().setEnableSchedule(LMProgramDirect.NOTIFY_SCHEDULE_DISABLED);
            }
        }
    }

}
