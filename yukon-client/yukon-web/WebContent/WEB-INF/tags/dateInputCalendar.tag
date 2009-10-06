<%@ attribute name="fieldId" required="false" type="java.lang.String"%>
<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="fieldValue" required="false" type="java.lang.String"%>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="springInput" required="false" type="java.lang.Boolean"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<cti:uniqueIdentifier var="uniqueId" prefix="dateInputCalendarId_"/>
<c:if test="${springInput}">
    <c:set var="uniqueId" value="${fieldName}" />
</c:if>
<c:if test="${!springInput && not empty fieldId}">
    <c:set var="uniqueId" value="${fieldId}" />
</c:if>

<c:set var="disabledStr" value=""/>
<c:if test="${disabled}">
    <c:set var="disabledStr" value=" disabled"/>
    <script type="text/javascript">disabledCalendars['${uniqueId}'] = true;</script>
</c:if>

<span style="white-space:nowrap;">

<c:if test="${springInput}">
    <form:input path="${fieldName}"
        size="10" maxlength="10" cssStyle="width:70px;"/>&nbsp;
</c:if>
<c:if test="${!springInput}">
    <input id="${uniqueId}" name="${fieldName}"${disabledStr} type="text"
        size="10" maxlength="10" value="${fieldValue}" style="width:70px;">&nbsp;
</c:if>

<cti:msg key="yukon.common.calendarcontrol.months" var="months"/>
<cti:msg key="yukon.common.calendarcontrol.days" var="days"/>
<cti:msg key="yukon.common.calendarcontrol.clear" var="clear"/>
<cti:msg key="yukon.common.calendarcontrol.close" var="close"/>

<c:url var="calImgUrl" value="/WebConfig/yukon/Icons/StartCalendar.gif"/>
    <span onclick="javascript:showCalendarControl('${uniqueId}', '${months}', '${days}', '${clear}', '${close}');" style="cursor:pointer;">
        <img id="calImg_${uniqueId}" src="${calImgUrl}" width="20" height="15" border="0" />
    </span>
</span>
