<%@ page import="com.cannontech.database.data.lite.LiteYukonRoleProperty" %>
<%@ page import="com.cannontech.roles.YukonRoleDefs" %>
<%@ page import="com.cannontech.database.cache.functions.RoleFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%@ page import="com.cannontech.roles.YukonGroupRoleDefs" %>
<%@ page import="com.cannontech.database.PoolManager" %>
<%@ page import="com.cannontech.common.util.CtiUtilities" %>
<%@ page import="com.cannontech.database.data.lite.LiteComparators" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="com.cannontech.database.cache.functions.YukonUserFuncs" %>
<%@ page import="com.cannontech.user.UserUtils" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.clientutils.CTILogger" %>


<script language="JavaScript">
<!--
function dispStatusMsg(msgStr) { //v1.0
  status=msgStr;
  document.statVal = true;
}
//-->
</script>

<html>
<head>
<title>Yukon Setup</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="WebConfig/yukon/CannonStyle.css" type="text/css">

</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0" onLoad="document.forms.form1.db_user_name.focus()">
<br>
<table width="650" border="1" align="center" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
  <tr> 
    <td colspan = "2" align = "center" height="51" valign = "bottom"> 
      <table width="400" border="0" height="43">
        <tr> 
          <td width="34%" align = "center"><span class="TableCell"><font face="Verdana, Arial, Helvetica, sans-serif" size="3" color="#999999"><b><font face="Arial, Helvetica, sans-serif" color="#666666" size="4">Welcome 
            to</font></b></font> </span></td>
          <td width="66%">&nbsp;</td>
        </tr>
        <tr> 
          <td colspan = "2" align = "center"><span class="TableCell"><b><font size="4">Yukon 
            Administration Setup
            </font></b></span>
          </td>
        </tr>
      </table>
      
    </td>
  </tr>
  <tr> 
    <td valign = "top" class = "MainText">    
    <div align="center">
		<span class="defaultText">Blue</span> items are default values.
		<span class="redMsg">Red</span> items are required.<br><br>
<%
	String retMsg = "";
	String temp = null;
	if(request.getParameter("invalid") != null) 
		retMsg = "<span class=\"ErrorMsg\">* Invalid password entered, unable to save ANY changes</span><br>";
	
	if( (temp = request.getParameter("dbprop")) != null) 
	{
		if( Boolean.valueOf(temp).booleanValue() )
			retMsg += "<span class=\"MainText\">* Saved DATABASE properties successfully</span><br>";
		else
			retMsg += "<span class=\"ErrorMsg\">* Unable to save DATABASE propertie changes</span><br>";
	}

	if( (temp = request.getParameter("yukprop")) != null) 
	{
		if( Boolean.valueOf(temp).booleanValue() )
			retMsg += "<span class=\"MainText\">* Saved SERVER properties successfully</span><br>";
		else
			retMsg += "<span class=\"ErrorMsg\">* Unable to save SERVER propertie changes</span><br>";
	}

	if( (temp = request.getParameter("disp")) != null) 
	{
		if( Boolean.valueOf(temp).booleanValue() )
			retMsg += "<span class=\"MainText\">* Connected to the DISPATCH service successfully</span><br>";
		else
			retMsg += "<span class=\"ErrorMsg\">* Unable to connect to the DISPATCH service</span><br>";
	}

%>
	  <% if (retMsg != null)
	  		out.write(retMsg ); %>
	 </div>
	  
      <table width="640"  height="186" border="0" align="center" cellpadding="0" cellspacing="0">
        <tr> 
          <td rowspan = "3" width="555" height="102" valign="top" ><br>
            <form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/SetupServlet">
			  <br>
              <table width="635" border="0" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td align="center" colspan=2>
				  <img src="WebConfig/yukon/databaseProps.jpg" align="center"></td>
				</tr>

                <tr> 
                  <td width="200" class = "MainText" align="right" onMouseOver="dispStatusMsg('The username the database will use to login with');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">
				  DB User Name:</td>
                  <td width="435" valign="bottom" class="MainText"> 
                    <input type="text" name="db_user_name" value="<%= PoolManager.getInstance().getProperty(PoolManager.USER) %>">
                  </td>
				</tr>
                <tr> 
                  <td width="200" class = "MainText" align="right" onMouseOver="dispStatusMsg('The password the database user will use to login with');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">
				  DB User Password:</td>
                  <td width="435" valign="bottom" class="MainText"> 
                    <input type="password" name="db_user_password">
                    <span class="defaultText"> (Used to change password only)</span>
                  </td>
				</tr>
				<tr>
                  <td width="200" class = "MainText" align="right" onMouseOver="dispStatusMsg('Where the database is at on your network and how to connect to it');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">
				  DB Host:</td>
                  <td width="435" valign="bottom" class="MainText"> 
                    <input type="text" name="db_url" value="<%= PoolManager.getInstance().getProperty(PoolManager.HOST) %>">
                    <span class="defaultText"> dbserver.here.com</span> </td>
                </tr>
				<tr>
                  <td width="200" class = "MainText" align="right" onMouseOver="dispStatusMsg('Where the database is at on your network and how to connect to it');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">
				  DB Port:</td>
                  <td width="435" valign="bottom" class="MainText"> 
                    <input type="text" name="db_port" value="<%= PoolManager.getInstance().getProperty(PoolManager.PORT) %>">
                    <span class="defaultText"> 1433</span> </td>
                </tr>
                <tr> 
                  <td width="200" class = "MainText" align="right" onMouseOver="dispStatusMsg('What type of database you are using');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">
				  Database:</td>
                  <td width="435" valign="bottom" class="MainText"> 
					<select name="db_driver">
					<option value="jtds" <% if( PoolManager.getInstance().isJTDS() ) {%> SELECTED <%}%> > SQL Server (JTDS)</option>
					<option value="sql" <% if( PoolManager.getInstance().isMS() ) {%> SELECTED <%}%> > SQL Server (MS)</option>
					<option value="oracle" <% if( PoolManager.getInstance().isOracle() ) {%> SELECTED <%}%> > Oracle </option>
					</select>					
                  </td>
				</tr>
				<tr>
                  <td width="200" class = "MainText" align="right" onMouseOver="dispStatusMsg('Oracle service name');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">
				  DB Service Name(Oracle only):</td>
                  <td width="435" valign="bottom" class="MainText"> 
                    <input type="text" name="db_service" value="<%= PoolManager.getInstance().getProperty(PoolManager.SERVICE) %>">
                    </td>
                </tr>
				<tr>
                  <td width="200" class = "MainText" align="right" onMouseOver="dispStatusMsg('The number of DB connections initially created in the pool');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">
				  Initial DB Connections:</td>
                  <td width="435" valign="bottom" class="MainText"> 
                    <input type="text" name="db_initconns" value="<%= PoolManager.getInstance().getProperty(PoolManager.INITCONNS) %>"><span class="defaultText"> 2</span> </td>
                </tr>
				<tr>
                  <td width="200" class = "MainText" align="right" onMouseOver="dispStatusMsg('The maximum number of DB connections used');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">
				  Max DB Connections:</td>
                  <td width="435" valign="bottom" class="MainText"> 
                    <input type="text" name="max_initconns" value="<%= PoolManager.getInstance().getProperty(PoolManager.MAXCONNS) %>"><span class="defaultText"> 6</span> </td>
                </tr>

              </table>
			  <br><br><br>              
              <table width="635" border="0" cellspacing="0" cellpadding="3" align="center">
			  
                <tr> 
                  <td align="center" colspan=2>
				  <img src="WebConfig/yukon/serverProps.jpg" align="center"></td>
				</tr>
			  
<%

boolean isValidConn = false;
try
{
	java.sql.Connection c = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	isValidConn = (c != null);
	PoolManager.getInstance().freeConnection( CtiUtilities.getDatabaseAlias(), c );
}
catch( Throwable t ) {}


LiteYukonUser admin = null;

if( isValidConn )
{
	LiteYukonRoleProperty[] props = 
			RoleFuncs.getRoleProperties( YukonRoleDefs.SYSTEM_ROLEID );

	Arrays.sort( props, LiteComparators.liteStringComparator );

	for( int i = 0; i < props.length; i++ )
	{
		LiteYukonRoleProperty p = props[i];
%>
            <tr> 
              <td width="200" class = "MainText" align="right" 
				  onMouseOver="dispStatusMsg('<%= p.getDescription() %>');return document.statVal" 
				  onMouseOut="dispStatusMsg('');return document.statVal">
				  <%= p.getKeyName() %></td>
                  <td width="435" valign="bottom" class="MainText"> 
                    <input type="text" name="<%= p.getKeyName() %>" 
					value="<%= AuthFuncs.getRolePropValueGroup(
					AuthFuncs.getGroup( YukonGroupRoleDefs.GRP_YUKON ),p.getRolePropertyID(), p.getDefaultValue()) %>">

					<span class="defaultText"> <%= p.getDefaultValue()%></span> 
              </td>
				</tr>
<%
	}
	
	admin = YukonUserFuncs.getLiteYukonUser( UserUtils.USER_YUKON_ID );
	if( admin != null )
	{
%>
				<tr>
              <td width="200" class="redMsg" align="right" onMouseOver="dispStatusMsg('For security reasons, enter the password for the admin account');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">
				  <%= admin.getUsername() %> password:</td>
              <td width="435" valign="bottom" class="MainText">
                <input type="password" name="admin_password">
                <span class="defaultText"> yukon</span>
              </td>
            </tr>

<%
	}
}

if( !isValidConn )
{
%>
            <tr> 
                  <td width="200" class = "ErrorMsg" align="right" 
					  onMouseOver="dispStatusMsg('No database connection found, configure your DB connection above');return document.statVal" 
					  onMouseOut="dispStatusMsg('');return document.statVal">
				  UNABLE TO </td>
                  <td width="435" valign="bottom" class="ErrorMsg"
					  onMouseOver="dispStatusMsg('No database connection found, configure your DB connection above');return document.statVal" 
					  onMouseOut="dispStatusMsg('');return document.statVal">                  
				  CONNECT TO THE DATABASE
                  </td>
				</tr>
<%
}
else if( admin == null )
{
%>
            <tr> 
                  <td width="200" class = "ErrorMsg" align="right" 
					  onMouseOver="dispStatusMsg('No ADMIN user found in the database configuration above');return document.statVal" 
					  onMouseOut="dispStatusMsg('');return document.statVal">
				  NO ADMIN </td>
                  <td width="435" valign="bottom" class="ErrorMsg"
					  onMouseOver="dispStatusMsg('No ADMIN user found in the database configuration above');return document.statVal" 
					  onMouseOut="dispStatusMsg('');return document.statVal">                  
				  USER FOUND IN DATABASE
                  </td>
				</tr>
<%
}
%>

                <tr> 
                  <td width="200">&nbsp;</td>
                  <td width="435">
                    <div align="left"> 
                      <input type="submit" name="Submit2" value="Submit">
                    </div>
				  </td>
				  
                </tr>
              </table>			  
			  
			  
			  
			  
              <div align="center" class="MainText"><br>
                If you need help or have forgotten your password, click <a href="<%=request.getContextPath()%>/pwordreq.jsp">here</a>.
                <br>
                <br>
              </div>
            </form>
</td>
        </tr>
        </table>
      <div align="center"> </div>
    </td>
  </tr>
</table>
<br>
<div align="center" class="TableCell1"><img src="YukonLogo.gif" width="139" height="29"></div>
</body>

</html>
