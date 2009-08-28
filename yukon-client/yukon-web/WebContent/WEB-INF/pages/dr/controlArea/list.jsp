<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.controlArea.list.pageTitle"/>
<cti:standardPage module="dr" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|controlareas"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.controlArea.list.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.controlArea.list.breadcrumb.controlAreas"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.controlArea.list.controlAreas"/></h2>

	<table id="controlAreaList" class="compactMiniResultsTable">
		<tr>
			<th><cti:msg key="yukon.web.modules.dr.controlArea.list.heading.name"/></th>
		</tr>
		<c:forEach var="controlArea" items="${controlAreas}">
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
				<td>
				<c:url var="controlAreaURL" value="/spring/dr/controlArea/detail">
					<c:param name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
				</c:url>
				<a href="${controlAreaURL}">${controlArea.name}</a>
				</td>
			</tr>
		</c:forEach>
	</table>

</cti:standardPage>
