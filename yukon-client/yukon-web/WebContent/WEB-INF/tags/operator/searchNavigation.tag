<%@ attribute name="results" required="true" type="com.cannontech.common.search.SearchResult"%>
<%@ attribute name="searchByDefinitionId" required="false" type="java.lang.Integer"%>
<%@ attribute name="searchValue" required="false" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="op" tagdir="/WEB-INF/tags/operator" %>

<cti:url var="searchUrl" value="/spring/stars/operator/general/account/search">
	<cti:param name="count" value="${pageScope.count}" />
	<cti:param name="searchByDefinitionId" value="${pageScope.searchByDefinitionId}" />
	<cti:param name="searchValue" value="${pageScope.searchValue}" />
</cti:url>

<script language="JavaScript">

	function gotoPage(select) {
		window.location = '${searchUrl}&startIndex=' + $F(select);
	}

</script>

<table width="95%">
	<tr>
		<td>
			${results.startIndex + 1}-${results.endIndex} of ${results.hitCount} 
			<c:choose>
				<c:when test="${results.previousNeeded}">
					| 
			    	<op:searchNavigationLink 
						startIndex="0" 
						count="${results.count}"
						searchByDefinitionId="${pageScope.searchByDefinitionId}"
						searchValue="${pageScope.searchValue}"
						selected="false">
							First
					</op:searchNavigationLink>						    	  
					| 
			    	<op:searchNavigationLink 
						startIndex="${results.previousStartIndex}" 
						count="${results.count}"
						searchByDefinitionId="${pageScope.searchByDefinitionId}"
						searchValue="${pageScope.searchValue}"
						selected="false">
							Previous
					</op:searchNavigationLink>						    	  
				</c:when>
				<c:otherwise>
					| First
					| Previous
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${results.nextNeeded}">
					| 
			    	<op:searchNavigationLink 
						startIndex="${results.endIndex}" 
						count="${results.count}"
						searchByDefinitionId="${pageScope.searchByDefinitionId}"
						searchValue="${pageScope.searchValue}"
						selected="false">
							Next
					</op:searchNavigationLink>
					| 
			    	<op:searchNavigationLink 
						startIndex="${results.lastStartIndex}" 
						count="${results.count}"
						searchByDefinitionId="${pageScope.searchByDefinitionId}"
						searchValue="${pageScope.searchValue}"
						selected="false">
							Last
					</op:searchNavigationLink>
				</c:when>
				<c:otherwise>
					| Next
					| Last
				</c:otherwise>
			</c:choose>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			Goto page 
			<select style="font-size: .8em;" id="page" onchange="gotoPage(this)">
				<c:forEach var="page" begin="1" end="${results.numberOfPages + 1}" step="${1}">
					<c:choose>
						<c:when test="${((page - 1) * results.count) == results.startIndex}">
							<option value="${(page - 1) * results.count}" selected="selected">${page}</option>				
						</c:when>
						<c:otherwise>
							<option value="${(page - 1) * results.count}">${page}</option>				
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
        
		<td align="right">
			Results per page:
			<op:searchNavigationLink 
				startIndex="0" 
				count="10"
				searchByDefinitionId="${pageScope.searchByDefinitionId}"
				searchValue="${pageScope.searchValue}"
				selected="${results.count == 10}">
					10
			</op:searchNavigationLink>
			| 
			<op:searchNavigationLink 
				startIndex="0" 
				count="25"
				searchByDefinitionId="${pageScope.searchByDefinitionId}"
				searchValue="${pageScope.searchValue}"
				selected="${results.count == 25}">
					25
			</op:searchNavigationLink>
			| 
			<op:searchNavigationLink 
				startIndex="0" 
				count="50"
				searchByDefinitionId="${pageScope.searchByDefinitionId}"
				searchValue="${pageScope.searchValue}"
				selected="${results.count == 50}">
					50
			</op:searchNavigationLink>
		</td>
	</tr>
</table>