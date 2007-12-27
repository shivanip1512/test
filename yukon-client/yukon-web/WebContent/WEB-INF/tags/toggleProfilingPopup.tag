<%@ attribute name="channelNum" required="true" type="java.lang.String"%>
<%@ attribute name="newToggleVal" required="true" type="java.lang.Boolean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script type="text/javascript"> 
    function toggleChanPopup(chanId) {
        $(chanId).toggle();
    }
</script>

<%-- SET POPUP VAR NAMES --%>
<c:set var="popupName" scope="page" value="togglePopupDiv${channelNum}"/>
<c:set var="radioName" scope="page" value="toggleRadio${channelNum}"/>
<c:set var="toggleOnDate" scope="page" value="toggleOnDate${channelNum}"/>
<c:set var="toggleOnHour" scope="page" value="toggleOnHour${channelNum}"/>
<c:set var="toggleOnMinute" scope="page" value="toggleOnMinute${channelNum}"/>
<c:set var="toggleParamName" scope="page" value="toggleChannel${channelNum}ProfilingOn"/>
<c:choose>
    <c:when test="${newToggleVal}">
        <c:set var="toggleDesc" scope="page" value="Start"/>
    </c:when>
    <c:otherwise>
        <c:set var="toggleDesc" scope="page" value="Stop"/>
    </c:otherwise>
</c:choose>

<a title="${toggleDesc}" href="javascript:toggleChanPopup('${popupName}')">${toggleDesc}</a>
<div id="${popupName}" class="popUpDiv" style="width: 280px; display: none; background-color: white; border: 1px solid black;padding: 5px 5px;">
    
    <%-- CANCEL --%>
    <div style="width: 100%; text-align: right;margin-bottom: 0px;">
        <a href="javascript:toggleChanPopup('${popupName}');">cancel</a>
    </div>
    
    <%-- NOW OR FUTURE RADIOS --%>
    <div style="width: 100%; text-align: left;margin-bottom: 10px;">
        <table>
            <tr>
                <td><input type="radio" name="${radioName}" id="radio" value="now" checked></td>
                <td colspan="2">Now</td>
            </tr>
            <tr>
                <td><input type="radio" name="${radioName}" id="radio" value="future"></td>
                <td>Future</td>
                <td>
                    <tags:dateInputCalendar fieldName="${toggleOnDate}" fieldValue="${futureScheduleDate}"/> 
                    <select name="${toggleOnHour}">
                        <c:forEach var="hour" items="${hours}">
                            <option value="${hour}">${hour}</option>
                        </c:forEach>
                    </select> : 
                    <select name="${toggleOnMinute}">
                        <c:forEach var="minute" items="${minutes}">
                            <option value="${minute}">${minute}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
        </table>
    </div>
    
    <%-- TOGGLE BUTTON --%>
    <input type="button" id="toggleButton${channelNum}" name="toggleButton${channelNum}" value="${toggleDesc} Profiling" onClick="javascript:doToggleScanning('${channelNum}', '${newToggleVal}');">
</div>