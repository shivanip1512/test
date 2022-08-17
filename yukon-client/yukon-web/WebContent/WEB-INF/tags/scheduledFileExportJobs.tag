<%@ tag body-content="empty" trimDirectiveWhitespaces="true" 
    description="Creates a scrolled table listing all jobs. Provides an actions menu for each row
                 with edit, view and delete options with class names (js-edit-job, js-view-job, js-delete-job).  The view 
                 option is linked for you.  You will need to register click handlers for edit and delete. The tr, edit 
                 option, and delete opiton have data-job-id attributes. Clicking the ok button on the confrimation dialog 
                 for deleting will fire the 'yukon.job.delete' event with the delete option li element as the event target." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<%@ attribute name="id" description="html id attribute on the table" %>
<%@ attribute name="jobs" required="true" type="java.util.List" %>
<%@ attribute name="jobType" required="true" type="com.cannontech.common.fileExportHistory.FileExportType" %>
<%@ attribute name="fromURL" description="from URL to enable/disable job" %>

<cti:msgScope paths="yukon.web.modules.tools.scheduledFileExport.jobs">
    <c:choose>
        <c:when test="${fn:length(jobs) == 0}">
            <span class="empty-list"><i:inline key=".noJobs"/></span>
        </c:when>
        <c:otherwise>
            <div class="scroll-lg">
                <table class="compact-results-table dashed has-actions" <c:if test="${!empty pageScope.id}">id="${id}"</c:if>>
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
                            <tr data-job-id="${job.id}">
                                <td>${fn:escapeXml(job.name)}</td>
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
                                    <cti:url var="historyUrl" value="/support/fileExportHistory/list">
                                        <cti:param name="jobName" value="${job.name}"/>
                                        <cti:param name="jobGroupId" value="${job.jobGroupId}"/>
                                        <cti:param name="exportType" value="${jobType}"/>
                                    </cti:url>
                                    <cm:dropdown>
                                        <cm:dropdownOption classes="js-edit-job" data-job-id="${job.id}" icon="icon-pencil" key="components.button.edit.label"/>
                                        <cm:dropdownOption classes="js-view-job" icon="icon-script" key="components.button.history.label" href="${historyUrl}"/>
                                        <c:choose>
                                                <c:when
                                                    test="${not(job.jobState eq 'RUNNING')}">

                                                    <c:if
                                                        test="${job.jobState eq 'DISABLED' or !job.jobState.active}">
                                                        <cti:url
                                                            var="enableUrl"
                                                            value="${fromURL}/jobs/${job.id}/enable" />
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
                                                            value="${fromURL}/jobs/${job.id}/disable" />
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
                                        <cm:dropdownOption classes="js-delete-job" id="delete-job-${job.id}" data-job-id="${job.id}" icon="icon-cross" key="components.button.delete.label" data-ok-event="yukon.job.delete"/>
                                    </cm:dropdown>
                                    <d:confirm on="#delete-job-${job.id}" nameKey="confirmDelete" argument="${job.name}"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</cti:msgScope>