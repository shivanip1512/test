<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>


<cti:msg key="yukon.web.deviceGroups.editor.pageName" var="pageName"/>
<cti:standardPage title="${pageName}" module="amr">
<cti:standardMenu menuSelection="devicegroups|home"/>

    <cti:breadCrumbs>
    	<cti:msg key="yukon.web.components.button.home.label" var="homeLabel"/>
        <cti:crumbLink url="/operator/Operations.jsp" title="${homeLabel}" />
        <cti:msg key="yukon.web.deviceGroups.editor.title" var="deviceGroupsLabel"/>
        <cti:crumbLink title="${deviceGroupsLabel}" />
    </cti:breadCrumbs>
    
    <cti:msg var="removeGroupAreYouSure" key="yukon.web.deviceGroups.editor.operationsContainer.removeGroup.areYouSure" javaScriptEscape="true"/>
    <cti:msg var="invalidGroupNameError" key="yukon.web.deviceGroups.editor.operationsContainer.invalidGroupNameError" javaScriptEscape="true"/>
    
    <script type="text/javascript">
    
        function showGroupPopup(popupDiv, focusElem){
            
            $(popupDiv).toggle();
            if($(popupDiv).visible() && focusElem != null){
                $(focusElem).value = '';
                $(focusElem).focus();
            }
        }
        
        function removeGroup(formName){
            var confirmRemove = confirm('${removeGroupAreYouSure}');
            if(confirmRemove) {
                
                if(formName != null) {
                    $(formName).submit();
                }
                
                return true;
            }
            
            if(formName != null) {
                return;
            }
            return false;
        }

        // js implementation of CtiUtilities.isValidGroupName(name).
        function isValidGroupName(name) {
            if(name == null || name.strip() == '' || (name.indexOf('/') != -1) || (name.indexOf('\\') != -1)) {
                return false;
            }
            return true;
        }
        
        function checkAndSubmitNewName(nameId, formId, buttonId, waitImgId) {
        
            var newName = $F(nameId);
            if(!isValidGroupName(newName)) {
                alert('${invalidGroupNameError}');
                $(nameId).focus();
            } else {
                $(buttonId).disabled = true;
                $(waitImgId).show();
                $(formId).submit();
            }
        }
        
        function submitMoveGroupForm() {
            $('moveGroupForm').submit();
        }
        
        
        function submitCopyContentsToGroupForm() {
        
            $('copyContentsToGroupForm').submit();
        }
        
        function showDevices() {
        
            $('showDevicesButton').disabled = true;
            $('showDevicesWaitImg').show();
            
            // escape the group name to escape problem characters
            var groupName = '${cti:escapeJavaScript(group.fullName)}';
            var params = {'groupName': groupName};
            new Ajax.Updater('deviceMembers', '/group/editor/getDevicesForGroup', {method: 'post', parameters: params});
            
        }
        
        function removeAllDevices(confirmText) {
        
            var doRemove = confirm(confirmText);
            
            if (doRemove) {
            
                $('removeAllDevicesButton').disabled = true;
                $('removeAllDevicesWaitImg').show();
            
                var groupName = '${cti:escapeJavaScript(group.fullName)}';
                var params = {'groupName': groupName};
                new Ajax.Updater('deviceMembers', '/group/editor/removeAllDevicesFromGroup', {method: 'post', parameters: params});
            }
        }
    
        function confirmRemoveAllDevices(confirmText) {
        
            var doRemove = confirm(confirmText);
            
            if (doRemove) {
                $('removeAllDevicesFromGroupForm').submit();
            }
        }
        
    </script>

    <h2><cti:msg key="yukon.web.deviceGroups.editor.pageName"/></h2>
    <br>

    <c:if test="${not empty param.errorMessage}">
        <div class="error">
            ${fn:escapeXml(param.errorMessage)}
        </div>
        <br>
    </c:if>
    
    <table>
   	<tr style="vertical-align:top;">
   	
   	<td style="padding-right:10px;">
   	<%-- GROUPS HIERARCHY BOX --%>
    <div style="width: 450px;">

        <cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.title" var="groupsTitle"/>
        <tags:boxContainer title="${groupsTitle}" hideEnabled="false">
            
            <c:set var="selectedGroupParam">
                <jsp:attribute name="value">
                    "${fn:escapeXml(groupFullName)}"
                </jsp:attribute>
            </c:set>
                                                
            <jsTree:nodeValueRedirectingInlineTree name="groupName"
                                                hrefBase="/group/editor/home"
                                                otherHrefParameters=""
                                                id="deviceGroupEditorTree"
                                                dataJson="${allGroupsDataJson}"
                                                width="432"
                                                height="600"
                                                highlightNodePath="${extSelectedNodePath}"
                                                includeControlBar="true" />
                                
        </tags:boxContainer>
    </div>
   	</td>
   	
   	<td>
   	<%-- OPERATIONS BOX --%>
    <div style="width: 450px;">
        
        <cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.title" var="operationsTitle"/>
        <cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.topLevelLabel" var="topLevelLabel"/>
        <tags:boxContainer hideEnabled="false">
            
            <jsp:attribute name="title">
                <table cellpadding="0px" cellspacing="0px">
                    <tr>
                        <td valign="top">
                            ${operationsTitle}:&nbsp;&nbsp;
                        </td>
                        <td valign="top">
                            <c:choose>
                                <c:when test="${empty group.name}">
                                    [ ${topLevelLabel} ]
                                </c:when>
                                <c:otherwise>
                                    ${fn:escapeXml(groupFullName)}
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </table>
            </jsp:attribute>
            
            <jsp:body>
                <div style="font-size: .75em;">
                
                    <%--############################## EDIT GROUP ##############################--%>
                    <%--########################################################################--%>
                    <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
                    <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_EDIT">
                    
                    <c:if test="${group.editable}">
                    
                        <h3><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.editGroupLabel"/></h3>
                        <div class="groupEditorContentDetail">
                        
                        <%-- EDIT NAME --%>
                        <c:set var="editGroupNamePopupId" value="editGroupNamePopup" />
                        <cti:msg var="editGroupNameText" key="yukon.web.deviceGroups.editor.operationsContainer.editGroupNameText" />
                        <cti:msg var="newGroupNameText" key="yukon.web.deviceGroups.editor.operationsContainer.newGroupNameText" />
                        <cti:msg var="changeNameButtonText" key="yukon.web.deviceGroups.editor.operationsContainer.newGroupNameSaveText" />
                        
                        <a title="Click to edit group name" onclick="showGroupPopup('${editGroupNamePopupId}', 'newGroupName')" href="javascript:void(0);">
                            ${editGroupNameText}
                        </a>
                        
                        <tags:simplePopup id="${editGroupNamePopupId}" title="${editGroupNameText}" styleClass="groupEditorPopup"  onClose="showGroupPopup('${editGroupNamePopupId}');">
                            
                            <form id="editGroupNameForm" method="post" action="/group/editor/updateGroupName" onsubmit="return changeGroupName();">
                            
                                <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                                
                                <tags:nameValueContainer>
                                    <tags:nameValue name="${newGroupNameText}">
                                        <input id="newGroupName" name="newGroupName" type="text" value="${fn:escapeXml(group.name)}" />
                                    </tags:nameValue>
                                </tags:nameValueContainer>
                                
                                <div class="actionArea">
                                    <input id="editGroupNameSaveButton" type="button" value="${changeNameButtonText}" 
                                            onclick="checkAndSubmitNewName('newGroupName', 'editGroupNameForm', 'editGroupNameSaveButton', 'editGroupNameWaitImg');">
                                    <img id="editGroupNameWaitImg" src="<cti:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">
                                </div>
                            </form>
                            
                        </tags:simplePopup>
                        
                        <%-- REMOVE --%>
                        <form id="removeGroupForm" action="/group/editor/removeGroup" method="post">
                            <input type="hidden" name="removeGroupName" value="${fn:escapeXml(group.fullName)}">
                            <cti:link key="yukon.web.deviceGroups.editor.operationsContainer.removeGroup" href="javascript:removeGroup('removeGroupForm')"/>
                        </form>
                        
                        <%-- MOVE --%>
                        <div>
                        <cti:link id="moveGroupLink" href="javascript:void(0);" key="yukon.web.deviceGroups.editor.operationsContainer.moveGroup"/>
                        
                        <form id="moveGroupForm" action="/group/editor/moveGroup" method="post">
                            
                            <cti:msg var="moveGroupPopupTitle" key="yukon.web.deviceGroups.editor.operationsContainer.moveGroupPopup.title"/>
                            <cti:msg var="moveGroupPopupSubmitButtonText" key="yukon.web.deviceGroups.editor.operationsContainer.moveGroupPopup.submitButtonText"/>
                            <cti:msg var="moveGroupPopupCancelButtonText" key="yukon.web.deviceGroups.editor.operationsContainer.moveGroupPopup.cancelButtonText"/>
                            <cti:msg var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />
                            
                            <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                            
                            <jsTree:nodeValueSelectingPopupTree    fieldId="parentGroupName"
                                                                fieldName="parentGroupName"
                                                                nodeValueName="groupName"
                                                                submitButtonText="${moveGroupPopupSubmitButtonText}"
                                                                cancelButtonText="${moveGroupPopupCancelButtonText}"
                                                                submitCallback="submitMoveGroupForm();"
                                                                
                                                                id="groupsEditorMoveGroupTree"
                                                                triggerElement="moveGroupLink"
                                                                dataJson="${moveGroupDataJson}"
                                                                title="${moveGroupPopupTitle}"
                                                                width="432"
                                                                height="600"
                                                                noSelectionAlertText="${noGroupSelectedAlertText}"
                                                                includeControlBar="true" />
                        </form>
                        </div>
                        </div>
                    </c:if>
                    
                    </cti:checkProperty>
                    </cti:checkRole>
                    
                    
                    <%--############################ MODIFY GROUP ##############################--%>
                    <%--########################################################################--%>
                    <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
                    <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_MODIFY">
                    <h3><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.modifyGroupsLabel" /></h3>
                    <div class="groupEditorContentDetail">
                    
                    <%-- CHANGE COMPOSED GROUPS --%>
                    <c:if test="${isComposedGroup}">
                    
                    	<cti:msg var="editComposedLinkText" key="yukon.web.deviceGroups.editor.operationsContainer.editComposed.linkText"/>
                    	<cti:msg var="editComposedLinkTitle" key="yukon.web.deviceGroups.editor.operationsContainer.editComposed.linkTitle"/>
                    
                        <cti:url var="editComposedGroupUrl" value="/group/composedGroup/build">
                            <cti:param name="groupName" value="${group.fullName}" />
                        </cti:url>
                        <a title="${editComposedLinkTitle}" href="${editComposedGroupUrl}">
                            ${editComposedLinkText}
                        </a>
                        <br>
                    </c:if>
                    
                    <%-- ADD GROUP --%>
                    <c:choose>
                        <c:when test="${group.modifiable}">
                            
                            <c:set var="addSubGroupPopupId" value="addSubGroupPopup" />
                            <cti:msg var="addSubgroupText" key="yukon.web.deviceGroups.editor.operationsContainer.addSubgroupText"/>
                            <cti:msg var="addSubgroupLinkTitle" key="yukon.web.deviceGroups.editor.operationsContainer.addSubgroupLinkTitle" />
                            <cti:msg var="subgroupNameLabel" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupNameLabel" />
                            <cti:msg var="subgroupTypeLabel" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupTypeLabel" />
                            <cti:msg var="subgroupTypeBasicLabel" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupType.basicLabel" />
                            <cti:msg var="subgroupTypeComposedLabel" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupType.composedLabel" />
                            <cti:msg var="subgroupEmptyGroupTitle" key="yukon.web.deviceGroups.editor.operationsContainer.subgroup.emptyGroupTitle" />
                            <cti:msg var="subgroupComposedGroupTitle" key="yukon.web.deviceGroups.editor.operationsContainer.subgroup.composedGroupTitle" />
                            
                            <cti:msg var="subgroupNameSaveText" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupNameSaveText" />
                            
                            <a title="${addSubgroupLinkTitle}" onclick="showGroupPopup('${addSubGroupPopupId}', 'childGroupName')" href="javascript:void(0);">
                                ${addSubgroupText}
                            </a>
                            
                            <tags:simplePopup id="${addSubGroupPopupId}" title="${addSubgroupText}" styleClass="groupEditorPopup"  onClose="showGroupPopup('${addSubGroupPopupId}');">
                            
                                <form id="addSubGroupForm" method="post" action="/group/editor/addChild" onsubmit="return changeGroupName();">
                                	
                                	<input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                                	
                                	<br>
                                	<tags:nameValueContainer>
                                	
                                        <tags:nameValue name="${subgroupNameLabel}" nameColumnWidth="120px">
                                            <input id="childGroupName" name="childGroupName" type="text" />
                                        </tags:nameValue>
                                        
                                        <tags:nameValue name="${subgroupTypeLabel}">
                                        
                                        	<select name="subGroupType">
                                        		<option value="STATIC" title="${subgroupEmptyGroupTitle}">${subgroupTypeBasicLabel}</option>
                                        		<option value="COMPOSED" title="${subgroupComposedGroupTitle}">${subgroupTypeComposedLabel}</option>
                                        	</select>
                                            
                                        </tags:nameValue>
                                        
                                    </tags:nameValueContainer>
                                    
                                    <br>
                                    <div class="actionArea">
                                        <input id="addSubGroupSaveButton" type="button" value="${subgroupNameSaveText}" 
                                               onclick="checkAndSubmitNewName('childGroupName', 'addSubGroupForm', 'addSubGroupSaveButton', 'addSubGroupWaitImg');">
                                    	<img id="addSubGroupWaitImg" src="<cti:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">
                                	</div>
                                </form>
                                
                            </tags:simplePopup>
                            
                        </c:when>
                        <c:otherwise>
                            <span><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.cannotAddSubgroupText"/></span>
                        </c:otherwise>
                    </c:choose>
                    
                    <%-- ADD DEVICES --%>
                    <br>
                    <c:choose>
                        <c:when test="${groupModifiable}">
                            <cti:url var="addByDeviceCollectionUrl" value="/group/editor/showAddDevicesByCollection">
                                <cti:param name="groupName" value="${group.fullName}" />
                            </cti:url>
                            <a title="Click to add multiple devices" href="${addByDeviceCollectionUrl}">
                                <cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.addDevicesText" />
                            </a>
                        </c:when>
                        <c:otherwise>
                            <cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.cannotAddDevicesText" />
                        </c:otherwise>
                    </c:choose>
                    
                    <%-- COPY CONTENTS TO ANOTHER GROUP --%>
                    <div>
                    <cti:msg var="copyContentsLinkText" key="yukon.web.deviceGroups.editor.operationsContainer.copyContents.linkText"/>
                    <cti:msg var="copyContentsLinkTitle" key="yukon.web.deviceGroups.editor.operationsContainer.copyContents.linkTitle"/>
                    <a title="${copyContentsLinkTitle}" href="javascript:void(0);" id="copyContentsToGroupLink">${copyContentsLinkText}</a>
                    
                    <form id="copyContentsToGroupForm" action="/group/editor/copyContentsToGroup">
                        
                        <cti:msg var="copyContentsPopupTitle" key="yukon.web.deviceGroups.editor.operationsContainer.copyContents.popupTree.title"/>
                        <cti:msg var="copyContentsPopupSubmitText" key="yukon.web.deviceGroups.editor.operationsContainer.copyContents.popupTree.submitButtonText"/>
                        <cti:msg var="copyContentsPopupCancelText" key="yukon.web.deviceGroups.editor.operationsContainer.copyContents.popupTree.cancelButtonText"/>
                        <cti:msg var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />
                        
                        <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                        
                        <jsTree:nodeValueSelectingPopupTree    fieldId="copyContentsToGroupName"
                                                            fieldName="copyContentsToGroupName"
                                                            nodeValueName="groupName"
                                                            submitButtonText="${copyContentsPopupSubmitText}"
                                                            cancelButtonText="${copyContentsPopupCancelText}"
                                                            submitCallback="submitCopyContentsToGroupForm();"
                                                            
                                                            id="copyContentsToGroupTree"
                                                            triggerElement="copyContentsToGroupLink"
                                                            dataJson="${copyGroupDataJson}"
                                                            title="${copyContentsPopupTitle}"
                                                            width="432"
                                                            height="600"
                                                            noSelectionAlertText="${noGroupSelectedAlertText}"
                                                            includeControlBar="true" />
                    </form>
                    </div>
                    </div>
                    </cti:checkProperty>
                    </cti:checkRole>
                    
                    <%--############################ GENERATE REPORTS ##########################--%>
                    <%--########################################################################--%>
                    <h3><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.generateReportsLabel"/></h3>
                    <div class="groupEditorContentDetail">
                    
                    <cti:url value="/amr/reports/groupDevicesReport" var="htmlUrl">
                        <cti:param name="groupName" value="${group.fullName}"/>
                    </cti:url>
                    <c:choose>
                        <c:when test="${childDeviceCount > 0}">
                            <a href="${htmlUrl}"><cti:msg key="yukon.web.modules.amr.fileFormatHtml"/></a>
                            |
                            <cti:simpleReportLinkFromNameTag definitionName="deviceGroupDefinition" viewType="csvView" deviceGroup="${group}"><cti:msg key="yukon.web.modules.amr.fileFormatCsv"/></cti:simpleReportLinkFromNameTag>
                            |
                            <cti:simpleReportLinkFromNameTag definitionName="deviceGroupDefinition" viewType="pdfView" deviceGroup="${group}"><cti:msg key="yukon.web.modules.amr.fileFormatPdf"/></cti:simpleReportLinkFromNameTag>
                       
                        </c:when>
                        <c:otherwise>
                            <cti:msg key="yukon.web.deviceGroups.editor.membersContainer.noDevices"/>
                        </c:otherwise>
                    </c:choose>
                    </div>
                    
                    
                    <%--########################## COLLECTION ACTIONS ##########################--%>
                    <%--########################################################################--%>
                    <h3><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.collectionActionLabel"/></h3>
                    <div class="groupEditorContentDetail">
                    
                    <c:choose>
                        <c:when test="${deviceCount > 0}">
                            
                            <cti:checkProperty property="operator.DeviceActionsRole.GROUP_COMMANDER">
                            <cti:link href="/group/commander/collectionProcessing" key="yukon.web.deviceGroups.editor.operationsContainer.sendCommand">
                                <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                            </cti:link>
                            <br>
                            </cti:checkProperty>
                            
                            <cti:checkProperty property="operator.DeviceActionsRole.MASS_CHANGE">
                            <cti:link href="/bulk/massChange/massChangeSelect" key="yukon.web.deviceGroups.editor.operationsContainer.massChange">
                                <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                            </cti:link>
                            <br>
                            </cti:checkProperty>
                            
                            <cti:link href="/bulk/collectionActions" key="yukon.web.deviceGroups.editor.operationsContainer.otherActions">
                                <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                            </cti:link>
                            
                        </c:when>
                        <c:otherwise>
                            No devices
                        </c:otherwise>
                    </c:choose>
                    </div>
                </div>
            </jsp:body>
        </tags:boxContainer>
        
    </div>
   	
   	<%-- MEMBERS BOX --%>
    <br>
    <div style="width: 450px">
    
        <tags:boxContainer hideEnabled="false">
        
            <jsp:attribute name="title">
                <table>
                    <tr>
                        <td valign="top">
                            <cti:msg key="yukon.web.deviceGroups.editor.membersContainer.membersLabel"/>
                        </td>
                        <td valign="top">
                            ${fn:escapeXml((empty group.name)? '[ Top Level ]' : groupFullName)}
                        </td>
                    </tr>
                </table>
            </jsp:attribute>
        
            <jsp:body>
            
                <div style="overflow: auto; height: 330px;">
    
                    <%--############################### SUBGROUPS ##############################--%>
                    <%--########################################################################--%>
                   	<h4><cti:msg key="yukon.web.deviceGroups.editor.memberContainer.subGroups"/></h4>
                   	<div class="groupEditorContentDetail">
                    <table style="width: 95%; padding-bottom: 10px;" >
                        <c:choose>
                            <c:when test="${fn:length(subGroupMap) > 0}">
                            
                                <%-- User must have DEVICE_GROUP_EDIT to delete groups. Set once for use in loop. --%>
                                <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
                                <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_EDIT">
                                    <c:set var="hasEditRoleProperty" value="true"/>
                                </cti:checkProperty>
                                </cti:checkRole>
                            
                                <c:forEach var="subGroup" items="${subGroupMap}">
                                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                        <td style="border: none;">
                                            <cti:url var="homeUrl" value="/group/editor/home">
                                                <cti:param name="groupName" value="${subGroup.value}" />
                                            </cti:url>
                                        
                                            <span title="${fn:escapeXml(subGroup.value)}">
                                                <a href="${homeUrl}"><c:out value="${subGroup.value}"/></a>
                                            </span>
                                        </td>
                                        
                                        <c:if test="${hasEditRoleProperty}">
                                            <td style="border: none; width: 15px;text-align: center;">
                                                <c:choose>
                                                
                                                    <%-- the group being removed must itself be modifiable --%>
                                                    <c:when test="${groupModifiable}">
                                            
                                                        <cti:uniqueIdentifier prefix="subGroup_" var="subId"/>
                                                        <form style="display: inline;" id="${subId}removeSubGroupForm" action="/group/editor/removeGroup" method="post">
                                                            <input type="hidden" name="removeGroupName" value="${fn:escapeXml(subGroup.key.fullName)}">
                                                            <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                                                            <div class="dib">
                                                                <a href="#" class="icon icon_remove" onclick="this.event.stop(); removeGroup('${subId}removeSubGroupForm');">remove</a>
                                                            </div>
                                                        </form>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <cti:msg var="cannotDeleteGroupLinkTitle" key="yukon.web.deviceGroups.editor.membersContainer.cannotDeleteGroupLinkTitle"/>
                                                        <img class="graycssicon" title="${cannotDeleteGroupLinkTitle}" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>" >
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </c:if>
                                        
                                    </tr>
                                </c:forEach>
                            </c:when> 
                            <c:otherwise>
                                    <tr>
                                        <td>
                                            <cti:msg key="yukon.web.deviceGroups.editor.membersContainer.noChildGroups"/>
                                        </td>
                                    </tr>
                            </c:otherwise>
                        </c:choose>
                    </table>
                    </div>
                
                    <%--################################ DEVICES ###############################--%>
                    <%--########################################################################--%>
                    <h4><cti:msg key="yukon.web.deviceGroups.editor.memberContainer.groupMembers"/></h4>
                   	<div class="groupEditorContentDetail">
                   	
                    <div id="deviceMembers">
                        <c:choose>
                            <c:when test="${not showImmediately && (showDevices == false )}">
                                <table style="width: 95%;" >
                                    <tr>
                                        <td>
                                            <cti:msg key="yukon.web.deviceGroups.editor.membersContainer.groupContainsCountLabel" arguments="${childDeviceCount}"/>
                                            <c:if test="${childDeviceCount > 0}">
                                                <cti:msg var="showDevicesLimitText" 
                                                         key="yukon.web.deviceGroups.editor.membersContainer.showDevicesLimitText" 
                                                         argument="${maxGetDevicesSize}"/>
                                                         
                                                <cti:msg var="showDeviceslabel" key="yukon.web.deviceGroups.editor.membersContainer.showDeviceslabel"/>         
                                                <input id="showDevicesButton" type="button" onclick="showDevices()" value="${showDeviceslabel}" title="${showDevicesLimitText}">
                                                <img id="showDevicesWaitImg" src="<cti:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">       
                                            </c:if>
                                        </td>
                                    </tr>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <jsp:include page="deviceMembers.jsp">
                                    <jsp:param name="groupName" value="${group.fullName}"/>
                                </jsp:include>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    </div>
                    
                </div>
            </jsp:body>
        </tags:boxContainer>
        
    </div>
   	
   	</td>
   	</tr>
    </table>

</cti:standardPage>