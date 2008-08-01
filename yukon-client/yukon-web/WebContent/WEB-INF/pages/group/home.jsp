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
                $(focusElem).focus();
            }
            
        }
        
        function addDevice(devices){
        
            var ids = '';
            $(devices).each(function(device){
                ids += device.paoId + ',';
            });
        
            $('deviceToAdd').value = ids;
            $('addDeviceForm').submit();
            window.event.returnValue = false;
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
        
        function changeGroupName() {
            
            var newName = $F('newGroupName');
            if(newName == null || newName == '' || (newName.indexOf('/') != -1) || (newName.indexOf('\\') != -1)) {
                alert('Please enter a New Group Name.  Group names may not contain slashes.');
            } else {
                return true;
            }
            
            $('newGroupName').focus();
            
            return false;
        }
        
        function addSubGroup() {
            
            var subGroupName = $F('childGroupName');
            if(subGroupName == null || subGroupName == '' || (subGroupName.indexOf('/') != -1) || (subGroupName.indexOf('\\') != -1)) {
                alert('Please enter a Sub Group Name.  Group names may not contain slashes.');
            } else {
                return true;
            }
            
            $('childGroupName').focus();
            
            return false;
        }
        
        function submitMoveGroupForm() {
        
            $('moveGroupForm').submit();
        }
        
        
        function submitCopyContentsToGroupForm() {
        
            $('copyContentsToGroupForm').submit();
        }
        
        function showDevices() {
            
            // escape the group name to escape problem characters
            var groupName = '${cti:escapeJavaScript(group.fullName)}';
            var params = {'groupName': groupName};
            new Ajax.Updater('deviceMembers', '/spring/group/editor/getDevicesForGroup', {method: 'post', parameters: params});
            
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
                        <a title="Click to edit group name" href="javascript:showGroupPopup('editGroupNameDiv', 'newGroupName');"><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.editGroupNameText"/></a>
                        <div id="editGroupNameDiv" class="popUpDiv" style="width: 330px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
                            <div style="width: 100%; text-align: right;margin-bottom: 10px;">
                                <a href="javascript:showGroupPopup('editGroupNameDiv');"><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.editGroupNameCancelText"/></a>
                            </div>
                            <form method="post" action="/spring/group/editor/updateGroupName" onsubmit="return changeGroupName();">
                                <cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.newGroupNameText"/>: <input id="newGroupName" name="newGroupName" type="text" value="${fn:escapeXml(group.name)}" />
                                <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                                <input type="submit" value="<cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.newGroupNameSaveText"/>" onclick="return changeGroupName();">
                            </form>
                        </div>
                        
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
                                                                dataJson="${modifiableNoChildrenGroupsDataJson}"
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
                            <a title="<cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.addSubgroupLinkTitle"/>" href="javascript:showGroupPopup('addGroup', 'childGroupName');"><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.addSubgroupText"/></a>
                            <div id="addGroup" class="popUpDiv" style="width: 330px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
                                <div style="width: 100%; text-align: right;margin-bottom: 10px;">
                                    <a href="javascript:showGroupPopup('addGroup');"><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.addSubgroupCancelText"/></a>
                                </div>
                                <form id="addSubGroupForm" method="post"  action="/spring/group/editor/addChild" onsubmit="return addSubGroup()">
                                    <cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.subgroupNameLabel"/>: <input id="childGroupName" name="childGroupName" type="text" />
                                    <input type="submit" value="<cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.subgroupNameSaveText"/>" onclick="return addSubGroup();" >
                                    <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                                </form>
                            </div>
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
                                                            dataJson="${modifiableNoChildrenGroupsDataJson}"
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
                    <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
                    
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
                            
                            <cti:checkMultiProperty property="DeviceActionsRole.DEVICE_GROUP_MODIFY,DeviceActionsRole.GROUP_COMMANDER,DeviceActionsRole.MASS_CHANGE,DeviceActionsRole.LOCATE_ROUTE,DeviceActionsRole.MASS_DELETE">
                            <cti:link href="/spring/bulk/collectionActions" key="yukon.web.deviceGroups.editor.operationsContainer.otherActions">
                                <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                            </cti:link>
                            </cti:checkMultiProperty>
                            
                        </c:when>
                        <c:otherwise>
                            No devices
                        </c:otherwise>
                    </c:choose>
                    
                    </cti:checkRole>
                    
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
                                                <a href="javascript:showDevices()">
                                                    <cti:msg key="yukon.web.deviceGroups.editor.membersContainer.showDeviceslabel"/>
                                                </a>
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