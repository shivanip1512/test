<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="inputClass" value="${status.error ? 'error' : ''}"/>
<c:choose>
    <c:when test="${not empty param.mode and param.mode == 'VIEW'}">
        <span <tags:attributeHelper name="id" value="${param.id}"/>>${fn:escapeXml(status.value)}</span><input type="hidden" value="${status.value}"/>
    </c:when>
    <c:otherwise>
        <input <tags:attributeHelper name="id" value="${param.id}"/> type="text" size="30" name="${status.expression}" value="${status.value}" class="${inputClass}">
    </c:otherwise>
</c:choose>
