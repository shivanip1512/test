package com.cannontech.web.stars.action.importmanager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.ImportStarsDataTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsImportManagerActionController;

public class AssignSelectionListController extends StarsImportManagerActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String redirect = null;
        
        try {
            ImportStarsDataTask.assignSelectionList( request, session, energyCompany );
        }
        catch (WebClientException e) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
            redirect = this.getReferer(request);
        }
        
        if (redirect == null) redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
