package com.cannontech.web.dr;


import java.beans.PropertyEditor;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.dr.loadgroup.filter.LoadGroupsForProgramFilter;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.user.YukonUserContext;

@Controller
public class ProgramController {
    private ControlAreaService controlAreaService = null;
    private ScenarioDao scenarioDao = null;
    private ProgramService programService = null;
    private PaoAuthorizationService paoAuthorizationService;
    private ProgramControllerHelper programControllerHelper;
    private LoadGroupControllerHelper loadGroupControllerHelper;
    private RolePropertyDao rolePropertyDao;
    private DatePropertyEditorFactory datePropertyEditorFactory;

    public static class StartProgramBackingBean {
        private int programId;
        private int gearNumber;
        // only used for target cycle gears
        private String gearAdjustments;
        private boolean startNow;
        private Date startDate;
        private boolean scheduleStop;
        private Date stopDate;
        private boolean autoObserveConstraints;

        public int getProgramId() {
            return programId;
        }

        public void setProgramId(int programId) {
            this.programId = programId;
        }

        public int getGearNumber() {
            return gearNumber;
        }

        public void setGearNumber(int gearNumber) {
            this.gearNumber = gearNumber;
        }

        public String getGearAdjustments() {
            return gearAdjustments;
        }

        public void setGearAdjustments(String gearAdjustments) {
            this.gearAdjustments = gearAdjustments;
        }

        public boolean isStartNow() {
            return startNow;
        }

        public void setStartNow(boolean startNow) {
            this.startNow = startNow;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public boolean isScheduleStop() {
            return scheduleStop;
        }

        public void setScheduleStop(boolean scheduleStop) {
            this.scheduleStop = scheduleStop;
        }

        public Date getStopDate() {
            return stopDate;
        }

        public void setStopDate(Date stopDate) {
            this.stopDate = stopDate;
        }

        public boolean isAutoObserveConstraints() {
            return autoObserveConstraints;
        }

        public void setAutoObserveConstraints(boolean autoObserveConstraints) {
            this.autoObserveConstraints = autoObserveConstraints;
        }
    }

    @RequestMapping("/program/list")
    public String list(ModelMap modelMap, YukonUserContext userContext,
            @ModelAttribute("backingBean") ProgramControllerHelper.ProgramListBackingBean backingBean,
            BindingResult result, SessionStatus status) {

        programControllerHelper.filterPrograms(modelMap, userContext, backingBean,
                                               result, status, null);

        return "dr/program/list.jsp";
    }

    @RequestMapping("/program/detail")
    public String detail(int programId, ModelMap modelMap,
            YukonUserContext userContext,
            @ModelAttribute("backingBean") LoadGroupControllerHelper.LoadGroupListBackingBean backingBean,
            BindingResult result, SessionStatus status) {
        
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE);
        
        modelMap.addAttribute("program", program);

        UiFilter<DisplayablePao> detailFilter = new LoadGroupsForProgramFilter(programId);
        loadGroupControllerHelper.filterGroups(modelMap, userContext, backingBean,
                                               result, status, detailFilter);

        DisplayablePao parentControlArea =
            controlAreaService.findControlAreaForProgram(userContext, programId);
        modelMap.addAttribute("parentControlArea", parentControlArea);
        List<DisplayablePao> parentScenarios = scenarioDao.findScenariosForProgram(programId);
        modelMap.addAttribute("parentScenarios", parentScenarios);
        return "dr/program/detail.jsp";
    }

    /**
     * Page one of the "start program" saga.
     */
    @RequestMapping("/program/startProgramDetails")
    public String startProgramDetails(
            @ModelAttribute("backingBean") StartProgramBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {
        LiteYukonUser user = userContext.getYukonUser();

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);
        
        backingBean.setGearNumber(1);
        backingBean.setStartNow(true);
        backingBean.setStartDate(new Date());
        backingBean.setScheduleStop(true);
        backingBean.setStopDate(new DateTime(DateTimeZone.forTimeZone(userContext.getTimeZone())).plusHours(4).toDate());

        modelMap.addAttribute("program", program);

        boolean autoObserveConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS, user);
        boolean checkConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS, user);

        modelMap.addAttribute("autoObserveConstraintsAllowed", autoObserveConstraintsAllowed);
        modelMap.addAttribute("checkConstraintsAllowed", checkConstraintsAllowed);

        if (checkConstraintsAllowed && autoObserveConstraintsAllowed) {
            // It might be more sane to change the "DEFAULT_CONSTRAINT_SELECTION"
            // role property to something more like "AUTO_OBSERVE_CONSTRAINTS_BY_DEFAULT".
            String defaultConstraint =
                rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DEFAULT_CONSTRAINT_SELECTION, user);
            backingBean.setAutoObserveConstraints(defaultConstraint.equalsIgnoreCase(LMManualControlRequest.CONSTRAINT_FLAG_STRS[LMManualControlRequest.CONSTRAINTS_FLAG_USE]));
        }

        // TODO:  I don't know how to create programs that aren't LMProgramDirect
        // programs so I don't know how to test them.
        List<LMProgramDirectGear> gears = Collections.emptyList();
        LMProgramBase programBase = programService.map(program);
        if (programBase instanceof IGearProgram) {
            gears = ((IGearProgram) programBase).getDirectGearVector();
        }
        modelMap.addAttribute("gears", gears);

        return "dr/program/startProgramDetails.jsp";
    }

    @RequestMapping("/program/startProgramConstraints")
    public String startProgramConstraints(
            @ModelAttribute("backingBean") StartProgramBackingBean backingBean,
            ModelMap modelMap, YukonUserContext userContext) {
        LiteYukonUser user = userContext.getYukonUser();

        DisplayablePao program = programService.getProgram(backingBean.getProgramId());
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        modelMap.addAttribute("program", program);

        // If the gear is a target cycle gear, ask for target cycle adjustments.
        // (Maybe we want to make this a role property and/or a checkbox
        // on the first page?)
        boolean notYet = true;  // TODO: finish adjustments
        if (!notYet && StringUtils.isEmpty(backingBean.getGearAdjustments())) {
            LMProgramBase programBase = programService.map(program);
            if (programBase instanceof IGearProgram) {
                LMProgramDirectGear gear =
                    ((IGearProgram) programBase).getDirectGearVector().get(backingBean.getGearNumber() - 1);
                if (gear.isTargetCycle()) {
                    modelMap.addAttribute("gear", gear);
                    // TODO:  populate default gear adjustments...
                    // something like ...
                    // backingBean.setGearAdjustments("adjustments 100 100 100");
                    return "dr/program/startProgramGearAdjustments.jsp";
                }
            }
        }

        boolean autoObserveConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OBSERVE_CONSTRAINTS, user);
        boolean checkConstraintsAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CHECK_CONSTRAINTS, user);
        boolean overrideAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT, user);

        if (autoObserveConstraintsAllowed && backingBean.isAutoObserveConstraints()) {
            return startProgram(backingBean, false, modelMap, userContext);
        }

        if (!checkConstraintsAllowed) {
            // they're not allowed to do anything...they got here by hacking
            // (or a bug)
            return closeDialog(modelMap);
        }

        modelMap.addAttribute("overrideAllowed", overrideAllowed);
        ConstraintViolations violations =
            programService.getConstraintViolationForStartProgram(userContext,
                                                                 backingBean.getProgramId(),
                                                                 backingBean.getGearNumber(),
                                                                 backingBean.getStartDate(),
                                                                 backingBean.getStopDate());
        modelMap.addAttribute("violations", violations);

        return "dr/program/startProgramConstraints.jsp";
    }

    @RequestMapping("/program/startProgram")
    public String startProgram(
            @ModelAttribute("backingBean") StartProgramBackingBean backingBean,
            Boolean overrideConstraints,
            ModelMap modelMap, YukonUserContext userContext) {
        LiteYukonUser user = userContext.getYukonUser();

        int programId = backingBean.getProgramId();
        DisplayablePao program = programService.getProgram(programId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     program, 
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);
        
        // TODO:  if override, make sure override is allowed...
        boolean overrideAllowed =
            rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_OVERRIDE_CONSTRAINT, user);

        programService.startProgram(userContext, programId,
                                    backingBean.getGearNumber(),
                                    backingBean.getStartDate(),
                                    backingBean.getStopDate(),
                                    overrideConstraints);

        return closeDialog(modelMap);
    }

    private String closeDialog(ModelMap modelMap) {
        modelMap.addAttribute("popupId", "drDialog");
        return "common/closePopup.jsp";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        PropertyEditor fullDateTimeEditor =
            datePropertyEditorFactory.getPropertyEditor(DateFormatEnum.DATEHM, userContext);
        binder.registerCustomEditor(Date.class, fullDateTimeEditor);
        programControllerHelper.initBinder(binder, userContext);
        loadGroupControllerHelper.initBinder(binder, userContext);
    }

    @Autowired
    public void setControlAreaService(ControlAreaService controlAreaService) {
        this.controlAreaService = controlAreaService;
    }

    @Autowired
    public void setScenarioDao(ScenarioDao scenarioDao) {
        this.scenarioDao = scenarioDao;
    }

    @Autowired
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }

    @Autowired
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }

    @Autowired
    public void setProgramControllerHelper(
            ProgramControllerHelper programControllerHelper) {
        this.programControllerHelper = programControllerHelper;
    }

    @Autowired
    public void setLoadGroupControllerHelper(
            LoadGroupControllerHelper loadGroupControllerHelper) {
        this.loadGroupControllerHelper = loadGroupControllerHelper;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setDatePropertyEditorFactory(
            DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
}
