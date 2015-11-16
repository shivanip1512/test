<%@ attribute name="orderBy" required="true" type="com.cannontech.amr.meter.search.model.OrderBy"%>
<%@ attribute name="startIndex" required="true" type="java.lang.String"%>
<%@ attribute name="count" required="true" type="java.lang.String"%>
<%@ attribute name="selected" required="false" type="java.lang.Boolean"%>
<%@ attribute name="filterByList" required="false" type="java.util.List"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url  var="url" scope="page" value="/meter/search">
	<c:param name="startIndex" value="${startIndex}" />
	<c:param name="count" value="${count}" />
	<c:param name="orderBy" value="${orderBy.field}" />
	<c:if test="${orderBy.descending}">
		<c:param name="descending" value="true"/>
	</c:if>
	<c:forEach var="filter" items="${pageScope.filterByList}">
		<c:param name="${filter.name}" value="${filter.filterValue}" />
	</c:forEach>
</c:url>

<c:choose>
	<c:when test="${pageScope.selected}">
		<span class="navLink selected"><jsp:doBody/></span>
	</c:when>
	<c:otherwise>
		<span class="navLink">
			<a href="${url}"><jsp:doBody/></a>
		</span>
	</c:otherwise>
</c:choose>
