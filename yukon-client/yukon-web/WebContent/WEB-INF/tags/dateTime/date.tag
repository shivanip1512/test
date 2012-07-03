<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="path" required="false" description="Spring binding path"%>
<%@ attribute name="id" description="Name of the field in the supplied object"%>
<%@ attribute name="value" type="java.lang.Object" description="Default: null. Sets the initial value of the input." %>
<%@ attribute name="disabled" type="java.lang.Boolean" description="Default: false. Determines if the input is disabled." %>
<%@ attribute name="cssClass" type="java.lang.String" description="Class added to the input of the widget" %>
<%@ attribute name="cssDialogClass" type="java.lang.String" description="Class added to the outer dialog div" %>
<%@ attribute name="maxDate" type="java.lang.String" description="Set a maximum selectable date via a Date object or as a string in the current dateFormat, or a number of days from today (e.g. +7) or a string of values and periods ('y' for years, 'm' for months, 'w' for weeks, 'd' for days, e.g. '+1m +1w'), or null for no limit." %>
<%@ attribute name="minDate" type="java.lang.String" description="Set a maximum selectable date via a Date object or as a string in the current dateFormat, or a number of days from today (e.g. +7) or a string of values and periods ('y' for years, 'm' for months, 'w' for weeks, 'd' for days, e.g. '+1m +1w'), or null for no limit." %>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ include file="pickerIncludes.jspf" %>

<c:if test="${empty id}">
	<c:if test="${empty pageScope.path}">
		<cti:uniqueIdentifier var="id" prefix="dateTimeInputId_"/>
    </c:if>
    <c:if test="${!empty pageScope.path}">
	    <c:set var="id" value="${path}"/>
    </c:if>
</c:if>

<c:if test="${!empty pageScope.value}">
    <cti:formatDate var="dateTime" value="${pageScope.value}" type="DATE"/>
    <cti:formatDate var="timeZoneShort" value="${pageScope.value}" type="TIMEZONE"/>
    <cti:formatDate var="timeZoneFull" value="${pageScope.value}" type="TIMEZONE_FULL"/>
</c:if>

<c:if test="${!empty pageScope.disabled}">
    <c:set var="disabled" value="false"/>
</c:if>

<cti:msg2 var="jsDateTimeFormat" key="yukon.common.dateFormatting.DATE.js" />

<c:choose>
	<c:when test="${not empty pageScope.path}">
		<spring:bind path="${path}">
			<cti:displayForPageEditModes modes="VIEW">
				${dateTime}
			</cti:displayForPageEditModes>
			<cti:displayForPageEditModes modes="EDIT,CREATE">
                <form:input id="${id}" 
                            path="${path}"
                            value="${dateTime}"
                            cssClass="f_datePicker f_datePickerUI ${cssClass}"
                            disabled="${pageScope.disabled}"
                            data-date-time-format="${jsDateTimeFormat}"
                            data-max-date="${pageScope.maxDate}"
                            data-min-date="${pageScope.minDate}"
                            data-time-zone-short="${timeZoneShort}"
                            data-time-zone-full="${timeZoneFull}"
                            data-class="${pageScope.cssDialogClass}" />
			</cti:displayForPageEditModes>
			<c:if test="${status.error}">
				<br>
				<form:errors path="${path}" cssClass="errorMessage" />
			</c:if>
		</spring:bind>
	</c:when>
	<c:otherwise>
		<cti:displayForPageEditModes modes="VIEW">
				${dateTime}
			</cti:displayForPageEditModes>
			<cti:displayForPageEditModes modes="EDIT,CREATE">
				<input	id="${id}" 
                        value="${dateTime}"
						class="f_datePicker f_datePickerUI ${cssClass}"
						<c:if test="${disabled}">disabled="true"</c:if>
						<c:if test="${!empty pageScope.maxDate}">data-max-date="${pageScope.maxDate}"</c:if>
						<c:if test="${!empty pageScope.minDate}">data-min-date="${pageScope.minDate}"</c:if>
                        <c:if test="${!empty pageScope.cssDialogClass}">data-class="${pageScope.cssDialogClass}"</c:if>
                        data-date-time-format="${jsDateTimeFormat}"
                        data-time-zone-short="${timeZoneShort}"
                        data-time-zone-full="${timeZoneFull}"/>
			</cti:displayForPageEditModes>
	</c:otherwise>
</c:choose>