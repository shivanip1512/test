<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%
	StarsSubstation substation = null;
	int subIdx = Integer.parseInt( request.getParameter("Sub") );
	if (subIdx >= 0 && subIdx < substations.getStarsSubstationCount()) {
		substation = substations.getStarsSubstation(subIdx);
	}
	else {
		substation = new StarsSubstation();
		substation.setSubstationID(-1);
		substation.setSubstationName("");
		substation.setRouteID(0);
	}
	
	String viewOnly = substation.getInherited()? "disabled" : "";
%>
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
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - SUBSTATION</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin" onsubmit="return <%= !substation.getInherited() %>">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Edit Substation Information</td>
                </tr>
                <tr> 
                  <td height="67"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
                      <input type="hidden" name="action" value="UpdateSubstation">
                      <input type="hidden" name="SubID" value="<%= substation.getSubstationID() %>">
                      <tr> 
                        <td width="30%" align="right" class="TableCell">Substation 
                          Name:</td>
                        <td width="70%" class="TableCell"> 
                          <input type="text" name="SubName" value="<%= substation.getSubstationName() %>" onchange="setContentChanged(true)" size="40">
                        </td>
                      </tr>
                      <tr> 
                        <td width="30%" align="right" class="TableCell">Route:</td>
                        <td width="70%" class="TableCell"> 
                          <select name="Route" onchange="setContentChanged(true)">
                            <option value="0">(none)</option>
<%
	LiteYukonPAObject[] routes = liteEC.getAllRoutes();
	for (int i = 0; i < routes.length; i++) {
		String selected = (routes[i].getYukonID() == substation.getRouteID())? "selected" : "";
%>
                            <option value="<%= routes[i].getYukonID() %>" <%= selected %>><%= routes[i].getPaoName() %></option>
<%
	}
%>
                          </select>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <br>
              <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr>
                  <td width="290" align="right"> 
                    <input type="submit" name="Submit" value="Submit" <%= viewOnly %>>
                  </td>
                  <td width="205"> 
                    <input type="reset" name="Reset" value="Reset" <%= viewOnly %> onclick="setContentChanged(false)">
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='AdminTest.jsp'">
                  </td>
                </tr>
              </table>
            </form>
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
