package com.cannontech.web.tools.paoNote;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
import com.cannontech.common.pao.notes.dao.PaoNotesDao.SortBy;
import com.cannontech.common.pao.notes.filter.model.PaoNotesFilter;
import com.cannontech.common.pao.notes.filter.model.PaoSelectionMethod;
import com.cannontech.common.pao.notes.search.result.model.PaoNotesSearchResult;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/paoNote/*")
public class PaoNoteSearchController {

    @Autowired private PaoNotesService paoNotesService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DateFormattingService dateFormattingService;

    private static final String baseKey = "yukon.web.common.paoNote.";

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String search(@ModelAttribute("paoNoteFilter") PaoNotesFilter filter, ModelMap model,
            @DefaultSort(dir = Direction.desc, sort = "createDate") SortingParameters sorting,
            @DefaultItemsPerPage(value = 25) PagingParameters paging, YukonUserContext userContext,
            String deviceGroupNames) {
        
        if (StringUtils.isNotEmpty(deviceGroupNames)) {
            List<DeviceGroup> deviceGroups = Lists.newArrayList();
            for(String deviceGroupName : deviceGroupNames.split(",")) {
                deviceGroups.add(deviceGroupService.resolveGroupName(deviceGroupName));
            }
            filter.setDeviceGroups(deviceGroups);
            model.addAttribute("deviceGroupNames", deviceGroupNames);
        }

        SearchResults<PaoNotesSearchResult> searchResults = paoNotesService.getAllNotesByFilter(filter,
            PaoNoteSortBy.valueOf(sorting.getSort()).getValue(), sorting.getDirection(), paging);
        model.addAttribute("searchResults", searchResults);
        
        if (filter.getPaoSelectionMethod() != PaoSelectionMethod.selectIndividually) {
            filter.setPaoIds(null);
        }

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

        model.addAttribute("paoSelectionMethods", PaoSelectionMethod.values());
        return "paoNote/list.jsp";
    }

    @RequestMapping(value = "download", method = RequestMethod.GET)
    public void download(@ModelAttribute("paoNoteFilter") PaoNotesFilter filter,
            @DefaultSort(dir = Direction.desc, sort = "createDate") SortingParameters sorting,
            @DefaultItemsPerPage(value = 25) PagingParameters paging, YukonUserContext userContext,
            HttpServletResponse response) throws IOException {
        SearchResults<PaoNotesSearchResult> searchResults =
            paoNotesService.getAllNotesByFilter(filter, SortBy.CREATE_DATE, sorting.getDirection(), paging);

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String deviceNameHeader = accessor.getMessage(PaoNoteSortBy.deviceName);
        String typeHeader = accessor.getMessage(PaoNoteSortBy.type);
        String noteTextHeader = accessor.getMessage(PaoNoteSortBy.noteText);
        String createdByHeader = accessor.getMessage(PaoNoteSortBy.createdBy);
        String createDateHeader = accessor.getMessage(PaoNoteSortBy.createDate);
        String modifiedByHeader = accessor.getMessage(PaoNoteSortBy.modifiedBy);
        String modifiedDateHeader = accessor.getMessage(PaoNoteSortBy.modifiedDate);
        String[] headerRow = new String[] { deviceNameHeader, typeHeader, noteTextHeader, createdByHeader,
            createDateHeader, modifiedByHeader, modifiedDateHeader };

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

        WebFileUtils.writeToCSV(response, headerRow, dataRows, "Notes.csv");
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    public enum PaoNoteSortBy implements DisplayableEnum {

        deviceName(SortBy.PAO_NAME),
        type(SortBy.PAO_TYPE),
        noteText(SortBy.NOTE_TEXT),
        createdBy(SortBy.CREATE_USERNAME),
        createDate(SortBy.CREATE_DATE),
        modifiedBy(SortBy.EDIT_USERNAME),
        modifiedDate(SortBy.EDIT_DATE);

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
}
