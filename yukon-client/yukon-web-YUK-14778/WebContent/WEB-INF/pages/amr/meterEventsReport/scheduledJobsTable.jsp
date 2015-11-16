<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.tools.scheduledFileExport.jobs">

<tags:sectionContainer2 nameKey="scheduledJobs">
    <div id="scheduledJobsTableFlashScope"></div>
    <c:choose>
        <c:when test="${fn:length(jobs) == 0}">
            <span class="empty-list"><i:inline key=".noJobs"/></span>
        </c:when>
        <c:otherwise>
            <div class="scroll-lg">
                <table class="compact-results-table dashed has-actions">
                    <thead>
                        <th><i:inline key=".nameHeader"/></th>
                        <th><i:inline key=".scheduleHeader"/></th>
                        <th><i:inline key=".nextRunHeader"/></th>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="job" items="${jobs}">
                            <cti:url var="historyUrl" value="/support/fileExportHistory/list">
                                <cti:param name="jobName" value="${job.name}"/>
                            </cti:url>
                            <cti:url var="historyUrl" value="/support/fileExportHistory/list">
                                    <cti:param name="jobName" value="${job.name}" />
                                    <cti:param name="jobGroupId" value="${job.jobGroupId}"/>
                                    <cti:param name="exportType" value="METER_EVENTS"/>
                                </cti:url>
                            <cti:url var="editUrl" value="home">
                                <cti:param name="jobId" value="${job.id}"/>
                            </cti:url>
                            <tr>
                                <td>
                                    <a href='${editUrl}'>${fn:escapeXml(job.name)}</a>
                                </td>
                                <td>${job.cronString}</td>
                                <td>
                                    <cti:dataUpdaterValue type="JOB" identifier="${job.id}/NEXT_RUN_DATE"/>
                                    <cm:dropdown triggerClasses="fr">
                                        <cm:dropdownOption key="components.button.history.label" 
                                            href="${historyUrl}" icon="icon-script"/>
                                        <cm:dropdownOption key="components.button.delete.label"
                                            data-job-id="${job.id}"
                                            id="deleteScheduleItem_${job.id}"
                                            classes="js-delete-schedule-item"
                                            icon="icon-cross"/>
                                    </cm:dropdown>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</tags:sectionContainer2>

<div id="scheduledJobsConfirmDeleteDialog" class="dn" data-title="<cti:msg2 key=".deleteSchedule.title"/>">
    <i:inline key="modules.tools.scheduledFileExport.jobs.deleteSchedule.message"/>
</div>

</cti:msgScope>