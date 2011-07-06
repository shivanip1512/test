<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ attribute name="result" required="true" type="com.cannontech.common.device.commands.CommandResultHolder"%>
<%@ attribute name="errorMsg" required="false" type="java.lang.String"%>
<%@ attribute name="successMsg" required="false" type="java.lang.String"%>

<c:if test="${result.anyErrorOrException}">
<div style="max-height: 240px; overflow: auto">
    <c:choose>
        <c:when test="${not empty pageScope.errorMsg}">
            <span class="errorRed">${pageScope.errorMsg}</span>
        </c:when>
        <c:otherwise>
            <span class="errorRed"><i:inline key=".errorReading"/></span>
        </c:otherwise>
    </c:choose>
    <br>
  <c:forEach items="${result.errors}" var="error">
    <tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
    ${error.porter}<br>
    ${error.troubleshooting}<br>
    </tags:hideReveal><br>
  </c:forEach>
  <c:if test="${not empty result.exceptionReason}">
  	${result.exceptionReason}
  </c:if>
  
</div>
</c:if>

<c:if test="${!result.anyErrorOrException}">
  <span title="${result.lastResultString}">
    <c:choose>
        <c:when test="${not empty pageScope.successMsg}">
            ${pageScope.successMsg}
        </c:when>
        <c:otherwise>
            <i:inline key=".successReading"/>
        </c:otherwise>
    </c:choose>
  </span>
</c:if>