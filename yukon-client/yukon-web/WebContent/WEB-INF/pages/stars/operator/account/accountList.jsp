<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="accountList">

	<%-- SEACRH WIDGET --%>
	<form id="searchForm" action="/spring/stars/operator/account/search" method="get">
		
		<div style="padding-top:8px;padding-bottom:8px;">
			
			<select name="searchBy" onchange="$('accountSearchValue').value = ''">
				<c:forEach var="operatorAccountSearchBy" items="${operatorAccountSearchBys}" >
					<option value="${operatorAccountSearchBy}" <c:if test="${operatorAccountSearchBy == accountSearchResultHolder.searchBy}">selected</c:if>>
						<i:inline key="${operatorAccountSearchBy.formatKey}"/>
					</option>
				</c:forEach>
			</select>
			
			<input type="text" name="searchValue" id="accountSearchValue" value="${accountSearchResultHolder.searchValue}">
			
			<tags:slowInput2 formId="searchForm" key="search"/>
		
		</div>
		
	</form>
	<br>

	<%-- RESULTS --%>
	<c:if test="${accountSearchResultHolder.accountSearchResults.hitCount > 0}">
	
		<cti:msg2 var="resultBoxtitleText" key=".resultBoxtitle" arguments="${searchResultTitleArguments}"/>
		<tags:pagedBox searchResult="${accountSearchResultHolder.accountSearchResults}" baseUrl="/spring/stars/operator/account/search" title="${resultBoxtitleText}">
		
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
		    				<cti:url var="accountEditUrl" value="/spring/stars/operator/account/accountEdit">
		    					<cti:param name="accountId" value="${accountSearchResult.accountId}"/>
		    				</cti:url>
		    				<a href="${accountEditUrl}">
		   						${accountSearchResult.accountNumber}
		   					</a>
		    			</td>
		    			
		    			<td>
		    				${accountSearchResult.combinedName}
		    			</td>
		    			
		    			<td>
		    				<tags:homeAndWorkPhone homePhoneNotif="${accountSearchResult.homePhoneNotif}" workPhoneNotif="${accountSearchResult.workPhoneNotif}"/>
		    			</td>
		    			
		    			<td style="padding-bottom:8px;">
		    				<tags:address address="${accountSearchResult.address}"/>
		    			</td>
		    		
		    		</tr>
		    	
		    	</c:forEach>
	    	
	        </table>
        
		</tags:pagedBox>
	    
	</c:if>
    
</cti:standardPage>