<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag body-content="empty" %>
<%@ attribute name="path" required="true" type="java.lang.String" rtexprvalue="true"%>
<%@ attribute name="id"%>

<spring:bind path="${path}">

<c:set var="inputClass" value=""/>
<c:if test="${status.error}">
    <c:set var="inputClass" value="error"/>
</c:if>
<c:if test="${empty pageScope.id}">
    <c:set var="id" value="${path}"/>
</c:if>

<form:hidden path="${pageScope.path}" id="${pageScope.id}"/>

<c:if test="${status.error}">
    <br>
    <form:errors path="${path}" cssClass="error"/>
</c:if>

</spring:bind>