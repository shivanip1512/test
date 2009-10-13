<%@ attribute name="fieldId" required="true" type="java.lang.String"%>
<%@ attribute name="fieldValue" required="false" type="java.util.Date"%>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>

<c:if test="${!empty pageScope.fieldValue}">
    <%-- are these the best way to format these?  (Will DATE always work with the calendar?) --%>
    <cti:formatDate var="datePart" value="${pageScope.fieldValue}" type="DATE"/>
    <cti:formatDate var="timePart" value="${pageScope.fieldValue}" type="TIME24H"/>
</c:if>

    <c:set var="disabledStr" value=""/>
<c:if test="${!empty pageScope.disabled}">
    <c:set var="disabledStr" value=" disabled"/>
</c:if>

<%-- TODO:  localize and consider merging into dateInputCalendar.tag, turning on via attributes --%>
<input type="hidden" id="${fieldId}" name="${fieldId}" value="${datePart} ${timePart}"/>
<cti:msg key="yukon.common.calendarcontrol.date"/><tags:dateInputCalendar fieldId="${fieldId}DatePart" fieldName="${fieldId}DatePart" fieldValue="${datePart}" disabled="${pageScope.disabled}"/><br>
<cti:msg key="yukon.common.calendarcontrol.time"/><input id="${fieldId}TimePart" name="${fieldId}TimePart" type="text" size="5" value="${timePart}"${disabledStr}/>
