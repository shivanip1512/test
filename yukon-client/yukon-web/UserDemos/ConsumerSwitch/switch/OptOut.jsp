<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../demostyle.css" type="text/css">
<script language="JavaScript">
<!--
function confirmSubmit(form) { //v1.0
  if (form.OptOutPeriod.value == 0) return false;
  return confirm('Are you sure you would like to temporarily opt out of all programs?');
}
//-->
</script>
</head>

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
                <td colspan="4" height="74" background="../../Header.gif">&nbsp;</td>
              </tr>
              <tr>
			    <td width="265" height="28">&nbsp;</td> 
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="Main"><a href="../../../login.jsp" class="Link3">Log 
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
		  <% String pageName = "OptOut.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <table width="600" border="0" cellspacing="0">
                <tr> 
                  <td width="202"> 
                    <table width="200" border="0" cellspacing="0" cellpadding="3">
                      <tr> 
                        <td><span class="Main"><b>Acct #<%= account.getAccountNumber() %></b></span><br>
                          <span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><br>
                          <!--<%= account.getCompany() %><br> -->
                          <%= propAddr.getStreetAddr1() %>, <%= propAddr.getStreetAddr2() %><br>
                          <%= propAddr.getCity() %>, <%= propAddr.getState() %> <%= propAddr.getZip() %><br>
                          <%= primContact.getHomePhone() %></span></td>
                      </tr>
                    </table>
                  </td>
                  <td width="187" valign="top"> 
                    <div align="center"><b><span class="Main">PROGRAMS - OPT OUT</span></b></div>
                  </td>
                  <td valign="top" width="205" align = "right"> <%@ include file="Notice.jsp" %>
                 
                  </td>
                </tr>
              </table>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td> 
                    <hr>
                  </td>
                </tr>
              </table>
              <b><span class="Main"><br>
              </span></b> 
              <table border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td width="304" valign="top" align = "center"> 
                    <div align="center"> 
                      <table width="200" border="1" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <form method="get" action="">
                            <td height="58" bgcolor="#CCCCCC"> 
                              <p align="center" class="TableCell"></p>
                              <table width="190" border="0" cellspacing="0" cellpadding="0" align="center">
                                <tr> 
                                  <td class="HeaderCell"> 
                                    <input type="checkbox" name="checkbox3" value="checkbox">
                                    I would like to be notified by <br>
                                    e-mail prior to control. My e-mail address 
                                    is:</td>
                                </tr>
                              </table>
                              <table width="190" border="0" cellspacing="0" cellpadding="2">
                                <tr> 
                                  <td> 
                                    <input type="text" name="textfield2" maxlength="8" size="14">
                                  </td>
                                  <td> 
                                    <input type="submit" name="Submit22" value="Submit">
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </form>
                        </tr>
                      </table>
                      <br>
                      <br>
                    </div>
                    <table width="200" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC" align="center">
                      <tr> 
                        <td> 
                          <div align="center"> 
                            <p class="HeaderCell">Temporarily opt out of all programs 
                            </p>
                          </div>
                          <table width="180" border="0" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <form method="post" action="OptForm.jsp" onsubmit="return confirmSubmit(this)">
                                <td width="180" align="center"> 
                                  <select name="OptOutPeriod">
									<option value="0">&lt;none&gt;</option>
									<option value="1">One Day</option>
									<option value="2">Two Days</option>
									<option value="3">Three Days</option>
									<option value="7">One Week</option>
									<option value="14">Two Weeks</option>
                                  </select>
                                </td>
                                <td width="180" align="center"> 
                                  <input type="submit" name="Submit" value="Submit" >
                                </td>
                              </form>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                    <p align="center" class="MainHeader"><b>Temporary Opt Out 
                      History </b> 
                    <table width="200" border="1" cellspacing="0" align="center" cellpadding="3">
                      <tr> 
                        <td class="HeaderCell">Date</td>
                        <td class="HeaderCell">Duration</td>
                      </tr>
<%
	CommonUtils.ProgramHistory[] progHist = (CommonUtils.ProgramHistory[]) user.getAttribute( CommonUtils.TRANSIENT_ATT_LEADING + "PROGRAM_HISTORY" );
	if (progHist == null) {
		progHist = CommonUtils.createProgramHistory( programs );
		user.setAttribute( CommonUtils.TRANSIENT_ATT_LEADING + "PROGRAM_HISTORY", progHist );
	}
	
	for (int i = progHist.length - 1; i >= 0; i--) {
		if (progHist[i].getDuration() == null) continue;
%>
                      <tr> 
                        <td class="TableCell"><%= dateFormat.format(progHist[i].getDate()) %></td>
                        <td class="TableCell"><%= progHist[i].getDuration() %></td>
                      </tr>
<%
	}
%>
                    </table>
                  </td>
                </tr>
              </table>
              <br>
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
