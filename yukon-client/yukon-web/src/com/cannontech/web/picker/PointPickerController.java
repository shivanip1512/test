package com.cannontech.web.picker;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.search.PointDeviceSearcher;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPoint;
import com.cannontech.common.search.YukonObjectCriteria;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.web.util.JsonView;

public class PointPickerController extends YukonObjectPickerController {
    private PointDeviceSearcher pointDeviceSearcher;

    public PointPickerController() {
        super();
    }
    
    public ModelAndView sameDevice(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView(new JsonView());
        int currentPointId = ServletRequestUtils.getIntParameter(request, "currentPointId", PointTypes.INVALID_POINT);
        YukonObjectCriteria criteria = getCriteria(request);
        int start = getStartParameter(request);
        int count = getCountParameter(request);
        SearchResult<UltraLightPoint> hits = pointDeviceSearcher.sameDevicePoints(currentPointId, criteria, start, count);
        processHitList(mav, hits);
        mav.addObject("showAll", false);
        
        return mav;
    }

    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ModelAndView mav = new ModelAndView(new JsonView());
        String queryString = ServletRequestUtils.getStringParameter(request, "ss", "");
        int start = getStartParameter(request);
        int count = getCountParameter(request);
        YukonObjectCriteria criteria = getCriteria(request);
        boolean blank = StringUtils.isBlank(queryString);
        SearchResult<UltraLightPoint> hits;
        if (blank) {
            hits = pointDeviceSearcher.allPoints(criteria, start, count);
        } else {
            hits = pointDeviceSearcher.search(queryString, criteria, start , count);
        }
        processHitList(mav, hits);
        mav.addObject("showAll", false);
        
        return mav;
    }

    public PointDeviceSearcher getPointDeviceSearcher() {
        return pointDeviceSearcher;
    }

    public void setPointDeviceSearcher(PointDeviceSearcher pointDeviceSearcher) {
        this.pointDeviceSearcher = pointDeviceSearcher;
    }

    protected void processHitList(ModelAndView mav, SearchResult<UltraLightPoint> hits) {
        List<UltraLightPoint> hitList = hits.getResultList();
        
        mav.addObject("hitList", hitList);
        mav.addObject("hitCount", hits.getHitCount());
        mav.addObject("resultCount", hits.getResultCount());
        mav.addObject("startIndex", hits.getStartIndex());
        mav.addObject("endIndex", hits.getEndIndex());
        mav.addObject("previousIndex", hits.getPreviousStartIndex());
        mav.addObject("nextIndex", hits.getEndIndex());
    }
}
