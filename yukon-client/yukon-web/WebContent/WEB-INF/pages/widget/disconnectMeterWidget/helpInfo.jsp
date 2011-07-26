<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<table class="compactResultsTable">
	<tr>
        <th colspan="2"><i:inline key=".statusMessages"/></th>
    </tr>
	<tr>
		<td>
			<tags:nameValueContainer2>
				<c:if test="${is410Supported}">
					<tags:nameValue2 nameKey=".mct410Devices">
						<tags:nameValue2 nameKey=".stateGroupHeader"  argument="${stateGroups[0]}">
							<i:inline key=".confirmedDisconnectMsg"/>
						</tags:nameValue2>
						<tags:nameValueGap2 gapHeight="6px" />
				
						<tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[1]}">
							<i:inline key=".connectedMsg"/>
						</tags:nameValue2>
						<tags:nameValueGap2 gapHeight="6px" />
					
						<tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[2]}">
							<i:inline key=".unconfirmedDisconnectMsg"/>
						</tags:nameValue2>
						<tags:nameValueGap2 gapHeight="6px" />
					
						<tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[3]}">
							<i:inline key=".connectArmedMsg"/>
						</tags:nameValue2>
						<tags:nameValueGap2 gapHeight="6px" />
					</tags:nameValue2>
				</c:if>
				<c:if test="${is310Supported}">
					<tags:nameValue2 nameKey=".mct310Devices">
						<tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[0]}">
                            <i:inline key=".openMsg"/>
						</tags:nameValue2>
						<tags:nameValueGap2 gapHeight="6px" />
					
						<tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[1]}">
                            <i:inline key=".closedMsg"/>
						</tags:nameValue2>
						<tags:nameValueGap2 gapHeight="6px" />
					
						<tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[2]}">
							<i:inline key=".unknownMsg"/>
						</tags:nameValue2>
						<tags:nameValueGap2 gapHeight="6px" />
					</tags:nameValue2>
				</c:if>
				<c:if test="${is213Supported}">
					<tags:nameValue2 nameKey=".mct213Devices">
						<tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[0]}">
							<i:inline key=".openMsg"/>
						</tags:nameValue2>
						<tags:nameValueGap2 gapHeight="6px" />
					
						<tags:nameValue2 nameKey=".stateGroupHeader" argument="${stateGroups[1]}">
                            <i:inline key=".closedMsg"/>							
						</tags:nameValue2>
						<tags:nameValueGap2 gapHeight="6px" />
					</tags:nameValue2>
				</c:if>
			</tags:nameValueContainer2>
		</td>
	</tr>
	<tr align="left">
		<td>
			<span style="font-size: .8em"><i:inline key=".disconnectNote"/></span>
		</td>
	</tr>
</table>
