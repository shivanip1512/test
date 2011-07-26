<%@ attribute name="channelNum" required="true" type="java.lang.String"%>
<%@ attribute name="newToggleVal" required="true" type="java.lang.Boolean"%>

<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- SET POPUP VAR NAMES --%>
<c:set var="popupName" scope="page" value="togglePopupDiv${channelNum}"/>
<c:set var="startRadio" scope="page" value="startRadio${channelNum}"/>
<c:set var="stopRadio" scope="page" value="stopRadio${channelNum}"/>
<c:set var="startDate" scope="page" value="startDate${channelNum}"/>
<c:set var="startHour" scope="page" value="startHour${channelNum}"/>
<c:set var="stopDate" scope="page" value="stopDate${channelNum}"/>
<c:set var="stopHour" scope="page" value="stopHour${channelNum}"/>

<c:choose>
    <c:when test="${newToggleVal}">
        <cti:msg2 var="toggleDesc" key="yukon.web.defaults.start"/>
    </c:when>
    <c:otherwise>
        <cti:msg2 var="toggleDesc" key="yukon.web.defaults.stop"/>
    </c:otherwise>
</c:choose>

<a title="${toggleDesc}" href="javascript:toggleChanPopup('${popupName}')">${toggleDesc}</a>
<div id="${popupName}" class="popUpDiv" style="width: 280px; display: none; background-color: white; border: 1px solid black;padding: 5px 5px;">
    <%-- CANCEL --%>
    <div style="width: 100%; text-align: right;margin-bottom: 0px;">
        <a href="javascript:toggleChanPopup('${popupName}');"><i:inline key="yukon.common.cancel"/></a>
    </div>

    <div style="width: 100%; text-align: left;margin-bottom: 10px;">
    <c:choose>
    <%-- START --%>
    <c:when test="${newToggleVal}">
        <table>
            <tr>
                <td colspan="3"><div style="font-weight:bold;"><i:inline key=".scheduleStart"/></div></td>
            </tr>
            <tr>
                <td><input type="radio" name="${startRadio}" value="now" checked></td>
                <td colspan="2"><i:inline key=".now"/></td>
            </tr>
            <tr>
                <td><input type="radio" name="${startRadio}" value="future"></td>
                <td><i:inline key=".date"/></td>
                <td>
                    <tags:dateInputCalendar fieldName="${startDate}" fieldValue="${futureScheduleDate}"/> 
                    <select name="${startHour}">
                        <c:forEach var="hour" items="${hours}">
                            <cti:formatDate value="${hour}" type="TIME" var="formattedHour" />
                            <option value="${hour.hourOfDay}">${formattedHour}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
        </table>
        <br>
        <table>
            <tr>
                <td colspan="3"><div style="font-weight:bold;"><i:inline key=".scheduleStop"/></div></td>
            </tr>
            <tr>
                <td><input type="radio" name="${stopRadio}" id="radio" value="now" checked></td>
                <td colspan="2"><i:inline key=".neverStop"/></td>
            </tr>
            <tr>
                <td><input type="radio" name="${stopRadio}" id="radio" value="future"></td>
                <td><i:inline key=".date"/></td>
                <td>
                    <tags:dateInputCalendar fieldName="${stopDate}" fieldValue="${futureScheduleDate}"/> 
                    <select name="${stopHour}">
                        <c:forEach var="hour" items="${hours}">
                            <cti:formatDate value="${hour}" type="TIME" var="formattedHour" />
                            <option value="${hour.hourOfDay}">${formattedHour}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
        </table>
    </c:when>
        
    <%-- STOP --%>
    <c:otherwise>
        <table>
            <tr>
                <td colspan="3"><div style="font-weight:bold;"><i:inline key=".scheduleStop"/></div></td>
            </tr>
            <tr>
                <td><input type="radio" name="${stopRadio}" id="radio" value="now" checked></td>
                <td colspan="2"><i:inline key=".now"/></td>
            </tr>
            <tr>
                <td><input type="radio" name="${stopRadio}" id="radio" value="future"></td>
                <td><i:inline key=".date"/></td>
                <td>
                    <tags:dateInputCalendar fieldName="${stopDate}" fieldValue="${futureScheduleDate}"/> 
                    <select name="${stopHour}">
                        <c:forEach var="hour" items="${hours}">
                            <cti:formatDate value="${hour}" type="TIME" var="formattedHour" />
                            <option value="${hour.hourOfDay}">${formattedHour}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
        </table>
    </c:otherwise>
    </c:choose>
    </div>

    <%-- TOGGLE BUTTON --%>
    <input type="button" id="toggleButton${channelNum}" name="toggleButton${channelNum}" value="${toggleDesc}" onClick="javascript:doToggleScanning('${channelNum}', '${newToggleVal}');">
</div>