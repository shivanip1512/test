<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<script type="text/javascript">
	
	function addToGroupToggle() {
		$('addToGroup').toggle();
	}

	function addToGroup() {
		${widgetParameters.jsWidget}.doDirectActionRefresh("add");
	}
	
</script>

<div id="currentGroups">
	<div class="widgetInternalSectionHeader"> Current Groups </div>
	<span id="currentGroupList">
		<c:forEach var="removableGroup" items="${currentGroups}">
			<div style="margin-left: 10px;">
				${(empty removableGroup.name)? '[ Top Level ]' : removableGroup.fullName}<c:if test="${removableGroup.modifiable}"> (<ct:widgetLink method="remove" title="Remove" labelBusy="Removing" groupId="${removableGroup.id}">remove</ct:widgetLink>)</c:if> <br/>
			</div>
		</c:forEach>
	</span>
</div>

<br />

<div id="addGroups">

	<a title="Click to add device to a group" href="javascript:addToGroupToggle()">Add to group</a>
	<div id="addToGroup" class="popUpDiv" style="width: 310px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
		
		<div style="width: 100%; text-align: right;margin-bottom: 10px;">
			<a href="javascript:addToGroupToggle()">cancel</a>
		</div>
		<ct:groupSelect groupList="${addableGroups}" fieldName="groupName" onSelect="addToGroup"/>
	</div>

</div>
