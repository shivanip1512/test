package com.cannontech.web.picker.dr;

import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.search.PaoTypeSearcher;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.search.YukonObjectCriteria;
import com.cannontech.web.util.JsonView;

@Controller
@RequestMapping("/dr/*")
public class DemandResponsePickerController {
    private PaoTypeSearcher paoTypeSearcher;

    private YukonObjectCriteria getCriteria(String criteriaStr) {
        if (StringUtils.isNotBlank(criteriaStr)) {
            try {
                Class<?> criteriaClass = getClass().getClassLoader().loadClass(criteriaStr);
                return (YukonObjectCriteria) criteriaClass.newInstance();
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException("could not find class " + criteriaStr, cnfe);
            } catch (IllegalAccessException iae) {
                throw new RuntimeException("error instantiating class " + criteriaStr, iae);
            } catch (InstantiationException ie) {
                throw new RuntimeException("error instantiating class " + criteriaStr, ie);
            }
        }
        return null;
    }

    @RequestMapping
    public String initial(ModelMap model, String pickerId,
            String selectionLinkName) {
        if (StringUtils.isEmpty(selectionLinkName)) {
            selectionLinkName = "Done";
        }
        model.addAttribute("selectionLinkName", selectionLinkName);
        model.addAttribute("pickerId", pickerId);
        return "dr/inner";
    }

    @RequestMapping
    public ModelAndView search(
            @RequestParam(value = "start", required = false) String startStr,
            Integer count, String ss,
            @RequestParam("criteria") String criteriaStr)
            throws ServletException {
        ModelAndView mav = new ModelAndView(new JsonView());
        SearchResult<UltraLightPao> hits;
        YukonObjectCriteria criteria = getCriteria(criteriaStr);
        int start = 0;
        try {
            start = Integer.parseInt(startStr);
        } catch (NumberFormatException nfe) {
            // just keep 0
        }
        count = count == null ? 20 : count;
        if (StringUtils.isBlank(ss)) {
            hits = paoTypeSearcher.allPaos(criteria, start, count);
        } else {
            hits = paoTypeSearcher.search(ss, criteria, start, count);
        }
        processHitList(mav, hits);
        mav.addObject("showAll", false);
        
        return mav;
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

    @Autowired
    public void setPaoTypeSearcher(PaoTypeSearcher paoTypeSearcher) {
        this.paoTypeSearcher = paoTypeSearcher;
    }
}
