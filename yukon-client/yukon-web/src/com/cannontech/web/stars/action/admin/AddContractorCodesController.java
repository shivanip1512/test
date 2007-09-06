package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.report.ServiceCompanyDesignationCode;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class AddContractorCodesController extends StarsAdminActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            StarsServiceCompany scTemp = (StarsServiceCompany) session.getAttribute( StarsAdminUtil.SERVICE_COMPANY_TEMP );
            
            String newCodeString = ServletRequestUtils.getStringParameter(request, "NewCode1");
            insertContractorCode(newCodeString, scTemp.getCompanyID(), 5);
            
            newCodeString = ServletRequestUtils.getStringParameter(request, "NewCode2");
            insertContractorCode(newCodeString, scTemp.getCompanyID(), 5);
            
            newCodeString = ServletRequestUtils.getStringParameter(request, "NewCode3");
            insertContractorCode(newCodeString, scTemp.getCompanyID(), 5);
            
            newCodeString = ServletRequestUtils.getStringParameter(request, "NewCode4");
            insertContractorCode(newCodeString, scTemp.getCompanyID(), 5);
            
            newCodeString = ServletRequestUtils.getStringParameter(request, "NewCode5");
            insertContractorCode(newCodeString, scTemp.getCompanyID(), 5);
            
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Zip codes successfully assigned to contractor.");
            
        } catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to add new zip codes to contractor.");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
    private void insertContractorCode(final String code, final int servCompanyID, final int minLength) 
        throws TransactionException {
        if(code.length() >= minLength) {
            ServiceCompanyDesignationCode newCode = new ServiceCompanyDesignationCode(code, new Integer(servCompanyID));
            Transaction.createTransaction(Transaction.INSERT, newCode).execute();
            ServerUtils.handleDBChange(StarsLiteFactory.createLite(newCode), DBChangeMsg.CHANGE_TYPE_ADD);
        }
    }
    
}
