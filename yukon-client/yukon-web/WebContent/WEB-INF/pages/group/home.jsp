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
		
		function addDevice(){
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
		
		function moveGroup(parentGroup) {
		
			$('parentGroupName').value = parentGroup;
			$('moveGroupForm').submit();
			
		}
		
		function showDevices() {
			
			// encode the group name to escape problem characters
			var groupName = ${cti:jsonString(group.fullName)};
			var params = {'groupName': groupName};
    		new Ajax.Updater('deviceMembers', '/spring/group/getDevicesForGroup', {method: 'post', parameters: params});
			
		}
		
	</script>

	<h2>Groups Home</h2>
	
	<c:if test="${not empty param.errorMessage}">
		<div style="color: red">
			${param.errorMessage}
		</div>
	</c:if>

	<div style="float: left; margin-right: .75em; margin-bottom: .5em; width: 350px;">

		<tags:boxContainer title="Groups" hideEnabled="false">
			<table style="width: 100%;" >
				<c:choose>
					<c:when test="${fn:length(groupHierarchy.childGroupList) > 0}">
						<tags:groupHierarchy hierarchy="${groupHierarchy}" selectedGroup="${group.fullName}" />
					</c:when>
					<c:otherwise>
						<tr style="height: 20px;">
							<td colspan="2">No child groups</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</table>
		
		</tags:boxContainer>
	</div>
	
	<div style="float: left; margin-right: .75em; margin-bottom: .5em; width: 450px;">
		
		<tags:boxContainer hideEnabled="false">
			
			<jsp:attribute name="title">
				<table cellpadding="0px" cellspacing="0px">
					<tr>
						<td valign="top">
							Operations:&nbsp;&nbsp;
						</td>
						<td valign="top">
							${(empty group.name)? '[ Top Level ]' : group.fullName}
						</td>
					</tr>
				</table>
			</jsp:attribute>
			
			<jsp:body>
				<div style="font-size: .75em; display: inline;">
				
					<a title="Click to edit group name" href="javascript:showGroupPopup('editGroupNameDiv', 'newGroupName');">Edit Group Name</a>
					<div id="editGroupNameDiv" class="popUpDiv" style="width: 330px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
						<div style="width: 100%; text-align: right;margin-bottom: 10px;">
							<a href="javascript:showGroupPopup('editGroupNameDiv');">cancel</a>
						</div>
						<form style="display: inline;" method="post" action="/spring/group/updateGroupName">
							New Group Name: <input id="newGroupName" name="newGroupName" type="text" value="${group.name}" />
							<input type="hidden" name="groupName" value="${group.fullName}" />
							<input type="submit" value="Save" onclick="return changeGroupName();" />
						</form>
					</div>
					
					<br/>
					
					<a title="Click to add a sub group" href="javascript:showGroupPopup('addGroup', 'childGroupName');">Add Sub Group</a>
					<div id="addGroup" class="popUpDiv" style="width: 330px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
						<div style="width: 100%; text-align: right;margin-bottom: 10px;">
							<a href="javascript:showGroupPopup('addGroup');">cancel</a>
						</div>
						<form action="/spring/group/addChild">
							Sub Group Name: <input id="childGroupName" name="childGroupName" type="text" />
							<input type="submit" value="Save">
							<input type="hidden" name="groupName" value="${group.fullName}">
						</form>
					</div>
		
					<br/>
					
					<c:choose>
						<c:when test="${group.removable}">
							<form style="display: inline;" id="removeGroupForm" action="/spring/group/removeGroup" method="post">
								<input type="hidden" name="removeGroupName" value="${group.fullName}" />
								<a title="Click to delete this group" href="javascript:removeGroup('removeGroupForm')">Delete Group</a>
							</form>
						</c:when>
						<c:otherwise>
							<span title="Cannot delete system group">Delete Group</span>
						</c:otherwise>
					</c:choose>
					
					<br/>

					<c:choose>
						<c:when test="${group.movable}">
							<a title="Click to move this group" href="javascript:showGroupPopup('moveGroup');">Move Group</a>
							<div id="moveGroup" class="popUpDiv" style="width: 310px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
								
								<div style="width: 100%; text-align: right;margin-bottom: 10px;">
									<a href="javascript:showGroupPopup('moveGroup');">cancel</a>
								</div>
								<form id="moveGroupForm" action="/spring/group/moveGroup">
									Select Parent Group:
									<tags:groupSelect groupList="${moveGroups}" onSelect="moveGroup"/>
									
									<input type="hidden" name="groupName" value="${group.fullName}" />
									<input type="hidden" id="parentGroupName" name="parentGroupName" />
								</form>
							</div>
						</c:when>
						<c:otherwise>
							<span title="Cannot move system group">Move Group</span>
						</c:otherwise>
					</c:choose>
		
					<br/><br/>

					<h4>Add Devices</h4>
		
					<div style="display: inline; position: relative;">
						
						<form id="addDeviceForm" method="post" action="/spring/group/addDevice">
							<input type="hidden" name="groupName" value="${group.fullName}" />
							<input type="hidden" id="deviceToAdd" name="deviceId" />
						</form>
						<cti:paoPicker pickerId="devicePickerId" paoIdField="deviceToAdd" finalTriggerAction="addDevice"><span title="Click to add a device">Individual Device</span></cti:paoPicker>
					</div>
		
					<br/>
					
					<a title="Click to add multiple devices via file upload" href="/spring/group/addDevicesByFile?groupName=${group.fullName}">By File Upload</a>
					
					<br/>
					
					<a title="Click to add multiple devices via physical address range" href="/spring/group/addDevicesByAddress?groupName=${group.fullName}">By Physical Address Range</a>
				
				</div>
				
			</jsp:body>
			
		</tags:boxContainer>
	</div>
	
	<div style="float: left; margin-right: .75em; margin-bottom: .5em; width: 450px;">
	
		<tags:boxContainer hideEnabled="false">
		
			<jsp:attribute name="title">
				<table>
					<tr>
						<td valign="top">
							Members:
						</td>
						<td valign="top">
							${(empty group.name)? '[ Top Level ]' : group.fullName}
						</td>
					</tr>
				</table>
			</jsp:attribute>
		
			<jsp:body>
				<div style="overflow: auto; height: 300px;">
	
					<table style="width: 375px;border-bottom: 1px dotted black;padding-bottom: 10px; margin-bottom: 10px;" >
						<c:choose>
							<c:when test="${fn:length(subGroups) > 0}">
								<c:forEach var="subGroup" items="${subGroups}">
									<tr class="<tags:alternateRow odd="" even="altRow"/>">
										<td style="border: none;">
											<span title="${subGroup.fullName}">
												<a href="/spring/group/home?groupName=${subGroup.fullName}">${subGroup.name}</a>
											</span>
										</td>
										<td style="border: none; width: 15px;text-align: center;">
											<cti:uniqueIdentifier prefix="subGroup_" var="subId"/>
											<form style="display: inline;" id="${subId}removeSubGroupForm" action="/spring/group/removeGroup" method="post">
												<input type="hidden" name="removeGroupName" value="${subGroup.fullName}" />
												<input type="hidden" name="groupName" value="${group.fullName}" />
												<input type="image" title="Delete Group" class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>" onClick="return removeGroup()" />
											</form>
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
				
					<div id="deviceMembers">
						<table style="width: 375px;" >
							<tr>
								<td>
									This group contains ${deviceCount} device${(deviceCount == 1)? '' : 's'}.
									<c:if test="${deviceCount > 0}">
										<a href="javascript:showDevices()">Show Devices</a>
									</c:if>
								</td>
							</tr>
						</table>
					</div>
				</div>
				
			</jsp:body>
		
		</tags:boxContainer>
	
	</div>
	
	<br style="clear: both">

</cti:standardPage>