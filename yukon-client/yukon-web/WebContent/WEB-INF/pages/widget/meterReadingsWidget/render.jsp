<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<ct:nameValueContainer altRowOn="true">
<c:forEach items="${attributes}" var="attribute">
  <ct:nameValue name="${attribute.description}">
  <c:choose>
    <c:when test="${not supportedAttributes[attribute]}">
    unsupported
    </c:when>
    <c:when test="${not existingAttributes[attribute]}">
    not configured
    </c:when>
    <c:when test="${supportedAttributes[attribute]}">
    <ct:attributeValue device="${device}" attribute="${attribute}" />
    </c:when>
  </c:choose>
  </ct:nameValue>
</c:forEach>
</ct:nameValueContainer>

<br>
<div id="${widgetParameters.widgetId}_results"></div>
<div style="text-align: right">
<ct:widgetActionUpdate method="read" label="Read Now" labelBusy="Reading" container="${widgetParameters.widgetId}_results"/>
</div>
