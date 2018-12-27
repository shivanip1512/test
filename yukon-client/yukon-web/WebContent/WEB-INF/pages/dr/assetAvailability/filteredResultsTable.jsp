<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<table class="compact-results-table row-highlighting has-alerts has-actions">
    <thead>
        <th class="row-icon"/>
        <th class="row-icon"/>
        <tags:sort column="${SERIAL_NUM}"/>
        <tags:sort column="${TYPE}"/>
        <tags:sort column="${LAST_COMM}"/>
        <tags:sort column="${LAST_RUN}"/>
        <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
    </thead>
    <tbody>
        <c:forEach var="assetAvailabilitySearchResult" items="${searchResults.resultList}">
            <tr>
                <input type="hidden" class="js-inventory-id" value="${assetAvailabilitySearchResult.inventoryId}"/>
                <td class="vam">
                    <c:if test="${assetAvailabilitySearchResult.availability == 'ACTIVE'}">
                        <c:set var="circleColor" value="green-background"/>
                    </c:if>
                    <c:if test="${assetAvailabilitySearchResult.availability == 'OPTED_OUT'}">
                        <c:set var="circleColor" value="blue-background"/>
                    </c:if>
                    <c:if test="${assetAvailabilitySearchResult.availability == 'INACTIVE'}">
                        <c:set var="circleColor" value="orange-background"/>
                    </c:if>
                    <c:if test="${assetAvailabilitySearchResult.availability == 'UNAVAILABLE'}">
                        <c:set var="circleColor" value="grey-background"/>
                    </c:if>
                    <cti:msg2 var="statusText" key="yukon.web.modules.dr.assetDetails.status.${assetAvailabilitySearchResult.availability}"/>
                    <div class="small-circle ${circleColor}" title="${statusText}"></div>
                </td>
                <td>
                    <c:if test="${notesList.contains(assetAvailabilitySearchResult.deviceId)}">
                        <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                        <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" 
                                  data-pao-id="${assetAvailabilitySearchResult.deviceId}"/>
                    </c:if>
                </td>
                <td>
                    <cti:url var="inventoryViewUrl" value="/stars/operator/inventory/view">
                        <cti:param name="inventoryId" value="${assetAvailabilitySearchResult.inventoryId}"/>
                    </cti:url>
                    <a href="${inventoryViewUrl}">${fn:escapeXml(assetAvailabilitySearchResult.serialNumber)}</a>
                </td>
                <td><i:inline key="${assetAvailabilitySearchResult.type}"/></td>
                <td>
                    <cti:formatDate type="BOTH" value="${assetAvailabilitySearchResult.lastComm}" 
                                    var="lastCommunitation"/>
                    ${lastCommunitation}
                </td>
                <td>
                    <cti:formatDate type="BOTH" value="${assetAvailabilitySearchResult.lastRun}" 
                                    var="lastRun"/>
                    ${lastRun}
                </td>
                <td>
                    <cm:dropdown icon="icon-cog">
                        <c:if test="${assetAvailabilitySearchResult.deviceId != 0}"> 
                            <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                                <cti:param name="collectionType" value="idList"/>
                                <cti:param name="idList.ids" value="${assetAvailabilitySearchResult.deviceId}"/>
                            </cti:url>
                            <cm:dropdownOption key="yukon.common.collectionActions" href="${collectionActionsUrl}"
                                               icon="icon-cog-go" newTab="true"/>
                            <cti:url var="mapUrl" value="/tools/map">
                                <cti:param name="collectionType" value="idList"/>
                                <cti:param name="idList.ids" value="${assetAvailabilitySearchResult.deviceId}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-map-sat" key="yukon.common.mapDevices" href="${mapUrl}" newTab="true"/>
                            <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                                <cti:url var="readUrl" value="/group/groupMeterRead/homeCollection">
                                    <cti:param name="collectionType" value="idList"/>
                                    <cti:param name="idList.ids" value="${assetAvailabilitySearchResult.deviceId}"/>
                                </cti:url>
                            </cti:checkRolesAndProperties>
                        </c:if>
                        <cti:checkRolesAndProperties value="INVENTORY">
                            <cti:url var="inventoryActionUrl" value="/stars/operator/inventory/inventoryActions">
                                <cti:param name="collectionType" value="idList"/>
                                <cti:param name="idList.ids" value="${assetAvailabilitySearchResult.inventoryId}"/>
                            </cti:url>
                            <cm:dropdownOption icon="icon-cog-go" key="yukon.web.modules.dr.assetDetails.inventoryAction" href="${inventoryActionUrl}" newTab="true"/>
                        </cti:checkRolesAndProperties>
                    </cm:dropdown>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<tags:pagingResultsControls result="${searchResults}" adjustPageCount="true" thousands="true"/>