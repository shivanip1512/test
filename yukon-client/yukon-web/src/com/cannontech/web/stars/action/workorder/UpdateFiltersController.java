package com.cannontech.web.stars.action.workorder;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.WorkOrderBean;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsWorkorderActionController;

public class UpdateFiltersController extends StarsWorkorderActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String start = ServletRequestUtils.getStringParameter(request, "start");
        String stop = ServletRequestUtils.getStringParameter(request, "stop");
        WorkOrderBean woBean = (WorkOrderBean) session.getAttribute("workOrderBean");
        woBean.setStart(start);
        woBean.setStop(stop);
        woBean.setSearchResults(null);

        String[] selectionIDs = request.getParameterValues("SelectionIDs");
        String[] filterTexts = request.getParameterValues("FilterTexts");
        String[] yukonDefIDs = request.getParameterValues("YukonDefIDs");

        List<FilterWrapper> filterWrappers = new ArrayList<FilterWrapper>();
        if( filterTexts != null)
        {
            for(int j = 0; j < filterTexts.length; j++)
            {
                FilterWrapper wrapper = new FilterWrapper(yukonDefIDs[j], filterTexts[j], selectionIDs[j] );
                filterWrappers.add(wrapper);
            }
        }
        woBean.setViewAllResults(false);    //reset the view option
        session.setAttribute( ServletUtil.FILTER_WORKORDER_LIST, filterWrappers );
        
        String redirect = request.getContextPath() + "/operator/WorkOrder/WorkOrder.jsp";
        response.sendRedirect(redirect);
    }
    
}
