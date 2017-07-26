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
                        <th><i:inline key=".jobStatus"/></th>
                        <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
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
                                </td>
                                <td>
                                    <cti:classUpdater type="JOB" identifier="${job.id}/JOB_STATUS_CLASS">
                                        <cti:dataUpdaterValue type="JOB" identifier="${job.id}/JOB_STATE_TEXT" />
                                    </cti:classUpdater>    
                                </td>
                                <td>
                                    <cm:dropdown>
                                        <cm:dropdownOption key="components.button.history.label" 
                                            href="${historyUrl}" icon="icon-script"/>
                                        <c:choose>
                                                <c:when
                                                    test="${not(job.jobState eq 'RUNNING')}">

                                                    <c:if
                                                        test="${job.jobState eq 'DISABLED' or !job.jobState.active}">
                                                        <cti:url
                                                            var="enableUrl"
                                                            value="/amr/meterEventsReport/jobs/${job.id}/enable" />
                                                        <cm:dropdownOption
                                                            id="enableScheduleItem_${job.id}"
                                                            key="yukon.web.components.button.enable.label"
                                                            data-href="${enableUrl}"
                                                            icon="icon-accept" />
                                                    </c:if>
                                                    <c:if
                                                        test="${job.jobState eq 'SCHEDULED'}">

                                                        <cti:url
                                                            var="disableUrl"
                                                            value="/amr/meterEventsReport/jobs/${job.id}/disable" />
                                                        <cm:dropdownOption
                                                            id="disableScheduleItem_${job.id}"
                                                            key="yukon.web.components.button.disable.label"
                                                            data-href="${disableUrl}"
                                                            icon="icon-delete" />
                                                    </c:if>
                                                    </c:when>
                                                    <c:otherwise>

                                                    <c:if
                                                        test="${job.jobState eq 'DISABLED'}">
                                                        <cm:dropdownOption
                                                            id="enableScheduleItem_${job.id}"
                                                            key="yukon.web.components.button.enable.label"
                                                            icon="icon-accept"
                                                            disabled="true" />
                                                    </c:if>
                                                    <c:if
                                                        test="${not (job.jobState eq 'DISABLED')}">
                                                        <cm:dropdownOption
                                                            id="disableScheduleItem_${job.id}"
                                                            key="yukon.web.components.button.disable.label"
                                                            icon="icon-delete"
                                                            disabled="true" />
                                                    </c:if>
                                                    </c:otherwise>
                                        </c:choose>
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