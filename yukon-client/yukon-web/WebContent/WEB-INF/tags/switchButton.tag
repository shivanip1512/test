<%@ tag body-content="empty" description="A component that looks like a toggle switch of two buttons and
                                          drives an html checkbox." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="name" description="The name of the checkbox input." %>
<%@ attribute name="path" description="Spring path, used instead of 'name'." %>

<%@ attribute name="checked" type="java.lang.Boolean" description="If 'true' switch will be on.  Default: false." %>
<%@ attribute name="disabled" type="java.lang.Boolean" description="If 'true' switch will be disabled.  Default: false." %>
<%@ attribute name="inverse" type="java.lang.Boolean" description="If true, when the checkbox is checked, the red button 
                                                                   will be toggled on instead of the green button." %>
                                                                   
<%@ attribute name="toggleGroup" description="Used to setup a toggle group driven by a checkbox." %>

<%@ attribute name="onNameKey" description="i18n key used for the on button." %> 
<%@ attribute name="offNameKey" description="i18n key used for the off button." %> 

<%@ attribute name="classes" description="CSS class names applied to the outer label element." %>
<%@ attribute name="color" type="java.lang.Boolean" description="If 'true', buttons will have red and green text color.
                                                                 Default: 'true'." %>

<%@ attribute name="onClasses" description="CSS class names applied to the on button." %>
<%@ attribute name="offClasses" description="CSS class names applied to the off button." %>
<%@ attribute name="id" description="The html id attribute of the checkbox input." %>

<cti:default var="checked" value="${false}"/>
<cti:default var="disabled" value="${false}"/>
<cti:default var="inverse" value="${false}"/>
<cti:default var="color" value="${true}"/>
<cti:uniqueIdentifier prefix="switch-btn-" var="thisId"/>
<cti:default var="id" value="${thisId}"/>
<cti:default var="offNameKey" value=".off.label"/>
<cti:default var="onNameKey" value=".on.label"/>
<c:set var="offClasses" value="button left no ${pageScope.offClasses} ${!color ? 'no-color' : ''}"/>
<c:set var="onClasses" value="button right yes ${pageScope.onClasses} ${!color ? 'no-color' : ''}"/>

<cti:msgScope paths=",yukon.web.components.button">

<cti:displayForPageEditModes modes="EDIT,CREATE">
    <c:set var="clazz" value="${inverse ? 'inverse' : ''}"/>
    <label class="switch-btn ${clazz} ${pageScope.classes}">
        <c:choose>
            <c:when test="${not empty name}">
                <input type="checkbox" name="${name}" id="${id}" class="switch-btn-checkbox" 
                    data-toggle="${pageScope.toggleGroup}" 
                    <c:if test="${checked}">checked</c:if> 
                    <c:if test="${disabled}">disabled</c:if>>
            </c:when>
            <c:otherwise>
                <form:checkbox path="${path}" id="${id}" cssClass="switch-btn-checkbox" 
                    data-toggle="${pageScope.toggleGroup}" 
                    disabled="${disabled}"/>
            </c:otherwise>
        </c:choose>
        <label for="${id}" class="${offClasses}"><span class="b-label"><cti:msg2 key="${offNameKey}"/></span></label
        ><label for="${id}" class="${onClasses}"><span class="b-label"><cti:msg2 key="${onNameKey}"/></span></label>
    </label>
</cti:displayForPageEditModes>
<cti:displayForPageEditModes modes="VIEW">
    <c:if test="${empty name}">
        <spring:bind path="${path}">
            <c:set var="checked" value="${status.actualValue}" />
        </spring:bind>
    </c:if>
    <c:if test="${inverse}">
        <c:set var="checked" value="${not checked}"/>
    </c:if>
    <c:if test="${checked}">
        <span class="green"><cti:msg2 key="${onNameKey}.label"/></span>
    </c:if>
    <c:if test="${not checked}">
        <span class="red"><cti:msg2 key="${offNameKey}.label"/></span>
    </c:if>
</cti:displayForPageEditModes>

</cti:msgScope>