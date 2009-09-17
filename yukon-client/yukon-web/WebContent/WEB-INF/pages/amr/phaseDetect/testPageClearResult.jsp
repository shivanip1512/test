<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${not empty errorReason}">
        <font color="red"><b>Error Sending Clear Command: ${errorReason}</b></font>
    </c:when>
    <c:otherwise>
        <font color="green"><b>Clear Command Sent Successfully</b></font>
    </c:otherwise>
</c:choose>