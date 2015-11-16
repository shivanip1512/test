<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="deviceId" required="true" type="java.lang.Long" %>
<%@ attribute name="preResult" required="true" type="java.lang.Object" %>
<%@ attribute name="preAvailableDaysAfterPeak" required="true" type="java.util.List" %>
<%@ attribute name="postResult" required="true" type="java.lang.Object" %>
<%@ attribute name="postAvailableDaysAfterPeak" required="true" type="java.util.List" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="profileRequestOrigin" required="true" %>
<%@ attribute name="isReadable" required="true" type="java.lang.Boolean" %>
<%@ attribute name="email" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:uniqueIdentifier prefix="pdp_" var="id"/>
<cti:includeScript link="/resources/js/pages/yukon.peak.day.profile.js"/>

<cti:formatDate var="preStartDate" value="${preResult.rangeStartDate}" type="DATE" />
<cti:formatDate var="preStopDate" value="${preResult.rangeStopDate}" type="DATE_MIDNIGHT_PREV" />
<c:if test="${postResult != null}">
    <cti:formatDate var="postStartDate" value="${postResult.rangeStartDate}" type="DATE" />
    <cti:formatDate var="postStopDate" value="${postResult.rangeStopDate}" type="DATE_MIDNIGHT_PREV" />
</c:if>

<input type="hidden" id="${id}_deviceId" value="${deviceId}">
<input type="hidden" id="${id}_startDate" value="${preStartDate}">
<input type="hidden" id="${id}_stopDate" value="${preStopDate}">

<input type="hidden"
    data-ids-and-dates = '{
        "afterDaysId": "${id}_afterDays",
        "peakDateId": "${id}_selectedPeakDate",
        "startDateId": "${id}_startDate",
        "stopDateId": "${id}_stopDate",
        "preStartDate": "${preStartDate}",
        "preStopDate": "${preStopDate}",
        "postStartDate": "${postStartDate}",
        "postStopDate": "${postStopDate}"
    }'
</input>
<input type="hidden" data-pre-days = '{
        "preDaysAfterPeak": [
        <c:forEach var="preDay" items="${preAvailableDaysAfterPeak}" varStatus="status">
            "${cti:escapeJavaScript(preDay)}"<c:if test="${not status.last}">,</c:if>
        </c:forEach>
        ]
        }'
</input>

<input type="hidden" data-post-days = '{
        "postDaysAfterPeak": [
        <c:forEach var="postDay" items="${postAvailableDaysAfterPeak}" varStatus="status">
            "${cti:escapeJavaScript(postDay)}"<c:if test="${not status.last}">,</c:if>
        </c:forEach>
        ]
        }'
</input>

<tags:nameValueContainer2>
    <tags:nameValue2 nameKey=".daysBeforePeak">
        <select id="${id}_beforeDays">
            <option value="0" selected>0</option>
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
            <option value="all">All</option>
        </select>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".peakDay">
        <select id="${id}_selectedPeakDate" onchange="yukon.ami.peakDayProfile.changePeak();">
            <c:if test="${!preResult.noData && preResult.deviceError == ''}">
                <option value="${preResult.peakValue}" selected>${preResult.peakValue}</option>
            </c:if>
            <c:if test="${!postResult.noData && postResult.deviceError == ''}">
                <option value="${postResult.peakValue}">${postResult.peakValue}</option>
            </c:if>
        </select>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".daysAfterPeak">
        <select id="${id}_afterDays">
            <c:forEach var="d" items="${preAvailableDaysAfterPeak}">
                <option value="${d}" <c:if test="${d == '0'}">selected</c:if>>${d}</option>
            </c:forEach>
        </select>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".email">
        <input id="${id}_email" value="${pageScope.email}" type="text" size="20">
    </tags:nameValue2>
</tags:nameValueContainer2>

<div class="action-area">
    <cti:button id="${id}_startButton" nameKey="start" onclick="yukon.ami.peakDayProfile.peakDayProfile_start('${id}', '${profileRequestOrigin}');"/>
</div>