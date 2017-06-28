package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.infrastructure.dao.InfrastructureWarningsDao;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningSummary;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;

/**
 * Widget used to display infrastructure warnings
 */
@Controller
@RequestMapping("/infrastructureWarningsWidget")
public class InfrastructureWarningsWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private InfrastructureWarningsDao infrastructureWarningsDao;
        
    @RequestMapping("render")
    public String render(ModelMap model) {
        InfrastructureWarningSummary summary = infrastructureWarningsDao.getWarningsSummary();
        model.addAttribute("summary", summary);
        List<InfrastructureWarning> warnings = infrastructureWarningsDao.getWarnings();
        Comparator<InfrastructureWarning> comparator = (o1, o2) -> o1.getSeverity().name().compareTo(o2.getSeverity().name());
        Collections.sort(warnings, comparator);
        warnings = warnings.subList(0,  10);
        model.addAttribute("warnings",  warnings);
        return "infrastructureWarningsWidget/render.jsp";
    }

}
