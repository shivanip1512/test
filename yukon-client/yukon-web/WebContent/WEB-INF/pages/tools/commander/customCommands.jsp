<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.tools.commander.customCommands">
    
    <div class="column-6-18">
        <div class="column one">
            <select size="20" class="js-selected-category">
                <c:forEach var="type" items="${categories}">
                    <c:set var="selectedText" value="${type.dbString == selectedCategory ? 'selected=selected' : ''}"/>
                    <option ${selectedText} value="${type.dbString}">${type.dbString}</option>
                </c:forEach>
                <c:forEach var="paoType" items="${paoTypes}">
                    <c:set var="selectedText" value="${paoType == selectedPaoType ? 'selected=selected' : ''}"/>
                    <option ${selectedText} value="${paoType}"><i:inline key="${paoType.formatKey}"/></option>
                </c:forEach>
            </select>
        </div>
        
        <div class="column two nogutter">
        
            <div id="device-commands-table" class="bordered-div oa" style="height:350px">
                <%@ include file="customCommandsTable.jsp" %>
            </div>
            
            <div class="action-area">
                <cti:button nameKey="add" classes="fr js-add-command" icon="icon-add"/>
            </div>
            
        </div>
        
        <cti:msg2 var="saveChangesTitle" key=".saveChangesTitle"/>
        <div class="dn" id="save-changes-popup" data-dialog data-title="${saveChangesTitle}">
            <i:inline key=".changesNotSaved"/>
        </div>
        
</cti:msgScope>