<%@ include file="include/StarsHeader.jsp" %>

<%@ page import="com.cannontech.database.cache.functions.PAOFuncs"%>
<%@ page import="com.cannontech.database.cache.functions.DBPersistentFuncs"%>
<%@ page import="com.cannontech.database.db.device.DeviceMeterGroup"%>
<%@ page import="com.cannontech.database.data.device.IDeviceMeterGroup"%>
<%@ page import="com.cannontech.database.data.device.CarrierBase"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject"%>
<%@ page import="com.cannontech.database.data.pao.YukonPAObject"%>
<%@ page import="com.cannontech.database.data.pao.PAOGroups"%>
<%@ page import="com.cannontech.database.data.pao.RouteTypes"%>

<%
	java.util.Enumeration enum1 = request.getParameterNames();
	while (enum1.hasMoreElements()){
		
		java.lang.Object element = enum1.nextElement();
		  	System.out.println(" --" + element.toString() + " = " + request.getParameter(element.toString()));
	}
 %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } 
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsMCT starsMCT = inventories.getStarsMCT(invNo - inventories.getStarsLMHardwareCount());
	int deviceID = starsMCT.getDeviceID();

	//get the liteYukonPao using the deviceID
	LiteYukonPAObject liteYukonPao = PAOFuncs.getLiteYukonPAO(deviceID);
	YukonPAObject yukonPao = (YukonPAObject)DBPersistentFuncs.retrieveDBPersistent(liteYukonPao);

  	//DeviceMeterGroup - meterNumber, collectionGroup
	DeviceMeterGroup devMeterGroup = ((IDeviceMeterGroup)yukonPao).getDeviceMeterGroup();

	int routeID = -1;	//Get the device's routeID
	int address = -1;		//Get the device's physical Address
	
	if( yukonPao instanceof CarrierBase)
	{
		// DeviceTypesFuncs.isCarrier(liteYukonPao.getType())
		routeID = ((CarrierBase)yukonPao).getDeviceRoutes().getRouteID().intValue();
		address = ((CarrierBase)yukonPao).getDeviceCarrierSettings().getAddress().intValue();
	}
	int [] validRouteTypes = new int[]{
		RouteTypes.ROUTE_CCU,
		RouteTypes.ROUTE_MACRO
		};
	LiteYukonPAObject[] validRoutes = PAOFuncs.getRoutesByType(validRouteTypes);
%>

<%
//Update the Device when submit button was pressed.
if (request.getParameter("Submit") != null)
{
	//Error message when db value changed during page display!!
	
	//Meter Number Updated
	boolean updateYukonPAO = false;
	String updateMeterNumber = (String)request.getParameter("MeterNumber");
	String prevMeterNumber = (String)request.getParameter("PrevMeterNumber");
	if( prevMeterNumber.equalsIgnoreCase(devMeterGroup.getMeterNumber()))
	{
		if(!updateMeterNumber.equalsIgnoreCase(devMeterGroup.getMeterNumber().toString()))
		{
			devMeterGroup.setMeterNumber(updateMeterNumber.toString());
			updateYukonPAO = true;
		}
	}
	else {
		errorMsg = "Changes have been made to data by another user.  Please try again";
	}
	
	//CollectionGroup Updated
	String updateCollGroup = (String)request.getParameter("CollGroup");
	if (!updateCollGroup.equalsIgnoreCase(devMeterGroup.getCollectionGroup()))
	{
		devMeterGroup.setCollectionGroup(updateCollGroup.toString());
		updateYukonPAO = true;
	}

	//Physical Address Updated
	int updateAddress = Integer.valueOf((String)request.getParameter("Address")).intValue();
	int prevAddress = Integer.valueOf((String)request.getParameter("PrevAddress")).intValue();
	if( prevAddress == address)
	{
		if( updateAddress != address)
		{
			((CarrierBase)yukonPao).getDeviceCarrierSettings().setAddress(new Integer(updateAddress));
			address = updateAddress;
			updateYukonPAO = true;
		}
	}
	else {
		errorMsg = "Changes have been made to data by another user.  Please try again";
	}

	//Route Updated
	int updateRouteID = Integer.valueOf((String)request.getParameter("RouteID")).intValue();
	int prevRouteID = Integer.valueOf((String)request.getParameter("PrevRouteID")).intValue();
	if( prevRouteID == routeID)
	{
		if ( updateRouteID != routeID)
		{
			((CarrierBase)yukonPao).getDeviceRoutes().setRouteID(new Integer(updateRouteID));
			routeID = updateRouteID;
			updateYukonPAO = true;
		}
	}
	else {
		errorMsg = "Changes have been made to data by another user.  Please try again";
	}

	//Update the database when change is true.
	if( updateYukonPAO )
		DBPersistentFuncs.update(yukonPao);	
}%>	


<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
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
            <% String pageName = "ConfigMeter.jsp?InvNo=" + invNo;%>
            <%@ include file="include/Nav.jsp" %>
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
              
  
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>              

			  <form name="invForm" method="POST" action="ConfigMeter.jsp">
                <input type="hidden" name="action" value="UpdateMeterConfig">
                <input type="hidden" name="InvNo" value="<%=invNo%>">
				<input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/ConfigMeter.jsp?InvNo=<%=invNo%>">
				<input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Consumer/ConfigMeter.jsp?InvNo=<%= invNo%>">
                <table width="350" border="0" cellspacing="0" cellpadding="3">
                  <tr> 
                    <td width="30%" class="SubtitleHeader" height="2" align="right">Meter Name:</td>
                    <td width="70%" class="TableCell" height="2"><%= liteYukonPao.getPaoName()%></td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" height="2" align="right">Meter Type:</td>
                    <td width="70%" class="TableCell" height="2"><%=PAOGroups.getPAOTypeString(liteYukonPao.getType())%></td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" height="2" align="right">Meter Number:</td>
                    <td width="70%" height="2"> 
                      <input type="text" name="MeterNumber" maxlength="70" size="20" value="<%=devMeterGroup.getMeterNumber()%>">
                      <input type="hidden" name="PrevMeterNumber" value="<%=devMeterGroup.getMeterNumber()%>">
                    </td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" height="2" align="right">Physical Address:</td>
                    <td width="70%" height="2"> 
                      <input type="text" name="Address" size="20" value="<%= address%>" maxlength="70">
                      <input type="hidden" name="PrevAddress" value="<%=address%>">
                    </td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" height="2" align="right">Route:</td>
                    <td width="70%" height="2"> 
                      <select id="RouteID" name="RouteID">
                        <%
                      for (int i = 0; i < validRoutes.length; i++)
                      {
                        if( ((LiteYukonPAObject)validRoutes[i]).getYukonID() == routeID)
                          out.println("<OPTION SELECTED VALUE=\"" + ((LiteYukonPAObject)validRoutes[i]).getYukonID() + "\">" + ((LiteYukonPAObject)validRoutes[i]).getPaoName());
                        else
                          out.println("<OPTION VALUE=\"" + ((LiteYukonPAObject)validRoutes[i]).getYukonID() + "\">"+((LiteYukonPAObject)validRoutes[i]).getPaoName());
                      }
                      
                      %>
                      </select>
                      <input type="hidden" name="PrevRouteID" value="<%=routeID%>">
                    </td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" height="2" align="right">Collection Group:</td>
                    <td width="70%" height="2"> 
                      <select id="collgroup" name="CollGroup">
                        <% /* Fill in the period drop down and attempt to match the current period with one of the options */
					  String [] collGroups = DeviceMeterGroup.getDeviceCollectionGroups();
                      for( int i = 0; i < collGroups.length; i++ )
                      {
                        if( collGroups[i].equals(devMeterGroup.getCollectionGroup()) )
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
                      <input type="submit" name="Submit" value="Submit">
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
