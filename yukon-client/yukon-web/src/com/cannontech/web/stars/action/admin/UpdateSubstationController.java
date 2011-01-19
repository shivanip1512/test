package com.cannontech.web.stars.action.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteSubstation;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateSubstationController extends StarsAdminActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            int subID = ServletRequestUtils.getIntParameter(request, "SubID");
            boolean newSubstation = (subID == -1);

            com.cannontech.database.data.stars.Substation sub =
                new com.cannontech.database.data.stars.Substation();
            com.cannontech.database.db.stars.Substation subDB = sub.getSubstation();

            LiteSubstation liteSub = null;
            if (!newSubstation) {
                liteSub = energyCompany.getSubstation( subID );
                StarsLiteFactory.setSubstation( subDB, liteSub );
            }

            subDB.setSubstationName( request.getParameter("SubName") );
            subDB.setRouteID( Integer.valueOf(request.getParameter("Route")) );

            if (newSubstation) {
                sub.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
                sub = (com.cannontech.database.data.stars.Substation)
                Transaction.createTransaction( Transaction.INSERT, sub ).execute();

                liteSub = (LiteSubstation) StarsLiteFactory.createLite( sub.getSubstation() );
                energyCompany.addSubstation( liteSub );
            }
            else {
                subDB = (com.cannontech.database.db.stars.Substation)
                Transaction.createTransaction( Transaction.UPDATE, subDB ).execute();
                StarsLiteFactory.setLiteSubstation( liteSub, subDB );
            }

            if (newSubstation)
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Substation created successfully");
            else
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Substation information updated successfully");
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update substation information");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }

}
