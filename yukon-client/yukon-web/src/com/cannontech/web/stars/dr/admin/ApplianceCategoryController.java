package com.cannontech.web.stars.dr.admin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramNameFilter;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.ApplianceCategoryIcon;
import com.cannontech.stars.dr.appliance.model.ApplianceTypeEnum;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.appliance.model.ControlPercentIcon;
import com.cannontech.stars.dr.appliance.model.EnvironmentIcon;
import com.cannontech.stars.dr.appliance.model.SavingsIcon;
import com.cannontech.stars.dr.appliance.service.ApplianceCategoryService;
import com.cannontech.stars.dr.appliance.service.AssignedProgramService;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/applianceCategory/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY)
public class ApplianceCategoryController {
    private final static String baseKey = "yukon.web.modules.energyCompanyAdmin";
    private AssignedProgramDao assignedProgramDao;
    private AssignedProgramService assignedProgramService;
    private ApplianceCategoryDao applianceCategoryDao;
    private ApplianceCategoryService applianceCategoryService;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;
    private StarsDatabaseCache starsDatabaseCache;
    private PaoDao paoDao;

    private Validator detailsValidator = new SimpleValidator<ApplianceCategory>(ApplianceCategory.class) {
        @Override
        public void doValidation(ApplianceCategory target, Errors errors) {
            String[] requiredFields = new String[] { "name", "displayName" };
            for (String requiredField : requiredFields) {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                                                          requiredField,
                                                          baseKey + ".editApplianceCategory.empty");
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
    public String create(ModelMap model, YukonUserContext userContext) {
        return "applianceCategory/create.jsp";
    }

    @RequestMapping
    public String view(ModelMap model,
            @ModelAttribute("backingBean") ListBackingBean backingBean,
            int applianceCategoryId, YukonUserContext userContext) {
        return edit(model, backingBean, applianceCategoryId, userContext,
                    PageEditMode.VIEW);
    }

    @RequestMapping
    public String edit(ModelMap model,
            @ModelAttribute("backingBean") ListBackingBean backingBean,
            int applianceCategoryId, YukonUserContext userContext) {
        return edit(model, backingBean, applianceCategoryId, userContext,
                    PageEditMode.EDIT);
    }

    private String edit(ModelMap model, ListBackingBean backingBean,
            int applianceCategoryId, YukonUserContext userContext,
            PageEditMode pageEditMode) {
        ApplianceCategory applianceCategory =
            applianceCategoryDao.getById(applianceCategoryId);
        model.addAttribute("applianceCategory", applianceCategory);
        model.addAttribute("mode", pageEditMode);

        List<UiFilter<AssignedProgram>> filters = Lists.newArrayList();
        boolean isFiltered = false;

        if (!StringUtils.isEmpty(backingBean.getName())) {
            filters.add(new AssignedProgramNameFilter(backingBean.getName()));
            isFiltered = true;
        }

        model.addAttribute("isFiltered", isFiltered);

        boolean sortByName = !applianceCategory.isConsumerSelectable()
            || "PROGRAM_NAME".equals(backingBean.getSort());
        boolean sortDescending = backingBean.getDescending();

        SearchResult<AssignedProgram> assignedPrograms =
            assignedProgramService.filter(applianceCategoryId,
                                          UiFilterList.wrap(filters),
                                          sortByName, sortDescending,
                                          backingBean.getStartIndex(),
                                          backingBean.getItemsPerPage(),
                                          userContext);
        model.addAttribute("assignedPrograms", assignedPrograms);

        return "applianceCategory/edit.jsp";
    }

    @RequestMapping
    public String createDetails(ModelMap model,
            @ModelAttribute("backingBean") ListBackingBean backingBean,
            YukonUserContext userContext) {
        ApplianceCategory applianceCategory = new ApplianceCategory();

        return editDetails(model, applianceCategory, userContext);
    }

    @RequestMapping
    public String editDetails(ModelMap model, int applianceCategoryId,
            YukonUserContext userContext) {
        ApplianceCategory applianceCategory =
            applianceCategoryDao.getById(applianceCategoryId);

        return editDetails(model, applianceCategory, userContext);
    }

    private String editDetails(ModelMap model,
            ApplianceCategory applianceCategory, YukonUserContext userContext) {
        model.addAttribute("applianceCategory", applianceCategory);

        ApplianceTypeEnum[] applianceTypes = ApplianceTypeEnum.values();
        sortDisplayableEnum(applianceTypes, ApplianceTypeEnum.DEFAULT, null,
                            userContext);
        model.addAttribute("applianceTypes", applianceTypes);

        ApplianceCategoryIcon[] icons = ApplianceCategoryIcon.values();
        sortDisplayableEnum(icons, ApplianceCategoryIcon.NONE,
                            ApplianceCategoryIcon.OTHER, userContext);
        model.addAttribute("icons", icons);

        return "applianceCategory/editDetails.jsp";
    }

    @RequestMapping
    public String saveDetails(ModelMap model,
            @ModelAttribute ApplianceCategory applianceCategory,
            BindingResult bindingResult, YukonUserContext userContext, FlashScope flashScope) {
    	
        detailsValidator.validate(applianceCategory, bindingResult);
        if (bindingResult.hasErrors()) {
        	
        	List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);

            return editDetails(model, applianceCategory, userContext);
        }

        String newLocation = null;
        boolean wasNew = applianceCategory.getApplianceCategoryId() == 0;
        applianceCategoryService.save(applianceCategory, userContext);
        if (wasNew) {
            newLocation = "/spring/stars/dr/admin/applianceCategory/edit" +
            		"?applianceCategoryId=" +
            		applianceCategory.getApplianceCategoryId();
        }

        return closeDialog(model, newLocation);
    }

    @RequestMapping
    public String assignProgram(ModelMap model, int applianceCategoryId,
            Integer[] programsToAssign, YukonUserContext userContext) {
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
        return editAssignedProgram(model, applianceCategoryId, backingBean,
                                   userContext, PageEditMode.CREATE);
    }

    @RequestMapping
    public String createVirtualProgram(ModelMap model, int applianceCategoryId,
            YukonUserContext userContext) {
        AssignProgramBackingBean backingBean =
            new AssignProgramBackingBean(true, false, null, new AssignedProgram());
        return editAssignedProgram(model, applianceCategoryId, backingBean,
                                   userContext, PageEditMode.CREATE);
    }

    @RequestMapping
    public String editAssignedProgram(ModelMap model, int applianceCategoryId,
            int assignedProgramId, YukonUserContext userContext) {
        AssignedProgram assignedProgram =
            assignedProgramDao.getById(assignedProgramId);
        AssignProgramBackingBean backingBean =
            new AssignProgramBackingBean(assignedProgram.getProgramId() == 0,
                                         false, null, assignedProgram);
        return editAssignedProgram(model, applianceCategoryId, backingBean,
                                   userContext, PageEditMode.EDIT);
    }

    @RequestMapping
    public String viewAssignedProgram(ModelMap model, int applianceCategoryId,
            int assignedProgramId, YukonUserContext userContext) {
        AssignedProgram assignedProgram =
            assignedProgramDao.getById(assignedProgramId);
        AssignProgramBackingBean backingBean =
            new AssignProgramBackingBean(assignedProgram.getProgramId() == 0,
                                         false, null, assignedProgram);
        return editAssignedProgram(model, applianceCategoryId, backingBean,
                                   userContext, PageEditMode.VIEW);
    }
    
    private String editAssignedProgram(ModelMap model,
            int applianceCategoryId, AssignProgramBackingBean backingBean,
            YukonUserContext userContext, PageEditMode pageEditMode) {
        model.addAttribute("mode", pageEditMode);

        ApplianceCategory applianceCategory =
            applianceCategoryDao.getById(applianceCategoryId);
        model.addAttribute("applianceCategory", applianceCategory);

        backingBean.getAssignedProgram().setApplianceCategoryId(applianceCategoryId);
        model.addAttribute("backingBean", backingBean);

        LiteStarsEnergyCompany energyCompany =
            starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
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

        return "applianceCategory/editAssignedProgram.jsp";
    }

    @RequestMapping
    public String saveAssignedProgram(ModelMap model,
            @ModelAttribute("backingBean") AssignProgramBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext, FlashScope flashScope) {
    	
        AssignedProgram assignedProgram = backingBean.getAssignedProgram();
        assignedProgramValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
        	
        	List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);

            return editAssignedProgram(model, assignedProgram.getApplianceCategoryId(),
                                       backingBean, userContext, PageEditMode.EDIT);
        }

        if (backingBean.isVirtual()
                || backingBean.getProgramIds() == null
                   && assignedProgram.getProgramId() != 0) {
            applianceCategoryService.assignProgram(assignedProgram,
                                                   userContext);
        } else if (backingBean.getProgramIds() != null){
            for (int programId : backingBean.getProgramIds()) {
                assignedProgram.setProgramId(programId);
                String programName = paoDao.getYukonPAOName(programId);
                assignedProgram.setProgramName(programName);
                assignedProgram.setDisplayName(programName);
                assignedProgram.setShortName(programName);
                applianceCategoryService.assignProgram(assignedProgram,
                                                       userContext);
            }
        } else {
            throw new RuntimeException("invalid form values");
        }
        return closeDialog(model);
    }

    @RequestMapping
    public String confirmUnassignProgram(ModelMap model,
            int applianceCategoryId, int assignedProgramId,
            YukonUserContext userContext) {
        ApplianceCategory applianceCategory =
            applianceCategoryDao.getById(applianceCategoryId);
        model.addAttribute("applianceCategory", applianceCategory);
        AssignedProgram assignedProgram =
            assignedProgramDao.getById(assignedProgramId);
        model.addAttribute("assignedProgram", assignedProgram);
        MessageSourceResolvable confirmationQuestion = null;
        if (assignedProgram.getProgramId() == 0) {
            confirmationQuestion =
                new YukonMessageSourceResolvable(baseKey + ".editApplianceCategoryUnassignedProgram.virtualProgramConfirmationQuestion",
                                                 assignedProgram.getProgramName(),
                                                 applianceCategory.getName());
        } else {
            confirmationQuestion =
                new YukonMessageSourceResolvable(baseKey + ".editApplianceCategoryUnassignedProgram.confirmationQuestion",
                                                 assignedProgram.getProgramName(),
                                                 applianceCategory.getName());
        }
        model.addAttribute("confirmationQuestion", confirmationQuestion);
        return "applianceCategory/unassignProgram.jsp";
    }

    @RequestMapping
    public String unassignProgram(ModelMap model, int applianceCategoryId,
            int assignedProgramId, YukonUserContext userContext) {
        applianceCategoryService.unassignProgram(applianceCategoryId,
                                                 assignedProgramId, userContext);
        return closeDialog(model);
    }

    @RequestMapping
    public void moveProgram(HttpServletResponse response, ModelMap model,
            int applianceCategoryId, int assignedProgramId,
            String direction, YukonUserContext userContext) {

        JSONObject object = new JSONObject();

        if ("up".equals(direction)) {
            applianceCategoryService.moveAssignedProgramUp(applianceCategoryId,
                                                           assignedProgramId,
                                                           userContext);
        } else if ("down".equals(direction)) {
            applianceCategoryService.moveAssignedProgramDown(applianceCategoryId,
                                                             assignedProgramId,
                                                             userContext);
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

    // put into a service?
    private <T extends DisplayableEnum> void sortDisplayableEnum(T[] values,
            final T first, final T last, YukonUserContext userContext) {
        final MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(userContext);
        Arrays.sort(values, new Comparator<T>() {
            @Override
            public int compare(T t1, T t2) {
                if (t1 == t2) return 0;
                if (t1 == first || t2 == last) {
                    return -1;
                }
                if (t1 == last || t2 == first) {
                    return 1;
                }
                String localName1 = messageSourceAccessor.getMessage(t1.getFormatKey());
                String localName2 = messageSourceAccessor.getMessage(t2.getFormatKey());
                return localName1.compareToIgnoreCase(localName2);
            }
        });
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

    @Autowired
    public void setAssignedProgramDao(AssignedProgramDao assignedProgramDao) {
        this.assignedProgramDao = assignedProgramDao;
    }

    @Autowired
    public void setAssignedProgramService(
            AssignedProgramService assignedProgramService) {
        this.assignedProgramService = assignedProgramService;
    }

    @Autowired
    public void setApplianceCategoryDao(ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }

    @Autowired
    public void setApplianceCategoryService(
            ApplianceCategoryService applianceCategoryService) {
        this.applianceCategoryService = applianceCategoryService;
    }

    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

}
