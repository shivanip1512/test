<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:url var="previousReadingOptionsUrl" value="/WEB-INF/pages/point/previousReadingsOptions.jsp" />

<tags:nameValueContainer2>
    <c:forEach items="${attributes}" var="attribute">
        <c:choose>
            <c:when test="${not supportedAttributes[attribute]}">
	            <tags:nameValue2 label="${attribute}">
				    <i:inline key=".unsupported"/>
        		</tags:nameValue2>
			</c:when>
	        <c:when test="${not existingAttributes[attribute]}">
    			<tags:nameValue2 label="${attribute}">
			        <i:inline key=".notConfigured"/>
	    		</tags:nameValue2>
    		</c:when>
            <c:otherwise>
            	<tags:nameValue2 label="${attribute}">
                    <tags:attributeValue device="${device}" attribute="${attribute}"/>
				</tags:nameValue2>
                <c:if test="${attribute == previousReadingsAttribute}">
                    <tags:nameValue2 nameKey=".previousUsage">
                        <select onChange="${widgetParameters.widgetId}_usageSelection()"
                                id="${widgetParameters.widgetId}_prevSelect">
                           <jsp:include page="${previousReadingOptionsUrl}" />
                        </select>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".totalConsumption">
                        <div id="${widgetParameters.widgetId}_totalConsumption" class="untouched"></div>
                    </tags:nameValue2>
                </c:if>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</tags:nameValueContainer2>

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

function ${widgetParameters.widgetId}_updateDifference() {
  var currentUsage = ${widgetParameters.widgetId}_currentUsage;
  
  if (currentUsage == null) {
	  return false;
  }
  
  var previousVal = jQuery(document.getElementById('${widgetParameters.widgetId}_prevSelect')).val();
  var totalUsage = currentUsage - previousVal;
  var elem = jQuery(document.getElementById('${widgetParameters.widgetId}_totalConsumption'));
  var previousTotalUsage = elem.data("totalUsage"); 
  elem.data("totalUsage", totalUsage);
  elem.html(totalUsage.toFixed(3));
  
  //only makes sense to draw attention to the updated value if it actually changed
  if(previousVal && !jQuery(elem).hasClass('untouched') && totalUsage != previousTotalUsage){
	  jQuery(elem).flashYellow(3.5);
  }
  jQuery(elem).removeClass('untouched');
}
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

<div id="${widgetParameters.widgetId}_results"></div>
<div class="actionArea">
	<tags:widgetActionUpdate hide="${!readable}" method="read" nameKey="readNow" container="${widgetParameters.widgetId}_results"/>
</div>