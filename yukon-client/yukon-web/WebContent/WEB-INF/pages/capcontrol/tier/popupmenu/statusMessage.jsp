<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<input type="hidden" id="isFinished" value="${finished}">
<c:choose>
    <c:when test="${success}"><c:set var="styleClass" value="success"/></c:when>
    <c:otherwise><c:set var="styleClass" value="error"/></c:otherwise>
</c:choose>
<div class="${styleClass}">${message}</div>