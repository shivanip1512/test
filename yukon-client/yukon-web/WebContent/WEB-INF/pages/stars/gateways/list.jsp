<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="gateways.list">

    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="CREATE">
        <div id="gateway-create-popup" class="dn"
            data-dialog
            data-title="<cti:msg2 key=".create.gateway.label"/>" 
            data-url="<cti:url value="/stars/gateways/create"/>" 
            data-width="570" 
            data-min-width="570" 
            data-event="yukon:assets:gateway:save" 
            data-ok-text="<cti:msg2 key="components.button.save.label"/>" 
            data-load-event="yukon:assets:gateway:load">
        </div>

        <div id="send-firmware-upgrade-popup"
             data-dialog
             data-big-content
             data-width="750"
             data-height="450"
             data-help-text="<cti:msg key="yukon.web.modules.operator.gateways.firmwareUpdate.note"/>"
             data-url="<cti:url value="/stars/gateways/firmware-upgrade"/>"
             data-title="<cti:msg2 key="yukon.web.modules.operator.gateways.manageFirmware.firmwareUpdate"/>"
             data-event="yukon:assets:gateway:firmware-upgrade:send"
             data-ok-class="js-send-btn"
             data-ok-disabled
             data-confirm-multiple-text="<cti:msg2 key="yukon.web.modules.operator.gateways.manageFirmware.firmwareUpdate.confirmMultiple"/>"
             data-ok-text="<cti:msg2 key="components.button.send.label"/>">
            </div>
    </cti:checkRolesAndProperties>

    <div id="page-actions" class="dn">
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="CREATE">
            <cm:dropdownOption data-popup="#gateway-create-popup" icon="icon-plus-green">
                <i:inline key=".create.gateway.label"/>
            </cm:dropdownOption>
        </cti:checkRolesAndProperties>
        <cti:url var="comprehensiveMapUrl" value="/stars/comprehensiveMap/home"/>
        <cm:dropdownOption key="yukon.web.modules.operator.comprehensiveMap.pageName" href="${comprehensiveMapUrl}" icon="icon-map-pins"/>
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER">
            <cti:url var="manageCertificate" value="/stars/gateways/certificateUpdates"/>
            <cm:dropdownOption icon="icon-drive-go" key=".manageCertificates" href="${manageCertificate}"/>
        </cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="CREATE">
            <cti:url var="manageFirmware" value="/stars/gateways/firmwareDetails"/>
            <cm:dropdownOption icon="icon-drive-go" key=".manageFirmware" href="${manageFirmware}"/>
        </cti:checkRolesAndProperties>
    </div>

    <div id="gateway-collect-data-popup" class="dn"></div>

    <div data-url="<cti:url value="/stars/gateways"/>" data-static>
        <table id="gateways-table" class="compact-results-table has-actions has-alerts">
            <thead>
                <tr>
                    <th class="row-icon"/>
                    <th class="row-icon"/>
                    <tags:sort column="${NAME}"/>
                    <cti:checkLicenseKey keyName="RF_DATA_STREAMING_ENABLED">
                        <th width="10%"><i:inline key=".streamingCapacity"/></th>
                    </cti:checkLicenseKey>
                    <tags:sort column="${SERAILNO}"/>
                    <th><i:inline key=".ipaddress"/></th>
                    <tags:sort column="${FIRMWAREVERSION}"/>
                    <tags:sort column="${LASTCOMMUNICATION}"/>
                    <th><i:inline key=".detail.dataCollection.title"/></th>
                    <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach items="${gateways}" var="gateway">
                    <cti:url var="detailUrl" value="/stars/gateways/${gateway.paoIdentifier.paoId}"/>
                    <tr data-gateway="${gateway.paoIdentifier.paoId}" data-loaded="${not empty gateway.data}">
                        <td>
                            <c:if test="${notesList.contains(gateway.paoIdentifier.paoId)}">
                                <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                                <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" data-pao-id="${gateway.paoIdentifier.paoId}"/>
                            </c:if>
                        </td>
                        <c:choose>
                            <c:when test="${not empty gateway.data}">
                                
                                <c:set var="data" value="${gateway.data}"/>
                                <c:set var="title"><cti:msg2 key=".connectionStatus.${data.connectionStatus}"/></c:set>
                                <c:set var="clazz" value="${data.connectionStatus == 'CONNECTED' ? 'green' : 'red'}"/>
                                <td class="state-indicator js-gw-conn-status" title="${title}">
                                    <span class="state-box ${clazz}"></span>
                                </td>
                                <td class="js-gw-name"><a href="${detailUrl}">${fn:escapeXml(gateway.name)}</a></td>
                                <cti:checkLicenseKey keyName="RF_DATA_STREAMING_ENABLED">
                                    <td class="js-gw-capacity" width="10%">
                                        <c:choose>
                                            <c:when test="${gateway.dataStreamingSupported}">
                                                <c:set var="color" value="badge-success"/>
                                                <c:if test="${data.dataStreamingLoadingPercent > 100}">
                                                    <c:set var="color" value="badge-warning"/>
                                                </c:if>
                                                <c:if test="${data.dataStreamingLoadingPercent > 120}">
                                                    <c:set var="color" value="badge-error"/>
                                                </c:if>
                                                <span class="badge ${color} cp js-streaming-capacity" title="<cti:msg2 key=".streamingDetail"/>"><fmt:formatNumber pattern="###.##%" value="${data.dataStreamingLoadingPercent / 100}"/></span>
                                            </c:when>
                                            <c:otherwise>
                                                <i:inline key="yukon.common.unsupported"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </cti:checkLicenseKey>
                                <td class="js-gw-sn">${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</td>
                                <td class="js-gw-ip">${fn:escapeXml(gateway.data.ipAddress)}</td>
                                <td class="js-gw-rv" width="10%">
                                    <span class="js-gw-rv-text">
                                        ${fn:escapeXml(gateway.data.releaseVersion)}
                                    </span>
                                    <c:set var="dn" value=""/>
                                    <c:if test="${not gateway.upgradeAvailable}">
                                        <c:set var="dn" value="dn"/>
                                    </c:if>
                                    <cti:msg2 var="updateAvailable" key=".firmwareUpdateAvailable"/>
                                    <cti:icon icon="icon-download" data-popup="#send-firmware-upgrade-popup" classes="js-gateway-update-available fn cp ${dn}" title="${updateAvailable}"/>
                                </td>
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
                                <td>
                                    <cm:dropdown data-name="${fn:escapeXml(gateway.name)}" data-id="${gateway.paoIdentifier.paoId}">
                                        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER">
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
                                <cti:checkLicenseKey keyName="RF_DATA_STREAMING_ENABLED">
                                    <td><cti:icon icon="icon-loading-bars"/></td>
                                </cti:checkLicenseKey>
                                <td>${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</td>
                                <td><cti:icon icon="icon-loading-bars"/></td>
                                <td><cti:icon icon="icon-loading-bars"/></td>
                                <td><cti:icon icon="icon-loading-bars"/></td>
                                <td><cti:icon icon="icon-loading-bars"/></td>
                                <td></td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <div id="gateway-templates" class="dn">
        <table>
            <tr class="js-loaded-row" data-gateway="" data-loaded="true">
                <td class="js-notes">
                    <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp dn" title="${viewAllNotesTitle}" data-pao-id=""/>
                </td>
                <td class="js-gw-conn-status"><span class="state-box"></span></td>
                <td class="js-gw-name"><a></a></td>
                <cti:checkLicenseKey keyName="RF_DATA_STREAMING_ENABLED">
                    <td class="js-gw-capacity">
                        <span class="badge cp js-streaming-capacity dn" title="<cti:msg2 key=".streamingDetail"/>"></span>
                        <div class="js-streaming-unsupported dn"><i:inline key="yukon.common.unsupported"/></div>
                    </td>
                </cti:checkLicenseKey>
                <td class="js-gw-sn"></td>
                <td class="js-gw-ip"></td>
                <td class="js-gw-rv">
                    <span class="js-gw-rv-text"></span>
                    <cti:msg2 var="updateAvailable" key=".firmwareUpdateAvailable"/>
                    <cti:icon icon="icon-download" data-popup="#send-firmware-upgrade-popup" classes="js-gateway-update-available fn cp dn" title="${updateAvailable}"/>
                </td>
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
                <td class="js-notes"></td>
                <td class="js-gw-conn-status"><cti:icon icon="icon-loading-bars"/></td>
                <td class="js-gw-name"></td>
                <cti:checkLicenseKey keyName="RF_DATA_STREAMING_ENABLED">
                    <td class="js-gw-capacity">
                       <cti:icon icon="icon-loading-bars"/>
                    </td>
                </cti:checkLicenseKey>
                <td class="js-gw-sn"></td>
                <td><cti:icon icon="icon-loading-bars"/></td>
                <td><cti:icon icon="icon-loading-bars"/></td>
                <td><cti:icon icon="icon-loading-bars"/></td>
                <td><cti:icon icon="icon-loading-bars"/></td>
            </tr>
        </table>
        <cti:toJson object="${text}" id="gateway-text"/>
    </div>

    <div class="column-12-12 clearfix">
        <div class="column one">
            <cti:msg2 var="helpTextWidget" key="yukon.web.widgets.infrastructureWarningsWidget.helpText"/>
            <tags:widget bean="infrastructureWarningsWidget" helpText="${helpTextWidget}" infrastructureWarningDeviceCategory="${infrastructureWarningDeviceCategory}"/>
        </div>
        <div class="column two nogutter">
        </div>
    </div>

    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.list.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.shared.js"/>
    <cti:includeScript link="/resources/js/widgets/yukon.widget.infrastructureWarnings.js"/>
    <div class="dn" id="js-pao-notes-popup"></div>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
</cti:standardPage>