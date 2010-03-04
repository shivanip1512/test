package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.stars.dr.general.model.OperatorAccountSearchBy;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class OperatorAccountSearchWidget extends WidgetControllerBase {
	
	@Override
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
