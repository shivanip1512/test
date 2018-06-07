<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.mapNetwork">

    <tags:nameValueContainer2 tableClass="name-collapse">
        <tags:nameValue2 nameKey=".device" nameClass="js-device-display" valueClass="js-device js-device-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".meterNumber" nameClass="js-meter-number-display" valueClass="js-meter-number js-meter-number-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".ipAddress" nameClass="js-ip-address-display" valueClass="js-ip-address js-ip-address-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".type" nameClass="js-type-display" valueClass="js-type js-type-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".status" nameClass="js-status-display" valueClass="js-status js-status-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".nodeSN" nameClass="js-node-sn-display" valueClass="js-node-sn js-node-sn-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".serialNumber" nameClass="js-serial-number-display" valueClass="js-serial-number js-serial-number-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".gatewaySerialNumber" nameClass="js-gateway-serial-number-display" valueClass="js-gateway-serial-number js-gateway-serial-number-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".macAddress" nameClass="js-mac-address-display" valueClass="js-mac-address js-mac-address-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".primaryGateway" nameClass="js-primary-gateway-display" valueClass="js-primary-gateway js-primary-gateway-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".distance" nameClass="js-distance-display" valueClass="js-distance js-distance-display"></tags:nameValue2>
    </tags:nameValueContainer2>

</cti:msgScope>
