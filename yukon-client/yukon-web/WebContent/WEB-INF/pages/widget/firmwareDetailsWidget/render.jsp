<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:msgScope paths="yukon.web.modules.operator.gateways.manageFirmware, yukon.web.modules.operator.gateways.list">
    <div id="gateway-firmware-details-popup" class="dn"></div>

    <div data-url="<cti:url value="/stars/gateways/firmwareDetailsList"/>">
        <%@ include file="../../stars/gateways/firmwareTable.jsp" %>
    </div>

    <div id="gateway-templates" class="dn">
        <table>
            <tr class="js-new-firmware-update" data-update-id="">
                <td class="js-firmware-update-timestamp">
                    <a href="javascript:void(0);"></a>
                </td>
                <td class="js-firmware-gateways"></td>
                <td class="js-firmware-update-servers"></td>
                <td class="js-firmware-update-status">
                    <div class="progress dif vat">
                        <div class="progress-bar progress-bar-success"></div>
                        <div class="progress-bar progress-bar-danger"></div>
                    </div>
                    <span class="js-percent"></span>
                </td>
                <td class="js-firmware-update-pending subtle"></td>
                <td class="js-firmware-update-failed error"></td>
                <td class="js-firmware-update-successful success"></td>
            </tr>
        </table>
        <cti:toJson object="${text}" id="gateway-text"/>
    </div>
    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.manageFirmware.js"/>
</cti:msgScope>
