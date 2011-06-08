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
    <cti:includeScript link="/JavaScript/ivvcAmCharts.js" />

	<%@include file="/capcontrol/capcontrolHeader.jspf"%>
	<cti:includeCss link="/capcontrol/css/ivvc.css"/>

    <c:set var="chartId" value="zone_${subBusId}_IVVCGraph" />
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

        function editDelta(id) {
            $('viewDelta_' + id).hide();
            $('editDelta_' + id).show();
            $('editDelta_' + id).down().focus();
        }

        function saveDelta(id) {
            var newDelta = $('editDelta_' + id).down().value;
            var newStaticValue = $('staticDelta_' + id).checked;

            if (newDelta.length != 0) {
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
            if (key == 27) {
                /* Escape Key */
                cancelEdit(id);
            } else if (key == 13) {
                /* Enter Key */
                saveDelta(id);
            }
            return (key != 13);
        }

        function showZoneWizard(url) {
            openSimpleDialog('zoneWizardPopup', url, "${zoneWizardTitle}",
                    null, null, 'get');
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
                        <th><i:inline key=".details.table.type"/></th>
    					<th class="rightActionColumn"><i:inline key=".details.table.actions"/></th>
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
						<td class="rightActionColumn">
							<c:choose>
								<c:when test="${hasEditingRole}">
                                    <cti:button key="edit" renderMode="image" onclick="javascript:showZoneWizard('${zoneEditorUrl}');"/> 
								</c:when>
								<c:otherwise>
									<cti:button key="disabledEdit" renderMode="image" disabled="true"/>
								</c:otherwise>
							</c:choose>
                        </td>
					</tr>
                    <c:choose>
                        <c:when test="${zoneDto.zoneType == threePhase}">
        					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
        						<td><B><i:inline key=".details.table.regulator"/> - 
                                        <i:inline key="${zoneDto.regulatorA.phase}"/></B>
                                </td>
        						<td>
        							<capTags:regulatorModeIndicator paoId="${regulatorIdPhaseA}" type="VOLTAGE_REGULATOR"/>
        							<spring:escapeBody htmlEscape="true">${regulatorNamePhaseA}</spring:escapeBody>
        						</td>
                                <td>
                                    <spring:escapeBody htmlEscape="true">
                                        <i:inline key="yukon.web.modules.capcontrol.ivvc.regulator.${zoneDto.zoneType}"/>
                                    </spring:escapeBody>
                                </td>
        						<td class="rightActionColumn">
                                    <cti:button key="edit" renderMode="image" 
                                        href="/editor/cbcBase.jsf?type=2&amp;itemid=${regulatorIdPhaseA}"/>
                                </td>
        					</tr>
        					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
        						<td><B><i:inline key=".details.table.regulator"/> - 
                                        <i:inline key="${zoneDto.regulatorB.phase}"/></B>
                                </td>
        						<td>
        							<capTags:regulatorModeIndicator paoId="${regulatorIdPhaseB}" type="VOLTAGE_REGULATOR"/>
        							<spring:escapeBody htmlEscape="true">${regulatorNamePhaseB}</spring:escapeBody>
        						</td>
                                <td>
                                    <spring:escapeBody htmlEscape="true">
                                        <i:inline key="yukon.web.modules.capcontrol.ivvc.regulator.${zoneDto.zoneType}"/>
                                    </spring:escapeBody>
                                </td>
        						<td class="rightActionColumn">
        							<cti:button key="edit" renderMode="image" 
                                        href="/editor/cbcBase.jsf?type=2&amp;itemid=${regulatorIdPhaseB}" />
                                </td>
        					</tr>
        					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
        						<td><B><i:inline key=".details.table.regulator"/> - 
                                        <i:inline key="${zoneDto.regulatorC.phase}"/></B>
                                </td>
        						<td>
        							<capTags:regulatorModeIndicator paoId="${regulatorIdPhaseC}" type="VOLTAGE_REGULATOR"/>
        							<spring:escapeBody htmlEscape="true">${regulatorNamePhaseC}</spring:escapeBody>
        						</td>
                                <td>
                                    <spring:escapeBody htmlEscape="true">
                                        <i:inline key="yukon.web.modules.capcontrol.ivvc.regulator.${zoneDto.zoneType}"/>
                                    </spring:escapeBody>
                                </td>
        						<td class="rightActionColumn">
        							<cti:button key="edit" renderMode="image" 
                                        href="/editor/cbcBase.jsf?type=2&amp;itemid=${regulatorIdPhaseC}" />
                                </td>
        					</tr>
                        </c:when>
                        <c:when test="${zoneDto.zoneType == singlePhase}">
                            <tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
                                <td><B><i:inline key=".details.table.regulator"/> - 
                                        <i:inline key="${zoneDto.regulator.phase}"/></B>
                                </td>
                                <td>
                                    <capTags:regulatorModeIndicator paoId="${regulatorId}" type="VOLTAGE_REGULATOR"/>
                                    <spring:escapeBody htmlEscape="true">${regulatorName}</spring:escapeBody>
                                </td>
                                <td>
                                    <spring:escapeBody htmlEscape="true">
                                        <i:inline key="yukon.web.modules.capcontrol.ivvc.regulator.${zoneDto.zoneType}"/>
                                    </spring:escapeBody>
                                </td>
                                <td class="rightActionColumn">
                                    <cti:button key="edit" renderMode="image" 
                                        href="/editor/cbcBase.jsf?type=2&amp;itemid=${regulatorId}" />
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
        					<tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
        						<td><B><i:inline key=".details.table.regulator"/></B></td>
        						<td>
        							<capTags:regulatorModeIndicator paoId="${regulatorId}" type="VOLTAGE_REGULATOR"/>
        							<spring:escapeBody htmlEscape="true">${regulatorName}</spring:escapeBody>
        						</td>
                                <td>
                                    <spring:escapeBody htmlEscape="true">
                                        <i:inline key="yukon.web.modules.capcontrol.ivvc.regulator.${zoneDto.zoneType}"/>
                                    </spring:escapeBody>
                                </td>
        						<td class="rightActionColumn">
        							<cti:button key="edit" renderMode="image" 
                                        href="/editor/cbcBase.jsf?type=2&amp;itemid=${regulatorId}" />
                                </td>
        					</tr>
                        </c:otherwise>
                    </c:choose>
                </table>
			</tags:boxContainer2>
			<br>
			<tags:boxContainer2 nameKey="actions" hideEnabled="true" showInitially="true" styleClass="regulatorActions">			
                <c:choose>
                    <c:when test="${zoneDto.zoneType == threePhase}">
                        <table class="compactResultsTable">
                            <tr>
                                <th><i:inline key=".details.table.phase.A"/></th>
                                <th><i:inline key=".details.table.phase.B"/></th>
                                <th><i:inline key=".details.table.phase.C"/></th>
                            </tr>
                            <tr>
                                <td>
                                    <ul class="buttonStack">
                        				<li>
                        					<cti:button renderMode="labeledImage" key="scan" 
                                                onclick="executeCommand('${regulatorIdPhaseA}',
                    													'${scanCommandHolder.cmdId}',
                    													'${scanCommandHolder.commandName}',
                    													'${regulatorAType}',
                    													'false');"/>
                        				</li>
                        				<li>
                        					<cti:button renderMode="labeledImage" key="up" 
                                                onclick="executeCommand('${regulatorIdPhaseA}',
                                                                        '${tapUpCommandHolder.cmdId}',
                                                                        '${tapUpCommandHolder.commandName}',
                                                                        '${regulatorAType}',
                                                                        'false');"/>
                        				</li>
                        				<li>
                        					<cti:button renderMode="labeledImage" key="down" 
                                                onclick="executeCommand('${regulatorIdPhaseA}',
                    													'${tapDownCommandHolder.cmdId}',
                    													'${tapDownCommandHolder.commandName}',
                    													'${regulatorAType}',
                    													'false');"/>
                        				</li>
                        				<li>
                        					<cti:button renderMode="labeledImage" key="enable" 
                                                onclick="executeCommand('${regulatorIdPhaseA}',
                    													'${enableRemoteCommandHolder.cmdId}',
                    													'${enableRemoteCommandHolder.commandName}',
                    													'${regulatorAType}',
                    													'false');"/>
                        				</li>
                        				<li>
                        					<cti:button renderMode="labeledImage" key="disable" 
                                                onclick="executeCommand('${regulatorIdPhaseA}',
                    													'${disableRemoteCommandHolder.cmdId}',
                    													'${disableRemoteCommandHolder.commandName}',
                    													'${regulatorAType}',
                    													'false');"/>
                        				</li>
                                    </ul>
                                </td>
                                <td>
                                    <ul class="buttonStack">
                        				<li>
                        					<cti:button renderMode="labeledImage" key="scan" 
                                                onclick="executeCommand('${regulatorIdPhaseB}',
                    													'${scanCommandHolder.cmdId}',
                    													'${scanCommandHolder.commandName}',
                    													'${regulatorBType}',
                    													'false');"/>
                        				</li>
                        				<li>
                        					<cti:button renderMode="labeledImage" key="up" 
                                                onclick="executeCommand('${regulatorIdPhaseB}',
                    													'${tapUpCommandHolder.cmdId}',
                    													'${tapUpCommandHolder.commandName}',
                    													'${regulatorBType}',
                    													'false');"/>
                        				</li>
                        				<li>
                        					<cti:button renderMode="labeledImage" key="down" 
                                                onclick="executeCommand('${regulatorIdPhaseB}',
                    													'${tapDownCommandHolder.cmdId}',
                    													'${tapDownCommandHolder.commandName}',
                    													'${regulatorBType}',
                    													'false');"/>
                        				</li>
                        				<li>
                        					<cti:button renderMode="labeledImage" key="enable" 
                                                onclick="executeCommand('${regulatorIdPhaseB}',
                    													'${enableRemoteCommandHolder.cmdId}',
                    													'${enableRemoteCommandHolder.commandName}',
                    													'${regulatorBType}',
                    													'false');"/>
                        				</li>
                        				<li>
                        					<cti:button renderMode="labeledImage" key="disable" 
                                                onclick="executeCommand('${regulatorIdPhaseB}',
                    													'${disableRemoteCommandHolder.cmdId}',
                    													'${disableRemoteCommandHolder.commandName}',
                    													'${regulatorBType}',
                    													'false');"/>
                        				</li>
                                    </ul>
                                </td>
                                <td>
                                    <ul class="buttonStack">
                        				<li>
                        					<cti:button renderMode="labeledImage" key="scan" 
                                                onclick="executeCommand('${regulatorIdPhaseC}',
                    													'${scanCommandHolder.cmdId}',
                    													'${scanCommandHolder.commandName}',
                    													'${regulatorCType}',
                    													'false');"/>
                        				</li>
                        				<li>
                        					<cti:button renderMode="labeledImage" key="up" 
                                                onclick="executeCommand('${regulatorIdPhaseC}',
                    													'${tapUpCommandHolder.cmdId}',
                    													'${tapUpCommandHolder.commandName}',
                    													'${regulatorCType}',
                    													'false');"/>
                        				</li>
                        				<li>
                        					<cti:button renderMode="labeledImage" key="down" 
                                                onclick="executeCommand('${regulatorIdPhaseC}',
                    													'${tapDownCommandHolder.cmdId}',
                    													'${tapDownCommandHolder.commandName}',
                    													'${regulatorCType}',
                    													'false');"/>
                        				</li>
                        				<li>
                        					<cti:button renderMode="labeledImage" key="enable" 
                                                onclick="executeCommand('${regulatorIdPhaseC}',
                    													'${enableRemoteCommandHolder.cmdId}',
                    													'${enableRemoteCommandHolder.commandName}',
                    													'${regulatorCType}',
                    													'false');"/>
                        				</li>
                        				<li>
                        					<cti:button renderMode="labeledImage" key="disable" 
                                                onclick="executeCommand('${regulatorIdPhaseC}',
                    													'${disableRemoteCommandHolder.cmdId}',
                    													'${disableRemoteCommandHolder.commandName}',
                    													'${regulatorCType}',
                    													'false');"/>
                        				</li>
                                    </ul>
                                </td>
                            </tr>
                        </table>
                    </c:when>
                    <c:when test="${zoneDto.zoneType == singlePhase}">
                        <table class="compactResultsTable">
                            <tr>
                                <th><i:inline key=".details.table.phase.${zoneDto.regulator.phase}"/></th>
                            </tr>
                            <tr>
                                <td>
                                    <div>
                                        <cti:button renderMode="labeledImage" key="scan" 
                                            onclick="executeCommand('${regulatorId}',
                                                                    '${scanCommandHolder.cmdId}',
                                                                    '${scanCommandHolder.commandName}',
                                                                    '${regulatorType}',
                                                                    'false');"/>
                                    </div>
                                    <div>
                                        <cti:button renderMode="labeledImage" key="up" 
                                            onclick="executeCommand('${regulatorId}',
                                                                    '${tapUpCommandHolder.cmdId}',
                                                                    '${tapUpCommandHolder.commandName}',
                                                                    '${regulatorType}',
                                                                    'false');"/>
                                    </div>
                                    <div>
                                        <cti:button renderMode="labeledImage" key="down" 
                                            onclick="executeCommand('${regulatorId}',
                                                                    '${tapDownCommandHolder.cmdId}',
                                                                    '${tapDownCommandHolder.commandName}',
                                                                    '${regulatorType}',
                                                                    'false');"/>
                                    </div>
                                    <div>
                                        <cti:button renderMode="labeledImage" key="enable" 
                                            onclick="executeCommand('${regulatorId}',
                                                                    '${enableRemoteCommandHolder.cmdId}',
                                                                    '${enableRemoteCommandHolder.commandName}',
                                                                    '${regulatorType}',
                                                                    'false');"/>
                                    </div>
                                    <div>
                                        <cti:button renderMode="labeledImage" key="disable" 
                                            onclick="executeCommand('${regulatorId}',
                                                                    '${disableRemoteCommandHolder.cmdId}',
                                                                    '${disableRemoteCommandHolder.commandName}',
                                                                    '${regulatorType}',
                                                                    'false');"/>
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div>
                            <cti:button renderMode="labeledImage" key="scan" 
                                onclick="executeCommand('${regulatorId}',
                                                        '${scanCommandHolder.cmdId}',
                                                        '${scanCommandHolder.commandName}',
                                                        '${regulatorType}',
                                                        'false');"/>
                        </div>
                        <div>
                            <cti:button renderMode="labeledImage" key="up" 
                                onclick="executeCommand('${regulatorId}',
                                                        '${tapUpCommandHolder.cmdId}',
                                                        '${tapUpCommandHolder.commandName}',
                                                        '${regulatorType}',
                                                        'false');"/>
                        </div>
                        <div>
                            <cti:button renderMode="labeledImage" key="down" 
                                onclick="executeCommand('${regulatorId}',
                                                        '${tapDownCommandHolder.cmdId}',
                                                        '${tapDownCommandHolder.commandName}',
                                                        '${regulatorType}',
                                                        'false');"/>
                        </div>
                        <div>
                            <cti:button renderMode="labeledImage" key="enable" 
                                onclick="executeCommand('${regulatorId}',
                                                        '${enableRemoteCommandHolder.cmdId}',
                                                        '${enableRemoteCommandHolder.commandName}',
                                                        '${regulatorType}',
                                                        'false');"/>
                        </div>
                        <div>
                            <cti:button renderMode="labeledImage" key="disable" 
                                onclick="executeCommand('${regulatorId}',
                                                        '${disableRemoteCommandHolder.cmdId}',
                                                        '${disableRemoteCommandHolder.commandName}',
                                                        '${regulatorType}',
                                                        'false');"/>
                        </div>
                    </c:otherwise>
                </c:choose>
            </tags:boxContainer2>
			<br>
			<tags:boxContainer2 nameKey="ivvcEvents" hideEnabled="true" showInitially="true">
				<tags:alternateRowReset/>
				<c:choose>
					<c:when test="${empty events}">
						<i:inline key=".ivvcEvents.none"/>
					</c:when>
					<c:otherwise>
						<div class="historyContainer">
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
            <c:choose>
                <c:when test="${zoneDto.zoneType == threePhase}">
                    <tags:tabbedBoxContainer nameKeys="${nameKeys}">
                        <tags:tabbedBoxContainerElement>
                            <tags:alternateRowReset/>
                            <table class="compactResultsTable">
                                <tr style="text-align: left;">
                                    <th><i:inline key=".attributes.name"/></th>
                                    <th><i:inline key=".attributes.phaseAValue"/></th>
                                    <th><i:inline key=".attributes.phaseBValue"/></th>
                                    <th><i:inline key=".attributes.phaseCValue"/></th>
                                </tr>
                                <c:forEach var="point" items="${regulatorPointMappingsMap[phaseA]}" varStatus="status">
                                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                        <td><spring:escapeBody htmlEscape="true">
                                            ${point.attribute.description}
                                        </spring:escapeBody></td>
                                        <td>
                                            <c:set var="pointId" value="${regulatorPointMappingsMap[phaseA][status.index].pointId}"/>
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
                                        <td>
                                            <c:set var="pointId" value="${regulatorPointMappingsMap[phaseB][status.index].pointId}"/>
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
                                        <td>
                                            <c:set var="pointId" value="${regulatorPointMappingsMap[phaseC][status.index].pointId}"/>
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
                                    </tr>
                                </c:forEach>
                            </table>
                        </tags:tabbedBoxContainerElement>
                        <tags:tabbedBoxContainerElement>
                            <tags:alternateRowReset/>
                            <table class="compactResultsTable">
                                <tr style="text-align: left;">
                                    <th><i:inline key=".attributes.name"/></th>
                                    <th><i:inline key=".attributes.value"/></th>
                                    <th><i:inline key=".attributes.timestamp"/></th>
                                </tr>
                                <c:forEach var="point" items="${regulatorPointMappingsMap[phaseA]}">
                                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                        <td><spring:escapeBody htmlEscape="true">
                                            ${point.attribute.description}
                                        </spring:escapeBody></td>
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
                        </tags:tabbedBoxContainerElement>
                        <tags:tabbedBoxContainerElement>
                            <tags:alternateRowReset/>
                            <table class="compactResultsTable">
                                <tr style="text-align: left;">
                                    <th><i:inline key=".attributes.name"/></th>
                                    <th><i:inline key=".attributes.value"/></th>
                                    <th><i:inline key=".attributes.timestamp"/></th>
                                </tr>
                                <c:forEach var="point" items="${regulatorPointMappingsMap[phaseB]}">
                                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                        <td><spring:escapeBody htmlEscape="true">
                                            ${point.attribute.description}
                                        </spring:escapeBody></td>
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
                        </tags:tabbedBoxContainerElement>
                        <tags:tabbedBoxContainerElement>
                            <tags:alternateRowReset/>
                            <table class="compactResultsTable">
                                <tr style="text-align: left;">
                                    <th><i:inline key=".attributes.name"/></th>
                                    <th><i:inline key=".attributes.value"/></th>
                                    <th><i:inline key=".attributes.timestamp"/></th>
                                </tr>
                                <c:forEach var="point" items="${regulatorPointMappingsMap[phaseC]}">
                                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                        <td><spring:escapeBody htmlEscape="true">
                                            ${point.attribute.description}
                                        </spring:escapeBody></td>
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
                        </tags:tabbedBoxContainerElement>
                    </tags:tabbedBoxContainer>
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
        		            <c:forEach var="point" items="${regulatorPointMappings}">
        		                <tr class="<tags:alternateRow odd="" even="altRow"/>">
        		                    <td><spring:escapeBody htmlEscape="true">
                                        ${point.attribute.description}
                                    </spring:escapeBody></td>
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
        		        </table>
        			</tags:boxContainer2>
                </c:otherwise>
            </c:choose>
		</cti:dataGridCell>
		
		<cti:dataGridCell>
			<tags:boxContainer2 nameKey="voltageProfile" hideEnabled="true" showInitially="true">
				<!--Chart -->
		        <c:set var="amChartsProduct" value="amline"/>
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
                
                <cti:dataUpdaterCallback function="checkGraphExpired('${chartId}')" initialize="true" 
                    largestTime="CAPCONTROL/${zoneId}/IVVC_LARGEST_GRAPH_TIME_FOR_ZONE"/>
                
			</tags:boxContainer2>
			
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
		                <tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
		                    <td>
		                    	<c:if test="${capBank.notAssignedToZone}">
                                    <span class="strongWarningMessage">*</span>
                                </c:if>
		                    	<spring:escapeBody htmlEscape="true">
                                    ${capBank.controlDevice.paoName}
                                </spring:escapeBody>
		                    </td>
		                    <td><spring:escapeBody htmlEscape="true">
                                ${capBank.capBankDevice.ccName}
                            </spring:escapeBody></td>
		                    <td>
		                    	<cti:capBankStateColor paoId="${capBank.capBankDevice.ccId}" 
                                    type="CAPBANK" format="CB_STATUS_COLOR">
    					       		<cti:capControlValue paoId="${capBank.capBankDevice.ccId}" 
                                        type="CAPBANK" format="CB_STATUS"/>
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