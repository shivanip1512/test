<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="path" type="java.lang.String" description="Spring binding path." %>
<%@ attribute name="id" type="java.lang.String" description="Id of the field in the supplied object." %>
<%@ attribute name="name" type="java.lang.String" description="Name of the field in the supplied object." %>
<%@ attribute name="value" type="java.lang.Integer" description="Default: 0. The initial value to display in number of minutes." %>
<%@ attribute name="minValue" type="java.lang.Integer" description="The minimum value to allow in number of minutes." %>
<%@ attribute name="maxValue" type="java.lang.Integer" description="The maximum value to allow in number of minutes." %>
<%@ attribute name="disabled" type="java.lang.Boolean" description="Default: false. Determines if the input is disabled." %>
<%@ attribute name="cssClass" type="java.lang.String" description="Class added to the input of the widget." %>
<%@ attribute name="cssDialogClass" type="java.lang.String" description="Class added to the outer dialog div." %>
<%@ attribute name="stepHour" type="java.lang.Integer" description="Steps when incrementing/decrementing hours." %>
<%@ attribute name="stepMinute" type="java.lang.Integer" description="Steps when incrementing/decrementing minutes." %>
<%@ attribute name="wrapClass" type="java.lang.String" description="Class added to the wrapper of the widget." %>

<cti:msg2 var="timeOffsetChooseText" key="yukon.common.timeOffsetChoose"/>
<cti:msg2 var="timeOffsetText" key="yukon.common.timeOffset"/>
<cti:msg2 var="hoursText" key="yukon.common.hours"/>
<cti:msg2 var="minutesText" key="yukon.common.minutes"/>

<dt:pickerIncludes/>

<c:if test="${empty id}">
    <c:if test="${empty pageScope.path}">
        <cti:uniqueIdentifier var="id" prefix="timeInputId_"/>
    </c:if>
    <c:if test="${!empty pageScope.path}">
        <c:set var="id" value="${path}"/>
    </c:if>
</c:if>

<c:if test="${empty pageScope.disabled}">
    <c:set var="disabled" value="false"/>
</c:if>

<c:choose>
    <c:when test="${empty value && not empty pageScope.path}">
        <spring:bind path="${path}">
            <cti:formatDate var="displayValue" value="${status.actualValue}" type="TIME_OFFSET"/>
        </spring:bind>
    </c:when>
    <c:otherwise>
        <cti:default var="value" value="${!empty minValue ? minValue : 0}"/>
        <cti:formatDate var="displayValue" type="TIME_OFFSET" value="${value}"/>
    </c:otherwise>
</c:choose>

<c:if test="${!empty minValue}">
    <cti:formatDate var="minFormattedValue" type="TIME_OFFSET" value="${minValue}"/>
</c:if>
<c:if test="${!empty maxValue}">
    <cti:formatDate var="maxFormattedValue" type="TIME_OFFSET" value="${maxValue}"/>
</c:if>


<cti:msg var="jsDateTimeFormat" key="yukon.common.dateFormatting.TIME24H.js"/>

<c:choose>
    <c:when test="${not empty pageScope.path}">
        <spring:bind path="${path}">
            <cti:displayForPageEditModes modes="VIEW">
                ${displayValue}
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <span class="datetimeEntry_wrap timeOffsetWrap ${wrapClass}">
                    <form:input type="hidden" path="${path}"/>
                    <input id="${id}" 
                        name="${path}_inputField"
                        value="${displayValue}"
                        class="js-timeOffsetPicker js-timeOffsetPickerUI timeOffsetPicker ${cssClass}"
                        <c:if test="${disabled}">disabled="true"</c:if>
                        data-date-time-format="${jsDateTimeFormat}"
                        data-step-hour="${pageScope.stepHour}"
                        data-step-minute="${pageScope.stepMinute}"
                        data-class="${pageScope.cssDialogClass}"
                        data-timeOffsetChoose-text="${timeOffsetChooseText}"
                        data-timeOffset-text="${timeOffsetText}"
                        data-hours-text="${hoursText}"
                        data-minutes-text="${minutesText}"
                        data-value-field="${path}"
                        data-max-value="${maxFormattedValue}"
                        data-min-value="${minFormattedValue}"
                        autocomplete="off" />
                </span>
            </cti:displayForPageEditModes>
            <c:if test="${status.error}">
                <br>
                <form:errors path="${path}" cssClass="error" />
            </c:if>
        </spring:bind>
    </c:when>
    <c:otherwise>
        <cti:displayForPageEditModes modes="VIEW">
            ${displayValue}
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <span class="datetimeEntry_wrap timeOffsetWrap ${wrapClass}">
                <input type="hidden" <c:if test="${!empty pageScope.name}">name="${pageScope.name}"</c:if> value="${value}"/>
                <input id="${id}" 
                    <c:if test="${!empty pageScope.name}">name="${pageScope.name}_inputField"</c:if>
                    value="${displayValue}"
                    class="js-timeOffsetPicker js-timeOffsetPickerUI timeOffsetPicker ${cssClass}"
                    <c:if test="${disabled}">disabled="true"</c:if>
                    data-step-hour="${pageScope.stepHour}"
                    data-step-minute="${pageScope.stepMinute}"
                    data-class="${pageScope.cssDialogClass}"
                    data-date-time-format="${jsDateTimeFormat}"
                    data-timeOffsetChoose-text="${timeOffsetChooseText}"
                    data-timeOffset-text="${timeOffsetText}"
                    data-hours-text="${hoursText}"
                    data-minutes-text="${minutesText}"
                    data-value-field="${pageScope.name}"
                    data-max-value="${maxFormattedValue}"
                    data-min-value="${minFormattedValue}"
                    autocomplete="off" />
            </span>
        </cti:displayForPageEditModes>
    </c:otherwise>
</c:choose>
<span class="error dn js-${id}-min-value-error"><cti:msg2 key="yukon.common.timeOffsetMinError" argument="${minFormattedValue}"/></span>
<span class="error dn js-${id}-max-value-error"><cti:msg2 key="yukon.common.timeOffsetMaxError" argument="${maxFormattedValue}"/></span>