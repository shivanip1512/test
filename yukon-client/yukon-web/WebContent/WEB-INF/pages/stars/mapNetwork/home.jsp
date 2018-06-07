<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="mapNetwork">
    
<style>
#map-network-container { height: 400px; }
</style>

    <%@ include file="/WEB-INF/pages/stars/mapNetwork/mapPopup.jsp" %>
    
    <input type="hidden" class="js-device-status" value="${deviceStatus}"/>
    <input type="hidden" class="js-device-id" value="${deviceId}"/>
    
    <c:if test="${not empty geojson}"><cti:toJson id="geojson" object="${geojson}"/></c:if>
    
    <div class="column-10-14 clearfix">
        <div class="column one">
            <c:choose>
                <c:when test="${isGateway}">
                    <tags:widget bean="gatewayInformationWidget" deviceId="${deviceId}" container="section"/>
                </c:when>
                <c:when test="${isRelay}">
                    <tags:widget bean="relayInformationWidget" deviceId="${deviceId}" container="section"/>
                </c:when>
                <c:when test="${isRfLcr}">
                    <tags:widget bean="lcrInformationWidget" deviceId="${deviceId}" container="section"/>
                </c:when>
                <c:otherwise>
                    <tags:widget bean="meterInformationWidget" deviceId="${deviceId}" container="section"/>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="column two nogutter">
            <cti:msg2 var="locationHelp" key=".location.helpText"/>
            <tags:sectionContainer2 nameKey="location" helpText="${locationHelp}">
                <%@ include file="locationInput.jspf"%>
                <div style="height:400px;">
                    <div id="map-network-container" class="${empty geojson.features ? 'dn' : ''}" style="height:90%;width:100%;">
                        <div id="device-location" class="map" data-has-location="${not empty geojson.features}"></div>
                        <div class="buffered">
                            <c:set var="groupClass" value=""/>
                            <c:if test="${numLayers > 1}">
                                <c:set var="groupClass" value="button-group"/>
                            </c:if>
                            <div class="${groupClass} stacked">
                                <c:if test="${displayNeighborsLayer}">
                                    <tags:check name="neighbors" key=".neighbors" classes="js-neighbor-data" />                            
                                </c:if>
                                <c:if test="${displayParentNodeLayer}">
                                    <tags:check name="parent" key=".parent" classes="js-parent-node" />
                                </c:if>
                                <c:if test="${displayPrimaryRouteLayer}">
                                    <tags:check name="primary" key=".primary" classes="js-primary-route" />
                                </c:if>
                                <c:if test="${displayNearbyLayer}">
                                    <span class="fl" style="text-transform:capitalize">
                                        <i:inline key=".distance.miles"/>:&nbsp;
                                        <select id="miles" class="js-miles">
                                            <option>0.25</option>
                                            <option>0.5</option>
                                            <option selected="selected">1</option>
                                            <option>5</option>
                                            <option>10</option>
                                        </select>
                                    </span>
                                    <tags:check name="nearby" key=".nearby" classes="js-nearby" />
                                </c:if>
                            </div>
                            <div id="map-tiles" class="fr button-group">
                                <cti:button nameKey="map" data-layer="mqosm" icon="icon-map" classes="on"/>
                                <cti:button nameKey="satellite" data-layer="mqsat" icon="icon-map-sat"/>
                                <cti:button nameKey="hybrid" data-layer="hybrid" icon="icon-map-hyb"/>
                            </div>
                        </div>
                    </div>
                </div>
                
                <%@ include file="/WEB-INF/pages/stars/mapNetwork/neighborsLegend.jsp" %>

            </tags:sectionContainer2>

        </div>   
    </div>
    
    <div id="gateway-templates" class="dn"><cti:toJson object="${text}" id="gateway-text"/></div>
    
    <cti:includeScript link="OPEN_LAYERS"/>
    <cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
    <cti:includeScript link="/resources/js/common/yukon.mapping.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.map.network.js"/>
    <cti:includeScript link="/resources/js/widgets/yukon.widget.gateway.info.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.shared.js"/>
    
</cti:standardPage>