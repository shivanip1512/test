<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="modules.capcontrol.cbAddInfo">

	<c:choose>
		<c:when test="${capBankAdd.paoId > 0}">
			<table class="compactResultsTable">
                <tr>
                    <th><i:inline key=".setting"/></th>
                    <th><i:inline key=".value"/></th>
                </tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".address"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.address}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".maintAreaId"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.maintenanceAreaId}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".poleNumber"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.poleNumber}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".latitude"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.latitude}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".longitude"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.longitude}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".cbConfig"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.capbankConfig}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".commMedium"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.commMedium}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".externalAntenna"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.extAntenna}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".antennaType"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.antennaType}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".lastMaintVisit"/></td>
					<td><cti:formatDate type="DATE" value="${capBankAdd.lastMaintenanceVisit}"/></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".lastInspection"/></td>
					<td><cti:formatDate type="DATE" value="${capBankAdd.lastInspection}"/></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".opCountResetDate"/></td>
					<td><cti:formatDate type="DATE" value="${capBankAdd.opCountResetDate}"/></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".potentialTransformer"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.potentialTransformer}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".maintRequestPending"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.maintenanceRequired}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".otherComments"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.otherComments}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".opteamComments"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.opTeamComments}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".cbcInstallDate"/></td>
					<td><cti:formatDate type="DATE" value="${capBankAdd.cbcInstallDate}"/></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".cbMapAddress"/></td>
					<td><spring:escapeBody htmlEscape="true">${lite.paoDescription}</spring:escapeBody></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td><i:inline key=".drivingDirections"/></td>
					<td><spring:escapeBody htmlEscape="true">${capBankAdd.driveDirections}</spring:escapeBody></td>
				</tr>
			</table>
		</c:when>
	</c:choose>

</cti:msgScope>