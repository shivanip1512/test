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
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.db.stars.report.WorkOrderBase;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.WorkOrderManagerUtil;
import com.cannontech.web.stars.action.StarsWorkorderActionController;
import com.google.common.collect.PrimitiveArrays;

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
        
        List<LiteWorkOrderBase> liteWorkOrderList = new ArrayList<LiteWorkOrderBase>();        
        if( searchBy < 0)//we use -1 for search by orderID.  This is a shortcut! and is useful when having member searches.
        {   //We are short cutting a big loop through all energy companies to find the EC with this OrderID by passing in the ECID.
            int workOrderId = Integer.valueOf(searchValue);
            LiteWorkOrderBase workOrderBase = starsWorkOrderBaseDao.getById(workOrderId);
            liteWorkOrderList.add(workOrderBase);
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
            liteWorkOrderList = cleanList(energyCompany.searchWorkOrderByOrderNo( searchValue, searchMembers),
                                          LiteWorkOrderBase.class);
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ACCT_NO) {
            List<LiteStarsCustAccountInformation> accounts = cleanList(energyCompany.searchAccountByAccountNumber( searchValue, searchMembers, true),
                                                                       LiteStarsCustAccountInformation.class);
            liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ) );
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_PHONE_NO) {
            try {
                String phoneNo = ServletUtils.formatPhoneNumberForSearch( searchValue );
                List<LiteStarsCustAccountInformation> accounts = cleanList(energyCompany.searchAccountByPhoneNo( phoneNo, searchMembers),
                                                                           LiteStarsCustAccountInformation.class);
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
            List<LiteStarsCustAccountInformation> accounts = cleanList(energyCompany.searchAccountByLastName( searchValue, searchMembers , true),
                                                                       LiteStarsCustAccountInformation.class);
            liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ) );
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_SERIAL_NO) {
            List<LiteStarsCustAccountInformation> accounts = cleanList(energyCompany.searchAccountBySerialNo( searchValue, searchMembers),
                                                                       LiteStarsCustAccountInformation.class);
            liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ) );
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ADDRESS) {
            List<LiteStarsCustAccountInformation> accounts = cleanList(energyCompany.searchAccountByAddress( searchValue, searchMembers, true),
                                              LiteStarsCustAccountInformation.class);
            liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ));
        }
        
        if (liteWorkOrderList.size() == 0) {
            session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET_DESC, "<div class='ErrorMsg' align='center'>No service order found matching the search criteria.</div>");
        }
        else {
            session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET, liteWorkOrderList);
            session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET_DESC, "Click on a Order # to view the service order details.");
            session.setAttribute(ServletUtils.ATT_REFERRER, this.getReferer(request));
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
    private List<LiteWorkOrderBase> getOrderIDsByAccounts(List<LiteStarsCustAccountInformation> accounts, LiteStarsEnergyCompany defaultEnergyCompany) {
        List<Integer> workOrderIdList = new ArrayList<Integer>();
        
        for (final LiteStarsCustAccountInformation account : accounts) {
            int[] woIDs = WorkOrderBase.searchByAccountID(account.getAccountID());
            if (woIDs == null) continue;
            workOrderIdList.addAll(PrimitiveArrays.asList(woIDs));
        }
        
        List<LiteWorkOrderBase> list = starsWorkOrderBaseDao.getByIds(workOrderIdList);
        return list;
    }
    
    @SuppressWarnings("unchecked")
    private <E> List<E> cleanList(List<Object> list, Class<E> type) {
        List<E> resultList = new ArrayList<E>(list.size());
        
        for (final Object obj : list) {
            Object realObject = (obj instanceof Pair) ? ((Pair) obj).getFirst() : obj;
            resultList.add((E) realObject);
        }
        
        return resultList;
    }
    
}
