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
  <ct:widgetActionRefresh hide="${!readable}" method="read" label="Read Now" labelBusy="Reading"/>
</div>
<br>

<c:if test="${isOutageSupported}">
	<ct:nameValueContainer>
	  <ct:nameValue name="Outages Last Retrieved">
	    <c:if test="${not isOutageConfigured}">
	    	Outage Log Analog Point is not configured.
	    </c:if>
	    <c:if test="${isOutageConfigured}">
	    	<c:if test="${data.readDate != null}">
				<cti:formatDate value="${data.readDate}" type="BOTH" var="formattedReadDate" />
	        	${formattedReadDate}
	        </c:if>
	    </c:if>
	  </ct:nameValue>
	</ct:nameValueContainer>
	
	<div class="widgetInternalSection">
	<table class="miniResultsTable">
	  <tr>
	  	<th>Log</th>
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
		<td>${outage.outageLogIndex}</td>
		<td>
			<cti:formatDate value="${outage.timestamp.pointDataTimeStamp}" type="BOTH" var="formattedTimestamp" />
			${formattedTimestamp}
		</td>
		<td>${outage.duration }</td>
	  </tr>
	  </c:forEach>
	  </c:if>
	</table>
	</div>
</c:if>
<br>