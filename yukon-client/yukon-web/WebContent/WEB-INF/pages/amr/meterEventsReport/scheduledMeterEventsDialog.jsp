<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:msgScope paths="yukon.web.modules.amr.meterEventsReport.report">
    <div id="flashScopeMsg">
        <cti:flashScopeMessages/>
    </div>
    
    <cti:toJson id="scheduleModelData" object="${scheduleModelData}"/>
    <form:form id="schedule-form" action="saveScheduledMeterEventJob" method="post" modelAttribute="exportData">
        <cti:csrfToken/>
        <cti:deviceCollection deviceCollection="${deviceCollection}" />
        <tags:nameValueContainer2>
            <tags:inputNameValue nameKey=".filter.daysPrevious" path="daysPrevious" size="3"/>
            <tags:scheduledFileExportInputs cronExpressionTagState="${cronExpressionTagState}" exportData="${exportData}" />
        </tags:nameValueContainer2>
        <c:if test="${not empty jobId}">
            <input type="hidden" name="jobId" value="${jobId}">
        </c:if>
        <div class="action-area">
            <cti:button nameKey="${empty jobId ? 'schedule' : 'update'}" id="scheduledJobDialogSchedule" classes="primary action"/>
            <cti:button nameKey="cancel" id="scheduledJobDialogCancel"/>
            <cti:button nameKey="ok" id="scheduledJobDialogOk" classes="${success eq true ? '' : 'dn'}"/>
        </div>
    </form:form>
</cti:msgScope>