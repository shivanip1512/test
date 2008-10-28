<%@ page import="com.cannontech.amr.deviceread.model.DeviceReadJobLog" %>
<%@ page import="java.util.Vector" %>
<%@ page import="com.cannontech.analysis.tablemodel.ReportModelBase" %>
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
<%@ page import= "java.util.List" %>
<%@ page import= "java.util.ArrayList" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:verifyRolesAndProperties value="application.ReportingRole"/>

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
	/*
	switch(REPORT_BEAN.getReportGroup().ordinal()){
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
	}*/
	
	final ReportGroup reportGroup = REPORT_BEAN.getReportGroup();
	if( reportGroup == ReportGroup.ADMINISTRATIVE)
	    menuSelection = "reports|administrator";
	else if (reportGroup == ReportGroup.METERING)
	    menuSelection = "reports|metering";
	else if ( reportGroup == ReportGroup.STATISTICAL)
	    menuSelection = "reports|statistical";
	else if (reportGroup == ReportGroup.LOAD_MANAGEMENT)
	    menuSelection = "reports|management";
	else if (reportGroup == ReportGroup.CAP_CONTROL)
	    menuSelection = "reports|capcontrol";
	else if (reportGroup == ReportGroup.DATABASE)
	    menuSelection = "reports|database";
	else if (reportGroup == ReportGroup.STARS)
	    menuSelection = "reports|stars";
	else if (reportGroup == ReportGroup.CCURT)
	    menuSelection = "reports|cni";
	else if (reportGroup == ReportGroup.SETTLEMENT)
	    menuSelection = "reports|settlement";
	else
	    menuSelection = "reports";
	    
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

function checkDates(){
	var good = false;
	var startDate = $F('startCal');
	var stopDate = $F('stopCal');
	var myregex = /\d{1,2}\/\d{1,2}\/\d{4}/g;
	if(startDate.match(myregex) && stopDate.match(myregex)){
		var p = startDate.split("/");
		var t = stopDate.split("/");
		var realStartDate = new Date(p[2], (p[0]-1), p[1]);
		var realStopDate = new Date(t[2], t[0]-1, t[1]);
		if(realStartDate < realStopDate){
			loadTarget(document.reportForm);
		} else {
			if($F('startHourID').visible = true){
	            if(startDate == stopDate){
	            	var startHour = $F('startHourID');
	                var stopHour = $F('stopHourID');
	                if(startHour < stopHour){
	                	loadTarget(document.reportForm);
	                }else if(startHour == stopHour){
	                	var startMinute = $F('startMinuteID');
	                	var stopMinute = $F('stopMinuteID');
	                	if(startMinute >= stopMinute){
	                		alert("The start time must occur before the stop time. ");
	                        $('startCal').focus();
	                        return false;
	                	}else{
	                		loadTarget(document.reportForm);
	                	}
	                }else {
	                	alert("The start time must occur before the stop time. ");
	                    $('startCal').focus();
	                    return false;
	                }
	            }else{
	            	alert("The start date must occur before the stop date. ");
	                $('startCal').focus();
	                return false;
	            }
			} else {
				alert("The start date must occur before the stop date. ");
				$('startCal').focus();
				return false;
			}
		}
	} else {
		alert("One of the dates entered is not a valid date.");
		$('startCal').focus();
		return false;
	}
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
	
	final ReportModelBase model = REPORT_BEAN.getModel();
%>

	  <form name="reportForm" method="post" action="<%=request.getContextPath()%>/servlet/ReportGenerator?" onSubmit="return checkDates()">
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
	                <%if ( REPORT_BEAN.getReportGroup() != null)
					  {
                        Vector <ReportTypes> rptTypes = REPORT_BEAN.getReportTypes();
						for (ReportTypes reportType : rptTypes){%>
				    	<tr>
						  <td class="main">
						  <input type="radio" name="type" value="<%=reportType%>" <%=(reportType==REPORT_BEAN.getReportType()?"checked":"")%> onclick='document.reportForm.ACTION.value="LoadParameters";document.reportForm.submit();'><font title="<%= reportType.getDescription() %>"><%=reportType.getTitle()%></font>
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
                      
                        <tags:dateInputCalendar fieldId="startCal" 
                                                fieldName="startDate" 
                                                disabled="<%=(model != null && model.useStartDate() ? false : true)%>" 
                                                fieldValue="<%= datePart.format(REPORT_BEAN.getStartDate()) %>"/>
				   		
					  </td>
			   		  <% if( (model != null && model instanceof com.cannontech.analysis.tablemodel.PointDataIntervalModel || 
			   		  		  model instanceof com.cannontech.analysis.tablemodel.LoadControlVerificationModel) ){%>
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
                      
                        <tags:dateInputCalendar fieldId="stopCal" 
                                                fieldName="stopDate" 
                                                disabled="<%=(model != null && model.useStopDate() ? false : true)%>" 
                                                fieldValue="<%= datePart.format(REPORT_BEAN.getStopDate()) %>"/>
                                                	
                	  </td>
					  <% if( (model != null && model instanceof com.cannontech.analysis.tablemodel.PointDataIntervalModel || 
			   		  		  model instanceof com.cannontech.analysis.tablemodel.LoadControlVerificationModel) ){%>

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
					    <input type="image" id="Generate" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButtonGray.gif" name="Generate" border="0" alt="Generate" align="middle" <%=(model == null ? "DISABLED style='cursor:default'":"")%> onclick='document.reportForm.ACTION.value="DownloadReport"; return true;'>
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

        function changeFilter(filterBy) {
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
                    		<BR><span class='NavText'>* Enter a comma separated list of Meter Number(s).</span><br></div>
            			<%} else if( filter.equals(ReportFilter.DEVICE)) {%>
                    		<div id="Div<%=filter.getFilterTitle()%>" style="display:<%=isFirst==0?"true":"none"%>">
		                    <input type='text' name='filterDeviceValues' style='width:650px;'/>
        		            <BR><span class='NavText'>* Enter a comma separated list of Device Name(s).</span><br></div>                    
            			<% }else {%>
            				<div id="Div<%=filter.getFilterTitle()%>" style="display:<%=isFirst==0?"true":"none"%>">
                    		<select id="selectFilterValues" name='filterValues' size='10' multiple style='width:350px;'>
                			<%List objects = filterObjectsMap.get(filter);%>
                			<%if (objects != null) {
                    			for (Object object : objects) {
                        			if( object instanceof String) {%>
                                    	<option value='<c:out value="<%=object.toString()%>" />'><c:out value="<%=object.toString()%>" /></option>
                        			<%}else if (object instanceof LiteYukonPAObject) {%>
                                    	<option value='<c:out value="<%=((LiteYukonPAObject)object).getYukonID()%>" />'><c:out value="<%=((LiteYukonPAObject)object).getPaoName()%>" /></option>
                        			<%}else if (object instanceof LiteDeviceMeterNumber){%>
                                    	<option value='<c:out value="<%=((LiteDeviceMeterNumber)object).getDeviceID()%>" />'><c:out value="<%=((LiteDeviceMeterNumber)object).getMeterNumber()%>" /></option>
                        			<%}else if (object instanceof DeviceReadJobLog){%>
                                    	<option value='<c:out value="<%=((DeviceReadJobLog)object).getDeviceReadJobLogID()%>" />'><c:out value="<%=((DeviceReadJobLog)object).toString()%>" /></option>
                        		<%}
                    			}
                			}%>
                        	</select>
                			<BR><span class='NavText'>* Hold &lt;CTRL&gt; key down to select multiple values</span><br>
                			<span class='NavText'>* Hold &lt;Shift&gt; key down to select range of values</span>
                      	</div>
            			<% } %>
            		<% isFirst++; %>
        			<% }%>
        		</td>
        	</tr>
        </table>
        </cti:titledContainer>
		<%}%>	  	  	  
      </form>
</cti:standardPage>