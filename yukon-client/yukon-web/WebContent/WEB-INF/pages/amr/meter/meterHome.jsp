<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage title="Meter Home Page" module="amr">
	<cti:standardMenu menuSelection="meters" />
	<cti:breadCrumbs>
		<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
		<cti:crumbLink url="/spring/meter/start" title="Metering" />
		<c:if test="${searchResults != null}">
			<cti:crumbLink url="${searchResults}" title="Search" />
		</c:if>
    	<cti:crumbLink><cti:deviceName deviceId="${deviceId}"></cti:deviceName></cti:crumbLink>
</cti:breadCrumbs>

	<table class="widgetColumns">
		<tr>
			<td class="widgetColumnCell" valign="top">
				<h2>
					Meter Detail
				</h2>
			</td>
			<td  class="widgetColumnCell" align="right">
				<amr:searchResultsLink></amr:searchResultsLink>
			</td>
		</tr>
	</table>


	<ct:widgetContainer deviceId="${deviceId}" identify="false">

		<table class="widgetColumns"><tr>
			<td class="widgetColumnCell" valign="top">
				<ct:widget bean="meterInformationWidget" />

				<ct:widget bean="meterReadingsWidget" />

				<c:if test="${mspSupported}">
					<ct:widget bean="accountInformationWidget" />
				</c:if>

                <ct:widget bean="deviceGroupWidget" />

				<ct:boxContainer title="Actions" styleClass="widgetContainer">
                
                    <!-- Actions: High Bill Complaint -->
                    <c:choose>
                        <c:when test="${moveSupported}">
                            <cti:url var="moveInUrl" value="/spring/meter/moveIn">
                                <cti:param name="deviceId" value="${deviceId}" />
                            </cti:url>
                            <a href="${moveInUrl}">Move In</a> <br />

                            <cti:url var="moveOutUrl" value="/spring/meter/moveOut">
                                <cti:param name="deviceId" value="${deviceId}" />
                            </cti:url>
                            <a href="${moveOutUrl}">Move Out</a> <br />
                        </c:when>
                        <c:otherwise>
                        Move In (not supported) <br />
                        Move Out (not supported) <br />
                        </c:otherwise>
                    </c:choose>
                    
                    <cti:checkRole role="operator.MeteringRole.ROLEID">
                    <cti:checkProperty property="operator.MeteringRole.HIGH_BILL_COMPLAINT">
                    <c:choose>
                        <c:when test="${highBillSupported}">
                            <cti:url var="highBillUrl" value="/spring/meter/highBill/view">
                                <cti:param name="deviceId" value="${deviceId}" />
                            </cti:url>
                            <a href="${highBillUrl}">High Bill Complaint</a>
                        </c:when>
                        <c:otherwise>
                        High Bill Complaint (not supported)
                        </c:otherwise>
                    </c:choose>
                    <br/>
                    </cti:checkProperty>
                    </cti:checkRole>
                    
                        <!-- Actions: Profile -->
                        <c:choose>
                            <c:when test="${lpSupported && (profileCollection || profileCollectionScanning)}">
        						<cti:url var="profileUrl" value="/spring/amr/profile/home">
        							<cti:param name="deviceId" value="${deviceId}" />
        						</cti:url>
                                <a href="${profileUrl}">Profile</a><br/>
                            </c:when>
                            <c:when test="${not (profileCollection || profileCollectionScanning)}">
                                <%-- user has no access, hide --%>
                            </c:when>
						    <c:otherwise>
                            Profile (not supported)<br/>
                            </c:otherwise>
                        </c:choose>
                        
                        
						<!-- Actions: Voltage & TOU -->
                        <c:choose>
                            <c:when test="${isMCT4XX && voltageSupported}">
        						<cti:url var="voltageTouUrl" value="/spring/amr/voltageAndTou/home">
        							<cti:param name="deviceId" value="${deviceId}" />
        						</cti:url>
        						<a href="${voltageTouUrl}">Voltage &amp; TOU</a>
                            </c:when>
                            <c:otherwise>
                            Voltage &amp; TOU (not supported)
                            </c:otherwise>
                        </c:choose>
                        <br/>
                        
                        <!-- Actions: >Manual Commander -->
						<cti:checkProperty property="CommanderRole.ENABLE_WEB_COMMANDER">
							<cti:url var="commanderUrl" value="/spring/amr/manualCommand/home">
								<cti:param name="deviceId" value="${deviceId}" />
							</cti:url>
							<a href="${commanderUrl}">Manual Commander</a>
							<br>
						</cti:checkProperty>
                        
                        <!-- Actions: Locate Route -->
                        <cti:checkProperty property="operator.DeviceActionsRole.LOCATE_ROUTE">
                        <cti:url var="routeLocateUrl" value="/spring/bulk/routeLocate/home">
                            <cti:param name="collectionType" value="idList" />
                            <cti:param name="idList.ids" value="${deviceId}" />
                        </cti:url>
                        <a href="${routeLocateUrl}">Locate Route</a>
                        <br>
                        </cti:checkProperty>


                    <!-- Actions: Other Collection actions -->
                    <cti:url var="collectionActionsUrl" value="/spring/bulk/collectionActions">
                        <cti:param name="collectionType" value="idList" />
                        <cti:param name="idList.ids" value="${deviceId}" />
                    </cti:url>
                    <a href="${collectionActionsUrl}">Other Actions...</a>
                        
                    <cti:url var="commanderUrl" value="/spring/moveInMoveOut/moveInMoveOut">
                        <cti:param name="deviceId" value="${deviceId}" />
                    </cti:url>
                    <br />
                    
				</ct:boxContainer>

			</td>
			<td class="widgetColumnCell" valign="top">

				<ct:widget bean="csrTrendWidget" tabularDataViewer="archivedDataReport" />
				
				<c:if test="${disconnectSupported}">
					<ct:widget bean="disconnectMeterWidget" />
				</c:if>

				<c:if test="${outageSupported}">
					<ct:widget bean="meterOutagesWidget" />
				</c:if>

				<c:if test="${touSupported}">
					<ct:widget bean="touWidget" />
				</c:if>
			</td>
		</tr></table>

	</ct:widgetContainer>

</cti:standardPage>
