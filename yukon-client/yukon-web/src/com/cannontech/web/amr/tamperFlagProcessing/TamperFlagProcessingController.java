package com.cannontech.web.amr.tamperFlagProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.amr.meter.dao.GroupMetersDao;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.BaseAlert;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

@Controller
@RequestMapping("/tamperFlagProcessing/process/*")
@CheckRoleProperty(YukonRoleProperty.TAMPER_FLAG_PROCESSING)
public class TamperFlagProcessingController {

    @Autowired private TamperFlagMonitorDao tamperFlagMonitorDao;
	@Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	@Autowired private TamperFlagMonitorService tamperFlagMonitorService;
	@Autowired private AlertService alertService;
	@Autowired private GroupCommandExecutor groupCommandExecutor;
	@Autowired private GroupMetersDao groupMetersDao;
	@Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	@Autowired private DeviceGroupService deviceGroupService;
	@Autowired private DeviceAttributeReadService deviceAttributeReadService;
	
	private ListMultimap<Integer, String> monitorToRecentReadKeysCache = ArrayListMultimap.create();
	private ListMultimap<Integer, String> monitorToRecentResetKeysCache = ArrayListMultimap.create();
	private static final String RESET_FLAGS_COMMAND = "putstatus reset";
	
	// EDIT
	@RequestMapping("process")
    public void process(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws ServletException {
		
		int tamperFlagMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "tamperFlagMonitorId");
		TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
		
		// tamper flag group device collection
		StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
		DeviceCollection tamperFlagGroupDeviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tamperFlagGroup);
		
		// read results
		List<GroupMeterReadResult> allReadsResults = new ArrayList<GroupMeterReadResult>();
		allReadsResults.addAll(deviceAttributeReadService.getPendingByType(DeviceRequestType.GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ));
		allReadsResults.addAll(deviceAttributeReadService.getCompletedByType(DeviceRequestType.GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ));
		
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
		allResetResults.addAll(groupCommandExecutor.getPendingByType(DeviceRequestType.GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_RESET));
		allResetResults.addAll(groupCommandExecutor.getCompletedByType(DeviceRequestType.GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_RESET));
		
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
		String basePath = deviceGroupService.getFullPath(SystemGroupEnum.TAMPER_FLAG);
		model.addAttribute("tamperFlagGroupBase", basePath);
		model.addAttribute("tamperFlagGroupDeviceCollection", tamperFlagGroupDeviceCollection);
		
	}
	
	// READ FLAGS
	@RequestMapping("readFlags")
    public String readFlags(HttpServletRequest request, YukonUserContext userContext, ModelMap model) throws ServletException {
		
		int tamperFlagMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "tamperFlagMonitorId");
		final TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
		
		StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
		DeviceCollection tamperFlagGroupDeviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tamperFlagGroup);
		
		// alert callback
		SimpleCallback<GroupMeterReadResult> alertCallback = new SimpleCallback<GroupMeterReadResult>() {
            @Override
            public void handle(GroupMeterReadResult result) {
            	
            	// alert
                ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.web.modules.amr.tamperFlagDetail.readInternalFlags.completionAlert");
                resolvableTemplate.addData("tamperFlagMonitorName", tamperFlagMonitor.getTamperFlagMonitorName());
                resolvableTemplate.addData("tamperFlagMonitorId", tamperFlagMonitor.getTamperFlagMonitorId());
                int successCount = result.getResultHolder().getResultStrings().size();
                int total = (int)result.getOriginalDeviceCollectionCopy().getDeviceCount();
                float percentSuccess = 100.0f;
                if (total > 0) {
                    percentSuccess = ((float) successCount * 100) / total;
                }
                resolvableTemplate.addData("percentSuccess", percentSuccess);
                resolvableTemplate.addData("resultKey", result.getKey());
                
                Alert readInternalFlagsCompletionAlert = new BaseAlert(new Date(), resolvableTemplate) {
                    @Override
                    public com.cannontech.common.alert.model.AlertType getType() {
                        return AlertType.TAMPER_FLAG_PROCESSING_READ_INTERNAL_FLAGS_COMPLETION;
                    };
                };
                                
                alertService.add(readInternalFlagsCompletionAlert);
            }
        };
	
        String resultKey = deviceAttributeReadService.initiateRead(tamperFlagGroupDeviceCollection, Collections.singleton(BuiltInAttribute.GENERAL_ALARM_FLAG), DeviceRequestType.GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ, alertCallback, userContext.getYukonUser());
        monitorToRecentReadKeysCache.put(tamperFlagMonitorId, resultKey);
		
		
		model.addAttribute("tamperFlagMonitorId", tamperFlagMonitorId);
		model.addAttribute("readOk", true);
		
		return "redirect:process";
	}
	
	// RESET FLAGS
	@RequestMapping("resetFlags")
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
	        
			String resultKey = groupCommandExecutor.execute(tamperFlagGroupDeviceCollection, RESET_FLAGS_COMMAND, DeviceRequestType.GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_RESET, dummyCallback, userContext.getYukonUser());
			monitorToRecentResetKeysCache.put(tamperFlagMonitorId, resultKey);
			
		} catch (Exception e) {
			processError = e.getMessage();
		}
		
		model.addAttribute("tamperFlagMonitorId", tamperFlagMonitorId);
		model.addAttribute("processError", processError);
		model.addAttribute("resetOk", processError == null);
		
		return "redirect:process";
	}
	
	// CLEAR TAMPER FLAG GROUP
	@RequestMapping("clearTamperFlagGroup")
	public String clearTamperFlagGroup(HttpServletRequest request, YukonUserContext userContext, ModelMap model) throws ServletException {
		
		int tamperFlagMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "tamperFlagMonitorId");
		TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
		
		StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
		
		List<? extends YukonDevice> deviceList = groupMetersDao.getChildMetersByGroup(tamperFlagGroup);
        deviceGroupMemberEditorDao.removeDevices(tamperFlagGroup, deviceList);
        
        model.addAttribute("tamperFlagMonitorId", tamperFlagMonitorId);
		
        return "redirect:process";
	}
	
}
