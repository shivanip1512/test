<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<spring:bind path="${input.field}">
	<tags:inputName input="${input}" error="${status.error}" />
	<dt:date path="${status.expression}" value="${status.actualValue}" />
</spring:bind>