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
                      <div align="right"><span class="Main"><a href="/login.jsp" class="blueLink">Log 
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
      <table width="500" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="400"><font face="Arial, Helvetica, sans-serif" size="1">Search 
            for existing customer:</font><font face="Arial, Helvetica, sans-serif" size="2"> 
            </font></td>
          <td width="138">&nbsp;</td>
        </tr>
        <tr> 
          <form method="POST" action="/servlet/SOAPClient">
		    <input type="hidden" name="action" value="SearchCustAccount">
            <td width="400"> 
              <select name="SearchBy">
                <option value="AccountNumber">Acct #</option>
                <option value="PhoneNumber">Phone #</option>
                <option value="Name">Name</option>
              </select>
              &nbsp; 
              <input type="text" name="SearchValue">
              &nbsp; 
              <input type="submit" name="Search2" value="Search">
            </td>
          </form>
          <form method="get" action="Consumer/New.jsp">
            <td width="138"> 
              <input type="submit" name="New" value="New Signup">
            </td>
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
      <table width="500" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="66">&nbsp;</td>
          <td width="181">&nbsp;</td>
          <td width="335"><font face="Arial, Helvetica, sans-serif" size="1">Search 
            for Trend:</font></td>
        </tr>
        <tr> 
          <form method="post" action="Metering/Billing.jsp">
            <td width="66"> 
              <input type="submit" name="" value="Billing">
            </td>
          </form>
          <form method="post" action="Metering/Metering.jsp">
            <td width="181"> 
              <input type="submit" name="" value="All Trends">
            </td>
          </form>
          <form method="get" action="Metering/Metering.jsp">
            <td width="335"> 
              <select name="select3">
                <option>Acct #12345</option>
                <option>Acct #67890</option>
              </select>
              &nbsp; 
              <input type="submit" name="Searchg" value="Search">
            </td>
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
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="LoadHeader.gif" width="103" height="15"><br>
      <table width="500" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="61">&nbsp;</td>
          <td width="110">&nbsp;</td>
          <td width="201">&nbsp;</td>
          <td width="204"><font face="Arial, Helvetica, sans-serif" size="1">Today's 
            Odds for Control:</font></td>
        </tr>
        <tr> 
          <form method="post" action="LoadControl/oper_direct.jsp">
            <td width="61"> 
              <input type="submit" name="tab" value="Direct">
            </td>
          </form>
          <form method="post" action="LoadControl/oper_mand.jsp">
            <td width="110"> 
              <input type="submit" name="tab" value="Notification">
            </td>
          </form>
          <form method="post" action="LoadControl/oper_ee.jsp">
            <td width="201"> 
              <input type="submit" name="" value="Buy Back">
            </td>
          </form>
          <td width="204"> 
            <input type="submit" name="Likely" value="Likely">
            &nbsp; 
            <input type="submit" name="Unlikely" value="Unlikely">
          </td>
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
      <table width="500" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="335" height="30" valign="bottom"><font face="Arial, Helvetica, sans-serif" size="1">Search 
            by serial number:</font></td>
        </tr>
        <tr> 
          <form method="post" action="Hardware/InventoryDetail.jsp">
            <td width="335"> 
              <input type="text" name="textfield22">
              &nbsp; 
              <input type="submit" name="" value="Search">
            </td>
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
      <table width="500" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="169">&nbsp;</td>
          <td width="369">&nbsp;</td>
        </tr>
        <tr> 
          <form method="post" action="WorkOrder/SOList.jsp">
            <td width="169"> 
              <input type="submit" name="" value="Service Order List">
            </td>
          </form>
          <form method="post" action="WorkOrder/InstallList.jsp">
            <td width="369"> 
              <input type="submit" name="" value="New Installs List">
            </td>
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
      <table width="500" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <form method="post" action="Admin/Privileges.jsp">
            <td> 
              <input type="submit" name="" value="Privileges">
            </td></form>
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
