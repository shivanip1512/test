<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<spring:bind path="${input.field}">

	<span <c:if test="${status.error}">style="color: red"</c:if>>
		${input.displayName} 
		<c:if test="${input.type.minValue != input.type.maxValue}">
			(${input.type.minValue} - ${input.type.maxValue}) 
		</c:if>
	</span>
	<input type="text" name="${status.expression}" value="${status.value}" />
</spring:bind>