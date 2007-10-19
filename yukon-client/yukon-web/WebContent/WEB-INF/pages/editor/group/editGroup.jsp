<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="User Assignment for Yukon Visibility" module="userlm">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/spring/editor/userGroupSelector" title="User/Group Editor"  />
    &gt; Group Editor
</cti:breadCrumbs>

<!-- These filese are included for the paoPicker on the imported paoTable jsp -->
<cti:includeScript link="/JavaScript/itemPicker.js" />
<cti:includeScript link="/JavaScript/tableCreation.js" />
<cti:includeScript link="/JavaScript/paoPicker.js" />
<cti:includeCss link="/WebConfig/yukon/styles/itemPicker.css" />

<script language="JavaScript">

	function save(){
		$('saveConfirm').innerHTML = '';
		new Ajax.Updater('saveConfirm', '/spring/editor/group/save', {method: 'get', parameters: {groupId: ${group.liteID}, paoIdList: $('paoIdList').value, cbcPaoIdList: $('cbcPaoIdList').value}});
	}
	
	function addPao(paoId){
		$('saveConfirm').innerHTML = '';
		new Ajax.Updater('paoTable', '/spring/editor/group/addPao', {method: 'get', parameters: {paoId: $('newPaoId').value, paoIdList: $('paoIdList').value, cbcPaoId: $('newCbcPaoId').value, cbcPaoIdList: $('cbcPaoIdList').value}});
	}
	
	function removePao(paoId){
		$('saveConfirm').innerHTML = '';
		new Ajax.Updater('paoTable', '/spring/editor/group/removePao', {method: 'get', parameters: {paoId: paoId, paoIdList: $('paoIdList').value, cbcPaoIdList: $('cbcPaoIdList').value}});
	}
	
</script>

	<h2>${group.groupName}</h2>
	<div>
		<div id="saveConfirm"></div>
		<div id="paoTable" style="width: 60%">
			<jsp:include page="../paoTable.jsp" />
		</div>
	
	</div>			

</cti:standardPage>