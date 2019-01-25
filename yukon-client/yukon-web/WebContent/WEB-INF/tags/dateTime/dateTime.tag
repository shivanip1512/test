<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="path" description="Spring binding path." %>
<%@ attribute name="name" description="Name of the input." %>
<%@ attribute name="toggleGroup" description="Used to setup a toggle group driven by a checkbox." %>

<%@ attribute name="id" description="Id of the input." %>
<%@ attribute name="value" type="java.lang.Object" description="Default: null. Sets the initial value of the input." %>
<%@ attribute name="disabled" type="java.lang.Boolean" description="Default: false. Determines if the input is disabled." %>

<%@ attribute name="wrapperClass" description="CSS class names applied to the outer container of the picker component." %>
<%@ attribute name="cssClass" description="CSS class names applied to the text input" %>
<%@ attribute name="cssDialogClass" description="CSS class names applied to the popup container." %>
<%@ attribute name="cssErrorClass" description="CSS class names applied to the error element. 
                                                Note: error element defaults to span." %>
<%@ attribute name="maxDate" type="java.lang.Object" 
    description="Set a maximum selectable date via an object consumable by the DateFormatingService#format method (Date, ReadablePartial, ReadableInstant, Long)." %>
<%@ attribute name="minDate" type="java.lang.Object" 
    description="Set a minimum selectable date via an object consumable by the DateFormatingService#format method (Date, ReadablePartial, ReadableInstant, Long)." %>
<%@ attribute name="stepHour" description="Steps when incrementing/decrementing hours. If step hours is greater than 1, 
                                           step minutes will be ignored." %>
<%@ attribute name="stepMinute" description="Steps when incrementing/decrementing minutes." %>

<dt:pickerIncludes/>

<c:if test="${empty pageScope.id}">
    <c:if test="${empty pageScope.path}">
        <cti:uniqueIdentifier var="id" prefix="dateTimeInputId_"/>
    </c:if>
    <c:if test="${!empty pageScope.path}">
        <c:set var="id" value="${pageScope.path}"/>
    </c:if>
</c:if>

<cti:default var="wrapperClass" value=""/>
<c:set var="dateTimeValue" value=""/>
<c:set var="timeZoneShort" value=""/>
<c:set var="timeZoneFull" value=""/>

<c:choose>
    <c:when test="${empty pageScope.value}">
        <c:if test="${not empty pageScope.path}">
            <spring:bind path="${path}">
                <c:if test="${not empty pageScope.path && not empty status.actualValue}">
                    <cti:formatDate var="dateTimeValue" value="${status.actualValue}" type="DATEHM"/>
                    <cti:formatDate var="timeZoneShort" value="${status.actualValue}" type="TIMEZONE"/>
                    <cti:formatDate var="timeZoneFull" value="${status.actualValue}" type="TIMEZONE_EXTENDED"/>
                </c:if>
            </spring:bind>
        </c:if>
    </c:when>
    <c:otherwise>
        <cti:formatDate var="dateTimeValue" value="${pageScope.value}" type="DATEHM"/>
        <cti:formatDate var="timeZoneShort" value="${pageScope.value}" type="TIMEZONE"/>
        <cti:formatDate var="timeZoneFull" value="${pageScope.value}" type="TIMEZONE_EXTENDED"/>
    </c:otherwise>
</c:choose>

<c:if test="${!empty pageScope.minDate}">
    <cti:formatDate var="minFormattedDate" value="${pageScope.minDate}" type="DATEHM"/>
</c:if>
<c:if test="${!empty pageScope.maxDate}">
    <cti:formatDate var="maxFormattedDate" value="${pageScope.maxDate}" type="DATEHM"/>
</c:if>

<c:if test="${empty pageScope.disabled}">
    <c:set var="disabled" value="false"/>
</c:if>

<cti:msg var="jsDateTimeFormat" key="yukon.common.dateFormatting.BOTH.js" />

<c:choose>
    <c:when test="${not empty pageScope.path}">
        <spring:bind path="${pageScope.path}">
            <cti:displayForPageEditModes modes="VIEW">${dateTimeValue}</cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <c:if test="${status.error}"><c:set var="cssClass" value="${cssClass} error"/></c:if>
                <span class="datetimeEntry_wrap ${wrapperClass}">
                    <form:input id="${id}" 
                        path="${path}" 
                        value="${dateTimeValue}"
                        cssClass="js-dateTimePicker js-dateTimePickerUI dateTimePicker ${cssClass}"
                        disabled="${pageScope.disabled}"
                        data-date-time-format="${jsDateTimeFormat}"
                        data-max-date="${pageScope.maxFormattedDate}"
                        data-min-date="${pageScope.minFormattedDate}"
                        data-step-hour="${pageScope.stepHour}"
                        data-step-minute="${pageScope.stepMinute}"
                        data-time-zone-short="${timeZoneShort}"
                        data-time-zone-full="${timeZoneFull}"
                        data-class="${pageScope.cssDialogClass}"
                        data-toggle-group="${pageScope.toggleGroup}" 
                        autocomplete="off"/>
                </span>
            </cti:displayForPageEditModes>
            <c:if test="${status.error}">
                <br>
                <%-- TODO: <br> doesn't work here. Since this date/time element will be taller than
                     than line height, a <br> will only bump you down line height. Need figure out best way to fix. --%>
                <form:errors path="${pageScope.path}" cssClass="${cssErrorClass} error" />
            </c:if>
        </spring:bind>
    </c:when>
    <c:otherwise>
        <cti:displayForPageEditModes modes="VIEW">
                ${dateTimeValue}
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <span class="datetimeEntry_wrap ${wrapperClass}">
                    <input id="${id}" 
                        <c:if test="${!empty pageScope.name}">name="${pageScope.name}"</c:if>
                        value="${dateTimeValue}"
                        class="js-dateTimePicker js-dateTimePickerUI dateTimePicker ${cssClass}"
                        <c:if test="${disabled}">disabled="true"</c:if>
                        data-max-date="${pageScope.maxFormattedDate}"
                        data-min-date="${pageScope.minFormattedDate}"
                        data-step-hour="${pageScope.stepHour}"
                        data-step-minute="${pageScope.stepMinute}"
                        data-class="${pageScope.cssDialogClass}"
                        data-date-time-format="${jsDateTimeFormat}"
                        data-time-zone-short="${timeZoneShort}"
                        data-time-zone-full="${timeZoneFull}"
                        autocomplete="off"/>
                </span>
            </cti:displayForPageEditModes>
    </c:otherwise>
</c:choose>