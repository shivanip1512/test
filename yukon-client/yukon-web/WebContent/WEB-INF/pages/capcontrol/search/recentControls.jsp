<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<cti:standardPage title="Results" module="capcontrol">
    <script>
	    function dateFilter(selectID, values) {
	        url = "/spring/capcontrol/search/recentControls";
	        dayCnt = $(selectID).options[$(selectID).selectedIndex].value;
	        url+= "?dayCnt=" + dayCnt;
	        for (var i=0; i < values.length; i++) {
	            url += "&value=" + values[i];
	        }
	        window.location.replace(url);
	    }
    </script>
	<cti:standardMenu menuSelection="view|recentcontrols"/>

	<!-- necessary DIV element for the OverLIB popup library -->
	<div id="overDiv" style="position: absolute; visibility: hidden; z-index: 1000;"></div>

	<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="" />

    <cti:breadCrumbs>
	   <cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home" /> 
	   <cti:crumbLink title="Events" />
	</cti:breadCrumbs>

	<div align="left">
		<table id="filterTable">
			<tr>
				<td>Filter By Date <br />
					<select onchange="dateFilter('rcDateFilter', ${paoIdString})" id="rcDateFilter">
                        <c:forEach var="i" begin="1" end="7">
                            <option value="${i}" <c:if test="${i == dayCnt}">selected</c:if>> ${i} Day (s) </option>
					   </c:forEach>
					</select>
				</td>
			</tr>
		</table>
	</div>

	<c:forEach var="eventSet" items="${listOfEventSets}">

		<form id="resForm" action="feeders.jsp" method="post">
            <input type="hidden" name="itemid" />
               
            <div style="padding-top: 10px;padding-bottom: 5px;" align="center">
				<table id="resTable${eventSet.paoId}" class="miniResultsTable" width="100%">
				    <thead style="border-bottom: none;">
	                    <tr><th colspan="5" style="color: black; font-size: 14; text-align: left;">Events For ${eventSet.paoName}</th></tr>
	                    <tr style="border-bottom: none;">
	                        <th width="17%" style="border-bottom: none;">Timestamp</th>
	                        <th width="14%" style="border-bottom: none;">Device Controlled</th>
	                        <th width="20%" style="border-bottom: none;">Item</th>
	                        <th width="36%" style="border-bottom: none;">Event</th>
	                        <th width="13%" style="border-bottom: none;">User</th>
	                    </tr>
                    </thead>
                    <tfoot/>
                    <tbody style="border-top: none;">
                        <tr style="border-top: none;">
	                        <td colspan="5" style="padding: 0;border-bottom: none;border-right:none;border-top: none;">
		                        <div style="max-height:14em;overflow-y: auto;overflow-x: hidden;">
			                        <table id="innerResTable${eventSet.paoId}"  width="100%" style="border-top: none;border-left: none;border-bottom: none;border-right:none;border-collapse: collapse;">
				                        <tbody>
											<c:choose>
												<c:when test="${empty eventSet.controlEvents}">
													<tr class="alert cAlign" style="border-top: none;border-left: none;border-bottom: none;border-right:none;border-collapse: collapse;">
														<td colspan="5" width="100%" style="color: red;border-left: none;border-bottom: none;border-right:none;">No data found</td>
													</tr>
						                        </c:when>
						                        <c:otherwise>
													<c:forEach var="event" items="${eventSet.controlEvents}">
														<tr class="<ct:alternateRow odd="" even="altRow"/>" style="border-top: none;border-left: none;border-bottom: none;border-right:none;border-collapse: collapse;">
															<td width="17%" style="border-left: none;border-bottom: none;border-right:none;">${event.formattedTimestamp}</td>
															
															<c:choose>
																<c:when test="${event.pointId == null || event.pointId <= 0}">
																	<td width="14%" style="border-bottom: none;border-right:none;">----</td>
																</c:when>
																<c:otherwise>
																	<td width="14%" style="border-bottom: none;border-right:none;">${event.deviceControlled}</td>
																</c:otherwise>
															</c:choose>
							
															<c:choose>
																<c:when test="${event.timestamp == null}">
																	<td width="20%" style="border-bottom: none;border-right:none;">----</td>
																</c:when>
																<c:otherwise>
																	<td width="20%" style="border-bottom: none;border-right:none;">${event.item}</td>
																</c:otherwise>
															</c:choose>
							
															<c:choose>
																<c:when test="${event.event == null}">
																	<td width="36%" style="border-bottom: none;border-right:none;">----</td>
																</c:when>
																<c:otherwise>
																	<td width="36%" style="border-bottom: none;border-right:none;">${event.event}</td>
																</c:otherwise>
															</c:choose>
							
															<c:choose>
																<c:when test="${event.user == null}">
																	<td width="13%" style="border-bottom: none;border-right:none;">----</td>
																</c:when>
																<c:otherwise>
																	<td width="13%" style="border-bottom: none;border-right:none;">${event.user}</td>
																</c:otherwise>
															</c:choose>
														</tr>
													</c:forEach>
												</c:otherwise>
											</c:choose>
										</tbody>
									</table>
								</div>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</form>
	</c:forEach>
</cti:standardPage>