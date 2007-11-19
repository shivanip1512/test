package com.cannontech.web.picker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.search.YukonObjectCriteria;

public class YukonObjectPickerController extends MultiActionController {
    public YukonObjectPickerController() {
        super();
    }
    
    public ModelAndView initial(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("inner");
        String pickerId = ServletRequestUtils.getStringParameter(request, "pickerId", "");
        mav.addObject("pickerId", pickerId);
        String sameItemLink = ServletRequestUtils.getStringParameter(request, "sameItemLink", "");
        mav.addObject("sameItemLink", sameItemLink);
        
        return mav;
    }
    
    protected YukonObjectCriteria getCriteria(HttpServletRequest request) {
        String criteriaString = ServletRequestUtils.getStringParameter(request, "criteria", "");
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
        return ServletRequestUtils.getIntParameter(request, "count", 20);
    }

    protected int getStartParameter(HttpServletRequest request) {
        return ServletRequestUtils.getIntParameter(request, "start", 0);
    }
}
