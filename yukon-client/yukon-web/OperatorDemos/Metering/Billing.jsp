<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<SCRIPT  LANGUAGE="JavaScript1.2" SRC="Calendar1-82.js"></SCRIPT>

<SCRIPT  LANGUAGE="JavaScript1.2">

function Today() {
	var dd = new Date();
	return((dd.getMonth()+1) + "/" + dd.getDate() + "/" + dd.getFullYear());
}

function init() {
	var today = Today();
	document.MForm.billDate.value = today;
}

</SCRIPT>

</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0" onload = "init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="MeterImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="Header3" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Commercial Metering&nbsp;&nbsp;</td>
                  <td width="253" valign="middle"><img src="SearchButton.gif" width="51" height="14"></td>
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
            <p>&nbsp;</p>
            <p align="center">&nbsp;</p>
          </td>
          <td width="1" bgcolor="#000000"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <br>
                <span class="Main"><b>BILLING</b></span>
          
              
            </div><form name = "MForm">
              <table width="51%" border="0" cellspacing="0" cellpadding="4" height="209" align = "center">
                <tr> 
                  <td height="162" colspan = "3"> 
                    <table width="100%" border="1" cellspacing="0" cellpadding="3" height="148" align = "center">
                      <tr> 
                        <td height="35" width="67%"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="2" class = "TableCell">
                            <tr> 
                              <td width="50%" align = "right" >File Format: </td>
                              <td width="50%"> 
                                <select name = "select3">
                                  <option selected>CADP</option>
                                </select>
                              </td>
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="0" cellpadding="2" class = "TableCell">
                            <tr> 
                              <td width="50%" align = "right">Billing End Date: 
                              </td>
                              <td width="50%"> <input type="text" name="billDate" size = "10" value = ""> <a href="javascript:show_calendar('MForm.billDate')"
						onMouseOver="window.status='Pop Calendar';return true;"
						onMouseOut="window.status='';return true;"><img src="StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a> 
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="0" cellpadding="2" class = "TableCell">
                            <tr> 
                              <td width="50%" align = "right">Demand Days Previous:</td>
                              <td width="50%"> 
                                <input type="text" name="textfield222" size = "5">
                              </td>
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="0" cellpadding="2" class = "TableCell" height="9">
                            <tr> 
                              <td width = "50%" align = "right">Energy Days Previous: 
                              </td>
                              <td width = "50%"> 
                                <input type="text" name="textfield322" size = "5">
                              </td>
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="0" cellpadding="3" height="10" class = "TableCell">
                            <tr> 
                              <td width="50%" align = "right" > 
                                <input type="checkbox" name="checkbox322" value="checkbox" >
                              </td>
                              <td width="50%">Append to File </td>
                            </tr>
                            <tr> 
                              <td width="50%" align = "right"> 
                                <input type="checkbox" name="checkbox22" value="checkbox">
                              </td>
                              <td width="50%">Remove Multiplier </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
                <tr> 
                  <td width="72%" valign = "top"> 
                    <table width="100%" border="1" cellspacing="0" cellpadding="4" height="60" align = "center">
                      <tr> 
                        <td height="35" width="67%"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" height="10" class = "TableCell">
                            <tr> 
                              <td width="50%" align = "right" colspan = "2"> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="1" class = "TableCell">
                                  <tr> 
                                    <td width="48%">Billing Group Type:</td>
                                    <td width="52%"> 
                                      <select name = "select">
                                        <option selected>Alternate Group</option>
                                      </select>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                  <td width="28%" align = "center"  valign = "top"> 
                    <table width="100%" border="1" cellspacing="0" cellpadding="8" height = "60">
                      <tr>
                        <td bgcolor ="#CCCCCC">
                          <input type="submit" name="Submit" value="Generate">
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              </form>
            <p align="center">&nbsp;</p>
            <p align="center">&nbsp;</p>
          </td>
        <td width="1" bgcolor="#000000"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>

</html>
