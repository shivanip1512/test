<%@ attribute name="groupList" required="true" type="java.util.List"%>
<%@ attribute name="fieldName" required="false" type="java.lang.String"%>
<%@ attribute name="onSelect" required="false" type="java.lang.String"%>

<%@ taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:uniqueIdentifier prefix="groupSelect_" var="fieldId"/>
<script type="text/javascript">

	function highlight(theDiv) {
	
		if($(theDiv).hasClassName('highlighted')) {
			$(theDiv).removeClassName('highlighted');
		} else {
			$(theDiv).addClassName('highlighted');
		}
	
	}
	
	function groupSelected(selectedDiv, groupName) {

		<c:if test="${not empty pageScope.fieldName}">
			$('${pageScope.fieldName}').value = groupName;
		</c:if>
	
		<c:if test="${not empty pageScope.onSelect}">
			${pageScope.onSelect}(groupName);
		</c:if>
	}

</script>

<div style="border: 1px solid black; height: 100px; overflow: auto; margin: 5px 0px;">
	
	<c:forEach var="group" items="${groupList}">
		<div style="border-bottom: 1px solid #BBBBBB; padding: 2px 3px;" onmouseover="highlight(this);" onmouseout="highlight(this);" onclick="groupSelected(this, '${fn:escapeXml(cti:escapeJavaScript(group.fullName))}')">
			<c:out value="${group.fullName}"/>
		</div>
	</c:forEach>
	
</div>

<c:if test="${not empty pageScope.fieldName}">
	<input type="hidden" id="${pageScope.fieldName}" name="${pageScope.fieldName}" />
</c:if>
