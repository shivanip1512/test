package com.cannontech.web.stars.dr.operator.deviceReconfig;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.deviceReconfig.dao.DeviceReconfigDao;
import com.cannontech.stars.dr.deviceReconfig.model.DeviceReconfigDeviceType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExprTagAmPmOptionEnum;
import com.cannontech.web.amr.util.cronExpressionTag.CronExprTagDailyOptionEnum;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.input.type.BooleanType;
import com.cannontech.web.input.type.DateType;
import com.cannontech.web.input.type.IntegerSetType;
import com.cannontech.web.input.type.IntegerType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.deviceReconfig.service.DeviceReconfigService;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEVICE_RECONFIG)
public class DeviceReconfigController {
	
	private DeviceReconfigDao deviceReconfigDao;
	private StarsDatabaseCache starsDatabaseCache;
	private DeviceReconfigService deviceReconfigService;
	private DateFormattingService dateFormattingService;
	
	// CHOOSE SELECTION TYPE
	@RequestMapping(value = "/operator/deviceReconfig/chooseSelectionType", method = RequestMethod.GET)
    public String chooseSelectionType(ModelMap modelMap, YukonUserContext userContext) {
		

		return "operator/deviceReconfig/chooseSelectionType.jsp";
	}
	
	// CHOOSE OPERATION
	@RequestMapping(value = "/operator/deviceReconfig/chooseOperation")
    public String chooseOperation(ModelMap modelMap, YukonUserContext userContext) {
		

		return "operator/deviceReconfig/chooseOperation.jsp";
	}
	
	// ADD RULE DIALOG
	@RequestMapping(value = "/operator/deviceReconfig/addRuleDialog")
    public String addRuleDialog(ModelMap modelMap, YukonUserContext userContext) {
		

		return "operator/deviceReconfig/addRuleDialog.jsp";
	}
	
	// HOME
	@RequestMapping(value = "/operator/deviceReconfig/home", method = RequestMethod.GET)
    public String home(ModelMap modelMap, YukonUserContext userContext) {
		

		return "operator/deviceReconfig/home.jsp";
	}
	
	// BATCH DETAIL
	@RequestMapping(value = "/operator/deviceReconfig/batchDetail", method = RequestMethod.GET)
    public String batchDetail(ModelMap modelMap, YukonUserContext userContext) {
		

		return "operator/deviceReconfig/batchDetail.jsp";
	}
	
	// SETUP SELECTION
	@RequestMapping(value = "/operator/deviceReconfig/setupSelection", method = RequestMethod.GET)
    public String setupSelection(@ModelAttribute("deviceReconfigOptions") DeviceReconfigOptions deviceReconfigOptions, HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) {
		
		modelMap.addAttribute("deviceReconfigOptions", deviceReconfigOptions);
		
		// errors
		String errorKeys = ServletRequestUtils.getStringParameter(request, "errorKeys", null);
		if (errorKeys != null) {
			String[] errorKeysList = StringUtils.split(errorKeys, ",");
			List<MessageSourceResolvable> errors = Lists.newArrayListWithCapacity(errorKeysList.length);
			for (String errorKey : errorKeysList) {
				errors.add(new YukonMessageSourceResolvable("yukon.web.modules.dr.deviceReconfig.home.error." + errorKey));
			}
			modelMap.addAttribute("errors", errors);
		}
		
		// device types
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		List<DeviceReconfigDeviceType> availableDeviceTypes = deviceReconfigDao.getDeviceTypes(energyCompany.getEnergyCompanyID());
		modelMap.addAttribute("availableDeviceTypes", availableDeviceTypes);

		return "operator/deviceReconfig/setupSelection.jsp";
	}
	
	// SETUP SCHEDULE
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/operator/deviceReconfig/setupSchedule", method = RequestMethod.POST)
    public String setupSchedule(@ModelAttribute("deviceReconfigOptions") DeviceReconfigOptions deviceReconfigOptions, BindingResult bindingResult, ModelMap modelMap, YukonUserContext userContext) {
		
		// errors
		List<FieldError> fieldErrors = (List<FieldError>)bindingResult.getFieldErrors();
		List<String> errorKeysList = Lists.newArrayListWithCapacity(fieldErrors.size());
		for (ObjectError fieldError : fieldErrors) {
			for (Object msg : fieldError.getArguments()) {
				errorKeysList.add(((MessageSourceResolvable)msg).getDefaultMessage());
			}
		}
		
		if (errorKeysList.size() > 0) {
			modelMap.addAttribute("errorKeys", StringUtils.join(errorKeysList, ","));
			modelMap.addAllAttributes(deviceReconfigOptions.getUrlParameterMap());
			return "redirect:home";
		}
		
		// get inventory
		List<Integer> programPaoIds = Lists.newArrayList();
		programPaoIds.add(67);
		deviceReconfigOptions.setLoadProgramPaoIds(programPaoIds);
		List<Integer> inventoryIdsList = deviceReconfigService.getInventoryIdsIntersectionFromReconfigOptions(deviceReconfigOptions, userContext);
		modelMap.addAttribute("inventoryIdsCount", inventoryIdsList.size());
		
		// run schedule
		CronExpressionTagState cronState = new CronExpressionTagState();
		cronState.setCronExpressionAmPm(CronExprTagAmPmOptionEnum.PM);
		cronState.setHour(9);
		cronState.setMinute(55);
		cronState.setCronExpressionDailyOption(CronExprTagDailyOptionEnum.WEEKDAYS);
		modelMap.addAttribute("cronState", cronState);
		
		return "operator/deviceReconfig/setupSchedule.jsp";
	}
	
	// PREVIEW SCHEDULE
	@RequestMapping(value = "/operator/deviceReconfig/previewSchedule", method = RequestMethod.POST)
    public String setupSchedule(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException, ParseException {
		
		int inventoryIdsCount = ServletRequestUtils.getRequiredIntParameter(request, "inventoryIdsCount");
		String startDate = ServletRequestUtils.getRequiredStringParameter(request, "startDate");
		int startHour = ServletRequestUtils.getRequiredIntParameter(request, "startHour");
		int startMinute = ServletRequestUtils.getRequiredIntParameter(request, "startMinute");
//		String startAmPm = ServletRequestUtils.getRequiredStringParameter(request, "startAmPm");
//		int pageDelay = ServletRequestUtils.getRequiredIntParameter(request, "pageDelay");
		int batchCount = ServletRequestUtils.getRequiredIntParameter(request, "batchCount");
		int batchDelay = ServletRequestUtils.getRequiredIntParameter(request, "batchDelay");
		
		int remainder = inventoryIdsCount % batchCount;
		int quotient = (inventoryIdsCount - remainder) / batchCount;
		
		//TODO make this right
		Date dateTime = dateFormattingService.flexibleDateParser(startDate, userContext);
		dateTime = DateUtils.truncate(dateTime, Calendar.DATE);
		dateTime = DateUtils.addHours(dateTime, startHour);
		dateTime = DateUtils.addMinutes(dateTime, startMinute);
		
		List<PreviewScheduleRow> previewScheduleRows = Lists.newArrayList();
		for (int i = 0; i < batchCount; i++) {
			
			if (i == batchCount - 1) {
				quotient += remainder;
			}
			
			PreviewScheduleRow row = new PreviewScheduleRow(i + 1, (Date)dateTime.clone(), quotient);
			previewScheduleRows.add(row);
			
			dateTime = DateUtils.addMinutes(dateTime, batchDelay);
		}
		
		 
		modelMap.addAttribute("previewScheduleRows", previewScheduleRows);
		modelMap.addAttribute("rowCount", previewScheduleRows.size());
		
		return "operator/deviceReconfig/previewSchedulePopup.jsp";
	}
	
	public class PreviewScheduleRow {
		
		private int batchNumber;
		private Date dateTime;
		private int deviceCount;
		
		public PreviewScheduleRow(int batchNumber, Date dateTime, int deviceCount) {
			this.batchNumber = batchNumber;
			this.dateTime = dateTime;
			this.deviceCount = deviceCount;
		}
		
		public int getBatchNumber() {
			return batchNumber;
		}
		public Date getDateTime() {
			return dateTime;
		}
		public int getDeviceCount() {
			return deviceCount;
		}
	}
	
	
	// PROCESS
	@RequestMapping(value = "/operator/deviceReconfig/process", method = RequestMethod.POST)
    public String process(@ModelAttribute("deviceReconfigOptions") DeviceReconfigOptions deviceReconfigOptions, BindingResult bindingResult, ModelMap modelMap, YukonUserContext userContext) {
		
		
		return "redirect:http://www.google.com";
	}
	
	// PREVIEW
	@RequestMapping(value = "/operator/deviceReconfig/preview", method = RequestMethod.POST)
    public String process(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		String selectionTypeStr = ServletRequestUtils.getRequiredStringParameter(request, "selectionType");
		DeviceReconfigSelectionType deviceReconfigSelectionType = DeviceReconfigSelectionType.valueOf(selectionTypeStr);
		List<LiteStarsLMHardware> inventoryList = deviceReconfigService.getInventoryBySelectionTypeFromRequest(deviceReconfigSelectionType, request, userContext);
		
		modelMap.addAttribute("inventoryList", inventoryList);
		modelMap.addAttribute("objectCount", inventoryList.size());
		
		return "operator/deviceReconfig/inventoryPreviewPopup.jsp";
	}
	
	// BINDER
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		
		binder.registerCustomEditor(Object.class, "deviceSelectionStyle", new DeviceReconfigDeviceSelectionStylePropertyEditor());
		
		binder.registerCustomEditor(Object.class, "loadGroupPaoIds", (new IntegerSetType()).getPropertyEditor());
		binder.registerCustomEditor(Object.class, "loadProgramPaoIds", (new IntegerSetType()).getPropertyEditor());
		binder.registerCustomEditor(Object.class, "fieldInstallDate", (new DateType()).getPropertyEditor());
		binder.registerCustomEditor(Object.class, "programSignupDate", (new DateType()).getPropertyEditor());
		binder.registerCustomEditor(Object.class, "deviceType", (new IntegerType()).getPropertyEditor());
		binder.registerCustomEditor(Object.class, "serialNumberRangeStart", (new IntegerType()).getPropertyEditor());
		binder.registerCustomEditor(Object.class, "serialNumberRangeEnd", (new IntegerType()).getPropertyEditor());
		binder.registerCustomEditor(Object.class, "notEnrolled", (new BooleanType()).getPropertyEditor());
		
		binder.registerCustomEditor(Object.class, "reconfigurationStyle", new DeviceReconfigReconfigurationStylePropertyEditor());
		binder.registerCustomEditor(Object.class, "reconfigStyleByLoadGroupId", (new IntegerType()).getPropertyEditor());
	}
	
	private class DeviceReconfigDeviceSelectionStylePropertyEditor extends PropertyEditorSupport {
		
		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			setValue(DeviceReconfigDeviceSelectionStyle.valueOf(text));
		}
		
		@Override
		public String getAsText() {
			DeviceReconfigDeviceSelectionStyle deviceReconfigDeviceSelectionStyle = (DeviceReconfigDeviceSelectionStyle)getValue();
			return deviceReconfigDeviceSelectionStyle.name();
		}
	}
	
	private class DeviceReconfigReconfigurationStylePropertyEditor extends PropertyEditorSupport {
		
		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			setValue(DeviceReconfigReconfigurationStyle.valueOf(text));
		}
		
		@Override
		public String getAsText() {
			DeviceReconfigReconfigurationStyle deviceReconfigReconfigurationStyle = (DeviceReconfigReconfigurationStyle)getValue();
			return deviceReconfigReconfigurationStyle.name();
		}
	}
	
	@Autowired
	public void setDeviceReconfigDao(DeviceReconfigDao deviceReconfigDao) {
		this.deviceReconfigDao = deviceReconfigDao;
	}
	
	@Autowired
	public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
		this.starsDatabaseCache = starsDatabaseCache;
	}
	
	@Autowired
	public void setDeviceReconfigService(DeviceReconfigService deviceReconfigService) {
		this.deviceReconfigService = deviceReconfigService;
	}
	
	@Autowired
	public void setDateFormattingService(DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
}
