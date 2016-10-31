<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:nameValueContainer2 tableClass="name-collapse">
    <tags:nameValue2 nameKey=".device" valueClass="js-device"></tags:nameValue2>
    <tags:nameValue2 nameKey=".type" valueClass="js-type"></tags:nameValue2>
    <tags:nameValue2 nameKey=".status" valueClass="js-status"></tags:nameValue2>
    <tags:nameValue2 nameKey=".nodeSN" valueClass="js-node-sn"></tags:nameValue2>
    <tags:nameValue2 nameKey=".serialNumber" valueClass="js-serial-number"></tags:nameValue2>
    <tags:nameValue2 nameKey=".destinationAddress" nameClass="js-route-display" valueClass="js-destination-address js-route-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".nextHopAddress" nameClass="js-route-display" valueClass="js-next-hop-address js-route-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".totalCost" nameClass="js-route-display" valueClass="js-total-cost js-route-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".hopCount" nameClass="js-route-display" valueClass="js-hop-count js-route-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".flags" nameClass="js-route-display" valueClass="js-route-flag js-route-display"></tags:nameValue2>
    <tags:nameValue2 nameKey=".distanceToNextHop" nameClass="js-route-display" valueClass="js-distance js-route-display"></tags:nameValue2>
</tags:nameValueContainer2>
