<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="mapNetwork">
    
<style>
#map-network-container { height: 400px; }
#legend {
    text-align:center;
    border:1px solid #ccc;
    padding:2px;
    margin-top:50px;
    font-size:11px;
    box-shadow: 0px 0px 2px #888;
}
#legend hr {
    width:30px;
    display:inline-block;
    margin-bottom:3px;
}
#legend span {
    padding-left:5px;
    padding-right:10px;
}

</style>

    <div id="marker-info" class="well dn">
        <div id="device-info" class="dn"></div>
        <div id="parent-info" class="dn">
            <%@ include file="parentInfo.jsp" %>
        </div>
        <div id="neighbor-info" class="dn">
            <%@ include file="neighborInfo.jsp" %>
        </div>
        <div id="route-info" class="dn">
            <%@ include file="routeInfo.jsp" %>
        </div>
    </div>  
    
    <input type="hidden" class="js-device-status" value="${deviceStatus}"/>
    
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
            
            <div id="legend" class="dn js-legend-neighbors">
                <span><b><i:inline key=".neighborsLineColor"/></b></span><br/>
                <hr class="js-etx-1"/><span><i:inline key=".etxBand1"/></span>
                <hr class="js-etx-2"/><span><i:inline key=".etxBand2"/></span>
                <hr class="js-etx-3"/><span><i:inline key=".etxBand3"/></span><br/>
                <hr class="js-etx-4"/><span><i:inline key=".etxBand4"/></span>
                <hr class="js-etx-5"/><span><i:inline key=".etxBand5"/></span><br/>              
                <span><b><i:inline key=".neighborsLineThickness"/></b></span><br/>
                <hr style="border-top:1px solid;"/><span><i:inline key=".numSamplesZeroToFifty"/></span>
                <hr style="border-top:2px solid;"/><span><i:inline key=".numSamplesFiftyOneToFiveHundred"/></span>
                <hr style="border-top:3px solid;"/><span><i:inline key=".numSamplesOverFiveHundred"/></span>
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