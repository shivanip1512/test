<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:msgScope paths="yukon.web.modules.operator.gateways.manageCertificates, yukon.web.modules.operator.gateways.list">
    <div id="gateway-cert-details-popup" class="dn"></div>

    <div data-url="<cti:url value="/stars/gateways/certificateDetailsList"/>">
        <%@ include file="../../stars/gateways/certificateTable.jsp" %>
    </div>
    <div id="gateway-templates" class="dn">
        <table>
            <tr class="js-new-cert-update" data-yui="">
                <td class="js-cert-update-timestamp">
                    <a href="javascript:void(0);"></a>
                </td>
                <td class="js-cert-update-file"></td>
                <td class="js-cert-update-gateways"></td>
                <td class="js-cert-update-status">
                    <div class="progress dif vat">
                        <div class="progress-bar progress-bar-success"></div>
                        <div class="progress-bar progress-bar-danger"></div>
                    </div>
                    <span class="js-percent"></span>
                </td>
                <td class="js-cert-update-pending subtle"></td>
                <td class="js-cert-update-failed error"></td>
                <td class="js-cert-update-successful success"></td>
            </tr>
        </table>
        <cti:toJson object="${text}" id="gateway-text"/>
    </div>
    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.manageCertificate.js"/>
</cti:msgScope>