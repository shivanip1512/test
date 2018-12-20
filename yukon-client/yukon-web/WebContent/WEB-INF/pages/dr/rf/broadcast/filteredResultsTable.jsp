<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<cti:msgScope paths="modules.dr.rf.broadcast.eventDetail">
    <table class="compact-results-table row-highlighting has-alerts has-actions">
        <thead>
            <th class="row-icon"/>
            <th class="row-icon"/>
            <tags:sort column="${DEVICE_NAME}"/>
            <tags:sort column="${DEVICE_TYPE}"/>
            <tags:sort column="${ACCOUNT_NUMBER}"/>
            <tags:sort column="${CURRENT_STATUS}"/>
            <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
        </thead>
        <tbody>
            <c:forEach var="broadcastEventSearchResult" items="${searchResults.resultList}">
                <tr>
                    <td class="vam">
                        <c:if test="${broadcastEventSearchResult.messageStatus == 'SUCCESS'}">
                            <c:set var="circleColor" value="green-background"/>
                        </c:if>
                        <c:if test="${broadcastEventSearchResult.messageStatus == 'FAILURE'}">
                            <c:set var="circleColor" value="orange-background"/>
                        </c:if>
                        <c:if test="${broadcastEventSearchResult.messageStatus == 'UNKNOWN'}">
                            <c:set var="circleColor" value="grey-background"/>
                        </c:if>
                        <cti:msg2 var="statusText" key=".status.${broadcastEventSearchResult.messageStatus}"/>
                        <div class="small-circle ${circleColor}" title="${statusText}"></div>
                    </td>
                    <td>
                        <c:if test="${notesList.contains(broadcastEventSearchResult.hardware.deviceId)}">
                            <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                            <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" 
                                  data-pao-id="${broadcastEventSearchResult.hardware.deviceId}"/>
                        </c:if>
                    </td>
                    <td>
                        <cti:url value="/stars/operator/inventory/view" var="url">
                            <cti:param name="inventoryId" value="${broadcastEventSearchResult.hardware.identifier.inventoryId}"/>
                        </cti:url>
                        <a href="${url}"><cti:deviceName deviceId="${broadcastEventSearchResult.hardware.deviceId}"/></a>
                    </td>
                    <td><i:inline key="${broadcastEventSearchResult.hardware.identifier.hardwareType}"/></td>
                    <td>
                        <c:if test="${broadcastEventSearchResult.hardware.accountId > 0}">
                            <cti:url value="/stars/operator/account/view" var="url">
                                <cti:param name="accountId" value="${broadcastEventSearchResult.hardware.accountId}"/>
                            </cti:url>
                            <a href="${url}">${fn:escapeXml(broadcastEventSearchResult.hardware.accountNo)}</a>
                        </c:if>
                    </td>
                    <cti:formatDate var="dateTime" type="FULL" value="${broadcastEventSearchResult.lastComm}"/>
                    <cti:msg2 var="lastComm" key=".lastComm" argument="${dateTime}"/>
                    <td title="${lastComm}"><i:inline key="${broadcastEventSearchResult.deviceStatus}"/></td>
                    <td>
                        <c:if test="${broadcastEventSearchResult.hardware.deviceId != 0}">
                            <cm:dropdown icon="icon-cog">
                                <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                                    <cti:param name="collectionType" value="idList"/>
                                    <cti:param name="idList.ids" value="${broadcastEventSearchResult.hardware.deviceId}"/>
                                </cti:url>
                                <cm:dropdownOption key="yukon.common.collectionActions" href="${collectionActionsUrl}"
                                               icon="icon-cog-go" newTab="true"/>
                                <cti:url var="mapUrl" value="/tools/map">
                                    <cti:param name="collectionType" value="idList"/>
                                    <cti:param name="idList.ids" value="${broadcastEventSearchResult.hardware.deviceId}"/>
                                </cti:url>
                                <cm:dropdownOption icon="icon-map-sat" key="yukon.common.mapDevices" href="${mapUrl}" newTab="true"/>
                                <cti:checkRolesAndProperties value="INVENTORY">
                                    <cti:url var="inventoryActionUrl" value="/dr/rf/eventDetail/${broadcastEventSearchResult.hardware.deviceId}/inventoryAction"/>
                                    <cm:dropdownOption key=".inventoryAction" href="${inventoryActionUrl}"
                                                   icon="icon-cog-go" newTab="true"/>
                                </cti:checkRolesAndProperties>
                            </cm:dropdown>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${searchResults}" adjustPageCount="true" thousands="true"/>
</cti:msgScope>