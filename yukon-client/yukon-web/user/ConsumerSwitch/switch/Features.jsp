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
			<% String pageName = "Features.jsp"; %>
            <%@ include file="Nav.jsp" %>  
          </td>
          <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              
            <div align="center">
<table width="100%" border="0" cellpadding="10">
                  <tr> 
                    <td width="56%"> 
                      
                    <p align="center" class="Main"><b>Welcome Edward Holland</b><br>
                        You are enrolled in the following programs:</p>
                      <div align="center"> 
                        <table width="300" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td width="90"> 
                              <div align="center"><img src="../AC.gif" width="60" height="59"></div>
                            </td>
                            <td width="89"> 
                              <div align="center"><img src="../WaterHeater.gif" width="60" height="59"></div>
                            </td>
                            <td width="90"> 
                              <div align="center"><img src="../Pool.gif" width="60" height="59"></div>
                            </td>
                          </tr>
                        </table>
                        <table width="300" border="0" cellspacing="0" cellpadding="3">
                          <tr>
						  <form method="post" action="Programs.jsp">  
                            <td> 
                              <div align="center"> 
                                <input type="submit" name="" value="Program Details">
                              </div>
                            </td>
						</form>	
                          </tr>
                        </table>
                      </div>
                      <p align="left">&nbsp;</p>
                    </td>
                    <td width="44%"> 
                      <table width="200" border="1" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <form method="post" action="Features.jsp">
						  <td height="58" bgcolor="#CCCCCC"> 
                            <p align="center" class="TableCell"><i>Today's odds 
                              for control?</i><br>
                              <b>Unlikely<br>
                              </b> <br>
                              Last updated 08:00 AM</p>
                            <table width="190" border="0" cellspacing="0" cellpadding="0" align="center">
                              <tr> 
                                <td class="TableCell"> 
                                  <input type="checkbox" name="checkbox" value="checkbox">
                                  I would like to be notified by <br>
                                  e-mail prior to control. My e-mail address is:</td>
                              </tr>
                            </table>
                            <table width="190" border="0" cellspacing="0" cellpadding="2">
                              <tr> 
                                <td width="141"> 
                                  <input type="text" name="textfield" maxlength="8">
                                </td>
								
                                <td width="49"> 
                                  <input type="submit" name="Submit2" value="Submit">
                                </td>
								
                              </tr>
                            </table>
                          </td>
						  </form>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <table width="600" border="1" cellpadding="3" cellspacing="0">
                  <tr bgcolor="#CCCCCC"> 
                    <td align="center" width="184"> <span class="HeaderCell">HISTORY</span> 
                    </td>
                    <td align="center" width="183"> <span class="HeaderCell">OPT 
                      OUT</span></td>
                    <td align="center" width="199"> <span class="HeaderCell">CHANGE 
                      PROGRAM</span></td>
                  </tr>
                  <tr> 
                    <td valign="top" align="center" width="184"> 
                      <p align="center" class="TableCell">Select 
                        the historical time period you would like to see, then 
                        click View.</p>
						<form method="post" action="History.jsp">
                      <table width="180" border="0" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <td width="180" valign="top" align="center"> 
                            <select name="select17">
                              <option>Past Day</option>
                              <option>Past Week</option>
                              <option>Past Month </option>
                            </select>
                          </td>
                        </tr>
                        <tr>
					      
                          <td width="180" valign="top" align="center"> 
                            <input type="submit" name="" value="View">
                          </td>
						 
                        </tr>
                      </table>
					  </form> 
                    <table width="180" border="0" cellspacing="0" cellpadding="3" align="center">
                      <tr> 
                        <form method="post" action="Summary.jsp">
                          <td width="180" valign="top" height="67" align="center"> 
                            <input type="submit" name="Input" value="Summary">
                          </td>
                        </form>
                      </tr>
                    </table>
                  </td>
                    <td valign="top" align="center" width="183"> 
                      
                    <p align="center" class="TableCell">Select the time period 
                      you would like to opt out of, then click Submit.</p>
<form method="post" action="OptForm.jsp"> 
                        <table width="180" border="0" cellspacing="0" cellpadding="3">
                          <tr> 
                            <td width="180" align="center"> 
                              <select name="select7">
                                <option>&lt;none&gt;</option>
                                <option>One Day</option>
                                <option>Two Days</option>
                                <option>Three Days</option>
                                <option>One Week</option>
                                <option>Two Weeks</option>
                                <option>Permanently</option>
                              </select>
                            </td>
                          </tr>
                          <tr>
                            <td width="180" align="center"> 
                              <input type="submit" name="" value="Submit">
                            </td>	
                          </tr>
                        </table>
						</form>
                    </td>
                    <td valign="top" align="center" width="199"> 
                      <p align="center" class="TableCell">Select 
                        a new program, then click Submit.</p>
                      <table width="180" border="0" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <td width="107"><img src="../AC.gif" width="60" height="59"></td>
                          <td width="102"></td>
                        </tr>
                        <tr>
						<form method="post" action="Features.jsp"> 
                          <td width="107"> 
                            <select name="select15">
                              <option>Select One</option>
                              <option>Cycle AC Med</option>
                              <option>Cycle AC Lgt</option>
                            </select>
                          </td>
						   
                          <td width="102" align="right"> 
                            <input type="submit" name="Submit3" value="Submit">
                          </td>
						  </form>
                        </tr>
                        <tr> 
                          <td width="107"><img src="../WaterHeater.gif" width="60" height="59"></td>
                          <td width="102">&nbsp;</td>
                        </tr>
                        <tr>
						<form method="post" action="Features.jsp">
                          <td width="107"> 
                            <select name="select">
                              <option>Select One</option>
                              <option>8 Hours</option>
                              <option>4 Hours</option>
                            </select>
                          </td>
                          <td width="102" align="right"> 
                            <input type="submit" name="Submit3" value="Submit">
                          </td>
						</form>  
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <p align="center" class="Main">&nbsp;</p>
                </div>
			
          </td>
		  
        <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
    </tr>
      </table>
	  
    </td>
	</tr>
</table>
<br>
</body>
</html>
