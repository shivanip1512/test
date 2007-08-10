package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.stars.appliance.ApplianceBase;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.web.navigation.CtiNavObject;
import com.roguewave.tools.v2_0.Collection;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class InventoryBean {
	
	public static final int SORT_ORDER_ASCENDING = 0;
	public static final int SORT_ORDER_DESCENDING = 1;
	
	public static final int INV_LOCATION_WAREHOUSE = 0;
	public static final int INV_LOCATION_RESIDENCE = 1;
	
	public static final int HTML_STYLE_LIST_INVENTORY = 1;
	public static final int HTML_STYLE_SELECT_INVENTORY = 2;
	public static final int HTML_STYLE_SELECT_LM_HARDWARE = 4;
	public static final int HTML_STYLE_INVENTORY_SET = 8;
    public static final int HTML_STYLE_FILTERED_INVENTORY_SUMMARY = 9;
	
	private static final int DEFAULT_PAGE_SIZE = 250;
    
    private final int MAX_ALLOW_DISPLAY = 500;
	
	private static final java.text.SimpleDateFormat dateFormat =
			new java.text.SimpleDateFormat("MM/dd/yyyy");
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private static final Comparator INV_ID_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteInventoryBase inv1 = (LiteInventoryBase)
					((o1 instanceof Pair)? ((Pair)o1).getFirst() :  o1);
			LiteInventoryBase inv2 = (LiteInventoryBase)
					((o2 instanceof Pair)? ((Pair)o2).getFirst() :  o2);
			return inv1.getInventoryID() - inv2.getInventoryID();
		}
	};
    
    private HttpServletRequest internalRequest;
    private String filterInventoryHTML;
    private String numberOfRecords = "0";
    private boolean viewResults = false;
    private boolean shipmentCheck = false;
    private boolean overHardwareDisplayLimit = false;
	private boolean differentOrigin = true;
    private boolean checkInvenForAccount = false;
    
	/**
	 * Comparator of serial # and device names. Serial # is always "less than"
	 * device name. To compare two serial #s, try to convert them into decimal
	 * values first, compare the decimal values if conversion is successful.
	 */
	private static final Comparator SERIAL_NO_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteInventoryBase inv1 = (LiteInventoryBase)
					((o1 instanceof Pair)? ((Pair)o1).getFirst() :  o1);
			LiteInventoryBase inv2 = (LiteInventoryBase)
					((o2 instanceof Pair)? ((Pair)o2).getFirst() :  o2);
			int result = 0;
			
			if ((inv1 instanceof LiteStarsLMHardware) && (inv2 instanceof LiteStarsLMHardware)) {
				LiteStarsLMHardware hw1 = (LiteStarsLMHardware) inv1;
				LiteStarsLMHardware hw2 = (LiteStarsLMHardware) inv2;
				
				Long sn1 = null;
				try {
					sn1 = Long.valueOf( hw1.getManufacturerSerialNumber() );
				}
				catch (NumberFormatException e) {}
				
				Long sn2 = null;
				try {
					sn2 = Long.valueOf( hw2.getManufacturerSerialNumber() );
				}
				catch (NumberFormatException e) {}
				
				if (sn1 != null && sn2 != null) {
					result = sn1.compareTo( sn2 );
					if (result == 0) result = hw1.getManufacturerSerialNumber().compareTo( hw2.getManufacturerSerialNumber() );
				}
				else if (sn1 != null && sn2 == null)
					return -1;
				else if (sn1 == null && sn2 != null)
					return 1;
				else
					result = hw1.getManufacturerSerialNumber().compareTo( hw2.getManufacturerSerialNumber() );
			}
			else if ((inv1 instanceof LiteStarsLMHardware) && !(inv2 instanceof LiteStarsLMHardware))
				return -1;
			else if (!(inv1 instanceof LiteStarsLMHardware) && (inv2 instanceof LiteStarsLMHardware))
				return 1;
			else {
				String devName1 = null;
				if (inv1.getDeviceID() > 0)
				{
                    try
                    {
                        devName1 = DaoFactory.getPaoDao().getYukonPAOName( inv1.getDeviceID() );
                    }
                    catch(NotFoundException e) {}
                }
				else if (inv1.getDeviceLabel() != null && inv1.getDeviceLabel().length() > 0)
					devName1 = inv1.getDeviceLabel();
				
				String devName2 = null;
				if (inv2.getDeviceID() > 0)
					devName2 = DaoFactory.getPaoDao().getYukonPAOName( inv2.getDeviceID() );
				else if (inv2.getDeviceLabel() != null && inv2.getDeviceLabel().length() > 0)
					devName2 = inv2.getDeviceLabel();
				
				if (devName1 != null && devName2 != null)
					result = devName1.compareTo( devName2 );
				else if (devName1 != null && devName2 == null)
					return -1;
				else if (devName1 == null && devName2 != null)
					return 1;
				else
					return -1;
			}
			
			if (result == 0) result = inv1.getInventoryID() - inv2.getInventoryID();
			return result;
		}
	};
	
	private static final Comparator INST_DATE_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteInventoryBase inv1 = (LiteInventoryBase)
					((o1 instanceof Pair)? ((Pair)o1).getFirst() :  o1);
			LiteInventoryBase inv2 = (LiteInventoryBase)
					((o2 instanceof Pair)? ((Pair)o2).getFirst() :  o2);
			
			int result = new java.util.Date(inv1.getInstallDate()).compareTo( new java.util.Date(inv2.getInstallDate()) );
			if (result == 0)
				result = inv1.getInventoryID() - inv2.getInventoryID();
			
			return result;
		}
	};
	
	private int sortBy = YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO;
	private int sortOrder = SORT_ORDER_ASCENDING;
	private int page = 1;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int energyCompanyID = 0;
	private int htmlStyle = HTML_STYLE_LIST_INVENTORY;
	private String referer = null;
	private ArrayList inventorySet = null;
	private int member = -1;
	private int searchBy = CtiUtilities.NONE_ZERO_ID;
	private String searchValue = null;
	private String action = null;
	
	private LiteStarsEnergyCompany energyCompany = null;
	private ArrayList inventoryList = null;
    private ArrayList filterByList = null;
    private boolean showAll = false;
	
	public InventoryBean() {
	}
	
	private LiteStarsEnergyCompany getEnergyCompany() {
		if (energyCompany == null || energyCompany.getLiteID() != energyCompanyID)
			energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( energyCompanyID );
		return energyCompany;
	}
	
    public ArrayList getInventoryList()
    {
        return inventoryList;
    }
    
	private ArrayList getHardwareList(boolean showEnergyCompany) throws WebClientException {
		if (inventoryList != null && !shipmentCheck) return inventoryList;
		
		ArrayList hardwares = null;
		if ((getHtmlStyle() & HTML_STYLE_INVENTORY_SET) != 0 && inventorySet != null) {
			hardwares = inventorySet;
		}
		else if (showEnergyCompany) {
			if (getMember() >= 0) {
				LiteStarsEnergyCompany member = StarsDatabaseCache.getInstance().getEnergyCompany( getMember() );
				ArrayList inventory = null;
				if (getSearchBy() == 0)
					inventory = member.loadAllInventory( true );
				else
					inventory = InventoryManagerUtil.searchInventory( member, getSearchBy(), getSearchValue(), false );
				
				hardwares = new ArrayList();
				for (int i = 0; i < inventory.size(); i++)
					hardwares.add( new Pair(inventory.get(i), member) );
			}
			else if (getSearchBy() == 0) {
				ArrayList members = ECUtils.getAllDescendants( getEnergyCompany() );
				hardwares = new ArrayList();
				
				for (int i = 0; i < members.size(); i++) {
					LiteStarsEnergyCompany member = (LiteStarsEnergyCompany) members.get(i);
					ArrayList inventory = member.loadAllInventory( true );
					for (int j = 0; j < inventory.size(); j++)
						hardwares.add( new Pair(inventory.get(j), member) );
				}
			}
			else {
				hardwares = InventoryManagerUtil.searchInventory( getEnergyCompany(), getSearchBy(), getSearchValue(), true );
			}
		}
		else {
			if (getSearchBy() == 0)
				hardwares = getEnergyCompany().loadAllInventory( true );
			else
				hardwares = InventoryManagerUtil.searchInventory( getEnergyCompany(), getSearchBy(), getSearchValue(), false );
		}
		
		if ((getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0) {
			Iterator it = hardwares.iterator();
			while (it.hasNext()) {
				Object invObj = it.next();
				if (invObj instanceof Pair)
					invObj = ((Pair)invObj).getFirst();
				if (!(invObj instanceof LiteStarsLMHardware))
					it.remove();
			}
		}
		
        /*
         * Now that we have potentially n filters instead of one, we need to iterate through.
         */
        if(getHtmlStyle() != HTML_STYLE_INVENTORY_SET)
        {
            List<FilterWrapper> slowFilters = new ArrayList<FilterWrapper>();
            ArrayList previousFilteredHardwareFromSameFilterType = new ArrayList();
            for(int x = 0; x < getFilterByList().size(); x++)
            {
                ArrayList filteredHardware = new ArrayList();
                Integer filterType = new Integer(((FilterWrapper)getFilterByList().get(x)).getFilterTypeID());
                
                /*Adding OR logic to the filters instead of AND logic, so we need to track the previous and next filter type*/
                Integer previousFilterType = (x > 0 ? new Integer(((FilterWrapper)getFilterByList().get(x - 1)).getFilterTypeID()) : null);
                Integer nextFilterType = (x + 1 < getFilterByList().size() ? new Integer(((FilterWrapper)getFilterByList().get(x + 1)).getFilterTypeID()) : null);
                
                String specificFilterString = ((FilterWrapper)getFilterByList().get(x)).getFilterID(); 
                Integer specificFilterID = InventoryUtils.returnIntegerIfPossible(specificFilterString);
                
                if (filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE) 
                {
        			int devTypeMCT = getEnergyCompany().getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT ).getEntryID();
        			
        			for (int i = 0; i < hardwares.size(); i++) {
        				LiteInventoryBase liteInv = (LiteInventoryBase)
        						(showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
        				
                        /*
                         * Was comparing the yukon def ids.  we don't want that.  we want it to instead compare entry IDs 
                         * (ie. the customized type that the customer has given)
                         *
                           DaoFactory.getYukonListDao().areSameInYukon( ((LiteStarsLMHardware)liteInv).getLmHardwareTypeID(), specificFilterID.intValue() )
                         */
                        if (liteInv instanceof LiteStarsLMHardware && ((LiteStarsLMHardware)liteInv).getLmHardwareTypeID() == specificFilterID.intValue() 
                                || specificFilterID.intValue() == devTypeMCT && InventoryUtils.isMCT(liteInv.getCategoryID()))
        				{
        					filteredHardware.add( hardwares.get(i) );
        				}
        			}
        		}
        		else if (filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SRV_COMPANY) 
                {
        			for (int i = 0; i < hardwares.size(); i++) 
                    {
        				LiteInventoryBase liteInv = (LiteInventoryBase)
        						(showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
        				
        				if (liteInv.getInstallationCompanyID() == specificFilterID.intValue())
        					filteredHardware.add( hardwares.get(i) );
        			}
        		}
                else if( filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_POSTAL_CODES)
                {
                    /*Put away until easier filters have been processed.  This should help performance.*/
                    slowFilters.add((FilterWrapper)getFilterByList().get(x));
                    filteredHardware.addAll( hardwares );
                }
                else if (filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_WAREHOUSE) 
                {
                    /*Put away until easier filters have been processed.  This should help performance.*/
                    slowFilters.add((FilterWrapper)getFilterByList().get(x));
                    filteredHardware.addAll( hardwares );
                }
                else if (filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MIN)
                {
                    for (int i = 0; i < hardwares.size(); i++) 
                    {
                        LiteInventoryBase liteInv = (LiteInventoryBase)
                            (showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
                        
                        if (liteInv instanceof LiteStarsLMHardware) 
                        {
                            String serial = ((LiteStarsLMHardware)liteInv).getManufacturerSerialNumber();
    
                            if(InventoryUtils.isSerialWithPossibleCharsGreaterThan(serial, specificFilterString))
                                filteredHardware.add( hardwares.get(i) );
                        }
                    }
                }
                else if (filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MAX)
                {
                    for (int i = 0; i < hardwares.size(); i++) 
                    {
                        LiteInventoryBase liteInv = (LiteInventoryBase)
                            (showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
                   
                        if (liteInv instanceof LiteStarsLMHardware) 
                        {
                            String serial = ((LiteStarsLMHardware)liteInv).getManufacturerSerialNumber();
        
                            if(InventoryUtils.isSerialWithPossibleCharsLessThan(serial, specificFilterString))
                                filteredHardware.add( hardwares.get(i) );
                        }
                    }
                }
        		else if (filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_APPLIANCE_TYPE) 
                {
                    /*Put away until easier filters have been processed.  This should help performance.*/
                    slowFilters.add((FilterWrapper)getFilterByList().get(x));
                    filteredHardware.addAll( hardwares );
        		}
                else if( filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_CUST_TYPE)
                {
                    /*Put away until easier filters have been processed.  This should help performance.*/
                    slowFilters.add((FilterWrapper)getFilterByList().get(x));
                    filteredHardware.addAll( hardwares );
                }
                /*else if (filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_CONFIG) {
        			Hashtable ecHwCfgMap = new Hashtable();
        			
        			for (int i = 0; i < hardwares.size(); i++) {
        				LiteInventoryBase liteInv = (LiteInventoryBase)
        						(showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
        				if (!(liteInv instanceof LiteStarsLMHardware) || liteInv.getAccountID() == CtiUtilities.NONE_ZERO_ID)
        					continue;
        				
        				LiteStarsEnergyCompany company = (showEnergyCompany)?
        						(LiteStarsEnergyCompany) ((Pair)hardwares.get(i)).getSecond() : getEnergyCompany();
        				
        				if (company.isAccountsLoaded()) {
        					LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation)
        							company.getCustAccountInformation( liteInv.getAccountID(), false );
        					
        					if (liteAcctInfo != null) {
        						for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
        							LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
        							if (liteApp.getInventoryID() == liteInv.getInventoryID()
        								&& liteApp.getAddressingGroupID() == specificFilterID.intValue())
        							{
        								filteredHardware.add( hardwares.get(i) );
        								break;
        							}
        						}
        					}
        				}
        				else {
        					Hashtable hwCfgMap = (Hashtable) ecHwCfgMap.get( company.getEnergyCompanyID() );
        					if (hwCfgMap == null) {
        						hwCfgMap = new Hashtable();
        						
        						com.cannontech.database.db.stars.hardware.LMHardwareConfiguration[] hwConfig =
        								com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.getAllLMHardwareConfiguration( company.getLiteID() );
        						
        						for (int j = 0; j < hwConfig.length; j++) {
        							ArrayList cfgList = (ArrayList) hwCfgMap.get( hwConfig[j].getInventoryID() );
        							if (cfgList == null) {
        								cfgList = new ArrayList();
        								hwCfgMap.put( hwConfig[j].getInventoryID(), cfgList );
        							}
        							cfgList.add( hwConfig[j].getAddressingGroupID() );
        						}
        						
        						ecHwCfgMap.put( company.getEnergyCompanyID(), hwCfgMap );
        					}
        					
        					ArrayList cfgList = (ArrayList) hwCfgMap.get( new Integer(liteInv.getInventoryID()) );
        					if (cfgList != null) {
        						for (int j = 0; j < cfgList.size(); j++) {
        							if (((Integer)cfgList.get(j)).intValue() == specificFilterID.intValue()) {
        								filteredHardware.add( hardwares.get(i) );
        								break;
        							}
        						}
        					}
        				}
        			}
        		}*/
        		else if (filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_STATUS) {
        			for (int i = 0; i < hardwares.size(); i++) {
        				LiteInventoryBase liteInv = (LiteInventoryBase)
        						(showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
        				
                        /*This is another ugly Xcel specific requirement
                         * They don't want meters shown on the "(none)" state filter
                         */
                        if(specificFilterID.intValue() == 0 && !(liteInv instanceof LiteStarsLMHardware) )
                            continue;
                        /**
                         * TODO CurrentState in the Xcel world will need to be more elegantly
                         * combined with the old DeviceStatus.  They are currently separate so as
                         * to not break STARS too much for existing customers.
                         */
        				if (liteInv.getCurrentStateID() == specificFilterID.intValue() ||
                                liteInv.getDeviceStatus() == specificFilterID.intValue())
        					filteredHardware.add( hardwares.get(i) );
        			}
        		}
        		else {
        			filteredHardware.addAll( hardwares );
        		}
                
                /*Adding OR logic to the filters instead of AND logic, so we need to send the same original set
                 * of hardware back through the filter if they are the same type of filter
                 * */
                if(nextFilterType != null && filterType.compareTo(nextFilterType) == 0) 
                    previousFilteredHardwareFromSameFilterType.addAll(filteredHardware);
                else if(previousFilterType != null && filterType.compareTo(previousFilterType) == 0) {
                    previousFilteredHardwareFromSameFilterType.addAll(filteredHardware);
                    hardwares = previousFilteredHardwareFromSameFilterType;
                    previousFilteredHardwareFromSameFilterType = new ArrayList();
                }
                else
                    hardwares = filteredHardware;
            }
            
            /*
             * Do the meatier filters now, after the total number of hardware has become a little bit more reasonable
             */
            for(int y = 0; y < slowFilters.size(); y++)
            {
                Integer filterType = new Integer(slowFilters.get(y).getFilterTypeID());
                String specificFilterString = slowFilters.get(y).getFilterID(); 
                Integer specificFilterID = InventoryUtils.returnIntegerIfPossible(specificFilterString);
                ArrayList filteredHardware = new ArrayList();
                
                /*Adding OR logic to the filters instead of AND logic, so we need to track the previous and next filter type*/
                Integer previousFilterType = (y > 0 ? new Integer(((FilterWrapper)getFilterByList().get(y - 1)).getFilterTypeID()) : null);
                Integer nextFilterType = (y + 1 < getFilterByList().size() ? new Integer(((FilterWrapper)getFilterByList().get(y + 1)).getFilterTypeID()) : null);
               
                
                if (filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_APPLIANCE_TYPE) 
                {
                    HashMap<Integer, Integer> accounts = new HashMap<Integer, Integer>();
                    /*If there is already zero hardware, by bother the db at all?*/
                    if(hardwares.size() > 0)
                         accounts = ApplianceBase.getAllAccountIDsFromApplianceCategory(specificFilterID.intValue());
                    
                    Date timerStart = new Date();
                    
                    for (int i = 0; i < hardwares.size(); i++) 
                    {
                        LiteInventoryBase liteInv = (LiteInventoryBase)
                                (showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
                        
                        if(accounts.get(liteInv.getAccountID()) != null && liteInv instanceof LiteStarsLMHardware)
                        {
                            filteredHardware.add( hardwares.get(i) );
                        }
                    }
                    
                    CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " seconds for appliance type filter to complete iterations." );
                }
                else if (filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_WAREHOUSE) 
                {
                    List<Integer> warehousedInventory  = new ArrayList<Integer>();
                    /*If there is already zero hardware, why bother the db at all?*/
                    if(hardwares.size() > 0) {
                        if(specificFilterID == 0)
                            warehousedInventory = Warehouse.getAllInventoryNotInAWarehouse();
                        else
                            warehousedInventory = Warehouse.getAllInventoryInAWarehouse(specificFilterID);
                    }
                    
                    for (int i = 0; i < hardwares.size(); i++) 
                    {
                        LiteInventoryBase liteInv = (LiteInventoryBase)
                            (showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
                        
                        for(int j = 0; j < warehousedInventory.size(); j++)
                        {
                            if(liteInv.getLiteID() == warehousedInventory.get(j).intValue())
                            {
                                filteredHardware.add( hardwares.get(i) );
                                //let's try to speed this up over time
                                warehousedInventory.remove(j);
                                break;
                            }
                        }
                    }
                }
                else if( filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_CUST_TYPE)
                {
                    HashMap<Integer, Integer> accounts = new HashMap<Integer, Integer>();
                    /*If there is already zero hardware, by bother the db at all?*/
                    if(hardwares.size() > 0)
                    {
                        if( specificFilterID.intValue() == -1)  // RESIDENTIAL CUSTOMER
                        {
                            accounts = CustomerAccount.getAccountIDsNonCommercial(member);
                        }
                        else
                        {   //Some type of CICustomer Type
                            accounts = CustomerAccount.getAccountIDsFromCustomerType(specificFilterID.intValue());
                        }
                    }
                    for (int i = 0; i < hardwares.size(); i++) 
                    {
                        LiteInventoryBase liteInv = (LiteInventoryBase)
                            (showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
                         
                        if(accounts.get(liteInv.getAccountID()) != null && liteInv instanceof LiteStarsLMHardware)
                        {
                            filteredHardware.add( hardwares.get(i) );
                        }
                    }
                }
                else if( filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_POSTAL_CODES)
                {
                    HashMap<Integer, Integer> accounts = new HashMap<Integer, Integer>();
                    /*If there is already zero hardware, by bother the db at all?*/
                    if(hardwares.size() > 0)
                         accounts = CustomerAccount.getAccountIDsFromZipCode(specificFilterString);
                        
                    for (int i = 0; i < hardwares.size(); i++) 
                    {
                        LiteInventoryBase liteInv = (LiteInventoryBase)
                            (showEnergyCompany? ((Pair)hardwares.get(i)).getFirst() : hardwares.get(i));
                         
                        if(accounts.get(liteInv.getAccountID()) != null && liteInv instanceof LiteStarsLMHardware)
                        {
                            filteredHardware.add( hardwares.get(i) );
                        }
                    }
                }
                
                /*Adding OR logic to the filters instead of AND logic, so we need to send the same original set
                 * of hardware back through the filter if they are the same type of filter
                 * */
                if(nextFilterType != null && filterType.compareTo(nextFilterType) == 0) 
                    previousFilteredHardwareFromSameFilterType.addAll(filteredHardware);
                else if(previousFilterType != null && filterType.compareTo(previousFilterType) == 0) {
                    previousFilteredHardwareFromSameFilterType.addAll(filteredHardware);
                    hardwares = previousFilteredHardwareFromSameFilterType;
                    previousFilteredHardwareFromSameFilterType = new ArrayList();
                }
                else
                    hardwares = filteredHardware;
            }
        }
            
        java.util.TreeSet sortedInvs = null;
        if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO)
            sortedInvs = new java.util.TreeSet( SERIAL_NO_CMPTOR );
        else if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_INST_DATE)
            sortedInvs = new java.util.TreeSet( INST_DATE_CMPTOR );
        else
            sortedInvs = new java.util.TreeSet( INV_ID_CMPTOR );

        sortedInvs.addAll(hardwares);
        
		inventoryList = new ArrayList();
		java.util.Iterator it = sortedInvs.iterator();
		while (it.hasNext()) {
			if (getSortOrder() == SORT_ORDER_ASCENDING)
				inventoryList.add( it.next() );
			else
				inventoryList.add( 0, it.next() );
		}
		
		return inventoryList;
	}
	
	public void resetInventoryList() {
		inventoryList = null;
	}
	
	public String getHTML(HttpServletRequest req) 
    {
		StarsYukonUser user = (StarsYukonUser) req.getSession().getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        String previousPage = ((CtiNavObject) req.getSession().getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();        
        setDifferentOrigin(previousPage.lastIndexOf("Inventory.jsp") < 0);
        
		boolean showEnergyCompany = false;
        if((getHtmlStyle() == HTML_STYLE_FILTERED_INVENTORY_SUMMARY) && DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
                && (getEnergyCompany().getChildren().size() > 0))
                showEnergyCompany = true;
        else if ((getHtmlStyle() & HTML_STYLE_INVENTORY_SET) != 0) 
        {
			if (inventorySet != null && inventorySet.size() > 0 && inventorySet.get(0) instanceof Pair)
				showEnergyCompany = true;
		}
		else if ((getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) != 0 || 
                (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0 ||
                (getHtmlStyle() & HTML_STYLE_FILTERED_INVENTORY_SUMMARY) != 0 ) 
        {
			if (DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
				&& (getEnergyCompany().getChildren().size() > 0))
				showEnergyCompany = true;
		}
		
		ArrayList hwList = null;
		String errorMsg = null;
        int numberOfHardware = 0;
		try {
			hwList = getHardwareList( showEnergyCompany );
			if (hwList == null || hwList.size() == 0)
            {
				errorMsg = "No hardware found.";
            }
        }
		catch (WebClientException e) {
			errorMsg = e.getMessage();
		}
		
        StringBuffer htmlBuf = new StringBuffer();
        
        numberOfHardware = hwList.size();
        if(getHtmlStyle() == HTML_STYLE_FILTERED_INVENTORY_SUMMARY)
        {
            return htmlBuf.append(numberOfHardware).toString();
        }
        		
		if (errorMsg != null) {
			htmlBuf.append("<p class='ErrorMsg'>").append(errorMsg).append("</p>").append(LINE_SEPARATOR);
			if ((getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) == 0) {
				htmlBuf.append("<table width='200' border='0' cellspacing='0' cellpadding='0'>").append(LINE_SEPARATOR);
				htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
				htmlBuf.append("    <td align='center'>");
				if (referer != null)
					htmlBuf.append("<input type='button' name='Back' value='Back' onclick='location.href=\"").append(referer).append("\"'>").append(LINE_SEPARATOR);
				else
					htmlBuf.append("<input type='button' name='Back' value='Back' onclick='history.back()'>").append(LINE_SEPARATOR);
				htmlBuf.append("    </td>").append(LINE_SEPARATOR);
				htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
				htmlBuf.append("</table>").append(LINE_SEPARATOR);
			}
			return htmlBuf.toString();
		}
		
		String uri = req.getRequestURI();
		String pageName = uri.substring( uri.lastIndexOf('/') + 1 );
		
		String srcStr = "";
		if ((getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0
			|| (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0)
		{
			srcStr = "&src=SelectInv";
		}
		else if ((getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) != 0) {
			srcStr = "&src=Inventory";
		}
		else if (getHtmlStyle() == HTML_STYLE_INVENTORY_SET) {
			srcStr = "&src=ResultSet";
		}
		
		if (page < 1) page = 1;
		int maxPageNo = (int) Math.ceil(hwList.size() * 1.0 / pageSize);
		if (page > maxPageNo) page = maxPageNo;
		
		int maxPageDigit = (int)(Math.log(maxPageNo) / Math.log(10)) + 1;
		
		int minInvNo = (page - 1) * pageSize + 1;
		int maxInvNo = Math.min(page * pageSize, hwList.size());
        
		StringBuffer navBuf = new StringBuffer();
		navBuf.append(minInvNo);
		if (maxInvNo > minInvNo)
			navBuf.append("-").append(maxInvNo);
		navBuf.append(" of ").append(hwList.size());
		navBuf.append(" | ");
		if (page == 1)
			navBuf.append("<font color='#CCCCCC'>First</font>");
		else
			navBuf.append("<a class='Link1' href='").append(pageName).append("?page=1'>First</a>");
		navBuf.append(" | ");
		if (page == 1)
			navBuf.append("<font color='#CCCCCC'>Previous</font>");
		else
			navBuf.append("<a class='Link1' href='").append(pageName).append("?page=").append(page-1).append("'>Previous</a>");
		navBuf.append(" | ");
		if (page == maxPageNo)
			navBuf.append("<font color='#CCCCCC'>Next</font>");
		else
			navBuf.append("<a class='Link1' href='").append(pageName).append("?page=").append(page+1).append("'>Next</a>");
		navBuf.append(" | ");
		if (page == maxPageNo)
			navBuf.append("<font color='#CCCCCC'>Last</font>");
		else
			navBuf.append("<a class='Link1' href='").append(pageName).append("?page=").append(maxPageNo).append("'>Last</a>");
		
		htmlBuf.append("<table width='80%' border='0' cellspacing='0' cellpadding='0'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td>").append(LINE_SEPARATOR);
		htmlBuf.append("      <table width='100%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>").append(LINE_SEPARATOR);
		htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td>").append(navBuf).append("</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td align='right'>Page(1-").append(maxPageNo).append("): ")
				.append("<input type='text' id='GoPage' style='border:1px solid #0066CC; font:11px' size='").append(maxPageDigit).append("' value='").append(page).append("'> ")
				.append("<input type='button' style='font:11px; margin-bottom:-1px' value='Go' onclick='location.href=\"").append(pageName).append("?page=\" + document.getElementById(\"GoPage\").value;'>")
				.append("</td>").append(LINE_SEPARATOR);
		htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
		htmlBuf.append("      </table>").append(LINE_SEPARATOR);
		htmlBuf.append("    </td>").append(LINE_SEPARATOR);
		htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
		
		htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td>").append(LINE_SEPARATOR);
		htmlBuf.append("      <table width='100%' border='1' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
		htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
		if ((getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0
			|| (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0
            || (getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) != 0)
		{
			htmlBuf.append("          <td class='HeaderCell' width='1%'>&nbsp;</td>").append(LINE_SEPARATOR);
		}
		htmlBuf.append("          <td class='HeaderCell' width='17%'>Serial # / Device Name</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td class='HeaderCell' width='17%'>Device Type</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td class='HeaderCell' width='15%'>Device Status</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td class='HeaderCell'>Location</td>").append(LINE_SEPARATOR);
		if (showEnergyCompany)
			htmlBuf.append("          <td class='HeaderCell' width='17%'>Member</td>").append(LINE_SEPARATOR);
		htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
        
        htmlBuf.append("<form name='chooseForm' method='post' action='").append(req.getContextPath()).append("/servlet/InventoryManager'>").append(LINE_SEPARATOR);
		htmlBuf.append("<input type='hidden' name='action' value='ManipulateInventoryResults'>").append(LINE_SEPARATOR);
        for (int i = minInvNo; i <= maxInvNo; i++) {
			LiteInventoryBase liteInv = null;
			LiteStarsEnergyCompany member = null;
			boolean isManagable = false;
			
			if (showEnergyCompany) {
				liteInv = (LiteInventoryBase) ((Pair)hwList.get(i-1)).getFirst();
				member = (LiteStarsEnergyCompany) ((Pair)hwList.get(i-1)).getSecond();
				isManagable = DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
						&& ECUtils.isDescendantOf( member, getEnergyCompany() );
			}
			else {
				liteInv = (LiteInventoryBase) hwList.get(i-1);
				member = getEnergyCompany();
			}
        	
			String deviceType = "(none)";
			String deviceName = "(none)";
			if (liteInv instanceof LiteStarsLMHardware) {
				deviceType = DaoFactory.getYukonListDao().getYukonListEntry( ((LiteStarsLMHardware)liteInv).getLmHardwareTypeID() ).getEntryText();
				deviceName = ((LiteStarsLMHardware)liteInv).getManufacturerSerialNumber();
			}
			else if (liteInv.getDeviceID() > 0) {
				LiteYukonPAObject litePao = DaoFactory.getPaoDao().getLiteYukonPAO( liteInv.getDeviceID() );
				deviceType = PAOGroups.getPAOTypeString( litePao.getType() );
				deviceName = litePao.getPaoName();
			}
			else if (InventoryUtils.isMCT( liteInv.getCategoryID() )) {
				deviceType = getEnergyCompany().getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT).getEntryText();
				if (liteInv.getDeviceLabel() != null && liteInv.getDeviceLabel().length() > 0)
					deviceName = liteInv.getDeviceLabel();
			}
        	
			String currentDeviceState = DaoFactory.getYukonListDao().getYukonListEntry(liteInv.getCurrentStateID()).getEntryText();
            
            /*Date installDate = StarsUtils.translateDate( liteInv.getInstallDate() );
			dateFormat.setTimeZone( getEnergyCompany().getDefaultTimeZone() );
			String instDate = (installDate != null)? dateFormat.format(installDate) : "----";
			*/
			htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
            
			if ((getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0
				|| (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0)
			{
				htmlBuf.append("          <td class='TableCell' width='1%'>");
				htmlBuf.append("<input type='radio' name='InvID' onclick='selectInventory(").append(liteInv.getInventoryID()).append(",").append(member.getLiteID()).append(")'>");
				htmlBuf.append("</td>").append(LINE_SEPARATOR);
			}
            else if ((getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) != 0)
            {
                htmlBuf.append("          <td class='TableCell' width='1%'>");
                htmlBuf.append("<input type='checkbox' name='checkMultiInven' value='").append(liteInv.getInventoryID()).append("'>");
                htmlBuf.append("</td>").append(LINE_SEPARATOR);
            }
	        
			htmlBuf.append("          <td class='TableCell' width='17%'>");
			if (!showEnergyCompany || member.equals(energyCompany))
				htmlBuf.append("<a href='").append(req.getContextPath()).append("/operator/Hardware/InventoryDetail.jsp?InvId=").append(liteInv.getInventoryID()).append(srcStr).append("'>").append(deviceName).append("</a>");
			else if (isManagable)
				htmlBuf.append("<a href='' onclick='selectMemberInventory(").append(liteInv.getInventoryID()).append(",").append(member.getLiteID()).append("); return false;'>").append(deviceName).append("</a>");
			else
				htmlBuf.append(deviceName);
			htmlBuf.append("</td>").append(LINE_SEPARATOR);
            
			htmlBuf.append("          <td class='TableCell' width='17%'>").append(deviceType).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='15%'>").append(currentDeviceState).append("</td>").append(LINE_SEPARATOR);
            
			htmlBuf.append("          <td class='TableCell'>");
			if (liteInv.getAccountID() == 0) 
            {
				String warehouseName = Warehouse.getWarehouseNameFromInventoryID(liteInv.getInventoryID());
                if(warehouseName.length() > 0)
                    htmlBuf.append(warehouseName);
                else
                    htmlBuf.append("General Inventory");
			}
			else {
				LiteStarsCustAccountInformation liteAcctInfo = member.getBriefCustAccountInfo( liteInv.getAccountID(), true );
				LiteContact liteCont = DaoFactory.getContactDao().getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
				LiteAddress liteAddr = member.getAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
            	
				String name = StarsUtils.formatName( liteCont );
				StreetAddress starsAddr = new StreetAddress();
				StarsLiteFactory.setStarsCustomerAddress( starsAddr, liteAddr );
				String address = ServletUtils.getOneLineAddress( starsAddr );
            	
            	if (!showEnergyCompany || member.equals(energyCompany) || isManagable)
					htmlBuf.append("<a href='' class='Link1' onclick='selectAccount(").append(liteAcctInfo.getAccountID()).append(",").append(member.getLiteID()).append("); return false;'>")
							.append("Acct # ").append(liteAcctInfo.getCustomerAccount().getAccountNumber()).append("</a>");
				else
					htmlBuf.append("Acct # ").append(liteAcctInfo.getCustomerAccount().getAccountNumber());
				
				if (name.length() > 0)
					htmlBuf.append(" ").append(name);
				if (address.length() > 0)
					htmlBuf.append(", ").append(address);
			}
			htmlBuf.append("</td>").append(LINE_SEPARATOR);
            
			if (showEnergyCompany)
				htmlBuf.append("          <td class='TableCell' width='17%'>").append(member.getName()).append("</td>").append(LINE_SEPARATOR);
            
			htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
		}
        htmlBuf.append("</form>").append(LINE_SEPARATOR);        
        
		htmlBuf.append("      </table>").append(LINE_SEPARATOR);
		htmlBuf.append("    </td>").append(LINE_SEPARATOR);
		htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
		
		htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td>").append(LINE_SEPARATOR);
		htmlBuf.append("      <table width='100%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>").append(LINE_SEPARATOR);
		htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td>").append(navBuf).append("</td>").append(LINE_SEPARATOR);
		htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
		htmlBuf.append("      </table>").append(LINE_SEPARATOR);
		htmlBuf.append("    </td>").append(LINE_SEPARATOR);
		htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
		htmlBuf.append("</table>").append(LINE_SEPARATOR);
        
        
        if ((getHtmlStyle() & HTML_STYLE_SELECT_INVENTORY) != 0
			|| (getHtmlStyle() & HTML_STYLE_SELECT_LM_HARDWARE) != 0)
		{
			htmlBuf.append("<br>").append(LINE_SEPARATOR);
			htmlBuf.append("<table width='200' border='0' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
			htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
			htmlBuf.append("    <td align='right'>").append(LINE_SEPARATOR);
			htmlBuf.append("      <input type='button' name='Select' value='Select' onclick='validate()'>").append(LINE_SEPARATOR);
			htmlBuf.append("    </td>").append(LINE_SEPARATOR);
			htmlBuf.append("    <td>").append(LINE_SEPARATOR);
			if (referer != null)
				htmlBuf.append("      <input type='button' name='Cancel' value='Cancel' onclick='location.href=\"").append(referer).append("\"'>").append(LINE_SEPARATOR);
			else
				htmlBuf.append("      <input type='button' name='Cancel' value='Cancel' onclick='history.back()'>").append(LINE_SEPARATOR);
			htmlBuf.append("    </td>").append(LINE_SEPARATOR);
			htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
			htmlBuf.append("</table>").append(LINE_SEPARATOR);
		}
        else if((getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) != 0)
        {
            htmlBuf.append("<br>").append(LINE_SEPARATOR);
            htmlBuf.append("<table width='200' border='0' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
            htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
            htmlBuf.append("    <td align='left'>").append(LINE_SEPARATOR);
            htmlBuf.append("      <input type='button' name='CheckAll' value='Check All On Page' onclick='checkAll()'>").append(LINE_SEPARATOR);
            htmlBuf.append("    </td>").append(LINE_SEPARATOR);
            htmlBuf.append("    <td align='left'>").append(LINE_SEPARATOR);
            htmlBuf.append("      <input type='button' name='UncheckAll' value='Uncheck All On Page' onclick='uncheckAll()'>").append(LINE_SEPARATOR);
            htmlBuf.append("    </td>").append(LINE_SEPARATOR);
            htmlBuf.append("    <td align='left'>").append(LINE_SEPARATOR);
            htmlBuf.append("      <input type='button' name='ChooseSelected' value='Manipulate Selected' onclick='manipSelected()'>").append(LINE_SEPARATOR);
            htmlBuf.append("    </td>").append(LINE_SEPARATOR);
            htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
            htmlBuf.append("</table>").append(LINE_SEPARATOR);
        }
        
		if (getHtmlStyle() == HTML_STYLE_INVENTORY_SET) {
			htmlBuf.append("<br>").append(LINE_SEPARATOR);
			htmlBuf.append("<table width='200' border='0' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
			htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
			htmlBuf.append("    <td align='center'>").append(LINE_SEPARATOR);
			if (referer != null)
				htmlBuf.append("      <input type='button' name='Back' value='Back' onclick='location.href=\"").append(referer).append("\"'>").append(LINE_SEPARATOR);
			else
				htmlBuf.append("      <input type='button' name='Back' value='Back' onclick='history.back()'>").append(LINE_SEPARATOR);
			htmlBuf.append("    </td>").append(LINE_SEPARATOR);
			htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
			htmlBuf.append("</table>").append(LINE_SEPARATOR);
		}
        
        htmlBuf.append("<form name='InventoryBeanForm' method='post' action='").append(req.getContextPath()).append("/servlet/InventoryManager'>").append(LINE_SEPARATOR);
        htmlBuf.append("  <input type='hidden' name='InvID' value=''>").append(LINE_SEPARATOR);
		if (getAction() != null)
			htmlBuf.append("  <input type='hidden' name='action' value='" + getAction() + "'>").append(LINE_SEPARATOR);
		if (showEnergyCompany)
			htmlBuf.append("  <input type='hidden' name='MemberID' value=''>").append(LINE_SEPARATOR);
		htmlBuf.append("</form>").append(LINE_SEPARATOR);
        
		htmlBuf.append("<form name='cusForm' method='post' action='").append(req.getContextPath()).append("/servlet/SOAPClient'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <input type='hidden' name='action' value='GetCustAccount'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <input type='hidden' name='AccountID' value=''>").append(LINE_SEPARATOR);
		htmlBuf.append("  <input type='hidden' name='SwitchContext' value=''>").append(LINE_SEPARATOR);
		htmlBuf.append("  <input type='hidden' name='REDIRECT' value='").append(req.getContextPath()).append("/operator/Consumer/Update.jsp'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <input type='hidden' name='REFERRER' value='").append(req.getRequestURI()).append("'>").append(LINE_SEPARATOR);
		htmlBuf.append("</form>").append(LINE_SEPARATOR);
        
		if (showEnergyCompany) {
			htmlBuf.append("<form name='invForm' method='post' action='").append(req.getContextPath()).append("/servlet/StarsAdmin'>").append(LINE_SEPARATOR);
			htmlBuf.append("  <input type='hidden' name='action' value='SwitchContext'>").append(LINE_SEPARATOR);
			htmlBuf.append("  <input type='hidden' name='MemberID' value=''>").append(LINE_SEPARATOR);
			htmlBuf.append("  <input type='hidden' name='REDIRECT' value=''>").append(LINE_SEPARATOR);
			htmlBuf.append("</form>").append(LINE_SEPARATOR);
		}
        
		htmlBuf.append("<script language='JavaScript'>").append(LINE_SEPARATOR);
		htmlBuf.append("function goToPage() {").append(LINE_SEPARATOR);
		htmlBuf.append("  location.href='").append(pageName).append("?page=' + document.getElementById('Page').value;").append(LINE_SEPARATOR);
		htmlBuf.append("}").append(LINE_SEPARATOR);
		
        htmlBuf.append("function selectInventory(invID, memberID) {").append(LINE_SEPARATOR);
        htmlBuf.append("  var form = document.InventoryBeanForm;").append(LINE_SEPARATOR);
        htmlBuf.append("  form.InvID.value = invID;").append(LINE_SEPARATOR);
        htmlBuf.append("  form.action.value = 'InsertInventory';").append(LINE_SEPARATOR);
        if (showEnergyCompany)
            htmlBuf.append("  form.MemberID.value = memberID;").append(LINE_SEPARATOR);
        htmlBuf.append("}").append(LINE_SEPARATOR);
        
		htmlBuf.append("function validate() {").append(LINE_SEPARATOR);
		htmlBuf.append("  var radioBtns = document.getElementsByName('InvID');").append(LINE_SEPARATOR);
		htmlBuf.append("  if (radioBtns != null) {").append(LINE_SEPARATOR);
		htmlBuf.append("    for (i = 0; i < radioBtns.length; i++)").append(LINE_SEPARATOR);
		htmlBuf.append("      if (radioBtns[i].checked) document.InventoryBeanForm.submit();").append(LINE_SEPARATOR);
		htmlBuf.append("  }").append(LINE_SEPARATOR);
		htmlBuf.append("}").append(LINE_SEPARATOR);
		
		htmlBuf.append("function selectAccount(accountID, memberID) {").append(LINE_SEPARATOR);
		htmlBuf.append("  var form = document.cusForm;").append(LINE_SEPARATOR);
		htmlBuf.append("  form.AccountID.value = accountID;").append(LINE_SEPARATOR);
		htmlBuf.append("  form.SwitchContext.value = memberID;").append(LINE_SEPARATOR);
		htmlBuf.append("  form.submit();").append(LINE_SEPARATOR);
		htmlBuf.append("}").append(LINE_SEPARATOR);
        
        //htmlBuf.append("function changeSelected() {").append(LINE_SEPARATOR);
        //htmlBuf.append("  form.action.value = 'ManipulateInventoryResults';").append(LINE_SEPARATOR);
        //htmlBuf.append("  form.submit();").append(LINE_SEPARATOR);
        //htmlBuf.append("}").append(LINE_SEPARATOR);

        if((getHtmlStyle() & HTML_STYLE_LIST_INVENTORY) != 0)
        {
            htmlBuf.append("function checkAll() {").append(LINE_SEPARATOR);
            htmlBuf.append("var checkBoxArray = new Array();").append(LINE_SEPARATOR);
            htmlBuf.append("checkBoxArray = document.chooseForm.checkMultiInven;").append(LINE_SEPARATOR);
            htmlBuf.append("if ( checkBoxArray.length == undefined) {").append(LINE_SEPARATOR);
            htmlBuf.append("  document.chooseForm.checkMultiInven.checked = true ;").append(LINE_SEPARATOR);
            htmlBuf.append("} else {").append(LINE_SEPARATOR);
            htmlBuf.append("  for (i = 0; i < checkBoxArray.length; i++)").append(LINE_SEPARATOR);
            htmlBuf.append("    checkBoxArray[i].checked = true ;").append(LINE_SEPARATOR);
            htmlBuf.append("  }  }").append(LINE_SEPARATOR);
            
            htmlBuf.append("function uncheckAll() {").append(LINE_SEPARATOR);
            htmlBuf.append("var checkBoxArray = new Array();").append(LINE_SEPARATOR);
            htmlBuf.append("checkBoxArray = document.chooseForm.checkMultiInven;").append(LINE_SEPARATOR);
            htmlBuf.append("if ( checkBoxArray.length == undefined) {").append(LINE_SEPARATOR);
            htmlBuf.append("  document.chooseForm.checkMultiInven.checked = false ;").append(LINE_SEPARATOR);
            htmlBuf.append("} else {").append(LINE_SEPARATOR);
            htmlBuf.append("  for (i = 0; i < checkBoxArray.length; i++)").append(LINE_SEPARATOR);
            htmlBuf.append("    checkBoxArray[i].checked = false ;").append(LINE_SEPARATOR);
            htmlBuf.append("  }  }").append(LINE_SEPARATOR);
            
            htmlBuf.append("function manipSelected() {").append(LINE_SEPARATOR);
            htmlBuf.append("    document.chooseForm.action.value = \"ManipulateSelectedResults\";").append(LINE_SEPARATOR);
            htmlBuf.append("    document.chooseForm.submit();").append(LINE_SEPARATOR);
            htmlBuf.append("}").append(LINE_SEPARATOR);
     
        }
        
		if (showEnergyCompany) {
			htmlBuf.append("function selectMemberInventory(invID, memberID) {").append(LINE_SEPARATOR);
			htmlBuf.append("  var form = document.invForm;").append(LINE_SEPARATOR);
			htmlBuf.append("  form.MemberID.value = memberID;").append(LINE_SEPARATOR);
			htmlBuf.append("  form.REDIRECT.value = '").append(req.getContextPath()).append("/operator/Hardware/InventoryDetail.jsp?InvId='");
			htmlBuf.append(" + invID + '").append(srcStr).append("';").append(LINE_SEPARATOR);
			htmlBuf.append("  form.submit();").append(LINE_SEPARATOR);
			htmlBuf.append("}").append(LINE_SEPARATOR);
		}
        
		htmlBuf.append("</script>").append(LINE_SEPARATOR);
        
		return htmlBuf.toString();
	}

	/**
	 * Returns the page.
	 * @return int
	 */
	public int getPage() {
		return page;
	}

	public int getSortBy() {
		return sortBy;
	}

    public ArrayList getFilterByList() 
    {
        return filterByList;
    }
    
    public void setFilterByList(ArrayList newFilters)
    {
        if(newFilters == null)
            return;
        
        ArrayList oldFilters = filterByList;
        filterByList = new ArrayList();
        boolean memberSpecified = false;
        
        /**
         * Because of the size of Xcel, we need to handle members as the first part of
         * the filter process, regardless of where it is in the list of filters.
         * This improves our best case significantly, and to some degree, also improves
         * the worst case scenario.
         * 
         * Also added to this method: goes through and checks to see whether the stored
         * inventoryList should be rebuilt, ie. did the filters actually change.
         */
        if(oldFilters == null || oldFilters.size() != newFilters.size())
            inventoryList = null;
        
        for(int j = 0; j < newFilters.size(); j++)
        {
            Integer filterType = new Integer(((FilterWrapper)newFilters.get(j)).getFilterTypeID());
            String specificFilterString = ((FilterWrapper)newFilters.get(j)).getFilterID();
            Integer specificFilterID = InventoryUtils.returnIntegerIfPossible(specificFilterString);
            if(filterType.intValue() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_MEMBER)
            {
                setMember(specificFilterID.intValue());
                memberSpecified = true;
            }
            
            if(inventoryList != null)
            {
                boolean found = false;
                for(int x = 0; x < oldFilters.size(); x++)
                {
                    if(specificFilterID.intValue() == new Integer(((FilterWrapper)oldFilters.get(x)).getFilterID()).intValue())
                    {
                        found = true;
                    }    
                }
                
                if(!found)
                    inventoryList = null;
            }
        }
        
        /*In order to add OR logic to the filters instead of AND logic we need to first
         * sort the filters by type
         */
        Collections.sort(((List<FilterWrapper>)newFilters), FilterWrapper.filterWrapperComparator);
        filterByList = newFilters;
        
        if(!memberSpecified)
            setMember(-1);
	}

	/**
	 * Sets the page.
	 * @param page The page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * Sets the sortBy.
	 * @param sortBy The sortBy to set
	 */
	public void setSortBy(int sortBy) {
		this.sortBy = sortBy;
	}

	/**
	 * Sets the energyCompanyID.
	 * @param energyCompanyID The energyCompanyID to set
	 */
	public void setEnergyCompanyID(int energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}

	/**
	 * Returns the sortOrder.
	 * @return int
	 */
	public int getSortOrder() {
		return sortOrder;
	}

	/**
	 * Sets the sortOrder.
	 * @param sortOrder The sortOrder to set
	 */
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * Returns the htmlStyle.
	 * @return int
	 */
	public int getHtmlStyle() {
		return htmlStyle;
	}

	/**
	 * Sets the htmlStyle.
	 * @param htmlStyle The htmlStyle to set
	 */
	public void setHtmlStyle(int htmlStyle) {
		this.htmlStyle = htmlStyle;
	}

	/**
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param i
	 */
	public void setPageSize(int i) {
		pageSize = i;
	}

	/**
	 * @param list
	 */
	public void setInventorySet(ArrayList list) {
		inventorySet = list;
		inventoryList = null;
	}

	/**
	 * @param string
	 */
	public void setReferer(String string) {
		referer = string;
	}

	/**
	 * @return
	 */
	public int getSearchBy() {
		return searchBy;
	}

	/**
	 * @return
	 */
	public String getSearchValue() {
		return searchValue;
	}

	/**
	 * @param i
	 */
	public void setSearchBy(int i) {
		if (searchBy != CtiUtilities.NONE_ZERO_ID || i != CtiUtilities.NONE_ZERO_ID) {
			searchBy = i;
			inventoryList = null;
			inventorySet = null;
		}
		
		if (searchBy != CtiUtilities.NONE_ZERO_ID) {
			//filterBy = CtiUtilities.NONE_ZERO_ID;
			htmlStyle |= HTML_STYLE_INVENTORY_SET;
		}
	}

	/**
	 * @param string
	 */
	public void setSearchValue(String string) {
		searchValue = string;
	}

	/**
	 * @return
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param string
	 */
	public void setAction(String string) {
		action = string;
	}

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }
    
    public String getFilterInventoryHTML()
    {
        setHtmlStyle(HTML_STYLE_FILTERED_INVENTORY_SUMMARY);
        
        setFilterByList((ArrayList) internalRequest.getSession().getAttribute(ServletUtils.FILTER_INVEN_LIST));
        String hardwareNum = getHTML(internalRequest);
        setHtmlStyle(HTML_STYLE_LIST_INVENTORY);
        numberOfRecords = hardwareNum;
        return hardwareNum;
    }
    
    public String getNumberOfRecords()
    {
        return numberOfRecords;
    }
    
    public void setInternalRequest(HttpServletRequest req)
    {
        internalRequest = req;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public boolean getViewResults()
    {
        if(isDifferentOrigin() && isOverHardwareDisplayLimit())
            return false;
        return viewResults;
    }
    
    public void setViewResults(boolean truth)
    {
        viewResults = truth;
    }

    public void setNumberOfRecords(String numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public void setInventoryList(ArrayList inventoryList) {
        this.inventoryList = inventoryList;
    }
    
    public ArrayList getLimitedHardwareList()
    {
        try
        {
            return getHardwareList(true);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public boolean isShipmentCheck() 
    {
        return shipmentCheck;
    }

    public void setShipmentCheck(boolean shipmentCheck) 
    {
        this.shipmentCheck = shipmentCheck;
    }

    public boolean isOverHardwareDisplayLimit() 
    {
        overHardwareDisplayLimit = Integer.parseInt(numberOfRecords) > MAX_ALLOW_DISPLAY;
        return overHardwareDisplayLimit;
    }
    
    public boolean isDifferentOrigin() 
    {
        return differentOrigin;
    }

    public void setDifferentOrigin(boolean truth) 
    {
        this.differentOrigin = truth;
    }

    public boolean isCheckInvenForAccount() {
        return checkInvenForAccount;
    }

    public void setCheckInvenForAccount(boolean checkInvenForAccount) {
        this.checkInvenForAccount = checkInvenForAccount;
    }
}
