<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<cti:standardPage title="Schedules" module="capcontrol">

	<script type="text/javascript" language="JavaScript">
    var firstRun = true;
    function removeSchedule(scheduleId, scheduleName) {

    	if( confirm("Are you sure you want to delete " + scheduleName + "?") ) {
	        var url = "/spring/capcontrol/schedule/deleteSchedule";
	
	        hideErrors();
	        
	        new Ajax.Request(url, {'parameters': {'scheduleId': scheduleId}, 
	            onComplete: function(transport, json) {
	                if (!json.success) {
	                    $('errorElement').innerHTML = json.resultText;
	                    $('errorElement').show();
	                } else {
	                    deleteScheduleFromPage(scheduleId);
	                }
	            
	            } });
    	}
    }

    function hideErrors() {
        
        if(!firstRun) { 
            $('errorElement').hide();
        }
        firstRun = false;
    }

    function deleteScheduleFromPage(scheduleId) {
        var line = document.getElementById('s_' + scheduleId);
        var table = document.getElementById('tableBody');

        table.removeChild(line);
    }
    </script>

	<cti:standardMenu menuSelection="view|schedules" />
	<cti:breadCrumbs>
		<cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home" />
	</cti:breadCrumbs>
	<b>
	<div id="errorElement" style="text-align: center; color: red"></div>
	</b>
	<table class="resultsTable" id="scheduleTable" width="90%" border="0"
		cellspacing="0" cellpadding="3" align="center">
		<thead>
			<tr id="header">
				<th>Schedule</th>
				<th>Next Run Time</th>
				<th>Last Run Time</th>
				<th>Interval</th>
				<th>Disabled</th>
				<th></th>
			</tr>
		</thead>
		<tbody id="tableBody">
			<c:forEach var="item" items="${scheduleList}">
				<tr class="<tags:alternateRow odd="" even="altRow"/>"
					id="s_${item.scheduleID}">
					<td><c:out value="${item.scheduleName}" /></td>
					<td>
                        <c:choose>
                            <c:when test="${item.nextRunTime.time <= startOfTime}">
                                ---
                            </c:when>
                            <c:otherwise>
                                <cti:formatDate value="${item.nextRunTime}" type="DATEHM" />
                            </c:otherwise>
                        </c:choose>
                    </td>
					<td>
                        <c:choose>
                            <c:when test="${item.lastRunTime.time <= startOfTime}">
                                ---
                            </c:when>
                            <c:otherwise>
                                <cti:formatDate value="${item.lastRunTime}" type="DATEHM" />
                            </c:otherwise>
                        </c:choose>
					</td>
					<td><c:out value="${item.intervalRateString}" /></td>
					<td>
                        <c:choose>
							<c:when test="${item.disabled}">
	                                Yes
	                        </c:when>
							<c:otherwise>
	                                No
	                        </c:otherwise>
                        </c:choose>
					</td>
					<td align="center">
                        <c:choose>
                            <c:when test="${hasEditingRole}">
	                            <a href="/editor/cbcBase.jsf?type=3&itemid=${item.scheduleID}">
	                                <img class="rAlign editImg" src="/WebConfig/yukon/Icons/pencil.gif" />
	                            </a>
	                            <img src="/WebConfig/yukon/Icons/delete.gif " class="pointer" onclick="removeSchedule(${item.scheduleID}, '${item.scheduleName}');">
                            </c:when>
                            <c:otherwise>
                                <a href="/editor/cbcBase.jsf?type=3&itemid=${item.scheduleID}">
                                    <img class="rAlign editImg" src="/WebConfig/yukon/Icons/help.gif" />
                                </a>
                            </c:otherwise>
                        </c:choose>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</cti:standardPage>