<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Selection" module="operations">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    &gt; Device selection
</cti:breadCrumbs>

<script language="JavaScript">

	function forwardToCsrHome(id) {
		window.location = "/spring/csr/home?deviceId=" + id;
		window.event.returnValue = false;
	}

	function clearFilter() {

		<c:forEach var="field" items="${fields}">
			$('${field.name}').value = '';
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
	
	<form id="filterForm" action="/spring/csr/search">
		<input type="hidden" name="Filter" value="true" />
		<input type="hidden" name="startIndex" value="${results.startIndex}" />
		<input type="hidden"  name="count" value="${results.count}" />
		<input type="hidden"  name="orderBy" value="${orderBy.field}" />
		<c:if test="${orderBy.descending}">
			<input type="hidden" name="descending" value="true"/>
		</c:if>
		
		Filter by<br/><br/>
		
		<div>
		<c:forEach var="field" items="${fields}">
			<div style="width: 21em; text-align: right; float:left; margin-bottom: 5px;margin-right: 5px;">${field.csrString}:&nbsp;<input style="width: 10em" type="text" id="${field.name}" name="${field.name}" value="${param[field.name]}"></input></div>
		</c:forEach>
		</div>
		<div style="clear:both"></div>
		
		<br/>
		<input type="submit" value="Filter"></input>
		<input type="button" value="Clear Filter" onclick="javascript:clearFilter()"></input>
	</form>
	<br/><br/>
	
	<c:choose>
		<c:when test="${results.hitCount == 0}">
			No results for Filter: ${filterByString}
		</c:when>
		<c:otherwise>
	
			<b>
				Filter: 
				<c:choose>
					<c:when test="${empty filterByString}">
						(no filter)
					</c:when>
					<c:otherwise>
						${filterByString}
					</c:otherwise>
				</c:choose>
			</b>
			<br/><br/>
			
			<amr:searchNavigation orderBy="${orderBy}" results="${results}" filterByList="${filterByList}"></amr:searchNavigation>
			
			<table class="resultsTable">
			  <tr>
			  	<th>
					Name
			  	</th>

				<!-- Output column headers -->
				<c:forEach var="field" items="${fields}">
				    <th>
				    	<amr:sortByLink 
				    		fieldName="${field.name}" 
				    		startIndex="${results.startIndex}" 
				    		count="${results.count}" 
				    		orderByField="${orderBy.field}" 
				    		orderByDesc="${orderBy.descending}" 
				    		selected="${orderBy.field == field.name}" 
				    		arrow="true" 
				    		filterByList="${filterByList}">
				    			${field.csrString}
				    	</amr:sortByLink>
				    </th>
				</c:forEach>

				</tr>
			    
			    <c:forEach var="device" items="${results.resultList}">
				  <tr class="<tags:alternateRow odd="" even="altRow"/>" onclick="javascript:forwardToCsrHome(${device.deviceId})" onmouseover="highLightRow(this)" onmouseout="unHighLightRow(this)">
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
						${device.collectionGroup}&nbsp;
				    </td>
				    <td>
						${device.billingGroup}&nbsp;
				    </td>
				    <td>
						${device.route}&nbsp;
				    </td>
				  </tr>
			    </c:forEach>
			</table>

			<amr:searchNavigation orderBy="${orderBy}" results="${results}" filterByList="${filterByList}"></amr:searchNavigation>

			</c:otherwise>
	</c:choose>
	
</cti:standardPage>