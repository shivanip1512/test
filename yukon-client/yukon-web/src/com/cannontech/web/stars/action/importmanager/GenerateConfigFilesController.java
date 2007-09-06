package com.cannontech.web.stars.action.importmanager;

import java.io.File;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.ImportDSMDataTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsImportManagerActionController;

public class GenerateConfigFilesController extends StarsImportManagerActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String redirect = null;
        
        try {
            File importDir = new File( request.getParameter("ImportDir") );
            if (importDir.isFile())
                importDir = importDir.getParentFile();
            
            if (!importDir.exists())
                throw new WebClientException("The specified directory doesn't exist");
            
            Properties savedReq = new Properties();
            savedReq.put("ImportDir", importDir.getAbsolutePath());
            session.setAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST, savedReq);
            
            ImportDSMDataTask.generateConfigFiles( importDir );
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Configuration files have been generated. Please follow the instructions in the files to complete them before converting the database.");
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            if (e instanceof WebClientException)
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
            else
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to generate the configuration files");
            redirect = this.getReferer(request);
        }
        
        if (redirect == null) redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
