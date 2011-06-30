package com.cannontech.web.bulk;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.web.bulk.model.ArchiveAnalysisResult;
import com.cannontech.web.bulk.model.DeviceCollectionCreationException;
import com.cannontech.web.bulk.model.collection.ArchiveDataAnalysisCollectionProducer;
import com.cannontech.web.bulk.service.AdaResultsHelper;

@Controller
@RequestMapping("archiveDataAnalysis/results/*")
public class AdaResultsController {
    private final static int TABULAR_SIZE_LIMIT = 5000; //maximum number of data points before tabular link is disabled
    private final static int BAR_WIDTH = 400;
    private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    private ArchiveDataAnalysisCollectionProducer adaCollectionProducer;
    
    @RequestMapping
    public String view(ModelMap model, int analysisId, HttpServletRequest request) throws ServletRequestBindingException, DeviceCollectionCreationException {
        Analysis analysis = archiveDataAnalysisDao.getAnalysisById(analysisId);
        ArchiveAnalysisResult result = new ArchiveAnalysisResult(analysis);
        
        // Build device collection
        DeviceCollection collection = adaCollectionProducer.buildDeviceCollection(analysisId);
        model.addAttribute("deviceCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        
        // Page the result
        List<PaoIdentifier> deviceIds = archiveDataAnalysisDao.getRelevantDeviceIds(analysisId);
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 25);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
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
        
        if (analysis.getAttribute().isProfile()) {
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
    
    @Autowired
    public void setArchiveDataAnalysisDao(ArchiveDataAnalysisDao archiveDataAnalysisDao) {
        this.archiveDataAnalysisDao = archiveDataAnalysisDao;
    }
    
    @Autowired
    public void setArchiveDataAnalysisCollectionProducer(ArchiveDataAnalysisCollectionProducer adaCollectionProducer) {
        this.adaCollectionProducer = adaCollectionProducer;
    }
}