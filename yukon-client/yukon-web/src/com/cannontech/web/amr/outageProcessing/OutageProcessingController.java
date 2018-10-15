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

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.meter.dao.GroupMetersDao;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.CollectionActionAlertHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

@RequestMapping("/outageProcessing/process/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.OUTAGE_PROCESSING)
public class OutageProcessingController {

    @Autowired private OutageMonitorDao outageMonitorDao;
	@Autowired private OutageMonitorService outageMonitorService;
	@Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	@Autowired private GroupMetersDao groupMetersDao;
	@Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	@Autowired private AlertService alertService;
	@Autowired private DeviceGroupService deviceGroupService;
	@Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private CollectionActionService collectionActionService;
	
	private final ListMultimap<Integer, Integer> monitorToRecentReadKeysCache = ArrayListMultimap.create();
    private final static String baseKey = "yukon.web.modules.amr.outageProcessing.";
	
	// PROCESS
	@RequestMapping(value = "process", method = RequestMethod.GET)
	public String process(ModelMap model, int outageMonitorId) throws ServletException {

		OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		
		//check for cached results
        List<CollectionActionResult> results =
            collectionActionService.getCachedResults(monitorToRecentReadKeysCache.get(outageMonitorId));
        model.addAttribute("readResults", results);
		
		model.addAttribute("outageMonitor", outageMonitor);
		model.addAttribute("outageGroupBase", deviceGroupService.getFullPath(SystemGroupEnum.OUTAGE));
		model.addAttribute("reportStartDate", DateUtils.addMonths(new Date(), -1));
		
		return "outageProcessing/process.jsp";
	}
	
	// READ OUTAGE LOGS
	@RequestMapping(value = "readOutageLogs", method = RequestMethod.GET)
	public String readOutageLogs(ModelMap model, int outageMonitorId, boolean removeFromOutageGroupAfterRead, YukonUserContext userContext, 
	                             HttpServletRequest request, FlashScope flash) throws ServletException {
		final OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
	    model.addAttribute("outageMonitorId", outageMonitorId);
		
        StoredDeviceGroup outageGroup = outageMonitorService.getOutageGroup(outageMonitor.getName());
		DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(outageGroup);
		
		if (deviceCollection.getDeviceCount() == 0) {
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "readOutageLogs.noDevices"));
	        return "redirect:process";
		}
				    	
        SimpleCallback<CollectionActionResult> outageRemovalCallback = new SimpleCallback<CollectionActionResult>() {
            @Override
            public void handle(CollectionActionResult result) throws Exception {
                if (removeFromOutageGroupAfterRead) {
                    DeviceCollection successCollection = result.getDeviceCollection(CollectionActionDetail.SUCCESS);
                    deviceGroupMemberEditorDao.removeDevices(outageGroup, successCollection.getDeviceList());
                }
            }
        };

        // alert callback
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String detailText = accessor.getMessage(baseKey + "readOutagesLog.completionAlertDetailText", outageMonitor.getName());
        SimpleCallback<CollectionActionResult> alertCallback =
            CollectionActionAlertHelper.createAlert(AlertType.OUTAGE_PROCESSING_READ_LOGS_COMPLETION, alertService,
                accessor, outageRemovalCallback, request, detailText);

        int cacheKey = deviceAttributeReadService.initiateRead(deviceCollection, Collections.singleton(BuiltInAttribute.OUTAGE_LOG), 
                                                                DeviceRequestType.GROUP_OUTAGE_PROCESSING_OUTAGE_LOGS_READ, alertCallback, userContext);
        monitorToRecentReadKeysCache.put(outageMonitorId, cacheKey);
		
		model.addAttribute("readOk", true);
		
		return "redirect:process";
	}
	
	// CLEAR OUTAGES GROUP
	@RequestMapping(value = "clearOutagesGroup", method = RequestMethod.GET)
	public String clearOutagesGroup(ModelMap model, int outageMonitorId) throws ServletException {

		OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		
		StoredDeviceGroup outageGroup = outageMonitorService.getOutageGroup(outageMonitor.getName());
		
		List<? extends YukonDevice> deviceList = groupMetersDao.getChildMetersByGroup(outageGroup);
        deviceGroupMemberEditorDao.removeDevices(outageGroup, deviceList);
        
		model.addAttribute("outageMonitorId", outageMonitorId);
		
		return "redirect:process";
	}
}
