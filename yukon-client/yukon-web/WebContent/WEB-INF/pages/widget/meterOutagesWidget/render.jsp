<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<ct:nameValueContainer>
  <ct:nameValue name="${attribute.description}">
    <c:if test="${not isBlinkConfigured}">
    	Blink Count Accumulator Point is not configured.
	</c:if>
  	<c:if test="${isBlinkConfigured}">
    	<ct:attributeValue device="${device}" attribute="${attribute}" />
	</c:if>
  </ct:nameValue>
  <br>
</ct:nameValueContainer>

<div style="text-align: right">
  <ct:widgetActionRefresh method="read" label="Read Now" labelBusy="Reading"/>
</div>
<br>

<c:if test="${isOutageSupported}">
	<ct:nameValueContainer>
	  <ct:nameValue name="Outages Last Retrieved">
	    <c:if test="${not isOutageConfigured}">
	    	Outage Log Analog Point is not configured.
	    </c:if>
	    <c:if test="${isOutageConfigured}">
			<cti:formatDate value="${data.readDate}" type="both" var="formattedReadDate" />
	        ${formattedReadDate}
	    </c:if>
	  </ct:nameValue>
	</ct:nameValueContainer>
	
	<div class="widgetInternalSection">
	<table class="miniResultsTable">
	  <tr>
	    <th>Time</th>
	    <th>Duration</th>
	  </tr>
	  <c:if test="${empty data.outageData}">
	    <c:forEach items="1">
	      <tr class="<ct:alternateRow odd="" even="altRow"/>">
	    	<td>n/a</td>
	    	<td>n/a</td>
	      </tr>
	    </c:forEach>
	  </c:if>
	  <c:if test="${not empty data.outageData}">
	  <c:forEach items="${data.outageData}" var="outage">
	  <tr class="<ct:alternateRow odd="" even="altRow"/>">
		<td>${outage.timestamp }</td>
		<td>${outage.duration }</td>
	  </tr>
	  </c:forEach>
	  </c:if>
	</table>
	</div>
</c:if>
<br>

<ct:nameValueContainer>
  <ct:nameValue name="Outage Status">
  	<c:if test="${state == 'RESTORED'}">
		Meter responded successfully.
	</c:if>
	<c:if test="${state == 'OUTAGE'}">
		Meter did not respond.
	</c:if>
	
	<div style="text-align: right">
	  <ct:widgetActionRefresh method="ping" label="Read" labelBusy="Reading"/>
	</div>
	<br>
  </ct:nameValue>
</ct:nameValueContainer>
<br>

<c:if test="${isRead}">
	<c:import url="/WEB-INF/pages/widget/common/meterReadingsResult.jsp"/>
</c:if>