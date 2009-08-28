<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<spring:bind path="${input.field}">
	${input.displayName}:
	<c:if test="${!(status.value == null) && !(status.value == 'null')}">
		${fn:escapeXml(status.value)}
	</c:if>
</spring:bind>
