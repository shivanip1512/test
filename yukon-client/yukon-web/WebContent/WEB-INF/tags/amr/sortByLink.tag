<%@ attribute name="field" required="true" type="com.cannontech.amr.meter.search.model.MeterSearchField"%>
<%@ attribute name="results" required="true" type="com.cannontech.common.search.SearchResult"%>
<%@ attribute name="orderBy" required="true" type="com.cannontech.amr.meter.search.model.OrderBy"%>
<%@ attribute name="filterByList" required="false" type="java.util.List"%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url  var="url" scope="page" value="/meter/search">
	<c:param name="startIndex" value="${results.startIndex}" />
	<c:param name="count" value="${results.count}" />
	<c:param name="orderBy" value="${field.name}" />
	<c:if test="${orderBy.field == field.name && !orderBy.descending}">
		<c:param name="descending" value="true"/>
	</c:if>
	<c:forEach var="filter" items="${pageScope.filterByList}">
		<c:param name="${filter.name}" value="${filter.filterValue}" />
	</c:forEach>
</c:url>

<c:choose>
	<c:when test="${orderBy.field == field.name}">
		<a href="${url}"><jsp:doBody/></a>
		<c:choose>
			<c:when test="${!orderBy.descending}">
				<span title="Sorted ascending">&#9650;</span>
			</c:when>
			<c:otherwise>
				<span title="Sorted descending">&#9660;</span>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
			<a href="${url}"><jsp:doBody/></a>
	</c:otherwise>
</c:choose>
