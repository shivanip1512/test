<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.bean.WorkOrderBean" %>
<%@ page import="com.cannontech.stars.web.servlet.WorkOrderManager" %>

<jsp:useBean id="searchRsltBean" class="com.cannontech.stars.web.bean.WorkOrderBean" scope="session">
	<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="searchRsltBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>
	<jsp:setProperty name="searchRsltBean" property="sortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_ORDER_NO %>"/>
	<jsp:setProperty name="searchRsltBean" property="sortOrder" value="<%= WorkOrderBean.SORT_ORDER_DESCENDING %>"/>
	<jsp:setProperty name="searchRsltBean" property="htmlStyle" value="<%= WorkOrderBean.HTML_STYLE_SEARCH_RESULTS %>"/>
</jsp:useBean>
	
<% if (request.getParameter("page") == null) { %>
	<%-- intialize bean properties --%>
	<jsp:setProperty name="searchRsltBean" property="page" value="1"/>
	<jsp:setProperty name="searchRsltBean" property="referer" value="<%= session.getAttribute(ServletUtils.ATT_REFERRER) %>"/>
<% } %>

<%-- Grab the search criteria --%>
<jsp:setProperty name="searchRsltBean" property="page" param="page"/>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
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
              <% String header = "SEARCH RESULTS"; %>
              <%@ include file="include/SearchBar.jsp" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>

<%
	if (errorMsg == null) {
		String resultDesc = (String) session.getAttribute(WorkOrderManager.WORK_ORDER_SET_DESC);
%>
              <span class="MainText"><%= resultDesc %></span><br>
              <br>
<%
		ArrayList searchResults = (ArrayList) session.getAttribute(WorkOrderManager.WORK_ORDER_SET);
		if (searchResults != null) {
			searchRsltBean.setSearchResults(searchResults);
%>
              <%= searchRsltBean.getHTML(request) %> 
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
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
