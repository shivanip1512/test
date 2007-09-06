package com.cannontech.web.stars.action.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class DeleteOperatorLoginController extends StarsAdminActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        int userID = ServletRequestUtils.getIntParameter(request, "UserID");
        List<Integer> operLoginIDs = energyCompany.getOperatorLoginIDs();
        
        synchronized (operLoginIDs) {
            Iterator<Integer> it = operLoginIDs.iterator();
            while (it.hasNext()) {
                int loginID = it.next().intValue();
                if (userID == -1 || loginID == userID) {
                    if (loginID == energyCompany.getUserID()) continue;
                    
                    com.cannontech.database.data.user.YukonUser.deleteOperatorLogin( new Integer(loginID) );
                    
                    LiteYukonUser liteUser = this.yukonUserDao.getLiteYukonUser(loginID);
                    ServerUtils.handleDBChange(liteUser, DBChangeMsg.CHANGE_TYPE_DELETE);
                    it.remove();
                }
            }
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
