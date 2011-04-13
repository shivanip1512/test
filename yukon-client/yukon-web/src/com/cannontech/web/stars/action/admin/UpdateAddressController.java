package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.stars.xml.serialize.StarsCustomerAddress;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateAddressController extends StarsAdminActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        int addressID = ServletRequestUtils.getIntParameter(request, "AddressID");
        boolean newAddress = (addressID <= 0);
        String referer = ServletRequestUtils.getStringParameter(request, "REFERER");

        try {

            if (referer.equalsIgnoreCase("EnergyCompany.jsp")) {
                StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute( StarsAdminUtil.ENERGY_COMPANY_TEMP );
                StarsCustomerAddress starsAddr = ecTemp.getCompanyAddress();
                processAddress(request, response, starsAddr, newAddress, energyCompany);
            }

        } catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update address information");
            referer = ServletUtil.createSafeRedirectUrl(request, referer);
            response.sendRedirect(referer);
            return;
        }

        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
    private void processAddress(final HttpServletRequest request, final HttpServletResponse response, final StarsCustomerAddress starsAddr, final boolean newAddress,  
            final LiteStarsEnergyCompany energyCompany) throws TransactionException {
        
        HttpSession session = request.getSession(false);
        
        if (newAddress) {
            starsAddr.setStreetAddr1( request.getParameter("StreetAddr1") );
            starsAddr.setStreetAddr2( request.getParameter("StreetAddr2") );
            starsAddr.setCity( request.getParameter("City") );
            starsAddr.setState( request.getParameter("State") );
            starsAddr.setZip( request.getParameter("Zip") );
            starsAddr.setCounty( request.getParameter("County") );
            
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Address information created, you must submit this page to save it");
        } else {
            LiteAddress liteAddr = energyCompany.getAddress( starsAddr.getAddressID() );
            
            com.cannontech.database.db.customer.Address addr =
                    (com.cannontech.database.db.customer.Address) StarsLiteFactory.createDBPersistent( liteAddr );
            addr.setLocationAddress1( request.getParameter("StreetAddr1") );
            addr.setLocationAddress2( request.getParameter("StreetAddr2") );
            addr.setCityName( request.getParameter("City") );
            addr.setStateCode( request.getParameter("State") );
            addr.setZipCode( request.getParameter("Zip") );
            addr.setCounty( request.getParameter("County") );
            
            addr = (com.cannontech.database.db.customer.Address)
                    Transaction.createTransaction( Transaction.UPDATE, addr ).execute();
            
            StarsLiteFactory.setLiteAddress( liteAddr, addr );
            StarsLiteFactory.setStarsCustomerAddress( starsAddr, liteAddr );
            
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Address information updated successfully");
        }    
    }

}
