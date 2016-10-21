<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="mapNetwork">
    
<style>
#map-network-container { height: 400px; }
</style>

    <div id="marker-info" class="well dn">
        <div id="device-info" class="dn"></div>
        <div id="parent-info" class="dn">
            <%@ include file="parentInfo.jsp" %>
        </div>
        <div id="neighbor-info" class="dn">
            <%@ include file="neighborInfo.jsp" %>
        </div>
    </div>  
    
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
                <c:otherwise>
                    <tags:widget bean="meterInformationWidget" deviceId="${deviceId}" container="section"/>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="column two nogutter">
        <tags:sectionContainer2 nameKey="location">
            <div id="map-network-container" class="${empty geojson.features ? 'dn' : ''}">
                <div id="device-location" class="map" data-has-location="${not empty geojson.features}"></div>
                <div class="buffered">
                    <div class="button-group stacked">
                        <c:if test="${displayNeighborsLayer}">
                            <tags:check name="neighbors" key=".neighbors" classes="js-neighbor-data" />                            
                        </c:if>
                        <tags:check name="primary" key=".primary" classes="js-primary-route" />
                        <c:if test="${displayParentNodeLayer}">
                            <tags:check name="parent" key=".parent" classes="js-parent-node" />
                        </c:if>
                    </div>
                    <div id="map-tiles" class="fr button-group">
                        <cti:button nameKey="map" data-layer="mqosm" icon="icon-map" classes="on"/>
                        <cti:button nameKey="satellite" data-layer="mqsat" icon="icon-map-sat"/>
                        <cti:button nameKey="hybrid" data-layer="hybrid" icon="icon-map-hyb"/>
                    </div>
                </div>
            </div>
            </tags:sectionContainer2>
        </div>        
    </div>
    
    <div id="gateway-templates" class="dn"><cti:toJson object="${text}" id="gateway-text"/></div>
    
    
    <cti:includeScript link="OPEN_LAYERS"/>
    <cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
    <cti:includeScript link="/resources/js/pages/yukon.map.network.js"/>
    <cti:includeScript link="/resources/js/widgets/yukon.widget.gateway.info.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.shared.js"/>
    

</cti:standardPage>