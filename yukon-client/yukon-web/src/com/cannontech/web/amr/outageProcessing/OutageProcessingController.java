package com.cannontech.web.amr.outageProcessing;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.meter.dao.GroupMetersDao;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
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

@RequestMapping("/outageProcessing/process/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.OUTAGE_PROCESSING)
public class OutageProcessingController extends MultiActionController {

    @Autowired private OutageMonitorDao outageMonitorDao;
	@Autowired private OutageMonitorService outageMonitorService;
	@Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	@Autowired private GroupMetersDao groupMetersDao;
	@Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	@Autowired private AlertService alertService;
	@Autowired private DeviceGroupService deviceGroupService;
	@Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
	
	private final ListMultimap<Integer, String> monitorToRecentReadKeysCache = ArrayListMultimap.create();
	
	// PROCESS
	@RequestMapping(value = "process", method = RequestMethod.GET)
	public String process(ModelMap model, int outageMonitorId) throws ServletException {

		OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		
		// read results
/*		List<GroupMeterReadResult> allReadsResults = new ArrayList<GroupMeterReadResult>();
		allReadsResults.addAll(deviceAttributeReadService.getPendingByType(DeviceRequestType.GROUP_OUTAGE_PROCESSING_OUTAGE_LOGS_READ));
		allReadsResults.addAll(deviceAttributeReadService.getCompletedByType(DeviceRequestType.GROUP_OUTAGE_PROCESSING_OUTAGE_LOGS_READ));
		
		List<GroupMeterReadResult> readResults = new ArrayList<GroupMeterReadResult>();
		List<String> readResultKeysForMonitor = monitorToRecentReadKeysCache.get(outageMonitorId);
		if (readResultKeysForMonitor != null) {
			for (GroupMeterReadResult result : allReadsResults) {
				if (readResultKeysForMonitor.contains(result.getKey())) {
					readResults.add(result);
				}
			}
			Collections.sort(readResults);
		}
		mav.addObject("readResults", readResults);*/
		
		model.addAttribute("outageMonitor", outageMonitor);
		model.addAttribute("outageGroupBase", deviceGroupService.getFullPath(SystemGroupEnum.OUTAGE));
		model.addAttribute("reportStartDate", DateUtils.addMonths(new Date(), -1));
		
		return "outageProcessing/process.jsp";
	}
	
	
	// READ OUTAGE LOGS
	@RequestMapping(value = "readOutageLogs", method = RequestMethod.GET)
	public String readOutageLogs(ModelMap model, int outageMonitorId, boolean removeFromOutageGroupAfterRead, YukonUserContext userContext, 
	                             HttpServletRequest request) throws ServletException {

		final OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		
        StoredDeviceGroup outageGroup = outageMonitorService.getOutageGroup(outageMonitor.getOutageMonitorName());
		DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(outageGroup);
		
		// alert callback
        SimpleCallback<CollectionActionResult> alertCallback =
                CollectionActionAlertHelper.createAlert(AlertType.OUTAGE_PROCESSING_READ_LOGS_COMPLETION, alertService,
                    messageResolver.getMessageSourceAccessor(userContext), request);
	
        int cacheKey = deviceAttributeReadService.initiateRead(deviceCollection, Collections.singleton(BuiltInAttribute.OUTAGE_LOG), 
                                                                DeviceRequestType.GROUP_OUTAGE_PROCESSING_OUTAGE_LOGS_READ, alertCallback, userContext);
        monitorToRecentReadKeysCache.put(outageMonitorId, Integer.toString(cacheKey));
		
		model.addAttribute("outageMonitorId", outageMonitorId);
		model.addAttribute("readOk", true);
		
		//return "redirect:process";
		return "redirect:/bulk/progressReport/detail?key=" + cacheKey;
	}
	
	
	
	
	// CLEAR OUTAGES GROUP
	@RequestMapping(value = "clearOutagesGroup", method = RequestMethod.GET)
	public String clearOutagesGroup(ModelMap model, int outageMonitorId) throws ServletException {

		OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		
		StoredDeviceGroup outageGroup = outageMonitorService.getOutageGroup(outageMonitor.getOutageMonitorName());
		
		List<? extends YukonDevice> deviceList = groupMetersDao.getChildMetersByGroup(outageGroup);
        deviceGroupMemberEditorDao.removeDevices(outageGroup, deviceList);
        
		model.addAttribute("outageMonitorId", outageMonitorId);
		
		return "redirect:process";
	}
}
