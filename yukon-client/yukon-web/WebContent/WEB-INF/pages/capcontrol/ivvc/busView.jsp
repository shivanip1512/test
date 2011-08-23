<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.capcontrol.ivvc.busView">

<cti:standardPage title="${title}" module="capcontrol" >
	
	<cti:includeScript link="/JavaScript/tableCreation.js" />
	<cti:includeScript link="/JavaScript/simpleDialog.js"/>
	<cti:includeScript link="/JavaScript/picker.js" />
    <cti:includeScript link="/JavaScript/dynamicTable.js"/>
    <cti:includeScript link="/JavaScript/ivvcAmCharts.js" />
	
	<%@include file="/capcontrol/capcontrolHeader.jspf"%>
    <cti:includeCss link="/capcontrol/css/ivvc.css"/>
    
    <cti:url var="zoneCreatorUrl" value="/spring/capcontrol/ivvc/wizard/zoneCreationWizard">
    	<cti:param name="subBusId" value="${subBusId}"/>
    </cti:url>

    <c:set var="chartId" value="subBus_${subBusId}_IVVCGraph" />

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
 	</script>

    <cti:standardMenu/>
    
    <cti:url value="/spring/capcontrol/tier/substations" var="substationAddress">
    	<cti:param name="areaId" value="${areaId}"/>
    	<cti:param name="isSpecialArea" value="${isSpecialArea}"/>
    </cti:url>
    
	<cti:url value="/spring/capcontrol/tier/feeders" var="feederAddress">
    	<cti:param name="areaId" value="${areaId}"/>
    	<cti:param name="subStationId" value="${subStationId}"/>
    	<cti:param name="isSpecialArea" value="${isSpecialArea}"/>
    </cti:url>
    
	<cti:breadCrumbs>
	    <cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home"/>
		<c:choose>
    		<c:when test="${isSpecialArea}">
    		  	<cti:crumbLink url="/spring/capcontrol/tier/areas?isSpecialArea=${isSpecialArea}" title="Special Substation Areas" />
    		</c:when>
    		<c:otherwise>
    			<cti:crumbLink url="/spring/capcontrol/tier/areas?isSpecialArea=${isSpecialArea}" title="Substation Areas" />
    		</c:otherwise>
    	</c:choose>
    
        <cti:crumbLink url="${substationAddress}" title="${areaName}" />
        <cti:crumbLink url="${feederAddress}" title="${subStationName}" />
		
		<cti:crumbLink title="${subBusName}" />
	</cti:breadCrumbs>
	
	<cti:dataGrid cols="2" tableClasses="ivvcGridLayout">
	
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
			</tags:boxContainer2>
			
			<br>

			<tags:boxContainer2 nameKey="zoneList" hideEnabled="true" showInitially="true">
                <table class="zoneListTable compactResultsTable">
                    <tr>
                        <th class="zoneName"><i:inline key=".zoneList.name" /></th>
                        <th class="zoneType"><i:inline key="modules.capcontrol.zoneType"/></th>
                        <th class="lastOperation"><i:inline key="modules.capcontrol.lastOperation"/></th>
                        <th class="rightActionColumn"><i:inline key="modules.capcontrol.actions"/></th>
                    </tr>
    				<cti:navigableHierarchy var="zone" depth="depth" hierarchy="${zones}">
    					<cti:url var="zoneDetailUrl" value="/spring/capcontrol/ivvc/zone/detail">
    				    	<cti:param name="zoneId" value="${zone.zoneId}"/>
    				    	<cti:param name="isSpecialArea" value="${isSpecialArea}"/>
    				    </cti:url>
    				    <cti:url var="zoneEditorUrl" value="/spring/capcontrol/ivvc/wizard/zoneEditor">
       						<cti:param name="zoneId" value="${zone.zoneId}"/>
    				    </cti:url>
    					<cti:url var="zoneDeleteUrl" value="/spring/capcontrol/ivvc/wizard/deleteZone">
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
                            <td class="rightActionColumn">
                                <c:choose>
                                    <c:when  test="${hasEditingRole}">
                                        <cti:button nameKey="edit" renderMode="image" onclick="javascript:showZoneEditorWizard('${zoneEditorUrl}');"/>
                                        <cti:button id="delete_${zone.zoneId}" nameKey="remove" renderMode="image" href="${zoneDeleteUrl}"/>
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
				</table>
			</tags:boxContainer2>
		</cti:dataGridCell>
		<cti:dataGridCell>
			<tags:boxContainer2 nameKey="voltageProfile" hideEnabled="true" showInitially="true">
				<!--Chart -->
		        <c:set var="amChartsProduct" value="amline"/>
		        <c:url var="amChartFile" scope="page" value="/spring/capcontrol/ivvc/bus/chart">
		        	<cti:param name="subBusId" value="${subBusId}"/>
		        </c:url>
		        <c:url var="amSrc" scope="page" value="/JavaScript/amChart/${amChartsProduct}.swf">
		            <c:param name="${amChartsProduct}_path" value="/JavaScript/amChart/" />
		            <c:param name="${amChartsProduct}_flashWidth" value="100%" />
		            <c:param name="${amChartsProduct}_flashHeight" value="100%" />
		            <c:param name="${amChartsProduct}_preloaderColor" value="#000000" />
		            <c:param name="${amChartsProduct}_settingsFile" value="${amChartFile}" />
		        </c:url>
		        
		        <c:url var="expressInstallSrc" scope="page" value="/JavaScript/expressinstall.swf" />
		        <cti:includeScript link="/JavaScript/swfobject.js"/>
		
		        <cti:uniqueIdentifier var="uniqueId" prefix="flashDiv_"/>
		        <div id="${uniqueId}">
		            <div style="width:90%;text-align:center;">
		                <br>
		                <br>
		                <h4>The Adobe Flash Player is required to view this graph.</h4>
		                <br>
		                Please download the latest version of the Flash Player by following the link below.
		                <br>
		                <br>
		                <a href="http://www.adobe.com" target="_blank"><img border="0" src="<c:url value="/WebConfig/yukon/Icons/visitadobe.gif"/>" /></a>
		                <br>
		            </div>
		        </div>
		        
		        <c:set var="swfWidth" value="100%"/>
		        
		        <script type="text/javascript">
                   var so = new SWFObject("${amSrc}", "${chartId}", "${swfWidth}", "300", "8", "#FFFFFF");
                   so.useExpressInstall('${expressInstallSrc}');
                   so.addVariable("chart_id", "${chartId}");
                   so.write("${uniqueId}");
		        </script>
                
                <cti:dataUpdaterCallback function="checkGraphExpired('${chartId}')" initialize="true" largestTime="CAPCONTROL/${subBusId}/IVVC_LARGEST_GRAPH_TIME_FOR_SUBBUS"/>

			</tags:boxContainer2>
		</cti:dataGridCell>
	</cti:dataGrid>
	
</cti:standardPage>
</cti:msgScope>