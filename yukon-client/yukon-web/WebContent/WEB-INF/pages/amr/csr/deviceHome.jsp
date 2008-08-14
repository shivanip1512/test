<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage title="Device Home Page" module="amr">
	<cti:standardMenu menuSelection="deviceselection" />
	<cti:breadCrumbs>
		<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
		<cti:crumbLink url="/spring/csr/search" title="Device Selection" />
    	&gt; <cti:deviceName deviceId="${deviceId}"></cti:deviceName>
</cti:breadCrumbs>

	<table class="widgetColumns">
		<tr>
			<td class="widgetColumnCell" valign="top">
				<h2>
					Device Detail
				</h2>
			</td>
			<td  class="widgetColumnCell" align="right">
				<amr:searchResultsLink></amr:searchResultsLink>
				<div style="margin-top: 5px;">
					<form name="quickSearchForm" action="/spring/csr/search">
						<input type="hidden" name="orderBy" value="METERNUMBER" />
						Quick Search:
						<input type="text" id="Quick Search" name="Quick Search" />
						<input type="submit" value="Search" />
					</form>
				</div>
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
                            <c:url var="moveInUrl" value="/spring/csr/moveIn">
                                <c:param name="deviceId" value="${deviceId}" />
                            </c:url>
                            <a href="${moveInUrl}">Move In</a> <br />

                            <c:url var="moveOutUrl" value="/spring/csr/moveOut">
                                <c:param name="deviceId" value="${deviceId}" />
                            </c:url>
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
                            <c:url var="highBillUrl" value="/spring/csr/highBill/view">
                                <c:param name="deviceId" value="${deviceId}" />
                            </c:url>
                            <a href="${highBillUrl}">High Bill Complaint</a>
                        </c:when>
                        <c:otherwise>
                        High Bill Complaint (not supported)
                        </c:otherwise>
                    </c:choose>
                    <br/>
                    </cti:checkProperty>
                    </cti:checkRole>
                    
					<cti:checkRole role="CommanderRole.ROLEID">
                        
                        <!-- Actions: Profile -->
                        <c:choose>
                            <c:when test="${lpSupported && (profileCollection || profileCollectionScanning)}">
        						<c:url var="profileUrl" value="/spring/amr/profile/home">
        							<c:param name="deviceId" value="${deviceId}" />
        						</c:url>
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
        						<c:url var="voltageTouUrl" value="/spring/amr/voltageAndTou/home">
        							<c:param name="deviceId" value="${deviceId}" />
        						</c:url>
        						<a href="${voltageTouUrl}">Voltage &amp; TOU</a>
                            </c:when>
                            <c:otherwise>
                            Voltage &amp; TOU (not supported)
                            </c:otherwise>
                        </c:choose>
                        <br/>
                        
                        <!-- Actions: >Manual Commander -->
						<c:url var="commanderUrl" value="/spring/amr/manualCommand/home">
							<c:param name="deviceId" value="${deviceId}" />
						</c:url>
						<a href="${commanderUrl}">Manual Commander</a>
						<br>
                        
                        <!-- Actions: Locate Route -->
                        <cti:checkProperty property="operator.DeviceActionsRole.LOCATE_ROUTE">
                        <c:url var="routeLocateUrl" value="/spring/csr/routeLocate/home">
                            <c:param name="collectionType" value="idList" />
                            <c:param name="idList.ids" value="${deviceId}" />
                        </c:url>
                        <a href="${routeLocateUrl}">Locate Route</a>
                        <br>
                        </cti:checkProperty>
                        
                        <!-- Actions: Other Collection actions -->
                        <c:url var="collectionActionsUrl" value="/spring/bulk/collectionActions">
                            <c:param name="collectionType" value="idList" />
                            <c:param name="idList.ids" value="${deviceId}" />
                        </c:url>
                        <a href="${collectionActionsUrl}">Other Actions...</a>
                        <br>
                        
					</cti:checkRole>
                    
                    <c:url var="commanderUrl" value="/spring/moveInMoveOut/moveInMoveOut">
                        <c:param name="deviceId" value="${deviceId}" />
                    </c:url>
                    <br />
                    
				</ct:boxContainer>

			</td>
			<td class="widgetColumnCell" valign="top">

				<ct:widget bean="csrTrendWidget" tabularDataViewer="csrArchivedDataReport" />
				
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
