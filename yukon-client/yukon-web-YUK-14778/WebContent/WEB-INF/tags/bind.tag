<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ tag description="wrap a spring form input tag (or select tag) in this to bind for error validation" %>
<%@ attribute name="path" required="true" rtexprvalue="true" %>

<spring:bind path="${path}">
	<c:set var="inputClass" value=""/>
	<c:if test="${status.error}">
		<c:set var="inputClass" value="error"/>
	</c:if>
    <jsp:doBody/>
	<c:if test="${status.error}">
		<br>
		<form:errors path="${path}" cssClass="error"/>
	</c:if>
</spring:bind>
