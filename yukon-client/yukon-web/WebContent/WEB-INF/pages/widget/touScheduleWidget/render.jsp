<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:choose>

    <c:when test="${not empty schedules && fn:length(schedules) > 0}">
            
        <table width="100%">
            <tr> 
                <td colspan="2">
                    <select name="scheduleId" id="scheduleId">
                        <c:forEach var="schedule" items="${schedules}">
                            <option value="${schedule.scheduleID}">${schedule.scheduleName}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            
            <tr valign="bottom">
                <td width="45%">    
                    <br>
                    <tags:widgetActionUpdate method="downloadTouSchedule" label="downloadSchedule" 
                                             labelBusy="downloadingSchedule" container="${widgetParameters.widgetId}_results"/>
                </td>
                <td>
                    <div id="${widgetParameters.widgetId}_results"></div>
                </div>
            </td>
            </tr>
        </table>
          
    </c:when>
    
    <c:otherwise>
        No Schedules
    </c:otherwise>
    
</c:choose>

