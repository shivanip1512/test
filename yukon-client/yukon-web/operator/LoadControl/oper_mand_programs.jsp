<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link id="StyleSheet" rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramCurtailment" %>
<%@ page import="com.cannontech.loadcontrol.data.LMCurtailCustomer" %>
<%@ page import="java.util.ArrayList" %>
<%
    LMProgramCurtailment[] programs = cache.getEnergyCompanyCurtailmentPrograms(energyCompanyID);

%>
<body class="Background" text="#000000" leftmargin="0" topmargin="0">
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
                <td width="310" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;Load Response</td>
                <td width="235" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
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
                  <form name="form1" method="get" action="oper_mand.jsp?tab=current">
                    <p><br>
                      <input type="submit" name="tab" value="Current">
                    </p>
                  </form>
                  <form name="form1" method="get" action="oper_mand.jsp?tab=new">
                    <p> 
                      <input type="submit" name="tab" value="New">
                    </p>
                  </form>
                  <form name="form1" method="get" action="oper_mand.jsp?tab=history">
                    <p> 
                      <input type="submit" name="tab" value="History">
                    </p>
                  </form>
                  <form name="form1" method="get" action="oper_mand.jsp?tab=programs">
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
          <td width="657" valign="top" bgcolor="#FFFFFF" class="Main"> 
            <div align="center"><br>
              <b>NOTIFICATION - PROGRAMS<br>
              </b><br>
            </div>
            <table width="600" border="0" cellspacing="0" cellpadding="0" align="center">
		
          <%
               for( int i = 0; i < programs.length; i += 2 )
               {
                    LMProgramCurtailment p = programs[i];   
          %>
          <tr> 
            <td valign="top"> 
              <table width="250" border="1" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td  class="HeaderCell" width="50%"><%= p.getYukonName() %></td>
                </tr>
                <%
                    LMCurtailCustomer[] custs = cache.getCurtailmentCustomers(p.getYukonID().longValue() );
                    for( int j = 0; j < custs.length; j++ )
                    {                    
                %>
                <tr class="TableCell"> 
                  <td width="50%"> <a href="oper_mand.jsp?tab=profile&customerid=<%= custs[j].getCustomerID() %>" class="Link1"><%= custs[j].getCompanyName() %></a></td>
                </tr>
                <%
                    }
                 %>
              </table>
            </td>
            <%
                    if( i+1 < programs.length )
                    {

                        p = programs[i+1];

               %>
            <td valign="top"> 
              <table width="250" border="1" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td class="HeaderCell" width="50%"> <%= p.getYukonName() %></td>
                </tr>
                <%
                    custs = cache.getCurtailmentCustomers(p.getYukonID().longValue() );
                    for( int j = 0; j < custs.length; j++ )
                    {                    
                %>
                <tr class="TableCell"> 
                  <td width="50%"> <a href="oper_mand.jsp?tab=profile&customerid=<%= custs[j].getCustomerID() %>" class="Link1"><%= custs[j].getCompanyName() %></a></td>
                </tr>
                <%
                     }
                    }
               }
                %>
              </table>
            </td>
          </tr>
        </table>
      <p>&nbsp;
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
