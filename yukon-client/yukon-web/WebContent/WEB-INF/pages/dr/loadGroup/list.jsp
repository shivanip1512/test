<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.dr.loadGroup.list.pageTitle"/>
<cti:standardPage module="dr" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|loadGroups"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.dr.loadGroup.list.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.dr.loadGroup.list.breadcrumb.loadGroups"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.dr.loadGroup.list.loadGroups"/></h2>

	<table id="loadGroupList" class="compactMiniResultsTable">
		<tr>
			<th><cti:msg key="yukon.web.dr.loadGroup.list.heading.name"/></th>
		</tr>
		<c:forEach var="loadGroup" items="${loadGroups}">
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
				<td>
				<c:url var="loadGroupURL" value="/spring/dr/loadGroup/detail">
					<c:param name="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>
				</c:url>
				<a href="${loadGroupURL}">${loadGroup.name}</a>
				</td>
			</tr>
		</c:forEach>
	</table>

</cti:standardPage>
