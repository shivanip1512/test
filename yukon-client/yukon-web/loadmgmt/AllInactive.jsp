<!-- JavaScript needed for jump menu--->
<%@ include file="Functions.js" %>


<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../WebConfig/CannonStyle.css" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="images/LoadImage.jpg">&nbsp;</td>
		  <td width="1" bgcolor="#000000"><img src="images/VerticalRule.jpg" width="1"></td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="images/Header.gif">&nbsp;</td>
              </tr>
              <tr bgcolor="#666699"> 
                <td width="353" height = "28" class="Header3">&nbsp;&nbsp;&nbsp;<span class="PageHeader"> 
                  Load Management</span></td>
                <td width="235" valign="middle">&nbsp;</td>
                <td width="58" valign="middle" class="MainText"> 
                  <div align="center"><span><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                </td>
                <td width="57" valign="middle" class="MainText"> 
                  <div align="left"><span ><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr>
          <td width="759" bgcolor="#000000" valign="top"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td width="759" valign="top" bgcolor="#FFFFFF" height="261"> 
            <table width="740" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>
                <td valign="bottom"> 
                  <form >
                    <div align="left"><span class="MainText"><br>
                      Control Area State:</span> 
                      <select name="select" onchange="location=this.options[this.selectedIndex].value">
                        <option value="AllControlAreas.jsp">All Areas</option>
                        <option value="AllActive.jsp">Active</option>
                        <option value="AllInactive.jsp" selected>Inactive</option>
                      </select>
                    </div>
                  </form>
				</td>
              </tr>
            </table>
            <table width="740" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>
                <td width="740" valign="top" class="MainText" height="187"> 
                  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="740" border="0" cellspacing="0" cellpadding="0">
                          <tr class="HeaderCell"> 
                              
                            <td width="409"><span class="HeaderCell">&nbsp;&nbsp; 
                              Active Control Areas </span></td>

                              <td width="191"> 
                                <div align="right"> </div>
                              </td>
                            </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                  <table width="744" border="1" align="center" cellpadding="2" cellspacing="0">
                    <tr valign="top" class="HeaderCell"> 
                      <td width="111"> 
                        <div align="center">Control Area</div>
                      </td>
                      <td width="47"> 
                        <div align="center">State</div>
                      </td>
                      <td width="58"> 
                        <div align="center">Target</div>
                      </td>
                      <td width="86"> 
                        <div align="center">Current<br>
                          /Projected</div>
                      </td>
                      <td width="84"> 
                        <div align="center">Date/Time</div>
                      </td>
                      <td width="75"> 
                        <div align="center">Valid Time Window</div>
                      </td>
                      <td width="47"> 
                        <div align="center">Priority</div>
                      </td>
                      <td width="97"> 
                        <div align="center">Graphs</div>
                      </td>
                      <td width="83"> 
                        <div align="center">Reports</div>
                      </td>
                    </tr>
                    <tr valign="top"> 
                      <td width="111" class="TableCell">Western Substation</td>
                      <td width="47" class="TableCell">Inactive</td>
                      <td width="58" class="TableCell"> 33,277</td>
                      <td width="86" class="TableCell">32,588 / 33,100</td>
                      <td width="84" class="TableCell">08-13-03 11:01</td>
                      <td width="75" class="TableCell">N/A</td>
                      <td width="47" class="TableCell">1</td>
                      <td width="97" class="TableCell"> 
                        <select name="select" onChange="location = this.options[this.selectedIndex].value;">
                          <option value="AllControlAreas.jsp">Today's Load</option>
                          <option value="AllControlAreas.jsp"></option>
                        </select>
                      </td>
                      <td width="83" class="TableCell"> 
                        <select name="select4">
                          <option>Summary</option>
                          <option></option>
                        </select>
                      </td>
                    </tr>
                  </table>
                  <br>
              </tr>
</table>

          </td>
          <td width="1" bgcolor="#000000" height="261"><img src="images/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>



</body>
</html>
