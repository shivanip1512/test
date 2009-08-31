<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="loadGroupList">
    <cti:standardMenu menuSelection="details|loadGroups"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.loadGroupList.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.loadGroupList.breadcrumb.loadGroups"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.loadGroupList.loadGroups"/></h2>

	<table id="loadGroupList" class="compactMiniResultsTable">
		<tr>
			<th><cti:msg key="yukon.web.modules.dr.loadGroupList.heading.name"/></th>
		</tr>
		<c:forEach var="loadGroup" items="${loadGroups}">
            <c:url var="loadGroupURL" value="/spring/dr/loadGroup/detail">
                <c:param name="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>
            </c:url>
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
				<td>
                    <a href="${loadGroupURL}"><spring:escapeBody htmlEscape="true">${loadGroup.name}</spring:escapeBody></a>
				</td>
			</tr>
		</c:forEach>
	</table>

</cti:standardPage>
