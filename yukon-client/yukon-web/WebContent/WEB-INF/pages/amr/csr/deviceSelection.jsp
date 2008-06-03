<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Selection" module="amr">
<cti:standardMenu menuSelection="deviceselection"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    &gt; Device Selection
</cti:breadCrumbs>

<c:url value="/spring/csr/home" var="csrHomeUrl"/>

<script language="JavaScript">

	function forwardToCsrHome(row, id) {
		$('deviceTable').removeClassName('activeResultsTable');
		window.location = "${csrHomeUrl}?deviceId=" + id;
	}

	function clearFilter() {

		<c:forEach var="filter" items="${filterByList}">
			$('${filter.name}').value = '';
		</c:forEach>
		
		$('filterForm').submit();
	}

	// These two functions are neccessary since IE does not support css :hover
	function highLightRow(row) {
		row = $(row);
		row.addClassName('hover');
	}
	
	function unHighLightRow(row){
		row = $(row);
		row.removeClassName('hover');
	}
	
</script>
	
	<h2>Device Selection</h2>
	<br>
	
	<b>
	<c:choose>
		<c:when test="${results.hitCount == 0}">
			No results for Filter: ${filterByString}
		</c:when>
		<c:otherwise>
	
				Filter: 
				<c:choose>
					<c:when test="${empty filterByString}">
						Show All
					</c:when>
					<c:otherwise>
						${filterByString}
					</c:otherwise>
				</c:choose>
		</c:otherwise>
	</c:choose>
	</b>
	
	<br/><br/>

    <tags:hideReveal title="Edit Filters" showInitially="true">
    <form id="filterForm" action="/spring/csr/search">
        <input type="hidden" name="Filter" value="true" />
        <input type="hidden" name="startIndex" value="${results.startIndex}" />
        <input type="hidden"  name="count" value="${results.count}" />
        <input type="hidden"  name="orderBy" value="${orderBy.field}" />
        <c:if test="${orderBy.descending}">
            <input type="hidden" name="descending" value="true"/>
        </c:if>
        
        
        <div>
	        <c:forEach var="filter" items="${filterByList}">
	            <div style="width: 21em; text-align: right; float:left; margin-bottom: 5px;margin-right: 5px;">${filter.name}:&nbsp;<input style="width: 10em" type="text" id="${filter.name}" name="${filter.name}" value="${filter.filterValue}"></input></div>
	        </c:forEach>
        </div>
        <div style="clear:both"></div>
        
        <br>
        <input type="submit" value="Filter"></input>
        <input type="button" value="Show All" onclick="javascript:clearFilter()"></input>
    </form>
    </tags:hideReveal>
    <br>
    <br>
			
	<c:if test="${results.hitCount > 0}">
		<amr:searchNavigation 
			orderBy="${orderBy}" 
			results="${results}" 
			filterByList="${filterByList}" 
            deviceCollection="${deviceGroupCollection}">
		</amr:searchNavigation>
		
		<table id="deviceTable" class="resultsTable activeResultsTable">
		  <tr>
		  	<th>
				Name
		  	</th>
	
			<!-- Output column headers -->
			<c:forEach var="field" items="${orderByFields}">
			    <th nowrap>
			    	<amr:sortByLink 
			    		field="${field}" 
			    		results="${results}" 
			    		orderBy="${orderBy}" 
			    		filterByList="${filterByList}">
			    			${field.csrString}
			    	</amr:sortByLink>
			    </th>
			</c:forEach>
	
			</tr>
		    
		    <c:forEach var="device" items="${results.resultList}">
			  <tr class="<tags:alternateRow odd="" even="altRow"/>" onclick="javascript:forwardToCsrHome(this, ${device.deviceId})" onmouseover="highLightRow(this)" onmouseout="unHighLightRow(this)">
			    <td>
			    	<cti:deviceName device="${device}"/>&nbsp;
			    </td>
			    <td>
					${device.meterNumber}&nbsp;
			    </td>
			    <td>
					${device.name}&nbsp;
			    </td>
			    <td>
					${device.typeStr}&nbsp;
			    </td>
			    <td>
					${device.address}&nbsp;
			    </td>
			    <td>
					${device.route}&nbsp;
			    </td>
			  </tr>
		    </c:forEach>
		</table>
	
		<amr:searchNavigation 
			orderBy="${orderBy}" 
			results="${results}" 
			filterByList="${filterByList}"
            deviceCollection="${deviceGroupCollection}">
		</amr:searchNavigation>
    
	</c:if>

	
</cti:standardPage>