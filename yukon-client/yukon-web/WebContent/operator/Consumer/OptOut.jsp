<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	ArrayList enrolledHwIDs = new ArrayList();
	for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
		StarsAppliance app = appliances.getStarsAppliance(i);
		if (app.getProgramID() > 0 && app.getInventoryID() > 0) {
			Integer invID = new Integer(app.getInventoryID());
			if (!enrolledHwIDs.contains(invID))
				enrolledHwIDs.add(invID);
		}
	}
%>

<%@page import="com.cannontech.stars.dr.optout.dao.OptOutEventDao"%>
<%@page import="com.cannontech.stars.dr.optout.model.OptOutEventDto"%>
<%@page import="com.cannontech.stars.dr.optout.model.OptOutCountHolder"%>
<%@page import="com.cannontech.stars.dr.displayable.dao.DisplayableInventoryDao"%>
<%@page import="com.cannontech.stars.dr.displayable.model.DisplayableInventory"%>
<%@page import="com.cannontech.stars.dr.optout.dao.OptOutAdditionalDao"%>

<%@page import="com.cannontech.stars.dr.optout.dao.OptOutAdditionalDao"%><html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/yukon/styles/YukonGeneralStyles.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<!-- include calendar control stylesheet and .js file since this is NOT a standard page -->
<link rel="stylesheet" href="../../WebConfig/yukon/styles/calendarControl.css" type="text/css">

<script language="JavaScript" type="text/javascript"  src="/JavaScript/calendarControl.js"></script>

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
			<% String pageName = "OptOut.jsp"; %>
        	<%@ include file="include/Nav.jspf" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center" class="contentArea">
              <% String header = null; %>
              <%@ include file="include/InfoSearchBar.jspf" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
              
              <jsp:include page="/spring/stars/operator/optout" />
            
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
