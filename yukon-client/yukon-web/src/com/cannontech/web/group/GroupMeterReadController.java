package com.cannontech.web.group;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
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
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    
    private final static String baseKey = "yukon.web.modules.tools.bulk.readAttribute.";
	
    @RequestMapping(value = "homeCollection", method = RequestMethod.GET)
	public String homeCollection(ModelMap model, YukonUserContext userContext, HttpServletRequest request) throws ServletException {
		setupModel(model, userContext, request);
		model.addAttribute("action", CollectionAction.READ_ATTRIBUTE);
		model.addAttribute("actionInputs", "/WEB-INF/pages/group/groupMeterRead/groupMeterReadHomeCollection.jsp");
		return "../collectionActions/collectionActionsHome.jsp";
	}
    
    @RequestMapping(value = "readAttributeInputs", method = RequestMethod.GET)
    public String readAttributeInputs(ModelMap model, YukonUserContext userContext, HttpServletRequest request) throws ServletException {
        setupModel(model, userContext, request);
        return "groupMeterRead/groupMeterReadHomeCollection.jsp";
    }
    
    private void setupModel(ModelMap model, YukonUserContext userContext, HttpServletRequest request) throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAllAttributes(deviceCollection.getCollectionParameters());
        
        Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
        model.addAttribute("selectedAttributes", selectedAttributes);
        
        Set<Attribute> allReadableAttributes = attributeService.getReadableAttributes();
        Map<AttributeGroup, List<BuiltInAttribute>> allGroupedReadableAttributes = attributeService.
                getGroupedAttributeMapFromCollection(allReadableAttributes, userContext);
        model.addAttribute("allGroupedReadableAttributes", allGroupedReadableAttributes);
    }
	
	@RequestMapping(value = "readCollection", method = RequestMethod.POST)
	public String readCollection(ModelMap model, HttpServletRequest request, YukonUserContext userContext, HttpServletResponse resp) throws ServletException {
	    DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);

        Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
        if (selectedAttributes.size() == 0) {
            model.addAttribute("errorMsg", messageSourceAccessor.getMessage(baseKey + "noAttributesSelected"));
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            setupModel(model, userContext, request);
            return "groupMeterRead/groupMeterReadHomeCollection.jsp";
        }
        
        SimpleCallback<CollectionActionResult> alertCallback = CollectionActionAlertHelper.createAlert(AlertType.GROUP_METER_READ_COMPLETION, alertService,
                    messageSourceAccessor, request);

        int resultKey =  deviceAttributeReadService.initiateRead(deviceCollection, selectedAttributes,
                                      DeviceRequestType.GROUP_ATTRIBUTE_READ, alertCallback, userContext);
        return "redirect:/collectionActions/progressReport/detail?key=" + resultKey;
	}

}
