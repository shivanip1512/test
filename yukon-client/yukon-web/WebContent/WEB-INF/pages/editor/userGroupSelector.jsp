<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage title="User Assignment for Load Management Visibility" module="userlm">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    &gt; User/Group Editor
</cti:breadCrumbs>

<c:url var="removeURL" value="/picker/userlm/removePao"/> 
<c:url var="submitUserURL" value="/picker/userlm/submitUserChanges"/>
<c:url var="submitGroupURL" value="/picker/userlm/submitGroupChanges"/>

<script type="text/javascript" src="/JavaScript/json.js"></script>
<script language="JavaScript">

	function forwardToUserEdit(selectedItem) {
		window.location = "/spring/editor/user/editUser?userId=" + selectedItem.userId;
		window.event.returnValue = false;
	}

	function forwardToGroupEdit(selectedItem) {
		window.location = "/spring/editor/group/editGroup?groupId=" + selectedItem.groupId;
		window.event.returnValue = false;
	}
	
</script>

	<input id="userSelectedId" type="hidden" value="0"> 
	<div>
		Select user to edit:
		<cti:userPicker pickerId="lmUserPicker" userIdField="userSelectedId" constraint="" userNameElement="userSelectedName" finalTriggerAction="forwardToUserEdit">
			<img class="cssicon" src="/WebConfig/yukon/Icons/clearbits/search.gif"> 
		</cti:userPicker>
		<span style="display:none" id="userSelectedName"></span> 
	</div>
	
	<br/>
	OR
	<br/><br/>

	<input id="groupSelectedId" type="hidden" value="0"> 
	<div>
		Select group to edit:
		<cti:loginGroupPicker pickerId="lmGroupPicker" loginGroupIdField="groupSelectedId" constraint="" loginGroupNameElement="groupSelectedName" finalTriggerAction="forwardToGroupEdit">
			<img class="cssicon" src="/WebConfig/yukon/Icons/clearbits/search.gif"> 
		</cti:loginGroupPicker>
		<span style="display:none" id="groupSelectedName"></span> 
	</div>

</cti:standardPage>