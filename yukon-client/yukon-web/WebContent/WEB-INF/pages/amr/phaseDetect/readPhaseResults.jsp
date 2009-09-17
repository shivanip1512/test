<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${not empty errorMsg}">
        <font color="red"><b>Error Sending Read Command: ${errorMsg}</b></font>
    </c:when>
    <c:otherwise>
        <tags:updateableProgressBar totalCount="${totalCount}" countKey="COMMAND_REQUEST_EXECUTION/${id}/RESULTS_COUNT"/>
    </c:otherwise>
</c:choose>
