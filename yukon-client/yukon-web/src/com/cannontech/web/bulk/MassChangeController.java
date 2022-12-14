package com.cannontech.web.bulk;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldService;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
@Controller
@RequestMapping("massChange/*")
public class MassChangeController {

    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private BulkFieldService bulkFieldService;
    @Resource(name="recentResultsCache") private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    
    /**
     * SELECT MASS CHANGE TYPE
     * @throws DeviceCollectionCreationException 
     * @throws ServletRequestBindingException 
     */
    @RequestMapping(value = "massChangeSelect", method = RequestMethod.GET)
    public String massChangeSelect(ModelMap model, HttpServletRequest request) throws ServletException {
        setupModel(model, request);
        model.addAttribute("action", CollectionAction.MASS_CHANGE);
        model.addAttribute("actionInputs", "/WEB-INF/pages/bulk/massChange/massChangeSelect.jsp");
        return "../collectionActions/collectionActionsHome.jsp";
    }
    
    @RequestMapping(value = "massChangeSelectInputs", method = RequestMethod.GET)
    public String massChangeSelectInputs(ModelMap model, HttpServletRequest request) throws ServletException {
        setupModel(model, request);
        return "massChange/massChangeSelect.jsp";
    }
    
    private void setupModel(ModelMap model, HttpServletRequest request) throws ServletException {
        // pass along deviceCollection
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        // available mass change operations
        List<BulkField<?, ?>> massChangableBulkFields = bulkFieldService.getMassChangableBulkFields();
        model.addAttribute("massChangableBulkFields", massChangableBulkFields);
        
        // pass previously selected bulk field name through
        model.addAttribute("selectedBulkFieldName", ServletRequestUtils.getStringParameter(request, "selectedBulkFieldName", ""));
    }

}