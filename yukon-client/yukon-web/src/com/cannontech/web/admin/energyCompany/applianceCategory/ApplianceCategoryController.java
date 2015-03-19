package com.cannontech.web.admin.energyCompany.applianceCategory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.Move;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramNameFilter;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramRowMapper.SortBy;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.ApplianceCategoryIcon;
import com.cannontech.stars.dr.appliance.model.ApplianceTypeEnum;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.appliance.model.ControlPercentIcon;
import com.cannontech.stars.dr.appliance.model.EnvironmentIcon;
import com.cannontech.stars.dr.appliance.model.SavingsIcon;
import com.cannontech.stars.dr.appliance.service.ApplianceCategoryService;
import com.cannontech.stars.dr.appliance.service.AssignedProgramService;
import com.cannontech.stars.dr.hardware.dao.ProgramToAlternateProgramDao;
import com.cannontech.stars.dr.hardware.model.ProgramToAlternateProgram;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/energyCompany/applianceCategory/*")
public class ApplianceCategoryController {
    
    private final static String baseKey = "yukon.web.modules.adminSetup.applianceCategory";
    
    @Autowired private AssignedProgramDao assignedProgramDao;
    @Autowired private AssignedProgramService assignedProgramService;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private ApplianceCategoryService applianceCategoryService;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private StarsDatabaseCache cache;
    @Autowired private PaoDao paoDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnergyCompanyService ecService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private ProgramToAlternateProgramDao ptapDao;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private final Validator detailsValidator = new SimpleValidator<ApplianceCategory>(ApplianceCategory.class) {
        @Override
        public void doValidation(ApplianceCategory target, Errors errors) {
            String[] requiredFields = new String[] { "name", "displayName" };
            for (String requiredField : requiredFields) {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                                                          requiredField,
                                                          baseKey + ".EDIT.empty");
            }
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", target.getName(), 40);
            YukonValidationUtils.checkExceedsMaxLength(errors, "displayName", target.getDisplayName(), 100);
            YukonValidationUtils.checkExceedsMaxLength(errors, "icon", target.getIcon(), 100);
            YukonValidationUtils.checkExceedsMaxLength(errors, "description", target.getDescription(), 500);
        }
    };
    
    private final Validator assignedProgramValidator = new SimpleValidator<AssignProgramBackingBean>(AssignProgramBackingBean.class) {
        @Override
        public void doValidation(AssignProgramBackingBean assignedProgram, Errors errors) {
            if (assignedProgram.isVirtual()) {
                // display name is required for virtual programs
                ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                    "assignedProgram.displayName",
                    baseKey + ".editAssignedProgram.empty");
            }
            
            AssignedProgram ap = assignedProgram.getAssignedProgram();
            if (!assignedProgram.isMultiple()) {
                String alternateDisplayName = ap.getWebConfiguration().getAlternateDisplayName();
                String displayName = ap.getDisplayName();
                if (displayName.indexOf(',') != -1 && displayName.indexOf('"') != -1) {
                    errors.rejectValue("assignedProgram.displayName", baseKey + ".editAssignedProgram.commaAndQuotes");
                }
                String shortName = ap.getShortName();
                if (shortName.indexOf(',') != -1 && shortName.indexOf('"') != -1) {
                    errors.rejectValue("assignedProgram.shortName", baseKey + ".editAssignedProgram.commaAndQuotes");
                }
                if (alternateDisplayName.length() > 99) {
                    errors.reject(baseKey + ".editAssignedProgram.namesTooLong");
                }
            }
            YukonValidationUtils.checkExceedsMaxLength(errors, "assignedProgram.description",
                                                       ap.getDescription(), 500);
            if (ap.getWebConfiguration().getLogoLocation().length() > 100) {
                errors.reject(baseKey + ".editAssignedProgram.iconNamesTooLong");
            }
            if (ap.getSavingsIcon().indexOf(',') != -1) {
                errors.rejectValue("assignedProgram.savingsIcon", ".editAssignedProgram.noCommas");
            }
            if (ap.getControlPercentIcon().indexOf(',') != -1) {
                errors.rejectValue("assignedProgram.controlPercentIcon", ".editAssignedProgram.noCommas");
            }
            if (ap.getEnvironmentIcon().indexOf(',') != -1) {
                errors.rejectValue("assignedProgram.environmentIcon", ".editAssignedProgram.noCommas");
            }
        }
    };
    
    @RequestMapping("list")
    public String list(ModelMap model, YukonUserContext userContext, EnergyCompanyInfoFragment ecInfo) {
        
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        ecService.verifyViewPageAccess(userContext.getYukonUser(), ecId);
        
        List<ApplianceCategory> categories = applianceCategoryDao.getApplianceCategoriesByEcId(ecId);
        model.addAttribute("applianceCategories", categories);
        
        return "applianceCategory/list.jsp";
    }
    
    @RequestMapping("view")
    public String view(ModelMap model, 
                       int applianceCategoryId, 
                       YukonUserContext userContext,
                       EnergyCompanyInfoFragment ecInfo) {
        
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        ecService.verifyViewPageAccess(userContext.getYukonUser(), ecId);
        
        ApplianceCategory category = applianceCategoryDao.getById(applianceCategoryId);
        
        // default the filtering and sorting options 
        UiFilter<AssignedProgram> filter = UiFilterList.wrap(new ArrayList<UiFilter<AssignedProgram>>());
        
        SortBy sortBy = category.isConsumerSelectable() ? SortBy.PROGRAM_ORDER : SortBy.PROGRAM_NAME;
        
        SortingParameters sorting = SortingParameters.of(sortBy.name(), Direction.asc);
        
        model.addAttribute("assignedPrograms", assignedProgramService.filter(applianceCategoryId, filter, sorting, null));
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        String programName = accessor.getMessage(baseKey + ".programName");
        SortableColumn nameColumn = SortableColumn.of(sorting, programName, SortBy.PROGRAM_NAME.name());
        model.addAttribute("nameColumn", nameColumn);
        
        String displayOrder = accessor.getMessage(baseKey + ".displayOrder");
        SortableColumn orderColumn = SortableColumn.of(sorting, displayOrder, SortBy.PROGRAM_ORDER.name());
        model.addAttribute("orderColumn", orderColumn);
        
        return edit(PageEditMode.VIEW, model, category, userContext, ecId);
    }
    
    @RequestMapping("programs-list")
    public String programsList(ModelMap model,
            YukonUserContext userContext,
            int applianceCategoryId, 
            int ecId, 
            SortingParameters sorting,
            String filterBy) {
        
        // filter by name if provided
        ArrayList<UiFilter<AssignedProgram>> filters = new ArrayList<UiFilter<AssignedProgram>>();
        if (!StringUtils.isBlank(filterBy)) {
            filters.add(new AssignedProgramNameFilter(filterBy));
        }
        UiFilter<AssignedProgram> filter = UiFilterList.wrap(filters);
        
        ApplianceCategory category = applianceCategoryDao.getById(applianceCategoryId);
        
        if (sorting == null) {
            if (category.isConsumerSelectable()) {
                sorting = SortingParameters.of(SortBy.PROGRAM_ORDER.name(), Direction.asc);
            } else {
                sorting = SortingParameters.of(SortBy.PROGRAM_NAME.name(), Direction.asc);
            }
        }
        
        SearchResults<AssignedProgram> assignedPrograms =
            assignedProgramService.filter(category.getApplianceCategoryId(), filter, sorting, null);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        String programName = accessor.getMessage(baseKey + ".programName");
        SortableColumn nameColumn = SortableColumn.of(sorting, programName, SortBy.PROGRAM_NAME.name());
        
        String displayOrder = accessor.getMessage(baseKey + ".displayOrder");
        SortableColumn orderColumn = SortableColumn.of(sorting, displayOrder, SortBy.PROGRAM_ORDER.name());
        
        model.addAttribute("applianceCategory", category);
        model.addAttribute("isEditable", category.getEnergyCompanyId() == ecId);
        model.addAttribute("assignedPrograms", assignedPrograms);
        model.addAttribute("nameColumn", nameColumn);
        model.addAttribute("orderColumn", orderColumn);
        model.addAttribute("filterBy", filterBy);
        
        return "applianceCategory/ac.programs.list.jsp";
    }
    
    @RequestMapping("create")
    public String create(ModelMap model, YukonUserContext userContext, EnergyCompanyInfoFragment ecInfo) {
        
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        ecService.verifyEditPageAccess(userContext.getYukonUser(), ecId);

        ApplianceCategory applianceCategory = new ApplianceCategory();
        applianceCategory.setEnergyCompanyId(ecId);
        return edit(PageEditMode.CREATE, model, applianceCategory, userContext, ecInfo.getEnergyCompanyId());
    }

    @RequestMapping("edit")
    public String edit(ModelMap model, 
                       @ModelAttribute("backingBean") ListBackingBean backingBean,
                       int applianceCategoryId, 
                       YukonUserContext userContext,
                       EnergyCompanyInfoFragment ecInfo) {
        
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        ecService.verifyEditPageAccess(userContext.getYukonUser(), ecId);

        ApplianceCategory applianceCategory = applianceCategoryDao.getById(applianceCategoryId);
        return edit(PageEditMode.EDIT, model, applianceCategory, userContext, ecId);
    }

    private String edit(PageEditMode mode, ModelMap model, ApplianceCategory applianceCategory,
                        YukonUserContext userContext, int ecId) {
        
        model.addAttribute("applianceCategory", applianceCategory);
        model.addAttribute("mode", mode);
        
        boolean isEditable = applianceCategory.getEnergyCompanyId() == ecId;
        if (!isEditable && mode == PageEditMode.EDIT) {
            throw new NotAuthorizedException("Cannot edit inherited appliance categories");
        }
        model.addAttribute("isEditable", isEditable);

        if (mode == PageEditMode.EDIT || mode == PageEditMode.CREATE) {
            List<ApplianceTypeEnum> applianceTypes =
                objectFormattingService.sortDisplayableValues(ApplianceTypeEnum.values(),
                                                       ApplianceTypeEnum.DEFAULT, null, userContext);
            model.addAttribute("applianceTypes", applianceTypes);

            List<ApplianceCategoryIcon> icons =
                objectFormattingService.sortDisplayableValues(ApplianceCategoryIcon.values(),
                                                       ApplianceCategoryIcon.NONE,
                                                       ApplianceCategoryIcon.OTHER, userContext);
            model.addAttribute("icons", icons);
        }
        model.addAttribute("canAddVirtual", configurationSource.getBoolean(MasterConfigBooleanKeysEnum.VIRTUAL_PROGRAMS));
        
        return "applianceCategory/edit.jsp";
    }

    @RequestMapping(value="save", params="save", method=RequestMethod.POST)
    public String save(ModelMap model, 
                       @ModelAttribute ApplianceCategory applianceCategory,
                       BindingResult bindingResult, 
                       YukonUserContext userContext,
                       FlashScope flashScope) {
        
        int ecId = applianceCategory.getEnergyCompanyId();
        ecService.verifyEditPageAccess(userContext.getYukonUser(), ecId);
        int applianceCategoryId = applianceCategory.getApplianceCategoryId();
        if (applianceCategoryId != 0) {
            verifyApplianceCategoryEC(applianceCategoryId, ecId);
        }

        detailsValidator.validate(applianceCategory, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return edit(PageEditMode.EDIT, model, applianceCategory, userContext, ecId);
        }

        applianceCategoryService.save(applianceCategory, userContext);

        return "redirect:view?ecId=" + ecId + "&applianceCategoryId=" + applianceCategory.getApplianceCategoryId();
    }

    @RequestMapping(value="save", params="delete", method=RequestMethod.POST)
    public String delete(@ModelAttribute ApplianceCategory applianceCategory, YukonUserContext userContext) {
        
        int ecId = applianceCategory.getEnergyCompanyId();
        ecService.verifyEditPageAccess(userContext.getYukonUser(), ecId);
        applianceCategoryService.delete(applianceCategory.getApplianceCategoryId(), userContext);

        return "redirect:list?ecId=" + ecId;
    }

    @RequestMapping("assignProgram")
    public String assignProgram(HttpServletRequest request, ModelMap model, 
                                int ecId, 
                                int applianceCategoryId,
                                Integer[] programsToAssign, 
                                YukonUserContext userContext) {
        
        ecService.verifyEditPageAccess(userContext.getYukonUser(), ecId);
        
        AssignedProgram program = new AssignedProgram();
        boolean multiple = true;
        if (programsToAssign.length == 0) {
            throw new RuntimeException("need at least one program to assign");
        } else if (programsToAssign.length == 1) {
            int programId = programsToAssign[0];
            program.setProgramId(programId);
            program.setProgramName(paoDao.getYukonPAOName(programId));
            programsToAssign = null;
            multiple = false;
        }
        AssignProgramBackingBean backingBean = new AssignProgramBackingBean(false, multiple, programsToAssign, program);
        
        return editAssignedProgram(model, ecId, applianceCategoryId, backingBean, userContext, PageEditMode.CREATE);
    }

    @RequestMapping("createVirtualProgram")
    public String createVirtualProgram(ModelMap model, int ecId, int applianceCategoryId,
            YukonUserContext userContext) {
        AssignProgramBackingBean backingBean =
            new AssignProgramBackingBean(true, false, null, new AssignedProgram());
        return editAssignedProgram(model, ecId, applianceCategoryId, backingBean,
                                   userContext, PageEditMode.CREATE);
    }

    @RequestMapping("editAssignedProgram")
    public String editAssignedProgram(ModelMap model, 
                                      int ecId, 
                                      @RequestParam("applianceCategoryId") int acId, 
                                      int assignedProgramId, 
                                      YukonUserContext userContext) {
        
        verifyAssignedProgramAC(assignedProgramId, acId);
        AssignedProgram program = assignedProgramDao.getById(assignedProgramId);
        boolean virtual = program.getProgramId() == 0;
        AssignProgramBackingBean bean = new AssignProgramBackingBean(virtual, false, null, program);
        
        return editAssignedProgram(model, ecId, acId, bean, userContext, PageEditMode.EDIT);
    }

    private String editAssignedProgram(ModelMap model, int ecId, int acId, AssignProgramBackingBean bean, 
                                       YukonUserContext userContext, PageEditMode mode) {
        
        model.addAttribute("energycompanyId", ecId);
        
        ApplianceCategory applianceCategory = applianceCategoryDao.getById(acId);
        model.addAttribute("applianceCategory", applianceCategory);

        bean.getAssignedProgram().setApplianceCategoryId(acId);
        model.addAttribute("backingBean", bean);

        YukonEnergyCompany yec = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
        LiteStarsEnergyCompany lsec = cache.getEnergyCompany(yec);
        StarsCustSelectionList chanceOfControlList = lsec.getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL);
        List<ChanceOfControl> chanceOfControls = Lists.newArrayList();

        String chanceOfControl = "Not Specified";
        chanceOfControls.add(0, new ChanceOfControl(0, chanceOfControl));
        for (StarsSelectionListEntry cocEntry : chanceOfControlList.getStarsSelectionListEntry()) {
            chanceOfControls.add(new ChanceOfControl(cocEntry.getEntryID(), cocEntry.getContent()));
            if (cocEntry.getEntryID() == bean.getAssignedProgram().getChanceOfControlId()) {
                chanceOfControl = cocEntry.getContent();
            }
        }
        
        model.addAttribute("chanceOfControls", chanceOfControls);
        if (mode == PageEditMode.VIEW) {
            model.addAttribute("chanceOfControl", chanceOfControl);
        }

        SavingsIcon[] savingsIcons = SavingsIcon.values();
        model.addAttribute("savingsIcons", savingsIcons);

        ControlPercentIcon[] controlPercentIcons = ControlPercentIcon.values();
        model.addAttribute("controlPercentIcons", controlPercentIcons);

        EnvironmentIcon[] environmentIcons = EnvironmentIcon.values();
        model.addAttribute("environmentIcons", environmentIcons);

        boolean isEditable = applianceCategory.getEnergyCompanyId() == ecId;
        if (!isEditable && mode == PageEditMode.EDIT) {
            mode = PageEditMode.VIEW;
        }
        model.addAttribute("mode", mode);
        model.addAttribute("isEditable", isEditable);
                
        if (!bean.isMultiple()) {
            boolean showAlternateEnrollment = energyCompanySettingDao.getBoolean(EnergyCompanySettingType.ALTERNATE_PROGRAM_ENROLLMENT, yec.getEnergyCompanyId());
            if (showAlternateEnrollment) {
                /*
                 * Programs that should be excluded from Alternate Enrollment picker
                 * --programs that are alternate on another program
                 * --programs that have an alternate program assigned
                 */
                List<ProgramToAlternateProgram> allMappings = ptapDao.getAll();
                List<Integer> alternateProgramIds = Lists.transform(allMappings, ProgramToAlternateProgram.ALTERNATE_PROGRAM_IDS_FUNCTION);
                // The Alternate Enrollment selection option should not display if the program is an alternate for another program
                if (!alternateProgramIds.contains(bean.getAssignedProgram().getAssignedProgramId())){
                    List<Integer> parentProgramIds = Lists.transform(allMappings, ProgramToAlternateProgram.PARENT_PROGRAM_IDS_FUNCTION);
                    List<Integer> excludedProgramIds = Lists.newArrayList();
                    excludedProgramIds.addAll(alternateProgramIds);
                    excludedProgramIds.addAll(parentProgramIds);
                    excludedProgramIds.add(bean.getAssignedProgram().getAssignedProgramId());
                    if (bean.getAssignedProgram().getAlternateProgramId() != null){
                        excludedProgramIds.removeAll(Collections.singleton(bean.getAssignedProgram().getAlternateProgramId()));
                    }
                    model.addAttribute("showAlternateEnrollment", showAlternateEnrollment);
                    model.addAttribute("excludedProgramIds", excludedProgramIds);
                }
            }
        }
        
        return "applianceCategory/editAssignedProgram.jsp";
    }

    @RequestMapping("saveAssignedProgram")
    public String saveAssignedProgram(HttpServletResponse response, 
            ModelMap model, 
            YukonUserContext userContext,
            int ecId,
            @ModelAttribute("backingBean") AssignProgramBackingBean backingBean,
            BindingResult bindingResult, 
            FlashScope flashScope) throws IOException {
        
        int applianceCategoryId = backingBean.getAssignedProgram().getApplianceCategoryId();
        verifyApplianceCategoryEC(applianceCategoryId, ecId);
        int assignedProgramId = backingBean.getAssignedProgram().getAssignedProgramId();
        if (assignedProgramId > 0) {
            verifyAssignedProgramAC(assignedProgramId, applianceCategoryId);
        }

        AssignedProgram assignedProgram = backingBean.getAssignedProgram();
        assignedProgramValidator.validate(backingBean, bindingResult);
        
        if (bindingResult.hasErrors()) {
            
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setError(messages);

            return editAssignedProgram(model, ecId, assignedProgram.getApplianceCategoryId(),
                                       backingBean, userContext, PageEditMode.EDIT);
        }

        if (backingBean.isVirtual()
                || backingBean.getProgramIds() == null
                   && assignedProgram.getProgramId() != 0) {
            applianceCategoryService.assignProgram(assignedProgram, userContext);
        } else if (backingBean.getProgramIds() != null) {
            for (int programId : backingBean.getProgramIds()) {
                assignedProgram.setProgramId(programId);
                String programName = paoDao.getYukonPAOName(programId);
                assignedProgram.setProgramName(programName);
                assignedProgram.setDisplayName(programName);
                assignedProgram.setShortName(programName);
                applianceCategoryService.assignProgram(assignedProgram, userContext);
            }
            
        } else {
            throw new RuntimeException("invalid form values");
        }
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".editAssignedProgram.success"));
        return null;
    }

    @RequestMapping("confirmUnassignProgram")
    public String confirmUnassignProgram(ModelMap model, int applianceCategoryId,
                                         int assignedProgramId, YukonUserContext userContext) {
        
        verifyAssignedProgramAC(assignedProgramId, applianceCategoryId);
        ApplianceCategory applianceCategory = applianceCategoryDao.getById(applianceCategoryId);
        ecService.verifyEditPageAccess(userContext.getYukonUser(),
                                                  applianceCategory.getEnergyCompanyId());
        model.addAttribute("applianceCategory", applianceCategory);
        AssignedProgram assignedProgram = assignedProgramDao.getById(assignedProgramId);
        model.addAttribute("assignedProgram", assignedProgram);
        MessageSourceResolvable confirmationQuestion = null;
        if (assignedProgram.getProgramId() == 0) {
            confirmationQuestion =
                new YukonMessageSourceResolvable(baseKey + ".unassignedProgram.virtualProgramConfirmationQuestion",
                                                 assignedProgram.getDisplayName(),
                                                 applianceCategory.getName());
        } else {
            confirmationQuestion =
                new YukonMessageSourceResolvable(baseKey + ".unassignedProgram.confirmationQuestion",
                                                 assignedProgram.getProgramName(),
                                                 applianceCategory.getName());
        }
        model.addAttribute("confirmationQuestion", confirmationQuestion);
        return "applianceCategory/unassignProgram.jsp";
    }

    @RequestMapping("unassignProgram")
    public String unassignProgram(ModelMap model, FlashScope flash, int applianceCategoryId, int assignedProgramId, int ecId,
            YukonUserContext userContext) {
        
        AssignedProgram program = assignedProgramDao.getById(assignedProgramId);
        
        verifyAssignedProgramAC(assignedProgramId, applianceCategoryId);
        ApplianceCategory applianceCategory = applianceCategoryDao.getById(applianceCategoryId);
        ecService.verifyEditPageAccess(userContext.getYukonUser(),
                                                  applianceCategory.getEnergyCompanyId());
        applianceCategoryService.unassignProgram(applianceCategoryId, assignedProgramId, userContext);
        
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + ".unassignProgram.success", program.getName().getProgramName()));
        
        model.addAttribute("applianceCategoryId", applianceCategoryId);
        model.addAttribute("assignedProgramId", assignedProgramId);
        model.addAttribute("ecId", ecId);
        
        return "redirect:view";
    }
    
    @RequestMapping("moveProgram")
    public String moveProgram(int applianceCategoryId,
                              int assignedProgramId, 
                              @RequestParam(required=true) Move direction, 
                              int ecId,
                              YukonUserContext userContext) {
        
        verifyAssignedProgramAC(assignedProgramId, applianceCategoryId);
        ApplianceCategory applianceCategory = applianceCategoryDao.getById(applianceCategoryId);
        ecService.verifyEditPageAccess(userContext.getYukonUser(), applianceCategory.getEnergyCompanyId());
        
        if (direction == Move.up) {
            applianceCategoryService.moveAssignedProgramUp(applianceCategoryId, assignedProgramId, userContext);
        } else if (direction == Move.down) {
            applianceCategoryService.moveAssignedProgramDown(applianceCategoryId, assignedProgramId, userContext);
        }
        
        return "forward:programs-list";
    }
    
    /**
     * Ensure that the appliance category's energy company is the given ecId.
     */
    private void verifyApplianceCategoryEC(int applianceCategoryId, int ecId) {
        int ecIdInDatabase = applianceCategoryDao.getEnergyCompanyForApplianceCategory(applianceCategoryId);
        if (ecId != ecIdInDatabase) {
            throw new NotAuthorizedException("appliance category has been tampered with");
        }
    }
    
    /**
     * Make sure the assigned program really is for the given appliance category.
     */
    private void verifyAssignedProgramAC(int assignedProgramId, int applianceCategoryId) {
        AssignedProgram assignedProgram = assignedProgramDao.getById(assignedProgramId);
        if (assignedProgram.getApplianceCategoryId() != applianceCategoryId) {
            throw new NotAuthorizedException("assigned program has been tampered with");
        }
    }
    
    public static class ChanceOfControl {
        private final int chanceOfControlId;
        private final String name;
        private ChanceOfControl(int chanceOfControlId, String name) {
            this.chanceOfControlId = chanceOfControlId;
            this.name = name;
        }
        public int getChanceOfControlId() {
            return chanceOfControlId;
        }
        public String getName() {
            return name;
        }
    }
}
