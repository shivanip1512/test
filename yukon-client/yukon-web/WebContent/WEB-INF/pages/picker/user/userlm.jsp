<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage title="User Assignment for Load Management Visibility" module="userlm">

<c:url var="removeURL" value="/picker/userlm/removePao"/> 
<c:url var="submitUserURL" value="/picker/userlm/submitUserChanges"/>

<script type="text/javascript" src="/JavaScript/json.js"></script>
<script language="JavaScript">
	currentlyAssigned = [];
	
	removeUserPaoLink = function(choice) {
           return function() {
			removeUserPAO(choice);
        };
    };
		
	names = [
	       	{"title": "Name", "field": "paoName", "link": null },
	        {"title": "Type", "field": "type", "link": null },
	        {"title": "", "field": "Remove", "link": removeUserPaoLink}
		];	
	
	var displayAssignedUserPaos = function (transport, json) {
		var resultTable;
		if(json.addedPao) {
			var numberOfAssigned = currentlyAssigned.length;
			currentlyAssigned[numberOfAssigned] = json.addedPao;
			resultTable = createHtmlTableFromJson(currentlyAssigned, names, '');
		}
		else {
			resultTable = createHtmlTableFromJson(json.assignedLMPaos, names, '');
			currentlyAssigned = json.assignedLMPaos;	
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
  		jsonString.assigned = currentlyAssigned;
  		var args = {};
  		args.currentJson = jsonString.toJSONString();
		new Ajax.Updater('userAssignedLM', '${removeURL}', {'method':'get', 'onComplete':displayAssignedUserPaos, 'parameters': args});
	}
	
	function submitUserChanges() {
		var jsonString = {};
  		jsonString.userId = $F('userSelectedId');
  		jsonString.assigned = currentlyAssigned;
  		var args = {};
  		args.currentJson = jsonString.toJSONString();
		new Ajax.Updater('userAssignedLM', '${submitUserURL}', {'method': 'get', 'onComplete':displayAssignedUserPaos, 'parameters': args});
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
	<cti:paoPicker pickerId="newUserPaoPicker" paoIdField="newUserPao" constraint="com.cannontech.common.search.criteria.LMDeviceCriteria" finalTriggerAction="divToRefresh:userAssignedLM;url:/picker/userlm/addPaoToUser;onComplete:displayAssignedUserPaos">Add load management device...</cti:paoPicker><br>
	<br>
	<br>
	<br>
	<br>
	<a href="javascript:submitUserChanges()">Submit</a>
</div>	
<hr>

<input id="groupSelectedId" type="hidden" value="0"> 
<cti:loginGroupPicker pickerId="lmLoginGroupPicker" loginGroupIdField="groupSelectedId" constraint="" loginGroupNameElement="loginGroupNameSpan">Choose entire login group...</cti:loginGroupPicker><br>

<hr>
<input id="newGroupPao" type="hidden" value="0"> 
<cti:paoPicker pickerId="newGroupPaoPicker" paoIdField="newGroupPao" constraint="com.cannontech.common.search.criteria.LMDeviceCriteria">Add load management device...</cti:paoPicker><br>
 <br>
<hr>

</cti:standardPage>