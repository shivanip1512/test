<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="operator" page="inventoryConfiguration">

	<cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css" />

	<tags:boxContainer2 nameKey="actionsContainer" hideEnabled="false">

		<div class="containerHeader">
			<table>
				<tr>
					<td valign="top" colspan="2">
						<tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection" />
					</td>
				</tr>

				<tr>
					<td class="smallBoldLabel"><i:inline key=".instructionsLabel" /></td>
					<td><i:inline key=".instructions" /></td>
				</tr>
			</table>
		</div>

		<br>
		<cti:dataGrid cols="1" tableClasses="twoColumnLayout split">
			<cti:dataGridCell>
				<table>
					<tr>
						<td class="actionCell">
							<form action="deviceReconfig/setup" method="get">
								<cti:inventoryCollection inventoryCollection="${inventoryCollection}" />
								<cti:button nameKey="deviceReconfig" type="submit" styleClass="buttonGroup" />
							</form>
						</td>
						<td class="actionCell"><i:inline key=".deviceReconfigDescription" /></td>
					</tr>
				</table>
			</cti:dataGridCell>
			<cti:dataGridCell>
				<table>
					<tr>
						<td class="actionCell">
							<form action="resendConfig/view" method="get">
								<cti:inventoryCollection inventoryCollection="${inventoryCollection}" />
								<cti:button nameKey="resendConfig" type="submit" styleClass="buttonGroup" />
							</form>
						</td>
						<td class="actionCell"><i:inline key=".resendConfigDescription" /></td>
					</tr>
				</table>
			</cti:dataGridCell>
			<cti:dataGridCell>
				<cti:url value="/stars/operator/inventory/inventoryActions?" var="cancelUrl" />
				<cti:button href="${cancelUrl}${pageContext.request.queryString}" nameKey="cancel"/>
			</cti:dataGridCell>
		</cti:dataGrid>

	</tags:boxContainer2>

</cti:standardPage>