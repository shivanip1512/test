package com.cannontech.web.amr.porterResponseMonitor;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.porterResponseMonitor.dao.PorterResponseMonitorDao;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorErrorCode;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorMatchStyle;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorRule;
import com.cannontech.amr.porterResponseMonitor.service.PorterResponseMonitorService;
import com.cannontech.common.events.loggers.OutageEventLogService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/porterResponseMonitor/*")
@CheckRoleProperty(YukonRoleProperty.PORTER_RESPONSE_MONITORING)
public class PorterResponseMonitorController {

	private PorterResponseMonitorDao porterResponseMonitorDao;
	private PorterResponseMonitorService porterResponseMonitorService;
	private OutageEventLogService outageEventLogService;
	private AttributeService attributeService;
	private StateDao stateDao;
	private AtomicInteger atomicInt = new AtomicInteger();
	private final static String baseKey = "yukon.web.modules.amr.porterResponseMonitor";
	private Function<PorterResponseMonitorErrorCode, Integer> transformer = new Function<PorterResponseMonitorErrorCode, Integer>() {
		@Override
		public Integer apply(PorterResponseMonitorErrorCode from) {
			return from.getErrorCode();
		}
	};

	private Validator nameValidator = new SimpleValidator<PorterResponseMonitor>(PorterResponseMonitor.class) {
		@Override
		public void doValidation(PorterResponseMonitor monitor, Errors errors) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", baseKey + ".empty");
			YukonValidationUtils.checkExceedsMaxLength(errors, "name", monitor.getName(), 50);
		}
	};

	private Validator nameAndRulesValidator = new SimpleValidator<PorterResponseMonitor>(PorterResponseMonitor.class) {
		@Override
		public void doValidation(PorterResponseMonitor monitor, Errors errors) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", baseKey + ".empty");
			YukonValidationUtils.checkExceedsMaxLength(errors, "name", monitor.getName(), 50);

			// TODO: there is probably an easier way to check for uniqueness...
			// but good enough for now.

			// Order Uniqueness
			List<Integer> orderList = Lists.newArrayList();
			for (PorterResponseMonitorRule rule : monitor.getRules()) {
				orderList.add(rule.getRuleOrder());
			}
			if (containsDuplicates(orderList)) {
				// we have duplicate orders for this monitor
				errors.reject(baseKey + ".rulesTable.uniqueOrder");
			}

			// Error Code Uniqueness
			for (PorterResponseMonitorRule rule : monitor.getRules()) {
				List<Integer> errorsList = Lists.newArrayList();
				for (PorterResponseMonitorErrorCode errorCode : rule.getErrorCodes()) {
					errorsList.add(errorCode.getErrorCode());
				}

				if (containsDuplicates(errorsList)) {
					// we have duplicate errors for this rule
					errors.reject(baseKey + ".rulesTable.errorCodesFormat");
				}
			}
		}
	};

	private <T> boolean containsDuplicates(List<T> list) {
		Set<T> set = new HashSet<T>(list);
		if (set.size() < list.size()) {
			// duplicates were removed
			return true;
		}
		return false;
	}

	@RequestMapping
	public String viewPage(int monitorId, ModelMap model, YukonUserContext userContext, FlashScope flashScope)
			throws ServletRequestBindingException {

		setupViewPageModelMap(monitorId, model, userContext, flashScope);

		return "porterResponseMonitor/view.jsp";
	}

	@RequestMapping
	public String editPage(int monitorId, ModelMap model, YukonUserContext userContext, FlashScope flashScope)
			throws ServletRequestBindingException {

		PorterResponseMonitor monitor = porterResponseMonitorDao.getMonitorById(monitorId);
		setupEditPageModelMap(monitor, model, userContext);

		return "porterResponseMonitor/edit.jsp";
	}

	@RequestMapping
	public String createPage(ModelMap model, YukonUserContext userContext)
			throws ServletRequestBindingException {

	    setupCreatePageModelMap(model, userContext);

		return "porterResponseMonitor/create.jsp";
	}

	@RequestMapping
	public String create(@ModelAttribute PorterResponseMonitor monitor, BindingResult bindingResult, ModelMap modelMap,
			YukonUserContext userContext, FlashScope flashScope, HttpServletRequest request) {

		nameValidator.validate(monitor, bindingResult);

		if (bindingResult.hasErrors()) {
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
			setupCreatePageModelMap(modelMap, userContext);
			return "porterResponseMonitor/create.jsp";
		}

		try {
			porterResponseMonitorDao.save(monitor);
		} catch (DuplicateException e) {
			bindingResult.rejectValue("name", baseKey + ".alreadyExists");
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
			setupCreatePageModelMap(modelMap, userContext);
			return "porterResponseMonitor/create.jsp";
		}

		modelMap.addAttribute("monitorId", monitor.getMonitorId());

		MessageSourceResolvable createMessage = new YukonMessageSourceResolvable("yukon.web.modules.amr.porterResponseMonitor.created");
		flashScope.setConfirm(createMessage);

		outageEventLogService.porterResponseMonitorCreated(monitor.getMonitorId(),
														   monitor.getName(),
	                                                       monitor.getAttribute().getKey(), 
	                                                       monitor.getStateGroup().toString(), 
														   monitor.getEvaluatorStatus().getDescription(),
														   userContext.getYukonUser());

		return "redirect:editPage";
	}

	@RequestMapping(params = "cancel")
	public String cancel(ModelMap modelMap, HttpServletRequest request) {
		return "redirect:/spring/meter/start";
	}

	@RequestMapping
	public String update(@ModelAttribute PorterResponseMonitor monitor,
					BindingResult bindingResult, Integer[] rulesToRemove,
					HttpServletRequest request, ModelMap modelMap,
					YukonUserContext userContext, FlashScope flashScope) {

		String[] monitorCodes = request.getParameterValues("monitorCodes");

		try {
			// Assign Error Codes to Rules
			for (int i = 0; i < monitor.getRules().size(); i++) {
				PorterResponseMonitorRule rule = monitor.getRules().get(i);

				String singleRuleCodes = monitorCodes[i];
				String[] errors = singleRuleCodes.split("\\s*,\\s*");
				for (String error : errors) {
					PorterResponseMonitorErrorCode errorCode = new PorterResponseMonitorErrorCode();
					if (rule.getRuleId() != null) {
						errorCode.setRuleId(rule.getRuleId());
					}
					error = error.trim();
					errorCode.setErrorCode(Integer.valueOf(error));
					rule.getErrorCodes().add(errorCode);
				}
			}
		} catch (NumberFormatException e) {
			bindingResult.reject(baseKey + ".rulesTable.errorCodesFormat");
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
			setupErrorEditPageModelMap(monitor, monitorCodes, modelMap, userContext);
			return "porterResponseMonitor/edit.jsp";
		}

		// Need to do this after the assigning of the Error Codes so the indexes match above
		List<PorterResponseMonitorRule> remainingRules = getRemainingRules(monitor.getRules(), rulesToRemove);
		monitor.setRules(remainingRules);

		nameAndRulesValidator.validate(monitor, bindingResult);

		if (bindingResult.hasErrors()) {
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
			setupErrorEditPageModelMap(monitor, monitorCodes, modelMap, userContext);
			return "porterResponseMonitor/edit.jsp";
		}

		try {
			porterResponseMonitorDao.save(monitor);
		} catch (DuplicateException e) {
			bindingResult.rejectValue("name", baseKey + ".alreadyExists");
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
			setupErrorEditPageModelMap(monitor, monitorCodes, modelMap, userContext);
			return "porterResponseMonitor/edit.jsp";
		}

		MessageSourceResolvable updateMessage = new YukonMessageSourceResolvable(
				"yukon.web.modules.amr.porterResponseMonitor.updated", monitor.getName());
		flashScope.setConfirm(updateMessage);
		
        outageEventLogService.porterResponseMonitorUpdated(monitor.getMonitorId(), 
        		monitor.getName(),
                monitor.getAttribute().getKey(), 
                monitor.getStateGroup().toString(), 
        		monitor.getEvaluatorStatus().getDescription(),
        		userContext.getYukonUser());

		return "redirect:/spring/meter/start";
	}

	private List<PorterResponseMonitorRule> getRemainingRules(List<PorterResponseMonitorRule> rules, Integer[] rulesToRemove) {
		if (rulesToRemove == null) {
			return rules;
		}

		List<PorterResponseMonitorRule> remainingRules = Lists.newArrayList();

		for (PorterResponseMonitorRule rule : rules) {
			boolean isRemoved = Arrays.asList(rulesToRemove).contains(rule.getRuleId());
			if (!isRemoved) {
				remainingRules.add(rule);
			}
		}

		return remainingRules;
	}

	@RequestMapping(params = "delete")
	public String delete(@ModelAttribute PorterResponseMonitor monitor, ModelMap modelMap, FlashScope flashScope, YukonUserContext userContext)
					throws ServletRequestBindingException {

		porterResponseMonitorService.delete(monitor.getMonitorId());

		MessageSourceResolvable deleteMessage = new YukonMessageSourceResolvable(
				"yukon.web.modules.amr.porterResponseMonitor.deleted", monitor.getName());
		flashScope.setConfirm(deleteMessage);

        outageEventLogService.porterResponseMonitorDeleted(monitor.getMonitorId(),
                                                           monitor.getName(),
                                                           monitor.getAttribute().getKey(),
                                                           monitor.getStateGroup().toString(),
														   monitor.getEvaluatorStatus().getDescription(),
														   userContext.getYukonUser());

		return "redirect:/spring/meter/start";
	}

	@RequestMapping(params = "toggleEnabled")
	public String toggleEnabled(@ModelAttribute PorterResponseMonitor monitor, ModelMap modelMap, 
	                YukonUserContext userContext)
					throws ServletRequestBindingException {

		MonitorEvaluatorStatus status = monitor.getEvaluatorStatus();

		try {
			status = porterResponseMonitorService.toggleEnabled(monitor.getMonitorId());
			modelMap.addAttribute("monitorId", monitor.getMonitorId());
		} catch (NotFoundException e) {
			return "redirect:/spring/meter/start";
		}

		outageEventLogService.porterResponseMonitorEnableDisable(monitor.getMonitorId(), status.name(), userContext.getYukonUser());

		return "redirect:editPage";
	}

	@RequestMapping
	public String addRule(ModelMap model, LiteYukonUser user, int index, int monitorId, int nextOrder) {

		setupAddRule(model, index, monitorId, nextOrder);

		return "porterResponseMonitor/addRuleTableRow.jsp";
	}

	private void setupAddRule(ModelMap model, int index, int monitorId, int nextOrder) {
	    PorterResponseMonitor monitor = porterResponseMonitorDao.getMonitorById(monitorId);
	    List<LiteState> statesList = monitor.getStateGroup().getStatesList();
	    model.addAttribute("statesList", statesList);

		List<PorterResponseMonitorMatchStyle> matchStyleChoices = getMatchStyleChoices();
		model.addAttribute("matchStyleChoices", matchStyleChoices);
		model.addAttribute("order", nextOrder);
		model.addAttribute("newRowId", atomicInt.getAndIncrement());
		model.addAttribute("defaultError", 0);
		model.addAttribute("index", index);
	}

	private void setupCreatePageModelMap(ModelMap model, YukonUserContext userContext) {
        PorterResponseMonitor monitor = new PorterResponseMonitor();
        model.addAttribute("monitor", monitor);

        // state groups
        LiteStateGroup[] allStateGroups = stateDao.getAllStateGroups();
        List<LiteStateGroup> stateGroupList = Arrays.asList(allStateGroups);
        model.addAttribute("stateGroups", stateGroupList);
        model.addAttribute("mode", PageEditMode.CREATE);
	}

	private void setupViewPageModelMap(int monitorId, ModelMap model,
			YukonUserContext userContext, FlashScope flashScope) {

		PorterResponseMonitor monitor = porterResponseMonitorDao.getMonitorById(monitorId);

		Map<Integer, String> errorCodesMap = getErrorCodesMapFromMonitor(monitor);

		if (monitor.getRules().isEmpty()) {
			MessageSourceResolvable noRulesMessage = new YukonMessageSourceResolvable(baseKey + ".rulesTable.noRules");
			flashScope.setWarning(noRulesMessage);
		}

        Set<Attribute> allAttributes = attributeService.getReadableAttributes();
        model.addAttribute("allAttributes", allAttributes);

        List<String> stateStrings = Lists.newArrayList();
        for (PorterResponseMonitorRule rule : monitor.getRules()) {
            String stateString = monitor.getStateGroup().getStatesList().get(rule.getStateInt()).getStateText();
            stateStrings.add(stateString);
        }

        model.addAttribute("stateStrings", stateStrings);
		model.addAttribute("errorCodesMap", errorCodesMap);
		model.addAttribute("monitor", monitor);
		model.addAttribute("mode", PageEditMode.VIEW);
	}

	private void setupEditPageModelMap(PorterResponseMonitor monitor, ModelMap model, YukonUserContext userContext) {

		List<PorterResponseMonitorMatchStyle> matchStyleChoices = getMatchStyleChoices();
		model.addAttribute("matchStyleChoices", matchStyleChoices);

        Set<Attribute> allAttributes = attributeService.getReadableAttributes();
        model.addAttribute("allAttributes", allAttributes);

		Map<Integer, String> errorCodesMap = getErrorCodesMapFromMonitor(monitor);
		model.addAttribute("errorCodesMap", errorCodesMap);
		model.addAttribute("monitor", monitor);
		model.addAttribute("mode", PageEditMode.EDIT);
	}

	private void setupErrorEditPageModelMap(PorterResponseMonitor monitor,
						String[] errorCodes, ModelMap model, YukonUserContext userContext) {

		List<PorterResponseMonitorMatchStyle> matchStyleChoices = getMatchStyleChoices();
		model.addAttribute("matchStyleChoices", matchStyleChoices);

        Set<Attribute> allAttributes = attributeService.getReadableAttributes();
        model.addAttribute("allAttributes", allAttributes);

		Map<Integer, String> errorCodesMap = getErrorCodesMapFromList(monitor, errorCodes);
		model.addAttribute("errorCodesMap", errorCodesMap);
		model.addAttribute("monitor", monitor);
		model.addAttribute("mode", PageEditMode.EDIT);
	}

	// Error Codes are already in the monitor's rules
	private Map<Integer, String> getErrorCodesMapFromMonitor(PorterResponseMonitor monitor) {
		Map<Integer, String> errorCodesMap = Maps.newHashMap();

		for (PorterResponseMonitorRule aRule : monitor.getRules()) {
			List<Integer> errorCodeIntegers = Lists.transform(Lists.newArrayList(aRule.getErrorCodes()), transformer);
			errorCodesMap.put(aRule.getRuleId(), StringUtils.join(errorCodeIntegers, ", "));
		}

		return errorCodesMap;
	}

	// Error Codes are not yet in the rules
	private Map<Integer, String> getErrorCodesMapFromList(PorterResponseMonitor monitor, String[] errorCodes) {
		Map<Integer, String> errorCodesMap = Maps.newHashMap();

		for (int i = 0; i < monitor.getRules().size(); i++) {
			PorterResponseMonitorRule aRule = monitor.getRules().get(i);
			errorCodesMap.put(aRule.getRuleId(), errorCodes[i]);
		}

		return errorCodesMap;
	}

	private List<PorterResponseMonitorMatchStyle> getMatchStyleChoices() {
		List<PorterResponseMonitorMatchStyle> matchStyleChoices = Lists.newArrayList();
		matchStyleChoices.add(PorterResponseMonitorMatchStyle.all);
		matchStyleChoices.add(PorterResponseMonitorMatchStyle.any);
		matchStyleChoices.add(PorterResponseMonitorMatchStyle.none);
		return matchStyleChoices;
	}

    @InitBinder
    public void setupBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Attribute.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String attr) throws IllegalArgumentException {
                Attribute attribute = attributeService.resolveAttributeName(attr);
                setValue(attribute);
            }
            @Override
            public String getAsText() {
                Attribute attr = (Attribute) getValue();
                return attr.getKey();
            }
        });
        binder.registerCustomEditor(PorterResponseMonitorMatchStyle.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String styleString) throws IllegalArgumentException {
                PorterResponseMonitorMatchStyle matchStyle = PorterResponseMonitorMatchStyle.valueOf(styleString);
                setValue(matchStyle);
            }
            @Override
            public String getAsText() {
                PorterResponseMonitorMatchStyle matchStyle = (PorterResponseMonitorMatchStyle) getValue();
                return String.valueOf(matchStyle.name());
            }
        });
        binder.registerCustomEditor(LiteStateGroup.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String group) throws IllegalArgumentException {
                LiteStateGroup stateGroup = stateDao.getLiteStateGroup(group);
                setValue(stateGroup);
            }
            @Override
            public String getAsText() {
                LiteStateGroup stateGroup = (LiteStateGroup) getValue();
                return String.valueOf(stateGroup.getStateGroupName());
            }
        });
    }

    @Autowired
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }

	@Autowired
	public void setPorterResponseMonitorDao(PorterResponseMonitorDao porterResponseMonitorDao) {
		this.porterResponseMonitorDao = porterResponseMonitorDao;
	}

	@Autowired
	public void setPorterResponseMonitorService(PorterResponseMonitorService porterResponseMonitorService) {
		this.porterResponseMonitorService = porterResponseMonitorService;
	}

	@Autowired
	public void setOutageEventLogService(OutageEventLogService outageEventLogService) {
		this.outageEventLogService = outageEventLogService;
	}

	@Autowired
	public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}