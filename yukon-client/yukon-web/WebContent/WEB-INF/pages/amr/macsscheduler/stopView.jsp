<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<style>
    #stopform { margin:0px; padding:0px; }
</style>

<cti:url var="submitUrl" value="/macsscheduler/schedules" />
<cti:standardPage title="Scheduled Scripts" module="tools">
    
    <cti:includeScript link="/JavaScript/macsscheduledscripts.js" />
    
    <h2 class="stacked">Stop Schedule</h2>
    <div style="width: 400px;">
        <form id="stopform" method="post" action="${submitUrl}/action">
        <tags:boxContainer title="Schedule Name: ${schedule.scheduleName}">
            <h4>Stop</h4>
            <table>
                <tr>
                    <td><input type="radio" name="time" id="radio" value="stopnow" checked="checked" /></td>
                    <td>Now</td>
                </tr>
                <tr>
                    <td><input type="radio" name="time" id="radio" value="stoptime"/></td>
                    <td>Time</td>
                    <td>
                        <cti:formatDate value="${stopTime}" type="TIME" var="formattedStopTime" />
                        <input type="text" value="${formattedStopTime}"
                            name="stoptime" onfocus="javascript:macsscheduledscripts_stopFocus();"/>
                        ${zone}
                    </td>
                </tr>
                <tr>
                    <td><%-- Empty --%></td>
                    <td>Date</td>
                    <td>
                        <cti:formatDate value="${stopTime}" type="DATE" var="formattedStopDate" />
                        <input type="text" value="${formattedStopDate}"
                            name="stopdate" onfocus="javascript:macsscheduledscripts_stopFocus();" />
                    </td>
                </tr>
            </table>
        </tags:boxContainer>
                
        <input type="submit" name="buttonAction" value="Apply" />
        <input type="submit" name="buttonAction" value="Back" />
        <input type="submit" name="buttonAction" value="Reset" />
        <input type="hidden" name="currenttime" value="${currentTime}" />
        <input type="hidden" name="id" value="${schedule.id}" />
        <input type="hidden" name="sortBy" value="${sortBy}"/>
        <input type="hidden" name="descending" value="${descending}"/>
        </form>
    </div>
</cti:standardPage>
