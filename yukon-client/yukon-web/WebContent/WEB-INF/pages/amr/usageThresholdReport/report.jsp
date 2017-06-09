<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="usageThresholdReport.report">
        
        <form:form id="filter-form" action="report" method="post" commandName="criteria">
            <cti:csrfToken/>
            
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".criteria.devices">
                    <tags:selectDevicesTabbed deviceCollection="${filter.deviceCollection}" 
                        pickerType="meterPicker" 
                        groupCallback="yukon.ami.usageThresholdReport.criteria_group_selected_callback" 
                        deviceCallback="yukon.ami.usageThresholdReport.criteria_individual_selected_callback"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".criteria.attribute">
                    <tags:selectWithItems path="attribute" items="${usageAttributes}"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".criteria.dateRange">
                    <dt:dateRange startValue="${criteria.startDate}" endValue="${criteria.endDate}" startName="minDate" endName="maxDate"/>
                </tags:nameValue2>

            </tags:nameValueContainer2>
            
            <div class="page-action-area">
                <cti:button type="submit" nameKey="run" classes="primary button" busy="true"/>
            </div>
        </form:form>
        
        <cti:includeScript link="/resources/js/pages/yukon.ami.usage.threshold.report.js"/>
    
</cti:standardPage>