<%@ tag body-content="empty" trimDirectiveWhitespaces="true" description="A component that looks like a toggle switch of two buttons, drives an html checkbox." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="name" description="The name of the checkbox input." %>
<%@ attribute name="path" description="Spring path, used instead of 'name'." %>
<%@ attribute name="checked" type="java.lang.Boolean" description="If 'true' switch will be on.  Default: false." %>
<%@ attribute name="inverse" type="java.lang.Boolean" description="If true, when the checkbox is checked, the red button 
                                                                   will be toggled on instead of the green button." %>

<%@ attribute name="greenNameKey" required="true" description="i18n key used for the green button." %> 
<%@ attribute name="redNameKey" required="true" description="i18n key used for the green button." %> 

<%@ attribute name="classes" description="CSS class names applied to the outer label element." %>
<%@ attribute name="id" description="The html id attribute of the checkbox input." %>

<cti:default var="checked" value="${false}"/>
<cti:default var="inverse" value="${false}"/>
<cti:uniqueIdentifier prefix="switch-btn-" var="thisId"/>
<cti:default var="id" value="${thisId}"/>

<c:set var="clazz" value="${inverse ? 'inverse' : ''}"/>
<label class="switch-btn ${clazz}" <c:if test="${not empty pageScope.classes}">classes="${classes}"</c:if>>
    <c:choose>
        <c:when test="${not empty name}">
            <input type="checkbox" name="${name}" id="${id}" class="switch-btn-checkbox" <c:if test="${checked}">checked</c:if>>
        </c:when>
        <c:otherwise>
            <form:checkbox path="${path}" id="${id}" cssClass="switch-btn-checkbox"/>
        </c:otherwise>
    </c:choose>
    <cti:msgScope paths="yukon.web.components.button">
        <label for="${id}" class="button left yes"><span class="b-label"><cti:msg2 key=".${greenNameKey}.label"/></span></label>
        <label for="${id}" class="button right no"><span class="b-label"><cti:msg2 key=".${redNameKey}.label"/></span></label>
    </cti:msgScope>
</label>