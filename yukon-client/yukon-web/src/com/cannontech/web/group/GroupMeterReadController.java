package com.cannontech.web.group;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.CollectionActionAlertHelper;
import com.cannontech.web.util.AttributeSelectorHelperService;

@RequestMapping("/groupMeterRead/*")
@Controller
public class GroupMeterReadController {

	@Autowired private AlertService alertService;
	@Autowired private AttributeSelectorHelperService attributeSelectorHelperService;
	@Autowired private AttributeService attributeService;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private DeviceCollectionFactory deviceCollectionFactory;
	
    @RequestMapping(value = "homeCollection", method = RequestMethod.GET)
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
	
	@RequestMapping("readCollection")
	public String readCollection(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
	    DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
	    
		String errorPage = "homeCollection";
		ModelAndView errorMav = new ModelAndView("redirect:" + errorPage);
		
		// attributes
		Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
        if (selectedAttributes.size() == 0) {
            addErrorStateToMav(errorMav, "No Attribute Selected", null, makeSelectedAttributeStrsParameter(attributeSelectorHelperService.getAttributeSet(request, null, null)));
            errorMav.addAllObjects(deviceCollection.getCollectionParameters());
            return "";
        }
		
		return read(request, response, deviceCollection, errorPage, null);
	}
	
	// READ
	private String read(HttpServletRequest request, HttpServletResponse response, DeviceCollection deviceCollection, String errorPage, String groupName) throws ServletException {
		
	    int resultKey = 0;
	    
		ModelAndView mav = new ModelAndView("redirect:resultDetail");
		ModelAndView errorMav = new ModelAndView("redirect:" + errorPage);
		final YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		
		// attribute
		Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
		if (selectedAttributes.size() == 0) {
			
			addErrorStateToMav(errorMav, "No Attribute Selected", groupName, null);
			errorMav.addObject("deviceCollection", deviceCollection);
			errorMav.addAllObjects(deviceCollection.getCollectionParameters());
			return "";
		}
		
        SimpleCallback<CollectionActionResult> alertCallback =
                CollectionActionAlertHelper.createAlert(AlertType.GROUP_METER_READ_COMPLETION, alertService,
                    messageResolver.getMessageSourceAccessor(userContext), request);
        
		
        // read
        try {
            resultKey =  deviceAttributeReadService.initiateRead(deviceCollection,
                                          selectedAttributes,
                                          DeviceRequestType.GROUP_ATTRIBUTE_READ,
                                          alertCallback,
                                          userContext);
        	mav.addObject("resultKey", resultKey);
		
        } catch (Exception e) {
            String error = "Could not initiate read of attribute(s): " + e.getMessage();
        	addErrorStateToMav(errorMav, error, groupName, makeSelectedAttributeStrsParameter(selectedAttributes));
        	errorMav.addObject("deviceCollection", deviceCollection);
            errorMav.addAllObjects(deviceCollection.getCollectionParameters());
        }
		
        return "redirect:/bulk/progressReport/detail?key=" + resultKey;
	}
	
	private void addErrorStateToMav(ModelAndView mav, String errorMsg, String groupName, String selectedAttributeStrs) {
		
		mav.addObject("errorMsg", errorMsg);
		mav.addObject("groupName", groupName);
		mav.addObject("selectedAttributeStrs", selectedAttributeStrs);
	}
	
	@RequestMapping("resultsList")
	public ModelAndView resultsList(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		/*ModelAndView mav = new ModelAndView("groupMeterRead/groupMeterReadResults.jsp");
		
		// results
		List<GroupMeterReadResult> allReads = new ArrayList<>();
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
		List<GroupMeterReadResultWrapper> resultWrappers = new MappingList<>(allReads, mapper);

		YukonUserContext context = YukonUserContextUtils.getYukonUserContext(request);
		Map<GroupMeterReadResultWrapper,String> attributeDescriptions = Maps.newHashMap();

		for (GroupMeterReadResultWrapper wrapper : resultWrappers) {
		    attributeDescriptions.put(wrapper, wrapper.getAttributesDescription(context, objectFormattingService));
		}

		mav.addObject("resultWrappers",attributeDescriptions);*/
		
		return null;
	}
	
	
	@RequestMapping("resultDetail")
	public ModelAndView resultDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		/*ModelAndView mav = new ModelAndView("groupMeterRead/groupMeterReadResultDetail.jsp");
		
		String resultKey = ServletRequestUtils.getRequiredStringParameter(request, "resultKey");
		GroupMeterReadResult result = deviceAttributeReadService.getResult(resultKey);

		// friendly exception
		if (result == null) {
			throw new ResultExpiredException("Group Meter Result No Longer Exists");
		}
		
		GroupMeterReadResultWrapper resultWrapper = new GroupMeterReadResultWrapper(result);
		mav.addObject("attributesDescription", resultWrapper.getAttributesDescription(YukonUserContextUtils.getYukonUserContext(request), objectFormattingService));
		mav.addObject("resultWrapper", resultWrapper);*/
		return null;
	}
	
	@RequestMapping("errorsList")
    public ModelAndView errorsList(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		/*String resultKey = ServletRequestUtils.getRequiredStringParameter(request, "resultKey");
		GroupMeterReadResult result = deviceAttributeReadService.getResult(resultKey);
		
		ModelAndView mav = new ModelAndView("commander/errorsList.jsp");
		mav.addObject("definitionName", "groupMeterReadFailureResultDefinition");
		mav.addObject("result", result);*/
		return null;
    }
	
	@RequestMapping("successList")
	public ModelAndView successList(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		/*String resultKey = ServletRequestUtils.getRequiredStringParameter(request, "resultKey");
		GroupMeterReadResult result = deviceAttributeReadService.getResult(resultKey);
		
		ModelAndView mav = new ModelAndView("commander/successList.jsp");
		mav.addObject("definitionName", "groupMeterReadSuccessResultDefinition");
		mav.addObject("result", result);*/
		return null;
    }
	
    private String makeSelectedAttributeStrsParameter(Set<Attribute> attributeParameters) {
        return StringUtils.join(attributeParameters, ",");
    }
    
    @Resource(name="deviceCollectionFactory")
    public void setDeviceCollectionFactory(DeviceCollectionFactory deviceCollectionFactory) {
        this.deviceCollectionFactory = deviceCollectionFactory;
    }
}
