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
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(operator.getDatabaseAlias());       
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
<table width="800" border="0" cellspacing="0" cellpadding="0" align="left">
    <tr>
      <td width="150" class="LeftCell"><br>
        <p align="center"><a href="oper_ee.jsp?tab=current"><img src="CurrentButton.gif" border="0"></a></p>
        <p align="center"><a href="oper_ee.jsp?tab=new"><img src="NewButton.gif" border="0"></a></p>
        <p align="center"><a href="oper_ee.jsp?tab=history"><img src="HistoryButton.gif" border="0"></a></p>
        <p align="center"><a href="oper_ee.jsp?tab=programs"><img src="ProgramsButton.gif" border="0"></a></p>
            </td>
      <td width="650" class="MainText">
        <p>
          <center>
            &nbsp;<b><font size="-1" face="Arial">Customer Profile</font></b>
          </center>
        </p>
        <p align="center"><font size="-1" face="Arial"><%= cust.getCompanyName() %><br clear="ALL">
          <%= cust.getAddress().getLocationAddress1() %><br clear="ALL">
          <%= cust.getAddress().getLocationAddress2() %><br clear="ALL">
          <br>
          <a href="<%= referrer %>"><font face="Arial, Helvetica, sans-serif">Back</font></a></font> 
      </td>
    </tr>
  </table>
  
<%
    }
%>
