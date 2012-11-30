<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="User Assignment for Load Management Visibility" module="userlm">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    &gt; User/Group Editor
</cti:breadCrumbs>

<cti:url var="removeURL" value="/picker/userlm/removePao"/> 
<cti:url var="submitUserURL" value="/picker/userlm/submitUserChanges"/>
<cti:url var="submitGroupURL" value="/picker/userlm/submitGroupChanges"/>

<script language="JavaScript">

	function forwardToUserEdit(selectedItems) {
		window.location = "/adminSetup/user/permissions?userId=" +
		    $('userIdSelected').value;
		window.event.returnValue = false;
		return true;
	}

	function forwardToGroupEdit(selectedItems) {
		window.location = "/adminSetup/userGroup/permissions?groupId=" +
		    $('loginGroupIdSelected').value;
		window.event.returnValue = false;
		return true;
	}
	
</script>

	<input id="userIdSelected" type="hidden" value=""> 
	<div>
		Select user to edit:
	    <tags:pickerDialog type="userPicker" id="userPicker"
	        destinationFieldId="userIdSelected" endAction="forwardToUserEdit"
	        immediateSelectMode="true">
            <img class="cssicon" src="/WebConfig/yukon/Icons/clearbits/search.gif">
	    </tags:pickerDialog>
		<span style="display:none" id="userSelectedName"></span> 
	</div>
	
	<br/>
	OR
	<br/><br/>

	<div>
		Select group to edit:
	    <tags:pickerDialog type="loginGroupPicker" id="loginGroupPicker"
	        destinationFieldId="loginGroupIdSelected" endAction="forwardToGroupEdit"
	        immediateSelectMode="true">
            <img class="cssicon" src="/WebConfig/yukon/Icons/clearbits/search.gif">
	    </tags:pickerDialog>
	    <input id="loginGroupIdSelected" type="hidden" value=""> 
		<span style="display:none" id="groupSelectedName"></span> 
	</div>

</cti:standardPage>
