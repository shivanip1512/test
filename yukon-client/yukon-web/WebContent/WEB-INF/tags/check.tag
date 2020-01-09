<%@ tag description="A component that looks like a toggle group button and
                                          drives an html checkbox input." trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="name" description="The name of the radio input." %>
<%@ attribute name="path" description="Spring path, used instead of 'name'." %>
<%@ attribute name="value" description="The name of the value radio input." %>

<%@ attribute name="checked" type="java.lang.Boolean" description="If 'true' switch will be on.  Default: false." %>
<%@ attribute name="disabled" type="java.lang.Boolean" description="If 'true' switch will be disabled.  Default: false." %>
<%@ attribute name="forceDisplayCheckbox" type="java.lang.Boolean" description="If 'true' the button will be displayed in VIEW mode." %>

<%@ attribute name="toggleGroup" description="Used to setup a toggle group." %>

<%@ attribute name="key" type="java.lang.Object"
    description="i18n key object to use for the alert message. Note: key can be an i18n key String, 
        MessageSourceResolvable, Displayable, DisplayableEnum, or ResolvableTemplate.
        Note: the 'yukon.web.components.button' and 'yukon.common' scopes are added internally." %>
<%@ attribute name="label" description="Text used for the button. Html escaped internally." %>

<%@ attribute name="classes" description="CSS class names applied to the button element." %>
<%@ attribute name="buttonStyle" description="Style applied to the button." %>
<%@ attribute name="buttonTextClasses" description="CSS class names applied to the button text." %>
<%@ attribute name="buttonTextStyle" description="styles applied to the button text." %>
<%@ attribute name="id" description="The html id attribute of the radio input." %>
<%@ attribute name="onclick" description="Event to fire on click." %>


<cti:default var="checked" value="${false}"/>
<cti:default var="disabled" value="${false}"/>

<cti:displayForPageEditModes modes="VIEW">
    <c:if test="${not empty path}">
        <spring:bind path="${path}">
            <c:set var="checked" value="${status.value}" />
        </spring:bind>
    </c:if>
    <c:if test="${not empty checked}">
        <c:choose>
            <c:when test="${not empty pageScope.key}">
                <cti:msgScope paths=", yukon.web.components.button, yukon.common">
                    <cti:msg2 key="${key}"/>
                </cti:msgScope>
            </c:when>
            <c:when test="${not empty pageScope.label}">${fn:escapeXml(label)}</c:when>
            <c:otherwise><jsp:doBody/></c:otherwise>
        </c:choose>
    </c:if>
    
</cti:displayForPageEditModes>

<c:if test="${mode == 'EDIT' or mode == 'CREATE' or forceDisplayCheckbox or empty mode}">
    <label class="switch-btn">
        <c:choose>
            <c:when test="${not empty name}">
                <input type="checkbox" class="switch-btn-checkbox" name="${name}" value="${value}" 
                    <c:if test="${not empty pageScope.id}">id="${id}"</c:if> 
                    <c:if test="${not empty pageScope.onclick}">onclick="${onclick}"</c:if> 
                    <c:if test="${checked}">checked</c:if> 
                    <c:if test="${disabled}">disabled</c:if>>
            </c:when>
            <c:otherwise>
                <form:checkbox class="switch-btn-checkbox" path="${path}" value="${value}" id="${id}" onclick="${onclick}" disabled="${disabled}"/>
            </c:otherwise>
        </c:choose>
        <span class="button ${pageScope.classes} yes" style="${buttonStyle}">
            <span class="b-label ${pageScope.buttonTextClasses}" style="${pageScope.buttonTextStyle}">
                <c:choose>
                    <c:when test="${not empty pageScope.key}">
                        <cti:msgScope paths=", yukon.web.components.button, yukon.common">
                            <cti:msg2 key="${key}"/>
                        </cti:msgScope>
                    </c:when>
                    <c:when test="${not empty pageScope.label}">${fn:escapeXml(label)}</c:when>
                    <c:otherwise><jsp:doBody/></c:otherwise>
                </c:choose>
            </span>
        </span>
    </label>
</c:if>