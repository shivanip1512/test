<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="CssLink" rel="stylesheet" href="../../demostyle.css" type="text/css">
<% if (ecWebSettings.getURL().length() > 0) { %>
	<script language="JavaScript">document.getElementById("CssLink").href = "../../<%= ecWebSettings.getURL() %>";</script>
<% } %>
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
                <td id="Header" colspan="4" height="74" background="../../Header.gif">&nbsp;</td>
<% if (ecWebSettings.getLogoLocation().length() > 0) { %>
	<script language="JavaScript">document.getElementById("Header").background = "../../<%= ecWebSettings.getLogoLocation() %>";</script>
<% } %>
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
		  <% String pageName = "General.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <table width="235" border="1" cellspacing="0" cellpadding="3" align="center">
              </table>
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td width="429" valign="top"> <table width="400" border="0" cellspacing="3" cellpadding="0">
                      <tr> 
                        <td valign="bottom" class="Main">
<div align="center"><strong><br>
                            WELCOME TO CONSUMER ENERGY SERVICES! </strong> <br>
                            <br>
                            <br>
                            </div></td>
                      </tr>
                      <tr> 
                        <td><table width="400" border="0" cellspacing="0" cellpadding="5">
                            <tr> 
                              <td valign="top">
								<p class="Main"><%= ecWebSettings.getDescription() %></p></td>
                              <td valign="top"> 
                                <table width="200" border="1" cellspacing="0" cellpadding="3" align="center">
                                  <tr bgcolor="#CCCCCC" class="Main"> 
                                    <td width="139"> <div align="center"> 
                                        <p class="TableCell3">Your Enrolled Programs</p>
                                      </div></td>
                                    <td width="134"> <div align="center" class="TableCell3">Control 
                                        Since Midnight</div></td>
                                  </tr>
<%
	for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
		StarsLMProgram program = programs.getStarsLMProgram(i);
		StarsApplianceCategory category = null;
		StarsLMControlHistory todayCtrlHist = ServletUtils.getTodaysControlHistory( program.getStarsLMControlHistory() );
		
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory appCat = categories.getStarsApplianceCategory(j);
			if (appCat.getApplianceCategoryID() == program.getApplianceCategoryID()) {
				category = appCat;
				break;
			}
		}
%>
                                  <tr> 
                                    <td width="139"> <div align="center"> <span class="TableCell"> 
                                        <img src="../<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" height="59"><br>
                                        <%= program.getProgramName() %></span> 
                                        
                                      </div></td>
                                    <td width="134" class="Main"> 
                                      <div align="center"> 
                                        <%
		if (program.getStatus().equalsIgnoreCase(ServletUtils.OUT_OF_SERVICE)) {
%>
                                        <b>Out of Service</b>
<%
		}
		else if (todayCtrlHist.getBeingControlled()) {
%>
                                        <b>Currently<br>
                                        controlling</b> 
<%
		}
		else if (todayCtrlHist.getControlHistoryCount() > 0) {
%>
                                        <b>You have<br>
                                        been controlled</b> 
                                        <%
		}
		else {
%>
                                        <b>You have not<br>
                                        been controlled</b> 
                                        <%
		}
%>
                                      </div></td>
                                </tr>
<%
	}
%>
							</table></td>
                            </tr>
                          </table></td>
                      </tr>
                    </table></td>
                  <td width="171"><span class="Main"><b>Acct #<%= account.getAccountNumber() %></b></span><br> 
                    <span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><br>
                    <!--<%= account.getCompany() %><br> -->
                    <%= propAddr.getStreetAddr1() %>, <%= propAddr.getStreetAddr2() %><br>
                    <%= propAddr.getCity() %>, <%= propAddr.getState() %> <%= propAddr.getZip() %><br>
                    <%= primContact.getHomePhone() %></span><br> <br> <img src="Family.jpg" width="168" height="224"> 
                  </td>
                </tr>
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
