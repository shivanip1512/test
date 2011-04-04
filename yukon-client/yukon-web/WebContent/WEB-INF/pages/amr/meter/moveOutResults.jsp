<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:choose>
    <c:when test="${empty errors and empty errorMessage}">
        <span class="internalSectionHeader">${moveOutSuccessMsg}</span>
    </c:when>

    <c:otherwise>
        <span class="internalSectionHeader"><i:inline key="yukon.web.modules.amr.moveOut.unableToProcess" arguments="${meter.name}"/></span>
        <br/><br/>
        <c:if test="${not empty errorMessage}">
            <span class="internalSectionHeader">${errorMessage}</span>
            <br/><br/>
        </c:if>
        <c:forEach items="${errors}" var="error">
            <ct:hideReveal title="<span class=\"internalSectionHeader\">
            ${error.description} (${error.errorCode})</span>" showInitially="false">
                <span class="internalSectionHeader">${error.porter}</span><br>
                <span class="internalSectionHeader">${error.troubleshooting}</span><br>
            </ct:hideReveal>
            <br>
        </c:forEach>
        
        <a href="#" onclick="window.location.reload();"><i:inline key="yukon.web.modules.amr.moveOut.retry"/></a>     
    </c:otherwise>
</c:choose>
