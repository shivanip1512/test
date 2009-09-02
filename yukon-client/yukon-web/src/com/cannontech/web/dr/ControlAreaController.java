package com.cannontech.web.dr;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.controlarea.model.ControlArea;
import com.cannontech.dr.program.dao.ProgramDao;
import com.cannontech.dr.program.filter.ForControlAreaFilter;
import com.cannontech.user.YukonUserContext;

@Controller
public class ControlAreaController {
    private ControlAreaDao controlAreaDao = null;
    private ProgramDao programDao = null;
    private PaoAuthorizationService paoAuthorizationService;
    private ProgramControllerHelper programControllerHelper;

    @RequestMapping("/controlArea/list")
    public String list(ModelMap modelMap, YukonUserContext userContext) {
        List<ControlArea> controlAreas = controlAreaDao.getControlAreas();
        Iterator<ControlArea> iter = controlAreas.iterator();
        while (iter.hasNext()) {
            DisplayablePao controlArea = iter.next();
            if (!paoAuthorizationService.isAuthorized(userContext.getYukonUser(),
                                                      Permission.LM_VISIBLE, controlArea)) {
//                iter.remove();
            }
        }
        modelMap.addAttribute("controlAreas", controlAreas);
        return "/dr/controlArea/list.jsp";
    }

    @RequestMapping("/controlArea/detail")
    public String detail(int controlAreaId, ModelMap modelMap,
            YukonUserContext userContext,
            @ModelAttribute("filter") ProgramControllerHelper.ProgramListBackingBean backingBean,
            BindingResult result, SessionStatus status) {
        ControlArea controlArea = controlAreaDao.getControlArea(controlAreaId);
        if (false && !paoAuthorizationService.isAuthorized(userContext.getYukonUser(),
                                                  Permission.LM_VISIBLE, controlArea)) {
             throw new NotAuthorizedException("Control Area " + controlAreaId
                                              + " is not visible to user.");
        }
        modelMap.addAttribute("controlArea", controlArea);

        UiFilter<DisplayablePao> detailFilter = new ForControlAreaFilter(controlAreaId);
        programControllerHelper.filterPrograms(modelMap, userContext, backingBean,
                                               result, status, detailFilter);

        return "/dr/controlArea/detail.jsp";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programControllerHelper.initBinder(binder, userContext);
    }

    @Autowired
    public void setControlAreaDao(ControlAreaDao controlAreaDao) {
        this.controlAreaDao = controlAreaDao;
    }

    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
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
