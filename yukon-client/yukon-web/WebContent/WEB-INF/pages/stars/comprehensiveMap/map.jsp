
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="operator" page="comprehensiveMap">

<style>
.map .ol-viewport canvas {
     min-height: 600px;
}
</style>

    <%@ include file="/WEB-INF/pages/stars/mapNetwork/mapPopup.jsp" %>

    <cti:msg2 var="tooManyGateways" key=".tooManyGateways"/>
    <input type="hidden" id="tooManyGatewaysError" value="${tooManyGateways}"/>
    <cti:msg2 var="coordinatesDeleted" key="yukon.web.modules.operator.mapNetwork.location.delete.successful"/>
    <input id="coordinatesDeletedMsg" type="hidden" value="${coordinatesDeleted}"/>
    
    <input id="gatewayTypes" type="hidden" value="${gatewayPaoTypes}"/>
    <input id="relayTypes" type="hidden" value="${relayPaoTypes}"/>
    <input id="wifiTypes" type="hidden" value="${wifiPaoTypes}"/>
    
    <%@ include file="/WEB-INF/pages/tools/map/mapWarnings.jsp" %>
        
    <div style="height:800px;">
        <div id="comprehensive-map-container" style="height:100%;width:100%;background:white;">
            <div class="filter-section">
                <hr>
                <form:form id="filter-form" modelAttribute="filter">
                    <i:inline key="yukon.common.filterBy"/>&nbsp;
        
                    <cti:msg2 var="gatewayPlaceholder" key=".selectGateways"/>
                    <tags:selectWithItems items="${gateways}" path="selectedGatewayIds" itemLabel="name" itemValue="id"
                        inputClass="js-selected-gateways" dataPlaceholder="${gatewayPlaceholder}"/>&nbsp;&nbsp;
                        
                    <i:inline key=".colorCodeBy.LINK_QUALITY"/>:&nbsp;
                    <cti:msg2 var="allPlaceholder" key="yukon.common.all"/>
                    <tags:selectWithItems items="${linkQualityOptions}" path="linkQuality" inputClass="js-selected-link-qualities" dataPlaceholder="${allPlaceholder}"/>&nbsp;&nbsp;
                    
                    <i:inline key=".colorCodeBy.DESCENDANT_COUNT"/>:&nbsp;
                    <tags:selectWithItems items="${descendantCountOptions}" path="descendantCount" inputClass="js-selected-descendant-count" dataPlaceholder="${allPlaceholder}"/>
                                    
                    <span class="fr cp"><cti:icon icon="icon-help" data-popup="#map-help"/></span>
                    <cti:msg2 var="helpTitle" key=".helpTitle"/>
                    <div id="map-help" class="dn" data-width="600" data-height="360" data-title="${helpTitle}"><cti:msg2 key=".helpText"/></div><br/>
    
                    <div style="padding-top:5px;padding-left:60px;">
                        <i:inline key=".colorCodeBy.HOP_COUNT"/>:&nbsp;
                        <tags:selectWithItems items="${hopCountOptions}" path="hopCount" inputClass="js-selected-hop-count" dataPlaceholder="${allPlaceholder}"/>&nbsp;&nbsp;
                    
                        <i:inline key=".colorCodeBy"/>&nbsp;
                        <tags:selectWithItems items="${colorCodeByOptions}" path="colorCodeBy"/>
                        
                        <cti:button nameKey="filter" classes="js-filter-map primary action fr vab" disabled="true"/>
                    </div>
                    
                </form:form>
                <hr>
            </div>
            
            <div id="filtered-devices" class="dn PB10">
                <span class="fwn"><i:inline key="yukon.common.filteredResults"/></span>
                <span class="badge js-number-devices"></span>&nbsp;<i:inline key="yukon.common.devices"/>
                <input type="hidden" id="collection-group"/>
            
                <span class="js-cog-menu">
                    <cm:dropdown icon="icon-cog">
                        <cm:dropdownOption key="yukon.common.collectionActions" icon="icon-cog-go" linkId="collectionActionLink" newTab="true"/> 
                        <cm:dropdownOption icon="icon-csv" key="yukon.common.download" classes="js-download"/>  
                    </cm:dropdown>
                </span>
                
                <span class="fr">
                    <cm:criteria key=".infrastructure">
                        <cm:criteriaOption classes="js-all-gateways" key=".infrastructure.allGateways"/>
                        <cm:criteriaOption classes="js-all-relays" key=".infrastructure.allRelays"/>
                        <cm:criteriaOption classes="js-all-routes-comprehensive js-all-routes" key=".infrastructure.allPrimaryRoutes"/>
                    </cm:criteria>
                </span>
            </div>
            
            <div id="comprehensive-map" class="map" style="height:75%;"></div>
            <div class="buffered">
                <span class="fl">
                    <cti:msg2 var="searchPlaceholder" key=".meterNumberOrName"/>
                    <i:inline key=".findDevice"/>&nbsp;<input id="findDevice" type="text" placeholder="${searchPlaceholder}" size="22"/>
                    <span class="js-no-results-found error dn"><i:inline key=".searchNoResultsFound"/></span>
                </span>
                <div id="map-tiles" class="fr button-group">
                    <cti:msg2 var="map" key="yukon.web.components.button.map.label"/>
                    <cti:msg2 var="satellite" key="yukon.web.components.button.satellite.label"/>
                    <cti:msg2 var="hybrid" key="yukon.web.components.button.hybrid.label"/>
                    <cti:button renderMode="buttonImage" title="${map}" data-layer="mqosm" icon="icon-map" classes="on"/>
                    <cti:button renderMode="buttonImage" title="${satellite}" data-layer="mqsat" icon="icon-map-sat"/>
                    <cti:button renderMode="buttonImage" title="${hybrid}" data-layer="hybrid" icon="icon-map-hyb"/>
                </div>
                <cti:msg2 var="elevation" key="yukon.web.components.button.elevation.label"/>
                <cti:button renderMode="buttonImage" title="${elevation}" icon="icon-trend-up" classes="fr js-elevation-layer"/>
            </div>
            <div id="legend" class="dn" style="min-height:20px;"></div>
            <%@ include file="/WEB-INF/pages/stars/mapNetwork/neighborsLegend.jsp" %>
            <%@ include file="routeUpdateDetails.jsp" %>
        </div>
    </div>
    
    <cti:includeScript link="OPEN_LAYERS"/>
    <cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
    <cti:includeScript link="/resources/js/common/yukon.mapping.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.map.comprehensive.js"/>
    <div class="dn" id="js-pao-notes-popup"></div>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>

</cti:standardPage>