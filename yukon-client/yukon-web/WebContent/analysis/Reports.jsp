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
<%@ page import="com.cannontech.database.db.capcontrol.LiteCapControlStrategy" %>
<%@ page import="com.cannontech.analysis.ReportFilter"%>
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


<%@page import="com.cannontech.analysis.controller.ReportController"%>
<cti:verifyRolesAndProperties value="REPORTING"/>

<%@page import="com.cannontech.message.capcontrol.streamable.StreamableCapObject"%><cti:verifyRolesAndProperties value="REPORTING"/>

<%
	LiteYukonUser lYukonUser = (LiteYukonUser) session.getAttribute(ServletUtils.ATT_YUKON_USER);
%>
<jsp:useBean id="REPORT_BEAN" class="com.cannontech.analysis.gui.ReportBean" scope="session"/>
<jsp:setProperty name="REPORT_BEAN" property="energyCompanyID" value="<%=(DaoFactory.getEnergyCompanyDao().getEnergyCompany(lYukonUser)== null?EnergyCompany.DEFAULT_ENERGY_COMPANY_ID:DaoFactory.getEnergyCompanyDao().getEnergyCompany(lYukonUser).getEnergyCompanyID())%>"/>

<%-- Grab the search criteria --%>
<jsp:setProperty name="REPORT_BEAN" property="groupType" param="groupType"/>
<jsp:setProperty name="REPORT_BEAN" property="type" param="type"/>
<jsp:setProperty name="REPORT_BEAN" property="start" param="startDate"/>
<jsp:setProperty name="REPORT_BEAN" property="stop" param="stopDate"/>
<jsp:setProperty name="REPORT_BEAN" property="stop" param="stopDate"/>
<jsp:setProperty name="REPORT_BEAN" property="selectedReportFilter" param="selectedReportFilter"/> <%-- enum value of the ReportFilter to be selected initially Ex: GROUP Ex: METER. Optional, set to first filter is not present. --%>
<jsp:setProperty name="REPORT_BEAN" property="selectedReportFilterValues" param="selectedReportFilterValues"/> <%-- comma separated list of values to be selected initially. Ex: 01234599 Ex: /Meters/X,/Meters/Y. Optional, blank default value if not set --%>

<% 
    REPORT_BEAN.setUserID(lYukonUser.getUserID());
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

Event.observe (window, 'load', makeFirstSelectedFilterValueVisible);

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
	if(value) {
		$('calImg_startCal').show();
		$('calImg_stopCal').show();
	} else {
		$('calImg_startCal').hide();
		$('calImg_stopCal').hide();
	}

	$('startCal').disabled = !value;
	$('stopCal').disabled = !value;
	$('stopHourID').disabled = !value;
	$('stopMinuteID').disabled = !value;
	$('startHourID').disabled = !value;
	$('startMinuteID').disabled = !value;
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
		
		if (jQuery('#startCal')[0].disabled || jQuery('#stopCal')[0].disabled || realStartDate < realStopDate) {
			loadTarget(document.reportForm);
		} else {
			if($('startHourID')) {    //Check that one of the time fields exists 
	            if(startDate == stopDate){
	            	var startHour = Number($F('startHourID')); 
	                var stopHour = Number($F('stopHourID')); 
	                if(startHour < stopHour){
	                	loadTarget(document.reportForm);
	                }else if(startHour == stopHour){
	                	var startMinute = Number($F('startMinuteID')); 
	                	var stopMinute = Number($F('stopMinuteID')); 
	                	if(startMinute >= stopMinute){
	                		alert("<cti:msg key="yukon.common.errors.time.startBeforeStop"/>");
	                        $('startCal').focus();
	                        return false;
	                	}else{
	                		loadTarget(document.reportForm);
	                	}
	                }else {
	                	alert("<cti:msg key="yukon.common.errors.time.startBeforeStop"/>");
	                    $('startCal').focus();
	                    return false;
	                }
	            }else{
	            	alert("<cti:msg key="yukon.common.errors.date.startBeforeStop"/>");
	                $('startCal').focus();
	                return false;
	            }
			} else {
				alert("<cti:msg key="yukon.common.errors.date.startBeforeStop"/>");
				$('startCal').focus();
				return false;
			}
		}
	} else {
		alert("<cti:msg key="yukon.common.errors.date.oneInvalid"/>");
		$('startCal').focus();
		return false;
	}
}

function makeFirstSelectedFilterValueVisible() {
	
	var listbox = $('selectFilterValues');	
    if(listbox) {
    	var selectedOptions = new Array();
    	y=0;
    		for (x=0;x<listbox.options.length;x++){
    		if (listbox.options[x].selected){
    				selectedOptions[y]=x;
    				y++;
    		}
    		} 
    	listbox.selectedIndex=0;//this and next line prompt the listbox to 'wake up' 
    	listbox.options[0].selected=false; 
    	for (y=selectedOptions.length-1;y>-1;y--){//start from the end and work backwards so first selected item is the one scrolled to. 
    		listbox.options[selectedOptions[y]].selected=true;//select the options required 
    	}
    }
}

</script>
<cti:msg key="yukon.common.dateFormatting.DATE" var="dateFormat"/>
<%
	java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat((String)pageContext.getAttribute("dateFormat"));
		
	String bulletImg = "<img src='../WebConfig/" + DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED) + "' width='9' height='9'>";
	String bulletImgExp = "<img src='../WebConfig/" + DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_EXPAND) + "' width='9' height='9'>";
	String connImgMid = "<img src='../WebConfig/" + DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_CONNECTOR_MIDDLE) + "' width='10' height='12'>";
	String connImgBtm = "<img src='../WebConfig/" + DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, WebClientRole.NAV_CONNECTOR_BOTTOM) + "' width='10' height='12'>";
	
	String linkHtml = null;
	String linkImgExp = null;
	
	final ReportModelBase<?> model = REPORT_BEAN.getModel();
    final ReportController controller = REPORT_BEAN.getReportController();
    boolean supportPdf = controller == null ? true : controller.supportsPdf();
%>

	  <form name="reportForm" method="post" action="<%=request.getContextPath()%>/servlet/ReportGenerator?" onSubmit="return checkDates()">
	  <!-- THE EXTRA INPUT TYPES ARE MAKING THE DOWNLOAD DIALOG APPEAR TWO TIMES -->
	  <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
	  <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
	  <input type="hidden" name="ecID" value="<%=REPORT_BEAN.getEnergyCompanyID() %>">
	  <input type="hidden" name="ACTION" value="DownloadReport">
	  <cti:msg key="yukon.web.reportSelection" var="reportSelectionTitle"/>
      <cti:titledContainer title="${reportSelectionTitle}">
            <table width="100%" border="0" cellspacing="2" cellpadding="0" bordercolor="#FFFFFF" align="center">
              <tr>
				<td class="columnHeader"><cti:msg key="yukon.web.reports"/></td>
                <td class="columnHeader">&nbsp;</td>
                <td class="columnHeader"><cti:msg key="yukon.common.date.start"/></td>
                <td class="columnHeader">&nbsp;</td>
                <td class="columnHeader"><cti:msg key="yukon.common.date.stop"/></td>
                <td class="columnHeader">&nbsp;</td>
                <td class="columnHeader"><cti:msg key="yukon.web.format"/></td>
			  </tr>
			  <tr bgcolor="EEEEEE">
                <td class="main">&nbsp;</td>
                <td class="main">&nbsp;</td>
                <td class="main">
                    <%if(controller == null){ %>
                    <span class='NavText'><cti:msg key="yukon.common.reports.startTimeNotInclusive"/></span>
                    <%} else if(!controller.useStartStopTimes()){%>
                    <span class='NavText'><cti:msg key="yukon.common.reports.startTimeNotInclusive"/></span>
                    <%} %>
                </td>
                <td class="main">&nbsp;</td>
                <td class="main">
                    <%if(controller == null){ %>
                    <span class='NavText'><cti:msg key="yukon.common.reports.stopTimeInclusive"/></span>
                    <%} else if(!controller.useStartStopTimes()){%>
                    <span class='NavText'><cti:msg key="yukon.common.reports.stopTimeInclusive"/></span>
                    <%} %>
                </td>
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
						  <input type="radio" name="type" value="<%=reportType%>" <%=(reportType==REPORT_BEAN.getReportType()?"checked":"")%> onclick='document.reportForm.ACTION.value="LoadParameters";document.reportForm.submit();'><font title="<cti:msg key="<%= reportType.getDescriptionKey() %>"/>"><cti:msg key="<%= reportType.getFormatKey() %>"/></font>
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
				      <td valign="bottom">
                      
                        <tags:dateInputCalendar fieldId="startCal" 
                                                fieldName="startDate" 
                                                disabled="<%=(model != null && model.useStartDate() ? false : true)%>" 
                                                fieldValue="<%= datePart.format(REPORT_BEAN.getStartDate()) %>"/>
				   		
					  </td>
					  <td valign="bottom">&nbsp</td>
			   		  <%if( controller != null && controller.useStartStopTimes() ){%>
					  <td class="columnHeader" align="center">Hour<BR>
					    <select name="startHour" id="startHourID">
					    <% for (int i = 0; i < 24; i++) {
						    String iStr = String.valueOf(i);
						    if( i < 10)	{ iStr = "0" + iStr;}%>
						    <option value="<%=i%>"><%=iStr%></option>
						  <%}%>
						</select>
					  </td>
					  <td width="100%" class="columnHeader" align="center"><cti:msg key="yukon.web.min"/><BR>
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
					  <td valign="bottom">			
                      
                        <tags:dateInputCalendar fieldId="stopCal" 
                                                fieldName="stopDate" 
                                                disabled="<%=(model != null && model.useStopDate() ? false : true)%>" 
                                                fieldValue="<%= datePart.format(REPORT_BEAN.getStopDate()) %>"/>
                                                	
                	  </td>
					  <td valign="bottom">&nbsp</td>
					  <% if( controller != null && controller.useStartStopTimes() ){%>

					  <td class="columnHeader" align="center"><cti:msg key="yukon.web.min"/><BR>
					    <select name="stopHour" id="stopHourID">
					    <% for (int i = 0; i < 24; i++) {
						    String iStr = String.valueOf(i);
						    if( i < 10)	{ iStr = "0" + iStr;}%>
						    <option value="<%=i%>"><%=iStr%></option>
						  <%}%>
						</select>
					  </td>
					  <td width="100%" class="columnHeader" align="center"><cti:msg key="yukon.web.min"/><BR>
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
						<input <%=(!supportPdf ? " disabled='disabled' " : "")%> type="radio" name="ext" value="pdf" <%=(supportPdf ? " checked='checked' " : "")%>><cti:msg key="yukon.web.pdf"/><BR>
  						<input <%=(!supportPdf ? " checked='checked' " : "")%>type="radio" name="ext" value="csv"><cti:msg key="yukon.web.csv"/>
					  </td>
					  <td class="main">
					    <button id="Generate" name="Generate" border="0" alt="Generate" align="middle" <%=(model == null ? "DISABLED style='cursor:default'":"")%> onclick='document.reportForm.ACTION.value="DownloadReport"; return true;'>
					    	<cti:msg key="yukon.web.generate"/>
					    </button>
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
	  
	  <cti:msg key="yukon.web.options" var="optionsTitle"/>
      <cti:titledContainer title="${optionsTitle}">
            <%=REPORT_BEAN.buildOptionsHTML()%>
      </cti:titledContainer>
      
	  <%if (REPORT_BEAN.hasFilter())
	  {%>
		  <br>	  
		  
		  <cti:msg key="yukon.web.options" var="filterTitle"/>
          <cti:titledContainer title="${filterTitle}">
			<SCRIPT>

        function changeFilter(filterBy) {
        <%
        //Create a local instance of the map.
        Map<ReportFilter,List<? extends Object>> filterObjectsMap = REPORT_BEAN.getFilterObjectsMap();
        for (ReportFilter filter : filterObjectsMap.keySet()) {%>
                document.getElementById('Div<%=filter.getFilterTitle()%>').style.display = (filterBy == '<%=filter%>')? "block" : "none";
        <% } %>
        
        }
    
        </SCRIPT>
        
        <script type="text/javascript">

			function setPickerSelectedPaoNamesFunction(spanId) {
	              		
	        	return function(ids) {
					var selectedNames = $A();
      				for (var index = 0; index < ids.length; index++) {
      					selectedNames.push(ids[index].paoName);
      				}
      				$(spanId).innerHTML = selectedNames.join(", ");
      				return true;
	            }
			}

            function setPickerSelectedAccountNumberNamesFunction(spanId) {
                
                return function(ids) {
                    var selectedNames = $A();

                    for (var index = 0; index < ids.length; index++) {
                        selectedNames.push(ids[index].accountNumber);
                    }
                    $(spanId).innerHTML = selectedNames.join(", ");
                    return true;
                }
            }

            function setPickerSelectedSerialNumberNamesFunction(spanId) {
                
                return function(ids) {
                    var selectedNames = $A();

                    for (var index = 0; index < ids.length; index++) {
                        selectedNames.push(ids[index].manufacturerSerialNumber);
                    }
                    $(spanId).innerHTML = selectedNames.join(", ");
                    return true;
                }
            }
            
            function setPickerSelectedUserNamesFunction(spanId) {
                
                return function(ids) {
                    var selectedNames = $A();

                    for (var index = 0; index < ids.length; index++) {
                        selectedNames.push(ids[index].userName);
                    }
                    $(spanId).innerHTML = selectedNames.join(", ");
                    return true;
                }
            }
            
	    </script>

		<table width='100%' border='0' cellspacing='0' cellpadding='0' align='center'>
        	<tr>
            	<td class='main' style='padding-left:5; padding-top:5'>
					<div id='DivFilterModelType' style='display:true'>
					
						<%
						ReportFilter selectedReportFilter = null;
						String selectedReportFilterStr = REPORT_BEAN.getSelectedReportFilter();
						if (selectedReportFilterStr != null) {
							try {
								selectedReportFilter = ReportFilter.valueOf(selectedReportFilterStr);
							} catch (IllegalArgumentException e) {
								// not valid ReportFilter, ignore
							}
						}
						REPORT_BEAN.setSelectedReportFilter(null); // don't want to have this value persist in the report bean for other reports. Only used to setup filter defaults based on specific url parameter provided this time.
						%>
					
						<select id='filterModelType' name='filterModelType' onChange='changeFilter(this.value)'>
							<%for (ReportFilter filter : filterObjectsMap.keySet()) {%>
                    			<option value='<%=filter%>' 
                    				<% if (selectedReportFilter != null && selectedReportFilter.equals(filter)){%> selected <%}%>  
                    			>
                    			<%=filter.getFilterTitle() %>
                    			</option>
        					<% } %>
                		</select>
					</div>
        		</td>
          	</tr>
          	<tr><td height='9'></td></tr>
          	<tr>
            	<td class='main' valign='top' height='19' style='padding-left:5; padding-top:5'>
        			<% boolean isFirst = true; %>
        			<%for(ReportFilter filter: filterObjectsMap.keySet()) {%>
        			
        				<%
        				String displayStyle = "none";
        				if ((selectedReportFilter != null && selectedReportFilter.equals(filter))
        					|| (selectedReportFilter == null && isFirst)){
        					displayStyle = "true";
        				}
        				%>
        			
            			<%if( filter.equals(ReportFilter.METER ) ){%>
                    		<div id="Div<%=filter.getFilterTitle()%>" style="display:<%=displayStyle%>">
                    		<input type='text' name="filterMeterValues" style='width:650px;'/>
                    		<BR><span class='NavText'><cti:msg key="yukon.common.reports.filterMeterValues"/></span><br></div>
            			<%} else if( filter.equals(ReportFilter.DEVICE)) {%>
                    		<div id="Div<%=filter.getFilterTitle()%>" style="display:<%=displayStyle%>">
		                    <input type='text' name='filterDeviceValues' style='width:650px;'/>
        		            <BR><span class='NavText'><cti:msg key="yukon.common.reports.filterDeviceValues"/></span><br></div>   
        		            
        		        <%-- LM PROGRAM PICKER --%>
        		        <%} else if( filter.equals(ReportFilter.PROGRAM) || filter.equals(ReportFilter.PROGRAM_SINGLE_SELECT)) {%>
                    		
                        <div id="Div<%=filter.getFilterTitle()%>" style="display:<%=displayStyle%>">
                    		<input type="hidden" id="selectedPickerValues" name="filterValues">
                    		
                    		<span style="font-size:16px;">
	                    		<%
	                    		if(filter.isMultiSelect()) {
	                    		%>
	                    			
	                   			<tags:pickerDialog  type="lmProgramPicker"
	                   								extraArgs="<%=String.valueOf(REPORT_BEAN.getEnergyCompanyID())%>"
				                                 	id="programPicker" 
				                                 	multiSelectMode="true"
				                                 	destinationFieldId="selectedPickerValues"
				                                 	styleClass="simpleLink"
				                                 	endAction="setPickerSelectedPaoNamesFunction('selectedProgramNamesSpan');">
				             	
				             	<img src="/WebConfig/yukon/Icons/add.gif">
				             	<cti:msg key="yukon.web.choosePrograms" />
	                            </tags:pickerDialog>
	                    			
	                    		<%	
	                    		} else {
	                    		%>
	                    			
	                   			<tags:pickerDialog  type="lmProgramPicker"
	                   								extraArgs="<%=String.valueOf(REPORT_BEAN.getEnergyCompanyID())%>"
			                                 		id="programPicker" 
			                                 		multiSelectMode="false"
			                                 		destinationFieldId="selectedPickerValues"
			                                 		styleClass="simpleLink"
			                                 		endAction="setPickerSelectedPaoNamesFunction('selectedProgramNamesSpan');"
			                                 		immediateSelectMode="true">
			                                 		
				             	<img src="/WebConfig/yukon/Icons/add.gif">
								<cti:msg key="yukon.web.chooseProgram" />
	                            </tags:pickerDialog>
	                    			
	                    		<%	
	                    		}
	                    		%>
	                             
	                            <br>
	                            <span id="selectedProgramNamesSpan" style="font-weight:bold;font-size:12px;"></span>
                             
                            </span>
                        </div>
                        <%-- LM GROUP PICKER --%>
                        <%} else if( filter.equals(ReportFilter.LMGROUP)) {%>
                        <div id="Div<%=filter.getFilterTitle()%>" style="display:<%=displayStyle%>">
                    		<input type="hidden" id="selectedPickerValues" name="filterValues">
                    		
                    		<span style="font-size:16px;">
	                    			
	                   			<tags:pickerDialog  type="lmGroupPaoPermissionCheckingPicker"
				                                 	id="groupPicker" 
				                                 	multiSelectMode="true"
				                                 	destinationFieldId="selectedPickerValues"
				                                 	styleClass="simpleLink"
				                                 	endAction="setPickerSelectedPaoNamesFunction('selectedGroupNamesSpan');">
				             	
				             	<img src="/WebConfig/yukon/Icons/add.gif">
								<cti:msg key="yukon.web.chooseGroups" />
	                            </tags:pickerDialog>
	                             
	                            <br>
	                            <span id="selectedGroupNamesSpan" style="font-weight:bold;font-size:12px;"></span>
                             
                            </span>
                        </div>
                        <%-- LM CONTROL AREA PICKER --%>
                        <%} else if( filter.equals(ReportFilter.LMCONTROLAREA)) {%>
                    	<div id="Div<%=filter.getFilterTitle()%>" style="display:<%=displayStyle%>">
                    		<input type="hidden" id="selectedPickerValues" name="filterValues">
                    		
                    		<span style="font-size:16px;">
	                    			
	                   			<tags:pickerDialog  type="lmControlAreaPaoPermissionCheckingPicker"
				                                 	id="controlAreaPicker" 
				                                 	multiSelectMode="true"
				                                 	destinationFieldId="selectedPickerValues"
				                                 	styleClass="simpleLink"
				                                 	endAction="setPickerSelectedPaoNamesFunction('selectedControlAreaNamesSpan');">
				             	
				             	<img src="/WebConfig/yukon/Icons/add.gif">
								<cti:msg key="yukon.web.chooseControlAreas" />
	                            </tags:pickerDialog>
	                             
	                            <br>
	                            <span id="selectedControlAreaNamesSpan" style="font-weight:bold;font-size:12px;"></span>
                             
                            </span>
                        </div>
                        <%-- LM SCENARIO PICKER --%>
                        <%} else if( filter.equals(ReportFilter.LMSCENARIO)) {%>
                    	<div id="Div<%=filter.getFilterTitle()%>" style="display:<%=displayStyle%>">
                    		<input type="hidden" id="selectedPickerValues" name="filterValues">
                    		
                    		<span style="font-size:16px;">
	                    			
	                   			<tags:pickerDialog  type="lmScenarioPaoPermissionCheckingPicker"
				                                 	id="scenarioPicker" 
				                                 	multiSelectMode="true"
				                                 	destinationFieldId="selectedPickerValues"
				                                 	styleClass="simpleLink"
				                                 	endAction="setPickerSelectedPaoNamesFunction('selectedScenarioNamesSpan');">
				             	
				             	<img src="/WebConfig/yukon/Icons/add.gif">
								<cti:msg key="yukon.web.chooseScenarios" />
	                            </tags:pickerDialog>
	                             
	                            <br>
	                            <span id="selectedScenarioNamesSpan" style="font-weight:bold;font-size:12px;"></span>
                             
                            </span>
                        </div>
                        
                        <%-- ACCOUNT NUMBER PICKER --%>
                        <%} else if( filter.equals(ReportFilter.ACCOUNT_NUMBER)) {%>
                        <div id="Div<%=filter.getFilterTitle()%>" style="display:<%=displayStyle%>">
                            <input type="hidden" id="selectedPickerValues" name="filterValues">
                            
                            <span style="font-size:16px;">
                                    
                                <tags:pickerDialog  type="customerAccountPicker"
                                                    id="customerAccountPicker" 
                                                    multiSelectMode="true"
                                                    destinationFieldId="selectedPickerValues"
                                                    styleClass="simpleLink"
                                                    endAction="setPickerSelectedAccountNumberNamesFunction('selectedAccountNumberNamesSpan');">
                                
                                <img src="/WebConfig/yukon/Icons/add.gif">
								<cti:msg key="yukon.web.chooseAccountNumbers" />
                                </tags:pickerDialog>
                                 
                                <br>
                                <span id="selectedAccountNumberNamesSpan" style="font-weight:bold;font-size:12px;"></span>
                             
                            </span>
                        </div> 
                        
                        <%-- SERIAL NUMBER PICKER --%>
                        <%} else if( filter.equals(ReportFilter.SERIAL_NUMBER)) {%>
                        <div id="Div<%=filter.getFilterTitle()%>" style="display:<%=displayStyle%>">
                            <input type="hidden" id="selectedPickerValues" name="filterValues">
                            
                            <span style="font-size:16px;">
                                    
                                <tags:pickerDialog  type="lmHardwareBasePicker"
                                                    extraArgs="<%=String.valueOf(REPORT_BEAN.getEnergyCompanyID())%>"
                                                    id="serialNumberPicker" 
                                                    multiSelectMode="true"
                                                    destinationFieldId="selectedPickerValues"
                                                    styleClass="simpleLink"
                                                    endAction="setPickerSelectedSerialNumberNamesFunction('selectedSerialNumberNamesSpan');">
                                
                                <img src="/WebConfig/yukon/Icons/add.gif">
								<cti:msg key="yukon.web.chooseSerialNumbers" />
                                </tags:pickerDialog>
                                 
                                <br>
                                <span id="selectedSerialNumberNamesSpan" style="font-weight:bold;font-size:12px;"></span>
                             
                            </span>
                        </div> 
                        
                        <%-- USER PICKER --%>
                        <%} else if( filter.equals(ReportFilter.USER)) {%>
                        <div id="Div<%=filter.getFilterTitle()%>" style="display:<%=displayStyle%>">
                            <input type="hidden" id="selectedPickerValues" name="filterValues">
                            
                            <span style="font-size:16px;">
                                    
                                <tags:pickerDialog  type="userPicker"
                                                    extraArgs="<%=String.valueOf(REPORT_BEAN.getEnergyCompanyID())%>"
                                                    id="userPicker" 
                                                    multiSelectMode="true"
                                                    destinationFieldId="selectedPickerValues"
                                                    styleClass="simpleLink"
                                                    endAction="setPickerSelectedUserNamesFunction('selectedUserNamesSpan');">
                                
                                <img src="/WebConfig/yukon/Icons/add.gif">
								<cti:msg key="yukon.web.chooseUsers" />
                                </tags:pickerDialog>
                                 
                                <br>
                                <span id="selectedUserNamesSpan" style="font-weight:bold;font-size:12px;"></span>
                             
                            </span>
                        </div> 
                        
                        <%-- ALL OTHER FILTER OBJECT TYPES --%>   
            			<% }else {%>
            				<div id="Div<%=filter.getFilterTitle()%>" style="display:<%=displayStyle%>">
            				
                    		<select id="selectFilterValues" name='filterValues' size='10' <%if(filter.isMultiSelect()) {%>multiple<%}%> style='width:350px;'>
                			<%List objects = filterObjectsMap.get(filter);%>
                			<%if (objects != null) {
                				
                				List<String> selectedReportFilterValues = REPORT_BEAN.getSelectedReportFilterValuesList();
                				REPORT_BEAN.setSelectedReportFilterValues(null); // don't want to have this value persist in the report bean for other reports. Only used to setup filter defaults based on specific url parameter provided this time.
                				
                    			for (Object object : objects) {
                    				
                    				String objectStringVal = "";
                    				
                        			if( object instanceof String) {
                        			
                        				objectStringVal = object.toString();%>
                        				
                                    	<option value='<c:out value="<%=objectStringVal%>" />' <% if (selectedReportFilterValues != null && selectedReportFilterValues.contains(objectStringVal)) {%>selected<%} %>>
                                    		<c:out value="<%=object.toString()%>" />
                                    	</option>
                                    	
                        			<%}else if (object instanceof LiteYukonPAObject) {
                        			
                        				objectStringVal = String.valueOf(((LiteYukonPAObject)object).getYukonID());%>
                        				
                                    	<option value='<c:out value="<%=objectStringVal%>" />' <% if (selectedReportFilterValues != null && selectedReportFilterValues.contains(objectStringVal)) {%>selected<%} %>>
                                    		<c:out value="<%=((LiteYukonPAObject)object).getPaoName()%>" />
                                    	</option>
                                    	
                        			<%}else if (object instanceof LiteDeviceMeterNumber){
                        			
                        				objectStringVal = String.valueOf(((LiteDeviceMeterNumber)object).getDeviceID());%>
                        				
                                    	<option value='<c:out value="<%=objectStringVal%>" />' <% if (selectedReportFilterValues != null && selectedReportFilterValues.contains(objectStringVal)) {%>selected<%} %>>
                                    		<c:out value="<%=((LiteDeviceMeterNumber)object).getMeterNumber()%>" />
                                    	</option>
                                    	
                        			<%}else if (object instanceof DeviceReadJobLog){
                        			
                        				objectStringVal = String.valueOf(((DeviceReadJobLog)object).getDeviceReadJobLogID());%>
                        				
                                    	<option value='<c:out value="<%=objectStringVal%>" />' <% if (selectedReportFilterValues != null && selectedReportFilterValues.contains(objectStringVal)) {%>selected<%} %>>
                                    		<c:out value="<%=((DeviceReadJobLog)object).toString()%>" />
                                    	</option>
                                    	
                                   	<%}else if (object instanceof LiteCapControlStrategy){
                                   	
                                   		objectStringVal = ((LiteCapControlStrategy)object).getStrategyName();%>
                                   		
                                   		<option value='<c:out value="<%=((LiteCapControlStrategy)object).getStrategyId()%>" />' <% if (selectedReportFilterValues != null && selectedReportFilterValues.contains(objectStringVal)) {%>selected<%} %>>
                                   			<c:out value="<%=objectStringVal%>" />
                                   		</option>
                                   		
                                   	<%}else if (object instanceof StreamableCapObject){
                                   	
                                   		objectStringVal = String.valueOf(((StreamableCapObject)object).getCcId());%>
                                   		
                                   		<option value='<c:out value="<%=((StreamableCapObject)object).getCcId()%>" />' <% if (selectedReportFilterValues != null && selectedReportFilterValues.contains(objectStringVal)) {%>selected<%} %>>
                                   			<c:out value="<%=((StreamableCapObject)object).toString()%>" />
                                   		</option>
                        		<%}
                    			}
                			}%>
                        	</select>
                            <BR>
                            <%if(filter.isMultiSelect()){ %>
                    			<span class='NavText'><cti:msg key="yukon.web.help.multiSelect"/></span><br>
                    			<span class='NavText'><cti:msg key="yukon.web.help.rangeSelect"/></span>
                            <%} else { %>
                                <span class='NavText'><cti:msg key="yukon.common.reports.selectFilter"/></span>
                            <%} %>
                      	</div>
            			<% } %>
            			<% isFirst = false; %>
        			<% }%>
        		</td>
        	</tr>
        </table>
        </cti:titledContainer>
		<%}%>	  	  	  
      </form>
</cti:standardPage>