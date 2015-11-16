<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="maxObjectCountBeforeScroll" value="30"/>

<c:if test="${objectCount > maxObjectCountBeforeScroll}">
	<div style="overflow:auto; height:500px;">
</c:if>

<table class="compact-results-table">

	<tr>
		<th>Object Identifier</th>
		<th>Warning Reason</th>
	</tr>

	<c:forEach var="warningList" items="${warningListMap}">
		<tr>
			<td>${warningList.key}</td>
			<td>
				<table>
					<c:forEach var="warning" items="${warningList.value}">
						<tr>
							<td>
								${warning}
							</td>
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
	</c:forEach>

</table>

<c:choose>
	<c:when test="${objectCount > maxObjectCountBeforeScroll}">
		<br>
		</div>
	</c:when>
	<c:otherwise>
		<br>
	</c:otherwise>
</c:choose>