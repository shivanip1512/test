package com.cannontech.web.amr.porterResponseMonitor;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONObject;

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
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.OutageEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/porterResponseMonitor/*")
@CheckRoleProperty(YukonRoleProperty.PORTER_RESPONSE_MONITORING)
public class PorterResponseMonitorController {

	private PorterResponseMonitorDao porterResponseMonitorDao;
	private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
	private RolePropertyDao rolePropertyDao;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	private PorterResponseMonitorService porterResponseMonitorService;
	private TemporaryDeviceGroupService temporaryDeviceGroupService;
	private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	private OutageEventLogService outageEventLogService;
	private AttributeService attributeService;
	private DeviceGroupService deviceGroupService;
	private PointService pointService;
	private StateDao stateDao;
    protected YukonUserContextMessageSourceResolver messageSourceResolver;
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

	@RequestMapping(params = "cancelToView")
	public String cancelToView(int monitorId, ModelMap modelMap, HttpServletRequest request) {
        modelMap.addAttribute("monitorId", monitorId);
        return "redirect:viewPage";
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
			setupErrorEditPageModelMap(monitorDto, modelMap, userContext, bindingResult, flashScope);
			return "porterResponseMonitor/edit.jsp";
		}

		nameAndRulesValidator.validate(monitor, bindingResult);

		if (bindingResult.hasErrors()) {
            setupErrorEditPageModelMap(monitorDto, modelMap, userContext, bindingResult, flashScope);
			return "porterResponseMonitor/edit.jsp";
		}

		try {
			porterResponseMonitorDao.save(monitor);
		} catch (DuplicateException e) {
			bindingResult.rejectValue("name", baseKey + ".alreadyExists");
            setupErrorEditPageModelMap(monitorDto, modelMap, userContext, bindingResult, flashScope);
			return "porterResponseMonitor/edit.jsp";
		}

		modelMap.addAttribute("monitorId", monitor.getMonitorId());

		MessageSourceResolvable updateMessage = new YukonMessageSourceResolvable(
				"yukon.web.modules.amr.porterResponseMonitor.updated", monitorDto.getName());
		flashScope.setConfirm(updateMessage);
		
        outageEventLogService.porterResponseMonitorUpdated(monitorDto.getMonitorId(), 
        		monitorDto.getName(),
                monitorDto.getAttribute().getKey(), 
                monitorDto.getStateGroup().toString(), 
        		monitorDto.getEvaluatorStatus().getDescription(),
        		userContext.getYukonUser());

        return "redirect:viewPage";
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

    @RequestMapping
    public void getCounts(HttpServletRequest request, HttpServletResponse response,
                              int monitorId, YukonUserContext userContext) throws IOException {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        PorterResponseMonitor monitor = porterResponseMonitorDao.getMonitorById(monitorId);
        DeviceGroup group = deviceGroupService.resolveGroupName(monitor.getGroupName());
        int totalGroupCount = deviceGroupService.getDeviceCount(Collections.singleton(group));
        List<SimpleDevice> supportedDevices = deviceGroupService.getDevicesInGroupThatSupportAttribute(group, BuiltInAttribute.OUTAGE_STATUS);
        int existingPointCount = pointService.getCountOfGroupAttributeStateGroup(group,
                                                            monitor.getAttribute(),
                                                            monitor.getStateGroup());
        int missingPointCount = supportedDevices.size() - existingPointCount;
        String supportedDevicesMessage;
        
        if (missingPointCount > 0) {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String attributeString = messageSourceAccessor.getMessage("yukon.web.modules.amr.porterResponseMonitor." + monitor.getAttribute().getKey());
            supportedDevicesMessage = messageSourceAccessor.getMessage("yukon.web.modules.amr.porterResponseMonitor.supportedDevicesMessage", supportedDevices.size(), missingPointCount, attributeString);
        } else {
            supportedDevicesMessage = String.valueOf(supportedDevices.size());
        }
        
        JSONObject object = new JSONObject();
        object.put("totalGroupCount", totalGroupCount);
        object.put("supportedDevicesMessage", supportedDevicesMessage);
        object.put("missingPointCount", missingPointCount);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(object.toString());
        out.close();
    }

	private void setupCreatePageModelMap(ModelMap model, YukonUserContext userContext) {
        PorterResponseMonitor monitor = new PorterResponseMonitor();
        LiteStateGroup outageStatusStageGroup = stateDao.getLiteStateGroup("Outage Status");
        monitor.setStateGroup(outageStatusStageGroup);
        model.addAttribute("monitor", monitor);
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

        LiteYukonUser user = userContext.getYukonUser();
        boolean showAddRemovePoints = rolePropertyDao.checkProperty(YukonRoleProperty.ADD_REMOVE_POINTS, user);
        model.addAttribute("showAddRemovePoints", showAddRemovePoints);

        DeviceGroup group = deviceGroupService.resolveGroupName(monitor.getGroupName());
        List<SimpleDevice> supportedDevices = deviceGroupService.getDevicesInGroupThatSupportAttribute(group, BuiltInAttribute.OUTAGE_STATUS);
        StoredDeviceGroup supportedTempGroup = temporaryDeviceGroupService.createTempGroup(null);
        deviceGroupMemberEditorDao.addDevices(supportedTempGroup, supportedDevices);

        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(supportedTempGroup);
        model.addAttribute("deviceCollection", deviceCollection);

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String attributeString = messageSourceAccessor.getMessage("yukon.web.modules.amr.porterResponseMonitor." + monitor.getAttribute().getKey());
        model.addAttribute("attributeString", attributeString);

		List<LiteState> states = monitorDto.getStateGroup().getStatesList();
		model.addAttribute("states", states);
		model.addAttribute("monitorDto", monitorDto);
		model.addAttribute("mode", PageEditMode.VIEW);
	}

	private void setupErrorEditPageModelMap(PorterResponseMonitorDto monitorDto, ModelMap model, 
	                        YukonUserContext userContext, BindingResult bindingResult, FlashScope flashScope) {
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
        flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        setupEditPageModelMap(monitorDto, model, userContext);
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
        EnumPropertyEditor.register(binder, PorterResponseMonitorMatchStyle.class);
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
    public void setPointService(PointService pointService) {
        this.pointService = pointService;
    }

	@Autowired
	public void setPorterResponseMonitorDao(PorterResponseMonitorDao porterResponseMonitorDao) {
		this.porterResponseMonitorDao = porterResponseMonitorDao;
	}
	
	@Autowired
	public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
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

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }
    
    @Autowired
    public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}