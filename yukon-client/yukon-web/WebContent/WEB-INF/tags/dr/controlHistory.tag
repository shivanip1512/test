<%@ attribute name="displayableControlHistoryMap" required="true" type="java.util.List"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<c:forEach var="displayableControlHistory" items="${displayableControlHistoryMap}">
	<c:set var="controlHistory" value="${displayableControlHistory.controlHistory}" />
	<c:set var="eventList" value="${controlHistory.currentHistory}" />
	<c:set var="eventListSize" value="${fn:length(eventList)}" />
	<c:set var="rowspan" value="${eventListSize > 0 ? eventListSize : 1}" />
	
	<tags:sectionContainer title="${controlHistory.displayName}">
		<table width="100%">
			<tr>
				<td valign="top">
					<dr:controlHistoryEvents controlHistoryEventList="${eventList}" showControlSummary="true" />
				</td>
			</tr>
		</table>
	</tags:sectionContainer>
	<br />
</c:forEach>