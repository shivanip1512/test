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
          <td width="102" height="102" background="WorkImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" class="PageHeader">&nbsp;&nbsp;&nbsp;Work Orders</td>
                <td width="235" height = "28" valign="middle">&nbsp;</td>
                <form method="post" action="../Operations.jsp">
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
                </form>
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
            <div align="center"><span class="TableCell1"><a href="InstallList.jsp" class="Link2">Back 
              to List</a></span> </div>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "INSTALLATION DETAIL"; %>
              <%@ include file="SearchBar.jsp" %>
              
          
              <table width="610" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                    <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td><span class="MainHeader"><b>INSTALLATION INFORMATION</b></span>  
                          <hr>
                          <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Work Order #:</div>
                              </td>
                              <td width="200"> 
                                <input type="text" name="textfield2233" maxlength="2" size="14">
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Date Reported:</div>
                              </td>
                              <td width="200"> 
                                <input type="text" name="textfield2234" maxlength="2" size="14">
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Service Type:</div>
                              </td>
                              <td width="210"> 
                                <select name="select3">
                                  <option>Install</option>
                                </select>
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Assigned to:</div>
                              </td>
                              <td width="210"> 
                                <select name="select3">
                                  <option>Joe Technician</option>
                                </select>
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Description:</div>
                              </td>
                              <td width="210"> 
                                <textarea name="textarea2" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Status:</div>
                              </td>
                              <td width="210"> 
                                <select name="select3">
                                  <option>Pending</option>
                                  <option>Completed</option>
                                  <option>Scheduled</option>
                                </select>
                              </td>
                            </tr>
                            <tr> 
                              <td width="100" class="TableCell"> 
                                <div align="right">Action:</div>
                              </td>
                              <td width="210"> 
                                <textarea name="textarea2" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                              </td>
                            </tr>
                          </table>
                          <span class="Main"><br>
                          </span></td>
                      </tr>
                    </table>
                  </td>
					<form name="form5" method="get" action=""> 	
                    <td width="300" valign="top" bgcolor="#FFFFFF">
                      <table width="300" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td><span class="MainHeader"><b>CUSTOMER CONTACT</b></span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
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
                                <td class="TableCell"> Work #: 800 800-555-2121 
                                </td>
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
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td class="TableCell"> 1234 Main Street, Floor 
                                  3</td>
                              </tr>
                              <tr> 
                                <td class="TableCell"> Golden Valley, MN 55427</td>
                              </tr>
                              <tr> 
                                <td class="TableCell"> Map # 3A </td>
                              </tr>
                              <tr> 
                                <td class="TableCell"> 
                                  <textarea name="textarea3" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
				  </form>
                </tr>
              </table>
              <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                <tr> 
				<form name="form3" method="post" action="InstallDetail.jsp">
                  <td width="186"> 
                      <div align="right"> 
                        <input type="submit" name="" value="Save">
                      </div>
                  </td>
				  </form>
				  <form name="form4" method="get" action="">
                  <td width="194"> 
                      <div align="left"> 
                        <input type="reset" name="" value="Cancel">
                      </div>
                  </td>
				  </form>
                </tr>
              </table>
              <p>&nbsp;</p>
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
