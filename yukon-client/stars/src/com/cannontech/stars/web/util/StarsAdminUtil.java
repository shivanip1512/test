/*
 * Created on Nov 9, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMGroupExpressCom;
import com.cannontech.database.data.device.lm.MacroGroup;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.LiteSubstation;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.db.macro.GenericMacro;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.db.user.YukonGroupRole;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.InstallationCompany;
import com.cannontech.stars.xml.serialize.ServiceCompany;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsServiceRequest;
import com.cannontech.stars.xml.serialize.Substation;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StarsAdminUtil {

	public static final String ENERGY_COMPANY_TEMP = "ENERGY_COMPANY_TEMP";
	public static final String SERVICE_COMPANY_TEMP = "SERVICE_COMPANY_TEMP";
	
	public static final String FIRST_TIME = "FIRST_TIME";
	
	public static void updateDefaultRoute(LiteStarsEnergyCompany energyCompany, int routeID) throws Exception {
		if (energyCompany.getDefaultRouteID() != routeID) {
			if (energyCompany.getDefaultRouteID() == LiteStarsEnergyCompany.INVALID_ROUTE_ID) {
				// Assign the default route to the energy company
				LMGroupExpressCom grpDftRoute = (LMGroupExpressCom) LMFactory.createLoadManagement( DeviceTypes.LM_GROUP_EXPRESSCOMM );
				grpDftRoute.setPAOName( energyCompany.getName() + " Default Route" );
				grpDftRoute.setRouteID( new Integer(routeID) );
				grpDftRoute = (LMGroupExpressCom) Transaction.createTransaction( Transaction.INSERT, grpDftRoute ).execute();
				ServerUtils.handleDBChangeMsg( grpDftRoute.getDBChangeMsgs(DBChangeMsg.CHANGE_TYPE_ADD)[0] );
				
				MacroGroup grpSerial = (MacroGroup) LMFactory.createLoadManagement( DeviceTypes.MACRO_GROUP );
				grpSerial.setPAOName( energyCompany.getName() + " Serial Group" );
				GenericMacro macro = new GenericMacro();
				macro.setChildID( grpDftRoute.getPAObjectID() );
				macro.setChildOrder( new Integer(0) );
				macro.setMacroType( MacroTypes.GROUP );
				grpSerial.getMacroGroupVector().add( macro );
				grpSerial = (MacroGroup) Transaction.createTransaction( Transaction.INSERT, grpSerial ).execute();
				ServerUtils.handleDBChangeMsg( grpSerial.getDBChangeMsgs(DBChangeMsg.CHANGE_TYPE_ADD)[0] );
				
				String sql = "INSERT INTO UserPaoOwner VALUES (" + energyCompany.getUserID() + ", " + grpSerial.getPAObjectID() + ")";
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
			}
			else if (routeID > 0 || energyCompany.getDefaultRouteID() > 0) {
				if (routeID < 0) routeID = 0;
				
				String sql = "SELECT exc.LMGroupID FROM LMGroupExpressCom exc, GenericMacro macro, UserPaoOwner us " +
						"WHERE us.UserID = " + energyCompany.getUserID() +
						" AND us.PaoID = macro.OwnerID " +
						"AND macro.MacroType = '" + MacroTypes.GROUP +
						"' AND macro.ChildID = exc.LMGroupID AND exc.SerialNumber = '0'";
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				if (stmt.getRowCount() == 0)
					throw new Exception( "Not able to find the default route group, sql = \"" + sql + "\"" );
				int groupID = ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue();
				
				LMGroupExpressCom group = (LMGroupExpressCom)LMFactory.createLoadManagement( DeviceTypes.LM_GROUP_EXPRESSCOMM );
				group.setLMGroupID( new Integer(groupID) );
				group = (LMGroupExpressCom) Transaction.createTransaction( Transaction.RETRIEVE, group ).execute();
				
				com.cannontech.database.db.device.lm.LMGroupExpressCom grpDB = group.getLMGroupExpressComm();
				grpDB.setRouteID( new Integer(routeID) );
				Transaction.createTransaction( Transaction.UPDATE, grpDB ).execute();
				ServerUtils.handleDBChangeMsg( group.getDBChangeMsgs(DBChangeMsg.CHANGE_TYPE_UPDATE)[0] );
			}
			
			energyCompany.setDefaultRouteID( routeID );
		}
	}
	
	public static void removeDefaultRoute(LiteStarsEnergyCompany energyCompany) throws Exception {
		String sql = "SELECT exc.LMGroupID, us.PaoID FROM LMGroupExpressCom exc, GenericMacro macro, UserPaoOwner us " +
				"WHERE us.UserID = " + energyCompany.getUserID() + " AND us.PaoID = macro.OwnerID " +
				"AND macro.MacroType = '" + MacroTypes.GROUP + "' AND macro.ChildID = exc.LMGroupID AND exc.SerialNumber = '0'";
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		stmt.execute();
		
		if (stmt.getRowCount() > 0) {
			int dftRtGrpID = ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue();
			int serialGrpID = ((java.math.BigDecimal) stmt.getRow(0)[1]).intValue();
			
			sql = "DELETE FROM UserPaoOwner WHERE PaoID = " + serialGrpID;
			stmt.setSQLString( sql );
			stmt.execute();
			
			MacroGroup grpSerial = (MacroGroup) LMFactory.createLoadManagement( DeviceTypes.MACRO_GROUP );
			grpSerial.setDeviceID( new Integer(serialGrpID) );
			Transaction.createTransaction( Transaction.DELETE, grpSerial ).execute();
			ServerUtils.handleDBChangeMsg( grpSerial.getDBChangeMsgs(DBChangeMsg.CHANGE_TYPE_DELETE)[0] );
			
			LMGroupExpressCom grpDftRoute = (LMGroupExpressCom) LMFactory.createLoadManagement( DeviceTypes.LM_GROUP_EXPRESSCOMM );
			grpDftRoute.setLMGroupID( new Integer(dftRtGrpID) );
			Transaction.createTransaction( Transaction.DELETE, grpDftRoute ).execute();
			ServerUtils.handleDBChangeMsg( grpDftRoute.getDBChangeMsgs(DBChangeMsg.CHANGE_TYPE_DELETE)[0] );
		}
	}
	
	public static boolean updateGroupRoleProperty(LiteYukonGroup group, int roleID, int rolePropertyID, String newVal) throws Exception {
		String oldVal = AuthFuncs.getRolePropValueGroup( group, rolePropertyID, null );
		if (oldVal != null && oldVal.equals(newVal)) return false;
		
		if (oldVal != null) {
			String sql = "UPDATE YukonGroupRole SET Value = '" + newVal + "'" +
					" WHERE GroupID = " + group.getGroupID() +
					" AND RoleID = " + roleID +
					" AND RolePropertyID = " + rolePropertyID;
			new SqlStatement( sql, CtiUtilities.getDatabaseAlias() ).execute();
		}
		else {
			YukonGroupRole groupRole = new YukonGroupRole();
			groupRole.setGroupID( new Integer(group.getGroupID()) );
			groupRole.setRoleID( new Integer(roleID) );
			groupRole.setRolePropertyID( new Integer(rolePropertyID) );
			groupRole.setValue( newVal );
			Transaction.createTransaction( Transaction.INSERT, groupRole ).execute();
		}
		
		return true;
	}
	
	public static LiteApplianceCategory createApplianceCategory(String appCatName, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		com.cannontech.database.db.web.YukonWebConfiguration config =
				new com.cannontech.database.db.web.YukonWebConfiguration();
		config.setLogoLocation( "yukon/Icons/Load.gif" );
		config.setAlternateDisplayName( appCatName );
		config.setDescription( "" );
		config.setURL( "" );
		
		int dftCatID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DEFAULT).getEntryID();
		
		com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
				new com.cannontech.database.data.stars.appliance.ApplianceCategory();
		com.cannontech.database.db.stars.appliance.ApplianceCategory appCatDB = appCat.getApplianceCategory();
		
		appCatDB.setCategoryID( new Integer(dftCatID) );
		appCatDB.setDescription( appCatName );
		appCat.setWebConfiguration( config );
		appCat.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		
		appCat = (com.cannontech.database.data.stars.appliance.ApplianceCategory)
				Transaction.createTransaction( Transaction.INSERT, appCat ).execute();
		
		LiteApplianceCategory liteAppCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCat.getApplianceCategory() );
		energyCompany.addApplianceCategory( liteAppCat );
		LiteWebConfiguration liteConfig = (LiteWebConfiguration) StarsLiteFactory.createLite( appCat.getWebConfiguration() );
		StarsDatabaseCache.getInstance().addWebConfiguration( liteConfig );
		
		StarsApplianceCategory starsAppCat = StarsLiteFactory.createStarsApplianceCategory( liteAppCat, energyCompany );
		energyCompany.getStarsEnrollmentPrograms().addStarsApplianceCategory( starsAppCat );
		
		return liteAppCat;
	}
	
	public static void deleteApplianceCategory(int appCatID, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		LiteApplianceCategory liteAppCat = energyCompany.getApplianceCategory(appCatID);
		
		int[] programIDs = new int[ liteAppCat.getPublishedPrograms().size() ];
		for (int i = 0; i < liteAppCat.getPublishedPrograms().size(); i++)
			programIDs[i] = ((LiteLMProgramWebPublishing) liteAppCat.getPublishedPrograms().get(i)).getProgramID();
		Arrays.sort( programIDs );
		
		// Delete all program events in the category
		for (int j = 0; j < programIDs.length; j++)
			com.cannontech.database.data.stars.event.LMProgramEvent.deleteAllLMProgramEvents( programIDs[j] );
		
		// Delete all appliances in the category
		com.cannontech.database.db.stars.appliance.ApplianceBase.deleteAppliancesByCategory( appCatID );
		
		ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
			
			ArrayList accounts = company.getAllCustAccountInformation();
			for (int j = 0; j < accounts.size(); j++) {
				LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) accounts.get(j);
				
				Iterator appIt = liteAcctInfo.getAppliances().iterator();
				while (appIt.hasNext()) {
					LiteStarsAppliance liteApp = (LiteStarsAppliance) appIt.next();
					if (liteApp.getApplianceCategoryID() == liteAppCat.getApplianceCategoryID()) {
						appIt.remove();
					}
				}
				
				Iterator progIt = liteAcctInfo.getPrograms().iterator();
				while (progIt.hasNext()) {
					int progID = ((LiteStarsLMProgram) progIt.next()).getProgramID();
					if (Arrays.binarySearch(programIDs, progID) >= 0)
						progIt.remove();
				}
				
				Iterator it = liteAcctInfo.getProgramHistory().iterator();
				while (it.hasNext()) {
					int progID = ((LiteLMProgramEvent) it.next()).getProgramID();
					if (Arrays.binarySearch(programIDs, progID) >= 0)
						it.remove();
				}
				
				company.updateStarsCustAccountInformation( liteAcctInfo );
			}
		}
		
		com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
				new com.cannontech.database.data.stars.appliance.ApplianceCategory();
		StarsLiteFactory.setApplianceCategory( appCat.getApplianceCategory(), liteAppCat );
		Transaction.createTransaction( Transaction.DELETE, appCat ).execute();
		
		energyCompany.deleteApplianceCategory( liteAppCat.getApplianceCategoryID() );
		StarsDatabaseCache.getInstance().deleteWebConfiguration( liteAppCat.getWebConfigurationID() );
		
		for (int j = 0; j < liteAppCat.getPublishedPrograms().size(); j++) {
			LiteLMProgramWebPublishing liteProg = (LiteLMProgramWebPublishing) liteAppCat.getPublishedPrograms().get(j);
			
			com.cannontech.database.db.web.YukonWebConfiguration cfg =
					new com.cannontech.database.db.web.YukonWebConfiguration();
			cfg.setConfigurationID( new Integer(liteProg.getWebSettingsID()) );
			Transaction.createTransaction( Transaction.DELETE, cfg ).execute();
			
			StarsDatabaseCache.getInstance().deleteWebConfiguration( liteProg.getWebSettingsID() );
		}
		
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
			company.updateStarsEnrollmentPrograms();
		}
	}
	
	public static void deleteAllApplianceCategories(LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		ArrayList appCats = energyCompany.getApplianceCategories();
		for (int i = appCats.size() - 1; i >= 0; i--) {
			LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCats.get(i);
			deleteApplianceCategory( liteAppCat.getApplianceCategoryID(), energyCompany );
		}
	}
	
	public static LiteServiceCompany createServiceCompany(String companyName, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		com.cannontech.database.data.stars.report.ServiceCompany company =
				new com.cannontech.database.data.stars.report.ServiceCompany();
		com.cannontech.database.db.stars.report.ServiceCompany companyDB = company.getServiceCompany();
		
		companyDB.setCompanyName( companyName );
		company.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		
		company = (com.cannontech.database.data.stars.report.ServiceCompany)
				Transaction.createTransaction( Transaction.INSERT, company ).execute();
		
		LiteContact liteContact = (LiteContact) StarsLiteFactory.createLite(company.getPrimaryContact());
		ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_ADD );
		
		LiteServiceCompany liteCompany = (LiteServiceCompany) StarsLiteFactory.createLite( companyDB );
		energyCompany.addServiceCompany( liteCompany );
		
		ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany ec = (LiteStarsEnergyCompany) descendants.get(i);
			ec.updateStarsServiceCompanies();
		}
		
		return liteCompany;
	}
	
	public static void deleteServiceCompany(int companyID, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		// set InstallationCompanyID = 0 for all inventory assigned to this service company
		com.cannontech.database.db.stars.hardware.InventoryBase.resetInstallationCompany( companyID );
		
		ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
			
			ArrayList inventory = company.getAllInventory();
			for (int j = 0; j < inventory.size(); j++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(j);
				if (liteInv.getInstallationCompanyID() == companyID) {
					liteInv.setInstallationCompanyID(0);
					
					if (liteInv.getAccountID() > 0) {
						StarsCustAccountInformation starsAcctInfo = company.getStarsCustAccountInformation( liteInv.getAccountID(), false );
						if (starsAcctInfo != null) {
							for (int k = 0; k < starsAcctInfo.getStarsInventories().getStarsInventoryCount(); k++) {
								StarsInventory starsInv = starsAcctInfo.getStarsInventories().getStarsInventory(k);
								if (starsInv.getInventoryID() == liteInv.getInventoryID()) {
									starsInv.setInstallationCompany( (InstallationCompany)StarsFactory.newEmptyStarsCustListEntry(InstallationCompany.class) );
									break;
								}
							}
						}
					}
				}
			}
			
			// set ServiceCompanyID = 0 for all work orders assigned to this service company
			com.cannontech.database.db.stars.report.WorkOrderBase.resetServiceCompany( companyID );
			
			ArrayList orders = company.getAllWorkOrders();
			for (int j = 0; j < orders.size(); j++) {
				LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) orders.get(j);
				if (liteOrder.getServiceCompanyID() == companyID) {
					liteOrder.setServiceCompanyID(0);
					
					if (liteOrder.getAccountID() > 0) {
						StarsCustAccountInformation starsAcctInfo = company.getStarsCustAccountInformation( liteOrder.getAccountID(), false );
						if (starsAcctInfo != null) {
							for (int k = 0; k < starsAcctInfo.getStarsServiceRequestHistory().getStarsServiceRequestCount(); k++) {
								StarsServiceRequest starsReq = starsAcctInfo.getStarsServiceRequestHistory().getStarsServiceRequest(k);
								if (starsReq.getOrderID() == liteOrder.getOrderID()) {
									starsReq.setServiceCompany( (ServiceCompany)StarsFactory.newEmptyStarsCustListEntry(ServiceCompany.class) );
									break;
								}
							}
						}
					}
				}
			}
		}
		
		LiteServiceCompany liteCompany = energyCompany.getServiceCompany( companyID );
		
		com.cannontech.database.data.stars.report.ServiceCompany servCompany =
				new com.cannontech.database.data.stars.report.ServiceCompany();
		StarsLiteFactory.setServiceCompany( servCompany.getServiceCompany(), liteCompany );
		
		Transaction.createTransaction( Transaction.DELETE, servCompany ).execute();
		
		energyCompany.deleteAddress( liteCompany.getAddressID() );
		energyCompany.deleteServiceCompany( companyID );
		
		LiteContact liteContact = ContactFuncs.getContact( liteCompany.getPrimaryContactID() );
		ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_DELETE );
		
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
			company.updateStarsServiceCompanies();
		}
	}
	
	public static void deleteAllServiceCompanies(LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		ArrayList companies = energyCompany.getServiceCompanies();
		for (int i = companies.size() - 1; i >= 0; i--) {
			LiteServiceCompany liteCompany = (LiteServiceCompany) companies.get(i);
			deleteServiceCompany( liteCompany.getCompanyID(), energyCompany );
		}
	}
	
	public static LiteSubstation createSubstation(String subName, int routeID, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		com.cannontech.database.data.stars.Substation sub = new com.cannontech.database.data.stars.Substation();
		com.cannontech.database.db.stars.Substation subDB = sub.getSubstation();
		
		subDB.setSubstationName( subName );
		subDB.setRouteID( new Integer(routeID) );
		sub.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		
		sub = (com.cannontech.database.data.stars.Substation)
				Transaction.createTransaction( Transaction.INSERT, sub ).execute();
		
		LiteSubstation liteSub = (LiteSubstation) StarsLiteFactory.createLite( subDB );
		energyCompany.addSubstation( liteSub );
		
		ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany ec = (LiteStarsEnergyCompany) descendants.get(i);
			ec.updateStarsSubstations();
		}
		
		return liteSub;
	}
	
	public static void deleteSubstation(int subID, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		// set SubstationID = 0 for all sites using this substation
		com.cannontech.database.db.stars.customer.SiteInformation.resetSubstation( subID );
		
		ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
			
			ArrayList accounts = company.getAllCustAccountInformation();
			for (int j = 0; j < accounts.size(); j++) {
				LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) accounts.get(j);
				if (liteAcctInfo.getSiteInformation().getSubstationID() == subID) {
					liteAcctInfo.getSiteInformation().setSubstationID(0);
					
					StarsCustAccountInformation starsAcctInfo = company.getStarsCustAccountInformation(liteAcctInfo.getAccountID(), false);
					if (starsAcctInfo != null) {
						starsAcctInfo.getStarsCustomerAccount().getStarsSiteInformation().setSubstation(
								(Substation)StarsFactory.newEmptyStarsCustListEntry(Substation.class) );
					}
				}
			}
		}
		
		LiteSubstation liteSub = energyCompany.getSubstation( subID );
		
		com.cannontech.database.data.stars.Substation sub = new com.cannontech.database.data.stars.Substation();
		StarsLiteFactory.setSubstation( sub.getSubstation(), liteSub );
		Transaction.createTransaction( Transaction.DELETE, sub ).execute();
		
		energyCompany.deleteSubstation( subID );
		
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
			company.updateStarsSubstations();
		}
	}
	
	public static void deleteAllSubstations(LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		ArrayList substations = energyCompany.getSubstations();
		for (int i = substations.size() - 1; i >= 0; i--) {
			LiteSubstation liteSub = (LiteSubstation) substations.get(i);
			deleteSubstation( liteSub.getSubstationID(), energyCompany );
		}
	}
	
	/**
	 * Update entries of a customer selection list. The entryData parameter
	 * is an array of {entryID(Integer), entryText(String), yukDefID(Integer)}
	 */
	public static void updateYukonListEntries(YukonSelectionList cList, Object[][] entryData, LiteStarsEnergyCompany energyCompany)
		throws WebClientException, java.sql.SQLException
	{
		java.sql.Connection conn = null;
		boolean autoCommit = true;
		
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit( false );
			
			// Create a copy of the old entry list, so we won't lose it if something goes wrong
			ArrayList oldEntries = new ArrayList();
			oldEntries.addAll( cList.getYukonListEntries() );
			
			ArrayList newEntries = new ArrayList();
			
			if (entryData != null) {
				for (int i = 0; i < entryData.length; i++) {
					int entryID = ((Integer)entryData[i][0]).intValue();
					
					if (entryID == 0) {
						// This is a new entry, add it to the new entry list
						com.cannontech.database.db.constants.YukonListEntry entry =
								new com.cannontech.database.db.constants.YukonListEntry();
						entry.setListID( new Integer(cList.getListID()) );
						entry.setEntryOrder( new Integer(i+1) );
						entry.setEntryText( (String)entryData[i][1] );
						entry.setYukonDefID( (Integer)entryData[i][2] );
						entry.setDbConnection( conn );
						entry.add();
						
						com.cannontech.common.constants.YukonListEntry cEntry =
								new com.cannontech.common.constants.YukonListEntry();
						StarsLiteFactory.setConstantYukonListEntry( cEntry, entry );
						newEntries.add( cEntry );
					}
					else {
						// This is an existing entry, update it
						for (int j = 0; j < oldEntries.size(); j++) {
							YukonListEntry cEntry = (YukonListEntry) oldEntries.get(j);
							
							if (cEntry.getEntryID() == entryID) {
								com.cannontech.database.db.constants.YukonListEntry entry = StarsLiteFactory.createYukonListEntry(cEntry);
								entry.setEntryOrder( new Integer(i+1) );
								entry.setEntryText( (String)entryData[i][1] );
								entry.setYukonDefID( (Integer)entryData[i][2] );
								entry.setDbConnection( conn );
								entry.update();
								
								StarsLiteFactory.setConstantYukonListEntry(cEntry, entry);
								newEntries.add( oldEntries.remove(j) );
								break;
							}
						}
					}
				}
			}
			
			// Delete all the remaining entries
			for (int i = 0; i < oldEntries.size(); i++) {
				int entryID = ((YukonListEntry) oldEntries.get(i)).getEntryID();
				
				try {
					com.cannontech.database.db.constants.YukonListEntry entry =
							new com.cannontech.database.db.constants.YukonListEntry();
					entry.setEntryID( new Integer(entryID) );
					entry.setDbConnection( conn );
					entry.delete();
				}
				catch (java.sql.SQLException e) {
					CTILogger.error( e.getMessage(), e );
					conn.rollback();
					throw new WebClientException("Cannot delete list entry with id = " + entryID + ", make sure it is not referenced", e);
				}
			}
			
			conn.commit();
			
			// Sort the entry list by the ordering specified in the selection list
			if (cList.getOrdering().equalsIgnoreCase("A"))
				Collections.sort( newEntries, StarsUtils.YUK_LIST_ENTRY_ALPHA_CMPTR );
			
			// Update the constant objects
			Properties cListEntries = YukonListFuncs.getYukonListEntries();
			synchronized (cListEntries) {
				for (int i = 0; i < cList.getYukonListEntries().size(); i++) {
					YukonListEntry entry = (YukonListEntry) cList.getYukonListEntries().get(i);
					YukonListFuncs.getYukonListEntries().remove( new Integer(entry.getEntryID()) );
				}
				
				for (int i = 0; i < newEntries.size(); i++) {
					YukonListEntry entry = (YukonListEntry) newEntries.get(i);
					YukonListFuncs.getYukonListEntries().put( new Integer(entry.getEntryID()), entry );
				}
			}
			
			cList.setYukonListEntries( newEntries );
		}
		finally {
			if (conn != null) {
				conn.setAutoCommit( autoCommit );
				conn.close();
			}
		}
	}
	
	public static void removeRoute(LiteStarsEnergyCompany energyCompany, int routeID)
		throws TransactionException
	{
		ArrayList routeIDs = energyCompany.getRouteIDs();
		Integer rtID = new Integer(routeID);
		if (!routeIDs.contains( rtID )) return;
		
		ArrayList inventory = energyCompany.loadAllInventory( true );
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				if (!(inventory.get(i) instanceof LiteStarsLMHardware)) continue;
				
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) inventory.get(i);
				if (liteHw.getRouteID() == routeID) {
					com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
							new com.cannontech.database.data.stars.hardware.LMHardwareBase();
					StarsLiteFactory.setLMHardwareBase( hw, liteHw );
					hw.getLMHardwareBase().setRouteID( new Integer(CtiUtilities.NONE_ID) );
					
					Transaction.createTransaction( Transaction.UPDATE, hw.getLMHardwareBase() ).execute();
					liteHw.setRouteID( CtiUtilities.NONE_ID );
					
					if (liteHw.getAccountID() > 0) {
						StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteHw.getAccountID() );
						if (starsAcctInfo != null) {
							StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, energyCompany );
							UpdateLMHardwareAction.parseResponse( liteHw.getInventoryID(), starsInv, starsAcctInfo, null );
						}
					}
				}
			}
		}
		
		ECToGenericMapping map = new ECToGenericMapping();
		map.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		map.setItemID( rtID );
		map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_ROUTE );
		Transaction.createTransaction( Transaction.DELETE, map ).execute();
		
		synchronized (routeIDs) { routeIDs.remove(rtID); }
	}
	
	public static void addMember(LiteStarsEnergyCompany energyCompany, int memberID, int loginID) throws Exception {
		ECToGenericMapping map = new ECToGenericMapping();
		map.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		map.setItemID( new Integer(memberID) );
		map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER );
		Transaction.createTransaction( Transaction.INSERT, map ).execute();
		
		if (loginID != -1) {
			map.setItemID( new Integer(loginID) );
			map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
			Transaction.createTransaction( Transaction.INSERT, map ).execute();
		}
		
		energyCompany.clearHierarchy();
		StarsDatabaseCache.getInstance().getEnergyCompany( memberID ).clearHierarchy();
	}
	
	public static void removeMember(LiteStarsEnergyCompany energyCompany, int memberID) throws Exception {
		ArrayList members = energyCompany.getChildren();
		ArrayList loginIDs = energyCompany.getMemberLoginIDs();
		
		Iterator it = members.iterator();
		while (it.hasNext()) {
			LiteStarsEnergyCompany member = (LiteStarsEnergyCompany) it.next();
			if (memberID != -1 && member.getLiteID() != memberID) continue;
			
			ECToGenericMapping map = new ECToGenericMapping();
			map.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
			map.setItemID( member.getEnergyCompanyID() );
			map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER );
			Transaction.createTransaction( Transaction.DELETE, map ).execute();
			
			member.clearHierarchy();
			
			for (int i = 0; i < loginIDs.size(); i++) {
				Integer loginID = (Integer) loginIDs.get(i);
				LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( loginID.intValue() );
				
				if (EnergyCompanyFuncs.getEnergyCompany( liteUser ).getEnergyCompanyID() == member.getLiteID()) {
					map.setItemID( loginID );
					map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
					Transaction.createTransaction( Transaction.DELETE, map ).execute();
					break;
				}
			}
		}
		
		energyCompany.clearHierarchy();
	}
}
