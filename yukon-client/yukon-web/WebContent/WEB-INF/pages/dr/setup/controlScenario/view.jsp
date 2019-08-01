<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage module="dr" page="setup.controlScenario.${mode}">
    <tags:setFormEditMode mode="${mode}" />
    
    <!-- Actions drop-down -->
    <cti:displayForPageEditModes modes="VIEW">
        <div id="page-actions" class="dn">
            <!-- Create -->
            <cti:url var="createUrl" value="/dr/setup/controlScenario/create" />
            <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" id="js-create-option" href="${createUrl}"/>

            <!-- Edit -->
            <cti:url var="editUrl" value="/dr/setup/controlScenario/${controlScenario.id}/edit"/>
            <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}" />
            <li class="divider"></li>

            <!-- Delete -->
            <cm:dropdownOption icon="icon-cross" key="yukon.web.components.button.delete.label" classes="js-hide-dropdown"
                               id="js-delete-option" data-ok-event="yukon:control-scenario:delete"/>
            <d:confirm on="#js-delete-option" nameKey="confirmDelete" argument="${controlScenario.name}" />
            <cti:url var="deleteUrl" value="/dr/setup/controlScenario/${controlScenario.id}/delete"/>
            <form:form id="js-delete-control-scenario-form" action="${deleteUrl}" method="delete" modelAttribute="controlScenario">
                <tags:hidden path="id"/>
                <tags:hidden path="name"/>
                <cti:csrfToken/>
            </form:form> 
        </div>
    </cti:displayForPageEditModes>
    
    <cti:url var="action" value="/dr/setup/controlScenario/save" />
    <form:form action="${action}" method="post" id="js-control-scenario-form" modelAttribute="controlScenario">
        <cti:csrfToken />
        <form:hidden path="id"/>
        <tags:sectionContainer2 nameKey="general">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name">
                    <tags:input path="name" size="25" maxlength="60" autofocus="autofocus"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <tags:sectionContainer2 nameKey="addOrRemovePrograms" styleClass="select-box">
                <div class="column-8-16 clearfix select-box bordered-div">
                    <!-- Available programs -->
                    <div class="column one bordered-div">
                        <h3><i:inline key=".availablePrograms.title"/></h3>
                        <div id="js-inline-picker-container" style="height:470px;" class="oa"></div>
                        <tags:pickerDialog type="availableLoadProgramPicker" id="js-available-programs-picker" 
                                           container="js-inline-picker-container" multiSelectMode="true" 
                                           disabledIds="${selectedProgramIds}" />
                        <div class="action-area">
                            <cti:button nameKey="add" classes="fr js-add-programs" icon="icon-add"/>
                        </div>
                    </div>
                    <!-- Assigned Programs -->
                    <div class="column two nogutter bordered-div oa" id="js-assigned-programs-div" style="height:595px;">
                        <%@include file="assignedPrograms.jsp" %>
                    </div>
                </div>
            </tags:sectionContainer2>
        </cti:displayForPageEditModes>
        
        <cti:displayForPageEditModes modes="VIEW">
            <tags:sectionContainer2 nameKey="assignedPrograms" styleClass="select-box">
                <table id="js-assigned-programs-table" class="compact-results-table dashed">
                    <thead>
                        <tr>
                            <th><i:inline key=".name"/></th>
                            <th><i:inline key=".startOffset"/></th>
                            <th><i:inline key=".stopOffset"/></th>
                            <th><i:inline key=".startGear"/></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="program" items="${controlScenario.allPrograms}">
                            <tr>
                                <td>
                                    <cti:url var="viewProgramUrl" value='/dr/program/detail?programId=${program.programId}'/>
                                    <a href="${viewProgramUrl}">
                                        <cti:deviceName deviceId="${program.programId}"/>
                                    </a>
                                </td>
                                <td><dt:timeOffset value ="${fn:escapeXml(program.startOffsetInMinutes)}" id="js-start-offset-lbl"/></td>
                                <td><dt:timeOffset value ="${fn:escapeXml(program.stopOffsetInMinutes)}" id="js-stop-offset-lbl"/></td>
                                <td>${fn:escapeXml(program.gears[0].name)}</td>
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
                <cti:url var="viewUrl" value="/dr/setup/controlScenario/${controlScenario.id}"/>
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button id="js-cancel-btn" nameKey="cancel" />
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.controlScenario.js" />
    <cti:includeScript link="JQUERY_SCROLL_TABLE_BODY"/>
    <dt:pickerIncludes/>
</cti:standardPage>