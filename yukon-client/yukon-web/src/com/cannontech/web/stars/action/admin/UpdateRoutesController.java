package com.cannontech.web.stars.action.admin;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ExceptionHelper;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.web.stars.action.StarsAdminActionController;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class UpdateRoutesController extends StarsAdminActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    
        try {
            List<Integer> routeIds = energyCompany.getRouteIDs();
            Set<Integer> oldRouteIDs = Sets.newHashSet(routeIds);
            List<Integer> inheritedRouteIds = getAllRouteIdsUp(energyCompany.getParent());
            
            // Some of these "RouteIDs" will be the inherited routes (the JavaScript appears
            // to be able to tell the difference, but it just submits everything).
            int[] intParameters = ServletRequestUtils.getIntParameters(request, "RouteIDs");
            List<Integer> submittedRouteIds = Lists.newArrayList(ArrayUtils.toObject(intParameters));
            submittedRouteIds.removeAll(inheritedRouteIds);
            for (final Integer routeId : submittedRouteIds) {

                if (oldRouteIDs.remove(routeId)) {
                    // Route already assigned to this energy company
                } else {
                    // New route
                    
                    // If any of our descendants have the route, remove it from them.
                    callbackWithEachDescendant(energyCompany, new SimpleCallback<LiteStarsEnergyCompany>() {
                        @Override
                        public void handle(LiteStarsEnergyCompany child) throws Exception {
                            if (child.getRouteIDs().contains(routeId)) {
                                energyCompanyService.removeRouteFromEnergyCompany(child.getEnergyCompanyId(), routeId);
                                // I think it is correct that the above doesn't call StarsAdminUtil.removeRoute().
                                // That method will additionally change the route on all relevant hardware,
                                // in this case, we know that the route is still valid for the child, so
                                // it doesn't make sense to change it.
                            }
                        }
                    });
                    
                    energyCompanyService.addRouteToEnergyCompany(energyCompany.getEnergyCompanyId(), routeId);
                }
            }

            for (final Integer routeId : oldRouteIDs) {
                // Routes to be removed
                if (routeId == energyCompany.getDefaultRouteId()) {
                    defaultRouteService.updateDefaultRoute( energyCompany, CtiUtilities.NONE_ZERO_ID, user.getYukonUser());
                }
                callbackWithEachDescendant(energyCompany, new SimpleCallback<LiteStarsEnergyCompany>() {
                    @Override
                    public void handle(LiteStarsEnergyCompany child) throws Exception {
                        if (routeId == child.getDefaultRouteId()) {
                            defaultRouteService.updateDefaultRoute( child, CtiUtilities.NONE_ZERO_ID, user.getYukonUser());
                        }
                    }
                });
                StarsAdminUtil.removeRoute( energyCompany, routeId );
            }
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to update the assigned routes" );
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }

    private static void callbackWithEachDescendant(LiteStarsEnergyCompany energyCompany,
            SimpleCallback<LiteStarsEnergyCompany> simpleCallback) {
        Iterable<LiteStarsEnergyCompany> children = energyCompany.getChildren();
        for (LiteStarsEnergyCompany liteStarsEnergyCompany : children) {
            try {
                simpleCallback.handle(liteStarsEnergyCompany);
            } catch (Exception e) {
                ExceptionHelper.throwOrWrap(e);
            }
            callbackWithEachDescendant(liteStarsEnergyCompany, simpleCallback);
        }
    }

    private static List<Integer> getAllRouteIdsUp(LiteStarsEnergyCompany energyCompany) {
        if (energyCompany == null) return ImmutableList.of();
        
        Builder<Integer> builder = ImmutableList.builder();
        builder.addAll(energyCompany.getRouteIDs());
        builder.addAll(getAllRouteIdsUp(energyCompany.getParent()));
        return builder.build();
    }
    
}
