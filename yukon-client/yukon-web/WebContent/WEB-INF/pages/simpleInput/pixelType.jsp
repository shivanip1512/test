<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:choose>
    <c:when test="${empty param.mode or param.mode != 'VIEW'}">
        <input type="text" <tags:attributeHelper name="id" value="${param.id}"/> name="${status.expression}" value="${status.value}" maxlength="3" size="3" style="text-align: right;"><span>&nbsp;px</span>
    </c:when>
    <c:otherwise>
        <span <tags:attributeHelper name="id" value="${param.id}"/>>${fn:escapeXml(status.value)}&nbsp;px</span>
    </c:otherwise>
</c:choose>