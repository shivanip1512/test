<html>
<%@ page import="com.cannontech.analysis.*" %>
<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.database.db.device.DeviceMeterGroup"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.database.model.ModelFactory"%>
<%@ page import="com.cannontech.roles.application.CommanderRole" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.roles.application.ReportingRole" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.stars.web.StarsYukonUser" %>
<%@page import="com.cannontech.roles.capcontrol.CBCSettingsRole" %>
<%@ page import="com.cannontech.database.db.company.EnergyCompany" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:standardPage module="reporting" title="Reports"> 
<cti:standardMenu/>
<%
	LiteYukonUser lYukonUser = (LiteYukonUser) session.getAttribute(ServletUtils.ATT_YUKON_USER);
	StarsYukonUser starsYukonUser = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
%>
<jsp:useBean id="REPORT_BEAN" class="com.cannontech.analysis.gui.ReportBean" scope="session"/>
	<jsp:setProperty name="REPORT_BEAN" property="energyCompanyID" value="<%=(DaoFactory.getEnergyCompanyDao().getEnergyCompany(lYukonUser)== null?EnergyCompany.DEFAULT_ENERGY_COMPANY_ID:DaoFactory.getEnergyCompanyDao().getEnergyCompany(lYukonUser).getEnergyCompanyID())%>"/>

<%-- Grab the search criteria --%>
<jsp:setProperty name="REPORT_BEAN" property="type" param="type"/>
<jsp:setProperty name="REPORT_BEAN" property="groupType" param="groupType"/>
<jsp:setProperty name="REPORT_BEAN" property="start" param="startDate"/>
<jsp:setProperty name="REPORT_BEAN" property="stop" param="stopDate"/>

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
	java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");
		
	String bulletImg = "<img src='../WebConfig/" + DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED) + "' width='9' height='9'>";
	String bulletImgExp = "<img src='../WebConfig/" + DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_EXPAND) + "' width='9' height='9'>";
	String connImgMid = "<img src='../WebConfig/" + DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_CONNECTOR_MIDDLE) + "' width='10' height='12'>";
	String connImgBtm = "<img src='../WebConfig/" + DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_CONNECTOR_BOTTOM) + "' width='10' height='12'>";
	
	String linkHtml = null;
	String linkImgExp = null;
%>

	  <form name="reportForm" method="post" action="<%=request.getContextPath()%>/servlet/ReportGenerator?" onSubmit="loadTarget(document.reportForm)">
	  <!-- THE EXTRA INPUT TYPES ARE MAKING THE DOWNLOAD DIALOG APPEAR TWO TIMES -->
	  <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
	  <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
	  <input type="hidden" name="ACTION" value="DownloadReport">
      <cti:titledContainer title="Report Selection">
            <table width="100%" border="0" cellspacing="2" cellpadding="0" bordercolor="#FFFFFF" align="center">
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
                <td class="main"><span class='NavText'>* Greater than 00:00, not inclusive</span></td>
                <td class="main">&nbsp;</td>
                <td class="main"><span class='NavText'>* Less than or equal to 00:00, inclusive</span></td>
                <td class="main">&nbsp;</td>
                <td class="main">&nbsp;</td>
              </tr>
			  <tr>			  
                <td class="main" width="25%" valign="top" style="padding-left:5; padding-top:5">
	              <table width="100%" border="0" cellspacing="0" cellpadding="0">
	                <%if ( REPORT_BEAN.getGroupType() >= 0)
					  {
                        int [] rptTypes = REPORT_BEAN.getReportTypes();
						for (int i = 0; i < rptTypes.length; i++){%>
				    	<tr>
						  <td class="main">
						  <input type="radio" name="type" value="<%=rptTypes[i]%>" <%=(rptTypes[i]==REPORT_BEAN.getType()?"checked":"")%> onclick='document.reportForm.ACTION.value="LoadParameters";document.reportForm.submit();'><%=ReportTypes.getReportName(rptTypes[i])%>
						  </td>
						</tr>
					    <%}
					  } else {%>
					    <tr>
						  <td class="main">&nbsp;
						  </td>
						</tr>
					  <%}%>
				  </table>
				</td>
                <td bgcolor="EEEEEE" class="main">&nbsp;</td>
				<td valign="top" style="padding-left:5; padding-top:5">
				  <table width="100%" border="0" cellspacing="0" cellpadding="0">				
				    <tr>
				      <td>
				   		<input id="startCal" type="text" name="startDate"  <%=(REPORT_BEAN.getModel() != null && REPORT_BEAN.getModel().useStartDate() ? "" : "DISABLED")%> value="<%= datePart.format(REPORT_BEAN.getStartDate()) %>" size="8">
				  		  <%=(REPORT_BEAN.getModel() != null && REPORT_BEAN.getModel().useStartDate() ? 
						  "<a id='startCalHref' href='javascript:openCalendar(document.reportForm.startCal)'><img src='"+ request.getContextPath() + "/WebConfig/yukon/Icons/StartCalendar.gif' width='20' height='15' align='ABSMIDDLE' border='0'></a>" : "<img src='"+ request.getContextPath() + "/WebConfig/yukon/Icons/StartCalendar.gif' width='20' height='15' align='ABSMIDDLE' border='0'>")%> 
					  </td>
			   		  <% if( (REPORT_BEAN.getModel() != null && REPORT_BEAN.getModel() instanceof com.cannontech.analysis.tablemodel.PointDataIntervalModel || 
			   		  		  REPORT_BEAN.getModel() instanceof com.cannontech.analysis.tablemodel.LoadControlVerificationModel) ){%>
					  <td width="45" class="columnHeader" align="center">Hour<BR>
					    <select name="startHour" id="startHourID">
					    <% for (int i = 0; i < 24; i++) {
						    String iStr = String.valueOf(i);
						    if( i < 10)	{ iStr = "0" + iStr;}%>
						    <option value="<%=i%>"><%=iStr%></option>
						  <%}%>
						</select>
					  </td>
					  <td width="45" class="columnHeader" align="center">Min<BR>
					    <select name="startMinute" id="startMinuteID">
					    <% for (int i = 0; i < 60; i=i+5) {
						    String iStr = String.valueOf(i);
							if( i < 10)	{ iStr = "0" + iStr;}%>
							<option value="<%=i%>"><%=iStr%></option>
						  <%}%>			  
						</select>
				 	  </td>
					  <%}%>
			    	</tr>
			      </table>
			    </td>
                <td bgcolor="EEEEEE" class="main">&nbsp;</td>
				<td valign="top" style="padding-left:5; padding-top:5">
				  <table width="100%" border="0" cellspacing="0" cellpadding="0">				
				    <tr>
					  <td>				
                        <input id="stopCal" type="text" name="stopDate"  <%=(REPORT_BEAN.getModel() != null && REPORT_BEAN.getModel().useStopDate() ? "" : "DISABLED")%> value="<%= datePart.format(REPORT_BEAN.getStopDate()) %>" size="8">
						  <%=(REPORT_BEAN.getModel() != null && REPORT_BEAN.getModel().useStopDate() ? 
        		          "<a id='stopCalHref' href='javascript:openCalendar(document.reportForm.stopCal)'><img src='"+ request.getContextPath() + "/WebConfig/yukon/Icons/StartCalendar.gif' width='20' height='15' align='ABSMIDDLE' border='0'></a>" : "<img src='"+ request.getContextPath() + "/WebConfig/yukon/Icons/StartCalendar.gif' width='20' height='15' align='ABSMIDDLE' border='0'>")%> 
                	  </td>
					  <% if( (REPORT_BEAN.getModel() != null && REPORT_BEAN.getModel() instanceof com.cannontech.analysis.tablemodel.PointDataIntervalModel || 
			   		  		  REPORT_BEAN.getModel() instanceof com.cannontech.analysis.tablemodel.LoadControlVerificationModel) ){%>

					  <td width="45" class="columnHeader" align="center">Hour<BR>
					    <select name="stopHour" id="stopHourID">
					    <% for (int i = 0; i < 24; i++) {
						    String iStr = String.valueOf(i);
						    if( i < 10)	{ iStr = "0" + iStr;}%>
						    <option value="<%=i%>"><%=iStr%></option>
						  <%}%>
						</select>
					  </td>
					  <td width="45" class="columnHeader" align="center">Min<BR>
					    <select name="stopMinute" id="stopMinuteID">
					    <% for (int i = 0; i < 60; i=i+5) {
						    String iStr = String.valueOf(i);
							if( i < 10)	{ iStr = "0" + iStr;}%>
							<option value="<%=i%>"><%=iStr%></option>
						  <%}%>			  
						</select>
				 	  </td>
					  <%}%>
			    	</tr>
			      </table>
			    </td>
				<td bgcolor="EEEEEE" class="main">&nbsp;</td>
				<td valign="top" class="main" style="padding-left:5; padding-top:5">
	              <table width="100%" border="0" cellspacing="0" cellpadding="0">
				    <tr>
					  <td class="main">
						<input type="radio" name="ext" value="pdf" checked>PDF<BR>
  						<input type="radio" name="ext" value="csv">CSV
					  </td>
					  <td class="main">
					    <input type="image" id="Generate" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButtonGray.gif" name="Generate" border="0" alt="Generate" align="middle" <%=(REPORT_BEAN.getModel() == null ? "DISABLED style='cursor:default'":"")%> onclick='document.reportForm.ACTION.value="DownloadReport";reportForm.submit();'>
					  </td>
					</tr>
				  </table>
				</td>
              </tr>
              <tr> 
                <td colspan="7">&nbsp;</td>
              </tr>
            </table>
         </cti:titledContainer>
         
	  <br>	  
	  
      <cti:titledContainer title="Options">
            <%=REPORT_BEAN.buildOptionsHTML()%>
      </cti:titledContainer>
      
	  <%if (REPORT_BEAN.getModel() != null && REPORT_BEAN.getModel().getFilterModelTypes() != null)
	  {%>
		  <br>	  
		  
          <cti:titledContainer title="Filter">
				<%=REPORT_BEAN.buildBaseOptionsHTML()%>
          </cti:titledContainer>
		<%}%>	  	  	  
      </form>
</cti:standardPage>