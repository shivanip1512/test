<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.bean.InventoryBean" %>

<jsp:useBean id="resultSetBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session"/>
<jsp:setProperty name="resultSetBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>

<% if (request.getParameter("page") == null) { %>
	<%-- intialize bean properties --%>
	<jsp:setProperty name="resultSetBean" property="sortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO %>"/>
	<jsp:setProperty name="resultSetBean" property="htmlStyle" value="<%= InventoryBean.HTML_STYLE_INVENTORY_SET %>"/>
	<jsp:setProperty name="resultSetBean" property="page" value="1"/>
	<jsp:setProperty name="resultSetBean" property="referer" value="<%= session.getAttribute(ServletUtils.ATT_REFERRER) %>"/>
	<% resultSetBean.resetInventoryList(); %>
<% } %>

<%-- Grab the search criteria --%>
<jsp:setProperty name="resultSetBean" property="page" param="page"/>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
              <% String header = "OPERATION RESULT"; %>
              <%@ include file="include/SearchBar.jsp" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>

<%
	if (errorMsg == null) {
		String resultDesc = (String) session.getAttribute(InventoryManagerUtil.INVENTORY_SET_DESC);
%>
              <table width="80%" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td class="MainText"><%= resultDesc %></td>
                </tr>
              </table>
<%
		ArrayList inventorySet = (ArrayList) session.getAttribute(InventoryManagerUtil.INVENTORY_SET);
		if (inventorySet != null) {
			resultSetBean.setInventorySet(inventorySet);
%>
              <%= resultSetBean.getHTML(request) %> 
<%
		}
		else {
%>
              <table width="200" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td align='center'><input type="button" name="Back" value="Back" onclick="history.back()"></td>
                </tr>
              </table>
<%
		}
	}
%>
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
