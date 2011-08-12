package com.cannontech.web.bulk;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.model.ADAStatus;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("archiveDataAnalysis/list/*")
public class AdaListController {
    private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    
    @RequestMapping
    public String view(ModelMap model) {
        List<Analysis> analyses = archiveDataAnalysisDao.getAllAnalyses();
        
        Map<Analysis, Integer> analysisMap = Maps.newLinkedHashMap();
        for(Analysis analysis : analyses) {
            int numberOfDevices = 0;
            if(analysis.getStatus() != ADAStatus.RUNNING){ 
                numberOfDevices = archiveDataAnalysisDao.getNumberOfDevicesInAnalysis(analysis.getAnalysisId());
            }
            analysisMap.put(analysis, numberOfDevices);
        }
        model.addAttribute("analysisMap", analysisMap);
        
        return "archiveDataAnalysis/list.jsp";
    }
    
    @RequestMapping
    public String delete(ModelMap model, int analysisId) {
        archiveDataAnalysisDao.deleteAnalysis(analysisId);
        
        return "redirect:/spring/bulk/archiveDataAnalysis/list/view";
    }
    
    @Autowired
    public void setArchiveDataAnalysisDao(ArchiveDataAnalysisDao archiveDataAnalysisDao) {
        this.archiveDataAnalysisDao = archiveDataAnalysisDao;
    }
}
