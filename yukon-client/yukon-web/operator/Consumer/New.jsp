<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="CssLink" rel="stylesheet" href="../demostyle.css" type="text/css">
<% if (ecWebSettings.getURL().length() > 0) { %>
	<script language="JavaScript">document.getElementById("CssLink").href = "../<%= ecWebSettings.getURL() %>";</script>
<% } %>

<script language="JavaScript">
function copyAddress(form) {
	form.BAddr1.value = form.SAddr1.value;
	form.BAddr2.value = form.SAddr2.value;
	form.BCity.value = form.SCity.value;
	form.BState.value = form.SState.value;
	form.BZip.value = form.SZip.value;
}

function checkPassword(form) {
	if (form.Password.value != form.Password2.value) {
		alert("Passwords don't match, please make sure you input the same password twice.");
		return false;
	}
	return true;
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
            <table width="656" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td id="Header" colspan="4" height="74" background="../Header.gif">&nbsp;</td>
<% if (ecWebSettings.getLogoLocation().length() > 0) { %>
	<script language="JavaScript">document.getElementById("Header").background = "../<%= ecWebSettings.getLogoLocation() %>";</script>
<% } %>
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
          <td valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" bgcolor="#FFFFFF" valign = "top" align = "center"> 
           <% String header = "NEW SIGNUP"; %><%@ include file="SearchBar.jsp" %><br>
		   <% if (errorMsg != null) out.write("<br><span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
		   
		   <form name="form1" method="POST" action="/servlet/SOAPClient" onSubmit="return checkPassword(this)">
			<input type="hidden" name="action" value="NewCustAccount">
            <table width="600" border="0" cellspacing="0" cellpadding="10" align="center">
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
                          <input type="text" name="SAddr1" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 2:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SAddr2" maxlength="30" size="24">
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
                        <td width="90" class="TableCell">&nbsp;</td>
                        <td width="210"> 
                          <input type="button" name="Same" value="Same as Above" onclick="copyAddress(this.form)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 1:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BAddr1" maxlength="30" size="24">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 2:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BAddr2" maxlength="30" size="24">
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
                        <td width="90" class="TableCell"> 
                          <div align="right">Zip:</div>
                        </td>
                        <td width="210"> 
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
<!--
            <table width="600" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td class="MainHeader"><b>PROGRAMS</b> 
                  <hr>
                  <table width="610" border="1" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                    <tr valign="top"> 
                        <td> 
                          <table width="95" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="23"> 
                                <input type="checkbox" name="checkbox2" value="checkbox">
                              </td>
                              <td width="84" class="TableCell">Cycle AC</td>
                            </tr>
                          </table>
                          <table width="95" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="37"> 
                                <div align="right"> 
                                  <input type="radio" name="radiobutton" value="radiobutton">
                                </div>
                              </td>
                              <td width="70" class="TableCell">Medium</td>
                            </tr>
                            <tr> 
                              <td width="37"> 
                                <div align="right"> 
                                  <input type="radio" name="radiobutton" value="radiobutton">
                                </div>
                              </td>
                              <td width="70" class="TableCell"> Light</td>
                            </tr>
                          </table>
                        </td>
                        <td> 
                          <table width="95" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="23"> 
                                <input type="checkbox" name="checkbox22" value="checkbox">
                              </td>
                              <td width="84" class="TableCell">Water Heater</td>
                            </tr>
                          </table>
                          <table width="95" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="37"> 
                                <div align="right"> 
                                  <input type="radio" name="radiobutton" value="radiobutton">
                                </div>
                              </td>
                              <td width="70" class="TableCell">8 Hours</td>
                            </tr>
                            <tr> 
                              <td width="37"> 
                                <div align="right"> 
                                  <input type="radio" name="radiobutton" value="radiobutton">
                                </div>
                              </td>
                              <td width="70" class="TableCell">4 Hours </td>
                            </tr>
                            <tr> 
                              <td width="37"> 
                                <div align="right"> 
                                  <input type="radio" name="radiobutton" value="radiobutton">
                                </div>
                              </td>
                              <td width="70" class="TableCell">ETS </td>
                            </tr>
                          </table>
                        </td>
                        <td> 
                          <table width="95" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="23"> 
                                <input type="checkbox" name="checkbox23" value="checkbox">
                              </td>
                              <td width="84" class="TableCell">Duel Fuel</td>
                            </tr>
                          </table>
                          <table width="95" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="37"> 
                                <div align="right"> 
                                  <input type="radio" name="radiobutton" value="radiobutton">
                                </div>
                              </td>
                              <td width="70" class="TableCell">Unlimited</td>
                            </tr>
                            <tr> 
                              <td width="37"> 
                                <div align="right"> 
                                  <input type="radio" name="radiobutton" value="radiobutton">
                                </div>
                              </td>
                              <td width="70" class="TableCell"> Limited </td>
                            </tr>
                          </table>
                        </td>
                        <td> 
                          <table width="95" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="23"> 
                                <input type="checkbox" name="checkbox24" value="checkbox">
                              </td>
                              <td width="84" class="TableCell">Electrical Heat 
                                ETS</td>
                            </tr>
                          </table>
                        </td>
                        <td> 
                          <table width="95" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="23"> 
                                <input type="checkbox" name="checkbox25" value="checkbox">
                              </td>
                              <td width="84" class="TableCell">Hot Tub</td>
                            </tr>
                          </table>
                        </td>
                        <td> 
                          <table width="95" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="23"> 
                                <input type="checkbox" name="checkbox26" value="checkbox">
                              </td>
                              <td width="84" class="TableCell">Pool Pump</td>
                            </tr>
                          </table>
                        </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
-->
              <table width="400" border="0" cellspacing="0" cellpadding="5" align="center">
              <tr> 
                  <td width="191"> 
                    <div align="right"> 
                      <input type="submit" name="Save" value="Save">
                    </div>
                  </td>
                  <td width="189"> 
                    <div align="left"> 
                      <input type="button" name="Cancel" value="Cancel" onclick="location='../Operations.jsp'">
                    </div>
                  </td>
              </tr>
            </table>
		   </form>
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
