<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.bean.InventoryBean" %>
<%
	boolean inWizard = ((String) session.getAttribute(ServletUtils.ATT_REFERRER)).indexOf("Wizard=true") >= 0;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	if (request.getParameter("SerialNo") != null) {
		session.setAttribute(ServletUtils.ATT_REFERRER2, request.getHeader("referer"));
		session.setAttribute(ServletUtils.ATT_REDIRECT, request.getParameter(ServletUtils.ATT_REDIRECT));
	}
	String referer = (String) session.getAttribute(ServletUtils.ATT_REFERRER2);
%>

<jsp:useBean id="selectInvBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session">
	<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="selectInvBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>
	<jsp:setProperty name="selectInvBean" property="action" value="InsertInventory"/>
</jsp:useBean>

<% if (request.getParameter("page") == null) { %>
	<%-- intialize bean properties --%>
	<jsp:setProperty name="selectInvBean" property="sortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO %>"/>
	<jsp:setProperty name="selectInvBean" property="htmlStyle" value="<%= InventoryBean.HTML_STYLE_SELECT_INVENTORY %>"/>
	<jsp:setProperty name="selectInvBean" property="filterBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_LOCATION %>"/>
	<jsp:setProperty name="selectInvBean" property="location" value="<%= InventoryBean.INV_LOCATION_WAREHOUSE %>"/>
	<jsp:setProperty name="selectInvBean" property="page" value="1"/>
	<jsp:setProperty name="selectInvBean" property="referer" value="<%= referer %>"/>
<% } %>
<% if (request.getParameter("SearchBy") == null) { %>
	<jsp:setProperty name="selectInvBean" property="searchBy" value="0"/>
	<jsp:setProperty name="selectInvBean" property="searchValue" value=""/>
<% } %>

<%-- Grab the search criteria --%>
<jsp:setProperty name="selectInvBean" property="htmlStyle" param="HtmlStyle"/>
<jsp:setProperty name="selectInvBean" property="sortBy" param="SortBy"/>
<jsp:setProperty name="selectInvBean" property="filterBy" param="FilterBy"/>
<jsp:setProperty name="selectInvBean" property="location" param="Location"/>
<jsp:setProperty name="selectInvBean" property="page" param="page"/>
<jsp:setProperty name="selectInvBean" property="searchBy" param="SearchBy"/>
<jsp:setProperty name="selectInvBean" property="searchValue" param="SearchValue"/>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function showAll(form) {
	form.FilterBy.value = 0;
	form.Location.value = 0;
	form.submit();
}

function showWarehouse(form) {
	form.FilterBy.value = <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_LOCATION %>;
	form.Location.value = <%= InventoryBean.INV_LOCATION_WAREHOUSE %>;
	form.submit();
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
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "SELECT INVENTORY"; %>
              <%@ include file="../Hardware/include/SearchBar2.jsp" %>

			  <form name="MForm" method="post" action="">
				<input type="hidden" name="HtmlStyle" value="<%= InventoryBean.HTML_STYLE_SELECT_INVENTORY %>">
			    <input type="hidden" name="SortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO %>">
			    <input type="hidden" name="FilterBy" value="<%= selectInvBean.getFilterBy() %>">
				<input type="hidden" name="Location" value="<%= selectInvBean.getLocation() %>">
				<input type="hidden" name="page" value="1">
                <table width="80%" border="0" cellspacing="0" cellpadding="1">
                  <tr> 
                    <td class="MainText" align="center">Check the radio button 
                      of the hardware you want to select, then click Select.</td>
                  </tr>
                  <tr> 
                    <td class="MainText" align="center"> 
                      <input type="button" name="ShowAll" value="Show All" onclick="showAll(this.form)"
                      <% if (selectInvBean.getFilterBy() == 0 && (selectInvBean.getHtmlStyle() & InventoryBean.HTML_STYLE_INVENTORY_SET) == 0) { %>disabled<% } %>>
                      <input type="button" name="ShowWarehouse" value="Show Warehouse" onclick="showWarehouse(this.form)"
                      <% if (selectInvBean.getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_LOCATION && selectInvBean.getLocation() == InventoryBean.INV_LOCATION_WAREHOUSE && (selectInvBean.getHtmlStyle() & InventoryBean.HTML_STYLE_INVENTORY_SET) == 0) { %>disabled<% } %>>
                    </td>
                  </tr>
                </table>
              </form>
              <%= selectInvBean.getHTML(request) %> 
              <p>&nbsp; </p>
            </div>
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
