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

    <form id="filterForm" action="/spring/meter/search">
        <tags:boxContainer2 nameKey="meterSearch">
            <input type="hidden" name="startIndex" value="${meterSearchResults.startIndex}" />
            <input type="hidden" name="itemsPerPage" value="${meterSearchResults.count}" />
            <input type="hidden" name="orderBy" value="${orderBy.field}" />
            <c:if test="${orderBy.descending}">
                <input type="hidden" name="descending" value="true" />
            </c:if>
            <div class="filters">
                <c:forEach var="filter" items="${filterByList}">
                    <div style="width: 21em; text-align: right; float: left; margin-bottom: 5px; margin-right: 5px;">
                        <label for="${filter.name}"><i:inline key="${filter.formatKey}" /> </label><input
                            style="width: 10em" type="text" id="${filter.name}" name="${filter.name}"
                            value="${filter.filterValue}"
                        />
                    </div>
                </c:forEach>
            </div>
            <div class="actionArea clear">
                <cti:button nameKey="search" name="filter" value="true" type="submit" />
                <cti:button nameKey="showAll" onclick="javascript:clearFilter()" />
            </div>
        </tags:boxContainer2>
    </form>

    <br>
    
 		
        <%-- DATA ROWS --%>
    <c:if test="${meterSearchResults.hitCount > 0}">
        <c:set var="linkHeaderHtml">
            <span class="navLink"> 
                <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.deviceSelection.performCollectionAction">
                    <cti:mapParam value="${deviceGroupCollection.collectionParameters}" />
                </cti:link> 
            </span>
        </c:set>
    </c:if>
    <cti:msg2 var="meterSearchTitle" key=".meterSearchResultsTitle" />
    <tags:pagedBox title="${meterSearchTitle}" searchResult="${meterSearchResults}" baseUrl="${baseUrl}"
        pageByHundereds="true" titleLinkHtml="${linkHeaderHtml}">
        <table class="compactResultsTable rowHighlighting">
            <c:if test="${meterSearchResults.hitCount > 0}">
                <tr>
                    <th><tags:sortLink nameKey="columnHeader.deviceName" baseUrl="${baseUrl}" fieldName="PAONAME"
                            sortParam="orderBy" isDefault="${defaultSearchField == 'PAONAME'}"/>
                    </th>
                    <th><tags:sortLink nameKey="columnHeader.meterNumber" baseUrl="${baseUrl}"
                            fieldName="METERNUMBER" sortParam="orderBy"
                            isDefault="${defaultSearchField == 'METERNUMBER'}" />
                    </th>
                    <th><tags:sortLink nameKey="columnHeader.deviceType" baseUrl="${baseUrl}" fieldName="TYPE"
                            sortParam="orderBy" />
                    </th>
                    <th><tags:sortLink nameKey="columnHeader.address" baseUrl="${baseUrl}" fieldName="ADDRESS"
                            sortParam="orderBy"/>
                    </th>
                    <th><tags:sortLink nameKey="columnHeader.route" baseUrl="${baseUrl}" fieldName="ROUTE"
                            sortParam="orderBy" />
                   </th>
                </tr>
            </c:if>
            <c:forEach var="searchResultRow" items="${meterSearchResults.resultList}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td><cti:paoDetailUrl yukonPao="${searchResultRow}">
                            <c:choose>
                                <c:when test="${empty searchResultRow.name}"></c:when>
                                <c:otherwise>${searchResultRow.name}</c:otherwise>
                            </c:choose>
                        </cti:paoDetailUrl></td>
                    <td><c:choose>
                            <c:when test="${empty searchResultRow.meterNumber}"></c:when>
                            <c:otherwise>${searchResultRow.meterNumber}</c:otherwise>
                        </c:choose>
                    </td>
                    <td><tags:paoType yukonPao="${searchResultRow}" /></td>
                    <td><c:choose>
                            <c:when test="${empty searchResultRow.address}"></c:when>
                            <c:otherwise>${searchResultRow.address}</c:otherwise>
                        </c:choose>
                    </td>
                    <td><c:choose>
                            <c:when test="${empty searchResultRow.route}"></c:when>
                            <c:otherwise>${searchResultRow.route}</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${meterSearchResults.hitCount == 0}">
                <tr>
                    <td colspan="5"><i:inline key=".notFound" />
                    </td>
                </tr>
            </c:if>

        </table>

    </tags:pagedBox>

</cti:standardPage>
