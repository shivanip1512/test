<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.bean.WorkOrderBean" %>
<%@ page import="com.cannontech.stars.web.util.WorkOrderManagerUtil" %>

<jsp:useBean id="workOrderBean" class="com.cannontech.stars.web.bean.WorkOrderBean" scope="session">
	<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="workOrderBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>
	<jsp:setProperty name="workOrderBean" property="sortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_ORDER_NO %>"/>
	<jsp:setProperty name="workOrderBean" property="sortOrder" value="<%= WorkOrderBean.SORT_ORDER_DESCENDING %>"/>
	<jsp:setProperty name="workOrderBean" property="htmlStyle" value="<%= WorkOrderBean.HTML_STYLE_SEARCH_RESULTS %>"/>
</jsp:useBean>
	
<% if (request.getParameter("page") == null) { %>
	<%-- intialize bean properties --%>
	<jsp:setProperty name="workOrderBean" property="page" value="1"/>
	<jsp:setProperty name="workOrderBean" property="referer" value="<%= session.getAttribute(ServletUtils.ATT_REFERRER) %>"/>
<% } %>

<%-- Grab the search criteria --%>
<jsp:setProperty name="workOrderBean" property="page" param="page"/>


<%@page import="com.cannontech.stars.database.data.lite.LiteWorkOrderBase"%><html>
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "SEARCH RESULTS"; %>
              <%@ include file="include/SearchBar.jspf" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
                        <form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/WorkOrderManager">
                            <input type="hidden" name="page" value="1">
                            <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
                            <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
                            <input type="hidden" name="showAll" value="0">
                            <input type="hidden" name="action" value=""> 

<%
	if (errorMsg == null) {
		String resultDesc = (String) session.getAttribute(WorkOrderManagerUtil.WORK_ORDER_SET_DESC);
%>
              <span class="MainText"><%= resultDesc %></span><br>
              <br>
<%
		@SuppressWarnings("unchecked") List<LiteWorkOrderBase> searchResults =
		    (List<LiteWorkOrderBase>) session.getAttribute(WorkOrderManagerUtil.WORK_ORDER_SET);
		if (searchResults != null) {
			workOrderBean.setSearchResults(searchResults);
%>
              <%= workOrderBean.getHTML(request) %> 
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
              </form>
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
