package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.DeleteEnergyCompanyTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.login.LoginService;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class DeleteEnergyCompanyController extends StarsAdminActionController {
    
    private LoginService loginService;
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        if (energyCompany.hasChildEnergyCompanies()) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Cannot delete this energy company because its member list is not empty");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }

        TimeConsumingTask task = new DeleteEnergyCompanyTask( user.getEnergyCompanyID(), dbPersistentDao, yukonListDao);
        // addTask starts the task
        long id = ProgressChecker.addTask( task );

//        // Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {}

            task = ProgressChecker.getTask(id);

            if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED) {
                loginService.logout(request, response);
                ProgressChecker.removeTask( id );
                return;
            }

            if (task.getStatus() == TimeConsumingTask.STATUS_ERROR) {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
                ProgressChecker.removeTask( id );
                return;
            }
        }

        session.setAttribute(ServletUtils.ATT_REDIRECT, this.getRedirect(request));
        String redirect = request.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
        response.sendRedirect(redirect);
    }
    
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

}
