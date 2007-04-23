<%@ attribute name="odd" required="true" type="java.lang.String"%>
<%@ attribute name="even" required="true" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="alternate" value="${!alternate}" scope="request"/>

<c:choose>
	<c:when test="${alternate}">
		${odd}
	</c:when>
	<c:otherwise>
		${even}
	</c:otherwise>
</c:choose>	