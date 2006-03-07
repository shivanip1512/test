<%@ page import="java.util.Hashtable" %>
<%
	// Table of [link, label, page name(optional)]
	String linkTable[][] = {{"CreateOrder.jsp", "New Order"},
						  {"Report.jsp", "Report"},
						  {"WOFilter.jsp", "Edit Filters"},
						  {"WorkOrder.jsp", "View Work Orders"}
						 };

	String bulletImg = "<img src='../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED) + "' width='9' height='9'>";
	
	// List of String[] (link image, link html)
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkTable.length; i++) {
		String linkImg = null;
		String linkHtml = null;
		String linkPageName = (linkTable[i].length == 3)? linkTable[i][2] : linkTable[i][0];
		if (linkPageName.equalsIgnoreCase(pageName)) {
			linkImg = bulletImg;
			linkHtml = "<span class='Nav'>" + linkTable[i][1] + "</span>";
		}
		else {
			linkImg = "";
			linkHtml = "<a href='" + linkTable[i][0] + "' class='Link2' onclick='return warnUnsavedChanges();'><span class='NavText'>" + linkTable[i][1] + "</span></a>";
		}
		links.put(linkPageName, new String[] {linkImg, linkHtml});
	}
%>

<cti:checkMultiProperty propertyid="<%= Integer.toString(WorkOrderRole.WORK_ORDER_SHOW_ALL) + ',' + Integer.toString(WorkOrderRole.WORK_ORDER_CREATE_NEW) %>">
<table width="101" border="0" cellspacing="0" cellpadding="5">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Work Orders</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
		  <tr> 
            <td width="10"><%= ((String[]) links.get("WOFilter.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("WOFilter.jsp"))[1] %></td>
          </tr>
		  <cti:checkProperty propertyid="<%= WorkOrderRole.WORK_ORDER_SHOW_ALL %>">
          <tr> 
            <% if(((java.util.ArrayList)session.getAttribute(ServletUtil.FILTER_WORKORDER_LIST)) == null || ((java.util.ArrayList)session.getAttribute(ServletUtil.FILTER_WORKORDER_LIST)).size() < 1) { %>
            <% } else {%>
            	<td width="10"><%= ((String[]) links.get("WorkOrder.jsp"))[0] %></td>
	            <td style="padding:1"><%= ((String[]) links.get("WorkOrder.jsp"))[1] %></td>
	        <% } %>
          </tr>
		  </cti:checkProperty>        
<cti:checkProperty propertyid="<%= WorkOrderRole.WORK_ORDER_CREATE_NEW %>">
          <tr> 
            <td width="10"><%= ((String[]) links.get("CreateOrder.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("CreateOrder.jsp"))[1] %></td>
          </tr>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= WorkOrderRole.WORK_ORDER_REPORT %>">
          <tr> 
            <td width="10"><%= ((String[]) links.get("Report.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Report.jsp"))[1] %></td>
          </tr>
</cti:checkProperty>
        </table>
      </div>
    </td>
  </tr>
</table>
</cti:checkMultiProperty>
