<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">
	document.getElementById("StyleSheet").href = '../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_STYLE_SHEET %>"/>';
</script>
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
                <td id="Header" colspan="4" height="74" background="../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height="28" class="PageHeader">&nbsp;&nbsp;&nbsp;Customer 
                  Account Information</td>
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
		  <% String pageName = "SOHistory.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"><% String header = "WORK ORDERS - SERVICE HISTORY"; %><%@ include file="InfoSearchBar.jsp" %></div>
            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
              <tr> 
                <td width="300" valign="top" bgcolor="#FFFFFF"> 
                  <table width="300" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td><span class="MainHeader"><b>CUSTOMER CONTACT</b></span> 
                        <hr>
                        <table width="300" border="0" cellspacing="0" cellpadding="3" align="center">
                          <tr> 
                            <td class="TableCell"> Account # 12345 </td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> Acme Company </td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> Last Name, First Name</td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> Home #: 800-555-1212 </td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> Work #: 800 800-555-2121 </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                  <br>
                  <table width="300" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td><span class="MainHeader"><b>SERVICE ADDRESS</b> </span> 
                        <hr>
                        <table width="300" border="0" cellspacing="0" cellpadding="3" align="center">
                          <tr> 
                            <td class="TableCell"> 1234 Main Street, Floor 3</td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> Golden Valley, MN 55427</td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> Map # 3A </td>
                          </tr>
                          <tr> 
                            <form name="form2" method="" action="">
                              <td class="TableCell"> 
                                <textarea name="textarea2" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                              </td>
                            </form>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="300" valign="top" bgcolor="#FFFFFF"> 
                  <table width="310" border="0" cellspacing="0" cellpadding="0" align="center">
                    <tr> 
                      <td><span class="MainHeader"><b>SERVICE REQUEST INFORMATION</b></span> 
                        <hr>
                        <table width="300" border="0" cellspacing="0" cellpadding="3" align="center">
                          <tr> 
                            <td class="TableCell"> Service Order # 12345</td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> Reported 07/11/02</td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> Service Call</td>
                          </tr>
                        </table>
                        <br>
                        <span class="MainHeader"><b>DEVICE</b></span> 
                        <hr>
                        <table width="300" border="0" cellspacing="0" cellpadding="3" align="center">
                          <tr> 
                            <td class="TableCell"> Switch</td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> LCR 5000</td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> Serial # 12345</td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> Inventory Tag Group 3: </td>
                          </tr>
                          <tr> 
                            <form name="form2" method="" action="">
                              <td class="TableCell"> 
                                <textarea name="textarea3" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                              </td>
                            </form>
                          </tr>
                        </table>
                        <br>
                        <span class="MainHeader"><b>STATUS</b></span> 
                        <hr>
                        <table width="300" border="0" cellspacing="0" cellpadding="3" align="center">
                          <tr> 
                            <td class="TableCell"> Complete</td>
                          </tr>
                          <tr> 
                            <form name="form2" method="" action="">
                              <td class="TableCell"> 
                                <textarea name="textarea" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                              </td>
                            </form>
                          </tr>
                          <tr> 
                            <td class="TableCell"> Joe Contractor</td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> Joe Technician</td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> Closed 07/14/02</td>
                          </tr>
                          <tr> 
                            <td class="TableCell"> 2 Hours, No Overtime</td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
            <form name="form1" method="get" action="ServiceSummary.jsp">
              <div align="center"> 
                <input type="submit" name="Back" value="Back">
                <br>
              </div>
            </form>
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
