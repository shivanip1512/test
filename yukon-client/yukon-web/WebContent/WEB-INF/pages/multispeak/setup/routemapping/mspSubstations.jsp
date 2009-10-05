<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<script type="text" language="javascript">

	

</script>

<form action="/spring/multispeak/setup/routemapping/mspSubstations/add" method="post">

	<div style="text-align:right;">
	<a href="javascript:void(0);" onclick="mspSubstations_check(false);">Uncheck All</a> |
	<a href="javascript:void(0);" onclick="mspSubstations_check(true);">Check All</a>
	<br><br>
	</div>

	<div style="overflow:auto; height:300px;">
	<table class="compactResultsTable" style="width:95%;">
	
		<tr>
			<th>Substation Name</th>
		</tr>
		
		<c:forEach var="mspSubstation" items="${mspSubstations}">
		
			<c:set var="show" value="${mspSubstation.show}"/>
			<c:set var="trClass" value=""/>
			<c:if test="${!show}">
				<c:set var="trClass" value="class='subtleGray'"/>
			</c:if>
		
			<tr ${trClass}>
				<td>
					<c:choose>
						<c:when test="${show}">
							<label><input class="mspSubstationCheckbox" type="checkbox" checked name="substationName_${mspSubstation.name}" value="${mspSubstation.name}"> ${fn:escapeXml(mspSubstation.name)}</label>
						</c:when>
						<c:otherwise>
							<input class="mspSubstationCheckbox" type="checkbox" disabled> ${fn:escapeXml(mspSubstation.name)}
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
	
	</table>
	</div>
	
	<br>
	<input type="submit" value="Add">

</form>