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
	
    <c:set var="baseUrl" value="/spring/meter/search"/>

	<b>
	<c:choose>
		<c:when test="${meterSearchResults.hitCount == 0}">
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
        <input type="hidden" name="startIndex" value="${meterSearchResults.startIndex}" />
        <input type="hidden" name="itemsPerPage" value="${meterSearchResults.count}" />
        <input type="hidden" name="orderBy" value="${orderBy.field}" />
        <c:if test="${orderBy.descending}">
            <input type="hidden" name="descending" value="true"/>
        </c:if>
        
        <div>
	        <c:forEach var="filter" items="${filterByList}">
	            <div style="width: 21em; text-align: right; float:left; margin-bottom: 5px;margin-right: 5px;">
                    ${filter.name}:&nbsp;<input style="width: 10em" type="text" id="${filter.name}" name="${filter.name}" value="${filter.filterValue}" />
                </div>
	        </c:forEach>
        </div>
        <div style="clear:both"></div>
        <br>
        <cti:button key="filter" name="filter" value="true" type="submit"/>
        <cti:button key="showAll" onclick="javascript:clearFilter()"/>
    </form>
    </tags:hideReveal>
    <br>
    <br>
			
	<c:if test="${meterSearchResults.hitCount > 0}">
		
        <%-- DATA ROWS --%>

        <c:set var="linkHeaderHtml" >
            <span class="navLink">
                <cti:link href="/spring/bulk/collectionActions" key="yukon.web.metering.deviceSelection.performCollectionAction">
                    <cti:mapParam value="${deviceGroupCollection.collectionParameters}"/>                        
                </cti:link>
            </span>
        </c:set>

        <cti:msg2 var="meterSearchTitle" key=".meterSearchResultsTitle"/>
        <tags:pagedBox title="${meterSearchTitle}" searchResult="${meterSearchResults}" baseUrl="${baseUrl}" 
                       pageByHundereds="true" titleLinkHtml="${linkHeaderHtml}" >
            <table class="compactResultsTable rowHighlighting">
                <tr>
                    <th>
                        <tags:sortLink key=".columnHeader.deviceName"
                                       baseUrl="${baseUrl}" fieldName="PAONAME"
                                       sortParam="orderBy" descendingParam="descending"/>
                    </th>
                    <th>
                        <tags:sortLink key=".columnHeader.meterNumber"
                                       baseUrl="${baseUrl}" fieldName="METERNUMBER"
                                       sortParam="orderBy" descendingParam="descending"/>
                    
                    </th>
                    <th>
                        <tags:sortLink key=".columnHeader.deviceType"
                                       baseUrl="${baseUrl}" fieldName="TYPE"
                                       sortParam="orderBy" descendingParam="descending"/>
                    
                    </th>
                    <th>
                        <tags:sortLink key=".columnHeader.address"
                                       baseUrl="${baseUrl}" fieldName="ADDRESS"
                                       sortParam="orderBy" descendingParam="descending"/>
                    
                    </th>
                    <th>
                        <tags:sortLink key=".columnHeader.route"
                                       baseUrl="${baseUrl}" fieldName="ROUTE"
                                       sortParam="orderBy" descendingParam="descending"/>
                    
                    </th>
                </tr>

                <c:forEach var="searchResultRow" items="${meterSearchResults.resultList}">
                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                        <td>
                            <cti:paoDetailUrl  yukonPao="${searchResultRow}" >
                                <c:choose>
                                    <c:when test="${empty searchResultRow.name}"></c:when>
                                    <c:otherwise>${searchResultRow.name}</c:otherwise>
                                </c:choose>
                            </cti:paoDetailUrl>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${empty searchResultRow.meterNumber}"></c:when>
                                <c:otherwise>${searchResultRow.meterNumber}</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <cti:paoTypeIcon yukonPao="${searchResultRow}" /> &nbsp
                            <c:choose>
                                <c:when test="${empty searchResultRow.paoType}"></c:when>
                                <c:otherwise>${searchResultRow.paoType.dbString}</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${empty searchResultRow.address}"></c:when>
                                <c:otherwise>${searchResultRow.address}</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${empty searchResultRow.route}"></c:when>
                                <c:otherwise>${searchResultRow.route}</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty meterSearchResults.resultList}">
                    <tr>
                        <td colspan="5"><i:inline key=".noResults"/></td>
                    </tr>
                </c:if>
                
            </table>
            
        </tags:pagedBox>
            
	</c:if>

</cti:standardPage>
