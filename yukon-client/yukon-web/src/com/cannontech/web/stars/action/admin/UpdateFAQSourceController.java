package com.cannontech.web.stars.action.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.stars.LiteCustomerFAQ;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateFAQSourceController extends StarsAdminActionController {

    @SuppressWarnings("unchecked")
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            String source = request.getParameter("Source");
            String faqLink = request.getParameter("FAQLink");
            if (!source.equalsIgnoreCase("Link")) faqLink = CtiUtilities.STRING_NONE;

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

            if (source.equalsIgnoreCase("Inherited")) {
                if (energyCompany.getCustomerFAQs() != null)
                    StarsAdminUtil.deleteAllFAQSubjects( energyCompany, true );
            }
            else if (source.equalsIgnoreCase("Customized")) {
                if (energyCompany.getCustomerFAQs() == null) {
                    // Make a copy of the default the customer FAQs
                    YukonSelectionList dftList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP );
                    YukonSelectionList list = energyCompany.addYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP, dftList, true );

                    List<LiteCustomerFAQ> dftFAQs = null;
                    if (energyCompany.getParent() != null)
                        dftFAQs = energyCompany.getParent().getAllCustomerFAQs();
                    else
                        dftFAQs = this.starsDatabaseCache.getDefaultEnergyCompany().getAllCustomerFAQs();

                    for (int i = 0; i < dftFAQs.size(); i++) {
                        LiteCustomerFAQ dftFAQ = dftFAQs.get(i);

                        com.cannontech.database.db.stars.CustomerFAQ faq = new com.cannontech.database.db.stars.CustomerFAQ();
                        faq.setQuestion( dftFAQ.getQuestion() );
                        faq.setAnswer( dftFAQ.getAnswer() );

                        for (int j = 0; j < dftList.getYukonListEntries().size(); j++) {
                            if (dftFAQ.getSubjectID() == dftList.getYukonListEntries().get(j).getEntryID()) {
                                int subjectID = list.getYukonListEntries().get(j).getEntryID();
                                faq.setSubjectID( new Integer(subjectID) );
                                break;
                            }
                        }

                        faq = (com.cannontech.database.db.stars.CustomerFAQ)
                        Transaction.createTransaction(Transaction.INSERT, faq).execute();

                        LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) StarsLiteFactory.createLite( faq );
                        energyCompany.getCustomerFAQs().add( liteFAQ );
                    }

                    List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants( energyCompany );
                    for (int i = 0; i < descendants.size(); i++) {
                        LiteStarsEnergyCompany company = descendants.get(i);
                        company.updateStarsCustomerFAQs();
                    }
                }
            }

            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer FAQs updated successfully");
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update customer FAQs");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }

}
