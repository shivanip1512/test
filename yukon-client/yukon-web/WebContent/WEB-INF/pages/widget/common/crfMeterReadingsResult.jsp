<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:choose>
    <c:when test="${not empty errors}">
        <div style="max-height: 240px; overflow: auto">
            <span class="errorRed">There was an error reading the meter:</span>
            <br>
            <c:forEach items="${errors}" var="error">
                ${error.errorString}<br>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        <span class="okGreen">Successful Read</span>
    </c:otherwise>
</c:choose>