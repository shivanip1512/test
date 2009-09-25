<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${not empty errorReason}">
        <span class="errorRed" style="font-weight: bold;">Error Sending Clear Command: ${errorReason}</span>
    </c:when>
    <c:otherwise>
        <span class="okGreen" style="font-weight: bold;">Clear Command Sent Successfully</span>
    </c:otherwise>
</c:choose>