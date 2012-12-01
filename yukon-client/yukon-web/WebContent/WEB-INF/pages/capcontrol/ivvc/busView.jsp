<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="capcontrol" page="ivvc.busView">
	
	<cti:includeScript link="/JavaScript/tableCreation.js" />
	<cti:includeScript link="/JavaScript/simpleDialog.js"/>
	<cti:includeScript link="/JavaScript/picker.js" />
    <cti:includeScript link="/JavaScript/dynamicTable.js"/>
    <cti:includeScript link="/JavaScript/ivvcAmCharts.js" />
	
	<%@include file="/capcontrol/capcontrolHeader.jspf"%>
    <cti:includeCss link="/WebConfig/yukon/styles/da/ivvc.css"/>
    
    <cti:url var="zoneCreatorUrl" value="/capcontrol/ivvc/wizard/zoneCreationWizard">
    	<cti:param name="subBusId" value="${subBusId}"/>
    </cti:url>

    <cti:msg2 key="yukon.web.modules.capcontrol.ivvc.zoneWizard.creation.title" var="zoneCreationWizardTitle"/>
    <cti:msg2 key="yukon.web.modules.capcontrol.ivvc.zoneWizard.editor.title" var="zoneEditorWizardTitle"/>

    <!-- Zone Wizard Dialog -->
    <tags:simpleDialog id="zoneWizardPopup" title="" styleClass="smallSimplePopup"/>

    <script type="text/javascript">
    	function showZoneCreationWizard(url) {
			openSimpleDialog('zoneWizardPopup', url, "${zoneCreationWizardTitle}", null, null, 'get');
		}

    	function showZoneEditorWizard(url) {
			openSimpleDialog('zoneWizardPopup', url, "${zoneEditorWizardTitle}", null, null, 'get');
		}
    	
		function selectZone(event) {
			var span = event.target;

			$$('.selectedZone').each( function(n){ 
				n.removeClassName('selectedZone');
			});

			span.addClassName('selectedZone');
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
    

	<cti:dataGrid cols="2" tableClasses="ivvcGridLayout twoColumnLayout">
	
		<cti:dataGridCell>
			<tags:boxContainer2 nameKey="strategyDetails" arguments="${strategyName}" argumentSeparator=":" hideEnabled="true" showInitially="true">
				<table class="compactResultsTable" >
					<tr>
						<th><i:inline key=".strategyDetails.table.setting" /></th>
						<th><i:inline key=".strategyDetails.table.peak"/></th>
						<th><i:inline key=".strategyDetails.table.offPeak"/></th>
						<th><i:inline key=".strategyDetails.table.units"/></th>
					</tr>
					<c:forEach var="setting" items="${strategySettings}">
						<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
							<td>
								<spring:escapeBody htmlEscape="true">${setting.type.displayName}</spring:escapeBody>
							</td>
							<td>
								<spring:escapeBody htmlEscape="true">${setting.peakValue}</spring:escapeBody>
							</td>
							<td>
								<spring:escapeBody htmlEscape="true">${setting.offPeakValue}</spring:escapeBody>
							</td>
							<td>
								<spring:escapeBody htmlEscape="true">${setting.type.units}</spring:escapeBody>
							</td>
						</tr>
					</c:forEach>
				</table>
				<c:if test="${hasEditingRole}">
	                <div class="actionArea">
						<cti:url var="editorUrl" value="/editor/cbcBase.jsf">
							<cti:param name="type" value="5"/>
							<cti:param name="itemid" value="${strategyId}"/>
						</cti:url>
						<cti:button nameKey="edit" href="${editorUrl}"/>
	                </div>
				</c:if>
			</tags:boxContainer2>
			
			<br>

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
                
                        <tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
                            <td class="zoneName">
                                <c:if test="${depth > 0}">
                                    <c:forEach begin="0" end="${depth-1}">
                                        <span class="leftIndent"></span>
                                    </c:forEach>
                                </c:if>
                                <a href="${zoneDetailUrl}">
                                    <spring:escapeBody htmlEscape="true">${zone.name}</spring:escapeBody>
                                </a>
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
                                <capTags:regulatorThreePhaseTapIndicator zone="${zone}" type="VOLTAGE_REGULATOR"
                                    phaseMap="${phaseMap}"/>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when  test="${hasEditingRole}">
                                        <cti:button nameKey="edit" renderMode="image" onclick="javascript:showZoneEditorWizard('${zoneEditorUrl}');"/>
                                        <cti:button id="delete_${zone.zoneId}" nameKey="remove" renderMode="image"/>
                                        <tags:confirmDialog nameKey=".deleteConfirmation" argument="${zone.name}" submitName="delete" href="${zoneDeleteUrl}" on="#delete_${zone.zoneId}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:button nameKey="disabledEdit" renderMode="image" disabled="true"/>
                                        <cti:button nameKey="disabledRemove" renderMode="image" disabled="true"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
    				</cti:navigableHierarchy>
                </table>

				<c:if test="${hasEditingRole}">
					<div class="actionArea">
        				<c:if test="${unassignedBanksExist}">
        					<span class="strongWarningMessage fl"><i:inline key=".zoneList.unassignedBanks"/></span>
        				</c:if>
						<cti:button nameKey="add" onclick="javascript:showZoneCreationWizard('${zoneCreatorUrl}');"/>
					</div>
				</c:if>
				
			</tags:boxContainer2>
			<br>
			<tags:boxContainer2 nameKey="busDetail" hideEnabled="true" showInitially="true">
				<tags:alternateRowReset/>	
				<table class="compactResultsTable">
					<thead>
						<tr>
							<th><i:inline key=".busDetail.table.point"/></th>
							<th><i:inline key=".busDetail.table.value"/></th>
						</tr>
					</thead>
					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
						<td><i:inline key=".busDetail.table.state"/>: </td>
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
					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
						<td><i:inline key=".busDetail.table.volts"/>: </td>
						<td>
						    <cti:capControlValue paoId="${subBusId}" type="SUBBUS" format="VOLTS"/>
						    <cti:classUpdater type="SUBBUS" identifier="${subBusId}/VOLT_QUALITY">
						    	<img class="tierImg" src="/WebConfig/yukon/Icons/bullet_red.gif">
						    </cti:classUpdater>
						</td>
					</tr>
					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
						<td><i:inline key=".busDetail.table.kvar"/>: </td>
						<td>
							<cti:capControlValue paoId="${subBusId}" type="SUBBUS" format="KVAR_LOAD"/>
							<cti:classUpdater type="SUBBUS" identifier="${subBusId}/KVAR_LOAD_QUALITY">
								<img class="tierImg"  src="/WebConfig/yukon/Icons/bullet_red.gif">
							</cti:classUpdater>
						</td>
					</tr>
					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
						<td><i:inline key=".busDetail.table.kw"/>: </td>
						<td>
							<cti:capControlValue paoId="${subBusId}" type="SUBBUS" format="KW"/>
                        	<cti:classUpdater type="SUBBUS" identifier="${subBusId}/WATT_QUALITY">
                        		<img class="tierImg"  src="/WebConfig/yukon/Icons/bullet_red.gif">
                        	</cti:classUpdater>
                        </td>
					</tr>
                    <c:forEach items="${allSubBusPoints}" var="point">
                        <tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
                            <td>${point.pointName}: </td>
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
	                    <cti:url var="editorUrl" value="/editor/cbcBase.jsf">
	                        <cti:param name="type" value="2"/>
	                        <cti:param name="itemid" value="${subBusId}"/>
	                    </cti:url>
	                    <cti:button nameKey="edit" href="${editorUrl}"/>
	                </div>
                </c:if>
			</tags:boxContainer2>
		</cti:dataGridCell>
		<cti:dataGridCell>
			<cti:tabbedContentSelector>
				<cti:msg2 var="tabName" key=".voltageProfile.title" />
				<cti:tabbedContentSelectorContent selectorName="${tabName}">
					<%@ include file="aboveGraph.jspf"%>

					<c:set var="chartId" value="subBus_${subBusId}_IVVCGraph" />
					<input type="hidden" value="${chartId}" id="ivvcChartIdValue" />
					<c:url var="amChartFile" scope="page" value="/capcontrol/ivvc/bus/chart">
						<cti:param name="subBusId" value="${subBusId}" />
					</c:url>
					<tags:amchart chartType="amline" settingsUrl="${amChartFile}" chartId="${chartId}" cssClass="ivvcGraphContainer"/>
	                <cti:dataUpdaterCallback function="checkGraphExpired('${chartId}')" initialize="true" largestTime="CAPCONTROL/${subBusId}/IVVC_LARGEST_GRAPH_TIME_FOR_SUBBUS"/>
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
            
            <br>
            <tags:boxContainer2 nameKey="ivvcAnalysisContainer">
                <div id="ivvcAnalysisMsg" class="wsn">
                    <span class="strongMessage"><i:inline key=".noIvvcMessage"/></span>
                    <span></span>
                </div>
                <cti:dataUpdaterCallback function="ivvcAnalysisMessageRecieved(['div#ivvcAnalysisMsg'])" initialize="true" value="CAPCONTROL/${subBusId}/IVVC_ANALYSIS_MESSAGE" />
            </tags:boxContainer2>
		</cti:dataGridCell>
	</cti:dataGrid>
	
</cti:standardPage>