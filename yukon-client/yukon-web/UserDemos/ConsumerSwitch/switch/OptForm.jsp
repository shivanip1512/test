<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../demostyle.css" type="text/css">
<script language="JavaScript">
<!--
function MM_popupMsg(msg) { //v1.0
  alert(msg);
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
                  <div align="left"></div>
                </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
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
			<% String pageName = "Features.jsp"; %>
            <%@ include file="Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              <div align="center"><br>
              <table width="600" border="0" cellspacing="0">
                <tr> 
                  <td width="202"> 
                    <table width="200" border="0" cellspacing="0" cellpadding="3">
                      <tr> 
                        <td><span class="Main"><b>Acct #<%= account.getAccountNumber() %></b></span><br>
                          <span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><br>
                          <%= account.getCompany() %><br>
                          <%= propAddr.getStreetAddr1() %>, <%= propAddr.getStreetAddr2() %><br>
                          <%= propAddr.getCity() %>, <%= propAddr.getState() %> <%= propAddr.getZip() %><br>
                          <%= primContact.getHomePhone() %></span></td>
                      </tr>
                    </table>
                  </td>
                  <td width="187" valign="top"> 
                    <div align="center"><b><span class="Main">OPT OUT OR CHANGE 
                      PROGRAM </span></b></div>
                  </td>
                  <td valign="top" width="205"> 
                    <form name="form2" method="post" action="../../../login.jsp">
                      <div align="right"> 
                        <input type="submit" name="Log Off" value="Log Off">
                      </div>
                    </form>
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
              <br>
                <div align="center"> 
                  
                <p class="Main">Please complete the following form to opt out 
                  of or change your program:</p>
                  <hr>
				  <form method="post" action="Features.jsp"> 
                  <table width="500" border="0" cellspacing="0" cellpadding="3" valign="top">
                    <tr> 
                      <td class="TableCell"> 
                        <p>Reason for opting out of or changing your program:</p>
                      </td>
                    </tr>
                    <tr> 
                      <td> 
                        <div align="left"> 
                          <input type="text" name="textfield4" size="80">
                        </div>
                      </td>
                    </tr>
                    <tr> 
                      <td class="TableCell"> Satisfaction with the program:</td>
                    </tr>
                    <tr> 
                      <td> 
                        <div align="left"> 
                          <input type="text" name="textfield42" size="80">
                        </div>
                      </td>
                    </tr>
                    <tr> 
                      <td class="TableCell"> Would you be interested in other 
                        programs. If so, which ones:</td>
                    </tr>
                    <tr> 
                      <td> 
                        <div align="left"> 
                          <input type="text" name="textfield43" size="80">
                        </div>
                      </td>
                    </tr>
                    <tr> 
                      <td class="TableCell"> Comments:</td>
                    </tr>
                    <tr> 
                      <td> 
                        <div align="left"> 
                          <input type="text" name="textfield44" size="80">
                        </div>
                      </td>
                    </tr>
                  </table>
                  <p align="center"> 
                    <input type="submit" name="Submit" value="Submit" onClick="MM_popupMsg('You will not receive a discount for this program during this time.  Are you sure you want to opt out of or change this program?')">
                    </form>
					<form method="post" action="Features.jsp"> 
					<input type="submit" name="" value="Cancel">
					</form>
                    <br>
                </div>
                </div>
			
          </td>
		  
		  
        <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
    </tr>
      </table>
	  
    </td>
	</tr>
</table>
<p align="center" class="TableCell2">&nbsp;</p>
</body>
</html>
