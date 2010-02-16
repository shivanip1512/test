<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="startIndex" required="true" type="java.lang.String"%>
<%@ attribute name="count" required="true" type="java.lang.String"%>
<%@ attribute name="searchByDefinitionId" required="false" type="java.lang.Integer"%>
<%@ attribute name="searchValue" required="false" type="java.lang.String"%>
<%@ attribute name="selected" required="false" type="java.lang.Boolean"%>

<cti:url var="searchUrl" scope="page" value="/spring/stars/operator/general/account/search">
	<cti:param name="startIndex" value="${pageScope.startIndex}" />
	<cti:param name="count" value="${pageScope.count}" />
	<cti:param name="searchByDefinitionId" value="${pageScope.searchByDefinitionId}" />
	<cti:param name="searchValue" value="${pageScope.searchValue}" />
</cti:url>

<c:choose>
	<c:when test="${pageScope.selected}">
		<span class="navLink selected"><jsp:doBody/></span>
	</c:when>
	<c:otherwise>
		<span class="navLink">
			<a href="${pageScope.searchUrl}"><jsp:doBody/></a>
		</span>
	</c:otherwise>
</c:choose>
