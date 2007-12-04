<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonRoleProperty" %>
<%@ page import="com.cannontech.roles.YukonRoleDefs" %>
<%@ page import="com.cannontech.roles.YukonGroupRoleDefs" %>
<%@ page import="com.cannontech.database.PoolManager" %>
<%@ page import="com.cannontech.common.util.CtiUtilities" %>
<%@ page import="com.cannontech.database.data.lite.LiteComparators" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="com.cannontech.user.UserUtils" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="org.springframework.jdbc.support.JdbcUtils" %>


<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>Energy Services Operations Center</title>           

        
<link rel="stylesheet" type="text/css" href="WebConfig/yukon/styles/StandardStyles.css" >
<link rel="stylesheet" type="text/css" href="WebConfig/yukon/styles/YukonGeneralStyles.css" >
<link rel="stylesheet" href="WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="WebConfig/yukon/styles/loginStyles.css" type="text/css" />

<script type="text/javascript" src="JavaScript/prototype.js" ></script>
<script type="text/javascript" src="JavaScript/CtiMenu.js" ></script>

    </head>

	<script type="text/javascript" language="">
		<!--
		var ctiMenu = new CtiMenu('subMenu');
		// -->
	</script>
	
	<script language="JavaScript">
		<!--
		function dispStatusMsg(msgStr) { //v1.0
		  status=msgStr;
		  document.statVal = true;
		}
		//-->
	</script>

	<body class="blank_module">
			
		<div id="Header">
		    <div class="stdhdr_left"><div id="TopLeftLogo"></div><div id="TopLeftLogo2"></div></div>
		    <div class="stdhdr_right"><div id="TopRightLogo"></div></div>
		    <div class="stdhdr_clear"></div>
		</div>
		<div id="Menu">
			<div id="topMenu">
				<div>
					<div class="stdhdr_leftSide"></div>
					<div class="stdhdr_rightSide"></div>
					<div style="clear: both"></div>
				</div>
			</div>
		</div>
	
		<div class="loginMain" style="width: 550px">
			<div class="loginTopSection">
				<div class="loginTitleIntroText">Welcome to</div>
				<div class="loginTitleText">Yukon Administration Setup</div>
			</div>
	  
	  		<div class="loginMainSection">
				
				<cti:titledContainer title="Database">
	    			<div align="center" class="redMsg"><p>Database settings are now configured via the master.cfg file.<br> 
	    			Seperate configuration for client and server is no longer required.</p></div>
				</cti:titledContainer>
				
				<br/>
				
				<cti:titledContainer title="Server">
	
					<div class="loginIntroText">
    			
<%
	String retMsg = "";
	String temp = null;
	if(request.getParameter("invalid") != null) 
		retMsg = "<span class=\"ErrorMsg\">* Invalid password entered, unable to save ANY changes</span><br>";
	
	if( (temp = request.getParameter("dbprop")) != null) {
		if( Boolean.valueOf(temp).booleanValue() )
			retMsg += "<span class=\"MainText\">* Saved DATABASE properties successfully</span><br>";
		else
			retMsg += "<span class=\"ErrorMsg\">* Unable to save DATABASE properties changes</span><br>";
	}

	if( (temp = request.getParameter("yukprop")) != null) {
		if( Boolean.valueOf(temp).booleanValue() )
			retMsg += "<span class=\"MainText\">* Saved SERVER properties successfully</span><br><span class=\"redMsg\">(Webserver must be restarted for changes to take effect!)</span><br>";
		else
			retMsg += "<span class=\"ErrorMsg\">* Unable to save SERVER properties changes</span><br>";
	}

	if( (temp = request.getParameter("disp")) != null) {
		if( Boolean.valueOf(temp).booleanValue() )
			retMsg += "<span class=\"MainText\">* Connected to the DISPATCH service successfully</span><br>";
		else
			retMsg += "<span class=\"ErrorMsg\">* Unable to connect to the DISPATCH service</span><br>";
	}

	if (retMsg != null) {
		out.write(retMsg + "<br/>" );
	}
%>
			
						<span class="MainText"><span class="defaultText">Blue</span> items are default values.<br/>
						<span class="redMsg">Red</span> items are required.</span>
					</div>
				
					<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/SetupServlet">
						<table align="center">
	  
<%
	boolean isValidConn = false;
	try {
		java.sql.Connection c = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
		isValidConn = (c != null);
		JdbcUtils.closeConnection(c);
	} catch( Throwable t ) {}
	
	LiteYukonUser admin = null;
	
	if( isValidConn ) {
		LiteYukonRoleProperty[] props = 
				DaoFactory.getRoleDao().getRoleProperties( YukonRoleDefs.SYSTEM_ROLEID );
	
		Arrays.sort( props, LiteComparators.liteStringComparator );
	
		for( int i = 0; i < props.length; i++ )
		{
			LiteYukonRoleProperty p = props[i];
%>
					        <tr> 
						    	<td class = "MainText" align="right"  
						    		onMouseOver="dispStatusMsg('<%= p.getDescription() %>');return document.statVal" 
						    		onMouseOut="dispStatusMsg('');return document.statVal">
									<%= p.getKeyName() %>
								</td>
						        <td valign="bottom" class="MainText"> 
						        	<input type="text" name="<%= p.getKeyName() %>" 
											value="<%= DaoFactory.getAuthDao().getRolePropValueGroup(
											DaoFactory.getAuthDao().getGroup( YukonGroupRoleDefs.GRP_YUKON ),p.getRolePropertyID(), p.getDefaultValue()) %>" />
									<span class="defaultText"> <%= p.getDefaultValue()%></span> 
								</td>
							</tr>
<%
		}
		
		admin = DaoFactory.getYukonUserDao().getLiteYukonUser( UserUtils.USER_YUKON_ID );
		if( admin != null )
		{
%>
							<tr>
					             <td class="redMsg" align="right" 
					             	onMouseOver="dispStatusMsg('For security reasons, enter the password for the admin account');return document.statVal" 
					             	onMouseOut="dispStatusMsg('');return document.statVal">
								 	<%= admin.getUsername() %> password:</td>
					             <td valign="bottom" class="MainText">
					             	<input type="password" name="admin_password" />
					             </td>
							</tr>

<%
		}
	}
	
	if( !isValidConn ) {
%>
							<tr> 
					        	<td class = "ErrorMsg" align="right" 
									onMouseOver="dispStatusMsg('No database connection found, configure your DB connection above');return document.statVal" 
								  	onMouseOut="dispStatusMsg('');return document.statVal">
							  		UNABLE TO 
							  	</td>
					            <td valign="bottom" class="ErrorMsg"
									onMouseOver="dispStatusMsg('No database connection found, configure your DB connection above');return document.statVal" 
									onMouseOut="dispStatusMsg('');return document.statVal">                  
							  		CONNECT TO THE DATABASE
					            </td>
							</tr>
<%
	} else if( admin == null ) {
%>
							<tr> 
						    	<td class = "ErrorMsg" align="right" 
									onMouseOver="dispStatusMsg('No ADMIN user found in the database configuration above');return document.statVal" 
									onMouseOut="dispStatusMsg('');return document.statVal">
							  		NO ADMIN 
							  	</td>
						        <td valign="bottom" class="ErrorMsg"
									onMouseOver="dispStatusMsg('No ADMIN user found in the database configuration above');return document.statVal" 
								  	onMouseOut="dispStatusMsg('');return document.statVal">                  
							  		USER FOUND IN DATABASE
								</td>
							</tr>
<%
	}
%>

						</table>	
								  
		            	<div align="center"> 
		            		<br/>
		                	<input type="submit" name="Submit2" value="Submit">
		              	</div>
							  
					</form>
	
				</cti:titledContainer>
			</div>
		</div>
	</body>
</html>
