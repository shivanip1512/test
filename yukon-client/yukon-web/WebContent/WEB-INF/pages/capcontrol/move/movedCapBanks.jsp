<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<cti:standardPage title="Temp Move Report" module="capcontrol">

	<script type="text/javascript">
	    Event.observe(window, 'load', function() { getMoveBackMenu(); });
	</script>

    <cti:includeScript link="/capcontrol/js/cbc_funcs.js"/>
    <cti:includeScript link="/JavaScript/cc_command_menu.js"/>
    <cti:includeCss link="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>
    
    <ct:simplePopup onClose="closeTierPopup()" title="" id="tierPopup" styleClass="thinBorder menuItems">
	    <div id="popupBody"></div>
	</ct:simplePopup>
    
	<cti:url var="movedCapBanksUrl" value="/spring/capcontrol/move/movedCapBanks" />
	<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="" />
	<!-- necessary DIV element for the OverLIB popup library -->
	<div id="overDiv"style="position: absolute; visibility: hidden; z-index: 1000;"></div>
	<cti:standardMenu menuSelection="view|recentcapbankmoves" />
	<cti:breadCrumbs>
		<cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home" />
		<cti:crumbLink title="Recent Cap Bank Moves"/>
	</cti:breadCrumbs>

	<tags:pagedBox title="Moved Cap Banks" searchResult="${searchResult}"
		filterDialog="" baseUrl="${movedCapBanksUrl}"
		isFiltered="false" showAllUrl="${movedCapBanksUrl}">
			
		<c:choose>
			<c:when test="${searchResult.hitCount == 0}">
				No items to display.
			</c:when>
			<c:otherwise>
				<table id="movedCBTable" class="compactResultsTable activeResultsTable" align="center">
		               <tr class="columnHeader lAlign">
		                <th>Recent Feeder</th>
		                <th>Original Feeder</th>
		                <th>Cap Bank</th>
		            </tr>
					<c:forEach var="movedCapbank" items="${movedCaps}">
						<tr id="tr_cap_${movedCapbank.capbank.ccId}" class="<ct:alternateRow odd="" even="altRow"/>">
							<td id="${movedCapbank.capbank.ccName}">
		                           <a href="javascript:void(0);" style="color: #F09100;cursor: pointer;" onclick="getCapBankTempMoveBack('${movedCapbank.capbank.ccId}', event)">
		                               ${movedCapbank.currentFeederName}
		                           </a>
		                       </td>
							<td>${movedCapbank.originalFeederName}</td>
							<td>${movedCapbank.capbank.ccName}</td>
						</tr>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>
	</tags:pagedBox>
</cti:standardPage>