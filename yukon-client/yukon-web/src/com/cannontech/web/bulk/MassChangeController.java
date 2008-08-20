package com.cannontech.web.bulk;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldService;
import com.cannontech.common.bulk.service.BulkOperationCallbackResults;
import com.cannontech.common.bulk.service.MassChangeFileInfo;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.roles.operator.DeviceActionsRole;
import com.cannontech.web.security.WebSecurityChecker;
import com.cannontech.web.security.annotation.CheckRole;

@CheckRole(DeviceActionsRole.ROLEID)
public class MassChangeController extends BulkControllerBase {

    private BulkFieldService bulkFieldService = null;
    private RecentResultsCache<BulkOperationCallbackResults<?>> recentBulkOperationResultsCache = null;
    private WebSecurityChecker webSecurityChecker = null;
    
    /**
     * SELECT MASS CHANGE TYPE
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    public ModelAndView massChangeSelect(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        webSecurityChecker.checkRoleProperty(DeviceActionsRole.MASS_CHANGE);
        
        ModelAndView mav = new ModelAndView("massChange/massChangeSelect.jsp");
        
        // pass along deviceCollection
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        // available masss change operations
        List<BulkField<?, ?>> massChangableBulkFields = bulkFieldService.getMassChangableBulkFields();
        mav.addObject("massChangableBulkFields", massChangableBulkFields);
        
        // pass previously selected bulk field name through
        mav.addObject("selectedBulkFieldName", ServletRequestUtils.getStringParameter(request, "selectedBulkFieldName", ""));
        
        return mav;
    }
    
    // VIEW RESULTS
    public ModelAndView massChangeResults(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        webSecurityChecker.checkRoleProperty(DeviceActionsRole.MASS_CHANGE);
        
        ModelAndView mav = new ModelAndView("massChange/massChangeResults.jsp");

        // result info
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        BulkOperationCallbackResults<?> bulkOperationCallbackResults = recentBulkOperationResultsCache.getResult(resultsId);
        
        // file info
        MassChangeFileInfo massChangeFileInfo = (MassChangeFileInfo)bulkOperationCallbackResults.getBulkFileInfo();
        
        mav.addObject("deviceCollection", massChangeFileInfo.getDeviceCollection());
        mav.addObject("massChangeBulkFieldName", massChangeFileInfo.getMassChangeBulkFieldName());
        mav.addObject("bulkUpdateOperationResults", bulkOperationCallbackResults);

        return mav;
    }

    @Required
    public void setBulkFieldService(BulkFieldService bulkFieldService) {
        this.bulkFieldService = bulkFieldService;
    }

    @Required
    public void setRecentBulkOperationResultsCache(
            RecentResultsCache<BulkOperationCallbackResults<?>> recentBulkOperationResultsCache) {
        this.recentBulkOperationResultsCache = recentBulkOperationResultsCache;
    }
    
    @Autowired
    public void setWebSecurityChecker(WebSecurityChecker webSecurityChecker) {
        this.webSecurityChecker = webSecurityChecker;
    }
    
}
