<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<ct:nameValueContainer altRowOn="true">
<c:forEach items="${attributes}" var="attribute">
  <ct:nameValue name="${attribute.description}">
  <c:if test="${supportedAttributes[attribute]}">
    <ct:attributeValue device="${device}" attribute="${attribute}" />
  </c:if>
  <c:if test="${not supportedAttributes[attribute]}">
    unsupported
  </c:if>
  </ct:nameValue>
</c:forEach>
</ct:nameValueContainer>

<br>
<div id="${widgetParameter.widgetId}_results"></div>
<div style="text-align: right">
<ct:widgetActionUpdate method="read" label="Read Now" labelBusy="Reading" container="${widgetParameter.widgetId}_results"/>
</div>
