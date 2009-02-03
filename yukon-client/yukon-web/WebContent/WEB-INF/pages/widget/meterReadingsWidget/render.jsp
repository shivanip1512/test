<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<cti:url var="previousReadingOptionsUrl"
    value="/WEB-INF/pages/point/previousReadingsOptions.jsp" />

<ct:nameValueContainer altRowOn="true">
    <c:forEach items="${attributes}" var="attribute">
        <c:choose>
            <c:when test="${not supportedAttributes[attribute]}">
	            <ct:nameValue name="${attribute.description}">
				    unsupported
        		</ct:nameValue>
			</c:when>
	        <c:when test="${not existingAttributes[attribute]}">
    			<ct:nameValue name="${attribute.description}">
			    not configured
	    		</ct:nameValue>
    		</c:when>
            <c:when test="${supportedAttributes[attribute]}">
            	<ct:nameValue name="${attribute.description}">
                    <ct:attributeValue device="${device}"
                        attribute="${attribute}" />
				</ct:nameValue>

                <c:if test="${attribute == 'USAGE'}">
                    <ct:nameValue name="Previous Usage Reading">
                        <select
                            onChange="${widgetParameters.widgetId}_usageSelection()"
                            id="${widgetParameters.widgetId}_prevSelect">
                            <jsp:include page="${previousReadingOptionsUrl}" />
                        </select>
                    </ct:nameValue>

                    <ct:nameValue name="Total Consumption">
                        <div id="${widgetParameters.widgetId}_totalConsumption"></div>
                    </ct:nameValue>
                </c:if>
            </c:when>
        </c:choose>
    </c:forEach>
</ct:nameValueContainer>

<script type="text/javascript">
<%--
  The following sets up a global variable (with a unique name) to hold the current usage value.
  This variable is updated by the <widgetId>_usageUpdate function which is registered with the
  dataUpdaterCallback tag to be called whenever the point's value changes. It simply updates the 
  global variable and then calls the yukonGeneral_updatePrevious function. Similarily, 
  <widgetId>_usageSelection is registered as a change callback on the drop down.
--%>
var ${widgetParameters.widgetId}_currentUsage = null;
function ${widgetParameters.widgetId}_usageSelection() {
  ${widgetParameters.widgetId}_updateDifference();
}
function ${widgetParameters.widgetId}_prependPrevious(allIdentifierValues) {

    // get formatted results
    var valueIdentifier = allIdentifierValues['valueIdentifier'];
    var fullIdentifier = allIdentifierValues['fullIdentifier'];

    // adjust the drop down (won't add duplicates)
    yukonGeneral_addOtpionToTopOfSelect($('${widgetParameters.widgetId}'+'_prevSelect'),valueIdentifier,fullIdentifier);

    // update difference
    ${widgetParameters.widgetId}_updateDifference();
}

function ${widgetParameters.widgetId}_updateCurrent(allIdentifierValues) {
    ${widgetParameters.widgetId}_currentUsage = allIdentifierValues['valueIdentifier'];
    // update difference
    ${widgetParameters.widgetId}_updateDifference();
}

function ${widgetParameters.widgetId}_updateDifference() {
  var currentUsage = ${widgetParameters.widgetId}_currentUsage;
  if (currentUsage == null) return;
  var previousVal = $('${widgetParameters.widgetId}_prevSelect').value;
  var totalUsage = currentUsage - previousVal;
  
  $('${widgetParameters.widgetId}_totalConsumption').innerHTML = totalUsage.toFixed(3);
}
${widgetParameters.widgetId}_updateDifference();
</script>

<cti:attributeResolver device="${device}" attributeName="USAGE"
        var="pointId" />
        
<cti:dataUpdaterCallback
    function="${widgetParameters.widgetId}_prependPrevious"
    initialize="false"
    valueIdentifier="POINT/${pointId}/{value}"
    fullIdentifier="POINT/${pointId}/FULL" />

<cti:dataUpdaterCallback
    function="${widgetParameters.widgetId}_updateCurrent"
    initialize="true"
    valueIdentifier="POINT/${pointId}/{value}"/>

<br>
<div id="${widgetParameters.widgetId}_results"></div>
<div style="text-align: right">
	<ct:widgetActionUpdate hide="${!readable}" method="read" label="Read Now" labelBusy="Reading" container="${widgetParameters.widgetId}_results"/>
</div>

