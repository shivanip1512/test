<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.mapNetwork">

    <tags:nameValueContainer2 tableClass="name-collapse">
        <%@ include file="/WEB-INF/pages/stars/mapNetwork/commonInfo.jsp" %>
        <tags:nameValue2 nameKey=".distance" nameClass="js-distance-display" valueClass="js-distance js-distance-display"></tags:nameValue2>
    </tags:nameValueContainer2>

</cti:msgScope>
