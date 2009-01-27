<%@ attribute name="columnName" required="true" type="java.lang.String"%>
<%@ attribute name="orderedColumnName" required="true" type="java.lang.String"%>
<%@ attribute name="ascending" required="true" type="java.lang.Boolean"%>
<%@ attribute name="callback" required="true" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>

	<c:when test="${columnName == orderedColumnName}">
	
		<a href="javascript:void(0);" onclick="${callback}">
			<jsp:doBody/>
			<c:choose>
				<c:when test="${ascending}">
					<span title="Sorted descending">&#9650;</span>
				</c:when>
				<c:otherwise>
					<span title="Sorted ascending">&#9660;</span>
				</c:otherwise>
			</c:choose>
		</a>
		
	</c:when>
	
	<c:otherwise>
		<a href="javascript:void(0);" onclick="${callback}"><jsp:doBody/></a>
	</c:otherwise>

</c:choose>

