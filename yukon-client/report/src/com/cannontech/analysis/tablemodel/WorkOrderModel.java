/*
 * Created on Dec 27, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis.tablemodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.stars.WorkOrder;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkOrderModel extends ReportModelBase {
	
	/** A string for the title of the data */
	private static String title = "Work Order";
	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 26;
	
	/** Enum values for column representation */
	public final static int EC_NAME_COLUMN = 0;
	public final static int EC_INFO_COLUMN = 1;
	public final static int ORDER_NO_COLUMN = 2;
	public final static int DATE_TIME_TODAY_COLUMN = 3;
	public final static int DATE_TIME_REPORTED_COLUMN = 4;
	public final static int DATE_SCHEDULED_COLUMN = 5;
	public final static int TIME_SCHEDULED_COLUMN = 6;
	public final static int DATE_TIME_CLOSED_COLUMN = 7;
	public final static int SERVICE_TYPE_COLUMN = 8;
	public final static int SERVICE_COMPANY_COLUMN = 9;
	
	public final static int ACCOUNT_NO_COLUMN = 10;
	public final static int NAME_COLUMN = 11;
	public final static int PHONE_HOME_COLUMN = 12;
	public final static int PHONE_WORK_COLUMN = 13;
	public final static int ADDRESS1_COLUMN = 14;
	public final static int ADDRESS2_COLUMN = 15;
	public final static int CITY_STATE_ZIP_COLUMN = 16;
//	public final static int STATE_COLUMN = ;
//	public final static int ZIP_COLUMN = ;
	
	public final static int COMPANY_NAME_COLUMN = 17;
	public final static int MAP_NO_COLUMN = 18;
	public final static int METER_NO_COLUMN = 19;
	public final static int ACCOUNT_NOTES_COLUMN = 20;
	
	public final static int SERIAL_NO_COLUMN = 21;
	public final static int DEVICE_TYPE_COLUMN = 22;
	public final static int INSTALL_DATE_COLUMN = 23;
	public final static int INSTALL_COMPANY_COLUMN = 24;

	public final static int WORK_DESC_COLUMN = 25;
	public final static int ACTION_TAKEN_COLUMN = 26;

	public final static int HEADER_START_INDEX = ORDER_NO_COLUMN;
	public final static int HEADER_END_INDEX = ACCOUNT_NOTES_COLUMN; 
	public final static int ITEM_BAND_START_INDEX = SERIAL_NO_COLUMN;
	public final static int ITEM_BAND_END_INDEX = INSTALL_COMPANY_COLUMN;
	public final static int FOOTER_START_INDEX = WORK_DESC_COLUMN;
	public final static int FOOTER_END_INDEX = ACTION_TAKEN_COLUMN;
	
	/** String values for column representation */
	public final static String EC_NAME_STRING = "Energy Company Name";
	public final static String EC_INFO_STRING = "Energy Company Info";
	public final static String ORDER_NO_STRING = "Order Number";
	public final static String DATE_TIME_TODAY_STRING = "Today's Date";
	public final static String DATE_TIME_REPORTED_STRING = "Date Reported";
	public final static String DATE_SCHEDULED_STRING = "Date Scheduled";
	public final static String TIME_SCHEDULED_STRING = "Time Scheduled";
	public final static String DATE_TIME_CLOSED_STRING = "Date Closed";
	public final static String SERVICE_TYPE_STRING = "Type";
	public final static String SERVICE_COMPANY_STRING = "Srv. Company";
	public final static String WORK_DESC_STRING = "Work Description";
	public final static String ACTION_TAKEN_STRING = "Action Taken";
	
	public final static String ACCOUNT_NO_STRING = "Account Number";
	public final static String NAME_STRING = "Name";
	public final static String PHONE_HOME_STRING = "Home Phone";
	public final static String PHONE_WORK_STRING = "Work Phone";
	public final static String ADDRESS1_STRING = "Address";
	public final static String ADDRESS2_STRING = "Address ";
	public final static String CITY_STATE_ZIP_STRING = "City State Zip";
//	public final static String STATE_STRING = "State";
//	public final static String ZIP_STRING = "Zip";
	
	public final static String COMPANY_NAME_STRING = "Company Name";
	public final static String MAP_NO_STRING = "Map Number";
	public final static String METER_NO_STRING = "Meter Number";
	public final static String ACCOUNT_NOTES_STRING = "Account Notes";
	
	public final static String SERIAL_NO_STRING = "Serial No";
	public final static String DEVICE_TYPE_STRING = "Device Type";
	public final static String INSTALL_DATE_STRING = "Install Date";
	public final static String INSTALL_COMPANY_STRING = "Installer";
	
	/** Enum values for search column */
	public final static int SEARCH_COL_NONE = 0;
	public final static int SEARCH_COL_DATE_REPORTED = 1;
	public final static int SEARCH_COL_DATE_SCHEDULED = 2;
	public final static int SEARCH_COL_DATE_CLOSED = 3;
	
	private int searchColumn = SEARCH_COL_NONE;
	
	private Integer orderID = null;
	private Integer accountID = null;
	private Integer serviceStatus = null;
	
	private final String ATT_SEARCH_COL = "searchCol";
	
	private HashMap accountIDToMeterNumberMap = null;
	
	public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
	public static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
	
	public static final Comparator workOrderCmptor = new Comparator() {
		public int compare(Object o1, Object o2) {
			WorkOrder wo1 = (WorkOrder) o1;
			WorkOrder wo2 = (WorkOrder) o2;
			
			if (wo1.getEnergyCompanyID() != wo2.getEnergyCompanyID())
				return wo1.getEnergyCompanyID() - wo2.getEnergyCompanyID();
			
			LiteStarsEnergyCompany ec = StarsDatabaseCache.getInstance().getEnergyCompany( wo1.getEnergyCompanyID() );
			LiteWorkOrderBase lOrder1 = ec.getWorkOrderBase( wo1.getOrderID(), true );
			LiteWorkOrderBase lOrder2 = ec.getWorkOrderBase( wo2.getOrderID(), true );
			
			int result = 0;
			
			// First compare order numbers as numeric values
			Long on1 = null;
			try {
				on1 = Long.valueOf( lOrder1.getOrderNumber() );
			}
			catch (NumberFormatException e) {}
			
			Long on2 = null;
			try {
				on2 = Long.valueOf( lOrder2.getOrderNumber() );
			}
			catch (NumberFormatException e) {}
			
			if (on1 != null && on2 != null) {
				result = on1.compareTo( on2 );
				if (result == 0) result = lOrder1.getOrderNumber().compareTo( lOrder2.getOrderNumber() );
			}
			else if (on1 != null && on2 == null)
				return -1;
			else if (on1 == null && on2 != null)
				return 1;
			else
				result = lOrder1.getOrderNumber().compareTo( lOrder2.getOrderNumber() );
			
			// If order numbers are the same, compare order IDs
			if (result == 0) result = lOrder1.getOrderID() - lOrder2.getOrderID();
			
			if (result == 0) {
				// If order IDs are the same, compare serial numbers as numeric values
				String serialNo1 = "(none)";
				String serialNo2 = "(none)";
				
				Long sn1 = null;
				try {
					if (wo1.getInventoryID() > 0) {
						serialNo1 = ((LiteStarsLMHardware) ec.getInventoryBrief(wo1.getInventoryID(), true)).getManufacturerSerialNumber();
						sn1 = Long.valueOf( serialNo1 );
					}
				}
				catch (NumberFormatException e) {}
				
				Long sn2 = null;
				try {
					if (wo2.getInventoryID() > 0) {
						serialNo2 = ((LiteStarsLMHardware) ec.getInventoryBrief(wo2.getInventoryID(), true)).getManufacturerSerialNumber();
						sn2 = Long.valueOf( serialNo2 );
					}
				}
				catch (NumberFormatException e) {}
				
				if (sn1 != null && sn2 != null) {
					result = sn1.compareTo( sn2 );
					if (result == 0) result = serialNo1.compareTo( serialNo2 );
				}
				else if (sn1 != null && sn2 == null)
					return -1;
				else if (sn1 == null && sn2 != null)
					return 1;
				else
					result = serialNo1.compareTo( serialNo2 );
			}
			
			return result;
		}
	};
	
	public WorkOrderModel() {
		super();
	}
	
	public WorkOrderModel(Integer ecID) {
		this(ecID, null, SEARCH_COL_NONE, null, null);
	}
	
	public WorkOrderModel(Integer ecID, Integer orderID) {
		this( ecID , orderID, SEARCH_COL_NONE, null, null);
	}
	
	public WorkOrderModel(Integer ecID, Integer orderID, int searchColumn, Date start_, Date stop_) {
		super(start_, stop_);
		setECIDs( ecID );
		setOrderID( orderID );
		setSearchColumn( searchColumn );
	}

	/**
	 * @return
	 */
	public int getSearchColumn() {
		return searchColumn;
	}

	/**
	 * @param i
	 */
	public void setSearchColumn(int i) {
		searchColumn = i;
	}

	/**
	 * @return
	 */
	public Integer getAccountID() {
		return accountID;
	}

	/**
	 * @return
	 */
	public Integer getOrderID() {
		return orderID;
	}

	/**
	 * @return
	 */
	public Integer getServiceStatus() {
		return serviceStatus;
	}

	/**
	 * @param integer
	 */
	public void setAccountID(Integer integer) {
		accountID = integer;
	}

	/**
	 * @param integer
	 */
	public void setOrderID(Integer integer) {
		orderID = integer;
	}

	/**
	 * @param integer
	 */
	public void setServiceStatus(Integer integer) {
		serviceStatus = integer;
	}

	/**
	 * Build a mapping of AccountID(Integer):String).
	 * @return String an Sqlstatement
	 */
	public HashMap getAccountIDToMeterNumberMap() throws java.sql.SQLException
	{
		if (accountIDToMeterNumberMap == null )
		{
			String sql = "";
			if (getOrderID() != null) {
				sql = "SELECT inv.AccountID, dmg.MeterNumber" +
					" FROM WorkOrderBase wo, InventoryBase inv, DeviceMeterGroup dmg" +
					" WHERE wo.OrderID = " + getOrderID() + " AND wo.AccountID = inv.AccountID" +
					" AND inv.DeviceID > 0 AND inv.DeviceID = dmg.DeviceID" +
					" ORDER BY inv.InventoryID";
			}
			else if (getAccountID() != null) {
				sql =  "SELECT inv.AccountID, dmg.MeterNumber" +
					" FROM InventoryBase inv, DeviceMeterGroup dmg" +
					" WHERE inv.AccountID = " + getAccountID() + " AND inv.DeviceID > 0 AND inv.DeviceID = dmg.DeviceID" +
					" ORDER BY inv.InventoryID";
			}
			else {
				sql = "SELECT inv.AccountID, dmg.MeterNumber" +
					" FROM InventoryBase inv, DeviceMeterGroup dmg, ECToInventoryMapping map" +
					" WHERE inv.DeviceID > 0 AND inv.DeviceID = dmg.DeviceID AND inv.InventoryID = map.InventoryID";
				if (getECIDs() != null) {
					sql += " AND map.EnergyCompanyID IN (" + getECIDs()[0];
					for (int i = 1; i < getECIDs().length; i++)
						sql += ", " + getECIDs()[i];
					sql += ")";
				}
				sql += " ORDER BY inv.InventoryID";
			}		
			CTILogger.info( sql );
	
			java.sql.Connection conn = null;
			java.sql.PreparedStatement pstmt = null;
			java.sql.ResultSet rset = null;
			
			try {
				conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
				
				if( conn == null ) {
					CTILogger.error(getClass() + ":  Error getting database connection.");
					return null;
				}
				
				pstmt = conn.prepareStatement( sql );
				rset = pstmt.executeQuery();
				accountIDToMeterNumberMap = new HashMap();
				while (rset.next()) {
					Integer accountID = new Integer(rset.getInt(1));
					String meterNumber = rset.getString(2);
					if (accountIDToMeterNumberMap.get( accountID ) == null)
						accountIDToMeterNumberMap.put(accountID, meterNumber);
				}
				
				CTILogger.info( "AccountID:MeterNumber records collected from database: " + accountIDToMeterNumberMap.size());
			}
			finally {
				try {
					if (pstmt != null) pstmt.close();
					if (conn != null) conn.close();
					if (rset != null) rset.close();
				}
				catch (java.sql.SQLException e) {
					CTILogger.error( e.getMessage(), e );
				}
			}
		}
		
		return accountIDToMeterNumberMap;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.tablemodel.ReportModelBase#collectData()
	 */
	public void collectData() {
		
		//Reset all objects, new data being collected!
		setData(null);
		
		if (getECIDs() == null) return;
		
		for (int i = 0; i < getECIDs().length; i++) {
			LiteStarsEnergyCompany ec = StarsDatabaseCache.getInstance().getEnergyCompany( getECIDs()[i] );
			ArrayList woList = new ArrayList();
			
			if (getOrderID() != null) {
				LiteWorkOrderBase liteOrder = ec.getWorkOrderBase( getOrderID().intValue(), true );
				if (liteOrder != null) woList.add( liteOrder );
			}
			else if (getAccountID() != null) {
				LiteStarsCustAccountInformation liteAcctInfo = ec.getCustAccountInformation( getAccountID().intValue(), true );
				if (liteAcctInfo != null) {
					for (int j = 0; j < liteAcctInfo.getServiceRequestHistory().size(); j++) {
						Integer orderID = (Integer) liteAcctInfo.getServiceRequestHistory().get(j);
						woList.add( ec.getWorkOrderBase(orderID.intValue(), true) );
					}
				}
			}
			else {
				ArrayList allWOs = ec.loadAllWorkOrders(true);
				if( allWOs != null)
					woList.addAll( allWOs );
			}
			
			if (getServiceStatus() != null && getServiceStatus().intValue() > 0) {
				Iterator it = woList.iterator();
				while (it.hasNext()) {
					LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) it.next();
					if (liteOrder.getCurrentStateID() != getServiceStatus().intValue())
						it.remove();
				}
			}
			
			if (getSearchColumn() != SEARCH_COL_NONE) {
				Iterator it = woList.iterator();
				while (it.hasNext()) {
					LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) it.next();
					
					long timestamp = 0;
					if (getSearchColumn() == SEARCH_COL_DATE_REPORTED)
						timestamp = liteOrder.getDateReported();
					else if (getSearchColumn() == SEARCH_COL_DATE_SCHEDULED)
						timestamp = liteOrder.getDateScheduled();
					else if (getSearchColumn() == SEARCH_COL_DATE_CLOSED)
						timestamp = liteOrder.getDateCompleted();
					
					if (timestamp < getStartDate().getTime() || timestamp > getStopDate().getTime())
						it.remove();
				}
			}
			
			for (int j = 0; j < woList.size(); j++) {
				LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) woList.get(j);
				
				if (liteOrder.getAccountID() == 0) {
					WorkOrder wo = new WorkOrder( ec.getLiteID(), liteOrder.getOrderID(), 0 );
					getData().add( wo );
				}
				else {
					LiteStarsCustAccountInformation liteAcctInfo = ec.getCustAccountInformation( liteOrder.getAccountID(), true );
					
					if (liteAcctInfo.getInventories().size() == 0) {
						WorkOrder wo = new WorkOrder( ec.getLiteID(), liteOrder.getOrderID(), 0 );
						getData().add( wo );
					}
					else {
						for (int k = 0; k < liteAcctInfo.getInventories().size(); k++) {
							int invID = ((Integer) liteAcctInfo.getInventories().get(k)).intValue();
							if (ec.getInventoryBrief(invID, true) instanceof LiteStarsLMHardware) {
								WorkOrder wo = new WorkOrder( ec.getLiteID(), liteOrder.getOrderID(), invID );
								getData().add( wo );
							}
						}
					}
				}
			}
		}
		
		Collections.sort( getData(), workOrderCmptor );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o) {
		if (o instanceof WorkOrder) {
			WorkOrder wo = (WorkOrder) o;
			
			LiteStarsEnergyCompany ec = StarsDatabaseCache.getInstance().getEnergyCompany( wo.getEnergyCompanyID() );
			LiteWorkOrderBase lOrder = ec.getWorkOrderBase( wo.getOrderID(), true );
			
			LiteStarsCustAccountInformation lAcctInfo = null;
			LiteContact lc = null;
			LiteAddress la = null;
			if (lOrder.getAccountID() > 0) {
				lAcctInfo = ec.getCustAccountInformation( lOrder.getAccountID(), true );
				lc = ContactFuncs.getContact( lAcctInfo.getCustomer().getPrimaryContactID() );
				la = ec.getAddress( lAcctInfo.getAccountSite().getStreetAddressID() );
			}
			
			LiteStarsLMHardware lHw = null;
			if (wo.getInventoryID() > 0)
				lHw = (LiteStarsLMHardware) ec.getInventoryBrief( wo.getInventoryID(), true );
			
			switch (columnIndex) {
				case EC_NAME_COLUMN:
					return ec.getName();
				case EC_INFO_COLUMN:
					String returnStr = "WORK ORDER";
					LiteContact lc_ec = ContactFuncs.getContact( ec.getPrimaryContactID() );
					if( lc_ec != null)
					{
						LiteAddress lAddr = ec.getAddress( lc_ec.getAddressID() );
						if (lAddr != null) {
							returnStr += "\r\n" + lAddr.getLocationAddress1();
							if (StarsUtils.forceNotNone(lAddr.getLocationAddress2()).length() > 0)
								returnStr += "\r\n" + lAddr.getLocationAddress2();
							returnStr += "\r\n" + lAddr.getCityName()+ "  " + lAddr.getStateCode() + "  " + lAddr.getZipCode();
						}
						if(  ContactFuncs.getContactNotification(lc_ec, YukonListEntryTypes.YUK_ENTRY_ID_PHONE) != null)
							returnStr += "\r\n" + ContactFuncs.getContactNotification(lc_ec, YukonListEntryTypes.YUK_ENTRY_ID_PHONE);
					}
					return returnStr;
				case ORDER_NO_COLUMN:
					return lOrder.getOrderNumber();
				case DATE_TIME_TODAY_COLUMN:
					return ServletUtils.formatDate( new Date(), dateFormatter );
				case DATE_TIME_REPORTED_COLUMN:
					return ServletUtils.formatDate( new Date(lOrder.getDateReported()), dateFormatter );
				case DATE_SCHEDULED_COLUMN:
					return ServletUtils.formatDate( new Date(lOrder.getDateScheduled()), dateFormatter );
				case TIME_SCHEDULED_COLUMN:
					return ServletUtils.formatDate( new Date(lOrder.getDateScheduled()), timeFormatter );
				case DATE_TIME_CLOSED_COLUMN:
					return ServletUtils.formatDate( new Date(lOrder.getDateCompleted()), dateFormatter );
				case SERVICE_TYPE_COLUMN:
					return YukonListFuncs.getYukonListEntry(lOrder.getWorkTypeID()).getEntryText();
				case SERVICE_COMPANY_COLUMN:
					LiteServiceCompany sc = ec.getServiceCompany( lOrder.getServiceCompanyID() );
					if (sc != null)
						return sc.getCompanyName();
					else
						return "(none)";
				case WORK_DESC_COLUMN:
					return lOrder.getDescription();
				case ACTION_TAKEN_COLUMN:
					return lOrder.getActionTaken();
				case ACCOUNT_NO_COLUMN:
					if (lAcctInfo != null)
						return lAcctInfo.getCustomerAccount().getAccountNumber();
					else
						return "";
				case NAME_COLUMN:
					if (lc != null)
						return lc.getContLastName()+ ", "+ lc.getContFirstName();
					else
						return "";
				case PHONE_HOME_COLUMN:
					if (lc != null)
						return ContactFuncs.getContactNotification(lc, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
					else
						return "";
				case PHONE_WORK_COLUMN:
					if (lc != null)
						return ContactFuncs.getContactNotification(lc, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);
					else
						return "";
				case ADDRESS1_COLUMN:
					if (la != null)
						return la.getLocationAddress1();
					else
						return "";
				case ADDRESS2_COLUMN:
					if (la != null)
						return la.getLocationAddress2();
					else
						return "";					
				case CITY_STATE_ZIP_COLUMN:
					if (la != null)
						return la.getCityName()+ "   " + la.getStateCode() + "   " + la.getZipCode();
					else
						return "";
//				case STATE_COLUMN:
//					return "";
//				case ZIP_COLUMN:
//					return "";
				case COMPANY_NAME_COLUMN:
					if (lAcctInfo != null && lAcctInfo.getCustomer() instanceof LiteCICustomer)
						return ((LiteCICustomer)lAcctInfo.getCustomer()).getCompanyName();
					else
						return "";
				case MAP_NO_COLUMN:
					if (lAcctInfo != null)
						return lAcctInfo.getAccountSite().getSiteNumber();
					else
						return "";
				case METER_NO_COLUMN:
					if (lAcctInfo != null) {
						String meterNo = null;
						try {
							meterNo = (String)getAccountIDToMeterNumberMap().get( new Integer(lAcctInfo.getAccountID()) );
						}
						catch (java.sql.SQLException e) {
							CTILogger.error( e.getMessage(), e );
						}
						
						if (meterNo != null)
							return meterNo;
						else
							return "N/A";
					}
					else
						return "";
				case ACCOUNT_NOTES_COLUMN:
					if (lAcctInfo != null)
						return lAcctInfo.getCustomerAccount().getAccountNotes();
					else
						return "";
				case SERIAL_NO_COLUMN:
					if (lHw != null)
						return lHw.getManufacturerSerialNumber();
					else
						return "(none)";
				case DEVICE_TYPE_COLUMN:
					if (lHw != null)
						return YukonListFuncs.getYukonListEntry(lHw.getLmHardwareTypeID()).getEntryText();
					else
						return "";
				case INSTALL_DATE_COLUMN:
					if (lHw != null)
						return ServletUtils.formatDate( new Date(lHw.getInstallDate()), dateFormatter );
					else
						return "";
				case INSTALL_COMPANY_COLUMN:
					if (lHw != null) {
						LiteServiceCompany ic = ec.getServiceCompany( lHw.getInstallationCompanyID() );
						if (ic != null)
							return ic.getCompanyName();
						else
							return "(none)";
					}
					else
						return "";
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnNames()
	 */
	public String[] getColumnNames() {
		if (columnNames == null) {
			columnNames = new String[] {
				//HEADER
				EC_NAME_STRING,
				EC_INFO_STRING,
				ORDER_NO_STRING,
				DATE_TIME_TODAY_STRING,
				DATE_TIME_REPORTED_STRING,
				DATE_SCHEDULED_STRING,
				TIME_SCHEDULED_STRING,
				DATE_TIME_CLOSED_STRING,
				SERVICE_TYPE_STRING,
				SERVICE_COMPANY_STRING,
				ACCOUNT_NO_STRING,
				NAME_STRING,
				PHONE_HOME_STRING,
				PHONE_WORK_STRING,
				ADDRESS1_STRING,
				ADDRESS2_STRING,
				CITY_STATE_ZIP_STRING,
//				STATE_STRING,
//				ZIP_STRING,
				COMPANY_NAME_STRING,
				MAP_NO_STRING,
				METER_NO_STRING,
				ACCOUNT_NOTES_STRING,
				//ITEM BAND
				SERIAL_NO_STRING,
				DEVICE_TYPE_STRING,
				INSTALL_DATE_STRING,
				INSTALL_COMPANY_STRING,
				//FOOTER
				WORK_DESC_STRING,
				ACTION_TAKEN_STRING
			};
		}
		
		return columnNames;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
	public Class[] getColumnTypes() {
		if (columnTypes == null) {
			columnTypes = new Class[] {
				String.class,//0
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,//5
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,//10
				String.class,
				String.class,
				String.class,
//				String.class,
//				String.class,
				String.class,
				String.class,//15
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,//20
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,//25
				String.class,
				String.class
			};
		}
		
		return columnTypes;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties() {
		int colHeight = 14;
		if (columnProperties == null) {
			int offset = 0;
			int _home = 0;
			int _1third = 184;
			int _2third = _1third*2;
			int _1half = 276;
			int _1fourth = 138;
			int _thirdWidth = 180;
			int _halfWidth = 270;
			int _wholeWidth = 500;
			int _fourthWidth = 130;
			
			int _yPos = 1; 
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(_1third, 1, _thirdWidth,null),		//EC_NAME_STRING,
				new ColumnProperties(_1third, (colHeight*1), _thirdWidth, null),		//EC_INFO_STRING,
				//HEADER
				new ColumnProperties(_2third, (colHeight*1), _thirdWidth, null),		//ORDER_NO_STRING,
				new ColumnProperties(_2third, (colHeight*2), _thirdWidth, null),		//DATE_TIME_TODAY_STRING,
				new ColumnProperties(_home, (colHeight*4), _thirdWidth, null),		//DATE_TIME_REPORTED_STRING,
				new ColumnProperties(_2third, (colHeight*4), _thirdWidth, null),		//DATE_SCHEDULED_STRING,
				new ColumnProperties(_2third, (colHeight*5), _thirdWidth, null),		//TIME_SCHEDULED_STRING,
				new ColumnProperties(_home, (colHeight*5), _thirdWidth, null),		//DATE_TIME_CLOSED_STRING,
				new ColumnProperties(_1half, (colHeight*7), _halfWidth, null),		//SERVICE_TYPE_STRING,
				new ColumnProperties(_1half, (colHeight*14), _halfWidth, null),		//CONTRACTOR_STRING,
				new ColumnProperties(_home, (colHeight*7), _halfWidth, null),		//ACCOUNT_NO_STRING,
				new ColumnProperties(_home, (colHeight*9), _halfWidth, null),		//NAME_STRING,
				new ColumnProperties(_1half, (colHeight*9), _halfWidth,  null),		//PHONE_HOME_STRING,
				new ColumnProperties(_1half, (colHeight*10), _halfWidth, null),		//PHONE_WORK_STRING,
				new ColumnProperties(_home, (colHeight*11), _halfWidth, null),		//ADDRESS1_STRING,				
				new ColumnProperties(_home, (colHeight*12), _halfWidth, null),		//ADDRESS2_STRING,
				new ColumnProperties(_home, (colHeight*13), _thirdWidth, null),		//CITY_STATE_ZIP_STRING,
//				new ColumnProperties(_1third, (colHeight*13), _thirdWidth, null),	//STATE_STRING,
//				new ColumnProperties(_2third, (colHeight*13), _thirdWidth, null),	//ZIP_STRING,
				new ColumnProperties(_home, (colHeight*10), _halfWidth, null),		//COMPANY_NAME_STRING,
				new ColumnProperties(_1half, (colHeight*12), _halfWidth, null),		//MAP_NO_STRING,
				new ColumnProperties(_home, (colHeight*15), _wholeWidth, null),		//METER_NO_STRING,				
				new ColumnProperties(_home, (colHeight*17), _wholeWidth, null),		//ACCOUNT_NOTES_STRING,
				//ITEMBAND
				new ColumnProperties(_home+5, _yPos, _fourthWidth, null),			//SERIAL_NO_STRING,
				new ColumnProperties(_1fourth, _yPos, _fourthWidth, null),			//DEVICE_TYPE_STRING,
				new ColumnProperties(_1fourth*2, _yPos, _fourthWidth, null),		//INSTALL_DATE_STRING,
				new ColumnProperties(_1fourth*3,_yPos, _fourthWidth, null),		//INSTALL_COMPANY_STRING
				//FOOTER
				new ColumnProperties(_home, (colHeight*1), _wholeWidth, null),		//WORK_DESC_STRING,
				new ColumnProperties(_home, (colHeight*5), _wholeWidth, null),		//ACTION_TAKEN_STRING,
			};				
		}
		
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString() {
		return title;
	}

	public String getHTMLOptionsTable()
	{
		String html = "";
		
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>Search BY Column</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='radio' name='" + ATT_SEARCH_COL +"' value='" + SEARCH_COL_DATE_CLOSED + "'>Date Closed" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='radio' name='" + ATT_SEARCH_COL +"' value='" + SEARCH_COL_DATE_REPORTED+ "'>Date Reported" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='radio' name='" + ATT_SEARCH_COL +"' value='" + SEARCH_COL_DATE_SCHEDULED + "'>Date Scheduled" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='radio' name='" + ATT_SEARCH_COL +"' value='" + SEARCH_COL_NONE+ "' checked >None" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		html += "  </tr>" + LINE_SEPARATOR;
		html += "</table>" + LINE_SEPARATOR;
		return html;
	}

	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
		if( req != null)
		{
			String param = req.getParameter(ATT_SEARCH_COL);
			if( param != null)
				setSearchColumn(Integer.valueOf(param).intValue());
			else
				setSearchColumn(SEARCH_COL_NONE);
		}
	}
}
