package com.cannontech.web.tools.paoNote;

import static com.cannontech.common.pao.notes.service.PaoNotesService.MAX_CHARACTERS_IN_NOTE;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoSelectionMethod;
import com.cannontech.common.pao.notes.dao.PaoNotesDao.SortBy;
import com.cannontech.common.pao.notes.filter.model.PaoNotesFilter;
import com.cannontech.common.pao.notes.model.PaoNote;
import com.cannontech.common.pao.notes.search.result.model.PaoNotesSearchResult;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.AccessLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.paonote.validator.PaoNoteValidator;
import com.cannontech.web.security.annotation.CheckAccessLevel;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/paoNotes/*")
public class PaoNotesSearchController {

    @Autowired private PaoNotesService paoNotesService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private PaoNotesFilterValidator validator;
    @Autowired private PaoNoteValidator paoNoteValidator;
    @Autowired private ServerDatabaseCache databaseCache;
    
    private static final String baseKey = "yukon.web.common.paoNote.";
    
    public static final int NOTE_TEXTAREA_MAX_LENGTH = 2000;

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String search(@ModelAttribute("paoNoteFilter") PaoNotesFilter filter, BindingResult bindingResult,
            ModelMap model, FlashScope flashScope,
            @DefaultSort(dir = Direction.desc, sort = "createDate") SortingParameters sorting,
            @DefaultItemsPerPage(value = 25) PagingParameters paging, YukonUserContext userContext,
            String deviceGroupNames) {

        if (filter.getPaoSelectionMethod() == PaoSelectionMethod.byDeviceGroups
            && StringUtils.isNotEmpty(deviceGroupNames)) {
            List<DeviceGroup> deviceGroups = Lists.newArrayList();
            for (String deviceGroupName : deviceGroupNames.split(",")) {
                deviceGroups.add(deviceGroupService.resolveGroupName(deviceGroupName));
            }
            filter.setDeviceGroups(deviceGroups);
            model.addAttribute("deviceGroupNames", deviceGroupNames);
        } else {
            filter.setDeviceGroups(null);
        }

        if (filter.getPaoSelectionMethod() != PaoSelectionMethod.selectIndividually) {
            filter.setPaoIds(null);
        }
        
        validator.validate(filter, bindingResult);

        SearchResults<PaoNotesSearchResult> searchResults = new SearchResults<>();
        searchResults.setResultList(Lists.newArrayList());
        
        if (!bindingResult.hasErrors()) {
            searchResults = paoNotesService.getAllNotesByFilter(filter,
                PaoNoteSortBy.valueOf(sorting.getSort()).getValue(), sorting.getDirection(), paging);
        }
        model.addAttribute("searchResults", searchResults);

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        PaoNoteSortBy sortBy = PaoNoteSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        for (PaoNoteSortBy column : PaoNoteSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn sortableColumn = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), sortableColumn);
        }

        List<SimpleDevice> devices = new ArrayList<>();
        StoredDeviceGroup tempGroup = tempDeviceGroupService.createTempGroup();
        searchResults.getResultList().forEach(
            item -> devices.add(deviceDao.getYukonDevice(item.getPaoNote().getPaoId())));
        deviceGroupMemberEditorDao.addDevices(tempGroup, devices);
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tempGroup);
        model.addAttribute("deviceCollection", deviceCollection);

        model.addAttribute("allDevicesEnumValue", PaoSelectionMethod.allDevices);
        model.addAttribute("selectIndividuallyEnumValue", PaoSelectionMethod.selectIndividually);
        model.addAttribute("byDeviceGroupsEnumValue", PaoSelectionMethod.byDeviceGroups);

        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("paoIds") || bindingResult.hasFieldErrors("deviceGroups")) {
                model.addAttribute("hasDeviceFilterErrors", true);
            } 
            if (bindingResult.hasFieldErrors("dateRange.min")) {
                model.addAttribute("hasDateFilterErrors", true);
            } 
            
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        }

        return "paoNote/list.jsp";
    }

    @RequestMapping(value = "download", method = RequestMethod.GET)
    public void download(@ModelAttribute("paoNoteFilter") PaoNotesFilter filter,
            @DefaultSort(dir = Direction.desc, sort = "createDate") SortingParameters sorting,
            @DefaultItemsPerPage(value = 25) PagingParameters paging, YukonUserContext userContext,
            HttpServletResponse response) throws IOException {
        SearchResults<PaoNotesSearchResult> searchResults =
            paoNotesService.getAllNotesByFilter(filter, SortBy.CREATE_DATE, sorting.getDirection(), PagingParameters.EVERYTHING);

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String deviceNameHeader = accessor.getMessage(PaoNoteSortBy.deviceName);
        String typeHeader = accessor.getMessage(PaoNoteSortBy.deviceType);
        String noteTextHeader = accessor.getMessage(PaoNoteSortBy.noteText);
        String createdByHeader = accessor.getMessage(PaoNoteSortBy.createdBy);
        String createDateHeader = accessor.getMessage(PaoNoteSortBy.createDate);
        String editedByHeader = accessor.getMessage(PaoNoteSortBy.editedBy);
        String editDateHeader = accessor.getMessage(PaoNoteSortBy.editDate);
        String[] headerRow = new String[] { deviceNameHeader, typeHeader, noteTextHeader, createdByHeader,
            createDateHeader, editedByHeader, editDateHeader };

        List<String[]> dataRows = Lists.newArrayList();
        for (PaoNotesSearchResult event : searchResults.getResultList()) {
            String createDate =
                dateFormattingService.format(event.getPaoNote().getCreateDate(), DateFormatEnum.BOTH, userContext);
            String modifiedDate = "";
            if (event.getPaoNote().getEditDate() != null) {
                modifiedDate =
                    dateFormattingService.format(event.getPaoNote().getEditDate(), DateFormatEnum.BOTH, userContext);
            }
            String[] dataRow = new String[] { event.getPaoName(), accessor.getMessage(event.getPaoType()),
                event.getPaoNote().getNoteText(), event.getPaoNote().getCreateUserName(), createDate,
                event.getPaoNote().getEditUserName(), modifiedDate };
            dataRows.add(dataRow);
        }

        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "Notes_" + now + ".csv");
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        PropertyEditor dayStartDateEditor =
            datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.START_OF_DAY, userContext);
        PropertyEditor dayEndDateEditor =
            datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.END_OF_DAY, userContext);
        binder.registerCustomEditor(Date.class, "dateRange.min", dayStartDateEditor);
        binder.registerCustomEditor(Date.class, "dateRange.max", dayEndDateEditor);
    }

    public enum PaoNoteSortBy implements DisplayableEnum {

        deviceName(SortBy.PAO_NAME),
        deviceType(SortBy.PAO_TYPE),
        noteText(SortBy.NOTE_TEXT),
        createdBy(SortBy.CREATE_USERNAME),
        createDate(SortBy.CREATE_DATE),
        editedBy(SortBy.EDIT_USERNAME),
        editDate(SortBy.EDIT_DATE);

        private final SortBy value;

        private PaoNoteSortBy(SortBy value) {
            this.value = value;
        }

        public SortBy getValue() {
            return value;
        }

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }
    
    @RequestMapping(value = "viewAllNotes", method = RequestMethod.GET)
    public String viewAllNotes(ModelMap model, YukonUserContext userContext, int paoId) {
        setupModel(paoId, userContext, model);
        return "paoNote/paoNotesPopup.jsp";
    }

    @CheckAccessLevel(property = YukonRoleProperty.MANAGE_NOTES, level = AccessLevel.OWNER)
    @RequestMapping(value = "deletePaoNote/{noteId}", method = RequestMethod.DELETE)
    public String deletePaoNote(ModelMap model, @PathVariable int noteId, int paoId, YukonUserContext userContext) {
        if (paoNotesService.canUpdateNote(noteId, userContext.getYukonUser())) {
            paoNotesService.delete(noteId);
        }
        setupModel(paoId, userContext, model);
        return "paoNote/paoNotesPopup.jsp";
    }
    
    @CheckAccessLevel(property = YukonRoleProperty.MANAGE_NOTES, level = AccessLevel.OWNER)
    @RequestMapping(value = "editPaoNote", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> editPaoNote(ModelMap model, YukonUserContext userContext, PaoNote paoNote) {
        Map<String, Object> jsonResponse = new HashMap<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        if (!paoNotesService.canUpdateNote(paoNote.getNoteId(), userContext.getYukonUser())) {
            jsonResponse.put("hasError", true);
            jsonResponse.put("errorMessage", accessor.getMessage("yukon.web.error.notOwner"));
        } else if (StringUtils.isBlank(paoNote.getNoteText())) {
            jsonResponse.put("hasError", true);
            jsonResponse.put("errorMessage", accessor.getMessage("yukon.web.error.isBlank"));
        } else if (paoNote.getNoteText().length() > MAX_CHARACTERS_IN_NOTE) {
            jsonResponse.put("hasError", true);
            jsonResponse.put("errorMessage", accessor.getMessage("yukon.web.error.exceedsMaximumLength", MAX_CHARACTERS_IN_NOTE));
        } else {
            jsonResponse.put("hasError", false);
            paoNotesService.edit(paoNote, userContext.getYukonUser());
        }
        return jsonResponse;
    }
    
    @RequestMapping(value = "createPaoNote", method = RequestMethod.POST)
    public String createPaoNote(ModelMap model, YukonUserContext userContext,
            @ModelAttribute("paoNote") PaoNote paoNote, BindingResult result) {
        paoNoteValidator.validate(paoNote, result);
        if (result.hasErrors()) {
            setupModel(paoNote.getPaoId(), userContext, model);
            return "paoNote/paoNotesPopup.jsp";
        }
        paoNotesService.create(paoNote, userContext.getYukonUser());
        return "redirect:viewAllNotes?paoId=" + paoNote.getPaoId();
    }
    
    private void setupModel(int paoId, YukonUserContext userContext, ModelMap model) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        PaoNote createPaoNote = null;
        if (model.containsAttribute("paoNote")) {
            createPaoNote = (PaoNote) model.get("paoNote");
        } else {
            createPaoNote = new PaoNote();
            createPaoNote.setCreateUserName(userContext.getYukonUser().getUsername());
            createPaoNote.setPaoId(paoId);
        }
        model.addAttribute("paoNote", createPaoNote);
        List<PaoNotesSearchResult> searchResults = null;
        if (model.containsAttribute("searchResults")) {
            searchResults = (List<PaoNotesSearchResult>) model.get("searchResults");
        } else {
            searchResults = paoNotesService.getAllNotesByPaoId(paoId).getResultList();
        }
        
        searchResults.stream().forEach(note -> {
            if (paoNotesService.canUpdateNote(note.getPaoNote().getNoteId(), userContext.getYukonUser())) {
                note.setModifiable(true);
            }
         });
        
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("popupTitlePrefix", accessor.getMessage("yukon.web.common.paoNotesPopup.title"));
        model.addAttribute("paoName", databaseCache.getAllPaosMap().get(paoId).getPaoName());
        model.addAttribute("maxCharactersInNote", MAX_CHARACTERS_IN_NOTE);
        model.addAttribute("noteTextAreaMaxLength", NOTE_TEXTAREA_MAX_LENGTH);
    }
    
    @RequestMapping(value = "hasNotes", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> hasNotes(ModelMap model, YukonUserContext userContext, int paoId) {
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("hasNotes", paoNotesService.hasNotes(paoId));
        return jsonResponse;
    }
}
