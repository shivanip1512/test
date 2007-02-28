<%@ include file="userpao_include.jspf" %>

<cti:standardPage title="Yukon User to Object " module="blank">

<h2>This should show all users</h2>
<input id="blah" type="hidden" value="0"> 
<span id="loginGroupNameSpan">LOGIN GROUP NAME</span> / <span id="userNameSpan">USER NAME</span>
<cti:userPicker pickerId="funWithUserPicker" userIdField="blah" constraint="" userNameElement="userNameSpan" loginGroupNameElement="loginGroupNameSpan">Select user...</cti:userPicker><br>

<hr>
 some other content 
 <br>
 <br>
 <br>
 <br>
 <br>
<hr>
<h2>This should show Login Groups</h2>

</cti:standardPage>