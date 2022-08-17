package com.cannontech.web.admin.energyCompany.list;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.constants.DisplayableSelectionList;
import com.cannontech.common.constants.SelectionListCategory;
import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.selectionList.dao.SelectionListDao;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.MeteringType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.SortedSetMultimap;

@Controller
@RequestMapping("/energyCompany/list/*")
public class ListController {
    private final static String baseKey = "yukon.web.modules.adminSetup.list.";

    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private SelectionListDao selectionListDao;
    @Autowired private SelectionListService selectionListService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private YukonListDao listDao;
    @Autowired private ConfigurationSource configSource;

    private final Validator validator = new SimpleValidator<SelectionListDto>(SelectionListDto.class) {
        @Override
        protected void doValidation(SelectionListDto list, Errors errors) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "selectionLabel", list.getSelectionLabel(), 30);
            YukonValidationUtils.checkExceedsMaxLength(errors, "whereIsList", list.getWhereIsList(), 100);
            int index = 0;
            boolean requiresType = list.getType().isRequiresType();
            boolean requiresText = list.getType().isRequiresText();
            for (SelectionListDto.Entry entry : list.getEntries()) {
                errors.setNestedPath("entries[" + index + "].");
                if (!entry.isDeletion()) {
                    YukonValidationUtils.checkExceedsMaxLength(errors, "text", entry.getText(), 50);
                    if (requiresText) {
                        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "text", baseKey + "textRequired");
                    }
                    if (requiresType && entry.getDefinitionId() == 0) {
                        errors.rejectValue("definitionId", baseKey + "definitionRequired");
                    }
                    if (list.getType() == YukonSelectionListEnum.SETTLEMENT_TYPE &&
                        entry.getDefinitionId() != YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO) {
                        errors.rejectValue("definitionId", baseKey + "notAValidSettlementId");
                    }
                }
                index++;
            }
            errors.setNestedPath("");
        }
    };

    @RequestMapping("list")
    public String list(ModelMap model, LiteYukonUser user, EnergyCompanyInfoFragment ecInfo) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        energyCompanyService.verifyViewPageAccess(user, ecId);

        SortedSetMultimap<SelectionListCategory, DisplayableSelectionList> lists =
            selectionListService.getUserEditableLists(ecInfo.getEnergyCompanyId(), user);
        model.addAttribute("lists", lists.asMap());

        return "list/list.jsp";
    }

    @RequestMapping("view")
    public String view(ModelMap model, int listId, YukonUserContext userContext, EnergyCompanyInfoFragment ecInfo) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        energyCompanyService.verifyViewPageAccess(userContext.getYukonUser(), ecId);

        YukonSelectionList list = listDao.getYukonSelectionList(listId);
        model.addAttribute("list", list);
        model.addAttribute("isInherited", selectionListService.isListInherited(ecId, list));
        addListDefinitionsToModel(model, list, userContext);

        model.addAttribute("mode", PageEditMode.VIEW);
        return "list/view.jsp";
    }

    @RequestMapping("edit")
    public String edit(ModelMap model, int listId, YukonUserContext userContext, EnergyCompanyInfoFragment ecInfo) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        YukonSelectionList list = listDao.getYukonSelectionList(listId);
        SelectionListDto listDto = new SelectionListDto(list);
        model.addAttribute("list", listDto);

        if (list.getEnergyCompanyId() != ecInfo.getEnergyCompanyId()) {
            throw new NotAuthorizedException("energy company appears to have been tampered with");
        }
        return prepareEdit(model, listDto, userContext);
    }

    private String prepareEdit(ModelMap model, SelectionListDto list, YukonUserContext userContext) {
        energyCompanyService.verifyEditPageAccess(userContext.getYukonUser(), list.getEnergyCompanyId());

        addListDefinitionsToModel(model, list.getYukonSelectionList(), userContext);

        model.addAttribute("mode", PageEditMode.EDIT);
        return "list/edit.jsp";
    }

    @RequestMapping("addItem")
    public String addItem(ModelMap model, int itemIndex, int listId, YukonUserContext userContext) {
        model.addAttribute("entryIndex", itemIndex);

        YukonSelectionList list = listDao.getYukonSelectionList(listId);
        model.addAttribute("list", list);

        energyCompanyService.verifyEditPageAccess(userContext.getYukonUser(), list.getEnergyCompanyId());
        addListDefinitionsToModel(model, list, userContext);

        return "list/entry.jsp";
    }

    private void addListDefinitionsToModel(ModelMap model, YukonSelectionList list, YukonUserContext userContext) {
        List<YukonDefinition> listDefinitions =
            selectionListService.getValidDefinitions(list.getEnergyCompanyId(), list.getType());
        listDefinitions =
            objectFormattingService.sortDisplayableValues(listDefinitions, null, null, userContext);

        // For the CPARM (ADD_STARS_METERS_ALLOWED) the  "Meter" type should be
        // displayed in selection list only if entry with the "Meter" type already exists when the CPARM is false.
        // Changed in YUK-20341
        if (!configSource.getBoolean(MasterConfigBoolean.ADD_STARS_METERS_ALLOWED)) {
            List<YukonListEntry> entries = list.getYukonListEntries();
            final Predicate<YukonListEntry> meterTypeEntry = new Predicate<YukonListEntry>() {
                @Override
                public boolean apply(YukonListEntry entry) {
                    return entry.getDefinition() == YukonDefinition.DEV_TYPE_NON_YUKON_METER;
                }
            };
            final List<YukonListEntry> meterTypeEntries = Lists.newArrayList(Iterables.filter(entries, meterTypeEntry));
            if (meterTypeEntries.isEmpty()) {
                // If the entry with the "Meter" type was not found, the type is removed from the
                // selection list.
                listDefinitions.remove(YukonDefinition.DEV_TYPE_NON_YUKON_METER);
            }
        }
        
        if (!configSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE)) {
            //Used to remove the Nest Thermostat from the Admin EC > Lists > Device Type YUK-19820
            listDefinitions.remove(YukonDefinition.DEV_TYPE_NEST_THERMOSTAT);
        }
        
        model.addAttribute("listDefinitions", listDefinitions);
        model.addAttribute("usesType", !listDefinitions.isEmpty());
    }

    @RequestMapping(value="save", params="save", method=RequestMethod.POST)
    public String save(ModelMap model, @ModelAttribute("list") SelectionListDto list, BindingResult bindingResult,
            YukonUserContext userContext, FlashScope flashScope) {
        energyCompanyService.verifyEditPageAccess(userContext.getYukonUser(),
                                                  list.getEnergyCompanyId());
        YukonSelectionList oldList = listDao.getYukonSelectionList(list.getListId());
        if (oldList.getEnergyCompanyId() != list.getEnergyCompanyId()) {
            throw new NotAuthorizedException("energy company appears to have been tampered with");
        }
        if (!oldList.isUserUpdateAvailable()) {
            throw new NotAuthorizedException("listId appears to have been tampered with");
        }

        // Get type from old list since it can't be changed.  (We don't have to pass it and
        // we don't have to worry about the user tampering with it.)
        list.setType(oldList.getType());

        // Moving items up and down on the page doesn't change their indexes so the entries
        // are in the order they were originally added to the page...not the order specified.
        // Sort them so they match again in case we have validation errors and need to go back
        // to the edit page.  (We have to do this before actual validation so the errors show
        // up on the correct rows.)
        list.sortEntries();

        validator.validate(list, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setError(messages);
            return prepareEdit(model, list, userContext);
        }

        YukonSelectionList newList = list.getYukonSelectionList();
        try {
            selectionListDao.saveList(newList, list.getEntryIdsToDelete());
        } catch (DataIntegrityViolationException dive) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + "cannotDeleteUsedEntries"));
            return prepareEdit(model, list, userContext);
        }

        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "listSaved"));

        model.addAttribute("listId", list.getListId());
        model.addAttribute("ecId", list.getEnergyCompanyId());
        return "redirect:view";
    }

    @RequestMapping(value="save", params="restoreDefault", method=RequestMethod.POST)
    public String restoreDefault(ModelMap model, @ModelAttribute("list") SelectionListDto list,
            YukonUserContext userContext, FlashScope flashScope) {
        int ecId = list.getEnergyCompanyId();
        energyCompanyService.verifyEditPageAccess(userContext.getYukonUser(), list.getEnergyCompanyId());

        YukonSelectionList newList = listDao.getYukonSelectionList(list.getListId());
        try {
            selectionListService.restoreToDefault(newList);
        } catch (DataIntegrityViolationException dive) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + "cannotDeleteUsedEntries"));
            list.setType(newList.getType());
            return prepareEdit(model, list, userContext);
        }

        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "defaultRestored"));

        model.addAttribute("listId", list.getListId());
        model.addAttribute("ecId", ecId);
        return "redirect:view";
    }
}
