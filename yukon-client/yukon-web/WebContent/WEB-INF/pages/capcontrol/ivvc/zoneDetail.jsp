<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.capcontrol.ivvc.zoneDetail">

<cti:standardPage title="${title}" module="capcontrol">
	<cti:includeScript link="/JavaScript/tableCreation.js" />
	<cti:includeScript link="/JavaScript/simpleDialog.js"/>
	<cti:includeScript link="/JavaScript/picker.js" />
    <cti:includeScript link="/JavaScript/amChart.js" />

	<%@include file="/capcontrol/capcontrolHeader.jspf"%>
	<cti:includeCss link="/capcontrol/css/ivvc.css"/>

    <c:set var="chartId" value="zone_${subBusId}_IVVCGraph" />

	<script type="text/javascript">
	
		function editDelta(id) {
			$('viewDelta_' + id).hide();
			$('editDelta_' + id).show();
			$('editDelta_' + id).down().focus();
	    }

		function saveDelta(id) {
            var newDelta = $('editDelta_' + id).down().value;
            var newStaticValue = $('staticDelta_' + id).checked;
            
            if(newDelta.length != 0) {
                $('delta').value = newDelta.escapeHTML();
                $('staticDelta').value = newStaticValue;
                $('bankId').value = id.split('_')[0]; 
               	$('pointId').value = id.split('_')[1];
                $('deltaForm').submit();
            }
	    }

	    function cancelEdit(id) {
			$('viewDelta_' + id).show();
			$('editDelta_' + id).hide();
	    }

	    function saveOrCancel(event, id) {
            var key = event.keyCode;
            if(key == 27) {
                /* Escape Key */
            	cancelEdit(id);
            } else if (key == 13) {
                /* Enter Key */
                saveDelta(id);
            }
            return (key != 13);
	    }
	    
    	function showZoneWizard(url) {
			openSimpleDialog('tierContentPopup', url, 'Zone Wizard', null, null, 'get');
		}
	</script>

    
    <cti:standardMenu/>
    
    <cti:url var="substationAddress" value="/spring/capcontrol/tier/substations">
    	<cti:param name="areaId" value="${areaId}"/>
    	<cti:param name="isSpecialArea" value="${isSpecialArea}"/>
    </cti:url>
    
	<cti:url var="feederAddress" value="/spring/capcontrol/tier/feeders">
    	<cti:param name="areaId" value="${areaId}"/>
    	<cti:param name="subStationId" value="${subStationId}"/>
    	<cti:param name="isSpecialArea" value="${isSpecialArea}"/>
    </cti:url>
    
	<cti:url var="ivvcBusViewAddress" value="/spring/capcontrol/ivvc/bus/detail" >
    	<cti:param name="subBusId" value="${subBusId}"/>
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
		<cti:crumbLink url="${ivvcBusViewAddress}" title="${subBusName}" />
		<cti:crumbLink title="${zoneName}" />
	</cti:breadCrumbs>

    <c:choose>
        <c:when test="${hasEditingRole}">
            <c:set var="editInfoImage" value="/WebConfig/yukon/Icons/pencil.gif"/>
        </c:when>
        <c:otherwise>
            <c:set var="editInfoImage" value="/WebConfig/yukon/Icons/information.gif"/>
        </c:otherwise>
    </c:choose>
    

	<cti:url var="baseUrl" value="/spring/capcontrol/ivvc/zone/detail" />
	<cti:url var="zoneEditorUrl" value="/spring/capcontrol/ivvc/wizard/zoneEditor">
    	<cti:param name="zoneId" value="${zoneId}"/>
    </cti:url>
    
	<cti:dataGrid cols="2" tableClasses="ivvcGridLayout">
	
		<cti:dataGridCell>			
			
			<tags:boxContainer2 nameKey="details" hideEnabled="true" showInitially="true">
				<tags:alternateRowReset/>			
				<table class="compactResultsTable">
					<tr>
						<th></th>
						<th><i:inline key=".details.table.name"/></th>
						<th><i:inline key=".details.table.actions"/></th>
					</tr>
					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
						<td><B><i:inline key=".details.table.zone"/></B></td>
						<td>
							<spring:escapeBody htmlEscape="true">${zoneName}</spring:escapeBody>
						</td>
						<td>
							<c:choose>
								<c:when test="${hasEditingRole}">
									<a href="javascript:showZoneWizard('${zoneEditorUrl}');">
										<cti:img key="edit"/>
									</a>
								</c:when>
								<c:otherwise>
									<cti:img key="disabledEdit"/>
								</c:otherwise>
							</c:choose>
                        </td>
					</tr>
				
					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
						<td><B><i:inline key=".details.table.regulator"/></B></td>
						<td>
							<capTags:regulatorModeIndicator paoId="${regulatorId}" type="VOLTAGE_REGULATOR"/>
							<spring:escapeBody htmlEscape="true">${regulatorName}</spring:escapeBody>
						</td>
						<td>
							<a title="Edit" href="/editor/cbcBase.jsf?type=2&amp;itemid=${regulatorId}" class="tierIconLink">
                            	<img alt="Edit" class="tierImg" src="${editInfoImage}">
                            </a>
                        </td>
					</tr>
				</table>
			</tags:boxContainer2>
			<br>
			<tags:boxContainer2 nameKey="actions" hideEnabled="true" showInitially="true">			
				<div>
					<cti:labeledImg key="scan" href="javascript:executeCommand('${regulatorId}',
													'${scanCommandHolder.cmdId}',
													'${scanCommandHolder.commandName}',
													'${regulatorType}',
													'false');"/>
				</div>
				<div>
					<cti:labeledImg key="up" href="javascript:executeCommand('${regulatorId}',
													'${tapUpCommandHolder.cmdId}',
													'${tapUpCommandHolder.commandName}',
													'${regulatorType}',
													'false');"/>
				</div>
				<div>
					<cti:labeledImg key="down" href="javascript:executeCommand('${regulatorId}',
													'${tapDownCommandHolder.cmdId}',
													'${tapDownCommandHolder.commandName}',
													'${regulatorType}',
													'false');"/>
				</div>
				<div>
					<cti:labeledImg key="enable" href="javascript:executeCommand('${regulatorId}',
													'${enableRemoteCommandHolder.cmdId}',
													'${enableRemoteCommandHolder.commandName}',
													'${regulatorType}',
													'false');"/>
				</div>
				<div>
					<cti:labeledImg key="disable" href="javascript:executeCommand('${regulatorId}',
													'${disableRemoteCommandHolder.cmdId}',
													'${disableRemoteCommandHolder.commandName}',
													'${regulatorType}',
													'false');"/>
				</div>
			</tags:boxContainer2>
			<br>
			<tags:boxContainer2 nameKey="ivvcEvents" hideEnabled="true" showInitially="true">
				<tags:alternateRowReset/>
				<c:choose>
					<c:when test="${empty events}">
						<i:inline key=".ivvcEvents.none"/>
					</c:when>
					<c:otherwise>
						<div class="historyContainer ">
							<table class="compactResultsTable ">
								<tr>
									<th><i:inline key=".ivvcEvents.description"/></th>
									<th><i:inline key=".attributes.timestamp"/></th>
								</tr>
					            <c:forEach var="ccEvent" items="${events}">
									<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
										<td><spring:escapeBody htmlEscape="true">${ccEvent.text}</spring:escapeBody></td>
										<td><cti:formatDate value="${ccEvent.dateTime}" type="BOTH"/></td>
									</tr>
					            </c:forEach>
							</table>
						</div>	
					</c:otherwise>
				</c:choose>
				
			</tags:boxContainer2>
			<br>
			<tags:boxContainer2 nameKey="attributes" hideEnabled="true" showInitially="true">
				<tags:alternateRowReset/>
		        <table class="compactResultsTable">
		            <tr style="text-align: left;">
		                <th><i:inline key=".attributes.name"/></th>
		                <th><i:inline key=".attributes.value"/></th>
		                <th><i:inline key=".attributes.timestamp"/></th>
		            </tr>
		            
		            <c:forEach var="point" items="${regulatorPointMappings}">
		                <tr class="<tags:alternateRow odd="" even="altRow"/>">
		                    <td><spring:escapeBody htmlEscape="true">${point.attribute.description}</spring:escapeBody></td>
		                    <td>
		                        <c:choose>
		                            <c:when test="${point.pointId > 0}">
		                                <cti:pointValue pointId="${point.pointId}" format="VALUE"/>
		                            </c:when>
		                            <c:otherwise>
		                                ---
		                            </c:otherwise>
		                        </c:choose>
		                    </td>
		                    <td>
		                        <c:choose>
		                            <c:when test="${point.pointId > 0}">
		                                <cti:pointValue pointId="${point.pointId}" format="DATE"/>
		                            </c:when>
		                            <c:otherwise>
		                                ---
		                            </c:otherwise>
		                        </c:choose>
		                    </td>
		                </tr>
		            </c:forEach>
		            
		        </table>
			</tags:boxContainer2>
		</cti:dataGridCell>
		
		<cti:dataGridCell>
			<tags:boxContainer2 nameKey="voltageProfile" hideEnabled="true" showInitially="true">
				<!--Chart -->
		        <c:set var="amChartsProduct" value="amxy"/>
		        <c:url var="amChartFile" scope="page" value="/spring/capcontrol/ivvc/zone/chart">
		        	<cti:param name="zoneId" value="${zoneId}"/>
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
                
                <cti:dataUpdaterCallback function="checkGraphExpired('${chartId}')" initialize="true" largestTime="CAPCONTROL/${zoneId}/IVVC_LARGEST_GRAPH_TIME_FOR_ZONE"/>
                
			</tags:boxContainer2>
			
			<br>
			
			<tags:boxContainer2 nameKey="capBanks" hideEnabled="true" showInitially="true">
				<tags:alternateRowReset/>
				<c:if test="${unassignedBanksExist}">
					<div class="strongWarningMessage"><i:inline key=".capBanks.unassignedBanks"/></div>
				</c:if>
				<table class="compactResultsTable ">
					<tr>
						<th><i:inline key=".capBanks.cbcName"/></th>
						<th><i:inline key=".capBanks.bankName"/></th>
						<th><i:inline key=".capBanks.bankState"/></th>
						<th><i:inline key=".capBanks.bankVoltage"/></th>
					</tr>
		            <c:forEach var="capBank" items="${capBankList}">
		                <tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
		                    <td>
		                    	<c:if test="${capBank.notAssignedToZone}"><span class="strongWarningMessage">*</span></c:if>
		                    	<spring:escapeBody htmlEscape="true">${capBank.controlDevice.paoName}</spring:escapeBody>
		                    </td>
		                    <td><spring:escapeBody htmlEscape="true">${capBank.capBankDevice.ccName}</spring:escapeBody></td>
		                    <td>
		                    	<cti:capBankStateColor paoId="${capBank.capBankDevice.ccId}" type="CAPBANK" format="CB_STATUS_COLOR">
    					       		<cti:capControlValue paoId="${capBank.capBankDevice.ccId}" type="CAPBANK" format="CB_STATUS"/>
                        		</cti:capBankStateColor>
                        	</td>
		                    <td>
								<c:choose>
		                            <c:when test="${capBank.voltagePointId > 0}">
		                                <cti:pointValue pointId="${capBank.voltagePointId}" format="VALUE"/>
		                            </c:when>
		                            <c:otherwise>
		                                ---
		                            </c:otherwise>
		                        </c:choose>
		                    </td>
		                </tr>
		            </c:forEach>
					
				</table>
			</tags:boxContainer2>
		</cti:dataGridCell>	
	</cti:dataGrid>
	<br>
	
	<cti:msg2 var="deltasTitle" key=".deltas.title"/>
	<div>
		<tags:pagedBox title="${deltasTitle}" searchResult="${searchResults}" 
					   baseUrl="${baseUrl}" showAllUrl="${baseUrl}">
			<tags:alternateRowReset/>					
			
			<form id="deltaForm" action="/spring/capcontrol/ivvc/zone/deltaUpdate">
			    <input type="hidden" name="bankId" id="bankId">
	            <input type="hidden" name="pointId" id="pointId">
	            <input type="hidden" name="staticDelta" id="staticDelta">
	            <input type="hidden" name="delta" id="delta">
	            <input type="hidden" name="zoneId" id="zoneId" value="${zoneId}">
	
				<table id="deltaTable" class="compactResultsTable" >
					<tr>
						<th><i:inline key=".deltas.cbcName"/></th>
						<th><i:inline key=".deltas.bankName"/></th>
						<th><i:inline key=".deltas.deviceName"/></th>
						<th><i:inline key=".deltas.pointName"/></th>
						<th><i:inline key=".deltas.preOp"/></th>
						<th><i:inline key=".deltas.static"/></th>
						<th><i:inline key=".deltas.delta"/></th>
					</tr>
					
					<c:if test="${searchResults.hitCount == 0}">
						<tr>
							<td><i:inline key=".deltas.emptyTable"/></td>
						</tr>
					</c:if>
					
					<c:forEach var="pointDelta" items="${searchResults.resultList}">
						<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
							<td style="width:13%"><spring:escapeBody htmlEscape="true">${pointDelta.cbcName}</spring:escapeBody></td>
							<td style="width:13%"><spring:escapeBody htmlEscape="true">${pointDelta.bankName}</spring:escapeBody></td>
							<td style="width:13%"><spring:escapeBody htmlEscape="true">${pointDelta.affectedDeviceName}</spring:escapeBody></td>
							<td style="width:15%"><spring:escapeBody htmlEscape="true">${pointDelta.affectedPointName}</spring:escapeBody></td>
							<td style="width:10%"><spring:escapeBody htmlEscape="true">${pointDelta.preOpValue}</spring:escapeBody></td>
	                      		<c:choose>
	                       		<c:when test="${hasEditingRole}">
									<td style="width:8%"><input type="checkbox" id="staticDelta_${pointDelta.bankId}_${pointDelta.pointId}"
										onclick="saveDelta('${pointDelta.bankId}_${pointDelta.pointId}')" 
										<c:choose>
						                	<c:when test="${pointDelta.staticDelta}">
				                                checked="checked"
				                            </c:when>
				                        </c:choose> 
	                        		></td>
									<td class="editable" style="width:100%">
										<div id="viewDelta_${pointDelta.bankId}_${pointDelta.pointId}" title="Click to edit."
										     onclick="editDelta('${pointDelta.bankId}_${pointDelta.pointId}')">
											<spring:escapeBody htmlEscape="true">${pointDelta.delta}</spring:escapeBody>
										</div>
										<div id="editDelta_${pointDelta.bankId}_${pointDelta.pointId}" style="display:none">
											<input type="text" style="margin-right: 5px;width:30px;" name="editDeltaInput"
												   onKeyPress="return saveOrCancel(event, '${pointDelta.bankId}_${pointDelta.pointId}')"  
				                                   value="<spring:escapeBody htmlEscape="true">${pointDelta.delta}</spring:escapeBody>">
				                            <a href="javascript:saveDelta('${pointDelta.bankId}_${pointDelta.pointId}')">Save</a>
				                            <a href="javascript:cancelEdit('${pointDelta.bankId}_${pointDelta.pointId}')">Cancel</a>
										</div>
									</td>
								</c:when>
								<c:otherwise>
									<td style="width:8%">
										<input type="checkbox" id="staticDelta_${pointDelta.bankId}_${pointDelta.pointId}"
											   disabled="disabled"
										<c:choose>
						                	<c:when test="${pointDelta.staticDelta}">
				                                checked="checked"
				                            </c:when>
				                        </c:choose> 
	                        		></td>
									<td style="width:100%">
										<div id="viewDelta_${pointDelta.bankId}_${pointDelta.pointId}">
											<spring:escapeBody htmlEscape="true">${pointDelta.delta}</spring:escapeBody>
										</div>
									</td>
								</c:otherwise>
							</c:choose>
						</tr>
					</c:forEach>
				</table>
			</form>
		</tags:pagedBox>
	</div>
</cti:standardPage>

</cti:msgScope>