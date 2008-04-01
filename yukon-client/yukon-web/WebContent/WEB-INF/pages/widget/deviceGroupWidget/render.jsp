<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
    
    // list of groupIds to add device to
    var groupIds = $A();
    
    // to submit widget form once the hidden groupIds field has been set
    function commitGroupIds() {
        ${widgetParameters.jsWidget}.doDirectActionRefresh("add");
    }
    
    // tree click callback
    // call during beforeclick and return false to stop the click event for occuring
    // this is done to prevent ext from setting the 'node-selected' class on it, which cannot
    // be removed through the API. We don't want 'node-selected' because that changes the backgroud-color
    // of the node, which we are trying to manage ourself in this case. Notice also that the 
    // 'highlightNode' class has !important so that ext's 'x-tree-node-over' class doesn't overshadow
    // our style 
    function addGroupId(node, event) {
    
        // don't allow root to be added
        if (node.getDepth() == 0) {
            return false;
        }
    
        var groupIdIdx = groupIds.indexOf(node.id);
        var nodeUI = node.getUI();
        
        // not yet selected, add it
        if (groupIdIdx < 0) {
        
            nodeUI.addClass('highlightNode');
            groupIds.push(node.id);
            
        // already is selected, remove it
        }
        else {
            nodeUI.removeClass('highlightNode');
            groupIds.splice(groupIdIdx, 1);
        }
        
        // stop further events
        return false;
    }
    
    // the submit button handler function
    // set the hidden var with our selected group ids concatenated
    // then call commitGroupIds() which will submit the widget for refresh calling the add method
    function setGroupIds() {
        if (groupIds.length > 0) {
            $('groupIds').value = groupIds.join(',');
            commitGroupIds();
        }
    }
    
    // the close button handler function
    // remove any selected groups from the groupIds list bfore closing
    function clearAllGroupIds() {
        groupIds = $A();
        $('groupIds').value = '';
    }
    
	
</script>

<div id="currentGroups">
	<div class="widgetInternalSectionHeader">Current Groups</div>
        <c:choose>
            <c:when test="${not empty currentGroups}">
  <table style="width: 100%">
                <c:forEach var="group" items="${currentGroups}">
                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                        <td style="border: none">
                           <c:url value="/spring/group/home" var="groupEditorUrl">
                             <c:param name="groupName" value="${group.fullName}"/>
                           </c:url>
            
                            <a href="${groupEditorUrl}">${fn:escapeXml(group.fullName)}</a>
                        </td>
                        <td style="border: none; width: 15px; text-align: center;">
                            <c:choose>
                                <c:when test="${group.modifiable}">
                                <tags:widgetLink method="remove" title="Remove" labelBusy="Removing" groupId="${group.id}">
                                    <img class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>">
                                </tags:widgetLink>
                                </c:when>
                                <c:otherwise>
                                    <img class="graycssicon" title="Cannot remove device from group" src="<c:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>">
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
  </table>
            </c:when>
            <c:otherwise>
                No Groups
            </c:otherwise>
        </c:choose> 
</div>

<br>

<input type="hidden" id="groupIds" name="groupIds" />

<cti:msg key="yukon.web.deviceGroups.widget.groupTree.rootName" var="rootName"/>
<cti:msg key="yukon.web.deviceGroups.widget.groupTree.title" var="addDeviceTitle"/>
<cti:msg key="yukon.web.deviceGroups.widget.groupTree.submitButtonText" var="submitText"/>
<cti:msg key="yukon.web.deviceGroups.widget.groupTree.closeButtonText" var="closeText"/>

<tags:extDeviceGroupPopupTree   treeId="deviceGroupWidgetTreePopup"
                                width="432"
                                height="600" 
                                rootVisible="true"
                                treeCss="/JavaScript/extjs_cannon/resources/css/deviceGroup-tree.css"
                                treeCallbacks="{'beforeclick':addGroupId}"
                                
                                title="${addDeviceTitle}"
                                submitText="${submitText}"
                                closeText="${closeText}"
                                modal="true"
                                closeOnSubmit="true"
                                submitHandler="setGroupIds();"
                                closeHandler="clearAllGroupIds();"
                                
                                dataUrl="/spring/widget/deviceGroupWidget/deviceGroupHierarchyJson"
                                baseParams="{deviceId:'${deviceId}'}"
                                rootAttributes="{id:'root',text:'${rootName}',href:'javascript:void(0);',disabled:true}" />


