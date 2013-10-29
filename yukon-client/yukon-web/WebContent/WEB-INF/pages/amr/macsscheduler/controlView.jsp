<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="submitUrl" value="/macsscheduler/schedules" />
<cti:standardPage title="Scheduled Scripts" module="tools">

    <h2>Start Schedule</h2><br />
    <div>
        <form id="startform" method="post" action="${submitUrl}/action">
        <tags:boxContainer title="Schedule Name: ${schedule.scheduleName}">
        	<c:if test="${errorMsg != null}">
                <div class="error">${errorMsg}</div>
            </c:if>
            <h4>Start</h4>
            <table> 
                <tr>
                    <td><input type="radio" name="time" id="radio" value="startnow" checked="checked" /></td>
                    <td>Now</td>
                </tr>
                <tr>
                    <td><input type="radio" name="time" id="radio" value="starttime" /></td>
                    <td>Time</td>
                    <td>
                        <cti:formatDate value="${currentTime}" type="TIME" var="formattedCurrentTime" />
	                    <input type="text" value="${formattedCurrentTime}" name="starttime"
                            onfocus="javascript:macsscheduledscripts_startFocus();"/>
	                    ${zone}
	                </td>
	            </tr>
	            <tr>
                    <td><!-- Empty --></td>
                    <td>Date</td>
                    <td>
                        <cti:formatDate value="${currentTime}" type="DATE" var="formattedCurrentDate" />
                        <input type="text" value="${formattedCurrentDate}" name="startdate"
                            onfocus="javascript:macsscheduledscripts_startFocus();"/>
                    </td>
                </tr>
            </table>
            
            <h4>Stop</h4>
            <table>
                <tr>
                    <td><input type="radio" name="time2" id="radio" checked="checked"/></td>
                    <td>Time</td>
                    <td>
                        <cti:formatDate value="${stopTime}" type="TIME" var="formattedStopTime" />
                        <input type="text" value="${formattedStopTime}" name="stoptime" />
                        ${zone}
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>Date</td>
                    <td>
                        <cti:formatDate value="${stopTime}" type="DATE" var="formattedStopDate" />
                        <input type="text" value="${formattedStopDate}" name="stopdate" />          
                    </td>
                </tr>
            </table>
        </tags:boxContainer>
        
        <input type="submit" name="buttonAction" value="Apply" />
        <input type="submit" name="buttonAction" value="Back" />
        <input type="submit" name="buttonAction" value="Reset" />
        <input type="hidden" name="id" value="${schedule.id}" />
        <input type="hidden" name="sortBy" value="${sortBy}"/>
        <input type="hidden" name="descending" value="${descending}"/>
        
        </form>
    </div>
</cti:standardPage>
