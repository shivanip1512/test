/*
 * Created on Nov 6, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.bean;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.web.servlet.SOAPServer;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeviceBean {
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static final int DEFAULT_PAGE_SIZE = 20;
	
	private int categoryID = 0;
	private int page = 1;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int energyCompanyID = 0;
	private String referer = null;
	private List deviceList = null;
	
	private LiteStarsEnergyCompany energyCompany = null;

	public DeviceBean() {
	}
	
	private LiteStarsEnergyCompany getEnergyCompany() {
		if (energyCompany == null)
			energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		return energyCompany;
	}
	
	public String getHTML(HttpServletRequest req) {
		if (deviceList == null || deviceList.size() == 0) {
			StringBuffer htmlBuf = new StringBuffer();
			htmlBuf.append("<p class='MainText'>No device found.</p>").append(LINE_SEPARATOR);
			
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
			
			return htmlBuf.toString();
		}
		
		String uri = req.getRequestURI();
		String pageName = uri.substring( uri.lastIndexOf('/') + 1 );
		
		if (page < 1) page = 1;
		int maxPageNo = (int) Math.ceil(deviceList.size() * 1.0 / pageSize);
		if (page > maxPageNo) page = maxPageNo;
		
		int minInvNo = (page - 1) * pageSize + 1;
		int maxInvNo = Math.min(page * pageSize, deviceList.size());
        
		StringBuffer navBuf = new StringBuffer();
		navBuf.append(minInvNo);
		if (maxInvNo > minInvNo)
			navBuf.append("-").append(maxInvNo);
		navBuf.append(" of ").append(deviceList.size());
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
		htmlBuf.append("<form name='DeviceBeanForm' method='post' action='").append(req.getContextPath()).append("/servlet/InventoryManager'>").append(LINE_SEPARATOR);
		htmlBuf.append("<input type='hidden' name='action' value='SelectDevice'>").append(LINE_SEPARATOR);
		htmlBuf.append("<input type='hidden' name='CategoryID' value='").append(categoryID).append("'>").append(LINE_SEPARATOR);
		
		htmlBuf.append("<table width='50%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td>").append(navBuf).append("</td>").append(LINE_SEPARATOR);
		htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
		htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td>").append(LINE_SEPARATOR);
		htmlBuf.append("      <table width='100%' border='1' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
		htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td class='HeaderCell' width='10%'>&nbsp;</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td class='HeaderCell' width='50%'>Device Name</td>").append(LINE_SEPARATOR);
		htmlBuf.append("          <td class='HeaderCell' width='40%'>Device Type</td>").append(LINE_SEPARATOR);
		htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
        
		for (int i = minInvNo; i <= maxInvNo; i++) {
			LiteYukonPAObject litePao = (LiteYukonPAObject) deviceList.get(i-1);
        	
			htmlBuf.append("        <tr>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='10%'>");
			htmlBuf.append("<input type='radio' name='DeviceID' value='").append(litePao.getYukonID()).append("'>");
			htmlBuf.append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='50%'>").append( PAOFuncs.getYukonPAOName(litePao.getYukonID()) ).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("          <td class='TableCell' width='40%'>").append( PAOGroups.getPAOTypeString(litePao.getType()) ).append("</td>").append(LINE_SEPARATOR);
			htmlBuf.append("        </tr>").append(LINE_SEPARATOR);
		}
        
		htmlBuf.append("      </table>").append(LINE_SEPARATOR);
		htmlBuf.append("    </td>").append(LINE_SEPARATOR);
		htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
		htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td>").append(navBuf).append("</td>").append(LINE_SEPARATOR);
		htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
		htmlBuf.append("</table>").append(LINE_SEPARATOR);
        
		htmlBuf.append("<br>").append(LINE_SEPARATOR);
		htmlBuf.append("<table width='200' border='0' cellspacing='0' cellpadding='3'>").append(LINE_SEPARATOR);
		htmlBuf.append("  <tr>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td align='right'>").append(LINE_SEPARATOR);
		htmlBuf.append("      <input type='submit' name='Submit' value='Select'>").append(LINE_SEPARATOR);
		htmlBuf.append("    </td>").append(LINE_SEPARATOR);
		htmlBuf.append("    <td>").append(LINE_SEPARATOR);
		if (referer != null)
			htmlBuf.append("      <input type='button' name='Cancel' value='Cancel' onclick='location.href=\"").append(referer).append("\"'>").append(LINE_SEPARATOR);
		else
			htmlBuf.append("      <input type='button' name='Cancel' value='Cancel' onclick='history.back()'>").append(LINE_SEPARATOR);
		htmlBuf.append("    </td>").append(LINE_SEPARATOR);
		htmlBuf.append("  </tr>").append(LINE_SEPARATOR);
		htmlBuf.append("</table>").append(LINE_SEPARATOR);
		htmlBuf.append("</form>").append(LINE_SEPARATOR);
        
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
	 * @param string
	 */
	public void setReferer(String string) {
		referer = string;
	}

	/**
	 * @return
	 */
	public int getCategoryID() {
		return categoryID;
	}

	/**
	 * @param i
	 */
	public void setCategoryID(int i) {
		categoryID = i;
	}

	/**
	 * @return
	 */
	public List getDeviceList() {
		return deviceList;
	}

	/**
	 * @param list
	 */
	public void setDeviceList(List list) {
		deviceList = list;
	}

}
