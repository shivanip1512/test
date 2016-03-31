package com.cannontech.web.dev;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.security.annotation.IgnoreCsrfCheck;
import com.google.common.collect.ImmutableList;

@Controller
@RequestMapping("/ecobee/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class EcobeeMockServiceTestController {
	private static final List<String> status = ImmutableList.of("SUCCESS(0)",
			"AUTHENTICATION_FAILED(1)", "NOT_AUTHORIZED(2)",
			"PROCESSING_ERROR(3)", "SERIALIZATION_ERROR(4)",
			"INVALID_REQUEST_FORMAT(5)", "TOO_MANY_THERMOSTATS(6)",
			"VALIDATION_ERROR(7)", "INVALID_FUNCTION(8)",
			"INVALID_SELECTION(9)", "INVALID_PAGE(10)", "FUNCTION_ERROR(11)",
			"POST_NOT_SUPPORTED(12)", "GET_NOT_SUPPORTED(13)",
			"AUTHENTICATION_EXPIRED(14)", "DUPLICATE_DATA_VIOLATION(15)");
	@Autowired private EcobeeDataConfiguration ecobeeDataConfiguration;

	@RequestMapping("/viewBase")
	public String viewBase(ModelMap model) {
		model.addAttribute("status", status);
		return "ecobee/viewBase.jsp";
	}

	@IgnoreCsrfCheck
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Integer regDevice, Integer movDevice,
			Integer creatSet, Integer movSet, Integer remSet, Integer drSend,
			Integer restoreSend, Integer listHierarchy, Integer authenticateOp,
			Integer runtimeReportOp, Integer assignThermostatOp,
			FlashScope flashScope, ModelMap modelMap) throws IOException {
		ecobeeDataConfiguration.setEcobeeDataConfiguration(regDevice,
				movDevice, creatSet, movSet, remSet, drSend, restoreSend,
				listHierarchy, authenticateOp, runtimeReportOp,
				assignThermostatOp);
		modelMap.addAttribute("register",
				ecobeeDataConfiguration.getRegisterDevice());
		flashScope.setConfirm(new YukonMessageSourceResolvable(
				"yukon.web.modules.dev.ecobee.mockTest.saved"));
		return "redirect:viewBase";
	}

}
