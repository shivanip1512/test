<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ tag body-content="empty" %>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="onclick" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="descriptionNameKey" required="false" type="java.lang.String"%>

<spring:bind path="${path}">

	<%-- VIEW MODE --%>
	<cti:displayForPageEditModes modes="VIEW">
		<c:choose>
			<c:when test="${status.value == true && not empty pageScope.id}">
				<input type="checkbox" checked="checked" disabled="disabled" id="${pageScope.id}" class="${styleClass}">
			</c:when>
			<c:when test="${status.value == true && empty pageScope.id}">
				<input type="checkbox" checked="checked" disabled="disabled" class="${styleClass}">
			</c:when>
			<c:when test="${status.value == false && not empty pageScope.id}">
				<input type="checkbox" disabled="disabled" id="${pageScope.id}" class="${styleClass}">
			</c:when>
			<c:when test="${status.value == false && empty pageScope.id}">
				<input type="checkbox" disabled="disabled" class="${styleClass}">
			</c:when>
			<c:otherwise>
				<%-- BAD STATE!? --%>
			</c:otherwise>
		</c:choose>
	</cti:displayForPageEditModes>
	
	<%-- EDIT/CREATE MODE --%>
	<cti:displayForPageEditModes modes="EDIT,CREATE">
		<c:choose>
			<c:when test="${not empty pageScope.onclick && not empty pageScope.id}">
				<form:checkbox path="${path}" onclick="${pageScope.onclick}" id="${pageScope.id}" cssClass="${styleClass}"/>
			</c:when>
			<c:when test="${not empty pageScope.onclick && empty pageScope.id}">
				<form:checkbox path="${path}" onclick="${pageScope.onclick}" cssClass="${styleClass}"/>
			</c:when>
			<c:when test="${empty pageScope.onclick && not empty pageScope.id}">
				<form:checkbox path="${path}" id="${pageScope.id}" cssClass="${styleClass}"/>
			</c:when>
			<c:when test="${empty pageScope.onclick && empty pageScope.id}">
				<form:checkbox path="${path}" cssClass="${styleClass}"/>
			</c:when>
			<c:otherwise>
				<%-- BAD STATE!? --%>
			</c:otherwise>
		</c:choose>
	</cti:displayForPageEditModes>
	
	<c:if test="${not empty descriptionNameKey}">
		<c:choose>
			<c:when test="${not empty pageScope.id}">
				<label for="${pageScope.id}"><i:inline key="${pageScope.descriptionNameKey}"/></label>
			</c:when>
			<c:otherwise>
				<i:inline key="${pageScope.descriptionNameKey}"/>
			</c:otherwise>
		</c:choose>
		
	</c:if>

</spring:bind>
