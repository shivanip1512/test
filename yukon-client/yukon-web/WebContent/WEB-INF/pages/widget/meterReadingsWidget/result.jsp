<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${errorsExist}">
<div style="max-height: 240px; overflow: auto">
There was an error reading the meter<br>
  <c:forEach items="${result.errors}" var="error">
    <ct:hideReveal title="${error.description}" showInitially="false">
    ${error.porter}<br>
    ${error.troubleshooting}<br>
    </ct:hideReveal><br>
  </c:forEach>
</div>
</c:if>

<c:if test="${!errorsExist}">
  <span title="${result.lastResultString}">Read Succeeded</span>
</c:if>