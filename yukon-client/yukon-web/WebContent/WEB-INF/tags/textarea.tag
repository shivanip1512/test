<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="rows" required="true" type="java.lang.Integer"%>
<%@ attribute name="cols" required="true" type="java.lang.Integer"%>

<spring:bind path="${path}">


<%-- VIEW MODE --%>
<cti:displayForPageEditModes modes="VIEW">
	<spring:escapeBody htmlEscape="true">${status.value}</spring:escapeBody>
</cti:displayForPageEditModes>

<%-- EDIT/CREATE MODE --%>
<cti:displayForPageEditModes modes="EDIT,CREATE">

	<c:set var="inputClass" value=""/>
	<c:if test="${status.error}">
		<c:set var="inputClass" value="error"/>
	</c:if>
	
		<form:textarea path="${path }" rows="${rows}" cols="${cols}" cssClass="${inputClass}"/>
	
	<c:if test="${status.error}">
		<br>
		<form:errors path="${path}" cssClass="errorRed"/>
	</c:if>
	
</cti:displayForPageEditModes>

</spring:bind>