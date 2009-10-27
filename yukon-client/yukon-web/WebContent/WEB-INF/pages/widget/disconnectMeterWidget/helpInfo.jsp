<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msg key="yukon.web.modules.widgets.disconnectMeterWidget.mct410Devices" var="mct410Devices" />
<cti:msg key="yukon.web.modules.widgets.disconnectMeterWidget.mct310Devices" var="mct310Devices" />
<cti:msg key="yukon.web.modules.widgets.disconnectMeterWidget.mct213Devices" var="mct213Devices" />

<cti:msg key="yukon.web.modules.widgets.disconnectMeterWidget.statusMessages" var="statusMessages" />
<cti:msg key="yukon.web.modules.widgets.disconnectMeterWidget.connectedMsg" var="connectedMsg" />
<cti:msg key="yukon.web.modules.widgets.disconnectMeterWidget.connectArmedMsg" var="connectArmedMsg" />
<cti:msg key="yukon.web.modules.widgets.disconnectMeterWidget.confirmedDisconnectMsg" var="confirmedDisconnectMsg" />
<cti:msg key="yukon.web.modules.widgets.disconnectMeterWidget.unconfirmedDisconnectMsg" var="unconfirmedDisconnectMsg" />

<cti:msg key="yukon.web.modules.widgets.disconnectMeterWidget.openMsg" var="openMsg" />
<cti:msg key="yukon.web.modules.widgets.disconnectMeterWidget.closedMsg" var="closedMsg" />
<cti:msg key="yukon.web.modules.widgets.disconnectMeterWidget.unknownMsg" var="unknownMsg" />

<cti:msg key="yukon.web.modules.widgets.disconnectMeterWidget.disconnectNote" var="disconnectNote" />

<style>
table.compactResultsTable tr.vertical-middle td,
table.compactResultsTable tr.vertical-middle td.name {
	vertical-align: middle;
	
}
table.nameValueTable tr.indent1 td.name {
	font-size: 1.0em;
}
table.nameValueTable tr.indent1 td.value {
	font-size: 0.9em;
}
</style>

<table class="compactResultsTable">
	<tr>
        <th colspan="2">${statusMessages}</th>
    </tr>
	<tr>
		<td>
			<ct:nameValueContainer>
				<c:if test="${isMCT4xx}">
					<ct:nameValue name="${mct410Devices}">
						<ct:nameValue name="${stateGroups[0]}" nameColumnWidth="200px">
							${connectedMsg}
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
				
						<ct:nameValue name="${stateGroups[1]}">
							${connectArmedMsg}
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
					
						<ct:nameValue name="${stateGroups[2]}">
							${confirmedDisconnectMsg}
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
					
						<ct:nameValue name="${stateGroups[3]}">
							${unconfirmedDisconnectMsg}
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
					</ct:nameValue>
				</c:if>
				<c:if test="${isMCT310}">
					<ct:nameValue name="${mct310Devices}">
						<ct:nameValue name="${stateGroups[0]}" nameColumnWidth="200px">
							${openMsg}
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
					
						<ct:nameValue name="${stateGroups[1]}">
							${closedMsg}
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
					
						<ct:nameValue name="${stateGroups[2]}">
							${unknownMsg}
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
					</ct:nameValue>
				</c:if>
				<c:if test="${isMCT213}">
					<ct:nameValue name="${mct213Devices}">
						<ct:nameValue name="${stateGroups[0]}" nameColumnWidth="200px">
							${openMsg}
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
					
						<ct:nameValue name="${stateGroups[1]}">
							${closedMsg}
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
					</ct:nameValue>
				</c:if>
			</ct:nameValueContainer>
		</td>
	</tr>
	<tr>
		<td>
			${disconnectNote}
		</td>
	</tr>
</table>
