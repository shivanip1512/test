<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.bean.DeviceBean" %>
<%@ page import="com.cannontech.stars.web.servlet.InventoryManager" %>
<%
	boolean inWizard = ((String) session.getAttribute(ServletUtils.ATT_REFERRER)).indexOf("Wizard=true") >= 0;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}

	if (request.getParameter("SerialNo") != null) {
		// Submitted from SerialNumber.jsp
		session.setAttribute(ServletUtils.ATT_REFERRER2, request.getHeader("referer"));
		session.setAttribute(ServletUtils.ATT_REDIRECT, request.getParameter(ServletUtils.ATT_REDIRECT));
	}
	String referer = (String) session.getAttribute(ServletUtils.ATT_REFERRER2);
	
	int mctCatID = SOAPServer.getEnergyCompany(user.getEnergyCompanyID()).getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT).getEntryID();
%>

<jsp:useBean id="mctBean" class="com.cannontech.stars.web.bean.DeviceBean" scope="session">
	<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="mctBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>
	<jsp:setProperty name="mctBean" property="categoryID" value="<%= mctCatID %>"/>
</jsp:useBean>

<% if (request.getParameter("page") == null) { %>
	<%-- intialize bean properties --%>
<% if (request.getParameter("DeviceName") == null) { %>
	<jsp:setProperty name="mctBean" property="filter" value="<%= DeviceBean.DEV_FILTER_NOT_ASSIGNED %>"/>
	<jsp:setProperty name="mctBean" property="deviceName" value=""/>
<% } else { %>
	<jsp:setProperty name="mctBean" property="filter" value="<%= DeviceBean.DEV_FILTER_DEVICE_NAME %>"/>
<% } %>
	<jsp:setProperty name="mctBean" property="page" value="1"/>
	<jsp:setProperty name="mctBean" property="referer" value="<%= referer %>"/>
<% } %>

<%-- Grab the bean properties --%>
<jsp:setProperty name="mctBean" property="filter" param="Filter"/>
<jsp:setProperty name="mctBean" property="deviceName" param="DeviceName"/>
<jsp:setProperty name="mctBean" property="page" param="page"/>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
function submitIt(filter) {
	document.MForm.Filter.value = filter;
	document.MForm.submit();
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jsp" %>
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "SELECT METER"; %>
<% if (!inWizard) { %>
              <%@ include file="include/InfoSearchBar.jsp" %>
<% } else { %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
<% } %>

			  <form name="MForm" method="post" action="SelectMCT.jsp">
			    <input type="hidden" name="Filter" value="<%= DeviceBean.DEV_FILTER_DEVICE_NAME %>">
				<input type="hidden" name="page" value="1">
                <table width="80%" border="0" cellspacing="0" cellpadding="1">
                  <tr> 
                    <td class="MainText" align="center">Device Name: 
                      <input type="text" name="DeviceName" value="<%= ServerUtils.forceNotNull(mctBean.getDeviceName()) %>">
                      &nbsp;&nbsp;&nbsp;&nbsp; 
                      <input type="submit" name="Search" value="Search">
                    </td>
                  </tr>
                  <tr> 
                    <td class="MainText" align="center"> 
                      <input type="button" name="ShowAll" value="Show All" onclick="submitIt(<%= DeviceBean.DEV_FILTER_ALL %>)"
					  <% if (mctBean.getFilter() == DeviceBean.DEV_FILTER_ALL) { %>disabled<% } %>>
                      <input type="button" name="ShowNotAssigned" value="Show Not Assigned" onclick="submitIt(<%= DeviceBean.DEV_FILTER_NOT_ASSIGNED %>)"
					  <% if (mctBean.getFilter() == DeviceBean.DEV_FILTER_NOT_ASSIGNED) { %>disabled<% } %>>
                    </td>
                  </tr>
                </table>
              </form>
              <%= mctBean.getHTML(request) %> 
              <p>&nbsp; </p>
            </div>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
