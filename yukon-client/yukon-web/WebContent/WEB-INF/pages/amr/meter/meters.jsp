<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="meterSearchResults">

    <cti:url value="/meter/home" var="meterHomeUrl"/>

    <script type="text/javascript">
    
        function forwardToMeterHome(row, id) {
            $('#deviceTable').removeClass('activeResultsTable');
            window.location = "${meterHomeUrl}?deviceId=" + id;
        }
    
        function clearFilter() {
    
            <c:forEach var="filter" items="${filterByList}">
              $('#${filter.name}').val('');
            </c:forEach>
            
            $('#filterForm')[0].submit();
        }
        
    </script>
    
    
    <cti:url var="formBaseUrl" value="/meter/search"/>
    <form id="filterForm" action="${formBaseUrl}">
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
                            value="${fn:escapeXml(filter.filterValue)}"
                        />
                    </div>
                </c:forEach>
            </div>
            <div class="action-area clear">
                <cti:button nameKey="search" name="filter" value="true" type="submit" classes="primary"/>
                <cti:button nameKey="showAll" onclick="javascript:clearFilter()" />
            </div>
        </tags:boxContainer2>
    </form>

    <br>
    
    <c:set var="baseUrl" value="/meter/search"/>
        <%-- DATA ROWS --%>
    <c:if test="${meterSearchResults.hitCount > 0}">
        <c:set var="linkHeaderHtml">
            <span class="navLink fr">
                <cm:deviceCollectionMenu deviceCollection="${deviceGroupCollection}"
                    key="yukon.web.modules.common.contextualMenu.actions"/>
            </span>
        </c:set>
    </c:if>
    <cti:msg2 var="meterSearchTitle" key=".meterSearchResultsTitle" />
    <tags:pagedBox title="${meterSearchTitle}" searchResult="${meterSearchResults}" baseUrl="${baseUrl}"
        pageByHundereds="true" titleLinkHtml="${linkHeaderHtml}">
        <table class="compact-results-table f-traversable row-highlighting has-actions">
            <c:if test="${meterSearchResults.hitCount > 0}">
                <thead>
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
                       <th></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
            </c:if>
            <tbody>
            <c:forEach var="searchResultRow" items="${meterSearchResults.resultList}">
                <tr>
                    <td>
                        <cti:paoDetailUrl yukonPao="${searchResultRow}">
                            <c:if test="${!empty searchResultRow.name}">${fn:escapeXml(searchResultRow.name)}</c:if>
                        </cti:paoDetailUrl>
                    </td>
                    <td><c:if test="${!empty searchResultRow.meterNumber}">${fn:escapeXml(searchResultRow.meterNumber)}</c:if></td>
                    <td><tags:paoType yukonPao="${searchResultRow}"/></td>
                    <td><c:if test="${!empty searchResultRow.serialOrAddress}">${fn:escapeXml(searchResultRow.serialOrAddress)}</c:if></td>
                    <td><c:if test="${!empty searchResultRow.route}">${fn:escapeXml(searchResultRow.route)}</c:if></td>
                    <td>
                        <cm:singleDeviceMenu deviceId="${searchResultRow.paoIdentifier.paoId}" triggerClasses="fr"/>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${meterSearchResults.hitCount == 0}">
                <tr>
                    <td colspan="5"><i:inline key=".notFound" /></td>
                </tr>
            </c:if>
            </tbody>
        </table>

    </tags:pagedBox>

</cti:standardPage>