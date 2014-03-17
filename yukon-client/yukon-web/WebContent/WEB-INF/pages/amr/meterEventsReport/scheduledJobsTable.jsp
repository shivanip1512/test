<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="yukon.web.modules.tools.scheduledFileExport.jobs">
<cti:msg2 var="initiator" key="yukon.web.modules.support.fileExportHistory.types.${jobType}"/>
<cti:msg2 var="scheduleAppender" key="yukon.web.modules.support.fileExportHistory.types.scheduleAppender"/>

<tags:sectionContainer2 nameKey="scheduledJobs">
    <c:if test="${empty scheduledJobsSearchResult.resultList}">
        <span class="empty-list"><i:inline key=".noJobs"/></span>
    </c:if>
    <c:if test="${not empty scheduledJobsSearchResult.resultList}">
        <div id="scheduledJobsTableFlashScope"></div>
        <table class="compact-results-table dashed has-actions">
            <thead>
                <th><i:inline key=".nameHeader"/></th>
                <th><i:inline key=".scheduleHeader"/></th>
                <th><i:inline key=".nextRunHeader"/></th>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="job" items="${scheduledJobsSearchResult.resultList}">
                    <c:set var="ajaxDeleteUrl" value="javascript:yukon.ami.billing.delete_schedule_job(${job.id});" />
                    <cti:url var="historyUrl" value="/support/fileExportHistory/list">
                        <cti:param name="initiator" value="${initiator} ${scheduleAppender} ${job.name}"/>
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
                                <cm:dropdownOption key="yukon.web.components.button.history.label" 
                                    href="${historyUrl}" icon="icon-script"/>
                                <cm:dropdownOption key="yukon.web.components.button.delete.label"
                                    data-job-id="${job.id}"
                                    id="deleteScheduleItem_${job.id}"
                                    classes="f-delete-schedule-item"
                                    icon="icon-cross"/>
                            </cm:dropdown>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
    <tags:pagingResultsControls baseUrl="scheduledJobsTable"  result="${scheduledJobsSearchResult}" adjustPageCount="true"/>
</tags:sectionContainer2>
<div id="scheduledJobsConfirmDeleteDialog" class="dn">
    <i:inline key="modules.tools.scheduledFileExport.jobs.deleteSchedule.message"/>
</div>
</cti:msgScope>
