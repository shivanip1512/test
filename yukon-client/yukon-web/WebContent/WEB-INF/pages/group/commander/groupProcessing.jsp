<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="tools" page="deviceGroupsCommander">

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

<script type="text/javascript">
function validateGroupIsSelected(btn, alertText) {
    
    if ($('#groupName').val() === '') {
        alert(alertText);
        return false;
    }
    
    yukon.ui.busy('.js-group-commander');
    $('#groupCommanderForm').submit();
}
</script>

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
    <cti:msg var="selectCommandLabel" key="yukon.common.device.commander.commandSelector.selectCommand"/>
    <div>${selectCommandLabel}:</div>
    <amr:commandSelector selectName="commandSelectValue" fieldName="commandString" commands="${commands}" 
        selectedCommandString="${param.commandString}" selectedSelectValue="${param.commandSelectValue}"/>

    <br><br>
      <c:if test="${!isSmtpConfigured}">
        <cti:msg var="sendEmailAddressLabel" key="yukon.common.email.send" />
        <div>
            ${sendEmailAddressLabel} &nbsp;&nbsp;&nbsp; : <input type="checkbox" name="sendEmail" data-toggle="email-address">
        </div>
        <br>
        
        <%-- EMAIL --%>
        <div class="stacked">
            <div>
                <i:inline key="yukon.common.email.address" /> : <input type="text" name="emailAddress"  size="40" disabled="disabled" value="${email}" data-toggle-group="email-address">
            </div>
           
        </div>
	  </c:if>
    <%-- EXECUTE BUTTON --%>
    <cti:msg var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />
    <cti:url var="waitImgUrl" value="/WebConfig/yukon/Icons/spinner.gif" />
    
    <div class="page-action-area stacked half-width">
        <cti:button nameKey="execute"
            classes="js-group-commander-btn primary action M0"
            onclick="return validateGroupIsSelected(this, '${cti:escapeJavaScript(noGroupSelectedAlertText)}');"/>
    </div>
</form>

</cti:standardPage>