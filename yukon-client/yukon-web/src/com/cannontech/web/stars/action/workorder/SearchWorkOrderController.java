package com.cannontech.web.stars.action.workorder;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.Pair;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.report.WorkOrderBase;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.WorkOrderManagerUtil;
import com.cannontech.web.stars.action.StarsWorkorderActionController;

public class SearchWorkOrderController extends StarsWorkorderActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        session.removeAttribute( WorkOrderManagerUtil.WORK_ORDER_SET );
        
        int searchBy = ServletRequestUtils.getIntParameter(request, "SearchBy", -1);
        
        
        String searchValue = ServletRequestUtils.getStringParameter(request, "SearchValue");
        if (searchValue == null || searchValue.trim().length() == 0) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Search value cannot be empty");
            String redirect = this.getRedirect(request);
            response.sendRedirect(redirect);
            return;
        }
        
        List<Object> liteWorkOrderList = new ArrayList<Object>();        
        if( searchBy < 0)//we use -1 for search by orderID.  This is a shortcut! and is useful when having member searches.
        {   //We are short cutting a big loop through all energy companies to find the EC with this OrderID by passing in the ECID.
            liteWorkOrderList.add(energyCompany.getWorkOrderBase(Integer.parseInt(searchValue), true));
            session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET, liteWorkOrderList);
            String redirect = request.getContextPath() + "/operator/WorkOrder/ModifyWorkOrder.jsp";
            response.sendRedirect(redirect);
            return;
        }
        
        
        // Remember the last search option
        session.setAttribute( ServletUtils.ATT_LAST_SERVICE_SEARCH_OPTION, new Integer(searchBy));
        session.setAttribute( ServletUtils.ATT_LAST_SERVICE_SEARCH_VALUE, new String(searchValue) );
        boolean searchMembers = this.authDao.checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS ) && 
                                (energyCompany.getChildren().size() > 0);

        if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ORDER_NO) {
            liteWorkOrderList = energyCompany.searchWorkOrderByOrderNo( searchValue, searchMembers );
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ACCT_NO) {
            List<Object> accounts = energyCompany.searchAccountByAccountNumber( searchValue, searchMembers, true );
            liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ) );
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_PHONE_NO) {
            try {
                String phoneNo = ServletUtils.formatPhoneNumberForSearch( searchValue );
                List<Object> accounts = energyCompany.searchAccountByPhoneNo( phoneNo, searchMembers );
                liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ) );
            }
            catch (WebClientException e) {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
                String redirect = this.getRedirect(request);
                response.sendRedirect(redirect);
                return;
            }
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_LAST_NAME) {
            List<Object> accounts = energyCompany.searchAccountByLastName( searchValue, searchMembers , true);
            liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ) );
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_SERIAL_NO) {
            List<Object> accounts = energyCompany.searchAccountBySerialNo( searchValue, searchMembers );
            liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ) );
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ADDRESS) {
            List<Object> accounts = energyCompany.searchAccountByAddress( searchValue, searchMembers, true);
            liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ) );
        }
        
        if (liteWorkOrderList.size() == 0) {
            session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET_DESC, "<div class='ErrorMsg' align='center'>No service order found matching the search criteria.</div>");
        }
        //Don't be smart about going directly to the web page, this way we can go through the context switch of EC if needed
        /*else if (liteWorkOrderList.size() == 1) {   
            session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET, liteWorkOrderList);
            redirect = req.getContextPath() + "/operator/WorkOrder/ModifyWorkOrder.jsp";
        }*/
        else {
//          ArrayList soList = new ArrayList();
//          for (int i = 0; i < orderIDs.length; i++)
//              soList.add( energyCompany.getWorkOrderBase(orderIDs[i], true) );
            session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET, liteWorkOrderList);
            session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET_DESC, "Click on a Order # to view the service order details.");
            session.setAttribute(ServletUtils.ATT_REFERRER, this.getReferer(request));
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
    private List<Object> getOrderIDsByAccounts(List<Object> accounts, LiteStarsEnergyCompany defaultEnergyCompany) {
        List<Object> liteWorkOrderList = new ArrayList<Object>();
        if (accounts != null && accounts.size() > 0) {
            for (int i = 0; i < accounts.size(); i++) {
                LiteStarsCustAccountInformation liteAcctInfo = null;             
                LiteStarsEnergyCompany liteStarsEnergyCompany = null;
                if (accounts.get(i) instanceof Pair){
                    liteAcctInfo = (LiteStarsCustAccountInformation)((Pair)accounts.get(i)).getFirst();
                    liteStarsEnergyCompany = (LiteStarsEnergyCompany)((Pair)accounts.get(i)).getSecond();
                }
                else
                {
                    liteAcctInfo = (LiteStarsCustAccountInformation) accounts.get(i);
                    liteStarsEnergyCompany = defaultEnergyCompany;
                }
                
                if (liteAcctInfo.isExtended()) {
                    for(int j = 0; j < liteAcctInfo.getServiceRequestHistory().size(); j++)
                        liteWorkOrderList.add(liteStarsEnergyCompany.getWorkOrderBase(liteAcctInfo.getServiceRequestHistory().get(j).intValue(), true));
                }
                else {
                    int[] woIDs = WorkOrderBase.searchByAccountID( liteAcctInfo.getAccountID() );
                    for (int j = 0; j < woIDs.length; j++)
                        liteWorkOrderList.add(liteStarsEnergyCompany.getWorkOrderBase(woIDs[j], true));
                }
            }
        }
        return liteWorkOrderList;
    }
    
}
