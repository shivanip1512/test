<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="operator" page="gateways.detail">

<input type="hidden" class="js-device-id" value="${gateway.paoIdentifier.paoId}"/>

<c:if test="${not empty geojson}"><cti:toJson id="geojson" object="${geojson}"/></c:if>

<d:confirm on="#gateway-delete" nameKey="delete.confirm"/>

<div id="page-buttons" class="dn">
    <cm:dropdown type="button" data-name="${fn:escapeXml(gateway.name)}" data-id="${gateway.paoIdentifier.paoId}">
        <cm:dropdownOption icon="icon-table-row-insert" key=".collectData" classes="js-gw-collect-data"/>
        <cti:url var="mapNetworkUrl" value="/stars/mapNetwork/home?deviceId=${gateway.paoIdentifier.paoId}"/>
        <cm:dropdownOption icon="icon-map" key=".mapNetwork" href="${mapNetworkUrl}"/>
        <cti:checkRolesAndProperties value="INFRASTRUCTURE_ADMIN">
            <li class="divider"></li>
            <cm:dropdownOption icon="icon-connect" key=".connect" classes="js-gw-connect"/>
            <cm:dropdownOption icon="icon-disconnect" key=".disconnect" classes="js-gw-disconnect"/>
        </cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="INFRASTRUCTURE_CREATE_AND_UPDATE, INFRASTRUCTURE_DELETE">
            <li class="divider"></li>
        </cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="INFRASTRUCTURE_CREATE_AND_UPDATE">
            <c:set var="clazz" value="${empty data ? 'dn' : ''}"/>
            <cm:dropdownOption icon="icon-pencil" key="components.button.edit.label" data-popup="#gateway-edit-popup"
                    classes="js-edit ${clazz}"/>
        </cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="INFRASTRUCTURE_DELETE">
            <cm:dropdownOption icon="icon-cross" key="components.button.delete.label" id="gateway-delete"
                data-ok-event="yukon:assets:gateways:delete"/>
        </cti:checkRolesAndProperties>
    </cm:dropdown>
</div>

<div id="gateway-id" data-id="${gateway.paoIdentifier.paoId}" class="dn"></div>
    
<div id="gateway-collect-data-popup" class="dn"></div>


<cti:checkRolesAndProperties value="INFRASTRUCTURE_ADMIN">
    <cti:url var="scheduleUrl" value="/stars/gateways/${gateway.paoIdentifier.paoId}/schedule/options"/>
    <div id="gateway-schedule-popup" data-dialog class="dn" 
            data-title="<cti:msg2 key=".schedule"/>"
            data-event="yukon:assets:gateway:schedule:save"
            data-ok-text="<cti:msg2 key="components.button.save.label"/>"
            data-width="400" data-height="250"
            data-url="${scheduleUrl}"></div>
</cti:checkRolesAndProperties>

<tags:widgetContainer deviceId="${gateway.paoIdentifier.paoId}" identify="false">
    <c:set var="data" value="${gateway.data}"/>
    <div class="column-12-12 clearfix">
    
        <div class="one column">
            <tags:widget bean="gatewayInformationWidget" container="section"/>
            <tags:widget bean="paoNotesWidget" container="section" hideTableBorders="true"/> 
        </div>
        
        <div class="column two nogutter">
            <c:set var="infrastructureLabelWidth" value="90px"/>
            <%@ include file="/WEB-INF/pages/stars/mapNetwork/locationContainer.jsp" %>
        </div>
        
    </div>
    <div class="stacked">
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="comms" styleClass="stacked" id="gw-comm">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".admin" valueClass="js-gw-admin">
                            <c:if test="${empty data.admin}"><span class="empty-list"><i:inline key="yukon.common.none"/></span></c:if>
                            <c:if test="${not empty data.admin}">${fn:escapeXml(data.admin.username)}</c:if>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".superAdmin" valueClass="js-gw-super-admin">
                            <c:if test="${empty data.superAdmin}"><span class="empty-list"><i:inline key="yukon.common.none"/></span></c:if>
                            <c:if test="${not empty data.superAdmin}">${fn:escapeXml(data.superAdmin.username)}</c:if>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".connectionType" valueClass="js-gw-conn-type">
                            <c:if test="${not empty data}"><i:inline key=".connectionType.${data.connectionType}"/></c:if>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".ipaddress">
                            <span class="js-gw-ip">${fn:escapeXml(data.ipAddress)}</span>&nbsp;
                            <span class="js-gw-port">
                                <c:if test="${not empty data}">
                                    <i:inline key=".port" arguments="${data.port}"/>
                                </c:if>
                            </span>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".radios" valueClass="js-gw-radios">
                            <c:if test="${not empty data}">
                                <c:forEach var="radio" items="${data.radios}">
                                    <div title="<cti:formatDate type="DATEHM" value="${radio.timestamp}"/>" class="stacked">
                                        <div><i:inline key=".radioType.${radio.type}"/></div>
                                        <div><i:inline key=".macAddress" arguments="${radio.macAddress}"/></div>
                                        <div><i:inline key=".version" arguments="${radio.version}"/></div>
                                    </div>
                                </c:forEach>
                            </c:if>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".connectionStatus">
                            <cti:attributeResolver pao="${gateway}" attribute="${commStatusAttribute}" var="pointId"/>
                            <cti:pointStatus pointId="${pointId}"/>
                            <cti:uniqueIdentifier var="popupId" prefix="historical-readings-"/>
                            <cti:url var="historyUrl" value="/meter/historicalReadings/view">
                                <cti:param name="pointId" value="${pointId}"/>
                                <cti:param name="deviceId" value="${gateway.paoIdentifier.paoId}"/>
                            </cti:url>
                            <a href="javascript:void(0);" data-popup="#${popupId}" class="${pageScope.classes}">
                                <cti:pointValue pointId="${pointId}"/>
                            </a>
                            <div id="${popupId}" data-width="500" data-height="400" data-url="${historyUrl}" class="dn"></div>
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
                                <c:if test="${not empty data}">
                                    <i:inline key=".lastCommStatus.${data.lastCommStatus}"/>
                                </c:if>
                            </span>
                            <cti:formatDate var="lastCommTime" type="DATEHM" value="${data.lastCommStatusTimestamp}"/>
                            <span class="js-gw-last-comm-time subtle">${lastCommTime}</span>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>    
                <%-- Add back when NM test connection works. --%>
                <%-- <div class="action-area"> --%>
                    <%-- <cti:button nameKey="testConnection" busy="true" icon="icon-server-connect"/> --%>
                <%-- </div> --%>
            </div>
            <div class="column two nogutter">
                <cti:msg2 var="infraStructureWarningsTitle" key=".infraStructureWarnings.title"/>
                <tags:widget bean="deviceInfrastructureWarningsWidget" title="${infraStructureWarningsTitle}" container="section"/>
                <c:set var="attributes" value="READY_NODES,STREAMING_CAPABLE_DEVICE_COUNT"/>
                <cti:checkRolesAndProperties value="RF_DATA_STREAMING_ENABLED">
                    <c:set var="attributes" value="READY_NODES,STREAMING_CAPABLE_DEVICE_COUNT,STREAMING_ACTIVE_DEVICE_COUNT"/>
                </cti:checkRolesAndProperties>
                <cti:msg2 var="widgetTitle" key=".metrics.title"/>
                <tags:widget bean="simpleAttributesWidget" title="${widgetTitle}" container="section" attributes="${attributes}" />
                <!-- Gateway node Information  -->
                <cti:msg2 var="nodeInfoTitle" key=".gatewayNodeInfo.title"/>
                <tags:widget bean="gatewayNodeInformationWidget" title="${nodeInfoTitle}" container="section" />
            </div>
        </div>
    </div>
</tags:widgetContainer>
&nbsp;

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
                            <c:set var="clazz" value="${empty data ? 'dn' : ''}"/>
                            <cti:button nameKey="edit" icon="icon-pencil" classes="fr ${clazz} js-edit" 
                                data-popup="#gateway-schedule-popup"/>
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
    <cti:csrfToken/>
    <input type="hidden" name="_method" value="delete">
</form>

<cti:includeScript link="OPEN_LAYERS"/>
<cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
<cti:includeScript link="/resources/js/pages/yukon.assets.gateway.shared.js"/>
<cti:includeScript link="/resources/js/pages/yukon.assets.gateway.details.js"/>
<cti:includeScript link="/resources/js/widgets/yukon.widget.gateway.info.js"/>
<cti:includeScript link="/resources/js/common/yukon.mapping.js"/>
<cti:includeScript link="/resources/js/pages/yukon.map.network.js"/>

</cti:standardPage>