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
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <c:forEach var="job" items="${jobs}">
                                <cti:url var="editUrl" value="scheduleReport">
                                    <cti:param name="jobId" value="${job.id}"/>
                                </cti:url>
                                <cti:url var="historyUrl" value="/support/fileExportHistory/list">
                                    <cti:param name="jobName" value="${job.name}" />
                                    <cti:param name="jobGroupId" value="${job.jobGroupId}"/>
                                    <cti:param name="exportType" value="ARCHIVED_DATA_EXPORT"/>
                                </cti:url>
                                <cti:url var="deleteUrl" value="deleteJob">
                                    <cti:param name="jobId" value="${job.id}"/>
                                </cti:url>
                                <tr>
                                    <td><a href="${editUrl}">${fn:escapeXml(job.name)}</a></td>
                                    <td>${job.cronString}</td>
                                    <td>
                                        <cti:dataUpdaterValue type="JOB" identifier="${job.id}/NEXT_RUN_DATE"/>
                                        <cm:dropdown triggerClasses="fr">
                                            <cm:dropdownOption key="yukon.web.components.button.history.label" href="${historyUrl}" icon="icon-script"/>
                                            <cm:dropdownOption id="deleteScheduleItem_${job.id}"
                                                key="yukon.web.components.button.delete.label"
                                                data-href="${deleteUrl}" 
                                                icon="icon-cross"/>
                                        </cm:dropdown>
                                        <d:confirm on="#deleteScheduleItem_${job.id}" nameKey="deleteSchedule"/>
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