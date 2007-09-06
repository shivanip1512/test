package com.cannontech.web.stars.action.importmanager;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.ImportCustAccountsTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.util.task.UploadGenericFileTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsImportManagerActionController;

public class UploadGenericController extends StarsImportManagerActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        List<FileItem> itemList = ServletUtils.getItemList(request);
        String redirect = null;
        
        try 
        {
            FileItem genericFile = ServletUtils.getUploadFile( itemList, "GenericFile" );
            
            if (genericFile == null)
                throw new WebClientException( "No file is provided" );
            
            TimeConsumingTask task = new UploadGenericFileTask( energyCompany, genericFile );
            long id = ProgressChecker.addTask( task );
            
            // Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {}
                
                task = ProgressChecker.getTask(id);
                
                if (task.getStatus() == ImportCustAccountsTask.STATUS_FINISHED) {
                    session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
                    ProgressChecker.removeTask( id );
                    String location = this.getRedirect(request);
                    response.sendRedirect(location);
                    return;
                }
                
                if (task.getStatus() == ImportCustAccountsTask.STATUS_ERROR) {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
                    ProgressChecker.removeTask( id );
                    String location = this.getRedirect(request);
                    response.sendRedirect(location);
                    return;
                }
            }
            
            session.setAttribute(ServletUtils.ATT_REDIRECT, this.getRedirect(request));
            session.setAttribute(ServletUtils.ATT_REFERRER, this.getRedirect(request));
            redirect = request.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
        }
        catch (WebClientException e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
            redirect = this.getReferer(request);
        }
        
        response.sendRedirect(redirect);
    }
    
}
