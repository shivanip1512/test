<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="attributes" required="true" type="java.util.List"%>
<%@ attribute name="selectedAttribute" required="false" type="java.lang.String"%>

<cti:uniqueIdentifier var="uniqueId" prefix="attributeSelector_"/>

<cti:msg var="selectAttributeLabel" key="yukon.common.device.commander.attributeSelector.selectAttribute"/>

<div class="largeBoldLabel">${selectAttributeLabel}</div>

<select id="${uniqueId}" name="${fieldName}">
	<c:forEach var="attr" items="${attributes}">
	
		<c:set var="selected" value=""/>
		<c:if test="${attr == selectedAttribute}">
			<c:set var="selected" value="selected"/>
		</c:if>
	
		<option value="${attr}" ${selected}>${attr.description}</option>
	</c:forEach>
</select>