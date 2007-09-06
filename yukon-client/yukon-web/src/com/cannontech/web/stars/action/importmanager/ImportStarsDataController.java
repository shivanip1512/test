package com.cannontech.web.stars.action.importmanager;

import java.io.File;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.ImportStarsDataTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.ImportManagerUtil;
import com.cannontech.web.stars.action.StarsImportManagerActionController;

public class ImportStarsDataController extends StarsImportManagerActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        Hashtable preprocessedData = (Hashtable) session.getAttribute(ImportManagerUtil.PREPROCESSED_DATA);
        File importDir = (File) session.getAttribute(ImportManagerUtil.CUSTOMER_FILE_PATH);
        
        Hashtable processedData = ImportStarsDataTask.postProcessStarsData( preprocessedData, importDir, energyCompany );
        
        ImportStarsDataTask task = new ImportStarsDataTask( energyCompany, processedData, importDir );
        long id = ProgressChecker.addTask( task );
        
        session.setAttribute(ServletUtils.ATT_REDIRECT, this.getRedirect(request));
        session.setAttribute(ServletUtils.ATT_REFERRER, this.getReferer(request));
        
        String redirect = request.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
        response.sendRedirect(redirect);
    }
    
}
