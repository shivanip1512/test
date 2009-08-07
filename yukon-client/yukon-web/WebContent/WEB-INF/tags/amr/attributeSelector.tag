<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="attributes" required="true" type="java.util.List"%>
<%@ attribute name="selectedAttribute" required="false" type="java.lang.String"%>
<%@ attribute name="includeDummyOption" required="false" type="java.lang.Boolean"%>

<c:if test="${empty includeDummyOption}">
	<c:set var="includeDummyOption" value="false"/>
</c:if>

<cti:uniqueIdentifier var="uniqueId" prefix="attributeSelector_"/>

<select id="${uniqueId}" name="${fieldName}">

	<c:if test="${includeDummyOption}">
		<cti:msg var="selectOneLabel" key="yukon.common.device.commander.selector.selectOne"/>
		<option value="">${selectOneLabel}</option>
	</c:if>
	
	<c:forEach var="attr" items="${attributes}">
	
		<c:set var="selected" value=""/>
		<c:if test="${attr == selectedAttribute}">
			<c:set var="selected" value="selected"/>
		</c:if>
	
		<option value="${attr}" ${selected}>${attr.description}</option>
	</c:forEach>
</select>