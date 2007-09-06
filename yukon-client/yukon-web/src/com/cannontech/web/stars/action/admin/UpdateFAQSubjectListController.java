package com.cannontech.web.stars.action.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateFAQSubjectListController extends StarsAdminActionController {

    @SuppressWarnings("unchecked")
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            String[] subjectIDs = request.getParameterValues("SubjectIDs");
            ArrayList newSubjects = new ArrayList();

            for (int i = 0; i < subjectIDs.length; i++) {
                int subjectID = Integer.parseInt( subjectIDs[i] );
                YukonListEntry subject = this.yukonListDao.getYukonListEntry( subjectID );
                subject.setEntryOrder( i+1 );

                com.cannontech.database.db.constants.YukonListEntry entry =
                    StarsLiteFactory.createYukonListEntry( subject );
                Transaction.createTransaction( Transaction.UPDATE, entry ).execute();

                newSubjects.add( subject );
            }

            energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP ).setYukonListEntries( newSubjects );

            List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants( energyCompany );
            for (int i = 0; i < descendants.size(); i++) {
                LiteStarsEnergyCompany company = descendants.get(i);
                company.updateStarsCustomerFAQs();
            }

            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "FAQ subjects updated successfully");
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update FAQ subjects");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }        
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }

}
