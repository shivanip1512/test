package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.Range;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.dr.controlarea.filter.PriorityFilter;
import com.cannontech.dr.controlarea.filter.StateFilter;
import com.cannontech.dr.controlarea.model.ControlArea;
import com.cannontech.dr.controlarea.model.ControlAreaDisplayField;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.dr.filter.NameFilter;
import com.cannontech.dr.program.filter.ForControlAreaFilter;
import com.cannontech.user.YukonUserContext;

@Controller
public class ControlAreaController {
    public static class ControlAreaListBackingBean extends ListBackingBean {
        private String state;
        private Range<Integer> priority = new Range<Integer>();

        // TODO:
        // START and STOP (HH:MM only)
        // private String start;
        // private String stop;

        // probably can move this up to ListBackingBean
        private Range<Double> loadCapacity = new Range<Double>();

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public Range<Integer> getPriority() {
            return priority;
        }

        public void setPriority(Range<Integer> priority) {
            this.priority = priority;
        }

        public Range<Double> getLoadCapacity() {
            return loadCapacity;
        }

        public void setLoadCapacity(Range<Double> loadCapacity) {
            this.loadCapacity = loadCapacity;
        }
    }

    private ControlAreaService controlAreaService = null;
    private PaoAuthorizationService paoAuthorizationService;
    private ProgramControllerHelper programControllerHelper;

    @RequestMapping("/controlArea/list")
    public String list(ModelMap modelMap, YukonUserContext userContext,
            ControlAreaListBackingBean backingBean, BindingResult result,
            SessionStatus status) {

        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();
        if (!StringUtils.isEmpty(backingBean.getName())) {
            filters.add(new NameFilter(backingBean.getName()));
        }
        String stateFilter = backingBean.getState();
        if (!StringUtils.isEmpty(stateFilter)) {
            if (stateFilter.equals("active")) {
                filters.add(new StateFilter(controlAreaService, true));
            } else if (stateFilter.equals("inactive")) {
                filters.add(new StateFilter(controlAreaService, false));
            }
        }
        if (!backingBean.getPriority().isUnbounded()) {
            filters.add(new PriorityFilter(controlAreaService, backingBean.getPriority()));
        }

        ControlAreaDisplayField sortField = StringUtils.isEmpty(backingBean.getSort())
            ? ControlAreaDisplayField.NAME : ControlAreaDisplayField.valueOf(backingBean.getSort());
        Comparator<DisplayablePao> sorter =
            sortField.getSorter(controlAreaService, userContext,
                                backingBean.getDescending());
        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);
        int startIndex = (backingBean.getPage() - 1) * backingBean.getItemsPerPage();
        SearchResult<ControlArea> searchResult =
            controlAreaService.filterControlAreas(userContext, filter, sorter,
                                                  startIndex,
                                                  backingBean.getItemsPerPage());

        modelMap.addAttribute("searchResult", searchResult);
        modelMap.addAttribute("controlAreas", searchResult.getResultList());
        modelMap.addAttribute("backingBean", backingBean);

        return "dr/controlArea/list.jsp";
    }

    @RequestMapping("/controlArea/detail")
    public String detail(int controlAreaId, ModelMap modelMap,
            YukonUserContext userContext,
            ProgramControllerHelper.ProgramListBackingBean backingBean,
            BindingResult result, SessionStatus status) {
        ControlArea controlArea = controlAreaService.getControlArea(controlAreaId);
        if (false && !paoAuthorizationService.isAuthorized(userContext.getYukonUser(),
                                                  Permission.LM_VISIBLE, controlArea)) {
             throw new NotAuthorizedException("Control Area " + controlAreaId
                                              + " is not visible to user.");
        }
        modelMap.addAttribute("controlArea", controlArea);

        UiFilter<DisplayablePao> detailFilter = new ForControlAreaFilter(controlAreaId);
        programControllerHelper.filterPrograms(modelMap, userContext, backingBean,
                                               result, status, detailFilter);

        return "dr/controlArea/detail.jsp";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programControllerHelper.initBinder(binder, userContext);
    }

    @Autowired
    public void setControlAreaService(ControlAreaService controlAreaService) {
        this.controlAreaService = controlAreaService;
    }

    @Autowired
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }

    @Autowired
    public void setProgramControllerHelper(
            ProgramControllerHelper programControllerHelper) {
        this.programControllerHelper = programControllerHelper;
    }
}
