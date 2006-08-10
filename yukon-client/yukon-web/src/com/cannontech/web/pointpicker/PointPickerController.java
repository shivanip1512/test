package com.cannontech.web.pointpicker;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.search.PointDeviceCriteria;
import com.cannontech.common.search.PointDeviceSearcher;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPoint;
import com.cannontech.database.data.point.PointTypes;

public class PointPickerController extends MultiActionController {
    private PointDeviceSearcher pointDeviceSearcher;

    public PointPickerController() {
        super();
    }
    
    public ModelAndView initial(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("inner");
        int currentPointId = RequestUtils.getIntParameter(request, "currentPointId", PointTypes.INVALID_POINT);
        PointDeviceCriteria criteria = getCriteria(request);
        int start = getStartParameter(request);
        int count = getCountParameter(request);
        SearchResult<UltraLightPoint> hits = pointDeviceSearcher.sameDevicePoints(currentPointId, criteria, start, count);
        processHitList(mav, hits);
        mav.addObject("showAll", false);
        
        return mav;
    }

    private PointDeviceCriteria getCriteria(HttpServletRequest request) {
        String criteriaString = RequestUtils.getStringParameter(request, "criteria", "");
        PointDeviceCriteria criteria = null;
        if (StringUtils.isNotBlank(criteriaString)) {
            try {
                Class criteriaClass = getClass().getClassLoader().loadClass(criteriaString);
                criteria = (PointDeviceCriteria) criteriaClass.newInstance();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return criteria;
    }

    private int getCountParameter(HttpServletRequest request) {
        return RequestUtils.getIntParameter(request, "count", 20);
    }

    private int getStartParameter(HttpServletRequest request) {
        return RequestUtils.getIntParameter(request, "start", 0);
    }

    public ModelAndView showAll(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("results");
        int start = getStartParameter(request);
        int count = getCountParameter(request);
        PointDeviceCriteria criteria = getCriteria(request);
        SearchResult<UltraLightPoint> hits = pointDeviceSearcher.allPoints(criteria, start, count);
        processHitList(mav, hits);
        mav.addObject("showAll", true);
        
        return mav;
    }
    
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ModelAndView mav = new ModelAndView("results");
        String queryString = RequestUtils.getStringParameter(request, "ss", "");
        int start = getStartParameter(request);
        int count = getCountParameter(request);
        PointDeviceCriteria criteria = getCriteria(request);
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

    private void processHitList(ModelAndView mav, SearchResult<UltraLightPoint> hits) {
        List<UltraLightPoint> hitList = hits.getResultList();
        
        mav.addObject("hitList", hitList);
        mav.addObject("hitCount", hits.getHitCount());
        mav.addObject("resultCount", hits.getResultCount());
        mav.addObject("startIndex", hits.getStartIndex());
        mav.addObject("endIndex", hits.getEndIndex());
        mav.addObject("previousIndex", Math.max(0, hits.getStartIndex() - hits.getResultCount() ));
        mav.addObject("nextIndex", hits.getEndIndex());
    }
    
//    public ModelAndView initializeIndex(HttpServletRequest request, HttpServletResponse response) throws ServletException {
//        if (pointDeviceSearcher instanceof PointDeviceLuceneSearcher) {
//            Runnable task = new Runnable() {
//                public void run() {
//                    ((PointDeviceLuceneSearcher) pointDeviceSearcher).createInitialIndex();
//                };
//            };
//            new Thread(task).start();
//        }
//        return null;
//    }

    public PointDeviceSearcher getPointDeviceSearcher() {
        return pointDeviceSearcher;
    }

    public void setPointDeviceSearcher(PointDeviceSearcher pointDeviceSearcher) {
        this.pointDeviceSearcher = pointDeviceSearcher;
    }

}
