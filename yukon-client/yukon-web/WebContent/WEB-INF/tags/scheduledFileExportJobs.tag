<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ attribute name="searchResult" required="true" type="com.cannontech.common.search.SearchResult" %>
<%@ attribute name="jobType" required="true" type="com.cannontech.common.scheduledFileExport.ScheduledExportType" %>
<%@ attribute name="baseUrl" %>
<%@ attribute name="editUrl" required="true" %>
<%@ attribute name="deleteUrl" required="true" %>

<cti:msgScope paths="yukon.web.modules.amr.scheduledFileExport.jobs">

<tags:pagedBox2 nameKey="tableTitle.${jobType}" searchResult="${searchResult}" baseUrl="${baseUrl}">
	<table id="jobsTable" class="compactResultsTable">
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
					<td>
						${fn:escapeXml(job.name)}
					</td>
					<td>
						${job.cronString}
					</td>
					<td>
						<cti:dataUpdaterValue type="JOB" identifier="${job.id}/NEXT_RUN_DATE"/>
					</td>
					<td>
						<cti:url var="finalEditUrl" value="${editUrl}">
							<cti:param name="jobId" value="${job.id}"/>
						</cti:url>
						<cti:button nameKey="edit" renderMode="image" href="${finalEditUrl}"/>
						
						<cti:url var="historyUrl" value="/support/fileExportHistory/list">
							<cti:param name="initiator" value="${jobType.initiator}${job.name}"/>
						</cti:url>
						<cti:button nameKey="history" renderMode="image" href="${historyUrl}"/>
						
						<cti:url var="finalDeleteUrl" value="${deleteUrl}">
							<cti:param name="jobId" value="${job.id}"/>
						</cti:url>
						<cti:button nameKey="delete" renderMode="image" href="${finalDeleteUrl}"/>
					</td>
				</tr>
			</c:forEach>
			<c:if test="${fn:length(searchResult.resultList) == 0}">
				<tr>
					<td class="empty-list" colspan="4">
						<i:inline key=".noJobs"/>
					</td>
				</tr>
			</c:if>
		</tbody>
	</table>
</tags:pagedBox2>

</cti:msgScope>