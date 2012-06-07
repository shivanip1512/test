package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.util.Pair;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class SelectLMHardwareController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        final Integer invID = ServletRequestUtils.getIntParameter(request, "InvID");
        final Integer memberId = ServletRequestUtils.getIntParameter(request, "MemberID");
        
        if (memberId != null) {
            LiteInventoryBase liteInv = starsInventoryBaseDao.getByInventoryId(invID);
            LiteStarsEnergyCompany member = this.starsDatabaseCache.getEnergyCompany(memberId);
            session.setAttribute(InventoryManagerUtil.INVENTORY_SELECTED, new Pair<Object,Object>(liteInv, member));
        } else {
            LiteInventoryBase liteInv = starsInventoryBaseDao.getByInventoryId(invID);
            session.setAttribute(InventoryManagerUtil.INVENTORY_SELECTED, liteInv);
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
