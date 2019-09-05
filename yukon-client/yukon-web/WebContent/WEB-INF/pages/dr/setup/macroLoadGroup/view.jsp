<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dr" page="setup.macroLoadGroup.${mode}">
    <tags:setFormEditMode mode="${mode}" />
    
    <!-- Actions drop-down -->
    <cti:displayForPageEditModes modes="VIEW">
        <div id="page-actions" class="dn">
            <!-- Create -->
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="CREATE">
                <cti:url var="createUrl" value="/dr/setup/macroLoadGroup/create" />
                <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label"  id="js-create-option" href="${createUrl}"/>
            </cti:checkRolesAndProperties>    
            <!-- Edit -->
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="UPDATE">
                <cti:url var="editUrl" value="/dr/setup/macroLoadGroup/${macroLoadGroup.id}/edit"/>
                <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}" />
            </cti:checkRolesAndProperties>
            <!-- Copy -->
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="CREATE">
            <li class="divider"></li>
                <cm:dropdownOption key="yukon.web.components.button.copy.label" icon="icon-disk-multiple" data-popup="#js-copy-popup"/>
                <li class="divider"></li>
            </cti:checkRolesAndProperties>
            <!-- Delete -->
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="OWNER">
                <cm:dropdownOption icon="icon-cross" key="yukon.web.components.button.delete.label" classes="js-hide-dropdown"
                               id="js-delete-option" data-ok-event="yukon:macro-load-group:delete"/>
            
                <d:confirm on="#js-delete-option" nameKey="confirmDelete" argument="${macroLoadGroup.name}" />
                <cti:url var="deleteUrl" value="/dr/setup/macroLoadGroup/${macroLoadGroup.id}/delete"/>
                <form:form id="js-delete-macro-load-group-form" action="${deleteUrl}" method="delete" modelAttribute="macroLoadGroup">
                    <tags:hidden path="name"/>
                    <cti:csrfToken/>
                </form:form>
            </cti:checkRolesAndProperties>
        </div>
    </cti:displayForPageEditModes>
    
    <!-- Copy dialog -->
    <cti:msg2 var="copyPopUpTitle" key="yukon.web.modules.dr.setup.macroLoadGroup.copy.title"/>
    <cti:url var="renderCopyPopupUrl" value="/dr/setup/macroLoadGroup/${macroLoadGroup.id}/renderCopyPopup"/>
    <cti:msg2 var="copyText" key="components.button.copy.label"/>
    <div class="dn" id="js-copy-popup" data-title="${copyPopUpTitle}" data-dialog data-ok-text="${copyText}" 
         data-event="yukon:macro-load-group:copy" data-url="${renderCopyPopupUrl}"></div>
    
    <cti:url var="action" value="/dr/setup/macroLoadGroup/save" />
    <form:form modelAttribute="macroLoadGroup" action="${action}" method="post" id="js-macro-load-group-form">
        <cti:csrfToken />
        <form:hidden path="id"/>
        <form:hidden path="type"/>
        <input type="hidden" name="selectedLoadGroupIds" id="js-selected-load-group-ids"/>
        <tags:sectionContainer2 nameKey="general">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name">
                    <tags:input path="name" size="25" maxlength="60" autofocus="autofocus"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <tags:sectionContainer2 nameKey="addOrRemoveLoadGroups" styleClass="select-box">
                <div class="column-12-12 clearfix select-box bordered-div">
                    <!-- Available Load Groups -->
                    <div class="column one bordered-div">
                        <h3><i:inline key="yukon.common.available"/></h3>
                        <div id="js-inline-picker-container" style="height:470px;" class="oa"></div>
                        <tags:pickerDialog id="js-avaliable-load-groups-picker" type="lmAvaliableGroupsForMacroGroupPicker" container="js-inline-picker-container"
                                           multiSelectMode="${true}" disabledIds="${selectedLoadGroupIds}"/>
                        <div class="action-area">
                            <cti:button nameKey="add" classes="fr js-add-load-group" icon="icon-add"/>
                        </div>
                    </div>
                
                    <!-- Assigned Load Groups -->
                    <div class="column two nogutter bordered-div">
                        <h3><i:inline key="yukon.common.assigned"/></h3>
                        <div style="height:515px;" class="oa">
                            <div id="js-assigned-load-groups" class="select-box-selected js-with-movables" style="min-height:150px;" data-item-selector=".select-box-item">
                                <c:forEach var="item" items="${macroLoadGroup.assignedLoadGroups}" varStatus="status">
                                    <div class="select-box-item cm js-assigned-load-group" data-id="${item.id}">
                                        <cti:deviceName deviceId="${item.id}"/>
                                    
                                        <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-remove"/>
                                        <div class="select-box-item-movers">
                                            <c:set var="disabled" value="${status.first}"/>
                                            <cti:button icon="icon-bullet-go-up" renderMode="buttonImage"
                                                        classes="left select-box-item-up js-move-up" disabled="${disabled}"/>
                                            <c:set var="disabled" value="${status.last}"/>
                                            <cti:button icon="icon-bullet-go-down" renderMode="buttonImage"
                                                        classes="right select-box-item-down js-move-down" disabled="${disabled}"/>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                            <div class="select-box-item cm js-assigned-load-group js-template-row dn" data-id="0">
                                <span class="js-load-group-name"></span>
                                <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-remove"/>
                                <div class="select-box-item-movers">
                                    <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up"/>
                                    <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down" disabled="${true}"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </tags:sectionContainer2>
        </cti:displayForPageEditModes>
        
        <cti:displayForPageEditModes modes="VIEW">
            <tags:sectionContainer2 nameKey="assignedLoadGroups" styleClass="select-box">
                <table id="js-assigned-load-groups-table" class="compact-results-table dashed">
                    <thead>
                        <tr>
                            <th><i:inline key="yukon.common.name"/></th>
                            <th><i:inline key="yukon.common.type"/></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="assignedLoadGroup" items="${macroLoadGroup.assignedLoadGroups}">
                            <tr>
                                <td>
                                    <cti:url var="viewUrl" value="/dr/setup/loadGroup/${assignedLoadGroup.id}"/>
                                    <a href="${viewUrl}">${fn:escapeXml(assignedLoadGroup.name)}</a>
                                </td>
                                 <td><i:inline key="${assignedLoadGroup.type}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </tags:sectionContainer2>
        </cti:displayForPageEditModes>
        
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" classes="primary action" id="js-save"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:url var="viewUrl" value="/dr/setup/macroLoadGroup/${macroLoadGroup.id}"/>
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:url var="setupUrl" value="/dr/setup/filter?filterByType=MACRO_LOAD_GROUP" />
                <cti:button nameKey="cancel" href="${setupUrl}" />
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.macroloadGroup.js" />
    <cti:includeScript link="JQUERY_SCROLL_TABLE_BODY"/>
</cti:standardPage>