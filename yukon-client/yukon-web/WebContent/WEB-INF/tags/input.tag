<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="autocomplete" type="java.lang.Boolean"%>
<%@ attribute name="disabled" %>
<%@ attribute name="id" %>
<%@ attribute name="inputClass" %>
<%@ attribute name="onkeyup" %>
<%@ attribute name="onchange" %>
<%@ attribute name="onblur" %>
<%@ attribute name="maxlength" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="readonly" %>
<%@ attribute name="size" %>
<%@ attribute name="tabindex" %>
<%@ attribute name="password" type="java.lang.Boolean" description="If true, a password field is used. Default: false." %>

<cti:default var="password" value="${false}"/>

<spring:bind path="${path}">

<%-- VIEW MODE --%>
<cti:displayForPageEditModes modes="VIEW">${fn:escapeXml(status.value)}</cti:displayForPageEditModes>

<%-- EDIT/CREATE MODE --%>
<cti:displayForPageEditModes modes="EDIT,CREATE">

<c:if test="${status.error}">
    <c:set var="inputClass" value="error ${pageScope.inputClass}"/>
</c:if>

<c:choose>
    <c:when test="${password}">
        <form:password path="${path}" id="${pageScope.id}" disabled="${pageScope.disabled}" 
            readonly="${pageScope.readonly}" size="${pageScope.size}" 
            maxlength="${pageScope.maxlength}" autocomplete="${pageScope.autocomplete}" 
            cssClass="${pageScope.inputClass}" onkeyup="${pageScope.onkeyup}" 
            onchange="${pageScope.onchange}" onblur="${pageScope.onblur}"
            tabindex="${pageScope.tabindex}"/>
    </c:when>
    <c:otherwise>
        <form:input path="${path}" id="${pageScope.id}" disabled="${pageScope.disabled}" 
            readonly="${pageScope.readonly}" size="${pageScope.size}" 
            maxlength="${pageScope.maxlength}" autocomplete="${pageScope.autocomplete}" 
            cssClass="${pageScope.inputClass}" onkeyup="${pageScope.onkeyup}" 
            onchange="${pageScope.onchange}" onblur="${pageScope.onblur}"
            tabindex="${pageScope.tabindex}"/>
    </c:otherwise>
</c:choose>

<c:if test="${status.error}"><br><form:errors path="${path}" cssClass="error"/></c:if>

</cti:displayForPageEditModes>

</spring:bind>