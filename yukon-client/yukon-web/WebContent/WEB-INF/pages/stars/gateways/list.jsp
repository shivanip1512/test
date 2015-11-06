<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="gateways.list">

<c:set var="showPageActions" value="${false}"/>
<cti:checkRolesAndProperties value="INFRASTRUCTURE_CREATE_AND_UPDATE">
    <c:set var="showPageActions" value="${true}"/>

    <div id="gateway-create-popup" class="dn"
        data-dialog
        data-title="<cti:msg2 key=".create.gateway.label"/>" 
        data-url="<cti:url value="/stars/gateways/create"/>" 
        data-width="570" 
        data-min-width="570" 
        data-event="yukon:assets:gateway:save" 
        data-ok-text="<cti:msg2 key="components.button.save.label"/>" 
        data-load-event="yukon:assets:gateway:load"></div>

    <c:if test="${enableNMGatewayVersion}">
        <div id="firmware-upgrade-popup"
            class="js-dialog-scroll"
            data-dialog
            data-url="<cti:url value="/stars/gateways/update-servers"/>"
            data-title="<cti:msg2 key=".updateServer.set"/>"
            data-event="yukon:assets:gateway:update-server:save"
            data-ok-text="<cti:msg2 key="components.button.save.label"/>">
        </div>

        <cti:checkGlobalRolesAndProperties value="DEVELOPMENT_MODE">
            <div id="send-firmware-upgrade-popup"
                class="js-dialog-scroll"
                data-dialog
                data-url="<cti:url value="/stars/gateways/firmware-upgrade"/>"
                data-title="<cti:msg2 key=".firmwareUpdate"/>"
                data-event="yukon:assets:gateway:firmware-upgrade:send"
                data-confirm-multiple-text="<cti:msg2 key=".firmwareUpdate.confirmMultiple"/>"
                data-ok-text="<cti:msg2 key="components.button.send.label"/>">
            </div>
        </cti:checkGlobalRolesAndProperties>
    </c:if>
</cti:checkRolesAndProperties>

<c:if test="${showPageActions}">
    <div id="page-actions" class="dn">
        <cti:checkRolesAndProperties value="INFRASTRUCTURE_CREATE_AND_UPDATE">
            <cm:dropdownOption data-popup="#gateway-create-popup" icon="icon-plus-green">
                <i:inline key=".create.gateway.label"/>
            </cm:dropdownOption>

            <c:if test="${enableNMGatewayVersion}">
                <cm:dropdownOption data-popup="#firmware-upgrade-popup" icon="icon-drive-go"
                    classes="update-servers disabled" disabled="true">

                    <i:inline key=".updateServer.set"/>
                </cm:dropdownOption>
                <cti:checkGlobalRolesAndProperties value="DEVELOPMENT_MODE">
                    <cm:dropdownOption data-popup="#send-firmware-upgrade-popup" icon="icon-drive-go"
                        classes="update-servers disabled" disabled="true">
    
                        <i:inline key=".firmwareUpdate"/>
                    </cm:dropdownOption>
                </cti:checkGlobalRolesAndProperties>
            </c:if>
        </cti:checkRolesAndProperties>
    </div>
</c:if>

<cti:checkRolesAndProperties value="INFRASTRUCTURE_ADMIN">
    <div id="gateway-cert-popup" class="dn" data-dialog 
        data-title="<cti:msg2 key=".cert.update.label"/>"
        data-event="yukon:assets:gateway:cert:update"
        data-url="<cti:url value="/stars/gateways/cert-update/options"/>"
        data-ok-text="<cti:msg2 key="components.button.start.label"/>"></div>
</cti:checkRolesAndProperties>
    
<div id="gateway-collect-data-popup" class="dn"></div>
<div id="gateway-cert-details-popup" class="dn"></div>
<div id="gateway-firmware-details-popup" class="dn"></div>

<div class="stacked-lg">
<table id="gateways-table" class="compact-results-table has-actions has-alerts">
    <thead>
        <tr>
            <th></th>
            <th><i:inline key=".name"/></th>
            <th><i:inline key=".serialNumber"/></th>
            <th><i:inline key=".ipaddress"/></th>
            <c:if test="${enableNMGatewayVersion}">
                <th><i:inline key=".firmwareVersion"/></th>
            </c:if>
            <th><i:inline key=".lastComms"/></th>
            <th colspan="2"><i:inline key=".detail.dataCollection.title"/></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach items="${gateways}" var="gateway">
            <cti:url var="detailUrl" value="/stars/gateways/${gateway.paoIdentifier.paoId}"/>
            <tr data-gateway="${gateway.paoIdentifier.paoId}" data-loaded="${not empty gateway.data}">
                <c:choose>
                    <c:when test="${not empty gateway.data}">
                        <c:set var="data" value="${gateway.data}"/>
                        <c:set var="title"><cti:msg2 key=".connectionStatus.${data.connectionStatus}"/></c:set>
                        <c:set var="clazz" value="${data.connectionStatus == 'CONNECTED' ? 'green' : 'red'}"/>
                        <td class="state-indicator js-gw-conn-status" title="${title}">
                            <span class="state-box ${clazz}"></span>
                        </td>
                        <td class="js-gw-name"><a href="${detailUrl}">${fn:escapeXml(gateway.name)}</a></td>
                        <td class="js-gw-sn">${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</td>
                        <td class="js-gw-ip">${fn:escapeXml(gateway.data.ipAddress)}</td>
                        <c:if test="${enableNMGatewayVersion}">
                            <td class="js-gw-rv">${fn:escapeXml(gateway.data.releaseVersion)}
                                <c:if test="${not gateway.upgradeAvailable}">
                                   <cti:msg2 var="updateAvailable" key=".firmwareUpdateAvailable"/>
                                   <cti:icon icon="icon-download" classes="js-gateway-update-available fn dn" title="${updateAvailable}"/>
                                </c:if>
                            </td>
                        </c:if>
                        <c:set var="clazz" value="green"/>
                        <c:if test="${gateway.lastCommFailed}">
                            <c:set var="clazz" value="red"/>
                        </c:if>
                        <c:if test="${gateway.lastCommMissed}">
                            <c:set var="clazz" value="orange"/>
                        </c:if>
                        <c:if test="${gateway.lastCommUnknown}">
                            <c:set var="clazz" value="subtle"/>
                        </c:if>
                        <td class="js-gw-last-comm ${clazz}" 
                            title="<cti:formatDate type="DATEHM" value="${gateway.data.lastCommStatusTimestamp}"/>">
                            <i:inline key=".lastCommStatus.${gateway.data.lastCommStatus}"/>
                        </td>
                        <td class="js-gw-data-collection">
                            <div class="dib vat progress">
                                <c:set var="clazz" value="progress-bar-success"/>
                                <c:if test="${gateway.totalCompletionLevelWarning}">
                                    <c:set var="clazz" value="progress-bar-warning"/>
                                </c:if>
                                <c:if test="${gateway.totalCompletionLevelDanger}">
                                    <c:set var="clazz" value="progress-bar-danger"/>
                                </c:if>
                                <div class="progress-bar ${clazz}" 
                                    style="width: ${gateway.totalCompletionPercentage}%"></div>
                            </div>&nbsp;
                            <span class="js-gw-data-collection-percent">
                                <fmt:formatNumber pattern="###.##%" value="${gateway.totalCompletionPercentage / 100}"/>
                            </span>
                        </td>
                        <td class="action-column">
                            <cm:dropdown data-name="${fn:escapeXml(gateway.name)}" data-id="${gateway.paoIdentifier.paoId}">
                                <cti:checkRolesAndProperties value="INFRASTRUCTURE_ADMIN">
                                    <cm:dropdownOption icon="icon-connect" key=".connect" classes="js-gw-connect"/>
                                    <cm:dropdownOption icon="icon-disconnect" key=".disconnect" classes="js-gw-disconnect"/>
                                    <li class="divider"></li>
                                </cti:checkRolesAndProperties>
                                <cm:dropdownOption icon="icon-table-row-insert" key=".collectData" 
                                    classes="js-gw-collect-data"/>
                            </cm:dropdown>
                        </td>
                    </c:when>
                    <c:otherwise>
                        <td><cti:icon icon="icon-loading-bars"/></td>
                        <td><a href="${detailUrl}">${fn:escapeXml(gateway.name)}</a></td>
                        <td>${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</td>
                        <td colspan="4"><em><i:inline key=".loadingGatewayData"/></em></td>
                    </c:otherwise>
                </c:choose>
            </tr>
        </c:forEach>
    </tbody>
</table>
<cti:checkRolesAndProperties value="INFRASTRUCTURE_CREATE_AND_UPDATE">
    <div class="action-area">
        <cti:button icon="icon-plus-green" nameKey="create.gateway" data-popup="#gateway-create-popup"/>
    </div>
</cti:checkRolesAndProperties>
</div>

<h3><i:inline key=".cert.updates"/></h3>
<c:set var="clazz" value="${empty certUpdates ? 'dn' : ''}"/>
<c:if test="${empty certUpdates}">
    <div class="js-no-cert-updates empty-list">
        <i:inline key=".cert.updates.none"/>
    </div>
</c:if>
<div data-url="<cti:url value="/stars/gateways/"/>" data-static> 
<table id="cert-table" class="compact-results-table ${clazz}">
    <thead>
        <tr>
            <tags:sort column="${TIMESTAMP}"/>
            <tags:sort column="${CERTIFICATE}"/>
            <th><i:inline key=".cert.update.gateways"/></th>
            <th><i:inline key="yukon.common.status"/></th>
            <th class="tar"><i:inline key="yukon.common.pending"/></th>
            <th class="tar"><i:inline key="yukon.common.failed"/></th>
            <th class="tar"><i:inline key="yukon.common.successful"/></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="update" items="${certUpdates}">
            <tr data-yui="${update.yukonUpdateId}">
                <td class="js-cert-update-timestamp">
                    <a href="javascript:void(0);"><cti:formatDate value="${update.timestamp}" type="DATEHM_12"/></a>
                </td>
                <td class="js-cert-update-file">${fn:escapeXml(update.fileName)}</td>
                <c:set var="all" value="${update.gateways}"/>
                <td class="js-cert-update-gateways">
                    ${fn:escapeXml(all[0].name)}<c:if test="${fn:length(all) > 1}">,&nbsp;${fn:escapeXml(all[1].name)}</c:if>
                    <c:if test="${fn:length(all) > 2}">
                        <i:inline key=".cert.update.more" arguments="${fn:length(all) - 2}"/>
                    </c:if>
                </td>
                <td class="js-cert-update-status">
                    <c:choose>
                        <c:when test="${fn:length(update.pending) == 0}">
                            <span class="success"><i:inline key="yukon.common.complete"/></span>
                        </c:when>
                        <c:otherwise>
                            <div class="progress dib vat">
                                <div class="progress-bar progress-bar-success" style="width: ${update.successPercent};"></div>
                                <div class="progress-bar progress-bar-danger" style="width: ${update.failedPercent};"></div>
                            </div>
                            <span class="js-percent">${update.totalPercent}</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="js-cert-update-pending tar subtle">${fn:length(update.pending)}</td>
                <td class="js-cert-update-failed tar error">${fn:length(update.failed)}</td>
                <td class="js-cert-update-successful tar success">${fn:length(update.successful)}</td>
            </tr>
        </c:forEach>
        
    </tbody>
</table>
</div>
<cti:checkRolesAndProperties value="INFRASTRUCTURE_ADMIN">
    <div class="page-action-area">
        <cti:button icon="icon-plus-green" nameKey="cert.update" data-popup="#gateway-cert-popup" classes="M0"/>
    </div>
</cti:checkRolesAndProperties>

<c:if test="${enableNMGatewayVersion}">
<h3><i:inline key=".firmwareUpdates"/></h3>
<c:if test="${empty firmwareUpdates}">
    <div class="js-no-firmware-updates empty-list">
        <i:inline key=".firmwareUpdates.none"/>
    </div>
</c:if>
<div data-url="<cti:url value="/stars/gateways/"/>" data-static>
    <c:set var="clazz" value="${empty firmwareUpdates ? 'dn' : ''}"/>
    <table id="firmware-table" class="compact-results-table ${clazz}">
        <thead>
            <tr>
                <th><i:inline key=".firmwareUpdates.timestamp"/></th>
                <th><i:inline key=".firmwareUpdates.totalGateways"/></th>
                <th><i:inline key=".firmwareUpdates.totalServers"/></th>
                <th><i:inline key="yukon.common.status"/></th>
                <th class="tar"><i:inline key="yukon.common.pending"/></th>
                <th class="tar"><i:inline key="yukon.common.failed"/></th>
                <th class="tar"><i:inline key="yukon.common.successful"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="update" items="${firmwareUpdates}">
                <tr data-yui="${update.updateId}">
                    <td class="js-firmware-update-timestamp">
                        <a href="javascript:void(0);">
                            <cti:formatDate value="${update.sendDate}" type="DATEHM_12"/>
                        </a>
                    </td>
                    <td class="js-firmware-gateways">${update.totalGateways}</td>
                    <td class="js-firmware-update-servers">${update.totalUpdateServers}</td>
                    <td class="js-firmware-update-status">
                        <c:choose>
                            <c:when test="${update.gatewayUpdatesPending == 0}">
                                <span class="success"><i:inline key="yukon.common.complete"/></span>
                            </c:when>
                            <c:otherwise>
                                <div class="progress dib vat">
                                    <div class="progress-bar progress-bar-success" style="width: ${update.successPercent};"></div>
                                    <div class="progress-bar progress-bar-danger" style="width: ${update.failedPercent};"></div>
                                </div>
                                <span class="js-percent">${update.totalPercent}</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="js-firmware-update-pending tar subtle">${update.gatewayUpdatesPending}</td>
                    <td class="js-firmware-update-failed tar error">${update.gatewayUpdatesFailed}</td>
                    <td class="js-firmware-update-successful tar success">${update.gatewayUpdatesSuccessful}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
</c:if>

<div id="gateway-templates" class="dn">
    <table>
        <tr class="js-loaded-row" data-gateway="" data-loaded="true">
            <td class="js-gw-conn-status"><span class="state-box"></span></td>
            <td class="js-gw-name"><a></a></td>
            <td class="js-gw-sn"></td>
            <td class="js-gw-ip"></td>
            <c:if test="${enableNMGatewayVersion}">
                <td class="js-gw-rv"></td>
            </c:if>
            <td class="js-gw-last-comm"></td>
            <td class="js-gw-data-collection">
                <div class="dib vat progress">
                    <div class="progress-bar progress-bar-success"></div>
                </div>&nbsp;
                <span class="js-gw-data-collection-percent"></span>
            </td>
            <td class="action-column">
                <cm:dropdown>
                    <cm:dropdownOption icon="icon-connect" key=".connect" classes="js-gw-connect"/>
                    <cm:dropdownOption icon="icon-disconnect" key=".disconnect" classes="js-gw-disconnect"/>
                    <li class="divider"></li>
                    <cm:dropdownOption icon="icon-table-row-insert" key=".collectData" classes="js-gw-collect-data"/>
                </cm:dropdown>
            </td>
        </tr>
        <tr class="js-loading-row" data-gateway="" data-loaded="false">
            <td class="js-gw-conn-status"><cti:icon icon="icon-loading-bars"/></td>
            <td class="js-gw-name"></td>
            <td class="js-gw-sn"></td>
            <td colspan="4"><i:inline key=".loadingGatewayData"/></td>
        </tr>
        <tr class="js-new-cert-update" data-yui="">
            <td class="js-cert-update-timestamp">
                <a href="javascript:void(0);"></a>
            </td>
            <td class="js-cert-update-file"></td>
            <td class="js-cert-update-gateways"></td>
            <td class="js-cert-update-status">
                <div class="progress dib vat">
                    <div class="progress-bar progress-bar-success"></div>
                    <div class="progress-bar progress-bar-danger"></div>
                </div>
                <span class="js-percent"></span>
            </td>
            <td class="js-cert-update-pending tar subtle"></td>
            <td class="js-cert-update-failed tar error"></td>
            <td class="js-cert-update-successful tar success"></td>
        </tr>
        
        
        <tr class="js-new-firmware-update" data-yui="">
            <td class="js-firmware-update-timestamp">
                <a href="javascript:void(0);"></a>
            </td>
            <td class="js-firmware-gateways"></td>
            <td class="js-firmware-update-servers"></td>
            <td class="js-firmware-update-status">
                <div class="progress dib vat">
                    <div class="progress-bar progress-bar-success"></div>
                    <div class="progress-bar progress-bar-danger"></div>
                </div>
                <span class="js-percent"></span>
            </td>
            <td class="js-firmware-update-pending tar subtle"></td>
            <td class="js-firmware-update-failed tar error"></td>
            <td class="js-firmware-update-successful tar success"></td>
        </tr>
        
        
    </table>
    <cti:toJson object="${text}" id="gateway-text"/>
</div>

<cti:includeScript link="/resources/js/pages/yukon.assets.gateway.list.js"/>
<cti:includeScript link="/resources/js/pages/yukon.assets.gateway.shared.js"/>

</cti:standardPage>