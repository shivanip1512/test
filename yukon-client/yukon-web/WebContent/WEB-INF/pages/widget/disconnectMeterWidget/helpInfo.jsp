<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<table class="compactResultsTable">
	<tr>
        <th colspan="2"><i:inline key=".statusMessages"/></th>
    </tr>
	<tr>
		<td>
			<ct:nameValueContainer2>
				<c:if test="${is410Supported}">
					<ct:nameValue2 nameKey=".mct410Devices">
						<ct:nameValue2 nameKey=".stateGroupHeader"  argument="${stateGroups[0]}">
							<i:inline key=".confirmedDisconnectMsg"/>
						</ct:nameValue2>
						<ct:nameValueGap2 gapHeight="6px" />
				
						<ct:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[1]}">
							<i:inline key=".connectedMsg"/>
						</ct:nameValue2>
						<ct:nameValueGap2 gapHeight="6px" />
					
						<ct:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[2]}">
							<i:inline key=".unconfirmedDisconnectMsg"/>
						</ct:nameValue2>
						<ct:nameValueGap2 gapHeight="6px" />
					
						<ct:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[3]}">
							<i:inline key=".connectArmedMsg"/>
						</ct:nameValue2>
						<ct:nameValueGap2 gapHeight="6px" />
					</ct:nameValue2>
				</c:if>
				<c:if test="${is310Supported}">
					<ct:nameValue2 nameKey=".mct310Devices">
						<ct:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[0]}">
                            <i:inline key=".openMsg"/>
						</ct:nameValue2>
						<ct:nameValueGap2 gapHeight="6px" />
					
						<ct:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[1]}">
                            <i:inline key=".closedMsg"/>
						</ct:nameValue2>
						<ct:nameValueGap2 gapHeight="6px" />
					
						<ct:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[2]}">
							<i:inline key=".unknownMsg"/>
						</ct:nameValue2>
						<ct:nameValueGap2 gapHeight="6px" />
					</ct:nameValue2>
				</c:if>
				<c:if test="${is213Supported}">
					<ct:nameValue2 nameKey=".mct213Devices">
						<ct:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[0]}">
							<i:inline key=".openMsg"/>
						</ct:nameValue2>
						<ct:nameValueGap2 gapHeight="6px" />
					
						<ct:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[1]}">
                            <i:inline key=".closedMsg"/>							
						</ct:nameValue2>
						<ct:nameValueGap2 gapHeight="6px" />
					</ct:nameValue2>
				</c:if>
			</ct:nameValueContainer2>
		</td>
	</tr>
	<tr align="left">
		<td>
			<span style="font-size: .8em"><i:inline key=".disconnectNote"/></span>
		</td>
	</tr>
</table>
