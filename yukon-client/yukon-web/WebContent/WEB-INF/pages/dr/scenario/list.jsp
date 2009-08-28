<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.scenario.list.pageTitle"/>
<cti:standardPage module="dr" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|scenarios"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.scenario.list.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.scenario.list.breadcrumb.scenarios"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.scenario.list.scenarios"/></h2>

	<table id="scenarioList" class="compactMiniResultsTable">
		<tr>
			<th><cti:msg key="yukon.web.modules.dr.scenario.list.heading.name"/></th>
		</tr>
		<c:forEach var="scenario" items="${scenarios}">
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
				<td>
				<c:url var="scenarioURL" value="/spring/dr/scenario/detail">
					<c:param name="scenarioId" value="${scenario.paoIdentifier.paoId}"/>
				</c:url>
				<a href="${scenarioURL}">${scenario.name}</a>
				</td>
			</tr>
		</c:forEach>
	</table>

</cti:standardPage>
