package com.cannontech.web.picker.user;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.search.UltraLightLoginGroup;
import com.cannontech.common.search.LoginGroupSearcher;
import com.cannontech.common.search.YukonObjectCriteria;
import com.cannontech.common.search.SearchResult;
import com.cannontech.web.picker.YukonObjectPickerController;
import com.cannontech.web.util.JsonView;

public class LoginGroupPickerController extends YukonObjectPickerController {
    private LoginGroupSearcher loginGroupSearcher;

    public LoginGroupPickerController() {
        super();
    }
    
    public ModelAndView showAll(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView(new JsonView());
        int start = getStartParameter(request);
        int count = getCountParameter(request);
        YukonObjectCriteria criteria = getCriteria(request);
        SearchResult<UltraLightLoginGroup> hits = loginGroupSearcher.allLoginGroups(criteria, start, count);
        processHitList(mav, hits);
        mav.addObject("showAll", true);
        
        return mav;
    }
    
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ModelAndView mav = new ModelAndView(new JsonView());
        String queryString = RequestUtils.getStringParameter(request, "ss", "");
        int start = getStartParameter(request);
        int count = getCountParameter(request);
        YukonObjectCriteria criteria = getCriteria(request);
        boolean blank = StringUtils.isBlank(queryString);
        SearchResult<UltraLightLoginGroup> hits;
        if (blank) {
            hits = loginGroupSearcher.allLoginGroups(criteria, start, count);
        } else {
            hits = loginGroupSearcher.search(queryString, criteria, start , count);
        }
        processHitList(mav, hits);
        mav.addObject("showAll", false);
        
        return mav;
    }

    public LoginGroupSearcher getLoginGroupSearcher() {
        return loginGroupSearcher;
    }

    public void setLoginGroupSearcher(LoginGroupSearcher loginGroupSearcher) {
        this.loginGroupSearcher = loginGroupSearcher;
    }

    protected void processHitList(ModelAndView mav, SearchResult<UltraLightLoginGroup> hits) {
        List<UltraLightLoginGroup> hitList = hits.getResultList();
        
        mav.addObject("hitList", hitList);
        mav.addObject("hitCount", hits.getHitCount());
        mav.addObject("resultCount", hits.getResultCount());
        mav.addObject("startIndex", hits.getStartIndex());
        mav.addObject("endIndex", hits.getEndIndex());
        mav.addObject("previousIndex", hits.getPreviousStartIndex());
        mav.addObject("nextIndex", hits.getEndIndex());
    }
}
