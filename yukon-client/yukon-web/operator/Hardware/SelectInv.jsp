<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.bean.InventoryBean" %>
<%
	if (request.getParameter("action") != null) {
		String redirect = request.getParameter(ServletUtils.ATT_REDIRECT);
		if (redirect == null) redirect = request.getHeader("referer");
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		
		String referer = request.getParameter(ServletUtils.ATT_REFERRER);
		if (referer == null) referer = request.getHeader("referer");
		session.setAttribute(ServletUtils.ATT_REFERRER2, referer);
	}
	
	String referer = (String) session.getAttribute(ServletUtils.ATT_REFERRER2);
%>

<jsp:useBean id="selectHwBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session">
	<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="selectHwBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>
	<jsp:setProperty name="selectHwBean" property="action" value="SelectLMHardware"/>
	<jsp:setProperty name="selectHwBean" property="filterBy" value="0"/>
</jsp:useBean>

<% if (request.getParameter("page") == null) { %>
	<%-- intialize bean properties --%>
	<jsp:setProperty name="selectHwBean" property="htmlStyle" value="<%= InventoryBean.HTML_STYLE_SELECT_LM_HARDWARE %>"/>
	<jsp:setProperty name="selectHwBean" property="sortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO %>"/>
	<jsp:setProperty name="selectHwBean" property="page" value="1"/>
	<jsp:setProperty name="selectHwBean" property="referer" value="<%= referer %>"/>
<% } %>
<% if (request.getParameter("SearchBy") == null) { %>
	<jsp:setProperty name="selectHwBean" property="searchBy" value="0"/>
	<jsp:setProperty name="selectHwBean" property="searchValue" value=""/>
<% } %>

<%-- Grab the bean properties --%>
<jsp:setProperty name="selectHwBean" property="htmlStyle" param="HtmlStyle"/>
<jsp:setProperty name="selectHwBean" property="sortBy" param="SortBy"/>
<jsp:setProperty name="selectHwBean" property="page" param="page"/>
<jsp:setProperty name="selectHwBean" property="searchBy" param="SearchBy"/>
<jsp:setProperty name="selectHwBean" property="searchValue" param="SearchValue"/>

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
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "SELECT INVENTORY"; %>
              <%@ include file="include/SearchBar2.jsp" %>
              <br>
              <form name="MForm" method="post" action="">
                <input type="hidden" name="HtmlStyle" value="<%= InventoryBean.HTML_STYLE_SELECT_LM_HARDWARE %>">
                <input type="hidden" name="SortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO %>">
                <input type="hidden" name="page" value="1">
                <table width="80%" border="0" cellspacing="0" cellpadding="1">
                  <tr> 
                    <td class="MainText" align="center">Check the radio button 
                      of the hardware you want to select, then click Select.</td>
                  </tr>
                  <tr> 
                    <td class="MainText" align="center"> 
                      <input type="submit" name="ShowAll" value="Show All"
                      <% if ((selectHwBean.getHtmlStyle() & InventoryBean.HTML_STYLE_INVENTORY_SET) == 0) { %>disabled<% } %>>
                    </td>
                  </tr>
                </table>
              </form>
              <%= selectHwBean.getHTML(request) %>
			<p>&nbsp;</p></div>
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
