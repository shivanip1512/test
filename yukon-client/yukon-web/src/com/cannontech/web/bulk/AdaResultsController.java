package com.cannontech.web.bulk;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.bulk.collection.device.ArchiveDataAnalysisCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.model.ArchiveAnalysisResult;
import com.cannontech.web.bulk.service.AdaResultsHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.ARCHIVED_DATA_ANALYSIS)
@Controller
@RequestMapping("archiveDataAnalysis/results/*")
public class AdaResultsController {
    
    private final static int TABULAR_SIZE_LIMIT = 5000; //maximum number of data points before tabular link is disabled
    private final static int BAR_WIDTH = 400;
    
    @Autowired private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    @Autowired private ArchiveDataAnalysisCollectionProducer adaCollectionProducer;
    @Autowired private RolePropertyDao rolePropertyDao;
    
    @RequestMapping
    public String view(ModelMap model, int analysisId,
            @RequestParam(defaultValue="25") int itemsPerPage, 
            @RequestParam(defaultValue="1") int currentPage, 
            YukonUserContext userContext, FlashScope flashScope) throws ServletRequestBindingException, DeviceCollectionCreationException {
        Analysis analysis = archiveDataAnalysisDao.getAnalysisById(analysisId);
        ArchiveAnalysisResult result = new ArchiveAnalysisResult(analysis);
        
        // Warn the user if the analysis was interrupted
        flashScope.setWarning(new YukonMessageSourceResolvable("yukon.web.modules.tools.bulk.analysis.analysisInterruptedWarning"));
        
        // Build device collection
        DeviceCollection collection = adaCollectionProducer.buildDeviceCollection(analysisId);
        model.addAttribute("deviceCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        
        // Page the result
        List<PaoIdentifier> deviceIds = archiveDataAnalysisDao.getRelevantDeviceIds(analysisId);
        int startIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = startIndex + itemsPerPage;
        int numberOfResults = deviceIds.size();
        if (numberOfResults < toIndex) toIndex = numberOfResults;
        
        // Build bars for this page of devices
        deviceIds = deviceIds.subList(startIndex, toIndex);
        List<DeviceArchiveData> data = archiveDataAnalysisDao.getSlotValues(analysisId, deviceIds);
        AdaResultsHelper.buildBars(analysis, BAR_WIDTH, data);
        
        SearchResult<DeviceArchiveData> searchResult = new SearchResult<DeviceArchiveData>();
        searchResult.setResultList(data);
        searchResult.setBounds(startIndex, itemsPerPage, numberOfResults);
        result.setSearchResult(searchResult);
        
        model.addAttribute("barWidth", BAR_WIDTH);
        
        if (analysis.getAttribute().isReadableProfile() && rolePropertyDao.checkProperty(YukonRoleProperty.PROFILE_COLLECTION, userContext.getYukonUser())) {
            model.addAttribute("showReadOption", true);
        }
        
        int numberOfIntervals = data.get(0).getNumberOfIntervals();
        model.addAttribute("intervals", numberOfIntervals);
        
        int numberOfDataPoints = data.size() * numberOfIntervals;
        boolean underTabularSizeLimit = true;
        if(numberOfDataPoints > TABULAR_SIZE_LIMIT) {
            underTabularSizeLimit = false;
        }
        model.addAttribute("underTabularSizeLimit", underTabularSizeLimit);
        
        model.addAttribute("result", result);
        
        return "archiveDataAnalysis/results.jsp";
    }
    
}