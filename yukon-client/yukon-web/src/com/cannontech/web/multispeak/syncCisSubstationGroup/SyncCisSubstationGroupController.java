package com.cannontech.web.multispeak.syncCisSubstationGroup;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP)
@RequestMapping("/setup/syncCisSubstationGroup/*")
public class SyncCisSubstationGroupController {

	private RolePropertyDao rolePropertyDao;
	private MspObjectDao mspObjectDao;
	private MultispeakFuncs multispeakFuncs;
	private MultispeakDao multispeakDao;
	private MeterDao meterDao;
	private DeviceGroupEditorDao deviceGroupEditorDao;
	private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	
	@RequestMapping
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
        
        ModelAndView mav = new ModelAndView("setup/syncCisSubstationGroup/get.jsp");
        
        return mav;
    }
	
	@RequestMapping
    public ModelAndView getMeters(HttpServletRequest request, HttpServletResponse response) throws RemoteException {
        
        ModelAndView mav = new ModelAndView("setup/syncCisSubstationGroup/sync.jsp");
        
        // get all msp meters
        List<com.cannontech.multispeak.deploy.service.Meter> allMeters = new ArrayList<com.cannontech.multispeak.deploy.service.Meter>();
        int vendorId = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.MSP_PRIMARY_CB_VENDORID, null);
    	if (vendorId > 0) {
    		MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
    		allMeters = mspObjectDao.getAllMspMeters(mspVendor);
    	}
    	
    	// meter number to msp meter map
    	ImmutableMap<String, com.cannontech.multispeak.deploy.service.Meter> mspMeterMap = Maps.uniqueIndex(allMeters, new Function<com.cannontech.multispeak.deploy.service.Meter, String>() {
    		@Override
    		public String apply(com.cannontech.multispeak.deploy.service.Meter device) {
    			return device.getMeterNo();
    		}
    	});
    	
    	// meter number to yukon meter map
    	List<String> meterNumberList = new ArrayList<String>(mspMeterMap.keySet());
    	List<Meter> meters = meterDao.getMetersForMeterNumbers(meterNumberList);
    	
    	ImmutableMap<String, Meter> yukonMeterMap = Maps.uniqueIndex(meters, new Function<Meter, String>() {
    		@Override
    		public String apply(Meter device) {
    			return device.getMeterNumber();
    		}
    	});
    	
    	// init stats
    	int totalAdded = 0;
    	int totalRemoved = 0;
    	int totalNoChange = 0;
    	
    	// build meterAndSubs list
    	StoredDeviceGroup substationSystemGroup = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.SUBSTATION_NAME.getFullPath(), true);
    	
    	List<MeterAndSubs> meterAndSubsList = new ArrayList<MeterAndSubs>();
    	for (Meter meter : yukonMeterMap.values()) {
    		
    		// get substation name from msp meter
    		String meterNumber = meter.getMeterNumber();
    		com.cannontech.multispeak.deploy.service.Meter mspMeter = mspMeterMap.get(meterNumber);
    		String substationName = mspMeter.getUtilityInfo().getSubstationName();
    		
    		if (!StringUtils.isBlank(substationName)) {
    			
    			String substationDeviceGroupName = SystemGroupEnum.SUBSTATION_NAME.getFullPath() + substationName;
    			StoredDeviceGroup belongsIn = deviceGroupEditorDao.getStoredGroup(substationDeviceGroupName, true);
    			
    			// add
    			boolean added = false;
    			if (!deviceGroupMemberEditorDao.isChildDevice(belongsIn, meter)) {
    				deviceGroupMemberEditorDao.addDevices(belongsIn, meter);
    				added = true;
    				totalAdded++;
    			} else {
    				totalNoChange++;
    			}
    			
    			// remove
        		Set<StoredDeviceGroup> removed = new HashSet<StoredDeviceGroup>();
        		
        		Set<StoredDeviceGroup> doesNotBelongIn = deviceGroupMemberEditorDao.getGroupMembership(substationSystemGroup, meter);
        		doesNotBelongIn.remove(belongsIn);
        		
        		for (StoredDeviceGroup group : doesNotBelongIn) {
        			if (deviceGroupMemberEditorDao.isChildDevice(group, meter)) {
        				deviceGroupMemberEditorDao.removeDevices(group, meter);
        				removed.add(group);
        				totalRemoved++;
        			}
        		}
    			
    			meterAndSubsList.add(new MeterAndSubs(meter, belongsIn, added, removed));
    		}
    	}
    	
    	mav.addObject("substationGroupPrefix", SystemGroupEnum.SUBSTATION_NAME.getFullPath());
    	mav.addObject("meterAndSubsList", meterAndSubsList);
    	mav.addObject("totalAdded", totalAdded);
    	mav.addObject("totalRemoved", totalRemoved);
    	mav.addObject("totalNoChange", totalNoChange);
    	
        return mav;
    }
	
	public class MeterAndSubs {
		
		private Meter meter;
		private StoredDeviceGroup belongsIn;
		private boolean added;
		private Set<StoredDeviceGroup> removed;
		
		public MeterAndSubs(Meter meter, StoredDeviceGroup belongsIn, boolean added, Set<StoredDeviceGroup> removed) {
			this.meter = meter;
			this.belongsIn = belongsIn;
			this.added = added;
			this.removed = removed;
		}
		
		public Meter getMeter() {
			return meter;
		}
		public StoredDeviceGroup getBelongsIn() {
			return belongsIn;
		}
		public boolean isAdded() {
			return added;
		}
		public Set<StoredDeviceGroup> getRemoved() {
			return removed;
		}
	}
	
	
	@Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
    
    @Autowired
    public void setMspObjectDao(MspObjectDao mspObjectDao) {
		this.mspObjectDao = mspObjectDao;
	}
    
    @Autowired
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
		this.multispeakFuncs = multispeakFuncs;
	}
    
    @Autowired
    public void setMultispeakDao(MultispeakDao multispeakDao) {
		this.multispeakDao = multispeakDao;
	}
    
    @Autowired
    public void setMeterDao(MeterDao meterDao) {
		this.meterDao = meterDao;
	}
    
    @Autowired
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
		this.deviceGroupEditorDao = deviceGroupEditorDao;
	}
    
    @Autowired
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
		this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
	}
}
