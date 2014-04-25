<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="tools" page="map">

<style>
.map {
  height: 600px;
  width: 100%;
  border: 1px solid #bbb;
  box-shadow: 0px 0px 5px #ddd;
}
.ol-scale-line, .ol-mouse-position {
    background: rgba(0,60,136,0.3);
    border-radius: 2px;
    padding: 2px;
    margin-right: 2px;
    position: inherit;
}
.ol-mouse-position {color: #eee;padding: 2px 5px;width: 142px;}
#filter-form .chosen-results {max-height: 100px;}
#marker-info {
    background: #fff;
    border: 1px solid #bbb;
    border-radius: 2px;
    padding: 4px;
    position: relative;
    box-shadow: 0px 0px 10px #888;
}
#marker-info:after, #marker-info:before {
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

    <input id="filtered-msg" type="hidden" value="<cti:msg2 key=".filtered"/>">
    <input id="unfiltered-msg" type="hidden" value="<cti:msg2 key=".filter.label"/>">
    <div id="marker-info" class="well dn"></div>

    <div id="page-buttons">
        <cti:button id="filter-btn" icon="icon-filter" nameKey="filter" popup="#map-popup"/>
        <cti:button id="no-filter-btn" icon="icon-cross disabled cp" classes="right dn" renderMode="buttonImage"/>
    </div>
    <div id="map-popup" dialog class="dn" data-title="<cti:msg2 key=".filter.title"/>" data-event="yukon.map.filter" data-width="500" data-height="250">
        <cti:url value="/tools/map/filter" var="filterUrl"/>
        <form:form commandName="filter" id="filter-form" action="${filterUrl}">
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                <cti:msg2 key=".chooseAttribute" var="chooseAttribute"/>
                <tags:selectNameValue nameKey=".attribute"
                                      path="attribute"
                                      items="${attributes}"
                                      itemLabel="message"
                                      itemValue="key"
                                      groupItems="true"
                                      id="attribute-select"
                                      defaultItemLabel="${chooseAttribute}"
                                      defaultItemValue="-1"/>
                <tags:nameValue2 nameKey=".states" valueClass="full-width">
                    <div id="waiting-for-states" class="dn"><cti:icon icon="icon-spinner" style="margin-top:5px;"/><i:inline key=".retrievingStates"/></div>
                    <div id="no-states-for-attribute" class="dn"><cti:icon icon="icon-error" style="margin-top:5px;"/><i:inline key=".noStatesForAttribute"/></div>
                    <div id="filter-states"></div>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </form:form>
    </div>
    <div id="state-group-template" class="dn">
        <input type="hidden" name="groups[?].id">
        <select name="groups[?].state"></select>
    </div>
    
    <div class="stacked clearfix">
        <tags:selectedDevices deviceCollection="${deviceCollection}" id="selectedDevices"/>
    </div>

    <div id="map" class="clearfix map f-focus" tabindex="0"></div>
    <div class="buffered">
        <div id="mouse-position" class="fl detail"></div>
        <div id="scale-line" class="fl"></div>
        <div id="map-tiles" class="fr button-group">
            <cti:button nameKey="map" data-layer="mqosm" icon="icon-map" classes="on"/>
            <cti:button nameKey="satellite" data-layer="mqsat" icon="icon-map-sat"/>
            <cti:button nameKey="hybrid" data-layer="hybrid" icon="icon-map-hyb"/>
        </div>
    </div>
    
    <cti:url value="/tools/map/locations" var="locationsUrl">
        <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
            <cti:param name="${cp.key}" value="${cp.value}"/>
        </c:forEach>
    </cti:url>
    <input id="locations" type="hidden" value="${fn:escapeXml(locationsUrl)}">
    
    <cti:url value="/tools/map/filter/state-groups" var="stateGroupBaseUrl">
        <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
            <cti:param name="${cp.key}" value="${cp.value}"/>
        </c:forEach>
    </cti:url>
    <input id="state-group-base-url" type="hidden" value="${fn:escapeXml(stateGroupBaseUrl)}">

    <cti:includeScript link="OPEN_LAYERS"/>
    <cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
    <cti:includeScript link="/JavaScript/yukon.tools.map.js"/>
    
</cti:standardPage>