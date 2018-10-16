<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.amr.waterLeakReport.report">
<cti:flashScopeMessages/>
<form:form id="schedule-form" action="schedule" method="post" modelAttribute="fileExportData">

    <c:if test="${not empty jobId}">
        <input type="hidden" name="jobId" value="${jobId}">
    </c:if>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".filter.devices">
            <tags:selectDevicesTabbed deviceCollection="${deviceCollection}" 
                pickerType="waterMeterPicker"
                groupCallback="yukon.ami.waterLeakReport.schedule_group_selected_callback" 
                deviceCallback="yukon.ami.waterLeakReport.schedule_individual_selected_callback"
                uniqueId="scheduleSelector"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey="yukon.common.blank" excludeColon="true">
            <label>
                <input type="checkbox" name="includeDisabledPaos" ${includeDisabledPaos ? "checked" : ""}>
                <i:inline key=".filter.includeDisabledDevices"/>
            </label>
        </tags:nameValue2>
        <tags:inputNameValue nameKey=".schedule.daysOffset" path="daysOffset" size="3"/>
        <tags:inputNameValue nameKey=".schedule.hoursPrevious" path="hoursPrevious" size="3"/>
        <tags:inputNameValue nameKey=".filter.threshold" path="threshold" size="3"/>
        <tags:scheduledFileExportInputs cronExpressionTagState="${cronExpressionTagState}" exportData="${fileExportData}" />
    </tags:nameValueContainer2>
    
</form:form>
</cti:msgScope>