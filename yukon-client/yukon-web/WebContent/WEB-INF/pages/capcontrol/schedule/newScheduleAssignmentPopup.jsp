<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="modules.capcontrol.scheduleAssignments">
    <cti:flashScopeMessages/>
    <form id="add-schedule-assignment-form" method="post" action="addPao">
        <cti:csrfToken/>
        <input type="hidden" name="paoIdList" id="paoIdList" />
        <input type="hidden" name="filterCommand" value="${param.command}" />
        <input type="hidden" name="filterSchedule" value="${param.schedule}" />
        <input type="hidden" name="schedule" value="${param.schedule}" />
        <input type="hidden" name="command" value="${param.command}" />
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".schedules">
                <select name="scheduleId" id="scheduleId">
                    <c:forEach var="schedule" items="${scheduleList}">
                            <option value="${schedule.id}" <c:if test="${param.schedule == schedule.name}">selected="selected" </c:if> >${fn:escapeXml(schedule.name)}</option>
                    </c:forEach>
                </select>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".commands">
                <select name="cmd" id="cmd">
                    <c:forEach var="aCommand" items="${commandList}">
                        <c:choose>
                            <c:when test="${param.command == 'All'}">
                                <option value="${aCommand}">${aCommand.commandName}</option>
                            </c:when>
                            <c:otherwise>
                                <option value="${aCommand}" <c:if test="${param.command == aCommand}">selected="selected" </c:if> >${fn:escapeXml(aCommand.commandName)}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
                <br/>
                <input name="cmdInput" id="cmdInput" type="text" value="${commandList[0].commandName}" class="dn" size="54"></input>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".substationBus">
                <tags:pickerDialog id="cbcSubBusPicker" type="cbcSubBusPicker"
                    destinationFieldId="paoIdList" multiSelectMode="true"
                    linkType="selection" selectionProperty="paoName"/>
            </tags:nameValue2>
            <tags:nameValue2 rowId="dmvTestDiv" nameKey=".dmvTest" rowClass="dn">
               <input type="hidden" name="dmvTestCommand" value="${dmvTestCommand}"/>  
                <tags:pickerDialog type="dmvTestPicker"
                   id="dmvTestPicker" 
                   destinationFieldName="dmvTestId"
                   linkType="selection" 
                   selectionProperty="dmvTestName" />
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </form>
</cti:msgScope>