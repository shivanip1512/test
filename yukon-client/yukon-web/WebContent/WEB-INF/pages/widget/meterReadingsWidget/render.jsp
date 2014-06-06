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
                                id="${widgetParameters.widgetId}_prevSelect">
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
        $(elem).flashYellow(3.5);
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
<cti:url var="meterPointsUrl" value="/common/device/points">
    <cti:param name="deviceId">${device.deviceId}</cti:param>
</cti:url>
<div class="action-area">
    <a href="${meterPointsUrl}" class="fl"><i:inline key="yukon.web.defaults.showAll"/></a>
    <a href="javascript:void(0);" class="fl" style="margin-left: 10px;" id="readings_quick_view"><i:inline key="yukon.web.defaults.quickView"/></a>
    <tags:widgetActionUpdate hide="${!readable}" method="read" nameKey="readNow" container="${widgetParameters.widgetId}_results" icon="icon-read"/>
</div>

<%--TODO make this into a tag --%>
<i:simplePopup id="poinvalues-quickview" titleKey="yukon.web.components.dialog.devicePoints.title" arguments="${deviceName}" on="#readings_quick_view" options="{'modal' : false, 'width' : 600, 'height' : 500}">
    <table class="compact-results-table row-highlighting">
        <thead>
            <tr>
                <th><i:inline key="yukon.web.components.dialog.devicePoints.pointName"/></th>
                <th class="state-indicator tar"></th>
                <th><i:inline key="yukon.web.components.dialog.devicePoints.value"/></th>
                <th><i:inline key="yukon.web.components.dialog.devicePoints.timestamp"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <c:forEach var="pointResultRow" items="${points}">
             <tr>
                 <td>${fn:escapeXml(pointResultRow.pointName)}</td>
                 <td class="state-indicator tar">
                    <c:if test="${pointResultRow.paoPointIdentifier.pointIdentifier.pointType.status}">
                        <cti:pointStatusColor pointId="${pointResultRow.pointId}" styleClass="box state-box" background="true">&nbsp;</cti:pointStatusColor>
                    </c:if>
                 </td>
                 <td><cti:pointValue pointId="${pointResultRow.pointId}" format="SHORT"/></td>
                 <td><tags:historicalValue device="${device}" pointId="${pointResultRow.pointId}"/></td>
             </tr>
         </c:forEach>
        </tbody>
    </table>
</i:simplePopup>