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
		<th>Error Reason</th>
	</tr>

	<c:forEach var="errorList" items="${errorListMap}">
		<tr>
			<td>${errorList.key}</td>
			<td>
				<table>
					<c:forEach var="error" items="${errorList.value}">
						<tr>
							<td>
								${error}
							</td>
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
	</c:forEach>

</table>	

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