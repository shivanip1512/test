<html>
  <head>
  <meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%
    int invID = 0;
    Integer[] invIDs = new Integer[1];
    
    boolean allTherm = request.getParameter("AllTherm") != null;
    String thermNoStr = "AllTherm";
    
    if (allTherm) {
        // Set multiple thermostats
        invIDs = (Integer[]) session.getAttribute(ServletUtils.ATT_THERMOSTAT_INVENTORY_IDS);
    }
    else {
        // Set a single thermostat
        int thermNo = Integer.parseInt(request.getParameter("InvNo"));
        StarsInventory thermostat = inventories.getStarsInventory(thermNo);
        invID = thermostat.getInventoryID();
        thermNoStr = "InvNo=" + thermNo;
        
        invIDs[0] = invID;
    }
    
    String thermostatIds = StringUtils.join(invIDs, ",");

%>
<c:set var="thermostatIds" value="<%=thermostatIds%>" />

    <title>Energy Services Operations Center</title>
      <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
      <link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
      <link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=YukonRoleProperty.STYLE_SHEET.getPropertyId()%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
              <div style="margin: 10px"> 
                <!-- Don't have to include the 'message' param in this url because it is already
                     on the request (message param is used by controller) -->
                <cti:url var="completeUrl" value="/stars/operator/thermostat/schedule/hints">
                    <cti:param name="thermostatIds" value="${thermostatIds}" />
                </cti:url>
                <jsp:include page="${completeUrl}"></jsp:include>
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
