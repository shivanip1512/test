package com.cannontech.web.bulk;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.callbackResult.ArchiveDataAnalysisCallbackResult;
import com.cannontech.common.bulk.model.AdaStatus;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Maps;

@CheckRoleProperty(YukonRoleProperty.ARCHIVED_DATA_ANALYSIS)
@Controller
@RequestMapping("archiveDataAnalysis/list/*")
public class AdaListController {
    @Resource(name="recentResultsCache") private RecentResultsCache<ArchiveDataAnalysisCallbackResult> recentResultsCache;
    @Autowired private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    
    @RequestMapping
    public String view(ModelMap model) {
        List<Analysis> analyses = archiveDataAnalysisDao.getAllAnalyses();
        
        Map<Analysis, Integer> analysisMap = Maps.newLinkedHashMap();
        for(Analysis analysis : analyses) {
            if(analysis.getStatus().isRunning() && isAnalysisInterrupted(analysis)) {
                archiveDataAnalysisDao.updateStatus(analysis.getAnalysisId(), AdaStatus.INTERRUPTED, null);
                analysis.setStatus(AdaStatus.INTERRUPTED);
            }
            
            int numberOfDevices = archiveDataAnalysisDao.getNumberOfDevicesInAnalysis(analysis.getAnalysisId());
            analysisMap.put(analysis, numberOfDevices);
        }
        model.addAttribute("analysisMap", analysisMap);
        
        return "archiveDataAnalysis/list.jsp";
    }
    
    @RequestMapping
    public String delete(ModelMap model, int analysisId) {
        archiveDataAnalysisDao.deleteAnalysis(analysisId);
        
        return "redirect:/bulk/archiveDataAnalysis/list/view";
    }
    
    private boolean isAnalysisInterrupted(Analysis analysis) {
        if(analysis.getStatus().isRunning()) {
            ArchiveDataAnalysisCallbackResult callbackResult = recentResultsCache.getResult(analysis.getStatusId());
            return callbackResult == null;
        }
        return false;
    }
}
