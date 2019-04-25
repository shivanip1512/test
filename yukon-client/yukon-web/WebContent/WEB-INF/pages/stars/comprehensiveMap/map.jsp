<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="operator" page="comprehensiveMap">

    <%@ include file="/WEB-INF/pages/stars/mapNetwork/mapPopup.jsp" %>

    <cti:msg2 var="tooManyGateways" key=".tooManyGateways"/>
    <input type="hidden" id="tooManyGatewaysError" value="${tooManyGateways}"/>
    
    <div style="height:600px;">
        <div id="comprehensive-map-container" style="height:100%;width:100%;background:white;">
            <div class="filter-section">
                <hr>
                <form:form id="filter-form" modelAttribute="filter">
                    <i:inline key="yukon.common.filterBy"/>
        
                    <cti:msg2 var="gatewayPlaceholder" key=".selectGateways"/>
                    <tags:selectWithItems items="${gateways}" path="selectedGatewayIds" itemLabel="name" itemValue="id"
                        inputClass="js-chosen" dataPlaceholder="${gatewayPlaceholder}"/>
                    
                    <div style="padding-top:5px">
                        <i:inline key=".colorCodeBy"/>
                        <tags:selectWithItems items="${colorCodeByOptions}" path="colorCodeBy"/>
                        
                        <cti:button nameKey="filter" classes="js-filter-map primary action fr vab"/>
                    </div>
                    
                </form:form>
                <hr>
            </div>
            
            <div id="comprehensive-map" class="map"></div>
            <div class="buffered">
                <div id="map-tiles" class="fr button-group">
                    <cti:msg2 var="map" key="yukon.web.components.button.map.label"/>
                    <cti:msg2 var="satellite" key="yukon.web.components.button.satellite.label"/>
                    <cti:msg2 var="hybrid" key="yukon.web.components.button.hybrid.label"/>
                    <cti:button renderMode="buttonImage" title="${map}" data-layer="mqosm" icon="icon-map" classes="on"/>
                    <cti:button renderMode="buttonImage" title="${satellite}" data-layer="mqsat" icon="icon-map-sat"/>
                    <cti:button renderMode="buttonImage" title="${hybrid}" data-layer="hybrid" icon="icon-map-hyb"/>
                </div>
            </div>
        </div>
    </div>
    <div id="legend" class="dn" style="margin-top:150px;width:40%;"></div>

    
    <cti:includeScript link="OPEN_LAYERS"/>
    <cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
    <cti:includeScript link="/resources/js/common/yukon.mapping.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.map.network.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.map.comprehensive.js"/>

</cti:standardPage>