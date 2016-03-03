<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script>

$('.js-add-schedule').click(function(event) {
    var idx = $('#schedules-table tr').length - 1;
    var lastIndex = idx - 1;
    var indexString = "[" + lastIndex + "]";
    var nextIndexString = "[" + idx + "]";
    
    var original = $('.js-schedule-add-row');
    var clone = $('.js-schedule-add-row').clone(true);
    clone.removeClass('js-schedule-add-row dn');
    clone.addClass('js-schedule-row');
    clone.appendTo('#schedules-table tbody');
    
    //change the name of the elements so it's ready for the next add
    original.find('select, input').each(function(index, item){
        var name = item.name;
        var newName = name.replace(indexString, nextIndexString);
        item.name = newName;
    });
    //also need to change the id for the switch button
    var checkbox = original.find('.switch-btn-checkbox')[0];
    var newId = checkbox.id + "-2";
    checkbox.id = newId;
    original.find('.left, .right').each(function(index, item){
        item.htmlFor = newId;
    });
});

$('.js-remove-schedule').click(function(event) {
    $(this).closest(".js-schedule-row").remove();
});

</script>

<cti:msgScope paths="modules.capcontrol">

<div class="column-12-12 clearfix select-box">
<cti:url var="url" value="/capcontrol/buses/${bus.id}/schedules"/>
<form:form modelAttribute="bus" method="post" action="${url}">
<cti:csrfToken/>

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
                <td>
                    <select name="schedules[${idx}].commandName" onchange="this.nextElementSibling.value=this.value">
                        <option value=""><i:inline key=".schedules.pleaseSelect"/></option>
                        <c:forEach var="commandOption" items="${scheduleCommands}">
                            <option value="${commandOption.commandName}"
                                <c:if test="${schedule.command == commandOption.commandName}"> selected="selected" </c:if>>
                                ${fn:escapeXml(commandOption.commandName)}</option>
                        </c:forEach>
                    </select>
                    <tags:input path="schedules[${idx}].command" size="40"/>
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
            <td>
                <select name="schedules[${idx}].commandName" onchange="this.nextElementSibling.value=this.value">
                    <option value="-1"><i:inline key=".schedules.pleaseSelect"/></option>
                     <c:forEach var="commandOption" items="${scheduleCommands}">
                        <option value="${commandOption.commandName}">
                            ${fn:escapeXml(commandOption.commandName)}</option>
                    </c:forEach>
                </select>
                <tags:input path="schedules[${idx}].command" size="40"/>
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
