<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="op" tagdir="/WEB-INF/tags/operator" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="accountList">

	<c:if test="${accountSearchResultHolder.hasError}">
		<div class="errorRed">${accountSearchResultHolder.error}</div>
		<br>
	</c:if>

	<%-- SEACRH WIDGET --%>
	<div style="width:100%;">
		<tags:widget bean="operatorAccountSearchWidget" searchValue="${accountSearchResultHolder.searchValue}" searchBy="${accountSearchResultHolder.searchBy}" hideEnabled="false"/>
	</div>
	<br>

	<%-- RESULTS --%>
	<c:if test="${accountSearchResultHolder.accountSearchResults.hitCount > 0}">
	
		<cti:msg var="searchByText" key="${accountSearchResultHolder.searchBy.formatKey}"/>
		<cti:msg2 var="resultBoxtitleText" key=".resultBoxtitle" arguments="${searchByText},${accountSearchResultHolder.searchValue}"/>
		<tags:pagedBox searchResult="${accountSearchResultHolder.accountSearchResults}" baseUrl="/spring/stars/operator/general/account/search" title="${resultBoxtitleText}">
		
			<table class="compactResultsTable rowHighlighting" style="width:100%;">
	            
	            <tr>
		    		<th><i:inline key=".accountNumberHeader"/></th>
		    		<th><i:inline key=".nameHeader"/></th>
		    		<th><i:inline key=".phoneNumberHeader"/></th>
		    		<th><i:inline key=".addressHeader"/></th>
		    	</tr>
		    	
		    	<c:forEach var="accountSearchResult" items="${accountSearchResultHolder.accountSearchResults.resultList}">
	    	
	    			<tr style="vertical-align:top;" class="<tags:alternateRow odd="" even="altRow"/>">
		    		
		    			<td>
		    				<cti:url var="accountEditUrl" value="/spring/stars/operator/general/account/accountEdit">
		    					<cti:param name="energyCompanyId" value="${accountSearchResult.energyCompanyId}"/>
		    					<cti:param name="accountId" value="${accountSearchResult.accountId}"/>
		    				</cti:url>
		    				<a href="${accountEditUrl}">
		   						${accountSearchResult.accountNumber}
		   					</a>
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
        
		</tags:pagedBox>
	    
	</c:if>
    
</cti:standardPage>