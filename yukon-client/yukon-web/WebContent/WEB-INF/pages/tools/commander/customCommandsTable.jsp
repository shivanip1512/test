<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.tools.commander.customCommands">

    <table class="compact-results-table row-highlighting">
        <thead>
            <th style="max-width:30px;"></th>
            <th><i:inline key=".commandName"/></th>
            <th><i:inline key=".command"/></th>
            <th><i:inline key=".visible"/></th>
            <th><i:inline key=".category"/></th>
            <th style="width:90px"></th>
        </thead>
        <tbody>
            <c:forEach var="typeCommand" items="${typeCommands}" varStatus="status">
                <c:set var="command" value="${commands[typeCommand.commandId]}"/>
                <c:set var="isEditable" value="${typeCommand.commandId > 0}"/>
                <c:set var="disabledText" value="${isEditable ? '' : 'disabled=disabled'}"/>
                <tr>
                    <td>
                        <c:if test="${isEditable}">
                            <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove ML0 MR0"/>
                        </c:if>
                    </td>
                    <td>
                        <input type="text" value="${command.label}" title="${command.label}" size="20" ${disabledText}/>
                    </td>
                    <td>
                        <input type="text" value="${command.command}" title="${command.command}" size="20" ${disabledText}/>
                    </td>
                    <td>
                        <c:set var="checkedText" value="${typeCommand.isVisible() ? 'checked=checked' : ''}"/>
                        <input type="checkbox" ${checkedText}/>
                    </td>
                    <td>
                        ${command.category}
                    </td>
                    <td>
                        <div class="select-box-item-movers">
                            <c:set var="disabled" value="${status.first}" />
                            <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up" disabled="${disabled}" />
                            <c:set var="disabled" value="${status.last}" />
                            <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down" disabled="${disabled}" />
                        </div>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty typeCommands}">
                <tr>
                    <td colspan="6"><span class="empty-list"><i:inline key=".noResultsFound" /></span></td>
                </tr>
            </c:if>
        </tbody>
    </table>

</cti:msgScope>