<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msg2 key="yukon.web.modules.widgets.disconnectMeterWidget.mct410Devices" var="mct410Devices" />
<cti:msg2 key="yukon.web.modules.widgets.disconnectMeterWidget.mct310Devices" var="mct310Devices" />
<cti:msg2 key="yukon.web.modules.widgets.disconnectMeterWidget.mct213Devices" var="mct213Devices" />

<cti:msg2 key="yukon.web.modules.widgets.disconnectMeterWidget.statusMessages" var="statusMessages" />
<cti:msg2 key="yukon.web.modules.widgets.disconnectMeterWidget.connectedMsg" var="connectedMsg" />
<cti:msg2 key="yukon.web.modules.widgets.disconnectMeterWidget.connectArmedMsg" var="connectArmedMsg" />
<cti:msg2 key="yukon.web.modules.widgets.disconnectMeterWidget.confirmedDisconnectMsg" var="confirmedDisconnectMsg" />
<cti:msg2 key="yukon.web.modules.widgets.disconnectMeterWidget.unconfirmedDisconnectMsg" var="unconfirmedDisconnectMsg" />

<cti:msg2 key="yukon.web.modules.widgets.disconnectMeterWidget.openMsg" var="openMsg" />
<cti:msg2 key="yukon.web.modules.widgets.disconnectMeterWidget.closedMsg" var="closedMsg" />
<cti:msg2 key="yukon.web.modules.widgets.disconnectMeterWidget.unknownMsg" var="unknownMsg" />

<cti:msg2 key="yukon.web.modules.widgets.disconnectMeterWidget.disconnectNote" var="disconnectNote" />

<table class="compactResultsTable">
	<tr>
        <th colspan="2">${statusMessages}</th>
    </tr>
	<tr>
		<td>
			<ct:nameValueContainer>
				<c:if test="${is410Supported}">
					<ct:nameValue name="${mct410Devices}">
						<ct:nameValue name="${stateGroups[0]}" nameColumnWidth="200px">
							${confirmedDisconnectMsg}
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
				
						<ct:nameValue name="${stateGroups[1]}">
							${connectedMsg}
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
					
						<ct:nameValue name="${stateGroups[2]}">
							${unconfirmedDisconnectMsg}
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
					
						<ct:nameValue name="${stateGroups[3]}">
							${connectArmedMsg}
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
					</ct:nameValue>
				</c:if>
				<c:if test="${is310Supported}">
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
				<c:if test="${is213Supported}">
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
	<tr align="left">
		<td>
			<span style="font-size: .8em">${disconnectNote}</span>
		</td>
	</tr>
</table>
