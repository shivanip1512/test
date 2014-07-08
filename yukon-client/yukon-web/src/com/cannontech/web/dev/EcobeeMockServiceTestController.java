package com.cannontech.web.dev;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.AuthorizeByCparm;
import com.cannontech.web.security.annotation.IgnoreCsrfCheck;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/ecobee/*")
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class EcobeeMockServiceTestController {
	private List<String> status;
	@Autowired
	private EcobeeDataConfiguration ecobeeDataConfiguration;

	@PostConstruct
	public void init() throws IOException {
		status = Lists.newArrayList();
		status.add("SUCCESS(0)");
		status.add("AUTHENTICATION_FAILED(1)");
		status.add("NOT_AUTHORIZED(2)");
		status.add("PROCESSING_ERROR(3)");
		status.add("SERIALIZATION_ERROR(4)");
		status.add("INVALID_REQUEST_FORMAT(5)");
		status.add("TOO_MANY_THERMOSTATS(6)");
		status.add("VALIDATION_ERROR(7)");
		status.add("INVALID_FUNCTION(8)");
		status.add("INVALID_SELECTION(9)");
		status.add("INVALID_PAGE(10)");
		status.add("FUNCTION_ERROR(11)");
		status.add("POST_NOT_SUPPORTED(12)");
		status.add("GET_NOT_SUPPORTED(13)");
		status.add("AUTHENTICATION_EXPIRED(14)");
		status.add("DUPLICATE_DATA_VIOLATION(15)");
	}

	@RequestMapping("/viewBase")
	public String viewBase(ModelMap model) {
		model.addAttribute("status", status);
		return "ecobee/viewBase.jsp";
	}

	@IgnoreCsrfCheck
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public String executeRequest(Integer regDevice, Integer movDevice,
			Integer creatSet, Integer movSet,Integer remSet,Integer drSend, Integer restoreSend,
			Integer listHierarchy, Integer authenticateOp,Integer runtimeReportOp,Integer assignThermostatOp,FlashScope flashScope,ModelMap modelMap) throws IOException {
		ecobeeDataConfiguration.setEcobeeDataConfiguration(regDevice, movDevice,
				creatSet,movSet,remSet,drSend, restoreSend,listHierarchy,authenticateOp,runtimeReportOp, assignThermostatOp);
		modelMap.addAttribute("register", ecobeeDataConfiguration.getRegisterDevice());
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.ecobee.mockTest.saved"));
		return "redirect:viewBase";
	}
	

}
