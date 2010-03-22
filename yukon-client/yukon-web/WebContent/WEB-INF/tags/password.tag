<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="size" required="false" type="java.lang.String"%>
<%@ attribute name="maxlength" required="false" type="java.lang.String"%>
<%@ attribute name="autocomplete" required="false" type="java.lang.Boolean"%>

<spring:bind path="${path}">

<c:set var="inputClass" value=""/>
<c:if test="${status.error}">
	<c:set var="inputClass" value="error"/>
</c:if>

<form:password path="${path}" id="${path}" disabled="${pageScope.disabled}" size="${pageScope.size}" maxlength="${pageScope.maxlength}" autocomplete="${pageScope.autocomplete}" cssClass="${inputClass}" />

<c:if test="${status.error}">
	<br>
	<form:errors path="${path}" cssClass="errorRed"/>
</c:if>

</spring:bind>