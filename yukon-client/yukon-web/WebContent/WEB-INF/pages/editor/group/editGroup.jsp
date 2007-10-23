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
    
      <tags:widget bean="groupPermissionEditorWidget" constraint="com.cannontech.common.search.criteria.LMDeviceCriteria" permission="LM_VISIBLE" allow="true"/>
      <tags:widget bean="groupPermissionEditorWidget" constraint="com.cannontech.common.search.criteria.CBCDeviceCriteria" permission="PAO_VISIBLE" allow="false"/>
  
    </tags:widgetContainer>

</cti:standardPage>