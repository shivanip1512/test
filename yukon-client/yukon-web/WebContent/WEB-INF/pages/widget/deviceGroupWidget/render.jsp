<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="currentGroups">
<div class="widgetInternalSectionHeader"> Current Groups </div>
<span id="currentGroupList">
<c:forEach var="removableGroup" items="${currentGroups}">
	&nbsp&nbsp${removableGroup.fullName}<c:if test="${removableGroup.removable}"> (<ct:widgetLink method="remove" title="Remove" labelBusy="Removing" groupId="${removableGroup.id}">remove</ct:widgetLink>)</c:if> <br/>
</c:forEach>
</span>
</div>
<br />
<div id="addGroups">
<span class="widgetInternalSectionHeader"> Add to group: </span>
<select name="groupId">
<c:forEach var="addableGroup" items="${addableGroups}">
<option value="${addableGroup.id}">${addableGroup.fullName}</option>
</c:forEach>
</select>
<ct:widgetActionRefresh method="add" label="Add" labelBusy="Adding" />
</div>
	