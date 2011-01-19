/*
 * Created on Oct 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.event.EventWorkOrder;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.event.dao.EventWorkOrderDao;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.filter.DirectionAwareOrderBy;
import com.cannontech.stars.util.filter.OrderBy;
import com.cannontech.stars.util.filter.filterby.workorder.WorkOrderOrderBy;
import com.cannontech.stars.web.collection.SimpleCollection;
import com.cannontech.stars.web.collection.SimpleCollectionFactory;
import com.cannontech.stars.xml.serialize.StarsServiceRequest;
import com.cannontech.util.ServletUtil;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkOrderBean {
	private static final EventWorkOrderDao eventWorkOrderDao = YukonSpringHook.getBean("eventWorkOrderDao", EventWorkOrderDao.class);
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
	
	private String start = null;
	private String stop = null;
	private Date startDate = null;
	private Date stopDate = null;
	
	private boolean viewAllResults = false;
	private List<FilterWrapper> filters = null;
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
	private List<LiteWorkOrderBase> searchResults = null;
	
	private LiteStarsEnergyCompany energyCompany = null;
	
    private final SimpleCollectionFactory simpleCollectionFactory = 
        YukonSpringHook.getBean("simpleCollectionFactory", SimpleCollectionFactory.class);
	
	public WorkOrderBean() {
	}
	
	public LiteStarsEnergyCompany getEnergyCompany() {
		if (energyCompany == null)
			energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( energyCompanyID );
		return energyCompany;
	}
	
	public boolean addWorkOrder(LiteWorkOrderBase workOrder) {
	    if (searchResults == null || (getHtmlStyle() != HTML_STYLE_SEARCH_RESULTS)) {
	        // This workOrder will be applied against the filters next time they are run.
	        return false;
	    }
	    
	    //workOrderList was set by a Search
	    searchResults.add(workOrder);
	    return true;
	}
	
	private DirectionAwareOrderBy getDirectionAwareOrderBy() {
	    boolean isAscending = getSortOrder() == SORT_ORDER_ASCENDING;
	    OrderBy orderBy = WorkOrderOrderBy.valueOf(getSortBy());
	    return new DirectionAwareOrderBy(orderBy, isAscending);
	}
    
	private SimpleCollection<LiteWorkOrderBase> getSimpleCollection() {
	    boolean isSearch = (searchResults != null && (getHtmlStyle() == HTML_STYLE_SEARCH_RESULTS));
	    if (isSearch) {
	        return simpleCollectionFactory.createWorkOrderSearchCollection(searchResults);
	    }
	    
	    if (searchResults != null) {
	        return simpleCollectionFactory.createWorkOrderSearchCollection(searchResults);
	    }
	    
	    List<Integer> energyCompanyIds = Arrays.asList(getEnergyCompany().getEnergyCompanyId());
        return simpleCollectionFactory.createWorkOrderFilterCollection(energyCompanyIds,
	                                                                   getFilters(),
	                                                                   getDirectionAwareOrderBy(),
	                                                                   getStartDate(),
	                                                                   getStopDate());
	}
	
	public List<LiteWorkOrderBase> getWorkOrderList() {
	    SimpleCollection<LiteWorkOrderBase> simpleCollection = getSimpleCollection();
	    return simpleCollection.getList();
	}
	
	public String getHTML(HttpServletRequest req) {
	    
	    SimpleCollection<LiteWorkOrderBase> simpleCollection = getSimpleCollection();
	    int count = simpleCollection.getCount();
	    
	    if (count == 0) {
			return "<p class='ErrorMsg'>No service order found.</p>";
	    }
	    
		String uri = req.getRequestURI();
		String pageName = uri.substring( uri.lastIndexOf('/') + 1 );
		
		if (page < 1) page = 1;
		int maxPageNo = (int) Math.ceil(count * 1.0 / pageSize);
		if (page > maxPageNo) page = maxPageNo;
		
		int maxPageDigit = (int)(Math.log(maxPageNo) / Math.log(10)) + 1;
		
		int minOrderNo = (page - 1) * pageSize + 1;
		int maxOrderNo = Math.min(page * pageSize, count);
        
		StringBuilder navBuf = new StringBuilder();
		navBuf.append(minOrderNo);
		if (maxOrderNo > minOrderNo)
			navBuf.append("-").append(maxOrderNo);
		navBuf.append(" of ").append(count);
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
		
		StringBuilder htmlBuf = new StringBuilder();
		
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
        htmlBuf.append("	document.MForm.action.value = \"ManipulateSelectedResults\";").append(LINE_SEPARATOR);
        htmlBuf.append(" 	document.MForm.submit();").append(LINE_SEPARATOR);
        htmlBuf.append("}").append(LINE_SEPARATOR);

        htmlBuf.append("function searchOrderID(orderID, memberID) {").append(LINE_SEPARATOR);
        htmlBuf.append("    document.MForm.action.value = \"SearchWorkOrder\";").append(LINE_SEPARATOR);
        htmlBuf.append("    document.MForm.SearchValue.value = orderID;").append(LINE_SEPARATOR);
        htmlBuf.append("    document.MForm.SwitchContext.value = memberID;").append(LINE_SEPARATOR);
        htmlBuf.append("    document.MForm.submit();").append(LINE_SEPARATOR);
        htmlBuf.append("}").append(LINE_SEPARATOR);
        htmlBuf.append("</script>").append(LINE_SEPARATOR);

        htmlBuf.append("<input type=\"hidden\" name=\"SearchValue\" value=\"\">");
        htmlBuf.append("<input type=\"hidden\" name=\"SwitchContext\" value=\"").append(getEnergyCompany().getEnergyCompanyId()).append("\">");
        
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
		htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
		
		int fromIndex = minOrderNo - 1;
		int toIndex = maxOrderNo;
		
		List<LiteWorkOrderBase> workOrderList = simpleCollection.getList(fromIndex, toIndex);
		
		Map<Integer,List<EventWorkOrder>> eventWorkOrderMap = 
		    eventWorkOrderDao.getByWorkOrders(workOrderList);
		
		Map<Integer, LiteStarsEnergyCompany> energyCompanyIdMap = 
		    StarsDatabaseCache.getInstance().getAllEnergyCompanyMap();
		
		for (final LiteWorkOrderBase liteOrder : workOrderList) {
		    LiteStarsEnergyCompany liteStarsEC_Order = energyCompanyIdMap.get(liteOrder.getEnergyCompanyID());
            
            List<EventWorkOrder> eventWorkOrderList = eventWorkOrderMap.get(liteOrder.getLiteID());
            if (eventWorkOrderList == null) eventWorkOrderList = new ArrayList<EventWorkOrder>();
            
			StarsServiceRequest starsOrder = StarsLiteFactory.createStarsServiceRequest(liteOrder, liteStarsEC_Order);
			Date date = null;
			if (eventWorkOrderList.size() > 0)
				date = StarsUtils.translateDate(eventWorkOrderList.get(0).getEventBase().getEventTimestamp().getTime());

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
			htmlBuf.append("          <td class='TableCell' width='13%' >").append(ServletUtils.forceNotEmpty( StringEscapeUtils.escapeHtml(starsOrder.getOrderedBy()) )).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='20%' >").append(ServletUtils.forceNotEmpty( starsOrder.getServiceCompany().getContent() )).append("</td>").append(LINE_SEPARATOR);
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
	public void setSearchResults(List<LiteWorkOrderBase> list) {
        searchResults = list;
	}

	public List<FilterWrapper> getFilters() {
		return filters;
	}

	public void setFilters(List<FilterWrapper> newFilters) {
		if (newFilters == null || !(newFilters.equals(filters)))
		{
			if (newFilters == null) {
				this.filters = new ArrayList<FilterWrapper>();
			} else {
				this.filters = newFilters;
			}
			setPage(1);
		}
	}

	public String getFilterHTML(HttpServletRequest req)
    {
		//TODO Need to tweak so we don't load all the WorkOrders multiple times. 
//        setHtmlStyle(HTML_STYLE_FILTERED_INVENTORY_SUMMARY);
	    @SuppressWarnings("unchecked") List<FilterWrapper> filterList = 
	        (List<FilterWrapper>) req.getSession(false).getAttribute(ServletUtil.FILTER_WORKORDER_LIST);
        setFilters(filterList);
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
	    return getSimpleCollection().getCount();
	}
	
	public boolean isInitialized() {
		return filters != null;
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
	    List<LiteWorkOrderBase> cleanList = 
	        new ArrayList<LiteWorkOrderBase>(Pair.removePair(workOrderList, LiteWorkOrderBase.class));
		this.searchResults = cleanList;
	}

	public int getSortBy() {
		return sortBy;
	}

	public void setSortBy(int sortBy) {
		if( this.sortBy != sortBy)
		{
			this.sortBy = sortBy;
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
