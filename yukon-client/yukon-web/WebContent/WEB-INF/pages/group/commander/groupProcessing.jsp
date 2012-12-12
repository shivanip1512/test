<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage title="Group Processing" module="amr">

    <cti:standardMenu menuSelection="devicegroups|commander"/>

       	<cti:breadCrumbs>
            <cti:msg key="yukon.web.components.button.home.label" var="homeLabel"/>
            <cti:crumbLink url="/operator/Operations.jsp" title="${homeLabel}" />
            <cti:msg key="yukon.web.deviceGroups.editor.title" var="deviceGroupsLabel"/>
    	    <cti:crumbLink url="/spring/group/editor/home" title="${deviceGroupsLabel}" />
            <cti:msg key="yukon.web.deviceGroups.commander.title" var="groupCommandProcessingLabel"/>
    	    <cti:crumbLink title="${groupCommandProcessingLabel}"/>
    	</cti:breadCrumbs>
        
        <script type="text/javascript">
        	
            function validateGroupIsSelected(btn, alertText) {
        
                if ($('groupName').value == '') {
                    alert(alertText);
                    return false;
                }
                
                $('submitGroupCommanderButton').disable();
                $('waitImg').show();
                $('groupCommanderForm').submit();
            }
        
        </script>
		
		<h2><i:inline key="yukon.web.deviceGroups.commander.title"/></h2>
		<br>
		
    	<%-- ERROR MSG --%>
        <c:if test="${not empty param.errorMsg}">
        	<div class="errorRed">${param.errorMsg}</div>
        	<c:set var="errorMsg" value="" scope="request"/>
        	<br>
        </c:if>
	
    	<div style="width: 700px;">
        
            <form id="groupCommanderForm" action="<cti:url value="/spring/group/commander/executeGroupCommand" />" method="post">
        
            <%-- SELECT COMMAND --%>
            <cti:msg var="selectCommandLabel" key="yukon.common.device.commander.commandSelector.selectCommand"/>
      		<div class="largeBoldLabel">${selectCommandLabel}:</div>
            <amr:commandSelector selectName="commandSelectValue" fieldName="commandString" commands="${commands}" selectedCommandString="${param.commandString}" selectedSelectValue="${param.commandSelectValue}" includeDummyOption="true" />
            
            <%-- SELECT DEVICE GROUP TREE INPUT --%>
            <br><br>
            <div class="largeBoldLabel"><i:inline key="yukon.web.deviceGroups.commander.groupSelectionLabel"/></div>
            
            <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="dataJson" selectGroupName="${param.groupName}" selectedNodePathVar="selectedNodePath"/>
            <jsTree:nodeValueSelectingInlineTree   fieldId="groupName" 
                                                fieldName="groupName"
                                                nodeValueName="groupName" 
                                                multiSelect="false"
                                                id="selectGroupTree" 
                                                dataJson="${dataJson}" 
                                                width="500"
                                                height="400" 
                                                highlightNodePath="${selectedNodePath}"
                                                includeControlBar="true"/>
                                                
            <%-- EMAIL --%>
            <div class="largeBoldLabel"><i:inline key="yukon.web.deviceGroups.commander.emailLabel"/></div>
            <input type="text" name="emailAddress" value="" size="40">
            <br><br>
                                                
                  
            <%-- EXECUTE BUTTON --%>
            <cti:msg var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />
            <cti:url var="waitImgUrl" value="/WebConfig/yukon/Icons/indicator_arrows.gif" />
            
            <cti:msg2 var="executeButtonLabel" key="yukon.web.deviceGroups.commander.executeButton"/>
            <input type="button" id="submitGroupCommanderButton" value="${executeButtonLabel}" onclick="return validateGroupIsSelected(this, '${cti:escapeJavaScript(noGroupSelectedAlertText)}');">
            <img id="waitImg" src="${waitImgUrl}" style="display:none;">
            
            <br><br>
            <span class="largeBoldLabel"><i:inline key="yukon.web.deviceGroups.commander.recentResultsLabel"/></span> 
            <a href="/spring/group/commander/resultList"><i:inline key="yukon.web.deviceGroups.commander.recentResultsLink"/></a>
    			 
    		</form>
    	</div>
	
</cti:standardPage>