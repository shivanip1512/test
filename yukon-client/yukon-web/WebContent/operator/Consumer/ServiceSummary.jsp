<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteWorkOrderBase" %>
<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%@page import="com.cannontech.database.data.stars.event.EventWorkOrder"%>
<%@page import="com.cannontech.stars.dr.event.dao.EventWorkOrderDao"%>
<%@page import="com.cannontech.spring.YukonSpringHook"%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
var changed = false;

function setChanged(idx) {
	document.getElementsByName("Changed")[idx].value = 'true';
	changed = true;
}

function setDeleted(idx, deleted) {
	document.getElementsByName("Deleted")[idx].value = deleted;
	changed = true;
}

function checkOrderNo(form) {
	if (!changed) return false;
	for (i = 0; i < form.Changed.length; i++) {
		if (form.Deleted[i].value == 'false' && form.Changed[i].value == 'true' && form.OrderNo[i].value == '') {
			alert("Order # cannot be empty");
			return false;
		}
	}
	return true;
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jspf" %>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101">
		  <% String pageName = "ServiceSummary.jsp"; %>
          <%@ include file="include/Nav.jspf" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
              
            <div align="center"> 
              <% String header = DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_SERVICE_HISTORY); %>
              <%@ include file="include/InfoSearchBar.jspf" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <span class="MainText">Click on an Order # to view the complete 
              order history.</span> 
            </div>
            <br>
			<table width="95%" border="1" cellspacing="0" cellpadding="1" align="center">
              <tr>
                <td width="12%" class="HeaderCell" nowrap>Order # </td>
                <td width="12%" class="HeaderCell">Date/Time</td>
                <td width="12%" class="HeaderCell">Type</td>
                <td width="12%" class="HeaderCell">State</td>
                <td width="12%" class="HeaderCell" nowrap>Ordered By</td>
                <td width="12%" class="HeaderCell">Assigned</td>
                 <td width="28%" class="HeaderCell">Desription</td>
              </tr>
              <%
    EventWorkOrderDao eventWorkOrderDao = YukonSpringHook.getBean("eventWorkOrderDao", EventWorkOrderDao.class);
	for (int i = 0; i < serviceHist.getStarsServiceRequestCount(); i++) {
		StarsServiceRequest order = serviceHist.getStarsServiceRequest(i);
		LiteWorkOrderBase liteOrder = liteEC.getWorkOrderBase(order.getOrderID(), true);
        List<EventWorkOrder> eventWorkOrderList = eventWorkOrderDao.getByWorkOrderId(liteOrder.getOrderID());
		String companyName = "";
		for (int j = 0; j < companies.getStarsServiceCompanyCount(); j++) {
			if (companies.getStarsServiceCompany(j).getCompanyID() == liteOrder.getServiceCompanyID()) {
				companyName = companies.getStarsServiceCompany(j).getCompanyName();
				break;
			}
		}
%>
              <tr valign="middle"> 
                <td width="12%" class="TableCell"><a href="SOHistory.jsp?OrderNo=<%= i %>" class="Link1"><%= liteOrder.getOrderNumber() %></a></td>
                <td width="12%" class="TableCell"><cti:formatDate value="<%=eventWorkOrderList.get(0).getEventBase().getEventTimestamp()%>" type="BOTH"/></td>
                <td width="12%" class="TableCell"><%= ServletUtils.forceNotEmpty(DaoFactory.getYukonListDao().getYukonListEntry(liteOrder.getWorkTypeID()).getEntryText()) %></td>
                <td width="12%" class="TableCell"><%= ServletUtils.forceNotEmpty(DaoFactory.getYukonListDao().getYukonListEntry(liteOrder.getCurrentStateID()).getEntryText()) %></td>
                <td width="12%" class="TableCell"><%= ServletUtils.forceNotEmpty(liteOrder.getOrderedBy()) %></td>
                <td width="12%" class="TableCell"><%= ServletUtils.forceNotEmpty(companyName) %></td>
                <td width="28%"> 
                  <textarea name="Description" rows="2" wrap="soft" cols="35" class="TableCell" readonly><%= liteOrder.getDescription().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                </td>
              </tr>
              <%
	}
%>
            </table>
            <p>&nbsp;</p>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
