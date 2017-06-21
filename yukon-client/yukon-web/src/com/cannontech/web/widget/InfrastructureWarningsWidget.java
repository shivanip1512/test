package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.infrastructure.dao.InfrastructureWarningsDao;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;

/**
 * Widget used to display infrastructure warnings
 */
@Controller
@RequestMapping("/infrastructureWarningsWidget")
public class InfrastructureWarningsWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private InfrastructureWarningsDao infrastructureWarningsDao;
    
    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request) throws Exception {
        //TODO: get counts based on type
        
        //TODO: only get partial list
        List<InfrastructureWarning> warnings = infrastructureWarningsDao.getWarnings();
        model.addAttribute("warnings", warnings);

        return "infrastructureWarningsWidget/render.jsp";
    }

}
