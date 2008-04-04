<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Groups Home" module="amr">
<cti:standardMenu menuSelection="devicegroups|home"/>

   	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
	    &gt; Groups Home
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
		
		function moveGroup(parentGroup) {
		
			$('parentGroupName').value = parentGroup;
			$('moveGroupForm').submit();
			
		}
        
        function copyContentsToGroup(copyContentsToGroup) {
        
            $('copyContentsToGroupName').value = copyContentsToGroup;
            $('copyContentsToGroupForm').submit();
        }
		
		function showDevices() {
			
			// escape the group name to escape problem characters
			var groupName = '${cti:escapeJavaScript(group.fullName)}';
			var params = {'groupName': groupName};
    		new Ajax.Updater('deviceMembers', '/spring/group/getDevicesForGroup', {method: 'post', parameters: params});
			
		}
        
        function toggleExpand(expand) {
        
            var rootNode = window['root_deviceGroupEditorTree'];
            
            if (expand) {
                rootNode.expandChildNodes(true);
            }
            else {
                rootNode.collapseChildNodes(true);
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
            
            <div style="font-size:9px;text-align:right;padding-left:2px;padding-bottom:5px;">
                <a href="javascript:void(0);" onclick="toggleExpand(true);"><cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.groupTree.expandAllText"/></a> | 
                <a href="javascript:void(0);" onclick="toggleExpand(false);"><cti:msg key="yukon.web.deviceGroups.editor.groupsContainer.groupTree.collapseAllText"/></a>
            </div>
            
            
            <tags:extTree   id="deviceGroupEditorTree"
                            dataJson="${dataJson}"
                            width="432"
                            height="600" 
                            rootVisible="true"
                            treeCss="/JavaScript/extjs_cannon/resources/css/deviceGroup-tree.css" />
                                
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
				
                    <%-- EDIT GROUP --%>
                    <h4><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.editGroupLabel"/></h4>
					<c:choose>
						<c:when test="${group.editable}">
                        
                            <%-- EDIT NAME --%>
							<a title="Click to edit group name" href="javascript:showGroupPopup('editGroupNameDiv', 'newGroupName');"><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.editGroupNameText"/></a>
							<div id="editGroupNameDiv" class="popUpDiv" style="width: 330px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
								<div style="width: 100%; text-align: right;margin-bottom: 10px;">
									<a href="javascript:showGroupPopup('editGroupNameDiv');"><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.editGroupNameCancelText"/></a>
								</div>
								<form method="post" action="/spring/group/updateGroupName" onsubmit="return changeGroupName();">
									<cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.newGroupNameText"/>: <input id="newGroupName" name="newGroupName" type="text" value="${fn:escapeXml(group.name)}" />
									<input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
									<input type="submit" value="<cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.newGroupNameSaveText"/>" onclick="return changeGroupName();">
								</form>
							</div>
                            
                            <%-- REMOVE --%>
                            <form id="removeGroupForm" action="/spring/group/removeGroup" method="post">
                                <input type="hidden" name="removeGroupName" value="${fn:escapeXml(group.fullName)}">
                                <a title="<cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.removeGroupLinkTitle"/>" href="javascript:removeGroup('removeGroupForm')"><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.removeGroupText"/></a>
                            </form>
                            
                            <%-- MOVE --%>
                            <div>
                            <a title="<cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.moveGroupLinkTitle"/>" href="javascript:showGroupPopup('moveGroup');"><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.moveGroupLinkTitle"/></a>
                            <div id="moveGroup" class="popUpDiv" style="width: 310px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
                                
                                <div style="width: 100%; text-align: right;margin-bottom: 10px;">
                                    <a href="javascript:showGroupPopup('moveGroup');"><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.moveGroupCancelText"/></a>
                                </div>
                                <form id="moveGroupForm" action="/spring/group/moveGroup">
                                    <cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.selectParentGroupText"/>:
                                    <tags:groupSelect groupList="${moveGroups}" onSelect="moveGroup"/>
                                    
                                    <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                                    <input type="hidden" id="parentGroupName" name="parentGroupName">
                                </form>
                            </div>
                            
						</c:when>
						<c:otherwise>
							<%-- <span><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.cannotEditGroupText"/></span> --%>
						</c:otherwise>
                        
					</c:choose>
                    
                    <%-- COPY CONTENTS --%>
                    <div>
                    <a title="<cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.copyContentsToGroupLinkTitle"/>" href="javascript:showGroupPopup('copyContentsToGroupDiv');"><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.copyContentsToGroupText"/></a>
                    <div id="copyContentsToGroupDiv" class="popUpDiv" style="width: 310px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
                        
                        <div style="width: 100%; text-align: right;margin-bottom: 10px;">
                            <a href="javascript:showGroupPopup('copyContentsToGroupDiv');"><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.copyContentsToGroupCancelText"/></a>
                        </div>
                        <form id="copyContentsToGroupForm" action="/spring/group/copyContentsToGroup">
                            <cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.selectGroupToCopyContentsToText"/>:
                            <tags:groupSelect groupList="${copyToGroups}" onSelect="copyContentsToGroup"/>
                            
                            <input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
                            <input type="hidden" id="copyContentsToGroupName" name="copyContentsToGroupName">
                        </form>
                    </div>
					
                    <%-- ADD GROUPS --%>
                    <h4><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.addGroupsLabel"/></h4>
                    <c:choose>
                        <c:when test="${group.modifiable}">
                            <a title="<cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.addSubgroupLinkTitle"/>" href="javascript:showGroupPopup('addGroup', 'childGroupName');"><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.addSubgroupText"/></a>
                            <div id="addGroup" class="popUpDiv" style="width: 330px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
                                <div style="width: 100%; text-align: right;margin-bottom: 10px;">
                                    <a href="javascript:showGroupPopup('addGroup');"><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.addSubgroupCancelText"/></a>
                                </div>
                                <form id="addSubGroupForm" method="post"  action="/spring/group/addChild" onsubmit="return addSubGroup()">
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
					<h4><cti:msg key="yukon.web.deviceGroups.editor.operationsContainer.addDevicesLabel"/></h4>
		
					<c:choose>
						<c:when test="${group.modifiable and group.parent != null}">
							
                            <%-- BY SELECTING DEVICE --%>
                            <div>
								<form id="addDeviceForm" method="post" action="/spring/group/addDevice">
									<input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}" />
									<input type="hidden" name="showDevices" value="true" />
									<input type="hidden" id="deviceToAdd" name="deviceId" />
								</form>
								<cti:multiPaoPicker pickerId="devicePickerId" paoIdField="deviceToAdd" constraint="com.cannontech.common.search.criteria.MeterCriteria" finalTriggerAction="addDevice" selectionLinkName="Add Devices to Group" excludeIds="${deviceIdsInGroup}"><span title="Click to select devices to add">Select Devices</span></cti:multiPaoPicker>
							</div>
				            
                            <%-- BY FILE UPLOAD --%>
							<c:url var="addByFileUrl" value="/spring/group/showAddDevicesByFile">
								<c:param name="groupName" value="${group.fullName}" />
							</c:url>
							<a title="Click to add multiple devices via file upload" href="${addByFileUrl}">By File Upload</a>
							<br>
							
                            <%-- BY PHYSICAL RANGE --%>
							<c:url var="addByAddressUrl" value="/spring/group/showAddDevicesByAddress">
								<c:param name="groupName" value="${group.fullName}" />
							</c:url>
							<a title="Click to add multiple devices via physical address range" href="${addByAddressUrl}">By Physical Address Range</a>
						</c:when>
						<c:otherwise>
							Cannot add Devices to this group
						</c:otherwise>
					</c:choose>
                    
                    <%-- GENERATE REPORTS --%>
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
				</div>
				
			</jsp:body>
			
		</tags:boxContainer>
	</div>
	
    <%-- MEMBERS BOX --%>
	<div style="float: left; margin-right: .75em; margin-bottom: .5em; width: 450px">
	
		<tags:boxContainer hideEnabled="false">
		
			<jsp:attribute name="title">
				<table>
					<tr>
						<td valign="top">
							Members:
						</td>
						<td valign="top">
							${fn:escapeXml((empty group.name)? '[ Top Level ]' : group.fullName)}
						</td>
					</tr>
				</table>
			</jsp:attribute>
		
			<jsp:body>
				<div style="overflow: auto; height: 353px;">
	
                    <%-- MEMBER SUBGROUPS --%>
					<table style="width: 95%; border-bottom: 1px dotted black;padding-bottom: 10px; margin-bottom: 10px;" >
						<c:choose>
							<c:when test="${fn:length(subGroups) > 0}">
								<c:forEach var="subGroup" items="${subGroups}">
									<tr class="<tags:alternateRow odd="" even="altRow"/>">
										<td style="border: none;">
											<c:url var="homeUrl" value="/spring/group/home">
												<c:param name="groupName" value="${subGroup.fullName}" />
											</c:url>
										
											<span title="${fn:escapeXml(subGroup.fullName)}">
												<a href="${homeUrl}"><c:out value="${subGroup.name}"/></a>
											</span>
										</td>
										<td style="border: none; width: 15px;text-align: center;">
											<c:choose>
												<c:when test="${group.modifiable && subGroup.editable}">
										
													<cti:uniqueIdentifier prefix="subGroup_" var="subId"/>
													<form style="display: inline;" id="${subId}removeSubGroupForm" action="/spring/group/removeGroup" method="post">
														<input type="hidden" name="removeGroupName" value="${fn:escapeXml(subGroup.fullName)}">
														<input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}">
														<input type="image" title="Delete Group" class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>" onClick="return removeGroup()">
													</form>
												</c:when>
												<c:otherwise>
													<img class="graycssicon" title="Cannot Delete Group" src="<c:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>" >
												</c:otherwise>
											</c:choose>
										</td>
									</tr>
								</c:forEach>
							</c:when> 
							<c:otherwise>
									<tr>
										<td>
											No child groups
										</td>
									</tr>
							</c:otherwise>
						</c:choose>
					</table>
				
                    <%-- MEMBER DEVICES --%>
					<div id="deviceMembers">
						<c:choose>
							<c:when test="${deviceCount > 15 && (showDevices == false )}">
								<table style="width: 95%;" >
									<tr>
										<td>
											This group contains ${deviceCount} devices.
											<c:if test="${deviceCount > 0}">
												<a href="javascript:showDevices()">Show Devices</a>
											</c:if>
										</td>
									</tr>
								</table>
							</c:when>
							<c:otherwise>
                                <jsp:include page="/spring/group/getDevicesForGroup">
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