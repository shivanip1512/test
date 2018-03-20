package com.cannontech.web.amr.tamperFlagProcessing;

import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.meter.dao.GroupMetersDao;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.CollectionActionAlertHelper;
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
	@Autowired private CommandExecutionService commandExecutionService;
	@Autowired private GroupMetersDao groupMetersDao;
	@Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	@Autowired private DeviceGroupService deviceGroupService;
	@Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private CollectionActionService collectionActionService;
	
	private ListMultimap<Integer, Integer> monitorToRecentReadKeysCache = ArrayListMultimap.create();
	private ListMultimap<Integer, Integer> monitorToRecentResetKeysCache = ArrayListMultimap.create();
	private static final String RESET_FLAGS_COMMAND = "putstatus reset";
	
	// EDIT
	@RequestMapping(value = "process", method = RequestMethod.GET)
    public String process(ModelMap model, int tamperFlagMonitorId) throws ServletException {
		
		TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
		
		// tamper flag group device collection
		StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
		DeviceCollection tamperFlagGroupDeviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tamperFlagGroup);
		
       List<CollectionActionResult> results =
            collectionActionService.getCachedResults(monitorToRecentReadKeysCache.get(tamperFlagMonitorId));

        // read results
        /*
         * List<GroupMeterReadResult> allReadsResults = new ArrayList<GroupMeterReadResult>();
         * allReadsResults.addAll(deviceAttributeReadService.getPendingByType(DeviceRequestType.
         * GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ));
         * allReadsResults.addAll(deviceAttributeReadService.getCompletedByType(DeviceRequestType.
         * GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ));
         * List<GroupMeterReadResult> readResults = new ArrayList<GroupMeterReadResult>();
         * List<String> readResultKeysForMonitor = monitorToRecentReadKeysCache.get(tamperFlagMonitorId);
         * if (readResultKeysForMonitor != null) {
         * for (GroupMeterReadResult result : allReadsResults) {
         * if (readResultKeysForMonitor.contains(result.getKey())) {
         * readResults.add(result);
         * }
         * }
         * Collections.sort(readResults);
         * }
         * model.addAttribute("readResults", readResults);
         */
		
		// reset results
/*		List<GroupCommandResult> allResetResults = new ArrayList<GroupCommandResult>();
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
		model.addAttribute("resetResults", resetResults);*/
		
		model.addAttribute("tamperFlagMonitor", tamperFlagMonitor);
		String basePath = deviceGroupService.getFullPath(SystemGroupEnum.TAMPER_FLAG);
		model.addAttribute("tamperFlagGroupBase", basePath);
		model.addAttribute("tamperFlagGroupDeviceCollection", tamperFlagGroupDeviceCollection);
		
	    return "tamperFlagProcessing/process/process.jsp";
	}
	
	// READ FLAGS
	@RequestMapping(value = "readFlags", method = RequestMethod.GET)
    public String readFlags(ModelMap model, int tamperFlagMonitorId, YukonUserContext userContext, HttpServletRequest request) throws ServletException {
		
		TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
		
		StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
		DeviceCollection tamperFlagGroupDeviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tamperFlagGroup);
				
		// alert callback
        SimpleCallback<CollectionActionResult> alertCallback =
                CollectionActionAlertHelper.createAlert(AlertType.TAMPER_FLAG_PROCESSING_READ_INTERNAL_FLAGS_COMPLETION, alertService,
                    messageResolver.getMessageSourceAccessor(userContext), request);
	
        int cacheKey = deviceAttributeReadService.initiateRead(tamperFlagGroupDeviceCollection, Collections.singleton(BuiltInAttribute.GENERAL_ALARM_FLAG), 
                                                                DeviceRequestType.GROUP_TAMPER_FLAG_PROCESSING_INTERNAL_STATUS_READ, alertCallback, userContext);
        monitorToRecentReadKeysCache.put(tamperFlagMonitorId, cacheKey);
		
		model.addAttribute("tamperFlagMonitorId", tamperFlagMonitorId);
		
		//return "redirect:process";
	    return "redirect:/bulk/progressReport/detail?key=" + cacheKey;
	}
	
	// RESET FLAGS
	@RequestMapping(value = "resetFlags", method = RequestMethod.GET)
    public String resetFlags(ModelMap model, int tamperFlagMonitorId, YukonUserContext userContext) throws ServletException {

		TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
		
		StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
		DeviceCollection tamperFlagGroupDeviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tamperFlagGroup);

        int cacheKey = commandExecutionService.execute(CollectionAction.SEND_COMMAND, null, tamperFlagGroupDeviceCollection,
                                                       RESET_FLAGS_COMMAND, CommandRequestType.DEVICE, DeviceRequestType.GROUP_COMMAND, null, userContext);
		monitorToRecentResetKeysCache.put(tamperFlagMonitorId, cacheKey);

		model.addAttribute("tamperFlagMonitorId", tamperFlagMonitorId);
		//return "redirect:process";
	    return "redirect:/bulk/progressReport/detail?key=" + cacheKey;
	}
	
	// CLEAR TAMPER FLAG GROUP
	@RequestMapping(value = "clearTamperFlagGroup", method = RequestMethod.GET)
	public String clearTamperFlagGroup(ModelMap model, int tamperFlagMonitorId) throws ServletException {
		
		TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
		
		StoredDeviceGroup tamperFlagGroup = tamperFlagMonitorService.getTamperFlagGroup(tamperFlagMonitor.getTamperFlagMonitorName());
		
		List<? extends YukonDevice> deviceList = groupMetersDao.getChildMetersByGroup(tamperFlagGroup);
        deviceGroupMemberEditorDao.removeDevices(tamperFlagGroup, deviceList);
        
        model.addAttribute("tamperFlagMonitorId", tamperFlagMonitorId);
		
        return "redirect:process";
	}
	
}
