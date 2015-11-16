<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<c:choose>
    <c:when test="${empty errors and empty errorMessage}">
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
            <c:if test="${not empty error.detail}">
                <tags:hideReveal2 titleKey="${error.summary}" showInitially="false"><i:inline key="${error.detail}"/></tags:hideReveal2>
            </c:if>  
            <c:if test="${empty error.detail}">
                <div><i:inline key="${error.summary}"/></div>
            </c:if>
        </c:forEach>
        <a href="javascript:window.location.reload();"><i:inline key=".retry"/></a>
    </c:otherwise>
</c:choose>
