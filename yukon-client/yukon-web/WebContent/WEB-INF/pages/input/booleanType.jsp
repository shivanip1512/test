<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<spring:bind path="${input.field}">

	<span <c:if test="${status.error}">style="color: red"</c:if>>
		${input.displayName} 
	</span>
	<input type="checkbox" name="${status.expression}" value="true" <c:if test="${status.value}">checked</c:if>>
</spring:bind>