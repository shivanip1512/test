<%@ tag trimDirectiveWhitespaces="true" body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="result" required="true" type="com.cannontech.common.device.commands.CommandResultHolder" %>
<%@ attribute name="errorMsg" %>
<%@ attribute name="successMsg" %>

<c:if test="${result.anyErrorOrException}">
    <div class="scroll-md">
        <c:if test="${not empty pageScope.errorMsg}">
            <div class="error stacked">${pageScope.errorMsg}</div>
        </c:if>
        <c:forEach items="${result.errors}" var="error">
            <tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
                ${error.porter}<br>
                ${error.troubleshooting}<br>
            </tags:hideReveal>
        </c:forEach>
        <c:if test="${not empty result.exceptionReason}"><div>${result.exceptionReason}</div></c:if>
    </div>
</c:if>

<c:if test="${!result.anyErrorOrException}">
    <span title="${result.lastResultString}" class="success">
        <c:choose>
            <c:when test="${not empty pageScope.successMsg}">${pageScope.successMsg}</c:when>
            <c:otherwise><i:inline key="yukon.common.operationSuccess"/></c:otherwise>
        </c:choose>
    </span>
</c:if>