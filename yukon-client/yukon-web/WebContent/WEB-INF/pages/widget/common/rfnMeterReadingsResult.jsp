<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:choose>
    <c:when test="${not (responseStatus eq 'OK')}">
        <div style="max-height: 240px; overflow: auto">
            <span class="errorRed"><i:inline key=".errorSending"/></span>
            <br>${responseStatus}<br>
        </div>
    </c:when>
    <c:otherwise>
        <span class="okGreen"><i:inline key=".successSending"/></span>
    </c:otherwise>
</c:choose>