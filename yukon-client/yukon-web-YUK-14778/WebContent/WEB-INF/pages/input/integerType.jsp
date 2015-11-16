<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<spring:bind path="${input.field}">
	<ct:inputName input="${input}" error="${status.error}" />
	<input type="text" name="${status.expression}" value="${status.value}" />
</spring:bind>