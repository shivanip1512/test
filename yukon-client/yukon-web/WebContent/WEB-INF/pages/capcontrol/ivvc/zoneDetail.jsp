<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="highChart" tagdir="/WEB-INF/tags/highChart" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="capcontrol" page="ivvc.zoneDetail">

    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    
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
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".details.table.zone" nameClass="mw100">
                        <i:inline key="yukon.web.modules.capcontrol.ivvc.zone.${zoneDto.zoneType}"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".details.table.regulator" nameClass="mw100">
                        <%@ include file="zoneRegulators.jsp" %>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
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
                    <highChart:ivvcChart chartId="${chartId}"
                        feederList="${feederList}"
                        jsonDataAndOptions="${graphAsJSON}"
                        title="${fn:escapeXml(graphSettings.graphTitle)}" />
                    <cti:dataUpdaterCallback function="yukon.da.ivvcChart.reloadChartIfExpired({chartId:'${chartId}', dataUrl:'${chartJsonDataUrl}'})"
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
            <div class="js-events-timeline clear" data-zone-id="${zoneId}"></div>
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
                <table id="ivvc-events" class="compact-results-table has-alerts full-width dashed stacked striped">
                    <thead>
                        <th></th>
                        <th><i:inline key=".ivvc.busView.deviceName"/></th>
                        <th><i:inline key=".ivvc.busView.eventMessage"/></th>
                        <th><i:inline key=".ivvc.busView.user"/></th>
                        <th><i:inline key=".ivvc.busView.timestamp"/></th></thead>
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
            <td class="js-timestamp"></td>
        </tr>
    </table>
    
    <input type="hidden" id="zone-id" value="${zoneId}"/>
    <cti:toJson object="${hours}" id="range-hours"/>
    
    <cti:msg2 var="editorTitle" key="yukon.web.modules.capcontrol.ivvc.zoneWizard.editor.title" />
    <cti:url var="editorUrl" value="/capcontrol/ivvc/wizard/zoneEditor">
        <cti:param name="zoneId" value="${zoneId}"/>
    </cti:url>
    <div id="zone-editor-info" data-editor-url="${editorUrl}" data-editor-title="${editorTitle}"/>
    
    <cti:includeScript link="/resources/js/common/yukon.table.dynamic.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.da.zone.js" />
    <cti:includeScript link="/resources/js/pages/yukon.da.command.js" />
    <cti:includeScript link="/resources/js/pages/yukon.da.zone.wizard.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.da.ivvcChart.js"/>

</cti:standardPage>