<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:standardPage module="operator" page="selectTrends">

	<cti:includeCss link="/WebConfig/yukon/styles/operator/metering.css"/>

	<tags:sectionContainer2 key="availableTrends">
	
		<form id="selectTrendsForm" action="/spring/stars/operator/metering/saveSelectedTrends">
		
			<input type="hidden" name="accountId" value="${accountId}">
	
			<c:forEach var="trend" items="${availablTrendWrappers}">
			
				<div class="availableTrendOption">
					<label>
						<input type="checkbox" name="graphDefinitionId" value="${trend.graphDefinitionId}" <c:if test="${trend.selected}">checked</c:if>>
						<span class="radioLabel">
							<spring:escapeBody htmlEscape="true">${trend.name}</spring:escapeBody>
						</span>
					</label>
				</div>
			</c:forEach>
			
			<br>
			<tags:slowInput2 myFormId="selectTrendsForm" key="save" width="80px"/>
			<tags:reset/>
		
		</form>
	
	</tags:sectionContainer2>

</cti:standardPage>