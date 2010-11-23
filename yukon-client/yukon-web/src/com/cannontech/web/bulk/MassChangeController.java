package com.cannontech.web.bulk;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.callbackResult.MassChangeCallbackResult;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldService;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
public class MassChangeController extends BulkControllerBase {

    private BulkFieldService bulkFieldService = null;
    private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache = null;
    
    /**
     * SELECT MASS CHANGE TYPE
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ModelAndView massChangeSelect(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("massChange/massChangeSelect.jsp");
        
        // pass along deviceCollection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        // available mass change operations
        List<BulkField<?, ?>> massChangableBulkFields = bulkFieldService.getMassChangableBulkFields();
        mav.addObject("massChangableBulkFields", massChangableBulkFields);
        
        // pass previously selected bulk field name through
        mav.addObject("selectedBulkFieldName", ServletRequestUtils.getStringParameter(request, "selectedBulkFieldName", ""));
        
        return mav;
    }
    
    // VIEW RESULTS
    public ModelAndView massChangeResults(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("massChange/massChangeResults.jsp");

        // result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        MassChangeCallbackResult callbackResult = (MassChangeCallbackResult)recentResultsCache.getResult(resultsId);
        
        // results
        mav.addObject("deviceCollection", callbackResult.getDeviceCollection());
        mav.addObject("massChangeBulkFieldName", callbackResult.getMassChangeBulkFieldColumnHeader().getFieldName());
        mav.addObject("callbackResult", callbackResult);

        return mav;
    }

    @Required
    public void setBulkFieldService(BulkFieldService bulkFieldService) {
        this.bulkFieldService = bulkFieldService;
    }

    @Required
    public void setRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }
}
