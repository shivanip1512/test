<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	AdditionalContact[] contacts = new AdditionalContact[3];
	for (int i = 0; i < 3; i++) {
		if (i < account.getAdditionalContactCount())
			contacts[i] = account.getAdditionalContact(i);
		else
			contacts[i] = (AdditionalContact) StarsFactory.newStarsCustomerContact(AdditionalContact.class);
	}
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
var changed = false;

function setChanged() {
	changed = true;
}

function saveChanges() {
	var form = document.form1;
	if (changed) {
		if (form.LastName.value.length == 0 || form.FirstName.value.length == 0)
			alert("Last and first name of the primary contact cannot be empty, please correct them before submitting the changes");
		else
			form.submit();
	}
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
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
		  <% String pageName = "Contacts.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"><% String header = "ACCOUNT - CONTACTS"; %>
			<%@ include file="include/InfoSearchBar.jsp" %> 
			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %></div>
              
			<form name="form1" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
			  <input type="hidden" name="action" value="UpdateContacts">
<%
	for (int i = 0; i < 3; i++) {
%>
			  <input type="hidden" name="ContactID<%= i+2 %>" value="<%= contacts[i].getContactID() %>">
<%
	}
%>
              <table width="610" border="0" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                    <td width="300" valign="top" bgcolor="#FFFFFF"><span class="SubtitleHeader">CONTACT (Primary)</span> 
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
                            <div align="right">Home #:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="HomePhone" maxlength="14" size="14" value="<%= primContact.getHomePhone() %>" onChange="setChanged()">
                          </td>
                        </tr>
                        <tr> 
                          <td width="90" class="TableCell"> 
                            <div align="right">Work #:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="WorkPhone" maxlength="14" size="14" value="<%= primContact.getWorkPhone() %>" onChange="setChanged()">
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
                      </table>
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td><br>
                            <span class="SubtitleHeader">CONTACT 2</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Last Name:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="LastName3" maxlength="30" size="24" value="<%= contacts[0].getLastName() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">First Name:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="FirstName3" maxlength="30" size="24" value="<%= contacts[0].getFirstName() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Home #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="HomePhone3" maxlength="14" size="14" value="<%= contacts[0].getHomePhone() %>" onChange="setChanged()">
                                </td>
                              </tr>
                              <tr> 
                                <td width="90" class="TableCell"> 
                                  <div align="right">Work #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="WorkPhone3" maxlength="14" size="14" value="<%= contacts[0].getWorkPhone() %>" onChange="setChanged()">
                                </td>
                              </tr>
							  <tr>
							    <td width="90" class="TableCell">
								  <div align="right">e-mail Address:</div>
							    </td>
							    <td width="210">
								  <input type="text" name="Email3" maxlength="50" size="24" value="<%= contacts[0].getEmail().getNotification() %>" onChange="setChanged()">
							    </td>
							  </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                    
                  <td width="300" valign="top" bgcolor="#FFFFFF"> <span class="SubtitleHeader">CONTACT 
                    3</span>
<hr>
                      <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                        <tr> 
                          <td width="90" class="TableCell"> 
                            <div align="right">Last Name:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="LastName2" maxlength="30" size="24" value="<%= contacts[1].getLastName() %>" onchange="setChanged()">
                          </td>
                        </tr>
                        <tr> 
                          <td width="90" class="TableCell"> 
                            <div align="right">First Name:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="FirstName2" maxlength="30" size="24" value="<%= contacts[1].getFirstName() %>" onchange="setChanged()">
                          </td>
                        </tr>
                        <tr> 
                          <td width="90" class="TableCell"> 
                            <div align="right">Home #:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="HomePhone2" maxlength="14" size="14" value="<%= contacts[1].getHomePhone() %>" onchange="setChanged()">
                          </td>
                        </tr>
                        <tr> 
                          <td width="90" class="TableCell"> 
                            <div align="right">Work #:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="WorkPhone2" maxlength="14" size="14" value="<%= contacts[1].getWorkPhone() %>" onchange="setChanged()">
                          </td>
                        </tr>
                        <tr>
                          <td width="90" class="TableCell">
                            <div align="right">e-mail Address:</div>
                          </td>
                          <td width="210">
                            <input type="text" name="Email2" maxlength="50" size="24" value="<%= contacts[1].getEmail().getNotification() %>" onChange="setChanged()">
                          </td>
                        </tr>
                      </table>
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td><br>
                            <span class="SubtitleHeader">CONTACT 4</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
							<tr> 
							  <td width="90" class="TableCell"> 
								<div align="right">Last Name:</div>
							  </td>
							  <td width="210"> 
								<input type="text" name="LastName4" maxlength="30" size="24" value="<%= contacts[2].getLastName() %>" onchange="setChanged()">
							  </td>
							</tr>
							<tr> 
							  <td width="90" class="TableCell"> 
								<div align="right">First Name:</div>
							  </td>
							  <td width="210"> 
								<input type="text" name="FirstName4" maxlength="30" size="24" value="<%= contacts[2].getFirstName() %>" onchange="setChanged()">
							  </td>
							</tr>
							<tr> 
							  <td width="90" class="TableCell"> 
								<div align="right">Home #:</div>
							  </td>
							  <td width="210"> 
								<input type="text" name="HomePhone4" maxlength="14" size="14" value="<%= contacts[2].getHomePhone() %>" onchange="setChanged()">
							  </td>
							</tr>
							<tr> 
							  <td width="90" class="TableCell"> 
								<div align="right">Work #:</div>
							  </td>
							  <td width="210"> 
								<input type="text" name="WorkPhone4" maxlength="14" size="14" value="<%= contacts[2].getWorkPhone() %>" onchange="setChanged()">
							  </td>
							</tr>
							<tr>
							  <td width="90" class="TableCell">
								<div align="right">e-mail Address:</div>
							  </td>
							  <td width="210">
								<input type="text" name="Email4" maxlength="50" size="24" value="<%= contacts[2].getEmail().getNotification() %>" onChange="setChanged()">
							  </td>
							</tr>
                            </table></td>
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
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
