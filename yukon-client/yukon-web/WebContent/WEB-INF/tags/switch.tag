<%@ tag body-content="empty" trimDirectiveWhitespaces="true" description="A component that looks like a toggle switch with an html checkbox." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="name" required="true" description="The name of the checkbox input." %>
<%@ attribute name="checked" type="java.lang.Boolean" description="If 'true' switch will be on.  Default: false." %>

<%@ attribute name="key" description="i18n key used for accessability text." %> 

<%@ attribute name="classes" description="CSS class names applied to the outer label element." %>
<%@ attribute name="id" description="The html id attribute of the checkbox input." %>

<cti:default var="checked" value="${false}"/>

<label <c:if test="${not empty pageScope.classes}">classes="${classes}"</c:if>>
    <c:if test="${not empty pageScope.key}"><cti:msg2 key="${key}"/></c:if>
    <span class="checkbox">
        <input type="checkbox" name="${name}" 
                <c:if test="${not empty pageScope.id}">id="${id}"</c:if>
                <c:if test="${checked}">checked</c:if>>
        <span class="checkbox-value" aria-hidden="true"></span>
    </span>
</label>