<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<ct:nameValueContainer altRowOn="true">
  <ct:nameValue name="${attribute.description}">
  <c:if test="${isConfigured}">
    <ct:attributeValue device="${device}" attribute="${attribute}" />
  </c:if>
  <c:if test="${not isConfigured}">
    Disconnect Status Point is not configured.
  </c:if>
  </ct:nameValue>
</ct:nameValueContainer>
<BR>
<div style="text-align: right">
<ct:widgetActionRefresh method="read" label="Read Status" labelBusy="Reading"/>
<c:if test="${state != 'CONNECTED'}">
	<ct:widgetActionRefresh method="connect" label="Connect" labelBusy="Connecting"/>
</c:if>
<c:if test="${state != 'DISCONNECTED'}">
	<ct:widgetActionRefresh method="disconnect" label="Disconnect" labelBusy="Disconnecting"/>
</c:if>
</div>
<c:if test="${isRead}">
	<c:import url="/WEB-INF/pages/widget/common/meterReadingsResult.jsp"/>
</c:if>
