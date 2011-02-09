package com.cannontech.web.stars.action.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateRoutesController extends StarsAdminActionController {

    @SuppressWarnings("unchecked")
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    
        try {
            List<Integer> routeIDs = energyCompany.getRouteIDs();
            List<Integer> oldRouteIDs = new ArrayList<Integer>( routeIDs );
            
            String[] rtIDs = request.getParameterValues( "RouteIDs" );
            if (routeIDs != null) {
                for (int i = 0; i < rtIDs.length; i++) {
                    Integer routeID = Integer.valueOf( rtIDs[i] );
                    
                    if (oldRouteIDs.contains( routeID )) {
                        // Route already assigned to this energy company
                        oldRouteIDs.remove( routeID );
                    }
                    else {
                        // New route
                        ECToGenericMapping map = new ECToGenericMapping();
                        map.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
                        map.setItemID( routeID );
                        map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_ROUTE );
                        Transaction.createTransaction( Transaction.INSERT, map ).execute();
                        
                        synchronized (routeIDs) { routeIDs.add( routeID ); }
                    }
                }
            }
            
            for (int i = 0; i < oldRouteIDs.size(); i++) {
                // Routes to be removed
                int routeID = oldRouteIDs.get(i).intValue();
                if (routeID == energyCompany.getDefaultRouteId())
                    StarsAdminUtil.updateDefaultRoute( energyCompany, 0, user.getYukonUser());
                StarsAdminUtil.removeRoute( energyCompany, routeID );
            }
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to update the assigned routes" );
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
