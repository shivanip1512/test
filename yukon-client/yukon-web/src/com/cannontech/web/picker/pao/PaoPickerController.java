package com.cannontech.web.picker.pao;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.search.PaoTypeSearcher;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.search.YukonObjectCriteria;
import com.cannontech.web.picker.YukonObjectPickerController;
import com.cannontech.web.util.JsonView;

public class PaoPickerController extends YukonObjectPickerController {
    private PaoTypeSearcher paoTypeSearcher;

    public PaoPickerController() {
        super();
    }
    
    public ModelAndView sameType(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView(new JsonView());
        int currentPaoId = ServletRequestUtils.getIntParameter(request, "currentPaoId", 0);
        YukonObjectCriteria criteria = getCriteria(request);
        int start = getStartParameter(request);
        int count = getCountParameter(request);
        SearchResult<UltraLightPao> hits = paoTypeSearcher.sameTypePaos(currentPaoId, criteria, start, count);
        processHitList(mav, hits);
        mav.addObject("showAll", false);
        
        return mav;
    }

    public ModelAndView showAll(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView(new JsonView());
        int start = getStartParameter(request);
        int count = getCountParameter(request);
        YukonObjectCriteria criteria = getCriteria(request);
        SearchResult<UltraLightPao> hits = paoTypeSearcher.allPaos(criteria, start, count);
        processHitList(mav, hits);
        mav.addObject("showAll", true);
        
        return mav;
    }
    
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ModelAndView mav = new ModelAndView(new JsonView());
        String queryString = ServletRequestUtils.getStringParameter(request, "ss", "");
        int start = getStartParameter(request);
        int count = getCountParameter(request);
        YukonObjectCriteria criteria = getCriteria(request);
        boolean blank = StringUtils.isBlank(queryString);
        SearchResult<UltraLightPao> hits;
        if (blank) {
            hits = paoTypeSearcher.allPaos(criteria, start, count);
        } else {
            hits = paoTypeSearcher.search(queryString, criteria, start , count);
        }
        processHitList(mav, hits);
        mav.addObject("showAll", false);
        
        return mav;
    }

    public PaoTypeSearcher getPaoTypeSearcher() {
        return paoTypeSearcher;
    }

    public void setPaoTypeSearcher(PaoTypeSearcher paoTypeSearcher) {
        this.paoTypeSearcher = paoTypeSearcher;
    }

    protected void processHitList(ModelAndView mav, SearchResult<UltraLightPao> hits) {
        List<UltraLightPao> hitList = hits.getResultList();
        
        mav.addObject("hitList", hitList);
        mav.addObject("hitCount", hits.getHitCount());
        mav.addObject("resultCount", hits.getResultCount());
        mav.addObject("startIndex", hits.getStartIndex());
        mav.addObject("endIndex", hits.getEndIndex());
        mav.addObject("previousIndex", hits.getPreviousStartIndex());
        mav.addObject("nextIndex", hits.getEndIndex());
    }
}
