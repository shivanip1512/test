package com.cannontech.web.amr.tamperFlagProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.amr.deviceread.service.GroupMeterReadService;
import com.cannontech.amr.meter.dao.GroupMetersDao;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

@Controller
@RequestMapping("/tamperFlagProcessing/process/*")
public class TamperFlagProcessingController implements InitializingBean {

	private TamperFlagMonitorDao tamperFlagMonitorDao;
	private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	private TamperFlagMonitorService tamperFlagMonitorService;
	private AlertService alertService;
	private GroupMeterReadService groupMeterReadService;
	private GroupCommandExecutor groupCommandExecutor;
	private GroupMetersDao groupMetersDao;
	private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	
	private Map<Integer, List<String>> monitorToRecentReadKeysCache;
	private Map<Integer, List<String>> monitorToRecentResetKeysCache;
	private static final String RESET_FLAGS_COMMAND = "putstatus reset";
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		monitorToRecentReadKeysCache = new HashMap<Integer, List<String>>();
		monitorToRecentResetKeysCache = new HashMap<Integer, List<String>>();
	}
	
	// EDIT
	@RequestMapping
    public void process(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws ServletException {
		
		int tamperFlagMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "tamperFlagMonitorId");
		TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
		
		String processError = ServletRequestUtils.getStringParameter(request, "processError");
		Boolean readOk = ServletRequestUtils.getBooleanParameter(request, "readOk");
		Boolean resetOk = ServletRequestUtils.getBooleanParameter(request, "resetOk");
		model.addAttribute("processError", processError);
		model.addAttribute("readOk", readOk);
		model.addAttribute("resetOk", resetOk);
		
		// tamper flag group device collection
		StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
		DeviceCollection tamperFlagGroupDeviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tamperFlagGroup);
		
		// read results
		List<GroupMeterReadResult> allReadsResults = new ArrayList<GroupMeterReadResult>();
		allReadsResults.addAll(groupMeterReadService.getPendingByType(CommandRequestExecutionType.TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ));
		allReadsResults.addAll(groupMeterReadService.getCompletedByType(CommandRequestExecutionType.TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ));
		
		List<GroupMeterReadResult> readResults = new ArrayList<GroupMeterReadResult>();
		List<String> readResultKeysForMonitor = monitorToRecentReadKeysCache.get(tamperFlagMonitorId);
		if (readResultKeysForMonitor != null) {
			for (GroupMeterReadResult result : allReadsResults) {
				if (readResultKeysForMonitor.contains(result.getKey())) {
					readResults.add(result);
				}
			}
			Collections.sort(readResults);
		}
		model.addAttribute("readResults", readResults);
		
		// reset results
		List<GroupCommandResult> allResetResults = new ArrayList<GroupCommandResult>();
		allResetResults.addAll(groupCommandExecutor.getPendingByType(CommandRequestExecutionType.TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_RESET));
		allResetResults.addAll(groupCommandExecutor.getCompletedByType(CommandRequestExecutionType.TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_RESET));
		
		List<GroupCommandResult> resetResults = new ArrayList<GroupCommandResult>();
		List<String> resetResultKeysForMonitor = monitorToRecentResetKeysCache.get(tamperFlagMonitorId);
		if (resetResultKeysForMonitor != null) {
			for (GroupCommandResult result : allResetResults) {
				if (resetResultKeysForMonitor.contains(result.getKey())) {
					resetResults.add(result);
				}
			}
			Collections.sort(resetResults);
		}
		model.addAttribute("resetResults", resetResults);
		
		model.addAttribute("tamperFlagMonitor", tamperFlagMonitor);
		model.addAttribute("tamperFlagGroupBase", SystemGroupEnum.TAMPER_FLAG_PROCESSING.getFullPath());
		model.addAttribute("tamperFlagGroupDeviceCollection", tamperFlagGroupDeviceCollection);
		
	}
	
	// READ FLAGS
	@RequestMapping
    public String readFlags(HttpServletRequest request, YukonUserContext userContext, ModelMap model) throws ServletException {
		
		String processError = null;
		int tamperFlagMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "tamperFlagMonitorId");
		
		try {
			
			final TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
			
			StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
			DeviceCollection tamperFlagGroupDeviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tamperFlagGroup);
			
			// alert callback
			SimpleCallback<GroupMeterReadResult> alertCallback = new SimpleCallback<GroupMeterReadResult>() {
	            @Override
	            public void handle(GroupMeterReadResult result) {
	            	
	            	// alert
	                ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.web.modules.amr.tamperFlagProcessing.readInternalFlags.completionAlert");
	                resolvableTemplate.addData("tamperFlagMonitorName", tamperFlagMonitor.getTamperFlagMonitorName());
	                resolvableTemplate.addData("tamperFlagMonitorId", tamperFlagMonitor.getTamperFlagMonitorId());
	                int successCount = result.getResultHolder().getResultStrings().size();
	                int total = (int)result.getOriginalDeviceCollectionCopy().getDeviceCount();
	                resolvableTemplate.addData("percentSuccess", (float)((successCount * 100) / total));
	                resolvableTemplate.addData("resultKey", result.getKey());
	                
	                TamperFlagProcessingReadInternalFlagsCompletionAlert readInternalFlagsCompletionAlert = new TamperFlagProcessingReadInternalFlagsCompletionAlert(new Date(), resolvableTemplate);
	                
	                alertService.add(readInternalFlagsCompletionAlert);
	            }
	        };
		
	        String resultKey = groupMeterReadService.readDeviceCollection(tamperFlagGroupDeviceCollection, Collections.singleton(BuiltInAttribute.GENERAL_ALARM_FLAG), CommandRequestExecutionType.TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ, alertCallback, userContext.getYukonUser());
	        addReadResultKeyToCache(tamperFlagMonitorId, resultKey);
		
		} catch (PaoAuthorizationException e) {
			processError = "User does not have access to run read flags command.";
		} catch (Exception e) {
			processError = e.getMessage();
		}
		
		model.addAttribute("tamperFlagMonitorId", tamperFlagMonitorId);
		model.addAttribute("processError", processError);
		model.addAttribute("readOk", processError == null);
		
		return "redirect:process";
	}
	
	// RESET FLAGS
	@RequestMapping
    public String resetFlags(HttpServletRequest request, YukonUserContext userContext, ModelMap model) throws ServletException {
		
		String processError = null;
		int tamperFlagMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "tamperFlagMonitorId");
		
		try {
			
			final TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
			
			StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
			DeviceCollection tamperFlagGroupDeviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tamperFlagGroup);
			
			SimpleCallback<GroupCommandResult> dummyCallback = new SimpleCallback<GroupCommandResult>() {
	    		@Override
	    		public void handle(GroupCommandResult item) throws Exception {}
	        };
	        
			String resultKey = groupCommandExecutor.execute(tamperFlagGroupDeviceCollection, RESET_FLAGS_COMMAND, CommandRequestExecutionType.TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_RESET, dummyCallback, userContext.getYukonUser());
			addResetResultKeyToCache(tamperFlagMonitorId, resultKey);
			
		} catch (Exception e) {
			processError = e.getMessage();
		}
		
		model.addAttribute("tamperFlagMonitorId", tamperFlagMonitorId);
		model.addAttribute("processError", processError);
		model.addAttribute("resetOk", processError == null);
		
		return "redirect:process";
	}
	
	// CLEAR TAMPER FLAG GROUP
	@RequestMapping
	public String clearTamperFlagGroup(HttpServletRequest request, YukonUserContext userContext, ModelMap model) throws ServletException {
		
		int tamperFlagMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "tamperFlagMonitorId");
		TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
		
		StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
		
		List<? extends YukonDevice> deviceList = groupMetersDao.getChildMetersByGroup(tamperFlagGroup);
        deviceGroupMemberEditorDao.removeDevices(tamperFlagGroup, deviceList);
        
        model.addAttribute("tamperFlagMonitorId", tamperFlagMonitorId);
		
        return "redirect:process";
	}
	
	private void addReadResultKeyToCache(int tamperFlagMonitorId, String resultKey) {
		
		if (!monitorToRecentReadKeysCache.containsKey(tamperFlagMonitorId)) {
			monitorToRecentReadKeysCache.put(tamperFlagMonitorId, new ArrayList<String>());
		}
		monitorToRecentReadKeysCache.get(tamperFlagMonitorId).add(resultKey);
	}
	
	private void addResetResultKeyToCache(int tamperFlagMonitorId, String resultKey) {
		
		if (!monitorToRecentResetKeysCache.containsKey(tamperFlagMonitorId)) {
			monitorToRecentResetKeysCache.put(tamperFlagMonitorId, new ArrayList<String>());
		}
		monitorToRecentResetKeysCache.get(tamperFlagMonitorId).add(resultKey);
	}
	
	@Autowired
	public void setTamperFlagMonitorDao(TamperFlagMonitorDao tamperFlagMonitorDao) {
		this.tamperFlagMonitorDao = tamperFlagMonitorDao;
	}
	
	@Autowired
	public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
		this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
	}
	
	@Autowired
	public void setTamperFlagMonitorService(TamperFlagMonitorService tamperFlagMonitorService) {
		this.tamperFlagMonitorService = tamperFlagMonitorService;
	}
	
	@Autowired
	public void setAlertService(AlertService alertService) {
		this.alertService = alertService;
	}
	
	@Autowired
	public void setGroupMeterReadService(GroupMeterReadService groupMeterReadService) {
		this.groupMeterReadService = groupMeterReadService;
	}
	
	@Autowired
    public void setGroupCommandExecutor(GroupCommandExecutor groupCommandExecutor) {
        this.groupCommandExecutor = groupCommandExecutor;
    }
	
	@Autowired
	public void setGroupMetersDao(GroupMetersDao groupMetersDao) {
		this.groupMetersDao = groupMetersDao;
	}
	
	@Autowired
	public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
		this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
	}
}
