<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.mapNetwork">

    <div id="marker-info" class="well dn">
        <div id="actionsDiv" class="fr dn" style="padding-left: 10px">
            <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
            <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp dn" title="${viewAllNotesTitle}" data-pao-id="${pao.paoIdentifier.paoId}"/>
            <cm:dropdown icon="icon-cog" triggerClasses="js-cog-menu fr">
                <cm:dropdownOption key=".mapDevice" classes="js-device-map" showIcon="false"></cm:dropdownOption>
                <cm:dropdownOption key=".viewNeighbors" classes="js-device-neighbors" showIcon="false"></cm:dropdownOption>
                <cm:dropdownOption key=".viewPrimaryRoute" classes="js-device-route" showIcon="false"></cm:dropdownOption>
            </cm:dropdown>
        </div>
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
        <tags:hideReveal2 styleClass="mw300 dn js-nm-error" titleClass="error" titleKey="yukon.web.modules.tools.map.network.error" showInitially="false">
            <span class="js-nm-error-text"></span>
        </tags:hideReveal2>
    </div>

</cti:msgScope>