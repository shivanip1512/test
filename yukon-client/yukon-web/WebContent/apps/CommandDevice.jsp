<%-- A wrapper file for access to commander with a deviceID.
--%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ include file="../operator/Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.core.dao.DaoFactory"%> 
<%@ page import="com.cannontech.database.data.pao.YukonPAObject"%>
<%@ page import="com.cannontech.message.dispatch.message.PointData"%> 
<%@ page import="com.cannontech.database.data.lite.LiteRawPointHistory"%>
<%@ page import="com.cannontech.database.data.pao.PAOGroups"%>
<%@ page import="com.cannontech.database.data.point.PointTypes"%>
<%@ page import="com.cannontech.database.db.point.RawPointHistory"%>
<%@ page import="com.cannontech.database.db.command.CommandCategory"%>
<%@ page import="com.cannontech.roles.application.CommanderRole"%>
<%@ page import="com.cannontech.yc.bean.CommandDeviceBean"%>
<cti:checklogin/> 

<jsp:useBean id="YC_BEAN" class="com.cannontech.yc.bean.YCBean" scope="session"/>
<jsp:setProperty name="YC_BEAN" property="userID" value="<%= lYukonUser.getUserID()%>"/>
<%-- Grab the search criteria --%>

<%
	PointData pointData = null;

	int invNo = -1;	//used for directing to different application starting points
	int deviceID = PAOGroups.INVALID;
	if( request.getParameter("deviceID") != null)
	{
		deviceID = Integer.parseInt(request.getParameter("deviceID"));
        if( YC_BEAN.getDeviceID() != deviceID)
            session.removeAttribute("CustomerDetail"); //delete this for now, we'll figure out a way to store per meter later
	}
	else
	{
		deviceID = YC_BEAN.getDeviceID();
	}

	//get the liteYukonPao using the deviceID
	LiteYukonPAObject liteYukonPao = null;
    if (deviceID >= 0)
        liteYukonPao = DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);
	
	boolean manual = false;
	if( request.getParameter("manual") != null)
	{	//Force going to the Commander page instead of a page based on the DeviceType
		manual = true;
	}
	boolean lp = false;
	if( request.getParameter("lp") != null)
	{	//Force going to the Load Profile
		lp = true;
	}
	boolean isMCT410 = liteYukonPao!=null && com.cannontech.database.data.device.DeviceTypesFuncs.isMCT410(liteYukonPao.getType());
	if( !isMCT410 )
	{	//MUST BE Manual...force it
		manual = true;
	}
		
	Vector serialNumbers;
	String serialNum = "";
	String serialType = "";
	if( request.getParameter("xcom") != null){
		serialNum = request.getParameter("xcom");
		serialType = "xcom";
	}
	else if( request.getParameter("vcom") != null){
		serialNum = request.getParameter("vcom");
		serialType = "vcom";
	}
	else if( request.getParameter("sa205") != null){
		serialNum = request.getParameter("sa205");
		serialType = "sa205";
	}
	else if( request.getParameter("sa305") != null){
		serialNum = request.getParameter("sa305");
		serialType = "sa305";
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<link rel="stylesheet" href="../WebConfig/yukon/Base.css" type="text/css">
<SCRIPT  LANGUAGE="JavaScript" SRC="../JavaScript/calendar.js"></SCRIPT>
</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
	  <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
		<tr> 
		  <td width="102" height="102" background="../WebConfig/yukon/MeterImage.jpg">&nbsp;</td>
		  <td valign="bottom" height="102"> 
			<table width="657" cellspacing="0"  cellpadding="0" border="0">
			  <tr> 
               	<td colspan="4" height="74" background="../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
			  </tr>
			  <tr>
				<td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Commander&nbsp;&nbsp;</td>
				<td width="253" valign="middle">&nbsp;</td>
				<td width="58" valign="middle">
                  <div align="center"><span class="MainText"><a href="../operator/Operations.jsp" class="Link3">Home</a></span></div>
				</td>
				<td width="57" valign="middle"> 
				  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
				</td>
			  </tr>
			</table>
		  </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
	    </tr>
	  </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="102" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101"> 
          	<%--"redirect" is required by Commander.jsp and for the sake of this wrapper being able to know the deviceID--%>
            <% String redirect = request.getRequestURI()+ "?deviceID=" + deviceID;%>
            <%if( manual) redirect = redirect + "&manual";%>
            <%if( lp ) redirect = redirect + "&lp";%>
            <% String referrer = request.getRequestURI()+ "?deviceID=" + deviceID;%>
            <% String pageName = "CommandDevice.jsp?deviceID=" + deviceID;%>
            <% if( serialType.length() > 0 )
            {
            	redirect = redirect + "&" + serialType + "=" + serialNum;
            	referrer = referrer + "&" + serialType + "=" + serialNum;
            	pageName = pageName + "&" + serialType + "=" + serialNum;
            }
            %>
			<table width="101" border="0" cellspacing="0" cellpadding="5">
			  <tr> 
			    <td> 
			      <div align="left">
				    <span class="NavHeader">Go To...</span>
			        <table width="100%" border="0" cellspacing="0" cellpadding="0">
					  <tr>
					  	<% String link = "";
					  	if (request.getParameter("InvNo") != null){	//we came from the Customer Account page
		              	  link =  request.getContextPath()+"/operator/Consumer/CommandInv.jsp?InvNo="+invNo+"&manual&command=null";
		              	} else {
		              	  link = request.getContextPath()+"/apps/CommandDevice.jsp?deviceID="+deviceID+"&manual&command=null";
		                }
   					  	if (manual ){%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'>Manual</span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='<%=link%>' class='Link2'><span class='NavText'>Manual</span></a></td>
						<%}%>						
			          </tr>	
 					  <tr>
					  	<% 
					  	  if (request.getParameter("InvNo") != null){	//we came from the Customer Account page
		              	    link =  request.getContextPath()+"/operator/Consumer/CommandInv.jsp?InvNo="+invNo+"&command=null";
		              	  } else {
		              	    link = request.getContextPath()+"/apps/CommandDevice.jsp?deviceID="+deviceID+"&command=null";
		                  }
   					  	  if (! (lp || manual) ){%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'>MCT410 Custom</span></td>
						  <%} else if (isMCT410){%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='<%=link%>' class='Link2'><span class='NavText'>MCT410 Custom</span></a></td> 
						  <%} else {%>
			            <td width="10"></td>
			            <td style="padding:1;color: #CCCCCC"><span class='NavText'>MCT410 Custom</span></td>
						  <%}%>
			          </tr>			          
			          <tr>
					  	<%
					  	  if (request.getParameter("InvNo") != null) {	//we came from the Customer Account page
	                	    link = request.getContextPath()+"/operator/Consumer/CommandInv.jsp?InvNo="+invNo+"&lp";
              			  } else {
						    link = request.getContextPath()+"/apps/CommandDevice.jsp?deviceID="+deviceID+"&lp";
                          }
					  	  if (lp) {%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'>MCT410 Profile</span></td>
						  <%} else if (isMCT410){%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='<%=link%>' class='Link2'><span class='NavText'>MCT410 Profile</span></a></td>
						  <%} else {%>
			            <td width="10"></td>
			            <td style="padding:1;color: #CCCCCC"><span class='NavText'>MCT410 Profile</span></td>
						  <%}%>
			          </tr>
  					  <tr><td height="10"></td></tr>
			          <tr>
			            <td width="10"></td>
			            <td style="padding:1"><a href='SelectDevice.jsp' class='Link2'><span class='NavText'>Select Device</span></a></td>
			          </tr>
  					  <tr><td height="10"></td></tr>
			        </table>
			      </div>
			    </td>
			  </tr>
			  <tr> 
			    <td> 
			      <div align="left">
				    <span class="NavHeader">Devices</span>
			        <table width="100%" border="0" cellspacing="0" cellpadding="0">
			          <% for (int i = 0; i < YC_BEAN.getDeviceIDs().size(); i++)
			          {
			          	int id = ((Integer)YC_BEAN.getDeviceIDs().get(i)).intValue();
			          	LiteYukonPAObject lPao = DaoFactory.getPaoDao().getLiteYukonPAO(id);
			          	if( CommandDeviceBean.isDeviceSortByGroup(lPao) )
			          	{%>
			          <tr>
					  	<% if (id == deviceID) {%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'><%=DaoFactory.getPaoDao().getYukonPAOName(id)%></span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=id%><%=(manual?"&manual":"")%>' class='Link2'><span class='NavText'><%=DaoFactory.getPaoDao().getYukonPAOName(id)%></span></a></td>
						<%}%>						
			          </tr>
					  <tr><td height="3"></td></tr>
			          <% } }%>
					  <tr><td height="25"></td></tr>
			        </table>			      
				    <span class="NavHeader">Load Management</span>
			        <table width="100%" border="0" cellspacing="0" cellpadding="0">
			          <% for (int i = 0; i < YC_BEAN.getDeviceIDs().size(); i++)
			          {
			          	int id = ((Integer)YC_BEAN.getDeviceIDs().get(i)).intValue();
			          	LiteYukonPAObject lPao = DaoFactory.getPaoDao().getLiteYukonPAO(id);
			          	if( CommandDeviceBean.isLoadManagementSortByGroup(lPao) )
			          	{%>
			          <tr>
					  	<% if (id == deviceID) {%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'><%=DaoFactory.getPaoDao().getYukonPAOName(id)%></span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=id%><%=(manual?"&manual":"")%>' class='Link2'><span class='NavText'><%=DaoFactory.getPaoDao().getYukonPAOName(id)%></span></a></td>
						<%}%>						
			          </tr>
					  <tr><td height="3"></td></tr>
			          <% } }%>
					  <cti:checkProperty propertyid="<%= CommanderRole.DCU_SA205_SERIAL_MODEL %>">
 					  <tr>
					  	<% if (serialType.equals("sa205") ) {%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'>DCU-205 Serial</span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&sa205' class='Link2'><span class='NavText'>DCU-205 Serial</span></a></td>
						<%}%>						
			          </tr>
  					  <%serialNumbers = YC_BEAN.getSerialNumbers(CommandCategory.STRING_CMD_SA205_SERIAL); 
  					  if(serialNumbers != null)
  					  {
  			            for (int i = 0; i < serialNumbers.size(); i++)
  			            {
			          	  String sn = (String)serialNumbers.get(i);%>
			          <tr>
					  	<% if (serialType.equals("sa205") && sn.equals(serialNum)) {%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'><%=sn%></span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&sa205=<%=sn%>' class='Link2'><span class='NavText'><%=sn%></span></a></td>
						<%}%>						
			          </tr>
			          <%}
			          }%>
  					  <tr><td height="5"></td></tr>
					  </cti:checkProperty>
					  <cti:checkProperty propertyid="<%= CommanderRole.DCU_SA305_SERIAL_MODEL %>">
					  <tr>
					  	<% if (serialType.equals("sa305") ) {%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'>DCU-305 Serial</span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&sa305' class='Link2'><span class='NavText'>DCU-305 Serial</span></a></td>
						<%}%>						
			          </tr>
  					  <%serialNumbers = YC_BEAN.getSerialNumbers(CommandCategory.STRING_CMD_SA305_SERIAL); 
  					  if(serialNumbers != null)
  					  {
  			            for (int i = 0; i < serialNumbers.size(); i++)
  			            {
			          	  String sn = (String)serialNumbers.get(i);%>
			          <tr>
					  	<% if (serialType.equals("sa305") && sn.equals(serialNum)) {%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'><%=sn%></span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&sa305=<%=sn%>' class='Link2'><span class='NavText'><%=sn%></span></a></td>
						<%}%>						
			          </tr>
			          <%}
			          }%>
  					  <tr><td height="5"></td></tr>
					  </cti:checkProperty>
					  <cti:checkProperty propertyid="<%= CommanderRole.EXPRESSCOM_SERIAL_MODEL %>">
			          <tr> 
					  	<% if (serialType.equals("xcom") ) {%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'>Expresscom Serial</span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&xcom' class='Link2'><span class='NavText'>Expresscom Serial</span></a></td>
						<%}%>						
			          </tr>
  					  <%serialNumbers = YC_BEAN.getSerialNumbers(CommandCategory.STRING_CMD_EXPRESSCOM_SERIAL); 
  					  if(serialNumbers != null)
  					  {
  			            for (int i = 0; i < serialNumbers.size(); i++)
  			            {
			          	  String sn = (String)serialNumbers.get(i);%>
			          <tr>
					  	<% if (serialType.equals("xcom") && sn.equals(serialNum)) {%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'><%=sn%></span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&xcom=<%=sn%>' class='Link2'><span class='NavText'><%=sn%></span></a></td>
						<%}%>						
			          </tr>
			          <%}
			          }%>
  					  <tr><td height="5"></td></tr>
  					  </cti:checkProperty>
  					  <cti:checkProperty propertyid="<%= CommanderRole.VERSACOM_SERIAL_MODEL %>">
			          <tr> 
					  	<% if (serialType.equals("vcom") ) {%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'>Versacom Serial</span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&vcom' class='Link2'><span class='NavText'>Versacom Serial</span></a></td>
						<%}%>						
			          </tr>
  					  <%serialNumbers = YC_BEAN.getSerialNumbers(CommandCategory.STRING_CMD_VERSACOM_SERIAL); 
  					  if(serialNumbers != null)
  					  {
  			            for (int i = 0; i < serialNumbers.size(); i++)
  			            {
			          	  String sn = (String)serialNumbers.get(i);%>
			          <tr>
					  	<% if (serialType.equals("vcom") && sn.equals( serialNum)) {%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'><%=sn%></span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&vcom=<%=sn%>' class='Link2'><span class='NavText'><%=sn%></span></a></td>
						<%}%>						
			          </tr>
			          <%}
			          }%>
					  <tr><td height="25"></td></tr>
					  </cti:checkProperty>
			        </table>
				    <span class="NavHeader">Cap Control</span>
			        <table width="100%" border="0" cellspacing="0" cellpadding="0">
			          <% for (int i = 0; i < YC_BEAN.getDeviceIDs().size(); i++)
			          {
			          	int id = ((Integer)YC_BEAN.getDeviceIDs().get(i)).intValue();
			          	LiteYukonPAObject lPao = DaoFactory.getPaoDao().getLiteYukonPAO(id);
			          	if( CommandDeviceBean.isCapControlSortByGroup(lPao) )
			          	{%>
			          <tr>
					  	<% if (id == deviceID) {%>
			            <td width="10"><img src='../WebConfig/<%=DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav'><%=DaoFactory.getPaoDao().getYukonPAOName(id)%></span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=id%><%=(manual?"&manual":"")%>' class='Link2'><span class='NavText'><%=DaoFactory.getPaoDao().getYukonPAOName(id)%></span></a></td>
						<%}%>						
			          </tr>
					  <tr><td height="3"></td></tr>
			          <% } }%>
			        </table>
			      </div>
			    </td>
			  </tr>
			</table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
<%
			if( lp )
			{%>
				<%@ include file="AdvancedCommander410.jspf"%>
			<%}
			else if (isMCT410 && !manual)
			{
			%>
				<%@ include file="Commander410.jspf"%>
			<%}
			else
			{%>
	 			<%@ include file="Commander.jspf"%>
			<%}%>
			
          <td width="1" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
