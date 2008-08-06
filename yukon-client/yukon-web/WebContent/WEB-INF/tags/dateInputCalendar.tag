<%@ attribute name="fieldId" required="false" type="java.lang.String"%>
<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="fieldValue" required="false" type="java.lang.String"%>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<cti:uniqueIdentifier var="uniqueId" prefix="dateInputCalendarId_"/>
<c:if test="${not empty fieldId}">
    <c:set var="uniqueId" value="${fieldId}" />
</c:if>

<c:set var="disabledStr" value="" />
<c:if test="${disabled}">
    <c:set var="disabledStr" value="disabled" />
</c:if>

<input id="${uniqueId}" name="${fieldName}" ${disabledStr}  type="text" size="10" maxlength="10" value="${fieldValue}" style="width:70px;">&nbsp;

<cti:msg key="yukon.common.calendarcontrol.months" var="months"/>
<cti:msg key="yukon.common.calendarcontrol.days" var="days"/>
<cti:msg key="yukon.common.calendarcontrol.clear" var="clear"/>
<cti:msg key="yukon.common.calendarcontrol.close" var="close"/>

<c:url var="calImgUrl" value="/WebConfig/yukon/Icons/StartCalendar.gif"/>
<c:choose>
    <c:when test="${not disabled}">
        <span id="calSpanOn_${uniqueId}" onclick="javascript:showCalendarControl($('${uniqueId}'), '${months}', '${days}', '${clear}', '${close}');" style="cursor:pointer;">
            <img src="${calImgUrl}" width="20" height="15" border="0" />
        </span>
    </c:when>
    <c:otherwise>
        <span id="calSpanOff_${uniqueId}">
            <img src="${calImgUrl}" width="20" height="15" border="0" />
        </span>
    </c:otherwise>
</c:choose>



