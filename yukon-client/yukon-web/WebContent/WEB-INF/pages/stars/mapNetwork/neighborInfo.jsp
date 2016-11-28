<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:nameValueContainer2 tableClass="name-collapse">    
    <tags:nameValue2 nameKey=".device" nameClass="js-device-display" valueClass="js-device js-device-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".type" nameClass="js-type-display" valueClass="js-type js-type-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".status" nameClass="js-status-display" valueClass="js-status js-status-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".nodeSN" nameClass="js-node-sn-display" valueClass="js-node-sn js-node-sn-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".serialNumber" nameClass="js-serial-number-display" valueClass="js-serial-number js-serial-number-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".gatewaySerialNumber" nameClass="js-gateway-serial-number-display" valueClass="js-gateway-serial-number js-gateway-serial-number-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".macAddress" nameClass="js-address-display" valueClass="js-address js-address-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".etxBand" nameClass="js-etx-band-display" valueClass="js-etx-band js-etx-band-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".linkCost" nameClass="js-link-cost-display" valueClass="js-link-cost js-link-cost-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".numSamples" nameClass="js-num-samples-display" valueClass="js-num-samples js-num-samples-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".flags" nameClass="js-flags-display" valueClass="js-flags js-flags-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".distance" nameClass="js-distance-display" valueClass="js-distance js-distance-display"></tags:nameValue2>
</tags:nameValueContainer2>