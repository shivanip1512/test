<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:choose>
    <c:when test="${not empty errorReason}">
        <span class="errorMessage" style="font-weight: bold;"><i:inline key="yukon.web.modules.amr.phaseDetect.errorSendingClear" arguments="${errorReason}"/></span>
    </c:when>
    <c:otherwise>
        <span class="successMessage" style="font-weight: bold;"><i:inline key="yukon.web.modules.amr.phaseDetect.clearSuccess"/></span>
    </c:otherwise>
</c:choose>