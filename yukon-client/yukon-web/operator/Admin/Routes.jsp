<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%
	LiteYukonPAObject[] inheritedRoutes = null;
	if (liteEC.getParent() != null)
		inheritedRoutes = liteEC.getParent().getAllRoutes();
	LiteYukonPAObject[] routes = liteEC.getRoutes(inheritedRoutes);
	
	ArrayList assignedRoutes = new ArrayList();
	for (int i = 0; i < routes.length; i++)
		assignedRoutes.add(routes[i]);
	
	if (inheritedRoutes != null) {
		for (int i = 0; i < inheritedRoutes.length; i++)
			assignedRoutes.add(inheritedRoutes[i]);
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
var defaultRouteSelected = false;
var removeWarned = false;

function isRemovable(routeID) {
<%
	if (inheritedRoutes != null) {
		for (int i = 0; i < inheritedRoutes.length; i++) {
%>
	if (routeID == <%= inheritedRoutes[i].getYukonID() %>)
		return false;
<%
		}
	}
%>
	return true;
}

function selectionChanged(form) {
	form.RemoveButton.disabled = false;
	defaultRouteSelected = false;
	
	for (i = 0; i < form.RoutesAssigned.length; i++) {
		var oOption = form.RoutesAssigned.options[i];
		if (oOption.selected && oOption.value > 0) {
			if (!isRemovable(oOption.value)) {
				form.RemoveButton.disabled = true;
				return;
			}
			if (oOption.value == <%= liteEC.getDefaultRouteID() %>)
				defaultRouteSelected = true;
		}
	}
}

function addRoutes(form) {
	var insertIdx = 0;
	if (form.RoutesAssigned.length > 0 && form.RoutesAssigned.options[0].value == 0)
		insertIdx = 1;
	
	for (i = form.RoutesAvailable.length - 1; i >= 0; i--) {
		var oOption = form.RoutesAvailable.options[i];
		if (oOption.selected && oOption.value > 0) {
			form.RoutesAvailable.options.remove(i);
			form.RoutesAssigned.options.add(oOption, insertIdx);
			setContentChanged(true);
		}
	}
	
	if (form.RoutesAvailable.length == 0) {
		var emptyOption = document.createElement("OPTION");
		form.RoutesAvailable.options.add(emptyOption);
		emptyOption.innerText = "<No Route Available>";
		emptyOption.value = "0";
	}
	
	if (form.RoutesAssigned.length > 1 && form.RoutesAssigned.options[0].value == 0)
		form.RoutesAssigned.options.remove(0);
}

function removeRoutes(form) {
	var insertIdx = 0;
	if (form.RoutesAvailable.length > 0 && form.RoutesAvailable.options[0].value == 0)
		insertIdx = 1;
	
	if (defaultRouteSelected && !removeWarned) {
		if (confirm('You are going to remove the default route "<%= PAOFuncs.getYukonPAOName(liteEC.getDefaultRouteID()) %>" from the energy company. ' +
			'Doing so could cause severe problem to your system. Are you sure you want to continue?'))
			removeWarned = true;
		else
			return;
	}
	
	for (i = form.RoutesAssigned.length - 1; i >= 0; i--) {
		var oOption = form.RoutesAssigned.options[i];
		if (oOption.selected && oOption.value > 0) {
			form.RoutesAssigned.options.remove(i);
			form.RoutesAvailable.options.add(oOption, insertIdx);
			setContentChanged(true);
		}
	}
	
	if (form.RoutesAssigned.length == 0) {
		var emptyOption = document.createElement("OPTION");
		form.RoutesAssigned.options.add(emptyOption);
		emptyOption.innerText = "<No Route Assigned>";
		emptyOption.value = "0";
	}
	
	if (form.RoutesAvailable.length > 1 && form.RoutesAvailable.options[0].value == 0)
		form.RoutesAvailable.options.remove(0);
}

function init() {
	addRoutes(document.form1);
	removeRoutes(document.form1);
}

function prepareSubmit(form) {
	if (form.RoutesAssigned.options[0].value > 0) {
		for (i = 0; i < form.RoutesAssigned.length; i++) {
			var html = '<input type="hidden" name="RouteIDs" value="' + form.RoutesAssigned.options[i].value + '">';
			form.insertAdjacentHTML("beforeEnd", html);
		}
	}
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
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
              <span class="TitleHeader">ADMINISTRATION - ROUTES</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin" onsubmit="prepareSubmit(this)">
              <input type="hidden" name="action" value="UpdateRoutes">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Edit Routes</td>
                </tr>
                <tr> 
                  <td> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
                      <tr> 
                        <td width="85%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                  <tr> 
                                    <td width="45%" valign="top"> Available Routes<br>
                                      <select id="RoutesAvailable" name="RoutesAvailable" size="20" style="width:235" multiple>
<%
	LiteYukonPAObject[] allRoutes = PAOFuncs.getAllLiteRoutes();
	for (int i = 0; i < allRoutes.length; i++) {
		if (assignedRoutes.contains(allRoutes[i])) continue;
%>
                                        <option value="<%= allRoutes[i].getYukonID() %>"><%= allRoutes[i].getPaoName() %></option>
<%
	}
%>
                                      </select>
                                    </td>
                                    <td width="10%"> 
                                      <input type="button" id="AddButton" name="AddButton" value=" >> " onclick="addRoutes(this.form)">
                                      <br>
                                      <input type="button" id="RemoveButton" name="RemoveButton" value=" << " onclick="removeRoutes(this.form)">
                                    </td>
                                    <td width="45%" valign="top"> Assigned Routes:<br>
                                      <select id="RoutesAssigned" name="RoutesAssigned" size="20" style="width:235" multiple onchange="selectionChanged(this.form)">
<%
	for (int i = 0; i < routes.length; i++) {
%>
                                        <option value="<%= routes[i].getYukonID() %>"><%= routes[i].getPaoName() %></option>
<%
	}
	if (inheritedRoutes != null) {
		for (int i = 0; i < inheritedRoutes.length; i++) {
%>
                                        <option value="<%= inheritedRoutes[i].getYukonID() %>" style="color:#999999"><%= inheritedRoutes[i].getPaoName() %></option>
<%
		}
	}
%>
                                      </select>
                                      <br>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr>
                  <td width="290" align="right"> 
                    <input type="submit" name="Submit" value="Submit">
                  </td>
                  <td width="205"> 
                    <input type="button" name="Reset" value="Reset" onclick="location.reload()">
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
