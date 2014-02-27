<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:choose>
    <c:when test="${empty errors and empty errorMessage and scheduled}">
        <div class="success">${moveInSuccessMsg}</div>

        <c:if test="${prevMeter.name != newMeter.name}">
            <div class="success">${renameSuccessMsg}</div>
        </c:if>

        <c:if test="${prevMeter.meterNumber != newMeter.meterNumber}">
            <div class="success">${newNumberSuccessMsg}</div>
        </c:if>
    </c:when>

    <c:otherwise>
        <div class="error"><i:inline key=".unableToProcess" arguments="${prevMeter.name}"/></div>
        <c:if test="${not empty errorMessage}">
            <div class="error">${errorMessage}</div>
        </c:if>
        <c:forEach items="${errors}" var="error">
            <ct:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
                <div class="internalSectionHeader">${error.porter}</div>
                <div class="internalSectionHeader">${error.troubleshooting}</div>
            </ct:hideReveal>
        </c:forEach>
        <a href="javascript:window.location.reload();"><i:inline key=".retry"/></a>
    </c:otherwise>
</c:choose>
