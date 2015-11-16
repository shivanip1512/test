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
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="job" items="${jobs}">
                            <tr data-job-id="${job.id}">
                                <td>${fn:escapeXml(job.name)}</td>
                                <td>${job.cronString}</td>
                                <td>
                                    <cti:dataUpdaterValue type="JOB" identifier="${job.id}/NEXT_RUN_DATE"/>
                                    <cti:url var="historyUrl" value="/support/fileExportHistory/list">
                                        <cti:param name="jobName" value="${job.name}"/>
                                        <cti:param name="jobGroupId" value="${job.jobGroupId}"/>
                                        <cti:param name="exportType" value="${jobType}"/>
                                    </cti:url>
                                    <cm:dropdown triggerClasses="fr">
                                        <cm:dropdownOption classes="js-edit-job" data-job-id="${job.id}" icon="icon-pencil" key="components.button.edit.label"/>
                                        <cm:dropdownOption classes="js-view-job" icon="icon-script" key="components.button.history.label" href="${historyUrl}"/>
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