<%@ include file="user_ee.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">

function confirm_form()
{
    checker.submitted.value = "true";
	return true;
}

function decline_form()
{
	checker.submitted.value = "false";
	checker.submit();
}
</script>
</head>
<%
  //Look up the customer curtailable amount, use this as the default amount
//  Object[][] gData2 = com.cannontech.util.ServletUtil.executeSQL( session, "select curtailamount from cicustomerbase where customerid=" +  customerID);  
//  String curtailAmount = gData2[0][0].toString();

  // Grab the 24 customers baseline values
  double[] baseLineValues = cache.getCustomerBaseLine( customerID);

  // If we couldn't find a baseline create an empty
  // array so below doesn't bomb
  if( baseLineValues == null )
    baseLineValues = new double[24];

%>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr>
                <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Buyback</td> 
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="Main"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
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
          <% String pageName = "user_ee_profile.jsp"; %>
          <%@ include file="nav.jsp" %> </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
		    <td width="657" valign="top" bgcolor="#FFFFFF">
              <table width="657" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                <td width="650" class="Main" valign="top"> 
                  <p align="center"><b><br>
                    ADMINISTRATION - PROFILE </b><br>
                    There must be a Primary Contact. Any number of additional 
                    contacts may also be included.
                  <div align="center"></div>
                  <form name="form1" method="POST" action="/servlet/UpdateContacts">
                    <%
	for (int i = 0; i < 3; i++) {
%>
                    <input type="hidden" name="ContactID<%= i+2 %>" value="<%= contacts[i].getContactID() %>">
                    <%
	}
%>
                    <table width="610" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td width="300" valign="top" bgcolor="#FFFFFF"><span class="MainHeader"><b>CONTACT 
                          (Primary) </b></span> 
                          <hr>
                          <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Last Name:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="LastName" maxlength="30" size="24" value="<%= primContact.getLastName() %>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">First Name:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="FirstName" maxlength="30" size="24" value="<%= primContact.getFirstName() %>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">e-mail Address:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="Email" maxlength="50" size="24" value="<%= primContact.getEmail().getNotification() %>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Phone 1:</div>
                              </td>
                              <td width="210">
                                <input type="text" name="HomePhone" maxlength="14" size="14" value="<%= primContact.getHomePhone() %>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Phone 2:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="WorkPhone" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Pager:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="Pager" maxlength="14" size="14" value="<%=%>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">User Name:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="UserName" maxlength="14" size="14" value="<%=  %>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Password:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="Password" maxlength="14" size="14" value="<%=  %>" onChange="setChanged()">
                              </td>
                            </tr>
							<tr>
							<td height="35"></td>
							</tr>
                          </table>
                          <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td><br>
                                <span class="MainHeader"><b>CONTACT 2</b></span> 
                                <hr>
                                <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">Last Name:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="LastName22" maxlength="30" size="24" value="<%= primContact.getLastName() %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">First Name:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="FirstName22" maxlength="30" size="24" value="<%= primContact.getFirstName() %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">e-mail Address:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="Email22" maxlength="50" size="24" value="<%= primContact.getEmail().getNotification() %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">Phone 1:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="HomePhone22" maxlength="14" size="14" value="<%= primContact.getHomePhone() %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">Phone 2:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="WorkPhone22" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">Pager:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="Pager2" maxlength="14" size="14" value="<%=%>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">User Name:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="UserName2" maxlength="14" size="14" value="<%=  %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">Password:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="Password2" maxlength="14" size="14" value="<%=  %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right"></div>
                                    </td>
                                    <td width="210"> 
                                      <input type="submit" name="Delete2" value="Delete">
                                    </td>
                                  </tr>
                                  <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Last Name:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="LastName3" maxlength="30" size="24" value="<%= primContact.getLastName() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">First Name:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="FirstName3" maxlength="30" size="24" value="<%= primContact.getFirstName() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">e-mail Address:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="Email3" maxlength="50" size="24" value="<%= primContact.getEmail().getNotification() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Phone 1:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="HomePhone3" maxlength="14" size="14" value="<%= primContact.getHomePhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Phone 2:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone3" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Pager:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone3" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">User Name:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone3" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Password:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone3" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                  </table>
                                  <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Last Name:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="LastName3" maxlength="30" size="24" value="<%= primContact.getLastName() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">First Name:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="FirstName3" maxlength="30" size="24" value="<%= primContact.getFirstName() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">e-mail Address:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="Email3" maxlength="50" size="24" value="<%= primContact.getEmail().getNotification() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Phone 1:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="HomePhone3" maxlength="14" size="14" value="<%= primContact.getHomePhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Phone 2:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone3" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Pager:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone3" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">User Name:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone3" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Password:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone3" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                  </table>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                        <td width="300" valign="top" bgcolor="#FFFFFF"> <span class="MainHeader"><b>CONTACT 
                          2 </b></span> 
                          <hr>
                          <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Last Name:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="LastName2" maxlength="30" size="24" value="<%= primContact.getLastName() %>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">First Name:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="FirstName2" maxlength="30" size="24" value="<%= primContact.getFirstName() %>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">e-mail Address:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="Email2" maxlength="50" size="24" value="<%= primContact.getEmail().getNotification() %>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Phone 1:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="HomePhone2" maxlength="14" size="14" value="<%= primContact.getHomePhone() %>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Phone 2:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="WorkPhone2" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Pager:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="Pager" maxlength="14" size="14" value="<%=%>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">User Name:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="UserName" maxlength="14" size="14" value="<%=  %>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Password:</div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="Password" maxlength="14" size="14" value="<%=  %>" onChange="setChanged()">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right"></div>
                              </td>
                              <td width="210"> 
                                <input type="submit" name="Delete" value="Delete">
                              </td>
                            </tr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Last Name:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="LastName" maxlength="30" size="24" value="<%= primContact.getLastName() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">First Name:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="FirstName" maxlength="30" size="24" value="<%= primContact.getFirstName() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">e-mail Address:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="Email" maxlength="50" size="24" value="<%= primContact.getEmail().getNotification() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Phone 1:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="HomePhone" maxlength="14" size="14" value="<%= primContact.getHomePhone() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Phone 2:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="WorkPhone" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Pager:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="WorkPhone" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">User Name:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="WorkPhone" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Password:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="WorkPhone" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                </td>
                              </tr>
                            </table>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Last Name:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="LastName" maxlength="30" size="24" value="<%= primContact.getLastName() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">First Name:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="FirstName" maxlength="30" size="24" value="<%= primContact.getFirstName() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">e-mail Address:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="Email" maxlength="50" size="24" value="<%= primContact.getEmail().getNotification() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Phone 1:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="HomePhone" maxlength="14" size="14" value="<%= primContact.getHomePhone() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Phone 2:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="WorkPhone" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Pager:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="WorkPhone" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">User Name:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="WorkPhone" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Password:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="WorkPhone" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                </td>
                              </tr>
                            </table>
                          </table>
                          <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td><br>
                                <span class="MainHeader"><b>CONTACT 4</b></span> 
                                <hr>
                                <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">Last Name:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="LastName23" maxlength="30" size="24" value="<%= primContact.getLastName() %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">First Name:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="FirstName23" maxlength="30" size="24" value="<%= primContact.getFirstName() %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">e-mail Address:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="Email23" maxlength="50" size="24" value="<%= primContact.getEmail().getNotification() %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">Phone 1:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="HomePhone23" maxlength="14" size="14" value="<%= primContact.getHomePhone() %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">Phone 2:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="WorkPhone23" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">Pager:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="Pager3" maxlength="14" size="14" value="<%=%>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">User Name:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="UserName3" maxlength="14" size="14" value="<%=  %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right">Password:</div>
                                    </td>
                                    <td width="210"> 
                                      <input type="text" name="Password3" maxlength="14" size="14" value="<%=  %>" onChange="setChanged()">
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="90" class="TableCell"> 
                                      <div align="right"></div>
                                    </td>
                                    <td width="210"> 
                                      <input type="submit" name="Delete3" value="Delete">
                                    </td>
                                  </tr>
                                  <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Last Name:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="LastName4" maxlength="30" size="24" value="<%= primContact.getLastName() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">First Name:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="FirstName4" maxlength="30" size="24" value="<%= primContact.getFirstName() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">e-mail Address:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="Email4" maxlength="50" size="24" value="<%= primContact.getEmail().getNotification() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Phone 1:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="HomePhone4" maxlength="14" size="14" value="<%= primContact.getHomePhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Phone 2:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone4" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Pager:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone4" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">User Name:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone4" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Password:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone4" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                  </table>
                                  <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Last Name:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="LastName4" maxlength="30" size="24" value="<%= primContact.getLastName() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">First Name:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="FirstName4" maxlength="30" size="24" value="<%= primContact.getFirstName() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">e-mail Address:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="Email4" maxlength="50" size="24" value="<%= primContact.getEmail().getNotification() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Phone 1:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="HomePhone4" maxlength="14" size="14" value="<%= primContact.getHomePhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Phone 2:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone4" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Pager:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone4" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">User Name:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone4" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="90" class="TableCell"> 
                                        <div align="right">Password:</div>
                                      </td>
                                      <td width="210"> 
                                        <input type="text" name="WorkPhone4" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
                                      </td>
                                    </tr>
                                  </table>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                    <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                      <tr> 
                        <td width="50%"> 
                          <div align="right"> 
                            <input type="submit" name="Save" value="Save">
                          </div>
                        </td>
                        <td width="50%"> 
                          <div align="left"> 
                            <input type="reset" name="Cancel" value="Cancel">
                          </div>
                        </td>
                      </tr>
                    </table>
                  </form>
                  <p>&nbsp;</p>
                  
                </td>
  </tr>
</table>


			</td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<input type="text" name="HomePhone5" maxlength="14" size="14" value="<%= primContact.getHomePhone() %>" onChange="setChanged()">
<br>
</body>
</html>
