<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ tag body-content="empty" %>
<%@ attribute name="path" required="true" type="java.lang.String" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" type="java.lang.String"%>
<%@ attribute name="readonly" required="false" type="java.lang.String"%>
<%@ attribute name="size" required="false" type="java.lang.String"%>
<%@ attribute name="maxlength" required="false" type="java.lang.String"%>
<%@ attribute name="autocomplete" required="false" type="java.lang.Boolean"%>
<%@ attribute name="inputClass" required="false" type="java.lang.String"%>
<%@ attribute name="id"%>
<%@ attribute name="onkeyup"%>
<%@ attribute name="onchange"%>
<%@ attribute name="onblur"%>

<spring:bind path="${path}">

<%-- VIEW MODE --%>
<cti:displayForPageEditModes modes="VIEW">
${status.value}
</cti:displayForPageEditModes>

<%-- EDIT/CREATE MODE --%>
<cti:displayForPageEditModes modes="EDIT,CREATE">

<c:if test="${status.error}">
	<c:set var="inputClass" value="error ${pageScope.inputClass}"/>
</c:if>
<c:if test="${empty pageScope.id}">
    <c:set var="id" value="${path}"/>
</c:if>

<form:input path="${pageScope.path}" id="${pageScope.id}" disabled="${pageScope.disabled}" readonly="${pageScope.readonly}" size="${pageScope.size}" maxlength="${pageScope.maxlength}" autocomplete="${pageScope.autocomplete}" cssClass="${pageScope.inputClass}" onkeyup="${pageScope.onkeyup}" onchange="${pageScope.onchange}" onblur="${pageScope.onblur}"/>

<c:if test="${status.error}">
	<br>
	<form:errors path="${path}" cssClass="errorMessage"/>
</c:if>

</cti:displayForPageEditModes>

</spring:bind>