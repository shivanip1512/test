<html>
<%@ page import="com.cannontech.amr.deviceread.model.DeviceReadJobLog" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.cannontech.analysis.*" %>
<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.database.db.device.DeviceMeterGroup"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject"%>
<%@ page import="com.cannontech.database.data.lite.LiteDeviceMeterNumber"%>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter"%>
<%@ page import="com.cannontech.roles.application.CommanderRole" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.roles.application.ReportingRole" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.stars.web.StarsYukonUser" %>
<%@ page import="com.cannontech.roles.capcontrol.CBCSettingsRole" %>
<%@ page import="com.cannontech.database.db.company.EnergyCompany" %>
<%@ page import= "java.util.List;" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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

<%
	String menuSelection = null;
	switch(REPORT_BEAN.getGroupType()){
		case 0:
		    menuSelection = "reports|administrator";
		    break;
		case 1:
		    menuSelection = "reports|metering";
		    break;
		case 2:
		    menuSelection = "reports|statistical";
		    break;
		case 3:
		    menuSelection = "reports|management";
		    break;
		case 4:
		    menuSelection = "reports|capcontrol";
		    break;
		case 5:
		    menuSelection = "reports|database";
		    break;
		case 6:
		    menuSelection = "reports|stars";
		    break;
		case 8:
		    menuSelection = "reports|cni";
		    break;
		case 100:
		    menuSelection = "reports|settlement";
		    break;
		default:
		    menuSelection = "reports";
		    break;
	}
%>


<cti:standardPage module="reporting" title="Reports"> 
<cti:standardMenu menuSelection="<%= menuSelection %>"/>

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
						  <input type="radio" name="type" value="<%=rptTypes[i]%>" <%=(rptTypes[i]==REPORT_BEAN.getType()?"checked":"")%> onclick='document.reportForm.ACTION.value="LoadParameters";document.reportForm.submit();'><font title="<%= ReportTypes.getReportDescription(rptTypes[i]) %>"><%=ReportTypes.getReportName(rptTypes[i])%></font>
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
					    <input type="image" id="Generate" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButtonGray.gif" name="Generate" border="0" alt="Generate" align="middle" <%=(REPORT_BEAN.getModel() == null ? "DISABLED style='cursor:default'":"")%> onclick='document.reportForm.ACTION.value="DownloadReport";return true;'>
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
      
	  <%if (REPORT_BEAN.hasFilter())
	  {%>
		  <br>	  
		  
          <cti:titledContainer title="Filter">
			<SCRIPT>
        function selectNoneFilter(group){
          group.selectedIndex = -1;
          group.disabled = false;
          document.reportForm.selectAll.checked = false;
        }
        
        function selectAllFilter(selectVal, group){
          if (selectVal) {
            group.disabled = true;
            for (var i = 0; i < group.length; i++) {
              group[i].selected = true;     
            }
          } else {
            group.disabled = false;
          }
        }
        
        function changeFilter(filterBy) {
          document.getElementById('selectAll').disabled = (filterBy == -1);
          var filterModelValues = document.reportForm.filterValues;
          for (var i = 0; i < filterModelValues.length; i++) {
            filterModelValues[i].selectedIndex = -1;
          }
        
        <%
        //Create a local instance of the map.
        Map<ReportFilter,List<? extends Object>> filterObjectsMap = REPORT_BEAN.getFilterObjectsMap();
        for (ReportFilter filter : filterObjectsMap.keySet()) {%>
				document.getElementById('Div<%=filter.getFilterTitle()%>').style.display = (filterBy == <%=filter.ordinal()%>)? "block" : "none";
        <% }  %>
        }
    
        </SCRIPT>

		<table width='100%' border='0' cellspacing='0' cellpadding='0' align='center'>
        	<tr>
            	<td class='main' style='padding-left:5; padding-top:5'>
					<div id='DivFilterModelType' style='display:true'>
						<select id='filterModelType' name='filterModelType' onChange='changeFilter(this.value)'>
							<%for (ReportFilter filter : filterObjectsMap.keySet()) {%>
                    			<option value='<%=filter.ordinal()%>'>  <%=filter.getFilterTitle() %></option>
        					<% } %>
                		</select>
					</div>
        		</td>
          	</tr>
          	<tr><td height='9'></td></tr>
          	<tr>
            	<td class='main' valign='top' height='19' style='padding-left:5; padding-top:5'>
        			<% int isFirst = 0; %>
        			<%for(ReportFilter filter: filterObjectsMap.keySet()) {%>
            			<%if( filter.equals(ReportFilter.METER ) ){%>
                    		<div id="Div<%=filter.getFilterTitle()%>" style="display:<%=isFirst==0?"true":"none"%>">
                    		<input type='text' name="filterMeterValues" style='width:650px;'/>
                    		<input type='hidden' name='selectAll' value='-1'>
                    		<input type = 'hidden' name='filterValues' value = '-1'>
                    		<BR><span class='NavText'>* Enter a comma separated list of Meter Number(s).</span><br></div>
            			<%} else if( filter.equals(ReportFilter.DEVICE)) {%>
                    		<div id="Div<%=filter.getFilterTitle()%>" style="display:<%=isFirst==0?"true":"none"%>">
		                    <input type='text' name='filterDeviceValues' style='width:650px;'/>
		                    <input type='hidden' name='selectAll' value='-1'>
		                    <input type = 'hidden' name='filterValues' value = '-1' >
        		            <BR><span class='NavText'>* Enter a comma separated list of Device Name(s).</span><br></div>                    
            			<% }else {%>
            				<div id="Div<%=filter.getFilterTitle()%>" style="display:<%=isFirst==0?"true":"none"%>">
                    		<select name='filterValues' size='10' multiple style='width:350px;'>
                			<%List objects = filterObjectsMap.get(filter);%>
                			<%if (objects != null) {
                    			for (Object object : objects) {
                        			if( object instanceof String) {%>
                                    	<option value='<%=object.toString()%>'><%=object.toString()%></option>
                        			<%}else if (object instanceof LiteYukonPAObject) {%>
                                    	<option value='<%=((LiteYukonPAObject)object).getYukonID()%>'><%=((LiteYukonPAObject)object).getPaoName()%></option>
                        			<%}else if (object instanceof LiteDeviceMeterNumber){%>
                                    	<option value='<%=((LiteDeviceMeterNumber)object).getDeviceID()%>'><%=((LiteDeviceMeterNumber)object).getMeterNumber()%></option>
                        			<%}else if (object instanceof DeviceReadJobLog){%>
                                    	<option value='<%=((DeviceReadJobLog)object).getDeviceReadJobLogID()%>'><%=((DeviceReadJobLog)object).toString()%></option>
                        		<%}
                    			}
                			}%>
                        	</select>
                			<BR><span class='NavText'>* Hold &ltCTRL&gt key down to select multiple values</span><br>
                			<span class='NavText'>* Hold &ltShift&gt key down to select range of values</span>
                      		<div id='DivSelectAll' style='display:true'>
                        	<input type='checkbox' name='selectAll' value='selectAll' onclick='selectAllFilter(this.checked, document.reportForm.filterValues);'>Select All
                      		</div>
                      		<div id='DivSelectNone' style='display:true'>         
                        	<input type='button' value='Unselect All' onclick='selectNoneFilter(document.reportForm.filterValues);'/>
                      		</div>                
                      	</div>
            			<% } %>
            		<% isFirst++; %>
        			<% }%>
        		</td>
        	</tr>
          	<tr>
            	<td class='main' height='10' style='padding-left:5; padding-top:5'>
        		</td>
          	</tr>
        </table>
        </cti:titledContainer>
		<%}%>	  	  	  
      </form>
</cti:standardPage>