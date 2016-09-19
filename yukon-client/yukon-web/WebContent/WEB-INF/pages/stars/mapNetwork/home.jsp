<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="mapNetwork">
    
<style>
.map {
  height: 100%;
  width: 100%;
  border: 1px solid #bbb;
  box-shadow: 0px 0px 5px #ddd;
  outline: none;
}
.map .ol-viewport canvas { vertical-align: middle; }
#map-network-container { height: 400px; }
#marker-info {
    background: #fff;
    border: 1px solid #bbb;
    border-radius: 2px;
    padding: 4px;
    position: relative;
    box-shadow: 0px 0px 10px #888;
}

#marker-info:after,#marker-info:before {
    top: 100%;
    left: 50%;
    border: solid transparent;
    content: " ";
    height: 0;
    width: 0;
    position: absolute;
    pointer-events: none;
}

#marker-info:after {
    border-color: rgba(252, 252, 252, 0);
    border-top-color: #fff;
    border-width: 6px;
    margin-left: -6px;
}

#marker-info:before {
    border-color: rgba(153, 153, 153, 0);
    border-top-color: #bbb;
    border-width: 7px;
    margin-left: -7px;
}
</style>
    
    <div id="marker-info" class="well dn"></div>
    
    <c:if test="${not empty geojson}"><cti:toJson id="geojson" object="${geojson}"/></c:if>
    
    <div class="column-10-14 clearfix">
        <div class="column one">
            <c:if test="${!isGateway}">
                <tags:widget bean="meterInformationWidget" deviceId="${deviceId}" container="section"/>
            </c:if>
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
    
    <cti:includeScript link="OPEN_LAYERS"/>
    <cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
    <cti:includeScript link="/resources/js/pages/yukon.map.network.js"/>

</cti:standardPage>