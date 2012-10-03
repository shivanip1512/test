/*
 * Created on Nov 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DBDeleteResult;
import com.cannontech.core.dao.DBDeletionDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.TransactionType;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.hardware.InventoryBase;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.dr.hardware.service.impl.PorterExpressComCommandBuilder;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.impl.YukonSettingsDaoImpl;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.IDatabaseCache;

public class InventoryManagerUtil {
    
    private static final Logger log = YukonLogManager.getLogger(InventoryManagerUtil.class);

	public static final String STARS_INVENTORY_TEMP = "STARS_INVENTORY_TEMP";
	public static final String STARS_INVENTORY_NO = "STARS_INVENTORY_NO";
	public static final String STARS_INVENTORY_OPERATION = "STARS_INVENTORY_OPERATION";
	
	public static final String INVENTORY_TO_CHECK = "INVENTORY_TO_CHECK";
	public static final String INVENTORY_TO_DELETE = "INVENTORY_TO_DELETE";
	public static final String INVENTORY_SET = "INVENTORY_SET";
	public static final String INVENTORY_SET_DESC = "INVENTORY_SET_DESCRIPTION";
	public static final String SN_RANGE_TO_CONFIG = "SN_RANGE_TO_CONFIG";
	public static final String INVENTORY_SELECTED = "INVENTORY_SELECTED";
	public static final String DEVICE_SELECTED = "DEVICE_SELECTED";
	public static final String HARDWARE_ADDRESSING = "HARDWARE_ADDRESSING";
	
	private static Map<Integer,Object[]> batchCfgSubmission = null;
	
	/**
	 * Search for devices with specified category and device name
	 */
	public static List<LiteYukonPAObject> searchDevice(int categoryID, String deviceName) {
        List<LiteYukonPAObject> devList = new ArrayList<LiteYukonPAObject>();
        List<LiteYukonPAObject> allDevices = null;
		
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized (cache) {
			if (InventoryUtils.isMCT( categoryID ))
				allDevices = cache.getAllMCTs();
			
			if (deviceName == null || deviceName.length() == 0) {
				devList.addAll( allDevices );
			}
			else {
				deviceName = deviceName.toUpperCase();
				for (int i = 0; i < allDevices.size(); i++) {
					LiteYukonPAObject litePao = allDevices.get(i);
					if (litePao.getPaoName().toUpperCase().startsWith( deviceName ))
						devList.add( litePao );
				}
			}
		}
		
		return devList;
	}
	
	public static void sendSwitchCommand(SwitchCommandQueue.SwitchCommand cmd) throws CommandCompletionException {
	    
		InventoryBaseDao inventoryBaseDao = YukonSpringHook.getBean("inventoryBaseDao", InventoryBaseDao.class); 
		PorterExpressComCommandBuilder xcomCommandBuilder = YukonSpringHook.getBean("porterExpressComCommandBuilder", PorterExpressComCommandBuilder.class); 
		LmHardwareCommandService commandService = YukonSpringHook.getBean("lmHardwareCommandService", LmHardwareCommandService.class); 

		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( cmd.getEnergyCompanyID() );
        boolean writeToFile = false;
        
        String batchProcessType = YukonSpringHook.getBean("yukonSettingsDao",YukonSettingsDaoImpl.class).getSettingStringValue(YukonSetting.BATCHED_SWITCH_COMMAND_TOGGLE);
        if(batchProcessType != null) {
            writeToFile = batchProcessType.compareTo(StarsUtils.BATCH_SWITCH_COMMAND_MANUAL) == 0 
                && VersionTools.staticLoadGroupMappingExists();
        }
        
		try {
		    LiteLmHardwareBase liteHw = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(cmd.getInventoryID());
		    
		    // Parameter options corresponds to the infoString field of the switch
            // command queue.
            // It takes the format of "GroupID:XX;RouteID:XX"
		    Integer optGroupId = null;
            Integer optRouteId = null;
            if (cmd.getInfoString() != null) {
                String[] fields = cmd.getInfoString().split(";");
                for (String field : fields) {
                    try {
                        if (field.startsWith("GroupID:")) {
                            optGroupId = Integer.valueOf(field.substring("GroupID:".length()));
                        } else if (field.startsWith("RouteID:")) {
                            optRouteId = Integer.valueOf(field.substring("RouteID:".length()));
                        }
                    } catch (NumberFormatException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        
    		if (cmd.getCommandType().equalsIgnoreCase(SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE)) {
    			if (writeToFile) {
    			    xcomCommandBuilder.fileWriteConfigCommand(energyCompany, liteHw, true, cmd.getInfoString());
    			} else {
    			    LmHardwareCommand.Builder b = new LmHardwareCommand.Builder(liteHw, LmHardwareCommandType.CONFIG, energyCompany.getUser());
    			    b.withParam(LmHardwareCommandParam.FORCE_IN_SERVICE, true);
    			    if (optGroupId != null) {
    			        b.withParam(LmHardwareCommandParam.OPTIONAL_GROUP_ID, optGroupId);
    			    }
    			    if (optRouteId != null) {
                        b.withParam(LmHardwareCommandParam.OPTIONAL_ROUTE_ID, optRouteId);
                    }
		            LmHardwareCommand command = b.build();
                    commandService.sendConfigCommand(command);
                }
            }
            else if (cmd.getCommandType().equalsIgnoreCase(SwitchCommandQueue.SWITCH_COMMAND_DISABLE)) {
                if (writeToFile) { 
                    xcomCommandBuilder.fileWriteDisableCommand(energyCompany, liteHw);
                } else {
                    LmHardwareCommand.Builder b = new LmHardwareCommand.Builder(liteHw, LmHardwareCommandType.OUT_OF_SERVICE, energyCompany.getUser());
                    b.withParam(LmHardwareCommandParam.FORCE_IN_SERVICE, true);
                    if (optGroupId != null) {
                        b.withParam(LmHardwareCommandParam.OPTIONAL_GROUP_ID, optGroupId);
                    }
                    if (optRouteId != null) {
                        b.withParam(LmHardwareCommandParam.OPTIONAL_ROUTE_ID, optRouteId);
                    }
                    LmHardwareCommand command = b.build();
                    commandService.sendOutOfServiceCommand(command);
                }
            } else if (cmd.getCommandType().equalsIgnoreCase(SwitchCommandQueue.SWITCH_COMMAND_ENABLE)) {
                if (writeToFile) { 
                    xcomCommandBuilder.fileWriteEnableCommand(energyCompany, liteHw);
                } else {
                    LmHardwareCommand.Builder b = new LmHardwareCommand.Builder(liteHw, LmHardwareCommandType.IN_SERVICE, energyCompany.getUser());
                    b.withParam(LmHardwareCommandParam.FORCE_IN_SERVICE, true);
                    if (optGroupId != null) {
                        b.withParam(LmHardwareCommandParam.OPTIONAL_GROUP_ID, optGroupId);
                    }
                    if (optRouteId != null) {
                        b.withParam(LmHardwareCommandParam.OPTIONAL_ROUTE_ID, optRouteId);
                    }
                    LmHardwareCommand command = b.build();
                    commandService.sendInServiceCommand(command);
                }
            }
    		
    		if (liteHw.getAccountID() > 0) {
                StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation(liteHw.getAccountID());
                if (starsAcctInfo != null) {
                    StarsInventory starsInv = StarsLiteFactory.createStarsInventory(liteHw, energyCompany);
                    StarsUtils.populateInventoryFields(starsAcctInfo, starsInv);
                }
            }
    		
		} catch (NotFoundException e) {
		    // Inventory (no longer?) exists in Yukon. Switch command
		    log.warn(e.getMessage() + " - Inventory commands skipped for " + cmd.toString());
		}
		
		SwitchCommandQueue.getInstance().removeCommand(cmd.getInventoryID());
	}
    
	public static void deleteInventory(LiteInventoryBase liteInv, YukonEnergyCompany yukonEnergyCompany, boolean deleteFromYukon)
	throws SQLException, PersistenceException {
	    DBPersistentDao dbPersistentDao = YukonSpringHook.getBean("dbPersistentDao", DBPersistentDao.class); 

	    
	    InventoryBase inventory = (InventoryBase)StarsLiteFactory.createDBPersistent(liteInv);
	    dbPersistentDao.performDBChange(inventory, TransactionType.DELETE);
		
		if (liteInv.getDeviceID() > 0 && deleteFromYukon) {
			
			DBDeleteResult delRes = new DBDeleteResult( liteInv.getDeviceID(), DBDeletionDao.DEVICE_TYPE );
			byte status = DaoFactory.getDbDeletionDao().deletionAttempted( delRes );

			if (status == DBDeletionDao.STATUS_DISALLOW) {
				throw new NotAuthorizedException(delRes.getDescriptionMsg().toString());
			}
			
			LiteYukonPAObject litePao = DaoFactory.getPaoDao().getLiteYukonPAO( liteInv.getDeviceID() );
			DBPersistent dbPer = LiteFactory.convertLiteToDBPers( litePao );
			dbPersistentDao.performDBChange(dbPer, TransactionType.DELETE);
		}
	}
	
	/**
	 * Get all the hardware/devices for the given accounts in the inventory
	 * @param accounts List of LiteStarsCustAccountInformation, or Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany)
	 * @return List of LiteInventoryBase or Pair(LiteInventoryBase, LiteStarsEnergyCompany), based on the element type of accounts
	 */
	private static List<LiteInventoryBase> getInventoryByAccounts(List<Object> accounts, LiteStarsEnergyCompany energyCompany) {
		List<LiteInventoryBase> invList = new ArrayList<LiteInventoryBase>();
		
		InventoryBaseDao inventoryBaseDao = 
			YukonSpringHook.getBean("inventoryBaseDao", InventoryBaseDao.class);
		InventoryDao inventoryDao = YukonSpringHook.getBean("inventoryDao", InventoryDao.class);
		
		for (int i = 0; i < accounts.size(); i++) {
			if (accounts.get(i) instanceof Pair) {
				@SuppressWarnings("unchecked") Integer accountId = (Integer) ((Pair)accounts.get(i)).getFirst();
				List<Integer> inventoryIds = inventoryDao.getInventoryIdsByAccount(accountId);
				for (Integer inventoryId : inventoryIds) {
					LiteInventoryBase liteInv = inventoryBaseDao.getByInventoryId(inventoryId);
					invList.add(liteInv);
				}
			}
			else {
				Integer accountId = (Integer) accounts.get(i);
				List<Integer> inventoryIds = inventoryDao.getInventoryIdsByAccount(accountId);
				for (Integer inventoryId : inventoryIds) {
					LiteInventoryBase liteInv = inventoryBaseDao.getByInventoryId(inventoryId);
					invList.add(liteInv);
				}
			}
		}
		
		return invList;
	}
	
	/**
	 * Search the inventory for hardware/devices by the given search type and value.
	 * If searchMembers is true, returns a list of Pair(LiteInventoryBase, LiteStarsEnergyCompany);
	 * otherwise, returns a list of LiteInventoryBase
	 */
	public static List<LiteInventoryBase> searchInventory(LiteStarsEnergyCompany energyCompany, int searchBy, String searchValue, boolean searchMembers)
		throws WebClientException
	{
		
		StarsSearchDao starsSearchDao = YukonSpringHook.getBean("starsSearchDao", StarsSearchDao.class);
		List<YukonEnergyCompany> ecList = new ArrayList<YukonEnergyCompany>();
		ecList.add(energyCompany);
		if(searchMembers) {
			ecList.addAll(ECUtils.getAllDescendants(energyCompany));
		}
		
		if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_SERIAL_NO) {
			List<LiteInventoryBase> hardwareList = 
				starsSearchDao.searchLMHardwareBySerialNumber(searchValue, ecList);
			return hardwareList;
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_DEVICE_NAME) {
			List<LiteInventoryBase> inventoryList = 
				starsSearchDao.searchInventoryByDeviceName(searchValue, ecList);
			return inventoryList;
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ALT_TRACK_NO) {
			List<LiteInventoryBase> inventoryList = 
				starsSearchDao.searchInventoryByAltTrackNumber(searchValue, ecList);
			return inventoryList;
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ACCT_NO) {
			List<Object> accounts = energyCompany.searchAccountByAccountNumber( searchValue, searchMembers, true);
			return getInventoryByAccounts( accounts, energyCompany );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_PHONE_NO) {
			String phoneNo = ServletUtils.formatPhoneNumberForSearch( searchValue );
			List<Object> accounts = energyCompany.searchAccountByPhoneNo( phoneNo, searchMembers );
			return getInventoryByAccounts( accounts, energyCompany );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_LAST_NAME) {
            List<Object> accounts = energyCompany.searchAccountByLastName( searchValue, searchMembers, true );
			return getInventoryByAccounts( accounts, energyCompany );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ORDER_NO) {
			// TODO: The WorkOrderBase table doesn't have InventoryID column, maybe should be added
            List<Object> accounts = energyCompany.searchAccountByOrderNo( searchValue, searchMembers );
			return getInventoryByAccounts( accounts, energyCompany );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ADDRESS) {
            List<Object> accounts = energyCompany.searchAccountByAddress( searchValue, searchMembers, true );
			return getInventoryByAccounts( accounts, energyCompany );
		}
		else {
			throw new WebClientException("Unrecognized search type");
		}
	}
	
	public synchronized static Map<Integer,Object[]> getBatchConfigSubmission() {
		if (batchCfgSubmission == null) {
			batchCfgSubmission = new Hashtable<Integer,Object[]>();
			
			String sql = "SELECT TimeStamp, EnergyCompanyID, Description FROM ActivityLog " +
				"WHERE TimeStamp > ? AND Action = '" + ActivityLogActions.HARDWARE_SEND_BATCH_CONFIG_ACTION + "'";
			
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rset = null;
            
			try {
				conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				
				stmt = conn.prepareStatement( sql );
				stmt.setDate( 1, new java.sql.Date(ServletUtil.getToday().getTime() - 600 * 1000) );	// set the time to be a bit earlier than midnight today
				rset = stmt.executeQuery();
				
				while (rset.next()) {
					long timeStamp = rset.getTimestamp(1).getTime();
					int energyCompanyID = rset.getInt(2);
					String description = rset.getString(3);
					batchCfgSubmission.put( new Integer(energyCompanyID), new Object[]{new Date(timeStamp), description} );
				}
			}
			catch (java.sql.SQLException e) {
				log.error( e.getMessage(), e );
			}
			finally {
				SqlUtils.close(rset, stmt, conn);
			}
		}
		
		return batchCfgSubmission;
	}
}
