<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../demostyle.css" type="text/css">
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
            <% String pageName = "Programs.jsp"; %>
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
                          <!--<%= account.getCompany() %><br> -->
                          <%= propAddr.getStreetAddr1() %>, <%= propAddr.getStreetAddr2() %><br>
                          <%= propAddr.getCity() %>, <%= propAddr.getState() %> <%= propAddr.getZip() %><br>
                          <%= primContact.getHomePhone() %></span></td>
                      </tr>
                    </table>
                  </td>
                  <td width="187" valign="top"> 
                    <div align="center"><b><span class="Main">PROGRAM DETAILS</span></b></div>
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
              </div>
              
            <div align="center">
              <p class="Main"> </p>
            </div>
            <table width="95%" border="1" class = "TableCell" align = "center" height="28" cellspacing = "0" cellpadding = "4">
              <tr> 
                <td width="17%"><b>The following symbols represent:</b></td>
                <td width="8%"><img src="../../$$Sm.gif" ></td>
                <td width="21%">Savings: More dollar signs means more savings!</td>
                <td width="8%"><img src="../../ThirdSm.gif"></td>
                <td width="13%" valign="top">Percent of Control</td>
                <td width="8%"><img src="../../Tree2Sm.gif"></td>
                <td width="25%" valign="top">Environment: More trees means mean 
                  a healthier environment.</td>
              </tr>
            </table>
            <br> <b><span class="Main"> </span></b> <table width="100%" border="0" cellpadding="20" >
              <tr> 
                <td valign="top"> <table width="280" border="0">
                    <tr> 
                      <td class = "TableCell"><b>Cycle AC - Medium</b></td>
                    </tr>
                    <tr> 
                      <td class = "TableCell" valign = "top">When controlled, 
                        your air conditioning compressor will be interrupted for 
                        15 minutes out of every half hour. Your furnace fan will 
                        keep running. You may notice a slight increase in your 
                        indoor air temperature.<br> <table width="210" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td><img src="../../ACSm.gif"></td>
                            <td><img src="../../$$$Sm.gif" ></td>
                            <td><img src="../../HalfSm.gif"></td>
                            <td><img src="../../Tree3Sm.gif"></td>
                          </tr>
                        </table></td>
                    </tr>
                  </table>
                  <hr> <table width="280" border="0">
                    <tr> 
                      <td class = "TableCell"><b>Cycle AC - Light</b></td>
                    </tr>
                    <tr> 
                      <td class = "TableCell" valign = "top">When controlled, 
                        your air conditioning compressor will be interrupted for 
                        ten minutes out of every half hour. Your furnace fan will 
                        keep running. You may notice a slight increase in your 
                        indoor air temperature.<br> <table width="210" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td><img src="../../ACSm.gif"></td>
                            <td><img src="../../$$Sm.gif" ></td>
                            <td><img src="../../ThirdSm.gif"></td>
                            <td><img src="../../Tree2Sm.gif"></td>
                          </tr>
                        </table></td>
                    </tr>
                  </table></td>
                <td valign="top"> <table width="280" border="0">
                    <tr> 
                      <td class = "TableCell"><b>Setback - 4&deg</b></td>
                    </tr>
                    <tr> 
                      <td class = "TableCell" valign = "top">When controlled, 
                        your air conditioning compressor will be set back four 
                        degrees. Your furnace fan will keep running. You may notice 
                        a slight increase in your indoor air temperature.<br> 
                        <table width="210" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td><img src="../../SetbackSm.gif"></td>
                            <td><img src="../../$$$Sm.gif" ></td>
                            <td><img src="../../HalfSm.gif"></td>
                            <td><img src="../../Tree3Sm.gif"></td>
                          </tr>
                        </table></td>
                    </tr>
                  </table>
                  <hr> <table width="280" border="0">
                    <tr> 
                      <td class = "TableCell"><b>Setback - 2&deg</b></td>
                    </tr>
                    <tr> 
                      <td class = "TableCell" valign = "top">When controlled, 
                        your air conditioning compressor will be set back two 
                        degrees. Your furnace fan will keep running. You may notice 
                        a slight increase in your indoor air temperature.<br> 
                        <table width="210" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td><img src="../../SetbackSm.gif"></td>
                            <td><img src="../../$Sm.gif" ></td>
                            <td><img src="../../QuarterSm.gif"></td>
                            <td><img src="../../Tree1Sm.gif"></td>
                          </tr>
                        </table></td>
                    </tr>
                  </table></td>
              </tr>
            </table>
            <div align="center"> 
              <form method="post" action="Enrollment.jsp">
                <input type="submit" name="Back" value="Back">
                <br>
                <br>
              </form>
            </div>
          </td>
		  
		  
        <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
    </tr>
      </table>
	  
    </td>
	</tr>
</table>
<div align="center"></div>
</body>
</html>
