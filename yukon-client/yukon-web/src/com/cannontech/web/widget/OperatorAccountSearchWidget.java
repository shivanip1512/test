package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.cannontech.stars.dr.general.model.OperatorAccountSearchBy;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class OperatorAccountSearchWidget extends WidgetControllerBase {
	
	@Override
	public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView mav = new ModelAndView("operatorAccountSearchWidget/render.jsp");
		
		int searchByDefinitionId = WidgetParameterHelper.getIntParameter(request, "searchByDefinitionId", OperatorAccountSearchBy.ACCOUNT_NUMBER.getDefinitionId());
		String searchValue = WidgetParameterHelper.getStringParameter(request, "searchValue", "");
		mav.addObject("searchByDefinitionId", searchByDefinitionId);
		mav.addObject("searchValue", searchValue);
		
		OperatorAccountSearchBy[] operatorAccountSearchBys = OperatorAccountSearchBy.values();
		mav.addObject("operatorAccountSearchBys", operatorAccountSearchBys);

		return mav;
	}
}
