package com.cannontech.web.amr.porterResponseMonitor;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.porterResponseMonitor.dao.PorterResponseMonitorDao;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorDto;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorErrorCode;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorMatchStyle;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorRule;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorRuleDto;
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
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/porterResponseMonitor/*")
@CheckRoleProperty(YukonRoleProperty.PORTER_RESPONSE_MONITORING)
public class PorterResponseMonitorController {

	private PorterResponseMonitorDao porterResponseMonitorDao;
	private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
	private PorterResponseMonitorService porterResponseMonitorService;
	private OutageEventLogService outageEventLogService;
	private AttributeService attributeService;
	private StateDao stateDao;
	private final static String baseKey = "yukon.web.modules.amr.porterResponseMonitor";

	private Validator nameValidator = new SimpleValidator<PorterResponseMonitor>(PorterResponseMonitor.class) {
		@Override
		public void doValidation(PorterResponseMonitor monitor, Errors errors) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name","yukon.web.error.required");
			YukonValidationUtils.checkExceedsMaxLength(errors, "name", monitor.getName(), 50);
		}
	};

	private Validator nameAndRulesValidator = new SimpleValidator<PorterResponseMonitor>(PorterResponseMonitor.class) {
		@Override
		public void doValidation(PorterResponseMonitor monitor, Errors errors) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.required");
			YukonValidationUtils.checkExceedsMaxLength(errors, "name", monitor.getName(), 50);

			// --- uniqueness checks ---

			List<Integer> orderList = Lists.newArrayList();
			for (PorterResponseMonitorRule rule : monitor.getRules()) {
				orderList.add(rule.getRuleOrder());

				// Error Code Uniqueness
				List<Integer> errorsList = Lists.newArrayList();
                for (PorterResponseMonitorErrorCode errorCode : rule.getErrorCodes()) {
                    errorsList.add(errorCode.getErrorCode());
                }
                if (containsDuplicates(errorsList)) {
                    // we have duplicate errors for this rule
                    errors.reject(baseKey + ".rulesTable.errorCodesFormat");
                }
			}

			// Order Uniqueness check
			// which should not be a problem now that I am normalizing the order in the Constructor
			// -- keeping this here in case that normalization is ever changed / removed
			if (containsDuplicates(orderList)) {
				// we have duplicate orders for this monitor
				errors.reject(baseKey + ".rulesTable.uniqueOrder");
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
        PorterResponseMonitorDto monitorDto = new PorterResponseMonitorDto(monitor);
		setupEditPageModelMap(monitorDto, model, userContext);

		return "porterResponseMonitor/edit.jsp";
	}

	@RequestMapping
	public String createPage(ModelMap model, YukonUserContext userContext)
			throws ServletRequestBindingException {

	    setupCreatePageModelMap(model, userContext);

		return "porterResponseMonitor/create.jsp";
	}

	@RequestMapping
	public String create(@ModelAttribute("monitor") PorterResponseMonitor monitor, BindingResult bindingResult, ModelMap modelMap,
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
	public String update(@ModelAttribute("monitorDto") PorterResponseMonitorDto monitorDto,
					BindingResult bindingResult, Integer[] rulesToRemove,
					HttpServletRequest request, ModelMap modelMap,
					YukonUserContext userContext, FlashScope flashScope) {

	    removeRulesFromMap(monitorDto.getRules(), rulesToRemove);

	    PorterResponseMonitor monitor = null;

		try {
		    monitor = new PorterResponseMonitor(monitorDto);
		} catch (NumberFormatException e) {
			bindingResult.reject(baseKey + ".rulesTable.errorCodesFormat");
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
			setupEditPageModelMap(monitorDto, modelMap, userContext);
			return "porterResponseMonitor/edit.jsp";
		}

		nameAndRulesValidator.validate(monitor, bindingResult);

		if (bindingResult.hasErrors()) {
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
			setupEditPageModelMap(monitorDto, modelMap, userContext);
			return "porterResponseMonitor/edit.jsp";
		}

		try {
			porterResponseMonitorDao.save(monitor);
		} catch (DuplicateException e) {
			bindingResult.rejectValue("name", baseKey + ".alreadyExists");
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
			setupEditPageModelMap(monitorDto, modelMap, userContext);
			return "porterResponseMonitor/edit.jsp";
		}

		MessageSourceResolvable updateMessage = new YukonMessageSourceResolvable(
				"yukon.web.modules.amr.porterResponseMonitor.updated", monitorDto.getName());
		flashScope.setConfirm(updateMessage);
		
        outageEventLogService.porterResponseMonitorUpdated(monitorDto.getMonitorId(), 
        		monitorDto.getName(),
                monitorDto.getAttribute().getKey(), 
                monitorDto.getStateGroup().toString(), 
        		monitorDto.getEvaluatorStatus().getDescription(),
        		userContext.getYukonUser());

		return "redirect:/spring/meter/start";
	}

	private void removeRulesFromMap(Map<Integer, PorterResponseMonitorRuleDto> rulesMap, Integer[] rulesToRemove) {
		if (rulesToRemove == null) {
			return;
		}
		for (Integer integer : rulesToRemove) {
            rulesMap.remove(integer);
        }
	}

	@RequestMapping(params = "delete")
	public String delete(@ModelAttribute PorterResponseMonitorDto monitorDto, ModelMap modelMap, FlashScope flashScope, YukonUserContext userContext)
					throws ServletRequestBindingException {

		porterResponseMonitorService.delete(monitorDto.getMonitorId());

		MessageSourceResolvable deleteMessage = new YukonMessageSourceResolvable(
				"yukon.web.modules.amr.porterResponseMonitor.deleted", monitorDto.getName());
		flashScope.setConfirm(deleteMessage);

        outageEventLogService.porterResponseMonitorDeleted(monitorDto.getMonitorId(),
                                                           monitorDto.getName(),
                                                           monitorDto.getAttribute().getKey(),
                                                           monitorDto.getStateGroup().toString(),
                                                           monitorDto.getEvaluatorStatus().getDescription(),
														   userContext.getYukonUser());

		return "redirect:/spring/meter/start";
	}

	@RequestMapping(params = "toggleEnabled")
	public String toggleEnabled(@ModelAttribute PorterResponseMonitorDto monitorDto, ModelMap modelMap, 
	                YukonUserContext userContext)
					throws ServletRequestBindingException {

		MonitorEvaluatorStatus status = monitorDto.getEvaluatorStatus();

		try {
			status = porterResponseMonitorService.toggleEnabled(monitorDto.getMonitorId());
			modelMap.addAttribute("monitorId", monitorDto.getMonitorId());
		} catch (NotFoundException e) {
			return "redirect:/spring/meter/start";
		}

		outageEventLogService.porterResponseMonitorEnableDisable(monitorDto.getMonitorId(), status.name(), userContext.getYukonUser());

		return "redirect:editPage";
	}

	@RequestMapping
	public String addRule(ModelMap model, LiteYukonUser user, int monitorId, int maxOrder) {

		setupAddRule(model, monitorId, maxOrder);

		return "porterResponseMonitor/addRuleTableRow.jsp";
	}

	private void setupAddRule(ModelMap model, int monitorId, int maxOrder) {
	    PorterResponseMonitor monitor = porterResponseMonitorDao.getMonitorById(monitorId);
        List<PorterResponseMonitorMatchStyle> matchStyleChoices = getMatchStyleChoices();
        model.addAttribute("matchStyleChoices", matchStyleChoices);
	    List<LiteState> statesList = monitor.getStateGroup().getStatesList();
	    model.addAttribute("statesList", statesList);
		model.addAttribute("nextOrder", ++maxOrder);
		model.addAttribute("newMapKey", PorterResponseMonitorDto.getNextKey());
	}

	private void setupCreatePageModelMap(ModelMap model, YukonUserContext userContext) {
        PorterResponseMonitor monitor = new PorterResponseMonitor();
        model.addAttribute("monitor", monitor);

        LiteStateGroup[] allStateGroups = stateDao.getAllStateGroups();
        List<LiteStateGroup> stateGroupList = Arrays.asList(allStateGroups);
        model.addAttribute("stateGroups", stateGroupList);
        model.addAttribute("mode", PageEditMode.CREATE);
	}

	private void setupViewPageModelMap(int monitorId, ModelMap model,
			YukonUserContext userContext, FlashScope flashScope) {

		PorterResponseMonitor monitor = porterResponseMonitorDao.getMonitorById(monitorId);
		PorterResponseMonitorDto monitorDto = new PorterResponseMonitorDto(monitor);

		if (monitorDto.getRules().isEmpty()) {
			MessageSourceResolvable noRulesMessage = new YukonMessageSourceResolvable(baseKey + ".rulesTable.noRules");
			flashScope.setWarning(noRulesMessage);
		}

		List<LiteState> states = monitorDto.getStateGroup().getStatesList();
		model.addAttribute("states", states);
		model.addAttribute("monitorDto", monitorDto);
		model.addAttribute("mode", PageEditMode.VIEW);
	}

	private void setupEditPageModelMap(PorterResponseMonitorDto monitorDto, ModelMap model, YukonUserContext userContext) {
        Set<Attribute> allAttributes = attributeService.getReadableAttributes();
        model.addAttribute("allAttributes", allAttributes);
        List<PorterResponseMonitorMatchStyle> matchStyleChoices = getMatchStyleChoices();
        model.addAttribute("matchStyleChoices", matchStyleChoices);
		model.addAttribute("monitorDto", monitorDto);
		model.addAttribute("mode", PageEditMode.EDIT);
	}

    private List<PorterResponseMonitorMatchStyle> getMatchStyleChoices() {
        List<PorterResponseMonitorMatchStyle> matchStyleChoices = Lists.newArrayList();
        matchStyleChoices.add(PorterResponseMonitorMatchStyle.any);
        matchStyleChoices.add(PorterResponseMonitorMatchStyle.all);
        matchStyleChoices.add(PorterResponseMonitorMatchStyle.none);
        return matchStyleChoices;
    }

    @ModelAttribute("allErrors")
    public Iterable<DeviceErrorDescription> getAllErrors() {
        Iterable<DeviceErrorDescription> allErrors = deviceErrorTranslatorDao.getAllErrors();
        return allErrors;
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
	public void setDeviceErrorTranslatorDao(DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
        this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
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