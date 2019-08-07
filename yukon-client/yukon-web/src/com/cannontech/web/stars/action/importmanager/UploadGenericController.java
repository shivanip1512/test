package com.cannontech.web.stars.action.importmanager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.exception.FileImportException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.FileUploadUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.util.task.UploadGenericFileTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsImportManagerActionController;

public class UploadGenericController extends StarsImportManagerActionController {
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        String redirect = null;
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContextUtils.getYukonUserContext(request));
        try 
        {
            MultipartFile genericMultipartFile = mRequest.getFile("GenericFile");
            FileUploadUtils.validateTabularDataUploadFileType(genericMultipartFile);
            
            TimeConsumingTask task = new UploadGenericFileTask( energyCompany, genericMultipartFile );
            long id = ProgressChecker.addTask( task );
            
            // Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {}
                
                task = ProgressChecker.getTask(id);
                
                if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED) {
                    session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
                    ProgressChecker.removeTask( id );
                    String location = this.getRedirect(request);
                    response.sendRedirect(location);
                    return;
                }
                
                if (task.getStatus() == TimeConsumingTask.STATUS_ERROR) {
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
        catch (FileImportException e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, accessor.getMessage(e.getMessage()));
            redirect = this.getReferer(request);
        }
        
        response.sendRedirect(redirect);
    }
    
}
