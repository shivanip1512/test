<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.bean.DeviceBean" %>
<%
	String action = request.getParameter("action");
	if (action != null && action.equalsIgnoreCase("CreateMCT")) {
		// Submitted from CreateMCT.jsp
		String ref = request.getHeader("referer");
		session.setAttribute(ServletUtils.ATT_REFERRER2, ref);
		if (ref.indexOf("Selected") < 0) {
			if (ref.indexOf("?") < 0)
				ref += "?Selected";
			else
				ref += "&Selected";
		}
		session.setAttribute(ServletUtils.ATT_REDIRECT, ref);
	}
	
	String referer = (String) session.getAttribute(ServletUtils.ATT_REFERRER2);
	int mctCatID = StarsDatabaseCache.getInstance().getEnergyCompany(user.getEnergyCompanyID()).getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT).getEntryID();
%>

<jsp:useBean id="mctBean2" class="com.cannontech.stars.web.bean.DeviceBean" scope="session">
	<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="mctBean2" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>
	<jsp:setProperty name="mctBean2" property="categoryID" value="<%= mctCatID %>"/>
	<jsp:setProperty name="mctBean2" property="action" value="SelectDevice"/>
</jsp:useBean>

<% if (request.getParameter("page") == null) { %>
	<%-- intialize bean properties --%>
	<jsp:setProperty name="mctBean2" property="filter" value="<%= DeviceBean.DEV_FILTER_NOT_IN_INVENTORY %>"/>
	<jsp:setProperty name="mctBean2" property="page" value="1"/>
	<jsp:setProperty name="mctBean2" property="referer" value="<%= referer %>"/>
<% } %>
<% if (request.getParameter("SearchDeviceName") == null || request.getParameter("SearchDeviceName").equals("")) { %>
	<jsp:setProperty name="mctBean2" property="deviceName" value=""/>
<% } %>

<%-- Grab the bean properties --%>
<jsp:setProperty name="mctBean2" property="filter" param="Filter"/>
<jsp:setProperty name="mctBean2" property="deviceName" param="SearchDeviceName"/>
<jsp:setProperty name="mctBean2" property="page" param="page"/>

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
              <% String header = "SELECT METER"; %>
              <%@ include file="include/SearchBar.jsp" %>
              <form name="MForm" method="post" action="SelectMCT.jsp">
                <input type="hidden" name="Filter" value="<%= mctBean2.getFilter() %>">
                <input type="hidden" name="page" value="1">
                <table width="80%" border="0" cellspacing="0" cellpadding="1">
                  <tr> 
                    <td class="MainText" align="center">Device Name: 
                      <input type="text" name="SearchDeviceName" value="<%= StarsUtils.forceNotNull(mctBean2.getDeviceName()) %>">
                      &nbsp;&nbsp;&nbsp;&nbsp; 
                      <input type="submit" name="Search" value="Search">
                    </td>
                  </tr>
                </table>
              </form>
              <%= mctBean2.getHTML(request) %>
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
