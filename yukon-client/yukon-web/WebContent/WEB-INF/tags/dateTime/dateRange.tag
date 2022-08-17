<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="startPath" description="Spring binding path" %>
<%@ attribute name="startName" description="Name of the field in the supplied object" %>
<%@ attribute name="startValue" type="java.lang.Object" description="Default: null. Sets the initial value of the input." %>
<%@ attribute name="endPath" description="Spring binding path" %>
<%@ attribute name="endName" description="Name of the field in the supplied object" %>
<%@ attribute name="endValue" type="java.lang.Object" description="Default: null. Sets the initial value of the input." %>
<%@ attribute name="maxDate" type="java.lang.Object" description="Default: null. Sets the maxDate for this dateRange" %>
<%@ attribute name="disabled" type="java.lang.Boolean" description="Default: false. Determines if the input is disabled." %>
<%@ attribute name="cssClass" description="Classes added to the input of the widget" %>
<%@ attribute name="cssDialogClass" description="Classes added to the outer dialog div" %>
<%@ attribute name="wrapperClasses" description="Classes added to the element wrapping the date pickers" %>
<%@ attribute name="toText" description="text between date range" %>
<%@ attribute name="toStyle" description="Classes added to the element wrapping the date pickers" %>
<%@ attribute name="displayValidationToRight" type="java.lang.Boolean" description="If true, any validation will display to the right of the field. Default: false." %>

<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="js-dateRange oh ${wrapperClasses}">
    <dt:date path="${pageScope.startPath}"
             name="${pageScope.startName}"
             value="${pageScope.startValue}"
             disabled="${pageScope.disabled}"
             cssClass="${pageScope.cssClass} js-dateStart"
             cssDialogClass="${pageScope.cssDialogClass}"
             maxDate="${pageScope.endValue}"
             displayValidationToRight="${pageScope.displayValidationToRight}"/>
    <jsp:doBody/>
    <c:if test="${not empty pageScope.toText}">
        <span class="fl" style="${pageScope.toStyle}">${pageScope.toText}</span>
    </c:if>
<!-- Only force includes on the first input -->
    <dt:date path="${pageScope.endPath}"
             name="${pageScope.endName}"
             value="${pageScope.endValue}"
             disabled="${pageScope.disabled}"
             cssClass="${pageScope.cssClass} js-dateEnd"
             cssDialogClass="${pageScope.cssDialogClass}"
             minDate="${pageScope.startValue}" 
             maxDate="${pageScope.maxDate}" 
             displayValidationToRight="${pageScope.displayValidationToRight}"/>
</div>