<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="startIndex" required="true" type="java.lang.String"%>
<%@ attribute name="count" required="true" type="java.lang.String"%>
<%@ attribute name="orderByField" required="true" type="java.lang.String"%>
<%@ attribute name="orderByDesc" required="true" type="java.lang.Boolean"%>

<%@ attribute name="selected" required="false" type="java.lang.Boolean"%>
<%@ attribute name="arrow" required="false" type="java.lang.Boolean"%>

<%@ attribute name="filterByList" required="false" type="java.util.List"%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url  var="url" scope="page" value="/spring/csr/search">
	<c:param name="startIndex" value="${startIndex}" />
	<c:param name="count" value="${count}" />
	<c:param name="orderBy" value="${fieldName}" />
	<c:if test="${orderByField == fieldName && !orderByDesc}">
		<c:param name="descending" value="true"/>
	</c:if>
	<c:forEach var="filter" items="${filterByList}">
		<c:param name="${filter.field}" value="${filter.filterValue}" />
	</c:forEach>
</c:url>

<c:choose>
	<c:when test="${selected}">
		<a href="${url}"><jsp:doBody/></a>
		<c:if test="${arrow}">
			<c:choose>
				<c:when test="${orderByField == fieldName && !orderByDesc}">
					<span title="Sorted ascending">&#9650;</span>
				</c:when>
				<c:otherwise>
					<span title="Sorted descending">&#9660;</span>
				</c:otherwise>
			</c:choose>
		</c:if>
	</c:when>
	<c:otherwise>
		<a href="${url}"><jsp:doBody/></a>
	</c:otherwise>
</c:choose>
