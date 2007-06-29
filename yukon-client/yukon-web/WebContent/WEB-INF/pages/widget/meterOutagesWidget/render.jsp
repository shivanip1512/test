<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<ct:nameValueContainer>
  <ct:nameValue name="${attribute.description}">
  <c:if test="${isConfigured}">
    <ct:attributeValue device="${device}" attribute="${attribute}" />
  </c:if>
  <c:if test="${not isConfigured}">
    Blink Count Accumulator Point is not configured.
  </c:if>
  </ct:nameValue>
<br>
</ct:nameValueContainer>
<div style="text-align: right">
<ct:widgetActionUpdate method="read" label="Read Blink Count" labelBusy="Reading" container="${widgetParameters.widgetId}_results"/>
</div>
<BR>
<div id="${widgetParameters.widgetId}_results">
<c:import url="/WEB-INF/pages/widget/meterOutagesWidget/outages.jsp"/>
</div>
