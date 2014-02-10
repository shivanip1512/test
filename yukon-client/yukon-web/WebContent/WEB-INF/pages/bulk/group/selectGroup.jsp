<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

<c:choose>
  <c:when test="${addRemove eq 'ADD'}">
      <c:set var="pageName" value="bulk.addToGroup"/>
      <c:set var="containerKey" value="yukon.common.device.bulk.addToGroup"/>
      <cti:msg var="buttonText" key="yukon.common.device.bulk.addToGroup.addToGroupButtonText"/>
      <cti:msg var="noDeviceGroupSelectedAlertText" key="yukon.common.device.bulk.addToGroup.noDeviceGroupSelectedAlertText"/>
      <c:set var="formAction" value="/bulk/group/addToGroup"/>
  </c:when>
  <c:when test="${addRemove eq 'REMOVE'}">
      <c:set var="pageName" value="bulk.removeFromGroup"/>
      <c:set var="containerKey" value="yukon.common.device.bulk.removeFromGroup"/>
      <cti:msg var="buttonText" key="yukon.common.device.bulk.removeFromGroup.removeFromGroupButtonText"/>
      <cti:msg var="noDeviceGroupSelectedAlertText" key="yukon.common.device.bulk.removeFromGroup.noDeviceGroupSelectedAlertText"/>
      <c:set var="formAction" value="/bulk/group/removeFromGroup"/>
  </c:when>
</c:choose>

<cti:standardPage module="tools" page="${pageName}">

<script type="text/javascript">
function validateGroupIsSelected(btn, alertText) {
    if (document.getElementById('groupName').value == '') {
        alert(alertText);
        return false;
    }
    btn.disabled = true;
    document.getElementById('selectGroupForm').submit();
}
</script>
    
    <tags:bulkActionContainer key="${containerKey}" deviceCollection="${deviceCollection}">
    
        <form id="selectGroupForm" action="${formAction}" method="post" style="max-width: 500px;">
            <cti:csrfToken/>   
             <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
                                
            <%-- SELECT DEVICE GROUP TREE INPUT --%>
            <cti:deviceGroupHierarchyJson predicates="MODIFIABLE" var="dataJson" />
            <jsTree:nodeValueSelectingInlineTree fieldId="groupName"
                                                 fieldName="groupName"
                                                 nodeValueName="groupName"
                                                 multiSelect="false"
                                                 id="selectGroupTree"
                                                 dataJson="${dataJson}"
                                                 maxHeight="400"
                                                 includeControlBar="true"/>
                            
            <div class="page-action-area">
                <button name="addRemoveButton" onclick="return validateGroupIsSelected(this, '${noDeviceGroupSelectedAlertText}');">
                    <span class="b-label">${buttonText}</span>
                </button>
            </div>
        </form>
    
   </tags:bulkActionContainer>
    
</cti:standardPage>