
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
</head>
<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache" %>

<%  
    /* Go through the devices and find a matching deviceid */
    String customerIDStr = request.getParameter("customerid");

    if( customerIDStr != null )
    {   
        int customerID = Integer.parseInt(customerIDStr);
        
        // Attempt to retrieve the customer's info
        com.cannontech.database.data.customer.CICustomerBase cust = 
            (com.cannontech.database.data.customer.CICustomerBase) 
                com.cannontech.database.data.customer.CustomerFactory.createCustomer(com.cannontech.database.data.customer.CustomerTypes.CUSTOMER_CI);

        cust.setCustomerID( new Integer(customerID) );

        
        java.sql.Connection conn = null;
        
        try
        {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);       
            cust.setDbConnection(conn);
            cust.retrieve();
            cust.setDbConnection(null);
        }
        catch(java.sql.SQLException e )
        {
            e.printStackTrace();
        }
        finally
        {
            try 
            {
                if( conn != null ) conn.close();
            }
            catch(java.sql.SQLException e2) { }         
        }
%>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="LoadImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="253" height="28" class="Header3">&nbsp;&nbsp;&nbsp;<cti:text roleid="<%= RoleTypes.ENERGYEXCHANGE_TEXT %>"/></td>
				<td width="235" valign="middle">&nbsp;</td>
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
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
                        <table width="101" border="0" cellspacing="0" cellpadding="6" height="200">
              <tr> 
                <td height="20" valign="top" align="center"> 
                  <form name="form1" method="Get" action="oper_ee.jsp?tab=current">
                    <p><br>
                      <input type="submit" name="tab" value="Current">
                    </p>
				  </form>
                  <form name="form1" method="get" action="oper_ee.jsp?tab=new">
				    <p> 
                      <input type="submit" name="tab" value="New">
                    </p>
				  </form>
                  <form name="form1" method="get" action="oper_ee.jsp?tab=history">
				    <p> 
                      <input type="submit" name="tab" value="History">
                    </p>
					</form>
				   <form name="form1" method="get" action="oper_ee.jsp?tab=programs">
                    <p> 
                      <input type="submit" name="tab" value="Programs">
                    </p>
                  </form>
                  <p>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
<table width="640" border="0" cellspacing="0" cellpadding="0" align="left">
    <tr>
      <td width="635">
        <p align="center" class="Main"><br><b><cti:text roleid="<%= RoleTypes.ENERGYEXCHANGE_HEADING_TEXT %>"/> - CUSTOMER PROFILE</b> 
        </p>
        <p align="center" class="Main"><%= cust.getCiCustomerBase().getCompanyName() %><br clear="ALL">
          <%= cust.getAddress().getLocationAddress1() %><br clear="ALL">
          <%= cust.getAddress().getLocationAddress2() %><br clear="ALL">
          </p>
    </td>
    </tr>
  </table>
  
            <%
    }
%>
            <p>&nbsp;</p>
            <p>&nbsp;</p>
            <p>&nbsp;</p>
            <p align="center" class="MainHeader">&nbsp; 
            <p align="center" class="MainHeader"><a href="<%= referrer %>" class="Link1"><b>Back 
              to Program Summary</b></a> 
            <p>&nbsp;</p>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
