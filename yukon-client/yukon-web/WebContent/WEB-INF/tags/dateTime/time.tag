<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="path" required="false" description="Spring binding path"%>
<%@ attribute name="id" description="Name of the field in the supplied object"%>
<%@ attribute name="name" description="Name of the field in the supplied object"%>
<%@ attribute name="value" type="java.lang.Object" description="Default: null. Sets the initial value of the input." %>
<%@ attribute name="disabled" type="java.lang.Boolean" description="Default: false. Determines if the input is disabled." %>
<%@ attribute name="cssClass" type="java.lang.String" description="Class added to the input of the widget" %>
<%@ attribute name="cssDialogClass" type="java.lang.String" description="Class added to the outer dialog div" %>
<%@ attribute name="maxDate" type="java.lang.Object" description="Set a maximum selectable date via a Date object or as a string in the current dateFormat, or a number of days from today (e.g. +7) or a string of values and periods ('y' for years, 'm' for months, 'w' for weeks, 'd' for days, e.g. '+1m +1w'), or null for no limit." %>
<%@ attribute name="minDate" type="java.lang.Object" description="Set a minimum selectable date via a Date object or as a string in the current dateFormat, or a number of days from today (e.g. +7) or a string of values and periods ('y' for years, 'm' for months, 'w' for weeks, 'd' for days, e.g. '+1m +1w'), or null for no limit." %>
<%@ attribute name="stepHour" type="java.lang.String" description="Steps when incrementing/decrementing hours" %>
<%@ attribute name="stepMinute" type="java.lang.String" description="Steps when incrementing/decrementing minutes" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<dt:pickerIncludes/>

<c:if test="${empty id}">
	<c:if test="${empty pageScope.path}">
		<cti:uniqueIdentifier var="id" prefix="timeInputId_"/>
    </c:if>
    <c:if test="${!empty pageScope.path}">
	    <c:set var="id" value="${path}"/>
    </c:if>
</c:if>

<c:set var="timeValue" value=""/>
<c:set var="timeZoneShort" value=""/>
<c:set var="timeZoneFull" value=""/>

<c:choose>
	<c:when test="${empty pageScope.value}">
		<c:if test="${not empty pageScope.path && not empty status.actualValue}">
			<spring:bind path="${path}">
			    <cti:formatDate var="timeValue" value="${status.actualValue}" type="TIME24H"/>
			    <cti:formatDate var="timeZoneShort" value="${status.actualValue}" type="TIMEZONE"/>
			    <cti:formatDate var="timeZoneFull" value="${status.actualValue}" type="TIMEZONE_EXTENDED"/>
		    </spring:bind>
	    </c:if>
	</c:when>
	<c:otherwise>
	    <cti:formatDate var="timeValue" value="${pageScope.value}" type="TIME24H"/>
	    <cti:formatDate var="timeZoneShort" value="${pageScope.value}" type="TIMEZONE"/>
	    <cti:formatDate var="timeZoneFull" value="${pageScope.value}" type="TIMEZONE_EXTENDED"/>
	</c:otherwise>
</c:choose>

<c:if test="${!empty pageScope.minDate}">
    <cti:formatDate var="minFormattedDate" value="${pageScope.minDate}" type="DATE"/>
</c:if>
<c:if test="${!empty pageScope.maxDate}">
    <cti:formatDate var="maxFormattedDate" value="${pageScope.maxDate}" type="DATE"/>
</c:if>

<c:if test="${empty pageScope.disabled}">
    <c:set var="disabled" value="false"/>
</c:if>

<cti:msg var="jsDateTimeFormat" key="yukon.common.dateFormatting.TIME24H.js" />

<c:choose>
	<c:when test="${not empty pageScope.path}">
		<spring:bind path="${path}">
			<cti:displayForPageEditModes modes="VIEW">
				${status.value}
			</cti:displayForPageEditModes>
			<cti:displayForPageEditModes modes="EDIT,CREATE">
                <form:input id="${id}" 
                            path="${path}"
                            value="${timeValue}"
                            cssClass="f-timePicker f-timePickerUI timePicker ${cssClass}"
                            disabled="${pageScope.disabled}"
                            data-date-time-format="${jsDateTimeFormat}"
                            data-max-date="${maxFormattedDate}"
                            data-min-date="${minFormattedDate}"
                            data-step-hour="${pageScope.stepHour}"
                            data-step-minute="${pageScope.stepMinute}"
                            data-time-zone-short="${timeZoneShort}"
                            data-time-zone-full="${timeZoneFull}"
                            data-class="${pageScope.cssDialogClass}"
                            autocomplete="off" />
			</cti:displayForPageEditModes>
			<c:if test="${status.error}">
				<br>
				<form:errors path="${path}" cssClass="errorMessage" />
			</c:if>
		</spring:bind>
	</c:when>
	<c:otherwise>
		<cti:displayForPageEditModes modes="VIEW">
				${timeValue}
			</cti:displayForPageEditModes>
			<cti:displayForPageEditModes modes="EDIT,CREATE">
				<input	id="${id}" 
                        <c:if test="${!empty pageScope.name}">name="${pageScope.name}"</c:if>
				        value="${timeValue}"
						class="f-timePicker f-timePickerUI timePicker ${cssClass}"
						<c:if test="${disabled}">disabled="true"</c:if>
						data-max-date="${maxFormattedDate}"
						data-min-date="${minFormattedDate}"
                        data-step-hour="${pageScope.stepHour}"
                        data-step-minute="${pageScope.stepMinute}"
                        data-class="${pageScope.cssDialogClass}"
						data-date-time-format="${jsDateTimeFormat}"
                        data-time-zone-short="${timeZoneShort}"
                        data-time-zone-full="${timeZoneFull}"
                        autocomplete="off" />
			</cti:displayForPageEditModes>
	</c:otherwise>
</c:choose>