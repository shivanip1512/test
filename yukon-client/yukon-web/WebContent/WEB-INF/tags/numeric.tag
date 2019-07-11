<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="toggleGroup" description="Used to setup a toggle group driven by a checkbox." %>
<%@ attribute name="autocomplete" 
        description="HTML input autocomplete attribute. Possible values: 'on|off'. Default: 'on'." %>
<%@ attribute name="disabled" %>
<%@ attribute name="id" %>
<%@ attribute name="inputClass" %>
<%@ attribute name="onkeyup" %>
<%@ attribute name="onchange" %>
<%@ attribute name="onblur" %>
<%@ attribute name="maxlength" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="placeholder" %>
<%@ attribute name="readonly" %>
<%@ attribute name="size" %>
<%@ attribute name="tabindex" %>
<%@ attribute name="autofocus" %>
<%@ attribute name="units" %>
<%@ attribute name="displayValidationToRight" type="java.lang.Boolean" description="If true, any validation will display to the right of the field. Default: false." %>
<%@ attribute name="minValue" type="java.lang.String" description="The minimum value that can be entered." %>
<%@ attribute name="maxValue" type="java.lang.String" description="The maximum value that can be entered." %>
<%@ attribute name="stepValue" type="java.lang.Integer" description="The step value to increment/decrement when clicking on the arrows." %>

<cti:default var="autocomplete" value="on"/>

<c:if test="${empty id}">
    <c:set var="id" value="${path}"/>
</c:if>

<spring:bind path="${path}">

    <%-- VIEW MODE --%>
    <cti:displayForPageEditModes modes="VIEW">
        <span id="${pageScope.id}" class="${pageScope.inputClass}" data-toggle-group="${pageScope.toggleGroup}">
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
            id="${pageScope.id}"
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
        
        <c:if test="${!displayValidationToRight}">
            <br>
        </c:if>
        <span class="error dn js-${id}-min-value-error"><cti:msg2 key="yukon.common.numericMinError" argument="${minValue}"/></span>
        <span class="error dn js-${id}-max-value-error"><cti:msg2 key="yukon.common.numericMaxError" argument="${maxValue}"/></span>
    
    </cti:displayForPageEditModes>

</spring:bind>

<cti:includeScript link="/resources/js/common/yukon.ui.numeric.js"/>
