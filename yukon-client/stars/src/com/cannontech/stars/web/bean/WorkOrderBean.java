/*
 * Created on Oct 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Comparator;
import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.serialize.StarsServiceRequest;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkOrderBean {
	
	public static final int SORT_ORDER_ASCENDING = 0;
	public static final int SORT_ORDER_DESCENDING = 1;
	
	public static final int HTML_STYLE_WORK_ORDERS = 0;
	public static final int HTML_STYLE_SEARCH_RESULTS = 1;
	
	private static final int DEFAULT_PAGE_SIZE = 20;
	
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
			int rslt = so1.getOrderNumber().compareTo( so2.getOrderNumber() );
			if (rslt == 0)
				rslt = so1.getOrderID() - so2.getOrderID();
			return rslt;
		}
	};
	
	private final Comparator ORDER_DATE_CMPTOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			LiteWorkOrderBase so1 = (LiteWorkOrderBase) o1;
			LiteWorkOrderBase so2 = (LiteWorkOrderBase) o2;
			int rslt = new Date(getRelevantDate(so1)).compareTo( new Date(getRelevantDate(so2)) );
			if (rslt == 0)
				rslt = so1.getOrderID() - so2.getOrderID();
			return rslt;
		}
	};
	
	private int sortBy = CtiUtilities.NONE_ID;
	private int sortOrder = SORT_ORDER_ASCENDING;
	private int filterBy = CtiUtilities.NONE_ID;
	private int serviceStatus = CtiUtilities.NONE_ID;
	private int serviceType = CtiUtilities.NONE_ID;
	private int serviceCompany = CtiUtilities.NONE_ID;
	private int page = 1;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int energyCompanyID = 0;
	private int htmlStyle = HTML_STYLE_WORK_ORDERS;
	private String referer = null;
	private ArrayList searchResults = null;
	
	private LiteStarsEnergyCompany energyCompany = null;
	private ArrayList workOrderList = null;
	
	public WorkOrderBean() {
	}
	
	private long getRelevantDate(LiteWorkOrderBase liteOrder) {
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
	}

	/**
	 * @return
	 */
	public LiteStarsEnergyCompany getEnergyCompany() {
		if (energyCompany == null)
			energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		return energyCompany;
	}

	/**
	 * @return
	 */
	public ArrayList getWorkOrderList() {
		//if (workOrderList != null) return workOrderList;
		
		ArrayList workOrders = null;
		if (getHtmlStyle() == HTML_STYLE_SEARCH_RESULTS)
			workOrders = searchResults;
		else
			workOrders = getEnergyCompany().loadWorkOrders();
		
		java.util.TreeSet sortedOrders = null;
		if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_ORDER_NO)
			sortedOrders = new java.util.TreeSet( ORDER_NO_CMPTOR );
		else if (getSortBy() == YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_DATE_TIME)
			sortedOrders = new java.util.TreeSet( ORDER_DATE_CMPTOR );
		else
			sortedOrders = new java.util.TreeSet( ORDER_ID_CMPTOR );
		
		ArrayList workOrders2 = workOrders;
		if (getServiceStatus() > 0) {
			workOrders2 = new ArrayList();
			for (int i = 0; i < workOrders.size(); i++) {
				LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) workOrders.get(i);
				if (liteOrder.getCurrentStateID() == getServiceStatus())
					workOrders2.add( liteOrder );
			}
		}
		
		if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_TYPE) {
			for (int i = 0; i < workOrders2.size(); i++) {
				LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) workOrders2.get(i);
				if (liteOrder.getWorkTypeID() == getServiceType())
					sortedOrders.add( liteOrder );
			}
		}
		else if (getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMPANY) {
			for (int i = 0; i < workOrders2.size(); i++) {
				LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) workOrders2.get(i);
				if (liteOrder.getServiceCompanyID() == getServiceCompany())
					sortedOrders.add( liteOrder );
			}
		}
		else {
			for (int i = 0; i < workOrders2.size(); i++)
				sortedOrders.add( workOrders2.get(i) );
		}
		
		workOrderList = new ArrayList();
		java.util.Iterator it = sortedOrders.iterator();
		while (it.hasNext()) {
			if (getSortOrder() == SORT_ORDER_ASCENDING)
				workOrderList.add( it.next() );
			else
				workOrderList.add( 0, it.next() );
		}
		
		return workOrderList;
	}
	
	public String getHTML(HttpServletRequest req) {
		ArrayList soList = getWorkOrderList();
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
		
		StringBuffer htmlBuf = new StringBuffer();
		htmlBuf.append("<table width='95%' border='0' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td>").append(LINE_SEPARATOR);
		htmlBuf.append("      <table width='100%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>").append(LINE_SEPARATOR);
		htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td>").append(navBuf).append("</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td align='right'>Page(1-").append(maxPageNo).append("): ")
				.append("<input type='text' id='GoPage' style='border:1px solid #666699; font:11px' size='").append(maxPageDigit).append("' value='").append(page).append("'> ")
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
		htmlBuf.append("          <td  class='HeaderCell' width='13%' >Order #</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td  class='HeaderCell' width='13%' >Date/Time</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td  class='HeaderCell' width='10%' >Type</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td  class='HeaderCell' width='10%' >Status</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td  class='HeaderCell' width='8%' >By Who</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td  class='HeaderCell' width='12%' >Assigned</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td  class='HeaderCell' width='34%' >Description</td>").append(LINE_SEPARATOR);
		htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
		
		for (int i = minOrderNo; i <= maxOrderNo; i++) {
			LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) soList.get(i-1);
			StarsServiceRequest starsOrder = StarsLiteFactory.createStarsServiceRequest(liteOrder, getEnergyCompany());
			Date date = ServerUtils.translateDate( getRelevantDate(liteOrder) );
			String dateStr = (date != null)? ServerUtils.formatDate(date, energyCompany.getDefaultTimeZone()) : "----";
			
			htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='13%' >")
					.append("<a href='WorkOrder.jsp?OrderId=").append(liteOrder.getOrderID()).append("' class='Link1'>")
					.append(starsOrder.getOrderNumber()).append("</a></td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='13%' >").append(dateStr).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='10%' >").append(ServletUtils.forceNotEmpty( starsOrder.getServiceType().getContent() )).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='10%' >").append(starsOrder.getCurrentState().getContent()).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='8%' >").append(ServletUtils.forceNotEmpty( starsOrder.getOrderedBy() )).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='12%' >").append(ServletUtils.forceNotEmpty( starsOrder.getServiceCompany().getContent() )).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='34%'>").append(LINE_SEPARATOR);
			htmlBuf.append("            <textarea name='textarea' rows='2' wrap='soft' cols='35' class='TableCell' readonly>")
					.append(starsOrder.getDescription().replaceAll("<br>", LINE_SEPARATOR)).append("</textarea>").append(LINE_SEPARATOR);
			htmlBuf.append("          </td>").append(LINE_SEPARATOR);
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
			if (referer != null)
				htmlBuf.append("      <input type='button' name='Back' value='Back' onclick='location.href=\"").append(referer).append("\"'>").append(LINE_SEPARATOR);
			else
				htmlBuf.append("      <input type='button' name='Back' value='Back' onclick='history.back()'>").append(LINE_SEPARATOR);
			htmlBuf.append("    </td>").append(LINE_SEPARATOR);
			htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
			htmlBuf.append("</table>").append(LINE_SEPARATOR);
		}
		
		return htmlBuf.toString();
	}

	/**
	 * @return
	 */
	public int getFilterBy() {
		return filterBy;
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
	public int getServiceCompany() {
		return serviceCompany;
	}

	/**
	 * @return
	 */
	public int getServiceStatus() {
		return serviceStatus;
	}

	/**
	 * @return
	 */
	public int getServiceType() {
		return serviceType;
	}

	/**
	 * @return
	 */
	public int getSortBy() {
		return sortBy;
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
	public void setFilterBy(int i) {
		filterBy = i;
		// Search result should be updated
		workOrderList = null;
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
	public void setServiceCompany(int i) {
		serviceCompany = i;
	}

	/**
	 * @param i
	 */
	public void setServiceStatus(int i) {
		serviceStatus = i;
	}

	/**
	 * @param i
	 */
	public void setServiceType(int i) {
		serviceType = i;
	}

	/**
	 * @param i
	 */
	public void setSortBy(int i) {
		sortBy = i;
	}

	/**
	 * @param i
	 */
	public void setSortOrder(int i) {
		sortOrder = i;
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
		searchResults = list;
	}

}
