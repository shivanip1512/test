<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>

<cti:standardPage title="${title}" module="capcontrol">
	<cti:includeScript link="/JavaScript/itemPicker.js" />
	<cti:includeScript link="/JavaScript/tableCreation.js" />
	<cti:includeScript link="/JavaScript/paoPicker.js" />
	<cti:includeScript link="/JavaScript/simpleDialog.js"/>
	<cti:includeScript link="/JavaScript/picker.js" />
	<cti:includeCss link="/WebConfig/yukon/styles/itemPicker.css" />
	
	<%@include file="/capcontrol/capcontrolHeader.jspf"%>
	<cti:includeCss link="/capcontrol/css/ivvc.css"/>
	
	<script type="text/javascript">
	
		function editDelta(id) {
			$('viewDelta_' + id).hide();
			$('editDelta_' + id).show();
			$('editDelta_' + id).down().focus();
	    }

		function saveDelta(id) {
            var newDelta = $('editDelta_' + id).down().value;
            if(newDelta.length != 0) {
                $('delta').value = newDelta.escapeHTML();
                $('bankId').value = id.split('_')[0]; 
               	$('pointId').value = id.split('_')[1];
               	
                //$('deltaForm').action = '${baseUrl}' + 'deltaUpdate';
                $('deltaForm').submit();
                //submitFormViaAjax('voltageDeltaDiv', 'deltaForm', null, null);


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
	    
    	function showZoneEditor(url) {
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
    
        <cti:crumbLink url="${subStationAddress}" title="${areaName}" />
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
    
	<cti:dataGrid tableStyle="width:100%;" cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;width:30%">
	
		<cti:dataGridCell>
			<tags:boxContainer title="Zone Details" hideEnabled="true" showInitially="true">
				<tags:alternateRowReset/>			
				<table class="compactResultsTable">
					<tr>
						<th></th>
						<th>Name</th>
						<th>Actions</th>
					</tr>
					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
						<td><B>Zone</B></td>
						<td>${zoneName}</td>
						<td>
							<a title="Edit" href="javascript:showZoneEditor('${zoneEditorUrl}');" class="tierIconLink">
                            	<img alt="Edit" class="tierImg" src="${editInfoImage}">
                            </a>
                        </td>
					</tr>
				
					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
						<td><B>Regulator</B></td>
						<td>${ltcName}</td>
						<td>
							<a title="Edit" href="/editor/cbcBase.jsf?type=2&amp;itemid=${ltcId}" class="tierIconLink">
                            	<img alt="Edit" class="tierImg" src="${editInfoImage}">
                            </a>
                        </td>
					</tr>
				</table>
				<br>
				<tags:alternateRowReset/>	
				
				<tags:sectionContainer title="Points">
					<tags:nameValueContainer>
						<tags:nameValue name="${voltageMapping.attribute.description}" nameColumnWidth="30%">
							<c:choose>
	                            <c:when test="${voltageMapping.pointId > 0}">
	                                <cti:pointValue pointId="${voltageMapping.pointId}" format="VALUE"/>
	                            </c:when>
	                            <c:otherwise>
	                                ---
	                            </c:otherwise>
	                        </c:choose>
						</tags:nameValue>
						<tags:nameValue name="${tapPositionMapping.attribute.description}" nameColumnWidth="30%">
							<c:choose>
	                            <c:when test="${tapPositionMapping.pointId > 0}">
	                                <cti:pointValue pointId="${tapPositionMapping.pointId}" format="VALUE"/>
	                            </c:when>
	                            <c:otherwise>
	                                ---
	                            </c:otherwise>
	                        </c:choose>
						</tags:nameValue>
						<tags:nameValue name="${localAutoMapping.attribute.description}" nameColumnWidth="30%">
							<c:choose>
	                            <c:when test="${localAutoMapping.pointId > 0}">
	                                <cti:pointValue pointId="${localAutoMapping.pointId}" format="VALUE"/>
	                            </c:when>
	                            <c:otherwise>
	                                ---
	                            </c:otherwise>
	                        </c:choose>
							
						</tags:nameValue>
						
					</tags:nameValueContainer>
				</tags:sectionContainer>
				
				
			</tags:boxContainer>
			<br>
			<tags:boxContainer title="LTC / Regulator Attributes" hideEnabled="true" showInitially="true">
				<tags:alternateRowReset/>
		        <table class="compactResultsTable">
		            <tr style="text-align: left;">
		                <th>Attribute</th>
		                <th>Value</th>
		                <th>Timestamp</th>
		            </tr>
		            
		            <c:forEach var="point" items="${ltcPointMappings}">
		                <tr class="<tags:alternateRow odd="" even="altRow"/>">
		                    <td>${point.attribute.description}</td>
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
			</tags:boxContainer>
			<br>
			<tags:boxContainer title="Cap Banks" hideEnabled="true" showInitially="true">
				<tags:alternateRowReset/>
				<table class="compactResultsTable ">
					<tr>
						<th>CBC Name</th>
						<th>Cap Bank Name</th>
						<th>Bank State</th>
						<th>Voltage</th>
					</tr>
		            <c:forEach var="capBank" items="${capBankList}">
		                <tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
		                    <td>${capBank.controlDevice.paoName}</td>
		                    <td>${capBank.capBankDevice.ccName}</td>
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
			</tags:boxContainer>			
		</cti:dataGridCell>
		
		<cti:dataGridCell>
			<tags:boxContainer title="Voltage Profile" hideEnabled="true" showInitially="true">
				<!--Chart -->
		        <c:set var="amChartsProduct" value="amxy"/>
		        <c:url var="amDataFile" scope="page" value="/spring/capcontrol/ivvc/zone/chartData">
		        	<cti:param name="zoneId" value="${zoneId}"/>
		        </c:url>
		        <c:url var="amSettingsFile" scope="page" value="/spring/capcontrol/ivvc/zone/chartSettings">
		        	<cti:param name="zoneId" value="${zoneId}"/>
		        </c:url>
		        <c:url var="amSrc" scope="page" value="/JavaScript/amChart/${amChartsProduct}.swf">
		            <c:param name="${amChartsProduct}_path" value="/JavaScript/amChart/" />
		            <c:param name="${amChartsProduct}_flashWidth" value="100%" />
		            <c:param name="${amChartsProduct}_flashHeight" value="100%" />
		            <c:param name="${amChartsProduct}_preloaderColor" value="#000000" />
		            <c:param name="${amChartsProduct}_settingsFile" value="${amSettingsFile}" />
		            <c:param name="${amChartsProduct}_dataFile" value="${amDataFile}" />
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
		           var so = new SWFObject("${amSrc}", "dataGraph", "${swfWidth}", "500", "8", "#FFFFFF");
		           so.useExpressInstall('${expressInstallSrc}');
		           so.write("${uniqueId}");
		        </script>
			</tags:boxContainer>
			<br>
			<tags:pagedBox title="Voltage Deltas" searchResult="${searchResult}" 
						   baseUrl="${baseUrl}" showAllUrl="${baseUrl}">
				<c:choose>
					<c:when test="${searchResult.hitCount == 0}">
						No items to display.
					</c:when>
					<c:otherwise>
						<tags:alternateRowReset/>					
						<form id="deltaForm" action="/spring/capcontrol/ivvc/zone/deltaUpdate">
						    <input type="hidden" name="bankId" id="bankId">
				            <input type="hidden" name="pointId" id="pointId">
				            <input type="hidden" name="delta" id="delta">
				            <input type="hidden" name="zoneId" id="zoneId" value="${zoneId}">
				        </form>
						<table id="deltaTable" class="compactResultsTable" >
							<tr>
								<th>Bank Name</th>
								<th>Cbc Name</th>
								<th>Affected Device Name</th>
								<th>Point Name</th>
								<th>Pre Op Value</th>
								<th>Delta</th>
							</tr>
							<c:forEach var="pointDelta" items="${pointDeltas}">
								<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
									<td style="width:15%"><spring:escapeBody htmlEscape="true">${pointDelta.bankName}</spring:escapeBody></td>
									<td style="width:15%"><spring:escapeBody htmlEscape="true">${pointDelta.cbcName}</spring:escapeBody></td>
									<td style="width:15%"><spring:escapeBody htmlEscape="true">${pointDelta.affectedDeviceName}</spring:escapeBody></td>
									<td style="width:18%"><spring:escapeBody htmlEscape="true">${pointDelta.affectedPointName}</spring:escapeBody></td>
									<td style="width:13%"><spring:escapeBody htmlEscape="true">${pointDelta.preOpValue}</spring:escapeBody></td>
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
								</tr>
							</c:forEach>
						</table>
					</c:otherwise>
				</c:choose>
			</tags:pagedBox>			

		</cti:dataGridCell>
	
	</cti:dataGrid>

	<br>

	<div style="width:100%" id="voltageDeltaDiv">
		
	</div>
</cti:standardPage>