<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link id="StyleSheet" rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function copyAddress(form) {
	if (form.CopyAddress.checked) {
		form.BAddr1.value = form.SAddr1.value;
		form.BAddr2.value = form.SAddr2.value;
		form.BCity.value = form.SCity.value;
		form.BState.value = form.SState.value;
		form.BZip.value = form.SZip.value;
		form.BAddr1.disabled = true;
		form.BAddr2.disabled = true;
		form.BCity.disabled = true;
		form.BState.disabled = true;
		form.BZip.disabled = true;
	}
	else {
		form.BAddr1.disabled = false;
		form.BAddr2.disabled = false;
		form.BCity.disabled = false;
		form.BState.disabled = false;
		form.BZip.disabled = false;
	}
}

function validate(form) {
	if (form.AcctNo.value == "") {
		alert("Account # cannot be empty!");
		return false;
	}
	if (form.Username != null && (form.Username.value != "" || form.Password.value != "")) {
		if (form.Username.value == "") {
			alert("Username cannot be empty!");
			return false;
		}
		if (form.Password.value == "") {
			alert("Password cannot be empty!");
			return false;
		}
		if (form.Password.value != form.Password2.value) {
			alert("Passwords don't match, please check the passwords.");
			return false;
		}
	}
	return true;
}

function confirmCancel() {
	if (confirm('Are you sure you want to cancel and discard all the changes you made?'))
		location = "../Operations.jsp";
}
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
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
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
          <td valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" bgcolor="#FFFFFF" valign = "top" align = "center"> 
            <% String header = "NEW SIGNUP"; %>
            <%@ include file="InfoSearchBar2.jsp" %>
            <% if (errorMsg != null) out.write("<br><span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
            <form name="form1" method="POST" action="/servlet/SOAPClient" onSubmit="return validate(this)">
              <input type="hidden" name="action" value="NewCustAccount">
<% if (request.getParameter("Wizard") != null) { %>
			  <input type="hidden" name="Wizard" value="true">
<% } %>
              <table width="610" border="0" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td width="300" valign="top"><span class="MainHeader"><b>CUSTOMER 
                    CONTACT</b></span> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Account #:</div>
                        </td>
                        <td width="210" valign="top"> 
                          <table width="191" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="95"> 
                                <input type="text" name="AcctNo" maxlength="40" size="14">
                              </td>
                              <td valign="top" class="TableCell" width="95" align="center">&nbsp;&nbsp;Commercial: 
                                <input type="checkbox" name="Commercial" value="true">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Company: </div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="Company" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Last Name:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="LastName" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">First Name:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="FirstName" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Home #:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="HomePhone" maxlength="14" size="14">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Work #:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="WorkPhone" maxlength="14" size="14">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">e-mail Address:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="Email" maxlength="50" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell">&nbsp;</td>
                        <td width="210"> 
                          <input type="checkbox" name="NotifyControl" value="true">
                          <span class="TableCell">Notify day of control</span></td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Notes:</div>
                        </td>
                        <td width="210"> 
                          <textarea name="AcctNotes" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Last Name (2):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="LastName2" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">First Name (2):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="FirstName2" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Home # (2):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="HomePhone2" maxlength="14" size="14">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Work # (2):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="WorkPhone2" maxlength="14" size="14">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Last Name (3):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="LastName3" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">First Name (3):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="FirstName3" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Home # (3):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="HomePhone3" maxlength="14" size="14">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Work # (3):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="WorkPhone3" maxlength="14" size="14">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Last Name (4):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="LastName4" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">First Name (4):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="FirstName4" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Home # (4):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="HomePhone4" maxlength="14" size="14">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Work # (4):</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="WorkPhone4" maxlength="14" size="14">
                        </td>
                      </tr>
                    </table>
                  </td>
                  <td width="300" valign="top"><span class="MainHeader"><b>SERVICE 
                    ADDRESS </b>&nbsp;&nbsp;</span> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 1:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SAddr1" maxlength="40" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 2:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SAddr2" maxlength="40" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">City:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SCity" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">County:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SCounty" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">State:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SState" maxlength="2" size="14">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Zip:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SZip" maxlength="12" size="14">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Map #:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="PropNo" maxlength="12" size="14">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Notes:</div>
                        </td>
                        <td width="210"> 
                          <textarea name="PropNotes" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                        </td>
                      </tr>
                    </table>
                    <br>
                    <span class="MainHeader"><b>BILLING ADDRESS</b></span> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="TableCell" height="2">&nbsp;</td>
                        <td width="210" height="2" class="TableCell"> 
                          <input type="checkbox" name="CopyAddress" value="true" onClick="copyAddress(this.form)">
                          Same as above</td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 1:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BAddr1" maxlength="40" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 2:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BAddr2" maxlength="40" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">City:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BCity" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">State:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BState" maxlength="2" size="14">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell" height="2"> 
                          <div align="right">Zip:</div>
                        </td>
                        <td width="210" height="2"> 
                          <input type="text" name="BZip" maxlength="12" size="14">
                        </td>
                      </tr>
                    </table>
                    <br>
                    <span class="MainHeader"><b>SERVICE INFORMATION</b></span> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Substation Name: </div>
                        </td>
                        <td width="210"> 
                          <select name="Substation">
                            <%
	StarsCustSelectionList substationList = (StarsCustSelectionList) selectionListTable.get( com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION );
	for (int i = 0; i < substationList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = substationList.getStarsSelectionListEntry(i);
%>
                            <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
                            <%
	}
%>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Feeder: </div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="Feeder" maxlength="20" size="20">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Pole: </div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="Pole" maxlength="20" size="20">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Transformer Size: </div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="TranSize" maxlength="20" size="20">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Service Voltage: </div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="ServVolt" maxlength="20" size="20">
                        </td>
                      </tr>
                    </table>
                    <br>
                  </td>
                </tr>
              </table>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_ADMIN_CHANGE_LOGIN %>">
              <table width="300" border="0" cellspacing="0" cellpadding="1">
                <tr> 
                  <td width="100" class="TableCell"> 
                    <div align="right">User Name: </div>
                  </td>
                  <td width="200"> 
                    <input type="text" name="Username" maxlength="20" size="20">
                  </td>
                </tr>
                <tr> 
                  <td width="100" class="TableCell"> 
                    <div align="right">Password:</div>
                  </td>
                  <td width="200"> 
                    <input type="text" name="Password" maxlength="20" size="20">
                  </td>
                </tr>
                <tr> 
                  <td width="100" class="TableCell"> 
                    <div align="right">Confirm Password:</div>
                  </td>
                  <td width="200"> 
                    <input type="text" name="Password2" maxlength="20" size="20">
                  </td>
                </tr>
              </table>
              <br>
</cti:checkRole>
              <table width="400" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td width="190"> 
                    <div align="right"> 
<% if (request.getParameter("Wizard") == null) { %>
                      <input type="submit" name="Save" value="Save">
<% } else { %>
                      <input type="submit" name="Next" value="Next">
<% } %>
                    </div>
                  </td>
                  <td width="190"> 
                    <div align="left"> 
                      <input type="button" name="Cancel" value="Cancel" onclick="confirmCancel()">
                    </div>
                  </td>
                </tr>
              </table>
            </form>
            <p>&nbsp;</p>
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
