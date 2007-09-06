package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class DeleteSubstationController extends StarsAdminActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {

        try {
            int subID = ServletRequestUtils.getIntParameter(request, "SubID");
            if (subID == -1) {
                StarsAdminUtil.deleteAllSubstations( energyCompany );
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Substations have been deleted successfully");
            }
            else {
                StarsAdminUtil.deleteSubstation( subID, energyCompany );
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Substation has been deleted successfully");
            }
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete the substation");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }

}
