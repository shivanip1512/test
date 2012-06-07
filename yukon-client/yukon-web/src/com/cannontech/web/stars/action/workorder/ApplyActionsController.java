package com.cannontech.web.stars.action.workorder;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.ManipulateWorkOrderTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.ManipulationBean;
import com.cannontech.stars.web.bean.WorkOrderBean;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsWorkorderActionController;

public class ApplyActionsController extends StarsWorkorderActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String[] selectionIDs = request.getParameterValues("SelectionIDs");
        String[] actionTexts = request.getParameterValues("ActionTexts");
        String[] actionTypeIDs = request.getParameterValues("ActionTypeIDs");
        List<String> appliedActions = new ArrayList<String>();
        
        Integer changeServiceCompanyID = null;
        Integer changeServiceStatusID = null;
        Integer changeServiceTypeID = null;        
        
        if(selectionIDs != null && selectionIDs.length > 0 && selectionIDs.length == actionTypeIDs.length)
        {
            for(int j = 0; j < selectionIDs.length; j++)
            {
                if(new Integer(actionTypeIDs[j]).intValue() == ServletUtils.ACTION_TOSERVICECOMPANY)
                    changeServiceCompanyID = new Integer(selectionIDs[j]);
                else if(new Integer(actionTypeIDs[j]).intValue() == ServletUtils.ACTION_CHANGE_WO_SERVICE_STATUS)
                    changeServiceStatusID = new Integer(selectionIDs[j]);
                else if(new Integer(actionTypeIDs[j]).intValue() ==  ServletUtils.ACTION_CHANGE_WO_SERVICE_TYPE)
                    changeServiceTypeID = new Integer(selectionIDs[j]);    
                
                appliedActions.add(actionTexts[j]);
            }
            
            WorkOrderBean workOrderBean = (WorkOrderBean) session.getAttribute("workOrderBean");
            ManipulationBean mBean = (ManipulationBean) session.getAttribute("woManipulationBean");
            mBean.setActionsApplied(appliedActions);
            if(workOrderBean.getNumberOfRecords() < 1)
            {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "There are no Work Orders selected to update.");
                String location = this.getReferer(request);
                response.sendRedirect(location);
                return;
            }
            
            session.removeAttribute( ServletUtils.ATT_REDIRECT );
            LiteYukonUser liteYukonUser = (LiteYukonUser ) session.getAttribute( ServletUtils.ATT_YUKON_USER );

            boolean confirmOnMessagePage = request.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null;
            String redirectUrl = ServletUtil.createSafeRedirectUrl(request, "/operator/WorkOrder/WorkOrderResultSet.jsp");
            
            TimeConsumingTask  task = new ManipulateWorkOrderTask( liteYukonUser, workOrderBean.getWorkOrderList(), 
                changeServiceCompanyID, changeServiceStatusID, changeServiceTypeID, confirmOnMessagePage, redirectUrl, session);
            long id = ProgressChecker.addTask( task );
            String redir = request.getContextPath() + "/operator/WorkOrder/WorkOrderResultSet.jsp";
            
            //Wait 2 seconds for the task to finish (or error out), if not, then go to the progress page
            for (int i = 0; i < 2; i++) 
            {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {}
                
                task = ProgressChecker.getTask(id);
                
                if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED) {
                    session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
                    ProgressChecker.removeTask( id );
                    String redirect = this.getRedirect(request);
                    if (redir != null) redirect = redir;
                    response.sendRedirect(redirect);
                    return;
                }
                
                if (task.getStatus() == TimeConsumingTask.STATUS_ERROR) {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
                    ProgressChecker.removeTask( id );
                    String redirect = this.getReferer(request);
                    response.sendRedirect(redirect);
                    return;
                }
            }
            session.setAttribute(ServletUtils.ATT_REDIRECT, redir);
            session.setAttribute(ServletUtils.ATT_REFERRER, redir);
            String redirect = request.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
            response.sendRedirect(redirect);
        }
        
    }
    
}
