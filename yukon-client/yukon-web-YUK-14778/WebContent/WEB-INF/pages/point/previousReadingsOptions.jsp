<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<cti:formatDate type="DATE" var="cutOff" value="${previousReadings_CutoffDate}" />

<optgroup id="firstOptGroup" label="<cti:msg2 key="yukon.web.widgetClasses.MeterReadingsWidget.recentReadings" argument="${cutOff}"/>">

    <c:forEach items="${previousReadings_All}" var="reading">
        <c:if test="${previousReadings_OptionValue == 'VALUE'}">
            <option value="${reading.value}">
                <cti:pointValueFormatter format="FULL"
                    value="${reading}" />
            </option>
        </c:if>
        
        <c:if test="${previousReadings_OptionValue == 'DATE'}">
            <option value="${reading.pointDataTimeStamp.time}">
                <cti:pointValueFormatter format="FULL" value="${reading}" />
            </option>
        </c:if>

    </c:forEach>
</optgroup>

<c:if test="${previousReadings_Cutoff}">
    <cti:formatDate type="DATE" var="cutOff" value="${previousReadings_CutoffDate}" />
    <optgroup label="<cti:msg2 key="yukon.web.widgetClasses.MeterReadingsWidget.dailyReadings" argument="${cutOff}" />">

        <c:forEach items="${previousReadings_Daily}" var="reading">
            <c:if test="${previousReadings_OptionValue == 'VALUE'}">
                <option value="${reading.value}">
                    <cti:pointValueFormatter format="FULL" value="${reading}" />
                </option>
            </c:if>

            <c:if test="${previousReadings_OptionValue == 'DATE'}">
                <option value="${reading.pointDataTimeStamp.time}">
                    <cti:pointValueFormatter format="FULL" value="${reading}" />
                </option>
            </c:if>

        </c:forEach>
    </optgroup>
</c:if>

