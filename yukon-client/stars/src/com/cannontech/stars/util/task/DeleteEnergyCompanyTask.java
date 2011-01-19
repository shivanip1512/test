/*
 * Created on Feb 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.TransactionType;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.web.action.AccountAction;
import com.cannontech.stars.web.util.StarsAdminUtil;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeleteEnergyCompanyTask extends TimeConsumingTask {

    private DBPersistentDao dbPersistentDao;
    private YukonListDao yukonListDao;
    
	private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
	
	int energyCompanyID = StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID;
	
	String currentAction = null;
	int numAccount = 0;
	int numAcctDeleted = 0;
	int numInventory = 0;
	int numInvDeleted = 0;
	int numWorkOrder = 0;
	int numOrderDeleted = 0;
	
	private final Logger log = YukonLogManager.getLogger(getClass());
	
	public DeleteEnergyCompanyTask(int energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	@Override
    public String getProgressMsg() {
		if (energyCompanyID != StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID) {
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
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (energyCompanyID == StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID) {
			status = STATUS_ERROR;
			errorMsg = "Energy company ID is not specified";
			return;
		}
		
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( energyCompanyID );
		
		status = STATUS_RUNNING;
		String dbAlias = CtiUtilities.getDatabaseAlias();
		
		try {
			// Delete operator logins (except the default login)
			currentAction = "Deleting operator logins";
			
			String sql = "SELECT OperatorLoginID FROM EnergyCompanyOperatorLoginList WHERE EnergyCompanyID=" + energyCompanyID;
			SqlStatement stmt = new SqlStatement( sql, dbAlias );
			stmt.execute();
			
			int[] userIDs = new int[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				userIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
			
			for (int i = 0; i < userIDs.length; i++) {
				if (userIDs[i] == energyCompany.getUser().getUserID()) continue;
				
				try {
				    com.cannontech.database.data.user.YukonUser.deleteOperatorLogin(userIDs[i]);
				    ServerUtils.handleDBChange( DaoFactory.getYukonUserDao().getLiteYukonUser(userIDs[i]), DbChangeType.DELETE );
				} catch (UnsupportedOperationException e) {
				    log.error(e);
				}
			}
			
			// Delete all customer accounts
			currentAction = "Deleting customer accounts";
			
			Object[][] accounts = CustomerAccount.getAllCustomerAccounts( energyCompany.getEnergyCompanyId() );
			if (accounts != null) {
				numAccount = accounts.length;
				
				for (int i = 0; i < accounts.length; i++) {
					int accountID = ((Integer) accounts[i][0]).intValue();
					
					currentAction = "Deleting customer account id = " + accountID;
					LiteStarsCustAccountInformation liteAcctInfo = 
					    energyCompany.getCustAccountInformation( accountID, true );
					AccountAction.deleteCustomerAccount( liteAcctInfo, energyCompany );
					
					numAcctDeleted++;
					if (isCanceled) {
						status = STATUS_CANCELED;
						return;
					}
				}
			}
			
			// Delete all inventory
			currentAction = "Deleting inventory";
			
			sql = "SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID=" + energyCompanyID;
			stmt.setSQLString( sql );
			stmt.execute();
			
			int[] invIDs = new int[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				invIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
			numInventory = invIDs.length;
			
			for (int i = 0; i < invIDs.length; i++) {
				currentAction = "Deleting inventory id = " + invIDs[i];
				
				com.cannontech.database.data.stars.hardware.InventoryBase inventory =
						new com.cannontech.database.data.stars.hardware.InventoryBase();
				inventory.setInventoryID( new Integer(invIDs[i]) );
				
                dbPersistentDao.performDBChange(inventory, TransactionType.DELETE);
                
				numInvDeleted++;
				if (isCanceled) {
					status = STATUS_CANCELED;
					return;
				}
			}
			
			// Delete all work orders that don't belong to any account
			currentAction = "Deleting work orders";
			
			sql = "SELECT WorkOrderID FROM ECToWorkOrderMapping WHERE EnergyCompanyID=" + energyCompanyID;
			stmt.setSQLString( sql );
			stmt.execute();
			
			int[] orderIDs = new int[ stmt.getRowCount() ];
			for (int i = 0; i < stmt.getRowCount(); i++)
				orderIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
			numWorkOrder = orderIDs.length;
			
			for (int i = 0; i < orderIDs.length; i++) {
				currentAction = "Deleting work order id = " + orderIDs[i];
				
				com.cannontech.database.data.stars.report.WorkOrderBase order =
						new com.cannontech.database.data.stars.report.WorkOrderBase();
				order.setOrderID( new Integer(orderIDs[i]) );
				
                dbPersistentDao.performDBChange(order, TransactionType.DELETE);
				
				numOrderDeleted++;
				if (isCanceled) {
					status = STATUS_CANCELED;
					return;
				}
			}
			
			// Delete all default thermostat schedules
			currentAction = "Deleting default thermostat schedules";
			
			AccountThermostatScheduleDao accountThermostatScheduleDao = 
                YukonSpringHook.getBean("accountThermostatScheduleDao", AccountThermostatScheduleDao.class);
			List<AccountThermostatSchedule> schedules = accountThermostatScheduleDao.getAllThermostatSchedulesForEC(energyCompany.getEnergyCompanyId());

		    for(AccountThermostatSchedule schedule : schedules){
		        accountThermostatScheduleDao.deleteById(schedule.getAccountThermostatScheduleId());
		    }
			
			// Delete all substations, CANNOT cancel the operation from now on
			currentAction = "Deleting substations";
			
			ECToGenericMapping[] substations = ECToGenericMapping.getAllMappingItems(
					energyCompany.getEnergyCompanyId(), com.cannontech.database.db.stars.Substation.TABLE_NAME );
			if (substations != null) {
				for (int i = 0; i < substations.length; i++) {
					com.cannontech.database.data.stars.Substation substation =
							new com.cannontech.database.data.stars.Substation();
					substation.setSubstationID( substations[i].getItemID() );
					
	                dbPersistentDao.performDBChange(substation, TransactionType.DELETE);
					
				}
			}
			
			// Delete all service companies
			currentAction = "Deleting service companies";
			
			for (int i = 0; i < energyCompany.getServiceCompanies().size(); i++) {
				LiteServiceCompany liteCompany = energyCompany.getServiceCompanies().get(i);
				com.cannontech.database.data.stars.report.ServiceCompany company =
						new com.cannontech.database.data.stars.report.ServiceCompany();
				StarsLiteFactory.setServiceCompany( company, liteCompany );
				
                dbPersistentDao.performDBChange(company, TransactionType.DELETE);

			}
			
			// Delete all appliance categories
			currentAction = "Deleting appliance categories";
			
			for (LiteApplianceCategory liteAppCat : energyCompany.getApplianceCategories()) {
			    // Delete programs
				for (LiteLMProgramWebPublishing liteProg : liteAppCat.getPublishedPrograms()) {
			        com.cannontech.database.data.stars.LMProgramWebPublishing pubProg =
			            new com.cannontech.database.data.stars.LMProgramWebPublishing();
			        pubProg.setProgramID( new Integer(liteProg.getProgramID()) );
			        pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(liteProg.getWebSettingsID()) );
	                dbPersistentDao.performDBChange(pubProg, TransactionType.DELETE);

			        energyCompany.deleteProgram( liteProg.getProgramID() );
			        StarsDatabaseCache.getInstance().deleteWebConfiguration( liteProg.getWebSettingsID() );
				}
    	        com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
    	                new com.cannontech.database.data.stars.appliance.ApplianceCategory();
    	        StarsLiteFactory.setApplianceCategory( appCat.getApplianceCategory(), liteAppCat );
                dbPersistentDao.performDBChange(appCat, TransactionType.DELETE);
    	        
    	        energyCompany.deleteApplianceCategory( liteAppCat.getApplianceCategoryID() );
    	        StarsDatabaseCache.getInstance().deleteWebConfiguration( liteAppCat.getWebConfigurationID() );			
			}
			
			// Delete customer selection lists
			currentAction = "Deleting customer selection lists";
			
			List<YukonSelectionList> energyCompanySelectionLists = 
			    yukonListDao.getSelectionListsByEnergyCompanyId(energyCompany.getEnergyCompanyId());
			for (YukonSelectionList cList : energyCompanySelectionLists) {
				if (cList.getListID() == LiteStarsEnergyCompany.FAKE_LIST_ID) continue;
				
				Integer listID = new Integer( cList.getListID() );
				com.cannontech.database.data.constants.YukonSelectionList list =
						new com.cannontech.database.data.constants.YukonSelectionList();
				list.setListID( listID );
				
                dbPersistentDao.performDBChange(list, TransactionType.DELETE);
			}
			
			// Delete all other generic mappings
			currentAction = "Deleting all other generic mappings";
			
			sql = "DELETE FROM ECToGenericMapping WHERE EnergyCompanyID = " + energyCompanyID;
			stmt.setSQLString( sql );
			stmt.execute();
			
			// Delete membership from the energy company hierarchy
			if (energyCompany.getParent() != null) {
				currentAction = "Deleting membership";
				StarsAdminUtil.removeMember( energyCompany.getParent(), energyCompany.getLiteID() );
			}
			
			// Delete LM groups created for the default route
			if (energyCompany.getDefaultRouteId() >= 0) {
				currentAction = "Deleting LM groups created for the default route";
				StarsAdminUtil.removeDefaultRoute( energyCompany );
			}
			
			// Get the privilege group before the default login is deleted
			LiteYukonGroup liteGroup = energyCompany.getOperatorAdminGroup();
			
			// Delete the energy company!
			currentAction = "Deleting the energy company";
			
			com.cannontech.database.data.company.EnergyCompanyBase ec =
					new com.cannontech.database.data.company.EnergyCompanyBase();
			ec.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
			ec.getEnergyCompany().setPrimaryContactID( new Integer(energyCompany.getPrimaryContactID()) );
			
            dbPersistentDao.performDBChange(ec, TransactionType.DELETE);
			
			StarsDatabaseCache.getInstance().deleteEnergyCompany( energyCompany.getLiteID() );
			ServerUtils.handleDBChange( energyCompany, DbChangeType.DELETE );
			if (energyCompany.getPrimaryContactID() != CtiUtilities.NONE_ZERO_ID) {
			    try {
    				LiteContact liteContact = DaoFactory.getContactDao().getContact( energyCompany.getPrimaryContactID() );
    				ServerUtils.handleDBChange( liteContact, DbChangeType.DELETE );
			    }catch(EmptyResultDataAccessException ignore) {}
			}
			
			// Delete the default operator login
			int defaultUserId = energyCompany.getUser().getUserID();
			if (defaultUserId != com.cannontech.user.UserUtils.USER_ADMIN_ID &&
			        defaultUserId != com.cannontech.user.UserUtils.USER_DEFAULT_ID)
			{
				com.cannontech.database.data.user.YukonUser.deleteOperatorLogin(defaultUserId);
				ServerUtils.handleDBChange( energyCompany.getUser(), DbChangeType.DELETE );
			}
			
			// Delete the privilege group of the default operator login
            if (liteGroup != null) {
                com.cannontech.database.data.user.YukonGroup dftGroup = new com.cannontech.database.data.user.YukonGroup();
                dftGroup.setGroupID(new Integer(liteGroup.getGroupID()));

                dbPersistentDao.performDBChange(dftGroup, TransactionType.DELETE);
                ServerUtils.handleDBChange(liteGroup, DbChangeType.DELETE);
            }
			status = STATUS_FINISHED;
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			
			status = STATUS_ERROR;
			if (currentAction != null)
				errorMsg = currentAction + " failed";
			else
				errorMsg = "Failed to delete the energy company";
		}
	}

	// DI Setters
	@Autowired
	public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
	
	@Autowired
	public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
	
}
