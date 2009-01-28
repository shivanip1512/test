<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<table class="resultsTable">
					    			
	<tr>
		<th>
			<tags:sortByLink ascending="${ascending}" 
							columnName="STRATEGY" 
							orderedColumnName="${orderedColumnName}" 
							callback="reloadAllMappingsTable('STRATEGY', true);">Strategy Name</tags:sortByLink>
		</th>
		<th>
			<tags:sortByLink ascending="${ascending}" 
							columnName="SUBSTATION" 
							orderedColumnName="${orderedColumnName}" 
							callback="reloadAllMappingsTable('SUBSTATION', true);">Substation Name</tags:sortByLink>
		</th>
		<th>
			<tags:sortByLink ascending="${ascending}" 
							columnName="PAO" 
							orderedColumnName="${orderedColumnName}" 
							callback="reloadAllMappingsTable('PAO', true);">Program/Scenario Name</tags:sortByLink>
		</th>
		<th>Remove Mapping</th>
	</tr>
	
	<c:forEach var="mapping" items="${allMappings}">
	
		<cti:paoName var="paoName" paoId="${mapping.paobjectId}"/>
	
		<tr>
			<td><spring:escapeBody htmlEscape="true">${mapping.strategyName}</spring:escapeBody></td>
			<td><spring:escapeBody htmlEscape="true">${mapping.substationName}</spring:escapeBody></td>
			<td><spring:escapeBody htmlEscape="true">${paoName}</spring:escapeBody></td>
			<td style="text-align:center;">
				<img class="cssicon" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>" onclick="removeMapping(${mapping.mspLMInterfaceMappingId});">
			</td>
		</tr>
	</c:forEach>
	
</table>