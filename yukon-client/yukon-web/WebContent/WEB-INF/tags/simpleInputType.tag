<%@ attribute name="input" required="true" type="com.cannontech.web.input.type.InputType"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="inputType" value="${input}" scope="request"/>
<spring:bind path="${path}" htmlEscape="true">
<jsp:include flush="false" page="/WEB-INF/pages/simpleInput/${input.renderer}"><jsp:param name="id" value="${pageScope.id}"/><jsp:param name="inputType" value="${input}"/></jsp:include>
</spring:bind>