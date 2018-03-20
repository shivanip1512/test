package com.cannontech.web.amr.outageProcessing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.meter.dao.GroupMetersDao;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

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
	
	private final ListMultimap<Integer, String> monitorToRecentReadKeysCache = ArrayListMultimap.create();
	
	// PROCESS
/*	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView process(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("outageProcessing/process.jsp");
		
		int outageMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "outageMonitorId");
		OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		
		// read results
		List<GroupMeterReadResult> allReadsResults = new ArrayList<GroupMeterReadResult>();
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
		mav.addObject("readResults", readResults);
		
		mav.addObject("outageMonitor", outageMonitor);
		mav.addObject("outageGroupBase", deviceGroupService.getFullPath(SystemGroupEnum.OUTAGE));
		mav.addObject("reportStartDate", DateUtils.addMonths(new Date(), -1));
		
		return mav;
	}
	
	
	// READ OUTAGE LOGS
	public ModelAndView readOutageLogs(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("redirect:process");
		
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		int outageMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "outageMonitorId");
		final boolean removeFromOutageGroupAfterRead = ServletRequestUtils.getBooleanParameter(request, "removeFromOutageGroupAfterRead", false);
		final OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		
		final StoredDeviceGroup outageGroup = outageMonitorService.getOutageGroup(outageMonitor.getOutageMonitorName());
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
                resolvableTemplate.addData("outageMonitorName", outageMonitor.getOutageMonitorName());
                resolvableTemplate.addData("outageMonitorId", outageMonitor.getOutageMonitorId());
                int successCount = result.getResultHolder().getResultStrings().size();
                int total = result.getOriginalDeviceCollectionCopy().getDeviceCount();
                float percentSuccess = 100.0f;
                if (total > 0) {
                    percentSuccess = ((float) successCount * 100) / total;
                }
                resolvableTemplate.addData("percentSuccess", percentSuccess);
                resolvableTemplate.addData("resultKey", result.getKey());
                
                Alert readLogsCompletionAlert = new SimpleAlert(AlertType.OUTAGE_PROCESSING_READ_LOGS_COMPLETION, resolvableTemplate);
                
                alertService.add(readLogsCompletionAlert);
            }
        };
	
        String resultKey = deviceAttributeReadService.initiateRead(deviceCollection, Collections.singleton(BuiltInAttribute.OUTAGE_LOG), DeviceRequestType.GROUP_OUTAGE_PROCESSING_OUTAGE_LOGS_READ, alertCallback, userContext.getYukonUser());
        monitorToRecentReadKeysCache.put(outageMonitorId, resultKey);
		
		mav.addObject("outageMonitorId", outageMonitorId);
		mav.addObject("readOk", true);
		
		return mav;
	}
	
	
	
	
	// CLEAR OUTAGES GROUP
	public ModelAndView clearOutagesGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("redirect:process");
		
		int outageMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "outageMonitorId");
		OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		
		StoredDeviceGroup outageGroup = outageMonitorService.getOutageGroup(outageMonitor.getOutageMonitorName());
		
		List<? extends YukonDevice> deviceList = groupMetersDao.getChildMetersByGroup(outageGroup);
        deviceGroupMemberEditorDao.removeDevices(outageGroup, deviceList);
        
		mav.addObject("outageMonitorId", outageMonitorId);
		
		return mav;
	}*/
}
