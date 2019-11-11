<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.tools.commander.customCommands">

    <c:if test="${not empty successMsg}"><tags:alertBox type="success" includeCloseButton="true">${successMsg}</tags:alertBox></c:if>
    <c:if test="${not empty errorMsg}"><tags:alertBox includeCloseButton="true">${errorMsg}</tags:alertBox></c:if>
    
    <cti:checkRolesAndProperties value="MANAGE_CUSTOM_COMMANDS" level="UPDATE">
        <c:set var="hasEditPermissions" value="${true}"/>
        <input type="hidden" id="hasEditPermissions" value="true"/>
    </cti:checkRolesAndProperties>
    
        
    <cti:url var="action" value="/tools/commander/customCommands"/>
    <form:form id="custom-commands-form" modelAttribute="formBean" action="${action}" method="POST">
        <cti:csrfToken/>
        <tags:hidden id="selectedCategory" path="selectedCategory"/>
        <input type="hidden" id="isCategory" value="${isCategory}"/>
        <cti:msg2 var="notAvailableForCategory" key=".notEnabledForCategory"/>
        <c:set var="titleText" value="${isCategory ? notAvailableForCategory : ''}"/>
        <c:set var="disabledForCategory" value="${isCategory ? 'readonly=readonly' : ''}"/>
        <c:set var="cursorClass" value="${!isCategory && hasEditPermissions ? 'cm' : ''}"/>

        <table id="commands" class="compact-results-table row-highlighting select-box-selected js-with-movables" data-item-selector=".select-box-item">
            <thead>
                <cti:checkRolesAndProperties value="MANAGE_CUSTOM_COMMANDS" level="OWNER">      
                    <th style="max-width:30px;"></th>
                </cti:checkRolesAndProperties>
                <th><i:inline key=".commandName"/></th>
                <th><i:inline key=".command"/></th>
                <th><i:inline key=".visible"/></th>
                <th><i:inline key=".category"/></th>
                <cti:checkRolesAndProperties value="MANAGE_CUSTOM_COMMANDS" level="UPDATE">      
                    <th style="width:90px"></th>
                </cti:checkRolesAndProperties>
            </thead>
            <tbody>
                <c:forEach var="typeCommand" items="${formBean.detail}" varStatus="status">
                
                    <c:set var="isEditable" value="${typeCommand.commandId > 0 || empty typeCommand.commandId}"/>
                    <c:set var="matchesSelectedCategory" value="${typeCommand.category == formBean.selectedCategory}"/>
                    <tr class="select-box-item ${cursorClass}" data-command-id=${typeCommand.commandId}>
                        <tags:hidden path="detail[${status.index}].deviceCommandId"/>
                        <tags:hidden path="detail[${status.index}].commandId"/>
                        <input type="hidden" name="detail[${status.index}].displayOrder" class="js-display-order" value="${typeCommand.displayOrder}"/>
                        <tags:hidden path="detail[${status.index}].category"/>
                        <cti:checkRolesAndProperties value="MANAGE_CUSTOM_COMMANDS" level="OWNER">      
                            <td class="PL0 PR0">
                                <c:if test="${isEditable}">
                                    <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove ML0 MR0"/>
                                </c:if>
                            </td>
                        </cti:checkRolesAndProperties>
                        <c:choose>
                            <c:when test="${hasEditPermissions}">
                                <td>
                                    <span title="${typeCommand.commandName}">
                                        <tags:input path="detail[${status.index}].commandName" size="20" readonly="${!isEditable || !matchesSelectedCategory}" inputClass="js-command-fields"/>
                                    </span>
                                </td>
                                <td>
                                    <span title="${typeCommand.command}">
                                        <tags:input path="detail[${status.index}].command" size="20" readonly="${!isEditable || !matchesSelectedCategory}" inputClass="js-command-fields"/>
                                    </span>
                                </td>
                                <td>
                                    <span title="${titleText}">
                                        <tags:checkbox path="detail[${status.index}].visibleFlag" styleClass="js-command-fields" disabled="${isCategory}"/>
                                        <c:if test="${isCategory}">
                                            <tags:hidden path="detail[${status.index}].visibleFlag"/>
                                        </c:if>
                                    </span>
                                </td>
                            </c:when>
                            <c:otherwise>
                                <td style="width:35%;">
                                    ${typeCommand.commandName}
                                </td>
                                <td style="width:35%;">
                                    ${typeCommand.command}
                                </td>
                                <td>
                                    <tags:checkbox path="detail[${status.index}].visibleFlag" disabled="true"/>
                                </td>
                            </c:otherwise>
                        </c:choose>
                        <td>
                            ${typeCommand.category}
                        </td>
                        <cti:checkRolesAndProperties value="MANAGE_CUSTOM_COMMANDS" level="UPDATE">      
                            <td>
                                <div class="select-box-item-movers">
                                    <c:set var="disabled" value="${status.first || isCategory}" />
                                    <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up" disabled="${disabled}" title="${titleText}" />
                                    <c:set var="disabled" value="${status.last || isCategory}" />
                                    <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down" disabled="${disabled}" title="${titleText}"/>
                                </div>
                            </td>
                        </cti:checkRolesAndProperties>
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
            <tr class="select-box-item js-template-row dn ${cursorClass}">
                <input type="hidden" name="detail[0].commandId" disabled="disabled"/>
                <input type="hidden" name="detail[0].deviceCommandId" disabled="disabled"/>
                <input type="hidden" name="detail[0].displayOrder" class="js-display-order" disabled="disabled"/>
                <input type="hidden" name="detail[0].category" value="${formBean.selectedCategory}" disabled="disabled"/>
                <cti:checkRolesAndProperties value="MANAGE_CUSTOM_COMMANDS" level="OWNER">      
                    <td class="PL0 PR0">
                        <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove ML0 MR0"/>
                    </td>
                </cti:checkRolesAndProperties>
                <td>
                    <input type="text" name="detail[0].commandName" size="20" class="js-command-fields" disabled="disabled"/>
                </td>
                <td>
                    <input type="text" name="detail[0].command" size="20" class="js-command-fields" disabled="disabled"/>
                </td>
                <td>
                    <input type="checkbox" name="detail[0].visibleFlag" checked="checked" styleClass="js-command-fields" disabled="disabled" title="${titleText}"/>
                    <c:if test="${isCategory}">
                        <input type="hidden" name="detail[0].visibleFlag" value="true" disabled="disabled"/>
                    </c:if>
                </td>
                <td>
                    ${formBean.selectedCategory}
                </td>
                <td>
                    <div class="select-box-item-movers">
                        <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up" title="${titleText}"/>
                        <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down" disabled="true" title="${titleText}"/>
                    </div>
                </td>
            </tr>
        </table>
                
    </form:form>

</cti:msgScope>