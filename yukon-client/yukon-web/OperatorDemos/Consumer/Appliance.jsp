<%@ include file="StarsHeader.jsp" %>
<%
	String appNoStr = request.getParameter("AppNo");
	int appNo = -1;
	if (appNoStr != null)
		try {
			appNo = Integer.parseInt(appNoStr);
		}
		catch (NumberFormatException e) {}
		
	StarsAppliance appliance = null;
	StarsLMHardware hardware = null;
	StarsLMProgram program = null;
	StarsApplianceCategory category = null;
	
	if (appNo >= 0) {
		appliance = appliances.getStarsAppliance(appNo);
		
		for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
			StarsLMHardware hw = inventories.getStarsLMHardware(i);
			if (hw.getInventoryID() == appliance.getInventoryID()) {
				hardware = hw;
				break;
			}
		}
	
		for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
			StarsLMProgram starsProg = programs.getStarsLMProgram(i);
			if (starsProg.getProgramID() == appliance.getLmProgramID()) {
				program = starsProg;
				break;
			}
		}
		
		for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
			StarsApplianceCategory appCat = categories.getStarsApplianceCategory(i);
			if (appCat.getApplianceCategoryID() == appliance.getApplianceCategoryID()) {
				category = appCat;
				break;
			}
		}
	}
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
                  <td width="265" height = "28" class="Header3" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
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
            <% String pageName = "Appliance.jsp?AppNo=" + appNoStr; %>
            <%@ include file="Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><% String header = "APPLIANCES"; %> <%@ include file="InfoSearchBar.jsp" %>
              <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                <tr>
				<form name="form6" method="get" action=""> 
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Description: </div>
                          </td>
                          <td width="200"> 
                            <input type="text" name="textfield5322" maxlength="40" size="30" value="<%= appliance.getCategoryDescription() %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Manufacturer:</div>
                          </td>
                          <td width="200"> 
                            <select name="select3">
                              <option>Century</option>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Year Manufactured:</div>
                          </td>
                          <td width="200"> 
                            <input type="text" name="textfield243" maxlength="14" size="14">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Location:</div>
                          </td>
                          <td width="200"> 
                            <select name="select4">
                              <option>Basement</option>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Service Company:</div>
                          </td>
                          <td width="200">
                            <input type="text" name="textfield532" maxlength="40" size="30">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Notes:</div>
                          </td>
                          <td width="200"> 
                            <textarea name="notes" rows="3" wrap="soft" cols="28" class = "TableCell"><%= appliance.getNotes() %></textarea>
                          </td>
                        </tr>
                      </table>
                      </td>
				  </form>
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
<%
	if (program != null) {
%>
				    <form name="form7" method="POST" action="/servlet/SOAPClient">
					  <input type="hidden" name="action" value="GetLMCtrlHist">
					  <%-- Group ID is used for finding control history --%>
					  <input type="hidden" name="Group" value="<%= program.getGroupID() %>">
					  <%-- AppNo will be transferred to the destination page to identify the program --%>
					  <input type="hidden" name="AppNo" value="<%= appNoStr %>">
					  <%-- BackURL will be transferred to the destination page to indicate the referrer --%>
					  <input type="hidden" name="BackURL" value="Appliance.jsp">
                      <table width="250" border="1" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <td width="109" class="HeaderCell"> 
                            <div align="center">
                             Enrolled Programs
                            </div>
                          </td>
                          <td width="151" class="HeaderCell"> 
                            <div align="center">Control 
                              History</div>
                          </td>
                        </tr>
                        <tr valign="top"> 
                          <td width="109" bgcolor="#FFFFFF"> 
                            <div align="center"> <img src="<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" height="59"><br>
							  <span class="TableCell"><%= program.getProgramName() %></span>
							</div>
                          </td>
                          <td width="151" bgcolor="#FFFFFF"> 
                            <table width="150" border="0" cellspacing="0" cellpadding="3" align="center">
                              <tr> 
                                <td width="180" valign="top" align="center"> 
                                  <select name="Period">
                                    <option value="PastWeek">Past Week</option>
                                    <option value="PastMonth">Past Month </option>
                                    <option value="All">All</option>
                                  </select>
                                  <br>
                                </td>
                              </tr>
                              <tr> 
                                <td width="180" valign="top" align="center"> 
                                  <input type="submit" name="View22" value="View">
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      <table width="250" border="1" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <td class="TableCell"> 
                            <div align="center">Controllable kW:&nbsp; 
                              <input type="text" name="textfield2233" maxlength="2" size="10">
                            </div>
                          </td>
                          </tr>
                      </table>
                     </form>
<%
	}
	else {
%>
					<p align="center">There is no program for this appliance</p>
<%
	}
%>
                    </td>
                </tr>
              </table>
              <table width="250" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                <tr>
                  <td width="33%" align = "right"> 
                    <input type="submit" name="Submit2" value="Submit">
                  </td>
                  <form name="form1" method="get" action="Appliances.jsp">
                    <td width="33%" align = "center"> 
                      <div>
                        <input type="reset" name="Cancel" value="Cancel">
                      </div>
                    </td>
                  </form>
                  <form name="form1" method="get" action="">
                    <td width="33%"> 
                      <div align="left"> 
                        <input type="button" name="Submit" value="Delete" onclick = "Javascript:confirm('Are you sure you would like to delete this appliance?');">
                      </div>
                    </td>
                  </form>
                </tr>
              </table>
              <hr>
              <div align="center" class="MainHeader"><br>
                <b>Hardware Summary</b></div>
<%
	if (hardware != null) {
%>
              <table width="360" border="1" cellspacing="0" cellpadding="3" align="center">
                <tr bgcolor="#CCCCCC"> 
                  <td width="75" class="HeaderCell">Category</td>
                  <td width="75" class="HeaderCell">Type</td>
                  <td width="75" class="HeaderCell">Serial #</td>
                </tr>
                <tr bgcolor="#FFFFFF"> 
                  <td width="75" class="TableCell"><%= hardware.getCategory() %></td>
                  <td width="75" class="TableCell"><%= hardware.getLMDeviceType() %></td>
                  <td width="75" class="TableCell"><%= hardware.getManufactureSerialNumber() %></td>
                </tr>
              </table>
<%
	}
	else {
%>
			  <p>There is no hardware attached to this appliance.</p>
<%
	}
%>
              <p>&nbsp;</p>
              </div>
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
