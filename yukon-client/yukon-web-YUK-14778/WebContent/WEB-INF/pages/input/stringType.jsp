<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<spring:bind path="${input.field}">
	<ct:inputName input="${input}" error="${status.error}" />
	<input type="text" maxlength="60" size="30" id="${status.expression}" name="${status.expression}" value="${fn:escapeXml(status.value)}" />
</spring:bind>