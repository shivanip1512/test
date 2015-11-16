<%@ tag description="A component that looks like a toggle group button and
                                          drives an html radio input." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="name" description="The name of the radio input." %>
<%@ attribute name="path" description="Spring path, used instead of 'name'." %>
<%@ attribute name="value" required="true" description="The name of the value radio input." %>

<%@ attribute name="checked" type="java.lang.Boolean" description="If 'true' switch will be on.  Default: false." %>
<%@ attribute name="disabled" type="java.lang.Boolean" description="If 'true' switch will be disabled.  Default: false." %>

<%@ attribute name="toggleGroup" description="Used to setup a toggle group." %>

<%@ attribute name="key" type="java.lang.Object" 
    description="i18n key object to use for the alert message. Note: key can be an i18n key String, 
        MessageSourceResolvable, Displayable, DisplayableEnum, or ResolvableTemplate.
        Note: the 'yukon.web.components.button' and 'yukon.common' scopes are added internally." %>
<%@ attribute name="label" description="Text used for the button. Html escaped internally." %>

<%@ attribute name="inputClass" description="CSS class names applied to the input." %>
<%@ attribute name="classes" description="CSS class names applied to the button element." %>
<%@ attribute name="id" description="The html id attribute of the radio input." %>

<cti:default var="checked" value="${false}"/>
<cti:default var="disabled" value="${false}"/>

<cti:displayForPageEditModes modes="VIEW">
    <c:if test="${not empty path}">
        <spring:bind path="${path}">
            <c:set var="checked" value="${status.value == value}" />
        </spring:bind>
    </c:if>
    <c:if test="${checked}">
        <c:choose>
            <c:when test="${not empty pageScope.key}">
                <cti:msgScope paths=", yukon.web.components.button, yukon.common">
                    <cti:msg2 key="${key}"/>
                </cti:msgScope>
            </c:when>
            <c:when test="${not empty pageScope.label}">${fn:escapeXml(label)}</c:when>
            <c:otherwise><jsp:doBody/></c:otherwise>
        </c:choose>
        <c:if test="${not empty path}">
            <spring:bind path="${path}">
                <form:radiobutton path="${path}" id="${id}" value="${value}" 
                    data-toggle="${pageScope.toggleGroup}" cssClass="dn ${pageScope.inputClass}"/>
            </spring:bind>
        </c:if> 
        <c:if test="${empty path}">
            <input type="radio" name="${name}" value="${value}" class="dn ${pageScope.inputClass}" checked="checked"/>
        </c:if> 
    </c:if>
    
    
</cti:displayForPageEditModes>

<cti:displayForPageEditModes modes="CREATE,EDIT">

    <label class="radio-btn">
        <c:choose>
            <c:when test="${not empty name}">
                <input type="radio" name="${name}" value="${value}" 
                    data-toggle="${pageScope.toggleGroup}" 
                    class="${pageScope.inputClass}"
                    <c:if test="${not empty pageScope.id}">id="${id}"</c:if> 
                    <c:if test="${checked}">checked</c:if> 
                    <c:if test="${disabled}">disabled</c:if>>
            </c:when>
            <c:otherwise>
                <form:radiobutton path="${path}" id="${id}" value="${value}" 
                    data-toggle="${pageScope.toggleGroup}"
                    cssClass="${pageScope.inputClass}"
                    disabled="${disabled}"/>
            </c:otherwise>
        </c:choose>
        <span class="button ${pageScope.classes}">
            <span class="b-label">
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
    
</cti:displayForPageEditModes>