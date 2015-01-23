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
    
    <cti:url value="/capcontrol/schedules/startMultiScheduleAssignmentPopup" var="startAssignmentsUrl">
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
    
    <cti:url value="/capcontrol/schedules/stopMultiScheduleAssignmentPopup" var="stopAssignmentsUrl">
        <cti param name="schedule" value="${param.schedule}" />
        <cti param name="command" value="${param.command}" />
    </cti:url>
    <div id="stop-assignments" 
        data-dialog
        data-event="yukon.vv.schedules.stop.all"
        data-title="<cti:msg2 key=".stop.label"/>" 
        data-url="${stopAssignmentsUrl}" 
        data-width="500" class="dn"></div>

    <cti:url value="/capcontrol/schedules/newScheduleAssignmentPopup" var="newAssignmentsUrl">
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
    <cti:linkTabbedContainer mode="section">
        <cti:linkTab selectorKey="yukon.web.modules.capcontrol.schedules.tab.title">
            <c:url value="/capcontrol/schedules" />
        </cti:linkTab>
        <cti:linkTab selectorKey="yukon.web.modules.capcontrol.scheduleAssignments.tab.title" initiallySelected="${true}">
            <c:url value="/capcontrol/schedules/scheduleAssignments" />
        </cti:linkTab>
    </cti:linkTabbedContainer>
    
    <cti:url value="/capcontrol/schedules/filter" var="filterURL"/>
    <div id="filter-popup" data-title="<cti:msg2 key=".filterTitle"/>" class="dn">
        <form action="${filterURL}">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".schedules">
                    <select name="schedule" id="scheduleSelection">
                        <option value="All"><cti:msg2 key=".allSchedules"/></option>
                        <c:forEach var="schedule" items="${scheduleList}">
                            <option value="${schedule.name}"<c:if test="${param.schedule == schedule.name}"> selected="selected" </c:if>>
                                ${fn:escapeXml(schedule.name)}
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
    
    <c:set var="baseUrl" value="/capcontrol/schedules/scheduleAssignments"/>
    <cti:url var="showAllUrl" value="/capcontrol/schedules/scheduleAssignments"/>

    <div id="schedule-assignments-table" class="scroll-xl">
        <%@include file="scheduleassignmentTable.jsp" %>
    </div>
    
    <div class="action-area">
        <c:choose>
            <c:when test="${hasActionRoles}">
                <cti:button id="systemAddScheduleAssignments" data-popup="#add-assignments" icon="icon-add" nameKey="add"/>
                <cti:button id="systemStartScheduleAssignments" data-popup="#start-assignments" icon="icon-control-play-blue" nameKey="play" />
                <cti:button id="systemStopScheduleAssignments" data-popup="#stop-assignments" icon="icon-control-stop-blue" nameKey="stop"/>
            </c:when>
            <c:otherwise>
                <cti:button id="systemAddScheduleAssignments" icon="icon-add" nameKey="addDisabled"/>
                <cti:button id="systemStartScheduleAssignments" icon="icon-control-play-blue" nameKey="playDisabled"/>
                <cti:button id="systemStopScheduleAssignments" icon="icon-control-stop-blue" nameKey="stopDisabled"/>
            </c:otherwise>
        </c:choose>
    </div>
    
</cti:standardPage>