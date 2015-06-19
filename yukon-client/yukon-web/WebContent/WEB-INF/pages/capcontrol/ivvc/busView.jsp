<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="ivvc.busView">
    
<cti:includeScript link="/resources/js/common/yukon.table.dynamic.js"/>
<cti:includeScript link="/resources/js/pages/yukon.da.busview.js"/>

<%@ include file="/capcontrol/capcontrolHeader.jspf" %>

<cti:url var="zoneCreatorUrl" value="/capcontrol/ivvc/wizard/zoneCreationWizard">
    <cti:param name="subBusId" value="${subBusId}"/>
</cti:url>

<cti:msg2 var="zoneCreationWizardTitle" key="modules.capcontrol.ivvc.zoneWizard.creation.title"/>
<cti:msg2 var="zoneEditorWizardTitle" key="modules.capcontrol.ivvc.zoneWizard.editor.title"/>

<!-- Zone Wizard Dialog -->
<tags:simpleDialog id="zoneWizardPopup"/>

<script type="text/javascript">
    function showZoneCreationWizard(url) {
        openSimpleDialog('zoneWizardPopup', url, "${zoneCreationWizardTitle}", null, 'get');
    }
    
    function showZoneEditorWizard(url) {
        openSimpleDialog('zoneWizardPopup', url, "${zoneEditorWizardTitle}", null, 'get');
    }
    
    function selectZone(event) {
        var span = event.target;
        $('.selectedZone').removeClass('selectedZone');
        $(span).addClass('selectedZone');
    }
    
</script>

<c:set var="dividerAdded" value="false" />

<c:if test="${hasSubBusControl}">
    
    <div class="js-page-additional-actions dn">
        <c:if test="${not dividerAdded}">
            <li class="divider">
            <c:set var="dividerAdded" value="true" />
        </c:if>
        <cm:dropdownOption linkId="subbusState_${subBusId}" key="yukon.common.actions" icon="icon-cog"/>
    </div>
</c:if>

<c:if test="${hasEditingRole}">
    <div class="js-page-additional-actions dn">
        <cti:url var="editUrl" value="/editor/cbcBase.jsf">
            <cti:param name="type" value="2"/>
            <cti:param name="itemid" value="${subBusId}"/>
        </cti:url>
        <c:if test="${not dividerAdded}">
            <li class="divider">
            <c:set var="dividerAdded" value="true" />
        </c:if>
        <cm:dropdownOption key="components.button.edit.label" icon="icon-pencil" href="${editUrl}" />
    </div>
</c:if>
<div class="column-12-12">
    <div class="column one">
        
        <tags:sectionContainer2 nameKey="busDetail">
            
            <table class="full-width striped">
                <%-- KVAR --%>
                <tr>
                    <td><i:inline key=".busDetail.table.kvar"/></td>
                    <td>
                        <cti:capControlValue paoId="${subBusId}" type="SUBBUS" format="KVAR_LOAD"/>
                        <cti:classUpdater type="SUBBUS" identifier="${subBusId}/KVAR_LOAD_QUALITY">
                            <cti:icon icon="icon-bullet-red" classes="thin fn M0"/>
                        </cti:classUpdater>
                    </td>
                </tr>
                <%-- KW --%>
                <tr>
                    <td><i:inline key=".busDetail.table.kw"/></td>
                    <td>
                        <cti:capControlValue paoId="${subBusId}" type="SUBBUS" format="KW"/>
                        <cti:classUpdater type="SUBBUS" identifier="${subBusId}/WATT_QUALITY">
                            <cti:icon icon="icon-bullet-red" classes="thin fn M0"/>
                        </cti:classUpdater>
                    </td>
                </tr>
                <%-- POWER FACTOR --%>
                <tr>
                    <td>${fn:escapeXml(pfPoint.pointName)}</td>
                    <td>
                        <cti:pointValue pointId="${pfPoint.liteID}" format="VALUE"/>
                    </td>
                </tr>
            </table>
            <div class="action-area">
                <a href="javascript:void(0);" data-popup=".js-strategy-details" data-popup-toggle>
                    <i:inline key=".busDetail.strategy.settings"/>
                </a>
            </div>
        </tags:sectionContainer2>
        
        <%-- STRATEGY SETTINGS POPUP --%>
        <cti:msg2 key=".strategyDetails.title" arguments="${strategyName}" var="strategyTitle"/>
        <div class="dn js-strategy-details" data-title="${strategyTitle}">
            <c:if test="${hasEditingRole}">
                <cti:url var="url" value="/capcontrol/strategies/${strategyId}" />
                <i:inline key=".strategyDetails.link"/><a href="${url}">${fn:escapeXml(strategyName)}</a>
            </c:if>
            <table class="full-width striped" >
                <thead>
                    <tr>
                        <th></th>
                        <th><i:inline key=".strategyDetails.table.peak"/></th>
                        <th><i:inline key=".strategyDetails.table.offPeak"/></th>
                        <th></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="setting" items="${strategySettings}">
                        <tr>
                        
                            <td><i:inline key="${setting.key}"/></td>
                            <td class="tar">
                                ${setting.value.peakValue}
                            </td>
                            <td class="tar">
                                ${setting.value.offPeakValue}
                            </td>
                            <td>
                                ${setting.key.units}
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        
    </div>
    
    <div class="column two nogutter">
    
        <cti:tabs>
            <cti:msg2 var="tabName" key=".voltageProfile.title" />
            <cti:tab title="${tabName}">
                <c:set var="chartId" value="sub_${subBusId}_chart" />
                <flot:ivvcChart chartId="${chartId}"
                    jsonDataAndOptions="${graphAsJSON}"
                    title="${graphSettings.graphTitle}" />
                <cti:url var="url" value="/capcontrol/ivvc/bus/chart">
                    <cti:param name="subBusId" value="${subBusId}" />
                </cti:url>
                <cti:dataUpdaterCallback 
                    function="yukon.flot.reloadChartIfExpired({chartId:'${chartId}', dataUrl:'${url}'})"
                    initialize="false" largestTime="CAPCONTROL/${subBusId}/IVVC_LARGEST_GRAPH_TIME_FOR_SUBBUS"/>
            </cti:tab>
            <cti:msg2 var="voltagePointsTab" key=".voltagePoints.title" />
            <cti:tab title="${voltagePointsTab}">
                <div class="scroll-lg">
                    <c:forEach items="${zoneVoltagePointsHolders}" var="zoneVoltagePointsHolder">
                        <c:set var="zoneName" value="${zoneVoltagePointsHolder.zoneName}"/>
                        <cti:url var="zoneDetailUrl" value="/capcontrol/ivvc/zone/detail">
                            <cti:param name="zoneId" value="${zoneVoltagePointsHolder.zoneId}"/>
                        </cti:url>
                        <cti:url var="zoneVoltagePointsUrl" value="/capcontrol/ivvc/zone/voltagePoints">
                            <cti:param name="zoneId" value="${zoneVoltagePointsHolder.zoneId}"/>
                        </cti:url>
                        <%@ include file="voltagePoints.jspf" %>
                        <br>
                    </c:forEach>
                </div>
            </cti:tab>
        </cti:tabs>
    </div>
</div>

<tags:sectionContainer2 nameKey="zones">
    <table class="full-width">
        <tfoot></tfoot>
        <tbody>
            <cti:navigableHierarchy var="zone" depth="depth" hierarchy="${zones}">
                <tr>
                    <td class="half-width">
                        <cti:url var="url" value="/capcontrol/ivvc/zone/detail">
                            <cti:param name="zoneId" value="${zone.zoneId}"/>
                        </cti:url>
                        <a href="${url}">${fn:escapeXml(zone.name)}</a>
                    </td>
                    <td class="half-width">
                       <div class="js-events-timeline clear" data-zone-id="${zone.zoneId}"></div>
                    </td>
                </tr>
            </cti:navigableHierarchy>
        </tbody>
    </table>
    
    <c:if test="${hasEditingRole}">
        <div class="action-area">
            <c:if test="${unassignedBanksCount > 0}">
                <span class="warning fl"><i:inline key=".zoneList.unassignedBanksCount" arguments="${unassignedBanksCount}"/></span>
            </c:if>            
            <cti:button nameKey="add" onclick="javascript:showZoneCreationWizard('${zoneCreatorUrl}');" icon="icon-add"/>
        </div>
    </c:if>
    
</tags:sectionContainer2>

<div class="clear">
    <tags:sectionContainer2 nameKey="ivvcEvents">
        <input type="hidden" value="0" id="ivvc-events-last-update">
        <input type="hidden" value="${subBusId}" id="ivvc-bus-id">
        <div class="empty-list js-ivvc-events-empty">
            <i:inline key=".events.emptylist"/>
        </div>
        <div class="scroll-lg dn js-ivvc-events-holder stacked-md clear">
            <table id="ivvc-events" class="has-alerts full-width dashed stacked striped">
                <thead></thead>
                <tfoot></tfoot>
                <tbody>
                </tbody>
            </table>
        </div>
    </tags:sectionContainer2>
</div>

<table class="dn">
    <tr data-event-id="?" class="js-event-template">
        <td><cti:icon icon="js-event-icon"/></td>
        <td>
            <div class="js-device-name"></div>
        </td>
        <td>
            <div class="js-message"></div>
        </td>
        <td class="js-user"></td>
        <td class="tar js-timestamp"></td>
    </tr>
</table>
</cti:standardPage>