<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="gateways.detail">

<style>
.map {
  height: 300px;
  width: 100%;
  border: 1px solid #bbb;
  box-shadow: 0px 0px 5px #ddd;
  outline: none;
}
.map.fullscreen {
  height: 100%;
}
</style>

<c:if test="${not empty location}"><cti:toJson id="gateway-geojson" object="${location}"/></c:if>

<div id="page-actions" class="dn">
    <cm:dropdownOption icon="icon-pencil" key="components.button.edit.label" data-popup="#gateway-edit-popup"/>
    <cm:dropdownOption icon="icon-cross" key="components.button.delete.label"/>
    <li class="divider"></li>
    <cm:dropdownOption icon="icon-connect" key=".connect"/>
    <cm:dropdownOption icon="icon-disconnect" key=".disconnect"/>
    <li class="divider"></li>
    <cm:dropdownOption icon="icon-table-row-insert" key=".collectData"/>
</div>

<div id="gateway-edit-popup" class="dn" data-title="Edit Gateway" data-url="${gateway.id}/edit" 
    data-width="360" data-dialog data-event="yukon_assets_gateway_save"></div>

<div class="column-12-12 clearfix">
    <div class="column one">
        <tags:sectionContainer2 nameKey="info" styleClass="stacked">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name">GW001</tags:nameValue2>
                <tags:nameValue2 nameKey=".serialNumber">7500000383</tags:nameValue2>
                <tags:nameValue2 nameKey=".hardwareVersion">1000156_06</tags:nameValue2>
                <tags:nameValue2 nameKey=".softwareVersion">R_5_3_0</tags:nameValue2>
                <tags:nameValue2 nameKey=".upperStackVersion">R_5_3_0_0_1</tags:nameValue2>
                <tags:nameValue2 nameKey=".radioVersion">R2.3.0L</tags:nameValue2>
                <tags:nameValue2 nameKey=".releaseVersion">R5.3.0</tags:nameValue2>
                <tags:nameValue2 nameKey=".versionConflicts">
                    <span class="empty-list"><i:inline key="yukon.web.defaults.none"/></span>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".appMode">
                    <span class="error"><i:inline key=".appMode.failsafe"/></span>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        
        <div class="buffered clearfix">
            <cti:button nameKey="edit" icon="icon-pencil" data-popup="#gateway-edit-popup" classes="fr"/>
        </div>
        
    </div>
    <div class="column two nogutter">
        <tags:sectionContainer2 nameKey="location" styleClass="stacked">
            <div id="gateway-location" class="map"></div>
            <div class="buffered">
                <div id="map-tiles" class="fr button-group">
                    <cti:button nameKey="map" data-layer="mqosm" icon="icon-map" classes="on"/>
                    <cti:button nameKey="satellite" data-layer="mqsat" icon="icon-map-sat"/>
                    <cti:button nameKey="hybrid" data-layer="hybrid" icon="icon-map-hyb"/>
                </div>
            </div>
        </tags:sectionContainer2>
        
    </div>
    
</div>
<div class="stacked">
    <tags:sectionContainer2 nameKey="comms" styleClass="stacked">
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".username">
                        <span class="empty-list">admin</span>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".connectionType">TCP/IP</tags:nameValue2>
                    <tags:nameValue2 nameKey=".ipaddress">10.106.46.102 Port: 32030</tags:nameValue2>
                    <tags:nameValue2 nameKey=".radios">
                        <div title="3/31/2014 2:11:07 PM" class="stacked">
                            <div>EkaNet 915 MHz</div>
                            <div>MAC Address: 00:14:08:03:44:D0</div>
                        </div>
                        <div title="5/2/2014 7:49:12 PM">
                            <div>EkaNet 915 MHz</div>
                            <div>MAC Address: FF:FF:FF:FF:FF:FF</div>
                        </div>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="column two nogutter">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".connectionStatus">
                        <span class="state-box green"></span>&nbsp;Connected
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".lastComms">
                        <span class="green">Successful</span>&nbsp;(7/16/2014 9:45:07 AM)
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="action-area">
                    <cti:button nameKey="testConnection" busy="true" icon="icon-server-connect"/>
                </div>
            </div>
        </div>
    </tags:sectionContainer2>
</div>
<div class="stacked">
    <tags:sectionContainer2 nameKey="dataCollection">
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:nameValueContainer2 tableClass="with-form-controls">
                    <tags:nameValue2 nameKey=".dataCompleteness">
                        <div class="dib vatb progress">
                            <c:set var="clazz" value="progress-bar-success"/>
                            <c:if test="${gateway.dataWarning}">
                                <c:set var="clazz" value="progress-bar-warning"/>
                            </c:if>
                            <c:if test="${gateway.dataError}">
                                <c:set var="clazz" value="progress-bar-danger"/>
                            </c:if>
                            <div class="progress-bar ${clazz}" style="width: ${gateway.dataCollection}%"></div>
                        </div>&nbsp;${gateway.dataCollection}%
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="column two nogutter">
                <tags:nameValueContainer2 tableClass="with-form-controls">
                    <tags:nameValue2 nameKey=".schedule" valueClass="full-width">
                        <cti:formatCron value="0 0 */1 * * ? *"/>
                        <cti:button nameKey="edit" icon="icon-pencil" classes="fr"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
        </div>
        <h3><i:inline key=".sequencing"/></h3>
        <div class="scroll-lg">
            <table class="compact-results-table dashed">
                <thead>
                    <tr>
                        <th><i:inline key=".dataType"/></th>
                        <th><i:inline key=".sequenceStart"/></th>
                        <th><i:inline key=".sequenceEnd"/></th>
                        <th><i:inline key=".completeness"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="sequence" items="${sequences}">
                        <tr>
                            <td>${fn:escapeXml(sequence.name)}</td>
                            <td>${sequence.start}</td>
                            <td>${sequence.end}</td>
                            <td>
                                <div class="dib progress">
                                    <c:set var="clazz" value="progress-bar-success"/>
                                    <c:if test="${sequence.warning}">
                                        <c:set var="clazz" value="progress-bar-warning"/>
                                    </c:if>
                                    <c:if test="${sequence.error}">
                                        <c:set var="clazz" value="progress-bar-danger"/>
                                    </c:if>
                                    <div class="progress-bar ${clazz}" style="width: ${sequence.percent}%"></div>
                                </div>&nbsp;${sequence.percent}%
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </tags:sectionContainer2>
</div>

<cti:includeScript link="OPEN_LAYERS"/>
<cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
<cti:includeScript link="/JavaScript/yukon.assets.gateway.details.js"/>
    
</cti:standardPage>