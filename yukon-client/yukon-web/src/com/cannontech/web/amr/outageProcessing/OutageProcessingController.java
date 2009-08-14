package com.cannontech.web.amr.outageProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.amr.deviceread.service.GroupMeterReadService;
import com.cannontech.amr.meter.dao.GroupMetersDao;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
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

public class OutageProcessingController extends MultiActionController implements InitializingBean {

	private OutageMonitorDao outageMonitorDao;
	private OutageMonitorService outageMonitorService;
	private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	private GroupMetersDao groupMetersDao;
	private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	private GroupMeterReadService groupMeterReadService;
	private AlertService alertService;
	
	private Map<Integer, List<String>> monitorToRecentReadKeysCache;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		monitorToRecentReadKeysCache = new HashMap<Integer, List<String>>();
	}
	
	// PROCESS
	public ModelAndView process(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("outageProcessing/process.jsp");
		
		int outageMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "outageMonitorId");
		OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		
		String processError = ServletRequestUtils.getStringParameter(request, "processError");
		Boolean readOk = ServletRequestUtils.getBooleanParameter(request, "readOk");
		mav.addObject("processError", processError);
		mav.addObject("readOk", readOk);
		
		// read results
		List<GroupMeterReadResult> allReadsResults = new ArrayList<GroupMeterReadResult>();
		allReadsResults.addAll(groupMeterReadService.getPendingByType(CommandRequestExecutionType.OUTAGE_PROCESSING_OUTAGE_LOGS_READ));
		allReadsResults.addAll(groupMeterReadService.getCompletedByType(CommandRequestExecutionType.OUTAGE_PROCESSING_OUTAGE_LOGS_READ));
		
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
		mav.addObject("readResults", readResults);
		
		mav.addObject("outageMonitor", outageMonitor);
		mav.addObject("outageGroupBase", SystemGroupEnum.OUTAGE_PROCESSING.getFullPath());
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
			
			final StoredDeviceGroup outageGroup = outageMonitorService.getOutageGroup(outageMonitor.getName());
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
	                int total = (int)result.getOriginalDeviceCollectionCopy().getDeviceCount();
	                resolvableTemplate.addData("percentSuccess", (float)((successCount * 100) / total));
	                resolvableTemplate.addData("resultKey", result.getKey());
	                
	                OutageProcessingReadLogsCompletionAlert readLogsCompletionAlert = new OutageProcessingReadLogsCompletionAlert(new Date(), resolvableTemplate);
	                
	                alertService.add(readLogsCompletionAlert);
	            }
	        };
		
	        String resultKey = groupMeterReadService.readDeviceCollection(deviceCollection, Collections.singleton(BuiltInAttribute.OUTAGE_LOG), CommandRequestExecutionType.OUTAGE_PROCESSING_OUTAGE_LOGS_READ, alertCallback, userContext.getYukonUser());
	        addReadResultKeyToCache(outageMonitorId, resultKey);
		
		} catch (PaoAuthorizationException e) {
			processError = "User does not have access to run outage logs command.";
		} catch (Exception e) {
			processError = e.getMessage();
		}
		
		mav.addObject("outageMonitorId", outageMonitorId);
		mav.addObject("processError", processError);
		mav.addObject("readOk", processError == null);
		
		return mav;
	}
	
	
	
	
	// CLEAR OUTAGES GROUP
	public ModelAndView clearOutagesGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("redirect:process");
		
		int outageMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "outageMonitorId");
		OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		
		StoredDeviceGroup outageGroup = outageMonitorService.getOutageGroup(outageMonitor.getName());
		
		List<? extends YukonDevice> deviceList = groupMetersDao.getChildMetersByGroup(outageGroup);
        deviceGroupMemberEditorDao.removeDevices(outageGroup, deviceList);
        
		mav.addObject("outageMonitorId", outageMonitorId);
		
		return mav;
	}
	
	
	private void addReadResultKeyToCache(int outageMonitorId, String resultKey) {
		
		if (!monitorToRecentReadKeysCache.containsKey(outageMonitorId)) {
			monitorToRecentReadKeysCache.put(outageMonitorId, new ArrayList<String>());
		}
		monitorToRecentReadKeysCache.get(outageMonitorId).add(resultKey);
	}

	@Autowired
	public void setOutageMonitorDao(OutageMonitorDao outageMonitorDao) {
		this.outageMonitorDao = outageMonitorDao;
	}
	
	@Autowired
	public void setOutageMonitorService(OutageMonitorService outageMonitorService) {
		this.outageMonitorService = outageMonitorService;
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
