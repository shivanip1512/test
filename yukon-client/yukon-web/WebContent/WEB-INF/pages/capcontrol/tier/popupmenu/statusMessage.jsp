<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${success}"><c:set var="styleClass" value="successMessage"/></c:when>
    <c:otherwise><c:set var="styleClass" value="errorMessage"/></c:otherwise>
</c:choose>
<div class="${styleClass}">${message}</div>