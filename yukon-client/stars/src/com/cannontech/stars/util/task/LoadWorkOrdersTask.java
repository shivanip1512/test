/*
 * Created on Mar 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.stars.event.EventWorkOrder;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LoadWorkOrdersTask extends TimeConsumingTask {
    private static final JdbcTemplate jdbcTemplate;
    private static final PlatformTransactionManager transactionManager;
    private static final String selectSql;
    private static final String selectEventWorkOrder;
    private static final ParameterizedRowMapper<EventWorkOrder> eventWorkOrderRowMapper;
    private static final ParameterizedRowMapper<LiteWorkOrderBase> workOrderBaseRowMapper;
    
	private final LiteStarsEnergyCompany energyCompany;
	private int numOrderLoaded;

    static {

        jdbcTemplate = new JdbcTemplate(PoolManager.getYukonDataSource());
        
        transactionManager = new DataSourceTransactionManager(jdbcTemplate.getDataSource());
        
        selectSql = "SELECT OrderID,OrderNumber,WorkTypeID,CurrentStateID,ServiceCompanyID,DateReported, OrderedBy," +
                    "Description,DateScheduled,DateCompleted,ActionTaken,AdditionalOrderNumber," +
                    "AccountID,EnergyCompanyID " +
                    "FROM WorkOrderBase wo, ECToWorkOrderMapping map " +
                    "WHERE map.EnergyCompanyID = ? " +
                    "AND map.WorkOrderID = wo.OrderID";
        
        selectEventWorkOrder = "SELECT EB.EVENTID,USERID,SYSTEMCATEGORYID,ACTIONID,EVENTTIMESTAMP,ORDERID " +
                               "FROM EventBase EB,EventWorkOrder EWO,ECToWorkOrderMapping map " + 
                               "WHERE EB.EVENTID = EWO.EVENTID " +
                               "AND map.EnergyCompanyID = ? " +
                               "AND MAP.WORKORDERID = EWO.ORDERID " +
                               "ORDER BY EB.EVENTID, EVENTTIMESTAMP";
        
        eventWorkOrderRowMapper = createEventWorkOrderRowMapper();
        
        workOrderBaseRowMapper = createWorkOrderBaseRowMapper(); 
        
    }
    
	public LoadWorkOrdersTask(final LiteStarsEnergyCompany energyCompany) {
		this.energyCompany = energyCompany;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
    @Override
    public String getProgressMsg() {
        if (status == STATUS_RUNNING) {
            if (numOrderLoaded > 0) {
                return numOrderLoaded + " work orders loaded";
            }    
            return "Preparing for loading the work orders...";
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
	        jdbcTemplate.query(
	                           selectSql,
	                           new Object[]{energyCompany.getEnergyCompanyID()},
	                           new RowCallbackHandler() {
	                               public void processRow(ResultSet rs) throws SQLException {
	                                   if (isCanceled) {
	                                       status = STATUS_CANCELED;
	                                       return;
	                                   }

	                                   loadWorkOrder(rs);
	                                   numOrderLoaded++;
	                               }
	                           });

	        jdbcTemplate.query(
	                           selectEventWorkOrder,
	                           new Object[]{energyCompany.getEnergyCompanyID()},
	                           new RowCallbackHandler() {
	                               public void processRow(ResultSet rs) throws SQLException {
	                                   if (isCanceled) {
	                                       status = STATUS_CANCELED;
	                                       return;
	                                   }

	                                   final int orderId = rs.getInt("ORDERID");

	                                   LiteWorkOrderBase liteWorkOrderBase = energyCompany.getWorkOrderBase(orderId, false);
	                                   if (liteWorkOrderBase != null) {
	                                       EventWorkOrder eventWorkOrder = eventWorkOrderRowMapper.mapRow(rs, rs.getRow());
	                                       liteWorkOrderBase.getEventWorkOrders().add(0, eventWorkOrder);
	                                   } else {
	                                       CTILogger.error("WorkOrderID: " + orderId + " - Not loaded for EnergyCompanyID: " + energyCompany.getEnergyCompanyID());
	                                   }
	                               }
	                           });

	        energyCompany.setWorkOrdersLoaded( true );
	        status = STATUS_FINISHED;
	        CTILogger.info("All work orders loaded for energy company #" + energyCompany.getEnergyCompanyID());
	    } catch (Exception e) {
	        CTILogger.error( e.getMessage(), e );
	        status = STATUS_ERROR;
	        errorMsg = "Failed to load work orders";
	    }
	}
	
	private void loadWorkOrder(java.sql.ResultSet rs) throws java.sql.SQLException {
		final int orderID = rs.getInt( "OrderID" );
		
		if (energyCompany.getWorkOrderBase(orderID, false) != null) return; // work order already loaded
			
		LiteWorkOrderBase liteOrder = workOrderBaseRowMapper.mapRow(rs, rs.getRow());
		energyCompany.addWorkOrderBase(liteOrder);
	}
    
    private static ParameterizedRowMapper<LiteWorkOrderBase> createWorkOrderBaseRowMapper() {
        final ParameterizedRowMapper<LiteWorkOrderBase> mapper = new ParameterizedRowMapper<LiteWorkOrderBase>() {
            public LiteWorkOrderBase mapRow(ResultSet rs, int rowNum) throws SQLException {
                final LiteWorkOrderBase liteOrder = new LiteWorkOrderBase();
                liteOrder.setOrderID(rs.getInt("OrderID"));
                liteOrder.setOrderNumber(rs.getString("OrderNumber"));
                liteOrder.setWorkTypeID(rs.getInt("WorkTypeID"));
                liteOrder.setCurrentStateID(rs.getInt("CurrentStateID"));
                liteOrder.setServiceCompanyID(rs.getInt("ServiceCompanyID"));
                liteOrder.setDateReported(rs.getTimestamp("DateReported").getTime());
                liteOrder.setOrderedBy(rs.getString("OrderedBy"));
                liteOrder.setDescription(rs.getString("Description"));
                liteOrder.setDateScheduled(rs.getTimestamp("DateScheduled").getTime());
                liteOrder.setDateCompleted(rs.getTimestamp("DateCompleted").getTime());
                liteOrder.setActionTaken(rs.getString("ActionTaken"));
                liteOrder.setAdditionalOrderNumber(rs.getString("AdditionalOrderNumber"));
                liteOrder.setAccountID(rs.getInt("AccountID"));
                liteOrder.setEnergyCompanyID(rs.getInt("EnergyCompanyID"));
                return liteOrder;
            }
        };
        return mapper;
    }
    
	private static ParameterizedRowMapper<EventWorkOrder> createEventWorkOrderRowMapper() {
	    final ParameterizedRowMapper<EventWorkOrder> mapper = new ParameterizedRowMapper<EventWorkOrder>() {
	        public EventWorkOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
	            final EventWorkOrder eventWorkOrder = new EventWorkOrder();
	            eventWorkOrder.setEventID(rs.getInt("EVENTID"));
	            eventWorkOrder.getEventBase().setUserID(rs.getInt("USERID"));
	            eventWorkOrder.getEventBase().setSystemCategoryID(rs.getInt("SYSTEMCATEGORYID"));
	            eventWorkOrder.getEventBase().setActionID(rs.getInt("ACTIONID"));
	            eventWorkOrder.getEventBase().setEventTimestamp( new Date(rs.getTimestamp("EVENTTIMESTAMP").getTime() ));                
	            eventWorkOrder.getEventWorkOrder().setWorkOrderID(rs.getInt("ORDERID"));
	            return eventWorkOrder;
	        }
	    };
	    return mapper;
	}     
}