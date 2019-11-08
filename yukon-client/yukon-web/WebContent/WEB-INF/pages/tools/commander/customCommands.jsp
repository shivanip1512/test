<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.tools.commander.customCommands">
    
    <div class="column-6-18">
        <div class="column one">
            <select size="20" class="js-selected-category">
                <c:forEach var="type" items="${categories}">
                    <c:set var="selectedText" value="${type.dbString == formBean.selectedCategory ? 'selected=selected' : ''}"/>
                    <option ${selectedText} value="${type.dbString}">${type.dbString}</option>
                </c:forEach>
                <c:forEach var="paoType" items="${paoTypes}">
                    <c:set var="selectedText" value="${paoType.dbString == formBean.selectedCategory ? 'selected=selected' : ''}"/>
                    <option ${selectedText} value="${paoType.dbString}"><i:inline key="${paoType.formatKey}"/></option>
                </c:forEach>
            </select>
        </div>
        
        <div class="column two nogutter">
        
            <div id="device-commands-table" class="bordered-div oa" style="height:350px">
                <%@ include file="customCommandsTable.jsp" %>
            </div>
                    
            <cti:checkRolesAndProperties value="MANAGE_CUSTOM_COMMANDS" level="CREATE">      
                <div class="action-area">
                    <cti:button nameKey="add" classes="fr js-add-command" icon="icon-add" disabled="${formBean.selectedCategory == null}"/>
                </div>
            </cti:checkRolesAndProperties>
        </div>
        
        <cti:msg2 var="saveChangesTitle" key=".saveChangesTitle"/>
        <div class="dn" id="save-changes-popup" data-dialog data-title="${saveChangesTitle}">
            <i:inline key=".changesNotSaved"/>
        </div>
        
        <cti:msg2 var="deleteConfirmationTitle" key="yukon.web.components.ajaxConfirm.confirmDelete.title"/>
        <div class="dn" id="delete-from-all-popup" data-dialog data-title="${deleteConfirmationTitle}">
            <i:inline key=".deleteFromAllDeviceTypes"/>
        </div>
        
</cti:msgScope>