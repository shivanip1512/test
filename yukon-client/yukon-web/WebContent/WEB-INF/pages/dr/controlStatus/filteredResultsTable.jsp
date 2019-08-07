
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.controlStatus">

    <span class="fwn"><i:inline key="yukon.common.filteredResults"/></span>
    <span class="badge">${statuses.hitCount}</span>&nbsp;<i:inline key="yukon.common.devices"/>
    
    <c:if test="${statuses.hitCount > 0}">
        <span class="js-cog-menu">
            <cm:dropdown icon="icon-cog">
                <cti:url var="collectionActionUrl" value="/bulk/collectionActions" htmlEscape="true">
                    <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                </cti:url>
                <cm:dropdownOption key="yukon.common.collectionActions" icon="icon-cog-go" href="${collectionActionUrl}" newTab="true"/> 
                <cm:dropdownOption icon="icon-csv" key="yukon.common.download" classes="js-download"/>  
            </cm:dropdown>
        </span>
    </c:if>
    
    <table class="compact-results-table row-highlighting has-actions">
        <thead>
            <tags:sort column="${device}" />
            <tags:sort column="${controlStatus}" />
            <tags:sort column="${controlStatusTimestamp}" />
            <tags:sort column="${restoreStatus}" />
            <tags:sort column="${restoreStatusTimestamp}" />
            <th class="action-column"><cti:icon icon="icon-cog" classes="M0" /></th>
        </thead>
        <tbody>
            <c:forEach var="controlStatus" items="${statuses.resultList}">
                <tr>
                    <td>
                        <cti:url var="inventoryViewUrl" value="/stars/operator/inventory/view">
                            <cti:param name="inventoryId" value="${controlStatus.inventoryId}"/>
                        </cti:url>
                        <a href="${inventoryViewUrl}" target="_blank">${fn:escapeXml(controlStatus.meterDisplayName)}</a>
                    </td>
                    <td><i:inline key="${controlStatus.controlStatus.formatKey}"/></td>
                    <td><cti:formatDate type="BOTH" value="${controlStatus.controlStatusTime}"/></td>
                    <td><i:inline key="${controlStatus.restoreStatus.formatKey}"/></td>
                    <td><cti:formatDate type="BOTH" value="${controlStatus.restoreStatusTime}"/></td>
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <cti:checkRolesAndProperties value="ALLOW_DISCONNECT_CONTROL">
                                <cm:dropdownOption key="yukon.web.modules.dr.disconnectStatus.connect" classes="js-connect" icon="icon-connect" 
                                    data-device-id="${controlStatus.deviceId}"/>
                                <c:choose>
                                    <c:when test="${fn:contains(optedOutDevices, controlStatus.inventoryId)}">
                                        <cti:msg2 var="disconnectNotAllowed" key="yukon.web.modules.dr.disconnectStatus.disconnectNotAllowed"/>
                                        <span title="${disconnectNotAllowed}">
                                            <cm:dropdownOption key="yukon.web.modules.dr.disconnectStatus.disconnect" icon="icon-disconnect" disabled="true"/>
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <cm:dropdownOption key="yukon.web.modules.dr.disconnectStatus.disconnect" classes="js-disconnect" 
                                            icon="icon-disconnect" data-device-id="${controlStatus.deviceId}"/>
                                    </c:otherwise>
                                </c:choose>
                            </cti:checkRolesAndProperties>
                            <cti:paoDetailUrl var="meterDetailLink" paoId="${controlStatus.deviceId}"/>
                            <cm:dropdownOption key="yukon.web.modules.dr.disconnectStatus.meterDetail" href="${meterDetailLink}" newTab="true" icon="icon-control-equalizer-blue"/>
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty statuses.resultList}">
                <tr><td colspan="6"><span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span></td></tr>
            </c:if>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${statuses}" adjustPageCount="true" thousands="true"/>

</cti:msgScope>