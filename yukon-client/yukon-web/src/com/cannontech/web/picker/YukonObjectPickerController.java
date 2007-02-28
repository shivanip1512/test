package com.cannontech.web.picker;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.search.YukonObjectCriteria;
import com.cannontech.common.search.SearchResult;

public class YukonObjectPickerController extends MultiActionController {
    public YukonObjectPickerController() {
        super();
    }
    
    public ModelAndView initial(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("inner");
        String pickerId = RequestUtils.getStringParameter(request, "pickerId", "");
        mav.addObject("pickerId", pickerId);
        String sameItemLink = RequestUtils.getStringParameter(request, "sameItemLink", "");
        mav.addObject("sameItemLink", sameItemLink);
        
        return mav;
    }
    
    protected YukonObjectCriteria getCriteria(HttpServletRequest request) {
        String criteriaString = RequestUtils.getStringParameter(request, "criteria", "");
        YukonObjectCriteria criteria = null;
        if (StringUtils.isNotBlank(criteriaString)) {
            try {
                Class criteriaClass = getClass().getClassLoader().loadClass(criteriaString);
                criteria = (YukonObjectCriteria) criteriaClass.newInstance();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return criteria;
    }

    protected int getCountParameter(HttpServletRequest request) {
        return RequestUtils.getIntParameter(request, "count", 20);
    }

    protected int getStartParameter(HttpServletRequest request) {
        return RequestUtils.getIntParameter(request, "start", 0);
    }
}
