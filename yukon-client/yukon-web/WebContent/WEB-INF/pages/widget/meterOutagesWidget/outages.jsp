<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<ct:nameValueContainer>
<ct:nameValue name="Outages Last Retrieved">${data.readDate}
</ct:nameValue>
</ct:nameValueContainer>
<div class="widgetInternalSection">
<table class="miniResultsTable">
<tr>
  <th>Time</th>
  <th>Duration</th>
</tr>
<c:forEach items="${data.outageData}" var="outage">
  <tr class="<ct:alternateRow odd="" even="altRow"/>">
	<td>${outage.timstamp }</td>
	<td>${outage.duration }</td>
  </tr>
</c:forEach>
</table>
</div>
<BR>
<div style="text-align: right">
<ct:widgetActionUpdate method="outageRead" label="Read Outage Log" labelBusy="Reading" container="${widgetParameters.widgetId}_results"/>
</div>
<c:import url="/WEB-INF/pages/widget/common/meterReadingsResult.jsp"/>
