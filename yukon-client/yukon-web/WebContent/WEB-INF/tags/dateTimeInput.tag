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

<input type="hidden" id="${fieldId}" name="${fieldId}" value="${datePart} ${timePart}"/>
<table>
    <tr>
        <td><cti:msg key="yukon.common.calendarcontrol.date"/></td>
        <td><tags:dateInputCalendar fieldId="${fieldId}DatePart"
            fieldName="${fieldId}DatePart" fieldValue="${datePart}"
            disabled="${pageScope.disabled}"/></td>
    </tr>
    <tr>
        <td><cti:msg key="yukon.common.calendarcontrol.time"/></td>
        <td><input id="${fieldId}TimePart" name="${fieldId}TimePart" type="text"
            maxlength="5" size="10" style="width:70px;"
            value="${timePart}"${disabledStr}/></td>
    </tr>
</table>
