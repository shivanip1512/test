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
                  <td valign="top" width="205"> 
                   
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
                              
                          <p><b><span class="Main">Cycle AC - Medium</span></b></p>
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
                              
                          <p><b class="Main">Cycle AC - Light</b></p>
                          <p class="TableCell">When controlled, your air conditioning 
                            compressor will be interrupted for ten minutes out 
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
                                    <div align="center"><img src="../WaterHeater.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../$$.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../Sixth.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                              </table>
                            </td>
                            <td width="343" valign="top" height="92"> 
                              
                          <p><b><span class="Main">Water Heater - 4 Hours</span></b></p>
                          <p class="TableCell">When controlled, power to your 
                            water heater&#146;s heating elements is turned off 
                            for up to four hours. The hot water in the tank will 
                            still be available for you use.</p>
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
                                    <div align="center"><img src="../WaterHeater.gif" width="60" height="59"></div>
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
                            <td width="343" valign="top"> 
                              
                          <p><b><span class="Main">Water Heater - Electric Thermal 
                            Storage</span></b></p>
                          <p class="TableCell">Your Electric Thermal Storage water 
                            heater&#146;s heating elements are interrupted on 
                            a daily 12-hour on, 12-hour off cycle. The hot water 
                            stored in the tank will supply your hot water needs.</p>
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
                                    <div align="center"><img src="../Electric.gif" width="60" height="59"></div>
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
                            <td width="343" valign="top"> 
                              
                          <p><b class="Main">Electric Heat - ETS</b></p>
                          <p class="TableCell">Your Electric Thermal Storage heating 
                            system&#146;s heating elements are interrupted on 
                            a daily 12-hour on, 12-hour off cycle. The heat stored 
                            will supply your home needs.</p>
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
                                    <div align="center"><img src="../DualFuel.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../$$.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../Sixth.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                              </table>
                            </td>
                            <td width="343" valign="top"> 
                              
                          <p><b><span class="Main">Dual Fuel Space Heater - Limited 
                            4 Hour</span></b></p>
                          <p class="TableCell">When controlled, electric power 
                            to your home&#146;s heating system will be switched 
                            off, and your non-electric heat source will provide 
                            for your home&#146;s heating needs. Control is limited 
                            to four hours consecutively. </p>
                            </td>
                          </tr>
                        </table>
                        <hr>
                        <table width="300" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td width="107"0> 
                              <table width="80" border="0" cellspacing="0" cellpadding="5">
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../DualFuel.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../$$$.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../Third.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                              </table>
                            </td>
                            <td width="300" valign="top"0> 
                              
                          <p><b class="Main">Dual Fuel Space Heater - Unlimited 
                            </b></p>
                              
                          <p class="TableCell">When controlled, electric power 
                            to your home&#146;s heating system will be switched 
                            off, and your non-electric heat source will provide 
                            for your home&#146;s heating needs. While usually 
                            limited to a few hours, control could be for an extended 
                            period. </p>
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
                                    <div align="center"><img src="../Pool.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../$.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                                <tr> 
                                  <td> 
                                    <div align="center"><img src="../Sixth.gif" width="60" height="59"></div>
                                  </td>
                                </tr>
                              </table>
                            </td>
                            <td width="343" valign="top"> 
                              
                          <p><b class="Main">Pool Pump </b></p>
                              
                          <p class="TableCell">When controlled, power to your 
                            pool pump is interrupted. Interruptions usually last 
                            for 4 hours or less.<br>
                              </p>
                            </td>
                          </tr>
                        </table>
                        
                      </td>
                    </tr>
                  </table>
			<div align="center"> 
              <form method="post" action="Enrollment.jsp">
                <input type="submit" name="Back" value="Back">
              </form>
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
