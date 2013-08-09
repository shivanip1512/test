<%@ attribute name="hierarchy" required="true" type="com.cannontech.common.device.groups.model.DeviceGroupHierarchy"%>
<%@ attribute name="selectedGroup" required="true" type="java.lang.String"%>
<%@ attribute name="indentLevel" required="false" type="java.lang.Integer"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:uniqueIdentifier prefix="groupHierarchy_" var="thisId"/>

<c:if test="${empty pageScope.indentLevel}">
	<c:set var="indentLevel" value="0" />
</c:if>
	
<tr class="${(hierarchy.group.fullName == selectedGroup)?'highlighted':''}">
	<td style="border: none;">
		<div title="${fn:escapeXml(hierarchy.group.fullName)}" style="padding-left: ${pageScope.indentLevel * 12}px">
			<div style="float: left;">
				<c:if test="${not empty hierarchy.group.name}">
                    <cti:icon icon="icon-arrow-right"/>
				</c:if>
			</div>
			<div style="float: left;margin-left: 5px;">
				
				<c:url var="homeUrl" value="/group/home">
					<c:param name="groupName" value="${hierarchy.group.fullName}" />
				</c:url>
			
				<c:if test="${empty hierarchy.group.name}">[</c:if>
				<a href="${homeUrl}"><c:out value="${(empty hierarchy.group.name)? 'Top Level' : hierarchy.group.name}"/></a>
				<c:if test="${empty hierarchy.group.name}">]</c:if>
			</div>
		</div>
	</td>
</tr>

<c:url var="tagUrl" value="/WEB-INF/pages/group/groupHierarchyTag.jsp" />
<c:if test="${hierarchy.childGroupsPresent}">
	<c:forEach var="childHierarchy" items="${hierarchy.childGroupList}">
		<%-- 
			Hack for precompiling jsps - precompilation fails for recursive tags
			jsp:include allows for the recursion and precompilation
		 --%>
		<c:set var="hierarchy" value="${childHierarchy}" scope="request" />
		<c:set var="selectedGroup" value="${selectedGroup}" scope="request" />
		<c:set var="indentLevel" value="${pageScope.indentLevel + 1}" scope="request" />
		<jsp:include page="${tagUrl}" />
	</c:forEach>
</c:if>


