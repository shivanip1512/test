<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="porterResponseMonitorView">

	<cti:standardMenu menuSelection="meters" />

	<cti:breadCrumbs>
		<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
		<cti:crumbLink url="/spring/meter/start" title="Metering" />
		<cti:crumbLink><i:inline key=".title" /></cti:crumbLink>
	</cti:breadCrumbs>

	<h2><i:inline key=".title" /></h2>
	<br>

	<%-- MAIN DETAILS --%>
	<tags:formElementContainer nameKey="sectionHeader">
		<tags:nameValueContainer2>

			<%-- monitor name --%>
			<tags:nameValue2 nameKey=".name">
				<spring:escapeBody htmlEscape="true">${monitor.name}</spring:escapeBody>
			</tags:nameValue2>

			<%-- enable/disable monitoring --%>
			<tags:nameValue2 nameKey=".monitoring">
				<i:inline key="${monitor.evaluatorStatus}" />
			</tags:nameValue2>
		</tags:nameValueContainer2>

		<c:choose>
			<c:when test="${not empty monitor.rules}">
				<tags:boxContainer2 nameKey="rulesTable" id="rulesTable" styleClass="mediumContainer">
					<table class="compactResultsTable">
						<tr>
							<th><i:inline key=".rulesTable.header.ruleOrder" /></th>
							<th><i:inline key=".rulesTable.header.success" /></th>
							<th><i:inline key=".rulesTable.header.errors" /></th>
							<th><i:inline key=".rulesTable.header.matchStyle" /></th>
							<th><i:inline key=".rulesTable.header.action" /></th>
						</tr>

						<c:forEach items="${monitor.rules}" var="rule" varStatus="status">
							<tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
								<td nowrap="nowrap">${rule.ruleOrder}</td>
								<td nowrap="nowrap">${rule.success}</td>
								<td nowrap="nowrap">${errorCodesMap[rule.ruleId]}</td>
								<td nowrap="nowrap">${rule.matchStyle}</td>
								<td nowrap="nowrap">${rule.action}</td>
							</tr>
						</c:forEach>
					</table>
				</tags:boxContainer2>
			</c:when>
		</c:choose>

		<div class="pageActionArea">
			<form id="editMonitorForm" action="/spring/amr/porterResponseMonitor/editPage" method="get">
				<input type="hidden" name="monitorId" value="${monitor.monitorId}">
				<tags:slowInput2 formId="editMonitorForm" key="edit" />
			</form>
		</div>

	</tags:formElementContainer>
</cti:standardPage>