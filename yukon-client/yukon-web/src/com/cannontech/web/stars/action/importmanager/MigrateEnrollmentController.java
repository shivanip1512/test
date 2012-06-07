package com.cannontech.web.stars.action.importmanager;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.EnrollmentMigrationTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.stars.action.StarsImportManagerActionController;

public class MigrateEnrollmentController extends StarsImportManagerActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {

        try {
            final TimeConsumingTask task = new EnrollmentMigrationTask() {
                private TimeConsumingTask realTask;
                
                public void run() {
                    this.setStatus(EnrollmentMigrationTask.STATUS_RUNNING);
                    
                    realTask = new EnrollmentMigrationTask(energyCompany);
                    long realTaskId = ProgressChecker.addTask(realTask);
                    
                    for (realTask = ProgressChecker.getTask(realTaskId); ((realTask.getStatus() != EnrollmentMigrationTask.STATUS_FINISHED) ||
                            (realTask.getStatus() != EnrollmentMigrationTask.STATUS_ERROR));) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignore) {}
                        
                        if (realTask.getStatus() == EnrollmentMigrationTask.STATUS_FINISHED) {
                            ProgressChecker.removeTask(realTaskId);
                            this.setStatus(EnrollmentMigrationTask.STATUS_FINISHED);
                            break;
                        }
                        
                        if (realTask.getStatus() == EnrollmentMigrationTask.STATUS_ERROR) {
                            ProgressChecker.removeTask(realTaskId);
                            this.setStatus(EnrollmentMigrationTask.STATUS_ERROR);
                            return;
                        }
                    }
                    
                    this.setStatus(EnrollmentMigrationTask.STATUS_FINISHED);
                }
                
                public String getProgressMsg() {
                    return realTask.getProgressMsg(); 
                }
                
                public void cancel() {
                    if (this.getStatus() == EnrollmentMigrationTask.STATUS_RUNNING) {
                        this.setStatus(EnrollmentMigrationTask.STATUS_CANCELING);
                        if (realTask != null) {
                            realTask.cancel();
                            while (realTask.getStatus() != EnrollmentMigrationTask.STATUS_CANCELED ||
                                   realTask.getStatus() != EnrollmentMigrationTask.STATUS_ERROR ||
                                   realTask.getStatus() != EnrollmentMigrationTask.STATUS_FINISHED) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignore) {}
                                
                                if (realTask.getStatus() == EnrollmentMigrationTask.STATUS_CANCELED) {
                                    break;
                                }
                            }
                        }
                        this.setStatus(EnrollmentMigrationTask.STATUS_CANCELED);
                    }
                }
            };
            
            
            long id = ProgressChecker.addTask(task);
            int x;
            TimeConsumingTask t;
            for (t = ProgressChecker.getTask(id), x = 0; x < 5; x++ ) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {}
                
                if (t.getStatus() == EnrollmentMigrationTask.STATUS_FINISHED) {
                    session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Migration successful.");
                    ProgressChecker.removeTask(id);
                    String redirect = ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getCurrentPage();
                    redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
                    response.sendRedirect(redirect);
                    return;
                }
                
                if (t.getStatus() == EnrollmentMigrationTask.STATUS_ERROR) {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Migration did not finish correctly.");
                    ProgressChecker.removeTask(id);
                    String redirect = ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getCurrentPage();
                    redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
                    response.sendRedirect(redirect);                    
                    return;
                }
            }
            
            session.setAttribute(ServletUtils.ATT_REDIRECT, this.getRedirect(request));
            session.setAttribute(ServletUtils.ATT_REFERRER, this.getRedirect(request));
            String redirect = request.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
            response.sendRedirect(redirect);
            return;
        }
        catch (WebClientException e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
            String redirect = ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getCurrentPage();
            redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
            response.sendRedirect(redirect);            
            return;
        }
        
    }
}
