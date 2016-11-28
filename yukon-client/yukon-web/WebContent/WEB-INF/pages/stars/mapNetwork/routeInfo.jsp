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
    <tags:nameValue2 nameKey=".serialNumber" nameClass="js-serial-number-display js-serial-number-label" valueClass="js-serial-number js-serial-number-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".gatewaySerialNumber" nameClass="js-gateway-serial-number-display" valueClass="js-gateway-serial-number js-gateway-serial-number-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".destinationAddress" nameClass="js-destination-address-display" valueClass="js-destination-address js-destination-address-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".nextHopAddress" nameClass="js-next-hop-address-display" valueClass="js-next-hop-address js-next-hop-address-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".totalCost" nameClass="js-total-cost-display" valueClass="js-total-cost js-total-cost-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".hopCount" nameClass="js-hop-count-display" valueClass="js-hop-count js-hop-count-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".flags" nameClass="js-route-flag-display" valueClass="js-route-flag js-route-flag-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".distanceToNextHop" nameClass="js-distance-display" valueClass="js-distance js-distance-display"></tags:nameValue2>
</tags:nameValueContainer2>
