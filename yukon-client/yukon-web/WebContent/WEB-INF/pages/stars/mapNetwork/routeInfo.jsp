<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.mapNetwork">

    <tags:nameValueContainer2 tableClass="name-collapse">
        <%@ include file="/WEB-INF/pages/stars/mapNetwork/commonInfo.jsp" %>
        <tags:nameValue2 nameKey=".destinationAddress" nameClass="js-destination-address-display" valueClass="js-destination-address js-destination-address-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".nextHopAddress" nameClass="js-next-hop-address-display" valueClass="js-next-hop-address js-next-hop-address-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".totalCost" nameClass="js-total-cost-display" valueClass="js-total-cost js-total-cost-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".hopCount" nameClass="js-hop-count-display" valueClass="js-hop-count js-hop-count-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".descendantCount" nameClass="js-descendant-count-display" valueClass="js-descendant-count js-descendant-count-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".flags" nameClass="js-route-flag-display" valueClass="js-route-flag js-route-flag-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".distanceToNextHop" nameClass="js-distance-display" valueClass="js-distance js-distance-display"></tags:nameValue2>
    </tags:nameValueContainer2>

</cti:msgScope>
