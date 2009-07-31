package com.cannontech.web.amr.outageProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.amr.deviceread.service.GroupMeterReadService;
import com.cannontech.amr.meter.dao.GroupMetersDao;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;

public class OutageProcessingController extends MultiActionController {

	private OutageMonitorDao outageMonitorDao;
	private DeviceGroupEditorDao deviceGroupEditorDao;
	private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	private GroupMetersDao groupMetersDao;
	private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	private GroupMeterReadService groupMeterReadService;
	private AlertService alertService;
	
	// PROCESS
	public ModelAndView process(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("outageProcessing/process.jsp");
		
		int outageMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "outageMonitorId");
		OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		
		// read results
		List<GroupMeterReadResult> allReads = new ArrayList<GroupMeterReadResult>();
		allReads.addAll(groupMeterReadService.getPendingByType(CommandRequestExecutionType.OUTAGE_PROCESSING_OUTAGE_LOGS_READ));
		allReads.addAll(groupMeterReadService.getCompletedByType(CommandRequestExecutionType.OUTAGE_PROCESSING_OUTAGE_LOGS_READ));
		Collections.sort(allReads);
		
		mav.addObject("outageMonitor", outageMonitor);
		mav.addObject("outageGroupBase", SystemGroupEnum.OUTAGE_PROCESSING.getFullPath());
		mav.addObject("allReads", allReads);
		mav.addObject("reportStartDate", DateUtils.addMonths(new Date(), -1));
		
		return mav;
	}
	
	
	// READ OUTAGE LOGS
	public ModelAndView readOutageLogs(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("redirect:process");
		String processError = null;
		
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		int outageMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "outageMonitorId");
		final boolean removeFromOutageGroupAfterRead = ServletRequestUtils.getBooleanParameter(request, "removeFromOutageGroupAfterRead", false);
		final OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		
		try {
			
			String outageGroupName = SystemGroupEnum.OUTAGE_PROCESSING.getFullPath() + outageMonitor.getName();
			final StoredDeviceGroup outageGroup = deviceGroupEditorDao.getStoredGroup(outageGroupName, true);
			DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(outageGroup);
			
			// alert callback
			SimpleCallback<GroupMeterReadResult> alertCallback = new SimpleCallback<GroupMeterReadResult>() {
	            @Override
	            public void handle(GroupMeterReadResult result) {
	            	
	            	// remove success devices from outages group
	            	if (removeFromOutageGroupAfterRead) {
	            		
	            		DeviceCollection successCollection = result.getSuccessCollection();
	            		List<SimpleDevice> successDeviceList = successCollection.getDeviceList();
	            		deviceGroupMemberEditorDao.removeDevices(outageGroup, successDeviceList);
	            	}
	            	
	            	// alert
	                ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.web.modules.amr.outageProcessing.readOutagesLog.completionAlert");
	                resolvableTemplate.addData("outageMonitorName", outageMonitor.getName());
	                resolvableTemplate.addData("outageMonitorId", outageMonitor.getOutageMonitorId());
	                int successCount = result.getResultHolder().getResultStrings().size();
	                int failureCount = result.getResultHolder().getErrors().size();
	                int total = failureCount + successCount;
	                resolvableTemplate.addData("percentSuccess", (float)successCount *100 / total);
	                resolvableTemplate.addData("resultKey", result.getKey());
	                
	                OutageProcessingReadLogsCompletionAlert readLogsCompletionAlert = new OutageProcessingReadLogsCompletionAlert(new Date(), resolvableTemplate);
	                
	                alertService.add(readLogsCompletionAlert);
	            }
	        };
		
	        groupMeterReadService.readDeviceCollection(deviceCollection, Collections.singleton(BuiltInAttribute.OUTAGE_LOG), CommandRequestExecutionType.OUTAGE_PROCESSING_OUTAGE_LOGS_READ, alertCallback, userContext.getYukonUser());
		
		} catch (PaoAuthorizationException e) {
			processError = "User does not have access to run outage logs command.";
		}
		
		mav.addObject("outageMonitorId", outageMonitorId);
		mav.addObject("processError", processError);
		
		return mav;
	}
	
	
	
	
	// CLEAR OUTAGES GROUP
	public ModelAndView clearOutagesGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("redirect:process");
		
		int outageMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "outageMonitorId");
		
		OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		String outageGroupName = SystemGroupEnum.OUTAGE_PROCESSING.getFullPath() + outageMonitor.getName();
		StoredDeviceGroup outageGroup = deviceGroupEditorDao.getStoredGroup(outageGroupName, true);
		
		List<? extends YukonDevice> deviceList = groupMetersDao.getChildMetersByGroup(outageGroup);
        deviceGroupMemberEditorDao.removeDevices(outageGroup, deviceList);
        
		mav.addObject("outageMonitorId", outageMonitorId);
		
		return mav;
	}
	

	@Autowired
	public void setOutageMonitorDao(OutageMonitorDao outageMonitorDao) {
		this.outageMonitorDao = outageMonitorDao;
	}
	
	@Autowired
	public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
		this.deviceGroupEditorDao = deviceGroupEditorDao;
	}
	
	@Autowired
	public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
		this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
	}
	
	@Autowired
	public void setGroupMetersDao(GroupMetersDao groupMetersDao) {
		this.groupMetersDao = groupMetersDao;
	}
	
	@Autowired
	public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
		this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
	}
	
	@Autowired
	public void setGroupMeterReadService(GroupMeterReadService groupMeterReadService) {
		this.groupMeterReadService = groupMeterReadService;
	}
	
	@Autowired
	public void setAlertService(AlertService alertService) {
		this.alertService = alertService;
	}
}
