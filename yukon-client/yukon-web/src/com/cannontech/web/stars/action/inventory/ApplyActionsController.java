package com.cannontech.web.stars.action.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.ManipulateInventoryTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.stars.web.bean.ManipulationBean;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class ApplyActionsController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String[] selectionIDs = request.getParameterValues("SelectionIDs");
        String[] actionTexts = request.getParameterValues("ActionTexts");
        String[] actionTypeIDs = request.getParameterValues("ActionTypeIDs");
        List<String> appliedActions = new ArrayList<String>();
        
        Integer newDevTypeID = null;
        Integer newServiceCompanyID = null;
        Integer newWarehouseID = null;
        Integer newDevStateID = null;
        Integer newEnergyCompanyID = null;
        
        if(selectionIDs != null && selectionIDs.length > 0 && selectionIDs.length == actionTypeIDs.length)
        {
            for(int j = 0; j < selectionIDs.length; j++)
            {
                if(new Integer(actionTypeIDs[j]).intValue() == ServletUtils.ACTION_CHANGEDEVICE)
                    newDevTypeID = new Integer(selectionIDs[j]);
                else if(new Integer(actionTypeIDs[j]).intValue() == ServletUtils.ACTION_TOSERVICECOMPANY)
                    newServiceCompanyID = new Integer(selectionIDs[j]);
                else if(new Integer(actionTypeIDs[j]).intValue() ==  ServletUtils.ACTION_TOWAREHOUSE)
                    newWarehouseID = new Integer(selectionIDs[j]);    
                else if(new Integer(actionTypeIDs[j]).intValue() == ServletUtils.ACTION_CHANGESTATE)
                    newDevStateID = new Integer(selectionIDs[j]);
                
                appliedActions.add(actionTexts[j]);
            }
            
            InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
            ManipulationBean mBean = (ManipulationBean) session.getAttribute("manipBean");
            mBean.setActionsApplied(appliedActions);
            List<LiteInventoryBase> theWares = iBean.getSelectedInventoryList();
            if (theWares == null || theWares.size() < 1)
            {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "There is no selected inventory on which to apply actions.");
                String redirect = this.getReferer(request);
                response.sendRedirect(redirect);
                return;
            }
            
            session.removeAttribute( ServletUtils.ATT_REDIRECT );
            
            boolean confirmOnMessagePage = request.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null;
            String redirectUrl = ServletUtil.createSafeRedirectUrl(request, "/operator/Hardware/InvenResultSet.jsp");
            
            TimeConsumingTask task = new ManipulateInventoryTask( mBean.getEnergyCompany(), newEnergyCompanyID, theWares, 
                  newDevTypeID, newDevStateID, newServiceCompanyID, newWarehouseID, confirmOnMessagePage, redirectUrl, session);
            long id = ProgressChecker.addTask( task );
            String redir = request.getContextPath() + "/operator/Hardware/InvenResultSet.jsp";
            
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
