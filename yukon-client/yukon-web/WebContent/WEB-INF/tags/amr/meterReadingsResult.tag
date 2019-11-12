<%@ tag trimDirectiveWhitespaces="true" body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="result" required="true" type="com.cannontech.common.device.commands.CommandResultHolder" %>
<%@ attribute name="errorMsg" %>
<%@ attribute name="successMsg" %>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="titleClass" required="false" type="java.lang.String" %>

<c:if test="${result.anyErrorOrException}">
    <div class="scroll-md">
        <c:choose>
            <c:when test="${not empty pageScope.errorMsg}">
                <div class="error stacked">${pageScope.errorMsg}</div>
            </c:when>
            <c:otherwise>
                <div class="error"><i:inline key="yukon.common.device.attributeRead.general.errorHeading"/></div>
                <c:forEach items="${result.errors}" var="error">
                    <c:if test="${not empty error.detail}">
                        <tags:hideReveal2 titleKey="${error.summary}" showInitially="false" styleClass="${pageScope.styleClass}" titleClass="${pageScope.titleClass}"><i:inline key="${error.detail}"/></tags:hideReveal2>
                    </c:if>  
                    <c:if test="${empty error.detail}">
                        <div><i:inline key="${error.summary}"/></div>
                    </c:if>
                </c:forEach>
            </c:otherwise>
        </c:choose>
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