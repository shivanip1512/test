<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.WorkOrderBean" %>
<%@ page import="com.cannontech.stars.web.servlet.WorkOrderManager" %>

<jsp:useBean id="searchRsltBean" class="com.cannontech.stars.web.WorkOrderBean" scope="session">
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
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="WorkImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" height="28" class="PageHeader">&nbsp;&nbsp;&nbsp;Hardware 
                  Inventory </td>
                <td width="235" height = "30" valign="middle">&nbsp;</td>
                <form method="post" action="../Operations.jsp">
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
                </form>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
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
