<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<form id="searchForm" action="/spring/stars/operator/general/account/search" method="get">
		
	<div style="padding-top:8px;padding-bottom:8px;">
		
		<select name="searchByDefinitionId" onchange="$('searchForm').value = ''">
			<c:forEach var="operatorAccountSearchBy" items="${operatorAccountSearchBys}" >
				<option value="${operatorAccountSearchBy.definitionId}" <c:if test="${operatorAccountSearchBy.definitionId == searchByDefinitionId}">selected</c:if>>
					<i:inline key="${operatorAccountSearchBy.formatKey}"/>
				</option>
			</c:forEach>
		</select>
		
		<input type="text" name="searchValue" value="${searchValue}">
		
		<i:slowInput myFormId="searchForm" labelKey="defaults.search"/>
	
	</div>
	
	<!-- 
	<tags:nameValueContainer>
	
		<i:nameValue nameKey=".searchTypeLabel" nameColumnWidth="60px">
			<select name="searchByDefinitionId" onchange="$('searchForm').value = ''">
				<c:forEach var="operatorAccountSearchBy" items="${operatorAccountSearchBys}" >
					<option value="${operatorAccountSearchBy.definitionId}" <c:if test="${operatorAccountSearchBy.definitionId == searchByDefinitionId}">selected</c:if>>
						<i:inline key="${operatorAccountSearchBy.formatKey}"/>
					</option>
				</c:forEach>
			</select>
		</i:nameValue>
		
		<i:nameValue nameKey=".searchValueLabel">
			<input type="text" name="searchValue" value="${searchValue}">
		</i:nameValue>
	
	</tags:nameValueContainer>
	 
	 <div style="text-align:right;padding-top:5px;">
		<i:slowInput myFormId="searchForm" labelKey="defaults.search"/>
	</div>
	 -->
</form>

