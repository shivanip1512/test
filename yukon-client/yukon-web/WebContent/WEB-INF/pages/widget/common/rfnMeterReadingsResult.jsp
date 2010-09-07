<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:choose>
    <c:when test="${not (responseStatus eq 'OK')}">
        <div style="max-height: 240px; overflow: auto">
            <span class="errorRed">There was an error sending the read request:</span>
            <br>${responseStatus}<br>
        </div>
    </c:when>
    <c:otherwise>
        <span class="okGreen">Read request sent successfully.</span>
    </c:otherwise>
</c:choose>