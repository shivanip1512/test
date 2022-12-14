package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.stars.dr.general.model.OperatorAccountSearchBy;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/operatorAccountSearchWidget/*")
public class OperatorAccountSearchWidget extends WidgetControllerBase {
	
    @Autowired
    public OperatorAccountSearchWidget(
            @Qualifier("widgetInput.operatorAccountSearchSearchBy") 
            SimpleWidgetInput simpleWidgetInput,
            @Qualifier("widgetInput.operatorAccountSearchSearchValue")
            SimpleWidgetInput simpleWidgetInputOperatorAccount,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {

        String checkRole = YukonRole.CONSUMER_INFO.name();
        addInput(simpleWidgetInput);
        addInput(simpleWidgetInputOperatorAccount);
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }
    
	@Override
	@RequestMapping("render")
	public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView mav = new ModelAndView("operatorAccountSearchWidget/render.jsp");
		
		OperatorAccountSearchBy searchBy = OperatorAccountSearchBy.valueOf(ServletRequestUtils.getStringParameter(request, "searchBy", OperatorAccountSearchBy.ACCOUNT_NUMBER.name()));
		String searchValue = WidgetParameterHelper.getStringParameter(request, "searchValue", "");
		mav.addObject("searchBy", searchBy);
		mav.addObject("searchValue", searchValue);
		
		OperatorAccountSearchBy[] operatorAccountSearchBys = OperatorAccountSearchBy.values();
		mav.addObject("operatorAccountSearchBys", operatorAccountSearchBys);

		return mav;
	}
}
