<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<style>
.map .ol-viewport canvas {
     min-height: 400px; 
}
</style>

<%@ include file="/WEB-INF/pages/stars/mapNetwork/mapPopup.jsp" %>
<c:if test="${not empty geojson}"><cti:toJson id="geojson" object="${geojson}"/></c:if>

<cti:msg2 var="locationHelp" key=".mapNetwork.location.helpText"/>
<tags:sectionContainer2 nameKey="location" helpText="${locationHelp}">
    <div style="height:500px">
        <div id="map-network-container" style="height:100%;width:100%;background:white;">
            <%@ include file="locationInput.jspf"%>
            <c:if test="${!empty coordinates.latitude && displayInfrastructure}">
                <span class="fr js-infrastructure">
                    <c:set var="labelWidth" value="${!empty infrastructureLabelWidth ? infrastructureLabelWidth : '150px'}"/>
                    <cm:criteria key="yukon.web.modules.operator.comprehensiveMap.infrastructure" labelWidth="${labelWidth}">
                        <cm:criteriaOption classes="js-all-gateways" key="yukon.web.modules.operator.comprehensiveMap.infrastructure.allGateways"/>
                        <cm:criteriaOption classes="js-all-relays" key="yukon.web.modules.operator.comprehensiveMap.infrastructure.allRelays"/>
                        <cm:criteriaOption classes="js-all-routes js-all-routes-map-network" key="yukon.web.modules.operator.comprehensiveMap.infrastructure.allPrimaryRoutes"/>
                    </cm:criteria>
                </span>
            </c:if>
            <div class="${empty geojson.features ? 'dn' : ''}" style="height:85%;">
                <div id="device-location" class="map" data-has-location="${not empty geojson.features}"></div>
                <div class="buffered">
                    <c:set var="groupClass" value=""/>
                    <c:if test="${numLayers > 1}">
                        <c:set var="groupClass" value="button-group"/>
                    </c:if>
                    <div class="${groupClass} stacked">
                        <c:if test="${displayNearbyLayer}">
                            <span class="fl" style="text-transform:capitalize">
                                <i:inline key=".mapNetwork.distance.miles"/>:&nbsp;
                                <select id="miles" class="js-miles">
                                    <option>0.25</option>
                                    <option>0.5</option>
                                    <option selected="selected">1</option>
                                    <option>5</option>
                                    <option>10</option>
                                </select>
                            </span>
                            <tags:check name="nearby" key=".mapNetwork.nearby" classes="js-nearby bl1" />
                        </c:if>
                        <c:if test="${displayNeighborsLayer}">
                            <tags:check name="neighbors" key=".mapNetwork.neighbors" classes="js-neighbor-data" />                            
                        </c:if>
                        <c:if test="${displayParentNodeLayer}">
                            <tags:check name="parent" key=".mapNetwork.parent" classes="js-parent-node" />
                        </c:if>
                        <c:if test="${displayPrimaryRouteLayer}">
                            <tags:check name="primary" key=".mapNetwork.primary" classes="js-primary-route" />
                        </c:if>
                    </div>
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
            </div>
            <div style="padding-top:40px">
                <%@ include file="/WEB-INF/pages/stars/comprehensiveMap/routeUpdateDetails.jsp" %>
            </div>
        </div>
    </div>
    <div style="margin-top:50px;">
        <%@ include file="/WEB-INF/pages/stars/mapNetwork/neighborsLegend.jsp" %>
    </div>

</tags:sectionContainer2>

<div id="gateway-templates" class="dn"><cti:toJson object="${text}" id="gateway-text"/></div>