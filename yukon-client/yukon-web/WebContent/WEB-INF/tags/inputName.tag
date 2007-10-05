<%@ attribute name="input" required="true" type="com.cannontech.web.input.Input"%>
<%@ attribute name="error" required="true" type="java.lang.Boolean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<span <c:if test="${error}">style="color: red"</c:if> title="${input.description}">
	${input.displayName} 
	<c:forEach var="validator" items="${input.validatorList}" >
		<i>${validator.description}</i>
	</c:forEach>
</span>
