<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="capcontrol" page="ivvc.zoneDetail">

<%@include file="/capcontrol/capcontrolHeader.jspf"%>

<cti:includeScript link="/JavaScript/yukon.table.dynamic.js"/>
<cti:includeScript link="/JavaScript/yukon.da.zone.js" />
<cti:includeScript link="/JavaScript/yukon.da.command.js" />

<!-- Zone Wizard Dialog -->
<tags:simpleDialog id="zoneWizardPopup" title=""/>

<div class="js-page-additional-actions dn">
    <cti:url var="zoneVoltageDeltasUrl" value="/capcontrol/ivvc/zone/voltageDeltas">
       <cti:param name="zoneId" value="${zoneId}"/>
    </cti:url>

    <cm:dropdownOption key=".otherActions.voltageDeltas" href="${zoneVoltageDeltasUrl}" icon="icon-control-equalizer" />
    <cm:dropdownOption key="yukon.common.edit" classes="js-zone-editor" icon="icon-pencil" />
</div>

<div class="column-10-14">
    <div class="column one">
        <tags:sectionContainer2 nameKey="details">
        
            <table class="compact-results-table has-alerts has-actions">
                <thead></thead>
                <tfoot></tfoot>
                <tbody>
                <tr>
                    <td><cti:icon icon="icon-blank"/></td>
                    <td><span class="strong-label-small"><i:inline key=".details.table.zone"/></span></td>
                    <td>
                            <i:inline key="yukon.web.modules.capcontrol.ivvc.zone.${zoneDto.zoneType}"/>
                    </td>
                </tr>
                <c:forEach items="${zoneDto.regulators}" var="regulator">
                    <c:set value="${regulator.key}" var="phaseKey"/>
                    <tr>
                        <td>
                            <capTags:regulatorModeIndicator paoId="${regulatorIdMap[phaseKey]}"/>
                        </td>
                        <td>
                            <span class="strong-label-small">
                                <i:inline key=".details.table.regulator"/>
                                <c:if test="${zoneDto.zoneType != gangOperated}"> 
                                    - <i:inline key="${phaseKey}"/>
                                </c:if>
                            </span>
                        </td>
                        <td>
                            <cti:url var="regulatorUrl" value="/capcontrol/regulators/${regulatorIdMap[phaseKey]}" />

                            <a href="${regulatorUrl}">${fn:escapeXml(regulatorNameMap[phaseKey])}</a>
                            
                            <cm:dropdown data-pao-id="${regulatorIdMap[phaseKey]}" triggerClasses="fr">
                                <cm:dropdownOption key=".scan.label" icon="icon-transmit-blue" classes="js-command-button" 
                                    data-pao-id="${regulatorIdMap[phaseKey]}" data-command-id="${scanCommandHolder.commandId}" />
                                <cm:dropdownOption key=".up.label" icon="icon-arrow-up-green" classes="js-command-button" 
                                    data-pao-id="${regulatorIdMap[phaseKey]}" data-command-id="${tapUpCommandHolder.commandId}" />
                                <cm:dropdownOption key=".down.label" icon="icon-arrow-down-orange" classes="js-command-button" 
                                    data-pao-id="${regulatorIdMap[phaseKey]}" data-command-id="${tapDownCommandHolder.commandId}" />
                                <cm:dropdownOption key=".enable.label" icon="icon-accept" classes="js-command-button" 
                                    data-pao-id="${regulatorIdMap[phaseKey]}" data-command-id="${enableRemoteCommandHolder.commandId}" />
                                <cm:dropdownOption key=".disable.label" icon="icon-delete" classes="js-command-button" 
                                    data-pao-id="${regulatorIdMap[phaseKey]}" data-command-id="${disableRemoteCommandHolder.commandId}" />
                            </cm:dropdown>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <div class="action-area">
                    <cti:button classes="js-zone-editor" nameKey="edit" icon="icon-pencil"/>
                </div>
        </tags:sectionContainer2>
    </div>

    <div class="column two nogutter">
        <cti:tabs>
            <cti:msg2 var="tabName" key=".voltageProfile.title" />
            <cti:tab title="${tabName}">
                <c:set var="chartId" value="zone_${zoneId}_chart" />
                <cti:url var="chartJsonDataUrl" value="/capcontrol/ivvc/zone/chart">
                    <cti:param name="zoneId" value="${zoneId}" />
                </cti:url>
                <flot:ivvcChart chartId="${chartId}"
                    jsonDataAndOptions="${graphAsJSON}"
                    title="${graphSettings.graphTitle}" />

                <cti:dataUpdaterCallback function="yukon.flot.reloadChartIfExpired({chartId:'${chartId}', dataUrl:'${chartJsonDataUrl}'})"
                    initialize="false" largestTime="CAPCONTROL/${zoneId}/IVVC_LARGEST_GRAPH_TIME_FOR_ZONE"/>
            </cti:tab>
            <cti:msg2 var="voltagePointsTab" key=".voltagePoints.title" />
            <cti:tab title="${voltagePointsTab}">
                <div class="scroll-lg">
                    <cti:url var="zoneVoltagePointsUrl" value="/capcontrol/ivvc/zone/voltagePoints">
                       <cti:param name="zoneId" value="${zoneId}"/>
                   </cti:url>
                    <%@ include file="voltagePoints.jspf" %>
                </div>
            </cti:tab>
        </cti:tabs>
    </div>
</div>
<div class="clear">
    <tags:sectionContainer2 nameKey="ivvcEvents">
        <tags:stepper id="ivvc-events-range" key=".events.shown" classes="fr stacked">
            <c:forEach var="range" items="${ranges}">
                <c:set var="selected" value="${range eq lastRange ? 'selected' : ''}"/>
                <option value="${range}" ${selected}><cti:msg2 key="${range}"/></option>
            </c:forEach>
        </tags:stepper>
        <input type="hidden" value="0" id="ivvc-events-last-update">
        <div class="empty-list js-ivvc-events-empty">
            <i:inline key=".events.emptylist"/>
        </div>
        <div class="scroll-md dn js-ivvc-events-holder stacked-md clear">
            <table id="ivvc-events" class="has-alerts full-width dashed stacked striped">
                <thead></thead>
                <tfoot></tfoot>
                <tbody>
                </tbody>
            </table>
        </div>
        <div class="js-events-timeline clear" data-zone-id="${zoneId}"></div>
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

<input type="hidden" id="zone-id" value="${zoneId}"/>
<cti:toJson object="${hours}" id="range-hours"/>

<cti:msg2 var="editorTitle" key="yukon.web.modules.capcontrol.ivvc.zoneWizard.editor.title" />
<cti:url var="editorUrl" value="/capcontrol/ivvc/wizard/zoneEditor">
    <cti:param name="zoneId" value="${zoneId}"/>
</cti:url>

<div id="zone-editor-info" data-editor-url="${editorUrl}" data-editor-title="${editorTitle}"/></div>
</cti:standardPage>