<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:msgScope paths="deviceGroups.editor.operationsContainer,deviceGroups.editor.membersContainer,modules.tools.deviceGroups">

<cti:msg2 var="invalidGroupNameError" key="yukon.web.deviceGroups.editor.operationsContainer.invalidGroupNameError" javaScriptEscape="true"/>
 
 <input id="selectedGroup" type="hidden" value="${extSelectedNodePath}"/>

    <%-- OPERATIONS BOX --%>
    <cti:msg2 key=".title" var="operationsTitle"/>
    <cti:msg2 key=".topLevelLabel" var="topLevelLabel"/>
    <tags:boxContainer hideEnabled="false">
        <jsp:attribute name="title">
            ${operationsTitle}:&nbsp;
            <c:choose>
                <c:when test="${empty group.name}">[ ${topLevelLabel} ]</c:when>
                <c:otherwise>${fn:escapeXml(groupFullName)}</c:otherwise>
            </c:choose>
        </jsp:attribute>
        <jsp:body>
        
        <%-- EDIT GROUP --%>
        <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
        <cti:checkRolesAndProperties value="DEVICE_GROUP_EDIT">
            
                <c:if test="${group.editable}">
                    <h3><cti:msg2 key=".editGroupLabel"/></h3>
                    <div class="groupEditorContentDetail stacked">
                        
                        <%-- EDIT NAME --%>
                        <cti:msg2 var="editGroupNameText" key=".editGroupNameText" />
                        <cti:msg2 var="newGroupNameText" key=".newGroupNameText" />
                        <cti:msg2 var="changeNameButtonText" key=".newGroupNameSaveText" />
                        <a title="Click to edit group name" href="javascript:void(0);" class="js-edit-grp-name">${editGroupNameText}</a>
                        <div id="editGroupNamePopup" title="${editGroupNameText}" class="groupEditorPopup dn">
                            <form id="editGroupNameForm" method="post" action="<cti:url value="/group/editor/updateGroupName"/>" >
                                <div class="js-invalid-group-name error dn">${invalidGroupNameError}</div>
                                <cti:csrfToken/>    
                                <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                                <tags:nameValueContainer>
                                    <tags:nameValue name="${newGroupNameText}">
                                        <input id="newGroupName" name="newGroupName" type="text" value="${fn:escapeXml(group.name)}" maxlength="60"/>
                                    </tags:nameValue>
                                </tags:nameValueContainer>
                                <div class="action-area">
                                    <cti:button id="editGroupNameSaveButton" 
                                            label="${changeNameButtonText}" 
                                            onclick="yukon.tools.group.editor.checkAndSubmitNewName('newGroupName', 'editGroupNameForm', 'editGroupNameSaveButton');"
                                            classes="primary action"/>
                                </div>
                            </form>
                        </div>
                        
                        &nbsp;|&nbsp;
                        <%-- REMOVE --%>
                        <form id="removeGroupForm" action="<cti:url value="/group/editor/removeGroup"/>" method="post" class="di">
                            <cti:csrfToken/>
                            <input type="hidden" name="removeGroupName" value="${fn:escapeXml(group.fullName)}">
                            <span id="removeGroupLink" data-form-id="removeGroupForm" data-ok-event="yukon:devicegroup:removegroup">
                                <cti:link key="yukon.web.deviceGroups.editor.operationsContainer.removeGroup" href="#"/>
                            </span>
                            <d:confirm on="#removeGroupLink" nameKey="removeGroup.areYouSure"/>
                        </form>
                        
                        &nbsp;|&nbsp;
                        <%-- MOVE --%>
                        <cti:link id="moveGroupLink" href="javascript:void(0);" key="yukon.web.deviceGroups.editor.operationsContainer.moveGroup"/>
                            
                        <form id="moveGroupForm" action="<cti:url value="/group/editor/moveGroup"/>" method="post">
                            <cti:csrfToken/>    
                            <cti:msg2 var="moveGroupPopupTitle" key=".moveGroupPopup.title"/>
                            <cti:msg2 var="moveGroupPopupSubmitButtonText" key=".moveGroupPopup.submitButtonText"/>
                            <cti:msg2 var="moveGroupPopupCancelButtonText" key=".moveGroupPopup.cancelButtonText"/>
                            <cti:msg2 var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />
                            
                            <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                            
                            <jsTree:nodeValueSelectingPopupTree fieldId="parentGroupName"
                                fieldName="parentGroupName"
                                nodeValueName="groupName"
                                submitButtonText="${moveGroupPopupSubmitButtonText}"
                                cancelButtonText="${moveGroupPopupCancelButtonText}"
                                submitCallback="yukon.tools.group.editor.submitMoveGroupForm();"
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
                    
                        <cti:msg2 var="editComposedLinkText" key=".editComposed.linkText"/>
                        <cti:msg2 var="editComposedLinkTitle" key=".editComposed.linkTitle"/>
                    
                        <cti:url var="editComposedGroupUrl" value="/group/composedGroup/build">
                            <cti:param name="groupName" value="${group.fullName}" />
                        </cti:url>
                        <a title="${editComposedLinkTitle}" href="${editComposedGroupUrl}">${editComposedLinkText}</a>
                        
                    </c:if>
                    
                    <%-- ADD GROUP --%>
                    <c:if test="${!isComposedGroup}">
                        <cti:msg2 var="addSubgroupText" key=".addSubgroupText"/>
                        <c:choose>
                            <c:when test="${group.modifiable}">
                                <cti:msg2 var="addSubgroupLinkTitle" key=".addSubgroupLinkTitle" />
                                <cti:msg2 var="subgroupNameLabel" key=".subgroupNameLabel" />
                                <cti:msg2 var="subgroupTypeLabel" key=".subgroupTypeLabel" />
                                <cti:msg2 var="subgroupTypeBasicLabel" key=".subgroupType.basicLabel" />
                                <cti:msg2 var="subgroupTypeComposedLabel" key=".subgroupType.composedLabel" />
                                <cti:msg2 var="subgroupEmptyGroupTitle" key=".subgroup.emptyGroupTitle" />
                                <cti:msg2 var="subgroupComposedGroupTitle" key=".subgroup.composedGroupTitle" />
                                <cti:msg2 var="subgroupNameSaveText" key=".subgroupNameSaveText" />
                                
                                <a title="${addSubgroupLinkTitle}" class="js-add-sub-grp" href="javascript:void(0);">${addSubgroupText}</a>
                                <div id="addSubGroupPopup" title="${addSubgroupText}" class="groupEditorPopup dn">
                                    <form id="addSubGroupForm" method="post" action="<cti:url value="/group/editor/addChild"/>">
                                        <cti:csrfToken/>
                                        <div class="js-invalid-group-name error dn">${invalidGroupNameError}</div>
                                        <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                                        <tags:nameValueContainer>
                                            <tags:nameValue name="${subgroupNameLabel}" nameColumnWidth="120px">
                                                <input id="childGroupName" name="childGroupName" type="text" maxlength="60"/>
                                            </tags:nameValue>
                                            <tags:nameValue name="${subgroupTypeLabel}">
                                                <select name="subGroupType">
                                                    <option value="STATIC" title="${subgroupEmptyGroupTitle}">${subgroupTypeBasicLabel}</option>
                                                    <option value="COMPOSED" title="${subgroupComposedGroupTitle}">${subgroupTypeComposedLabel}</option>
                                                </select>
                                            </tags:nameValue>
                                        </tags:nameValueContainer>
                                        <div class="action-area">
                                            <cti:button id="addSubGroupSaveButton" 
                                                label="${subgroupNameSaveText}" 
                                                onclick="yukon.tools.group.editor.checkAndSubmitNewName('childGroupName', 'addSubGroupForm', 'addSubGroupSaveButton');"
                                                classes="primary action"/>
                                        </div>
                                    </form>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <cti:msg2 var="cannotAddSubgroupText" key=".cannotAddSubgroupText"/>
                                <span class="disabled" title="${cannotAddSubgroupText}">${addSubgroupText}</span>
                            </c:otherwise>
                        </c:choose>
                        
                        <%-- ADD DEVICES --%>
                        &nbsp;|&nbsp;
                        <c:choose>
                            <c:when test="${groupModifiable}">
                                <cti:url value="/group/editor/addDevicesByCollection" var="selectionUrl"/>
                                <form  method="post" action="${selectionUrl}" class="dib">
                                    <cti:csrfToken />
                                    <tags:deviceCollectionPicker multi="true" submit="true" 
                                        title=".add.title">
                                        <cti:msg2 key=".addDevices"/>
                                    </tags:deviceCollectionPicker>
                                    <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}"/>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <cti:msg2 var="cannotAddDevicesText" key=".cannotAddDevicesText"/>
                                <span class="disabled" title="${cannotAddDevicesText}"><i:inline key=".addDevices"/></span>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                    
                    <%-- COPY CONTENTS TO ANOTHER GROUP --%>
                    &nbsp;|&nbsp;
                    <cti:msg2 var="copyContentsLinkText" key=".copyContents.linkText"/>
                    <cti:msg2 var="copyContentsLinkTitle" key=".copyContents.linkTitle"/>
                    <a title="${copyContentsLinkTitle}" href="javascript:void(0);" id="copyContentsToGroupLink">${copyContentsLinkText}</a>
                    
                    <form id="copyContentsToGroupForm" action="<cti:url value="/group/editor/copyContentsToGroup"/>">
                        <cti:csrfToken/>    
                        
                        <cti:msg2 var="copyContentsPopupTitle" key=".copyContents.popupTree.title"/>
                        <cti:msg2 var="copyContentsPopupSubmitText" key=".copyContents.popupTree.submitButtonText"/>
                        <cti:msg2 var="copyContentsPopupCancelText" key=".copyContents.popupTree.cancelButtonText"/>
                        <cti:msg2 var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />
                        
                        <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                        
                        <jsTree:nodeValueSelectingPopupTree fieldId="copyContentsToGroupName"
                            fieldName="copyContentsToGroupName"
                            nodeValueName="groupName"
                            submitButtonText="${copyContentsPopupSubmitText}"
                            cancelButtonText="${copyContentsPopupCancelText}"
                            submitCallback="yukon.tools.group.editor.submitCopyContentsToGroupForm();"
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
        <h3><cti:msg2 key=".generateReportsLabel"/></h3>
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
                    <cti:msg2 key=".noDevices"/>
                </c:otherwise>
            </c:choose>
        </div>
        
        <%-- COLLECTION ACTIONS --%>
        <h3><cti:msg2 key=".collectionActionLabel"/></h3>
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
             <c:otherwise><i:inline key=".noDevices"/></c:otherwise>
         </c:choose>
        </div>
        </jsp:body>
    </tags:boxContainer>
    
   <%-- MEMBERS BOX --%>
    <cti:msg2 key=".membersLabel" var="membersTitle"/>
    <tags:boxContainer hideEnabled="false">
        <jsp:attribute name="title">
        ${membersTitle}:&nbsp;
            <c:choose>
                <c:when test="${empty group.name}">[ ${topLevelLabel} ]</c:when>
                <c:otherwise>${fn:escapeXml(groupFullName)}</c:otherwise>
            </c:choose>
        </jsp:attribute>
        <jsp:body>
        <span class="fr">${deviceCount} <cti:msg2 key=".devices"/></span>

            <%-- SUBGROUPS --%>
            <cti:msg2 var="subGroupsTitle" key=".subGroups"/>
            <tags:sectionContainer title="${subGroupsTitle}" styleClass="groupEditorContentDetail">
                <div class="scroll-sm">
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
                                                <cti:url var="homeUrl" value="/group/editor/home"><cti:param name="groupName" value="${subGroup.key.fullName}"/></cti:url>
                                                <span title="${fn:escapeXml(subGroup.key.fullName)}">
                                                    <input type="hidden" id="js-subgroup-full-name" value="${fn:escapeXml(subGroup.key.fullName)}"/>
                                                    <a href="#" id="js-subgroups"><c:out value="${subGroup.key.name}"/></a>
                                                </span>
                                                <span class="fr">${subGroup.value}&nbsp;<cti:msg2 key=".devices"/></span>
                                            </td>
                                            
                                            <c:if test="${hasEditRoleProperty}">
                                                <td class="last tar">
                                                    <c:choose>
                                                    
                                                        <%-- the group being removed must itself be modifiable --%>
                                                        <c:when test="${groupModifiable}">
                                                
                                                            <cti:uniqueIdentifier prefix="subGroup_" var="subId"/>
                                                            <form style="display: inline;" id="${subId}removeSubGroupForm" action="<cti:url value="/group/editor/removeGroup"/>" method="post">
                                                                <cti:csrfToken/>
                                                                <input type="hidden" name="removeGroupName" value="${fn:escapeXml(subGroup.key.fullName)}">
                                                                <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                                                                <div class="dib">
                                                                    <span id="removeGroupButton" data-ok-event="yukon:devicegroup:removegroup"
                                                                          data-form-id="${subId}removeSubGroupForm">
                                                                        <a href="#"><i class="icon icon-cross"></i></a>
                                                                    </span>
                                                                    <d:confirm on="#removeGroupButton" nameKey="removeGroup.areYouSure"/>
                                                                </div>
                                                            </form>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <cti:msg2 var="cannotDeleteGroupLinkTitle" key=".cannotDeleteGroupLinkTitle"/>
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
                        <c:otherwise><i:inline key=".noChildGroups"/></c:otherwise>
                    </c:choose>
                </div>
            </tags:sectionContainer>
        
            <%-- DEVICES --%>
            <cti:msg2 var="groupMemebersTitle" key=".groupMembers"/>
            <tags:sectionContainer title="${groupMemebersTitle}" styleClass="groupEditorContentDetail">
                <div id="deviceMembers">
                    <c:choose>
                        <c:when test="${not showImmediately && (showDevices == false )}">
                            <i:inline key=".groupContainsCountLabel" arguments="${childDeviceCount}"/>
                            <c:if test="${childDeviceCount > 0}">
                                <div class="action-area">
                                    <cti:msg2 var="showDevicesLimitText" key=".showDevicesLimitText" arguments="${maxGetDevicesSize}"/>
                                    <cti:msg2 var="showDeviceslabel" key=".showDeviceslabel"/>
                                    <button id="showDevicesButton" onclick="yukon.tools.group.editor.showDevices('${group.fullName}')" value="${showDeviceslabel}" title="${showDevicesLimitText}"><span class="b-label">${showDeviceslabel}</span></button>
                                </div>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <%@ include file="deviceMembers.jsp" %>
                        </c:otherwise>
                    </c:choose>
                </div>
            </tags:sectionContainer>
        </jsp:body>
    </tags:boxContainer>
    
</cti:msgScope>
                        
