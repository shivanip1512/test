<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" title="Consumer Energy Services">
<cti:standardMenu/>

<table class="contentTable">
    <tr>
        <td class="leftColumn">
            <h3>
                <cti:msg key="yukon.dr.consumer.savedSchedules.header" /><br>
            </h3>
            
            <div class="message">
                <form method="get" action="/spring/stars/consumer/thermostat/schedule/view">
                    
                    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
                    
                    <cti:msg key="yukon.dr.consumer.savedSchedules.chooseSchedule" />
                    <select name="scheduleId">
                        <c:forEach var="schedule" items="${schedules}">
                            <option value="${schedule.id}"><spring:escapeBody htmlEscape="true">${schedule.name}</spring:escapeBody></option>
                        </c:forEach>
                    </select>
                    
                    <br><br>
                    
                    <cti:msg var="viewText" key="yukon.dr.consumer.savedSchedules.viewSchedule" />
                    <input type="submit" value="${viewText}">
                </form>
            </div>
            

        </td>
        <td class="rightColumn">
            <div id="rightDiv">
                <cti:customerAccountInfoTag accountNumber="${customerAccount.accountNumber}"/>
            </div>
        </td>
    </tr>
</table>

</cti:standardPage>