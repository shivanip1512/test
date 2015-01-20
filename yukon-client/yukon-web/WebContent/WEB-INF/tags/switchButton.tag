<%@ tag body-content="empty" description="A component that looks like a toggle switch of two buttons and
                                          drives an html checkbox." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="name" description="The name of the checkbox input." %>
<%@ attribute name="path" description="Spring path, used instead of 'name'." %>
<%@ attribute name="checked" type="java.lang.Boolean" description="If 'true' switch will be on.  Default: false." %>
<%@ attribute name="disabled" type="java.lang.Boolean" description="If 'true' switch will be disabled.  Default: false." %>
<%@ attribute name="inverse" type="java.lang.Boolean" description="If true, when the checkbox is checked, the red button 
                                                                   will be toggled on instead of the green button." %>
<%@ attribute name="toggleGroup" description="Used to setup a toggle group driven by a checkbox." %>

<%@ attribute name="onNameKey" description="i18n key used for the green button." %> 
<%@ attribute name="offNameKey" description="i18n key used for the green button." %> 

<%@ attribute name="classes" description="CSS class names applied to the outer label element." %>
<%@ attribute name="onClasses" description="CSS class names applied to the on button." %>
<%@ attribute name="offClasses" description="CSS class names applied to the off button." %>
<%@ attribute name="id" description="The html id attribute of the checkbox input." %>

<cti:default var="checked" value="${false}"/>
<cti:default var="disabled" value="${false}"/>
<cti:default var="inverse" value="${false}"/>
<cti:uniqueIdentifier prefix="switch-btn-" var="thisId"/>
<cti:default var="id" value="${thisId}"/>
<cti:default var="onNameKey" value="on"/>
<cti:default var="offNameKey" value="off"/>

<c:set var="clazz" value="${inverse ? 'inverse' : ''}"/>
<label class="switch-btn ${clazz}" <c:if test="${not empty pageScope.classes}">classes="${classes}"</c:if>>
    <c:choose>
        <c:when test="${not empty name}">
            <input type="checkbox" name="${name}" id="${id}" class="switch-btn-checkbox" data-toggle="${pageScope.toggleGroup}" 
                <c:if test="${checked}">checked</c:if> 
                <c:if test="${disabled}">disabled</c:if>>
        </c:when>
        <c:otherwise>
            <form:checkbox path="${path}" id="${id}" cssClass="switch-btn-checkbox" data-toggle="${pageScope.toggleGroup}" 
                disabled="${disabled}"/>
        </c:otherwise>
    </c:choose>
    <cti:msgScope paths="yukon.web.components.button"> <%--Do not format, spaces will be added by browser. --%>
        <label for="${id}" class="button left yes ${pageScope.onClasses}"><span class="b-label"><cti:msg2 key=".${onNameKey}.label"/></span></label
        ><label for="${id}" class="button right no ${pageScope.offClasses}"><span class="b-label"><cti:msg2 key=".${offNameKey}.label"/></span></label>
    </cti:msgScope>
</label>