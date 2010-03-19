<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag body-content="empty" %>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="disabled" required="false" type="java.lang.String"%>
<%@ attribute name="size" required="false" type="java.lang.String"%>
<%@ attribute name="maxlength" required="false" type="java.lang.String"%>
<%@ attribute name="id"%>
<%@ attribute name="onkeyup"%>
<%@ attribute name="onblur"%>

<spring:bind path="${path}">

<c:set var="inputClass" value=""/>
<c:if test="${status.error}">
	<c:set var="inputClass" value="error"/>
</c:if>
<c:if test="${empty pageScope.id}">
    <c:set var="id" value="${path}"/>
</c:if>

<form:input path="${pageScope.path}" id="${pageScope.id}" disabled="${pageScope.disabled}" size="${pageScope.size}" maxlength="${pageScope.maxlength}" cssClass="${pageScope.inputClass}" onkeyup="${pageScope.onkeyup}" onblur="${pageScope.onblur}"/>

<c:if test="${status.error}">
	<br>
	<form:errors path="${path}" cssClass="errorMessage"/>
</c:if>

</spring:bind>