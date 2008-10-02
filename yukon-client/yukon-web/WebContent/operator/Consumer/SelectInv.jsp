<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.bean.InventoryBean" %>
<%@ page import="com.cannontech.web.navigation.CtiNavObject" %>
<%
	boolean inWizard = ((String) session.getAttribute(ServletUtils.ATT_REFERRER)).indexOf("Wizard=true") >= 0;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	if (request.getParameter("SerialNo") != null) {
		session.setAttribute(ServletUtils.ATT_REFERRER2, ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage());
		session.setAttribute(ServletUtils.ATT_REDIRECT, request.getParameter(ServletUtils.ATT_REDIRECT));
	}
	String referer = (String) session.getAttribute(ServletUtils.ATT_REFERRER2);
%>

<jsp:useBean id="inventoryBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session">
	<%-- this body is executed only if the bean is created --%>
</jsp:useBean>

<% if (request.getParameter("page") == null) inventoryBean.resetInventoryList(); %>

<jsp:setProperty name="inventoryBean" property="energyCompanyID" value="<%= liteEC.getEnergyCompanyID() %>"/>
<jsp:setProperty name="inventoryBean" property="action" value="InsertInventory"/>
<% if (request.getParameter("page") == null) { %>
	<%-- intialize bean properties --%>
	<jsp:setProperty name="inventoryBean" property="sortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO %>"/>
	<jsp:setProperty name="inventoryBean" property="htmlStyle" value="<%= InventoryBean.HTML_STYLE_SELECT_INVENTORY %>"/>
	<jsp:setProperty name="inventoryBean" property="page" value="1"/>
	<jsp:setProperty name="inventoryBean" property="referer" value="<%= referer %>"/>
<% } %>
<% if (request.getParameter("SearchBy") == null) { %>
	<jsp:setProperty name="inventoryBean" property="searchBy" value="0"/>
	<jsp:setProperty name="inventoryBean" property="searchValue" value=""/>
<% } %>

<%-- Grab the search criteria --%>
<jsp:setProperty name="inventoryBean" property="htmlStyle" param="HtmlStyle"/>
<jsp:setProperty name="inventoryBean" property="sortBy" param="SortBy"/>
<jsp:setProperty name="inventoryBean" property="page" param="page"/>
<jsp:setProperty name="inventoryBean" property="searchBy" param="SearchBy"/>
<jsp:setProperty name="inventoryBean" property="searchValue" param="SearchValue"/>
<c:set target="${inventoryBean}" property="filterByList" value="${sessionScope.InventoryFilters}" />
 
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function showAll(form) {
	form.submit();
}

function showWarehouse(form) {
	form.submit();
}

function goFilter(form) 
{
	form.action.value = "";
	form.REDIRECT.value = "<%= request.getContextPath() %>/operator/Consumer/SelectInvFilter.jsp";
	form.submit();
}

function goCheckAgain(form) 
{
    form.action.value = "";
    form.REDIRECT.value = "<%= request.getContextPath() %>/operator/Consumer/SerialNumber.jsp?action=New";
    form.submit();
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "SELECT INVENTORY"; %>
              <%@ include file="../Hardware/include/SearchBar2.jspf" %>

			  <form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager">
				<input type="hidden" name="HtmlStyle" value="<%= InventoryBean.HTML_STYLE_SELECT_INVENTORY %>">
			    <input type="hidden" name="SortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO %>">
				<input type="hidden" name="page" value="1">
				<input type="hidden" name="REDIRECT" value="">
                <c:choose>
                    <c:when test="${inventoryBean.checkInvenForAccount}">
                        <table width="80%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td> 
                                    <div align="left">
                                        <input type="button" name="Return" value="Return to Check Inventory" onClick="goCheckAgain(this.form)">
                                    </div>
                                    <br>
                                </td>
                            </tr>
                        </table>
                    </c:when> 
                    <c:otherwise>
                        <table width="80%" border="1" cellspacing="0" cellpadding="3" class="TableCell">
                        <tr> 
                            <td class="HeaderCell" width="100%">Current Filters</td>
                        </tr>
                        <c:forEach var="filterEntry" items="${inventoryBean.filterByList}">
                            <tr>        
                                <td>
                                    <div align="left"><c:out value="${filterEntry.filterText}"/></div>
                                <td>
                            </tr>
                        </c:forEach>
                        </table>
                        <table width="80%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td> 
                                    <div align="left">
                                        <input type="button" name="Filter" value="Add/Remove Filters" onClick="goFilter(this.form)">
                                        <!-- <input type="button" name="ShowAll" value="Show All Inventory (Filter Free)" onClick="showAll(this.form)"> -->
                                    </div>
                                    <br>
                                </td>
                            </tr>
                        </table>
                    </c:otherwise> 
                </c:choose>
                
                <table width="80%" border="0" cellspacing="0" cellpadding="1">
                  <tr> 
                    <td class="MainText" align="center">Check the radio button 
                      of the hardware you want to select, then click Select.</td>
                  </tr>
                </table>
              </form>
              <%= inventoryBean.getHTML(request) %> 
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
