package com.cannontech.web.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.amr.deviceread.service.GroupMeterReadService;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.model.DeviceCollectionFactory;

public class GroupMeterReadController extends MultiActionController {

	private GroupMeterReadService groupMeterReadService;
	private AlertService alertService;
	private DeviceGroupService deviceGroupService;
	private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	private DeviceCollectionFactory deviceCollectionFactory;
	
	// HOME (GROUP)
	public ModelAndView homeGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("groupMeterRead/groupMeterReadHomeGroup.jsp");
		
		String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg");
		mav.addObject("errorMsg", errorMsg);
		
		BuiltInAttribute[] allAttributes = BuiltInAttribute.values();
		mav.addObject("allAttributes", allAttributes);
		
		return mav;
	}
	
	// HOME (COLLECTION)
	public ModelAndView homeCollection(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("groupMeterRead/groupMeterReadHomeCollection.jsp");
		
		DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
		mav.addObject("deviceCollection", deviceCollection);
		
		String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg");
		mav.addObject("errorMsg", errorMsg);
		
		BuiltInAttribute[] allAttributes = BuiltInAttribute.values();
		mav.addObject("allAttributes", allAttributes);
		
		return mav;
	}
	
	// READ GROUP
	public ModelAndView readGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		String errorPage = "homeGroup";
		ModelAndView errorMav = new ModelAndView("redirect:" + errorPage);
		
		// device group
		String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
		if (StringUtils.isBlank(groupName)) {
			errorMav.addObject("errorMsg", "No Device Group Selected");
			return errorMav;
		}
		
		DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(groupName);
		DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(deviceGroup);
		
		return read(request, response, deviceCollection, errorPage);
	}
	
	// READ COLLECTION
	public ModelAndView readCollection(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		String errorPage = "homeCollection";
		
		DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
		
		return read(request, response, deviceCollection, errorPage);
	}
	
	// READ
	private ModelAndView read(HttpServletRequest request, HttpServletResponse response, DeviceCollection deviceCollection, String errorPage) throws ServletException {
		
		ModelAndView mav = new ModelAndView("redirect:resultDetail");
		ModelAndView errorMav = new ModelAndView("redirect:" + errorPage);
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		
		// attribute
		String attributeStr = ServletRequestUtils.getRequiredStringParameter(request, "attribute");
		BuiltInAttribute attribute = BuiltInAttribute.valueOf(attributeStr);
		
		
		// alert callback
		SimpleCallback<GroupMeterReadResult> alertCallback = new SimpleCallback<GroupMeterReadResult>() {
            @Override
            public void handle(GroupMeterReadResult result) {
            	
            	GroupMeterReadResultWrapper resultWrapper = new GroupMeterReadResultWrapper(result);
            	
            	// alert
                ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.groupMeterReadCompletion");
                resolvableTemplate.addData("attributesDescription", resultWrapper.getAttributesDescription());
                int successCount = result.getResultHolder().getResultStrings().size();
                int failureCount = result.getResultHolder().getErrors().size();
                int total = failureCount + successCount;
                resolvableTemplate.addData("percentSuccess", (float)successCount *100 / total);
                resolvableTemplate.addData("resultKey", result.getKey());
                
                GroupMeterReadCompletionAlert groupMeterReadCompletionAlert = new GroupMeterReadCompletionAlert(new Date(), resolvableTemplate);
                
                alertService.add(groupMeterReadCompletionAlert);
            }
        };
		
        // read
        try {
        
        	String resultKey = groupMeterReadService.readDeviceCollection(deviceCollection, Collections.singleton(attribute), CommandRequestExecutionType.GROUP_ATTRIBUTE_READ, alertCallback, userContext.getYukonUser());
        	mav.addObject("resultKey", resultKey);
		
        } catch (PaoAuthorizationException e) {
        	
        	errorMav.addObject("errorMsg", "User does not have access to read attribute: " + attribute.getDescription());
			return errorMav;
        }
		
        
		return mav;
	}
	
	// RESULT LIST
	public ModelAndView resultsList(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("groupMeterRead/groupMeterReadResults.jsp");
		
		// results
		List<GroupMeterReadResult> allReads = new ArrayList<GroupMeterReadResult>();
		allReads.addAll(groupMeterReadService.getPending());
		allReads.addAll(groupMeterReadService.getCompleted());
		Collections.sort(allReads);
		
		// result wrappers
		ObjectMapper<GroupMeterReadResult, GroupMeterReadResultWrapper> mapper = new ObjectMapper<GroupMeterReadResult, GroupMeterReadResultWrapper>() {
			@Override
			public GroupMeterReadResultWrapper map(GroupMeterReadResult from) throws ObjectMappingException {
				return new GroupMeterReadResultWrapper(from);
			}
		};
		List<GroupMeterReadResultWrapper> resultWrappers = new MappingList<GroupMeterReadResult, GroupMeterReadResultWrapper>(allReads, mapper);
		mav.addObject("resultWrappers", resultWrappers);
		
		return mav;
	}
	
	
	// RESULT DETAIL
	public ModelAndView resultDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("groupMeterRead/groupMeterReadResultDetail.jsp");
		
		String resultKey = ServletRequestUtils.getRequiredStringParameter(request, "resultKey");
		GroupMeterReadResult result = groupMeterReadService.getResult(resultKey);
		GroupMeterReadResultWrapper resultWrapper = new GroupMeterReadResultWrapper(result);
		
		mav.addObject("resultWrapper", resultWrapper);
		
		return mav;
	}
	
	// RESULT WRAPPER
	public class GroupMeterReadResultWrapper {
		
		private GroupMeterReadResult result;
		
		public GroupMeterReadResultWrapper(GroupMeterReadResult groupMeterReadResult) {
			this.result = groupMeterReadResult;
		}
		
		public GroupMeterReadResult getResult() {
			return this.result;
		}
		
		public String getAttributesDescription() {

			Set<? extends Attribute> attributes = this.result.getAttributes();
			List<String> descriptions = new ArrayList<String>();
			for (Attribute attribute : attributes) {
				descriptions.add(attribute.getDescription());
			}
			
			return StringUtils.join(descriptions, ", ");
		}
	}
	
	
	@Autowired
	public void setGroupMeterReadService(GroupMeterReadService groupMeterReadService) {
		this.groupMeterReadService = groupMeterReadService;
	}
	
	@Autowired
	public void setAlertService(AlertService alertService) {
		this.alertService = alertService;
	}
	
	@Autowired
	public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
		this.deviceGroupService = deviceGroupService;
	}
	
	@Autowired
	public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
		this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
	}
	
	@Resource(name="deviceCollectionFactory")
	public void setDeviceCollectionFactory(DeviceCollectionFactory deviceCollectionFactory) {
		this.deviceCollectionFactory = deviceCollectionFactory;
	}
}
