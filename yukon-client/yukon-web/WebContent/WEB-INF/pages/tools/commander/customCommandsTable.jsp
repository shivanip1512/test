<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.tools.commander.customCommands">
        
    <cti:url var="action" value="/tools/commander/customCommands" />
    <form:form id="custom-commands-form" modelAttribute="formBean" action="${action}" method="POST">
        <cti:csrfToken/>

        <table id="commands" class="compact-results-table row-highlighting select-box-selected js-with-movables" data-item-selector=".select-box-item">
            <thead>
                <th style="max-width:30px;"></th>
                <th><i:inline key=".commandName"/></th>
                <th><i:inline key=".command"/></th>
                <th><i:inline key=".visible"/></th>
                <th><i:inline key=".category"/></th>
                <th style="width:90px"></th>
            </thead>
            <tbody>
                <c:forEach var="typeCommand" items="${formBean.detail}" varStatus="status">
                
                    <c:set var="isEditable" value="${typeCommand.commandId > 0}"/>
                    <c:set var="disabledText" value="${isEditable ? '' : 'readonly=readonly'}"/>
                    <tr class="select-box-item">
                        <tags:hidden path="detail[${status.index}].deviceCommandId"/>
                        <tags:hidden path="detail[${status.index}].commandId"/>
                        <input type="hidden" name="detail[${status.index}].displayOrder" class="js-display-order" value="${typeCommand.displayOrder}"/>
                        <tags:hidden path="detail[${status.index}].category"/>
                        <td>
                            <c:if test="${isEditable}">
                                <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove ML0 MR0" data-command-id="${typeCommand.commandId}"/>
                            </c:if>
                        </td>
                        <td>
                            <input type="text" name="detail[${status.index}].commandName" value="${typeCommand.commandName}" 
                                title="${typeCommand.commandName}" size="20" ${disabledText}/>
                        </td>
                        <td>
                            <input type="text" name="detail[${status.index}].command" value="${typeCommand.command}" 
                                title="${typeCommand.command}" size="20" ${disabledText}/>
                        </td>
                        <td>
                            <tags:checkbox path="detail[${status.index}].visibleFlag"/>
                        </td>
                        <td>
                            ${typeCommand.category}
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
                <c:if test="${empty formBean.detail}">
                    <tr>
                        <td colspan="6"><span class="empty-list"><i:inline key=".noResultsFound" /></span></td>
                    </tr>
                </c:if>
            </tbody>
        </table>
                
    </form:form>

</cti:msgScope>