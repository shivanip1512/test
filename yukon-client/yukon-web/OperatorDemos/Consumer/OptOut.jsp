<%@ include file="StarsHeader.jsp" %>
<%
	String programStatus = "In Service";     
		programStatus = (String) session.getAttribute("PROGRAM_STATUS");
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
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
		  <% String pageName = "OptOut.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "PROGRAMS - OPT OUT"; %>
              <%@ include file="InfoSearchBar.jsp" %>
             <div align="center">
                <p class="TableCell">This customer would like to be notified of 
                  control by<br>
                  e-mail at info@cannontech.com</p>
              </div>
              <table width="200" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC" align="center">
                <tr> 
                  <td> 
                    <div align="center"> 
                      <p class="HeaderCell">Temporarily opt out of all programs 
                      </p>
                    </div>
				  <form name="form1" method="post" action="OptForm.jsp" onsubmit = "return confirmSubmit(this)">
				  	<input type="hidden" name="action" value="DisableService">
                    <table width="180" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
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
                         
                            <input type="submit" name="Submit" value="Submit">
                          
                        </td>
                      </tr>
                    </table>
				  </form>
                  </td>
                </tr>
              </table>
              <br>
              <p align="center" class="MainHeader"><b>Temporary Opt Out History 
                </b> 
              <table width="200" border="1" cellspacing="0" align="center" cellpadding="3">
                <tr> 
                  <td class="HeaderCell">Date</td>
                  <td class="HeaderCell">Duration</td>
                </tr>
                <tr> 
                  <td class="TableCell">12/15/00</td>
                  <td class="TableCell">3 Days</td>
                </tr>
                <tr> 
                  <td class="TableCell">07/17/01</td>
                  <td class="TableCell">1 Day</td>
                </tr>
              </table>
              <br>
              <table width="150" border="0" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td align="center">
				  <form method="POST" action="/servlet/SOAPClient">
				    <input type="hidden" name="action" value="EnableService"> 
                    <input type="submit" name="Re-enable" value="Re-enable">
				  </form>
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
