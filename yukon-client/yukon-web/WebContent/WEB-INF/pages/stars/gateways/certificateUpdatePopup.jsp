<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.gateways.cert.update">
    <c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>
    <cti:url var="url" value="/stars/gateways/cert-update"/>
    <form id="gateway-cert-form" method="POST" enctype="multipart/form-data" action="${url}">
        <cti:csrfToken/>
        <input type="hidden" class="js-no-file-upload" value="<cti:msg2 key="yukon.web.import.error.noImportFile"/>">
        <input type="hidden" class="js-no-gateway-selected" value="<cti:msg2 key=".noGatewaySelected"/>">
        <tags:nameValueContainer2 tableClass="with-form-controls">
            <tags:nameValue2 nameKey=".file">
                <tags:file name="file"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey="yukon.web.modules.operator.gateways.cert.update.gateways">
                <input type="hidden" id="gatewayIdList"/>
                <tags:pickerDialog id="gatewaysPicker" 
                                   type="rfGatewayLegacyPicker"
                                   linkType="selection"
                                   destinationFieldName="gatewayIdList"
                                   selectionProperty="paoName"
                                   multiSelectMode="true"
                                   allowEmptySelection="false"
                                   initialIds="${selectedGateways}"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </form>
</cti:msgScope>