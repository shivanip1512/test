<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="commanderSelect" page="select">
    <cti:standardMenu menuSelection="${menuSelection}" />
    
    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        <cti:msg key="yukon.web.components.button.home.label" var="homeLabel"/>
        <cti:crumbLink url="/operator/Operations.jsp" title="${homeLabel}" />
        
        <%-- bulk home --%>
        <cti:crumbLink><i:inline key=".pageTitle"/></cti:crumbLink>
    </cti:breadCrumbs>
    
    <h2><i:inline key=".pageTitle"/></h2>
    <br>
    
    <script type="text/javascript">
        function clearFilter() {
          <c:forEach var="filter" items="${filterByList}">
            jQuery('#${filter.name}').val('');
          </c:forEach>
          
          jQuery('#filterForm')[0].submit();
        }
    </script>
    
    <c:set var="baseUrl" value="/commander/select"/>
    
    <form id="filterForm" action="${baseUrl}">
        <tags:boxContainer2 nameKey="deviceSearch">
            <input type="hidden" name="startIndex" value="${deviceSearchResults.startIndex}" />
            <input type="hidden" name="itemsPerPage" value="${deviceSearchResults.count}" />
            <input type="hidden" name="orderBy" value="${orderBy}" />
            <input type="hidden" name="category" value="${category}" />
            
            <div class="filters">
                <c:forEach var="filter" items="${filterByList}">
                    <div style="width: 21em; text-align: right; float: left; margin-bottom: 5px; margin-right: 5px;">
                        <label for="${filter.name}"><i:inline key="${filter.field}" /> </label><input
                            style="width: 10em" type="text" id="${filter.name}" name="${filter.name}"
                            value="${filter.value}"
                        />
                    </div>
                </c:forEach>
            </div>
            
            <div class="actionArea clear">
                <cti:button nameKey="search" type="submit" />
                <cti:button nameKey="showAll" onclick="javascript:clearFilter()" />
            </div>
        </tags:boxContainer2>
    </form>
    
    <br>
    
    <cti:msg2 var="deviceSearchResultsTitle" key=".deviceSearchResults.title" />
    <tags:pagedBox title="${deviceSearchResultsTitle}" searchResult="${deviceSearchResults}" baseUrl="${baseUrl}" pageByHundereds="true">
        <table class="compactResultsTable rowHighlighting">
            <c:if test="${deviceSearchResults.hitCount > 0}">
                <tr>
                <c:forEach var="field" items="${fields}">
                    <th><tags:sortLink nameKey="deviceSearchField.${field}" baseUrl="${baseUrl}" fieldName="${field}" sortParam="orderBy"/></th>
                </c:forEach>
                </tr>
            </c:if>
            <c:forEach var="searchResultRow" items="${deviceSearchResults.resultList}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <c:forEach var="field" items="${fields}">
                        <c:choose>
                            <c:when test="${field eq 'NAME' || field eq 'LOAD_GROUP'}"><td><a title="${searchResultRow.paoIdentifier}" href="/commander/command?deviceId=${searchResultRow.paoIdentifier.paoId}">
                                <c:choose>
                                    <c:when test="${empty searchResultRow.paoName}"></c:when>
                                    <c:otherwise>${searchResultRow.paoName}</c:otherwise>
                                </c:choose>
                            </a></td></c:when>
                            <c:when test="${field eq 'TYPE' || field eq 'LMGROUP_TYPE'}"><td><span><cti:paoTypeIcon yukonPao="${searchResultRow}"/>&nbsp;${searchResultRow.paoType.dbString}</span></td></c:when>
                            <c:when test="${field eq 'ADDRESS'}"><td><c:choose>
                                <c:when test="${empty searchResultRow.address}">-</c:when>
                                <c:otherwise>${searchResultRow.address}</c:otherwise>
                            </c:choose></td></c:when>
                            <c:when test="${field eq 'METERNUMBER'}"><td><cti:meterNumber yukonPao="${searchResultRow}"/></td></c:when>
                            <c:when test="${field eq 'ROUTE'}"><td><cti:paoName paoId="${searchResultRow.routeID}"/></td></c:when>
                            <c:when test="${field eq 'COMM_CHANNEL'}"><td><cti:paoName paoId="${searchResultRow.portID}"/></td></c:when>
                        </c:choose>
                    </c:forEach>
                </tr>
            </c:forEach>
        </table>
    </tags:pagedBox>
</cti:standardPage>