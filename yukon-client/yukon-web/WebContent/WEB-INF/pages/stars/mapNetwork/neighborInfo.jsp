<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.mapNetwork">

    <tags:nameValueContainer2 tableClass="name-collapse">
        <%@ include file="/WEB-INF/pages/stars/mapNetwork/commonInfo.jsp" %>
        <tags:nameValue2 nameKey=".etxBand" nameClass="js-etx-band-display" valueClass="js-etx-band js-etx-band-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".linkCost" nameClass="js-link-cost-display" valueClass="js-link-cost js-link-cost-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".numSamples" nameClass="js-num-samples-display" valueClass="js-num-samples js-num-samples-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".flags" nameClass="js-flags-display" valueClass="js-flags js-flags-display"></tags:nameValue2>
        <tags:nameValue2 nameKey=".distance" nameClass="js-distance-display" valueClass="js-distance js-distance-display"></tags:nameValue2>
    </tags:nameValueContainer2>

</cti:msgScope>