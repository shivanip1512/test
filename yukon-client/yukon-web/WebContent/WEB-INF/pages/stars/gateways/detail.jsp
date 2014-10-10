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

<c:if test="${not empty gateway.location}"><cti:toJson id="gateway-geojson" object="${gateway.location}"/></c:if>

<div id="page-actions" class="dn">
    <cm:dropdownOption icon="icon-pencil" key="components.button.edit.label" data-popup="#gateway-edit-popup"/>
    <cm:dropdownOption icon="icon-cross" key="components.button.delete.label"/>
    <li class="divider"></li>
    <cm:dropdownOption icon="icon-connect" key=".connect"/>
    <cm:dropdownOption icon="icon-disconnect" key=".disconnect"/>
    <li class="divider"></li>
    <cm:dropdownOption icon="icon-table-row-insert" key=".collectData"/>
</div>

<div id="gateway-edit-popup" class="dn" data-title="Edit Gateway" data-url="${gateway.paoIdentifier.paoId}/edit" 
    data-width="360" data-dialog data-event="yukon_assets_gateway_save"></div>

<div class="column-12-12 clearfix">
    <div class="column one">
        <tags:sectionContainer2 nameKey="info" styleClass="stacked">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name">${fn:escapeXml(gateway.data.name)}</tags:nameValue2>
                <tags:nameValue2 nameKey=".serialNumber">${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</tags:nameValue2>
                <tags:nameValue2 nameKey=".hardwareVersion">${gateway.data.hardwareVersion}</tags:nameValue2>
                <tags:nameValue2 nameKey=".softwareVersion">${gateway.data.softwareVersion}</tags:nameValue2>
                <tags:nameValue2 nameKey=".upperStackVersion">${gateway.data.upperStackVersion}</tags:nameValue2>
                <tags:nameValue2 nameKey=".radioVersion">${gateway.data.radioVersion }</tags:nameValue2>
                <tags:nameValue2 nameKey=".releaseVersion">${gateway.data.releaseVersion}</tags:nameValue2>
                <tags:nameValue2 nameKey=".versionConflicts">
                    <c:if test="${empty gateway.data.versionConflicts}">
                        <span class="empty-list"><i:inline key="yukon.web.defaults.none"/></span>
                    </c:if>
                    <c:if test="${not empty gateway.data.versionConflicts}">
                        <div class="stacked">
                            <c:forEach var="conflict" items="${gateway.data.versionConflicts}">
                                <div><i:inline key=".conflictType.${conflict}"/></div>
                            </c:forEach>
                        </div>
                    </c:if>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".appMode">
                    <c:set var="clazz" value="green"/>
                    <c:if test="${gateway.appModeNonNormal}">
                        <c:set var="clazz" value="error"/>
                    </c:if>
                    <span class="${clazz}"><i:inline key=".appMode.${gateway.data.mode}"/></span>
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
                    <tags:nameValue2 nameKey=".user">
                        <c:if test="${empty gateway.data.user}">
                            <span class="empty-list"><i:inline key="yukon.web.defaults.none"/></span>
                        </c:if>
                        <c:if test="${not empty gateway.data.user}">
                            <span class="empty-list">${fn:escapeXml(gateway.data.user.username)}</span>
                        </c:if>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".admin">
                        <c:if test="${empty gateway.data.admin}">
                            <span class="empty-list"><i:inline key="yukon.web.defaults.none"/></span>
                        </c:if>
                        <c:if test="${not empty gateway.data.admin}">
                            <span class="empty-list">${fn:escapeXml(gateway.data.admin.username)}</span>
                        </c:if>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".superAdmin">
                        <c:if test="${empty gateway.data.superAdmin}">
                            <span class="empty-list"><i:inline key="yukon.web.defaults.none"/></span>
                        </c:if>
                        <c:if test="${not empty gateway.data.superAdmin}">
                            <span class="empty-list">${fn:escapeXml(gateway.data.superAdmin.username)}</span>
                        </c:if>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".connectionType">
                        <i:inline key=".connectionType.${gateway.data.connectionType}"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".ipaddress">
                        ${fn:escapeXml(gateway.data.ipAddress)} <i:inline key=".port" arguments="${gateway.data.port}"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".radios">
                        <c:forEach var="radio" items="${gateway.data.radios}">
                            <div title="<cti:formatDate type="DATEHM" value="${radio.timestamp}"/>" class="stacked">
                                <div><i:inline key=".radioType.${radio.type}"/></div>
                                <div><i:inline key=".macAddress" arguments="${radio.macAddress}"/></div>
                            </div>
                        </c:forEach>
                        </div>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="column two nogutter">
                <tags:nameValueContainer2>
    	            <tags:nameValue2 nameKey=".connectionStatus">
    	                <span class="state-box green"></span>
                        <i:inline key=".connectionStatus.${gateway.data.connectionStatus}"/>
    	            </tags:nameValue2>
    	            <tags:nameValue2 nameKey=".lastComms">
    	                <c:set var="clazz" value="green"/>
                        <c:if test="${gateway.lastCommFailed}">
                            <c:set var="clazz" value="red"/>
                        </c:if>
                        <c:if test="${gateway.lastCommMissed}">
                            <c:set var="clazz" value="orange"/>
                        </c:if>
                        <c:if test="${gateway.lastCommUnknown}">
                            <c:set var="clazz" value="gray"/>
                        </c:if>
                        <span class="${clazz}"><i:inline key=".lastCommStatus.${gateway.data.lastCommStatus}"/></span>
                        (<cti:formatDate type="DATEHM" value="${gateway.data.lastCommStatusTimestamp}"/>)
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
                            <c:if test="${gateway.totalCompletionLevelWarning}">
                                <c:set var="clazz" value="progress-bar-warning"/>
                            </c:if>
                            <c:if test="${gateway.totalCompletionLevelDanger}">
                                <c:set var="clazz" value="progress-bar-danger"/>
                            </c:if>
                            <div class="progress-bar ${clazz}" style="width: ${gateway.totalCompletionPercentage}%"></div>
                        </div>&nbsp;${gateway.totalCompletionPercentage}%
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="column two nogutter">
                <tags:nameValueContainer2 tableClass="with-form-controls">
                    <tags:nameValue2 nameKey=".collectionSchedule" valueClass="full-width">
                        <cti:formatCron value="${gateway.data.collectionSchedule}"/>
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
                    <c:forEach var="sequence" items="${gateway.data.sequences}">
                        <c:forEach var="block" items="${sequence.blocks}" varStatus="status">
                            <tr>
                                <c:if test="${status.first}">
                                    <td><i:inline key=".sequenceType.${sequence.type}"/></td>
                                </c:if>
                                <c:if test="${not status.first}">
                                    <td></td>
                                </c:if>
                                <td>${block.start}</td>
                                <td>${block.end}</td>
                                <c:if test="${status.first}">
                                    <td>
                                        <div class="dib progress">
                                            <c:set var="clazz" value="progress-bar-success"/>
                                            <c:if test="${sequence.completionPercentage < 90 and sequence.completionPercentage >= 75}">
                                                <c:set var="clazz" value="progress-bar-warning"/>
                                            </c:if>
                                            <c:if test="${sequence.completionPercentage <= 75}">
                                                <c:set var="clazz" value="progress-bar-danger"/>
                                            </c:if>
                                            <div class="progress-bar ${clazz}" style="width: ${sequence.completionPercentage}%"></div>
                                        </div>&nbsp;${sequence.completionPercentage}%
                                    </td>
                                </c:if>
                                <c:if test="${not status.first}">
                                    <td></td>
                                </c:if>
                            </tr>
                        </c:forEach>
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