<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="meterDetail">

	<div style="float: right;">
		<amr:searchResultsLink></amr:searchResultsLink>
	</div>

	<ct:widgetContainer deviceId="${deviceId}" identify="false">

		<table class="widgetColumns">
			<tr>
				<td class="widgetColumnCell" valign="top">
				    <ct:widget bean="meterInformationWidget" />
	
					<ct:widget bean="meterReadingsWidget" />
                    
                    <c:if test="${isRFMesh_JUST_HIDE_FOR_NOW}">
                        <ct:widget bean="rfnMeterInfoWidget" />
                    </c:if>
                    
					<c:if test="${cisInfoWidgetName != null}">
						<ct:widget bean="${cisInfoWidgetName}" />
					</c:if>
	
	                <ct:widget bean="deviceGroupWidget"/>

					<ct:boxContainer2 nameKey="actions" styleClass="widgetContainer">
	                
	                    <!-- Actions: Move In/Out -->
                        <c:if test="${moveSupported}">
                            <cti:url var="moveInUrl" value="/spring/meter/moveIn">
                                <cti:param name="deviceId" value="${deviceId}" />
                            </cti:url>
                            <a href="${moveInUrl}"><i:inline key=".moveIn"/></a><br/>

                            <cti:url var="moveOutUrl" value="/spring/meter/moveOut">
                                <cti:param name="deviceId" value="${deviceId}" />
                            </cti:url>
                            <a href="${moveOutUrl}"><i:inline key=".moveOut"/></a><br/>
                        </c:if>
	                    
						<!-- Actions: High Bill Complaint -->
	                    <cti:checkProperty property="operator.MeteringRole.HIGH_BILL_COMPLAINT">
	                        <c:if test="${highBillSupported}">
	                            <cti:url var="highBillUrl" value="/spring/meter/highBill/view">
	                                <cti:param name="deviceId" value="${deviceId}" />
	                            </cti:url>
	                            <a href="${highBillUrl}"><i:inline key=".highBill"/></a><br/>
	                        </c:if>
	                    </cti:checkProperty>
	                    
                        <!-- Actions: Profile -->
                        <%-- need one of these at least for the profile page to display anything --%>
                        <c:if test="${lpSupported || peakReportSupported}">
        					<cti:url var="profileUrl" value="/spring/amr/profile/home">
        						<cti:param name="deviceId" value="${deviceId}" />
							</cti:url>
                            <a href="${profileUrl}"><i:inline key=".profile"/></a><br/>
						</c:if >
	                        
						<!-- Actions: Voltage & TOU -->
						<c:if test="${isMCT4XX && voltageSupported}">
							<cti:url var="voltageTouUrl" value="/spring/amr/voltageAndTou/home">
								<cti:param name="deviceId" value="${deviceId}" />
							</cti:url>
       						<a href="${voltageTouUrl}"><i:inline key=".voltageAndTou"/></a><br/>
						</c:if>
                       
						<!-- Actions: Manual Commander -->
						<cti:checkProperty property="CommanderRole.ENABLE_WEB_COMMANDER">
                        	<c:if test="${porterCommandRequestsSupported}">
								<cti:url var="commanderUrl" value="/spring/amr/manualCommand/home">
        							<cti:param name="deviceId" value="${deviceId}" />
        						</cti:url>
        						<a href="${commanderUrl}"><i:inline key=".manualCommander"/></a><br/>
        					</c:if>
        				</cti:checkProperty>
	                        
                        <!-- Actions: Locate Route -->
                        <cti:checkProperty property="operator.DeviceActionsRole.LOCATE_ROUTE">
							<c:if test="${porterCommandRequestsSupported}">
								<cti:url var="routeLocateUrl" value="/spring/bulk/routeLocate/home">
        	                    	<cti:param name="collectionType" value="idList" />
        	                        <cti:param name="idList.ids" value="${deviceId}" />
								</cti:url>
        	                    <a href="${routeLocateUrl}"><i:inline key=".locateRoute"/></a><br/>
							</c:if>
						</cti:checkProperty>

	                    <!-- Actions: Other Collection actions -->
	                    <cti:url var="collectionActionsUrl" value="/spring/bulk/collectionActions">
	                        <cti:param name="collectionType" value="idList" />
	                        <cti:param name="idList.ids" value="${deviceId}" />
	                    </cti:url>
	                    <a href="${collectionActionsUrl}"><i:inline key=".otherActions"/></a><br/>
	                    
					</ct:boxContainer2>
	
				</td>
				<td class="widgetColumnCell" valign="top">
	
					<ct:widget bean="csrTrendWidget" tabularDataViewer="archivedDataReport" />
					
					<c:if test="${disconnectSupported}">
						<ct:widget bean="disconnectMeterWidget"/>
					</c:if>
					
					<c:if test="${rfnDisconnectSupported}">
						<ct:widget bean="rfnMeterDisconnectWidget"/>
					</c:if>
	
					<c:if test="${outageSupported}">
						<ct:widget bean="meterOutagesWidget" />
					</c:if>
	
					<c:if test="${touSupported}">
						<ct:widget bean="touWidget" />
					</c:if>
					
					<c:if test="${configSupported}">
	                    <ct:widget bean="configWidget" />
	                </c:if>
				</td>
			</tr>
		</table>

	</ct:widgetContainer>

</cti:standardPage>
