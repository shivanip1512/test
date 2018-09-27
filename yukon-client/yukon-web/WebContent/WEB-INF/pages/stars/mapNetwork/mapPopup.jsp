<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:msgScope paths="yukon.web.modules.operator.mapNetwork">

    <div id="marker-info" class="well dn">
        <div id="device-info" class="dn"></div>
        <div id="parent-info" class="dn">
            <%@ include file="/WEB-INF/pages/stars/mapNetwork/parentInfo.jsp" %>
        </div>
        <div id="neighbor-info" class="dn">
            <%@ include file="/WEB-INF/pages/stars/mapNetwork/neighborInfo.jsp" %>
        </div>
        <div id="route-info" class="dn">
            <%@ include file="/WEB-INF/pages/stars/mapNetwork/routeInfo.jsp" %>
        </div>
    </div>

</cti:msgScope>