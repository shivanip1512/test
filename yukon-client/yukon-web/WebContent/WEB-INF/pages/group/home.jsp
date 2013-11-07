<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

<cti:standardPage module="tools" page="deviceGroups">

<cti:msg2 var="removeGroupAreYouSure" key="yukon.web.deviceGroups.editor.operationsContainer.removeGroup.areYouSure" javaScriptEscape="true"/>
<cti:msg2 var="invalidGroupNameError" key="yukon.web.deviceGroups.editor.operationsContainer.invalidGroupNameError" javaScriptEscape="true"/>
    
<script type="text/javascript">
jQuery(function() {
    jQuery(".f-edit-grp-name").click(function(event) {
        jQuery("#editGroupNamePopup").dialog();
    });
    jQuery(".f-add-sub-grp").click(function(event) {
        jQuery("#addSubGroupPopup").dialog();
    });
});

function removeGroup(formName){
    var confirmRemove = confirm('${removeGroupAreYouSure}');
    if(confirmRemove) {
        if(formName != null) {
            jQuery("#" + formName).submit();
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

function checkAndSubmitNewName(nameId, formId, buttonId) {
    var newName = jQuery("#" + nameId).val();
    if(!isValidGroupName(newName)) {
        alert('${invalidGroupNameError}');
        jQuery("#" + nameId).focus();
    } else {
        jQuery("#" + buttonId).attr("disabled", "disabled");
        jQuery("#" + formId).submit();
    }
}
    
function submitMoveGroupForm() {
    jQuery("#moveGroupForm").submit();
}
function submitCopyContentsToGroupForm() {
    jQuery("#copyContentsToGroupForm").submit();
}

function showDevices() {
    jQuery("#showDevicesButton").attr("disabled", "disabled");
    // escape the group name to escape problem characters
    var groupName = '${cti:escapeJavaScript(group.fullName)}';
    jQuery("#deviceMembers").load('/group/editor/getDevicesForGroup', {'groupName': groupName});
}

function removeAllDevices(confirmText) {
    var doRemove = confirm(confirmText);
    if (doRemove) {
        jQuery("#removeAllDevicesButton").attr("disabled", "disabled");
        var groupName = '${cti:escapeJavaScript(group.fullName)}';
        jQuery("#deviceMembers").load('/group/editor/removeAllDevicesFromGroup', {'groupName': groupName});
    }
}

function confirmRemoveAllDevices(confirmText) {
    var doRemove = confirm(confirmText);
    if (doRemove) {
        jQuery("#removeAllDevicesFromGroupForm").submit();
    }
}
</script>

<cti:linkTabbedContainer mode="section">
    <cti:linkTab selectorKey="yukon.web.deviceGroups.editor.tab.title" initiallySelected="true">
        <c:url value="/group/editor/home" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.deviceGroups.commander.tab.title">
        <c:url value="/group/commander/groupProcessing" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.common.device.groupMeterRead.home.tab.title">
        <c:url value="/group/groupMeterRead/homeGroup" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.amr.deviceGroupUpload.tab.title">
        <c:url value="/group/updater/upload" />
    </cti:linkTab>
</cti:linkTabbedContainer>

    <c:if test="${not empty param.errorMessage}">
        <div class="error">${fn:escapeXml(param.errorMessage)}</div>
    </c:if>
    
    <div class="column-12-12 clear">
        <div class="column one">
            <%-- GROUPS HIERARCHY BOX --%>
	        <cti:msg2 key="yukon.web.deviceGroups.editor.groupsContainer.title" var="groupsTitle"/>
	        <tags:boxContainer title="${groupsTitle}" hideEnabled="false">
	            <jsTree:nodeValueRedirectingInlineTree  name="groupName"
                                                        hrefBase="/group/editor/home"
                                                        otherHrefParameters=""
                                                        id="deviceGroupEditorTree"
                                                        dataJson="${allGroupsDataJson}"
                                                        highlightNodePath="${extSelectedNodePath}"
                                                        includeControlBar="true" />
	        </tags:boxContainer>
        </div>
        
        <div class="column two nogutter">
	        <%-- OPERATIONS BOX --%>
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
	                <%-- EDIT GROUP --%>
	                <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                    <cti:checkRolesAndProperties value="DEVICE_GROUP_EDIT">
		                
		                    <c:if test="${group.editable}">
		                        <h3><cti:msg2 key="yukon.web.deviceGroups.editor.operationsContainer.editGroupLabel"/></h3>
		                        <div class="groupEditorContentDetail stacked">
		                            
		                            <%-- EDIT NAME --%>
		                            <cti:msg2 var="editGroupNameText" key="yukon.web.deviceGroups.editor.operationsContainer.editGroupNameText" />
		                            <cti:msg2 var="newGroupNameText" key="yukon.web.deviceGroups.editor.operationsContainer.newGroupNameText" />
		                            <cti:msg2 var="changeNameButtonText" key="yukon.web.deviceGroups.editor.operationsContainer.newGroupNameSaveText" />
		                            <a title="Click to edit group name" href="javascript:void(0);" class="f-edit-grp-name">${editGroupNameText}</a>
		                            <div id="editGroupNamePopup" title="${editGroupNameText}" class="groupEditorPopup dn">
		                                <form id="editGroupNameForm" method="post" action="/group/editor/updateGroupName" >
		                                    <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
		                                    <tags:nameValueContainer>
		                                        <tags:nameValue name="${newGroupNameText}">
		                                            <input id="newGroupName" name="newGroupName" type="text" value="${fn:escapeXml(group.name)}" />
		                                        </tags:nameValue>
		                                    </tags:nameValueContainer>
		                                    <div class="action-area">
		                                        <button id="editGroupNameSaveButton" type="button" value="${changeNameButtonText}" onclick="checkAndSubmitNewName('newGroupName', 'editGroupNameForm', 'editGroupNameSaveButton');">
		                                            <span class="label">${changeNameButtonText}</span>
		                                        </button>
		                                    </div>
		                                </form>
		                            </div>
		                            
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
			                                noSelectionAlertText="${noGroupSelectedAlertText}"
			                                includeControlBar="true" />
		                                </form>
		                        </div>
		                    </c:if>
		                
		                </cti:checkRolesAndProperties>
                    </cti:checkRolesAndProperties>
	                
	                <%-- MODIFY GROUP --%>
	                <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                    <cti:checkRolesAndProperties value="DEVICE_GROUP_MODIFY">
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
		                                <cti:msg2 var="addSubgroupLinkTitle" key="yukon.web.deviceGroups.editor.operationsContainer.addSubgroupLinkTitle" />
		                                <cti:msg2 var="subgroupNameLabel" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupNameLabel" />
		                                <cti:msg2 var="subgroupTypeLabel" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupTypeLabel" />
		                                <cti:msg2 var="subgroupTypeBasicLabel" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupType.basicLabel" />
		                                <cti:msg2 var="subgroupTypeComposedLabel" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupType.composedLabel" />
		                                <cti:msg2 var="subgroupEmptyGroupTitle" key="yukon.web.deviceGroups.editor.operationsContainer.subgroup.emptyGroupTitle" />
		                                <cti:msg2 var="subgroupComposedGroupTitle" key="yukon.web.deviceGroups.editor.operationsContainer.subgroup.composedGroupTitle" />
		                                <cti:msg2 var="subgroupNameSaveText" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupNameSaveText" />
		                                
		                                <a title="${addSubgroupLinkTitle}" class="f-add-sub-grp" href="javascript:void(0);">${addSubgroupText}</a>
		                                <div id="addSubGroupPopup" title="${addSubgroupText}" class="groupEditorPopup dn">
		                                    <form id="addSubGroupForm" method="post" action="/group/editor/addChild">
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
		                                        <div class="action-area">
		                                            <button id="addSubGroupSaveButton" value="${subgroupNameSaveText}" onclick="checkAndSubmitNewName('childGroupName', 'addSubGroupForm', 'addSubGroupSaveButton');">
		                                                <span class="label">${subgroupNameSaveText}</span>
		                                            </button>
		                                        </div>
		                                    </form>
		                                </div>
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
		                            
		                            <jsTree:nodeValueSelectingPopupTree fieldId="copyContentsToGroupName"
			                            fieldName="copyContentsToGroupName"
			                            nodeValueName="groupName"
			                            submitButtonText="${copyContentsPopupSubmitText}"
			                            cancelButtonText="${copyContentsPopupCancelText}"
			                            submitCallback="submitCopyContentsToGroupForm();"
			                            
			                            id="copyContentsToGroupTree"
			                            triggerElement="copyContentsToGroupLink"
			                            dataJson="${copyGroupDataJson}"
			                            title="${copyContentsPopupTitle}"
			                            noSelectionAlertText="${noGroupSelectedAlertText}"
			                            includeControlBar="true" />
		                        </form>
		                    </div>
		                </cti:checkRolesAndProperties>
                    </cti:checkRolesAndProperties>
	                
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
	                         <cti:checkRolesAndProperties value="GROUP_COMMANDER">
	                             <cti:link href="/group/commander/collectionProcessing" key="yukon.web.deviceGroups.editor.operationsContainer.sendCommand">
	                                 <cti:mapParam value="${deviceCollection.collectionParameters}"/>
	                             </cti:link>
	                             &nbsp;|&nbsp;
	                         </cti:checkRolesAndProperties>
                            
                            <cti:checkRolesAndProperties value="MASS_CHANGE">
	                             <cti:link href="/bulk/massChange/massChangeSelect" key="yukon.web.deviceGroups.editor.operationsContainer.massChange">
	                                 <cti:mapParam value="${deviceCollection.collectionParameters}"/>
	                             </cti:link>
	                             &nbsp;|&nbsp;
	                         </cti:checkRolesAndProperties>
	                         <cti:link href="/bulk/collectionActions" key="yukon.web.deviceGroups.editor.operationsContainer.otherActions">
	                             <cti:mapParam value="${deviceCollection.collectionParameters}"/>
	                         </cti:link>
	                     </c:when>
	                     <c:otherwise><i:inline key="yukon.web.deviceGroups.editor.membersContainer.noDevices"/></c:otherwise>
	                 </c:choose>
	                </div>
	            </jsp:body>
	        </tags:boxContainer>
	        
	       <%-- MEMBERS BOX --%>
	        <tags:boxContainer hideEnabled="false">
	            <jsp:attribute name="title">
	                <table>
	                    <tr>
	                        <td valign="top"><cti:msg2 key="yukon.web.deviceGroups.editor.membersContainer.membersLabel"/></td>
	                        <td valign="top">${fn:escapeXml((empty group.name)? '[ Top Level ]' : groupFullName)}</td>
	                    </tr>
	                </table>
	            </jsp:attribute>
	        
	            <jsp:body>
	                    <%-- SUBGROUPS --%>
	                    <cti:msg2 var="subGroupsTitle" key="yukon.web.deviceGroups.editor.memberContainer.subGroups"/>
	                    <tags:sectionContainer title="${subGroupsTitle}" styleClass="groupEditorContentDetail">
	                        <div class="scroll-small">
	                            <c:choose>
	                                <c:when test="${fn:length(subGroupMap) > 0}">
	                                    <%-- User must have DEVICE_GROUP_EDIT to delete groups. Set once for use in loop. --%>
	                                    <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                                        <cti:checkRolesAndProperties value="DEVICE_GROUP_EDIT">
	                                        <c:set var="hasEditRoleProperty" value="true"/>
	                                    </cti:checkRolesAndProperties>
                                        </cti:checkRolesAndProperties>
	                                    <table class="compact-results-table row-highlighting">
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
	                                                                            <a href="#" onclick="removeGroup('${subId}removeSubGroupForm');"><i class="icon icon-cross"></i></a>
	                                                                        </div>
	                                                                    </form>
	                                                                </c:when>
	                                                                <c:otherwise>
	                                                                    <cti:msg2 var="cannotDeleteGroupLinkTitle" key="yukon.web.deviceGroups.editor.membersContainer.cannotDeleteGroupLinkTitle"/>
                                                                        <cti:icon icon="icon-cross" disabled="disabled" title="${cannotDeleteGroupLinkTitle}"/>
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
	                                    <i:inline key="yukon.web.deviceGroups.editor.membersContainer.groupContainsCountLabel" arguments="${childDeviceCount}"/>
	                                    <c:if test="${childDeviceCount > 0}">
	                                        <div class="action-area">
	                                            <cti:msg2 var="showDevicesLimitText" key="yukon.web.deviceGroups.editor.membersContainer.showDevicesLimitText" arguments="${maxGetDevicesSize}"/>
	                                            <cti:msg2 var="showDeviceslabel" key="yukon.web.deviceGroups.editor.membersContainer.showDeviceslabel"/>
	                                            <button id="showDevicesButton" onclick="showDevices()" value="${showDeviceslabel}" title="${showDevicesLimitText}"><span class="label">${showDeviceslabel}</span></button>
	                                        </div>
	                                    </c:if>
	                                </c:when>
	                                <c:otherwise>
	                                    <jsp:include page="deviceMembers.jsp">
	                                        <jsp:param name="groupName" value="${group.fullName}"/>
	                                    </jsp:include>
	                                </c:otherwise>
	                            </c:choose>
	                        </div>
	                    </tags:sectionContainer>
	            </jsp:body>
	        </tags:boxContainer>
	    </div>
	</div>
</cti:standardPage>