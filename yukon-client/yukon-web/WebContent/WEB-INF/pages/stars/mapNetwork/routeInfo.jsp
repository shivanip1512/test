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
    <tags:nameValue2 nameKey=".destinationAddress" valueClass="js-destination-address"></tags:nameValue2>
    <tags:nameValue2 nameKey=".nextHopAddress" valueClass="js-next-hop-address"></tags:nameValue2>
    <tags:nameValue2 nameKey=".totalCost" valueClass="js-total-cost"></tags:nameValue2>
    <tags:nameValue2 nameKey=".hopCount" valueClass="js-hop-count"></tags:nameValue2>
    <tags:nameValue2 nameKey=".flags" valueClass="js-route-flag"></tags:nameValue2>
    <tags:nameValue2 nameKey=".distanceToNextHop" valueClass="js-distance"></tags:nameValue2>
</tags:nameValueContainer2>
