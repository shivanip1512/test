/*
 * Created on Feb 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteCustomerFAQ;
import com.cannontech.database.data.lite.stars.LiteInterviewQuestion;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.DeleteCustAccountAction;
import com.cannontech.stars.web.servlet.SOAPServer;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeleteEnergyCompanyTask implements TimeConsumingTask {

	private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
	
	int status = STATUS_NOT_INIT;
	boolean isCanceled = false;
	String errorMsg = null;
	
	StarsYukonUser user = null;
	
	String currentAction = null;
	int numAccount = 0;
	int numAcctDeleted = 0;
	int numInventory = 0;
	int numInvDeleted = 0;
	int numWorkOrder = 0;
	int numOrderDeleted = 0;
	
	public DeleteEnergyCompanyTask(StarsYukonUser user) {
		this.user = user;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getStatus()
	 */
	public int getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#cancel()
	 */
	public void cancel() {
		isCanceled = true;
		status = STATUS_CANCELING;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (user != null) {
			String msg = "";
			
			if (numAccount > 0) {
				if (numAcctDeleted < numAccount)
					return numAcctDeleted + " of " + numAccount + " customer accounts deleted";
				msg += numAcctDeleted + " customer accounts deleted successfully" + LINE_SEPARATOR;
			}
			
			if (numInventory > 0) {
				if (numInvDeleted < numInventory) {
					msg += numInvDeleted + " of " + numInventory + " inventory deleted";
					return msg;
				}
				msg += numInvDeleted + " inventory deleted successfully" + LINE_SEPARATOR;
			}
			
			if (numWorkOrder > 0) {
				if (numOrderDeleted < numWorkOrder) {
					msg += numOrderDeleted + " of " + numWorkOrder + " work orders deleted";
					return msg;
				}
				msg += numOrderDeleted + " work orders deleted successfully" + LINE_SEPARATOR;
			}
			
			if (currentAction != null) msg += currentAction + " ...";
			return msg;
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getErrorMsg()
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		if (user == null) {
			status = STATUS_ERROR;
			errorMsg = "User cannot be null";
		}
		else {
			status = STATUS_RUNNING;
			java.sql.Connection conn = null;
			
			try {
				conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				
				// Delete all customer accounts
				currentAction = "Deleting customer accounts";
				
				int[] accountIDs = CustomerAccount.searchByAccountNumber(
						energyCompany.getEnergyCompanyID(), "%" );
				
				if (accountIDs != null) {
					numAccount = accountIDs.length;
					
					for (int i = 0; i < accountIDs.length; i++) {
						currentAction = "Deleting customer account id = " + accountIDs[i];
						LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[i], true );
						DeleteCustAccountAction.deleteCustomerAccount( liteAcctInfo, energyCompany );
						
						numAcctDeleted++;
						if (isCanceled) {
							status = STATUS_CANCELED;
							return;
						}
					}
				}
				
				// Delete all inventory
				currentAction = "Deleting inventory";
				
				String sql = "SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID = ?";
				java.sql.PreparedStatement stmt = conn.prepareStatement( sql );
				stmt.setInt(1, user.getEnergyCompanyID());
				java.sql.ResultSet rset = stmt.executeQuery();
				
				ArrayList invIDs = new ArrayList();
				while (rset.next())
					invIDs.add( new Integer(rset.getInt(1)) );
				numInventory = invIDs.size();
				
				rset.close();
				stmt.close();
				
				for (int i = 0; i < invIDs.size(); i++) {
					int inventoryID = ((Integer) invIDs.get(i)).intValue();
					currentAction = "Deleting inventory id = " + inventoryID;
					
					com.cannontech.database.data.stars.hardware.InventoryBase inventory =
							new com.cannontech.database.data.stars.hardware.InventoryBase();
					inventory.setInventoryID( new Integer(inventoryID) );
					inventory.setDbConnection( conn );
					inventory.delete();
					
					numInvDeleted++;
					if (isCanceled) {
						status = STATUS_CANCELED;
						energyCompany.clear();
						return;
					}
				}
				
				// Delete all work orders that don't belong to any account
				currentAction = "Deleting work orders";
				
				sql = "SELECT WorkOrderID FROM ECToWorkOrderMapping WHERE EnergyCompanyID = ?";
				stmt = conn.prepareStatement( sql );
				stmt.setInt(1, user.getEnergyCompanyID());
				rset = stmt.executeQuery();
				
				ArrayList orderIDs = new ArrayList();
				while (rset.next())
					orderIDs.add( new Integer(rset.getInt(1)) );
				numWorkOrder = orderIDs.size();
				
				rset.close();
				stmt.close();
				
				for (int i = 0; i < orderIDs.size(); i++) {
					int orderID = ((Integer) orderIDs.get(i)).intValue();
					currentAction = "Deleting work order id = " + orderID;
					
					com.cannontech.database.data.stars.report.WorkOrderBase order =
							new com.cannontech.database.data.stars.report.WorkOrderBase();
					order.setOrderID( new Integer(orderID) );
					order.setDbConnection( conn );
					order.delete();
					
					numOrderDeleted++;
					if (isCanceled) {
						status = STATUS_CANCELED;
						energyCompany.clear();
						return;
					}
				}
				
				// Delete all substations, CANNOT cancel the operation from now on
				currentAction = "Deleting substations";
				
				ECToGenericMapping[] substations = ECToGenericMapping.getAllMappingItems(
						energyCompany.getEnergyCompanyID(), com.cannontech.database.db.stars.Substation.TABLE_NAME );
				if (substations != null) {
					for (int i = 0; i < substations.length; i++) {
						com.cannontech.database.data.stars.Substation substation =
								new com.cannontech.database.data.stars.Substation();
						substation.setSubstationID( substations[i].getItemID() );
						substation.setDbConnection( conn );
						substation.delete();
					}
				}
				
				// Delete all service companies
				currentAction = "Deleting service companies";
				
				for (int i = 0; i < energyCompany.getAllServiceCompanies().size(); i++) {
					LiteServiceCompany liteCompany = (LiteServiceCompany) energyCompany.getAllServiceCompanies().get(i);
					com.cannontech.database.data.stars.report.ServiceCompany company =
							new com.cannontech.database.data.stars.report.ServiceCompany();
					StarsLiteFactory.setServiceCompany( company.getServiceCompany(), liteCompany );
					company.setDbConnection( conn );
					company.delete();
				}
				
				// Delete all appliance categories
				currentAction = "Deleting appliance categories";
				
				for (int i = 0; i < energyCompany.getAllApplianceCategories().size(); i++) {
					LiteApplianceCategory liteAppCat = (LiteApplianceCategory) energyCompany.getAllApplianceCategories().get(i);
					
					com.cannontech.database.db.stars.LMProgramWebPublishing.deleteAllLMProgramWebPublishing(
							new Integer(liteAppCat.getApplianceCategoryID()), conn );
					for (int j = 0; j < liteAppCat.getPublishedPrograms().length; j++) {
						int configID = liteAppCat.getPublishedPrograms()[j].getWebSettingsID();
						com.cannontech.database.db.web.YukonWebConfiguration cfg =
								new com.cannontech.database.db.web.YukonWebConfiguration();
						cfg.setConfigurationID( new Integer(configID) );
						cfg.setDbConnection( conn );
						cfg.delete();
					}
					
					com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
							new com.cannontech.database.data.stars.appliance.ApplianceCategory();
					StarsLiteFactory.setApplianceCategory( appCat.getApplianceCategory(), liteAppCat );
					appCat.setDbConnection( conn );
					appCat.delete();
				}
				
				// Delete all interview questions
				currentAction = "Deleting interview questions";
				
				for (int i = 0; i < energyCompany.getAllInterviewQuestions().size(); i++) {
					LiteInterviewQuestion liteQuestion = (LiteInterviewQuestion) energyCompany.getAllInterviewQuestions().get(i);
					com.cannontech.database.data.stars.InterviewQuestion question =
							new com.cannontech.database.data.stars.InterviewQuestion();
					question.setQuestionID( new Integer(liteQuestion.getQuestionID()) );
					question.setDbConnection( conn );
					question.delete();
				}
				
				// Delete all customer FAQs
				currentAction = "Deleting customer FAQs";
				
				for (int i = 0; i < energyCompany.getAllCustomerFAQs().size(); i++) {
					LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) energyCompany.getAllCustomerFAQs().get(i);
					com.cannontech.database.db.stars.CustomerFAQ faq =
							new com.cannontech.database.db.stars.CustomerFAQ();
					faq.setQuestionID( new Integer(liteFAQ.getQuestionID()) );
					faq.setDbConnection( conn );
					faq.delete();
				}
				
				// Delete customer selection lists
				currentAction = "Deleting customer selection lists";
				
				for (int i = 0; i < energyCompany.getAllSelectionLists().size(); i++) {
					YukonSelectionList cList = (YukonSelectionList) energyCompany.getAllSelectionLists().get(i);
					if (cList.getListID() == LiteStarsEnergyCompany.FAKE_LIST_ID) continue;
					
					Integer listID = new Integer( cList.getListID() );
					com.cannontech.database.data.constants.YukonSelectionList list =
							new com.cannontech.database.data.constants.YukonSelectionList();
					list.setListID( listID );
					list.setDbConnection( conn );
					list.delete();
					
					YukonListFuncs.getYukonSelectionLists().remove( listID );
					for (int j = 0; j < cList.getYukonListEntries().size(); j++) {
						YukonListEntry cEntry = (YukonListEntry) cList.getYukonListEntries().get(j);
						YukonListFuncs.getYukonListEntries().remove( new Integer(cEntry.getEntryID()) );
					}
				}
				
				// Delete operator logins (except the default login)
				currentAction = "Deleting operator logins";
				
				sql = "SELECT OperatorLoginID FROM EnergyCompanyOperatorLoginList WHERE EnergyCompanyID = ?";
				stmt = conn.prepareStatement( sql );
				stmt.setInt(1, energyCompany.getLiteID());
				rset = stmt.executeQuery();
				
				ArrayList userIDs = new ArrayList();
				while (rset.next())
					userIDs.add( new Integer(rset.getInt(1)) );
				
				rset.close();
				stmt.close();
				
				for (int i = 0; i < userIDs.size(); i++) {
					int userID = ((Integer) userIDs.get(i)).intValue();
					if (userID == energyCompany.getUserID()) continue;
					
					com.cannontech.database.data.user.YukonUser yukonUser =
							new com.cannontech.database.data.user.YukonUser();
					yukonUser.setUserID( new Integer(userID) );
					yukonUser.setDbConnection( conn );
					yukonUser.deleteOperatorLogin();
					
					ServerUtils.handleDBChange( YukonUserFuncs.getLiteYukonUser(userID), DBChangeMsg.CHANGE_TYPE_DELETE );
				}
				
				// Delete the energy company!
				currentAction = "Deleting the energy company";
				
				com.cannontech.database.data.company.EnergyCompanyBase ec =
						new com.cannontech.database.data.company.EnergyCompanyBase();
				ec.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				ec.getEnergyCompany().setPrimaryContactID( new Integer(energyCompany.getPrimaryContactID()) );
				ec.setDbConnection( conn );
				ec.delete();
				
				SOAPServer.deleteEnergyCompany( energyCompany.getLiteID() );
				ServerUtils.handleDBChange( energyCompany, DBChangeMsg.CHANGE_TYPE_DELETE );
				if (energyCompany.getPrimaryContactID() != CtiUtilities.NONE_ID) {
					LiteContact liteContact = ContactFuncs.getContact( energyCompany.getPrimaryContactID() );
					ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_DELETE );
				}
				
				if (energyCompany.getUserID() != com.cannontech.user.UserUtils.USER_YUKON_ID) {
					com.cannontech.database.data.user.YukonUser yukonUser =
							new com.cannontech.database.data.user.YukonUser();
					yukonUser.setUserID( new Integer(energyCompany.getUserID()) );
					yukonUser.setDbConnection( conn );
					yukonUser.deleteOperatorLogin();
					
					ServerUtils.handleDBChange( YukonUserFuncs.getLiteYukonUser(energyCompany.getUserID()), DBChangeMsg.CHANGE_TYPE_DELETE );
				}
				
				status = STATUS_FINISHED;
			}
			catch (Exception e) {
				e.printStackTrace();
				
				status = STATUS_ERROR;
				if (currentAction != null)
					errorMsg = currentAction + " failed";
				else
					errorMsg = "Failed to delete the energy company";
			}
			finally {
				try {
					if (conn != null) conn.close();
				}
				catch (java.sql.SQLException e) {}
			}
		}
	}

}
