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

<ct:nameValue name="Previous Usage">
<select onChange="updatePrevious()"  name="prevSelect" id="${widgetParameters.widgetId}_prevSelect">
	<c:forEach items="${previousReadings}" var = "reading">
		<option value="${reading.value}"><cti:pointValueFormatter format="FULL" value="${reading}"/></option>
	</c:forEach>
</select>
</ct:nameValue>

<ct:nameValue name="Total Consumption">
	<div id="${widgetParameters.widgetId}_totalConsumption" > 
		
	</div>
</ct:nameValue>

<div style="display:none" id="${widgetParameters.widgetId}_currentUsage">
<cti:attributeResolver device="${device}" attributeName="USAGE" var="pointId"/>
<cti:pointValue format="{value}" pointId="${pointId}"/>
</div>

<script type="text/javascript">
Event.observe(window,"load", function(){updatePrevious()
});
function updatePrevious() {
	var currentVal = $('${widgetParameters.widgetId}_currentUsage').firstDescendant().innerHTML;
	var previousVal = $('${widgetParameters.widgetId}_prevSelect').value;
	var totalUsage = currentVal - previousVal;
	
	$('${widgetParameters.widgetId}_totalConsumption').innerHTML = totalUsage.toFixed(3);
}
</script>
</ct:nameValueContainer>

<br>
<div id="${widgetParameters.widgetId}_results"></div>
<div style="text-align: right">
<ct:widgetActionUpdate method="read" label="Read Now" labelBusy="Reading" container="${widgetParameters.widgetId}_results"/>
</div>
