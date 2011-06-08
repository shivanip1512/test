package com.cannontech.web.bulk;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.web.bulk.model.ArchiveAnalysisResult;
import com.cannontech.web.bulk.model.DeviceCollectionCreationException;
import com.cannontech.web.bulk.model.collection.DeviceIdListCollectionProducer;
import com.cannontech.web.bulk.service.AdaResultsHelper;
import com.google.common.collect.Lists;

@Controller
public class AdaResultsController {
    private final static int BAR_WIDTH = 400;
    private AdaResultsHelper adaResultsHelper;
    private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    private DeviceIdListCollectionProducer deviceIdListCollectionProducer;
    
    @RequestMapping("archiveDataAnalysis/results")
    public String view(ModelMap model, int analysisId, HttpServletRequest request) throws ServletRequestBindingException, DeviceCollectionCreationException {
        
        Analysis analysis = archiveDataAnalysisDao.getAnalysisById(analysisId);
        List<DeviceArchiveData> data = archiveDataAnalysisDao.getSlotValues(analysisId);
        
        ArchiveAnalysisResult result = adaResultsHelper.buildResults(analysis, BAR_WIDTH, data, request);
        
        if (analysis.getAttribute().isProfile()) {
            model.addAttribute("showReadOption", true);
        }
        
        //Generate device collection from device ids list
        List<Integer> idList = Lists.newArrayList();
        for(DeviceArchiveData deviceData : data) {
            int id = deviceData.getId().getPaoId();
            idList.add(id);
        }
        DeviceCollection collection = deviceIdListCollectionProducer.createDeviceCollection(idList);
        
        int numberOfIntervals = data.get(0).getNumberOfIntervals();
        
        model.addAttribute("intervals", numberOfIntervals);
        model.addAttribute("deviceCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        model.addAttribute("result", result);
        model.addAttribute("barWidth", BAR_WIDTH);
        
        return "intervalDataAnalysis/results.jsp";
    }

    @Autowired
    public void setAdaResultsHelper(AdaResultsHelper adaResultsHelper) {
        this.adaResultsHelper = adaResultsHelper;
    }
    
    @Autowired
    public void setArchiveDataAnalysisDao(ArchiveDataAnalysisDao archiveDataAnalysisDao) {
        this.archiveDataAnalysisDao = archiveDataAnalysisDao;
    }
    
    @Autowired
    public void setDeviceIdListCollectionProducer(DeviceIdListCollectionProducer deviceIdListCollectionProducer) {
        this.deviceIdListCollectionProducer = deviceIdListCollectionProducer;
    }
}