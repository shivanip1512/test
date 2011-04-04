<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="meterSearchResults">

<cti:url value="/spring/meter/home" var="meterHomeUrl"/>

    <script type="text/javascript">
    
    	function forwardToMeterHome(row, id) {
    		$('deviceTable').removeClassName('activeResultsTable');
    		window.location = "${meterHomeUrl}?deviceId=" + id;
    	}
    
    	function clearFilter() {
    
    		<c:forEach var="filter" items="${filterByList}">
    			$('${filter.name}').value = '';
    		</c:forEach>
    		
    		$('filterForm').submit();
    	}
    	
    </script>
	
	<b>
	<c:choose>
		<c:when test="${results.hitCount == 0}">
        <i:inline key=".noResultsForFilter" arguments="${filterByString}"/>  
		</c:when>
		<c:otherwise>
				<i:inline key=".filter"/> 
				<c:choose>
					<c:when test="${empty filterByString}">
						<i:inline key=".showAll"/>
					</c:when>
					<c:otherwise>
						${filterByString}
					</c:otherwise>
				</c:choose>
		</c:otherwise>
	</c:choose>
	</b>
	
	<br/><br/>

    <cti:msg2 var="editFilters" key="yukon.web.modules.amr.meterSearchResults.editFilters"/>
    <tags:hideReveal title="${editFilters}" showInitially="true" slide="true">
    <form id="filterForm" action="/spring/meter/search">
        <input type="hidden" name="Filter" value="true" />
        <input type="hidden" name="startIndex" value="${results.startIndex}" />
        <input type="hidden" name="count" value="${results.count}" />
        <input type="hidden" name="orderBy" value="${orderBy.field}" />
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
        <cti:button key="filter" type="submit"/>
        <cti:button key="showAll" onclick="javascript:clearFilter()"/>
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
		    
            <%-- COLUMN HEADERS --%>
            <tr>
          
                <c:forEach var="dispEnum" items="${orderedDispEnums}">
                
                    <c:choose>
                    
                        <c:when test="${not empty dispEnum.searchField}">
                            <th nowrap>
                                <amr:sortByLink 
                                    field="${dispEnum.searchField}" 
                                    results="${results}" 
                                    orderBy="${orderBy}" 
                                    filterByList="${filterByList}">
                                        ${dispEnum.searchField.meterSearchString}
                                </amr:sortByLink>
                            </th>
                        </c:when>
                        
                        <c:otherwise>
                            <th>${dispEnum.label}</th>
                        </c:otherwise>
                        
                    </c:choose>
                    
                </c:forEach>
	
			</tr>
            
            <%-- DATA ROWS --%>
            <c:forEach var="row" items="${resultColumnsList}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>" 
                	onclick="javascript:forwardToMeterHome(this, ${row[idEnum]})" 
                	onmouseover="activeResultsTable_highLightRow(this)" 
                	onmouseout="activeResultsTable_unHighLightRow(this)">
                	
                <c:forEach var="dispEnum" items="${orderedDispEnums}">
                    <td>
                        <%-- although the result of ${null} is <BLANK>, explicitly leave --%>
                        <%-- open place to put a default value if value is null --%>
                        <c:choose>
                            <c:when test="${empty row[dispEnum]}"></c:when>
                            <c:otherwise>${row[dispEnum]}</c:otherwise>
                        </c:choose>
                    </td>
                </c:forEach>
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
