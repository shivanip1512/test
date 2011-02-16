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
	
	<%@include file="/capcontrol/capcontrolHeader.jspf"%>
    <cti:includeCss link="/capcontrol/css/ivvc.css"/>
    
    <cti:url var="zoneCreatorUrl" value="/spring/capcontrol/ivvc/wizard/zoneCreation">
    	<cti:param name="subBusId" value="${subBusId}"/>
    </cti:url>
    
    <script type="text/javascript">
    	function showZoneWizard(url) {
			openSimpleDialog('tierContentPopup', url, 'Zone Wizard', null, null, 'get');
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
				<div class="zoneHierarchy">
					<ul>
						<li>
							<div style="font-weight: bold">
								<i:inline key=".zoneList.name"/>
								<div>
									<i:inline key="modules.capcontrol.actions"/>
									<div class="nonwrapping">
										<i:inline key="modules.capcontrol.lastOperation"/>
									</div>
								</div>
							</div>
						</li>
					</ul>
				</div>
				<cti:navigableHierarchy hierarchy="${zones}" 
					styleClass="zoneHierarchy"
					var="zone">
					<cti:url var="zoneDetailUrl" value="/spring/capcontrol/ivvc/zone/detail">
				    	<cti:param name="zoneId" value="${zone.id}"/>
				    	<cti:param name="isSpecialArea" value="${isSpecialArea}"/>
				    </cti:url>
				    <cti:url var="zoneEditorUrl" value="/spring/capcontrol/ivvc/wizard/zoneEditor">
   						<cti:param name="zoneId" value="${zone.id}"/>
				    </cti:url>
					<cti:url var="zoneDeleteUrl" value="/spring/capcontrol/ivvc/wizard/deleteZone">
   						<cti:param name="zoneId" value="${zone.id}"/>
				    </cti:url> 
					<div>
						<capTags:regulatorModeIndicator paoId="${zone.regulatorId}" type="VOLTAGE_REGULATOR"/>
						<a href="${zoneDetailUrl}">${zone.name}</a>
						<div>
						<c:choose>
							<c:when  test="${hasEditingRole}">
								<a href="javascript:showZoneWizard('${zoneEditorUrl}');"><cti:img key="edit"/></a> 
								<a href="${zoneDeleteUrl}"><cti:img key="remove"/></a>
							</c:when>
							<c:otherwise>
								<cti:img key="disabledEdit"/> 
								<cti:img key="disabledRemove"/>
							</c:otherwise>
						</c:choose>
							<div class="nonwrapping">
								<capTags:regulatorTapIndicator paoId="${zone.regulatorId}" type="VOLTAGE_REGULATOR"/>
							</div>
						</div>
					</div>
				</cti:navigableHierarchy>
				<c:if test="${hasEditingRole}">
					<div class="actionArea">
						<cti:button key="add" onclick="javascript:showZoneWizard('${zoneCreatorUrl}');"/>
					</div>
				</c:if>
				<c:if test="${unassignedBanksExist}">
					<div class="strongWarningMessage"><i:inline key=".zoneList.unassignedBanks"/></div>
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
		        <c:set var="amChartsProduct" value="amxy"/>
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
		           var so = new SWFObject("${amSrc}", "amline", "${swfWidth}", "350", "8", "#FFFFFF");
		           so.useExpressInstall('${expressInstallSrc}');
		           so.write("${uniqueId}");
		        </script>
			</tags:boxContainer2>
		</cti:dataGridCell>
	</cti:dataGrid>
	
</cti:standardPage>
</cti:msgScope>