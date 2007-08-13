<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="submitUrl" value="/spring/macsscheduler/schedules" />
<cti:standardPage title="Scheduled Scripts" module="amr">
	<cti:standardMenu menuSelection="scheduler"/>
   	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
  	    <cti:crumbLink url="/spring/macsscheduler/schedules/view" title="Scheduler" />
	    &gt; Start Schedule
	</cti:breadCrumbs>

    <center>
        <div
            style="font-weight: bold; font-size: 1em; padding-bottom: 10px;">
            START SCHEDULE
        </div>
        <div style="font-size: 1em; padding-bottom: 10px;">
            ${schedule.scheduleName}
        </div>
        <div>
            <form id="startform" method="post" action="${submitUrl}/action" style="margin: 0px; padding: 0px;">
                <div style="padding: 10px;">
                    <table cellspacing="5">
                        <tr>
                            <td>
                                <div
                                    style="font-weight: bold; font-size: 0.9em;">
                                    Start:
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <input type="radio" name="time"
                                    id="radio" value="startnow"
                                    checked="checked" />
                            </td>
                            <td>
                                <div style="font-size: .9em;">
                                    Now:
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <input type="radio" name="time"
                                    id="radio" value="starttime" />
                            </td>
                            <td>
                                <div style="font-size: .9em;">
                                    Time:
                                </div>
                            </td>
                            <td>
                                <div>
                                    <cti:formatDate
                                        value="${currentTime}"
                                        type="time"
                                        var="formattedCurrentTime" />
                                    <input type="text"
                                        value="${formattedCurrentTime}"
                                        name="starttime" onfocus="javascript:macsscheduledscripts_startFocus();"/>
                                </div>
                            </td>
                            <td>
                                <div>
                                    ${zone}
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>
                                <div>
                                    Date:
                                </div>
                            </td>
                            <td>
                                <cti:formatDate value="${currentTime}"
                                    type="date"
                                    var="formattedCurrentDate" />
                                <input type="text"
                                    value="${formattedCurrentDate}"
                                    name="startdate" onfocus="javascript:macsscheduledscripts_startFocus();"/>
                            </td>
                        </tr>
                    </table>
                </div>
                <div style="padding: 10px;">
                    <table cellspacing="5">
                        <tr>
                            <td>
                                <div
                                    style="font-weight: bold; font-size: 0.9em;">
                                    Stop:
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <input type="radio" name="time2"
                                    id="radio" checked="checked" />
                            </td>
                            <td>
                                <div>
                                    Time:
                                </div>
                            </td>
                            <td>
                                <cti:formatDate value="${stopTime}"
                                    type="time" var="formattedStopTime" />
                                <input type="text"
                                    value="${formattedStopTime}"
                                    name="stoptime" />
                            </td>
                            <td>
                                <div>
                                    ${zone}
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>
                                <div>
                                    Date:
                                </div>
                            </td>
                            <td>
                                <cti:formatDate value="${stopTime}"
                                    type="date" var="formattedStopDate" />
                                <input type="text"
                                    value="${formattedStopDate}"
                                    name="stopdate" />
                            </td>
                        </tr>
                    </table>
                    <div style="padding-top: 25px;">
                        <input type="submit" value="Apply" />
                        <input type="button" value="Back"
                            onclick="javascript:window.history.back();" />
                        <input type="reset" value="Reset" />
                        <input type="hidden" name="id"
                            value="${schedule.id}" />
                    </div>
                </div>
                <input type="hidden" name="sortBy" value="${sortBy}"/>
                <input type="hidden" name="descending" value="${descending}"/>
            </form>
        </div>
    </center>
</cti:standardPage>
