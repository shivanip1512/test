<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="toggleGroup" type="java.lang.String" description="Used to setup a toggle group driven by a checkbox." %>
<%@ attribute name="autocomplete" type="java.lang.String" description="HTML input autocomplete attribute. Possible values: 'on|off'. Default: 'on'." %>
<%@ attribute name="disabled" type="java.lang.Boolean" description="Set this value to true to disable the field."%>
<%@ attribute name="id" type="java.lang.String" description="The id for the field. If not set, the id will be the same as path."%>
<%@ attribute name="inputClass" type="java.lang.String" description="The classes to assign to the input."%>
<%@ attribute name="onkeyup" type="java.lang.String" description="Function to call on key up."%>
<%@ attribute name="onchange" type="java.lang.String" description="Function to call when the value changes."%>
<%@ attribute name="onblur" type="java.lang.String" description="Function to call on blur."%>
<%@ attribute name="maxlength" type="java.lang.Integer" description="The max number of characters to allow for the field." %>
<%@ attribute name="path" required="true" type="java.lang.String" description="The spring binding path."%>
<%@ attribute name="placeholder" type="java.lang.String" description="The placeholder text to use when the field is empty." %>
<%@ attribute name="readonly" type="java.lang.Boolean" description="Whether to make the field readonly."%>
<%@ attribute name="size" type="java.lang.Integer" description="The size to give the field."%>
<%@ attribute name="tabindex" type="java.lang.Integer" description="The tab index for the field."%>
<%@ attribute name="autofocus" type="java.lang.String" description="Gives initial focus to the field."%>
<%@ attribute name="units" type="java.lang.String" description="The units to display to the right of the field (ex: Volts)." %>
<%@ attribute name="displayValidationToRight" type="java.lang.Boolean" description="If true, any validation will display to the right of the field. Default: false." %>
<%@ attribute name="minValue" type="java.lang.String" description="The minimum value that can be entered." %>
<%@ attribute name="maxValue" type="java.lang.String" description="The maximum value that can be entered." %>
<%@ attribute name="stepValue" type="java.lang.Float" description="The step value to increment/decrement when clicking on the arrows." %>
<%@ attribute name="isDecimalNumber" type="java.lang.Boolean" description="Set this value to ture if the number is decimal." %>

<cti:default var="autocomplete" value="on"/>

<c:set var="id" value="${pageScope.id}"/>
<c:if test="${empty pageScope.id}">
    <cti:uniqueIdentifier var="uniqueId"/>
    <c:set var="id" value="${uniqueId}"/>
</c:if>

<spring:bind path="${path}">

    <%-- VIEW MODE --%>
    <cti:displayForPageEditModes modes="VIEW">
        <span id="${id}" class="${pageScope.inputClass}" data-toggle-group="${pageScope.toggleGroup}">
            ${fn:escapeXml(status.value)}
        </span>
        <form:hidden path="${path}" cssClass="${pageScope.inputClass}"/>
        <c:if test="${pageScope.units != null}">
            &nbsp;${pageScope.units}
        </c:if>
    </cti:displayForPageEditModes>
    
    <%-- EDIT/CREATE MODE --%>
    <cti:displayForPageEditModes modes="EDIT,CREATE">
    
        <c:if test="${status.error}">
            <c:set var="inputClass" value="error ${pageScope.inputClass}"/>
        </c:if>
        
        <form:input path="${path}"
            id="${id}"
            disabled="${pageScope.disabled}" 
            readonly="${pageScope.readonly}"
            size="${pageScope.size}" 
            maxlength="${pageScope.maxlength}"
            autocomplete="${autocomplete}" 
            cssClass="${pageScope.inputClass} js-numeric"
            onkeyup="${pageScope.onkeyup}" 
            onchange="${pageScope.onchange}"
            onblur="${pageScope.onblur}"
            tabindex="${pageScope.tabindex}"
            autofocus="${pageScope.autofocus}"
            data-toggle-group="${pageScope.toggleGroup}"
            data-min-value="${pageScope.minValue}"
            data-max-value="${pageScope.maxValue}"
            data-is-decimal-number="${pageScope.isDecimalNumber}"
            data-step-value="${pageScope.stepValue}"
            placeholder="${pageScope.placeholder}"/>
        <c:if test="${pageScope.units != null}">
            &nbsp;${pageScope.units}
        </c:if>
        <c:if test="${status.error}">
            <c:if test="${!displayValidationToRight}">
                <br>
            </c:if>
            <form:errors path="${path}" cssClass="error"/>
        </c:if>

        <c:choose>
            <c:when test="${displayValidationToRight}">
                <span class="error dn js-${id}-min-value-error ML15"><cti:msg2 key="yukon.common.numericMinError" argument="${minValue}"/></span>
                <span class="error dn js-${id}-max-value-error ML15"><cti:msg2 key="yukon.common.numericMaxError" argument="${maxValue}"/></span>
            </c:when>
            <c:otherwise>
                <div class="error dn js-${id}-min-value-error"><cti:msg2 key="yukon.common.numericMinError" argument="${minValue}"/></div>
                <div class="error dn js-${id}-max-value-error ${errorClass}"><cti:msg2 key="yukon.common.numericMaxError" argument="${maxValue}"/></div>
            </c:otherwise>
        </c:choose>
        
    </cti:displayForPageEditModes>

</spring:bind>

<cti:includeScript link="/resources/js/common/yukon.ui.numeric.js" force="true"/>
