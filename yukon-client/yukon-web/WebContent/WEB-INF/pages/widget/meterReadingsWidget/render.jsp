<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>

<cti:url var="previousReadingOptionsUrl" value="/WEB-INF/pages/point/previousReadingsOptions.jsp" />

<ct:nameValueContainer2>
    <c:forEach items="${attributes}" var="attribute">
        <c:choose>
            <c:when test="${not supportedAttributes[attribute]}">
	            <ct:nameValue2 nameKey=".argumentStub" argument="${attribute.description}">
				    <i:inline key=".unsupported"/>
        		</ct:nameValue2>
			</c:when>
	        <c:when test="${not existingAttributes[attribute]}">
    			<ct:nameValue2 nameKey=".argumentStub" argument="${attribute.description}">
			        <i:inline key=".notConfigured"/>
	    		</ct:nameValue2>
    		</c:when>
            <c:otherwise>
            	<ct:nameValue2 nameKey=".argumentStub" argument="${attribute.description}">
                    <ct:attributeValue device="${device}" attribute="${attribute}" />
				</ct:nameValue2>
                <c:if test="${attribute == previousReadingsAttribute}">
                    <ct:nameValue2 nameKey=".previousUsage">
                        <select onChange="${widgetParameters.widgetId}_usageSelection()"
                                id="${widgetParameters.widgetId}_prevSelect">
                           <jsp:include page="${previousReadingOptionsUrl}" />
                        </select>
                    </ct:nameValue2>
                    <ct:nameValue2 nameKey=".totalConsumption">
                        <div id="${widgetParameters.widgetId}_totalConsumption"></div>
                    </ct:nameValue2>
                </c:if>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</ct:nameValueContainer2>

<%-- The following js and dataUpdaters are for updating the USAGE values, don't write unless USAGE is supported and exists --%>
<c:if test="${usageAttributeExists}">

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
    var valueIdentifier = allIdentifierValues.get('valueIdentifier');
    var fullIdentifier = allIdentifierValues.get('fullIdentifier');

    // adjust the drop down (won't add duplicates)
    yukonGeneral_addOptionToTopOfSelect(jQuery(document.getElementById('${widgetParameters.widgetId}'+'_prevSelect')),valueIdentifier,fullIdentifier);

    // update difference
    ${widgetParameters.widgetId}_updateDifference();
}

function ${widgetParameters.widgetId}_updateCurrent(allIdentifierValues) {
    ${widgetParameters.widgetId}_currentUsage = allIdentifierValues.get('valueIdentifier');
    // update difference
    ${widgetParameters.widgetId}_updateDifference();
}

function ${widgetParameters.widgetId}_updateDifference(firstUpdate) {
  var currentUsage = ${widgetParameters.widgetId}_currentUsage;
  if (currentUsage == null) return;
  var previousVal = jQuery(document.getElementById('${widgetParameters.widgetId}_prevSelect')).val();
  debug("curr " + currentUsage);
  debug("prev " + previousVal);
  var totalUsage = currentUsage - previousVal;
  var elem = jQuery(document.getElementById('${widgetParameters.widgetId}_totalConsumption'));
  elem.html(totalUsage.toFixed(3));
  //only makes sense to draw attention to the updated value if it actually changed
  if(previousVal){
	  flashYellow(elem, 3.5);
  }
}
${widgetParameters.widgetId}_updateDifference();
</script>

<cti:attributeResolver device="${device}" attributeName="${previousReadingsAttribute}" var="pointId" />
        
<cti:dataUpdaterCallback
    function="${widgetParameters.widgetId}_prependPrevious"
    initialize="false"
    valueIdentifier="POINT/${pointId}/{value}"
    fullIdentifier="POINT/${pointId}/FULL" />

<cti:dataUpdaterCallback
    function="${widgetParameters.widgetId}_updateCurrent"
    initialize="true"
    valueIdentifier="POINT/${pointId}/{value}"/>
    
</c:if>

<br>
<div id="${widgetParameters.widgetId}_results"></div>
<div style="text-align: right">
	<ct:widgetActionUpdate hide="${!readable}" method="read" nameKey="readNow" container="${widgetParameters.widgetId}_results"/>
</div>

