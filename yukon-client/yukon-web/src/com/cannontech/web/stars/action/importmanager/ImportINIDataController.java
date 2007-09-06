package com.cannontech.web.stars.action.importmanager;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.ImportStarsDataTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsImportManagerActionController;

public class ImportINIDataController extends StarsImportManagerActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String redirect = null;
        
        try {
            boolean selListImported = false;
            
            if (request.getParameter("S3DATA_INI").length() > 0) {
                File selListFile = new File( request.getParameter("S3DATA_INI") );
                ImportStarsDataTask.importSelectionLists( selListFile, energyCompany );
                selListImported = true;
            }
            
            if (request.getParameter("STARS3_INI").length() > 0) {
                File appSettingsFile = new File( request.getParameter("STARS3_INI") );
                ImportStarsDataTask.importAppSettings( appSettingsFile, session );
            }
            
            String msg = "INI file(s) imported successfully.";
            if (selListImported)
                msg += "<br>Please go to the energy company settings page to update appliance categories and device type list.";
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, msg);
        }
        catch (WebClientException e) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
            redirect = this.getReferer(request);
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to import INI file(s)");
            redirect = this.getReferer(request);
        }
        
        if (redirect == null) redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
