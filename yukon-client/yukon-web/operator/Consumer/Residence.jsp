<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">

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
		  <% String pageName = "Update.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><% String header = "ACCOUNT - CUSTOMER RESIDENCE"; %><%@ include file="InfoSearchBar.jsp" %>
			<% if (errorMsg != null) out.write("<br><span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %></div>
			
			<form method="POST" action="/servlet/SOAPClient">
			<input type="hidden" name="action" value="UpdateCustAccount">
            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
              <tr> 
                  <td width="300" valign="top" bgcolor="#FFFFFF"> 
                    <hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Residence Type:</div>
                        </td>
                        <td width="184" valign="top"> 
                          <select name="select">
                            <option>Home</option>
                            <option>Duplex</option>
                            <option>Apartment</option>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Construction Material: </div>
                        </td>
                        <td width="184"> 
                          <select name="select2">
                            <option>Wood</option>
                            <option>Brick</option>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Decade Built:</div>
                        </td>
                        <td width="184"> 
                          <select name="select3">
                            <option>1920</option>
                            <option>1930</option>
                            <option>1940</option>
                            <option>1950</option>
                            <option>1960</option>
                            <option>1970</option>
                            <option>1980</option>
                            <option>1990</option>
                            <option>2000</option>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Square Feet:</div>
                        </td>
                        <td width="184"> 
                          <select name="select8">
                            <option>&lt;- 1000</option>
                            <option>1000 - 1500</option>
                            <option>1500 - 2000</option>
                            <option>2000 - 2500</option>
                            <option>2500 - 3000</option>
                            <option>3500 - 4000</option>
                            <option>4000+</option>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Insulation Depth:</div>
                        </td>
                        <td width="184"> 
                          <select name="select9">
                            <option>1 - 2</option>
                            <option>3 - 6</option>
                            <option>6+</option>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">General Condition:</div>
                        </td>
                        <td width="184"> 
                          <select name="select10">
                            <option>Poor</option>
                            <option>Fair</option>
                            <option>Good</option>
                            <option>Excelent</option>
                          </select>
                        </td>
                      </tr>
                    </table>
                    </td>
                  <td width="300" valign="top" bgcolor="#FFFFFF">
<hr>
                    <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Main Cooling System:</div>
                        </td>
                        <td width="184"> 
                          <select name="select4">
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Main Heating System:</div>
                        </td>
                        <td width="184"> 
                          <select name="select5">
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right"># of Occupants:</div>
                        </td>
                        <td width="184"> 
                          <select name="select11">
                            <option>1 - 2</option>
                            <option>3 - 4</option>
                            <option>5 - 6</option>
                            <option>7 - 8</option>
                            <option>9+</option>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Ownership Type:</div>
                        </td>
                        <td width="184"> 
                          <select name="select6">
                            <option>Own</option>
                            <option>Rent</option>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell"> 
                          <div align="right">Main Fuel Type:</div>
                        </td>
                        <td width="184"> 
                          <select name="select7">
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="112" class="TableCell" valign="top"> 
                          <div align="right">Notes:</div>
                        </td>
                        <td width="184"> 
                          <textarea name="PropNotes" rows="6" wrap="soft" cols="26" class = "TableCell"><%= account.getPropertyNotes() %></textarea>
                        </td>
                      </tr>
                    </table>
                    </td>
              </tr>
            </table>
            <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
              <tr> 
                  <td width="186"> 
                    <div align="right"> 
                      <input type="submit" name="Save2" value="Save">
                    </div>
                  </td>
                  <td width="194"> 
                    <div align="left"> 
                      <input type="reset" name="Cancel2" value="Cancel">
                    </div>
                  </td>
              </tr>
            </table>
			</form>
            <p align="center">&nbsp;</p>
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
