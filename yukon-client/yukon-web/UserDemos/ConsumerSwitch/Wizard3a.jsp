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
                <p><b><span class="Main">SIGN UP WIZARD - PRICE POINT OPTION
                </span></b></p>
                <table width="600" border="0">
                  <tr>
                    <td>
                      <div align="center"> 
                        <p class="Main">Your current Standard Rate $.75/kWh. To 
                          receive a rebate on this amount, <br>
                          select the Price/kWh amount you would like back for 
                          each of the following Price Point levels. <br>
                          Be sure that as the Price Point Level increases, your 
                          Price/kWh increases as well. 
                      </div>
                    </td>
                  </tr>
                </table>
                <table width="310" border="0" cellspacing="15" cellpadding="0">
                  <tr> 
                    <td width="280" background="PriceLow.gif" height="115"> <br>
                      <table width="250" border="0" align="center">
                        <tr> 
                          <td width="80"> 
                            <select name="select2">
                              <option>.10</option>
                              <option>.15</option>
                              <option selected>.20</option>
                              <option>.25</option>
                              <option>.30</option>
                              <option>.35</option>
                              <option>.40</option>
                              <option>.45</option>
                              <option>.50</option>
                            </select>
                          </td>
                          <td width="11"> 
                            <div align="right"> </div>
                          </td>
                          <td width="68"> 
                            <div align="center"> </div>
                          </td>
                          <td width="12">&nbsp;</td>
                          <td width="57"> 
                            <div align="center"> </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr> 
                    <td width="280" background="PriceMed.gif" height="115"> <br>
                      <table width="250" border="0" align="center">
                        <tr> 
                          <td width="80"> 
                            <select name="select">
                              <option>.10</option>
                              <option>.15</option>
                              <option>.20</option>
                              <option>.25</option>
                              <option selected>.30</option>
                              <option>.35</option>
                              <option>.40</option>
                              <option>.45</option>
                              <option>.50</option>
                            </select>
                          </td>
                          <td width="11"> 
                            <div align="right"> </div>
                          </td>
                          <td width="68"> 
                            <div align="center"> </div>
                          </td>
                          <td width="12">&nbsp;</td>
                          <td width="57"> 
                            <div align="center"> </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr> 
                    <td width="280" background="PriceHigh.gif" height="115"> <br>
                      <table width="250" border="0" align="center">
                        <tr> 
                          <td width="80"> 
                            <select name="select3">
                              <option>.10</option>
                              <option>.15</option>
                              <option>.20</option>
                              <option>.25</option>
                              <option>.30</option>
                              <option>.35</option>
                              <option selected>.40</option>
                              <option>.45</option>
                              <option>.50</option>
                            </select>
                          </td>
                          <td width="11"> 
                            <div align="right"> </div>
                          </td>
                          <td width="68"> 
                            <div align="center"> </div>
                          </td>
                          <td width="12">&nbsp;</td>
                          <td width="57"> 
                            <div align="center"> </div>
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
