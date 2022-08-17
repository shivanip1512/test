<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:msgScope paths="yukon.web.modules.tools.bulk.archivedValueExporter">
    <tags:sectionContainer2 nameKey="scheduledJobs">
        <c:choose>
            <c:when test="${fn:length(jobs) == 0}">
                <span class="empty-list"><i:inline key=".noJobs"/></span>
            </c:when>
            <c:otherwise>
                <div class="scroll-lg">
                    <table class="compact-results-table has-actions dashed">
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
                                <cti:url var="editUrl" value="scheduleReport">
                                    <cti:param name="jobId" value="${job.id}" />
                                </cti:url>
                                <cti:url var="historyUrl" value="/support/fileExportHistory/list">
                                    <cti:param name="jobName" value="${job.name}" />
                                    <cti:param name="jobGroupId" value="${job.jobGroupId}" />
                                    <cti:param name="exportType" value="ARCHIVED_DATA_EXPORT" />
                                </cti:url>
                                <tr>
                                    <td><a href="${editUrl}">${fn:escapeXml(job.name)}</a></td>
                                    <td>${job.cronString}</td>
                                    <td><cti:dataUpdaterValue type="JOB" identifier="${job.id}/NEXT_RUN_DATE" /> <!-- Display job State -->
                                    <td>
                                        <cti:classUpdater type="JOB" identifier="${job.id}/JOB_STATUS_CLASS">
                                            <cti:dataUpdaterValue type="JOB" identifier="${job.id}/JOB_STATE_TEXT" />
                                        </cti:classUpdater>    
                                    </td>
                                    <td>
                                        <cm:dropdown>
                                            <cm:dropdownOption key="yukon.web.components.button.history.label" href="${historyUrl}"
                                                icon="icon-script" />
                                            <c:choose>
                                                <c:when
                                                    test="${not(job.jobState eq 'RUNNING')}">

                                                    <c:if
                                                        test="${job.jobState eq 'DISABLED' or !job.jobState.active}">
                                                        <cti:url
                                                            var="enableUrl"
                                                            value="/tools/data-exporter/jobs/${job.id}/enable" />
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
                                                            value="/tools/data-exporter/jobs/${job.id}/disable" />
                                                        <cm:dropdownOption
                                                            id="disableScheduleItem_${job.id}"
                                                            key="yukon.web.components.button.disable.label"
                                                            data-href="${disableUrl}"
                                                            icon="icon-delete" />
                                                    </c:if>
                                                    <cti:url
                                                        var="deleteUrl"
                                                        value="/tools/data-exporter/jobs/${job.id}/delete" />
                                                    <cm:dropdownOption
                                                        id="deleteScheduleItem_${job.id}"
                                                        key="yukon.web.components.button.delete.label"
                                                        data-href="${deleteUrl}"
                                                        icon="icon-cross" />

                                                    <d:confirm
                                                        on="#deleteScheduleItem_${job.id}"
                                                        nameKey="deleteSchedule" />
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
                                                    <cm:dropdownOption
                                                        id="deleteScheduleItem_${job.id}"
                                                        key="yukon.web.components.button.delete.label"
                                                        icon="icon-cross"
                                                        disabled="true" />
                                                </c:otherwise>
                                            </c:choose>
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
</cti:msgScope>
