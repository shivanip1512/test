<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="capcontrol" page="ivvc.zoneDetail">

    <cti:includeScript link="/JavaScript/yukon.tables.js" />
    <cti:includeScript link="/JavaScript/simpleDialog.js"/>
    <cti:includeScript link="/JavaScript/dynamicTable.js"/>
    <cti:includeScript link="/JavaScript/picker.js" />
    <cti:includeScript link="/JavaScript/yukon.ivvc.zone.js" />

    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    <cti:includeCss link="/WebConfig/yukon/styles/da/ivvc.css"/>

    <!-- Zone Wizard Dialog -->
    <tags:simpleDialog id="zoneWizardPopup" title=""/>

<script type="text/javascript">
    jQuery( function() {
        yukon.Ivvc.Zone.init( {timeout : '${updaterDelay}',
                               controlRole : '${hasControlRole}',
                               subBusId : '${subBusId}',
                               zoneId: '${zoneId}'
       });
    });
</script>


<div class="f-page-additional-actions dn">
    <cti:url var="zoneVoltageDeltasUrl" value="/capcontrol/ivvc/zone/voltageDeltas">
       <cti:param name="zoneId" value="${zoneId}"/>
    </cti:url>

    <cm:dropdownOption key=".otherActions.voltageDeltas" href="${zoneVoltageDeltasUrl}" icon="icon-control-equalizer" />
</div>


    <div class="column-12-12">
        <div class="column one">
            <tags:boxContainer2 nameKey="details" hideEnabled="true" showInitially="true">
                <table class="compact-results-table">
                    <thead>
                    <tr>
                        <th></th>
                        <th><i:inline key=".details.table.name"/></th>
                        <th><i:inline key=".details.table.type"/></th>
                    </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                    <tr>
                        <td><cti:icon icon="icon-blank"/><span class="strong-label-small"><i:inline key=".details.table.zone"/></span></td>
                        <td>
                            <c:if test="${hasEditingRole}">
                                <cti:msg2 key="yukon.web.modules.capcontrol.ivvc.zoneWizard.editor.title" var="zoneWizardTitle"/>
                                <cti:url var="zoneEditorUrl" value="/capcontrol/ivvc/wizard/zoneEditor">
                                    <cti:param name="zoneId" value="${zoneId}"/>
                                </cti:url>
                                <a href="javascript:void(0);" class="f-zone-editor" data-editor-url="${zoneEditorUrl}" data-editor-title="${zoneWizardTitle}">
                            </c:if>
                                ${fn:escapeXml(zoneName)}
                            <c:if test="${hasEditingRole}">
                                </a>
                            </c:if>
                        </td>
                        <td>
                                <i:inline key="yukon.web.modules.capcontrol.ivvc.zone.${zoneDto.zoneType}"/>
                        </td>
                    </tr>
                    <c:forEach items="${zoneDto.regulators}" var="regulator">
                        <c:set value="${regulator.key}" var="phaseKey"/>
                        <tr>
                            <td>
                                <capTags:regulatorModeIndicator paoId="${regulatorIdMap[phaseKey]}" type="VOLTAGE_REGULATOR"/>
                                <span class="strong-label-small">
                                    <i:inline key=".details.table.regulator"/>
                                    <c:if test="${zoneDto.zoneType != gangOperated}"> - 
                                        <i:inline key="${phaseKey}"/>
                                    </c:if>
                                </span>
                            </td>
                            <td>
                                <cti:url var="editorUrl" value="/editor/cbcBase.jsf">
                                   <cti:param name="type" value="2"/>
                                   <cti:param name="itemid" value="${regulatorIdMap[phaseKey]}"/>
                                </cti:url>

                                <c:if test="${hasEditingRole}">
                                    <a href="${editorUrl}">
                                </c:if>
                                ${fn:escapeXml(regulatorNameMap[phaseKey])}
                                <c:if test="${hasEditingRole}">
                                    </a>
                                </c:if>
                            </td>
                            <td>
                                <i:inline key="yukon.web.modules.capcontrol.ivvc.regulator.${zoneDto.zoneType}"/>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </tags:boxContainer2>
            
            <tags:boxContainer2 nameKey="actions" hideEnabled="true" showInitially="true" styleClass="regulatorActions">
                <table class="compact-results-table">
                    <thead>
                    <tr>
                        <c:if test="${zoneDto.zoneType != gangOperated}">
                            <c:forEach items="${zoneDto.regulators}" var="regulator">
                                <th><i:inline key=".details.table.phase.${regulator.key}"/></th>
                            </c:forEach>
                        </c:if>
                    </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                    <tr>
                        <c:forEach items="${zoneDto.regulators}" var="regulator">
                            <c:set var="phaseKey" value="${regulator.key}"/>
                            <td>
                                <ul class="button-stack">
                                    <li>
                                        <cti:button renderMode="labeledImage" nameKey="scan" classes="commandButton" icon="icon-transmit-blue"/>
                                        <input type="hidden" class="paoId" value="${regulatorIdMap[phaseKey]}">
                                        <input type="hidden" class="cmdId" value="${scanCommandHolder.commandId}">
                                    </li>
                                    <li>
                                        <cti:button renderMode="labeledImage" nameKey="up" classes="commandButton" icon="icon-bullet-go-up"/>
                                        <input type="hidden" class="paoId" value="${regulatorIdMap[phaseKey]}">
                                        <input type="hidden" class="cmdId" value="${tapUpCommandHolder.commandId}">
                                    </li>
                                    <li>
                                        <cti:button renderMode="labeledImage" nameKey="down" classes="commandButton" icon="icon-bullet-go-down"/>
                                        <input type="hidden" class="paoId" value="${regulatorIdMap[phaseKey]}">
                                        <input type="hidden" class="cmdId" value="${tapDownCommandHolder.commandId}">
                                    </li>
                                    <li>
                                        <cti:button renderMode="labeledImage" nameKey="enable" classes="commandButton" icon="icon-accept"/> 
                                        <input type="hidden" class="paoId" value="${regulatorIdMap[phaseKey]}">
                                        <input type="hidden" class="cmdId" value="${enableRemoteCommandHolder.commandId}">
                                    </li>
                                    <li>
                                        <cti:button renderMode="labeledImage" nameKey="disable" classes="commandButton" icon="icon-delete"/>
                                        <input type="hidden" class="paoId" value="${regulatorIdMap[phaseKey]}">
                                        <input type="hidden" class="cmdId" value="${disableRemoteCommandHolder.commandId}">
                                    </li>
                                </ul>
                            </td>
                        </c:forEach>
                    </tr>
                    </tbody>
                </table>
            </tags:boxContainer2>
            <c:choose>
                <c:when test="${zoneDto.zoneType == threePhase}">
                    <cti:tabbedContentSelector>
                        <cti:msg2 var="tabName" key=".ivvc.zoneDetail.attributesRegAll.title" />
                        <cti:tabbedContentSelectorContent selectorName="${tabName}">
                            <table class="compact-results-table">
                                <thead>
                                <tr style="text-align: left;">
                                    <th><i:inline key=".attributes.name"/></th>
                                    <c:forEach items="${zoneDto.regulators}" var="regulator">
                                        <th><i:inline key=".attributes.phaseValue.${regulator.key}"/></th>
                                    </c:forEach>
                                </tr>
                                </thead>
                                <tfoot></tfoot>
                                <tbody>
                                <c:forEach var="point" items="${regulatorPointMappingsMap[phaseA]}" varStatus="status">
                                    <tr class="regulatorAttributeRow">
                                        <td><i:inline key="${point.regulatorPointMapping}"/></td>
                                        <c:forEach items="${zoneDto.regulators}" var="regulator">
                                            <c:set var="phaseKey" value="${regulator.key}"/>
                                            <td>
                                                <c:set var="pointId" value="${regulatorPointMappingsMap[phaseKey][status.index].pointId}"/>
                                                <c:choose>
                                                    <c:when test="${pointId > 0}">
                                                        <span class="redBullet_${pointId}">
                                                            <cti:icon icon="icon-bullet-red" nameKey="questionable"/>
                                                        </span>
                                                        <cti:pointValue pointId="${pointId}" format="VALUE"/>
                                                        <cti:dataUpdaterCallback function="yukon.Ivvc.Zone.setRedBulletForPoint(${pointId})" 
                                                            initialize="true" quality="POINT/${pointId}/QUALITY"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i:inline key="yukon.web.defaults.dashes"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </c:forEach>
                                    </tr>
                                </c:forEach>
                                <tr class="regulatorAttributeRow">
                                    <td><i:inline key=".attributes.voltChangePerTap"/></td>
                                   <c:forEach items="${zoneDto.regulators}" var="regulator">
                                        <c:set var="phaseKey" value="${regulator.key}" />
                                        <td><cti:dataUpdaterValue identifier="${regulatorIdMap[phaseKey]}/VOLT_CHANGE_PER_TAP" type="VOLTAGE_REGULATOR"/></td>
                                   </c:forEach>
                                </tr>
                                </tbody>
                            </table>
                        </cti:tabbedContentSelectorContent>
                        <c:forEach items="${zoneDto.regulators}" var="regulator">
                            <c:set var="phaseKey" value="${regulator.key}"/>
                            <cti:msg2 var="tabName" key=".ivvc.zoneDetail.attributesReg${phaseKey}.title" />
                            <cti:tabbedContentSelectorContent selectorName="${tabName}">
                                <table class="compact-results-table">
                                    <thead>
                                    <tr style="text-align: left;">
                                        <th><i:inline key=".attributes.name"/></th>
                                        <th><i:inline key=".attributes.value"/></th>
                                        <th><i:inline key=".attributes.timestamp"/></th>
                                    </tr>
                                    </thead>
                                    <tfoot></tfoot>
                                    <tbody>
                                    <c:forEach var="point" items="${regulatorPointMappingsMap[phaseKey]}">
                                        <tr class="regulatorAttributeRow">
                                            <td><i:inline key="${point.regulatorPointMapping}"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${point.pointId > 0}">
                                                        <span class="redBullet_${point.pointId}">
                                                            <cti:icon icon="icon-bullet-red" title="Questionable Quality"/>
                                                        </span>
                                                        <cti:pointValue pointId="${point.pointId}" format="VALUE"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i:inline key="yukon.web.defaults.dashes"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${point.pointId > 0}">
                                                        <cti:pointValue pointId="${point.pointId}" 
                                                            format="DATE"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i:inline key="yukon.web.defaults.dashes"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <tr class="regulatorAttributeRow">
                                        <td><i:inline key=".attributes.voltChangePerTap"/></td>
                                        <td><cti:dataUpdaterValue identifier="${regulatorIdMap[phaseKey]}/VOLT_CHANGE_PER_TAP" type="VOLTAGE_REGULATOR"/></td>
                                    </tr>
                                    </tbody>
                                </table>
                            </cti:tabbedContentSelectorContent>
                        </c:forEach>
                    </cti:tabbedContentSelector>
                </c:when>
                <c:otherwise>
                    <tags:boxContainer2 nameKey="attributes" hideEnabled="true" showInitially="true">
                        <table class="compact-results-table">
                            <thead>
                            <tr style="text-align: left;">
                               <th><i:inline key=".attributes.name"/></th>
                               <th><i:inline key=".attributes.value"/></th>
                               <th><i:inline key=".attributes.timestamp"/></th>
                            </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                            <c:forEach items="${zoneDto.regulators}" var="regulator">
                                <c:set var="phaseKey" value="${regulator.key}"/>
                               <c:forEach var="point" items="${regulatorPointMappingsMap[phaseKey]}">
                                   <tr>
                                       <td><i:inline key="${point.regulatorPointMapping}"/></td>
                                       <td>
                                           <c:choose>
                                               <c:when test="${point.pointId > 0}">
                                                    <span class="redBullet_${point.pointId}">
                                                        <cti:icon icon="icon-bullet-red"/>
                                                    </span>
                                                    <cti:pointValue pointId="${point.pointId}" format="VALUE"/>
                                                    <cti:dataUpdaterCallback function="yukon.Ivvc.Zone.setRedBulletForPoint(${point.pointId})" 
                                                        initialize="true" quality="POINT/${point.pointId}/QUALITY"/>
                                               </c:when>
                                               <c:otherwise>
                                                   <i:inline key="yukon.web.defaults.dashes"/>
                                               </c:otherwise>
                                           </c:choose>
                                       </td>
                                       <td>
                                           <c:choose>
                                               <c:when test="${point.pointId > 0}">
                                                   <cti:pointValue pointId="${point.pointId}" format="DATE"/>
                                               </c:when>
                                               <c:otherwise>
                                                   <i:inline key="yukon.web.defaults.dashes"/>
                                               </c:otherwise>
                                           </c:choose>
                                       </td>
                                   </tr>
                               </c:forEach>
                                <tr class="regulatorAttributeRow">
                                    <td><i:inline key=".attributes.voltChangePerTap"/></td>
                                    <td><cti:dataUpdaterValue identifier="${regulatorIdMap[phaseKey]}/VOLT_CHANGE_PER_TAP" type="VOLTAGE_REGULATOR"/></td>
                                    <td><i:inline key="yukon.web.defaults.dashes"/></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                       </table>
                    </tags:boxContainer2>
                </c:otherwise>
            </c:choose>

        </div>

        <div class="column two nogutter">
            <cti:tabbedContentSelector>
                <cti:msg2 var="tabName" key=".voltageProfile.title" />
                <cti:tabbedContentSelectorContent selectorName="${tabName}">
                    <c:set var="chartId" value="zone_${zoneId}_chart" />
                    <cti:url var="chartJsonDataUrl" value="/capcontrol/ivvc/zone/chart">
                        <cti:param name="zoneId" value="${zoneId}" />
                    </cti:url>
                    <flot:ivvcChart chartId="${chartId}"
                        jsonDataAndOptions="${graphAsJSON}"
                        title="${graphSettings.graphTitle}" />

                    <cti:dataUpdaterCallback function="yukon.Flot.reloadChartIfExpired({chartId:'${chartId}', dataUrl:'${chartJsonDataUrl}'})"
                        initialize="false" largestTime="CAPCONTROL/${zoneId}/IVVC_LARGEST_GRAPH_TIME_FOR_ZONE"/>
                </cti:tabbedContentSelectorContent>
                <cti:msg2 var="voltagePointsTab" key=".voltagePoints.title" />
                <cti:tabbedContentSelectorContent selectorName="${voltagePointsTab}">
                    <div class="scroll-large">
                        <cti:url var="zoneVoltagePointsUrl" value="/capcontrol/ivvc/zone/voltagePoints">
                           <cti:param name="zoneId" value="${zoneId}"/>
                       </cti:url>
                        <%@ include file="voltagePoints.jspf" %>
                    </div>
                </cti:tabbedContentSelectorContent>
            </cti:tabbedContentSelector>
            <tags:boxContainer2 nameKey="ivvcEvents" hideEnabled="true" showInitially="true">
                <input type="hidden" value="${mostRecentDateTime}" id="mostRecentDateTime">
                <div class="scroll-large">
                    <c:if test="${empty events}">
                        <span class="empty-list"> <i:inline key=".ivvcEvents.none"/> </span>
                        <c:set var="tableClass" value="dn" />
                    </c:if>
                    <div>
                        <div>
                            <span class="notes"
                        </div>
                    </div>
                    <table id="recentEventsTable" class="compact-results-table ${tableClass}">
                        <thead>
                            <tr id="recentEventsHeaderRow">
                                <th><i:inline key=".ivvcEvents.deviceName"/></th>
                                <th><i:inline key=".ivvcEvents.description"/></th>
                                <th><i:inline key=".attributes.timestamp"/></th>
                            </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                           <c:forEach var="ccEvent" items="${events}">
                                <tr>
                                    <td class="deviceName">${fn:escapeXml(ccEvent.deviceName)}</td>
                                    <td class="description">${fn:escapeXml(ccEvent.text)}</td>
                                    <td class="formattedTime"><cti:formatDate value="${ccEvent.dateTime}" type="BOTH"/></td>
                                </tr>
                           </c:forEach>
                       </tbody>
                    </table>
                </div>
            </tags:boxContainer2>
        </div>
    </div>
    <div class="dn" id="templateRowContainer">
        <table>
        <tr>
            <td class="deviceName"></td>
            <td class="description"></td>
            <td class="formattedTime"></td>
        </tr>
        </table>
    </div>
</cti:standardPage>