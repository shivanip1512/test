<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage title="Device Home Page" module="amr">
	<cti:standardMenu menuSelection="deviceselection" />
	<cti:breadCrumbs>
		<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
		<cti:crumbLink url="/spring/csr/search" title="Device Selection" />
    &gt; Device Detail
</cti:breadCrumbs>

	<table class="widgetColumns">
		<tr>
			<td>
				<h2 style="display: inline;">
					<cti:deviceName deviceId="${deviceId}"></cti:deviceName>
				</h2>
			</td>
			<td align="right">
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

	<br />

	<ct:widgetContainer deviceId="${deviceId}" identify="false">

		<div class="widgetColumns">
			<div class="left">
				<ct:widget height="75px" bean="meterInformationWidget" />

				<ct:widget bean="meterReadingsWidget" />

				<c:if test="${mspSupported}">
					<ct:widget height="185px" bean="accountInformationWidget" />
				</c:if>

				<ct:boxContainer title="Actions">
					<c:choose>
						<c:when test="${highBillSupported}">
							<c:url var="highBillUrl" value="/spring/csr/highBill">
								<c:param name="deviceId" value="${deviceId}" />
							</c:url>
							<a href="${highBillUrl}">High Bill Complaint</a>
							<br />
						</c:when>
						<c:otherwise>
						High Bill Complaint (not supported)<br />
						</c:otherwise>
					</c:choose>
					<cti:checkRole role="CommanderRole.ROLEID">
						<c:url var="profileUrl" value="/apps/CommandDevice2.jsp">
							<c:param name="deviceID" value="${deviceId}" />
							<c:param name="lp" value="" />
						</c:url>
						<a href="${profileUrl}">Profile</a>
						<br />

						<c:url var="voltageTouUrl" value="/apps/CommandDevice2.jsp">
							<c:param name="deviceID" value="${deviceId}" />
							<c:param name="command" value="null" />
						</c:url>
						<a href="${voltageTouUrl}">Voltage &amp; TOU</a>
						<br>

						<c:url var="commanderUrl" value="/apps/CommandDevice.jsp">
							<c:param name="deviceID" value="${deviceId}" />
							<c:param name="manual" value="" />
							<c:param name="command" value="null" />
						</c:url>
						<a href="${commanderUrl}">Manual Commander</a>
						<br>
					</cti:checkRole>
				</ct:boxContainer>

			</div>
			<div class="right">

				<c:if test="${lpSupported && lpEnabled}">
					<ct:widget bean="profileWidget" />
				</c:if>
				
				<ct:widget bean="trendWidget" />
				
				<c:if test="${disconnectSupported}">
					<ct:widget bean="disconnectMeterWidget" />
				</c:if>

				<c:if test="${outageSupported}">
					<ct:widget bean="meterOutagesWidget" />
				</c:if>

				<c:if test="${deviceGroupsSupported}">
					<ct:widget bean="deviceGroupWidget" />
				</c:if>

				<c:if test="${touSupported}">
					<ct:widget bean="touWidget" />
				</c:if>
							</div>
		</div>

	</ct:widgetContainer>
	<div style="clear: both"></div>

	<ct:dataUpdateEnabler />
</cti:standardPage>
