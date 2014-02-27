<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:choose>
    <c:when test="${empty errors and empty errorMessage}">
        <div class="success">${moveOutSuccessMsg}</div>
    </c:when>

    <c:otherwise>
        <div class="error"><i:inline key="yukon.web.modules.amr.moveOut.unableToProcess" arguments="${meter.name}"/></div>
        <c:if test="${not empty errorMessage}">
            <div class="error">${errorMessage}</div>
        </c:if>
        <c:forEach items="${errors}" var="error">
            <ct:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
                <div class="error">${error.porter}</div>
                <div class="error">${error.troubleshooting}</div>
            </ct:hideReveal>
        </c:forEach>
        
        <a href="javascript:window.location.reload();"><i:inline key="yukon.web.modules.amr.moveOut.retry"/></a>
    </c:otherwise>
</c:choose>