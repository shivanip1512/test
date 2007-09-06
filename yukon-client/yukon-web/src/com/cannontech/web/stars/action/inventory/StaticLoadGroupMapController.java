package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.AddSNRangeTask;
import com.cannontech.stars.util.task.AdjustStaticLoadGroupMappingsTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class StaticLoadGroupMapController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    	
    	boolean resetAll = true;
        boolean sendConfig = true;
        String action = ServletRequestUtils.getStringParameter(request, "action");
        String redirect = null;
        
        if(action.startsWith("StaticLoadGroupMapSetDefaults"))
            resetAll = false;
        if(action.endsWith("NoConfig"))
            sendConfig = false;
        
        TimeConsumingTask task = new AdjustStaticLoadGroupMappingsTask( energyCompany, resetAll, sendConfig, request);
        long id = ProgressChecker.addTask( task );
        
        // Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
        for (int i = 0; i < 5; i++) 
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {}
            
            task = ProgressChecker.getTask(id);
            String redir = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
            
            if (task.getStatus() == AddSNRangeTask.STATUS_FINISHED) 
            {
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
                ProgressChecker.removeTask( id );
                if (redir != null) redirect = redir;
            }
            
            if (task.getStatus() == AddSNRangeTask.STATUS_ERROR) {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
                ProgressChecker.removeTask( id );
                redirect = this.getReferer(request);
                response.sendRedirect(redirect);
                return;
            }
        }
        
        String referer = request.getContextPath() + "/operator/Hardware/PowerUserStaticLoadGroupReset.jsp";
        redirect = request.getContextPath() + "/operator/Hardware/PowerUserStaticLoadGroupReset.jsp";
        
        session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
        session.setAttribute(ServletUtils.ATT_REFERRER, referer);
        
        redirect = request.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
        response.sendRedirect(redirect);
    }
	
}
