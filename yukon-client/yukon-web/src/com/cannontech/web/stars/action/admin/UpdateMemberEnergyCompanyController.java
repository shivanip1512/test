package com.cannontech.web.stars.action.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateMemberEnergyCompanyController extends StarsAdminActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        int memberID = ServletRequestUtils.getIntParameter(request, "MemberID");
        int loginID = ServletRequestUtils.getIntParameter(request, "LoginID");
        
        List<Integer> loginIDs = energyCompany.getMemberLoginIDs();
        Integer prevLoginID = null;
        
        for (int i = 0; i < loginIDs.size(); i++) {
            Integer id = loginIDs.get(i);
            LiteYukonUser liteUser = this.yukonUserDao.getLiteYukonUser( id.intValue() );
            if (this.energyCompanyDao.getEnergyCompany( liteUser ).getEnergyCompanyID() == memberID) {
                prevLoginID = id;
                break;
            }
        }
        
        try {
            if (prevLoginID != null) {
            	
            	if (prevLoginID.intValue() != loginID) {
	                if (loginID != -1) {
	                    String sql = "UPDATE ECToGenericMapping SET ItemID = " + loginID +
	                            " WHERE EnergyCompanyID = " + energyCompany.getEnergyCompanyId() +
	                            " AND MappingCategory = 'MemberLogin'";
	                    SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
	                    stmt.execute();
	                }
	                else {
	                    ECToGenericMapping map = new ECToGenericMapping();
	                    map.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
	                    map.setItemID( prevLoginID );
	                    map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
	                    Transaction.createTransaction( Transaction.DELETE, map ).execute();
	                }
            	}
            }
            else {
                if (loginID != -1) {
                
	                ECToGenericMapping map = new ECToGenericMapping();
	                map.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
	                map.setItemID( new Integer(loginID) );
	                map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
	                Transaction.createTransaction( Transaction.INSERT, map ).execute();
                }
            }
            
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Member setting updated successfully.");
           
            energyCompany.clearHierarchy();
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update member login.");
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
