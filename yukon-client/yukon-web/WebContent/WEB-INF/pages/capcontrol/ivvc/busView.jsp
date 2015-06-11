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
    
    <%@ include file="/capcontrol/capcontrolHeader.jspf" %>
    
    <cti:url var="zoneCreatorUrl" value="/capcontrol/ivvc/wizard/zoneCreationWizard">
        <cti:param name="subBusId" value="${subBusId}"/>
    </cti:url>
    
    <cti:msg2 key="yukon.web.modules.capcontrol.ivvc.zoneWizard.creation.title" var="zoneCreationWizardTitle"/>
    <cti:msg2 key="yukon.web.modules.capcontrol.ivvc.zoneWizard.editor.title" var="zoneEditorWizardTitle"/>
    
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
        
        function ivvcAnalysisMessageRecieved(msgDivId) {
            return function(data) {
                var msg = data.value;
                
                if (msg != null && msg != '' && msg != $(msgDivId + " span:last").html()) {
                    $(msgDivId + " span:last").html(msg);
                    $(msgDivId + " span:first").hide();
                    $(msgDivId + " span:last").show();
                    $(msgDivId + "").effect("highlight", {"color": "#FFFF00"}, 3000);
                } else if (msg == null || msg == '') {
                    $(msgDivId + " span:first").show();
                    $(msgDivId + " span:last").hide();
                }
            };
        }
        
        if (${hasSubBusControl}) {
            addCommandMenuBehavior('a[id^="subbusState"]');
        }
    </script>
    
    <c:set var="dividerAdded" value="false" />
    
    <c:if test="${hasSubBusControl}">
        
        <div class="js-page-additional-actions dn">
            <c:if test="${not dividerAdded}">
                <li class="divider">
                <c:set var="dividerAdded" value="true" />
            </c:if>
            <cm:dropdownOption linkId="subbusState_${subBusId}" key="defaults.actions" icon="icon-cog"/>
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
            
            <tags:boxContainer2 nameKey="zoneList" hideEnabled="true" showInitially="true">
                <table class="zoneListTable compact-results-table">
                    <thead>
                        <tr>
                            <th class="zoneName"><i:inline key=".zoneList.name" /></th>
                            <th class="zoneType"><i:inline key="modules.capcontrol.zoneType"/></th>
                            <th class="lastOperation"><i:inline key="modules.capcontrol.lastOperation"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <cti:navigableHierarchy var="zone" depth="depth" hierarchy="${zones}">
                            <cti:url var="zoneDetailUrl" value="/capcontrol/ivvc/zone/detail">
                                <cti:param name="zoneId" value="${zone.zoneId}"/>
                            </cti:url>
                            <tr>
                                <td class="zoneName">
                                    <c:if test="${depth > 0}">
                                        <c:forEach begin="0" end="${depth-1}">
                                            <span class="leftIndent"></span>
                                        </c:forEach>
                                    </c:if>
                                    <a href="${zoneDetailUrl}">${fn:escapeXml(zone.name)}</a>
                                </td>
                                <td class="zoneType">
                                    <c:choose>
                                        <c:when test="${zone.zoneType == singlePhaseZone}">
                                            <i:inline key="${zone.zoneType}"/>:&nbsp;<i:inline key="${zone.regulator.phase}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <i:inline key="${zone.zoneType}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="lastOperation">
                                    <capTags:regulatorThreePhaseTapIndicator zone="${zone}" phaseMap="${phaseMap}"/>
                                </td>
                            </tr>
                        </cti:navigableHierarchy>
                    </tbody>
                </table>

                <c:if test="${hasEditingRole}">
                    <div class="action-area">
                        <c:if test="${unassignedBanksExist}">
                            <span class="warning fl"><i:inline key=".zoneList.unassignedBanks"/></span>
                        </c:if>
                        <cti:button nameKey="add" onclick="javascript:showZoneCreationWizard('${zoneCreatorUrl}');" icon="icon-add"/>
                    </div>
                </c:if>
                
            </tags:boxContainer2>
            
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
            
            <cti:msg2 key=".strategyDetails.title" arguments="${strategyName}" var="strategyTitle"/>
            <div class="dn js-strategy-details" data-title="${strategyTitle}">
                <c:if test="${hasEditingRole}">
                    <cti:url var="url" value="/editor/cbcBase.jsf">
                        <cti:param name="type" value="5"/>
                        <cti:param name="itemid" value="${strategyId}"/>
                    </cti:url>
                    <i:inline key=".strategyDetails.link"/><a href="${url}">${fn:escapeXml(strategyName)}</a>
                </c:if>
                <table class="full-width striped" >
                    <thead>
                        <tr>
                            <th></th>
                            <th><i:inline key=".strategyDetails.table.peak"/></th>
                            <th><i:inline key=".strategyDetails.table.offPeak"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="setting" items="${strategySettings}">
                            <tr>
                                <td>${fn:escapeXml(setting.type.displayName)}</td>
                                <td>
                                    ${fn:escapeXml(setting.peakValue)}&nbsp;
                                    ${fn:escapeXml(setting.type.units)}
                                </td>
                                <td>
                                    ${fn:escapeXml(setting.offPeakValue)}&nbsp;
                                    ${fn:escapeXml(setting.type.units)}
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
                    <cti:url var="chartJsonDataUrl" value="/capcontrol/ivvc/bus/chart">
                        <cti:param name="subBusId" value="${subBusId}" />
                    </cti:url>
                    <flot:ivvcChart chartId="${chartId}"
                        jsonDataAndOptions="${graphAsJSON}"
                        title="${graphSettings.graphTitle}" />

                    <cti:dataUpdaterCallback function="yukon.flot.reloadChartIfExpired({chartId:'${chartId}', dataUrl:'${chartJsonDataUrl}'})"
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
            
            <tags:boxContainer2 nameKey="ivvcAnalysisContainer">
                <div id="ivvcAnalysisMsg" class="wsn">
                    <span class="empty-list"><i:inline key=".noIvvcMessage"/></span>
                    <span></span>
                </div>
                <cti:dataUpdaterCallback function="ivvcAnalysisMessageRecieved(['div#ivvcAnalysisMsg'])" initialize="true" value="CAPCONTROL/${subBusId}/IVVC_ANALYSIS_MESSAGE" />
            </tags:boxContainer2>
        </div>
    </div>
</cti:standardPage>