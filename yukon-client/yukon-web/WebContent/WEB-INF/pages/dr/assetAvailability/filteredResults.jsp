<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<span class="fwn"><i:inline key="yukon.common.filteredResults"/></span>
<span class="badge">${searchResults.hitCount}</span>&nbsp;<i:inline key="yukon.common.Devices"/>
<c:choose>
    <c:when test="${searchResults.hitCount > 0}">
        <span class="js-cog-menu">
            <cm:dropdown icon="icon-cog">
                <!-- Collection Actions -->
                <cm:dropdownOption key="yukon.common.collectionActions" icon="icon-cog-go"
                                   classes="js-collection-action" data-url="/bulk/collectionActions"/>
                <!-- Download -->
                <cm:dropdownOption icon="icon-csv" key="yukon.common.download" classes="primary action js-download-filter-result"/>
                <!-- Inventory Action -->
                <cti:checkRolesAndProperties value="INVENTORY">
                    <cm:dropdownOption icon="icon-cog-go" key="yukon.web.modules.dr.inventoryAction" classes="primary action js-inventory-actions" />
                </cti:checkRolesAndProperties>
                <!-- Map Devices -->
                <cm:dropdownOption icon="icon-map-sat" key="yukon.common.mapDevices" classes="js-collection-action"
                                   data-url="/tools/map"/>
                
            </cm:dropdown>
        </span>
        
        <cti:url var="filterUrl" value="/dr/assetAvailability/filterResultsTable">
            <cti:param name="paobjectId" value="${paobjectId}"/>
            <c:forEach var="subGroup" items="${deviceSubGroups}">
                <cti:param name="deviceSubGroups" value="${subGroup}"/>
            </c:forEach>
            <c:forEach var="status" items="${statuses}">
                <cti:param name="statuses" value="${status}"/>
            </c:forEach>
        </cti:url>

        <div data-url="${filterUrl}">
            <%@ include file="filteredResultsTable.jsp" %>
        </div>

        <div class="dn" id="js-pao-notes-popup"></div>
    </div>
    </c:when>
    <c:otherwise>
        <br/><br/><span class="empty-list"><i:inline key="yukon.common.search.noResultsFound" /></span>
    </c:otherwise>
</c:choose>