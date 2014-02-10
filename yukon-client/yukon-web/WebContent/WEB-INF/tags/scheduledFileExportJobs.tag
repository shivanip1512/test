<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<%@ attribute name="searchResult" required="true" type="com.cannontech.common.search.result.SearchResults" %>
<%@ attribute name="jobType" required="true" type="com.cannontech.common.fileExportHistory.FileExportType" %>
<%@ attribute name="baseUrl" %>
<%@ attribute name="editUrl" required="true" %>
<%@ attribute name="deleteUrl" required="true" %>
<%@ attribute name="ajaxEnableUrls"  type="java.lang.Boolean" %>

<cti:msgScope paths="yukon.web.modules.tools.scheduledFileExport.jobs">
    <c:if test="${fn:length(searchResult.resultList) == 0}">
        <span class="empty-list"><i:inline key=".noJobs"/></span>
    </c:if>
    <table class="compact-results-table">
        <thead>
            <th><i:inline key=".nameHeader"/></th>
            <th><i:inline key=".scheduleHeader"/></th>
            <th><i:inline key=".nextRunHeader"/></th>
            <th><i:inline key=".actions"/></th>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="job" items="${searchResult.resultList}">
                <tr>
                    <td>${fn:escapeXml(job.name)}</td>
                    <td>${job.cronString}</td>
                    <td><cti:dataUpdaterValue type="JOB" identifier="${job.id}/NEXT_RUN_DATE"/></td>
                    <td>
                        <cti:url var="finalEditUrl" value="${editUrl}">
                            <cti:param name="jobId" value="${job.id}"/>
                        </cti:url>
                        <cti:button nameKey="edit" renderMode="image"
                            href='${ajaxEnableUrls
                                ? "javascript:void(0);"
                                : finalEditUrl}'
                            data-url='${ajaxEnableUrls ? finalEditUrl : ""}' icon="icon-pencil"/>

                        <cti:msg2 var="initiator" key="yukon.web.modules.support.fileExportHistory.types.${jobType}"/>
                        <cti:msg2 var="scheduleAppender" key="yukon.web.modules.support.fileExportHistory.types.scheduleAppender"/>
                        <cti:url var="historyUrl" value="/support/fileExportHistory/list">
                            <cti:param name="initiator" value="${initiator} ${scheduleAppender} ${job.name}"/>
                        </cti:url>
                        <cti:button nameKey="history" renderMode="image" href="${historyUrl}" icon="icon-script"/>

                        <cti:url var="finalDeleteUrl" value="${deleteUrl}">
                            <cti:param name="jobId" value="${job.id}"/>
                        </cti:url>
                        <c:set var="ajaxDeleteUrl" value="javascript:yukon.MeteringBilling.delete_schedule_job(${job.id});" />
                        <cti:button id="deleteItem_${job.id}" nameKey="delete" renderMode="image"
                            href='${ajaxEnableUrls
                                ? ajaxDeleteUrl
                                : finalDeleteUrl}'
                            icon="icon-cross"/>
                        <dialog:confirm on="#deleteItem_${job.id}" nameKey="confirmDelete" argument="${job.name}"/>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls baseUrl="${baseUrl}"  result="${searchResult}" adjustPageCount="true"/>
</cti:msgScope>