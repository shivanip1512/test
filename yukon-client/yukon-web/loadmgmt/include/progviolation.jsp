
<%
if( lmSession.getResponseProgs() == null )
{
	CTILogger.warn( 
		"No server response for sync message found, redirecting request to: " + lmSession.DEF_REDIRECT ); 

	response.sendRedirect( lmSession.DEF_REDIRECT );
	return;
}

%>         

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="css/lm.css" type="text/css">
</head>


<body class="Background" leftmargin="0" topmargin="0" onload="reload();" >

<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="images/LoadImage.jpg">&nbsp;</td>
		  <td width="1" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
          <td valign="bottom" height="102"> 
            <table width="656" cellspacing="0" cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="images/Header.gif">&nbsp;</td>
              </tr>
              <tr>
                <td width="353" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp; 
                  Load Management
            		<% if( lmSession.getRefreshRate() < LMSession.REF_SECONDS_DEF ) {%>
            			<font class="refreshTxt"> 
            			(Auto-refresh in <%= lmSession.getRefreshRate() %>
            			seconds)</font>
            		<%
            			lmSession.setRefreshRate(LMSession.REF_SECONDS_DEF);
            		}%>
                </td>
				
                <td width="235" valign="middle">&nbsp;</td>
                <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../operator/Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="759" valign="top" bgcolor="#000000"> 
		  <td width="1" bgcolor="#000000" height="1"></td>
		</tr>
        <tr> 
          <td width="759" valign="middle" bgcolor="#FFFFFF">
			<table width="740" border="0" cellspacing="0" cellpadding="0" align="center" >
              <tr>
			  <td>
				<div align="center" class="boldMsg"><br>
				  Constraint Violation Override<br>
				  Select the constraints you would like to override
				</div>
			  </td>
			  			  
              </tr>
            </table>

	<form name="cmdForm" method="post" action="<%=request.getContextPath()%>/servlet/LCConnectionServlet" >
	<input type="hidden" name="override" value="true" >
	<input type="hidden" name="redirectURL" value="<%= request.getRequestURI() + (request.getQueryString()==null?"":"?"+request.getQueryString()) %>" >
            <table width="740" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>
                <td width="740" valign="top" class="MainText"> 
                  <table width="744" border="1" align="center" cellpadding="2" cellspacing="0">
                    <tr valign="top" class="HeaderCell"> 
					  <td width="168"><div align="left">
						<input type="checkbox" name="allChks" value="true" onClick="checkAll(cmdForm.allChks, cmdForm.dblarray1)">
						Override All</div>
  					  </td>
                      <td width="316"><div align="center"><%= ConstraintTableModel.COL_NAMES[ConstraintTableModel.COL_VIOLATION]%></div></td>					  
                    </tr>
<%
	ResponseProg[] respProgs = lmSession.getResponseProgs();
	for( int i = 0; respProgs != null && i < respProgs.length; i++ )
	{
		ResponseProg prg = respProgs[i];
%>
                    <tr valign="top">
                      <td width="168" class="TableCell" align="left">
						<input type="checkbox" name="dblarray1" value=<%= prg.getLmProgramBase().getYukonID() %> >
						<%= prg.getLmProgramBase().getYukonName() %>
					  </td>					  
					  <td width="316" class="TableCell" align="left">
					  <%= prg.getViolationsAsString() %>
					  </td>					  
                    </tr>
<%
	}
%>
                  </table>
              </tr>
              <tr>
                <td width="740" height="20"></td>
			  </tr>
              <tr>
                <td width="740" valign="middle" class="MainText"> 
				<div align="center">
					<input type="submit" name="Submit" value="Ok" class="defButton" >
				</div>
				
				</td>
			  </tr>			  
		</table>
	</form>


		   <br>
		</tr>
	</table>

          </td>
		  <td width="1" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
    </tr>
      </table>

</body>
</html>
