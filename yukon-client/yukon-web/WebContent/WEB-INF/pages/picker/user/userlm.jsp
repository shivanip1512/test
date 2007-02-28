\<%@ include file="userpao_include.jspf" %>

<cti:standardPage title="User Assignment for Load Management Visibility" module="blank">

<h2>ASSIGN LM DEVICES TO A USER OR LOGIN GROUP</h2>
<input id="userSelectedId" type="hidden" value="0"> 
<cti:userPicker pickerId="lmUserPicker" userIdField="userSelectedId" constraint="" userNameElement="userNameSpan">Add individual user...</cti:userPicker><br>

<hr>
<input id="newUserPao" type="hidden" value="0"> 
<cti:paoPicker pickerId="lmPaoPicker" paoIdField="newUserPao" constraint="com.cannontech.common.search.criteria.LMDeviceCriteria" paoNameElement="paoNameSpan" typeElement="typeSpan">Choose load management device...</cti:paoPicker><br>
 <br>
<hr>

<input id="groupSelectedId" type="hidden" value="0"> 
<cti:loginGroupPicker pickerId="lmLoginGroupPicker" loginGroupIdField="groupSelectedId" constraint="" loginGroupNameElement="loginGroupNameSpan">Add entire login group...</cti:loginGroupPicker><br>

<hr>
<input id="newGroupPao" type="hidden" value="0"> 
<cti:paoPicker pickerId="lmPaoPicker" paoIdField="newGroupPao" constraint="com.cannontech.common.search.criteria.LMDeviceCriteria" paoNameElement="paoNameSpan" typeElement="typeSpan">Choose load management device...</cti:paoPicker><br>
 <br>
<hr>

</cti:standardPage>