<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="path" required="true" %>
<%@ attribute name="disabled" type="java.lang.Boolean" %>
<%@ attribute name="size" %>
<%@ attribute name="maxlength" %>
<%@ attribute name="autocomplete" 
        description="HTML input autocomplete attribute. Possible values: 'on|off'. Default: 'off'." %>
<%@ attribute name="cssClass" %>

<cti:default var="autocomplete" value="off"/>

<spring:bind path="${path}">

<%-- VIEW MODE --%>
<cti:displayForPageEditModes modes="VIEW">
&bull;&bull;&bull;&bull;&bull;&bull;&bull;&bull;&bull;&bull;
</cti:displayForPageEditModes>

<%-- EDIT/CREATE MODE --%>
<cti:displayForPageEditModes modes="EDIT,CREATE">

<c:set var="inputClass" value=""/>
<c:if test="${status.error}">
    <c:set var="inputClass" value="error"/>
</c:if>

<form:password path="${path}" disabled="${pageScope.disabled}" size="${pageScope.size}" maxlength="${pageScope.maxlength}" 
    autocomplete="${autocomplete}" cssClass="${inputClass} ${pageScope.cssClass}"/>
<c:if test="${status.error}"><br><form:errors path="${path}" cssClass="error"/></c:if>

</cti:displayForPageEditModes>

</spring:bind>