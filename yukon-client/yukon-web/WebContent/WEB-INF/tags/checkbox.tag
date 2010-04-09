<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="onclick" required="false" type="java.lang.String"%>

<spring:bind path="${path}">

	<%-- VIEW MODE --%>
	<cti:displayForPageEditModes modes="VIEW">
		<c:if test="${status.value == true}">
			<i:inline key="defaults.yes"/>
		</c:if>
		<c:if test="${status.value == false}">
			<i:inline key="defaults.no"/>
		</c:if>
	</cti:displayForPageEditModes>
	
	<%-- EDIT/CREATE MODE --%>
	<cti:displayForPageEditModes modes="EDIT,CREATE">
		<form:checkbox path="${path}" onclick="${pageScope.onclick}"/>
	</cti:displayForPageEditModes>

</spring:bind>
