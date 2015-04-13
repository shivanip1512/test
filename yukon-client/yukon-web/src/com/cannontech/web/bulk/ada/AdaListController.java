package com.cannontech.web.bulk.ada;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.bulk.callbackResult.ArchiveDataAnalysisCallbackResult;
import com.cannontech.common.bulk.model.AdaStatus;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.AnalysisWithDeviceCount;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@CheckRoleProperty(YukonRoleProperty.ARCHIVED_DATA_ANALYSIS)
@Controller
@RequestMapping("archiveDataAnalysis/list/*")
public class AdaListController {
    
    @Resource(name="recentResultsCache") private RecentResultsCache<ArchiveDataAnalysisCallbackResult> recentResultsCache;
    @Autowired private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    
    @RequestMapping("view")
    public String view(ModelMap model, @RequestParam(defaultValue="25") int itemsPerPage,
                       @RequestParam(defaultValue="1") int page) {
        setUpList(model, page, itemsPerPage);
        return "archiveDataAnalysis/list.jsp";
    }
    
    @RequestMapping("page")
    public String page(ModelMap model, @RequestParam(defaultValue="25") int itemsPerPage,
                       @RequestParam(defaultValue="1") int page) {
        setUpList(model, page, itemsPerPage);
        return "archiveDataAnalysis/listFragment.jsp";
    }
    
    @RequestMapping("delete")
    public String delete(ModelMap model, FlashScope flashScope, int analysisId) {
        archiveDataAnalysisDao.deleteAnalysis(analysisId);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.tools.bulk.analysis.list.deleted"));
        return "redirect:view";
    }
    
    /**
     * Attempts to determine if an analysis has been interrupted (typically by a server shutdown). An analysis is
     * considered interrupted if its status is "running", but there is no callback result available for it in the
     * recent results cache.
     */
    private boolean isAnalysisInterrupted(Analysis analysis) {
        if(analysis.getStatus().isRunning()) {
            ArchiveDataAnalysisCallbackResult callbackResult = recentResultsCache.getResult(analysis.getStatusId());
            return callbackResult == null;
        }
        return false;
    }
    
    /**
     * Retrieves the list of analyses based on the current page and itemsPerPage, generates a SearchResult, and adds it
     * to the model.
     */
    private void setUpList(ModelMap model, int page, int itemsPerPage) {
        PagingParameters pagingParameter = PagingParameters.of(itemsPerPage, page);
        List<Analysis> analyses = archiveDataAnalysisDao.getAllNotDeletedAnalyses(pagingParameter);
        List<AnalysisWithDeviceCount> analysisSublist = Lists.newArrayList();
        for(Analysis analysis : analyses) {
            if(isAnalysisInterrupted(analysis)) {
                archiveDataAnalysisDao.updateStatus(analysis.getAnalysisId(), AdaStatus.INTERRUPTED, null);
                analysis.setStatus(AdaStatus.INTERRUPTED);
            }
            
            int numberOfDevices = archiveDataAnalysisDao.getNumberOfDevicesInAnalysis(analysis.getAnalysisId());
            analysisSublist.add(new AnalysisWithDeviceCount(analysis, numberOfDevices));
        }
        
        int totalAdaCount = archiveDataAnalysisDao.getTotalAdaCount();
        SearchResults<AnalysisWithDeviceCount> result =
            SearchResults.pageBasedForSublist(analysisSublist, page, itemsPerPage, totalAdaCount);
        model.addAttribute("result", result);
    }
}
