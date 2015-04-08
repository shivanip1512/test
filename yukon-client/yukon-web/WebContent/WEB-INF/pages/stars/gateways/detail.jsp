<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="operator" page="gateways.detail">

<style>
.map {
  height: 100%;
  width: 100%;
  border: 1px solid #bbb;
  box-shadow: 0px 0px 5px #ddd;
  outline: none;
}
.map .ol-viewport canvas { vertical-align: middle; }
#gateway-location-container { height: 300px; }
</style>

<c:if test="${not empty geojson}"><cti:toJson id="gateway-geojson" object="${geojson}"/></c:if>

<d:confirm on="#gateway-delete" nameKey="delete.confirm"/>

<div id="page-buttons" class="dn">
    <cm:dropdown type="button" data-name="${fn:escapeXml(gateway.name)}" data-id="${gateway.paoIdentifier.paoId}">
        <cm:dropdownOption icon="icon-table-row-insert" key=".collectData" classes="js-gw-collect-data"/>
        <cti:checkRolesAndProperties value="INFRASTRUCTURE_ADMIN">
            <li class="divider"></li>
            <cm:dropdownOption icon="icon-connect" key=".connect" classes="js-gw-connect"/>
            <cm:dropdownOption icon="icon-disconnect" key=".disconnect" classes="js-gw-disconnect"/>
        </cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="INFRASTRUCTURE_CREATE_AND_UPDATE, INFRASTRUCTURE_DELETE">
            <li class="divider"></li>
        </cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="INFRASTRUCTURE_CREATE_AND_UPDATE">
            <cm:dropdownOption icon="icon-pencil" key="components.button.edit.label" data-popup="#gateway-edit-popup"/>
        </cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="INFRASTRUCTURE_DELETE">
            <cm:dropdownOption icon="icon-cross" key="components.button.delete.label" id="gateway-delete"
                data-ok-event="yukon:assets:gateways:delete"/>
        </cti:checkRolesAndProperties>
    </cm:dropdown>
</div>

<div id="gateway-edit-popup" data-dialog class="dn" data-title="<cti:msg2 key=".edit.title"/>"
        data-url="${gateway.paoIdentifier.paoId}/edit"
        data-id="${gateway.paoIdentifier.paoId}"
        data-width="580" 
        data-event="yukon:assets:gateway:save" 
        data-load-event="yukon:assets:gateway:edit:load" 
        data-ok-text="<cti:msg2 key="components.button.save.label"/>"></div>
    
<div id="gateway-collect-data-popup" class="dn"></div>

<cti:url var="locationUrl" value="/stars/gateways/${gateway.paoIdentifier.paoId}/location/options"/>
<div id="gateway-location-popup" class="dn" data-dialog 
        data-event="yukon:assets:gateway:location:save"
        data-title="<cti:msg2 key=".location.set"/>"
        data-url="${locationUrl}"></div>

<cti:checkRolesAndProperties value="INFRASTRUCTURE_ADMIN">
    <cti:url var="scheduleUrl" value="/stars/gateways/${gateway.paoIdentifier.paoId}/schedule/options"/>
    <div id="gateway-schedule-popup" data-dialog class="dn" 
            data-title="<cti:msg2 key=".schedule"/>"
            data-event="yukon:assets:gateway:schedule:save"
            data-ok-text="<cti:msg2 key="components.button.save.label"/>"
            data-width="460" data-height="264"
            data-url="${scheduleUrl}"></div>
</cti:checkRolesAndProperties>

<c:set var="data" value="${gateway.data}"/>
<div class="column-12-12 clearfix">

    <div class="column one">
        <tags:sectionContainer2 nameKey="info" styleClass="stacked" id="gw-info">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name" valueClass="js-gw-name">${fn:escapeXml(gateway.name)}</tags:nameValue2>
                <tags:nameValue2 nameKey=".serialNumber" valueClass="js-gw-sn">${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</tags:nameValue2>
                <tags:nameValue2 nameKey=".hardwareVersion" valueClass="js-gw-hw-version">${data.hardwareVersion}</tags:nameValue2>
                <tags:nameValue2 nameKey=".softwareVersion" valueClass="js-gw-sw-version">${data.softwareVersion}</tags:nameValue2>
                <tags:nameValue2 nameKey=".upperStackVersion" valueClass="js-gw-us-version">${data.upperStackVersion}</tags:nameValue2>
                <tags:nameValue2 nameKey=".radioVersion" valueClass="js-gw-radio-version">${data.radioVersion }</tags:nameValue2>
                <tags:nameValue2 nameKey=".releaseVersion" valueClass="js-gw-release-version">${data.releaseVersion}</tags:nameValue2>
                <tags:nameValue2 nameKey=".versionConflicts">
                    <c:if test="${empty data.versionConflicts}">
                        <span class="empty-list js-gw-version-conflicts"><i:inline key="yukon.web.defaults.none"/></span>
                    </c:if>
                    <c:if test="${not empty data.versionConflicts}">
                        <span class="error js-gw-version-conflicts">
                            <c:forEach var="conflict" items="${data.versionConflicts}">
                                <i:inline key=".conflictType.${conflict}"/>,&nbsp;
                            </c:forEach>
                        </span>
                    </c:if>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".appMode">
                    <c:set var="clazz" value="green"/>
                    <c:if test="${gateway.appModeNonNormal}">
                        <c:set var="clazz" value="error"/>
                    </c:if>
                    <span class="${clazz}" valueClass="js-gw-app-mode">
                        <i:inline key=".appMode.${data.mode}"/>
                    </span>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        
        <cti:checkRolesAndProperties value="INFRASTRUCTURE_CREATE_AND_UPDATE">
            <div class="buffered clearfix">
                <cti:button nameKey="edit" icon="icon-pencil" data-popup="#gateway-edit-popup" classes="fr"/>
            </div>
        </cti:checkRolesAndProperties>
    </div>
    
    <div class="column two nogutter">
        <tags:sectionContainer2 nameKey="location" styleClass="stacked">
            <div class="empty-list form-control ${not empty gateway.location ? 'dn' : ''}">
                <i:inline key=".location.none"/>
                <cti:button icon="icon-map-sat" nameKey="location.set" classes="fr M0" 
                    data-popup="#gateway-location-popup"/>
            </div>
            <div id="gateway-location-container" class="${empty gateway.location ? 'dn' : ''}">
                <div id="gateway-location" class="map" data-has-location="${not empty gateway.location}"></div>
                <div class="buffered">
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

<div class="stacked">
    <tags:sectionContainer2 nameKey="comms" styleClass="stacked" id="gw-comm">
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".admin" valueClass="js-gw-admin">
                        <c:if test="${empty data.admin}"><i:inline key="yukon.web.defaults.none"/></c:if>
                        <c:if test="${not empty data.admin}">${fn:escapeXml(data.admin.username)}</c:if>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".superAdmin" valueClass="js-gw-super-admin">
                        <c:if test="${empty data.superAdmin}"><i:inline key="yukon.web.defaults.none"/></c:if>
                        <c:if test="${not empty data.superAdmin}">${fn:escapeXml(data.superAdmin.username)}</c:if>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".connectionType" valueClass="js-gw-conn-type">
                        <i:inline key=".connectionType.${data.connectionType}"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".ipaddress">
                        <span class="js-gw-ip">${fn:escapeXml(data.ipAddress)}</span>&nbsp;
                        <span class="js-gw-port"><i:inline key=".port" arguments="${data.port}"/></span>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".radios" valueClass="js-gw-radios">
                        <c:forEach var="radio" items="${data.radios}">
                            <div title="<cti:formatDate type="DATEHM" value="${radio.timestamp}"/>" class="stacked">
                                <div><i:inline key=".radioType.${radio.type}"/></div>
                                <div><i:inline key=".macAddress" arguments="${radio.macAddress}"/></div>
                            </div>
                        </c:forEach>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="column two nogutter">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".connectionStatus">
                        <c:set var="clazz" value="${data.connectionStatus == 'CONNECTED' ? 'green' : 'red'}"/>
                        <span class="state-box ${clazz} js-gw-conn-state"></span>
                        <span class="js-gw-conn-state-text">
                            <i:inline key=".connectionStatus.${data.connectionStatus}"/>
                        </span>
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
                        <span class="${clazz} js-gw-last-comm">
                            <i:inline key=".lastCommStatus.${data.lastCommStatus}"/>
                        </span>
                        <cti:formatDate var="lastCommTime" type="DATEHM" value="${data.lastCommStatusTimestamp}"/>
                        <span class="js-gw-last-comm-time subtle">${lastCommTime}</span>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
<%-- Add back when NM test connection works. --%>
<%--                 <div class="action-area"> --%>
<%--                     <cti:button nameKey="testConnection" busy="true" icon="icon-server-connect"/> --%>
<%--                 </div> --%>
            </div>
        </div>
    </tags:sectionContainer2>
</div>

<div class="stacked">
    <tags:sectionContainer2 nameKey="dataCollection" id="gw-data-collection">
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:nameValueContainer2 tableClass="with-form-controls">
                    <tags:nameValue2 nameKey=".dataCompleteness" valueClass="js-gw-data-completeness">
                        <div class="progress dib vatb">
                            <c:set var="clazz" value="progress-bar-success"/>
                            <c:if test="${gateway.totalCompletionLevelWarning}">
                                <c:set var="clazz" value="progress-bar-warning"/>
                            </c:if>
                            <c:if test="${gateway.totalCompletionLevelDanger}">
                                <c:set var="clazz" value="progress-bar-danger"/>
                            </c:if>
                            <div class="progress-bar ${clazz}" style="width: ${gateway.totalCompletionPercentage}%"></div>
                        </div>&nbsp;
                        <span class="js-gw-data-completeness-percent">
                            <fmt:formatNumber pattern="###.##%" value="${gateway.totalCompletionPercentage / 100}"/>
                        </span>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="column two nogutter">
                <tags:nameValueContainer2 tableClass="with-form-controls">
                    <tags:nameValue2 nameKey=".schedule" valueClass="full-width">
                        <span class="js-gw-schedule"><cti:formatCron value="${data.collectionSchedule}"/></span>
                        <cti:checkRolesAndProperties value="INFRASTRUCTURE_ADMIN">
                            <cti:button nameKey="edit" icon="icon-pencil" classes="fr" data-popup="#gateway-schedule-popup"/>
                        </cti:checkRolesAndProperties>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
        </div>
        <h3><i:inline key=".sequencing"/></h3>
        <div id="gw-sequence-table" class="scroll-lg">
            <%@ include file="sequences.jsp" %>
        </div>
    </tags:sectionContainer2>
</div>

<div id="gateway-templates" class="dn"><cti:toJson object="${text}" id="gateway-text"/></div>

<cti:url var="deleteUrl" value="/stars/gateways/${gateway.paoIdentifier.paoId}"/>
<form id="delete-gw-form" action="${deleteUrl}" method="post" class="dn">
    <input type="hidden" name="_method" value="delete">
</form>

<cti:includeScript link="OPEN_LAYERS"/>
<cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
<cti:includeScript link="/JavaScript/yukon.assets.gateway.shared.js"/>
<cti:includeScript link="/JavaScript/yukon.assets.gateway.details.js"/>

</cti:standardPage>