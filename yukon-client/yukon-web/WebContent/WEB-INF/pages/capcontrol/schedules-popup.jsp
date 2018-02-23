<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.capcontrol">

<div class="column-12-12 clearfix select-box">
    <cti:url var="url" value="/capcontrol/buses/${bus.id}/schedules"/>
    <form:form modelAttribute="bus" method="post" action="${url}">
        <cti:csrfToken/>
        <input type="hidden" name="dmvCommandPrefix" value="${dmvCommandPrefix}"/>
        
        <table id="schedules-table" class="full-width stacked">
            <thead>
                <tr>
                    <th></th>
                    <th><i:inline key=".schedules.schedule"/></th>
                    <th><i:inline key=".schedules.command"/></th>
                    <th style="width:15%"><i:inline key=".schedules.ovuv"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="schedule" items="${bus.schedules}" varStatus="status">
                    <c:set var="idx" value="${status.index}" />
                    <c:set var="prefix" value="${dmvCommandPrefix}" />
                    <c:set var="classes" value="${fn:containsIgnoreCase(schedule.command, prefix) ? 'dn' : ''}"/>
                    <tr class="js-schedule-row">
                        <td>
                            <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove-schedule"/>
                        </td>
                        <td>
                            <select name="schedules[${idx}].scheduleID">
                                <c:forEach var="scheduleOption" items="${allSchedules}">
                                    <option value="${scheduleOption.id}"
                                        <c:if test="${schedule.scheduleID == scheduleOption.id}"> selected="selected" </c:if>>
                                        ${fn:escapeXml(scheduleOption.name)}</option>
                                </c:forEach>
                            </select>
                        </td>
                        <td class="js-command">
                            <select name="schedules[${idx}].commandName" class="js-command-options">
                                <option value=""><i:inline key=".schedules.pleaseSelect"/></option>
                                <c:forEach var="commandOption" items="${scheduleCommands}">
                                    <option value="${commandOption.commandName}"
                                        <c:if test="${fn:containsIgnoreCase(schedule.command, commandOption.commandName)}"> selected="selected" </c:if>>
                                        ${fn:escapeXml(commandOption.commandName)}</option>
                                </c:forEach>
                            </select>
        
                             <c:forEach var="dmvTest" items="${dmvTests}">
                                  <c:if test="${fn:containsIgnoreCase(schedule.command, dmvTest.name)}">
                                      <div class="column-6-18 clearfix dmv-test-picker-${idx}">
                                          <div class="column one"><i:inline key=".schedules.dmvTest" /></div>
                                          <div class="column two nogutter">
                                              <tags:pickerDialog type="dmvTestPicker"
                                                 id="dmvTestPicker${idx}" 
                                                 linkType="selection" 
                                                 selectionProperty="dmvTestName" 
                                                 initialId="${dmvTest.dmvTestId}"
                                                 endAction="yukon.da.bus.addDMVPrefixInCommand"/>
                                          </div>
                                      </div>
                                  </c:if>
                          </c:forEach>
                          <tags:input path="schedules[${idx}].command" size="54" inputClass="${classes}"/>
                        </td>
                        <td>
                            <tags:switchButton name="schedules[${idx}].disableOvUvBoolean" checked="${schedule.disableOvUvBoolean}" />
                        </td>
                    </tr>
                </c:forEach>
                <c:set var="idx" value="${fn:length(bus.schedules)}" />
                <tr class="js-schedule-add-row dn">
                    <td>
                        <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove-schedule"/>
                    </td>
                    <td>
                        <select name="schedules[${idx}].scheduleID">
                            <option value="-1"><i:inline key=".schedules.pleaseSelect"/></option>
                            <c:forEach var="scheduleOption" items="${allSchedules}">
                                <option value="${scheduleOption.id}">
                                    ${fn:escapeXml(scheduleOption.name)}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td class="js-command">
                        <select name="schedules[${idx}].commandName" class="js-command-options">
                            <option value="-1"><i:inline key=".schedules.pleaseSelect"/></option>
                             <c:forEach var="commandOption" items="${scheduleCommands}">
                                <option value="${commandOption.commandName}">
                                <c:if test="${fn:containsIgnoreCase(schedule.command, commandOption.commandName)}"> selected="selected" </c:if>
                                    ${fn:escapeXml(commandOption.commandName)}</option>
                            </c:forEach>
                        </select>
                        <tags:input path="schedules[${idx}].command" size="54"/>
                    </td>
                    <td>
                        <tags:switchButton name="schedules[${idx}].disableOvUvBoolean" onClasses="fn"/>
                    </td>
                </tr>
            </tbody>
        </table>
        <cti:button nameKey="add" icon="icon-add" classes="js-add-schedule" />
    
    </form:form>

</div>

</cti:msgScope>
