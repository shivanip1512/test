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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMGroupExpressCom;
import com.cannontech.database.data.device.lm.MacroGroup;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteCustomerFAQ;
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
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.operator.InventoryRole;
import com.cannontech.roles.operator.OddsForControlRole;
import com.cannontech.roles.operator.WorkOrderRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
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
	
	public static void deleteFAQSubject(int subjectID, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		com.cannontech.database.db.stars.CustomerFAQ.deleteCustomerFAQs( subjectID );
		
		ArrayList liteFAQs = energyCompany.getCustomerFAQs();
		synchronized (liteFAQs) {
			Iterator it = liteFAQs.iterator();
			while (it.hasNext()) {
				LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) it.next();
				if (liteFAQ.getSubjectID() == subjectID) it.remove();
			}
		}
		
		YukonListEntry cEntry = YukonListFuncs.getYukonListEntry( subjectID );
		com.cannontech.database.db.constants.YukonListEntry entry = StarsLiteFactory.createYukonListEntry( cEntry );
		Transaction.createTransaction( Transaction.DELETE, entry ).execute();
		
		YukonSelectionList cList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP );
		cList.getYukonListEntries().remove( cEntry );
		YukonListFuncs.getYukonListEntries().remove( entry.getEntryID() );
		
		ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
			company.updateStarsCustomerFAQs();
		}
	}
	
	public static void deleteAllFAQSubjects(LiteStarsEnergyCompany energyCompany, boolean removeList)
		throws Exception
	{
		YukonSelectionList cList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP, false, false );
		if (cList == null) return;
		
		com.cannontech.database.db.stars.CustomerFAQ.deleteAllCustomerFAQs( cList.getListID() );
		
		if (removeList) {
			energyCompany.resetCustomerFAQs();
			
			com.cannontech.database.data.constants.YukonSelectionList list =
					new com.cannontech.database.data.constants.YukonSelectionList();
			list.setListID( new Integer(cList.getListID()) );
			Transaction.createTransaction( Transaction.DELETE, list ).execute();
			
			energyCompany.deleteYukonSelectionList( cList );
		}
		else {
			energyCompany.getCustomerFAQs().clear();
			
			java.sql.Connection conn = null;
			try {
				conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				com.cannontech.database.db.constants.YukonListEntry.deleteAllListEntries( new Integer(cList.getListID()), conn );
			}
			finally {
				if (conn != null) conn.close();
			}
			
			java.util.Properties entries = YukonListFuncs.getYukonListEntries();
			synchronized (entries) {
				for (int i = 0; i < cList.getYukonListEntries().size(); i++) {
					YukonListEntry entry = (YukonListEntry) cList.getYukonListEntries().get(i);
					entries.remove( new Integer(entry.getEntryID()) );
				}
			}
			cList.getYukonListEntries().clear();
		}
		
		ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
			company.updateStarsCustomerFAQs();
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
				YukonListEntry cEntry = (YukonListEntry) oldEntries.get(i);
				
				try {
					com.cannontech.database.db.constants.YukonListEntry entry =
							new com.cannontech.database.db.constants.YukonListEntry();
					entry.setEntryID( new Integer(cEntry.getEntryID()) );
					entry.setDbConnection( conn );
					entry.delete();
				}
				catch (java.sql.SQLException e) {
					CTILogger.error( e.getMessage(), e );
					conn.rollback();
					throw new WebClientException("Cannot delete list entry \"" + cEntry.getEntryText() + "\", make sure it is not referenced", e);
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
	
	private static String getReferenceColumn(String listName) {
		if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE))
			return "VoltageID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE))
			return "LMHardwareTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE))
			return "CallTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE))
			return "WorkTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER))
			return "ManufactureID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION))
			return "LocationID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL))
			return "ChanceOfControlID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE))
			return "ResidenceTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL))
			return "ConstructionMaterialID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT))
			return "DecadeBuiltID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET))
			return "SquareFeetID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH))
			return "InsulationDepthID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION))
			return "GeneralConditionID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM))
			return "MainCoolingSystemID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM))
			return "MainHeatingSystemID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS))
			return "NumberOfOccupantsID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE))
			return "OwnershipTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE))
			return "MainFuelTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE))
			return "TonnageID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE))
			return "TypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS))
			return "NumberOfGallonsID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE))
			return "EnergySourceId";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION))
			return null;
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE))
			return "SecondaryEnergySourceID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE))
			return "SwitchOverTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE))
			return "DryerTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE))
			return "BinSizeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE))
			return "BlowerEnergySourceID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE))
			return "BlowerHeatSourceID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER))
			return "BlowerHorsePowerID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE))
			return "StorageTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE))
			return "PumpSizeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE))
			return "PumpTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE))
			return "StandbySourceID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE))
			return "EnergySourceID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER))
			return "HorsePowerID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION))
			return "MeterLocationID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE))
			return "MeterVoltageID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE))
			return "SoilTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE))
			return "IrrigationTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG))
			return "TransferSwitchMfgID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE))
			return "TransferSwitchTypeID";
		return null;
	}
	
	private static String[] getReferenceTableConstraint(String listName) {
		if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE))
			return new String[] {
				"InventoryBase",
				"InventoryID IN (SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE))
			return new String[] {
				"LMHardwareBase",
				"InventoryID IN (SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE))
			return new String[] {
				"CallReportBase",
				"AccountID IN (SELECT AccountID FROM ECToAccountMapping WHERE EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE))
			return new String[] {
				"WorkOrderBase",
				"OrderID IN (SELECT WorkOrderID FROM ECToWorkOrderMapping WHERE EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION))
			return new String[] {
				"ApplianceBase",
				"AccountID IN (SELECT AccountID FROM ECToAccountMapping WHERE EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL))
			return new String[] {
				"LMProgramWebPublishing",
				"ApplianceCategoryID IN (SELECT ItemID FROM ECToGenericMapping " +
					"WHERE MappingCategory = 'ApplianceCategory' AND EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE))
			return new String[] {
				"CustomerResidence",
				"AccountSiteID IN (SELECT AccountSiteID FROM CustomerAccount acc, ECToAccountMapping map " +
					"WHERE acc.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE))
			return new String[] {
				"ApplianceAirConditioner",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION))
			return new String[] {
				"ApplianceWaterHeater",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE))
			return new String[] {
				"ApplianceDualFuel",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER))
			return new String[] {
				"ApplianceGrainDryer",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE))
			return new String[] {
				"ApplianceStorageHeat",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE))
			return new String[] {
				"ApplianceHeatPump",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE))
			return new String[] {
				"ApplianceIrrigation",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE))
			return new String[] {
				"ApplianceGenerator",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		return null;
	}
	
	public static void updateListEntryReferences(LiteStarsEnergyCompany energyCompany, YukonSelectionList newList)
		throws WebClientException, java.sql.SQLException
	{
		String columnName = getReferenceColumn( newList.getListName() );
		if (columnName == null) return;
		
		String[] res = getReferenceTableConstraint( newList.getListName() );
		String tableName = res[0];
		String constraint = res[1];
		
		String sql1 = "SELECT DISTINCT " + columnName + " FROM " + tableName + " WHERE " + constraint;
		String sql2 = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE " + columnName + " = ? AND " + constraint;
		
		Hashtable entryIDMap = new Hashtable();
		
		ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
		ArrayList companies = new ArrayList();
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
			if (company.equals(energyCompany) || company.getYukonSelectionList(newList.getListName(), false, false) == null)
				companies.add( company );
		}
		
		java.sql.Connection conn = null;
		boolean autoCommit = true;
		java.sql.PreparedStatement pstmt1 = null;
		java.sql.PreparedStatement pstmt2 = null;
		
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			pstmt1 = conn.prepareStatement( sql1 );
			
			for (int i = 0; i < companies.size(); i++) {
				LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) companies.get(i);
				
				pstmt1.setInt( 1, company.getLiteID() );
				java.sql.ResultSet rset = pstmt1.executeQuery();
				
				while (rset.next()) {
					int oldEntryID = rset.getInt(1);
					if (oldEntryID == 0) continue;
					
					YukonListEntry oldEntry = YukonListFuncs.getYukonListEntry( oldEntryID );
					int newEntryID = 0;
					
					for (int j = 0; j < newList.getYukonListEntries().size(); j++) {
						YukonListEntry newEntry = (YukonListEntry) newList.getYukonListEntries().get(j);
						if (newEntry.getYukonDefID() == oldEntry.getYukonDefID()
							&& newEntry.getEntryText().trim().equalsIgnoreCase(oldEntry.getEntryText().trim()))
						{
							newEntryID = newEntry.getEntryID();
							break;
						}
					}
					
					if (newEntryID == 0)
						throw new WebClientException("Cannot find a matching entry for \"" + oldEntry.getEntryText() + "\" in the new list");
					
					entryIDMap.put( new Integer(oldEntryID), new Integer(newEntryID) );
				}
				
				rset.close();
			}
			
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit( false );
			pstmt2 = conn.prepareStatement( sql2 );
			
			try {
				for (int i = 0; i < companies.size(); i++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) companies.get(i);
					
					Iterator it = entryIDMap.keySet().iterator();
					while (it.hasNext()) {
						Integer oldEntryID = (Integer) it.next();
						Integer newEntryID = (Integer) entryIDMap.get( oldEntryID );
						pstmt2.setInt( 1, newEntryID.intValue() );
						pstmt2.setInt( 2, oldEntryID.intValue() );
						pstmt2.setInt( 3, company.getLiteID() );
						pstmt2.execute();
					}
				}
			}
			catch (java.sql.SQLException e) {
				CTILogger.error( e.getMessage(), e );
				conn.rollback();
				throw new WebClientException("Failed to update refenreces from the old list to the new list");
			}
			
			conn.commit();
		}
		finally {
			if (pstmt1 != null) pstmt1.close();
			if (pstmt2 != null) pstmt2.close();
			if (conn != null) {
				conn.setAutoCommit( autoCommit );
				conn.close();
			} 
		}
		
		for (int i = 0; i < companies.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) companies.get(i);
			
			if (newList.getListName().equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE)) {
				ArrayList inventory = company.getAllInventory();
				for (int j = 0; j < inventory.size(); j++) {
					if (!(inventory.get(j) instanceof LiteStarsLMHardware))
						continue;
					LiteStarsLMHardware liteHw = (LiteStarsLMHardware) inventory.get(j);
					Integer newDevTypeID = (Integer) entryIDMap.get( new Integer(liteHw.getLmHardwareTypeID()) );
					if (newDevTypeID != null) liteHw.setLmHardwareTypeID( newDevTypeID.intValue() );
				}
			}
			
			company.clearActiveAccounts();
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
					hw.getLMHardwareBase().setRouteID( new Integer(CtiUtilities.NONE_ZERO_ID) );
					
					Transaction.createTransaction( Transaction.UPDATE, hw.getLMHardwareBase() ).execute();
					liteHw.setRouteID( CtiUtilities.NONE_ZERO_ID );
					
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
	
	public static void addMember(LiteStarsEnergyCompany energyCompany, LiteStarsEnergyCompany member, int loginID) throws Exception {
		ECToGenericMapping map = new ECToGenericMapping();
		map.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		map.setItemID( member.getEnergyCompanyID() );
		map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER );
		Transaction.createTransaction( Transaction.INSERT, map ).execute();
		
		if (loginID != -1) {
			map.setItemID( new Integer(loginID) );
			map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
			Transaction.createTransaction( Transaction.INSERT, map ).execute();
		}
		
		energyCompany.clearHierarchy();
		member.clearHierarchy();
		
		// Modify energy company hierarchy related role properties
		LiteYukonGroup adminGroup = energyCompany.getOperatorAdminGroup();
		boolean adminGroupUpdated = false;
		adminGroupUpdated |= updateGroupRoleProperty( adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.SINGLE_ENERGY_COMPANY, CtiUtilities.FALSE_STRING );
		adminGroupUpdated |= updateGroupRoleProperty( adminGroup, AdministratorRole.ROLEID, AdministratorRole.ADMIN_MANAGE_MEMBERS, CtiUtilities.TRUE_STRING );
		
		if (adminGroupUpdated)
			ServerUtils.handleDBChange( adminGroup, DBChangeMsg.CHANGE_TYPE_UPDATE );
		
		adminGroup = member.getOperatorAdminGroup();
		String value = null;
		if (updateGroupRoleProperty( adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.SINGLE_ENERGY_COMPANY, CtiUtilities.FALSE_STRING)
			|| ((value = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING )) != null
				&& updateGroupRoleProperty( adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING, value ))
			|| ((value = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.OPTIONAL_PRODUCT_DEV )) != null
				&& updateGroupRoleProperty( adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.OPTIONAL_PRODUCT_DEV, value )))
			ServerUtils.handleDBChange( adminGroup, DBChangeMsg.CHANGE_TYPE_UPDATE );
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
	
	public static LiteYukonGroup copyYukonGroup(LiteYukonGroup srcGrp, String grpName)
		throws TransactionException
	{
		com.cannontech.database.data.user.YukonGroup newGroup = new com.cannontech.database.data.user.YukonGroup();
		com.cannontech.database.db.user.YukonGroup groupDB = newGroup.getYukonGroup();
		
		groupDB.setGroupName( grpName );
		groupDB.setGroupDescription( srcGrp.getGroupDescription() );
		
		Map roleMap = (Map) DefaultDatabaseCache.getInstance().getYukonGroupRolePropertyMap().get( srcGrp );
		Iterator roleIt = roleMap.values().iterator();
		while (roleIt.hasNext()) {
			Map rolePropMap = (Map) roleIt.next();
			Iterator rolePropIt = rolePropMap.keySet().iterator();
			while (rolePropIt.hasNext()) {
				LiteYukonRoleProperty liteRoleProp = (LiteYukonRoleProperty) rolePropIt.next();
				String value = (String) rolePropMap.get(liteRoleProp);
				
				com.cannontech.database.db.user.YukonGroupRole groupRole = new com.cannontech.database.db.user.YukonGroupRole();
				groupRole.setRoleID( new Integer(liteRoleProp.getRoleID()) );
				groupRole.setRolePropertyID( new Integer(liteRoleProp.getRolePropertyID()) );
				groupRole.setValue( value );
				newGroup.getYukonGroupRoles().add( groupRole );
			}
		}
		
		newGroup = (com.cannontech.database.data.user.YukonGroup)
				Transaction.createTransaction(Transaction.INSERT, newGroup).execute();
		
		LiteYukonGroup liteGroup = new LiteYukonGroup( newGroup.getGroupID().intValue() );
		ServerUtils.handleDBChange( liteGroup, DBChangeMsg.CHANGE_TYPE_ADD );
		
		return AuthFuncs.getGroup( liteGroup.getGroupID() );
	}
	
	public static LiteYukonGroup createOperatorAdminGroup(String grpName, Hashtable rolePropMap)
		throws TransactionException
	{
		com.cannontech.database.data.user.YukonGroup adminGrp = new com.cannontech.database.data.user.YukonGroup();
		com.cannontech.database.db.user.YukonGroup groupDB = adminGrp.getYukonGroup();
		
		groupDB.setGroupName( grpName );
		groupDB.setGroupDescription( "Privilege group for the energy company's default operator login" );
		
		LiteYukonRoleProperty[] roleProps = RoleFuncs.getRoleProperties( EnergyCompanyRole.ROLEID );
		for (int i = 0; i < roleProps.length; i++) {
			com.cannontech.database.db.user.YukonGroupRole groupRole = new com.cannontech.database.db.user.YukonGroupRole();
			
			groupRole.setRoleID( new Integer(EnergyCompanyRole.ROLEID) );
			groupRole.setRolePropertyID( new Integer(roleProps[i].getRolePropertyID()) );
			String value = (String) rolePropMap.get( groupRole.getRolePropertyID() );
			if (value != null)
				groupRole.setValue( value );
			else
				groupRole.setValue( CtiUtilities.STRING_NONE );
			
			adminGrp.getYukonGroupRoles().add( groupRole );
		}
		
		roleProps = RoleFuncs.getRoleProperties( AdministratorRole.ROLEID );
		for (int i = 0; i < roleProps.length; i++) {
			com.cannontech.database.db.user.YukonGroupRole groupRole = new com.cannontech.database.db.user.YukonGroupRole();
			
			groupRole.setRoleID( new Integer(AdministratorRole.ROLEID) );
			groupRole.setRolePropertyID( new Integer(roleProps[i].getRolePropertyID()) );
			String value = (String) rolePropMap.get( groupRole.getRolePropertyID() );
			if (value != null)
				groupRole.setValue( value );
			else
				groupRole.setValue( CtiUtilities.STRING_NONE );
			
			adminGrp.getYukonGroupRoles().add( groupRole );
		}
		
		adminGrp = (com.cannontech.database.data.user.YukonGroup)
				Transaction.createTransaction(Transaction.INSERT, adminGrp).execute();
		
		LiteYukonGroup liteGroup = new LiteYukonGroup( adminGrp.getGroupID().intValue() );
		ServerUtils.handleDBChange( liteGroup, DBChangeMsg.CHANGE_TYPE_ADD );
		
		return AuthFuncs.getGroup( liteGroup.getGroupID() );
	}
	
	public static LiteYukonUser createOperatorLogin(String username, String password, String status, LiteYukonGroup[] operGroups,
		LiteStarsEnergyCompany energyCompany) throws Exception
	{
		if (username.length() == 0)
			throw new WebClientException( "Username cannot be empty" );
		if (password.length() == 0)
			throw new WebClientException( "Password cannot be empty" );
		if (YukonUserFuncs.getLiteYukonUser( username ) != null)
			throw new WebClientException( "Username already exists" );
		
		com.cannontech.database.data.user.YukonUser yukonUser = new com.cannontech.database.data.user.YukonUser();
		com.cannontech.database.db.user.YukonUser userDB = yukonUser.getYukonUser();
		
		userDB.setUsername( username );
		userDB.setPassword( password );
		userDB.setStatus( status );
		
		for (int i = 0; i < operGroups.length; i++) {
			com.cannontech.database.db.user.YukonGroup group =
					new com.cannontech.database.db.user.YukonGroup();
			group.setGroupID( new Integer(operGroups[i].getGroupID()) );
			yukonUser.getYukonGroups().add( group );
		}
		
		yukonUser = (com.cannontech.database.data.user.YukonUser)
				Transaction.createTransaction(Transaction.INSERT, yukonUser).execute();
		
		if (energyCompany != null) {
			SqlStatement stmt = new SqlStatement(
					"INSERT INTO EnergyCompanyOperatorLoginList VALUES(" +
						energyCompany.getEnergyCompanyID() + ", " + userDB.getUserID() + ")",
					CtiUtilities.getDatabaseAlias()
					);
			stmt.execute();
			
			ArrayList operLoginIDs = energyCompany.getOperatorLoginIDs();
			synchronized (operLoginIDs) {
				if (!operLoginIDs.contains( userDB.getUserID() ))
					operLoginIDs.add(userDB.getUserID());
			}
		}
		
		LiteYukonUser liteUser = new LiteYukonUser(
				userDB.getUserID().intValue(),
				userDB.getUsername(),
				userDB.getPassword(),
				userDB.getStatus()
				);
		ServerUtils.handleDBChange( liteUser, DBChangeMsg.CHANGE_TYPE_ADD );
		
		return liteUser;
	}
	
	public static void updateLogin(LiteYukonUser liteUser, String username, String password, String status,
		LiteYukonGroup loginGroup, LiteStarsEnergyCompany energyCompany) throws Exception
	{
		if (!liteUser.getUsername().equalsIgnoreCase(username) && YukonUserFuncs.getLiteYukonUser(username) != null)
			throw new WebClientException( "Username '" + username + "' already exists" );
		
		if (password.length() == 0) {
			if (!username.equalsIgnoreCase( liteUser.getUsername() ))
				throw new WebClientException( "Password cannot be empty" );
			// Only the login group and/or status have been changed, ignore the password
			password = liteUser.getPassword();
		}
		
		com.cannontech.database.data.user.YukonUser user = new com.cannontech.database.data.user.YukonUser();
		com.cannontech.database.db.user.YukonUser dbUser = user.getYukonUser();
		
		StarsLiteFactory.setYukonUser( dbUser, liteUser );
		dbUser.setUsername( username );
		dbUser.setPassword( password );
		if (status != null) dbUser.setStatus( status );
		
		boolean groupChanged = false;
		if (loginGroup != null) {
			com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized (cache) {
				List userGroups = (List) cache.getYukonUserGroupMap().get( liteUser );
				for (int i = 0; i < userGroups.size(); i++) {
					LiteYukonGroup liteGroup = (LiteYukonGroup) userGroups.get(i);
					if (liteGroup.getGroupID() == YukonGroupRoleDefs.GRP_YUKON)
						continue;
					if (liteUser.getUserID() == energyCompany.getUserID() && liteGroup.equals(energyCompany.getOperatorAdminGroup()))
						continue;
					if (liteGroup.getGroupID() != loginGroup.getGroupID()) {
						groupChanged = true;
						break;
					}
				}
			}
		}
		
		if (groupChanged) {
			com.cannontech.database.db.user.YukonGroup group =
					new com.cannontech.database.db.user.YukonGroup( new Integer(loginGroup.getGroupID()) );
			user.getYukonGroups().addElement( group );
			
			if (liteUser.getUserID() == energyCompany.getUserID()) {
				group = new com.cannontech.database.db.user.YukonGroup( new Integer(energyCompany.getOperatorAdminGroup().getGroupID()) );
				user.getYukonGroups().addElement( group );
			}
			
			Transaction.createTransaction( Transaction.UPDATE, user ).execute();
		}
		else {
			Transaction.createTransaction( Transaction.UPDATE, dbUser ).execute();
		}
		
		ServerUtils.handleDBChange( liteUser, com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE );
	}
	
	public static ArrayList getSelectionListsInUse(LiteStarsEnergyCompany energyCompany, StarsYukonUser user) {
		ArrayList userLists = new ArrayList();
		
		if (StarsUtils.isOperator( user )) {
			TreeMap listMap = new TreeMap();
			listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE,
				energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE) );
			
			if (AuthFuncs.checkRole(user.getYukonUser(), OddsForControlRole.ROLEID) != null)
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL) );
			
			if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_CALL_TRACKING))
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE) );
			
			if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT))
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD) );
			
			if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_APPLIANCES) ||
				AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_APPLIANCES_CREATE))
			{
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION) );
				
				ArrayList categories = energyCompany.getAllApplianceCategories();
				ArrayList catDefIDs = new ArrayList();
				
				for (int i = 0; i < categories.size(); i++) {
					LiteApplianceCategory liteAppCat = (LiteApplianceCategory) categories.get(i);
					int catDefID = YukonListFuncs.getYukonListEntry( liteAppCat.getCategoryID() ).getYukonDefID();
					if (catDefIDs.contains( new Integer(catDefID) )) continue;
					catDefIDs.add( new Integer(catDefID) );
					
					if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER) {
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE) );
					}
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER) {
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION) );
					}
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL) {
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE) );
					}
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR) {
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG) );
					}
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER) {
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE) );
					}
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT) {
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE) );
					}
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP) {
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE) );
					}
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION) {
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE,
							energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE) );
					}
				}
			}
			
			if (AuthFuncs.checkRole(user.getYukonUser(), InventoryRole.ROLEID) != null ||
				AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_HARDWARES) ||
				AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_HARDWARES_CREATE))
			{
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_RATE_SCHEDULE,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_RATE_SCHEDULE) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE) );
			}
			
			if (AuthFuncs.checkRole(user.getYukonUser(), WorkOrderRole.ROLEID) != null ||
				AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_WORK_ORDERS))
			{
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS) );
			}
			
			if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_RESIDENCE)) {
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE) );
			}
			
			if (AuthFuncs.checkRole(user.getYukonUser(), InventoryRole.ROLEID) != null) {
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY) );
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), InventoryRole.INVENTORY_SHOW_ALL)) {
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_INV_SORT_BY,
						energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INV_SORT_BY) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY,
						energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY) );
				}
			}
			
			if (AuthFuncs.checkRole(user.getYukonUser(), WorkOrderRole.ROLEID) != null) {
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY) );
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), WorkOrderRole.WORK_ORDER_SHOW_ALL)) {
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SO_SORT_BY,
						energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SO_SORT_BY) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY,
						energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY) );
				}
			}
			
			if (AuthFuncs.checkRoleProperty(user.getYukonUser(), AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY)) {
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY) );
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_ANSWER_TYPE,
					energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_ANSWER_TYPE) );
			}
			
			Iterator it = listMap.values().iterator();
			while (it.hasNext())
				userLists.add( it.next() );
		}
		else if (StarsUtils.isResidentialCustomer( user )) {
			userLists.add( energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL) );
			
			if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT)) {
				YukonSelectionList list = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD);
				if (list != null) userLists.add( list );
			}
		}
		
		return userLists;
	}
}
