<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

<cti:standardPage title="Groups Home" module="amr">
<cti:standardMenu menuSelection="devicegroups|home"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink title="Groups Home" />
    </cti:breadCrumbs>
    
    
    <script type="text/javascript">
        
        function showGroupPopup(popupDiv, focusElem){
            
            $(popupDiv).toggle();
            if($(popupDiv).visible() && focusElem != null){
                $(focusElem).value = '';
                $(focusElem).focus();
            }
        }
        
        function removeGroup(formName){
            var confirmRemove = confirm("Are you sure you want to permanently remove this group and all of it's sub groups?  You will not be able to undo this removal.");
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
        
        function isValidGroupName(name) {
            if(name == null || name == '' || (name.indexOf('/') != -1) || (name.indexOf('\\') != -1)) {
                return false;
            }
            return true;
        }
        
        function checkAndSubmitNewName(nameId, formId, buttonId, waitImgId) {
        
            var newName = $F(nameId);
            if(!isValidGroupName(newName)) {
                alert('Group names may not contain slashes.\n\nPlease enter a new name.');
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
            new Ajax.Updater('deviceMembers', '/spring/group/editor/getDevicesForGroup', {method: 'post', parameters: params});
            
        }
        
        function removeAllDevices(confirmText) {
        
            var doRemove = confirm(confirmText);
            
            if (doRemove) {
            
                $('removeAllDevicesButton').disabled = true;
                $('removeAllDevicesWaitImg').show();
            
                var groupName = '${cti:escapeJavaScript(group.fullName)}';
                var params = {'groupName': groupName};
                new Ajax.Updater('deviceMembers', '/spring/group/editor/removeAllDevicesFromGroup', {method: 'post', parameters: params});
            }
        }
    
        function confirmRemoveAllDevices(confirmText) {
        
            var doRemove = confirm(confirmText);
            
            if (doRemove) {
                $('removeAllDevicesFromGroupForm').submit();
            }
        }
        
    </script>

    <h2>Groups Home</h2>
    <br>
    <c:if test="${not empty param.errorMessage}">
        <div style="color: red;">
            ${param.errorMessage}
        </div>
        <br/><br/>
    </c:if>

    <%-- GROUPS HIERARCHY BOX --%>
    <div style="float: left; margin-right: .75em; margin-bottom: .5em; width: 450px;">

        <cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.title" var="groupsTitle"/>
        <tags:boxContainer title="${groupsTitle}" hideEnabled="false">
            
            <c:set var="selectedGroupParam">
                <jsp:attribute name="value">
                    "${fn:escapeXml(group.fullName)}"
                </jsp:attribute>
            </c:set>
            
            <ext:nodeValueRedirectingInlineTree name="groupName"
                                                hrefBase="/spring/group/editor/home"
                                                otherHrefParameters=""
                                                id="deviceGroupEditorTree"
                                                dataJson="${allGroupsDataJson}"
                                                width="432"
                                                height="600" />
                                
        </tags:boxContainer>
    </div>
    
    <%-- OPERATIONS BOX --%>
    <div style="float: left; margin-right: .75em; margin-bottom: 10px; width: 450px;">
        
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
                                    ${fn:escapeXml(group.fullName)}
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
                    
                        <h4><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.editGroupLabel"/></h4>
                        
                        <%-- EDIT NAME --%>
                        <c:set var="editGroupNamePopupId" value="editGroupNamePopup" />
                        <cti:msg var="editGroupNameText" key="yukon.web.deviceGroups.editor.operationsContainer.editGroupNameText" />
                        <cti:msg var="newGroupNameText" key="yukon.web.deviceGroups.editor.operationsContainer.newGroupNameText" />
                        <cti:msg var="changeNameButtonText" key="yukon.web.deviceGroups.editor.operationsContainer.newGroupNameSaveText" />
                        
                        <a title="Click to edit group name" onclick="showGroupPopup('${editGroupNamePopupId}', 'newGroupName')" href="javascript:void(0);">
                            ${editGroupNameText}
                        </a>
                        
                        <tags:simplePopup id="${editGroupNamePopupId}" title="${editGroupNameText}" styleClass="groupEditorPopup"  onClose="showGroupPopup('${editGroupNamePopupId}');">
                            
                            <form id="editGroupNameForm" method="post" action="/spring/group/editor/updateGroupName" onsubmit="return changeGroupName();">
                            
                                <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                                
                                <tags:nameValueContainer>
                                    <tags:nameValue name="${newGroupNameText}">
                                        <input id="newGroupName" name="newGroupName" type="text" value="${fn:escapeXml(group.name)}" />
                                    </tags:nameValue>
                                </tags:nameValueContainer>
                                
                                <br>
                                <input id="editGroupNameSaveButton" type="button" value="${changeNameButtonText}" 
                                        onclick="checkAndSubmitNewName('newGroupName', 'editGroupNameForm', 'editGroupNameSaveButton', 'editGroupNameWaitImg');">
                                <img id="editGroupNameWaitImg" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">
                            
                            </form>
                            
                        </tags:simplePopup>
                        
                        <%-- REMOVE --%>
                        <form id="removeGroupForm" action="/spring/group/editor/removeGroup" method="post">
                            <input type="hidden" name="removeGroupName" value="${fn:escapeXml(group.fullName)}">
                            <cti:link key="yukon.web.deviceGroups.editor.operationsContainer.removeGroup" href="javascript:removeGroup('removeGroupForm')"/>
                        </form>
                        
                        <%-- MOVE --%>
                        <div>
                        <cti:link id="moveGroupLink" class="lame" href="javascript:void(0);" key="yukon.web.deviceGroups.editor.operationsContainer.moveGroup"/>
                        
                        <form id="moveGroupForm" action="/spring/group/editor/moveGroup" method="post">
                            
                            <cti:msg var="moveGroupPopupTitle" key="yukon.web.deviceGroups.editor.operationsContainer.moveGroupPopup.title"/>
                            <cti:msg var="moveGroupPopupSubmitButtonText" key="yukon.web.deviceGroups.editor.operationsContainer.moveGroupPopup.submitButtonText"/>
                            <cti:msg var="moveGroupPopupCancelButtonText" key="yukon.web.deviceGroups.editor.operationsContainer.moveGroupPopup.cancelButtonText"/>
                            <cti:msg var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />
                            
                            <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                            
                            <ext:nodeValueSelectingPopupTree    fieldId="parentGroupName"
                                                                fieldName="parentGroupName"
                                                                nodeValueName="groupName"
                                                                submitButtonText="${moveGroupPopupSubmitButtonText}"
                                                                cancelButtonText="${moveGroupPopupCancelButtonText}"
                                                                submitCallback="submitMoveGroupForm();"
                                                                
                                                                id="groupsEditorMoveGroupTree"
                                                                treeAttributes="{}"
                                                                triggerElement="moveGroupLink"
                                                                dataJson="${moveGroupDataJson}"
                                                                title="${moveGroupPopupTitle}"
                                                                width="432"
                                                                height="600"
                                                                noSelectionAlertText="${noGroupSelectedAlertText}" />
                        </form>
                        </div>
                            
                    </c:if>
                    
                    </cti:checkProperty>
                    </cti:checkRole>
                    
                    
                    <%--############################ MODIFY GROUP ##############################--%>
                    <%--########################################################################--%>
                    <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
                    <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_MODIFY">
                    <h4><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.modifyGroupsLabel" /></h4>
                    
                    <%-- ADD GROUP --%>
                    <c:choose>
                        <c:when test="${group.modifiable}">
                            
                            <c:set var="addSubGroupPopupId" value="addSubGroupPopup" />
                            <cti:msg var="addSubgroupText" key="yukon.web.deviceGroups.editor.operationsContainer.addSubgroupText"/>
                            <cti:msg var="addSubgroupLinkTitle" key="yukon.web.deviceGroups.editor.operationsContainer.addSubgroupLinkTitle" />
                            <cti:msg var="subgroupNameLabel" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupNameLabel" />
                            <cti:msg var="subgroupNameSaveText" key="yukon.web.deviceGroups.editor.operationsContainer.subgroupNameSaveText" />
                            
                            <a title="${addSubgroupLinkTitle}" onclick="showGroupPopup('${addSubGroupPopupId}', 'childGroupName')" href="javascript:void(0);">
                                ${addSubgroupText}
                            </a>
                            
                            <tags:simplePopup id="${addSubGroupPopupId}" title="${addSubgroupText}" styleClass="groupEditorPopup"  onClose="showGroupPopup('${addSubGroupPopupId}');">
                            
                                <form id="addSubGroupForm" method="post" action="/spring/group/editor/addChild" onsubmit="return changeGroupName();">
                                
                                    <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                                    
                                    <tags:nameValueContainer>
                                        <tags:nameValue name="${subgroupNameLabel}">
                                            <input id="childGroupName" name="childGroupName" type="text" />
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                    
                                    <br>
                                    <input id="addSubGroupSaveButton" type="button" value="${subgroupNameSaveText}" 
                                           onclick="checkAndSubmitNewName('childGroupName', 'addSubGroupForm', 'addSubGroupSaveButton', 'addSubGroupWaitImg');">
                                    <img id="addSubGroupWaitImg" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">
                                
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
                            <c:url var="addByDeviceCollectionUrl" value="/spring/group/editor/showAddDevicesByCollection">
                                <c:param name="groupName" value="${group.fullName}" />
                            </c:url>
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
                    
                    <form id="copyContentsToGroupForm" action="/spring/group/editor/copyContentsToGroup">
                        
                        <cti:msg var="copyContentsPopupTitle" key="yukon.web.deviceGroups.editor.operationsContainer.copyContents.popupTree.title"/>
                        <cti:msg var="copyContentsPopupSubmitText" key="yukon.web.deviceGroups.editor.operationsContainer.copyContents.popupTree.submitButtonText"/>
                        <cti:msg var="copyContentsPopupCancelText" key="yukon.web.deviceGroups.editor.operationsContainer.copyContents.popupTree.cancelButtonText"/>
                        <cti:msg var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />
                        
                        <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                        
                        <ext:nodeValueSelectingPopupTree    fieldId="copyContentsToGroupName"
                                                            fieldName="copyContentsToGroupName"
                                                            nodeValueName="groupName"
                                                            submitButtonText="${copyContentsPopupSubmitText}"
                                                            cancelButtonText="${copyContentsPopupCancelText}"
                                                            submitCallback="submitCopyContentsToGroupForm();"
                                                            
                                                            id="copyContentsToGroupTree"
                                                            treeAttributes="{}"
                                                            triggerElement="copyContentsToGroupLink"
                                                            dataJson="${copyGroupDataJson}"
                                                            title="${copyContentsPopupTitle}"
                                                            width="432"
                                                            height="600"
                                                            noSelectionAlertText="${noGroupSelectedAlertText}" />
                    </form>
                    </div>
                    
                    </cti:checkProperty>
                    </cti:checkRole>
                    
                    <%--############################ GENERATE REPORTS ##########################--%>
                    <%--########################################################################--%>
                    <h4><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.generateReportsLabel"/></h4>
                    
                    <c:url value="/spring/amr/reports/groupDevicesReport" var="htmlUrl">
                        <c:param name="def" value="groupDevicesDefinition"/>
                        <c:param name="groupName" value="${group.fullName}"/>
                    </c:url>
                    <c:choose>
                        <c:when test="${deviceCount > 0}">
                            <a href="${htmlUrl}">HTML</a>
                            |
                            <cti:simpleReportLinkFromNameTag definitionName="groupDevicesDefinition" viewType="csvView" groupName="${group.fullName}">CSV</cti:simpleReportLinkFromNameTag>
                            |
                            <cti:simpleReportLinkFromNameTag definitionName="groupDevicesDefinition" viewType="pdfView" groupName="${group.fullName}">PDF</cti:simpleReportLinkFromNameTag>
                       
                        </c:when>
                        <c:otherwise>
                            No devices
                        </c:otherwise>
                    </c:choose>
                    
                    
                    <%--########################## COLLECTION ACTIONS ##########################--%>
                    <%--########################################################################--%>
                    <h4><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.collectionActionLabel"/></h4>
                    <c:choose>
                        <c:when test="${deviceCount > 0}">
                            
                            <cti:checkProperty property="operator.DeviceActionsRole.GROUP_COMMANDER">
                            <cti:link href="/spring/group/commander/collectionProcessing" key="yukon.web.deviceGroups.editor.operationsContainer.sendCommand">
                                <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                            </cti:link>
                            <br>
                            </cti:checkProperty>
                            
                            <cti:checkProperty property="operator.DeviceActionsRole.MASS_CHANGE">
                            <cti:link href="/spring/bulk/massChange/massChangeSelect" key="yukon.web.deviceGroups.editor.operationsContainer.massChange">
                                <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                            </cti:link>
                            <br>
                            </cti:checkProperty>
                            
                            <cti:checkProperty property="operator.DeviceActionsRole.MASS_DELETE">
                            <cti:link href="/spring/bulk/massChange/massDelete" key="yukon.web.deviceGroups.editor.operationsContainer.massDelete">
                                <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                            </cti:link>
                            <br>
                            </cti:checkProperty>
                            
                            <cti:link href="/spring/bulk/collectionActions" key="yukon.web.deviceGroups.editor.operationsContainer.otherActions">
                                <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                            </cti:link>
                            
                        </c:when>
                        <c:otherwise>
                            No devices
                        </c:otherwise>
                    </c:choose>
                    
                </div>
            </jsp:body>
        </tags:boxContainer>
        
    </div>
    
    <%-- MEMBERS BOX --%>
    <br>
    <div style="float: left; margin-right: .75em; margin-bottom: .5em; width: 450px">
    
        <tags:boxContainer hideEnabled="false">
        
            <jsp:attribute name="title">
                <table>
                    <tr>
                        <td valign="top">
                            <cti:msg key="yukon.web.deviceGroups.editor.membersContainer.membersLabel"/>
                        </td>
                        <td valign="top">
                            ${fn:escapeXml((empty group.name)? '[ Top Level ]' : group.fullName)}
                        </td>
                    </tr>
                </table>
            </jsp:attribute>
        
            <jsp:body>
                <div style="overflow: auto; height: 353px;">
    
                    <%--############################### SUBGROUPS ##############################--%>
                    <%--########################################################################--%>
                    <table style="width: 95%; border-bottom: 1px dotted black;padding-bottom: 10px; margin-bottom: 10px;" >
                        <c:choose>
                            <c:when test="${fn:length(subGroups) > 0}">
                            
                                <%-- User must have DEVICE_GROUP_EDIT to delete groups. Set once for use in loop. --%>
                                <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
                                <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_EDIT">
                                    <c:set var="hasEditRoleProperty" value="true"/>
                                </cti:checkProperty>
                                </cti:checkRole>
                            
                                <c:forEach var="subGroup" items="${subGroups}">
                                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                        <td style="border: none;">
                                            <c:url var="homeUrl" value="/spring/group/editor/home">
                                                <c:param name="groupName" value="${subGroup.fullName}" />
                                            </c:url>
                                        
                                            <span title="${fn:escapeXml(subGroup.fullName)}">
                                                <a href="${homeUrl}"><c:out value="${subGroup.name}"/></a>
                                            </span>
                                        </td>
                                        
                                        <c:if test="${hasEditRoleProperty}">
                                            <td style="border: none; width: 15px;text-align: center;">
                                                <c:choose>
                                                
                                                    <%-- the group being removed must itself be modifiable --%>
                                                    <c:when test="${groupModifiable}">
                                            
                                                        <cti:uniqueIdentifier prefix="subGroup_" var="subId"/>
                                                        <form style="display: inline;" id="${subId}removeSubGroupForm" action="/spring/group/editor/removeGroup" method="post">
                                                            <input type="hidden" name="removeGroupName" value="${fn:escapeXml(subGroup.fullName)}">
                                                            <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                                                            <input type="image" title="Delete Group" class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>" onClick="return removeGroup()">
                                                        </form>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <cti:msg var="cannotDeleteGroupLinkTitle" key="yukon.web.deviceGroups.editor.membersContainer.cannotDeleteGroupLinkTitle"/>
                                                        <img class="graycssicon" title="${cannotDeleteGroupLinkTitle}" src="<c:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>" >
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
                
                    <%--################################ DEVICES ###############################--%>
                    <%--########################################################################--%>
                    <div id="deviceMembers">
                        <c:choose>
                            <c:when test="${not showImmediately && (showDevices == false )}">
                                <table style="width: 95%;" >
                                    <tr>
                                        <td>
                                            <cti:msg key="yukon.web.deviceGroups.editor.membersContainer.groupContainsCountLabel" arguments="${deviceCount}"/>
                                            <c:if test="${deviceCount > 0}">
                                                <cti:msg var="showDevicesLimitText" 
                                                         key="yukon.web.deviceGroups.editor.membersContainer.showDevicesLimitText" 
                                                         argument="${maxGetDevicesSize}"/>
                                                         
                                                <cti:msg var="showDeviceslabel" key="yukon.web.deviceGroups.editor.membersContainer.showDeviceslabel"/>         
                                                <input id="showDevicesButton" type="button" onclick="showDevices()" value="${showDeviceslabel}" title="${showDevicesLimitText}">
                                                <img id="showDevicesWaitImg" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">       
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
            </jsp:body>
        </tags:boxContainer>
        
    </div>
    <div style="clear: both" />

</cti:standardPage>