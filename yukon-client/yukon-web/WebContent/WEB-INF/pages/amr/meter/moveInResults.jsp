<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:choose>
    <c:when test="${empty errors and empty errorMessage and scheduled}">
        <span class="internalSectionHeader">${moveInSuccessMsg}</span>
        <br><br>

        <c:if test="${prevMeter.name != newMeter.name}">
            <span class="internalSectionHeader">${renameSuccessMsg}</span>
            <br><br>
        </c:if>

        <c:if test="${prevMeter.meterNumber != newMeter.meterNumber}">
            <span class="internalSectionHeader">${newNumberSuccessMsg}</span>
            <br><br>
        </c:if>
    </c:when>

    <c:otherwise>
        <span class="internalSectionHeader"><i:inline key=".unableToProcess" arguments="${prevMeter.name}"/></span>
        <br><br>
        <c:if test="${not empty errorMessage}">
            <span class="internalSectionHeader">${errorMessage}</span>
            <br><br>
        </c:if>
        <c:forEach items="${errors}" var="error">
            <ct:hideReveal title="<span class=\"internalSectionHeader\">${error.description} (${error.errorCode})</span>"
                showInitially="false">
                        <span class="internalSectionHeader">${error.porter}</span>
                <br>
                <span class="internalSectionHeader">${error.troubleshooting}</span>
                <br>
            </ct:hideReveal>
            <br>
        </c:forEach>
        <a href="#" onclick="window.location.reload();"><i:inline key=".retry"/></a>     
    </c:otherwise>
</c:choose>
