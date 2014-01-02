<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
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
        
                if (jQuery('#groupName').val() === '') {
                    alert(alertText);
                    return false;
                }
                
                jQuery('#submitGroupCommanderButton').prop('disabled', true);
                jQuery('#waitImg').show();
                jQuery('#groupCommanderForm').submit();
            }

        </script>

    	<%-- ERROR MSG --%>
        <c:if test="${not empty param.errorMsg}">
            <br>
        	<div class="error">${param.errorMsg}</div>
        	<c:set var="errorMsg" value="" scope="request"/>
        	<br>
        </c:if>
	
    	<div style="width: 700px;">
        
            <form id="groupCommanderForm" action="<cti:url value="/group/commander/executeGroupCommand" />" method="post">

            <%-- SELECT DEVICE GROUP TREE INPUT --%>
            <div class="largeBoldLabel"><i:inline key="yukon.web.deviceGroups.commander.groupSelectionLabel"/></div>

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

            <br><br>

            <%-- SELECT COMMAND --%>
            <cti:msg var="selectCommandLabel" key="yukon.common.device.commander.commandSelector.selectCommand"/>
            <div class="largeBoldLabel">${selectCommandLabel}:</div>
            <amr:commandSelector selectName="commandSelectValue" fieldName="commandString" commands="${commands}" selectedCommandString="${param.commandString}" selectedSelectValue="${param.commandSelectValue}" includeDummyOption="true" />

            <br><br>
            <%-- EMAIL --%>
            <div class="largeBoldLabel"><i:inline key="yukon.web.deviceGroups.commander.emailLabel"/></div>
            <input type="text" name="emailAddress" value="" size="40">
            <br><br>


            <%-- EXECUTE BUTTON --%>
            <cti:msg var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />
            <cti:url var="waitImgUrl" value="/WebConfig/yukon/Icons/spinner.gif" />
            
            <cti:msg2 var="executeButtonLabel" key="yukon.web.deviceGroups.commander.executeButton"/>
            <input type="button" id="submitGroupCommanderButton" value="${executeButtonLabel}" onclick="return validateGroupIsSelected(this, '${cti:escapeJavaScript(noGroupSelectedAlertText)}');">
            <img id="waitImg" src="${waitImgUrl}" style="display:none;">
            
            <br><br>
            <span class="largeBoldLabel"><i:inline key="yukon.web.deviceGroups.commander.recentResultsLabel"/></span> 
            <a href="/group/commander/resultList"><i:inline key="yukon.web.deviceGroups.commander.recentResultsLink"/></a>
    			 
    		</form>
    	</div>
	
</cti:standardPage>