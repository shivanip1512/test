<%@ attribute name="odd" required="true" type="java.lang.String"%>
<%@ attribute name="even" required="true" type="java.lang.String"%>
<%@ attribute name="skipToggle" type="java.lang.Boolean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${!skipToggle}">
    <c:set var="alternate" value="${!alternate}" scope="request"/>
</c:if>

<c:choose>
	<c:when test="${alternate}">
		${odd}
	</c:when>
	<c:otherwise>
		${even}
	</c:otherwise>
</c:choose>	