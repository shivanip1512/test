<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="User Assignment for Load Management Visibility" module="userlm">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/spring/editor/userGroupSelector" title="User/Group Editor"  />
    &gt; Group Editor
</cti:breadCrumbs>


	<h2>${group.groupName}</h2>
    <tags:widgetContainer groupId="${param.groupId}" width="550px" identify="true">
    
		<br><br>
	  	<div style="width: 550px;">
	  		Use this permission for Load Management.  Select the objects to ALLOW access to for the group.
	  	</div>
		<tags:widget bean="groupPermissionEditorWidget" pickerType="lmDevicePicker"
		    permission="LM_VISIBLE" allow="true"/>
		
		<br><br>
	  	<div style="width: 550px;">
		    Use this permission for Cap Control.  Select the objects to DENY access to for the group.
	  	</div>
		<tags:widget bean="groupPermissionEditorWidget" pickerType="capControlAreaPicker"
		    permission="PAO_VISIBLE" allow="false"/>
  
    </tags:widgetContainer>

</cti:standardPage>