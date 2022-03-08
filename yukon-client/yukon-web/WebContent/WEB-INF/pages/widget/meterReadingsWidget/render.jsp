<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

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
                    <tags:attributeValue pao="${device}" attribute="${attribute}"/>
                </tags:nameValue2>
                <c:if test="${attribute == previousReadingsAttribute}">
                    <tags:nameValue2 nameKey=".previousUsage">
                        <select onChange="${widgetParameters.widgetId}_usageSelection()"
                                id="${widgetParameters.widgetId}_prevSelect" style="font-size:13px">
                           <jsp:include page="/WEB-INF/pages/point/previousReadingsOptions.jsp"/>
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
    var valueIdentifier = allIdentifierValues.valueIdentifier;
    var fullIdentifier = allIdentifierValues.fullIdentifier;

    // adjust the drop down (won't add duplicates)
    yukon.ui.util.yukonGeneral_addOptionToTopOfSelect($(document.getElementById('${widgetParameters.widgetId}'+'_prevSelect')),valueIdentifier,fullIdentifier);

    // update difference
    ${widgetParameters.widgetId}_updateDifference();
}

function ${widgetParameters.widgetId}_updateCurrent(allIdentifierValues) {
    var fn = "${widgetParameters.widgetId}_updateCurrent: ";
    ${widgetParameters.widgetId}_currentUsage = allIdentifierValues.valueIdentifier;
    // update difference
    ${widgetParameters.widgetId}_updateDifference();
}

function ${widgetParameters.widgetId}_updateDifference() {
    var currentUsage = ${widgetParameters.widgetId}_currentUsage;
  
    if (currentUsage == null) {
        return false;
    }
    var previousVal = $(document.getElementById('${widgetParameters.widgetId}_prevSelect')).val();
    var totalUsage = currentUsage - previousVal;
    var elem = $(document.getElementById('${widgetParameters.widgetId}_totalConsumption'));
    var previousTotalUsage = elem.data("totalUsage"); 
    elem.data("totalUsage", totalUsage);
    elem.html(totalUsage.toFixed(3));
    //only makes sense to draw attention to the updated value if it actually changed
    if(previousVal && !$(elem).hasClass('untouched') && totalUsage != previousTotalUsage){
        $(elem).flash(3.5);
    }
    $(elem).removeClass('untouched');
}
</script>

<cti:attributeResolver pao="${device}" attributeName="${previousReadingsAttribute}" var="pointId" />
        
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

<div id="${widgetParameters.widgetId}_results" class="buffered"></div>
<cti:url var="meterPointsUrl" value="/common/pao/${device.deviceId}/points"/>
<div class="action-area">
    <a href="${meterPointsUrl}">
        <i:inline key="yukon.common.viewAll"/>
    </a>&nbsp;|&nbsp;
    <a href="javascript:void(0);" 
        data-popup=".js-readings-quick-view" data-popup-toggle>
        <i:inline key="yukon.common.quickView"/>
    </a>
    <tags:widgetActionUpdate hide="${!readable}" method="read" nameKey="readNow" 
            container="${widgetParameters.widgetId}_results" icon="icon-read" classes="M0"/>
</div>

<%--TODO make this into a tag --%>
<cti:msg2 var="title" key="components.dialog.devicePoints.title" arguments="${deviceName}"/>
<div class="dn js-readings-quick-view" 
    data-title="${title}"
    data-width="600" data-height="500">
    <table class="compact-results-table row-highlighting">
        <thead>
            <tr>
                <th><i:inline key="components.dialog.devicePoints.pointName"/></th>
                <th></th>
                <th><i:inline key="components.dialog.devicePoints.value"/></th>
                <th><i:inline key="components.dialog.devicePoints.timestamp"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <c:forEach var="point" items="${points}">
             <tr>
                 <td>${fn:escapeXml(point.pointName)}</td>
                 <td class="state-indicator">
                    <c:if test="${point.paoPointIdentifier.pointIdentifier.pointType.status}">
                        <cti:pointStatus pointId="${point.pointId}"/>
                    </c:if>
                 </td>
                 <td class="wsnw"><cti:pointValue pointId="${point.pointId}" format="SHORT"/></td>
                 <td class="wsnw"><tags:historicalValue pao="${device}" pointId="${point.pointId}"/></td>
             </tr>
         </c:forEach>
        </tbody>
    </table>
</div>