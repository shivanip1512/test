<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramDirect" %>
<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function addRoutes(form) {
	var assgnRtList = document.getElementById("RoutesAssigned");
	var availRtList = document.getElementById("RoutesAvailable");
	var insertIdx = assgnRtList.length;
	
	for (i = availRtList.length - 1; i >= 0; i--) {
		var oOption = availRtList.options[i];
		if (oOption.selected && oOption.value > 0) {
			availRtList.options.remove(i);
			assgnRtList.options.add(oOption, insertIdx);
		}
	}
	
	if (availRtList.length == 0) {
		var emptyOption = document.createElement("OPTION");
		availRtList.options.add(emptyOption);
		emptyOption.innerText = "<No Route Available>";
		emptyOption.value = "0";
	}
	
	if (assgnRtList.length > 1 && assgnRtList.options[0].value == 0)
		assgnRtList.options.remove(0);
}

var removeWarned = false;

function removeRoutes(form) {
	var assgnRtList = document.getElementById("RoutesAssigned");
	var availRtList = document.getElementById("RoutesAvailable");
	var insertIdx = availRtList.length;
	
	if (assgnRtList.value != "" && !removeWarned) {
		removeWarned = true;
		alert('If you remove a route and click "Submit", all LM Hardware currently assigned to this route will be re-assigned to the default route.');
	}
	
	for (i = assgnRtList.length - 1; i >= 0; i--) {
		var oOption = assgnRtList.options[i];
		if (oOption.selected && oOption.value > 0) {
			if (oOption.value == <%= liteEC.getDefaultRouteID() %>) {
				alert("Cannot remove the default route!");
				continue;
			}
			
			assgnRtList.options.remove(i);
			availRtList.options.add(oOption, insertIdx);
		}
	}
	
	if (assgnRtList.length == 0) {
		var emptyOption = document.createElement("OPTION");
		assgnRtList.options.add(emptyOption);
		emptyOption.innerText = "<No Route Assigned>";
		emptyOption.value = "0";
	}
	
	if (availRtList.length > 1 && availRtList.options[0].value == 0)
		availRtList.options.remove(0);
}

function init() {
	addRoutes(document.form1);
	removeRoutes(document.form1);
}

function prepareSubmit(form) {
	var assgnRtList = document.getElementById("RoutesAssigned");
	if (assgnRtList.options[0].value > 0) {
		for (i = 0; i < assgnRtList.length; i++) {
			var html = '<input type="hidden" name="RouteIDs" value="' + assgnRtList.options[i].value + '">';
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
	ArrayList routeIDs = liteEC.getRouteIDs();
	
	TreeMap routeMap1 = new TreeMap();
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized (cache) {
		List allRoutes = cache.getAllRoutes();
		for (int i = 0; i < allRoutes.size(); i++) {
			LiteYukonPAObject litePao = (LiteYukonPAObject) allRoutes.get(i);
			if (routeIDs.contains( new Integer(litePao.getYukonID()) ))
				continue;
			routeMap1.put(litePao.getPaoName(), litePao);
		}
	}
	
	Iterator it1 = routeMap1.values().iterator();
	while (it1.hasNext()) {
		LiteYukonPAObject route = (LiteYukonPAObject) it1.next();
%>
                                        <option value="<%= route.getYukonID() %>"><%= route.getPaoName() %></option>
<%
	}
%>
                                      </select>
                                    </td>
                                    <td width="10%"> 
                                      <input type="button" id="AddButton" name="Remove" value=" >> " onClick="addRoutes(this.form)">
                                      <br>
                                      <input type="button" id="RemoveButton" name="Add" value=" << " onclick="removeRoutes(this.form)">
                                    </td>
                                    <td width="45%" valign="top"> Assigned Routes:<br>
                                      <select id="RoutesAssigned" name="RoutesAssigned" size="20" style="width:235" multiple>
<%
	if (liteEC.getDefaultRouteID() > 0) {
		LiteYukonPAObject dftRoute = PAOFuncs.getLiteYukonPAO( liteEC.getDefaultRouteID() );
%>
                                        <option value="<%= dftRoute.getYukonID() %>" style="color:#999999"><%= dftRoute.getPaoName() %> (Default)</option>
<%
	}
	
	TreeMap routeMap2 = new TreeMap();
	for (int i = 0; i < routeIDs.size(); i++) {
		int routeID = ((Integer) routeIDs.get(i)).intValue();
		if (routeID == liteEC.getDefaultRouteID()) continue;
		LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO( routeID );
		routeMap2.put( litePao.getPaoName(), litePao );
	}
	
	Iterator it2 = routeMap2.values().iterator();
	while (it2.hasNext()) {
		LiteYukonPAObject route = (LiteYukonPAObject) it2.next();
%>
                                        <option value="<%= route.getYukonID() %>"><%= route.getPaoName() %></option>
<%
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
                    <input type="button" name="Back" value="Back" onclick="location.href='AdminTest.jsp'">
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
