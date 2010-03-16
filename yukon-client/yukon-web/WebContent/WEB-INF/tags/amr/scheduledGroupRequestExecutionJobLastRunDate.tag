<%@ attribute name="lastCre" required="true" type="com.cannontech.common.device.commands.dao.model.CommandRequestExecution"%>
<%@ attribute name="lastRunDate" required="true" type="java.util.Date"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msg var="naLastRun" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.naLastRun" />

<c:choose>

	<c:when test="${empty lastCre}">
		${naLastRun}
	</c:when>

	<c:otherwise>
	
		<cti:url var="lastRunUrl" value="/spring/common/commandRequestExecutionResults/detail">
			<cti:param name="commandRequestExecutionId" value="${lastCre.id}"/>
		</cti:url>
		
		<a href="${lastRunUrl}">
			<cti:formatDate value="${lastRunDate}" type="DATEHM" nullText="${naLastRun}"/>
		</a>
	
	</c:otherwise>

</c:choose>