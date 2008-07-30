package com.cannontech.web.stars.action.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class DeleteOperatorLoginController extends StarsAdminActionController {
    private final Logger log = YukonLogManager.getLogger(getClass());
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        int userID = ServletRequestUtils.getIntParameter(request, "UserID");
        List<Integer> operLoginIDs = energyCompany.getOperatorLoginIDs();
        
        for (final Integer id : operLoginIDs) {
            final int loginID = id.intValue();
            
            if (userID == -1 || loginID == userID) {
                if (loginID == energyCompany.getUserID()) continue;
                
                try {
                    LiteYukonUser liteUser = this.yukonUserDao.getLiteYukonUser(loginID);
                    com.cannontech.database.data.user.YukonUser.deleteOperatorLogin(id);
                    ServerUtils.handleDBChange(liteUser, DBChangeMsg.CHANGE_TYPE_DELETE);
                } catch (UnsupportedOperationException e) {
                    log.error(e);
                }
            }
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
