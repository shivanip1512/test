<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.dr.program.list.pageTitle"/>
<cti:standardPage module="dr" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|programs"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.dr.program.list.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.dr.program.list.breadcrumb.programs"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.dr.program.list.programs"/></h2>

	<table id="programList" class="compactMiniResultsTable">
		<tr>
			<th><cti:msg key="yukon.web.dr.program.list.heading.name"/></th>
		</tr>
		<c:forEach var="program" items="${programs}">
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
				<td>
				<c:url var="programURL" value="/spring/dr/program/detail">
					<c:param name="programId" value="${program.paoIdentifier.paoId}"/>
				</c:url>
				<a href="${programURL}">${program.name}</a>
				</td>
			</tr>
		</c:forEach>
	</table>

</cti:standardPage>
