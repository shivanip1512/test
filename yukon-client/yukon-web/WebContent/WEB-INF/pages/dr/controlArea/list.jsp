<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="controlAreaList">
    <cti:standardMenu menuSelection="details|controlareas"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.controlAreaList.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.controlAreaList.breadcrumb.controlAreas"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.controlAreaList.controlAreas"/></h2>

	<table id="controlAreaList" class="compactMiniResultsTable">
		<tr>
			<th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.name"/></th>
		</tr>
		<c:forEach var="controlArea" items="${controlAreas}">
            <c:url var="controlAreaURL" value="/spring/dr/controlArea/detail">
                <c:param name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
            </c:url>
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
                <td>
                    <a href="${controlAreaURL}"><spring:escapeBody htmlEscape="true">${controlArea.name}</spring:escapeBody></a>
                </td>
			</tr>
		</c:forEach>
	</table>

</cti:standardPage>
