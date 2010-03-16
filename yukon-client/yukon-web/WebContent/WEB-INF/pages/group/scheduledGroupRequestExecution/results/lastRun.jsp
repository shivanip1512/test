<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<cti:msg var="naLastRun" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.naLastRun" />

<c:choose>

	<c:when test="${lastCreId <= 0}">
		${naLastRun}
	</c:when>

	<c:otherwise>
	
		<cti:url var="lastRunUrl" value="/spring/common/commandRequestExecutionResults/detail">
			<cti:param name="commandRequestExecutionId" value="${lastCreId}"/>
		</cti:url>
		
		<a href="${lastRunUrl}">
			<cti:formatDate value="${lastRun}" type="DATEHM" nullText="${naLastRun}"/>
		</a>
	
	</c:otherwise>

</c:choose>