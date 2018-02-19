<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="modules.capcontrol.scheduleAssignments">
    <form id="add-schedule-assignment-form" method="post" action="addPao">
        <cti:csrfToken/>
        <input type="hidden" name="paoIdList" id="paoIdList" />
        <input type="hidden" name="filterCommand" value="${param.command}" />
        <input type="hidden" name="filterSchedule" value="${param.schedule}" />
        <table class="filterSelection">
            <tr>
                <td>Schedules:</td>
                <td>
                    <select name="scheduleId" id="scheduleId">
                        <c:forEach var="schedule" items="${scheduleList}">
                                <option value="${schedule.id}" <c:if test="${param.schedule == schedule.name}">selected="selected" </c:if> >${fn:escapeXml(schedule.name)}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Commands:</td>
                <td>
                    <select name="cmd" id="cmd" style="width: 100%">
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
                </td>
            </tr>
            <tr>
                <td>
                </td>
                <td>
                    <input name="cmdInput" id="cmdInput" type="text" value="${commandList[0].commandName}" size="50"></input>
                </td>
            </tr>
        </table>
        <div id="deviceSelectionDiv">
            <table>
                <tr>
                    <td>Substation Bus:&nbsp;</td>
                    <td>
                        <tags:pickerDialog id="cbcSubBusPicker" type="cbcSubBusPicker"
                                destinationFieldId="paoIdList" multiSelectMode="true"
                                linkType="selection" selectionProperty="paoName"/>
                    </td>
                </tr>
            </table>
        </div>
    </form>
</cti:msgScope>