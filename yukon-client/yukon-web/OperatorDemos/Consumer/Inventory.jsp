<%
	String invNoStr = request.getParameter("InvNo");
	int invNo = -1;
	if (invNoStr != null)
		try {
			invNo = Integer.parseInt(invNoStr);
		}
		catch (NumberFormatException e) {}
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
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
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="BlueHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="blueLink">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="blueLink">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
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
		  <% String pageName = "Inventory.jsp?InvNo=" + invNoStr; %>
          <%@ include file="Nav.jsp" %>
<%	
	// Header files have already been included in Nav.jsp
	StarsLMHardware hardware = null;
	ArrayList appList = new ArrayList();
	StarsAppliance[] starsApps = null;
	
	if (invNo >= 0) {
		hardware = inventories.getStarsLMHardware(invNo);
		
		for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
			StarsAppliance app = appliances.getStarsAppliance(i);
			if (app.getInventoryID() == hardware.getInventoryID())
				appList.add(app);
		}
	}
	
	starsApps = new StarsAppliance[ appList.size() ];
	appList.toArray( starsApps );
%>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><% String header = "HARDWARE - " + Mappings.getLMHardwareName(hardware.getLMDeviceType()); %><%@ include file="InfoSearchBar.jsp" %>
             <br>
              </div>
            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
              <tr> 
                <td width="300" valign="top" bgcolor="#FFFFFF"> 
                  <table width="300" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <form name="form3" method="get" action="">
                        <td valign="top"><span class="MainHeader"><b>DEVICE</b></span> 
                          <hr>
                          <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Type: </div>
                              </td>
                              <td width="200"> 
                                <select name="DeviceType">
                                  <option <% if (hardware.getLMDeviceType().equalsIgnoreCase(LMHardwareBase.DEVICETYPE_LCR1000)) { %>selected<% } %>>
								  	<%= Mappings.getLMHardwareName(LMHardwareBase.DEVICETYPE_LCR1000) %></option>
                                  <option <% if (hardware.getLMDeviceType().equalsIgnoreCase(LMHardwareBase.DEVICETYPE_LCR2000)) { %>selected<% } %>>
								  	<%= Mappings.getLMHardwareName(LMHardwareBase.DEVICETYPE_LCR2000) %></option>
                                  <option <% if (hardware.getLMDeviceType().equalsIgnoreCase(LMHardwareBase.DEVICETYPE_LCR3000)) { %>selected<% } %>>
								  	<%= Mappings.getLMHardwareName(LMHardwareBase.DEVICETYPE_LCR3000) %></option>
                                  <option <% if (hardware.getLMDeviceType().equalsIgnoreCase(LMHardwareBase.DEVICETYPE_LCR4000)) { %>selected<% } %>>
								  	<%= Mappings.getLMHardwareName(LMHardwareBase.DEVICETYPE_LCR4000) %></option>
                                  <option <% if (hardware.getLMDeviceType().equalsIgnoreCase(LMHardwareBase.DEVICETYPE_LCR5000)) { %>selected<% } %>>
								  	<%= Mappings.getLMHardwareName(LMHardwareBase.DEVICETYPE_LCR5000) %></option>
                                </select>
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Serial #: </div>
                              </td>
                              <td width="200"> 
<!--
                                <select name="select">
                                  <option>12345</option>
                                  <option>67890</option>
                                </select>
-->
								<input type="text" name="SerialNumber" maxlength="30" size="24" value="<%= hardware.getManufactureSerialNumber() %>">
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Alt Tracking #: </div>
                              </td>
                              <td width="200"> 
                                <input type="text" name="AltTrackNumber" maxlength="30" size="24" value="<%= hardware.getAltTrackingNumber() %>">
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Receive Date: </div>
                              </td>
                              <td width="200"> 
                                <input type="text" name="ReceiveDate" maxlength="30" size="24" value="<%= hardware.getReceiveDate() %>">
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Remove Date: </div>
                              </td>
                              <td width="200"> 
                                <input type="text" name="RemoveDate" maxlength="30" size="24" value="<%= hardware.getRemoveDate() %>">
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Voltage: </div>
                              </td>
                              <td width="200"> 
                                <input type="text" name="Voltage" maxlength="30" size="24" value="<%= hardware.getVoltage() %>">
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Status: </div>
                              </td>
                              <td width="200"> 
                                <select name="select6">
                                  <option>Available</option>
                                  <option>Temp Unavail</option>
                                  <option>Unavailable</option>
                                </select>
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Notes: </div>
                              </td>
                              <td width="200"> 
                                <textarea name="Notes" rows="2 wrap="soft" cols="24"><%= hardware.getNotes() %></textarea>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </form>
                    </tr>
                  </table>
                  </td>
                <td width="300" valign="top" bgcolor="#FFFFFF"> 
                  <table width="300" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <form name="form3" method="get" action="">
                        <td valign="top"><span class="MainHeader"><b>INSTALL</b></span> 
                          <hr>
                          <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Date Installed: </div>
                              </td>
                              <td width="200"> 
                                <input type="text" name="InstallDate" maxlength="30" size="24" value="<%= hardware.getInstallDate() %>">
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Service Company: </div>
                              </td>
                              <td width="200"> 
                                <select name="ServiceCompany">
                                  <option><%= hardware.getInstallationCompany() %></option>
                                </select>
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Location: </div>
                              </td>
                              <td width="200"> 
                                <select name="select4">
                                  <option>Outside North</option>
                                </select>
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Notes: </div>
                              </td>
                              <td width="200"> 
                                <textarea name="notes" rows="2 wrap="soft" cols="24"></textarea>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </form>
                    </tr>
                  </table>
                  <div align="center"><br>
                    <span class="MainHeader">Hardware History</span><br>
                  </div>
                  <table width="250" border="1" cellspacing="0" cellpadding="3" align="center">
                    <tr> 
                      <td width="104" class="HeaderCell">Date</td>
                      <td width="100" class="HeaderCell">Action</td>
                    </tr>
                    <tr valign="top"> 
                      <td width="104" class="TableCell" bgcolor="#FFFFFF">11/02/99</td>
                      <td width="100" class="TableCell" bgcolor="#FFFFFF">Install</td>
                    </tr>
                    <tr valign="top"> 
                      <td width="104" class="TableCell" bgcolor="#FFFFFF">07/30/00</td>
                      <td width="100" class="TableCell" bgcolor="#FFFFFF">Reconfigure</td>
                    </tr>
                    <tr valign="top"> 
                      <td width="104" class="TableCell" bgcolor="#FFFFFF">04/05/01</td>
                      <td width="100" class="TableCell" bgcolor="#FFFFFF">Repair</td>
                    </tr>
                  </table>
                  </td>
              </tr>
            </table>
            <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
              <tr> 
                <form name="form1" type="get" action="LCR5000.jsp">
                  <td width="186"> 
                    <div align="right"> 
                      <input type="submit" name="Submit2" value="Submit">
                    </div>
                  </td>
                </form>
                <form name="form1">
                  <td width="194"> 
                    <div align="left"> 
                      <input type="reset" name="Cancel2" value="Cancel">
                    </div>
                  </td>
                </form>
              </tr>
            </table>
            <hr>
            <div align="center"><br>
              <span class="Main"><b>Appliance Summary</b></span><br>
              <table width="350" border="1" cellspacing="0" cellpadding="3">
                <tr bgcolor="#FFFFFF"> 
                  <td width="104" class="HeaderCell"> Appliance Type</td>
                  <td width="100" class="HeaderCell"> Status</td>
                  <td width="120" class="HeaderCell"> Enrolled Programs</td>
                </tr>
<!--
                <tr bgcolor="#FFFFFF" valign="top"> 
                  <td width="104" class="TableCell"> Water Heater</td>
                  <td width="100" class="TableCell"> In Service</td>
                  <td width="120"> 
                    <div align="center"><img src="WaterHeater.gif" width="60" height="59"></div>
                  </td>
                </tr>
                <tr bgcolor="#FFFFFF" valign="top"> 
                  <td width="104" class="TableCell"> Air Conditioner</td>
                  <td width="100" class="TableCell"> Out of Service</td>
                  <td width="120"> 
                    <div align="center"><img src="AC.gif" width="60" height="59"></div>
                  </td>
                </tr>
-->
<%
	for (int i = 0; i < starsApps.length; i++) {
		StarsLMProgram program = null;
		for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
			StarsLMProgram starsProg = (StarsLMProgram) programs.getStarsLMProgram(j);
			if (starsProg.getProgramID() == starsApps[i].getLmProgramID()) {
				program = starsProg;
				break;
			}
		}
%>
                <tr bgcolor="#FFFFFF" valign="top"> 
                  <td width="104" class="TableCell"> <%= Mappings.getApplianceName(starsApps[i].getStarsApplianceCategory().getCategory()) %></td>
                  <td width="100" class="TableCell"> In Service</td>
                  <td width="120"> 
                    <div align="center">
					  <img src="<%= Mappings.getApplianceImage(starsApps[i].getStarsApplianceCategory().getCategory()) %>" width="60" height="59"><br>
					  <span class="TableCell"><%= program.getProgramName() %></span>
					</div>
                  </td>
                </tr>
<%
	}
%>
              </table>
            </div>
            <p>&nbsp;</p>
          </td>
        <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
