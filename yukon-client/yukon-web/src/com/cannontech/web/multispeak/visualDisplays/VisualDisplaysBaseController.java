package com.cannontech.web.multispeak.visualDisplays;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.system.YukonSetting;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckSystemSetting;
import com.cannontech.web.util.JsonView;

@CheckSystemSetting(YukonSetting.MSP_LM_MAPPING_SETUP)
public class VisualDisplaysBaseController extends MultiActionController {

	private DateFormattingService dateFormattingService;
	
	public ModelAndView currentDateTime(HttpServletRequest request, HttpServletResponse response) {
		
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		String nowStr = dateFormattingService.format(new Date(), DateFormatEnum.BOTH, userContext);
		
		ModelAndView mav = new ModelAndView(new JsonView());
		mav.addObject("nowStr", nowStr);
		return mav;
	}
	
	@Autowired
	public void setDateFormattingService(DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
}
