<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>


<spring:bind path="${input.field}">
	<span <c:if test="${status.error}">style="color: red"</c:if>>
		${input.displayName} 
	</span>
		
	<select name="${status.expression}">
		<c:forEach var="option" items="${input.type.optionList}">
			<option value="${option}" <c:if test="${status.value == option}">selected</c:if>>${option}</option>
		</c:forEach>
	</select>
</spring:bind>