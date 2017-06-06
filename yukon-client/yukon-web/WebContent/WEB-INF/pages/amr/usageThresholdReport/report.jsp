<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="usageThresholdReport.report">
        
        <form:form id="filter-form" action="report" method="post" commandName="filter">
            <cti:csrfToken/>
            
            <tags:nameValueContainer2 tableClass="with-form-controls">
                <tags:nameValue2 nameKey=".filter.devices">
                    <tags:selectDevicesTabbed deviceCollection="${filter.deviceCollection}" 
                        pickerType="meterPicker" 
                        groupCallback="yukon.ami.usageThresholdReport.filter_group_selected_callback" 
                        deviceCallback="yukon.ami.usageThresholdReport.filter_individual_selected_callback"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".filter.attribute">
                    <tags:selectWithItems path="attribute" items="${usageAttributes}"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".filter.dateRange">
<%--                     <dt:dateTime path="startDate" value="${filter.startDate}"
                        stepMinute="60" stepHour="1" cssDialogClass="hide_minutes" />
                    <dt:dateTime path="endDate" value="${filter.endDate}"
                        stepMinute="60" stepHour="1" cssDialogClass="hide_minutes" /> --%>
<%--                     <dt:dateRange startPath="startDate" endPath="endDate"/> --%>
                    <dt:dateRange startValue="${filter.startDate}" endValue="${filter.endDate}" startName="minDate" endName="maxDate"/>
                </tags:nameValue2>

                </tags:nameValueContainer2>
            
            <div class="page-action-area">
                <cti:button type="submit" nameKey="run" classes="primary button"/>
            </div>
        </form:form>
        
        <cti:includeScript link="/resources/js/pages/yukon.ami.usage.threshold.report.js"/>
    
</cti:standardPage>