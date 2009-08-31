<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="scenarioList">
    <cti:standardMenu menuSelection="details|scenarios"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.scenarioList.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.scenarioList.breadcrumb.scenarios"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.scenarioList.scenarios"/></h2>

	<table id="scenarioList" class="compactMiniResultsTable">
		<tr>
			<th><cti:msg key="yukon.web.modules.dr.scenarioList.heading.name"/></th>
		</tr>
		<c:forEach var="scenario" items="${scenarios}">
            <c:url var="scenarioURL" value="/spring/dr/scenario/detail">
                <c:param name="scenarioId" value="${scenario.paoIdentifier.paoId}"/>
            </c:url>
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
				<td>
                    <a href="${scenarioURL}"><spring:escapeBody htmlEscape="true">${scenario.name}</spring:escapeBody></a>
				</td>
			</tr>
		</c:forEach>
	</table>

</cti:standardPage>
