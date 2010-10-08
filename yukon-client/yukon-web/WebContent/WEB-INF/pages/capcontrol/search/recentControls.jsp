<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage title="Results" module="capcontrol">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>

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
               
            <tags:abstractContainer type="box" title="Events For ${eventSet.paoName}" hideEnabled="true" showInitially="true">
				<table id="resTable${eventSet.paoId}" class="compactResultsTable" width="95%">
				    <thead>
	                    <tr>
	                        <th>Timestamp</th>
	                        <th>Device Controlled</th>
	                        <th>Item</th>
	                        <th>Event</th>
	                        <th>User</th>
	                    </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty eventSet.controlEvents}">
                                <tr>
                                    <td colspan="5">No data found</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="event" items="${eventSet.controlEvents}">
                                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                        <td>${event.formattedTimestamp}</td>
                                        
                                        <c:choose>
                                            <c:when test="${event.pointId == null || event.pointId <= 0}">
                                                <td>----</td>
                                            </c:when>
                                            <c:otherwise>
                                                <td>${event.deviceControlled}</td>
                                            </c:otherwise>
                                        </c:choose>
        
                                        <c:choose>
                                            <c:when test="${event.timestamp == null}">
                                                <td>----</td>
                                            </c:when>
                                            <c:otherwise>
                                                <td>${event.item}</td>
                                            </c:otherwise>
                                        </c:choose>
        
                                        <c:choose>
                                            <c:when test="${event.event == null}">
                                                <td>----</td>
                                            </c:when>
                                            <c:otherwise>
                                                <td>${event.event}</td>
                                            </c:otherwise>
                                        </c:choose>
        
                                        <c:choose>
                                            <c:when test="${event.user == null}">
                                                <td>----</td>
                                            </c:when>
                                            <c:otherwise>
                                                <td>${event.user}</td>
                                            </c:otherwise>
                                        </c:choose>
                                    </tr>
                                </c:forEach>
                                
                            </c:otherwise>
                        </c:choose>
                        
					</tbody>
				</table>
			</tags:abstractContainer>
            
            <br>
            
		</form>
	</c:forEach>
</cti:standardPage>