<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<script type="text/javascript">
	
	function addToGroupToggle() {
		$('addToGroup').toggle();
	}

	function addToGroup() {
		${widgetParameters.jsWidget}.doDirectActionRefresh("add");
	}
	
</script>

<div id="currentGroups">
	<div class="widgetInternalSectionHeader">Current Groups</div>
        <c:choose>
            <c:when test="${not empty currentGroups}">
  <table style="width: 100%">
                <c:forEach var="group" items="${currentGroups}">
                    <tr class="<ct:alternateRow odd="" even="altRow"/>">
                        <td style="border: none">
                           <c:url value="/spring/group/home" var="groupEditorUrl">
                             <c:param name="groupName" value="${group.fullName}"/>
                           </c:url>
            
                            <a href="${groupEditorUrl}">${group.fullName}</a>
                        </td>
                        <td style="border: none; width: 15px; text-align: center;">
                            <c:choose>
                                <c:when test="${group.modifiable}">
                                <ct:widgetLink method="remove" title="Remove" labelBusy="Removing" groupId="${group.id}">
                                    <img class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>">
                                </ct:widgetLink>
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

<div id="addGroups">

	<a title="Click to add device to a group" href="javascript:addToGroupToggle()">Add to group</a>
	<div id="addToGroup" class="popUpDiv" style="width: 310px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
		
		<div style="width: 100%; text-align: right;margin-bottom: 10px;">
			<a href="javascript:addToGroupToggle()">cancel</a>
		</div>
		<ct:groupSelect groupList="${addableGroups}" fieldName="groupName" onSelect="addToGroup"/>
	</div>

</div>
