<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="yukon.web.modules.operator.gateways.list, yukon.web.modules.operator.gateways">

    <div id="gateway-collect-data-popup" class="dn"></div>

    <div data-url="<cti:url value="/stars/gateways/list"/>">
        <%@ include file="../../stars/gateways/gatewayTable.jsp" %>
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

    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.list.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.shared.js"/>
    <div class="dn" id="js-pao-notes-popup"></div>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
</cti:msgScope>