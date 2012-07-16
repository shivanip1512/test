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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.stars.WorkOrder;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteMeterHardwareBase;
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkOrderModel extends ReportModelBase<WorkOrder> {
	
	/** A string for the title of the data */
	private static String title = "Work Order";
	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 33;
	
	/** Enum values for column representation */
	public final static int EC_NAME_COLUMN = 0;
	public final static int EC_INFO_COLUMN = 1;
	public final static int ORDER_NO_COLUMN = 2;
	public final static int DATE_TIME_TODAY_COLUMN = 3;
	public final static int RECENT_EVENT_COLUMN = 4;
	public final static int DATE_TIME_RECENT_EVENT_COLUMN = 5;
	public final static int SERVICE_TYPE_COLUMN = 6;
	public final static int SERVICE_COMPANY_COLUMN = 7;
	
	public final static int ACCOUNT_NO_COLUMN = 8;
	public final static int NAME_COLUMN = 9;
	public final static int CONSUMPTION_TYPE_COLUMN = 10;
    public final static int DEBTOR_NUM_COLUMN = 11;
	public final static int PHONE_HOME_COLUMN = 12;
	public final static int PHONE_WORK_COLUMN = 13;
	public final static int PHONE_CONTACT_COLUMN = 14;
    public final static int PRESENCE_REQUIRED_COLUMN = 15;
	public final static int ADDRESS1_COLUMN = 16;
	public final static int ADDRESS2_COLUMN = 17;
	public final static int CITY_STATE_COLUMN = 18;
    public final static int ZIP_COLUMN = 19;
    
	public final static int COMPANY_NAME_COLUMN = 20;
	public final static int ADDTL_ORDER_NO_COLUMN = 21;
	public final static int MAP_NO_COLUMN = 22;
	public final static int METER_NO_COLUMN = 23;
	public final static int ACCOUNT_NOTES_COLUMN = 24;
	
	public final static int SERIAL_NO_COLUMN = 25;
	public final static int METER_NO_TO_SERIAL_COLUMN = 26;
	public final static int DEVICE_TYPE_COLUMN = 27;
    public final static int DEVICE_STATUS_COLUMN = 28;
	public final static int INSTALL_DATE_COLUMN = 29;
	public final static int INSTALL_COMPANY_COLUMN = 30;

	public final static int WORK_DESC_COLUMN = 31;
	public final static int ACTION_TAKEN_COLUMN = 32;

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
	public final static String RECENT_EVENT_STRING = "Status";
	public final static String DATE_TIME_RECENT_EVENT_STRING = "Status Date";
	public final static String SERVICE_TYPE_STRING = "Service Type";
	public final static String SERVICE_COMPANY_STRING = "Srv. Company";
	public final static String WORK_DESC_STRING = "Work Description";
	public final static String ACTION_TAKEN_STRING = "Action Taken";
    public final static String DEVICE_STATUS_STRING = "Device Status";
    public final static String DEBTOR_NUM_STRING = "Debtor Number";
	
	public final static String ACCOUNT_NO_STRING = "Account Number";
	public final static String NAME_STRING = "Name";
	public final static String CONSUMPTION_TYPE_STRING = "Customer Type";
	public final static String PHONE_HOME_STRING = "Home Phone";
	public final static String PHONE_WORK_STRING = "Work Phone";
	public final static String PHONE_CONTACT_STRING = "Call Back Phone";
    public final static String PRESENCE_REQUIRED_STRING = "Presence Required";
	public final static String ADDRESS1_STRING = "Address";
	public final static String ADDRESS2_STRING = "Address ";
	public final static String CITY_STATE_STRING = "City State";
    public final static String ZIP_STRING = "Zip";
	
	public final static String COMPANY_NAME_STRING = "Company Name";
	public final static String ADDTL_ORDER_NO_STRING = "Addtl Order Number";
	public final static String MAP_NO_STRING = "Map Number";
	public final static String METER_NO_STRING = "Meter Number";
	public final static String ACCOUNT_NOTES_STRING = "Account Notes";
	
	public final static String SERIAL_NO_STRING = "Serial No";
	public final static String METER_NO_TO_SERIAL_STRING = "Meter No";
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
	
	private final String ATT_SEARCH_COL = "SearchColumn";
	
	private Map<LiteInventoryBase, String> liteInvBaseToMeterNumberMap = new HashMap<LiteInventoryBase, String>();
	public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");

	private WorkOrderHelper workOrderHelper;
	
	private static final Comparator<WorkOrder> workOrderCmptor = new Comparator<WorkOrder>() {
		public int compare(WorkOrder wo1, WorkOrder wo2) {
			
			if (wo1.getLiteWorkOrderBase().getEnergyCompanyID() != wo2.getLiteWorkOrderBase().getEnergyCompanyID())
				return wo1.getLiteWorkOrderBase().getEnergyCompanyID() - wo2.getLiteWorkOrderBase().getEnergyCompanyID();
			
			StarsWorkOrderBaseDao starsWorkOrderBaseDao = 
			    YukonSpringHook.getBean("starsWorkOrderBaseDao", StarsWorkOrderBaseDao.class);
			LiteWorkOrderBase lOrder1 = starsWorkOrderBaseDao.getById(wo1.getLiteWorkOrderBase().getOrderID());
			LiteWorkOrderBase lOrder2 = starsWorkOrderBaseDao.getById(wo2.getLiteWorkOrderBase().getOrderID());
			
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
		setEnergyCompanyID( ecID );
		setOrderID( orderID );
		setSearchColumn( searchColumn );
	}

	public synchronized WorkOrderHelper getWorkOrderHelper() {
	    if (workOrderHelper == null) {
	        workOrderHelper = new WorkOrderHelper();
	    }
        return workOrderHelper;
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

	private void setLiteInvToMeterNumberMap(Map<LiteInventoryBase, String> liteInvBaseToMeterNumberMap) {
	    this.liteInvBaseToMeterNumberMap = liteInvBaseToMeterNumberMap;
	}
	
	private Map<LiteInventoryBase, String> getLiteInvBaseToMeterNumberMap() {
        if (liteInvBaseToMeterNumberMap == null) {
            liteInvBaseToMeterNumberMap = new HashMap<LiteInventoryBase, String>();
        }
        return liteInvBaseToMeterNumberMap;
    }
	
	public String getLiteInvToMeterNumber(LiteInventoryBase liteInventoryBase) {
	    String meterNumber = getLiteInvBaseToMeterNumberMap().get(liteInventoryBase);
	    String result = (meterNumber != null) ? meterNumber : CtiUtilities.STRING_NONE;
	    return result;
	}

	@Override
	public void collectData() {
	    if (getEnergyCompanyID() == null) return;

	    StarsWorkOrderBaseDao starsWorkOrderBaseDao = 
	        YukonSpringHook.getBean("starsWorkOrderBaseDao", StarsWorkOrderBaseDao.class);
	    
	    LiteStarsEnergyCompany ec = StarsDatabaseCache.getInstance().getEnergyCompany( getEnergyCompanyID().intValue());
	    ArrayList<LiteWorkOrderBase> woList = new ArrayList<LiteWorkOrderBase>();

	    if (getOrderID() != null) {
	        LiteWorkOrderBase liteOrder = starsWorkOrderBaseDao.getById(getOrderID().intValue());
	        if (liteOrder != null) woList.add( liteOrder );
	    }
	    else if (getAccountID() != null) {
	        Map<Integer, LiteAccountInfo> accountMap = getWorkOrderHelper().getAccountMap(getEnergyCompanyID());
	        LiteAccountInfo liteAcctInfo = accountMap.get(getAccountID());
	        if (liteAcctInfo != null) {
	            List<Integer> workOrderIds = liteAcctInfo.getServiceRequestHistory();
	            List<LiteWorkOrderBase> workOrders = starsWorkOrderBaseDao.getByIds(workOrderIds);
	            woList.addAll(workOrders);
	        }
	    }
	    else {
	        List<LiteWorkOrderBase> allWOs = starsWorkOrderBaseDao.getAll(ec.getEnergyCompanyId());
	        if( allWOs != null)
	            woList.addAll( allWOs );
	    }

	    if (getServiceStatus() != null && getServiceStatus().intValue() > 0) {
	        Iterator<LiteWorkOrderBase> it = woList.iterator();
	        while (it.hasNext()) {
	            LiteWorkOrderBase liteOrder = it.next();
	            if (liteOrder.getCurrentStateID() != getServiceStatus().intValue())
	                it.remove();
	        }
	    }

	    if (getSearchColumn() != SEARCH_COL_NONE) {
	        Iterator<LiteWorkOrderBase> it = woList.iterator();
	        while (it.hasNext()) {
	            LiteWorkOrderBase liteOrder = it.next();

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

	    loadData(ec, woList);

	    Collections.sort( getData(), workOrderCmptor );
	}
	
	public void loadData(final LiteStarsEnergyCompany liteStarsEC, final List<LiteWorkOrderBase> woList)
	{
        //Reset all objects, new data being collected!
        setData(null);
        Date startTimer = new Date();
       
        CTILogger.info("Reporting Data Loading for " + woList.size() + " Work Orders.");
        
        final Integer energyCompanyId = liteStarsEC.getEnergyCompanyId();
        
        final Map<Integer, LiteAccountInfo> accountMap = getWorkOrderHelper().getAccountMap(energyCompanyId);
        
        final List<LiteInventoryBase> meterInventoryList = new ArrayList<LiteInventoryBase>();
        
        JdbcTemplate jdbcTemplate = new JdbcTemplate(PoolManager.getYukonDataSource());
        PlatformTransactionManager txManager = new DataSourceTransactionManager(jdbcTemplate.getDataSource());
        
        final InventoryBaseDao inventoryBaseDao = 
			YukonSpringHook.getBean("inventoryBaseDao", InventoryBaseDao.class);
        
        TransactionTemplate template = new TransactionTemplate(txManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
        template.setReadOnly(true);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                for (final LiteWorkOrderBase workOrder : woList) {
                    int accountId = workOrder.getAccountID();
                    if (accountId == 0) continue;
                    
                    LiteAccountInfo accountInfo = accountMap.get(accountId);
                    for (Integer inventoryId : accountInfo.getInventories()) {
                        LiteInventoryBase liteInvBase = inventoryBaseDao.getByInventoryId(inventoryId);
                        meterInventoryList.add(liteInvBase);
                    }
                }

                final Map<LiteInventoryBase, String> meterNumberMap = getWorkOrderHelper().getInventoryMeterNumbers(meterInventoryList);
                setLiteInvToMeterNumberMap(meterNumberMap);
            }
        });
        
		for (int j = 0; j < woList.size(); j++) {
			LiteWorkOrderBase liteOrder = woList.get(j);
			
			if (liteOrder.getAccountID() == 0) {
				WorkOrder wo = new WorkOrder( liteOrder);
				getData().add( wo );
			}
			else {
				LiteAccountInfo liteAcctInfo = accountMap.get(liteOrder.getAccountID());
				
				if (liteAcctInfo.getInventories().size() == 0) {
					WorkOrder wo = new WorkOrder( liteOrder);
					getData().add( wo );
				}
				else {
                    //First loop through, find all LiteStarsLMHardware, forcing this type to be first in the list.
                    ArrayList<String> ignoreMeterNumbers = new ArrayList<String>();
					for (int k = 0; k < liteAcctInfo.getInventories().size(); k++) {
						Integer invID = liteAcctInfo.getInventories().get(k);
						LiteInventoryBase liteInvBase = inventoryBaseDao.getByInventoryId(invID);
						 
						if (liteInvBase instanceof LiteLmHardwareBase)
						{
							WorkOrder wo = new WorkOrder( liteOrder , liteInvBase);
							getData().add(wo);	//add to the begining
							
                            String meterNumber = getLiteInvToMeterNumber(liteInvBase);
                            if (!meterNumber.equalsIgnoreCase(CtiUtilities.STRING_NONE))
                                ignoreMeterNumbers.add(meterNumber);
						}
                    }
                    //Second loop through and find anything that is not LiteStarsLMHardware, forcing other types to be last in the list.
                    for (int k = 0; k < liteAcctInfo.getInventories().size(); k++) {
                        int invID = liteAcctInfo.getInventories().get(k).intValue();
                        LiteInventoryBase liteInvBase = inventoryBaseDao.getByInventoryId(invID);
                        /*
                         * TODO: Now that non yukon meters have been removed from cache, need to make sure
                         * that this section is still properly obtaining meters for this account.
                         * It appears that using the getBriefCustAccountInfo call will do this, leaving
                         * the load for getInventoryBrief, which will load a non Yukon meter from the db
                         * when it doesn't find it in cache.
                         */
                        if (liteInvBase instanceof LiteMeterHardwareBase) {
                            if( !ignoreMeterNumbers.contains(((LiteMeterHardwareBase)liteInvBase).getMeterNumber()))
                            {   //Only add unassigned meternumbers to the data object
                                WorkOrder wo = new WorkOrder( liteOrder , liteInvBase);
                                getData().add(wo);  //add to the end
                            }
                        }
                        else if (! (liteInvBase instanceof LiteLmHardwareBase)){ 
                            WorkOrder wo = new WorkOrder( liteOrder , liteInvBase);
                            getData().add(wo);  //add to the end
                        }
					}
				}
			}
		}
		
		StarsDatabaseCache starsCache = StarsDatabaseCache.getInstance();
		
		final Set<Integer> contactIdSet = new HashSet<Integer>();
		final Set<Integer> energyContactIdSet = new HashSet<Integer>();
		final Set<Integer> addressIdSet = new HashSet<Integer>();

		int ecContactId = liteStarsEC.getPrimaryContactID();
		energyContactIdSet.add(ecContactId);

		// create a shallow copy of the data Vector for iterating, there is a performance gain here...
		List<Object> workOrderList = new ArrayList<Object>(getData());
		
		/* Round 1 - build up data collections of all the contact ID's and address ID's so
		 *           we can hit the database only twice.
		 */
		for (final Object o : workOrderList) {
		    if (!(o instanceof WorkOrder)) continue;
		    LiteWorkOrderBase liteWorkOrder = ((WorkOrder) o).getLiteWorkOrderBase();

		    LiteStarsEnergyCompany liteStarsEnergyCompany = starsCache.getEnergyCompany(liteWorkOrder.getEnergyCompanyID());
		    energyContactIdSet.add(liteStarsEnergyCompany.getPrimaryContactID());
		    
		    final Map<Integer, LiteAccountInfo> currentAccountMap =
		        getWorkOrderHelper().getAccountMap(liteStarsEnergyCompany.getEnergyCompanyId());
		    
		    int accountId = liteWorkOrder.getAccountID();
		    if (accountId > 0) {
		        LiteAccountInfo lAcctInfo = currentAccountMap.get(accountId);
		        if (lAcctInfo != null) {
		            int contactId = lAcctInfo.getCustomer().getPrimaryContactID();
		            contactIdSet.add(contactId);

		            int addressId = lAcctInfo.getAccountSite().getStreetAddressID();
		            addressIdSet.add(addressId);
		        }     
		    }
		}

		final List<Integer> contactIdList = new ArrayList<Integer>(contactIdSet.size() + energyContactIdSet.size());
		contactIdList.addAll(contactIdSet);
		contactIdList.addAll(energyContactIdSet);

		final Map<Integer, LiteContact> contactMap = DaoFactory.getContactDao().getContacts(contactIdList);
		
		for (final Integer contactId : energyContactIdSet) {
		    LiteContact ecContact = contactMap.get(contactId);
		    addressIdSet.add(ecContact.getAddressID());
		}

		final Map<Integer, LiteAddress> addressMap = DaoFactory.getAddressDao().getAddresses(new ArrayList<Integer>(addressIdSet));
		
		/* Round 2 - build AdditionalInformation objects and attach them to the WorkOrder
		 *           when getAttribute() is called the WorkOrder will contain anything it needs in memory. 
		 */
		for (final Object o : workOrderList) {
		    if (!(o instanceof WorkOrder)) continue;
		    WorkOrder workOrder = (WorkOrder) o;
		    LiteWorkOrderBase liteWorkOrder = workOrder.getLiteWorkOrderBase();
		    LiteStarsEnergyCompany liteStarsEnergyCompany = starsCache.getEnergyCompany(liteWorkOrder.getEnergyCompanyID());
		    
		    final WorkOrder.AdditionalInformation info = new WorkOrder.AdditionalInformation();
		    
		    LiteContact energyCompanyConact = contactMap.get(liteStarsEnergyCompany.getPrimaryContactID());
		    info.setEnergyCompanyContact(energyCompanyConact);
		    
		    LiteAddress energyCompanyAddress = addressMap.get(energyCompanyConact.getAddressID());
		    info.setEnergyCompanyAddress(energyCompanyAddress);

		    final Map<Integer, LiteAccountInfo> currentAccountMap =
		        getWorkOrderHelper().getAccountMap(liteStarsEnergyCompany.getEnergyCompanyId());
		    
		    int accountId = liteWorkOrder.getAccountID();
		    if (accountId > 0) {
		        LiteAccountInfo lAcctInfo = currentAccountMap.get(accountId);
		        if (lAcctInfo != null) {
		            int addressId = lAcctInfo.getAccountSite().getStreetAddressID();
		            LiteAddress address = addressMap.get(addressId);
		            info.setAddress(address);

		            int contactId = lAcctInfo.getCustomer().getPrimaryContactID();
		            LiteContact contact = contactMap.get(contactId);
		            info.setContact(contact);
		        }
		    }
		    
		    workOrder.setAdditionalInformation(info);
		}
		
		double totalTime = (System.currentTimeMillis() - startTimer.getTime()) / 1000;
		
        CTILogger.info("Loading of Data Objects (" + getData().size() + ")  took " + totalTime + " secs.");
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o) {
		if (o instanceof WorkOrder) {
			WorkOrder wo = (WorkOrder) o;
			
			LiteStarsEnergyCompany ec = StarsDatabaseCache.getInstance().getEnergyCompany( wo.getLiteWorkOrderBase().getEnergyCompanyID() );
			LiteWorkOrderBase lOrder = wo.getLiteWorkOrderBase();
			
			LiteAccountInfo lAcctInfo = null;
			LiteContact liteContact = null;
			LiteAddress liteAddress = null;
			if (lOrder.getAccountID() > 0) {
			    Map<Integer, LiteAccountInfo> accountMap = 
			        getWorkOrderHelper().getAccountMap(ec.getEnergyCompanyId());
				
			    lAcctInfo = accountMap.get(lOrder.getAccountID());
				liteContact = wo.getAdditionalInformation().getContact();
				liteAddress = wo.getAdditionalInformation().getAddress();
			}
			
			LiteInventoryBase liteInvBase = wo.getLiteInventoryBase(); 
			
			switch (columnIndex) {
				case EC_NAME_COLUMN:
					return ec.getName();
				case EC_INFO_COLUMN:
					String returnStr = "WORK ORDER";
					LiteContact lc_ec = wo.getAdditionalInformation().getEnergyCompanyContact();
					if( lc_ec != null)
					{
					    LiteAddress lAddr = wo.getAdditionalInformation().getEnergyCompanyAddress();
						if (lAddr != null) {
                            if (StarsUtils.forceNotNone(lAddr.getLocationAddress1()).length() > 0)
                                returnStr += "\r\n" + StarsUtils.forceNotNone(lAddr.getLocationAddress1());
							if (StarsUtils.forceNotNone(lAddr.getLocationAddress2()).length() > 0)
								returnStr += "\r\n" + StarsUtils.forceNotNone(lAddr.getLocationAddress2());
                            
                            boolean addSeparator = false;
                            returnStr += "\r\n";
                            if (StarsUtils.forceNotNone(lAddr.getCityName()).length() > 0){
                                returnStr += StarsUtils.forceNotNone(lAddr.getCityName());
                                addSeparator = true;
                            }
                            if (StarsUtils.forceNotNone(lAddr.getStateCode()).length() > 0)
                                returnStr += (addSeparator ? ", ":"") + StarsUtils.forceNotNone(lAddr.getStateCode());
                            if (StarsUtils.forceNotNone(lAddr.getZipCode()).length() > 0)
                                returnStr += " " + StarsUtils.forceNotNone(lAddr.getZipCode());
						}
						LiteContactNotification notification = DaoFactory.getContactNotificationDao().getFirstNotificationForContactByType(lc_ec, ContactNotificationType.PHONE);
                        if(  notification != null)
							returnStr += "\r\n" + notification;
					}
					return returnStr;
				case ORDER_NO_COLUMN:
					return lOrder.getOrderNumber();
				case DATE_TIME_TODAY_COLUMN:
					return ServletUtils.formatDate( new Date(), dateFormatter );
				case RECENT_EVENT_COLUMN:
					YukonListEntry entry = DaoFactory.getYukonListDao().getYukonListEntry(lOrder.getCurrentStateID());
					return (entry != null ? entry.getEntryText() : "");
				case DATE_TIME_RECENT_EVENT_COLUMN:
					return ServletUtils.formatDate( new Date(lOrder.getDateReported()), dateFormatter );
				case SERVICE_TYPE_COLUMN:
					return DaoFactory.getYukonListDao().getYukonListEntry(lOrder.getWorkTypeID()).getEntryText();
				case SERVICE_COMPANY_COLUMN:
					LiteServiceCompany sc = ec.getServiceCompany( lOrder.getServiceCompanyID() );
					if (sc != null)
						return sc.getCompanyName();
					else
						return "";
				case WORK_DESC_COLUMN:
					return lOrder.getDescription();
				case ACTION_TAKEN_COLUMN:
					return lOrder.getActionTaken();
				case ACCOUNT_NO_COLUMN:
					if (lAcctInfo != null)
						return lAcctInfo.getCustomerAccount().getAccountNumber();
					else
						return "";
				case CONSUMPTION_TYPE_COLUMN:
                    if (lAcctInfo != null){
                        if( lAcctInfo.getCustomer() instanceof LiteCICustomer && lAcctInfo.getCustomer().getCustomerTypeID() == CustomerTypes.CUSTOMER_CI)
    					{
    						YukonListEntry coTypeEntry = DaoFactory.getYukonListDao().getYukonListEntry(((LiteCICustomer)lAcctInfo.getCustomer()).getCICustType());
    						return (coTypeEntry != null ? coTypeEntry.getEntryText() : "");
    					}
    					else {
    						return "Residential";
    					}
                    }
					return null;
				case NAME_COLUMN:
					if (liteContact != null)
						return liteContact.getContLastName()+ ", "+ liteContact.getContFirstName();
					else
						return "";
				case PHONE_HOME_COLUMN:
					if (liteContact != null)
						return DaoFactory.getContactNotificationDao().getFirstNotificationForContactByType(liteContact, ContactNotificationType.HOME_PHONE);
					else
						return "";
				case PHONE_WORK_COLUMN:
					if (liteContact != null)
						return DaoFactory.getContactNotificationDao().getFirstNotificationForContactByType(liteContact, ContactNotificationType.WORK_PHONE);
					else
						return "";
				case PHONE_CONTACT_COLUMN:
					if (liteContact != null)
						return DaoFactory.getContactNotificationDao().getFirstNotificationForContactByType(liteContact, ContactNotificationType.CALL_BACK_PHONE);
					else
						return "";
                case PRESENCE_REQUIRED_COLUMN:
                    if (lAcctInfo != null) { 
                        return (lAcctInfo.getAccountSite().getCustAtHome().equalsIgnoreCase("Y") ?
                            "* Appointment Required" : "* Appointment Not Required");
                    }
                    return "";
				case ADDRESS1_COLUMN:
					if (liteAddress != null)
						return (liteAddress.getLocationAddress1().equalsIgnoreCase(CtiUtilities.STRING_NONE) ? null : liteAddress.getLocationAddress1());
					else
						return "";
				case ADDRESS2_COLUMN:
					if (liteAddress != null)
                        return (liteAddress.getLocationAddress2().equalsIgnoreCase(CtiUtilities.STRING_NONE) ? null : liteAddress.getLocationAddress2());
					else
						return "";					
				case CITY_STATE_COLUMN:
					if (liteAddress != null)
						return liteAddress.getCityName()+ ", " + liteAddress.getStateCode();
					else
						return "";
                case ZIP_COLUMN:
                    if (liteAddress != null)
                        return liteAddress.getZipCode();
                    else
                        return "";                    
				case COMPANY_NAME_COLUMN:
					if (lAcctInfo != null && lAcctInfo.getCustomer() instanceof LiteCICustomer && lAcctInfo.getCustomer().getCustomerTypeID() == CustomerTypes.CUSTOMER_CI
                            && ((LiteCICustomer)lAcctInfo.getCustomer()).getCompanyName() != null)
						return ((LiteCICustomer)lAcctInfo.getCustomer()).getCompanyName();
					else
						return "";
				case ADDTL_ORDER_NO_COLUMN:
					return lOrder.getAdditionalOrderNumber();
				case MAP_NO_COLUMN:
					if (lAcctInfo != null)
						return lAcctInfo.getAccountSite().getSiteNumber();
					else
						return "";
				case METER_NO_COLUMN:
					//Try to get the meter Number from the description.  (For Xcel PMSI, integration puts the meter number in the description field as below.
           			int beginIndex = lOrder.getDescription().indexOf("Meter Number: ");	//"Meter Number: " is hardcoded in the creation of the PTJ using CRSIntegrator
           			int endIndex = (beginIndex >= 0 ? lOrder.getDescription().indexOf(";", beginIndex + 14) : beginIndex);
           			if( beginIndex > -1 && endIndex > -1)
           				return lOrder.getDescription().substring(beginIndex+14, endIndex);

           			//TODO - update for all meter possiblilities - This is the alternate (older version way).
					/*if (lAcctInfo != null) {
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
					else*/
						return null;	//We'll just leave it null for now?
				case ACCOUNT_NOTES_COLUMN:
					if (lAcctInfo != null)
						return lAcctInfo.getCustomerAccount().getAccountNotes();
					else
						return "";
				case SERIAL_NO_COLUMN:
					if (liteInvBase != null )
					{
						if( liteInvBase instanceof LiteLmHardwareBase )
							return ((LiteLmHardwareBase)liteInvBase).getManufacturerSerialNumber();
					}
					return "";
				case METER_NO_TO_SERIAL_COLUMN:
					if( liteInvBase != null)
					{
						if( liteInvBase instanceof LiteMeterHardwareBase)
							return ((LiteMeterHardwareBase)liteInvBase).getMeterNumber();
                        
						String meterNumber = getLiteInvToMeterNumber(liteInvBase);
						return (meterNumber.equalsIgnoreCase(CtiUtilities.STRING_NONE) ? null : meterNumber);
					}
					return "";
				case DEVICE_TYPE_COLUMN:
					if (liteInvBase != null)
					{
						if( liteInvBase instanceof LiteLmHardwareBase)
							return DaoFactory.getYukonListDao().getYukonListEntry(((LiteLmHardwareBase)liteInvBase).getLmHardwareTypeID()).getEntryText();
						else if( liteInvBase instanceof LiteMeterHardwareBase)
							return DaoFactory.getYukonListDao().getYukonListEntry(liteInvBase.getCategoryID()).getEntryText();
					}
					return "";
				case INSTALL_DATE_COLUMN:
					if (liteInvBase != null)
						return ServletUtils.formatDate( new Date(liteInvBase.getInstallDate()), dateFormatter );
					else
						return "";
				case INSTALL_COMPANY_COLUMN:
					if (liteInvBase != null) {
						LiteServiceCompany ic = ec.getServiceCompany( liteInvBase.getInstallationCompanyID() );
						if (ic != null)
							return ic.getCompanyName();
						else
							return "";
					}
                    else
                        return "";
                case DEVICE_STATUS_COLUMN:
                    if (liteInvBase != null) {
                        return DaoFactory.getYukonListDao().getYukonListEntry(liteInvBase.getCurrentStateID()).getEntryText();
                    }
                    return "";
                case DEBTOR_NUM_COLUMN:
                    if (lAcctInfo != null && lAcctInfo.getCustomer() != null)
                        return lAcctInfo.getCustomer().getCustomerNumber();
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
            String addtlOrderNumberStr = ADDTL_ORDER_NO_STRING; 
            RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
            LiteYukonUser user = new LiteYukonUser();
            user.setUserID(getUserID());
            
            if( getUserID() != null)
            {
                boolean hasRole = false;
                hasRole = rolePropertyDao.checkRole(YukonRole.WORK_ORDER, user);
                
                if( hasRole ) 
                {
                    addtlOrderNumberStr = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.ADDTL_ORDER_NUMBER_LABEL, user);
                }
            }
            
			columnNames = new String[] {
				//HEADER
				EC_NAME_STRING,
				EC_INFO_STRING,
				ORDER_NO_STRING,
				DATE_TIME_TODAY_STRING,
				RECENT_EVENT_STRING,
				DATE_TIME_RECENT_EVENT_STRING,
				SERVICE_TYPE_STRING,
				SERVICE_COMPANY_STRING,
				ACCOUNT_NO_STRING,
				NAME_STRING,
				CONSUMPTION_TYPE_STRING,
                DEBTOR_NUM_STRING,
				PHONE_HOME_STRING,
				PHONE_WORK_STRING,
				PHONE_CONTACT_STRING,
                PRESENCE_REQUIRED_STRING,
				ADDRESS1_STRING,
				ADDRESS2_STRING,
				CITY_STATE_STRING,
                ZIP_STRING,
				COMPANY_NAME_STRING,
				addtlOrderNumberStr,
				MAP_NO_STRING,
				METER_NO_STRING,
				ACCOUNT_NOTES_STRING,
				//ITEM BAND
				SERIAL_NO_STRING,
				METER_NO_TO_SERIAL_STRING,
				DEVICE_TYPE_STRING,
                DEVICE_STATUS_STRING,
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
			columnTypes = new Class[NUMBER_COLUMNS];
			for (int i = 0; i < NUMBER_COLUMNS; i++)
				columnTypes[i] = String.class;
		}
		
		return columnTypes;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties() {
		if (columnProperties == null) 
		{
			int colHeight = 14;
			int _home = 0;
			int _1third = 184;
			int _2third = _1third*2;
			int _1half = 276;
			int _1sixth = 80;
			int _thirdWidth = 180;
			int _halfWidth = 270;
			int _wholeWidth = 500;
			int _sixthWidth = 82;
			
			int _yPos = 1; 
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(_1third, 1, _thirdWidth,null),		//EC_NAME_STRING,
				new ColumnProperties(_1third, (colHeight*1), _thirdWidth, null),		//EC_INFO_STRING,
				//HEADER
				new ColumnProperties(_2third, (colHeight*1), _thirdWidth, null),		//ORDER_NO_STRING,
				new ColumnProperties(_2third, (colHeight*2), _thirdWidth, null),		//DATE_TIME_TODAY_STRING,
				new ColumnProperties(_home, (colHeight*1), _thirdWidth, null),		//RECENT_EVENT_STRING,
				new ColumnProperties(_home, (colHeight*2), _thirdWidth, null),		//DATE_TIME_RECENT_EVENT_STRING,
				new ColumnProperties(_1half, (colHeight*4), _halfWidth, null),		//SERVICE_TYPE_STRING,
				new ColumnProperties(_1half, (colHeight*11), _halfWidth, null),		//SERVICE_COMPANY_STRING,
				new ColumnProperties(_home, (colHeight*4), _halfWidth, null),		//ACCOUNT_NO_STRING,
				new ColumnProperties(_home, (colHeight*6), _halfWidth, null),		//NAME_STRING,
				new ColumnProperties(_home, (colHeight*8), _halfWidth, null),		//CONSUMPTION_TYPE_STRING,
                new ColumnProperties(_home, (colHeight*9), _halfWidth, null),      //DEBTOR_NUM_STRING,
				new ColumnProperties(_1half, (colHeight*6), _halfWidth,  null),		//PHONE_HOME_STRING,
				new ColumnProperties(_1half, (colHeight*7), _halfWidth, null),		//PHONE_WORK_STRING,
				new ColumnProperties(_1half, (colHeight*8), _halfWidth, null),		//PHONE_CONTACT_STRING,
                new ColumnProperties(_1half, (colHeight*9), _halfWidth, null),      //PRESENCE_REQUIRED,
				new ColumnProperties(_home, (colHeight*10), _halfWidth, null),		//ADDRESS1_STRING,				
				new ColumnProperties(_home, (colHeight*11), _halfWidth, null),		//ADDRESS2_STRING,
				new ColumnProperties(_home, (colHeight*12), _halfWidth, null),		//CITY_STATE_STRING,
                new ColumnProperties(_home, (colHeight*13), _halfWidth, null),      //ZIP_STRING,
				new ColumnProperties(_home, (colHeight*7), _halfWidth, null),		//COMPANY_NAME_STRING,
				new ColumnProperties(_1half, (colHeight*12), _halfWidth, null),		//ADDTL_ORDER_NO_STRING,
				new ColumnProperties(_1half, (colHeight*13), _halfWidth, null),		//MAP_NO_STRING,
				new ColumnProperties(_1half, (colHeight*14), _halfWidth, null),		//METER_NO_STRING,				
				new ColumnProperties(_home, (colHeight*15), _wholeWidth, null),		//ACCOUNT_NOTES_STRING,
				//ITEMBAND
				new ColumnProperties(_home+5, _yPos, _sixthWidth, null),			//SERIAL_NO_STRING,
				new ColumnProperties(_1sixth, _yPos, _sixthWidth, null),			//METER_NO_TO_SERIAL_STRING,
				new ColumnProperties(_1sixth*2, _yPos, _sixthWidth, null),			//DEVICE_TYPE_STRING,
                new ColumnProperties(_1sixth*3, _yPos, _sixthWidth, null),      //DEVICE_STATUS_STRING,
				new ColumnProperties(_1sixth*4, _yPos, _sixthWidth, null),		    //INSTALL_DATE_STRING,
				new ColumnProperties(_1sixth*5,_yPos, _sixthWidth, null),           //INSTALL_COMPANY_STRING
				//FOOTER
                new ColumnProperties(_home, (colHeight*3), _wholeWidth, null),		//WORK_DESC_STRING,
				new ColumnProperties(_home, (colHeight*6), _wholeWidth, null)		//ACTION_TAKEN_STRING,
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
	@Override
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
	@Override
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
