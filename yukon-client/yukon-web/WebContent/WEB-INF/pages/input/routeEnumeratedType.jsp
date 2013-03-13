<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<spring:bind path="${input.field}">
	<ct:inputName input="${input}" error="${status.error}" />
		
	<select name="${status.expression}">
		<c:forEach var="option" items="${input.type.optionList}">
			<option value="${option.value}" <c:if test="${status.value == option.value}">selected</c:if>><cti:msg2 key="${option}"/></option>
		</c:forEach>
	</select>
</spring:bind>