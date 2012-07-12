<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="capcontrol" page="ivvc.zoneDetail">
	<cti:includeScript link="/JavaScript/tableCreation.js" />
	<cti:includeScript link="/JavaScript/simpleDialog.js"/>
    <cti:includeScript link="/JavaScript/dynamicTable.js"/>
	<cti:includeScript link="/JavaScript/picker.js" />
    <cti:includeScript link="/JavaScript/ivvcAmCharts.js" />

	<%@include file="/capcontrol/capcontrolHeader.jspf"%>
	<cti:includeCss link="/capcontrol/css/ivvc.css"/>

    <cti:msg2 key="yukon.web.modules.capcontrol.ivvc.zoneWizard.editor.title" var="zoneWizardTitle"/>

    <!-- Zone Wizard Dialog -->
    <tags:simpleDialog id="zoneWizardPopup" title="" styleClass="smallSimplePopup"/>

	<script type="text/javascript">

        function setRedBulletForPoint(pointId) {
            //assumes data is of type Hash
            return function(data) {
                var redBulletSpans = $$('.redBullet_' + pointId);
                var quality = data.get('quality');
                
                redBulletSpans.each(function(redBulletSpan) {
                    if (quality != 'Normal') {
                        redBulletSpan.show();
                    } else {
                        redBulletSpan.hide();
                    }
                });
            };
        }

        function showZoneWizard(url) {
            openSimpleDialog('zoneWizardPopup', url, "${zoneWizardTitle}",
                    null, null, 'get');
        }
        
        Event.observe(window, 'load', function() {
            var recentEventsUpdaterTimeout = setTimeout(updateRecentEvents, ${updaterDelay});
            if (${hasControlRole}) {
                $$('tr[id^="tr_cap"]').each(function (row) {
                    var bankId = row.id.split('_')[2];

                    // Add menus
                    var bankName = row.down('button[id^="bankName"]');
                    bankName.observe('click', function(event) {
                        getCommandMenu(bankId, event);
                        return false;
                    });

                    var bankState = row.down('a[id^="capbankState"]');
                    bankState.observe('click', function(event) {
                        getMenuFromURL('/spring/capcontrol/menu/capBankState?id=' + encodeURIComponent(bankId), event);
                        return false;
                    });
                });
            }
        });
        
        function updateRecentEvents() {
            var params = {
                'zoneId': ${zoneId},
                'subBusId': ${subBusId},
                'mostRecent': $('mostRecentDateTime').value
            };
            new Ajax.Request('getRecentEvents', { 
                'method': 'GET', 
                'parameters': params,
                onSuccess: function(transport) {
                    var dummyHolder = document.createElement('div');
                    dummyHolder.innerHTML = transport.responseText;
                    var rows = $(dummyHolder).select('tr');
                    if (typeof(rows[0]) != 'undefined') {
                    	// assign our hidden td field to mostRecentDateTime for use in future calls
                        $('mostRecentDateTime').value = rows[0].select('td input[type="hidden"]')[0].value;
                    }
                    for (var i = rows.length-1; i >= 0 ; i--) {
                        var topRow = $('recentEventsHeaderRow').next();
                        if (topRow.hasClassName('tableCell')) {
                            rows[i].addClassName('altTableCell');
                        } else {
                            rows[i].addClassName('tableCell');
                        }
                        $('recentEventsHeaderRow').insert({'after': rows[i]});
                        flashYellow(rows[i]);
                    }
                    // Keep table size <= 20 rows
                    $$('#recentEventsTable tr:gt(21)').each(function(tr){
                        tr.remove();
                    });
                },
                onComplete: function() {
                    recentEventsUpdaterTimeout = setTimeout(updateRecentEvents, ${updaterDelay});
                }
            });
        }
        
        YEvent.observeSelectorClick('button.commandButton', function(event) {
            var button = event.findElement('button');
            var cmdId = button.next('input.cmdId').value;
            var paoId = button.next('input.paoId').value;
            doItemCommand(paoId, cmdId, event);
        });
    </script>

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
                        <th><i:inline key=".details.table.type"/></th>
    					<th><i:inline key=".details.table.actions"/></th>
    				</tr>
					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
						<td><B><i:inline key=".details.table.zone"/></B></td>
						<td>
							<spring:escapeBody htmlEscape="true">${zoneName}</spring:escapeBody>
						</td>
                        <td>
                            <spring:escapeBody htmlEscape="true">
                                <i:inline key="yukon.web.modules.capcontrol.ivvc.zone.${zoneDto.zoneType}"/>
                            </spring:escapeBody>
                        </td>
						<td>
							<c:choose>
								<c:when test="${hasEditingRole}">
                                    <cti:button nameKey="edit" renderMode="image" onclick="javascript:showZoneWizard('${zoneEditorUrl}');"/> 
								</c:when>
								<c:otherwise>
									<cti:button nameKey="disabledEdit" renderMode="image" disabled="true"/>
								</c:otherwise>
							</c:choose>
                        </td>
					</tr>
                    <c:forEach items="${zoneDto.regulators}" var="regulator">
                        <c:set value="${regulator.key}" var="phaseKey"/>
                        <tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
                            <td><B><i:inline key=".details.table.regulator"/>
                                <c:if test="${zoneDto.zoneType != gangOperated}"> - 
                                    <i:inline key="${phaseKey}"/></c:if>
                                </B>
                            </td>
                            <td>
                                <capTags:regulatorModeIndicator paoId="${regulatorIdMap[phaseKey]}" type="VOLTAGE_REGULATOR"/>
                                <spring:escapeBody htmlEscape="true">${regulatorNameMap[phaseKey]}</spring:escapeBody>
                            </td>
                            <td>
                                <spring:escapeBody htmlEscape="true">
                                    <i:inline key="yukon.web.modules.capcontrol.ivvc.regulator.${zoneDto.zoneType}"/>
                                </spring:escapeBody>
                            </td>
                            <td>
								<c:choose>
								    <c:when test="${hasEditingRole}">
				                        <cti:url var="editorUrl" value="/editor/cbcBase.jsf">
				                            <cti:param name="type" value="2"/>
				                            <cti:param name="itemid" value="${regulatorIdMap[phaseKey]}"/>
				                        </cti:url>
                                        <cti:button nameKey="edit" renderMode="image" href="${editorUrl}"/>
								    </c:when>
                                    <c:otherwise>
                                        <cti:button nameKey="disabledEdit" renderMode="image" disabled="true"/>
									</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
			</tags:boxContainer2>
			<br>
			<tags:boxContainer2 nameKey="actions" hideEnabled="true" showInitially="true" styleClass="regulatorActions">			
                <table class="compactResultsTable">
                    <tr>
                        <c:if test="${zoneDto.zoneType != gangOperated}">
                            <c:forEach items="${zoneDto.regulators}" var="regulator">
                                <th><i:inline key=".details.table.phase.${regulator.key}"/></th>
                            </c:forEach>
                        </c:if>
                    </tr>
                    <tr>
                        <c:forEach items="${zoneDto.regulators}" var="regulator">
                            <c:set var="phaseKey" value="${regulator.key}"/>
                            <td>
                                <ul class="buttonStack">
                                    <li>
                                        <cti:button renderMode="labeledImage" nameKey="scan" styleClass="commandButton"/>
                                        <input type="hidden" class="paoId" value="${regulatorIdMap[phaseKey]}">
                                        <input type="hidden" class="cmdId" value="${scanCommandHolder.commandId}">
                                    </li>
                                    <li>
                                        <cti:button renderMode="labeledImage" nameKey="up" styleClass="commandButton"/>
                                        <input type="hidden" class="paoId" value="${regulatorIdMap[phaseKey]}">
                                        <input type="hidden" class="cmdId" value="${tapUpCommandHolder.commandId}">
                                    </li>
                                    <li>
                                        <cti:button renderMode="labeledImage" nameKey="down" styleClass="commandButton"/>
                                        <input type="hidden" class="paoId" value="${regulatorIdMap[phaseKey]}">
                                        <input type="hidden" class="cmdId" value="${tapDownCommandHolder.commandId}">
                                    </li>
                                    <li>
                                        <cti:button renderMode="labeledImage" nameKey="enable" styleClass="commandButton"/> 
                                        <input type="hidden" class="paoId" value="${regulatorIdMap[phaseKey]}">
                                        <input type="hidden" class="cmdId" value="${enableRemoteCommandHolder.commandId}">
                                    </li>
                                    <li>
                                        <cti:button renderMode="labeledImage" nameKey="disable" styleClass="commandButton"/>
                                        <input type="hidden" class="paoId" value="${regulatorIdMap[phaseKey]}">
                                        <input type="hidden" class="cmdId" value="${disableRemoteCommandHolder.commandId}">
                                    </li>
                                </ul>
                            </td>
                        </c:forEach>
                    </tr>
                </table>
            </tags:boxContainer2>
			<br>
			<tags:boxContainer2 nameKey="ivvcEvents" hideEnabled="true" showInitially="true">
                <input type="hidden" value="${mostRecentDateTime}" id="mostRecentDateTime">
				<tags:alternateRowReset/>
				<c:choose>
					<c:when test="${empty events}">
						<i:inline key=".ivvcEvents.none"/>
					</c:when>
					<c:otherwise>
						<div class="historyContainer">
							<table id="recentEventsTable" class="compactResultsTable">
								<tr id="recentEventsHeaderRow">
									<th><i:inline key=".ivvcEvents.deviceName"/></th>
                                    <th><i:inline key=".ivvcEvents.description"/></th>
									<th><i:inline key=".attributes.timestamp"/></th>
								</tr>
					            <c:forEach var="ccEvent" items="${events}">
									<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
										<td><spring:escapeBody htmlEscape="true">${ccEvent.deviceName}</spring:escapeBody></td>
                                        <td><spring:escapeBody htmlEscape="true">${ccEvent.text}</spring:escapeBody></td>
                                        <td class="last"><cti:formatDate value="${ccEvent.dateTime}" type="BOTH"/></td>
									</tr>
					            </c:forEach>
							</table>
						</div>	
					</c:otherwise>
				</c:choose>
			</tags:boxContainer2>
			<br>
            <c:choose>
                <c:when test="${zoneDto.zoneType == threePhase}">
                    <cti:tabbedContentSelector>
                        <cti:msg2 var="tabName" key=".ivvc.zoneDetail.attributesRegAll.title" />
                        <cti:tabbedContentSelectorContent selectorName="${tabName}">
                            <tags:alternateRowReset/>
                            <table class="compactResultsTable">
                                <tr style="text-align: left;">
                                    <th><i:inline key=".attributes.name"/></th>
                                    <c:forEach items="${zoneDto.regulators}" var="regulator">
                                        <th><i:inline key=".attributes.phaseValue.${regulator.key}"/></th>
                                    </c:forEach>
                                </tr>
                                <c:forEach var="point" items="${regulatorPointMappingsMap[phaseA]}" varStatus="status">
                                    <tr class="<tags:alternateRow odd="" even="altRow"/> regulatorAttributeRow">
                                        <td><i:inline key="${point.regulatorPointMapping}"/></td>
                                        <c:forEach items="${zoneDto.regulators}" var="regulator">
                                            <c:set var="phaseKey" value="${regulator.key}"/>
                                            <td>
                                                <c:set var="pointId" value="${regulatorPointMappingsMap[phaseKey][status.index].pointId}"/>
                                                <c:choose>
                                                    <c:when test="${pointId > 0}">
                                                        <span class="redBullet_${pointId}">
                                                            <img src="/WebConfig/yukon/Icons/bullet_red.gif" class="tierImg" title="Questionable Quality">
                                                        </span>
                                                        <cti:pointValue pointId="${pointId}" format="VALUE"/>
                                                        <cti:dataUpdaterCallback function="setRedBulletForPoint(${pointId})" 
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
                            </table>
                        </cti:tabbedContentSelectorContent>
                        <c:forEach items="${zoneDto.regulators}" var="regulator">
                            <c:set var="phaseKey" value="${regulator.key}"/>
                            <cti:msg2 var="tabName" key=".ivvc.zoneDetail.attributesReg${phaseKey}.title" />
                            <cti:tabbedContentSelectorContent selectorName="${tabName}">
                                <tags:alternateRowReset/>
                                <table class="compactResultsTable">
                                    <tr style="text-align: left;">
                                        <th><i:inline key=".attributes.name"/></th>
                                        <th><i:inline key=".attributes.value"/></th>
                                        <th><i:inline key=".attributes.timestamp"/></th>
                                    </tr>
                                    <c:forEach var="point" items="${regulatorPointMappingsMap[phaseKey]}">
                                        <tr class="<tags:alternateRow odd="" even="altRow"/> regulatorAttributeRow">
                                            <td><i:inline key="${point.regulatorPointMapping}"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${point.pointId > 0}">
                                                        <span class="redBullet_${point.pointId}">
                                                            <img src="/WebConfig/yukon/Icons/bullet_red.gif" class="tierImg" title="Questionable Quality">
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
                                </table>
                            </cti:tabbedContentSelectorContent>
                        </c:forEach>
                    </cti:tabbedContentSelector>
                </c:when>
                <c:otherwise>
        			<tags:boxContainer2 nameKey="attributes" hideEnabled="true" showInitially="true">
        				<tags:alternateRowReset/>
        		        <table class="compactResultsTable">
        		            <tr style="text-align: left;">
        		                <th><i:inline key=".attributes.name"/></th>
        		                <th><i:inline key=".attributes.value"/></th>
        		                <th><i:inline key=".attributes.timestamp"/></th>
        		            </tr>
                            <c:forEach items="${zoneDto.regulators}" var="regulator">
                                <c:set var="phaseKey" value="${regulator.key}"/>
            		            <c:forEach var="point" items="${regulatorPointMappingsMap[phaseKey]}">
            		                <tr class="<tags:alternateRow odd="" even="altRow"/>">
            		                    <td><i:inline key="${point.regulatorPointMapping}"/></td>
            		                    <td>
            		                        <c:choose>
            		                            <c:when test="${point.pointId > 0}">
                                                    <span class="redBullet_${point.pointId}">
                                                        <img src="/WebConfig/yukon/Icons/bullet_red.gif" class="tierImg" title="Questionable Quality">
                                                    </span>
                                                    <cti:pointValue pointId="${point.pointId}" format="VALUE"/>
                                                    <cti:dataUpdaterCallback function="setRedBulletForPoint(${point.pointId})" 
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
                            </c:forEach>
        		        </table>
        			</tags:boxContainer2>
                </c:otherwise>
            </c:choose>
            <br>
            <tags:boxContainer2 nameKey="otherActions" hideEnabled="true" showInitially="true">
				<cti:url var="zoneVoltageDeltasUrl" value="/spring/capcontrol/ivvc/zone/voltageDeltas">
			    	<cti:param name="zoneId" value="${zoneId}"/>
			    </cti:url>
            	<a href="${zoneVoltageDeltasUrl}"><i:inline key=".otherActions.voltageDeltas"/></a>
            </tags:boxContainer2>
		</cti:dataGridCell>
		
		<cti:dataGridCell>
			<cti:tabbedContentSelector>
				<cti:msg2 var="tabName" key=".voltageProfile.title" />
				<cti:tabbedContentSelectorContent selectorName="${tabName}">
					<%@ include file="aboveGraph.jspf"%>

					<c:set var="chartId" value="zone_${subBusId}_IVVCGraph" />
					<input type="hidden" value="${chartId}" id="ivvcChartIdValue" />
					<c:url var="amChartFile" value="/spring/capcontrol/ivvc/zone/chart">
						<cti:param name="zoneId" value="${zoneId}" />
					</c:url>
					<tags:amchart chartType="amline" settingsUrl="${amChartFile}" chartId="${chartId}" cssClass="ivvcGraphContainer" />

					<cti:dataUpdaterCallback function="checkGraphExpired('${chartId}')" initialize="true" 
	                    largestTime="CAPCONTROL/${zoneId}/IVVC_LARGEST_GRAPH_TIME_FOR_ZONE"/>
	                
				</cti:tabbedContentSelectorContent>
				<cti:msg2 var="voltagePointsTab" key=".voltagePoints.title" />
				<cti:tabbedContentSelectorContent selectorName="${voltagePointsTab}">
					<div class="largeDialogScrollArea">
						<cti:url var="zoneVoltagePointsUrl" value="/spring/capcontrol/ivvc/zone/voltagePoints">
					    	<cti:param name="zoneId" value="${zoneId}"/>
					    </cti:url>
						<%@ include file="voltagePoints.jspf" %>
					</div>
				</cti:tabbedContentSelectorContent>
			</cti:tabbedContentSelector>
			
			<br>
			
			<tags:boxContainer2 nameKey="capBanks" hideEnabled="true" showInitially="true">
				<tags:alternateRowReset/>
				<table class="compactResultsTable ">
					<tr>
						<th><i:inline key=".capBanks.cbcName"/></th>
						<th><i:inline key=".capBanks.bankName"/></th>
						<th><i:inline key=".capBanks.bankState"/></th>
						<th><i:inline key=".capBanks.bankVoltage"/></th>
					</tr>
		            <c:forEach var="capBank" items="${capBankList}">
                        <c:set var="bankId" value="${capBank.capBankDevice.ccId}"/>
		                <tr id="tr_cap_${bankId}" class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
		                    <td class="wsnw">
		                    	<c:if test="${capBank.notAssignedToZone}">
                                    <span class="strongWarningMessage">*</span>
                                </c:if>
                                <%-- CBC Actions --%>
                                <c:choose>
                                    <c:when test="${capBank.controlDevice.liteID != 0}">
                                        <a href="/editor/cbcBase.jsf?type=2&amp;itemid=${capBank.controlDevice.liteID}">
            		                    	<spring:escapeBody htmlEscape="true">
                                                ${capBank.controlDevice.paoName}
                                            </spring:escapeBody>
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <i:inline key=".capBanks.noCbc"/>
                                    </c:otherwise>
                                </c:choose>
		                    </td>
		                    <td>
                                <cti:button id="bankName_${bankId}" nameKey="capBanks.bankCommands" renderMode="image"/>
                                <a href="/editor/cbcBase.jsf?type=2&amp;itemid=${bankId}">
                                    <spring:escapeBody htmlEscape="true">
                                        ${capBank.capBankDevice.ccName}
                                    </spring:escapeBody>
                                </a>
                            </td>
		                    <td>
                                <capTags:capBankWarningImg paoId="${bankId}" type="CAPBANK"/>
                                <cti:capBankStateColor paoId="${bankId}" type="CAPBANK" format="CB_STATUS_COLOR">
                                    <a id="capbankState_${bankId}" href="#null">
                                       <cti:capControlValue paoId="${bankId}" type="CAPBANK" format="CB_STATUS"/>
                                    </a>
                                </cti:capBankStateColor>
                                
                        	</td>
		                    <td>
								<c:choose>
		                            <c:when test="${capBank.voltagePointId > 0}">
		                                <cti:pointValue pointId="${capBank.voltagePointId}" format="VALUE"/>
		                            </c:when>
		                            <c:otherwise>
		                                <i:inline key="yukon.web.defaults.dashes"/>
		                            </c:otherwise>
		                        </c:choose>
		                    </td>
		                </tr>
		            </c:forEach>
				</table>
                <c:if test="${unassignedBanksExist}">
                    <br>
                    <div class="strongWarningMessage"><i:inline key=".capBanks.unassignedBanks"/></div>
                </c:if>
			</tags:boxContainer2>
		</cti:dataGridCell>	
	</cti:dataGrid>
	<br>
</cti:standardPage>
