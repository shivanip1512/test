package com.cannontech.web.bulk.ada;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.collection.device.ArchiveDataAnalysisCollectionProducer;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.model.AdaStatus;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.ada.model.ArchiveAnalysisResult;
import com.cannontech.web.bulk.ada.service.AdaResultsHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.ARCHIVED_DATA_ANALYSIS)
@Controller
@RequestMapping("archiveDataAnalysis/results/*")
public class AdaResultsController {
    
    private final static String baseKey = "yukon.web.modules.tools.bulk.analysis";
    private final static int TABULAR_SIZE_LIMIT = 5000; //maximum number of data points before tabular link is disabled
    private final static int BAR_WIDTH = 400;
    
    private static enum Column implements DisplayableEnum {
        NAME, 
        TYPE, 
        METER_NUMBER, 
        MISSING;
        
        @Override
        public String getFormatKey() {
            return "yukon.web.modules.tools.bulk.analysis." + name();
        }
    }

    @Autowired private ArchiveDataAnalysisDao adaDao;
    @Autowired private ArchiveDataAnalysisCollectionProducer adaCollectionProducer;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view(ModelMap model, int analysisId, @DefaultItemsPerPage(10) PagingParameters paging,
            @DefaultSort(dir = Direction.asc, sort = "NAME") SortingParameters sorting, YukonUserContext userContext,
            FlashScope flash) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Analysis analysis = adaDao.getAnalysisById(analysisId);
        ArchiveAnalysisResult result = new ArchiveAnalysisResult(analysis);

        // Warn the user if the analysis was interrupted
        if (analysis.getStatus() == AdaStatus.INTERRUPTED) {
            flash.setWarning(new YukonMessageSourceResolvable(baseKey + ".analysisInterruptedWarning"));
        }

        // Build device collection
        DeviceCollection collection = adaCollectionProducer.buildDeviceCollection(analysisId);
        model.addAttribute("deviceCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        List<DeviceArchiveData> datas = adaDao.getSlotValues(analysisId, paging, sorting);

        // Add sorting columns
        String nameText = accessor.getMessage(Column.NAME);
        SortableColumn nameCol = SortableColumn.of(sorting, nameText, Column.NAME.name());
        model.addAttribute("nameCol", nameCol);

        String typeText = accessor.getMessage(Column.TYPE);
        SortableColumn typeCol = SortableColumn.of(sorting, typeText, Column.TYPE.name());
        model.addAttribute("typeCol", typeCol);

        String meterNumberText = accessor.getMessage(Column.METER_NUMBER);
        SortableColumn meterNumberCol = SortableColumn.of(sorting, meterNumberText, Column.METER_NUMBER.name());
        model.addAttribute("meterNumberCol", meterNumberCol);

        String invervalsText = accessor.getMessage(Column.MISSING);
        SortableColumn intervalsCol = SortableColumn.of(sorting, invervalsText, Column.MISSING.name());
        model.addAttribute("intervalsCol", intervalsCol);

        int totalHitCount = adaDao.getNumberOfDevicesInAnalysis(analysisId);
        SearchResults<DeviceArchiveData> searchResult = SearchResults.pageBasedForSublist(datas, paging, totalHitCount);
        result.setSearchResult(searchResult);

        // Build bars for this page
        AdaResultsHelper.buildBars(analysis, BAR_WIDTH, searchResult.getResultList());

        model.addAttribute("barWidth", BAR_WIDTH);

        LiteYukonUser user = userContext.getYukonUser();
        boolean profileCollection = rolePropertyDao.checkProperty(YukonRoleProperty.PROFILE_COLLECTION, user);
        if (analysis.getAttribute().isReadableProfile() && profileCollection) {
            model.addAttribute("showReadOption", true);
        }

        int numberOfIntervals = datas.get(0).getNumberOfIntervals();
        model.addAttribute("intervals", numberOfIntervals);

        int numberOfDataPoints = datas.size() * numberOfIntervals;
        boolean underTabularSizeLimit = true;
        if (numberOfDataPoints > TABULAR_SIZE_LIMIT) {
            underTabularSizeLimit = false;
        }
        model.addAttribute("underTabularSizeLimit", underTabularSizeLimit);

        model.addAttribute("result", result);
        return "archiveDataAnalysis/results.jsp";
    }
    
}