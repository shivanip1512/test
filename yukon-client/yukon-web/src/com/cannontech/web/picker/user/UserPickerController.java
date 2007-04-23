package com.cannontech.web.picker.user;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightYukonUser;
import com.cannontech.common.search.UserSearcher;
import com.cannontech.common.search.YukonObjectCriteria;
import com.cannontech.web.picker.YukonObjectPickerController;

public class UserPickerController extends YukonObjectPickerController {
    private UserSearcher userSearcher;

    public UserPickerController() {
        super();
    }
    
    public ModelAndView showAll(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("json");
        int start = getStartParameter(request);
        int count = getCountParameter(request);
        YukonObjectCriteria criteria = getCriteria(request);
        SearchResult<UltraLightYukonUser> hits = userSearcher.allUsers(criteria, start, count);
        processHitList(mav, hits);
        mav.addObject("showAll", true);
        
        return mav;
    }
    
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ModelAndView mav = new ModelAndView("json");
        String queryString = RequestUtils.getStringParameter(request, "ss", "");
        int start = getStartParameter(request);
        int count = getCountParameter(request);
        YukonObjectCriteria criteria = getCriteria(request);
        boolean blank = StringUtils.isBlank(queryString);
        SearchResult<UltraLightYukonUser> hits;
        if (blank) {
            hits = userSearcher.allUsers(criteria, start, count);
        } else {
            hits = userSearcher.search(queryString, criteria, start , count);
        }
        processHitList(mav, hits);
        mav.addObject("showAll", false);
        
        return mav;
    }

    public UserSearcher getUserSearcher() {
        return userSearcher;
    }

    public void setUserSearcher(UserSearcher userSearcher) {
        this.userSearcher = userSearcher;
    }

    protected void processHitList(ModelAndView mav, SearchResult<UltraLightYukonUser> hits) {
        List<UltraLightYukonUser> hitList = hits.getResultList();
        
        mav.addObject("hitList", hitList);
        mav.addObject("hitCount", hits.getHitCount());
        mav.addObject("resultCount", hits.getResultCount());
        mav.addObject("startIndex", hits.getStartIndex());
        mav.addObject("endIndex", hits.getEndIndex());
        mav.addObject("previousIndex", hits.getPreviousStartIndex());
        mav.addObject("nextIndex", hits.getEndIndex());
    }
}
