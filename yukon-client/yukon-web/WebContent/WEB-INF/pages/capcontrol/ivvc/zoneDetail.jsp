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
    <cti:includeScript link="/JavaScript/dynamicTable.js"/>
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
                Yukon.ui.blockPage();
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

    
    <cti:standardMenu/>
    
    <cti:url var="substationAddress" value="/spring/capcontrol/tier/substations">
    	<cti:param name="areaId" value="${areaId}"/>
    	<cti:param name="isSpecialArea" value="${isSpecialArea}"/>
    </cti:url>
    
	<cti:url var="feederAddress" value="/spring/capcontrol/tier/feeders">
    	<cti:param name="areaId" value="${areaId}"/>
    	<cti:param name="substationId" value="${substationId}"/>
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
        <cti:crumbLink url="${feederAddress}" title="${substationName}" />
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
                                <cti:button nameKey="edit" renderMode="image" 
                                    href="/editor/cbcBase.jsf?type=2&amp;itemid=${regulatorIdMap[phaseKey]}"/>
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
                    <tags:tabbedBoxContainer nameKeys="${nameKeys}" styleClass="ivvcRegulatorTabs">
                        <tags:tabbedBoxContainerElement>
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
                                        <td><spring:escapeBody htmlEscape="true">
                                            ${point.attribute.description}
                                        </spring:escapeBody></td>
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
                        </tags:tabbedBoxContainerElement>
                        <c:forEach items="${zoneDto.regulators}" var="regulator">
                            <c:set var="phaseKey" value="${regulator.key}"/>
                            <tags:tabbedBoxContainerElement>
                                <tags:alternateRowReset/>
                                <table class="compactResultsTable">
                                    <tr style="text-align: left;">
                                        <th><i:inline key=".attributes.name"/></th>
                                        <th><i:inline key=".attributes.value"/></th>
                                        <th><i:inline key=".attributes.timestamp"/></th>
                                    </tr>
                                    <c:forEach var="point" items="${regulatorPointMappingsMap[phaseKey]}">
                                        <tr class="<tags:alternateRow odd="" even="altRow"/> regulatorAttributeRow">
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
                        </c:forEach>
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
                            <c:forEach items="${zoneDto.regulators}" var="regulator">
                                <c:set var="phaseKey" value="${regulator.key}"/>
            		            <c:forEach var="point" items="${regulatorPointMappingsMap[phaseKey]}">
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
                        <c:set var="bankId" value="${capBank.capBankDevice.ccId}"/>
		                <tr id="tr_cap_${bankId}" class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
		                    <td class="nw">
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
					
                    <cti:msg2 var="deltaTitle" key=".deltas.deltaTitle" />
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
										<div id="viewDelta_${pointDelta.bankId}_${pointDelta.pointId}" title="${deltaTitle}"
										     onclick="editDelta('${pointDelta.bankId}_${pointDelta.pointId}')">
											<spring:escapeBody htmlEscape="true">${pointDelta.deltaRounded}</spring:escapeBody>
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
											<spring:escapeBody htmlEscape="true">${pointDelta.deltaRounded}</spring:escapeBody>
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