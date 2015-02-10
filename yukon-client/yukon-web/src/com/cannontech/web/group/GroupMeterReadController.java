package com.cannontech.web.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.BaseAlert;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.ResultResultExpiredException;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.util.AttributeSelectorHelperService;
import com.google.common.collect.Maps;

@RequestMapping("/groupMeterRead/*")
@Controller
public class GroupMeterReadController {

	@Autowired private AlertService alertService;
	@Autowired private DeviceGroupService deviceGroupService;
	@Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	@Autowired private AttributeSelectorHelperService attributeSelectorHelperService;
	@Autowired private AttributeService attributeService;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    private DeviceCollectionFactory deviceCollectionFactory;

	@RequestMapping("homeGroup")
	public ModelAndView homeGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
	    YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		ModelAndView mav = new ModelAndView("groupMeterRead/groupMeterReadHomeGroup.jsp");
		
		String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg");
		String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
		Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
		
		mav.addObject("errorMsg", errorMsg);
		mav.addObject("groupName", groupName);
		mav.addObject("selectedAttributes", selectedAttributes);
		
		Set<Attribute> allReadableAttributes = attributeService.getReadableAttributes();
	    Map<AttributeGroup, List<BuiltInAttribute>> allGroupedReadableAttributes = attributeService.
	            getGroupedAttributeMapFromCollection(allReadableAttributes, userContext);
		mav.addObject("allGroupedReadableAttributes", allGroupedReadableAttributes);
		
		return mav;
	}
	
	@RequestMapping("homeCollection")
	public ModelAndView homeCollection(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
	    YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		ModelAndView mav = new ModelAndView("groupMeterRead/groupMeterReadHomeCollection.jsp");
		
		DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
		mav.addObject("deviceCollection", deviceCollection);
		mav.addAllObjects(deviceCollection.getCollectionParameters());
		
		String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg");
		Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
		
		mav.addObject("errorMsg", errorMsg);
		mav.addObject("selectedAttributes", selectedAttributes);
		
		Set<Attribute> allReadableAttributes = attributeService.getReadableAttributes();
		Map<AttributeGroup, List<BuiltInAttribute>> allGroupedReadableAttributes = attributeService.
                getGroupedAttributeMapFromCollection(allReadableAttributes, userContext);
		mav.addObject("allGroupedReadableAttributes", allGroupedReadableAttributes);
		
		return mav;
	}
	
	@RequestMapping("readGroup")
	public ModelAndView readGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		String errorPage = "homeGroup";
		ModelAndView errorMav = new ModelAndView("redirect:" + errorPage);
		
		// device group
		String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
		if (StringUtils.isBlank(groupName)) {
		    addErrorStateToMav(errorMav, "No Device Group Selected", null, makeSelectedAttributeStrsParameter(attributeSelectorHelperService.getAttributeSet(request, null, null)));
		    return errorMav;
		}
		
		DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(groupName);
		DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(deviceGroup);
		
		return read(request, response, deviceCollection, errorPage, groupName);
	}
	
	@RequestMapping("readCollection")
	public ModelAndView readCollection(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
	    DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
	    
		String errorPage = "homeCollection";
		ModelAndView errorMav = new ModelAndView("redirect:" + errorPage);
		
		// attributes
		Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
        if (selectedAttributes.size() == 0) {
            addErrorStateToMav(errorMav, "No Attribute Selected", null, makeSelectedAttributeStrsParameter(attributeSelectorHelperService.getAttributeSet(request, null, null)));
            errorMav.addAllObjects(deviceCollection.getCollectionParameters());
            return errorMav;
        }
		
		return read(request, response, deviceCollection, errorPage, null);
	}
	
	// READ
	private ModelAndView read(HttpServletRequest request, HttpServletResponse response, DeviceCollection deviceCollection, String errorPage, String groupName) throws ServletException {
		
		ModelAndView mav = new ModelAndView("redirect:resultDetail");
		ModelAndView errorMav = new ModelAndView("redirect:" + errorPage);
		final YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		
		// attribute
		Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
		if (selectedAttributes.size() == 0) {
			
			addErrorStateToMav(errorMav, "No Attribute Selected", groupName, null);
			errorMav.addObject("deviceCollection", deviceCollection);
			errorMav.addAllObjects(deviceCollection.getCollectionParameters());
			return errorMav;
		}
		
		
		// alert callback
		SimpleCallback<GroupMeterReadResult> alertCallback = new SimpleCallback<GroupMeterReadResult>() {
            @Override
            public void handle(GroupMeterReadResult result) {
            	
            	GroupMeterReadResultWrapper resultWrapper = new GroupMeterReadResultWrapper(result);
            	
            	// alert
            	ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.groupMeterReadCompletion");
            	if (result.isExceptionOccured()) {
                	resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.commandCompletion.failed");
            	}
                	
            	int successCount = result.getResultHolder().getResultStrings().size();
            	int failureCount = result.getResultHolder().getErrors().size();
            	int completedCount = failureCount + successCount;
                
                resolvableTemplate.addData("completedCount", completedCount);
                resolvableTemplate.addData("percentSuccess", completedCount > 0 ? ((float) successCount * 100 / completedCount) : 0);
                resolvableTemplate.addData("attributesDescription", resultWrapper.getAttributesDescription(userContext, objectFormattingService));
                resolvableTemplate.addData("resultKey", result.getKey());
                
                if (result.isExceptionOccured()) {
                	
                	int deviceCount = result.getDeviceCollection().getDeviceCount();
                	int notCompletedCount = deviceCount - completedCount;
                	String exceptionReason = result.getExceptionReason();
                	
                	resolvableTemplate.addData("notCompletedCount", notCompletedCount);
                	resolvableTemplate.addData("exceptionReason", exceptionReason);
                }
                
                Alert groupMeterReadCompletionAlert = new BaseAlert(new Date(), resolvableTemplate) {
                    @Override
                    public com.cannontech.common.alert.model.AlertType getType() {
                        return AlertType.GROUP_METER_READ_COMPLETION;
                    };
                };
                
                alertService.add(groupMeterReadCompletionAlert);
            }
        };
		
        // read
        try {
            String resultKey =  deviceAttributeReadService.initiateRead(deviceCollection,
                                          selectedAttributes,
                                          DeviceRequestType.GROUP_ATTRIBUTE_READ,
                                          alertCallback,
                                          userContext.getYukonUser());
        	mav.addObject("resultKey", resultKey);
		
        } catch (Exception e) {
            String error = "Could not initiate read of attribute(s): " + e.getMessage();
        	addErrorStateToMav(errorMav, error, groupName, makeSelectedAttributeStrsParameter(selectedAttributes));
        	errorMav.addObject("deviceCollection", deviceCollection);
            errorMav.addAllObjects(deviceCollection.getCollectionParameters());
			return errorMav;
        }
		
		return mav;
	}
	
	private void addErrorStateToMav(ModelAndView mav, String errorMsg, String groupName, String selectedAttributeStrs) {
		
		mav.addObject("errorMsg", errorMsg);
		mav.addObject("groupName", groupName);
		mav.addObject("selectedAttributeStrs", selectedAttributeStrs);
	}
	
	@RequestMapping("resultsList")
	public ModelAndView resultsList(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("groupMeterRead/groupMeterReadResults.jsp");
		
		// results
		List<GroupMeterReadResult> allReads = new ArrayList<GroupMeterReadResult>();
		allReads.addAll(deviceAttributeReadService.getPending());
		allReads.addAll(deviceAttributeReadService.getCompleted());
		Collections.sort(allReads);
		
		// result wrappers
		ObjectMapper<GroupMeterReadResult, GroupMeterReadResultWrapper> mapper = new ObjectMapper<GroupMeterReadResult, GroupMeterReadResultWrapper>() {
			@Override
			public GroupMeterReadResultWrapper map(GroupMeterReadResult from) throws ObjectMappingException {
				return new GroupMeterReadResultWrapper(from);
			}
		};
		List<GroupMeterReadResultWrapper> resultWrappers = new MappingList<GroupMeterReadResult, GroupMeterReadResultWrapper>(allReads, mapper);

		YukonUserContext context = YukonUserContextUtils.getYukonUserContext(request);
		Map<GroupMeterReadResultWrapper,String> attributeDescriptions = Maps.newHashMap();

		for (GroupMeterReadResultWrapper wrapper : resultWrappers) {
		    attributeDescriptions.put(wrapper, wrapper.getAttributesDescription(context, objectFormattingService));
		}

		mav.addObject("resultWrappers",attributeDescriptions);
		
		return mav;
	}
	
	
	@RequestMapping("resultDetail")
	public ModelAndView resultDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("groupMeterRead/groupMeterReadResultDetail.jsp");
		
		String resultKey = ServletRequestUtils.getRequiredStringParameter(request, "resultKey");
		GroupMeterReadResult result = deviceAttributeReadService.getResult(resultKey);

		// friendly exception
		if (result == null) {
			throw new ResultResultExpiredException("Group Meter Result No Longer Exists");
		}
		
		GroupMeterReadResultWrapper resultWrapper = new GroupMeterReadResultWrapper(result);
		mav.addObject("attributesDescription", resultWrapper.getAttributesDescription(YukonUserContextUtils.getYukonUserContext(request), objectFormattingService));
		mav.addObject("resultWrapper", resultWrapper);
		return mav;
	}
	
	@RequestMapping("errorsList")
    public ModelAndView errorsList(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		String resultKey = ServletRequestUtils.getRequiredStringParameter(request, "resultKey");
		GroupMeterReadResult result = deviceAttributeReadService.getResult(resultKey);
		
		ModelAndView mav = new ModelAndView("commander/errorsList.jsp");
		mav.addObject("definitionName", "groupMeterReadFailureResultDefinition");
		mav.addObject("result", result);
		return mav;
    }
	
	@RequestMapping("successList")
	public ModelAndView successList(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		String resultKey = ServletRequestUtils.getRequiredStringParameter(request, "resultKey");
		GroupMeterReadResult result = deviceAttributeReadService.getResult(resultKey);
		
		ModelAndView mav = new ModelAndView("commander/successList.jsp");
		mav.addObject("definitionName", "groupMeterReadSuccessResultDefinition");
		mav.addObject("result", result);
		return mav;
    }
	
    private String makeSelectedAttributeStrsParameter(Set<Attribute> attributeParameters) {
        return StringUtils.join(attributeParameters, ",");
    }
    
    @Resource(name="deviceCollectionFactory")
    public void setDeviceCollectionFactory(DeviceCollectionFactory deviceCollectionFactory) {
        this.deviceCollectionFactory = deviceCollectionFactory;
    }
}
