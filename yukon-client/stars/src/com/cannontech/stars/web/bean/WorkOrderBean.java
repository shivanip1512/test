/*
 * Created on Oct 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.event.EventWorkOrder;
import com.cannontech.database.data.stars.report.WorkOrderBase;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.serialize.StarsServiceRequest;
import com.cannontech.util.ServletUtil;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkOrderBean {
	
	private boolean manageMembers = false;
	public static final int SORT_ORDER_ASCENDING = 0;
	public static final int SORT_ORDER_DESCENDING = 1;
	
	public static final int HTML_STYLE_WORK_ORDERS = 0;
	public static final int HTML_STYLE_SEARCH_RESULTS = 1;
	public static final int HTML_STYLE_LIST_ALL = 2;
	
	private static final int DEFAULT_PAGE_SIZE = 250;
    private final int MAX_ALLOW_DISPLAY = 500;
    private boolean maxDisplayLimit = false;
    
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private static final Comparator ORDER_ID_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteWorkOrderBase so1 = (LiteWorkOrderBase) o1;
			LiteWorkOrderBase so2 = (LiteWorkOrderBase) o2;
			return so1.getOrderID() - so2.getOrderID();
		}
	};
	
	private static final Comparator ORDER_NO_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteWorkOrderBase so1 = (LiteWorkOrderBase) o1;
			LiteWorkOrderBase so2 = (LiteWorkOrderBase) o2;
			int result = 0;
			
			Long on1 = null;
			try {
				on1 = Long.valueOf( so1.getOrderNumber() );
			}
			catch (NumberFormatException e) {}
			
			Long on2 = null;
			try {
				on2 = Long.valueOf( so2.getOrderNumber() );
			}
			catch (NumberFormatException e) {}
			
			if (on1 != null && on2 != null) {
				result = on1.compareTo( on2 );
				if (result == 0) result = so1.getOrderNumber().compareTo( so2.getOrderNumber() );
			}
			else if (on1 != null && on2 == null)
				return -1;
			else if (on1 == null && on2 != null)
				return 1;
			else
				result = so1.getOrderNumber().compareTo( so2.getOrderNumber() );
			
			if (result == 0) result = so1.getOrderID() - so2.getOrderID();
			return result;
		}
	};
	
	private static final Comparator SERVICE_COMPANY_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {

			LiteWorkOrderBase so1 = (LiteWorkOrderBase) o1;
			LiteWorkOrderBase so2 = (LiteWorkOrderBase) o2;
			if( so1 == null || so2 == null)
				System.out.println("HERE");
			int servCo1 = so1.getServiceCompanyID();
		    int servCo2 = so2.getServiceCompanyID();
		    
		    LiteServiceCompany lsc1 = StarsDatabaseCache.getInstance().getEnergyCompany(so1.getEnergyCompanyID()).getServiceCompany(servCo1);
		    LiteServiceCompany lsc2 = StarsDatabaseCache.getInstance().getEnergyCompany(so2.getEnergyCompanyID()).getServiceCompany(servCo2);

		    String thisVal = (lsc1 == null ? CtiUtilities.STRING_NONE : lsc1.getCompanyName());
		    String anotherVal = (lsc2 == null ? CtiUtilities.STRING_NONE : lsc2.getCompanyName());

			return (thisVal.compareToIgnoreCase(anotherVal));
		}
	};	
	
	private static final Comparator SERVICE_STATUS_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {

			LiteWorkOrderBase so1 = (LiteWorkOrderBase) o1;
			LiteWorkOrderBase so2 = (LiteWorkOrderBase) o2;
			if( so1 == null || so2 == null)
				System.out.println("HERE");			
			int servStat1 = so1.getCurrentStateID();
		    int servStat2 = so2.getCurrentStateID();
		    YukonListEntry yle1 = DaoFactory.getYukonListDao().getYukonListEntry(servStat1);
		    YukonListEntry yle2 = DaoFactory.getYukonListDao().getYukonListEntry(servStat2);
		    
		    String thisVal = (yle1 == null ? CtiUtilities.STRING_DASH_LINE : yle1.getEntryText());
		    String anotherVal = (yle2 == null ? CtiUtilities.STRING_DASH_LINE : yle2.getEntryText());

			return (thisVal.compareToIgnoreCase(anotherVal));
		}
	};	
	
	private static final Comparator SERVICE_TYPE_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {

			LiteWorkOrderBase so1 = (LiteWorkOrderBase) o1;
			LiteWorkOrderBase so2 = (LiteWorkOrderBase) o2;
			if( so1 == null || so2 == null)
				System.out.println("HERE");
			int servType1 = so1.getWorkTypeID();
		    int servType2 = so2.getWorkTypeID();
		    YukonListEntry yle1 = DaoFactory.getYukonListDao().getYukonListEntry(servType1);
		    YukonListEntry yle2 = DaoFactory.getYukonListDao().getYukonListEntry(servType2);
		    
		    String thisVal = (yle1 == null ? CtiUtilities.STRING_DASH_LINE : yle1.getEntryText());
		    String anotherVal = (yle2 == null ? CtiUtilities.STRING_DASH_LINE : yle2.getEntryText());

			return (thisVal.compareToIgnoreCase(anotherVal));
		}
	};
	
	private static final Comparator ORDER_DATE_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteWorkOrderBase so1 = (LiteWorkOrderBase) o1;
			LiteWorkOrderBase so2 = (LiteWorkOrderBase) o2;
			int rslt = (new Date(so1.getDateReported()).compareTo(new Date(so2.getDateReported())));
			if (rslt == 0)
				rslt = so1.getOrderID() - so2.getOrderID();
			return rslt;
		}
	};
	
	private String start = null;
	private String stop = null;
	private Date startDate = null;
	private Date stopDate = null;
	
	private int numberOfRecords = 0;
	private boolean viewAllResults = false;
	private ArrayList<FilterWrapper> filters = null;
	private int sortBy = CtiUtilities.NONE_ZERO_ID;
	private int sortOrder = SORT_ORDER_ASCENDING;
	private int filterBy = CtiUtilities.NONE_ZERO_ID;
	private int serviceStatus = CtiUtilities.NONE_ZERO_ID;
	private int serviceType = CtiUtilities.NONE_ZERO_ID;
	private int serviceCompany = CtiUtilities.NONE_ZERO_ID;
	private int page = 1;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int energyCompanyID = 0;
	private int htmlStyle = HTML_STYLE_WORK_ORDERS;
	private String referer = null;
	private ArrayList searchResults = null;
	
	private boolean sortByChanged = false;
	private boolean sortOrderChanged = false;
	
	private LiteStarsEnergyCompany energyCompany = null;
	private List<LiteWorkOrderBase> workOrderList = null;
	
	public WorkOrderBean() {
	}
	
/*	private long getRelevantDate(LiteWorkOrderBase liteOrder) {
		if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_STATUS) {
			if (getServiceStatus() == YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PENDING)
				return liteOrder.getDateReported();
			if (getServiceStatus() == YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_SCHEDULED)
				return liteOrder.getDateScheduled();
			if (getServiceStatus() == YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_COMPLETED)
				return liteOrder.getDateCompleted();
			if (getServiceStatus() == YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_CANCELLED)
				return liteOrder.getDateCompleted();
		}
		
		return liteOrder.getDateReported();
	}*/

	/**
	 * @return
	 */
	public LiteStarsEnergyCompany getEnergyCompany() {
		if (energyCompany == null)
			energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( energyCompanyID );
		return energyCompany;
	}


    private List<LiteWorkOrderBase> filterByServiceCompany(final List<LiteWorkOrderBase> list, final int specificFilterId) {
        final List<LiteWorkOrderBase> filteredWorkOrders = new ArrayList<LiteWorkOrderBase>();
        for (final LiteWorkOrderBase liteWorkOrder : list) {
            if (liteWorkOrder.getServiceCompanyID() == specificFilterId) {
                filteredWorkOrders.add(liteWorkOrder);
            }
        }
        return filteredWorkOrders;
    }
    
    private List<LiteWorkOrderBase> filterByServiceType(final List<LiteWorkOrderBase> list, final int specificFilterId) {
        final List<LiteWorkOrderBase> filteredWorkOrders = new ArrayList<LiteWorkOrderBase>();
        for (final LiteWorkOrderBase liteWorkOrder : list) {
            if (liteWorkOrder.getWorkTypeID() == specificFilterId) {
                filteredWorkOrders.add(liteWorkOrder);
            }
        }
        return filteredWorkOrders;
    }
    
    private List<LiteWorkOrderBase> filterByServiceCompanyCodes(final List<LiteWorkOrderBase> list, final int specificFilterId, final FilterWrapper filter) {
        final List<LiteWorkOrderBase> filteredWorkOrders = new ArrayList<LiteWorkOrderBase>();
        for (final LiteWorkOrderBase liteWorkOrder : list) {
            LiteStarsCustAccountInformation liteCustAcctInfo = this.energyCompany.getBriefCustAccountInfo(liteWorkOrder.getAccountID(), true);
            LiteAddress liteAddr = this.energyCompany.getAddress(liteCustAcctInfo.getAccountSite().getStreetAddressID());
            
            //The filterText is formatted "Label: value".  By parsing for the last space char we can get just the value.
            String tempCode = filter.getFilterText().substring(filter.getFilterText().lastIndexOf(" ")+1);
            if (liteAddr.getZipCode().startsWith(tempCode)) {
                filteredWorkOrders.add(liteWorkOrder);    
            }    
        }
        return filteredWorkOrders;
    }
    
    private List<LiteWorkOrderBase> filterByCustomerType(final List<LiteWorkOrderBase> list, final int specificFilterId) {
        final List<LiteWorkOrderBase> filteredWorkOrders = new ArrayList<LiteWorkOrderBase>();
        for (final LiteWorkOrderBase liteWorkOrder : list) {    
            LiteStarsCustAccountInformation liteCustAcctInfo = this.energyCompany.getBriefCustAccountInfo(liteWorkOrder.getAccountID(), true);
            LiteCustomer customer = liteCustAcctInfo.getCustomer();

            switch (specificFilterId) {
                case -1 : { //RESIDENTIAL CUSTOMER
                    if (customer.getCustomerTypeID() == CustomerTypes.CUSTOMER_RESIDENTIAL) {
                        filteredWorkOrders.add(liteWorkOrder);
                    }
                    break;
                }
                case YukonListEntryTypes.CUSTOMER_TYPE_COMMERCIAL : {
                    if (customer instanceof LiteCICustomer) {
                        int customerTypeId = ((LiteCICustomer) customer).getCICustType();
                        if (customerTypeId == specificFilterId) {
                            filteredWorkOrders.add(liteWorkOrder);
                        }
                    } else {
                        int customerTypeId = customer.getCustomerTypeID();
                        if (customerTypeId == CustomerTypes.CUSTOMER_CI) {
                            filteredWorkOrders.add(liteWorkOrder);
                        }
                    }
                    break;
                }
            }
        }
        return filteredWorkOrders;
    }
    
    private List<LiteWorkOrderBase> filterByStatus(final List<LiteWorkOrderBase> list, final int specificFilterId) {
        final ArrayList<LiteWorkOrderBase> filteredWorkOrders = new ArrayList<LiteWorkOrderBase>();
        for (final LiteWorkOrderBase liteWorkOrder : list) {
            //no dates are specified, just check current state ID
            if (getStartDate() == null && getStopDate() == null) {
                if (liteWorkOrder.getCurrentStateID() == specificFilterId) { 
                    filteredWorkOrders.add(liteWorkOrder);
                }    
            } else {
                //Dates were specified so we look at the event type to see if it matches the status filter
                final WorkOrderBase workOrderBase = (WorkOrderBase) StarsLiteFactory.createDBPersistent(liteWorkOrder);
                final ArrayList<EventWorkOrder> eventWorkOrderList = workOrderBase.getEventWorkOrders();
                for (final EventWorkOrder eventWorkOrder : eventWorkOrderList) {
                    boolean addWorkOrder = false;
                    if (eventWorkOrder.getEventBase().getActionID().intValue() == specificFilterId) {
                        if (getStartDate() != null && getStopDate() == null) {
                            if (eventWorkOrder.getEventBase().getEventTimestamp().compareTo(getStartDate()) >= 0)
                                addWorkOrder = true;
                        } else if (getStopDate() != null && getStartDate() == null) {
                            if (eventWorkOrder.getEventBase().getEventTimestamp().compareTo(getStopDate()) <= 0)
                                addWorkOrder = true;
                        } else {
                            if ((eventWorkOrder.getEventBase().getEventTimestamp().compareTo(getStartDate()) >= 0) &&
                                    (eventWorkOrder.getEventBase().getEventTimestamp().compareTo(getStopDate()) <= 0))
                                addWorkOrder = true;
                        }
                        if (addWorkOrder) {
                            filteredWorkOrders.add(liteWorkOrder);
                            break;
                        }
                    }
                }
            }
        }
        return filteredWorkOrders;
    }
    
	public List<LiteWorkOrderBase> getWorkOrderList() {
		if (workOrderList != null)
		{	//Update the order without reloading all of the workOrder objects.
			if( sortByChanged || sortOrderChanged )
				workOrderList = sortList(workOrderList);

			sortByChanged = sortOrderChanged = false;
			return workOrderList;
		}
		
		List<LiteWorkOrderBase> workOrders = null;
		if (getHtmlStyle() == HTML_STYLE_SEARCH_RESULTS)
        {
            workOrders = new ArrayList<LiteWorkOrderBase>();
            for (int i = 0; i < searchResults.size(); i++)
            {
                if( searchResults.get(i) instanceof Pair)
                    workOrders.add( (LiteWorkOrderBase)((Pair)searchResults.get(i)).getFirst());
                else
                    workOrders.add( (LiteWorkOrderBase)searchResults.get(i));
            }
            workOrderList = sortList(workOrders);
            return workOrderList;
        }

        workOrders = getEnergyCompany().loadAllWorkOrders( true );
		
        final List<FilterWrapper> filterList = getFilters();
		if (filterList != null  && filterList.size() > 0) {
            
			for (final FilterWrapper filter : filterList) {
                final int filterTypeID = Integer.parseInt(filter.getFilterTypeID());
	            final int specificFilterID = Integer.parseInt(filter.getFilterID());			
			
                switch (filterTypeID) {
                    case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMPANY : 
                        workOrders = this.filterByServiceCompany(workOrders, specificFilterID);
                        break;
                        
                    case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_TYPE :
                        workOrders = this.filterByServiceType(workOrders, specificFilterID);
                        break;
                        
                    case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMP_CODES :
                        workOrders = this.filterByServiceCompanyCodes(workOrders, specificFilterID, filter);
                        break;
                    
                    case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_CUST_TYPE :
                        workOrders = this.filterByCustomerType(workOrders, specificFilterID);
                        break;
                        
                    case YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_STATUS :
                        //Do nothing, skip this filter until all the other filters are done.  See the next iteration below.
                        break;
                }
			}

			//Do just the status filter, since we already handled the other filters.
            for (final FilterWrapper filter : filterList) {	
				final int filterTypeID = Integer.parseInt(filter.getFilterTypeID());
	            final int specificFilterID = Integer.parseInt(filter.getFilterID());			
					
				if (filterTypeID == YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_STATUS) {
                    workOrders = this.filterByStatus(workOrders, specificFilterID);
                }
			}
		} else {
			workOrders = Collections.emptyList();	
		}
		
		workOrderList = sortList(workOrders);
		return workOrderList;
	}

	private List<LiteWorkOrderBase> sortList(List<LiteWorkOrderBase> workOrders)
	{
		workOrderList = workOrders;
		if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_ORDER_NO)
			Collections.sort(workOrderList, ORDER_NO_CMPTOR );
		else if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_SERV_COMP)
			Collections.sort(workOrderList, SERVICE_COMPANY_CMPTOR );
		else if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_SERV_STAT)
			Collections.sort(workOrderList, SERVICE_STATUS_CMPTOR );
		else if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_SERV_TYPE)
			Collections.sort(workOrderList, SERVICE_TYPE_CMPTOR );
//		else if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_CUST_TYPE)
//			sortedOrders = new java.util.TreeSet( TODO);
		else if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_DATE_TIME)
			Collections.sort(workOrderList, ORDER_DATE_CMPTOR );
		else
			Collections.sort(workOrderList, ORDER_NO_CMPTOR );		
	
		if (getSortOrder() == SORT_ORDER_DESCENDING)
			Collections.reverse(workOrderList);
		
		return workOrderList;
	}
	
	public String getHTML(HttpServletRequest req) {
		StarsYukonUser user = (StarsYukonUser) req.getSession().getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		
		/*setManageMembers(false);
        if(DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS ) && 
        		(getEnergyCompany().getChildren().size() > 0)){
                setManageMembers(true);
		}*/		
		
        List<LiteWorkOrderBase> soList = getWorkOrderList();
		if (soList == null || soList.size() == 0)
			return "<p class='ErrorMsg'>No service order found.</p>";
		
		String uri = req.getRequestURI();
		String pageName = uri.substring( uri.lastIndexOf('/') + 1 );
		
		if (page < 1) page = 1;
		int maxPageNo = (int) Math.ceil(soList.size() * 1.0 / pageSize);
		if (page > maxPageNo) page = maxPageNo;
		
		int maxPageDigit = (int)(Math.log(maxPageNo) / Math.log(10)) + 1;
		
		int minOrderNo = (page - 1) * pageSize + 1;
		int maxOrderNo = Math.min(page * pageSize, soList.size());
        
		StringBuffer navBuf = new StringBuffer();
		navBuf.append(minOrderNo);
		if (maxOrderNo > minOrderNo)
			navBuf.append("-").append(maxOrderNo);
		navBuf.append(" of ").append(soList.size());
		navBuf.append(" | ");
		if (page == 1)
			navBuf.append("<font color='#CCCCCC'>First</font>");
		else
			navBuf.append("<a class='Link1' href='").append(pageName).append("?page_=1'>First</a>");
		navBuf.append(" | ");
		if (page == 1)
			navBuf.append("<font color='#CCCCCC'>Previous</font>");
		else
			navBuf.append("<a class='Link1' href='").append(pageName).append("?page_=").append(page-1).append("'>Previous</a>");
		navBuf.append(" | ");
		if (page == maxPageNo)
			navBuf.append("<font color='#CCCCCC'>Next</font>");
		else
			navBuf.append("<a class='Link1' href='").append(pageName).append("?page_=").append(page+1).append("'>Next</a>");
		navBuf.append(" | ");
		if (page == maxPageNo)
			navBuf.append("<font color='#CCCCCC'>Last</font>");
		else
			navBuf.append("<a class='Link1' href='").append(pageName).append("?page_=").append(maxPageNo).append("'>Last</a>");
		
		StringBuffer htmlBuf = new StringBuffer();
		
		htmlBuf.append("<script language='JavaScript'>").append(LINE_SEPARATOR);
        htmlBuf.append("function checkAll() {").append(LINE_SEPARATOR);
        htmlBuf.append("var checkBoxArray = new Array();").append(LINE_SEPARATOR);
        htmlBuf.append("checkBoxArray = document.MForm.checkWorkOrder;").append(LINE_SEPARATOR);
        htmlBuf.append("if ( checkBoxArray.length == undefined) {").append(LINE_SEPARATOR);
        htmlBuf.append("  document.MForm.checkWorkOrder.checked = true ;").append(LINE_SEPARATOR);
        htmlBuf.append("} else {").append(LINE_SEPARATOR);
        htmlBuf.append("  for (i = 0; i < checkBoxArray.length; i++)").append(LINE_SEPARATOR);
        htmlBuf.append("    checkBoxArray[i].checked = true ;").append(LINE_SEPARATOR);
        htmlBuf.append("  }  }").append(LINE_SEPARATOR);
        
        htmlBuf.append("function uncheckAll() {").append(LINE_SEPARATOR);
        htmlBuf.append("var checkBoxArray = new Array();").append(LINE_SEPARATOR);
        htmlBuf.append("checkBoxArray = document.MForm.checkWorkOrder;").append(LINE_SEPARATOR);
        htmlBuf.append("if ( checkBoxArray.length == undefined) {").append(LINE_SEPARATOR);
        htmlBuf.append("  document.MForm.checkWorkOrder.checked = false;").append(LINE_SEPARATOR);
        htmlBuf.append("} else {").append(LINE_SEPARATOR);
        htmlBuf.append("  for (i = 0; i < checkBoxArray.length; i++)").append(LINE_SEPARATOR);
        htmlBuf.append("    checkBoxArray[i].checked = false ;").append(LINE_SEPARATOR);
        htmlBuf.append("  }  }").append(LINE_SEPARATOR);
        
        htmlBuf.append("function manipSelected() {").append(LINE_SEPARATOR);
        htmlBuf.append("	this.MForm.action.value = \"ManipulateSelectedResults\";").append(LINE_SEPARATOR);
        htmlBuf.append(" 	this.MForm.submit();").append(LINE_SEPARATOR);
        htmlBuf.append("}").append(LINE_SEPARATOR);

        htmlBuf.append("function searchOrderID(orderID, memberID) {").append(LINE_SEPARATOR);
        htmlBuf.append("    this.MForm.action.value = \"SearchWorkOrder\";").append(LINE_SEPARATOR);
        htmlBuf.append("    this.MForm.SearchValue.value = orderID;").append(LINE_SEPARATOR);
        htmlBuf.append("    this.MForm.SwitchContext.value = memberID;").append(LINE_SEPARATOR);
        htmlBuf.append("    this.MForm.submit();").append(LINE_SEPARATOR);
        htmlBuf.append("}").append(LINE_SEPARATOR);
        htmlBuf.append("</script>").append(LINE_SEPARATOR);

        htmlBuf.append("<input type=\"hidden\" name=\"SearchValue\" value=\"\">");
        htmlBuf.append("<input type=\"hidden\" name=\"SwitchContext\" value=\"").append(getEnergyCompany().getEnergyCompanyID().intValue()).append("\">");
        
		htmlBuf.append("<table width='95%' border='0' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td>").append(LINE_SEPARATOR);
		htmlBuf.append("      <table width='100%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>").append(LINE_SEPARATOR);
		htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td>").append(navBuf).append("</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td align='right'>Page(1-").append(maxPageNo).append("): ")
				.append("<input type='text' id='GoPage' style='border:1px solid #0066CC; font:11px' size='").append(maxPageDigit).append("' value='").append(page).append("'> ")
				.append("<input type='button' style='font:11px; margin-bottom:-1px' value='Go' onclick='location.href=\"").append(pageName).append("?page_=\" + document.getElementById(\"GoPage\").value;'>")
				.append("</td>").append(LINE_SEPARATOR);
		htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
		htmlBuf.append("      </table>").append(LINE_SEPARATOR);
		htmlBuf.append("    </td>").append(LINE_SEPARATOR);
		htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
		
		htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td>").append(LINE_SEPARATOR);
		htmlBuf.append("      <table width='100%' border='1' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
		htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
        if ((getHtmlStyle() & HTML_STYLE_LIST_ALL) != 0)
            htmlBuf.append("          <td  class='HeaderCell' width='1%' >&nbsp;</td>").append(LINE_SEPARATOR);
        
        htmlBuf.append("          <td  class='HeaderCell' width='13%' >Order #</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td  class='HeaderCell' width='13%' >Date/Time</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td  class='HeaderCell' width='13%' >Order Type</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td  class='HeaderCell' width='13%' >Current State</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td  class='HeaderCell' width='13%' >Ordered By</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td  class='HeaderCell' width='20%' >Assigned</td>").append(LINE_SEPARATOR);
//		htmlBuf.append("          <td  class='HeaderCell' width='34%' >Description</td>").append(LINE_SEPARATOR);
		htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
		
		for (int i = minOrderNo; i <= maxOrderNo; i++) {
        LiteStarsEnergyCompany liteStarsEC_Order = null;
//		for (int i = 0; i < soList.size(); i++) {
//			LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) soList.get(i);
            LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) soList.get(i-1);
            if( liteStarsEC_Order == null || liteStarsEC_Order.getEnergyCompanyID().intValue() != liteOrder.getEnergyCompanyID())
                liteStarsEC_Order = StarsDatabaseCache.getInstance().getEnergyCompany(liteOrder.getEnergyCompanyID());
            
			StarsServiceRequest starsOrder = StarsLiteFactory.createStarsServiceRequest(liteOrder, liteStarsEC_Order);
			Date date = null;
			if( liteOrder.getEventWorkOrders().size() > 0)
				date = StarsUtils.translateDate( liteOrder.getEventWorkOrders().get(0).getEventBase().getEventTimestamp().getTime());

			String dateStr = (date != null)? StarsUtils.formatDate(date, liteStarsEC_Order.getDefaultTimeZone()) : "----";

			htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
			
			if ((getHtmlStyle() & HTML_STYLE_LIST_ALL) != 0)
            {
                htmlBuf.append("          <td class='TableCell' width='1%'>");
                htmlBuf.append("<input type='checkbox' name='checkWorkOrder' value='").append(liteOrder.getOrderID()).append("'>");
                htmlBuf.append("</td>").append(LINE_SEPARATOR);
            }        
			htmlBuf.append("          <td class='TableCell' width='13%' >")
					.append("<a href='javascript:searchOrderID(").append(liteOrder.getOrderID()).append(", ").append(liteOrder.getEnergyCompanyID()).append(")' class='Link1'>")
					.append(starsOrder.getOrderNumber()).append("</a></td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='13%' >").append(dateStr).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='13%' >").append(ServletUtils.forceNotEmpty( starsOrder.getServiceType().getContent() )).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='13%' >").append(starsOrder.getCurrentState().getContent()).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='13%' >").append(ServletUtils.forceNotEmpty( starsOrder.getOrderedBy() )).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='20%' >").append(ServletUtils.forceNotEmpty( starsOrder.getServiceCompany().getContent() )).append("</td>").append(LINE_SEPARATOR);
/*			htmlBuf.append("          <td class='TableCell' width='34%'>").append(LINE_SEPARATOR);
			htmlBuf.append("            <textarea name='textarea' rows='2' wrap='soft' cols='35' class='TableCell' readonly>")
					.append(starsOrder.getDescription().replaceAll("<br>", LINE_SEPARATOR)).append("</textarea>").append(LINE_SEPARATOR);
			htmlBuf.append("          </td>").append(LINE_SEPARATOR);*/
			htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
		}
		
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
        
		if (getHtmlStyle() == HTML_STYLE_SEARCH_RESULTS) {
			htmlBuf.append("<br>").append(LINE_SEPARATOR);
			htmlBuf.append("<table width='200' border='0' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
			htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
			htmlBuf.append("    <td align='center'>").append(LINE_SEPARATOR);
			if (referer != null && !referer.equals("") && !referer.equals(req.getRequestURL().toString()))
				htmlBuf.append("      <input type='button' name='Back' value='Back' onclick='location.href=\"").append(referer).append("\"'>").append(LINE_SEPARATOR);
			else
				htmlBuf.append("      <input type='button' name='Back' value='Back' onclick='history.back()'>").append(LINE_SEPARATOR);
			htmlBuf.append("    </td>").append(LINE_SEPARATOR);
			htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
			htmlBuf.append("</table>").append(LINE_SEPARATOR);
		}
		else if((getHtmlStyle() & HTML_STYLE_LIST_ALL) != 0)
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
            /*htmlBuf.append("    <td align='right'>").append(LINE_SEPARATOR);
            htmlBuf.append("      <input type='button' name='ChangeSelected' value='Change Selected' onclick='changeSelected()>").append(LINE_SEPARATOR);
            htmlBuf.append("    </td>").append(LINE_SEPARATOR);*/
            htmlBuf.append("    <td>").append(LINE_SEPARATOR);
            if (referer != null)
                htmlBuf.append("      <input type='button' name='Cancel' value='Cancel' onclick='location.href=\"").append(referer).append("\"'>").append(LINE_SEPARATOR);
            else
                htmlBuf.append("      <input type='button' name='Cancel' value='Cancel' onclick='history.back()'>").append(LINE_SEPARATOR);
            htmlBuf.append("    </td>").append(LINE_SEPARATOR);
            htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
            htmlBuf.append("</table>").append(LINE_SEPARATOR);
        }
		
		return htmlBuf.toString();
	}

	/**
	 * @return
	 */
	public int getPage() {
		return page;
	}

	/**
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @return
	 */
	public int getSortOrder() {
		return sortOrder;
	}

	/**
	 * @param i
	 */
	public void setEnergyCompanyID(int i) {
		energyCompanyID = i;
	}

	/**
	 * @param i
	 */
	public void setPage(int i) {
		page = i;
	}

	/**
	 * @param i
	 */
	public void setPageSize(int i) {
		pageSize = i;
	}

	/**
	 * @param i
	 */
	public void setSortOrder(int i) {
		if( sortOrder != i)
		{
			sortOrder = i;
			sortOrderChanged = true;
		}
	}

	/**
	 * @return
	 */
	public int getHtmlStyle() {
		return htmlStyle;
	}

	/**
	 * @param i
	 */
	public void setHtmlStyle(int i) {
		htmlStyle = i;
	}

	/**
	 * @param string
	 */
	public void setReferer(String string) {
		referer = string;
	}

	/**
	 * @param list
	 */
	public void setSearchResults(ArrayList list) {
        if( list == null || !list.equals(searchResults))
        {
            workOrderList = null;
            searchResults = list;
        }
	}

	public ArrayList<FilterWrapper> getFilters() {
		return filters;
	}

	public void setFilters(ArrayList<FilterWrapper> newFilters) {
		if( newFilters == null || !(newFilters.equals(filters)) )
		{
			this.filters = newFilters;
			workOrderList = null;	//TODO dump the existing list!
			setPage(1);
		}
	}

	public String getFilterHTML(HttpServletRequest req)
    {
		//TODO Need to tweak so we don't load all the WorkOrders multiple times. 
//        setHtmlStyle(HTML_STYLE_FILTERED_INVENTORY_SUMMARY);
        
        setFilters((ArrayList<FilterWrapper>) req.getSession().getAttribute(ServletUtil.FILTER_WORKORDER_LIST));
        String hardwareNum = getHTML(req);
//        setHtmlStyle(HTML_STYLE_LIST_INVENTORY);
//        numberOfRecords = hardwareNum;
        return hardwareNum;
    }

	public boolean isViewAllResults() {
		return viewAllResults;
	}

	public void setViewAllResults(boolean viewAllResults) {
		this.viewAllResults = viewAllResults;
		if( viewAllResults)
			setHtmlStyle(HTML_STYLE_LIST_ALL);
		else
			setHtmlStyle(HTML_STYLE_WORK_ORDERS);
	}

	public int getNumberOfRecords() {
		if( getWorkOrderList() != null)
			return getWorkOrderList().size();
		return 0;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String newStart)
	{
		this.start = newStart;
		if( newStart != null)
			setStartDate(ServletUtil.parseDateStringLiberally(start));
		else
			setStartDate(null);
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getStop() {
		return stop;
	}

	public void setStop(String newStop) {
		this.stop = newStop;
		if( newStop != null)
			setStopDate(ServletUtil.parseDateStringLiberally(stop));
		else
			setStopDate(null);		
	}

	public Date getStopDate() {
		return stopDate;
	}

	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}

	public void setWorkOrderList(List<LiteWorkOrderBase> workOrderList) {
		this.workOrderList = workOrderList;
	}

	public int getSortBy() {
		return sortBy;
	}

	public void setSortBy(int sortBy) {
		if( this.sortBy != sortBy)
		{
			this.sortBy = sortBy;
			sortByChanged = true;
		}
	}

	public int getFilterBy() {
		return filterBy;
	}

	public void setFilterBy(int filterBy) {
		this.filterBy = filterBy;
	}

	public int getServiceCompany() {
		return serviceCompany;
	}

	public void setServiceCompany(int serviceCompany) {
		this.serviceCompany = serviceCompany;
	}

	public int getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(int serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	public boolean isManageMembers() {
		return manageMembers;
	}

	public void setManageMembers(boolean manageMembers) {
		this.manageMembers = manageMembers;
	}

    public boolean isMaxDisplayLimit()
    {
        maxDisplayLimit = getNumberOfRecords() > MAX_ALLOW_DISPLAY;
        return maxDisplayLimit;
    }
}
