<%@ attribute name="fieldId" required="true" type="java.lang.String"%>
<%@ attribute name="fieldValue" required="false" type="java.util.Date"%>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>

<c:if test="${!empty fieldValue}">
    <%-- are these the best way to format these?  (Will DATE always work with the calendar?) --%>
    <cti:formatDate var="datePart" value="${fieldValue}" type="DATE"/>
    <cti:formatDate var="timePart" value="${fieldValue}" type="TIME24H"/>
</c:if>

    <c:set var="disabledStr" value=""/>
<c:if test="${!empty disabled}">
    <c:set var="disabledStr" value=" disabled"/>
</c:if>

<%-- TODO:  localize and consider merging into dateInputCalendar.tag, turning on via attributes --%>
<input type="hidden" id="${fieldId}" name="${fieldId}" value="${datePart} ${timePart}"/>
Date: <tags:dateInputCalendar fieldId="${fieldId}DatePart" fieldName="${fieldId}DatePart" fieldValue="${datePart}" disabled="${disabled}"/><br>
Time: <input id="${fieldId}TimePart" name="${fieldId}TimePart" type="text" size="5" value="${timePart}"${disabledStr}/>
