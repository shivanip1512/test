package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.trends.TrendUtils;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/trendsWidget")
public class TrendsWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public TrendsWidget(@Qualifier("widgetInput.trendId") SimpleWidgetInput simpleWidgetInput) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/trendIdentity.jsp");
    }
    
    @GetMapping("render")
    public String render(ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws Exception {
        Integer trendId = WidgetParameterHelper.getIntParameter(request, "trendId");
        model.addAttribute("trendId", trendId);
        model.addAttribute("labels", TrendUtils.getLabels(userContext, messageResolver));
        return "trendsWidget/render.jsp";
    }
}
