<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage title="User Assignment for Load Management Visibility" module="userlm">

<c:url var="removeURL" value="/picker/userlm/removePao"/> 
<c:url var="submitUserURL" value="/picker/userlm/submitUserChanges"/>
<c:url var="submitGroupURL" value="/picker/userlm/submitGroupChanges"/>

<script type="text/javascript" src="/JavaScript/json.js"></script>
<script language="JavaScript">
	currentlyAssignedToUser = [];
	
	removeUserPaoLink = function(choice) {
           return function() {
			removeUserPAO(choice);
        };
    };
    
	userColumnNames = [
	       	{"title": "Name", "field": "paoName", "link": null },
	        {"title": "Type", "field": "type", "link": null },
	        {"title": "", "field": "Remove", "link": removeUserPaoLink}
		];	

	removeGroupPaoLink = function(choice) {
    	return function() {
			removeGroupPAO(choice);
        };
    };
	
	groupColumnNames = [
	       	{"title": "Name", "field": "paoName", "link": null },
	        {"title": "Type", "field": "type", "link": null },
	        {"title": "", "field": "Remove", "link": removeGroupPaoLink}
		];	
	
	var displayAssignedUserPaos = function (transport, json) {
		var resultTable;
		if(json.addedPao) {
			var numberOfAssigned = currentlyAssignedToUser.length;
			currentlyAssignedToUser[numberOfAssigned] = json.addedPao;
			resultTable = createHtmlTableFromJson(currentlyAssignedToUser, userColumnNames, '');
		}
		else {
			resultTable = createHtmlTableFromJson(json.assignedLMPaos, userColumnNames, '');
			currentlyAssignedToUser = json.assignedLMPaos;	
		}
	    $('userAssignedLM').appendChild(resultTable);
	    
	    Element.show("newUserPaoPickerDiv");
	    
	    if(json.statusSuccess) {
	    	alert(json.statusSuccess);
	    }
	}
	
	function removeUserPAO(choice) {
		var jsonString = {};
  		jsonString.removeId = choice.paoId;
  		jsonString.assigned = currentlyAssignedToUser;
  		var args = {};
  		args.currentJson = jsonString.toJSONString();
		new Ajax.Updater('userAssignedLM', '${removeURL}', {'method':'get', 'onComplete':displayAssignedUserPaos, 'parameters': args});
	}
	
	function submitUserChanges() {
		var jsonString = {};
  		jsonString.userId = $F('userSelectedId');
  		jsonString.assigned = currentlyAssignedToUser;
  		var args = {};
  		args.currentJson = jsonString.toJSONString();
		new Ajax.Updater('userAssignedLM', '${submitUserURL}', {'method': 'get', 'onComplete':displayAssignedUserPaos, 'parameters': args});
	}
	
	var displayAssignedGroupPaos = function (transport, json) {
		var resultTable;
		if(json.addedPao) {
			var numberOfAssigned = currentlyAssignedToGroup.length;
			currentlyAssignedToGroup[numberOfAssigned] = json.addedPao;
			resultTable = createHtmlTableFromJson(currentlyAssignedToGroup, groupColumnNames, '');
		}
		else {
			resultTable = createHtmlTableFromJson(json.assignedLMPaos, groupColumnNames, '');
			currentlyAssignedToGroup = json.assignedLMPaos;	
		}
	    $('groupAssignedLM').appendChild(resultTable);
	    
	    Element.show("newGroupPaoPickerDiv");
	    
	    if(json.statusSuccess) {
	    	alert(json.statusSuccess);
	    }
	}
	
	function removeGroupPAO(choice) {
		var jsonString = {};
  		jsonString.removeId = choice.paoId;
  		jsonString.assigned = currentlyAssignedToGroup;
  		var args = {};
  		args.currentJson = jsonString.toJSONString();
		new Ajax.Updater('groupAssignedLM', '${removeURL}', {'method':'get', 'onComplete':displayAssignedGroupPaos, 'parameters': args});
	}
	
	function submitGroupChanges() {
		var jsonString = {};
  		jsonString.groupId = $F('groupSelectedId');
  		jsonString.assigned = currentlyAssignedToGroup;
  		var args = {};
  		args.currentJson = jsonString.toJSONString();
		new Ajax.Updater('groupAssignedLM', '${submitGroupURL}', {'method': 'get', 'onComplete':displayAssignedGroupPaos, 'parameters': args});
	}
</script>

<h2 align="center">ASSIGN LM DEVICES TO A USER OR LOGIN GROUP</h2>
<input id="userSelectedId" type="hidden" value="0"> 
<div class="simpleLeft">
	<cti:userPicker pickerId="lmUserPicker" userIdField="userSelectedId" constraint="" userNameElement="userSelectedName" finalTriggerAction="divToRefresh:userAssignedLM;url:/picker/userlm/refreshUser;onComplete:displayAssignedUserPaos">
		Choose individual user...</cti:userPicker>
	<br>
</div>
<div class="simpleRight">
	<table border="1" width="350">
			<tbody>
				<tr>
					<th width="350" align="left">Assigned LM Objects<font color="#99FFFF" 
							size="2" face="Arial, Helvetica, sans-serif">
						</font>
					</th>
				</tr>
			</tbody>
	</table>
	<span id="userTitle">User: </span><span id="userSelectedName"></span> 
		<div id="userAssignedLM">
		</div>
	</div>			
</div>
<br>
<br>
<br>

<div style="display: none" id="newUserPaoPickerDiv">
	<input id="newUserPao" type="hidden" value="0"> 
	<cti:paoPicker pickerId="newUserPaoPicker" paoIdField="newUserPao" constraint="com.cannontech.common.search.criteria.LMDeviceCriteria" finalTriggerAction="divToRefresh:userAssignedLM;url:/picker/userlm/addPao;onComplete:displayAssignedUserPaos">Add load management device...</cti:paoPicker><br>
	<br>
	<br>
	<br>
	<br>
	<a href="javascript:submitUserChanges()">Submit</a>
</div>	
<hr>

<input id="groupSelectedId" type="hidden" value="0"> 
<div class="simpleLeft">
	<cti:loginGroupPicker pickerId="lmGroupPicker" loginGroupIdField="groupSelectedId" constraint="" loginGroupNameElement="groupSelectedName" finalTriggerAction="divToRefresh:groupAssignedLM;url:/picker/userlm/refreshGroup;onComplete:displayAssignedGroupPaos">
		Choose a login group...</cti:loginGroupPicker>
	<br>
</div>
<div class="simpleRight">
	<table border="1" width="350">
			<tbody>
				<tr>
					<th width="350" align="left">Assigned LM Objects<font color="#99FFFF" 
							size="2" face="Arial, Helvetica, sans-serif">
						</font>
					</th>
				</tr>
			</tbody>
	</table>
	<span id="groupTitle">Login Group: </span><span id="groupSelectedName"></span> 
		<div id="groupAssignedLM">
		</div>
	</div>			
</div>
<br>
<br>
<br>

<div style="display: none" id="newGroupPaoPickerDiv">
	<input id="newGroupPao" type="hidden" value="0"> 
	<cti:paoPicker pickerId="newGroupPaoPicker" paoIdField="newGroupPao" constraint="com.cannontech.common.search.criteria.LMDeviceCriteria" finalTriggerAction="divToRefresh:groupAssignedLM;url:/picker/userlm/addPao;onComplete:displayAssignedGroupPaos">Add load management device...</cti:paoPicker><br>
	<br>
	<br>
	<br>
	<br>
	<a href="javascript:submitGroupChanges()">Submit</a>
</div>	
<hr>

</cti:standardPage>