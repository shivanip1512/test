<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="demostyle.css" type="text/css">
</head>
<body bgcolor="#666699" text="#000000" leftmargin="0" topmargin="0" link="#000000" vlink="#000000" alink="#000000">
<table width="658" border="0" cellspacing="0" height="102" cellpadding="0">
  <tr>
    <td width="657"valign="bottom">
      <table width="657" border="0" cellspacing="0" cellpadding="3" height="102">
        <tr> 
          <td  background="Header.gif" height="77" >&nbsp;</td>
        </tr>
        <tr>
          <form name="form2" method="post" action="/login.jsp"><td>
                      <div align="right"><span class="Main"><a href="/login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                    </td></form>
        </tr>
      </table>
    </td>
    <td width="1" bgcolor="#000000"><img src="VerticalRule.gif"></td>
  </tr>
</table>
<table width="658" border="0" cellspacing="0" cellpadding="0" align="left">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" background="ConsumerImage.jpg" height="102">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="ConsumerHeader.gif" width="229" height="15" border="0"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="97"><font face="Arial, Helvetica, sans-serif" size="2"> </font></td>
          <td width="114">&nbsp;</td>
          <td width="233"><font face="Arial, Helvetica, sans-serif" size="1">Search 
            for existing customer:</font></td>
          <td width="57">&nbsp;</td>
        </tr>
        <tr> 
          <form name = "custSearchForm" method="POST" action="/servlet/SOAPClient">
            <input type="hidden" name="action" value="SearchCustAccount">
            <td width="97" class = "Main" > 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "Consumer/New.jsp" class = "Link1" style = "text-decoration:none;">New 
                Account</a></div>
            </td>
            <td  class = "Main" width="114" >&nbsp;</td>
            <td  class = "Main" width="233" align = "right"> 
              <select name="SearchBy">
                <option value="AccountNumber">Acct #</option>
                <option value="PhoneNumber">Phone #</option>
                <option value="Name">Name</option>
              </select>
              &nbsp; 
              <input type="text" name="SearchValue">
              &nbsp; </td>
            <td class = "Main" width="57" valign = "top"><img src="GoButton.gif" width="23" height="20" onClick = "Javascript:document.custSearchForm.submit();"> 
            </td>
          </form>
          <form method="get" action="Consumer/New.jsp"> 
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="VerticalRule.gif" height="102"></td>
  </tr>
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="MeterImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="MeteringHeader.gif" width="161" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="80">&nbsp;</td>
          <td width="80">&nbsp;</td>
          <td >&nbsp;</td>
        </tr>
        <tr> 
          <form method="post" action="Metering/Billing.jsp">
            <td width="110" class = "Main"> 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "Metering/Billing.jsp" class = "Link1" style = "text-decoration:none;">Billing 
                </a></div>
            </td>
            <td width="110" class = "Main"> 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "Metering/Metering.jsp" class = "Link1" style = "text-decoration:none;">All 
                Trends </a></div>
            </td>
          </form>
          <form method="post" action="Metering/Metering.jsp">
            <td class = "Main"> </td>
          </form>
          <form method="get" action="Metering/Metering.jsp">
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="VerticalRule.gif" height="102"></td>
  </tr>
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="LoadImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="LoadHeader.gif"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="110" >&nbsp;</td>
          <td width="110" >&nbsp;</td>
          <td width="110" >&nbsp;</td>
          <td width="4" >&nbsp;</td>
          <td class="TableCell" align = "center" rowspan = "3" width="161" valign = "top"> 
            <table width="85%" border="0" cellspacing = "0" bordercolor="#666999">
              <tr> 
                <td colspan = "2" class = "TableCell">Today's Odds for Control: 
                </td>
              </tr>
              <tr> 
                <td class = "TableCell" width="49%"> 
                  <input type="radio" name="radiobutton" value="radiobutton" checked>
                  Likely&nbsp;</td>
                <td width="51%" rowspan = "2" valign = "bottom" align = "center"> 
                  <table width="100%" border="0" height="18">
                    <tr> 
                      <td class = "TableCell">
                        <div align = "center" style = "border:solid 1px #666999;">Submit 
                          </div>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr> 
                <td class = "TableCell" width="49%"> 
                  <input type="radio" name="radiobutton" value="radiobutton">
                  Unlikely</td>
                
              </tr>
            </table>
          </td>
        </tr>
        <tr> 
          <form method="post" action="LoadControl/oper_direct.jsp">
            <td width="110" class = "Main"> 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "LoadControl/oper_direct.jsp" class = "Link1" style = "text-decoration:none;">Direct 
                </a></div>
            </td>
          </form>
          <form method="post" action="LoadControl/oper_mand.jsp">
            <td width="110" class = "Main"> 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "LoadControl/oper_mand.jsp" class = "Link1" style = "text-decoration:none;">Notification 
                </a></div>
            </td>
          </form>
          <form method="post" action="LoadControl/oper_ee.jsp">
            <td width = "110" class = "Main"> 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "LoadControl/oper_ee.jsp" class = "Link1" style = "text-decoration:none;">Buy 
                Back </a></div>
            </td>
            <td class = "Main" width="4">&nbsp;</td>
          </form>
          <form name="form1" method="post" action="">
           
          </form>
        </tr>
        <tr>
          <td width="110" class = "Main">&nbsp;</td>
          <td width="110" class = "Main">&nbsp;</td>
          <td width = "110" class = "Main">&nbsp;</td>
          <td class = "Main" width="4">&nbsp;</td>
          
        </tr>
      </table>
    </td>
    <td width="1" background="VerticalRule.gif" height="102"></td>
  </tr>
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="InventoryImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="InventoryHeader.gif" width="148" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="144" height="30" valign="bottom"><font face="Arial, Helvetica, sans-serif" size="1">Search 
            by serial number:</font></td>
          <td width="369" height="30" valign="bottom">&nbsp;</td>
        </tr>
        <tr> 
          <form name = "serialSearchForm" method="post" action="Hardware/InventoryDetail.jsp">
            <td width="144"> 
              <input type="text" name="textfield22">
              &nbsp; </td>
            <td width="369" valign = "top"> <img src="GoButton.gif" width="23" height="20" onclick = "Javascript:document.serialSearchForm.submit();" > </td>
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="VerticalRule.gif" height="102"></td>
  </tr>
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="WorkImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="WorkHeader.gif" width="104" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td  >&nbsp;</td>
          <td  >&nbsp;</td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <form method="post" action="WorkOrder/SOList.jsp">
            <td width="110" class = "Main"> 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "WorkOrder/SOList.jsp" class = "Link1" style = "text-decoration:none;">Service 
                Order List </a></div>
            </td>
            <td width="110" class = "Main" > <div align = "center" style = "border:solid 1px #666999;"><a href = "WorkOrder/InstallList.jsp" class = "Link1" style = "text-decoration:none;">New Installs 
                List </a></div>
            
            </td>
          </form>
          <form method="post" action="WorkOrder/InstallList.jsp">
            <td  >&nbsp; </td>
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="VerticalRule.gif" height="102"></td>
  </tr>
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
  <tr>
    <td width="102" bgcolor="#000000" height="102" background="AdminImage.jpg">&nbsp;</td>
    <td bgcolor="#FFFFFF" height="102" valign="top"><img src="AdminHeader.gif" width="129" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td>&nbsp;</td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <form method="post" action="Admin/Privileges.jsp">
            <td align = "center" class = "Main" width = "110"> 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "Admin/Privileges.jsp" class = "Link1" style = "text-decoration:none;">Privileges 
                </a></div>
            </td>
            <td align = "center" class = "Main">&nbsp;</td>
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="VerticalRule.gif" height="16"></td>
  </tr>
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
  </table>
<div align="center"></div>
</body>

</html>
