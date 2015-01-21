<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="scheduleAssignments">
    <%@include file="/capcontrol/capcontrolHeader.jspf" %>

    <cti:includeScript link="/JavaScript/yukon.da.schedule.assignments.js"/>
    
    <cti:url value="/capcontrol/schedule/startMultiScheduleAssignmentPopup" var="startAssignmentsUrl">
        <cti param name="schedule" value="${param.schedule}" />
        <cti param name="command" value="${param.command}" />
    </cti:url>
    <div id="start-assignments"
        data-dialog
        data-event="yukon.vv.schedules.start.all" 
        data-title="<cti:msg2 key=".play.label"/>" 
        data-url="${startAssignmentsUrl}" 
        data-width="500" 
        class="dn"></div>
    
    <cti:url value="/capcontrol/schedule/stopMultiScheduleAssignmentPopup" var="stopAssignmentsUrl">
        <cti param name="schedule" value="${param.schedule}" />
        <cti param name="command" value="${param.command}" />
    </cti:url>
    <div id="stop-assignments" 
        data-dialog
        data-event="yukon.vv.schedules.stop.all"
        data-title="<cti:msg2 key=".stop.label"/>" 
        data-url="${stopAssignmentsUrl}" 
        data-width="500" class="dn"></div>

    <cti:url value="/capcontrol/schedule/newScheduleAssignmentPopup" var="newAssignmentsUrl">
        <cti param name="schedule" value="${param.schedule}" />
        <cti param name="command" value="${param.command}" />
    </cti:url>
    <div id="add-assignments" 
        data-dialog
        data-event="yukon.vv.schedules.add"
        data-title="<cti:msg2 key=".add.label"/>" 
        data-url="${newAssignmentsUrl}" 
        data-width="500" class="dn"></div>
    
    <div id="page-buttons" class="dn">
         <cti:button nameKey="filter" classes="js-show-filter" icon="icon-filter" data-popup="#filter-popup"/>
    </div>
    <div class="dn js-page-additional-actions">
        <li class="divider"></li>
        <c:choose>
            <c:when test="${hasActionRoles == true}">
                <cm:dropdownOption id="systemStartScheduleAssignments" data-popup="#start-assignments" icon="icon-control-play-blue" key=".play.label"/>
            </c:when>
            <c:otherwise>
                <cm:dropdownOption id="systemStartScheduleAssignments" icon="icon-control-play-blue" key=".playDisabled.label"/>
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${hasActionRoles == true}">
                <cm:dropdownOption id="systemStopScheduleAssignments" data-popup="#stop-assignments" icon="icon-control-stop-blue" key=".stop.label"/>
            </c:when>
            <c:otherwise>
                <cm:dropdownOption id="systemStopScheduleAssignments" icon="icon-control-stop-blue" key=".stopDisabled.label"/>
            </c:otherwise>
        </c:choose>
        
        <c:choose>
            <c:when test="${hasEditingRole == true}">
                <cm:dropdownOption id="systemAddScheduleAssignments" data-popup="#add-assignments" icon="icon-add" key=".add.label"/>
            </c:when>
            <c:otherwise>
                <cm:dropdownOption id="systemAddScheduleAssignments" icon="icon-add" key=".addDisabled.label"/>
            </c:otherwise>
        </c:choose>
    </div>

    <cti:linkTabbedContainer mode="section">
        <cti:linkTab selectorKey="yukon.web.modules.capcontrol.schedules.tab.title">
            <c:url value="/capcontrol/schedule/schedules" />
        </cti:linkTab>
        <cti:linkTab selectorKey="yukon.web.modules.capcontrol.scheduleAssignments.tab.title" initiallySelected="${true}">
            <c:url value="/capcontrol/schedule/scheduleAssignments" />
        </cti:linkTab>
    </cti:linkTabbedContainer>
    
    <cti:url value="/capcontrol/schedule/filter" var="filterURL"/>
    <div id="filter-popup" data-title="<cti:msg2 key=".filterTitle"/>" class="dn">
        <form action="${filterURL}">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".schedules">
                    <select name="schedule" id="scheduleSelection">
                        <option value="All"><cti:msg2 key=".allSchedules"/></option>
                        <c:forEach var="aSchedule" items="${scheduleList}">
                            <option value="${aSchedule.scheduleName}"<c:if test="${param.schedule == aSchedule.scheduleName}"> selected="selected" </c:if>>
                                ${fn:escapeXml(aSchedule.scheduleName)}
                            </option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".commands">
                    <select name="command" id="commandSelection">
                        <option value="All"><cti:msg2 key=".allCommands"/></option>
                        <c:forEach var="aCommand" items="${commandList}">
                            <c:choose>
                                <c:when test="${param.command == 'All'}">
                                    <option value="${aCommand}">${aCommand.commandName}</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${aCommand}"<c:if test="${param.command == aCommand}"> selected="selected"</c:if>>
                                        <spring:escapeBody htmlEscape="true">${aCommand.commandName}</spring:escapeBody>
                                    </option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
            </tags:nameValueContainer2>
            <div class="action-area">
                <cti:button nameKey="filter" id="set-filter" classes="primary action"/>
                <cti:button nameKey="clearFilter" id="clear-filter"/>
            </div>
        </form>
    </div>
    
<%--     <cti:uniqueIdentifier  prefix="addPao" var="addPao"/> --%>
<%--     <cti:uniqueIdentifier var="addPaoSpanId" prefix="addPaoSpan_"/> --%>
    
    <cti:msg2 var="confirmCommand" key=".confirmCommand"/>
    <cti:msg2 var="sendTimeSyncsCommand" key=".sendTimeSyncsCommand"/>
    
    <c:set var="baseUrl" value="/capcontrol/schedule/scheduleAssignments"/>
    <cti:url var="showAllUrl" value="/capcontrol/schedule/scheduleAssignments"/>

    <div id="schedule-assignments-table">
        <%@include file="scheduleassignmentTable.jsp" %>
    </div>
    
</cti:standardPage>