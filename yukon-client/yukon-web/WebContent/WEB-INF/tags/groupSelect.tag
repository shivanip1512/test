<%@ attribute name="groupList" required="true" type="java.util.List"%>
<%@ attribute name="fieldName" required="false" type="java.lang.String"%>
<%@ attribute name="onSelect" required="false" type="java.lang.String"%>

<%@ taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<script type="text/javascript">

	function highlight(theDiv) {
	
		if($(theDiv).hasClassName('highlighted')) {
			$(theDiv).removeClassName('highlighted');
		} else {
			$(theDiv).addClassName('highlighted');
		}
	
	}
	
	function groupSelected(groupName){
	
		<c:if test="${not empty onSelect}">
			${onSelect}(groupName);
		</c:if>

		<c:if test="${not empty fieldName}">
			${fieldName}.value = groupName;
		</c:if>
	
	}

</script>

<div style="border: 1px solid black; width: 300px; height: 100px; overflow: auto; margin: 5px 0px;">
	
	<c:forEach var="group" items="${groupList}">
		<div style="border-bottom: 1px solid #BBBBBB; padding: 2px 3px;" onmouseover="highlight(this);" onmouseout="highlight(this);" onclick="groupSelected('${group.fullName}')">
			${group.fullName}
		</div>
	</c:forEach>
	
</div>

<c:if test="${not empty fieldName}">
	<input type="text" name="${fieldName}" />
</c:if>
