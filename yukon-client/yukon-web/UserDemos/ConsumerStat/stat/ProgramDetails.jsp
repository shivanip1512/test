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
              <p class="Main"> </p>
              
            </div>
              <table width="100%" border="0" cellpadding="3">
                    <tr>
                      
                <td align="center" valign="top"> 
                  <table width="300" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td width="107"> 
                              <table width="80" border="0" cellspacing="0" cellpadding="5">
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../AC.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../$$$.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../Half.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                              </table>
                            </td>
                            <td width="300" valign="top"0> 
                              
                          <p class="Main"><b>Cycle AC - Medium</b></p>
                          <p class="TableCell">When controlled, your air conditioning 
                            compressor will be interrupted for 15 minutes out 
                            of every half hour. Your furnace fan will keep running. 
                            You may notice a slight increase in your indoor air 
                            temperature.</p>
                            </td>
                          </tr>
                        </table>
                        <hr>
                        <table width="300" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td width="107" height="92"> 
                              <table width="80" border="0" cellspacing="0" cellpadding="5">
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../AC.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../$$.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../Third.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                              </table>
                            </td>
                            <td width="343" valign="top" height="92"> 
                              
                          <p class="Main"><b>Cycle AC - Light</b></p>
                          <p class="TableCell">When controlled, your air conditioning 
                            compressor will be interrupted for ten minutes out 
                            of every half hour. Your furnace fan will keep running. 
                            You may notice a slight increase in your indoor air 
                            temperature.</p>
                            </td>
                          </tr>
                        </table>
                        
                  </td>
                      
                  
                <td align="center" valign="top"> 
                  <table width="300" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                        <td width="107"> 
                          <table width="80" border="0" cellspacing="0" cellpadding="5">
                            <tr> 
                              <td> 
                                <div align="center"><img src="../Setback.gif" width="60" height="59"></div>
                              </td>
                            </tr>
                            <tr> 
                              <td> 
                                <div align="center"><img src="../$$$.gif" width="60" height="59"></div>
                              </td>
                            </tr>
                            <tr> 
                              <td> 
                                <div align="center"><img src="../Half.gif" width="60" height="59"></div>
                              </td>
                            </tr>
                          </table>
                        </td>
                        <td width="280" valign="top"> 
                          <p class="Main"><b>Setback - 4&deg;</b></p>
                          <p class="TableCell">When controlled, your air conditioning 
                            compressor will be set back four degrees. Your furnace 
                            fan will keep running. You may notice a slight increase 
                            in your indoor air temperature.</p>
                        </td>
                      </tr>
                    </table>
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                        <td width="107"> 
                          <table width="80" border="0" cellspacing="0" cellpadding="5">
                            <tr> 
                              <td> 
                                <div align="center"><img src="../Setback.gif" width="60" height="59"></div>
                              </td>
                            </tr>
                            <tr> 
                              <td> 
                                <div align="center"><img src="../$$.gif" width="60" height="59"></div>
                              </td>
                            </tr>
                            <tr> 
                              <td> 
                                
                              <div align="center"><img src="../Quarter.gif" width="60" height="59"></div>
                              </td>
                            </tr>
                          </table>
                        </td>
                        <td width="280" valign="top"> 
                          <p class="Main"><b>Setback 
                            - 2&deg;</b></p>
                          <p class="TableCell">When 
                            controlled, your air conditioning compressor will 
                            be set back two degrees. Your furnace fan will keep 
                            running. You may notice a slight increase in your 
                            indoor air temperature.</p>
                        </td>
                      </tr>
                    </table>
                    
                  </td>
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
