<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="map">
<style>
.map .ol-viewport canvas {
     min-height: 600px; 
}

.ol-scale-line,.ol-mouse-position {
    background: rgba(0, 60, 136, 0.3);
    border-radius: 2px;
    padding: 2px;
    margin-right: 2px;
    position: inherit;
}

.ol-mouse-position {
    color: #eee;
    padding: 2px 5px;
    width: 150px;
}

#filter-form .chosen-results {
    max-height: 95px;
}

</style>

    <input id="filtered-msg" type="hidden" value="<cti:msg2 key=".filtered"/>">
    <input id="unfiltered-msg" type="hidden" value="<cti:msg2 key=".filter.label"/>">
    <%@ include file="/WEB-INF/pages/stars/mapNetwork/mapPopup.jsp" %>
    <c:if test="${not empty mappingColors}"><cti:toJson id="mappingColors" object="${mappingColors}"/></c:if>
    
    <div id="map-container" style="height:100%;width:100%;background:white;">
        <c:if test="${empty dynamic}">
            <div class="fr">
                <cti:button id="filter-btn" icon="icon-filter" nameKey="filter" data-popup="#map-popup"/>
                <cti:button id="no-filter-btn" icon="icon-cross disabled cp" classes="right dn" renderMode="buttonImage"/>
            </div>
            
            <div id="map-popup" data-dialog class="dn" data-title="<cti:msg2 key=".filter.title"/>" data-event="yukon.map.filter"
                data-width="500" data-height="250">
                <cti:url value="/tools/map/filter" var="filterUrl"/>
                <form:form modelAttribute="filter" id="filter-form" action="${filterUrl}">
                    <cti:csrfToken/>
                    <cti:deviceCollection deviceCollection="${deviceCollection}"/>
                    <form:hidden path="tempDeviceGroupName"/>
                    <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                        <cti:msg2 key=".chooseAttribute" var="chooseAttribute"/>
                        <tags:selectNameValue nameKey=".attribute" path="attribute" items="${attributes}" itemLabel="message"
                            itemValue="key" groupItems="true" id="attribute-select" defaultItemLabel="${chooseAttribute}"
                            defaultItemValue="-1"/>
                        <tags:nameValue2 nameKey=".states" valueClass="full-width">
                            <div id="waiting-for-states" class="dn">
                                <cti:icon icon="icon-spinner" style="margin-top:5px;"/>
                                <i:inline key=".retrievingStates"/>
                            </div>
                            <div id="no-states-for-attribute" class="dn">
                                <cti:icon icon="icon-error" style="margin-top:5px;"/>
                                <i:inline key=".noStatesForAttribute"/>
                            </div>
                            <div id="filter-states"></div>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </form:form>
            </div>
            <div id="state-group-template" class="dn">
                <input type="hidden" name="groups[?].id"> <select name="groups[?].state"></select>
            </div>
        </c:if>

        <div class="column-18-6 clearfix stacked">
            <div class="column one">

                <c:set var="deviceCollectionKey" value="${!empty monitorId ? 'yukon.web.modules.tools.map.monitorDevices' : ''}" />
                <tags:selectedDevices deviceCollection="${deviceCollection}" id="device-collection" labelKey="${deviceCollectionKey}" />
                <cti:url var="downloadUrl" value="/tools/map/locations/download">
                    <cti:mapParam value="${deviceCollection.collectionParameters}" />
                </cti:url>
                <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                    <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                        <cti:param name="${cp.key}" value="${cp.value}" />
                    </c:forEach>
                </cti:url>
                <cm:dropdown icon="icon-cog">
                    <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go" newTab="true" />
                    <cm:dropdownOption icon="icon-csv" key="yukon.common.download" href="${downloadUrl}" />
                </cm:dropdown>
                <c:if test="${!empty violationsCollection}">
                    <tags:selectedDevices deviceCollection="${violationsCollection}" id="violation-collection"
                        labelKey="yukon.web.modules.tools.map.violationDevices" badgeClasses="badge-warning js-violations" />
                    <cti:url var="downloadUrl" value="/tools/map/locations/download">
                        <cti:mapParam value="${violationsCollection.collectionParameters}" />
                    </cti:url>
                    <cti:url var="violationActionsUrl" value="/bulk/collectionActions">
                        <c:forEach items="${violationsCollection.collectionParameters}" var="cp">
                            <cti:param name="${cp.key}" value="${cp.value}" />
                        </c:forEach>
                    </cti:url>
                    <cm:dropdown icon="icon-cog">
                        <cm:dropdownOption key=".collectionActions" href="${violationActionsUrl}" icon="icon-cog-go" newTab="true" />
                        <cm:dropdownOption icon="icon-csv" key="yukon.common.download" href="${downloadUrl}" />
                    </cm:dropdown>
                </c:if>
                <c:if test="${!empty filteredCollection}">
                    <div class="js-filtered-devices PT10 dn">
                        <tags:selectedDevices deviceCollection="${filteredCollection}" id="filtered-collection"
                            labelKey="yukon.web.modules.tools.map.filter.filteredOnly" badgeClasses="badge-warning js-filtered" />
                        <cti:url var="downloadUrl" value="/tools/map/locations/download">
                            <cti:mapParam value="${filteredCollection.collectionParameters}" />
                        </cti:url>
                        <cti:url var="filteredActionsUrl" value="/bulk/collectionActions">
                            <c:forEach items="${filteredCollection.collectionParameters}" var="cp">
                                <cti:param name="${cp.key}" value="${cp.value}" />
                            </c:forEach>
                        </cti:url>
                        <cm:dropdown icon="icon-cog">
                            <cm:dropdownOption key=".collectionActions" href="${filteredActionsUrl}" icon="icon-cog-go" newTab="true" />
                            <cm:dropdownOption icon="icon-csv" key="yukon.common.download" href="${downloadUrl}" />
                        </cm:dropdown>
                    </div>
                </c:if>
                <c:if test="${!empty colorCollections}">
                    <table class="js-color-collections compact-results-table no-stripes">
                        <tr>
                            <c:forEach var="mapCollection" items="${colorCollections}" varStatus="status">
                                <c:if test="${status.index == 2}">
                        </tr>
                        <tr>
                            </c:if>
                            <td class="PB0"><c:set var="collection" value="${mapCollection.deviceCollection}" /> <tags:selectedDevices
                                    deviceCollection="${collection}" labelKey="${mapCollection.labelKey}"
                                    badgeColor="${mapCollection.color}" /> <c:if test="${collection.deviceCount > 0}">
                                    <cti:url var="downloadUrl" value="/tools/map/locations/download">
                                        <cti:mapParam value="${collection.collectionParameters}" />
                                    </cti:url>
                                    <cti:url var="filteredActionsUrl" value="/bulk/collectionActions">
                                        <c:forEach items="${collection.collectionParameters}" var="cp">
                                            <cti:param name="${cp.key}" value="${cp.value}" />
                                        </c:forEach>
                                    </cti:url>
                                    <cm:dropdown icon="icon-cog">
                                        <cm:dropdownOption key=".collectionActions" href="${filteredActionsUrl}" icon="icon-cog-go"
                                            newTab="true" />
                                        <cm:dropdownOption icon="icon-csv" key="yukon.common.download" href="${downloadUrl}" />
                                    </cm:dropdown>
                                </c:if></td>
                            </c:forEach>
                        </tr>
                    </table>
                </c:if>

                <div class="dn js-status-loading">
                    <cti:icon icon="icon-spinner" />
                    <i:inline key=".status.loading" />
                </div>
            </div>
            <div class="column two nogutter">
                <c:if test="${!empty monitorId}">
                    <input type="hidden" id="monitorId" value="${monitorId}" />
                    <cti:url value="/tools/map/locations/${monitorType}/${monitorId}" var="monitorLocationsUrl" />
                    <input id="monitorLocations" type="hidden" value="${fn:escapeXml(monitorLocationsUrl)}">
                    <span class="fr"> <i:inline key=".filter.label" />: <c:set var="violationsSelected"
                            value="${violationsOnly ? 'selected=selected' : ''}" /> <select id="violationsSelect">
                            <option value="false"><i:inline key=".filter.allDevices" /></option>
                            <option value="true" ${violationsSelected}><i:inline key=".filter.violationsOnly" /></option>
                    </select>
                    </span>
                </c:if>
                <c:if test="${!empty filter}">
                    <span class="fr js-filtered-devices PT10 dn"> <i:inline key=".filter.display.label" />: <select
                        id="devicesFilter">
                            <option value="false"><i:inline key=".filter.allDevices" /></option>
                            <option value="true" selected="selected"><i:inline key=".filter.filteredOnly" /></option>
                    </select>
                    </span>
                </c:if>
                <div class="dn js-status-retrieving">
                    <cti:icon icon="icon-spinner" />
                    <i:inline key=".status.retrieving" />
                </div>
                <div class="dn js-status-filtering">
                    <cti:icon icon="icon-spinner" />
                    <i:inline key=".status.filtering" />
                </div>
            </div>

        </div>
        <div id="map" class="map clearfix js-focus" <c:if test="${dynamic}">data-dynamic</c:if> tabindex="0"></div>
        <div class="buffered">
            <div id="mouse-position" class="fl detail"></div>
            <div id="scale-line" class="fl"></div>
            
            <div id="map-tiles" class="fr button-group">
                <cti:msg2 var="map" key="yukon.web.components.button.map.label"/>
                <cti:msg2 var="satellite" key="yukon.web.components.button.satellite.label"/>
                <cti:msg2 var="hybrid" key="yukon.web.components.button.hybrid.label"/>
                <cti:button renderMode="buttonImage" title="${map}" data-layer="mqosm" icon="icon-map" classes="on"/>
                <cti:button renderMode="buttonImage" title="${satellite}" data-layer="mqsat" icon="icon-map-sat"/>
                <cti:button renderMode="buttonImage" title="${hybrid}" data-layer="hybrid" icon="icon-map-hyb"/>
            </div>
            
            <c:if test="${dynamic}">
                <div id="map-updater" class="button-group button-group-toggle fr">
                    <cti:button nameKey="on" classes="on yes"/>
                    <cti:button nameKey="off" classes="no"/>
                </div>
                <span class="fr form-control"><i:inline key=".liveUpdate"/></span>
            </c:if>
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
    
    <%@ include file="/WEB-INF/pages/stars/mapNetwork/neighborsLegend.jsp" %>

    <cti:includeScript link="OPEN_LAYERS"/>
    <cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
    <cti:includeScript link="/resources/js/common/yukon.mapping.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.tools.map.js"/>
    <div class="dn" id="js-pao-notes-popup"></div>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
    
</cti:standardPage>