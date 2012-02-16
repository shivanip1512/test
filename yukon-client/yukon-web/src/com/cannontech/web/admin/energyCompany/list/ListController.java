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

import com.cannontech.common.constants.DisplayableSelectionList;
import com.cannontech.common.constants.SelectionListCategory;
import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.selectionList.dao.SelectionListDao;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.collect.SortedSetMultimap;

@Controller
@RequestMapping("/energyCompany/list/*")
public class ListController {
    private final static String baseKey = "yukon.web.modules.adminSetup.list.";

    private YukonListDao yukonListDao;
    private SelectionListDao selectionListDao;
    private SelectionListService selectionListService;
    private EnergyCompanyService energyCompanyService;
    private ObjectFormattingService objectFormattingService;

    private Validator validator = new SimpleValidator<SelectionListDto>(SelectionListDto.class) {
        @Override
        protected void doValidation(SelectionListDto list, Errors errors) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "selectionLabel",
                                                       list.getSelectionLabel(), 30);
            YukonValidationUtils.checkExceedsMaxLength(errors, "whereIsList",
                                                       list.getWhereIsList(), 100);
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

    @RequestMapping
    public String list(ModelMap model, YukonUserContext context, EnergyCompanyInfoFragment ecInfo) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        energyCompanyService.verifyViewPageAccess(context.getYukonUser(), ecId);

        SortedSetMultimap<SelectionListCategory, DisplayableSelectionList> lists =
            selectionListService.getUserEditableLists(ecInfo.getEnergyCompanyId(), context);
        model.addAttribute("lists", lists.asMap());

        return "list/list.jsp";
    }

    @RequestMapping
    public String view(ModelMap model, int listId, YukonUserContext context,
                       EnergyCompanyInfoFragment ecInfo) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        int ecId = ecInfo.getEnergyCompanyId();
        energyCompanyService.verifyViewPageAccess(context.getYukonUser(), ecId);

        YukonSelectionList list = yukonListDao.getYukonSelectionList(listId);
        model.addAttribute("list", list);
        model.addAttribute("isInherited", selectionListService.isListInherited(ecId, list));
        addListDefinitionsToModel(model, list.getEnergyCompanyId(), list.getType(), context);

        model.addAttribute("mode", PageEditMode.VIEW);
        return "list/view.jsp";
    }

    @RequestMapping
    public String edit(ModelMap model, int listId, YukonUserContext context,
                       EnergyCompanyInfoFragment ecInfo) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(ecInfo, model);
        YukonSelectionList list = yukonListDao.getYukonSelectionList(listId);
        SelectionListDto listDto = new SelectionListDto(list);
        model.addAttribute("list", listDto);

        if (list.getEnergyCompanyId() != ecInfo.getEnergyCompanyId()) {
            throw new NotAuthorizedException("energy company appears to have been tampered with");
        }
        return prepareEdit(model, listDto, context);
    }

    private String prepareEdit(ModelMap model, SelectionListDto list, YukonUserContext context) {
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(),
                                                  list.getEnergyCompanyId());

        addListDefinitionsToModel(model, list.getEnergyCompanyId(), list.getType(), context);

        model.addAttribute("mode", PageEditMode.EDIT);
        return "list/edit.jsp";
    }

    @RequestMapping
    public String addItem(ModelMap model, int itemIndex, int listId, YukonUserContext context) {
        model.addAttribute("entryIndex", itemIndex);

        YukonSelectionList list = yukonListDao.getYukonSelectionList(listId);
        model.addAttribute("list", list);
        
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(), list.getEnergyCompanyId());
        addListDefinitionsToModel(model, list.getEnergyCompanyId(), list.getType(), context);

        return "list/entry.jsp";
    }

    private void addListDefinitionsToModel(ModelMap model, int ecId, YukonSelectionListEnum type,
                                           YukonUserContext context) {
        List<YukonDefinition> listDefinitions =
            selectionListService.getValidDefinitions(ecId, type);
        listDefinitions =
            objectFormattingService.sortDisplayableValues(listDefinitions, null, null, context);
        model.addAttribute("listDefinitions", listDefinitions);
        model.addAttribute("usesType", !listDefinitions.isEmpty());
    }

    @RequestMapping(value="save", params="save", method=RequestMethod.POST)
    public String save(ModelMap model, @ModelAttribute("list") SelectionListDto list,
                       BindingResult bindingResult, YukonUserContext context, FlashScope flash) {
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(),
                                                  list.getEnergyCompanyId());
        YukonSelectionList oldList = yukonListDao.getYukonSelectionList(list.getListId());
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
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flash.setError(messages);
            return prepareEdit(model, list, context);
        }

        YukonSelectionList newList = list.getYukonSelectionList();
        try {
            selectionListDao.saveList(newList, list.getEntryIdsToDelete());
        } catch (DataIntegrityViolationException dive) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "cannotDeleteUsedEntries"));
            return prepareEdit(model, list, context);
        }

        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "listSaved"));

        model.addAttribute("listId", list.getListId());
        model.addAttribute("ecId", list.getEnergyCompanyId());
        return "redirect:view";
    }

    @RequestMapping(value="save", params="restoreDefault", method=RequestMethod.POST)
    public String restoreDefault(ModelMap model, @ModelAttribute("list") SelectionListDto list,
                       BindingResult bindingResult, YukonUserContext context, FlashScope flash) {
        int ecId = list.getEnergyCompanyId();
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(),
                                                  list.getEnergyCompanyId());

        YukonSelectionList newList = yukonListDao.getYukonSelectionList(list.getListId());
        try {
            selectionListService.restoreToDefault(newList);
        } catch (DataIntegrityViolationException dive) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "cannotDeleteUsedEntries"));
            list.setType(newList.getType());
            return prepareEdit(model, list, context);
        }

        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "defaultRestored"));

        model.addAttribute("listId", list.getListId());
        model.addAttribute("ecId", ecId);
        return "redirect:view";
    }

    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }

    @Autowired
    public void setSelectionListDao(SelectionListDao selectionListDao) {
        this.selectionListDao = selectionListDao;
    }

    @Autowired
    public void setSelectionListService(SelectionListService selectionListService) {
        this.selectionListService = selectionListService;
    }

    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }

    @Autowired
    public void setObjectFormattingService(ObjectFormattingService objectFormattingService) {
        this.objectFormattingService = objectFormattingService;
    }
}
