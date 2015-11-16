package com.cannontech.web.stars.action.workorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.core.service.StarsSearchService;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;
import com.cannontech.stars.database.db.report.WorkOrderBase;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.WorkOrderManagerUtil;
import com.cannontech.web.stars.action.StarsWorkorderActionController;

public class SearchWorkOrderController extends StarsWorkorderActionController {
    @Autowired private StarsWorkOrderBaseDao starsWorkOrderBaseDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private StarsSearchService starsSearchService;

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
        session.setAttribute(ServletUtils.ATT_LAST_SERVICE_SEARCH_OPTION, new Integer(searchBy));
        session.setAttribute(ServletUtils.ATT_LAST_SERVICE_SEARCH_VALUE, new String(searchValue));
        boolean searchMembers = this.rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, user.getYukonUser()) &&
                                (energyCompany.hasChildEnergyCompanies());

        if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ORDER_NO) {
            liteWorkOrderList = starsSearchService.searchWorkOrderByOrderNo(energyCompany, searchValue, searchMembers);
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ACCT_NO) {
            List<Integer> accountIds = 
                    starsSearchService.searchAccountByAccountNumber(energyCompany, searchValue, searchMembers, true);
            liteWorkOrderList.addAll(getOrderIDsByAccounts(accountIds));
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_PHONE_NO) {
            try {
                String phoneNo = ServletUtils.formatPhoneNumberForSearch( searchValue );
                List<Integer> accountIds = 
                        starsSearchService.searchAccountByPhoneNo(energyCompany, phoneNo, searchMembers);
                liteWorkOrderList.addAll(getOrderIDsByAccounts(accountIds));
            }
            catch (WebClientException e) {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
                String redirect = this.getRedirect(request);
                response.sendRedirect(redirect);
                return;
            }
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_LAST_NAME) {
            List<Integer> accountIds =
                    starsSearchService.searchAccountByLastName(energyCompany, searchValue, searchMembers, true);
            liteWorkOrderList.addAll(getOrderIDsByAccounts(accountIds));
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_SERIAL_NO) {
            List<Integer> accountIds = 
                    starsSearchService.searchAccountBySerialNo(energyCompany, searchValue, searchMembers);
            liteWorkOrderList.addAll(getOrderIDsByAccounts(accountIds) );
        }
        else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ADDRESS) {
            List<Integer> accountIds = 
                    starsSearchService.searchAccountByAddress(energyCompany, searchValue, searchMembers, true);
            liteWorkOrderList.addAll(getOrderIDsByAccounts(accountIds));
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

    private List<LiteWorkOrderBase> getOrderIDsByAccounts(List<Integer> accounts) {
        List<Integer> workOrderIdList = new ArrayList<>();

        for (final Integer accountId : accounts) {
            int[] woIDs = WorkOrderBase.searchByAccountID(accountId);
            if (woIDs == null) {
                continue;
            }
            workOrderIdList.addAll(Arrays.asList(ArrayUtils.toObject(woIDs)));
        }

        List<LiteWorkOrderBase> list = starsWorkOrderBaseDao.getByIds(workOrderIdList);
        return list;
    }

}
