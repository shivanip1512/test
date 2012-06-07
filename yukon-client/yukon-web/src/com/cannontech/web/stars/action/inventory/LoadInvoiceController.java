package com.cannontech.web.stars.action.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.purchasing.Invoice;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.PurchaseBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class LoadInvoiceController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    	
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        List<Invoice> invoices = pBean.getAvailableInvoices();
        Integer idToLoad = ServletRequestUtils.getIntParameter(request, "invoices");
        
        for (final Invoice invoice: invoices) {
        	if (invoice.getInvoiceID().equals(idToLoad)) pBean.setCurrentInvoice(invoice);
        }

        String redirect = request.getContextPath() + "/operator/Hardware/Invoice.jsp";
    	response.sendRedirect(redirect);
    }
	
}
