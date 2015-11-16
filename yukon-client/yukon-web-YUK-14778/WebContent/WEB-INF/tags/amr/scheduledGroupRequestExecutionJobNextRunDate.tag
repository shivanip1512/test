<%@ attribute name="jobId" required="true" type="java.lang.Integer"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<c:choose>
	<c:when test="${jobId > 0}">
		<cti:dataUpdaterValue type="JOB" identifier="${jobId}/NEXT_RUN_DATE"/>
	</c:when>
	<c:otherwise>
		N/A
	</c:otherwise>
</c:choose>