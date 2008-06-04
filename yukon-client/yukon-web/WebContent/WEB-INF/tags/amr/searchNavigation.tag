<%@ attribute name="orderBy" required="true" type="com.cannontech.amr.csr.model.OrderBy"%>
<%@ attribute name="results" required="true" type="com.cannontech.common.search.SearchResult"%>
<%@ attribute name="filterByList" required="false" type="java.util.List"%>
<%@ attribute name="deviceCollection" required="false" type="com.cannontech.common.bulk.collection.DeviceCollection"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<c:url  var="url" scope="page" value="/spring/csr/search">
	<c:param name="count" value="${results.count}" />
	<c:param name="orderBy" value="${orderBy.field}" />
	<c:if test="${orderBy.descending}">
		<c:param name="descending" value="true"/>
	</c:if>
	<c:forEach var="filter" items="${filterByList}">
		<c:param name="${filter.name}" value="${filter.filterValue}" />
	</c:forEach>
</c:url>

<script language="JavaScript">

	function gotoPage(select) {
		window.location = '${url}&startIndex=' + $F(select);
	}

</script>
<table width="95%">
	<tr>
		<td>
			${results.startIndex + 1}-${results.endIndex} of ${results.hitCount} 
			<c:choose>
				<c:when test="${results.previousNeeded}">
					| 
			    	<amr:searchNavigationLink 
						orderBy="${orderBy}" 
						startIndex="0" 
						count="${results.count}"
						selected="false"
						filterByList="${filterByList}">
							First
					</amr:searchNavigationLink>						    	  
					| 
			    	<amr:searchNavigationLink 
						orderBy="${orderBy}" 
						startIndex="${results.previousStartIndex}" 
						count="${results.count}"
						selected="false"
						filterByList="${filterByList}">
							Previous
					</amr:searchNavigationLink>						    	  
				</c:when>
				<c:otherwise>
					| First
					| Previous
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${results.nextNeeded}">
					| 
			    	<amr:searchNavigationLink 
						orderBy="${orderBy}" 
						startIndex="${results.endIndex}" 
						count="${results.count}"
						selected="false"
						filterByList="${filterByList}">
							Next
					</amr:searchNavigationLink>
					| 
			    	<amr:searchNavigationLink 
						orderBy="${orderBy}" 
						startIndex="${results.lastStartIndex}" 
						count="${results.count}"
						selected="false"
						filterByList="${filterByList}">
							Last
					</amr:searchNavigationLink>
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
        
        <td>
            <form method="get" action="/spring/bulk/collectionActions">
            <input type="submit" name="submit" value="Perform Collection Action">
    
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            </form>
        </td>
        
		<td align="right">
			Results per page:
			<amr:searchNavigationLink 
				orderBy="${orderBy}" 
				startIndex="0" 
				count="10"
				selected="${results.count == 10}"
				filterByList="${filterByList}">
					10
			</amr:searchNavigationLink>
			| 
			<amr:searchNavigationLink 
				orderBy="${orderBy}" 
				startIndex="0" 
				count="25"
				selected="${results.count == 25}"
				filterByList="${filterByList}">
					25
			</amr:searchNavigationLink>
			| 
			<amr:searchNavigationLink 
				orderBy="${orderBy}" 
				startIndex="0" 
				count="50"
				selected="${results.count == 50}"
				filterByList="${filterByList}">
					50
			</amr:searchNavigationLink>
		</td>
	</tr>
</table>