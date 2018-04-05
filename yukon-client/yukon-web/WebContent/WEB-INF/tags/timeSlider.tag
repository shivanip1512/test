<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="minValue" type="java.lang.Integer" description="The minimum value that is allowed for the time.  Default will be 0 (12:00am)."%>
<%@ attribute name="maxValue" type="java.lang.Integer" description="The maximum value that is allowed for the time.  Default will be 1440 (12:00pm)."%>
<%@ attribute name="stepValue" type="java.lang.Integer" description="The value that should be used for the step intervals.  Default will be 15 (15 minute intervals)."%>
<%@ attribute name="rangeEnabled" type="java.lang.Boolean" description="If true, the time slider should allow a range of values (start and stop).  Default will be false." %>
<%@ attribute name="showLabels" type="java.lang.Boolean" description="If true, display labels underneath the slider to show the time at that spot.  Default will be false." %>
<%@ attribute name="displayTimeToLeft" type="java.lang.Boolean" description="If true, display the start and/or stop times to the left of the slider, otherwise it will be on the top.  Default will be false." %>
<%@ attribute name="dataToggleGroup" type="java.lang.String" description="The data-toggle-group to use for the time slider (slider and inputs)." %>
<%@ attribute name="startPath" type="java.lang.String" description="The path to use for the start time.  Requries either startPath or startName." %>
<%@ attribute name="endPath" type="java.lang.String" description="The path to use for the end time.  This is only used if rangeEnabled=true" %>
<%@ attribute name="startName" type="java.lang.String" description="The name to use for the start time." %>
<%@ attribute name="startValue" type="java.lang.String" description="The value to use for the start time." %>
<%@ attribute name="endName" type="java.lang.String" description="The name to use for the end time.  This is only used if rangeEnabled=true" %>
<%@ attribute name="endValue" type="java.lang.String" description="The value to use for the end time. This is only used if rangeEnabled=true" %>
<%@ attribute name="viewOnlyMode" type="java.lang.Boolean" description="Displays the time value but not the slider.  Default value is false."%>
<%@ attribute name="timeFormat" type="java.lang.String" description="The format to save and retrieve the time in.  Possible values are SECONDS, MINUTES, HHMM.  Default is MINUTES."%>

<span class="js-time-slider-div">

    <c:choose>
        <c:when test="${not empty startPath}">
            <form:hidden path="${startPath}" cssClass="js-start-time"/>
            <c:if test="${rangeEnabled}">
                <c:if test="${empty endPath}">
                    <div class="error">timeSliderTag requires endPath parameter when rangeEnabled=true</div>
                </c:if>
                <form:hidden path="${endPath}" cssClass="js-end-time"/>
            </c:if>
        </c:when>
        <c:when test="${not empty startName}">
            <input type="hidden" name="${startName}" value="${startValue}" class="js-start-time"/>
            <c:if test="${rangeEnabled}">
                <c:if test="${empty endName}">
                    <div class="error">timeSliderTag requires endName parameter when rangeEnabled=true</div>
                </c:if>
                <input type="hidden" name="${endName}" value="${endValue}" class="js-end-time"/>
            </c:if>
        </c:when>
        <c:otherwise>
            <div class="error">timeSliderTag requires use of either startPath or startName parameter</div>
        </c:otherwise>
    </c:choose>
    
    <c:set var="displaySliderClass" value="${viewOnlyMode ? 'style=display:none;' : ''}"/>
    <c:set var="labelWidth" value="${rangeEnabled ? 'style=width:140px;' : 'style=width:70px;'}"/>
    <c:set var="columnLayout" value="${rangeEnabled ? 'column-8-16' : 'column-6-18'}"/>
    <c:choose>
        <c:when test="${displayTimeToLeft}">
            <div class="dib ${columnLayout} clearfix vat">
                <div class="column one">
                    <div class="js-time-label fwb" ${labelWidth} data-toggle-group="${dataToggleGroup}"></div>
                </div>
                <div class="column two nogutter">
                    <div class="js-time-slider buffered" ${displaySliderClass} style="margin-top:5px;width:250px;" data-toggle-group="${dataToggleGroup}"
                        data-step-value="${stepValue}" data-time-format="${timeFormat}" data-min-value="${minValue}" data-max-value="${maxValue}" data-show-labels="${showLabels}"></div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="js-time-label fwb" data-toggle-group="${dataToggleGroup}"></div>
            <div class="js-time-slider buffered full-width" ${displaySliderClass} data-toggle-group="${dataToggleGroup}"
                data-step-value="${stepValue}" data-time-format="${timeFormat}" data-min-value="${minValue}" data-max-value="${maxValue}" data-show-labels="${showLabels}"></div>
        </c:otherwise>
    </c:choose>
</span>

<cti:includeScript link="YUKON_TIME_FORMATTER"/>
<cti:includeScript link="/resources/js/common/yukon.ui.timeSlider.js"/>

