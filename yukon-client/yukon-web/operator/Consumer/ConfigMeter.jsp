<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsMCT mct = inventories.getStarsMCT(invNo - inventories.getStarsLMHardwareCount());
	// Stacey, here are the fields useful to you:
	int deviceID = mct.getDeviceID();
	String deviceName = mct.getDeviceName();
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

</head>
<%@ page import="com.cannontech.database.data.lite.LiteDeviceMeterNumber" %>
<%@ page import="com.cannontech.database.Transaction"%>
<%@ page import="com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase"%>
<%@ page import="com.cannontech.database.data.lite.LiteDeviceMeterNumber"%>
<%@ page import="com.cannontech.database.data.lite.LiteFactory"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject"%>
<%@ page import="com.cannontech.database.data.pao.YukonPAObject"%>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs"%>
<%@ page import="com.cannontech.database.data.device.CarrierBase"%>
<%
	int deviceID = 31;
  	//create a liteDeviceMeterNumber using deviceID
	LiteDeviceMeterNumber liteDevMeterNum = new LiteDeviceMeterNumber(deviceID);
	liteDevMeterNum.retrieve(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

	//create a DBPersistent DeviceMeterGroup using the liteDeviceMeterNumber
	DeviceMeterGroupBase devMeterGroup = null;
	if( liteDevMeterNum != null)
	{
		devMeterGroup = (DeviceMeterGroupBase)LiteFactory.createDBPersistent(liteDevMeterNum);
		try {
			Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, devMeterGroup);
			devMeterGroup = (DeviceMeterGroupBase)t.execute();
		}
		catch(Exception e) {
			com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
		}
	}

	//get the liteYukonPao using the deviceID
	LiteYukonPAObject liteYukonPao = PAOFuncs.getLiteYukonPAO(deviceID);
    //create a DBPersistent YukonPaobject using the liteYukonPao
	YukonPAObject yukonPao = (YukonPAObject)LiteFactory.createDBPersistent(liteYukonPao);
	try	{
		Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, yukonPao);
		yukonPao = (YukonPAObject)t.execute();
	}
	catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	//Get the device's routeID
	int deviceRouteID = -1;
	//Get the device's physical Address
	int address = -1;
	if( yukonPao instanceof CarrierBase)
	{
		deviceRouteID = ((CarrierBase)yukonPao).getDeviceRoutes().getRouteID().intValue();
		address = ((CarrierBase)yukonPao).getDeviceCarrierSettings().getAddress().intValue();
	}
	int [] validRouteTypes = new int[]{
		com.cannontech.database.data.pao.RouteTypes.ROUTE_CCU,
		com.cannontech.database.data.pao.RouteTypes.ROUTE_MACRO
		};
	LiteYukonPAObject[] validRoutes = PAOFuncs.getRoutesByType(validRouteTypes);
%>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="150" height="102" background="../../WebConfig/MomWide.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="609" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer Account Information&nbsp;&nbsp;</td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                </td>
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
          <td  valign="top" width="101"> 
            <% String pageName = "ConfigMeter.jsp?deviceid=" + 16; //TEMP - FIX!!%>
            <%@ include file="include/nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "METER - CONFIGURATION"; %>
              <br>
			  <table width="100%" border="0" cellspacing="0" cellpadding="3">
			    <tr>
			      <td align="center" class="TitleHeader"><%= header %></td>
			    </tr>
			  </table>
              
              <% String errorMsg = null;%>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>              

			  <form name="invForm" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
                <input type="hidden" name="action" value="UpdateMeterConfig">
                <input type="hidden" name="deviceID" value="<%= 16 %>"> <% // FIX ME!%>
				<input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/ConfigMeter.jsp?deviceid=<%= 16 %>"> <% //FIX ME %>
				<input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Consumer/ConfigMeter.jsp?deviceid=<%= 16 %>"> <% //FIX ME %>
                <table width="350" border="0" cellspacing="0" cellpadding="3">
                  <tr> 
                    <td width="30%" class="SubtitleHeader" height="2" align="right">Meter 
                      Name:</td>
                    <td width="70%" class="TableCell" height="2"><%= yukonPao.getPAOName()%></td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" height="2" align="right">Meter 
                      Type:</td>
                    <td width="70%" class="TableCell" height="2"><%=yukonPao.getPAOType()%></td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" height="2" align="right">Meter 
                      Number:</td>
                    <td width="70%" height="2"> 
                      <input type="text" name="MeterNumberLabel" maxlength="70" size="20" value="<%=devMeterGroup.getDeviceMeterGroup().getMeterNumber()%>">
                    </td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" height="2" align="right">Physical 
                      Address:</td>
                    <td width="70%" height="2"> 
                      <input type="text" name="PhysicalAddressLabel" size="20" value="<%= address%>" maxlength="70">
                    </td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" height="2" align="right">Route:</td>
                    <td width="70%" height="2"> 
                      <select id="getRouteID()" name="RouteID">
                        <%
                      for (int i = 0; i < validRoutes.length; i++)
                      {
                        if( ((LiteYukonPAObject)validRoutes[i]).getYukonID() == deviceRouteID)
                          out.println("<OPTION SELECTED>"+((LiteYukonPAObject)validRoutes[i]).getPaoName());
                        else
                          out.println("<OPTION>"+((LiteYukonPAObject)validRoutes[i]).getPaoName());
                      }
                      
                      %>
                      </select>
                    </td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" height="2" align="right">Collection 
                      Group:</td>
                    <td width="70%" height="2"> 
                      <select id="collgroup" name="CollGroup">
                        <% /* Fill in the period drop down and attempt to match the current period with one of the options */
					  String [] collGroups = com.cannontech.database.db.device.DeviceMeterGroup.getDeviceCollectionGroups();
                      for( int i = 0; i < collGroups.length; i++ )
                      {
                        if( collGroups[i].equals(devMeterGroup.getDeviceMeterGroup().getCollectionGroup()) )
                          out.println("<OPTION SELECTED>" + collGroups[i]);
                        else
                          out.println("<OPTION>" + collGroups[i]);
                      }%>
                      </select>
                    </td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" height="2" align="right">&nbsp; 
                    </td>
                    <td width="70%" height="2">
                      <input type="reset" name="Reset" value="Restore">
                      <input type="submit" name="Submit2" value="Submit">
                    </td>
                  </tr>
                  <tr> 
                    <td> </td>
                  </tr>
                </table>
                <br>
              </form>
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
