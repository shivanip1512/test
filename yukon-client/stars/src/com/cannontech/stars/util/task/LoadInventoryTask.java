/*
 * Created on Mar 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.util.WebClientException;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LoadInventoryTask extends TimeConsumingTask {
    private static final JdbcTemplate jdbcTemplate;
    private static final PlatformTransactionManager transactionManager;
    private static final String selectLmEventSql;
    private static final String selectLmHardwareSql;
    private static final String selectInventoryBaseSql;
    private static final ParameterizedRowMapper<LiteLMHardwareEvent> hardwareEventRowMapper;
	private final LiteStarsEnergyCompany energyCompany;
	private final int energyCompanyId;
    private int numInvLoaded = 0;
    private Map<Integer,List<LiteLMHardwareEvent>> invEventMap;
	
    static {
        
        jdbcTemplate = new JdbcTemplate(PoolManager.getYukonDataSource());
        
        transactionManager = new DataSourceTransactionManager(jdbcTemplate.getDataSource());
        
        selectLmEventSql = "SELECT totals.total,ce.EventID,EventTypeID,ActionID,EventDateTime,Notes,InventoryID " +
                           "FROM LMCustomerEventBase ce, LMHardwareEvent he, ECToLMCustomerEventMapping map, " +
                               "(SELECT COUNT(*) as total " +
                               "FROM LMCustomerEventBase ce, LMHardwareEvent he, ECToLMCustomerEventMapping map " +
                               "WHERE map.EnergyCompanyID = ? " +
                               "AND map.EventID = ce.EventID " +
                               "AND ce.EventID = he.EventID) totals " +
                           "WHERE map.EnergyCompanyID = ? " +
                           "AND map.EventID = ce.EventID " +
                           "AND ce.EventID = he.EventID " +
                           "ORDER BY ce.EventID";
        
        selectLmHardwareSql = "SELECT lmb.InventoryID,ManufacturerSerialNumber,LMHardwareTypeID,RouteID,ConfigurationID " + 
                              "FROM LMHardwareBase lmb, ECToInventoryMapping map " +
                              "WHERE map.EnergyCompanyID = ? " +
                              "AND map.InventoryID >= 0 " +
                              "AND map.InventoryID = lmb.InventoryID";
        
        selectInventoryBaseSql = "SELECT inv.InventoryID, AccountID, InstallationCompanyID, CategoryID, " +
                                    "ReceiveDate, InstallDate, RemoveDate, AlternateTrackingNumber, VoltageID, " +
                                    "Notes, DeviceID, DeviceLabel, CurrentStateID " +
                                 "FROM InventoryBase inv, ECToInventoryMapping map " +
                                 "WHERE map.EnergyCompanyID = ? " +
                                 "AND map.InventoryID >= 0 " +
                                 "AND map.InventoryID = inv.InventoryID" +
                                 " AND inv.InventoryID not in (select InventoryID from MeterHardwareBase)";
        
        hardwareEventRowMapper = createHardwareEventRowMapper();
        
    }
    
	public LoadInventoryTask(final LiteStarsEnergyCompany energyCompany) {
		this.energyCompany = energyCompany;
		this.energyCompanyId = energyCompany.getEnergyCompanyID();
    }

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
    @Override
	public String getProgressMsg() {
        if (status == STATUS_RUNNING) {
            if (numInvLoaded > 0)
                return numInvLoaded + " of items loaded in inventory";
            return "Preparing for loading the inventory...";
        }
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
        template.setReadOnly(true);
        template.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                doAction();
                return null;
            }
        });
	}
    
	private void doAction() {
	    if (energyCompany == null) {
	        status = STATUS_ERROR;
	        errorMsg = "Energy company cannot be null";
	        return;
	    }

	    status = STATUS_RUNNING;

	    try {
	        //Grab events first
	        jdbcTemplate.query(selectLmEventSql,
	                           new Object[]{energyCompanyId, energyCompanyId},
	                           new RowCallbackHandler() {
	            public void processRow(ResultSet rs) throws SQLException {
	                if (isCanceled) {
	                    status = STATUS_CANCELED;
	                    return;
	                }

	                if (invEventMap == null) {
	                    int rows = rs.getInt("total");
	                    int initialCap = (int) (rows / 0.75f);
	                    invEventMap = new HashMap<Integer,List<LiteLMHardwareEvent>>(initialCap);
	                }

	                final Integer inventoryId = rs.getInt("InventoryID");

	                List<LiteLMHardwareEvent> events = invEventMap.get(inventoryId);
	                if (events == null) {
	                    events = new ArrayList<LiteLMHardwareEvent>();
	                    invEventMap.put(inventoryId, events);
	                }

	                final LiteLMHardwareEvent event = hardwareEventRowMapper.mapRow(rs, rs.getRow());
	                events.add(event);
	            }
	        });

	        //Now do LMHardwareBase, these will be put in inventory map
	        jdbcTemplate.query(selectLmHardwareSql,
	                           new Object[]{energyCompanyId},
	                           new RowCallbackHandler() {
	            public void processRow(ResultSet rs) throws SQLException {
	                if (isCanceled) {
	                    status = STATUS_CANCELED;
	                    return;
	                }
	                loadLMHardwareInventory(rs);
	            }
	        });

	        /*
	         * Now make sure everybody both in the inventory map and 
	         * MCTs, LMhardware, get their inventoryBase information.  Either will be
	         * populated into the map or the current map value will be changed.
	         */
	        jdbcTemplate.query(selectInventoryBaseSql,
	                           new Object[]{energyCompanyId},
	                           new RowCallbackHandler() {
	            public void processRow(ResultSet rs) throws SQLException {
	                if (isCanceled) {
	                    status = STATUS_CANCELED;
	                    return;
	                }
	                loadInventoryBase(rs);
	                numInvLoaded++;
	            }
	        });

	        energyCompany.setInventoryLoaded( true );
	        status = STATUS_FINISHED;
	        CTILogger.info( "All inventory loaded for energy company #" + energyCompanyId);
	    }
	    catch (Exception e) {
	        CTILogger.error(e.getMessage(), e);
	        status = STATUS_ERROR;

	        if (e instanceof WebClientException)
	            errorMsg = e.getMessage();
	        else
	            errorMsg = "Failed to load inventory";
	    }
	}
	
    private void loadLMHardwareInventory(ResultSet rs) throws SQLException {
        final int inventoryId = rs.getInt("InventoryID");
        LiteInventoryBase liteInv = energyCompany.getInventoryFromMap(inventoryId);

        if (liteInv == null) {
            liteInv = new LiteStarsLMHardware();
            liteInv.setInventoryID(inventoryId);
            energyCompany.addInventory(liteInv);    
        }
        
        if (!(liteInv instanceof LiteStarsLMHardware)) return;
           
        LiteStarsLMHardware liteStarsLMHardware = (LiteStarsLMHardware) liteInv;
        liteStarsLMHardware.setManufacturerSerialNumber(rs.getString("ManufacturerSerialNumber"));
        liteStarsLMHardware.setLmHardwareTypeID(rs.getInt("LMHardwareTypeID"));
        liteStarsLMHardware.setRouteID(rs.getInt("RouteID"));
        liteStarsLMHardware.setConfigurationID(rs.getInt("ConfigurationID"));
    }
    
    private void loadInventoryBase(ResultSet rs) throws SQLException {
        final int inventoryId = rs.getInt("InventoryID");
        LiteInventoryBase liteInv = energyCompany.getInventoryFromMap(inventoryId);
        
        if (liteInv == null) {
            liteInv = new LiteInventoryBase();
            liteInv.setInventoryID(inventoryId);
            energyCompany.addInventory(liteInv);
        }
        
        liteInv.setInventoryID(inventoryId);
        liteInv.setAccountID(rs.getInt("AccountID"));
        liteInv.setInstallationCompanyID(rs.getInt("InstallationCompanyID"));
        liteInv.setCategoryID(rs.getInt("CategoryID") );
        if (rs.getTimestamp("ReceiveDate") != null)
            liteInv.setReceiveDate(rs.getTimestamp("ReceiveDate").getTime());
        if (rs.getTimestamp("InstallDate") != null)
            liteInv.setInstallDate(rs.getTimestamp("InstallDate").getTime());
        if (rs.getTimestamp("RemoveDate") != null)
            liteInv.setRemoveDate( rs.getTimestamp("RemoveDate").getTime());
        liteInv.setAlternateTrackingNumber(rs.getString("AlternateTrackingNumber"));
        liteInv.setVoltageID(rs.getInt("VoltageID"));
        liteInv.setNotes(rs.getString("Notes"));
        liteInv.setDeviceID(rs.getInt("DeviceID"));
        liteInv.setDeviceLabel(rs.getString("DeviceLabel"));
        liteInv.setCurrentStateID(rs.getInt("CurrentStateID"));
        
        if (invEventMap != null) {
            List<LiteLMHardwareEvent> events = invEventMap.get(inventoryId);
            if (events != null) {
                liteInv.updateDeviceStatus(events);
            } else {
                liteInv.setDeviceStatus(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL);
            }
            return;
        }

        liteInv.updateDeviceStatus();
    }

    private static ParameterizedRowMapper<LiteLMHardwareEvent> createHardwareEventRowMapper() {
        final ParameterizedRowMapper<LiteLMHardwareEvent> mapper = new ParameterizedRowMapper<LiteLMHardwareEvent>() {
            public LiteLMHardwareEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
                final LiteLMHardwareEvent event = new LiteLMHardwareEvent();
                event.setInventoryID(rs.getInt("InventoryID"));
                event.setEventID(rs.getInt("EventID"));
                event.setEventTypeID(rs.getInt("EventTypeID"));
                event.setActionID(rs.getInt("ActionID"));
                event.setEventDateTime(rs.getTimestamp("EventDateTime").getTime());
                event.setNotes(rs.getString("Notes"));
                return event;
            }
        };
        return mapper;
    }
    
}
