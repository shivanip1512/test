package com.cannontech.web.admin.energyCompany.applianceCategory;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
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
import com.cannontech.web.util.ListBackingBean;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
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
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private ConfigurationSource configurationSource;

    private Validator detailsValidator = new SimpleValidator<ApplianceCategory>(ApplianceCategory.class) {
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

    private Validator assignedProgramValidator = new SimpleValidator<AssignProgramBackingBean>(AssignProgramBackingBean.class) {
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

    @RequestMapping
    public String list(ModelMap model, YukonUserContext context,
                       EnergyCompanyInfoFragment ecInfo) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        energyCompanyService.verifyViewPageAccess(context.getYukonUser(), ecId);

        List<ApplianceCategory> applianceCategories = applianceCategoryDao.getApplianceCategoriesByEcId(ecId);
        model.addAttribute("applianceCategories", applianceCategories);

        return "applianceCategory/list.jsp";
    }

    @RequestMapping
    public String programs(ModelMap model,
                           @ModelAttribute("backingBean") ListBackingBean backingBean,
                           YukonUserContext context,
                           EnergyCompanyInfoFragment ecInfo) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        energyCompanyService.verifyViewPageAccess(context.getYukonUser(), ecId);

        List<UiFilter<AssignedProgram>> filters = Lists.newArrayList();
        boolean isFiltered = false;

        if (!StringUtils.isEmpty(backingBean.getName())) {
            filters.add(new AssignedProgramNameFilter(backingBean.getName()));
            isFiltered = true;
        }

        model.addAttribute("isFiltered", isFiltered);

        SortBy sortBy = SortBy.PROGRAM_NAME;
        if (!StringUtils.isEmpty(backingBean.getSort())) {
            sortBy = SortBy.valueOf(backingBean.getSort());
        }
        boolean sortDescending = backingBean.getDescending();

        Iterable<Integer> applianceCategoryIds = applianceCategoryDao.getApplianceCategoryIdsByEC(ecId);
        SearchResult<AssignedProgram> assignedPrograms =
            assignedProgramService.filter(applianceCategoryIds, UiFilterList.wrap(filters), sortBy,
                                          sortDescending, backingBean.getStartIndex(),
                                          backingBean.getItemsPerPage());
        model.addAttribute("assignedPrograms", assignedPrograms);

        Function<AssignedProgram, Integer> idFromApplianceCategory = new Function<AssignedProgram, Integer>() {
            @Override
            public Integer apply(AssignedProgram from) {
                return from.getApplianceCategoryId();
            }
        };
        applianceCategoryIds =
            Iterables.transform(assignedPrograms.getResultList(), idFromApplianceCategory);
        Map<Integer, ApplianceCategory> applianceCategoriesById =
            applianceCategoryDao.getByApplianceCategoryIds(applianceCategoryIds);
        model.addAttribute("applianceCategoriesById", applianceCategoriesById);

        return "applianceCategory/programs.jsp";
    }

    @RequestMapping
    public String view(ModelMap model, @ModelAttribute("backingBean") ListBackingBean backingBean,
                       int applianceCategoryId, YukonUserContext context,
                       EnergyCompanyInfoFragment ecInfo) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        energyCompanyService.verifyViewPageAccess(context.getYukonUser(), ecId);

        ApplianceCategory applianceCategory =
            applianceCategoryDao.getById(applianceCategoryId);

        List<UiFilter<AssignedProgram>> filters = Lists.newArrayList();
        boolean isFiltered = false;

        if (!StringUtils.isEmpty(backingBean.getName())) {
            filters.add(new AssignedProgramNameFilter(backingBean.getName()));
            isFiltered = true;
        }

        model.addAttribute("isFiltered", isFiltered);

        SortBy sortBy = applianceCategory.isConsumerSelectable() ? SortBy.PROGRAM_ORDER : SortBy.PROGRAM_NAME;
        if (!StringUtils.isEmpty(backingBean.getSort())) {
            sortBy = SortBy.valueOf(backingBean.getSort());
        }
        boolean sortDescending = backingBean.getDescending();

        SearchResult<AssignedProgram> assignedPrograms =
            assignedProgramService.filter(applianceCategory.getApplianceCategoryId(),
                                          UiFilterList.wrap(filters), sortBy, sortDescending,
                                          backingBean.getStartIndex(), backingBean.getItemsPerPage());
        model.addAttribute("assignedPrograms", assignedPrograms);

        return edit(PageEditMode.VIEW, model, applianceCategory, context, ecId);
    }

    @RequestMapping
    public String create(ModelMap model, YukonUserContext context,
                         EnergyCompanyInfoFragment ecInfo) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(), ecId);

        ApplianceCategory applianceCategory = new ApplianceCategory();
        applianceCategory.setEnergyCompanyId(ecId);
        return edit(PageEditMode.CREATE, model, applianceCategory, context,
                    ecInfo.getEnergyCompanyId());
    }

    @RequestMapping
    public String edit(ModelMap model, @ModelAttribute("backingBean") ListBackingBean backingBean,
                       int applianceCategoryId, YukonUserContext context,
                       EnergyCompanyInfoFragment ecInfo) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(), ecId);

        ApplianceCategory applianceCategory = applianceCategoryDao.getById(applianceCategoryId);
        return edit(PageEditMode.EDIT, model, applianceCategory, context, ecId);
    }

    private String edit(PageEditMode mode, ModelMap model, ApplianceCategory applianceCategory,
                        YukonUserContext context, int ecId) {
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
                                                       ApplianceTypeEnum.DEFAULT, null, context);
            model.addAttribute("applianceTypes", applianceTypes);

            List<ApplianceCategoryIcon> icons =
                objectFormattingService.sortDisplayableValues(ApplianceCategoryIcon.values(),
                                                       ApplianceCategoryIcon.NONE,
                                                       ApplianceCategoryIcon.OTHER, context);
            model.addAttribute("icons", icons);
        }

        model.addAttribute("canAddVirtual",
                           configurationSource.getBoolean(MasterConfigBooleanKeysEnum.VIRTUAL_PROGRAMS));
        return "applianceCategory/edit.jsp";
    }

    @RequestMapping(value="save", params="save", method=RequestMethod.POST)
    public String save(ModelMap model, @ModelAttribute ApplianceCategory applianceCategory,
                       BindingResult bindingResult, YukonUserContext context,
                       FlashScope flashScope) {
        int ecId = applianceCategory.getEnergyCompanyId();
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(), ecId);
        int applianceCategoryId = applianceCategory.getApplianceCategoryId();
        if (applianceCategoryId != 0) {
            verifyApplianceCategoryEC(applianceCategoryId, ecId);
        }

        detailsValidator.validate(applianceCategory, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return edit(PageEditMode.EDIT, model, applianceCategory, context, ecId);
        }

        applianceCategoryService.save(applianceCategory, context);

        return "redirect:view?ecId=" + ecId + "&applianceCategoryId="
            + applianceCategory.getApplianceCategoryId();
    }

    @RequestMapping(value="save", params="delete", method=RequestMethod.POST)
    public String delete(@ModelAttribute ApplianceCategory applianceCategory,
                         YukonUserContext context) {
        int ecId = applianceCategory.getEnergyCompanyId();
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(), ecId);

        applianceCategoryService.delete(applianceCategory.getApplianceCategoryId(), context);

        return "redirect:list?ecId=" + ecId;
    }

    @RequestMapping
    public String assignProgram(ModelMap model, int ecId, int applianceCategoryId,
                                Integer[] programsToAssign, YukonUserContext context) {
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(), ecId);
        AssignedProgram assignedProgram = new AssignedProgram();
        boolean multiple = true;
        if (programsToAssign.length == 0) {
            throw new RuntimeException("need at least one program to assign");
        } else if (programsToAssign.length == 1) {
            int programId = programsToAssign[0];
            assignedProgram.setProgramId(programId);
            assignedProgram.setProgramName(paoDao.getYukonPAOName(programId));
            programsToAssign = null;
            multiple = false;
        }
        AssignProgramBackingBean backingBean =
            new AssignProgramBackingBean(false, multiple,
                                         programsToAssign,
                                         assignedProgram);
        return editAssignedProgram(model, ecId, applianceCategoryId, backingBean,
                                   context, PageEditMode.CREATE);
    }

    @RequestMapping
    public String createVirtualProgram(ModelMap model, int ecId, int applianceCategoryId,
            YukonUserContext context) {
        AssignProgramBackingBean backingBean =
            new AssignProgramBackingBean(true, false, null, new AssignedProgram());
        return editAssignedProgram(model, ecId, applianceCategoryId, backingBean,
                                   context, PageEditMode.CREATE);
    }

    @RequestMapping
    public String editAssignedProgram(ModelMap model, int ecId, int applianceCategoryId,
                                      int assignedProgramId, YukonUserContext context) {
        verifyAssignedProgramAC(assignedProgramId, applianceCategoryId);
        AssignedProgram assignedProgram =
            assignedProgramDao.getById(assignedProgramId);
        AssignProgramBackingBean backingBean =
            new AssignProgramBackingBean(assignedProgram.getProgramId() == 0,
                                         false, null, assignedProgram);
        return editAssignedProgram(model, ecId, applianceCategoryId, backingBean,
                                   context, PageEditMode.EDIT);
    }

    private String editAssignedProgram(ModelMap model, int ecId, int applianceCategoryId,
                                       AssignProgramBackingBean backingBean,
                                       YukonUserContext context, PageEditMode pageEditMode) {
        ApplianceCategory applianceCategory =
            applianceCategoryDao.getById(applianceCategoryId);
        model.addAttribute("applianceCategory", applianceCategory);

        backingBean.getAssignedProgram().setApplianceCategoryId(applianceCategoryId);
        model.addAttribute("backingBean", backingBean);

        YukonEnergyCompany yukonEnergyCompany =
            yukonEnergyCompanyService.getEnergyCompanyByOperator(context.getYukonUser());
        LiteStarsEnergyCompany energyCompany =
            starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        StarsCustSelectionList chanceOfControlList =
            energyCompany.getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL);
        List<ChanceOfControl> chanceOfControls = Lists.newArrayList();

        String chanceOfControl = "Not Specified";
        chanceOfControls.add(0, new ChanceOfControl(0, chanceOfControl));
        for (StarsSelectionListEntry cocEntry : chanceOfControlList.getStarsSelectionListEntry()) {
            chanceOfControls.add(new ChanceOfControl(cocEntry.getEntryID(), cocEntry.getContent()));
            if (cocEntry.getEntryID() == backingBean.getAssignedProgram().getChanceOfControlId()) {
                chanceOfControl = cocEntry.getContent();
            }
        }
        model.addAttribute("chanceOfControls", chanceOfControls);
        if (pageEditMode == PageEditMode.VIEW) {
            model.addAttribute("chanceOfControl", chanceOfControl);
        }

        SavingsIcon[] savingsIcons = SavingsIcon.values();
        model.addAttribute("savingsIcons", savingsIcons);

        ControlPercentIcon[] controlPercentIcons = ControlPercentIcon.values();
        model.addAttribute("controlPercentIcons", controlPercentIcons);

        EnvironmentIcon[] environmentIcons = EnvironmentIcon.values();
        model.addAttribute("environmentIcons", environmentIcons);

        boolean isEditable = applianceCategory.getEnergyCompanyId() == ecId;
        if (!isEditable && pageEditMode == PageEditMode.EDIT) {
            pageEditMode = PageEditMode.VIEW;
        }
        model.addAttribute("isEditable", isEditable);
        model.addAttribute("mode", pageEditMode);

        return "applianceCategory/editAssignedProgram.jsp";
    }

    @RequestMapping
    public String saveAssignedProgram(ModelMap model, int ecId,
                                      @ModelAttribute("backingBean") AssignProgramBackingBean backingBean,
                                      BindingResult bindingResult, YukonUserContext context,
                                      FlashScope flashScope) {
        int applianceCategoryId = backingBean.getAssignedProgram().getApplianceCategoryId();
        verifyApplianceCategoryEC(applianceCategoryId, ecId);
        int assignedProgramId = backingBean.getAssignedProgram().getAssignedProgramId();
        if (assignedProgramId > 0) {
            verifyAssignedProgramAC(assignedProgramId, applianceCategoryId);
        }

        AssignedProgram assignedProgram = backingBean.getAssignedProgram();
        assignedProgramValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
        	
        	List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);

            return editAssignedProgram(model, ecId, assignedProgram.getApplianceCategoryId(),
                                       backingBean, context, PageEditMode.EDIT);
        }

        if (backingBean.isVirtual()
                || backingBean.getProgramIds() == null
                   && assignedProgram.getProgramId() != 0) {
            applianceCategoryService.assignProgram(assignedProgram,
                                                   context);
        } else if (backingBean.getProgramIds() != null){
            for (int programId : backingBean.getProgramIds()) {
                assignedProgram.setProgramId(programId);
                String programName = paoDao.getYukonPAOName(programId);
                assignedProgram.setProgramName(programName);
                assignedProgram.setDisplayName(programName);
                assignedProgram.setShortName(programName);
                applianceCategoryService.assignProgram(assignedProgram,
                                                       context);
            }
        } else {
            throw new RuntimeException("invalid form values");
        }
        return closeDialog(model);
    }

    @RequestMapping
    public String confirmUnassignProgram(ModelMap model, int applianceCategoryId,
                                         int assignedProgramId, YukonUserContext context) {
        verifyAssignedProgramAC(assignedProgramId, applianceCategoryId);
        ApplianceCategory applianceCategory = applianceCategoryDao.getById(applianceCategoryId);
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(),
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

    @RequestMapping
    public String unassignProgram(ModelMap model, int applianceCategoryId, int assignedProgramId,
                                  YukonUserContext context) {
        verifyAssignedProgramAC(assignedProgramId, applianceCategoryId);
        ApplianceCategory applianceCategory = applianceCategoryDao.getById(applianceCategoryId);
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(),
                                                  applianceCategory.getEnergyCompanyId());
        applianceCategoryService.unassignProgram(applianceCategoryId, assignedProgramId, context);
        return closeDialog(model);
    }

    @RequestMapping
    public void moveProgram(HttpServletResponse response, ModelMap model, int applianceCategoryId,
                            int assignedProgramId, String direction, YukonUserContext context) {
        verifyAssignedProgramAC(assignedProgramId, applianceCategoryId);
        ApplianceCategory applianceCategory = applianceCategoryDao.getById(applianceCategoryId);
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(),
                                                  applianceCategory.getEnergyCompanyId());
        JSONObject object = new JSONObject();

        if ("up".equals(direction)) {
            applianceCategoryService.moveAssignedProgramUp(applianceCategoryId,
                                                           assignedProgramId,
                                                           context);
        } else if ("down".equals(direction)) {
            applianceCategoryService.moveAssignedProgramDown(applianceCategoryId,
                                                             assignedProgramId,
                                                             context);
        } else {
            throw new RuntimeException("invalid diirection [" + direction + "]");
        }
        object.put("action", "reload");

        response.addHeader("X-JSON", object.toString());
        response.setContentType("text/plain");
    }

    private String closeDialog(ModelMap model) {
        return closeDialog(model, null);
    }

    private String closeDialog(ModelMap model, String newLocation) {
        model.addAttribute("popupId", "acDialog");
        if (newLocation != null) {
            model.addAttribute("newLocation", newLocation);
        }
        return "closePopup.jsp";
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
        private int chanceOfControlId;
        private String name;
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
