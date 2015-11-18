<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="tools" page="deviceGroupsCommander">

<cti:includeScript link="/resources/js/pages/yukon.tools.group.command.js"/>

<cti:linkTabbedContainer mode="section">
    <cti:linkTab selectorKey="yukon.web.deviceGroups.editor.tab.title">
        <c:url value="/group/editor/home" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.deviceGroups.commander.tab.title" initiallySelected="true">
        <c:url value="/group/commander/groupProcessing" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.common.device.groupMeterRead.home.tab.title">
        <c:url value="/group/groupMeterRead/homeGroup" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.amr.deviceGroupUpload.tab.title">
        <c:url value="/group/updater/upload" />
    </cti:linkTab>
</cti:linkTabbedContainer>


<%-- ERROR MSG --%>
<c:if test="${not empty param.errorMsg}">
    <div class="error stacked">${param.errorMsg}</div>
    <c:set var="errorMsg" value="" scope="request"/>
</c:if>

<a href="<cti:url value="/group/commander/resultList"/>" class="fr">
    <i:inline key="yukon.web.deviceGroups.commander.recentResultsLabel"/>
</a>
<form id="groupCommanderForm" action="<cti:url value="/group/commander/executeGroupCommand" />" method="post">
    <cti:csrfToken/>
    
    <%-- SELECT DEVICE GROUP TREE INPUT --%>
    <div><strong><i:inline key="yukon.web.deviceGroups.commander.groupSelectionLabel"/></strong></div>

    <div class="half-width stacked">
        <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="dataJson" selectGroupName="${param.groupName}" selectedNodePathVar="selectedNodePath"/>
        <jsTree:nodeValueSelectingInlineTree fieldId="groupName" 
                                            fieldName="groupName"
                                            nodeValueName="groupName" 
                                            multiSelect="false"
                                            id="selectGroupTree" 
                                            dataJson="${dataJson}" 
                                            maxHeight="400" 
                                            highlightNodePath="${selectedNodePath}"
                                            includeControlBar="true"/>
    </div>

    <%-- SELECT COMMAND --%>
    <h3><i:inline key="yukon.common.device.commander.commandSelector.selectCommand"/></h3>
    <div id="no-group-chosen" class="empty-list"><i:inline key=".noGroup"/></div>
    <div id="no-commands-available" class="dn empty-list"><i:inline key=".noCommands"/></div>
    <div id="commands-available" class="dn">
        <div class="stacked">
            <amr:commandSelector id="select-group-command"
                selectName="commandSelectValue"
                fieldName="commandString"
                commands="${commands}"
                selectedCommandString="${param.commandString}"
                selectedSelectValue="${param.commandSelectValue}"/>
        </div>

        <c:if test="${!isSmtpConfigured}">
            <%-- EMAIL --%>
            <div class="stacked">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey="yukon.common.email.send">
                        <tags:switchButton name="sendEmail" toggleGroup="email-address" offClasses="M0" offNameKey=".no.label" onNameKey=".yes.label"/>
                        <input type="text" name="emailAddress"  size="40" disabled="disabled" value="${email}" data-toggle-group="email-address">
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
        </c:if>
        <%-- EXECUTE BUTTON --%>
        <div class="page-action-area stacked half-width">
            <cti:msg2 var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />
            <cti:button nameKey="execute"
                type="submit"
                classes="js-group-commander-btn primary action M0"
                busy="true"
                data-no-group-selected-text="${noGroupSelectedAlertText}"/>
        </div>
    </div>
</form>

</cti:standardPage>