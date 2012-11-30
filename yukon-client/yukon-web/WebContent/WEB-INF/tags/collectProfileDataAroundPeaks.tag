<%@ attribute name="deviceId" required="true" type="java.lang.Long"%>

<%@ attribute name="preResult" required="true" type="java.lang.Object"%>
<%@ attribute name="preAvailableDaysAfterPeak" required="true" type="java.util.List"%>

<%@ attribute name="postResult" required="true" type="java.lang.Object"%>
<%@ attribute name="postAvailableDaysAfterPeak" required="true" type="java.util.List"%>

<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="profileRequestOrigin" required="true" type="java.lang.String"%>
<%@ attribute name="isReadable" required="true" type="java.lang.Boolean"%>
<%@ attribute name="email" required="false" type="java.lang.String"%>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:uniqueIdentifier prefix="pdp_" var="id"/>
<cti:includeScript link="/JavaScript/collectProfileDataAroundPeak.js"/>

<cti:formatDate var="preStartDate" value="${preResult.rangeStartDate}" type="DATE" />
<cti:formatDate var="preStopDate" value="${preResult.rangeStopDate}" type="DATE_MIDNIGHT_PREV" />
<c:if test="${postResult != null}">
	<cti:formatDate var="postStartDate" value="${postResult.rangeStartDate}" type="DATE" />
	<cti:formatDate var="postStopDate" value="${postResult.rangeStopDate}" type="DATE_MIDNIGHT_PREV" />
</c:if>

<input type="hidden" id="${id}_deviceId" value="${deviceId}">
<input type="hidden" id="${id}_startDate" value="${preStartDate}">
<input type="hidden" id="${id}_stopDate" value="${preStopDate}">

<script type="text/javascript">

    function changePeak() {
    
        // convert el lists into js arrays
        var preAvailableDaysAfterPeak = $A();
        <c:forEach var="d" items="${preAvailableDaysAfterPeak}">
            preAvailableDaysAfterPeak.push('${d}')
        </c:forEach>
        
        var postAvailableDaysAfterPeak = $A();
        <c:forEach var="d" items="${postAvailableDaysAfterPeak}">
            postAvailableDaysAfterPeak.push('${d}')
        </c:forEach>
    
        // remove after days options so they can be reset
        var afterDaysSelectElement = $('${id}_afterDays');
        var afterDaysItemsCount = afterDaysSelectElement.options.length;
        for (var i = afterDaysItemsCount - 1; i >= 0; i--) {
            afterDaysSelectElement.remove(i);
        }
    
        // set start stop date for selected peak date
        // reset days after drop down options
        var peakDaySelectElement = $('${id}_selectedPeakDate');
        
        if (peakDaySelectElement.selectedIndex == 0) {
        
            $('${id}_startDate').value='${preStartDate}';
            $('${id}_stopDate').value='${preStopDate}';
            
            setAvailableValuesForDaysAfterSelectElement(afterDaysSelectElement, preAvailableDaysAfterPeak);
        }
        else {
            
            $('${id}_startDate').value='${postStartDate}';
            $('${id}_stopDate').value='${postStopDate}';
            
            setAvailableValuesForDaysAfterSelectElement(afterDaysSelectElement, postAvailableDaysAfterPeak);
        }
    }
    
    // helper function to reset days after options
    function setAvailableValuesForDaysAfterSelectElement(selectElement, values) {
    
        values.each(function(optVal){
                                
            var newOpt = document.createElement('option');
            newOpt.setAttribute('value', optVal);
            newOpt.appendChild(document.createTextNode(optVal));
            selectElement.appendChild(newOpt);
       });
    }

</script>

<table class="resultsTable">

    <%--  HEADERS --%>
    <thead>
        <tr style="text-align:center; white-space:nowrap;">
            <th>Days Before Peak</th>
            <th>Peak Day</th>
            <th>Days After Peak</th>
            <th>Email</th>
            <th>Collect</th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        
        <%--  ADJUST +/- OFF PEAK DATE --%>
        <tr>
            <td style="text-align:center;">
                <select id="${id}_beforeDays">
                    <option value="0" selected>0</option>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="all">All</option>
                </select>
            </td>
            <td style="text-align:center;">
                
                <select id="${id}_selectedPeakDate" onchange="changePeak();">
                    <c:if test="${!preResult.noData && preResult.deviceError == ''}">
                        <option value="${preResult.peakValue}" selected>${preResult.peakValue}</option>
                    </c:if>
                    <c:if test="${!postResult.noData && postResult.deviceError == ''}">
                        <option value="${postResult.peakValue}">${postResult.peakValue}</option>
                    </c:if>
                </select>
                            
            </td>
            <td style="text-align:center;">
                <select id="${id}_afterDays">
                    <c:forEach var="d" items="${preAvailableDaysAfterPeak}">
                        <option value="${d}" <c:if test="${d == '0'}">selected</c:if>>${d}</option>
                    </c:forEach>
                </select>
            </td>
            
            <td>
                <input id="${id}_email" value="${pageScope.email}" type="text" size="20">
            </td>
            
            <td>
                <input type="button" id="${id}_startButton" value="Start" onclick="peakDayProfile_start('${id}', '${profileRequestOrigin}');">
            </td>
            
        </tr>
    </tbody>
    
</table>