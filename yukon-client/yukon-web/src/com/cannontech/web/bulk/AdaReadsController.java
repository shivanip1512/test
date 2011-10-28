package com.cannontech.web.bulk;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.device.ArchiveDataAnalysisCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.model.ArchiveAnalysisProfileReadResult;
import com.cannontech.common.bulk.service.ArchiveDataAnalysisService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.PROFILE_COLLECTION)
@Controller
@RequestMapping("archiveDataAnalysis/read/*")
public class AdaReadsController {
    private ArchiveDataAnalysisService archiveDataAnalysisService;
    private ArchiveDataAnalysisCollectionProducer adaCollectionProducer;
    
    @RequestMapping
    public String readNow(ModelMap model, HttpServletRequest request, LiteYukonUser user, int analysisId) throws ServletRequestBindingException {
        String resultId = archiveDataAnalysisService.runProfileReads(analysisId, user);
        
        model.addAttribute("resultId", resultId);
        model.addAttribute("analysisId", analysisId);
        
        return "redirect:readResults";
    }
    
    @RequestMapping
    public String readResults(ModelMap model, HttpServletRequest request, int analysisId, String resultId) {
        model.addAttribute("analysisId", analysisId);
        
        ArchiveAnalysisProfileReadResult result = archiveDataAnalysisService.getProfileReadResultById(resultId);
        model.addAttribute("resultId", resultId);
        model.addAttribute("result", result);
        
        DeviceCollection collection = adaCollectionProducer.buildDeviceCollection(analysisId);
        model.addAttribute("deviceCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        
        int deviceCount = result.getTotalCount();
        model.addAttribute("deviceCount", deviceCount);
        
        return "archiveDataAnalysis/readResults.jsp";
    }
    
    @Autowired
    public void setArchiveDataAnalysisService(ArchiveDataAnalysisService archiveDataAnalysisService) {
        this.archiveDataAnalysisService = archiveDataAnalysisService;
    }
    
    @Autowired
    public void setDeviceIdListCollectionProducer(ArchiveDataAnalysisCollectionProducer adaCollectionProducer) {
        this.adaCollectionProducer = adaCollectionProducer;
    }
}
