<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="op" tagdir="/WEB-INF/tags/operator" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="accountList">

	<cti:url var="accountEditUrl" value="/spring/stars/operator/general/account/accountEdit"/>
    				
	<script language="JavaScript">
	
		function forwardToAccountEdit(energyCompanyId, accountId) {
			$('accountTable').removeClassName('activeResultsTable');
			window.location = '${accountEditUrl}?energyCompanyId=' + energyCompanyId + '&accountId=' + accountId;
		}
		
	</script>
	
	<c:if test="${accountSearchResultHolder.hasError}">
		<div class="errorRed">${accountSearchResultHolder.error}</div>
		<br>
	</c:if>

	<%-- SEACRH WIDGET --%>
	<div style="width:95%;">
		<tags:widget bean="operatorAccountSearchWidget" searchValue="${accountSearchResultHolder.searchValue}" searchByDefinitionId="${accountSearchResultHolder.searchByDefinitionId}"/>
	</div>
	<br>

	<%-- RESULTS --%>
	<c:if test="${accountSearchResultHolder.accountSearchResults.hitCount > 0}">
	
		<%-- SEARCH NAVIGATION --%>
		<op:searchNavigation results="${accountSearchResultHolder.accountSearchResults}"
							searchByDefinitionId="${accountSearchResultHolder.searchByDefinitionId}"
							searchValue="${accountSearchResultHolder.searchValue}"/>
		
		<%-- RESULTS TABLE --%>
	    <table id="accountTable" class="resultsTable activeResultsTable">
	    
	    	<tr>
	    		<th><i:inline key=".accountNumberHeader"/></th>
	    		<th><i:inline key=".nameHeader"/></th>
	    		<th><i:inline key=".phoneNumberHeader"/></th>
	    		<th><i:inline key=".addressHeader"/></th>
	    	</tr>
	    	
	    	<c:forEach var="accountSearchResult" items="${accountSearchResultHolder.accountSearchResults.resultList}">
	    	
	    		<tr style="vertical-align:top;" 
	    			onclick="javascript:forwardToAccountEdit(${accountSearchResult.energyCompanyId}, ${accountSearchResult.accountId})" 
	    			onmouseover="activeResultsTable_highLightRow(this)" 
	                onmouseout="activeResultsTable_unHighLightRow(this)">
	    		
	    			<td>
	   					${accountSearchResult.accountNumber}
	    			</td>
	    			
	    			<td>
	    				${accountSearchResult.name}
	    			</td>
	    			
	    			<td>
	    				<tags:homeAndWorkPhone homePhoneNotif="${accountSearchResult.homePhoneNotif}" workPhoneNotif="${accountSearchResult.workPhoneNotif}"/>
	    			</td>
	    			
	    			<td>
	    				<tags:address address="${accountSearchResult.address}"/>
	    			</td>
	    		
	    		</tr>
	    	
	    	</c:forEach>
	    
	    </table>
	    
	    <%-- SEARCH NAVIGATION --%>
	    <op:searchNavigation results="${accountSearchResultHolder.accountSearchResults}"
							searchByDefinitionId="${accountSearchResultHolder.searchByDefinitionId}"
							searchValue="${accountSearchResultHolder.searchValue}"/>
	    
	</c:if>
    
</cti:standardPage>