<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%
	StarsGetEnrollmentProgramsResponse categories = (StarsGetEnrollmentProgramsResponse) session.getAttribute("ENROLLMENT_PROGRAMS");
	if (categories == null)
		response.sendRedirect("/UserDemos/ConsumerSwitch/login.jsp");
%>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<SCRIPT LANGUAGE="JavaScript">

</script>
</head>
<body bgcolor="#666699" leftmargin="0" topmargin="0">
<div id = "tool" class = "tooltip"> 
   </div>
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr bgcolor="#FFFFFF"> 
          <td width="102" height="102" background="Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr bgcolor="666699"> 
                <td width="265" height="28">&nbsp;&nbsp;&nbsp;</td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="Main"><a href="login.jsp" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bgcolor="#666699" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
		    <form method="post" action="Wizard4.jsp">
			<input type="hidden" name="AcctNo" value="<%= request.getParameter("AcctNo") %>">
            <div align="center"><br> 
                <p><b><span class="Main">SIGN UP WIZARD - PRICE POINT OPTION<br>
                </span></b></p>
                <table width="600" border="0">
                  <tr>
                    <td>
                      <div align="center"><font face="Arial, Helvetica, sans-serif" size="2">Select 
                        the Price/kWh for each Price Point level. Your Estimated 
                        Annual Rebate is shown at right. Be sure that as the Price 
                        Point increases, your Price/kWh increases as well.</font> 
                      </div>
                    </td>
                  </tr>
                </table>
                <br>
                <table width="540" border="0" cellspacing="15" bgcolor="#666699" cellpadding="0">
                  <tr> 
                    <td width="251" background="PriceStand.gif" height="113"> 
                      <table width="220" border="0" align="center">
                        <tr> 
                          <td> 
                            <select name="select2">
                              <option>None</option>
                            </select>
                          </td>
                          <td> 
                            <div align="right"> 
                              <input type="text" name="textfield232" size="4" maxlength="6" value="$0.00">
                            </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td width="250" background="PriceLow.gif" height="113"> 
                      <table width="220" border="0" align="center">
                        <tr> 
                          <td> 
                            <select name="select3">
                              <option>None</option>
							  <option>10</option>
                              <option>15</option>
                              <option>20</option>
                              <option>25</option>
                              <option>30</option>
                              <option>35</option>
                              <option>40</option>
                              <option>45</option>
                              <option>50</option>
                              <option>None</option>
                            </select>
                          </td>
                          <td> 
                            <div align="right"> 
                              <input type="text" name="textfield23" size="4" maxlength="6" value="$15.00">
                            </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr> 
                    <td width="251" background="PriceMed.gif" height="113"> 
                      <table width="220" border="0" align="center">
                        <tr> 
                          <td> 
                            <select name="select">
                              <option>None</option>
                              <option>10</option>
                              <option>15</option>
                              <option>20</option>
                              <option>25</option>
                              <option>30</option>
                              <option>35</option>
                              <option>40</option>
                              <option>45</option>
                              <option>50</option>
                              </select>
                          </td>
                          <td> 
                            <div align="right"> 
                              <input type="text" name="textfield22" size="4" maxlength="6" value="$20.00">
                            </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td width="250" background="PriceHigh.gif"> 
                      <table width="220" border="0" align="center">
                        <tr> 
                          <td> 
                            <select name="select4">
                              <option>None</option>
                              <option>10</option>
                              <option>15</option>
                              <option>20</option>
                              <option>25</option>
                              <option>30</option>
                              <option>35</option>
                              <option>40</option>
                              <option>45</option>
                              <option>50</option>
                              <option>None</option>
                            </select>
                          </td>
                          <td> 
                            <div align="right"> 
                              <input type="text" name="textfield24" size="4" maxlength="6" value="$25.00">
                            </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                </div>
            <table width="150" border="0" cellspacing="0" cellpadding="3" align="center">
              <tr> 
                  <td width="95" valign="top" align="center"> 
                    <input type="submit" name="Submit" value="  Next  ">
                  </td>
                  <td width="98" valign="top" align="center"> 
                    <input type = "button" value="Cancel" name = "cancel" onclick = "history.back()">
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
