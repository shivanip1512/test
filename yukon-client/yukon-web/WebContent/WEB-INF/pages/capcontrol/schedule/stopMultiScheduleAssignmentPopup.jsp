<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.scheduleAssignments">
<form name="stopMultipleSchedulesForm" id="stopMultipleSchedulesForm" action="/capcontrol/schedule/stopMultiple">
    
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".schedules">
            <select name="stopSchedule" id="stopSchedule">
                <option value="All" <c:if test="${param.schedule == 'All'}">selected="selected" </c:if>><cti:msg2 key=".allSchedules"/></option>
                <c:forEach var="aSchedule" items="${scheduleList}">
                    <option value="${aSchedule.scheduleName}"<c:if test="${param.schedule == aSchedule.scheduleName}"> selected="selected"</c:if>>
                        <spring:escapeBody htmlEscape="true">${aSchedule.scheduleName}</spring:escapeBody>
                    </option>
                </c:forEach>
            </select>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".commands">
            <select name="stopCommand" id="stopCommand">
                <option value="All"<c:if test="${param.command == 'All'}"> selected="selected"</c:if>><cti:msg2 key=".allCommands"/></option>
                <c:forEach var="aCommand" items="${verifyCommandsList}">
                    <c:choose>
                        <c:when test="${param.command == 'All'}">
                            <option value="${aCommand}">${aCommand.commandName}</option>
                        </c:when>
                        <c:otherwise>
                            <option value="${aCommand}"<c:if test="${param.command == aCommand}"> selected="selected"</c:if>><spring:escapeBody htmlEscape="true">${aCommand.commandName}</spring:escapeBody></option>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    
    <div class="action-area">
        <cti:button nameKey="stop" type="submit" id="actionSubmitButton" classes="primary action"/>
        <cti:button nameKey="cancel" onclick="yukon.da.hideContentPopup();"/>
    </div>
    
</form>
</cti:msgScope>