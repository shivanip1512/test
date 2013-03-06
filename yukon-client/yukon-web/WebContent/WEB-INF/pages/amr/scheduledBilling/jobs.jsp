<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:verifyRolesAndProperties value="APPLICATION_BILLING" />

<cti:msgScope paths="modules.amr.billing.jobs">

<cti:msg2 key=".pageName" var="pageName" />
<cti:standardPage title="${pageName}" module="amr">
	<cti:standardMenu menuSelection="billing|schedules"/>
	<cti:breadCrumbs>
		<cti:msg2 key="yukon.web.components.button.home.label" var="homeLabel"/>
	    <cti:crumbLink url="/operator/Operations.jsp" title="${homeLabel}" />
	    <cti:crumbLink><cti:msg2 key=".pageTitle"/></cti:crumbLink>
	</cti:breadCrumbs>
	
	<tags:pagedBox2 nameKey="tableTitle" searchResult="${filterResult}" baseUrl="jobs">
	<table id="jobsTable" class="compactResultsTable">
		<thead>
			<th><i:inline key=".nameHeader"/></th>
			<th><i:inline key=".scheduleHeader"/></th>
			<th><i:inline key=".nextRunHeader"/></th>
			<th><i:inline key=".actions"/></th>
		</thead>
		<tfoot></tfoot>
		<tbody>
			<c:forEach var="job" items="${filterResult.resultList}">
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
						<cti:url var="editUrl" value="showForm">
							<cti:param name="jobId" value="${job.id}"/>
						</cti:url>
						<cti:button nameKey="edit" renderMode="image" href="${editUrl}"/>
						
						<cti:url var="historyUrl" value="/support/fileExportHistory/list">
							<cti:param name="initiator" value="Billing Schedule: ${job.name}"/>
						</cti:url>
						<cti:button nameKey="history" renderMode="image" href="${historyUrl}"/>
						
						<cti:url var="deleteUrl" value="delete">
							<cti:param name="jobId" value="${job.id}"/>
						</cti:url>
						<cti:button nameKey="delete" renderMode="image" href="${deleteUrl}"/>
					</td>
				</tr>
			</c:forEach>
			<c:if test="${fn:length(filterResult.resultList) == 0}">
				<tr>
					<td class="noResults subtle" colspan="3">
						<i:inline key=".noJobs"/>
					</td>
				</tr>
			</c:if>
		</tbody>
	</table>
	</tags:pagedBox2>
	
</cti:standardPage>
</cti:msgScope>