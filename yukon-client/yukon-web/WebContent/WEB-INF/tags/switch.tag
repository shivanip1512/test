<%@ tag body-content="empty" trimDirectiveWhitespaces="true" description="A component that looks like a toggle switch with an html checkbox." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="name" description="The name of the checkbox input." %>
<%@ attribute name="path" description="Spring path, used instead of 'name'." %>
<%@ attribute name="checked" type="java.lang.Boolean" description="If 'true' switch will be on.  Default: false." %>

<%@ attribute name="key" description="i18n key used for accessability text." %> 

<%@ attribute name="classes" description="CSS class names applied to the outer label element." %>
<%@ attribute name="id" description="The html id attribute of the checkbox input." %>

<cti:default var="checked" value="${false}"/>
<cti:uniqueIdentifier prefix="checkbox-" var="thisId"/>
<cti:default var="id" value="${thisId}"/>

<label <c:if test="${not empty pageScope.classes}">classes="${classes}"</c:if>>
    <c:if test="${not empty pageScope.key}"><cti:msg2 key="${key}"/></c:if>
    <span class="checkbox">
        <c:choose>
            <c:when test="${not empty name}">
                <input type="checkbox" name="${name}" id="${id}" <c:if test="${checked}">checked</c:if>>
            </c:when>
            <c:otherwise>
                <form:checkbox path="${path}" id="${id}"/>
            </c:otherwise>
        </c:choose>
        <span class="checkbox-value" aria-hidden="true"></span>
    </span>
</label>