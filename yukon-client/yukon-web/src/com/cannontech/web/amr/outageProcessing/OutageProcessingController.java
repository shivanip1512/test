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

import com.cannontech.amr.deviceread.dao.PlcDeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.amr.meter.dao.GroupMetersDao;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.SimpleAlert;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

@CheckRoleProperty(YukonRoleProperty.OUTAGE_PROCESSING)
public class OutageProcessingController extends MultiActionController {

	private OutageMonitorDao outageMonitorDao;
	private OutageMonitorService outageMonitorService;
	private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	private GroupMetersDao groupMetersDao;
	private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	private PlcDeviceAttributeReadService plcDeviceAttributeReadService;
	private AlertService alertService;
	
	private ListMultimap<Integer, String> monitorToRecentReadKeysCache = ArrayListMultimap.create();
	
	// PROCESS
	public ModelAndView process(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("outageProcessing/process.jsp");
		
		int outageMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "outageMonitorId");
		OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
		
		// read results
		List<GroupMeterReadResult> allReadsResults = new ArrayList<GroupMeterReadResult>();
		allReadsResults.addAll(plcDeviceAttributeReadService.getPendingByType(DeviceRequestType.GROUP_OUTAGE_PROCESSING_OUTAGE_LOGS_READ));
		allReadsResults.addAll(plcDeviceAttributeReadService.getCompletedByType(DeviceRequestType.GROUP_OUTAGE_PROCESSING_OUTAGE_LOGS_READ));
		
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
                ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.web.modules.amr.outageDetail.readOutagesLog.completionAlert");
                resolvableTemplate.addData("outageMonitorName", outageMonitor.getOutageMonitorName());
                resolvableTemplate.addData("outageMonitorId", outageMonitor.getOutageMonitorId());
                int successCount = result.getResultHolder().getResultStrings().size();
                int total = (int)result.getOriginalDeviceCollectionCopy().getDeviceCount();
                float percentSuccess = 100.0f;
                if (total > 0) {
                	percentSuccess = (float)((successCount * 100) / total);
                }
                resolvableTemplate.addData("percentSuccess", percentSuccess);
                resolvableTemplate.addData("resultKey", result.getKey());
                
                Alert readLogsCompletionAlert = new SimpleAlert(AlertType.OUTAGE_PROCESSING_READ_LOGS_COMPLETION, resolvableTemplate);
                
                alertService.add(readLogsCompletionAlert);
            }
        };
	
        String resultKey = plcDeviceAttributeReadService.readDeviceCollection(deviceCollection, Collections.singleton(BuiltInAttribute.OUTAGE_LOG), DeviceRequestType.GROUP_OUTAGE_PROCESSING_OUTAGE_LOGS_READ, alertCallback, userContext.getYukonUser());
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
	public void setPlcDeviceAttributeReadService(PlcDeviceAttributeReadService plcDeviceAttributeReadService) {
		this.plcDeviceAttributeReadService = plcDeviceAttributeReadService;
	}
	
	@Autowired
	public void setAlertService(AlertService alertService) {
		this.alertService = alertService;
	}
}
