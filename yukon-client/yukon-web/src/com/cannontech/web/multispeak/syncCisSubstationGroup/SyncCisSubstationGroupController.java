package com.cannontech.web.multispeak.syncCisSubstationGroup;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;

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
	
	private Logger log = YukonLogManager.getLogger(SyncCisSubstationGroupController.class);
	
	@RequestMapping
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
        
        ModelAndView mav = new ModelAndView("setup/syncCisSubstationGroup/get.jsp");
        return mav;
    }
	
	@RequestMapping
    public ModelAndView getMeters(HttpServletRequest request, HttpServletResponse response) throws RemoteException, Exception {
        
        ModelAndView mav = new ModelAndView("setup/syncCisSubstationGroup/sync.jsp");
    	
    	// init results and stats
    	final List<MeterAndSubs> meterAndSubsList = new ArrayList<MeterAndSubs>();
    	final StoredDeviceGroup substationSystemGroup = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.SUBSTATION_NAME.getFullPath(), true);
    	final AtomicInteger totalAdded = new AtomicInteger(0);
    	final AtomicInteger totalRemoved = new AtomicInteger(0);
    	final AtomicInteger totalNoChange = new AtomicInteger(0);

    	// callback
    	// processes the list of msp meters as they are retrieved from the cis vendor
    	SimpleCallback<List<com.cannontech.multispeak.deploy.service.Meter>> callback = new SimpleCallback<List<com.cannontech.multispeak.deploy.service.Meter>>() {
    		
    		@Override
    		public void handle(List<com.cannontech.multispeak.deploy.service.Meter> mspMeters) throws Exception {
    			
    			// msp meter number map
    			log.debug("Handling msp meter list of size " + mspMeters.size());
    			ImmutableListMultimap<String,com.cannontech.multispeak.deploy.service.Meter> mspMeterMap = Multimaps.index(mspMeters, new Function<com.cannontech.multispeak.deploy.service.Meter, String>() {
    	    		@Override
    	    		public String apply(com.cannontech.multispeak.deploy.service.Meter device) {
    	    			return device.getMeterNo();
    	    		}
    	    	});
    			
    			// yukon meters
    			List<String> meterNumberList = new ArrayList<String>(mspMeterMap.keySet());
    	    	List<Meter> meters = meterDao.getMetersForMeterNumbers(meterNumberList);
    	    	log.debug("Found " + meters.size() + " yukon meters for msp meter list");
    	    	
    	    	for (Meter meter : meters) {
    	    		
    	    		String meterNumber = meter.getMeterNumber();
    	    		ImmutableList<com.cannontech.multispeak.deploy.service.Meter> mspMeterList = mspMeterMap.get(meterNumber);

    	    		for (com.cannontech.multispeak.deploy.service.Meter mspMeter : mspMeterList) {
        	    		
        	    		if (mspMeter.getUtilityInfo() != null) {
        	    		
        	    			String substationName = mspMeter.getUtilityInfo().getSubstationName();
        	    			
	        	    		if (!StringUtils.isBlank(substationName)) {
	        	    			
	        	    			String substationDeviceGroupName = SystemGroupEnum.SUBSTATION_NAME.getFullPath() + substationName;
	        	    			StoredDeviceGroup belongsIn = deviceGroupEditorDao.getStoredGroup(substationDeviceGroupName, true);
	        	    			
	        	    			// add
	        	    			boolean added = false;
	        	    			if (!deviceGroupMemberEditorDao.isChildDevice(belongsIn, meter)) {
	        	    				deviceGroupMemberEditorDao.addDevices(belongsIn, meter);
	        	    				added = true;
	        	    				totalAdded.incrementAndGet();
	        	    			} else {
	        	    				totalNoChange.incrementAndGet();
	        	    			}
	        	    			
	        	    			// remove
	        	        		Set<StoredDeviceGroup> removed = new HashSet<StoredDeviceGroup>();
	        	        		
	        	        		Set<StoredDeviceGroup> doesNotBelongIn = deviceGroupMemberEditorDao.getGroupMembership(substationSystemGroup, meter);
	        	        		doesNotBelongIn.remove(belongsIn);
	        	        		
	        	        		for (StoredDeviceGroup group : doesNotBelongIn) {
	        	        			if (deviceGroupMemberEditorDao.isChildDevice(group, meter)) {
	        	        				deviceGroupMemberEditorDao.removeDevices(group, meter);
	        	        				removed.add(group);
	        	        				totalRemoved.incrementAndGet();
	        	        			}
	        	        		}
	        	    			
	        	    			meterAndSubsList.add(new MeterAndSubs(meter, belongsIn, added, removed));
	        	    		}
        	    		}
    	    		}
    	    	}
    		}
    	};
    	
        int vendorId = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.MSP_PRIMARY_CB_VENDORID, null);
    	if (vendorId > 0) {
    		MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
    		mspObjectDao.getAllMspMeters(mspVendor, callback);
    	}
    	
    	
    	mav.addObject("substationGroupPrefix", SystemGroupEnum.SUBSTATION_NAME.getFullPath());
    	mav.addObject("meterAndSubsList", meterAndSubsList);
    	mav.addObject("totalAdded", totalAdded.get());
    	mav.addObject("totalRemoved", totalRemoved.get());
    	mav.addObject("totalNoChange", totalNoChange.get());
    	
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
