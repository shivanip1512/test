<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">
function copyAddress(form) {
	form.BAddr1.value = form.SAddr1.value;
	form.BAddr2.value = form.SAddr2.value;
	form.BCity.value = form.SCity.value;
	form.BState.value = form.SState.value;
	form.BZip.value = form.SZip.value;
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
		  <% String pageName = "Update.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><% String header = "ACCOUNT - GENERAL"; %><%@ include file="InfoSearchBar.jsp" %><br>
			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %></div>
			
			<form method="POST" action="/servlet/SOAPClient">
			<input type="hidden" name="action" value="UpdateCustAccount">
            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
              <tr> 
                  <td width="300" valign="top" bgcolor="#FFFFFF"> <span class="MainHeader"><b>CUSTOMER 
                    CONTACT</b></span> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Account #:</div>
                        </td>
                        <td width="210" valign="top"> 
                          <table width="200" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="113"> 
                                <input type="text" name="AcctNo" maxlength="40" size="14" value="<%= account.getAccountNumber() %>">
                              </td>
                              <td valign="top" class="TableCell" width="87"> Commercial: 
                                <input type="checkbox" name="Commercial" value="true" <% if (account.getIsCommercial()) { %>checked<% } %>>
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
                          <input type="text" name="Company" maxlength="30" size="24" value="<%= account.getCompany() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Last Name:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="LastName" maxlength="30" size="24" value="<%= primContact.getLastName() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">First Name:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="FirstName" maxlength="30" size="24" value="<%= primContact.getFirstName() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Home #:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="HomePhone" maxlength="14" size="14" value="<%= primContact.getHomePhone() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Work #:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="WorkPhone" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>">
                        </td>
                      </tr>
					   <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">e-mail Address:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="Email" maxlength="50" size="24" value="<%= primContact.getEmail() %>">
                        </td>
                      </tr>
					  <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right"> </div>
                        </td>
                        <td width="210" class="TableCell"> 
                          <input type="checkbox" name="checkbox" value="checkbox">
                          Notify of control by e-mail</td>
                      </tr>
					  <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right"> </div>
                        </td>
                        <td width="210" class="TableCell"> 
                          <input type="checkbox" name="checkbox3" value="checkbox">
                          Notify of control by phone</td>
                      </tr>
					  <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Notes:</div>
                        </td>
                        <td width="210"> 
                          <textarea name="AcctNotes" rows="3" wrap="soft" cols="28" class = "TableCell"><%= account.getAccountNotes() %></textarea>
                        </td>
                      </tr>
                    </table>
                    <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td><br>
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
		String selectedStr = (entry.getEntryID() == siteInfo.getSubstation().getEntryID()) ? "selected" : "";
%>
								  <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
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
                                <input type="text" name="Feeder" maxlength="20" size="24" value="<%= siteInfo.getFeeder() %>">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Pole: </div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="Pole" maxlength="20" size="24" value="<%= siteInfo.getPole() %>">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Transformer Size: </div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="TranSize" maxlength="20" size="24" value="<%= siteInfo.getTransformerSize() %>">
                              </td>
                            </tr>
                            <tr> 
                              <td width="90" class="TableCell"> 
                                <div align="right">Service Voltage: </div>
                              </td>
                              <td width="210"> 
                                <input type="text" name="ServVolt" maxlength="20" size="24" value="<%= siteInfo.getServiceVoltage() %>">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                  <td width="300" valign="top" bgcolor="#FFFFFF"><span class="MainHeader"><b>SERVICE 
                    ADDRESS</b></span> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 1:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SAddr1" maxlength="30" size="24" value="<%= propAddr.getStreetAddr1() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 2:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SAddr2" maxlength="30" size="24" value="<%= propAddr.getStreetAddr2() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">City:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SCity" maxlength="30" size="24" value="<%= propAddr.getCity() %>">
                        </td>
                      </tr>
					  <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">County:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SCounty" maxlength="30" size="24" value="<%= propAddr.getCounty() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">State:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SState" maxlength="2" size="14" value="<%= propAddr.getState() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Zip:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="SZip" maxlength="12" size="14" value="<%= propAddr.getZip() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Map #:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="PropNo" maxlength="12" size="14" value="<%= account.getPropertyNumber() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Notes:</div>
                        </td>
                        <td width="210"> 
                          <textarea name="PropNotes" rows="3" wrap="soft" cols="28" class = "TableCell"><%= account.getPropertyNotes() %></textarea>
                        </td>
                      </tr>
                    </table>
                    <b><br>
                    <span class="MainHeader">BILLING ADDRESS</span></b> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right"> </div>
                        </td>
                        <td width="210" class="TableCell"> 
                          <input type="checkbox" name="Same" value="Same as Above" onClick="copyAddress(this.form)">
                          Same as above</td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 1:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BAddr1" maxlength="30" size="24" value="<%= billAddr.getStreetAddr1() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Address 2:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BAddr2" maxlength="30" size="24" value="<%= billAddr.getStreetAddr2() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">City:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BCity" maxlength="30" size="24" value="<%= billAddr.getCity() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">State:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BState" maxlength="2" size="14" value="<%= billAddr.getState() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="90" class="TableCell"> 
                          <div align="right">Zip:</div>
                        </td>
                        <td width="210"> 
                          <input type="text" name="BZip" maxlength="12" size="14" value="<%= billAddr.getZip() %>">
                        </td>
                      </tr>
                    </table>
                    
                  </td>
              </tr>
            </table>
            <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
              <tr> 
                  <td width="186"> 
                    <div align="right"> 
                      <input type="submit" name="Save2" value="Save">
                    </div>
                  </td>
                  <td width="194"> 
                    <div align="left"> 
                      <input type="reset" name="Cancel2" value="Cancel">
                    </div>
                  </td>
              </tr>
            </table>
			</form>
            <p align="center">&nbsp;</p>
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
