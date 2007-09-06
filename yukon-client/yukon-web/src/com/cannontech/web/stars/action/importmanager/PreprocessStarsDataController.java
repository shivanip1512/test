package com.cannontech.web.stars.action.importmanager;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.ImportStarsDataTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsImportManagerActionController;

public class PreprocessStarsDataController extends StarsImportManagerActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        ServletUtils.saveRequest(request, session, new String[] {"ImportDir"});
        String redirect = null;
        
        try {
            File importDir = new File( request.getParameter("ImportDir") );
            if (!importDir.exists() || !importDir.isDirectory())
                throw new WebClientException("The specified directory doesn't exist");
            
            ImportStarsDataTask.preProcessStarsData( importDir, session, energyCompany );
        }
        catch (WebClientException e) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
            redirect = this.getReferer(request);
        }
        
        if (redirect == null) redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
