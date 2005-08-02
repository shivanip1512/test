<html>
<%@ page import="com.cannontech.analysis.*" %>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%@ page import="com.cannontech.database.db.device.DeviceMeterGroup"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.database.model.ModelFactory"%>
<%@ page import="com.cannontech.roles.application.CommanderRole" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.roles.application.ReportingRole" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>

<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<cti:checklogin/> 
<jsp:useBean id="REPORT_BEAN" class="com.cannontech.analysis.gui.ReportBean" scope="session"/>
<%-- Grab the search criteria --%>
<jsp:setProperty name="REPORT_BEAN" property="type" param="type"/>
<jsp:setProperty name="REPORT_BEAN" property="groupType" param="groupType"/>
<jsp:setProperty name="REPORT_BEAN" property="start" param="startDate"/>
<jsp:setProperty name="REPORT_BEAN" property="stop" param="stopDate"/>
<head>
<title>Yukon Reporting</title>
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<link rel="stylesheet" href="../WebConfig/yukon/Base.css" type="text/css">
</head>
<body class="#FFFFFF" leftmargin="0" topmargin="0">
<META NAME="robots" CONTENT="noindex, nofollow">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script language="JavaScript" src="<%= request.getContextPath() %>/JavaScript/change_monitor.js"></script>
<!-- Java script needed for the Calender Function--->
<SCRIPT  LANGUAGE="JavaScript" SRC="../JavaScript/calendar.js"></SCRIPT>
<script language="JavaScript">
<!--
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
// -->
</script>
<script>
function loadTarget(form)
{
	var extGroup = form.ext;
	for (var i = 0; i < extGroup.length; i++)
	{
		if( extGroup[i].checked)
		{
			if( extGroup[i].value == 'png')
			{
				form.target = '_blank';
				form.REDIRECT.value = '../analysis/reporting_png.jsp';
			}
			else
			{
				form.target = "";
			}
		}
	}
}

function enableDates(value)
{
	if( value)
	{
		document.getElementById("StartCalHref").style.cursor = 'pointer';
		document.getElementById("StopCalHref").style.cursor = 'pointer';
		document.getElementById("StartCalHref").href = 'javascript:openCalendar(document.reportForm.startCal)'
		document.getElementById("StopCalHref").href = 'javascript:openCalendar(document.reportForm.stopCal)'
	}
	else
	{
		document.getElementById("StartCalHref").style.cursor = 'default';
		document.getElementById("StopCalHref").style.cursor = 'default';
		document.getElementById("StartCalHref").href = 'javascript:;'
		document.getElementById("StopCalHref").href = 'javascript:;'
	}

	document.getElementById("startCal").disabled = !value;
	document.getElementById("stopCal").disabled = !value;

}
</script>
<%
	LiteYukonUser lYukonUser = (LiteYukonUser) session.getAttribute(ServletUtils.ATT_YUKON_USER);
	java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");
		
	String bulletImg = "<img src='../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED, "Bullet.gif") + "' width='9' height='9'>";
	String bulletImgExp = "<img src='../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_EXPAND, "BulletExpand.gif") + "' width='9' height='9'>";
	String connImgMid = "<img src='../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_CONNECTOR_MIDDLE, "MidConnector.gif") + "' width='10' height='12'>";
	String connImgBtm = "<img src='../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_CONNECTOR_BOTTOM, "BottomConnector.gif") + "' width='10' height='12'>";
	
	String linkHtml = null;
	String linkImgExp = null;
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" height="96">
        <tr> 
          <td width="63%" valign="top"> 
            <layer id="Layer1" pagex="0" width="100%" height="96" z-index="1"> 
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td> 
                  <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" height="53">
                    <tr> 
                      <td width="63%"><!--<img src="../WebConfig/yukon/YourLogo.gif">--></td>
                      <td width="37%"> 
                        <div align="right"><img src="../WebConfig/yukon/ReportHeader.gif"></div>
                      </td>
                    </tr>
                  </table>
                  <table width="100%" border="0" cellspacing="0" cellpadding="0" height="43">
                    <tr> 
                      <td height="1" bgcolor="888888"></td>
                    </tr>
                    <tr> 
                      <td class="menubackground"> 
                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td width="51%" height="23">
                              &nbsp;&nbsp; <a href="#" class="menuLink">Report</a></td>
                            <td width="49%"> 
                              <div align="right"><span class="menu">Module:</span> 
                                <select name="select" onChange="javascript:window.location=(this[this.selectedIndex].value);">
                                  <option value="<%=request.getContextPath()%>/analysis/Reports.jsp" >Reporting</option>
                                  <cti:checkRole roleid="<%= CommanderRole.ROLEID %>">
                                  <option value="<%=request.getContextPath()%>/apps/SelectDevice.jsp">Commander</option>
                                  </cti:checkRole>
                                  <option value="<%=request.getContextPath()%>/operator/Operations.jsp">Home</option>
                                </select>
                                &nbsp;&nbsp;&nbsp; <a href="#" class="menuLink">Help</a> 
                                &nbsp;&nbsp; <a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="menuLink">Log 
                                Out</a> &nbsp;&nbsp;</div>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    <tr> 
                      <td height="1" bgcolor="888888"></td>
                    </tr>
                    <tr> 
                      <td class="submenubackground" height="20">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
                          <tr> 					  
							<td height="20">
					        <cti:checkProperty propertyid="<%=ReportingRole.ADMIN_REPORTS_GROUP%>">
							&nbsp;&nbsp;<a href='<%=request.getContextPath()%>/analysis/Reports.jsp?groupType=<%=ReportTypes.ADMIN_REPORTS_GROUP%>' class='<%=(REPORT_BEAN.getGroupType() == ReportTypes.ADMIN_REPORTS_GROUP ? "submenu" :"submenuLink")%> onclick='return warnUnsavedChanges();'
								onMouseOver="window.status='Administrative Reports';return true;"
							  	onMouseOut="window.status='';return true;">
							  	<cti:getProperty propertyid="<%= ReportingRole.ADMIN_REPORTS_GROUP_LABEL%>" defaultvalue="<%=ReportTypes.getReportGroupName(ReportTypes.ADMIN_REPORTS_GROUP)%>"/></a>
					        </cti:checkProperty>
					        <cti:checkProperty propertyid="<%=ReportingRole.AMR_REPORTS_GROUP%>">
							&nbsp;&nbsp;<a href='<%=request.getContextPath()%>/analysis/Reports.jsp?groupType=<%=ReportTypes.AMR_REPORTS_GROUP%>' class='<%=(REPORT_BEAN.getGroupType() == ReportTypes.AMR_REPORTS_GROUP ? "submenu" :"submenuLink")%> onclick='return warnUnsavedChanges();'
								onMouseOver="window.status='AMR Reports';return true;"
							  	onMouseOut="window.status='';return true;">
							  	<cti:getProperty propertyid="<%= ReportingRole.AMR_REPORTS_GROUP_LABEL%>" defaultvalue="<%=ReportTypes.getReportGroupName(ReportTypes.AMR_REPORTS_GROUP)%>"/></a>
					        </cti:checkProperty>
					        <cti:checkProperty propertyid="<%=ReportingRole.CAP_CONTROL_REPORTS_GROUP%>">
							&nbsp;&nbsp;<a href='<%=request.getContextPath()%>/analysis/Reports.jsp?groupType=<%=ReportTypes.CAP_CONTROL_REPORTS_GROUP%>' class='<%=(REPORT_BEAN.getGroupType() == ReportTypes.CAP_CONTROL_REPORTS_GROUP ? "submenu" :"submenuLink")%> onclick='return warnUnsavedChanges();'
								onMouseOver="window.status='Capacitor Control Reports';return true;"
							  	onMouseOut="window.status='';return true;">
								<cti:getProperty propertyid="<%= ReportingRole.CAP_CONTROL_REPORTS_GROUP_LABEL%>" defaultvalue="<%=ReportTypes.getReportGroupName(ReportTypes.CAP_CONTROL_REPORTS_GROUP)%>"/></a>
					        </cti:checkProperty>
					        <cti:checkProperty propertyid="<%=ReportingRole.STATISTICAL_REPORTS_GROUP%>">
							&nbsp;&nbsp;<a href='<%=request.getContextPath()%>/analysis/Reports.jsp?groupType=<%=ReportTypes.STATISTICAL_REPORTS_GROUP%>' class='<%=(REPORT_BEAN.getGroupType() == ReportTypes.STATISTICAL_REPORTS_GROUP ? "submenu" :"submenuLink")%> onclick='return warnUnsavedChanges();'
								onMouseOver="window.status='Communication Statistics Reports';return true;"
							  	onMouseOut="window.status='';return true;">
								<cti:getProperty propertyid="<%= ReportingRole.STATISTICAL_REPORTS_GROUP_LABEL%>" defaultvalue="<%=ReportTypes.getReportGroupName(ReportTypes.STATISTICAL_REPORTS_GROUP)%>"/></a>
					        </cti:checkProperty>
					        <cti:checkProperty propertyid="<%=ReportingRole.DATABASE_REPORTS_GROUP%>">
							&nbsp;&nbsp;<a href='<%=request.getContextPath()%>/analysis/Reports.jsp?groupType=<%=ReportTypes.DATABASE_REPORTS_GROUP%>' class='<%=(REPORT_BEAN.getGroupType() == ReportTypes.DATABASE_REPORTS_GROUP ? "submenu" :"submenuLink")%> onclick='return warnUnsavedChanges();'
								onMouseOver="window.status='Database Reports';return true;"
							  	onMouseOut="window.status='';return true;">
								<cti:getProperty propertyid="<%= ReportingRole.DATABASE_REPORTS_GROUP_LABEL%>" defaultvalue="<%=ReportTypes.getReportGroupName(ReportTypes.DATABASE_REPORTS_GROUP)%>"/></a>
					        </cti:checkProperty>
					        <cti:checkProperty propertyid="<%=ReportingRole.LOAD_MANAGEMENT_REPORTS_GROUP%>">
							&nbsp;&nbsp;<a href='<%=request.getContextPath()%>/analysis/Reports.jsp?groupType=<%=ReportTypes.LOAD_MANAGEMENT_REPORTS_GROUP%>' class='<%=(REPORT_BEAN.getGroupType() == ReportTypes.LOAD_MANAGEMENT_REPORTS_GROUP ? "submenu" :"submenuLink")%> onclick='return warnUnsavedChanges();'
								onMouseOver="window.status='Load Management Reports';return true;"
							  	onMouseOut="window.status='';return true;">
							  	<cti:getProperty propertyid="<%= ReportingRole.LOAD_MANAGEMENT_REPORTS_GROUP_LABEL%>" defaultvalue="<%=ReportTypes.getReportGroupName(ReportTypes.LAOD_MANAGEMENT_REPORTS_GROUP)%>"/></a>
					        </cti:checkProperty>
					        <cti:checkProperty propertyid="<%=ReportingRole.STARS_REPORTS_GROUP%>">
							&nbsp;&nbsp;<a href='<%=request.getContextPath()%>/analysis/Reports.jsp?groupType=<%=ReportTypes.STARS_REPORTS_GROUP%>' class='<%=(REPORT_BEAN.getGroupType() == ReportTypes.STARS_REPORTS_GROUP ? "submenu" :"submenuLink")%> onclick='return warnUnsavedChanges();'
								onMouseOver="window.status='STARS Reports';return true;"
							  	onMouseOut="window.status='';return true;">
							  	<cti:getProperty propertyid="<%= ReportingRole.STARS_REPORTS_GROUP_LABEL%>" defaultvalue="<%=ReportTypes.getReportGroupName(ReportTypes.STARS_REPORTS_GROUP)%>"/></a>
					        </cti:checkProperty>
							</td>
						  </tr>
						</table>
					  </td>
                    </tr>
                    <tr> 
                      <td height="1" bgcolor="888888"></td>
                    </tr>
                  </table>
                </td>
            </table>
            </layer>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
 
     
    <td> 
      <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center" height="30">
        <tr>
		  <% if ( REPORT_BEAN.getGroupType() >= 0) {%>
          <td valign="bottom"><a href="#" class="crumbs">Report</a> <span class="crumbs">&gt;</span> 
            <a href="<%=request.getContextPath()%>/analysis/Reports.jsp?groupType=<%=REPORT_BEAN.getGroupType()%>" class="crumbs"><%=ReportTypes.getReportGroupName(REPORT_BEAN.getGroupType())%></a>
		  </td>
		  <%}%>
<!--          <td valign="middle"> 
            <div align="right">
              <p class="main">Find: 
                <input type="text" name="textfield">
                <img src="../WebConfig/yukon/Buttons/GoButtonGray.gif" width="23" height="20"> </p>
            </div>
          </td>
-->
        </tr>
      </table>
      <br>
	  <form name="reportForm" method="post" action="<%=request.getContextPath()%>/servlet/ReportGenerator?" onSubmit="loadTarget(document.reportForm)">
	  <!-- THE EXTRA INPUT TYPES ARE MAKING THE DOWNLOAD DIALOG APPEAR TWO TIMES -->
	  <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
	  <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
	  <input type="hidden" name="ACTION" value="DownloadReport">
      <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="6" height="19"><img src="../WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
          <td height="19" bgcolor="888888" class="tableHeader">&nbsp;</td>          
          <td width="6" height="19"><img src="../WebConfig/yukon/Header_right.gif" width="6" height="19"></td>
        </tr>
        <tr>
          <td width="6" background="../WebConfig/yukon/Side_left.gif">&nbsp;</td>
          <td>
            <table width="100%" border="1" cellspacing="0" cellpadding="0" bordercolor="#FFFFFF" align="center">
              <tr>
				<td class="columnHeader">Reports</td>
                <td class="columnHeader">&nbsp;</td>
                <td class="columnHeader">Start Date</td>
                <td class="columnHeader">&nbsp;</td>
                <td class="columnHeader">Stop Date</td>
                <td class="columnHeader">&nbsp;</td>
                <td class="columnHeader">Format</td>
			  </tr>
			  <tr bgcolor="EEEEEE">
                <td class="main">&nbsp;</td>
                <td class="main">&nbsp;</td>
                <td class="main">&nbsp;</td>
                <td class="main">&nbsp;</td>
                <td class="main">&nbsp;</td>
                <td class="main">&nbsp;</td>
                <td class="main">&nbsp;</td>
              </tr>
			  <tr>			  
                <td class="main" width="25%" valign="top" style="padding-left:5; padding-top:5">
	              <table width="100%" border="0" cellspacing="0" cellpadding="0">
					<% if ( REPORT_BEAN.getGroupType() >= 0) {
						int [] rptTypes = ReportTypes.getGroupToTypeMap()[REPORT_BEAN.getGroupType()];
						for (int i = 0; i < rptTypes.length; i++){%>
				    	<tr>
						  <td class="main">
						  <input type="radio" name="type" value="<%=rptTypes[i]%>" <%=(rptTypes[i]==REPORT_BEAN.getType()?"checked":"")%> onclick='document.reportForm.ACTION.value="LoadParameters";document.reportForm.submit();'><%=ReportTypes.getReportName(rptTypes[i])%>
						  </td>
						</tr>
					    <%}
					  }%>
				  </table>
				</td>
                <td bgcolor="EEEEEE" class="main">&nbsp;</td>
				<td valign="top" style="padding-left:5; padding-top:5">
				  <input id="startCal" type="text" name="startDate"  <%=(REPORT_BEAN.getModel() != null && REPORT_BEAN.getModel().useStartDate() ? "" : "DISABLED")%> value="<%= datePart.format(REPORT_BEAN.getStartDate()) %>" size="8">
				  <%=(REPORT_BEAN.getModel() != null && REPORT_BEAN.getModel().useStartDate() ? 
				  "<a id='startCalHref' href='javascript:openCalendar(document.reportForm.startCal)'><img src='"+ request.getContextPath() + "/WebConfig/yukon/Icons/StartCalendar.gif' width='20' height='15' align='ABSMIDDLE' border='0'></a>" : "<img src='"+ request.getContextPath() + "/WebConfig/yukon/Icons/StartCalendar.gif' width='20' height='15' align='ABSMIDDLE' border='0'>")%> 
			    </td>
                <td bgcolor="EEEEEE" class="main">&nbsp;</td>
				<td valign="top" style="padding-left:5; padding-top:5">
                  <input id="stopCal" type="text" name="stopDate"  <%=(REPORT_BEAN.getModel() != null && REPORT_BEAN.getModel().useStopDate() ? "" : "DISABLED")%> value="<%= datePart.format(REPORT_BEAN.getStopDate()) %>" size="8">
				  <%=(REPORT_BEAN.getModel() != null && REPORT_BEAN.getModel().useStopDate() ? 
                  "<a id='stopCalHref' href='javascript:openCalendar(document.reportForm.stopCal)'><img src='"+ request.getContextPath() + "/WebConfig/yukon/Icons/StartCalendar.gif' width='20' height='15' align='ABSMIDDLE' border='0'></a>" : "<img src='"+ request.getContextPath() + "/WebConfig/yukon/Icons/StartCalendar.gif' width='20' height='15' align='ABSMIDDLE' border='0'>")%> 
                </td>
				<td bgcolor="EEEEEE" class="main">&nbsp;</td>
				<td valign="top" class="main" style="padding-left:5; padding-top:5">
	              <table width="100%" border="0" cellspacing="0" cellpadding="0">
				    <tr>
					  <td class="main">
						<input type="radio" name="ext" value="pdf" checked=true>PDF<BR>
  						<input type="radio" name="ext" value="csv">CSV
					  </td>
					  <td class="main">
					    <input type="image" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButtonGray.gif" name="Generate" border="0" alt="Generate" align="middle" <%=(REPORT_BEAN.getModel() == null ? "DISABLED style='cursor:default'":"")%> onclick='document.reportForm.ACTION.value="DownloadReport";reportForm.submit();'>
					  </td>
					</tr>
				  </table>
				</td>
              </tr>
              <tr> 
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
              </tr>
            </table>
          </td>
          <td width="6" background="../WebConfig/yukon/Side_right.gif">&nbsp;</td>
        </tr>
        <tr>
          <td width="6" height="9"><img src="../WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
          <td background="../WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
          <td width="6" height="9"><img src="../WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
        </tr>
      </table>
	  <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
		<tr> 
		  <td><br>
		  </td>
		</tr>
	  </table>	  
	  
      <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="6" height="19"><img src="../WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
          <td height="19" bgcolor="888888" class="tableHeader">Options</td>
          <td width="6" height="19"><img src="../WebConfig/yukon/Header_right.gif" width="6" height="19"></td>
        </tr>
        <tr>
          <td width="6" background="../WebConfig/yukon/Side_left.gif">&nbsp;</td>
          <td class='main' valign='middle'>
            <%=REPORT_BEAN.buildOptionsHTML()%>
          </td>
          <td width="6" background="../WebConfig/yukon/Side_right.gif">&nbsp;</td>
        </tr>
        <tr>
          <td width="6" height="9"><img src="../WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
          <td background="../WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
          <td width="6" height="9"><img src="../WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
        </tr>
      </table>
	  <%if (REPORT_BEAN.getModel() != null && REPORT_BEAN.getModel().getFilterModelTypes() != null)
	  {%>
		  <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
			<tr> 
			  <td> <br>
			  </td>
			</tr>
		  </table>	  
		  
		  <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center">
			<tr> 
			  <td width="6" height="19"><img src="../WebConfig/yukon/Header_left.gif" width="6" height="19"></td>
			  <td height="19" bgcolor="888888" class="tableHeader">Filter</td>
			  <td width="6" height="19"><img src="../WebConfig/yukon/Header_right.gif" width="6" height="19"></td>
			</tr>
			<tr>
			  <td width="6" background="../WebConfig/yukon/Side_left.gif">&nbsp;</td>
			  <td>
				<%=REPORT_BEAN.buildBaseOptionsHTML()%>
			  </td>
			  <td width="6" background="../WebConfig/yukon/Side_right.gif">&nbsp;</td>
			</tr>
			<tr>
			  <td width="6" height="9"><img src="../WebConfig/yukon/Bottom_left.gif" width="6" height="9"></td>
			  <td background="../WebConfig/yukon/Bottom.gif" valign="bottom" height="9"></td>
			  <td width="6" height="9"><img src="../WebConfig/yukon/Bottom_right.gif" width="6" height="9"></td>
			</tr>
		  </table>
		<%}%>	  	  	  
      </form>
    </td>
  
  </tr>
  </table>
</body>
</html>