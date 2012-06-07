package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.purchasing.Shipment;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.AddSNRangeTask;
import com.cannontech.stars.util.task.AddShipmentSNRangeTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.PurchaseBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class ShipmentSNRangeAddController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    	
    	String redirect = request.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";
    	String referer = request.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";

    	PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        Shipment shipment = pBean.getCurrentShipment();
        
        TimeConsumingTask task = new AddShipmentSNRangeTask( pBean.getSerialNumberMember(), shipment.getSerialNumberStart(), shipment.getSerialNumberEnd(), 
                                                             pBean.getCurrentSchedule().getModelID(), new Integer(pBean.getSerialNumberDeviceState().getEntryID()), 
                                                             shipment.getWarehouseID(), session);
        long id = ProgressChecker.addTask( task );
        
        // Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {}
            
            task = ProgressChecker.getTask(id);
            String redir = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
            
            if (task.getStatus() == AddSNRangeTask.STATUS_FINISHED) {
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
                ProgressChecker.removeTask( id );
                if (redir != null) redirect = redir;
                /**
                 * Need to make sure that the newly created serial range is saved to the shipment
                 */
                try
                {
                    Transaction.createTransaction(Transaction.UPDATE, shipment).execute();
                }
                catch (TransactionException e) 
                {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Database error.  Serial range was added to inventory, but the shipment serial range fields could not be saved.");
                }
                response.sendRedirect(redirect);
                return;
            }
            
            if (task.getStatus() == AddSNRangeTask.STATUS_ERROR) {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
                ProgressChecker.removeTask( id );
                redirect = referer;
                response.sendRedirect(redirect);
                return;
            }
        }
        
        session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
        session.setAttribute(ServletUtils.ATT_REFERRER, referer);
        
        redirect = request.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
    	response.sendRedirect(redirect);
    }
	
}
