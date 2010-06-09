<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<cti:standardPage title="Cap Bank capBankAdd Info" module="capcontrol_internal">

	<c:choose>
		<c:when test="${capBankAdd.deviceID > 0}">
			<table class="compactResultsTable">
                <tr>
                    <th>Setting</th>
                    <th>Value</th>
                </tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Maint Area ID</td>
					<td>${capBankAdd.maintAreaID}</td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Pole Number</td>
					<td>${capBankAdd.poleNumber}</td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Latitude</td>
					<td>${capBankAdd.latit}</td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Longitude</td>
					<td>${capBankAdd.longtit}</td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Cap Bank Configuration</td>
					<td>${capBankAdd.capBankConfig}</td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Communication Medium</td>
					<td>${capBankAdd.commMedium}</td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>External Antenna</td>
					<td>${capBankAdd.extAntenna}</td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Antenna Type</td>
					<td>${capBankAdd.antennaType}</td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Last Maintenance Visit</td>
					<td><cti:formatDate type="DATE" value="${capBankAdd.lastMaintVisit}"/></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Last Inspection</td>
					<td><cti:formatDate type="DATE" value="${capBankAdd.lastInspVisit}"/></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Op Count Reset Date</td>
					<td><cti:formatDate type="DATE" value="${capBankAdd.opCountResetDate}"/></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Potential Transformer</td>
					<td>${capBankAdd.potentTransformer}</td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Maintenance Request Pending</td>
					<td>${capBankAdd.maintReqPending}</td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Other Comments</td>
					<td>${capBankAdd.otherComments}</td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Opteam Comments</td>
					<td>${capBankAdd.opTeamComments}</td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>CBC Install Date</td>
					<td><cti:formatDate type="DATE" value="${capBankAdd.cbcBattInstallDate}"/></td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Cap Bank Map Address</td>
					<td>${lite.paoDescription}</td>
				</tr>
				<tr class="<tags:alternateRow odd="" even="altRow"/>">
					<td>Driving Directions</td>
					<td>${capBankAdd.driveDir}</td>
				</tr>
			</table>
		</c:when>
	</c:choose>

</cti:standardPage>