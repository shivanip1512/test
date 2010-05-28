package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateSelectionListController extends StarsAdminActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            String listName = request.getParameter("ListName");
            YukonSelectionList cList = energyCompany.getYukonSelectionList( listName, false, false );
            
            if (request.getParameter("Inherited") != null) {
                // Nothing need to be done is the list is already an inherited list
                if (cList == null) {
                    String redirect = this.getRedirect(request);
                    response.sendRedirect(redirect);
                    return;
                }
                
                // Try to update the references from the current list to the inherited list
                StarsAdminUtil.updateListEntryReferences( energyCompany, energyCompany.getParent().getYukonSelectionList(listName) );
                
                com.cannontech.database.data.constants.YukonSelectionList list =
                        new com.cannontech.database.data.constants.YukonSelectionList();
                list.setListID( new Integer(cList.getListID()) );
                Transaction.createTransaction( Transaction.DELETE, list ).execute();
                
                energyCompany.deleteYukonSelectionList( cList );
            }
            else {
                String ordering = request.getParameter("Ordering");
                String label = request.getParameter("Label");
                String whereIsList = request.getParameter("WhereIsList");
                
                String[] entryIDs = request.getParameterValues("EntryIDs");
                String[] entryTexts = request.getParameterValues("EntryTexts");
                String[] yukonDefIDs = request.getParameterValues("YukonDefIDs");
                
                Object[][] entryData = null;
                if (entryIDs != null) {
                    entryData = new Object[ entryIDs.length ][];
                    for (int i = 0; i < entryIDs.length; i++) {
                        entryData[i] = new Object[3];
                        entryData[i][0] = Integer.valueOf( entryIDs[i] );
                        entryData[i][1] = entryTexts[i];
                        entryData[i][2] = Integer.valueOf( yukonDefIDs[i] );
                    }
                }
                
                if (cList != null) {
                    // Update yukon selection list
                    com.cannontech.database.db.constants.YukonSelectionList listDB =
                            StarsLiteFactory.createYukonSelectionList( cList );
                    listDB.setOrdering( ordering );
                    listDB.setSelectionLabel( label );
                    listDB.setWhereIsList( whereIsList );
                    listDB.setEnergyCompanyId(energyCompany.getEnergyCompanyID());
                    
                    listDB = (com.cannontech.database.db.constants.YukonSelectionList)
                            Transaction.createTransaction( Transaction.UPDATE, listDB ).execute();
                    
                    StarsLiteFactory.setConstantYukonSelectionList( cList, listDB );
                    StarsAdminUtil.updateYukonListEntries( cList, entryData, energyCompany );
                }
                else {
                    // Create a new selection list
                    com.cannontech.database.data.constants.YukonSelectionList list =
                            new com.cannontech.database.data.constants.YukonSelectionList();
                    com.cannontech.database.db.constants.YukonSelectionList listDB = list.getYukonSelectionList();
                    listDB.setOrdering( ordering );
                    listDB.setSelectionLabel( label );
                    listDB.setWhereIsList( whereIsList );
                    listDB.setListName( listName );
                    listDB.setUserUpdateAvailable( StarsDatabaseCache.getInstance().getDefaultEnergyCompany().getYukonSelectionList(listName).getUserUpdateAvailable() );
                    listDB.setEnergyCompanyId( energyCompany.getEnergyCompanyID() );
                    
                    list = (com.cannontech.database.data.constants.YukonSelectionList)
                            Transaction.createTransaction(Transaction.INSERT, list).execute();
                    listDB = list.getYukonSelectionList();
                    
                    cList = new YukonSelectionList();
                    StarsLiteFactory.setConstantYukonSelectionList( cList, listDB );
                    this.yukonListDao.getYukonSelectionLists().put( listDB.getListID(), cList );
                    energyCompany.addYukonSelectionList(cList);
                    
                    // Mark all entry data as new entries
                    if (entryData != null) {
                        for (int i = 0; i < entryData.length; i++)
                            entryData[i][0] = new Integer(0);
                    }
                    
                    StarsAdminUtil.updateYukonListEntries( cList, entryData, energyCompany );
                    
                    // Try to update the refenreces from the inherited list to the list we just created,
                    // if this step failed, then rollback all changes by deleting the list we just created.
                    try {
                        StarsAdminUtil.updateListEntryReferences( energyCompany, cList );
                    }
                    catch (Exception e) {
                        Transaction.createTransaction( Transaction.DELETE, list ).execute();
                        energyCompany.deleteYukonSelectionList( cList );
                        throw e;
                    }
                }
            }
            
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer selection list updated successfully");
        }
        catch (WebClientException e) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update customer selection list");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
