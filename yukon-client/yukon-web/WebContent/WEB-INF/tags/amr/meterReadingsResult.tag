<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<%@ attribute name="result" required="true" type="com.cannontech.common.device.commands.CommandResultHolder"%>
<%@ attribute name="errorMsg" required="false" type="java.lang.String"%>
<%@ attribute name="successMsg" required="false" type="java.lang.String"%>

<c:if test="${result.errorsExist}">
<div style="max-height: 240px; overflow: auto">
    <c:choose>
        <c:when test="${not empty errorMsg}">
            ${errorMsg}
        </c:when>
        <c:otherwise>
            There was an error reading the meter
        </c:otherwise>
    </c:choose>
    <br>
  <c:forEach items="${result.errors}" var="error">
    <tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
    ${error.porter}<br>
    ${error.troubleshooting}<br>
    </tags:hideReveal><br>
  </c:forEach>
</div>
</c:if>

<c:if test="${!result.errorsExist}">
  <span title="${result.lastResultString}">
    <c:choose>
        <c:when test="${not empty successMsg}">
            ${successMsg}
        </c:when>
        <c:otherwise>
            Successful Read
        </c:otherwise>
    </c:choose>
  </span>
</c:if>