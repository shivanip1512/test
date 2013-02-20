package com.cannontech.web.bulk;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.MassChangeCallbackResult;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
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
    @RequestMapping
    public String massChangeSelect(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException, DeviceCollectionCreationException {
        
        // pass along deviceCollection
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        // available mass change operations
        List<BulkField<?, ?>> massChangableBulkFields = bulkFieldService.getMassChangableBulkFields();
        model.addAttribute("massChangableBulkFields", massChangableBulkFields);
        
        // pass previously selected bulk field name through
        model.addAttribute("selectedBulkFieldName", ServletRequestUtils.getStringParameter(request, "selectedBulkFieldName", ""));
        
        return "massChange/massChangeSelect.jsp";
    }
    
    // VIEW RESULTS
    @RequestMapping
    public String massChangeResults(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException  {

        // result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        MassChangeCallbackResult callbackResult = (MassChangeCallbackResult)recentResultsCache.getResult(resultsId);
        
        // results
        model.addAttribute("deviceCollection", callbackResult.getDeviceCollection());
        model.addAttribute("massChangeBulkFieldName", callbackResult.getMassChangeBulkFieldColumnHeader().getFieldName());
        model.addAttribute("callbackResult", callbackResult);

        return "massChange/massChangeResults.jsp";
    }

}