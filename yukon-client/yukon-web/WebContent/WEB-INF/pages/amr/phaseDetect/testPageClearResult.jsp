<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:choose>
    <c:when test="${not empty errorReason}">
        <span class="error" style="font-weight: bold;"><i:inline key="yukon.web.modules.amr.phaseDetect.errorSending" arguments="${errorReason}"/></span>
    </c:when>
    <c:otherwise>
        <span class="success" style="font-weight: bold;"><i:inline key="yukon.web.modules.amr.phaseDetect.clearSuccess"/></span>
    </c:otherwise>
</c:choose>