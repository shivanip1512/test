<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="User Assignment for Load Management Visibility" module="userlm">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/spring/editor/userGroupSelector" title="User/Group Editor"  />
	&gt; User Editor
</cti:breadCrumbs>

<!-- These filese are included for the paoPicker on the imported paoTable jsp -->
<cti:includeScript link="/JavaScript/itemPicker.js" />
<cti:includeScript link="/JavaScript/tableCreation.js" />
<cti:includeScript link="/JavaScript/paoPicker.js" />
<cti:includeCss link="/WebConfig/yukon/styles/itemPicker.css" />

<script language="JavaScript">

	function save(){
		$('saveConfirm').innerHTML = '';
		new Ajax.Updater('saveConfirm', '/spring/editor/user/save', {method: 'get', parameters: {userId: ${user.liteID}, paoIdList: $('paoIdList').value}});
	}
	
	function addPao(paoId){
		$('saveConfirm').innerHTML = '';
		new Ajax.Updater('paoTable', '/spring/editor/user/addPao', {method: 'get', parameters: {paoId: $('newPaoId').value, paoIdList: $('paoIdList').value}});
	}
	
	function removePao(paoId){
		$('saveConfirm').innerHTML = '';
		new Ajax.Updater('paoTable', '/spring/editor/user/removePao', {method: 'get', parameters: {paoId: paoId, paoIdList: $('paoIdList').value}});
	}
	
</script>

	<h2>${user.username}</h2>
	<div>
		<div id="saveConfirm"></div>
		<div id="paoTable" style="width: 60%">
			<jsp:include page="../paoTable.jsp" />
		</div>
	
	</div>			

</cti:standardPage>