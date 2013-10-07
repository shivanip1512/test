<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:choose>
    <c:when test="${empty param.mode or param.mode != 'VIEW'}">
        <input type="text" <tags:attributeHelper name="id" value="${param.id}"/> name="${status.expression}" value="${status.value}">
    </c:when>
    <c:otherwise>
        <div class="simple-input-image" <tags:attributeHelper name="id" value="${param.id}"/>>
            <a href="${status.value}"><img alt="${status.value}" src="${status.value}"></a>
        </div>
    </c:otherwise>
</c:choose>