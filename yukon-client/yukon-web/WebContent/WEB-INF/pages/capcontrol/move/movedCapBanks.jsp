<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="movedCapBanks">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    <script type="text/javascript">
    jQuery(function() {
        jQuery('#movedCBTable').on('click', 'a.moveLink', function (event) {
            var a = event.currentTarget;
            Yukon.CapControl.getMovedBankMenu(a.id, event);
        });
    });
    </script>

	<cti:url var="movedCapBanksUrl" value="/capcontrol/move/movedCapBanks" />
	
    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="" />
	
    <tags:pagedBox2 nameKey="movedContainer" 
            searchResult="${searchResult}"
            baseUrl="${movedCapBanksUrl}"
            isFiltered="false" 
            showAllUrl="${movedCapBanksUrl}"
            styleClass="padBottom">
			
		<c:choose>
			<c:when test="${searchResult.hitCount == 0}">
				<i:inline key=".noRecentMoves"/>
			</c:when>
			<c:otherwise>
				<table id="movedCBTable" class="compact-results-table activeResultsTable">
                    <tr>
		                <th><i:inline key=".recentFeeder"/></th>
		                <th><i:inline key=".originalFeeder"/></th>
		                <th><i:inline key=".capBank"/></th>
		            </tr>
					<c:forEach var="movedCapbank" items="${movedCaps}">
						<tr id="tr_cap_${movedCapbank.capbank.ccId}">
							<td id="${movedCapbank.capbank.ccName}">
                                <a href="javascript:void(0);" class="moveLink" id="${movedCapbank.capbank.ccId}">${movedCapbank.currentFeederName}</a>
                            </td>
							<td>${movedCapbank.originalFeederName}</td>
							<td>${movedCapbank.capbank.ccName}</td>
						</tr>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>
	</tags:pagedBox2>
</cti:standardPage>