<%@ page import="com.cannontech.core.roleproperties.YukonRoleProperty" %>
<%@ page import="com.cannontech.amr.deviceread.model.DeviceReadJobLog" %>
<%@ page import="java.util.Vector" %>
<%@ page import="com.cannontech.analysis.tablemodel.ReportModelBase" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.cannontech.analysis.*" %>
<%@ page import="com.cannontech.database.db.device.DeviceMeterGroup" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.database.data.lite.LiteDeviceMeterNumber" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.database.db.capcontrol.LiteCapControlStrategy" %>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.analysis.ReportFilter" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.stars.web.StarsYukonUser" %>
<%@ page import="com.cannontech.stars.core.dao.EnergyCompanyDao" %>
<%@ page import= "java.util.List" %>
<%@ page import= "java.util.ArrayList" %>
<%@ page import="com.cannontech.message.capcontrol.streamable.StreamableCapObject" %>
<%@ page import="com.cannontech.analysis.controller.ReportController" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:verifyRolesAndProperties value="REPORTING"/>

<%
    LiteYukonUser lYukonUser = (LiteYukonUser) session.getAttribute(ServletUtils.ATT_YUKON_USER);
%>
<jsp:useBean id="REPORT_BEAN" class="com.cannontech.analysis.gui.ReportBean" scope="session"/>
<jsp:setProperty name="REPORT_BEAN" property="energyCompanyID" value="<%=(YukonSpringHook.getBean(EnergyCompanyDao.class).getEnergyCompany(lYukonUser).getId())%>"/>

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
    String groupName=null;
    Object groupType = request.getParameter("groupType");
    if (groupType == null) {
        %>
            <cti:checkRolesAndProperties value="STATISTICAL_REPORTS_GROUP">
                <% groupName =  ReportGroup.STATISTICAL.name(); %>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="ADMIN_REPORTS_GROUP">
                <cti:checkRolesAndProperties value="ENABLE_SETTLEMENTS">
                    <% groupName = ReportGroup.SETTLEMENT.name(); %>
                </cti:checkRolesAndProperties>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="DATABASE_REPORTS_GROUP">
                <% groupName = ReportGroup.DATABASE.name();%>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="CI_CURTAILMENT_REPORTS_GROUP">
                <% groupName = ReportGroup.CCURT.name();%>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="ADMIN_REPORTS_GROUP">
                <% groupName = ReportGroup.ADMINISTRATIVE.name();%>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="STARS_REPORTS_GROUP">
                <% groupName = ReportGroup.STARS.name(); %>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="CAP_CONTROL_REPORTS_GROUP"> 
                <% groupName = ReportGroup.CAP_CONTROL.name(); %>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="LOAD_MANAGEMENT_REPORTS_GROUP">  
                <% groupName = ReportGroup.LOAD_MANAGEMENT.name(); %>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="AMR_REPORTS_GROUP"> 
                <% groupName = ReportGroup.METERING.name(); %>
            </cti:checkRolesAndProperties>    
            
            
       <% REPORT_BEAN.setGroupType(groupName);
    
    }
    
    
%>

<cti:standardPage module="reporting" title="Reports">
<% final ReportGroup reportGroup = REPORT_BEAN.getReportGroup(); %>
<c:set var="reportGroup" value="<%= reportGroup %>" />
<cti:linkTabbedContainer mode="section">
    <cti:checkRolesAndProperties value="AMR_REPORTS_GROUP">
        <cti:linkTab selectorKey="yukon.web.menu.config.reporting.reports.metering.tab.title"
                     initiallySelected='${reportGroup == "METERING"}'>
            <c:url value="/analysis/Reports.jsp?groupType=METERING" />
        </cti:linkTab>
    </cti:checkRolesAndProperties>

    <cti:checkRolesAndProperties value="LOAD_MANAGEMENT_REPORTS_GROUP">
        <cti:linkTab selectorKey="yukon.web.menu.config.reporting.reports.management.tab.title"
                     initiallySelected='${reportGroup == "LOAD_MANAGEMENT"}'>
            <c:url value="/analysis/Reports.jsp?groupType=LOAD_MANAGEMENT" />
        </cti:linkTab>
    </cti:checkRolesAndProperties>

    <cti:checkRolesAndProperties value="CAP_CONTROL_REPORTS_GROUP">
        <cti:linkTab selectorKey="yukon.web.menu.config.reporting.reports.capcontrol.tab.title"
                     initiallySelected='${reportGroup == "CAP_CONTROL"}'>
            <c:url value="/analysis/Reports.jsp?groupType=CAP_CONTROL" />
        </cti:linkTab>
    </cti:checkRolesAndProperties>

    <cti:checkRolesAndProperties value="STARS_REPORTS_GROUP">
        <cti:linkTab selectorKey="yukon.web.menu.config.reporting.reports.stars.tab.title"
                     initiallySelected='${reportGroup == "STARS"}'>
            <c:url value="/analysis/Reports.jsp?groupType=STARS" />
        </cti:linkTab>
    </cti:checkRolesAndProperties>

    <cti:checkRolesAndProperties value="ADMIN_REPORTS_GROUP">
        <cti:linkTab selectorKey="yukon.web.menu.config.reporting.reports.administrator.tab.title"
                     initiallySelected='${reportGroup == "ADMINISTRATIVE"}'>
            <c:url value="/analysis/Reports.jsp?groupType=ADMINISTRATIVE" />
        </cti:linkTab>
    </cti:checkRolesAndProperties>

    <cti:checkRolesAndProperties value="CI_CURTAILMENT_REPORTS_GROUP">
        <cti:linkTab selectorKey="yukon.web.menu.config.reporting.reports.cni.tab.title"
                     initiallySelected='${reportGroup == "CCURT"}'>
            <c:url value="/analysis/Reports.jsp?groupType=CCURT" />
        </cti:linkTab>
    </cti:checkRolesAndProperties>

    <cti:checkRolesAndProperties value="DATABASE_REPORTS_GROUP">
        <cti:linkTab selectorKey="yukon.web.menu.config.reporting.reports.database.tab.title"
                     initiallySelected='${reportGroup == "DATABASE"}'>
            <c:url value="/analysis/Reports.jsp?groupType=DATABASE" />
        </cti:linkTab>
    </cti:checkRolesAndProperties>

    <cti:checkRolesAndProperties value="ENABLE_SETTLEMENTS">
    <cti:checkRolesAndProperties value="ADMIN_REPORTS_GROUP">
        <cti:linkTab selectorKey="yukon.web.menu.config.reporting.reports.settlement.tab.title"
                     initiallySelected='${reportGroup == "SETTLEMENT"}'>
            <c:url value="/analysis/Reports.jsp?groupType=SETTLEMENT" />
        </cti:linkTab>
    </cti:checkRolesAndProperties>
    </cti:checkRolesAndProperties>

    <cti:checkRolesAndProperties value="STATISTICAL_REPORTS_GROUP">
        <cti:linkTab selectorKey="yukon.web.menu.config.reporting.reports.statistical.tab.title"
                     initiallySelected='${reportGroup == "STATISTICAL"}'>
            <c:url value="/analysis/Reports.jsp?groupType=STATISTICAL" />
        </cti:linkTab>
    </cti:checkRolesAndProperties>

</cti:linkTabbedContainer>

<script>

$ (function () {
    makeFirstSelectedFilterValueVisible();
});
function loadTarget (form) {
    var extGroup = form.ext,
        i;
    for (i = 0; i < extGroup.length; i++) {
        if ( extGroup[i].checked) {
            if ( extGroup[i].value == 'png') {
                form.target = '_blank';
                form.REDIRECT.value = '../analysis/reporting_png.jsp';
            } else {
                form.target = "";
            }
        }
    }
}

function enableDates (value) {
    var dateFields = $('#startCal,#stopCal');
    dateFields.prop('disabled', !value);
}

function checkDates () {
    var startDate = $('#startCal').datetimepicker('getDate'),
        stopDate = $('#stopCal').datetimepicker('getDate');
    // start must be less than stop or call the whole deal off
    if (startDate.getTime() < stopDate.getTime()) {
        loadTarget(document.reportForm);
    } else {
        alert("<cti:msg key="yukon.common.error.time.startBeforeStop"/>");
        $('#startCal').focus();
        return false;
    }
}

function makeFirstSelectedFilterValueVisible () {
    var listbox = document.getElementById('selectFilterValues'),
        selectedOptions = [],
        y,
        x;
    if (listbox) {
        y=0;
        for (x=0;x<listbox.options.length;x++) {
            if (listbox.options[x].selected) {
                    selectedOptions[y]=x;
                    y++;
            }
        } 
        listbox.selectedIndex=0;//this and selected=false prompt the listbox to 'wake up'
        if (listbox.options.length > 0) {
            listbox.options[0].selected=false; 
        }
        for (y=selectedOptions.length-1;y>-1;y--) {//start from the end and work backwards so first selected item is the one scrolled to. 
            listbox.options[selectedOptions[y]].selected=true;//select the options required 
        }
    }
}

</script>
<cti:msg key="yukon.common.dateFormatting.DATE" var="dateFormat"/>
<%
    final ReportModelBase<?> model = REPORT_BEAN.getModel();
    final ReportController controller = REPORT_BEAN.getReportController();
    boolean supportPdf = controller == null ? true : controller.supportsPdf();

    String fromURI = "/analysis/Reports.jsp" + (groupType == null ? "" : "?groupType="+ groupType);
%>

      <form name="reportForm" method="post" action="<%=request.getContextPath()%>/servlet/ReportGenerator?" onSubmit="return checkDates()">
         <cti:csrfToken/>
      <!-- THE EXTRA INPUT TYPES ARE MAKING THE DOWNLOAD DIALOG APPEAR TWO TIMES -->
      <input type="hidden" name="REDIRECT" value="<%= fromURI %>">
      <input type="hidden" name="REFERRER" value="<%= fromURI %>">
      <input type="hidden" name="ecID" value="<%=REPORT_BEAN.getEnergyCompanyID() %>">
      <input type="hidden" name="ACTION" value="DownloadReport">
      
      <cti:msg key="yukon.web.reportSelection" var="reportSelectionTitle"/>
    <div class="titled-container box-container">

        <div class="title-bar clearfix"><h3 class="title">${reportSelectionTitle}</h3></div>
        <div class="content">
            <table style="width:100%;">
              <tr>
                <td class="column-header"><cti:msg key="yukon.common.reports"/></td>
                <td class="column-header">&nbsp;</td>
                <td class="column-header"><cti:msg key="yukon.common.date.start"/></td>
                <td class="column-header">&nbsp;</td>
                <td class="column-header"><cti:msg key="yukon.common.date.stop"/></td>
                <td class="column-header">&nbsp;</td>
                <td class="column-header"><cti:msg key="yukon.common.format"/></td>
              </tr>
              <tr>
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
                <td class="main">&nbsp;</td>
                <td valign="top" style="padding-left:5; padding-top:5">
                  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td valign="bottom">
                        <%if( controller != null && controller.useStartStopTimes() ){ %>
                            <BR>
                            <dt:dateTime id="startCal" name="startDate" disabled="<%=(model != null && model.useStartDate() ? false : true)%>" value="${REPORT_BEAN.startDate}" />
                        <%} else {%>
                            <dt:date id="startCal" name="startDate" disabled="<%=(model != null && model.useStartDate() ? false : true)%>" value="${REPORT_BEAN.startDate}" />
                        <%} %>
                      </td>
                    </tr>
                  </table>
                </td>
                <td class="main">&nbsp;</td>
                <td valign="top" style="padding-left:5; padding-top:5">
                  <table width="100%" border="0" cellspacing="0" cellpadding="0">                
                    <tr>
                      <td valign="bottom">        
                        <%if( controller != null && controller.useStartStopTimes() ){ %>
                            <BR>
                            <dt:dateTime id="stopCal" name="stopDate" disabled="<%=(model != null && model.useStopDate() ? false : true)%>" value="${REPORT_BEAN.stopDate}" />
                        <%} else { %>
                            <dt:date id="stopCal" name="stopDate" disabled="<%=(model != null && model.useStopDate() ? false : true)%>" value="${REPORT_BEAN.stopDate}" />
                        <%} %>
                      </td>
                    </tr>
                  </table>
                </td>
                <td class="main">&nbsp;</td>
                <td valign="top" class="main" style="padding-left:5; padding-top:5">
                  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td class="main">
                          <input type="radio" name="ext" value="csv" checked="checked"><cti:msg key="yukon.web.csv"/><BR>
                        <input <%=(!supportPdf ? " disabled='disabled' " : "")%> type="radio" name="ext" value="pdf"><cti:msg key="yukon.web.pdf"/>
                      </td>
                      <td class="main">
                        <button id="Generate" name="Generate" border="0" alt="Generate" align="middle" <%=(model == null ? "DISABLED style='cursor:default'":"")%> onclick='document.reportForm.ACTION.value="DownloadReport"; return true;'>
                            <cti:msg key="yukon.web.components.button.generate.label"/>
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
         </div>
         </div>
         
      <br>
      
        <cti:msg key="yukon.web.options" var="optionsTitle"/>
        <div class="titled-container box-container">
            <div class="title-bar clearfix"><h3 class="title">${optionsTitle}</h3></div>
            <div class="content">
                <%=REPORT_BEAN.buildOptionsHTML()%>
            </div>
        </div>
      
      <%if (REPORT_BEAN.hasFilter())
      {%>
          <br>
          
          <cti:msg key="yukon.web.options" var="filterTitle"/>
          <div class="titled-container box-container">
            <div class="title-bar clearfix"><h3 class="title">${filterTitle}</h3></div>
            <div class="content">
            <SCRIPT>

        function changeFilter(filterBy) {
            <% //Create a local instance of the map.
            Map<ReportFilter,List<? extends Object>> filterObjectsMap = REPORT_BEAN.getFilterObjectsMap();
            for (ReportFilter filter : filterObjectsMap.keySet()) {%>
                var container = $('#Div<%=filter.name()%>');
                var show = filterBy == '<%=filter%>';
                if (show) {
                    container.show();
                    container.find(':input').prop('disabled', false);
                } else {
                    container.hide();
                    container.find(':input').prop('disabled', true);
                }
            <%}%>
        }
    
        </SCRIPT>
        
        <script type="text/javascript">

            function setPickerSelectedPaoNamesFunction (spanId) {
                return function (ids) {
                      var selectedNames = [],
                          index;
                      for (index = 0; index < ids.length; index++) {
                          selectedNames.push(ids[index].paoName);
                      }
                      document.getElementById(spanId).innerHTML = selectedNames.join(", ");
                      return true;
                }
            }

            function setPickerSelectedAccountNumberNamesFunction (spanId) {
                return function (ids) {
                    var selectedNames = [],
                        index;

                    for (index = 0; index < ids.length; index++) {
                        selectedNames.push(ids[index].accountNumber);
                    }
                    document.getElementById(spanId).innerHTML = selectedNames.join(", ");
                    return true;
                }
            }

            function setPickerSelectedSerialNumberNamesFunction (spanId) {
                return function (ids) {
                    var selectedNames = [],
                        index;

                    for (index = 0; index < ids.length; index++) {
                        selectedNames.push(ids[index].manufacturerSerialNumber);
                    }
                    document.getElementById(spanId).innerHTML = selectedNames.join(", ");
                    return true;
                }
            }
            
            function setPickerSelectedUserNamesFunction (spanId) {
                return function (ids) {
                    var selectedNames = [],
                        index;

                    for (index = 0; index < ids.length; index++) {
                        selectedNames.push(ids[index].userName);
                    }
                    document.getElementById(spanId).innerHTML = selectedNames.join(", ");
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
                            <div id="Div<%=filter.name()%>" style="display:<%=displayStyle%>">
                            <input type='text' name="filterMeterValues" style='width:650px;'/>
                            <BR><span class='NavText'><cti:msg key="yukon.common.reports.filterMeterValues"/></span><br></div>
                        <%} else if( filter.equals(ReportFilter.DEVICE)) {%>
                            <div id="Div<%=filter.name()%>" style="display:<%=displayStyle%>">
                            <input type='text' name='filterDeviceValues' style='width:650px;'/>
                            <BR><span class='NavText'><cti:msg key="yukon.common.reports.filterDeviceValues"/></span><br></div>   
                            
                        <%-- LM PROGRAM PICKER --%>
                        <%} else if( filter.equals(ReportFilter.PROGRAM) || filter.equals(ReportFilter.PROGRAM_SINGLE_SELECT)) {%>
                            
                        <div id="Div<%=filter.name()%>" style="display:<%=displayStyle%>">
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
                                                     endAction="setPickerSelectedPaoNamesFunction('selectedProgramNamesSpan');">
                                 
                                <cti:icon icon="icon-add"/>
                                 <cti:msg key="yukon.common.choosePrograms" />
                                </tags:pickerDialog>
                                    
                                <%    
                                } else {
                                %>
                                    
                                   <tags:pickerDialog  type="lmProgramPicker"
                                                       extraArgs="<%=String.valueOf(REPORT_BEAN.getEnergyCompanyID())%>"
                                                     id="programPicker" 
                                                     multiSelectMode="false"
                                                     destinationFieldId="selectedPickerValues"
                                                     endAction="setPickerSelectedPaoNamesFunction('selectedProgramNamesSpan');"
                                                     immediateSelectMode="true">
                                                     
                                 <cti:icon icon="icon-add"/>
                                <cti:msg key="yukon.common.chooseProgram" />
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
                        <div id="Div<%=filter.name()%>" style="display:<%=displayStyle%>">
                            <input type="hidden" id="selectedPickerValues" name="filterValues">
                            
                            <span style="font-size:16px;">
                                    
                                   <tags:pickerDialog  type="lmGroupPaoPermissionCheckingPicker"
                                                     id="groupPicker" 
                                                     multiSelectMode="true"
                                                     destinationFieldId="selectedPickerValues"
                                                     endAction="setPickerSelectedPaoNamesFunction('selectedGroupNamesSpan');">
                                 
                                 <cti:icon icon="icon-add"/>
                                <cti:msg key="yukon.common.chooseGroups" />
                                 </tags:pickerDialog>
                                <br>
                                <span id="selectedGroupNamesSpan" style="font-weight:bold;font-size:12px;"></span>
                             
                            </span>
                        </div>
                        <%-- LM CONTROL AREA PICKER --%>
                        <%} else if( filter.equals(ReportFilter.LMCONTROLAREA)) {%>
                        <div id="Div<%=filter.name()%>" style="display:<%=displayStyle%>">
                            <input type="hidden" id="selectedPickerValues" name="filterValues">
                            
                            <span style="font-size:16px;">
                                    
                                   <tags:pickerDialog  type="lmControlAreaPaoPermissionCheckingPicker"
                                                     id="controlAreaPicker" 
                                                     multiSelectMode="true"
                                                     destinationFieldId="selectedPickerValues"
                                                     endAction="setPickerSelectedPaoNamesFunction('selectedControlAreaNamesSpan');">
                                 
                                 <cti:icon icon="icon-add"/>
                                <cti:msg key="yukon.common.chooseControlAreas" />
                                </tags:pickerDialog>
                                 
                                <br>
                                <span id="selectedControlAreaNamesSpan" style="font-weight:bold;font-size:12px;"></span>
                             
                            </span>
                        </div>
                        <%-- LM SCENARIO PICKER --%>
                        <%} else if( filter.equals(ReportFilter.LMSCENARIO)) {%>
                        <div id="Div<%=filter.name()%>" style="display:<%=displayStyle%>">
                            <input type="hidden" id="selectedPickerValues" name="filterValues">
                            
                            <span style="font-size:16px;">
                                    
                                   <tags:pickerDialog  type="lmScenarioPaoPermissionCheckingPicker"
                                                     id="scenarioPicker" 
                                                     multiSelectMode="true"
                                                     destinationFieldId="selectedPickerValues"
                                                     endAction="setPickerSelectedPaoNamesFunction('selectedScenarioNamesSpan');">
                                 
                                 <cti:icon icon="icon-add"/>
                                <cti:msg key="yukon.common.chooseScenarios" />
                                </tags:pickerDialog>
                                 
                                <br>
                                <span id="selectedScenarioNamesSpan" style="font-weight:bold;font-size:12px;"></span>
                             
                            </span>
                        </div>
                        
                        <%-- ACCOUNT NUMBER PICKER --%>
                        <%} else if( filter.equals(ReportFilter.ACCOUNT_NUMBER)) {%>
                        <div id="Div<%=filter.name()%>" style="display:<%=displayStyle%>">
                            <input type="hidden" id="selectedPickerValues" name="filterValues">
                            
                            <span style="font-size:16px;">
                                    
                                <tags:pickerDialog  type="customerAccountPicker"
                                                    id="customerAccountPicker" 
                                                    multiSelectMode="true"
                                                    destinationFieldId="selectedPickerValues"
                                                    endAction="setPickerSelectedAccountNumberNamesFunction('selectedAccountNumberNamesSpan');">
                                
                                <cti:icon icon="icon-add"/>
                                <cti:msg key="yukon.common.chooseAccountNumbers" />
                                </tags:pickerDialog>
                                 
                                <br>
                                <span id="selectedAccountNumberNamesSpan" style="font-weight:bold;font-size:12px;"></span>
                             
                            </span>
                        </div> 
                        
                        <%-- SERIAL NUMBER PICKER --%>
                        <%} else if( filter.equals(ReportFilter.SERIAL_NUMBER)) {%>
                        <div id="Div<%=filter.name()%>" style="display:<%=displayStyle%>">
                            <input type="hidden" id="selectedPickerValues" name="filterValues">
                            
                            <span style="font-size:16px;">
                                    
                                <tags:pickerDialog  type="lmHardwareBasePicker"
                                                    extraArgs="<%=String.valueOf(REPORT_BEAN.getEnergyCompanyID())%>"
                                                    id="serialNumberPicker" 
                                                    multiSelectMode="true"
                                                    destinationFieldId="selectedPickerValues"
                                                    endAction="setPickerSelectedSerialNumberNamesFunction('selectedSerialNumberNamesSpan');">
                                
                                <cti:icon icon="icon-add"/>
                                <cti:msg key="yukon.common.chooseSerialNumbers" />
                                </tags:pickerDialog>
                                 
                                <br>
                                <span id="selectedSerialNumberNamesSpan" style="font-weight:bold;font-size:12px;"></span>
                             
                            </span>
                        </div> 
                        
                        <%-- USER PICKER --%>
                        <%} else if( filter.equals(ReportFilter.USER)) {%>
                        <div id="Div<%=filter.name()%>" style="display:<%=displayStyle%>">
                            <input type="hidden" id="selectedPickerValues" name="filterValues">
                            
                            <span style="font-size:16px;">
                                    
                                <tags:pickerDialog  type="userPicker"
                                                    extraArgs="<%=String.valueOf(REPORT_BEAN.getEnergyCompanyID())%>"
                                                    id="userPicker" 
                                                    multiSelectMode="true"
                                                    destinationFieldId="selectedPickerValues"
                                                    endAction="setPickerSelectedUserNamesFunction('selectedUserNamesSpan');">
                                
                                <cti:icon icon="icon-add"/>
                                <cti:msg key="yukon.common.chooseUsers" />
                                </tags:pickerDialog>
                                 
                                <br>
                                <span id="selectedUserNamesSpan" style="font-weight:bold;font-size:12px;"></span>
                             
                            </span>
                        </div> 
                        
                        <%-- ALL OTHER FILTER OBJECT TYPES --%>   
                        <% }else {%>
                            <div id="Div<%=filter.name()%>" style="display:<%=displayStyle%>">
                            
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
                                       
                                           objectStringVal = ((LiteCapControlStrategy)object).getName();%>
                                           
                                           <option value='<c:out value="<%=((LiteCapControlStrategy)object).getId()%>" />' <% if (selectedReportFilterValues != null && selectedReportFilterValues.contains(objectStringVal)) {%>selected<%} %>>
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
                                <span class='NavText'><cti:msg key="yukon.common.help.multiSelect"/></span><br>
                                <span class='NavText'><cti:msg key="yukon.common.help.rangeSelect"/></span>
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
        </div>
        </div>
        <%}%>
      </form>
</cti:standardPage>