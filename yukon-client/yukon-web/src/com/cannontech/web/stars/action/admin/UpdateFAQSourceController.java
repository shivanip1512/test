package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateFAQSourceController extends StarsAdminActionController {
    private enum Type { Customized, Inherited, Link };
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            final String source = ServletRequestUtils.getRequiredStringParameter(request, "Source");
            String faqLink = ServletRequestUtils.getRequiredStringParameter(request, "FAQLink");
            
            final Type type = Type.valueOf(source);
            
            switch (type) {
                case Customized : {
                    updateFaqLink(energyCompany, CtiUtilities.STRING_NONE);
                    break;
                }
                case Inherited : {
                    updateFaqLink(energyCompany, ServletUtils.INHERITED_FAQ);
                    break;
                }
                case Link : {
                    updateFaqLink(energyCompany, faqLink);
                    break;
                }
                default : throw new UnsupportedOperationException("Type " + type.name() + " not supported.");
            }

            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer FAQs updated successfully");
        } catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update customer FAQs");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
    private void updateFaqLink(LiteStarsEnergyCompany energyCompany, String faqLink) throws Exception {
        LiteYukonGroup[] custGroups = energyCompany.getResidentialCustomerGroups();
        LiteYukonGroup[] operGroups = energyCompany.getWebClientOperatorGroups();

        for (int i = 0; i < operGroups.length; i++) {
            if (this.authDao.getRolePropValueGroup(operGroups[i], ConsumerInfoRole.WEB_LINK_FAQ, null) != null &&
                    StarsAdminUtil.updateGroupRoleProperty(operGroups[i], ConsumerInfoRole.ROLEID, ConsumerInfoRole.WEB_LINK_FAQ, faqLink))
                ServerUtils.handleDBChange( operGroups[i], DBChangeMsg.CHANGE_TYPE_UPDATE );
        }

        for (int i = 0; i < custGroups.length; i++) {
            if (this.authDao.getRolePropValueGroup(custGroups[i], ResidentialCustomerRole.WEB_LINK_FAQ, null) != null &&
                    StarsAdminUtil.updateGroupRoleProperty(custGroups[i], ResidentialCustomerRole.ROLEID, ResidentialCustomerRole.WEB_LINK_FAQ, faqLink))
                ServerUtils.handleDBChange( custGroups[i], DBChangeMsg.CHANGE_TYPE_UPDATE );
        }
    }

}
