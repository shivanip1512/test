<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="capcontrol" page="ivvc.busView">
	
	<cti:includeScript link="/JavaScript/tableCreation.js" />
	<cti:includeScript link="/JavaScript/simpleDialog.js"/>
	<cti:includeScript link="/JavaScript/picker.js" />
    <cti:includeScript link="/JavaScript/dynamicTable.js"/>
	
	<%@include file="/capcontrol/capcontrolHeader.jspf"%>
    <cti:includeCss link="/WebConfig/yukon/styles/da/ivvc.css"/>
    
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
			jQuery('.selectedZone').removeClass('selectedZone');
			jQuery(span).addClass('selectedZone');
		}
		
		function ivvcAnalysisMessageRecieved(msgDivId) {
		    //assumes data is of type Hash
            return function(data) {
                var msg = data.get('value');
                
                if (msg != null && msg != '' && msg != jQuery(msgDivId + " span:last").html()) {
                    jQuery(msgDivId + " span:last").html(msg);
                    jQuery(msgDivId + " span:first").hide();
                    jQuery(msgDivId + " span:last").show();
                    jQuery(msgDivId + "").effect("highlight", {"color": "#FFFF00"}, 3000);
                } else if (msg == null || msg == '') {
                    jQuery(msgDivId + " span:first").show();
                    jQuery(msgDivId + " span:last").hide();
                }
            };
		}

		if (${hasSubBusControl}) {
		    addCommandMenuBehavior('a[id^="subbusState"]');
		}
 	</script>
    
    <div class="column_12_12">
        <div class="column one">
        
			<tags:boxContainer2 nameKey="zoneList" hideEnabled="true" showInitially="true">
                <table class="zoneListTable compactResultsTable">
                    <tr>
                        <th class="zoneName"><i:inline key=".zoneList.name" /></th>
                        <th class="zoneType"><i:inline key="modules.capcontrol.zoneType"/></th>
                        <th class="lastOperation"><i:inline key="modules.capcontrol.lastOperation"/></th>
                        <th><i:inline key="modules.capcontrol.actions"/></th>
                    </tr>
    				<cti:navigableHierarchy var="zone" depth="depth" hierarchy="${zones}">
    					<cti:url var="zoneDetailUrl" value="/capcontrol/ivvc/zone/detail">
    				    	<cti:param name="zoneId" value="${zone.zoneId}"/>
    				    	<cti:param name="isSpecialArea" value="${isSpecialArea}"/>
    				    </cti:url>
    				    <cti:url var="zoneEditorUrl" value="/capcontrol/ivvc/wizard/zoneEditor">
       						<cti:param name="zoneId" value="${zone.zoneId}"/>
    				    </cti:url>
    					<cti:url var="zoneDeleteUrl" value="/capcontrol/ivvc/wizard/deleteZone">
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
                                <capTags:regulatorThreePhaseTapIndicator zone="${zone}" type="VOLTAGE_REGULATOR" phaseMap="${phaseMap}"/>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when  test="${hasEditingRole}">
                                        <cti:button nameKey="edit" icon="icon-pencil" renderMode="image" onclick="javascript:showZoneEditorWizard('${zoneEditorUrl}');"/>
                                        <cti:button id="delete_${zone.zoneId}" nameKey="remove" renderMode="image" icon="icon-cross"/>
                                        <tags:confirmDialog nameKey=".deleteConfirmation" argument="${zone.name}" submitName="delete" href="${zoneDeleteUrl}" on="#delete_${zone.zoneId}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:button nameKey="disabledEdit" renderMode="image" disabled="true" icon="icon-pencil"/>
                                        <cti:button nameKey="disabledRemove" renderMode="image" disabled="true" icon="icon-cross"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
    				</cti:navigableHierarchy>
                </table>

				<c:if test="${hasEditingRole}">
					<div class="actionArea">
        				<c:if test="${unassignedBanksExist}">
        					<span class="warning fl"><i:inline key=".zoneList.unassignedBanks"/></span>
        				</c:if>
						<cti:button nameKey="add" onclick="javascript:showZoneCreationWizard('${zoneCreatorUrl}');" icon="icon-add"/>
					</div>
				</c:if>
				
			</tags:boxContainer2>

			<tags:boxContainer2 nameKey="busDetail" hideEnabled="true" showInitially="true">
					
				<table class="compactResultsTable">
					<thead>
						<tr>
							<th><i:inline key=".busDetail.table.point"/></th>
							<th><i:inline key=".busDetail.table.value"/></th>
						</tr>
					</thead>
					<tr>
						<td><i:inline key=".busDetail.table.state"/>:&nbsp;</td>
                        <%-- State --%>
                        <td class="wsnw">
                            <capTags:warningImg paoId="${subBusId}" type="SUBBUS"/>
                            <c:if test="${hasSubBusControl}"><a id="subbusState_${subBusId}" href="javascript:void(0);"></c:if>
                            <c:if test="${!hasSubBusControl}"><span id="subbusState_${subBusId}"></c:if>
                                <cti:capControlValue paoId="${subBusId}" type="SUBBUS" format="STATE"/>
                            <c:if test="${hasSubBusControl}"></a></c:if>
                            <c:if test="${!hasSubBusControl}"></span></c:if>
                            <cti:dataUpdaterCallback function="updateStateColorGenerator('subbusState_${subBusId}')"
                                initialize="true" value="SUBBUS/${subBusId}/STATE"/>
                        </td>
					</tr>
					<tr>
						<td><i:inline key=".busDetail.table.volts"/>:&nbsp;</td>
						<td>
						    <cti:capControlValue paoId="${subBusId}" type="SUBBUS" format="VOLTS"/>
						    <cti:classUpdater type="SUBBUS" identifier="${subBusId}/VOLT_QUALITY">
						    	<cti:icon icon="icon-bullet-red"/>
						    </cti:classUpdater>
						</td>
					</tr>
					<tr>
						<td><i:inline key=".busDetail.table.kvar"/>:&nbsp;</td>
						<td>
							<cti:capControlValue paoId="${subBusId}" type="SUBBUS" format="KVAR_LOAD"/>
							<cti:classUpdater type="SUBBUS" identifier="${subBusId}/KVAR_LOAD_QUALITY">
								<cti:icon icon="icon-bullet-red"/>
							</cti:classUpdater>
						</td>
					</tr>
					<tr>
						<td><i:inline key=".busDetail.table.kw"/>:&nbsp;</td>
						<td>
							<cti:capControlValue paoId="${subBusId}" type="SUBBUS" format="KW"/>
                        	<cti:classUpdater type="SUBBUS" identifier="${subBusId}/WATT_QUALITY">
                        		<cti:icon icon="icon-bullet-red"/>
                        	</cti:classUpdater>
                        </td>
					</tr>
                    <c:forEach items="${allSubBusPoints}" var="point">
                        <tr>
                            <td>${point.pointName}:&nbsp;</td>
                            <td>
                                <cti:pointStatusColor pointId="${point.liteID}">
                                    <cti:pointValue pointId="${point.liteID}" format="VALUE"/>
                                </cti:pointStatusColor>
                            </td>
                        </tr>
                    </c:forEach>
				</table>
                <c:if test="${hasEditingRole}">
	                <div class="actionArea">
                        <a href="javascript:void(0);" class="f-show-strategy-details">Strategy Details</a>
	                    <cti:url var="editorUrl" value="/editor/cbcBase.jsf">
	                        <cti:param name="type" value="2"/>
	                        <cti:param name="itemid" value="${subBusId}"/>
	                    </cti:url>
	                    <cti:button nameKey="edit" icon="icon-pencil" href="${editorUrl}"/>
	                </div>
                </c:if>
			</tags:boxContainer2>
			
			<cti:msg2 key=".strategyDetails.title" arguments="${strategyName}" argumentSeparator=":" var="strategyTitle"/>
            <tags:simplePopup id="strategyDetails" title="${strategyTitle}" on=".f-show-strategy-details">
                <table class="compactResultsTable" >
                    <thead>
                        <tr>
                            <th><i:inline key=".strategyDetails.table.setting"/></th>
                            <th><i:inline key=".strategyDetails.table.peak"/></th>
                            <th><i:inline key=".strategyDetails.table.offPeak"/></th>
                            <th><i:inline key=".strategyDetails.table.units"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="setting" items="${strategySettings}">
                            <tr>
                                <td>${fn:escapeXml(setting.type.displayName)}</td>
                                <td>${fn:escapeXml(setting.peakValue)}</td>
                                <td>${fn:escapeXml(setting.offPeakValue)}</td>
                                <td>${fn:escapeXml(setting.type.units)}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <c:if test="${hasEditingRole}">
                    <div class="actionArea">
                        <cti:url var="editorUrl" value="/editor/cbcBase.jsf">
                            <cti:param name="type" value="5"/>
                            <cti:param name="itemid" value="${strategyId}"/>
                        </cti:url>
                        <cti:button nameKey="close" onclick="jQuery('#strategyDetails').dialog('close');"/>
                        <cti:button nameKey="edit" icon="icon-pencil" href="${editorUrl}"/>
                    </div>
                </c:if>
            </tags:simplePopup>
        </div>
        
        <div class="column two nogutter">
        
			<cti:tabbedContentSelector>
				<cti:msg2 var="tabName" key=".voltageProfile.title" />
				<cti:tabbedContentSelectorContent selectorName="${tabName}">
					<c:set var="chartId" value="sub_${subBusId}_chart" />
					<cti:url var="chartJsonDataUrl" value="/capcontrol/ivvc/bus/chart">
						<cti:param name="subBusId" value="${subBusId}" />
					</cti:url>
					<flot:ivvcChart chartId="${chartId}"
						jsonDataAndOptions="${graphAsJSON}"
						title="${graphSettings.graphTitle}" />

					<cti:dataUpdaterCallback function="Yukon.Flot.reloadChartIfExpired({chartId:'${chartId}', dataUrl:'${chartJsonDataUrl}'})"
                                             initialize="false" largestTime="CAPCONTROL/${subBusId}/IVVC_LARGEST_GRAPH_TIME_FOR_SUBBUS"/>
				</cti:tabbedContentSelectorContent>
				<cti:msg2 var="voltagePointsTab" key=".voltagePoints.title" />
				<cti:tabbedContentSelectorContent selectorName="${voltagePointsTab}">
					<div class="scrollingContainer_large">
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
				</cti:tabbedContentSelectorContent>
			</cti:tabbedContentSelector>
            
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