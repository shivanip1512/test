<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:msgScope paths="yukon.web.modules.operator.mapNetwork">

    <div id="marker-info" class="well dn">
        <cm:dropdown icon="icon-cog" triggerClasses="js-cog-menu dn" style="padding-left:10px;">
            <cm:dropdownOption key=".mapDevice" classes="js-device-map" showIcon="false"></cm:dropdownOption>
            <cm:dropdownOption key=".viewNeighbors" classes="js-device-neighbors" showIcon="false"></cm:dropdownOption>
            <cm:dropdownOption key=".viewPrimaryRoute" classes="js-device-route" showIcon="false"></cm:dropdownOption>
        </cm:dropdown>
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