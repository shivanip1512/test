package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.user.UserUtils;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateOperatorLoginController extends StarsAdminActionController {
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            int userID = ServletRequestUtils.getIntParameter(request, "UserID");
            int operGroupID = ServletRequestUtils.getIntParameter(request, "OperatorGroup");
            String username = ServletRequestUtils.getStringParameter(request, "Username" );
            String password = ServletRequestUtils.getStringParameter(request, "Password" );
            boolean enabled = ServletRequestUtils.getBooleanParameter(request, "Status");
            String status = (enabled)? UserUtils.STATUS_ENABLED : UserUtils.STATUS_DISABLED;
            
            if (userID == -1) {
                // Create new operator login
                LiteYukonGroup liteGroup = this.authDao.getGroup( operGroupID );
                LiteYukonUser liteUser = StarsAdminUtil.createOperatorLogin(
                        username, password, status, new LiteYukonGroup[] {liteGroup}, energyCompany );
                
                session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Operator login created successfully" );
                String redirect = this.getRedirect(request) + "?UserID=" + liteUser.getUserID();
                response.sendRedirect(redirect);
                return;
            }

            LiteYukonUser liteUser = this.yukonUserDao.getLiteYukonUser( userID );
            LiteYukonGroup loginGroup = this.authDao.getGroup( operGroupID );
            StarsAdminUtil.updateLogin( liteUser, username, password, status, loginGroup, energyCompany, false );

            session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Operator login updated successfully" );
        }
        catch (WebClientException e) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update operator login");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }

}
