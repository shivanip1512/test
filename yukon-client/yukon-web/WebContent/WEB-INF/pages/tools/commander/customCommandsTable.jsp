<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.tools.commander.customCommands">

    <c:if test="${not empty successMsg}"><tags:alertBox type="success">${successMsg}</tags:alertBox></c:if>
    <c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>
        
    <cti:url var="action" value="/tools/commander/customCommands"/>
    <form:form id="custom-commands-form" modelAttribute="formBean" action="${action}" method="POST">
        <cti:csrfToken/>
        <tags:hidden id="selectedCategory" path="selectedCategory"/>

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
                    <tr class="select-box-item cm">
                        <tags:hidden path="detail[${status.index}].deviceCommandId"/>
                        <tags:hidden path="detail[${status.index}].commandId"/>
                        <input type="hidden" name="detail[${status.index}].displayOrder" class="js-display-order" value="${typeCommand.displayOrder}"/>
                        <tags:hidden path="detail[${status.index}].category"/>
                        <td class="PL0 PR0">
                            <c:if test="${isEditable}">
                                <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove ML0 MR0"/>
                            </c:if>
                        </td>
                        <td>
                            <input type="text" name="detail[${status.index}].commandName" value="${typeCommand.commandName}" 
                                title="${typeCommand.commandName}" size="20" ${disabledText} class="js-command-fields"/>
                        </td>
                        <td>
                            <input type="text" name="detail[${status.index}].command" value="${typeCommand.command}" 
                                title="${typeCommand.command}" size="20" ${disabledText} class="js-command-fields"/>
                        </td>
                        <td>
                            <tags:checkbox path="detail[${status.index}].visibleFlag" styleClass="js-command-fields"/>
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
                    <tr class="js-empty-commands">
                        <td colspan="6"><span class="empty-list"><i:inline key=".noResultsFound" /></span></td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        
        <table>
            <tr class="select-box-item cm js-template-row dn">
                <input type="hidden" name="detail[0].commandId" disabled="disabled"/>
                <input type="hidden" name="detail[0].deviceCommandId" disabled="disabled"/>
                <input type="hidden" name="detail[0].displayOrder" class="js-display-order" disabled="disabled"/>
                <input type="hidden" name="detail[0].category" value="${formBean.selectedCategory}" disabled="disabled"/>
                <td class="PL0 PR0">
                    <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove ML0 MR0"/>
                </td>
                <td>
                    <input type="text" name="detail[0].commandName" size="20" class="js-command-fields" disabled="disabled"/>
                </td>
                <td>
                    <input type="text" name="detail[0].command" size="20" class="js-command-fields" disabled="disabled"/>
                </td>
                <td>
                    <input type="checkbox" name="detail[0].visibleFlag" checked="checked" styleClass="js-command-fields" disabled="disabled"/>
                </td>
                <td>
                    ${formBean.selectedCategory}
                </td>
                <td>
                    <div class="select-box-item-movers">
                        <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up" />
                        <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down" disabled="true" />
                    </div>
                </td>
            </tr>
        </table>
                
    </form:form>

</cti:msgScope>