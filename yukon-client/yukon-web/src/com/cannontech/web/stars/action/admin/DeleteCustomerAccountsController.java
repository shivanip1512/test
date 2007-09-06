package com.cannontech.web.stars.action.admin;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.DeleteCustAccountsTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class DeleteCustomerAccountsController extends StarsAdminActionController {

    @SuppressWarnings("unchecked")
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {

        try {
            ServletUtils.removeTransientAttributes( session );

            String fromStr = request.getParameter( "From" );
            String toStr = request.getParameter( "To" );
            Integer fromAcctNo = null;
            Integer toAcctNo = null;

            try {
                if (fromStr.equals("") || fromStr.equals("*"))
                    fromAcctNo = new Integer(Integer.MIN_VALUE);
                else
                    fromAcctNo = Integer.valueOf( fromStr );
                if (toStr.equals("") || toStr.equals("*"))
                    toAcctNo = new Integer(Integer.MAX_VALUE);
                else
                    toAcctNo = Integer.valueOf( toStr );
            }
            catch (NumberFormatException e) {}

            Object[][] accounts = CustomerAccount.getAllCustomerAccounts( energyCompany.getEnergyCompanyID() );
            ArrayList acctIDList = new ArrayList();

            for (int i = 0; i < accounts.length; i++) {
                Integer accountID = (Integer) accounts[i][0];
                String accountNo = (String) accounts[i][1];

                if (fromAcctNo != null && toAcctNo != null) {
                    // Account # is numeric
                    try {
                        int acctNo = Integer.parseInt( accountNo );
                        if (acctNo >= fromAcctNo.intValue() && acctNo <= toAcctNo.intValue())
                            acctIDList.add( accountID );
                    }
                    catch (NumberFormatException e) {}
                }
                else {
                    // Account # is alphabetic
                    if (accountNo.compareToIgnoreCase( fromStr ) >= 0 && accountNo.compareToIgnoreCase( toStr ) <= 0)
                        acctIDList.add( accountID );
                }
            }

            if (acctIDList.size() > 0) {
                int[] accountIDs = new int[ acctIDList.size() ];
                for (int i = 0; i < acctIDList.size(); i++)
                    accountIDs[i] = ((Integer)acctIDList.get(i)).intValue();

                TimeConsumingTask task = new DeleteCustAccountsTask(user, accountIDs);
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
                        
                        String redirect = this.getRedirect(request);
                        response.sendRedirect(redirect);
                        return;
                    }

                    if (task.getStatus() == TimeConsumingTask.STATUS_ERROR) {
                        session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
                        ProgressChecker.removeTask( id );
                        
                        String redirect = this.getRedirect(request);
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
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Delete customer accounts failed");
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }

}
