package com.cannontech.web.bulk;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.bulk.model.ArchiveAnalysisProfileReadResult;
import com.cannontech.web.bulk.model.collection.ArchiveDataAnalysisCollectionProducer;
import com.cannontech.web.bulk.service.ArchiveDataAnalysisService;

@Controller
public class AdaReadsController {
    ArchiveDataAnalysisService archiveDataAnalysisService;
    ArchiveDataAnalysisDao archiveDataAnalysisDao;
    ArchiveDataAnalysisCollectionProducer adaCollectionProducer;
    
    @RequestMapping("archiveDataAnalysis/readNow")
    public String readNow(ModelMap model, HttpServletRequest request, LiteYukonUser user, int analysisId) throws ServletRequestBindingException {
        model.addAttribute("analysisId", analysisId);
        String resultId = archiveDataAnalysisService.runProfileReads(analysisId, user);
        ArchiveAnalysisProfileReadResult result = archiveDataAnalysisService.getProfileReadResultById(resultId);
        model.addAttribute("resultId", resultId);
        model.addAttribute("result", result);
        
        DeviceCollection collection = adaCollectionProducer.createDeviceCollection(request);
        model.addAttribute("deviceCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        
        long deviceCount = collection.getDeviceCount();
        model.addAttribute("deviceCount", deviceCount);
        
        return "archiveDataAnalysis/readResults.jsp";
    }
    
    @Autowired
    public void setArchiveDataAnalysisService(ArchiveDataAnalysisService archiveDataAnalysisService) {
        this.archiveDataAnalysisService = archiveDataAnalysisService;
    }
    
    @Autowired
    public void setArchiveDataAnalysisDao(ArchiveDataAnalysisDao archiveDataAnalysisDao) {
        this.archiveDataAnalysisDao = archiveDataAnalysisDao;
    }
    
    @Autowired
    public void setDeviceIdListCollectionProducer(ArchiveDataAnalysisCollectionProducer adaCollectionProducer) {
        this.adaCollectionProducer = adaCollectionProducer;
    }
}
