package com.cannontech.web.stars.action.workorder;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.bean.WorkOrderBean;
import com.cannontech.web.stars.action.StarsWorkorderActionController;

public class ManipulateSelectedResultsController extends StarsWorkorderActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String[] selections = request.getParameterValues("checkWorkOrder");
        if( selections == null)//none selected
        {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "There are no individual Work Orders checked to manipulate.");
            String location = this.getReferer(request);
            response.sendRedirect(location);
            return;
        }
        int [] selectionIDs = new int[selections.length];
        for ( int i = 0; i < selections.length; i++)
            selectionIDs[i] = Integer.valueOf(selections[i]).intValue();
        
        WorkOrderBean workOrderBean = (WorkOrderBean) session.getAttribute("workOrderBean");
        List<LiteWorkOrderBase> liteWorkOrderList = new ArrayList<LiteWorkOrderBase>(); 
        for ( int i = 0; i < workOrderBean.getWorkOrderList().size(); i ++)
        {
            LiteWorkOrderBase liteWorkOrderBase = workOrderBean.getWorkOrderList().get(i);
            for (int j = 0; j < selectionIDs.length; j++)
            {
                if( liteWorkOrderBase.getOrderID() == selectionIDs[j])
                {
                    liteWorkOrderList.add(liteWorkOrderBase);
                    break;
                }
            }
        }
        workOrderBean.setWorkOrderList(liteWorkOrderList);

        String redirect = request.getContextPath() + "/operator/WorkOrder/ChangeWorkOrders.jsp";
        response.sendRedirect(redirect);
    }
    
}
