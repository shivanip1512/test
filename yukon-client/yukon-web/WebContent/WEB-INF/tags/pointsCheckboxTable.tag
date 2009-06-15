<%@ attribute name="deviceType" required="true" type="java.lang.Integer"%>
<%@ attribute name="pointTemplates" required="true" type="java.util.List"%>
<%@ attribute name="columnCount" required="true" type="java.lang.Integer"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="columnPercentage" value="${100 / columnCount}"/>
			
<table class="compactResultsTable">

	<c:forEach var="ptWrapper" items="${pointTemplates}" varStatus="status">
	
		<%-- close previous tr and/or start new tr --%>
		<c:choose>
			<c:when test="${status.first}">
				<tr>
			</c:when>
			<c:when test="${status.index % columnCount == 0}">
				</tr>
				<tr>
			</c:when>
		</c:choose>
		
		<td style="width:${columnPercentage}%;">
		
			<c:set var="disabled" value="${ptWrapper.masked ? 'disabled' : ''}"/>
			<c:set var="className" value="${ptWrapper.masked ? 'subtleGray' : ''}"/>
		
			<label>
			<input type="checkbox" ${disabled} name="PT:${deviceType}:${ptWrapper.pointTemplate.pointIdentifier.type}:${ptWrapper.pointTemplate.pointIdentifier.offset}">
				<span class="${className}">${ptWrapper.pointTemplate.name} [#${ptWrapper.pointTemplate.pointIdentifier.offset}]</span>
			</input>
			</label>
			
		</td>
		
		<%-- fill in remaining columns with place holders --%>
		<c:if test="${status.last}">
		
			<c:set var="remainingColumsCount" value="${(columnCount - (status.count % columnCount)) % columnCount}"/>      <%-- optional equation <c:set var="remainingColumsCount" value="${status.count % columnCount != 0 ? (columnCount - (status.count % columnCount)) : 0}"/> --%>
			
			<c:forEach begin="1" end="${remainingColumsCount}">
				<td style="width:${columnPercentage}%;">&nbsp;</td>
			</c:forEach>
		
			</tr>
		</c:if>
	
	</c:forEach>
	
</table>
<br>
