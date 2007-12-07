<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="previousReadingOptionsUrl"
    value="/WEB-INF/pages/point/previousReadingsOptions.jsp" />

<ct:nameValueContainer altRowOn="true">
    <c:forEach items="${attributes}" var="attribute">

        <c:choose>
            <c:when test="${attribute == 'USAGE'}">
                <ct:nameValue name="Reading">
                    <c:choose>
                        <c:when test="${not supportedAttributes[attribute]}">
                            unsupported
                        </c:when>
                        <c:when test="${not existingAttributes[attribute]}">
                            not configured
                        </c:when>
                        <c:when test="${supportedAttributes[attribute]}">
                            <ct:attributeValue device="${device}"
                                attribute="${attribute}" />
                        </c:when>
                    </c:choose>
                </ct:nameValue>

                <ct:nameValue name="Previous Readings">
                    <select
                        onChange="${widgetParameters.widgetId}_usageSelection()"
                        id="${widgetParameters.widgetId}_prevSelect">
                        <jsp:include page="${previousReadingOptionsUrl}" />
                    </select>
                </ct:nameValue>

                <ct:nameValue name="Total Consumption">
                    <div
                        id="${widgetParameters.widgetId}_totalConsumption" />
                </ct:nameValue>

            </c:when>
            <c:otherwise>
                <ct:nameValue name="${attribute.description}">
                    <c:choose>
                        <c:when test="${not supportedAttributes[attribute]}">
                            unsupported
                        </c:when>
                        <c:when test="${not existingAttributes[attribute]}">
                            not configured
                        </c:when>
                        <c:when test="${supportedAttributes[attribute]}">
                            <ct:attributeValue device="${device}"
                                attribute="${attribute}" />
                        </c:when>
                    </c:choose>
                </ct:nameValue>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</ct:nameValueContainer>



<div style="display:none" id="${widgetParameters.widgetId}_currentUsage">
    <cti:attributeResolver device="${device}" attributeName="USAGE"
        var="pointId" />
    <cti:pointValue format="{value}" pointId="${pointId}" />
</div>

<script type="text/javascript">
<%--
  The following sets up a global variable (with a unique name) to hold the current usage value.
  This variable is updated by the <widgetId>_usageUpdate function which is registered with the
  dataUpdaterCallback tag to be called whenever the point's value changes. It simply updates the 
  global variable and then calls the yukonGeneral_updatePrevious function. Similarily, 
  <widgetId>_currentSelection is registered as a change callback on the drop down and as the 
  page load callback. This method creates a closure around the <widgetId>_currentUsage variable
  so that its value can always be passed to the yukonGeneral_updatePrevious function. This is 
  the function that actually does the subtraction and updates the screen elements.
--%>
var ${widgetParameters.widgetId}_currentUsage = null;
function ${widgetParameters.widgetId}_usageSelection() {
  yukonGeneral_updatePrevious('${widgetParameters.widgetId}', ${widgetParameters.widgetId}_currentUsage);
}
function ${widgetParameters.widgetId}_usageUpdate(allIdentifierValues) {

    // get formatted results
    var valueIdentifier = allIdentifierValues['valueIdentifier'];
    var fullIdentifier = allIdentifierValues['fullIdentifier'];
    
    // adjust the drop down
    yukonGeneral_addOtpionToTopOfSelect($('${widgetParameters.widgetId}'+'_prevSelect'),valueIdentifier,fullIdentifier);
    
    // reset current usage
    ${widgetParameters.widgetId}_currentUsage = valueIdentifier;
    
    // update previous
    yukonGeneral_updatePrevious('${widgetParameters.widgetId}', ${widgetParameters.widgetId}_currentUsage);
    
}
Event.observe(window,"load", ${widgetParameters.widgetId}_usageSelection);
</script>
<cti:dataUpdaterCallback
    function="${widgetParameters.widgetId}_usageUpdate"
    valueIdentifier="POINT/${pointId}/{value}"
    fullIdentifier="POINT/${pointId}/FULL" />

<br>
<div id="${widgetParameters.widgetId}_results"></div>
<div style="text-align: right">
    <ct:widgetActionUpdate hide="${!readable}" method="read"
        label="Read Now" labelBusy="Reading"
        container="${widgetParameters.widgetId}_results" />
</div>
