<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>


<cti:msg2 key="yukon.web.deviceGroups.editor.pageName" var="pageName"/>
<cti:standardPage title="${pageName}" module="amr">
<cti:standardMenu menuSelection="devicegroups|home"/>

    <cti:breadCrumbs>
    	<cti:msg2 key="yukon.web.components.button.home.label" var="homeLabel"/>
        <cti:crumbLink url="/operator/Operations.jsp" title="${homeLabel}" />
        <cti:msg2 key="yukon.web.deviceGroups.editor.title" var="deviceGroupsLabel"/>
        <cti:crumbLink title="${deviceGroupsLabel}" />
    </cti:breadCrumbs>
    
    <cti:msg2 var="removeGroupAreYouSure" key="yukon.web.deviceGroups.editor.operationsContainer.removeGroup.areYouSure" javaScriptEscape="true"/>
    <cti:msg2 var="invalidGroupNameError" key="yukon.web.deviceGroups.editor.operationsContainer.invalidGroupNameError" javaScriptEscape="true"/>
    
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

        // js implementation of DeviceGroupUtil.isValidName(name).
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

    <h2><cti:msg2 key="yukon.web.deviceGroups.editor.pageName"/></h2>
    
    <c:if test="${not empty param.errorMessage}">
        <div class="error">${fn:escapeXml(param.errorMessage)}</div>
    </c:if>
    
    <table>
   	<tr style="vertical-align:top;">
   	
   	<td style="padding-right:10px;">
   	<%-- GROUPS HIERARCHY BOX --%>
    <div style="width: 450px;">

        <cti:msg2 key="yukon.web.deviceGroups.editor.groupsContainer.title" var="groupsTitle"/>
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
                                                height="400"
                                                highlightNodePath="${extSelectedNodePath}"
                                                includeControlBar="true" />
                                
        </tags:boxContainer>
    </div>
   	</td>
   	
   	<td>
   	<%-- OPERATIONS BOX --%>
    <div style="width: 450px;">
        
        <cti:msg2 key="yukon.web.deviceGroups.editor.operationsContainer.title" var="operationsTitle"/>
        <cti:msg2 key="yukon.web.deviceGroups.editor.operationsContainer.topLevelLabel" var="topLevelLabel"/>
        <tags:boxContainer hideEnabled="false">
            
            <jsp:attribute name="title">
                <table cellpadding="0px" cellspacing="0px">
                    <tr>
                        <td>${operationsTitle}:&nbsp;</td>
                        <td>
                            <c:choose>
                                <c:when test="${empty group.name}">[ ${topLevelLabel} ]</c:when>
                                <c:otherwise>${fn:escapeXml(groupFullName)}</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </table>
            </jsp:attribute>
            
            <jsp:body>
                <div style="font-size: .75em;">
                
                    <%-- EDIT GROUP --%>
                    <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
                    <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_EDIT">
                    
                        <c:if test="${group.editable}">
                            <h3><cti:msg2 key="yukon.web.deviceGroups.editor.operationsContainer.editGroupLabel"/></h3>
                            <div class="groupEditorContentDetail stacked">
                                
                                <%-- EDIT NAME --%>
                                <cti:msg2 var="editGroupNameText" key="yukon.web.deviceGroups.editor.operationsContainer.editGroupNameText" />
                                <cti:msg2 var="newGroupNameText" key="yukon.web.deviceGroups.editor.operationsContainer.newGroupNameText" />
                                <cti:msg2 var="changeNameButtonText" key="yukon.web.deviceGroups.editor.operationsContainer.newGroupNameSaveText" />
                                
                                <a title="Click to edit group name"
                                    onclick="showGroupPopup('editGroupNamePopup', 'newGroupName')" 
                                    href="javascript:void(0);">${editGroupNameText}</a>
                                
                                <tags:simplePopup id="editGroupNamePopup" title="${editGroupNameText}" styleClass="groupEditorPopup"  onClose="showGroupPopup('editGroupNamePopup');">
                                    
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
                                
                                &nbsp;|&nbsp;
                                <%-- REMOVE --%>
                                <form id="removeGroupForm" action="/group/editor/removeGroup" method="post" class="di">
                                    <input type="hidden" name="removeGroupName" value="${fn:escapeXml(group.fullName)}">
                                    <cti:link key="yukon.web.deviceGroups.editor.operationsContainer.removeGroup" href="javascript:removeGroup('removeGroupForm')"/>
                                </form>
                                
                                &nbsp;|&nbsp;
                                <%-- MOVE --%>
                                <cti:link id="moveGroupLink" href="javascript:void(0);" key="yukon.web.deviceGroups.editor.operationsContainer.moveGroup"/>
                                    
                                <form id="moveGroupForm" action="/group/editor/moveGroup" method="post">
                                    
                                    <cti:msg2 var="moveGroupPopupTitle" key="yukon.web.deviceGroups.editor.operationsContainer.moveGroupPopup.title"/>
                                    <cti:msg2 var="moveGroupPopupSubmitButtonText" key="yukon.web.deviceGroups.editor.operationsContainer.moveGroupPopup.submitButtonText"/>
                                    <cti:msg2 var="moveGroupPopupCancelButtonText" key="yukon.web.deviceGroups.editor.operationsContainer.moveGroupPopup.cancelButtonText"/>
                                    <cti:msg2 var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />
                                    
                                    <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                                    
                                    <jsTree:nodeValueSelectingPopupTree fieldId="parentGroupName"
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
                                                                        height="400"
                                                                        noSelectionAlertText="${noGroupSelectedAlertText}"
                                                                        includeControlBar="true" />
                                    </form>
                            </div>
                        </c:if>
                    
                    </cti:checkProperty>
                    </cti:checkRole>
                    
                    <%-- MODIFY GROUP --%>
                    <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
                    <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_MODIFY">
                        <h3><cti:msg2 key="yukon.web.deviceGroups.editor.operationsContainer.modifyGroupsLabel" /></h3>
                        <div class="groupEditorContentDetail stacked">
                        
                            <%-- CHANGE COMPOSED GROUPS --%>
                            <c:if test="${isComposedGroup}">
                            
                            	<cti:msg2 var="editComposedLinkText" key="yukon.web.deviceGroups.editor.operationsContainer.editComposed.linkText"/>
                            	<cti:msg2 var="editComposedLinkTitle" key="yukon.web.deviceGroups.editor.operationsContainer.editComposed.linkTitle"/>
                            
                                <cti:url var="editComposedGroupUrl" value="/group/composedGroup/build">
                                    <cti:param name="groupName" value="${group.fullName}" />
                                </cti:url>
                                <a title="${editComposedLinkTitle}" href="${editComposedGroupUrl}">${editComposedLinkText}</a>
                                
                            </c:if>
                            
                            <%-- ADD GROUP --%>
                            <cti:msg2 var="addSubgroupText" key="yukon.web.deviceGroups.editor.operationsContainer.addSubgroupText"/>
                            <c:choose>
                                <c:when test="${group.modifiable}">
                                    
                                    <c:set var="addSubGroupPopupId" value="addSubGroupPopup" />
                                    <cti:msg2 var="addSubgroupLinkTitle" key="yukon.web.deviceGroups.editor.operationsContainer.addSubgroupLinkTitle" />
                                    <cti:msg2 var="subgroupNameLabel" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupNameLabel" />
                                    <cti:msg2 var="subgroupTypeLabel" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupTypeLabel" />
                                    <cti:msg2 var="subgroupTypeBasicLabel" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupType.basicLabel" />
                                    <cti:msg2 var="subgroupTypeComposedLabel" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupType.composedLabel" />
                                    <cti:msg2 var="subgroupEmptyGroupTitle" key="yukon.web.deviceGroups.editor.operationsContainer.subgroup.emptyGroupTitle" />
                                    <cti:msg2 var="subgroupComposedGroupTitle" key="yukon.web.deviceGroups.editor.operationsContainer.subgroup.composedGroupTitle" />
                                    
                                    <cti:msg2 var="subgroupNameSaveText" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupNameSaveText" />
                                    
                                    <a title="${addSubgroupLinkTitle}" onclick="showGroupPopup('${addSubGroupPopupId}', 'childGroupName')" href="javascript:void(0);">${addSubgroupText}</a>
                                    
                                    <tags:simplePopup id="${addSubGroupPopupId}" title="${addSubgroupText}" styleClass="groupEditorPopup"  onClose="showGroupPopup('${addSubGroupPopupId}');">
                                    
                                        <form id="addSubGroupForm" method="post" action="/group/editor/addChild" onsubmit="return changeGroupName();">
                                        	
                                        	<input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
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
                                            
                                            
                                            <div class="actionArea">
                                                <input id="addSubGroupSaveButton" type="button" value="${subgroupNameSaveText}" 
                                                       onclick="checkAndSubmitNewName('childGroupName', 'addSubGroupForm', 'addSubGroupSaveButton', 'addSubGroupWaitImg');">
                                            	<img id="addSubGroupWaitImg" src="<cti:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">
                                        	</div>
                                        </form>
                                        
                                    </tags:simplePopup>
                                    
                                </c:when>
                                <c:otherwise>
                                    <cti:msg2 var="cannotAddSubgroupText" key="yukon.web.deviceGroups.editor.operationsContainer.cannotAddSubgroupText"/>
                                    <span class="disabled" title="${cannotAddSubgroupText}">${addSubgroupText}</span>
                                </c:otherwise>
                            </c:choose>
                            
                            <%-- ADD DEVICES --%>
                            &nbsp;|&nbsp;
                            <cti:msg2 var="addDevicesText" key="yukon.web.deviceGroups.editor.operationsContainer.addDevicesText" />
                            <c:choose>
                                <c:when test="${groupModifiable}">
                                    <cti:url var="addByDeviceCollectionUrl" value="/group/editor/showAddDevicesByCollection">
                                        <cti:param name="groupName" value="${group.fullName}" />
                                    </cti:url>
                                    <a title="Click to add multiple devices" href="${addByDeviceCollectionUrl}">${addDevicesText}</a>
                                </c:when>
                                <c:otherwise>
                                    <cti:msg2 var="cannotAddDevicesText" key="yukon.web.deviceGroups.editor.operationsContainer.cannotAddDevicesText"/>
                                    <span class="disabled" title="${cannotAddDevicesText}">${addDevicesText}</span>
                                </c:otherwise>
                            </c:choose>
                            
                            <%-- COPY CONTENTS TO ANOTHER GROUP --%>
                            &nbsp;|&nbsp;
                            <cti:msg2 var="copyContentsLinkText" key="yukon.web.deviceGroups.editor.operationsContainer.copyContents.linkText"/>
                            <cti:msg2 var="copyContentsLinkTitle" key="yukon.web.deviceGroups.editor.operationsContainer.copyContents.linkTitle"/>
                            <a title="${copyContentsLinkTitle}" href="javascript:void(0);" id="copyContentsToGroupLink">${copyContentsLinkText}</a>
                            
                            <form id="copyContentsToGroupForm" action="/group/editor/copyContentsToGroup">
                                
                                <cti:msg2 var="copyContentsPopupTitle" key="yukon.web.deviceGroups.editor.operationsContainer.copyContents.popupTree.title"/>
                                <cti:msg2 var="copyContentsPopupSubmitText" key="yukon.web.deviceGroups.editor.operationsContainer.copyContents.popupTree.submitButtonText"/>
                                <cti:msg2 var="copyContentsPopupCancelText" key="yukon.web.deviceGroups.editor.operationsContainer.copyContents.popupTree.cancelButtonText"/>
                                <cti:msg2 var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />
                                
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
                                                                    height="400"
                                                                    noSelectionAlertText="${noGroupSelectedAlertText}"
                                                                    includeControlBar="true" />
                            </form>
                        </div>
                    </cti:checkProperty>
                    </cti:checkRole>
                    
                    <%-- GENERATE REPORTS --%>
                    <h3><cti:msg2 key="yukon.web.deviceGroups.editor.operationsContainer.generateReportsLabel"/></h3>
                    <div class="groupEditorContentDetail stacked">
                        <cti:url value="/amr/reports/groupDevicesReport" var="htmlUrl">
                            <cti:param name="groupName" value="${group.fullName}"/>
                        </cti:url>
                        <c:choose>
                            <c:when test="${childDeviceCount > 0}">
                                <a href="${htmlUrl}"><cti:msg2 key="yukon.web.modules.amr.fileFormatHtml"/></a>
                                &nbsp;|&nbsp;
                                <cti:simpleReportLinkFromNameTag definitionName="deviceGroupDefinition" viewType="csvView" deviceGroup="${group}"><cti:msg2 key="yukon.web.modules.amr.fileFormatCsv"/></cti:simpleReportLinkFromNameTag>
                                &nbsp;|&nbsp;
                                <cti:simpleReportLinkFromNameTag definitionName="deviceGroupDefinition" viewType="pdfView" deviceGroup="${group}"><cti:msg2 key="yukon.web.modules.amr.fileFormatPdf"/></cti:simpleReportLinkFromNameTag>
                           
                            </c:when>
                            <c:otherwise>
                                <cti:msg2 key="yukon.web.deviceGroups.editor.membersContainer.noDevices"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    
                    <%-- COLLECTION ACTIONS --%>
                    <h3><cti:msg2 key="yukon.web.deviceGroups.editor.operationsContainer.collectionActionLabel"/></h3>
                    <div class="groupEditorContentDetail">
                    
                    <c:choose>
                        <c:when test="${deviceCount > 0}">
                            
                            <cti:checkProperty property="operator.DeviceActionsRole.GROUP_COMMANDER">
                                <cti:link href="/group/commander/collectionProcessing" key="yukon.web.deviceGroups.editor.operationsContainer.sendCommand">
                                    <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                                </cti:link>
                                &nbsp;|&nbsp;
                            </cti:checkProperty>
                            
                            <cti:checkProperty property="operator.DeviceActionsRole.MASS_CHANGE">
                                <cti:link href="/bulk/massChange/massChangeSelect" key="yukon.web.deviceGroups.editor.operationsContainer.massChange">
                                    <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                                </cti:link>
                                &nbsp;|&nbsp;
                            </cti:checkProperty>
                            
                            <cti:link href="/bulk/collectionActions" key="yukon.web.deviceGroups.editor.operationsContainer.otherActions">
                                <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                            </cti:link>
                            
                        </c:when>
                        <c:otherwise><i:inline key="yukon.web.deviceGroups.editor.membersContainer.noDevices"/></c:otherwise>
                    </c:choose>
                    </div>
                </div>
            </jsp:body>
        </tags:boxContainer>
        
    </div>
   	
   	<%-- MEMBERS BOX --%>
    <div style="width: 450px">
        <tags:boxContainer hideEnabled="false">
        
            <jsp:attribute name="title">
                <table>
                    <tr>
                        <td valign="top">
                            <cti:msg2 key="yukon.web.deviceGroups.editor.membersContainer.membersLabel"/>
                        </td>
                        <td valign="top">
                            ${fn:escapeXml((empty group.name)? '[ Top Level ]' : groupFullName)}
                        </td>
                    </tr>
                </table>
            </jsp:attribute>
        
            <jsp:body>
            
                <div>
    
                    <%-- SUBGROUPS --%>
                    <cti:msg2 var="subGroupsTitle" key="yukon.web.deviceGroups.editor.memberContainer.subGroups"/>
                    <tags:sectionContainer title="${subGroupsTitle}" styleClass="groupEditorContentDetail">
                        <div class="scrollingContainer_small">
                            <c:choose>
                                <c:when test="${fn:length(subGroupMap) > 0}">
                                    <%-- User must have DEVICE_GROUP_EDIT to delete groups. Set once for use in loop. --%>
                                    <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
                                    <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_EDIT">
                                        <c:set var="hasEditRoleProperty" value="true"/>
                                    </cti:checkProperty>
                                    </cti:checkRole>
                                    <table class="compactResultsTable rowHighlighting">
                                        <thead></thead>
                                        <tfoot></tfoot>
                                        <tbody>
                                            <c:forEach var="subGroup" items="${subGroupMap}">
                                                <tr>
                                                    <td>
                                                        <cti:url var="homeUrl" value="/group/editor/home"><cti:param name="groupName" value="${subGroup.value}" /></cti:url>
                                                        <span title="${fn:escapeXml(subGroup.value)}">
                                                            <a href="${homeUrl}"><c:out value="${subGroup.key.name}"/></a>
                                                        </span>
                                                    </td>
                                                    
                                                    <c:if test="${hasEditRoleProperty}">
                                                        <td class="last tar">
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
                                                                    <cti:msg2 var="cannotDeleteGroupLinkTitle" key="yukon.web.deviceGroups.editor.membersContainer.cannotDeleteGroupLinkTitle"/>
                                                                    <img class="graycssicon" title="${cannotDeleteGroupLinkTitle}" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>" >
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </c:if>
                                                    
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:when> 
                                <c:otherwise><i:inline key="yukon.web.deviceGroups.editor.membersContainer.noChildGroups"/></c:otherwise>
                            </c:choose>
                        </div>
                    </tags:sectionContainer>
                
                    <%-- DEVICES --%>
                    <cti:msg2 var="groupMemebersTitle" key="yukon.web.deviceGroups.editor.memberContainer.groupMembers"/>
                    <tags:sectionContainer title="${groupMemebersTitle}" styleClass="groupEditorContentDetail">
                        <div id="deviceMembers">
                            <c:choose>
                                <c:when test="${not showImmediately && (showDevices == false )}">
                                    <div>
                                        <i:inline key="yukon.web.deviceGroups.editor.membersContainer.groupContainsCountLabel" arguments="${childDeviceCount}"/>
                                        <c:if test="${childDeviceCount > 0}">
                                            <cti:msg2 var="showDevicesLimitText" 
                                                     key="yukon.web.deviceGroups.editor.membersContainer.showDevicesLimitText" 
                                                     arguments="${maxGetDevicesSize}" />
                                            <cti:msg2 var="showDeviceslabel" key="yukon.web.deviceGroups.editor.membersContainer.showDeviceslabel"/>
                                            <input id="showDevicesButton" type="button" onclick="showDevices()" value="${showDeviceslabel}" title="${showDevicesLimitText}">
                                            <img id="showDevicesWaitImg" src="<cti:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">
                                        </c:if>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <jsp:include page="deviceMembers.jsp">
                                        <jsp:param name="groupName" value="${group.fullName}"/>
                                    </jsp:include>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </tags:sectionContainer>
                    
                </div>
            </jsp:body>
        </tags:boxContainer>
        
    </div>
   	
   	</td>
   	</tr>
    </table>

</cti:standardPage>